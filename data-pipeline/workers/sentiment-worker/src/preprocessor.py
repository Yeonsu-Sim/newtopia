from typing import Optional
from config import SentimentConfig as C

def content_cut(s: Optional[str]) -> str:
    if not s:
        return ""
    s = s.strip()
    if len(s) > C.MAX_CONTENT_LEN:
        return s[:C.MAX_CONTENT_LEN]
    return s
