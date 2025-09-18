import React, { useState, useEffect } from 'react';
import { fetchHotNews } from '@/services/hotNewsService';
import {
  HotTopicContainer,
  HotTopicTitle,
  NewsScrollContainer,
  NewsItem,
  NewsIcon,
  NewsLink,
  ContentWrapper
} from './HotTopic.styles';

interface NewsData {
  title: string;
  url: string;
}

export interface HotTopicProps {
  scrollInterval?: number;
}

export const HotTopic: React.FC<HotTopicProps> = ({
  scrollInterval = 5000
}) => {
  const [newsData, setNewsData] = useState<NewsData[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const loadHotNews = async () => {
      try {
        setIsLoading(true);
        const hotNewsData = await fetchHotNews(20);
        const formattedNews: NewsData[] = hotNewsData.map((item) => ({
          title: item.title,
          url: item.sourceUrl
        }));
        setNewsData(formattedNews);
      } catch (error) {
        console.error('Failed to load hot news:', error);
        setNewsData([]);
      } finally {
        setIsLoading(false);
      }
    };

    loadHotNews();
  }, []);

  useEffect(() => {
    if (newsData.length === 0) return;

    const interval = setInterval(() => {
      setCurrentIndex((prevIndex) =>
        (prevIndex + 1) % newsData.length
      );
    }, scrollInterval);

    return () => clearInterval(interval);
  }, [newsData.length, scrollInterval]);

  if (isLoading) {
    return (
      <HotTopicContainer>
        <ContentWrapper>
          <HotTopicTitle>📰 이달의 핫토픽 뉴스</HotTopicTitle>
          <NewsScrollContainer>
            <NewsItem
              $isVisible={true}
              $index={0}
              $currentIndex={0}
              $totalItems={1}
            >
              <NewsLink>
                <NewsIcon>⏳</NewsIcon>
                뉴스를 불러오는 중...
              </NewsLink>
            </NewsItem>
          </NewsScrollContainer>
        </ContentWrapper>
      </HotTopicContainer>
    );
  }

  if (newsData.length === 0) {
    return (
      <HotTopicContainer>
        <ContentWrapper>
          <HotTopicTitle>📰 이달의 핫토픽 뉴스</HotTopicTitle>
          <NewsScrollContainer>
            <NewsItem
              $isVisible={true}
              $index={0}
              $currentIndex={0}
              $totalItems={1}
            >
              <NewsLink>
                <NewsIcon>❌</NewsIcon>
                뉴스를 불러올 수 없습니다
              </NewsLink>
            </NewsItem>
          </NewsScrollContainer>
        </ContentWrapper>
      </HotTopicContainer>
    );
  }

  const handleNewsClick = (url: string) => {
    window.open(url, '_blank', 'noopener,noreferrer');
  };

  return (
    <HotTopicContainer>
      <ContentWrapper>
        <HotTopicTitle>📰 이달의 핫토픽 뉴스</HotTopicTitle>

        <NewsScrollContainer>
          {newsData.map((news, index) => (
            <NewsItem
              key={index}
              $isVisible={index === currentIndex}
              $index={index}
              $currentIndex={currentIndex}
              $totalItems={newsData.length}
            >
              <NewsLink onClick={() => handleNewsClick(news.url)}>
                <NewsIcon>▶</NewsIcon>
                {news.title}
              </NewsLink>
            </NewsItem>
          ))}
        </NewsScrollContainer>
      </ContentWrapper>
    </HotTopicContainer>
  );
};