# FastAPI 뉴스 분류 서비스

sentence-transformers의 다국어 모델을 사용하여 뉴스 기사를 4개 메인 카테고리로 분류하는 FastAPI 서비스입니다.

## 주요 기능

- **다국어 뉴스 분류**: 한국어 뉴스 기사를 경제, 국방, 민생, 환경 카테고리로 분류
- **감정 분석**: 각 카테고리별 -100~+100 점수로 감정/영향도 분석
- **세부 카테고리 분석**: 각 메인 카테고리 내 서브카테고리별 가중치 제공
- **배치 처리**: 여러 뉴스 기사 일괄 분류 지원

## 사용 모델

- `sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2`
- 다국어 지원 (한국어 포함)
- 의미적 유사도 기반 분류

## 분류 카테고리

### 1. Economy (경제)
- **macroeconomy**: GDP, 인플레이션, 금리, 경제성장률
- **fiscalPolicy**: 예산, 세금, 정부지출, 재정정책  
- **financialMarket**: 증시, 채권, IPO, 투자, 금융상품
- **tradeInvestment**: 수출입, FDI, 무역협정, 국제투자

### 2. Defense (국방)
- **militaryStrength**: 군사력, 무기체계, 국방예산
- **diplomacy**: 외교관계, 국제협력, 외교정책
- **cybersecurity**: 사이버보안, 정보보호, 해킹
- **allianceSecurity**: 동맹관계, 집단안보, 군사협력

### 3. PublicSentiment (민생)
- **livingStandard**: 생활수준, 물가, 소득, 주거
- **welfare**: 복지정책, 사회보장, 의료, 교육
- **socialConflict**: 사회갈등, 시위, 노사분규
- **publicSafety**: 치안, 범죄, 안전, 재해대응

### 4. Environment (환경)
- **climateChange**: 기후변화, 온실가스, 탄소중립
- **greenIndustry**: 친환경산업, 재생에너지, 그린테크
- **pollutionControl**: 대기오염, 수질오염, 환경규제
- **naturalResource**: 자원관리, 생태계, 보존

## 환경 설정

### 1. Conda 환경 활성화
```bash
conda activate nlp
```

### 2. 의존성 설치
```bash
pip install -r requirements.txt
```

### 3. 자동 설정 (옵션)
```bash
python setup_env.py
```

## 서버 실행

### 방법 1: Python 직접 실행
```bash
python main.py
```

### 방법 2: uvicorn 사용
```bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

## API 엔드포인트

### 기본 정보
- `GET /` - 서비스 정보
- `GET /health` - 헬스 체크
- `GET /model/info` - 모델 정보

### 분류 서비스
- `POST /classify` - 단일 뉴스 분류
- `POST /classify/batch` - 배치 뉴스 분류
- `GET /categories` - 지원 카테고리 정보

### 웹 인터페이스
- `http://localhost:8000/docs` - Swagger UI
- `http://localhost:8000/redoc` - ReDoc

## 사용 예제

### 단일 뉴스 분류
```python
import requests

url = "http://localhost:8000/classify"
data = {
    "news": {
        "title": "한국은행, 기준금리 0.25%p 인상",
        "content": "한국은행이 물가상승 압력에 대응하기 위해 기준금리를 3.25%에서 3.5%로 인상했다..."
    }
}

response = requests.post(url, json=data)
result = response.json()
```

### 응답 형식
```json
{
  "result": {
    "economy": {
      "score": 85,
      "subcategories": {
        "macroeconomy": 0.8,
        "fiscalPolicy": 0.2
      }
    },
    "defense": {
      "score": 0,
      "subcategories": {}
    },
    "publicSentiment": {
      "score": -15,
      "subcategories": {
        "livingStandard": 0.3
      }
    },
    "environment": {
      "score": 0,
      "subcategories": {}
    }
  },
  "processing_time": 0.1234
}
```

## 프로젝트 구조

```
ai-poc/
├── main.py                 # FastAPI 앱
├── requirements.txt        # 의존성 패키지
├── setup_env.py           # 환경 설정 스크립트
├── Claude.md              # 프로젝트 설정
├── data/
│   └── ssafy_dataset_news_2019.csv  # 뉴스 데이터셋
├── config/
│   ├── __init__.py
│   └── categories.py      # 카테고리 정의
├── models/
│   ├── __init__.py
│   ├── classifier.py      # 분류 서비스
│   └── schemas.py         # Pydantic 모델
└── utils/
    ├── __init__.py
    └── preprocessor.py     # 텍스트 전처리
```

## 성능 및 특징

- **처리 속도**: 단일 뉴스 분류 약 0.1-0.5초
- **정확도**: 의미 기반 유사도로 높은 정확도
- **확장성**: 새로운 카테고리 쉽게 추가 가능
- **다국어**: 한국어 외 다른 언어도 지원