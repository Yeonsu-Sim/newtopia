from typing import Dict, Any

REQUIRED_FIELDS = ("source_url", "published_at", "title", "content")

def is_minimum_valid(doc: Dict[str, Any]) -> bool:
    if not isinstance(doc, dict):
        return False
    for f in REQUIRED_FIELDS:
        if f not in doc:
            return False
    return True
