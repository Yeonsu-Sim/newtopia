export const exampleStartResponse = {
  status: 'success',
  data: {
    game: {
      gameId: 'game-1',
      countryName: '광주5반',
      turn: {
        number: 1,
        countryStats: { eco: 50, mil: 50, opi: 50, env: 50 },
        card: {
          cardId: 'card-1',
          type: 'ORIGIN',
          npc: {
            name: '국방부 장관1',
            imageUrl: 's3/image/npc/1',
          },
          content: '주변국의 위협에 맞서 최신예 전투기 도입을 서둘러야 합니다.',
          choices: [
            { code: 'A', label: '국가 안보가 최우선' },
            { code: 'B', label: '평화적 해결이 우선' },
          ],
          relatedArticle: {
            title: '산업은행 회장에 박상진 전 산은 준법감시인 내정(종합)',
            url: 'https://www.yna.co.kr/view/AKR20250909137051002?section=economy/all',
          },
        },
      },
    },
  },
  message: '게임이 시작되었습니다.',
  error: null,
}

export const exampleChoiceResponse = {
  status: 'success',
  data: {
    applied: {
      turnNumber: 1,
      choiceCode: 'A',
      countryStats: {
        after: { eco: 62, mil: 41, opi: 85, env: 66 },
        delta: { eco: 0, mil: -5, opi: 12, env: 4 },
      },
    },
    gameState: {
      gameOver: false,
      gameResultId: null,
      ending: null,
    },
    nextTurn: {
      number: 2,
      countryStats: { eco: 62, mil: 41, opi: 85, env: 66 },
      card: {
        cardId: 'card-1',
        type: 'CONSEQUENCE',
        npc: {
          name: '국방부 장관2',
          imageUrl: 's3/image/npc/1',
        },
        content: '주변국의 위협에 맞서 최신예 전투기 도입을 서둘러야 합니다.',
        choices: [
          { code: 'A', label: '국가 안보가 최우선' },
          { code: 'B', label: '평화적 해결이 우선' },
        ],
        relatedArticle: {
          title: '산업은행 회장에 박상진 전 산은 준법감시인 내정(종합)',
          url: 'https://www.yna.co.kr/view/AKR20250909137051002?section=economy/all',
        },
      },
    },
  },
  message: '답변 선택을 완료했습니다.',
  error: null,
}

export const exampleContextResponse = {
  status: 'success',
  data: {
    context: {
      countryName: '뉴토피아',
      finalTurnNumber: 12,
      generatedAt: '2025-09-16T09:10:21Z',
      countryStats: {
        economy: 72,
        defense: 0,
        publicSentiment: 100,
        environment: 32,
      },
    },
  },
  message: '리포트를 불러왔습니다.',
  error: null,
}

export const exampleGraphResponse = {
  status: 'success',
  data: {
    graph: {
      metrics: ['economy', 'publicSentiment', 'environment', 'defense'],
      page: {
        number: 1,
        size: 12,
        sort: 'asc',
        totalElements: 12,
        totalPages: 1,
        hasNext: false,
        hasPrev: false,
        nextPage: null,
        prevPage: null,
      },
      series: [
        {
          metric: 'economy',
          points: [
            { turnNumber: 0, value: 50 },
            { turnNumber: 1, value: 53 },
            { turnNumber: 2, value: 47 },
            { turnNumber: 3, value: 62 },
            { turnNumber: 4, value: 55 },
            { turnNumber: 5, value: 70 },
            { turnNumber: 6, value: 66 },
            { turnNumber: 7, value: 72 },
            { turnNumber: 8, value: 68 },
            { turnNumber: 9, value: 75 },
            { turnNumber: 10, value: 80 },
            { turnNumber: 11, value: 77 },
            { turnNumber: 12, value: 85 },
          ],
        },
        {
          metric: 'defense',
          points: [
            { turnNumber: 0, value: 50 },
            { turnNumber: 1, value: 60 },
            { turnNumber: 2, value: 55 },
            { turnNumber: 3, value: 62 },
            { turnNumber: 4, value: 58 },
            { turnNumber: 5, value: 63 },
            { turnNumber: 6, value: 59 },
            { turnNumber: 7, value: 65 },
            { turnNumber: 8, value: 61 },
            { turnNumber: 9, value: 67 },
            { turnNumber: 10, value: 64 },
            { turnNumber: 11, value: 69 },
            { turnNumber: 12, value: 66 },
          ],
        },
        {
          metric: 'publicSentiment',
          points: [
            { turnNumber: 0, value: 50 },
            { turnNumber: 1, value: 50 },
            { turnNumber: 2, value: 54 },
            { turnNumber: 3, value: 48 },
            { turnNumber: 4, value: 52 },
            { turnNumber: 5, value: 57 },
            { turnNumber: 6, value: 53 },
            { turnNumber: 7, value: 60 },
            { turnNumber: 8, value: 56 },
            { turnNumber: 9, value: 62 },
            { turnNumber: 10, value: 58 },
            { turnNumber: 11, value: 65 },
            { turnNumber: 12, value: 61 },
          ],
        },
        {
          metric: 'environment',
          points: [
            { turnNumber: 0, value: 50 },
            { turnNumber: 1, value: 99 },
            { turnNumber: 2, value: 95 },
            { turnNumber: 3, value: 97 },
            { turnNumber: 4, value: 92 },
            { turnNumber: 5, value: 94 },
            { turnNumber: 6, value: 90 },
            { turnNumber: 7, value: 93 },
            { turnNumber: 8, value: 89 },
            { turnNumber: 9, value: 91 },
            { turnNumber: 10, value: 87 },
            { turnNumber: 11, value: 88 },
            { turnNumber: 12, value: 85 },
          ],
        },
      ],
    },
  },
  message: '그래프를 조회했습니다.',
  error: null,
}

