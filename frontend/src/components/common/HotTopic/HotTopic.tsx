import React, { useState, useEffect } from 'react';
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
  id: number;
  title: string;
  url: string;
}

// 더미 데이터
const DUMMY_NEWS: NewsData[] = [
  {
    id: 1,
    title: '"50억 유지" 양도세 우려 풀리자…"이제 더 뛴다" 코스피, 3400선 돌파',
    url: 'https://n.news.naver.com/mnews/article/009/0005558498'
  },
  {
    id: 2,
    title: '"돈 필요 없어요" 끝까지 안 받은 53만명…소비쿠폰, 국민 99%에 지급',
    url: 'https://n.news.naver.com/mnews/article/009/0005558573'
  },
  {
    id: 3,
    title: "살얼음판 걷는 위기의 프롭테크…'기술혁신·글로벌', 돌파구 될까",
    url: 'https://n.news.naver.com/mnews/article/009/0005558620'
  }
];

export interface HotTopicProps {
  newsData?: NewsData[];
  scrollInterval?: number;
}

export const HotTopic: React.FC<HotTopicProps> = ({
  newsData = DUMMY_NEWS,
  scrollInterval = 5000
}) => {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    if (newsData.length === 0) return;

    const interval = setInterval(() => {
      setCurrentIndex((prevIndex) =>
        (prevIndex + 1) % newsData.length
      );
    }, scrollInterval);

    return () => clearInterval(interval);
  }, [newsData.length, scrollInterval]);

  if (newsData.length === 0) {
    return null;
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
              key={news.id}
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