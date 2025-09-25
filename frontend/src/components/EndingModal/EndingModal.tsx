import React, { useEffect, useState } from 'react'
import { getEndingCollectionWithSummary, type EndingItem, type EndingSummary } from '@/services/endingApi'
import {
  ModalOverlay,
  ModalBackground,
  ModalContent,
  CloseButton,
  ModalHeader,
  HeaderTitle,
  SummaryInfo,
  CollectionContainer,
  EndingGrid,
  EndingCard,
  EndingImage,
  EndingTitle,
  EndingDescription,
  CountBadge,
  LockIcon,
  LoadingSpinner,
  ErrorMessage,
} from '@/components/EndingModal/EndingModal.styles'

export interface EndingModalProps {
  isOpen: boolean
  onClose: () => void
}

export const EndingModal: React.FC<EndingModalProps> = ({
  isOpen,
  onClose,
}) => {
  const [endingData, setEndingData] = useState<EndingItem[]>([])
  const [summaryData, setSummaryData] = useState<EndingSummary | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string>('')

  // 모달이 열릴 때 데이터 로드
  useEffect(() => {
    if (isOpen) {
      loadEndingCollection()
    }
  }, [isOpen])

  const loadEndingCollection = async () => {
    setIsLoading(true)
    setError('')

    try {
      const { endings, summary } = await getEndingCollectionWithSummary()
      setEndingData(endings)
      setSummaryData(summary)
    } catch (err) {
      setError(
        err instanceof Error ? err.message : '엔딩 컬렉션을 불러오는데 실패했습니다.',
      )
    } finally {
      setIsLoading(false)
    }
  }

  const handleOverlayClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose()
    }
  }

  const handleEndingClick = (ending: EndingItem) => {
    if (ending.isUnlocked) {
      // TODO: 엔딩 상세 모달 또는 페이지로 이동
      console.log('엔딩 상세 보기:', ending.code)
    }
  }


  if (!isOpen) return null

  return (
    <ModalOverlay>
      <ModalBackground onClick={handleOverlayClick} />

      <ModalContent>
        <CloseButton onClick={onClose}>✕</CloseButton>

        <ModalHeader>
          <HeaderTitle>엔딩 도감</HeaderTitle>
          {summaryData && (
            <SummaryInfo>
              수집된 엔딩: {summaryData.collected} / {summaryData.total}
            </SummaryInfo>
          )}
        </ModalHeader>

        {error && <ErrorMessage>{error}</ErrorMessage>}

        <CollectionContainer>
          {isLoading ? (
            <LoadingSpinner>엔딩 컬렉션 로딩 중...</LoadingSpinner>
          ) : (
            <EndingGrid>
              {endingData.map((ending) => (
                <EndingCard
                  key={ending.code}
                  $isUnlocked={ending.isUnlocked}
                  onClick={() => handleEndingClick(ending)}
                >
                  {ending.isUnlocked && ending.count > 0 && (
                    <CountBadge $isUnlocked={ending.isUnlocked}>
                      {ending.count}
                    </CountBadge>
                  )}

                  <EndingImage $isUnlocked={ending.isUnlocked}>
                    {ending.isUnlocked ? (
                      <img
                        src={ending.imageUrl}
                        alt={ending.title}
                        style={{
                          width: '100%',
                          height: '100%',
                          objectFit: 'cover',
                          borderRadius: '50%'
                        }}
                        onError={(e) => {
                          // 이미지 로드 실패 시 기본 아이콘 표시
                          e.currentTarget.style.display = 'none';
                          e.currentTarget.parentElement!.innerHTML = '🏛️';
                        }}
                      />
                    ) : (
                      '🔒'
                    )}
                  </EndingImage>

                  <EndingTitle $isUnlocked={ending.isUnlocked}>
                    {ending.isUnlocked ? ending.title : '???'}
                  </EndingTitle>

                  <EndingDescription $isUnlocked={ending.isUnlocked}>
                    {ending.isUnlocked
                      ? ending.description
                      : '이 엔딩을 해금하려면 게임을 플레이하세요!'
                    }
                  </EndingDescription>

                  {!ending.isUnlocked && <LockIcon></LockIcon>}
                </EndingCard>
              ))}

              {endingData.length === 0 && !isLoading && (
                <div style={{
                  gridColumn: '1 / -1',
                  textAlign: 'center',
                  color: '#fff',
                  fontSize: '24px',
                  padding: '40px'
                }}>
                  아직 해금된 엔딩이 없습니다.
                  <br />
                  게임을 플레이하여 다양한 엔딩을 경험해보세요!
                </div>
              )}
            </EndingGrid>
          )}
        </CollectionContainer>
      </ModalContent>
    </ModalOverlay>
  )
}