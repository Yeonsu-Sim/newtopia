// 엔딩 관련 API 서비스

// 기존 UI에서 사용하는 인터페이스 (하위 호환성 유지)
export interface EndingItem {
  code: string;
  title: string;
  description: string;
  imageUrl: string;
  isUnlocked: boolean;
  count: number;
}

// 새로운 API 응답 타입 정의
export interface EndingSummary {
  total: number;
  collected: number;
}

export interface EndingAssets {
  imageUrl: string;
  thumbnailUrl: string;
}

export interface EndingStatus {
  collected: boolean;
  count: number;
  lastCollectedAt: string;
}

export interface EndingData {
  code: string;
  title: string;
  content: string;
  assets: EndingAssets;
  status: EndingStatus;
}

export interface EndingsApiResponse {
  status: 'success' | 'error';
  data: {
    summary: EndingSummary;
    endings: EndingData[];
  };
  message: string;
  error: {
    code: string;
    details: {};
  } | null;
}

// 기존 응답 타입 (하위 호환성 유지)
export interface EndingCollectionResponse {
  status: 'success' | 'error';
  data: EndingItem[];
  message: string;
  error: null | {
    code: string;
    details: null;
  };
}

const API_BASE_URL = '/api/v1';

// API 응답을 UI에서 사용하는 형태로 변환하는 유틸리티 함수
const convertEndingDataToEndingItem = (endingData: EndingData): EndingItem => {
  return {
    code: endingData.code,
    title: endingData.title,
    description: endingData.content,
    imageUrl: endingData.assets.imageUrl,
    isUnlocked: endingData.status.collected,
    count: endingData.status.count,
  };
};

// 사용자의 엔딩 컬렉션과 요약 정보 조회
export const getEndingCollection = async (): Promise<EndingItem[]> => {
  try {
    const response = await fetch(`${API_BASE_URL}/endings/me`, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data: EndingsApiResponse = await response.json();

    if (data.status === 'error') {
      throw new Error(data.message || '엔딩 컬렉션을 불러오는데 실패했습니다.');
    }

    // API 응답을 기존 EndingItem 형태로 변환
    return data.data.endings.map(convertEndingDataToEndingItem);
  } catch (error) {
    console.error('Failed to fetch ending collection:', error);
    throw error instanceof Error ? error : new Error('엔딩 컬렉션을 불러오는데 실패했습니다.');
  }
};

// 엔딩 컬렉션과 요약 정보를 함께 반환하는 함수
export interface EndingCollectionWithSummary {
  endings: EndingItem[];
  summary: EndingSummary;
}

export const getEndingCollectionWithSummary = async (): Promise<EndingCollectionWithSummary> => {
  try {
    const response = await fetch(`${API_BASE_URL}/endings/me`, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data: EndingsApiResponse = await response.json();

    if (data.status === 'error') {
      throw new Error(data.message || '엔딩 컬렉션을 불러오는데 실패했습니다.');
    }

    return {
      endings: data.data.endings.map(convertEndingDataToEndingItem),
      summary: data.data.summary,
    };
  } catch (error) {
    console.error('Failed to fetch ending collection with summary:', error);
    throw error instanceof Error ? error : new Error('엔딩 컬렉션을 불러오는데 실패했습니다.');
  }
};

// 특정 엔딩 상세 정보 조회
// 현재 API에서는 모든 엔딩 정보를 한 번에 가져오므로,
// 필요한 경우 getEndingCollection()에서 특정 코드로 필터링
export const getEndingDetail = async (endingCode: string): Promise<EndingItem | null> => {
  try {
    const allEndings = await getEndingCollection();
    const ending = allEndings.find(item => item.code === endingCode);
    return ending || null;
  } catch (error) {
    console.error('Failed to fetch ending detail:', error);
    throw error instanceof Error ? error : new Error('엔딩 정보를 불러오는데 실패했습니다.');
  }
};