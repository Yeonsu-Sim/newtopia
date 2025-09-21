import { useState, useEffect, useRef } from 'react'
import {
  DialogOverlay,
  ArticleContainer,
  ArticleHeader,
  NewspaperName,
  VolumeInfo,
  DateInfo,
  Headline,
  NewsImage,
  CommentsSection,
  CommentsHeader,
  CommentsList,
  CommentItem,
  UserAvatar,
  CommentContent,
  CommentAuthor,
  CommentTime,
  CommentText,
  CommentActions,
  LikeButton,
  NavigationButtons,
  RelatedArticleButton,
  LoadingComment,
} from './ArticleDialog.styles'
import { useAudio } from '@/hooks/useAudio'

interface ArticleDialogProps {
  open: boolean
  playerName: string
  countryName: string
  currentTurn: number
  selectedChoice: {
    code: string
    label: string
    comments: string[]
  }
  onClose?: () => void
  onViewRelatedArticle: () => void
}

const ArticleDialog: React.FC<ArticleDialogProps> = ({
  open,
  playerName,
  countryName,
  currentTurn,
  selectedChoice,
  onViewRelatedArticle,
}) => {
  const { playClickSound } = useAudio({ enableBgm: false })
  const [visibleComments, setVisibleComments] = useState<number>(0)
  const [isScrolledToComments, setIsScrolledToComments] = useState(false)
  const [selectedImage] = useState(() => {
    // 컴포넌트 마운트 시 한 번만 이미지 선택
    const newsImages = [
      '/news/newsimg1.jpg',
      '/news/newsimg2.jpg',
      '/news/newsimg3.jpg',
      '/news/newsimg4.jpg',
      '/news/newsimg5.jpg',
      '/news/newsimg6.jpg',
      '/news/newsimg7.jpg',
      '/news/newsimg8.jpg',
    ]
    return newsImages[Math.floor(Math.random() * newsImages.length)]
  })
  const containerRef = useRef<HTMLDivElement>(null)
  const commentsRef = useRef<HTMLDivElement>(null)

  if (!open) return null

  // 랜덤 프로필 이미지
  const getRandomProfileImage = (index: number) => {
    const profileImages = [
      '/icons/1.png',
      '/icons/2.png',
      '/icons/3.png',
      '/icons/4.png',
    ]
    return profileImages[index % profileImages.length]
  }

  // 현재 날짜 형식
  const currentDate = new Date().toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'short',
  })

  // 댓글 작성자 더미 이름 생성
  const generateCommentAuthor = (index: number) => {
    const authors = ['시민1', '시민2', '시민3', '시민4']
    return authors[index % authors.length]
  }

  // 랜덤 좋아요 수 생성
  const generateLikeCount = (index: number) => {
    // 인덱스 기반으로 시드를 만들어 일관된 랜덤값 생성
    const seed = index * 17 + 42
    return (seed % 50) + 1 // 1~50 사이의 값
  }

  // 댓글 시간 생성
  const generateCommentTime = (index: number) => {
    const now = new Date()
    const minutesAgo = (index + 1) * 15 + Math.floor(Math.random() * 10)
    const commentTime = new Date(now.getTime() - minutesAgo * 60000)
    return commentTime.toLocaleString('ko-KR', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  // 스크롤 감지 및 댓글 로딩
  useEffect(() => {
    const handleScroll = () => {
      if (!containerRef.current || !commentsRef.current) return

      const container = containerRef.current
      const commentsSection = commentsRef.current
      const containerRect = container.getBoundingClientRect()
      const commentsRect = commentsSection.getBoundingClientRect()

      // 댓글 섹션이 뷰포트에 들어왔는지 확인
      if (commentsRect.top <= containerRect.bottom && !isScrolledToComments) {
        setIsScrolledToComments(true)
      }
    }

    const container = containerRef.current
    if (container) {
      container.addEventListener('scroll', handleScroll)
      return () => container.removeEventListener('scroll', handleScroll)
    }
  }, [])

  // 댓글 순차 로딩
  useEffect(() => {
    if (
      !isScrolledToComments ||
      visibleComments >= selectedChoice.comments.length
    )
      return

    const timer = setTimeout(() => {
      setVisibleComments((prev) =>
        Math.min(prev + 1, selectedChoice.comments.length),
      )
    }, 700) // 0.7초 간격으로 댓글 로딩

    return () => clearTimeout(timer)
  }, [isScrolledToComments, visibleComments, selectedChoice.comments.length])

  const handleRelatedArticleClick = () => {
    playClickSound()
    onViewRelatedArticle()
  }

  return (
    <DialogOverlay>
      <ArticleContainer ref={containerRef}>
        <ArticleHeader>
          <NewspaperName>[{countryName}] 일보</NewspaperName>
          <VolumeInfo>{currentTurn}턴 보</VolumeInfo>
          <DateInfo>{currentDate}</DateInfo>
        </ArticleHeader>

        <Headline>
          [{playerName}]시장, [{selectedChoice.label}] 결정!
        </Headline>

        <NewsImage src={selectedImage} alt="뉴스 이미지" />

        <CommentsSection ref={commentsRef}>
          <CommentsHeader>
            댓글 ({selectedChoice.comments.length})
          </CommentsHeader>

          <CommentsList>
            {Array.from({ length: visibleComments }).map((_, index) => (
              <CommentItem key={index}>
                <UserAvatar
                  src={getRandomProfileImage(index)}
                  alt="프로필 이미지"
                  onError={(e) => {
                    // 이미지 로드 실패 시 기본 이미지로 대체
                    ;(e.target as HTMLImageElement).src = '/icons/1.png'
                  }}
                />
                <CommentContent>
                  <CommentAuthor>{generateCommentAuthor(index)}</CommentAuthor>
                  <CommentTime>{generateCommentTime(index)}</CommentTime>
                  <CommentText>{selectedChoice.comments[index]}</CommentText>
                  <CommentActions>
                    <LikeButton $liked={false}>
                      👍 {generateLikeCount(index)}
                    </LikeButton>
                  </CommentActions>
                </CommentContent>
              </CommentItem>
            ))}

            {/* 로딩 중인 댓글 표시 */}
            {visibleComments < selectedChoice.comments.length &&
              isScrolledToComments && (
                <LoadingComment>
                  <div className="loading-avatar"></div>
                  <div className="loading-content">
                    <div className="loading-line short"></div>
                    <div className="loading-line medium"></div>
                    <div className="loading-line long"></div>
                  </div>
                </LoadingComment>
              )}
          </CommentsList>
        </CommentsSection>

        <NavigationButtons>
          <RelatedArticleButton
            $disabled={false}
            onClick={handleRelatedArticleClick}
          >
            관련기사 보기
          </RelatedArticleButton>
        </NavigationButtons>
      </ArticleContainer>
    </DialogOverlay>
  )
}

export default ArticleDialog
