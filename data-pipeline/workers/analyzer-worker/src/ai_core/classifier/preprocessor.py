# file: src/ai_core/classifier/preprocessor.py
import re
from typing import List


class TextPreprocessor:
    def __init__(self):
        self.korean_pattern = re.compile(r'[가-힣]+')
        self.english_pattern = re.compile(r'[a-zA-Z]+')
        self.number_pattern = re.compile(r'\d+')
        self.special_chars = re.compile(r'[^\w\s가-힣]')
    
    def clean_text(self, text: str) -> str:
        if not text:
            return ""
        text = re.sub(r'\s+', ' ', text)
        text = re.sub(r'[^\w\s가-힣.,!?]', '', text)
        text = text.strip()
        return text
    
    def extract_keywords(self, text: str, min_length: int = 2) -> List[str]:
        text = self.clean_text(text)
        korean_words = self.korean_pattern.findall(text)
        korean_words = [word for word in korean_words if len(word) >= min_length]
        english_words = self.english_pattern.findall(text)
        english_words = [word.lower() for word in english_words if len(word) >= min_length]
        return korean_words + english_words
    
    def prepare_for_embedding(self, title: str, article: str) -> str:
        combined_text = f"{title} {article}"
        cleaned_text = self.clean_text(combined_text)
        if len(cleaned_text) > 500:
            cleaned_text = cleaned_text[:500]
        return cleaned_text
    
    def calculate_relevance_score(self, similarity: float) -> int:
        if similarity < 0.3:
            return 0
        normalized_score = int(((similarity - 0.3) / 0.7) * 99 + 1)
        return min(100, max(1, normalized_score))
