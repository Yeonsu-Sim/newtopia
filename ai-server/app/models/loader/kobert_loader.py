import os
import logging
from pathlib import Path
from typing import Optional, Any
from transformers import AutoModel, AutoTokenizer
import torch

logger = logging.getLogger(__name__)


class KoBERTLoader:
    _instance: Optional['KoBERTLoader'] = None
    _model: Optional[Any] = None
    _tokenizer: Optional[Any] = None
    _model_name = "klue/bert-base"  # 더 안정적인 한국어 BERT 모델
    
    def __new__(cls) -> 'KoBERTLoader':
        if cls._instance is None:
            cls._instance = super(KoBERTLoader, cls).__new__(cls)
        return cls._instance
    
    def __init__(self):
        if not hasattr(self, '_initialized'):
            self._initialized = True
            self._model_path = Path(__file__).parent / "kobert_cache"
            self._model_path.mkdir(exist_ok=True)
    
    def _check_model_exists(self) -> bool:
        model_files = [
            "config.json",
            "pytorch_model.bin",
            "tokenizer_config.json",
            "tokenizer.json",
            "vocab.txt"
        ]
        
        return all((self._model_path / file).exists() for file in model_files)
    
    def _download_model(self) -> None:
        logger.info(f"Downloading KoBERT model to {self._model_path}")
        
        try:
            # KLUE BERT 모델 다운로드
            tokenizer = AutoTokenizer.from_pretrained(
                self._model_name,
                cache_dir=str(self._model_path)
            )
            
            model = AutoModel.from_pretrained(
                self._model_name,
                cache_dir=str(self._model_path)
            )
            
            tokenizer.save_pretrained(self._model_path)
            model.save_pretrained(self._model_path)
            
            logger.info("KoBERT model downloaded successfully")
            
        except Exception as e:
            logger.error(f"Failed to download KoBERT model: {e}")
            raise
    
    def _load_model(self) -> None:
        logger.info("Loading KoBERT model and tokenizer")
        
        try:
            # GPU 강제 사용
            if not torch.cuda.is_available():
                raise RuntimeError("CUDA is not available. GPU is required for this service.")
            
            device = torch.device('cuda')
            logger.info(f"Using GPU device: {torch.cuda.get_device_name()}")
            
            self._tokenizer = AutoTokenizer.from_pretrained(str(self._model_path))
            self._model = AutoModel.from_pretrained(str(self._model_path))
            self._model.to(device)
            self._model.eval()
            
            # GPU 메모리 정리
            torch.cuda.empty_cache()
            
            logger.info(f"KoBERT model loaded successfully on {device}")
            
        except Exception as e:
            logger.error(f"Failed to load KoBERT model: {e}")
            raise
    
    def get_model_and_tokenizer(self) -> tuple[Any, Any]:
        if self._model is None or self._tokenizer is None:
            if not self._check_model_exists():
                logger.info("KoBERT model not found locally, downloading...")
                self._download_model()
            
            self._load_model()
        
        return self._model, self._tokenizer
    
    def get_model(self) -> Any:
        model, _ = self.get_model_and_tokenizer()
        return model
    
    def get_tokenizer(self) -> Any:
        _, tokenizer = self.get_model_and_tokenizer()
        return tokenizer
    
    @property
    def model_path(self) -> Path:
        return self._model_path
    
    def is_loaded(self) -> bool:
        return self._model is not None and self._tokenizer is not None