export const exampleTurnDetail = {
  status: 'success',
  data: {
    context: {
      turnNumber: 7,
      metrics: ['economy', 'defense', 'publicSentiment', 'environment'],
    },
    applied: {
      turnNumber: 7,
      choiceCode: 'A',
      choiceLabel: '국가 안보가 최우선',
      countryStats: {
        before: {
          economy: 57,
          defense: 31,
          publicSentiment: 90,
          environment: 66,
        },
        after: {
          economy: 62,
          defense: 41,
          publicSentiment: 85,
          environment: 66,
        },
        delta: {
          economy: 5,
          defense: 10,
          publicSentiment: -5,
          environment: 0,
        },
      },
    },
    card: {
      title: '새 정부의 경제 어젠다',
      content: '주변국의 위협에 맞서 최신예 전투기 도입을 서둘러야 합니다.',
      type: 'ORIGIN',
      npc: { name: '과학자', imageUrl: 's3/image/npc/1.png' },
      choices: [
        { code: 'A', label: '국가 안보가 최우선' },
        { code: 'B', label: '평화적 해결이 우선' },
      ],
      relatedArticle: {
        title: '산업은행 회장에 박상진 전 산은 준법감시인 내정(종합)',
        url: 'https://www.yna.co.kr/view/AKR20250909137051002?section=economy/all',
        content:
          '2일 코스피 지수가 외국인의 매수세에 힘입어 1% 가까이 상승하고 있다. 2일 오전 서울 중구 하나은행 딜링룸에서 코스피지수가 장 초반 전 거래일 대비 19.43포인트(0.62%) 상승한 3162.36을 보이고 있다. /뉴스1 이날 오전 11시 23분 코스피 지수는 전 거래일 대비 23.21포인트(0.74%) 오른 3166.14를 기록하고 있다. 코스피 지수는 전장보다 12.80포인트(0.41%) 상승한 3155.73으로 개장했다. 외국인 투자자는 1435억원을 사들이고 있고, 이와 달리 개인과 기관은 각각 1748억원, 41억원을 팔아치우고 있다. 시가총액 상위 종목은 혼조세다. 반도체 대장주인 삼성전자와 SK하이닉스는 각각 2.22%, 1.86% 상승하고 있다. 우선주인 삼성전자우는 1.62% 오르고 있다. 삼성바이오로직스, 한화에어로스페이스, HD현대중공업, KB금융 등도 상승하고 있다. 이와 달리 LG에너지솔루션, 현대차, 기아 등은 하락하고 있다. 코스닥 지수는 전 거래일 대비 4.23포인트(0.53%) 상승한 789.16에 거래 중이다. 코스닥 지수는 전 거래일 대비 4.35포인트(0.55%) 상승한 789.35에 장을 열었다. 시가총액 상위 10개 종목을 보면 알테오젠, 펩트론, 에코프로, 레인보우로보틱스, 리가켐바이오, HLB, 삼천당제약, 파마리서치, 에이비엘바이오 등은 상승세다. 반면 에코프로비엠 등은 하락세다. 이날 오전 11시 25분 기준 달러화에 대한 원화 환율(원·달러 환율)은 전일 대비 1.20원 내린 1392.30원에 거래되고 있다',
      },
    },
  },
  message: '드릴다운 상세를 불러왔습니다.',
  error: null,
}
