import { useState, useEffect } from 'react'
import { createFileRoute } from '@tanstack/react-router'
import { useAudio } from '@/hooks/useAudio'
import {
  TestContainer,
  TestHeader,
  TestTitle,
  TestControls,
  TestButton,
  MockGameContainer,
  MockBackgroundImage,
  MockEventIcon,
} from '@/routes/test/-Test.styles'

import Parameter from '@/components/Parameter'
import GuestDialog from '@/components/GuestDialog/GuestDialog'
import ChoiceDialog from '@/components/ChoiceDialog/ChoiceDialog.tsx'
import FeedbackDialog from '@/components/FeedbackDialog/FeedbackDialog'
import ArticleDialog from '@/components/ArticleDialog/ArticleDialog'
// import FeedbackToastContainer from '@/components/FeedbackToast/FeedbackToastContainer'

export const Route = createFileRoute('/test/')({
  component: TestComponent,
})

function TestComponent() {
  const [guestOpen, setGuestOpen] = useState(false)
  const [choiceOpen, setChoiceOpen] = useState(false)
  const [articleOpen, setArticleOpen] = useState(false)
  const [feedbackOpen, setFeedbackOpen] = useState(false)
  const [clickPos, setClickPos] = useState<{ x: number; y: number } | null>(
    null,
  )
  const [selectedChoiceCode, setSelectedChoiceCode] = useState<
    'A' | 'B' | null
  >(null)
  // const [toastMessages, setToastMessages] = useState<string[]>([])
  const [showEventIcon, setShowEventIcon] = useState(false)
  const [eventIconAnimation, setEventIconAnimation] = useState(false)
  const [onboardingOpen, setOnboardingOpen] = useState(false)
  const [onboardingStep, setOnboardingStep] = useState(0)
  useAudio({ enableBgm: false })

  // 온보딩 데이터
  const onboardingTexts = [
    '좋은 아침입니다 시장님. 오늘부터 시장님을 보좌하게될 보좌관 뉴토라고 합니다.',
    '저 창문 너머로 보이는 네 개의 섬이 보이시죠?',
    '경제, 환경, 민심, 국방… 나라를 지탱하는 네 가지 기둥입니다.',
    '매 턴마다 민원인들이 찾아와 제안을 올릴 겁니다. 수용할지 거절할지는 오직 시장님의 몫입니다..',
    '다만… 그 선택 하나로 섬들의 지표가 오르내리게 됩니다.',
    '결정을 내리신 뒤에는, 시민들의 반응과 민원인의 제안에 영향을 준 실제 기사도 확인하실 수 있습니다.',
    '명심하세요! 어느 한 지표라도 0이나 100에 도달하면 시장님의 임기는 그 즉시 끝나게 됩니다.',
    '뉴스의 흐름을 예상하고, 모든 지표를 균형 있게 유지하세요.',
    '자, 이제 나만의 뉴토피아를 만들어가실 시간입니다.',
  ]

  // Mock 게임 데이터
  const mockStats = {
    eco: 65,
    mil: 45,
    opi: 75,
    env: 55,
  }

  const mockCard = {
    cardId: 'test-card-1',
    content:
      '새로운 경제 정책을 도입하려고 합니다. 이 정책은 단기적으로는 비용이 들지만 장기적으로는 경제 성장에 도움이 될 것으로 예상됩니다.',
    npc: {
      name: '재정경제부 장관',
      imageUrl: '/icons/npc1.png',
    },
    choices: [
      {
        code: 'A',
        label: '정책 즉시 도입',
        comments: [
          '강력한 정책이 필요한 시점입니다!',
          '경제 회복을 위해 과감한 결정을 지지합니다.',
          '즉시 도입은 리스크가 클 수 있어요.',
          '빠른 실행력이 돋보이는 선택이네요.',
        ],
      },
      {
        code: 'B',
        label: '점진적 도입',
        comments: [
          '신중한 접근이 현명해 보입니다.',
          '점진적 방식이 안전할 것 같아요.',
          '너무 느린 것 아닌가요? 속도가 필요해요.',
          '단계적 접근으로 부작용을 최소화할 수 있겠네요.',
        ],
      },
    ],
    relatedArticle: {
      title: '과기정통부 "KT·LG유플러스 해킹 정황 조사 중"',
      url: 'https://n.news.naver.com/mnews/article/366/0001104620',
      content:
        'KT·LG유플러스 "데이터 해킹 정황 없어" 과학기술정보통신부 /뉴스1 지난 4월 해킹 사실이 드러난 SK텔레콤에 이어 KT와 ##LG유플러스가 올해 상반기까지 수 개월간 해킹당했다는 정황이 드러났다. 과학기술정보통신부와 한국인터넷진흥원(KISA)이 현장점검에 나섰다. 1일 과기정통부는 "KT·LG유플러스 침해사고 여부 확인을 위해 현장점검을 진행 중이다. 관련 자료도 제출받아 정밀 포렌식 분석 중"이라고 밝혔다. 앞서 글로벌 해킹 전문지 \'프랙 매거진\' 40주년 기념호에서는 \'APT Down: The North Korea Files\'라는 보고서가 공개됐다. 익명의 화이트해커 두 명은 \'KIM\'이라는 공격자로부터 8GB에 달하는 한국 기관·기업 유출 데이터를 확보했다며 매거진에 제보했다. 유출 데이터 목록에는 KT와 LG유플러스에서 나온 자료가 포함된 것으로 나타났다. LG유플러스의 경우 ▲내부 서버 관리용 계정 권한 관리 시스템(APPM) 소스코드 및 데이터베이스 ▲8938대 서버 정보 ▲4만 2526개 계정 및 167명 직원/협력사 ID·실명 등이 유출된 것으로 나타났다. 올해 4월까지 해당 정보에 접근한 이상 기록이 확인된 것으로 나타났다. KT는 인증서(SSL 키)가 유출된 정황이 발견됐다. 인증서는 유출 당시 유효했지만, 현재는 만료됐다. 일부 정부 부처 역시 해킹당한 것으로 나타났다. 행정안전부 행정전자서명(GPKI) 인증서, 외교부 내부 메일 서버 소스코드, 통일부·해양수산부 \'온나라\' 소스코드 및 내부망 인증 기록 등이 유출됐다. 과기정통부·KISA는 7월부터 관련 사실을 인지하고 자체 조사에 착수한 것으로 나타났다. 다만 두 통신사가 당국의 상세 조사를 거부한 것으로 알려졌다. 현행 정보통신망법상 기업이 침해당했다고 자진신고 하지 않는 한 당국 현장 조사는 불가능하다. 류제명 과기정통부 2차관은 지난 20일 국회 과학기술정보방송통신위원회 전체회의에 출석해 최근 프랙이 "김수키가 한국 정부와 통신사를 공격했다"고 보도한 사안에 대해 통신사들로부터 자료를 제출받아 사실 관계 확인에 나서겠다고 밝힌 바 있다. 당시 최민희 국회 과학기술정보방송통신위원회 위원장이 "KT와 LG유플러스가 자체적으로 (해킹 의혹을) 확인하고 보고한 내용을 신뢰할 수 있나"라고 묻자, 류 차관은 "자료를 제출받아 볼 생각"이라고 말했다. 류 차관에 따르면 두 통신사는 모두 사이버 침해 사실이 없다고 보고했다. 과기정통부 측은 "두 통신사의 침해 사고가 확인되면 투명하게 공개하겠다"고 말했다. KT와 LG유플러스 측은 "현재까지 데이터가 침해된 사실은 없다"라며 "정부 조사에 적극적으로 임하겠다"라고 밝혔다.',
    },
  }

  const handleGuestSelect = (e: React.MouseEvent<HTMLButtonElement>) => {
    setClickPos({ x: e.clientX, y: e.clientY })
    setGuestOpen(false)
    setChoiceOpen(true)
    setShowEventIcon(false)
  }

  const handleChoice = (choiceCode: string) => {
    console.log('Selected choice:', choiceCode)
    setSelectedChoiceCode(choiceCode as 'A' | 'B')
    setChoiceOpen(false)
    setArticleOpen(true)
  }

  const handleFeedbackClose = () => {
    setFeedbackOpen(false)
    // 토스트 메시지 기능 비활성화 (ArticleDialog에서 댓글로 대체)
    // let messages: string[] = []
    // if (selectedChoiceCode) {
    //   const selectedChoice = mockCard.choices.find(
    //     (choice: any) => choice.code === selectedChoiceCode,
    //   )
    //   messages = selectedChoice?.comments || []
    // }
    // setToastMessages(messages)
  }

  const resetDialogs = () => {
    setGuestOpen(false)
    setChoiceOpen(false)
    setArticleOpen(false)
    setFeedbackOpen(false)
    setOnboardingOpen(false)
    setClickPos(null)
    setShowEventIcon(false)
    setEventIconAnimation(false)
  }

  // ArticleDialog 핸들러
  const handleViewRelatedArticle = () => {
    setArticleOpen(false)
    setFeedbackOpen(true)
  }

  const handleOnboardingClose = () => {
    setOnboardingOpen(false)
    setOnboardingStep(0)
  }

  const handleOnboardingNext = () => {
    if (onboardingStep < onboardingTexts.length - 1) {
      setOnboardingStep(onboardingStep + 1)
    } else {
      setOnboardingOpen(false)
      setOnboardingStep(0)
    }
  }

  const triggerEventIcon = () => {
    setShowEventIcon(false)
    setTimeout(() => {
      setShowEventIcon(true)
      setEventIconAnimation(true)
      // 팝업 사운드 재생
      const popSound = new Audio('/sounds/game-bonus-02-294436.mp3')
      popSound.volume = 0.7
      popSound.play().catch(console.error)

      // 애니메이션 클래스 제거
      setTimeout(() => {
        setEventIconAnimation(false)
      }, 600)
    }, 100)
  }

  // 처음 로드 시 말풍선 아이콘 표시
  useEffect(() => {
    triggerEventIcon()
  }, [])

  return (
    <TestContainer>
      <TestHeader>
        <TestTitle>GuestDialog 테스트 페이지</TestTitle>
        <TestControls>
          <TestButton onClick={() => setGuestOpen(true)}>
            Guest Dialog 열기
          </TestButton>
          <TestButton onClick={() => setChoiceOpen(true)}>
            Choice Dialog 열기
          </TestButton>
          <TestButton onClick={() => setArticleOpen(true)}>
            Article Dialog 열기
          </TestButton>
          <TestButton onClick={() => setFeedbackOpen(true)}>
            Feedback Dialog 열기
          </TestButton>
          <TestButton onClick={triggerEventIcon}>
            말풍선 애니메이션 테스트
          </TestButton>
          <TestButton
            onClick={() => setOnboardingOpen(true)}
            style={{ background: '#0883BD' }}
          >
            온보딩 다이얼로그 테스트
          </TestButton>
          <TestButton
            onClick={() => window.open('/ending?endingCode=ECO_HIGH', '_blank')}
          >
            엔딩 페이지 테스트
          </TestButton>
          <TestButton onClick={resetDialogs} variant="reset">
            모든 Dialog 닫기
          </TestButton>
        </TestControls>
      </TestHeader>

      {/* 게임 화면과 동일한 배경 */}
      <MockGameContainer>
        <MockBackgroundImage src="/backgrounds/game_background.png" />

        {/* 말풍선 아이콘 */}
        {showEventIcon && (
          <MockEventIcon
            src="/icons/말풍선.png"
            className={eventIconAnimation ? 'pop-animation' : ''}
            onClick={() => setGuestOpen(true)}
          />
        )}

        {/* 파라미터들 */}
        <Parameter type="eco" value={mockStats.eco} x={10} y={53} />
        <Parameter type="env" value={mockStats.env} x={29} y={54} />
        <Parameter type="opi" value={mockStats.opi} x={72} y={54} />
        <Parameter type="mil" value={mockStats.mil} x={90} y={54} />
      </MockGameContainer>

      {/* Dialogs */}
      {guestOpen && (
        <GuestDialog
          guestName={mockCard.npc.name}
          guestText={mockCard.content}
          guestImage={mockCard.npc.imageUrl}
          open
          onClose={() => setGuestOpen(false)}
          onSelect={handleGuestSelect}
          enableTypewriter={true}
        />
      )}

      {/* 온보딩 다이얼로그 */}
      {onboardingOpen && (
        <GuestDialog
          guestName="보좌관 뉴토"
          guestText={onboardingTexts[onboardingStep]}
          guestImage="/icons/npc1.png"
          open
          onClose={handleOnboardingClose}
          onSelect={handleOnboardingNext}
          variant="onboarding"
          closeButtonText="건너뛰기"
          selectButtonText={
            onboardingStep === onboardingTexts.length - 1 ? '시작하기' : '다음'
          }
          enableTypewriter={true}
        />
      )}

      {choiceOpen && (
        <ChoiceDialog
          gameId={999} // 테스트용 gameId
          guestText={mockCard.content}
          choices={mockCard.choices}
          currentStats={mockStats}
          open
          initialMousePos={clickPos}
          onBack={() => {
            setChoiceOpen(false)
            setGuestOpen(true)
          }}
          onSelect={handleChoice}
        />
      )}

      {/* ArticleDialog */}
      {articleOpen && (
        <ArticleDialog
          open={articleOpen}
          playerName="테스트플레이어"
          countryName="테스트나라"
          currentTurn={1}
          selectedChoice={{
            code: selectedChoiceCode || 'A',
            label:
              mockCard.choices.find(
                (choice: any) => choice.code === (selectedChoiceCode || 'A'),
              )?.label || '정책 즉시 도입',
            comments:
              mockCard.choices.find(
                (choice: any) => choice.code === (selectedChoiceCode || 'A'),
              )?.comments || [],
          }}
          onClose={undefined}
          onViewRelatedArticle={handleViewRelatedArticle}
        />
      )}

      {feedbackOpen && (
        <FeedbackDialog
          open
          article={mockCard.relatedArticle}
          onClose={handleFeedbackClose}
        />
      )}

      {/* 기존 토스트 메시지 (ArticleDialog에서 댓글로 대체) */}
      {/* <FeedbackToastContainer messages={toastMessages} /> */}
    </TestContainer>
  )
}
