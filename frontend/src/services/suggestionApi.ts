// 건의사항 관련 API 서비스

export interface SuggestionRequest {
  title: string;
  text: string;
  suggestionCategory: string;
  imageIds: number[];
}

export interface SuggestionResponse {
  status: 'success' | 'error';
  message: string;
  data: {
    suggestionId: number;
  };
  error: null | {
    code: string;
    details: object;
  };
}

export interface CategoryResponse {
  status: 'success' | 'error';
  message: string;
  data: string[];
  error: null | {
    code: string;
    details: object;
  };
}

const API_BASE_URL = '/api/v1';

// 건의사항 생성
export const createSuggestion = async (
  suggestionData: SuggestionRequest
): Promise<SuggestionResponse> => {
  console.log('Sending suggestion data:', suggestionData);

  const response = await fetch(`${API_BASE_URL}/suggestions`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify(suggestionData),
  });

  console.log('Response status:', response.status);
  console.log('Response headers:', response.headers);

  const data: SuggestionResponse = await response.json();
  console.log('Response data:', data);

  if (!response.ok || data.status === 'error') {
    console.error('API Error - Status:', response.status, 'Data:', data);
    throw new Error(data.message || '건의사항 제출에 실패했습니다.');
  }

  return data;
};

// 건의사항 카테고리 목록 조회
export const getSuggestionCategories = async (): Promise<string[]> => {
  console.log('Fetching suggestion categories from:', `${API_BASE_URL}/suggestions/categories`);

  const response = await fetch(`${API_BASE_URL}/suggestions/categories`, {
    method: 'GET',
    credentials: 'include',
  });

  console.log('Categories response status:', response.status);
  console.log('Categories response headers:', response.headers);

  const data: CategoryResponse = await response.json();
  console.log('Categories response data:', data);

  if (!response.ok || data.status === 'error') {
    console.error('Categories API Error - Status:', response.status, 'Data:', data);
    throw new Error(data.message || '카테고리 목록을 불러오는데 실패했습니다.');
  }

  return data.data;
};