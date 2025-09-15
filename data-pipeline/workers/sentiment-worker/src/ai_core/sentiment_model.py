# ai_core/sentiment_model.py
import os
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from typing import List, Tuple
from torch.nn.functional import softmax
from config import SentimentConfig as C

LABELS = ["positive", "neutral", "negative"]

# -------------------------------
# 모델 로더
# -------------------------------
def load_model():
    use_cuda = C.USE_CUDA and torch.cuda.is_available()
    device = "cuda" if use_cuda else "cpu"

    tokenizer = AutoTokenizer.from_pretrained(C.MODEL_NAME)

    # GPU면 half precision 활용
    dtype = torch.float16 if use_cuda else torch.float32
    model = AutoModelForSequenceClassification.from_pretrained(
        C.MODEL_NAME,
        dtype=dtype,  # torch_dtype(deprecated) -> dtype 로 수정
    )
    model.to(device)
    model.eval()

    # PyTorch 2.x + CUDA → compile 최적화 (가능한 경우)
    try:
        if use_cuda and hasattr(torch, "compile"):
            model = torch.compile(model)
    except Exception:
        pass

    try:
        torch.set_float32_matmul_precision("high")
    except Exception:
        pass

    return tokenizer, model, device


# -------------------------------
# 기본 배치 추론 (앞부분만 사용)
# -------------------------------
@torch.inference_mode()
def infer_batch(tokenizer, model, device, texts: List[str]) -> List[Tuple[str, float]]:
    if not texts:
        return []

    model_max = getattr(getattr(model, "config", None), "max_position_embeddings", 512)
    cfg_max = int(getattr(C, "MAX_SEQ_LEN_TOKENS", 512))
    max_len = min(model_max or 512, cfg_max or 512)

    if tokenizer.pad_token is None:
        tokenizer.pad_token = tokenizer.eos_token or tokenizer.cls_token

    enc = tokenizer(
        texts,
        return_tensors="pt",
        truncation=True,
        padding=True,
        max_length=max_len,
    )
    enc = {k: v.to(device) for k, v in enc.items()}

    if device == "cuda":
        with torch.cuda.amp.autocast():
            logits = model(**enc).logits
    else:
        logits = model(**enc).logits

    probs = torch.softmax(logits, dim=1)
    conf, pred = torch.max(probs, dim=1)

    out = []
    for p, c in zip(pred.tolist(), conf.tolist()):
        label = LABELS[p] if 0 <= p < len(LABELS) else "neutral"
        out.append((label, float(c)))
    return out


# -------------------------------
# 긴 기사 전용 추론 (윈도잉 + 집계)
# -------------------------------
def _windows_from_input_ids(
    input_ids: torch.Tensor,
    attention_mask: torch.Tensor,
    max_len: int,
    stride: int,
    pad_id: int,
):
    """
    input_ids: [seq_len], attention_mask: [seq_len]
    -> 여러 윈도우 (ids, mask) 리스트 반환 (각 윈도우 길이는 max_len로 패딩)
    """
    seq_len = input_ids.size(0)
    if seq_len <= max_len:
        # 부족하면 여기서 패딩
        pad_len = max_len - seq_len
        if pad_len > 0:
            input_ids = torch.cat([input_ids, torch.full((pad_len,), pad_id, dtype=torch.long)], dim=0)
            attention_mask = torch.cat([attention_mask, torch.zeros(pad_len, dtype=torch.long)], dim=0)
        return [(input_ids, attention_mask)]

    windows = []
    start = 0
    while start < seq_len:
        end = min(start + max_len, seq_len)
        ids = input_ids[start:end]
        mask = attention_mask[start:end]

        pad_len = max_len - ids.size(0)
        if pad_len > 0:
            ids = torch.cat([ids, torch.full((pad_len,), pad_id, dtype=torch.long)], dim=0)
            mask = torch.cat([mask, torch.zeros(pad_len, dtype=torch.long)], dim=0)

        windows.append((ids, mask))

        if end == seq_len:
            break
        start += max_len - stride  # overlap

    return windows


@torch.inference_mode()
def infer_longtext(tokenizer, model, device, texts: List[str]) -> List[Tuple[str, float]]:
    """
    긴 기사 전체 반영: 슬라이딩 윈도우로 자른 후 확률 평균
    - 문서마다 개별 인코딩(배치 인코딩 X) → 길이 달라도 안전
    """
    if not texts:
        return []

    if tokenizer.pad_token is None:
        tokenizer.pad_token = tokenizer.eos_token or tokenizer.cls_token
    pad_id = tokenizer.pad_token_id or 0

    max_len = int(getattr(C, "MAX_SEQ_LEN_TOKENS", 256))
    stride = int(getattr(C, "SLIDING_STRIDE_TOKENS", max_len // 2))

    # 문서별로 개별 인코딩 (return_tensors 사용하지 않음)
    all_windows_ids: List[torch.Tensor] = []
    all_windows_mask: List[torch.Tensor] = []
    doc_slices: List[slice] = []
    idx = 0

    for text in texts:
        enc = tokenizer(
            text,
            return_tensors=None,   # 리스트로 받기
            padding=False,
            truncation=False,
        )
        ids_list = enc["input_ids"]
        mask_list = enc["attention_mask"]

        # fast 토크나이저는 리스트 내부가 이미 int 리스트
        ids = torch.tensor(ids_list, dtype=torch.long)
        mask = torch.tensor(mask_list, dtype=torch.long)

        wins = _windows_from_input_ids(ids, mask, max_len, stride, pad_id)
        n = len(wins)
        for wi, wm in wins:
            all_windows_ids.append(wi)
            all_windows_mask.append(wm)
        doc_slices.append(slice(idx, idx + n))
        idx += n

    # 윈도우들을 배치로 추론
    input_batch = torch.stack(all_windows_ids, dim=0).to(device)   # [N, max_len]
    attn_batch = torch.stack(all_windows_mask, dim=0).to(device)

    if device == "cuda":
        with torch.cuda.amp.autocast():
            logits = model(input_batch, attention_mask=attn_batch).logits
    else:
        logits = model(input_batch, attention_mask=attn_batch).logits

    probs = softmax(logits, dim=1)

    # 문서별 평균 확률 → 최종 레이블
    outs: List[Tuple[str, float]] = []
    for s in doc_slices:
        doc_probs = probs[s]           # [n_windows, num_labels]
        mean_p = doc_probs.mean(dim=0) # [num_labels]
        conf, pred = torch.max(mean_p, dim=0)
        label = LABELS[pred.item()] if 0 <= pred.item() < len(LABELS) else "neutral"
        outs.append((label, float(conf.item())))
    return outs
