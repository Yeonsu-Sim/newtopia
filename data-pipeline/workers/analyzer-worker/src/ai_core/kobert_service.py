# file: data-pipeline/workers/analyzer-worker/src/ai_core/kobert_service.py
import os
import logging
from typing import List, Dict, Any
import numpy as np
import torch

from .kobert_loader import KoBERTLoader

logger = logging.getLogger(__name__)


class ModelError(Exception):
    """모델 로딩/인코딩 과정에서 사용하는 로컬 예외."""
    pass


class KoBERTService:
    def __init__(self):
        self.loader = KoBERTLoader()
        self.is_ready: bool = False
        self.device: torch.device | None = None
        self.hidden_size: int | None = None

    async def initialize(self) -> None:
        """모델/토크나이저 로딩 및 디바이스 세팅."""
        try:
            logger.info("Initializing KoBERT service")
            model, tokenizer = self.loader.get_model_and_tokenizer()

            # 디바이스 결정 (환경변수 우선)
            want = os.getenv("KOBERT_DEVICE", "").lower()
            if want in {"cuda", "gpu"} and torch.cuda.is_available():
                self.device = torch.device("cuda")
            elif want in {"cpu"}:
                self.device = torch.device("cpu")
            else:
                self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

            # 모델을 디바이스로 이동 + eval 모드
            model.to(self.device)
            model.eval()

            # 히든차원 캐시(통계/검증용)
            if hasattr(model, "config") and hasattr(model.config, "hidden_size"):
                self.hidden_size = int(model.config.hidden_size)

            self.is_ready = True
            logger.info("KoBERT service initialized successfully on %s", self.device)
        except Exception as e:
            self.is_ready = False
            logger.exception("Failed to initialize KoBERT service: %s", e)
            raise

    def health_check(self) -> Dict[str, Any]:
        try:
            ml, _ = self.loader.get_model_and_tokenizer()
            loaded = self.loader.is_loaded() and ml is not None
        except Exception:
            loaded = False
        return {
            "service_name": "KoBERT Service",
            "is_ready": self.is_ready,
            "model_loaded": loaded,
            "device": str(self.device) if self.device else None,
            "hidden_size": self.hidden_size,
        }

    # ---------- 내부 헬퍼 ----------

    def _ensure_ready(self):
        if not self.is_ready:
            raise ModelError("KoBERT service not ready. Initialize first.")

    def _tokenize(self, tokenizer, texts: List[str]):
        """
        안전한 토크나이즈: truncation/padding 사용.
        - model_max_length가 너무 크게 설정된 토크나이저가 있으므로 적절한 상한을 둔다.
        """
        # 우선순위: env → 모델 설정 → 합리적 기본값(256)
        max_len_env = os.getenv("KOBERT_MAX_LENGTH")
        if max_len_env and max_len_env.isdigit():
            max_len = int(max_len_env)
        elif hasattr(tokenizer, "model_max_length") and tokenizer.model_max_length and tokenizer.model_max_length < 100000:
            max_len = int(tokenizer.model_max_length)
        else:
            max_len = 256

        # 입력 정리
        cleaned = [t if (t and t.strip()) else "[UNK]" for t in texts]

        return tokenizer(
            cleaned,
            return_tensors="pt",
            max_length=max_len,
            truncation=True,
            padding=True,
            add_special_tokens=True,
        )

    def _forward(self, model, inputs) -> torch.Tensor:
        """
        순전파: CLS 임베딩을 반환 (shape: [batch, hidden]).
        """
        with torch.no_grad():
            outputs = model(**inputs)
            # 일반적으로 last_hidden_state: [batch, seq, hidden]
            # CLS 토큰은 index 0
            cls = outputs.last_hidden_state[:, 0, :]
        return cls

    # ---------- 퍼블릭 API ----------

    def encode_text(self, text: str) -> np.ndarray:
        """
        단일 텍스트 임베딩을 1D numpy array로 반환 (shape: [hidden]).
        """
        self._ensure_ready()
        try:
            model, tokenizer = self.loader.get_model_and_tokenizer()
            inputs = self._tokenize(tokenizer, [text])
            # 디바이스 이동
            inputs = {k: v.to(self.device) for k, v in inputs.items()}
            if next(model.parameters()).device != self.device:
                model.to(self.device)

            emb: torch.Tensor = self._forward(model, inputs)  # [1, hidden]
            vec = emb[0].detach().cpu().numpy()  # [hidden]
            return vec.astype(np.float32, copy=False)
        except Exception as e:
            # CUDA가 있다면 메모리 정리
            if torch.cuda.is_available():
                torch.cuda.empty_cache()
            logger.exception("Error encoding text: %s", e)
            raise ModelError(f"Text encoding failed: {e}")

    def encode_batch(self, texts: List[str]) -> np.ndarray:
        """
        여러 텍스트를 한 번에 인코딩. 2D numpy array 반환 (shape: [batch, hidden]).
        """
        self._ensure_ready()
        if not isinstance(texts, list) or len(texts) == 0:
            raise ModelError("encode_batch expects a non-empty list of strings.")
        try:
            model, tokenizer = self.loader.get_model_and_tokenizer()
            inputs = self._tokenize(tokenizer, texts)
            inputs = {k: v.to(self.device) for k, v in inputs.items()}
            if next(model.parameters()).device != self.device:
                model.to(self.device)

            emb: torch.Tensor = self._forward(model, inputs)  # [batch, hidden]
            return emb.detach().cpu().numpy().astype(np.float32, copy=False)
        except Exception as e:
            if torch.cuda.is_available():
                torch.cuda.empty_cache()
            logger.exception("Error encoding batch: %s", e)
            raise ModelError(f"Batch text encoding failed: {e}")

    def similarity(self, text1: str, text2: str) -> float:
        """
        코사인 유사도 반환 ([-1, 1]). 입력이 비정상일 경우 0.0 반환.
        """
        self._ensure_ready()
        try:
            v1 = self.encode_text(text1).reshape(-1)
            v2 = self.encode_text(text2).reshape(-1)
            n1 = np.linalg.norm(v1)
            n2 = np.linalg.norm(v2)
            if n1 == 0.0 or n2 == 0.0:
                return 0.0
            return float(np.dot(v1, v2) / (n1 * n2))
        except ModelError:
            # 이미 로깅됨
            return 0.0
        except Exception as e:
            logger.exception("Error calculating similarity: %s", e)
            return 0.0

    def get_embeddings_info(self, text: str) -> Dict[str, Any]:
        """
        임베딩 통계 정보 반환.
        """
        self._ensure_ready()
        try:
            v = self.encode_text(text)
            return {
                "text": text,
                "embedding_shape": list(v.shape),
                "embedding_dimension": int(v.size),
                "embedding_mean": float(np.mean(v)),
                "embedding_std": float(np.std(v)),
                "embedding_min": float(np.min(v)),
                "embedding_max": float(np.max(v)),
                "device": str(self.device) if self.device else None,
            }
        except Exception as e:
            logger.exception("Error getting embedding info: %s", e)
            raise ModelError(f"Embedding info extraction failed: {e}")


# 싱글톤 인스턴스
kobert_service = KoBERTService()
