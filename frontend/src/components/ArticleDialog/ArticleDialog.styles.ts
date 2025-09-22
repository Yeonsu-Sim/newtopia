import styled from 'styled-components'

export const DialogOverlay = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`

export const ArticleContainer = styled.div`
  background: #e49000;
  width: 90%;
  max-width: 800px;
  height: 90%;
  border-radius: 12px;
  overflow-y: auto;
  position: relative;
  border: 6px solid #f9bf26;
  box-shadow:
    inset 12px 12px 10px 4px #a35400,
    6px 6px 0px 2px #935100;
  font-family: 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
`

export const ArticleHeader = styled.div`
  background: #fdf3d8;
  padding: 1rem 2rem;
  border-bottom: 3px solid #f9bf26;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
`

export const NewspaperName = styled.h1`
  font-family: 'PFStardust ExtraBold', 'DNFBitBitv2', sans-serif;
  font-size: 1.8rem;
  font-weight: 800;
  color: #6e3400;
  text-shadow: 1px 1px 0 #fdf3d8;
  margin: 0;
`

export const VolumeInfo = styled.div`
  font-family: 'PFStardust ExtraBold', 'DNFBitBitv2', sans-serif;
  font-size: 1.2rem;
  font-weight: 800;
  color: #6e3400;
  text-shadow: 1px 1px 0 #fdf3d8;
  text-align: center;
  flex: 1;
`

export const DateInfo = styled.div`
  font-family: 'PFStardust ExtraBold', 'DNFBitBitv2', sans-serif;
  font-size: 1.2rem;
  font-weight: 800;
  color: #6e3400;
  text-shadow: 1px 1px 0 #fdf3d8;
  text-align: right;
`

export const Headline = styled.h2`
  background: #fdf3d8;
  padding: 2rem;
  margin: 0;
  font-family: 'DNFBitBitv2', sans-serif;
  font-size: 2rem;
  font-weight: bold;
  color: #6e3400;
  text-shadow: 2px 2px 0 #f9bf26;
  text-align: center;
  border-bottom: 3px solid #f9bf26;
  line-height: 1.3;
  word-break: keep-all;
  word-wrap: break-word;
  white-space: normal;

  @media (max-width: 768px) {
    font-size: 2rem;
    padding: 1.5rem;
  }

  @media (max-width: 480px) {
    font-size: 1.8rem;
    padding: 1rem;
  }
`

export const NewsImage = styled.img`
  width: 100%;
  height: 510px;
  object-fit: cover;
  border-bottom: 3px solid #f9bf26;
`

export const CommentsSection = styled.div`
  background: #fdf3d8;
  padding: 2rem;
  min-height: 400px;
`

export const CommentsHeader = styled.h3`
  font-family: 'DNFBitBitv2', sans-serif;
  font-size: 1.2rem;
  color: #6e3400;
  text-shadow: 1px 1px 0 #f9bf26;
  margin: 0 0 1.5rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #f9bf26;
`

export const CommentsList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`

export const CommentItem = styled.div`
  display: flex;
  gap: 0.75rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
`

export const UserAvatar = styled.img`
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 2px solid #f9bf26;
`

export const CommentContent = styled.div`
  flex: 1;
  min-width: 0;
`

export const CommentAuthor = styled.div`
  font-family: 'DNFBitBitv2', sans-serif;
  font-weight: bold;
  color: #333;
  font-size: 0.9rem;
  margin-bottom: 0.25rem;
`

export const CommentTime = styled.div`
  font-size: 0.75rem;
  color: #888;
  margin-bottom: 0.5rem;
`

export const CommentText = styled.p`
  font-family: 'DNFBitBitv2', sans-serif;
  color: #333;
  font-size: 1.4rem;
  line-height: 1;
  margin: 0 0 0.75rem 0;
  word-break: break-word;
`

export const CommentActions = styled.div`
  display: flex;
  gap: 1rem;
  align-items: center;
`

export const LikeButton = styled.div<{ $liked: boolean }>`
  background: none;
  border: none;
  font-size: 0.8rem;
  color: #666;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-family: 'PFStardust ExtraBold', 'DNFBitBitv2', sans-serif;
`

export const NavigationButtons = styled.div`
  background: #fdf3d8;
  padding: 1.5rem 2rem;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  border-top: 3px solid #f9bf26;
`

export const RelatedArticleButton = styled.button<{ $disabled?: boolean }>`
  background: ${({ $disabled }) =>
    $disabled
      ? 'linear-gradient(135deg, #999, #666)'
      : 'linear-gradient(135deg, #f9bf26, #e49000)'};
  border: 3px solid ${({ $disabled }) => ($disabled ? '#555' : '#8E4600')};
  color: #fff;
  text-shadow: 2px 2px 0 ${({ $disabled }) => ($disabled ? '#333' : '#6E3400')};
  padding: 0.75rem 1.5rem;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 800;
  font-family:
    'PF Stardust ExtraBold', 'DNFBitBitv2', 'Noto Sans KR', sans-serif;
  cursor: ${({ $disabled }) => ($disabled ? 'not-allowed' : 'pointer')};
  transition: all 0.2s ease;
  box-shadow:
    0 4px 8px rgba(0, 0, 0, 0.4),
    inset 2px 2px 4px rgba(255, 255, 255, 0.3);
  opacity: ${({ $disabled }) => ($disabled ? 0.6 : 1)};

  &:hover {
    background: ${({ $disabled }) =>
      $disabled
        ? 'linear-gradient(135deg, #999, #666)'
        : 'linear-gradient(135deg, #fcd34d, #f59e0b)'};
    transform: ${({ $disabled }) =>
      $disabled ? 'none' : 'translateY(-2px) scale(1.05)'};
    box-shadow: ${({ $disabled }) =>
      $disabled
        ? '0 4px 8px rgba(0, 0, 0, 0.4), inset 2px 2px 4px rgba(255, 255, 255, 0.3)'
        : '0 6px 12px rgba(0, 0, 0, 0.5), inset 2px 2px 4px rgba(255, 255, 255, 0.4)'};
  }

  &:active {
    transform: ${({ $disabled }) =>
      $disabled ? 'none' : 'translateY(0) scale(1)'};
    box-shadow: ${({ $disabled }) =>
      $disabled
        ? '0 4px 8px rgba(0, 0, 0, 0.4), inset 2px 2px 4px rgba(255, 255, 255, 0.3)'
        : '0 2px 4px rgba(0, 0, 0, 0.3), inset 1px 1px 2px rgba(255, 255, 255, 0.2)'};
  }
`

export const LoadingComment = styled.div`
  display: flex;
  gap: 0.75rem;
  padding: 1rem 0;
  align-items: center;
  opacity: 0.7;

  .loading-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
    background-size: 200% 100%;
    animation: loading 1.5s infinite;
  }

  .loading-content {
    flex: 1;

    .loading-line {
      height: 12px;
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: loading 1.5s infinite;
      border-radius: 4px;
      margin-bottom: 8px;

      &.short {
        width: 30%;
      }
      &.medium {
        width: 60%;
      }
      &.long {
        width: 80%;
      }
    }
  }

  @keyframes loading {
    0% {
      background-position: 200% 0;
    }
    100% {
      background-position: -200% 0;
    }
  }
`
