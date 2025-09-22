import React, { useEffect, useState } from 'react'
import { useAuthStore } from '@/store/authStore'
import { getMyRanking, getTopRanking } from '@/services/rankingApi'
import type { RankingItem } from '@/services/rankingApi'
import {
  ModalBackground,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  HeaderTab,
  CloseButton,
  TableContainer,
  RankingTable,
  TableHeader,
  TableRow,
  TableCell,
  LoadingSpinner,
  ErrorMessage,
} from '@/components/RankingModal/RankingModal.styles'

export interface RankingModalProps {
  isOpen: boolean
  onClose: () => void
}

type TabType = 'integrated' | 'my'

export const RankingModal: React.FC<RankingModalProps> = ({
  isOpen,
  onClose,
}) => {
  const { user } = useAuthStore()
  const [activeTab, setActiveTab] = useState<TabType>('integrated')
  const [rankingData, setRankingData] = useState<RankingItem[]>([])
  // const [searchGameId, setSearchGameId] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string>('')

  // 모달이 열릴 때 초기 데이터 로드
  useEffect(() => {
    if (isOpen) {
      loadInitialData()
    }
  }, [isOpen, activeTab])

  const loadInitialData = async () => {
    setIsLoading(true)
    setError('')

    try {
      if (activeTab === 'integrated') {
        const data = await getTopRanking(100)
        setRankingData(data)
      } else if (activeTab === 'my' && user) {
        const data = await getMyRanking()
        setRankingData(data)
      }
    } catch (err) {
      setError(
        err instanceof Error ? err.message : '데이터 로드에 실패했습니다.',
      )
    } finally {
      setIsLoading(false)
    }
  }

  const handleTabChange = (tab: TabType) => {
    if (tab === 'my' && !user) {
      setError('로그인이 필요합니다.')
      return
    }
    setActiveTab(tab)
    // setSearchGameId('');
    setError('')
  }

  // const handleSearch = async () => {
  //   if (!searchGameId.trim()) {
  //     setError('게임 ID를 입력해주세요.');
  //     return;
  //   }

  //   const gameId = parseInt(searchGameId.trim());
  //   if (isNaN(gameId)) {
  //     setError('올바른 게임 ID를 입력해주세요.');
  //     return;
  //   }

  //   setIsLoading(true);
  //   setError('');

  //   try {
  //     const data = await getGameRanking(gameId);
  //     setRankingData([data]);
  //   } catch (err) {
  //     setError(err instanceof Error ? err.message : '게임 검색에 실패했습니다.');
  //     setRankingData([]);
  //   } finally {
  //     setIsLoading(false);
  //   }
  // };

  const formatDate = (dateString: string): string => {
    return new Date(dateString).toISOString().split('T')[0]
  }

  const handleOverlayClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose()
    }
  }

  if (!isOpen) return null

  return (
    <ModalOverlay>
      <ModalBackground onClick={handleOverlayClick} />

      <ModalContent>
        <CloseButton onClick={onClose}>✕</CloseButton>

        <ModalHeader>
          <div style={{ display: 'flex', gap: '40px' }}>
            <HeaderTab
              $isActive={activeTab === 'integrated'}
              onClick={() => handleTabChange('integrated')}
            >
              통합랭킹
            </HeaderTab>
            {/* <HeaderTab
              $isActive={activeTab === 'my'}
              onClick={() => handleTabChange('my')}
            >
              내 랭킹
            </HeaderTab> */}
          </div>

          {/* 
          {activeTab === 'integrated' && (
            <SearchSection>
              <SearchInput
                type="number"
                placeholder={error && error.includes('게임') ? error : "게임 ID 검색"}
                value={searchGameId}
                onChange={(e) => setSearchGameId(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                $hasError={!!error && error.includes('게임')}
              />
              <SearchButton onClick={handleSearch}>검색</SearchButton>
            </SearchSection>
          )}
          */}
        </ModalHeader>

        {error && !error.includes('게임') && (
          <ErrorMessage>{error}</ErrorMessage>
        )}

        <TableContainer>
          {isLoading ? (
            <LoadingSpinner>로딩 중...</LoadingSpinner>
          ) : (
            <RankingTable>
              <TableHeader>
                <TableCell>순위</TableCell>
                <TableCell>나라이름</TableCell>
                <TableCell>통치기간</TableCell>
                <TableCell>시간</TableCell>
              </TableHeader>

              {rankingData.map((item) => (
                <TableRow key={item.gameId}>
                  <TableCell>{item.order}</TableCell>
                  <TableCell>{item.countryName}</TableCell>
                  <TableCell>{item.turn}턴</TableCell>
                  <TableCell>{formatDate(item.endedAt)}</TableCell>
                </TableRow>
              ))}

              {rankingData.length === 0 && !isLoading && (
                <TableRow style={{ gridColumn: '1 / -1' }}>
                  <TableCell
                    style={{ gridColumn: '1 / -1', textAlign: 'center' }}
                  >
                    데이터가 없습니다.
                  </TableCell>
                </TableRow>
              )}
            </RankingTable>
          )}
        </TableContainer>
      </ModalContent>
    </ModalOverlay>
  )
}
