// 엔딩 관련 API 서비스

export interface EndingItem {
  code: string;
  title: string;
  description: string;
  imageUrl: string;
  isUnlocked: boolean;
}

export interface EndingCollectionResponse {
  status: 'success' | 'error';
  data: EndingItem[];
  message: string;
  error: null | {
    code: string;
    details: null;
  };
}

// const API_BASE_URL = '/api/v1'; // 추후 실제 API 연동 시 사용

// 임시 목업 데이터 (추후 실제 API로 교체)
const mockEndingData: EndingItem[] = [
  {
    code: 'ECO_MAX',
    title: '경제 100달성.',
    description: '부는 쌓였지만, 나눌 생각은 없었다.',
    imageUrl: '/ending/ECO_MAX.png',
    isUnlocked: true,
  },
  {
    code: 'ECO_MIN',
    title: '경제 0달성.',
    description: '돈도, 빵도, 희망도 사라졌다.',
    imageUrl: '/ending/ECO_MIN.png',
    isUnlocked: false,
  },
  {
    code: 'DEF_MAX',
    title: '국방 100달성.',
    description: '철옹성은 완성됐다. 감옥도 함께.',
    imageUrl: '/ending/DEF_MAX.png',
    isUnlocked: true,
  },
  {
    code: 'DEF_MIN',
    title: '국방 0달성.',
    description: '군대가 사라지자, 나라가 사라졌다.',
    imageUrl: '/ending/DEF_MIN.png',
    isUnlocked: false,
  },
  {
    code: 'PUB_MAX',
    title: '민심 100달성.',
    description: '사랑이 지나쳐 숭배가 되었다.',
    imageUrl: '/ending/PUB_MAX.png',
    isUnlocked: true,
  },
  {
    code: 'PUB_MIN',
    title: '민심 0달성.',
    description: '군중은 환호를 멈추고 돌을 던졌다.',
    imageUrl: '/ending/PUB_MIN.png',
    isUnlocked: false,
  },
  {
    code: 'ENV_MAX',
    title: '환경 100달성.',
    description: '숲은 살아났지만, 사람은 사라졌다.',
    imageUrl: '/ending/ENV_MAX.png',
    isUnlocked: false,
  },
  {
    code: 'ENV_MIN',
    title: '환경 0달성.',
    description: '강은 말랐고, 숨은 막혔다.',
    imageUrl: '/ending/ENV_MIN.png',
    isUnlocked: false,
  },
  {
    code: 'DOUBLE_OVER',
    title: '나라가 반이나 남았네?',
    description: '두개의 지표가 붕괴했습니다…',
    imageUrl: '/ending/DOUBLE_OVER.png',
    isUnlocked: true,
  },
  {
    code: 'TRIPLE_OVER',
    title: '삼권붕괴',
    description: '세개의 지표가 붕괴했습니다… 우리나라의 미래는 어떻게 될까요?',
    imageUrl: '/ending/TRIPLE_OVER.png',
    isUnlocked: false,
  },
  {
    code: 'QUAD_OVER',
    title: '(나라가) 폭싹 망했수다',
    description: '진정한 뉴토피아',
    imageUrl: '/ending/QUAD_OVER.png',
    isUnlocked: false,
  },
];

// 사용자의 엔딩 컬렉션 조회 (추후 실제 API로 교체)
export const getEndingCollection = async (): Promise<EndingItem[]> => {
  // TODO: 실제 API 연결 시 아래 주석 해제하고 목업 데이터 제거
  /*
  const response = await fetch(`${API_BASE_URL}/endings/collection`, {
    method: 'GET',
    credentials: 'include',
  });

  const data: EndingCollectionResponse = await response.json();

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '엔딩 컬렉션을 불러오는데 실패했습니다.');
  }

  return data.data;
  */

  // 임시 목업 데이터 반환
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(mockEndingData);
    }, 500); // 로딩 시뮬레이션
  });
};

// 특정 엔딩 상세 정보 조회 (추후 실제 API로 교체)
export const getEndingDetail = async (endingCode: string): Promise<any> => {
  // TODO: 실제 API 연결 시 구현
  /*
  const response = await fetch(`${API_BASE_URL}/endings/${endingCode}`, {
    method: 'GET',
    credentials: 'include',
  });

  const data = await response.json();

  if (!response.ok || data.status === 'error') {
    throw new Error(data.message || '엔딩 정보를 불러오는데 실패했습니다.');
  }

  return data.data;
  */

  const ending = mockEndingData.find(item => item.code === endingCode);
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (ending) {
        resolve(ending);
      } else {
        reject(new Error('엔딩을 찾을 수 없습니다.'));
      }
    }, 300);
  });
};