import os
import logging
from pathlib import Path
from typing import Optional, Any, Tuple

from transformers import AutoModel, AutoTokenizer
import torch

logger = logging.getLogger(__name__)


class KoBERTLoader:
    _instance: Optional['KoBERTLoader'] = None
    _model: Optional[Any] = None
    _tokenizer: Optional[Any] = None

    # 기본 한국어 BERT
    _model_name = os.getenv("KOBERT_MODEL_ID", "klue/bert-base")

    def __new__(cls) -> 'KoBERTLoader':
        if cls._instance is None:
            cls._instance = super(KoBERTLoader, cls).__new__(cls)
        return cls._instance

    def __init__(self):
        if getattr(self, "_initialized", False):
            return

        # ✅ 모델 캐시 경로: 도커 볼륨(/models) 기본값, 환경변수로 오버라이드 가능
        cache_dir = os.getenv("KOBERT_CACHE_DIR", "/models/kobert")
        self._model_path = Path(cache_dir)
        self._model_path.mkdir(parents=True, exist_ok=True)

        # ✅ 디바이스 선택: USE_CUDA=true 이고 CUDA 사용 가능하면 cuda, 아니면 cpu
        use_cuda = os.getenv("USE_CUDA", "false").lower() == "true"
        if use_cuda and torch.cuda.is_available():
            self._device = torch.device("cuda")
            logger.info("KoBERTLoader device selected: CUDA (%s)", torch.cuda.get_device_name(0))
        else:
            self._device = torch.device("cpu")
            if use_cuda and not torch.cuda.is_available():
                logger.warning("USE_CUDA=true 이지만 CUDA 미사용 가능 → CPU로 폴백합니다.")
            else:
                logger.info("KoBERTLoader device selected: CPU")

        self._initialized = True

    # (선택) 간단 체크. HF 캐시는 스냅샷 구조라 파일명 고정 체크는 의미가 적음.
    def _cache_present(self) -> bool:
        return self._model_path.exists() and any(self._model_path.iterdir())

    def _download_if_needed(self) -> None:
        if self._cache_present():
            return
        logger.info("Downloading model '%s' to cache_dir=%s", self._model_name, self._model_path)
        try:
            # from_pretrained가 알아서 캐시에 저장
            _ = AutoTokenizer.from_pretrained(self._model_name, cache_dir=str(self._model_path), use_fast=True)
            _ = AutoModel.from_pretrained(self._model_name, cache_dir=str(self._model_path))
            logger.info("Model downloaded successfully")
        except Exception as e:
            logger.exception("Failed to download model: %s", e)
            raise

    def _load_model(self) -> None:
        logger.info("Loading tokenizer & model from cache_dir=%s", self._model_path)
        try:
            # 항상 캐시 우선, 없으면 자동 다운로드
            self._tokenizer = AutoTokenizer.from_pretrained(
                self._model_name,
                cache_dir=str(self._model_path),
                use_fast=True,
            )
            self._model = AutoModel.from_pretrained(
                self._model_name,
                cache_dir=str(self._model_path),
            )

            # 디바이스로 이동
            self._model.to(self._device)
            self._model.eval()

            if self._device.type == "cuda":
                torch.cuda.empty_cache()

            logger.info("KoBERT model loaded on device: %s", self._device)
        except Exception as e:
            logger.exception("Failed to load model: %s", e)
            raise

    def get_model_and_tokenizer(self) -> Tuple[Any, Any]:
        if self._model is None or self._tokenizer is None:
            # 캐시 없으면 받아오기 (네트워크 가능 환경 가정)
            self._download_if_needed()
            self._load_model()
        return self._model, self._tokenizer

    def get_model(self) -> Any:
        m, _ = self.get_model_and_tokenizer()
        return m

    def get_tokenizer(self) -> Any:
        _, t = self.get_model_and_tokenizer()
        return t

    @property
    def model_path(self) -> Path:
        return self._model_path

    def is_loaded(self) -> bool:
        return self._model is not None and self._tokenizer is not None

    @property
    def device(self) -> torch.device:
        return self._device
