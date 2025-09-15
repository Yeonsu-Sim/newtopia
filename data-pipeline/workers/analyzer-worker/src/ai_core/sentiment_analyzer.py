import os
import logging
import asyncio
from typing import Dict, Optional

logger = logging.getLogger(__name__)

# transformers/torch는 선택적 의존성: 없으면 자동으로 fallback 사용
try:
    import torch
    from transformers import AutoTokenizer, AutoModelForSequenceClassification
    _TRANSFORMERS_AVAILABLE = True
except Exception as e:
    logger.warning("Transformers/Torch not available. Will use fallback sentiment.", exc_info=False)
    _TRANSFORMERS_AVAILABLE = False


class SentimentAnalyzer:
    """
    - 목적: 한국어 뉴스 본문/제목에 대한 감정(positive/neutral/negative) 확률 반환
    - 사용:
        analyzer = SentimentAnalyzer(model_name="YOUR_MODEL_OR_LOCAL_DIR")
        await analyzer.initialize()
        scores = analyzer.analyze(text)  # {"positive":0.x, "neutral":0.x, "negative":0.x}
    """

    def __init__(
        self,
        model_name: Optional[str] = None,
        max_length: int = 256,
        device: Optional[str] = None,
        concurrency: int = 8,
    ):
        self.model_name = model_name or os.getenv("SENTIMENT_MODEL_NAME", "").strip() or None
        self.max_length = int(os.getenv("SENTIMENT_MAX_LEN", str(max_length)))
        self._device_str = device or os.getenv("SENTIMENT_DEVICE", "").strip() or None
        self._is_initialized = False

        # 모델/토크나이저
        self._tokenizer = None
        self._model = None
        self._device = None

        # 라벨 인덱스 매핑: {"negative": i, "neutral": j, "positive": k}
        self._label_index: Optional[Dict[str, int]] = None

        # 버전/메타
        self.model_version = self.model_name or "kobert-fallback"

        # 동시 처리 제한(메모리 보호)
        self._sem = asyncio.Semaphore(int(os.getenv("SENTIMENT_MAX_CONCURRENCY", str(concurrency))))

        # 간단한 fallback 사전(아주 소량)
        self._pos_lex = {"호재", "개선", "상승", "최고", "호전", "긍정", "급등", "반등", "혜택", "성장", "확대", "선정", "수상", "돌파"}
        self._neg_lex = {"악재", "하락", "추락", "최저", "악화", "부정", "급락", "적자", "감소", "축소", "폐쇄", "파산", "논란", "분쟁"}

    async def initialize(self):
        """
        - 가능한 경우 HF 모델 로딩(토크나이저/모델/디바이스/레이블 맵핑)
        - 실패하거나 설정이 없으면 fallback로 동작
        """
        if self._is_initialized:
            return

        if _TRANSFORMERS_AVAILABLE and self.model_name:
            try:
                logger.info(f"Loading sentiment model: {self.model_name}")
                self._tokenizer = AutoTokenizer.from_pretrained(self.model_name)
                self._model = AutoModelForSequenceClassification.from_pretrained(self.model_name)

                # device 설정
                if self._device_str:
                    self._device = torch.device(self._device_str)
                else:
                    self._device = torch.device("cuda") if torch.cuda.is_available() else torch.device("cpu")

                self._model.to(self._device)
                self._model.eval()

                # 라벨 맵핑 추론
                self._label_index = self._infer_label_index()
                if not self._label_index:
                    logger.warning("Could not infer label mapping. Falling back to 2/3-class heuristics.")
                logger.info(f"Sentiment label index: {self._label_index}")

            except Exception as e:
                logger.exception("Failed to load transformers model. Will use fallback sentiment.")
                self._tokenizer = None
                self._model = None
                self._device = None
                self._label_index = None

        self._is_initialized = True

    # --- 내부 유틸 ---

    def _infer_label_index(self) -> Optional[Dict[str, int]]:
        """
        모델 config.id2label / label2id에서 긍/중/부 키워드로 인덱스를 추정.
        - 우선순위: env SENTIMENT_LABEL_ORDER > config.id2label 탐색
        - 예: SENTIMENT_LABEL_ORDER="negative,neutral,positive"
        """
        # 1) 사용자가 명시한 라벨 순서
        order_env = os.getenv("SENTIMENT_LABEL_ORDER", "").strip()
        if order_env:
            parts = [p.strip().lower() for p in order_env.split(",")]
            # 받아온 순서대로 0..n-1 매핑
            try:
                idx = {name: i for i, name in enumerate(parts)}
                # 키 정규화
                return {
                    "negative": idx.get("negative", idx.get("neg")),
                    "neutral": idx.get("neutral", idx.get("neu")),
                    "positive": idx.get("positive", idx.get("pos")),
                }
            except Exception:
                pass

        # 2) config 기반 자동 탐색
        try:
            id2label = getattr(self._model.config, "id2label", None)
            if not id2label:
                return None
            labels = [str(id2label[i]).lower() for i in range(len(id2label))]

            def _find(*keys):
                for i, name in enumerate(labels):
                    for k in keys:
                        if k in name:
                            return i
                return None

            neg = _find("neg", "negative", "부정")
            neu = _find("neu", "neutral", "중립")
            pos = _find("pos", "positive", "긍정")

            # 일부 2클래스(neg/pos) 모델 대응: neu가 없으면 None 유지
            return {"negative": neg, "neutral": neu, "positive": pos}
        except Exception:
            return None

    def _softmax(self, logits):
        import math
        m = max(logits)
        exps = [math.exp(x - m) for x in logits]
        s = sum(exps)
        return [x / s for x in exps]

    # --- 공개 API ---

    def analyze(self, text: str) -> Dict[str, float]:
        """
        입력 텍스트에 대한 감정 점수 반환.
        - 가능하면 HF 모델 사용
        - 아니면 간단한 사전 기반 점수로 fallback
        """
        text = (text or "").strip()
        if not text:
            return {"positive": 0.0, "neutral": 1.0, "negative": 0.0}

        # Transformers 경로
        if _TRANSFORMERS_AVAILABLE and self._tokenizer and self._model:
            try:
                inputs = self._tokenizer(
                    text,
                    max_length=self.max_length,
                    truncation=True,
                    padding=False,
                    return_tensors="pt",
                )
                inputs = {k: v.to(self._device) for k, v in inputs.items()}

                with torch.no_grad():
                    outputs = self._model(**inputs)
                    logits = outputs.logits.squeeze().tolist()
                    probs = self._softmax(logits)

                # 라벨 인덱스가 3클래스일 때
                if self._label_index and all(v is not None for v in self._label_index.values()):
                    neg = probs[self._label_index["negative"]]
                    neu = probs[self._label_index["neutral"]]
                    pos = probs[self._label_index["positive"]]
                    s = neg + neu + pos or 1.0
                    return {"positive": pos / s, "neutral": neu / s, "negative": neg / s}

                # 2클래스(neg/pos) 대응: neutral=0
                if len(probs) == 2:
                    # id2label 추론 실패 시 [neg, pos] 가정
                    neg, pos = probs[0], probs[1]
                    return {"positive": float(pos), "neutral": 0.0, "negative": float(neg)}

                # 3클래스지만 라벨 추론 실패: 최대값을 positive로 가정하고 나머지는 균등(보수적)
                if len(probs) == 3:
                    # 단순 휴리스틱: index=0,1,2 중 argmax를 positive로 보고 나머지 절반을 neutral/negative에 분배
                    mx_idx = max(range(3), key=lambda i: probs[i])
                    pos = probs[mx_idx]
                    rest = 1.0 - pos
                    return {"positive": float(pos), "neutral": float(rest / 2), "negative": float(rest / 2)}

                # 기타 케이스: 안전 기본값
                return {"positive": 0.0, "neutral": 1.0, "negative": 0.0}

            except Exception:
                logger.exception("Transformer sentiment inference failed. Falling back to lexicon.")
                # 아래로 폴백

        # Fallback: 아주 단순한 한국어 키워드 기반
        return self._fallback_scores(text)

    # --- 폴백 로직(간단 사전) ---

    def _fallback_scores(self, text: str) -> Dict[str, float]:
        t = text.lower()
        pos_hits = sum(1 for w in self._pos_lex if w in t)
        neg_hits = sum(1 for w in self._neg_lex if w in t)

        if pos_hits == 0 and neg_hits == 0:
            return {"positive": 0.1, "neutral": 0.8, "negative": 0.1}

        total = pos_hits + neg_hits
        pos = pos_hits / total
        neg = neg_hits / total
        neu = max(0.0, 1.0 - (pos + neg) * 0.8)  # 약간 중립 가중
        # 정규화
        s = pos + neu + neg or 1.0
        return {"positive": pos / s, "neutral": neu / s, "negative": neg / s}
