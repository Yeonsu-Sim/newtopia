import re
from typing import List


class TextPreprocessor:
    def __init__(self):
        self.korean_pattern = re.compile(r'[가-힣]+')
        self.english_pattern = re.compile(r'[a-zA-Z]+')
        self.number_pattern = re.compile(r'\d+')
        self.special_chars = re.compile(r'[^\w\s가-힣]')
    
    def clean_text(self, text: str) -> str:
        """기본적인 텍스트 정리"""
        if not text:
            return ""
        
        # 불필요한 공백 제거
        text = re.sub(r'\s+', ' ', text)
        # 특수문자 제거 (구두점은 유지)
        text = re.sub(r'[^\w\s가-힣.,!?]', '', text)
        # 앞뒤 공백 제거
        text = text.strip()
        
        return text
    
    def extract_keywords(self, text: str, min_length: int = 2) -> List[str]:
        """텍스트에서 키워드 추출"""
        text = self.clean_text(text)
        
        # 한글 단어 추출
        korean_words = self.korean_pattern.findall(text)
        korean_words = [word for word in korean_words if len(word) >= min_length]
        
        # 영어 단어 추출
        english_words = self.english_pattern.findall(text)
        english_words = [word.lower() for word in english_words if len(word) >= min_length]
        
        return korean_words + english_words
    
    def prepare_for_embedding(self, title: str, article: str) -> str:
        """임베딩을 위한 텍스트 준비"""
        # 제목과 내용을 결합
        combined_text = f"{title} {article}"
        
        # 텍스트 정리
        cleaned_text = self.clean_text(combined_text)
        
        # 너무 긴 텍스트는 자르기 (모델 최대 길이 고려)
        if len(cleaned_text) > 500:
            cleaned_text = cleaned_text[:500]
        
        return cleaned_text
    
    def calculate_relevance_score(self, similarity: float) -> int:
        """유사도 기반 관련성 점수 계산"""
        # 임계값 이하는 관련 없음으로 처리
        if similarity < 0.3:
            return 0
        
        # 0.3~1.0 범위를 1~100으로 변환
        normalized_score = int(((similarity - 0.3) / 0.7) * 99 + 1)
        return min(100, max(1, normalized_score))