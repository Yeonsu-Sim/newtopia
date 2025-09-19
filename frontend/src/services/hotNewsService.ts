interface HotNewsItem {
  sourceUrl: string;
  title: string;
  publishedAt: string;
}

interface HotNewsResponse {
  status: 'success' | 'error';
  data: HotNewsItem[];
  message: string;
  error: {
    code: string;
    details: object;
  } | null;
}

export const fetchHotNews = async (limit: number = 20): Promise<HotNewsItem[]> => {
  try {
    const response = await fetch(`/api/v1/public/hotnews?limit=${limit}`);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result: HotNewsResponse = await response.json();

    if (result.status === 'success') {
      return result.data;
    } else {
      throw new Error(result.message || 'Failed to fetch hot news');
    }
  } catch (error) {
    console.error('Error fetching hot news:', error);
    throw error;
  }
};