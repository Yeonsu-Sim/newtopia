import logging
from typing import List, Dict, Any, Union
import numpy as np
import torch
from fastapi import HTTPException

from ..loader.kobert_loader import KoBERTLoader

logger = logging.getLogger(__name__)


class KoBERTService:
    def __init__(self):
        self.loader = KoBERTLoader()
        self.is_ready = False
    
    async def initialize(self) -> None:
        try:
            logger.info("Initializing KoBERT service")
            # KoBERTLoader는 이미 자동으로 초기화됨 (싱글톤)
            model, tokenizer = self.loader.get_model_and_tokenizer()
            self.is_ready = True
            logger.info("KoBERT service initialized successfully")
        except Exception as e:
            logger.error(f"Failed to initialize KoBERT service: {str(e)}")
            self.is_ready = False
            raise
    
    def health_check(self) -> Dict[str, Any]:
        return {
            "service_name": "KoBERT Service",
            "is_ready": self.is_ready,
            "model_loaded": self.loader.is_loaded()
        }
    
    def encode_text(self, text: str) -> np.ndarray:
        if not self.is_ready:
            raise HTTPException(
                status_code=503, 
                detail="KoBERT service not ready. Please wait for model initialization."
            )
        
        try:
            model, tokenizer = self.loader.get_model_and_tokenizer()
            device = torch.device('cuda')  # GPU 강제 사용
            
            # 텍스트 전처리 - 빈 문자열 체크
            if not text or not text.strip():
                text = "[UNK]"  # 빈 텍스트 대체
            
            # 토큰화 - 매우 안전한 방법
            inputs = tokenizer(
                text,
                return_tensors="pt",
                max_length=256,  # 더 짧은 길이로 제한
                truncation=True,
                padding=True,  # 동적 패딩
                add_special_tokens=True
            )
            
            # 모델의 실제 vocab 크기 확인
            model_vocab_size = model.config.vocab_size
            logger.info(f"Model vocab size: {model_vocab_size}, Tokenizer vocab size: {len(tokenizer)}")
            
            # 토큰 ID 강제 클리핑 - 모델 vocab 크기에 맞춤
            input_ids = inputs['input_ids']
            max_token_id = input_ids.max().item()
            
            if max_token_id >= model_vocab_size:
                logger.warning(f"Token ID {max_token_id} exceeds model vocab size {model_vocab_size}. Clipping...")
                # 모델 vocab 크기에 맞게 강제 클리핑
                inputs['input_ids'] = torch.clamp(input_ids, 0, model_vocab_size - 1)
                
            # 토큰 시퀀스 길이도 확인
            seq_len = inputs['input_ids'].size(1)
            if seq_len > 512:
                logger.warning(f"Sequence length {seq_len} too long. Truncating...")
                inputs['input_ids'] = inputs['input_ids'][:, :512]
                inputs['attention_mask'] = inputs['attention_mask'][:, :512]
            
            # 입력 텐서를 GPU로 이동
            inputs = {k: v.to(device) for k, v in inputs.items()}
            
            # 모델이 실제로 GPU에 있는지 확인하고 강제 이동
            if not next(model.parameters()).is_cuda:
                logger.warning("Model not on CUDA, moving to GPU...")
                model.to(device)
            
            # 모든 입력 텐서가 GPU에 있는지 재확인
            for key, tensor in inputs.items():
                if not tensor.is_cuda:
                    logger.warning(f"Moving {key} to CUDA")
                    inputs[key] = tensor.to(device)
            
            # 모델 추론
            with torch.no_grad():
                outputs = model(**inputs)
                # [CLS] 토큰의 임베딩 사용 (첫 번째 토큰)
                embeddings = outputs.last_hidden_state[:, 0, :].detach()
                
            # CPU로 이동하고 numpy 변환
            return embeddings.cpu().numpy()
            
        except Exception as e:
            logger.error(f"Error encoding text: {str(e)}")
            # GPU 메모리 정리
            if torch.cuda.is_available():
                torch.cuda.empty_cache()
            raise HTTPException(status_code=500, detail=f"Text encoding failed: {str(e)}")
    
    def encode_batch(self, texts: List[str]) -> List[np.ndarray]:
        if not self.is_ready:
            raise HTTPException(
                status_code=503, 
                detail="KoBERT service not ready. Please wait for model initialization."
            )
        
        try:
            results = []
            for text in texts:
                embedding = self.encode_text(text)
                results.append(embedding)
            return results
        except Exception as e:
            logger.error(f"Error encoding batch texts: {str(e)}")
            raise HTTPException(status_code=500, detail=f"Batch text encoding failed: {str(e)}")
    
    def similarity(self, text1: str, text2: str) -> float:
        if not self.is_ready:
            raise HTTPException(
                status_code=503, 
                detail="KoBERT service not ready. Please wait for model initialization."
            )
        
        try:
            embedding1 = self.encode_text(text1)
            embedding2 = self.encode_text(text2)
            
            # 코사인 유사도 계산
            embedding1_flat = embedding1.flatten()
            embedding2_flat = embedding2.flatten()
            
            dot_product = np.dot(embedding1_flat, embedding2_flat)
            norm1 = np.linalg.norm(embedding1_flat)
            norm2 = np.linalg.norm(embedding2_flat)
            
            if norm1 == 0 or norm2 == 0:
                return 0.0
            
            cosine_sim = dot_product / (norm1 * norm2)
            return float(cosine_sim)
            
        except Exception as e:
            logger.error(f"Error calculating similarity: {str(e)}")
            raise HTTPException(status_code=500, detail=f"Similarity calculation failed: {str(e)}")
    
    def get_embeddings_info(self, text: str) -> Dict[str, Any]:
        if not self.is_ready:
            raise HTTPException(
                status_code=503, 
                detail="KoBERT service not ready. Please wait for model initialization."
            )
        
        try:
            embedding = self.encode_text(text)
            
            return {
                "text": text,
                "embedding_shape": embedding.shape,
                "embedding_dimension": embedding.size,
                "embedding_mean": float(np.mean(embedding)),
                "embedding_std": float(np.std(embedding)),
                "embedding_min": float(np.min(embedding)),
                "embedding_max": float(np.max(embedding))
            }
            
        except Exception as e:
            logger.error(f"Error getting embedding info: {str(e)}")
            raise HTTPException(status_code=500, detail=f"Embedding info extraction failed: {str(e)}")


kobert_service = KoBERTService()