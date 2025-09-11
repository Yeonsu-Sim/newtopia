export const exampleStartResponse = {
  "status": "success",
  "data": {
	  "game": {
		  "gameId": "game-1",
			"countryName": "광주5반",
	    "turn": {
	      "number": 1,
	      "countryStats": { "eco": 50, "mil": 50, "opi": 50, "env": 50 },
	      "card": {
	        "cardId": "card-1",
	        "type": "ORIGIN",
	        "npc": {
		        "name": "국방부 장관1",
		        "imageUrl": "s3/image/npc/1"
	        },
	        "content": "주변국의 위협에 맞서 최신예 전투기 도입을 서둘러야 합니다.",
	        "choices": [
	          { "code": "A", "label": "국가 안보가 최우선" },
	          { "code": "B", "label": "평화적 해결이 우선" }
	        ],
	        "relatedArticle": {
		        "title": "산업은행 회장에 박상진 전 산은 준법감시인 내정(종합)",
		        "url": "https://www.yna.co.kr/view/AKR20250909137051002?section=economy/all"
	        }
	      }
      }
    }
  },
  "message": "게임이 시작되었습니다.",
  "error": null
};

export const exampleChoiceResponse = {
  "status": "success",
  "data": {
    "applied": {
      "turnNumber": 1,
      "choiceCode": "A",
      "countryStats": {
        "after": { "eco": 62, "mil": 41, "opi": 85, "env": 66 },
        "delta": { "eco": 0, "mil": -5, "opi": 12, "env": 4 }
      }
    },
    "gameState": {
      "gameOver": false,
      "gameResultId": null,
      "ending": null
    },
    "nextTurn": {
      "number": 2,
      "countryStats": { "eco": 62, "mil": 41, "opi": 85, "env": 66 },
      "card": {
	      "cardId": "card-1",
        "type": "CONSEQUENCE",
        "npc": {
	        "name": "국방부 장관2",
	        "imageUrl": "s3/image/npc/1"
        },
        "content": "주변국의 위협에 맞서 최신예 전투기 도입을 서둘러야 합니다.",
        "choices": [
          { "code": "A", "label": "국가 안보가 최우선" },
          { "code": "B", "label": "평화적 해결이 우선" }
        ],
	      "relatedArticle": {
	        "title": "산업은행 회장에 박상진 전 산은 준법감시인 내정(종합)",
	        "url": "https://www.yna.co.kr/view/AKR20250909137051002?section=economy/all"
        }
      }
    }
  },
  "message": "답변 선택을 완료했습니다.",
  "error": null
};
