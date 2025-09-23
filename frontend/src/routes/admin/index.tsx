import { createFileRoute } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { verifyAdminApi, getNoticeStatsApi, getSuggestionStatsApi } from '../../services/adminApi'

export const Route = createFileRoute('/admin/')({
  component: AdminPage,
})

function AdminPage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [loading, setLoading] = useState(true)
  const [adminUser, setAdminUser] = useState('')
  const [noticeStats, setNoticeStats] = useState({ totalCount: 0, recentCount: 0 })
  const [suggestionStats, setSuggestionStats] = useState({ totalCount: 0, recentCount: 0 })

  useEffect(() => {
    const checkAuth = async () => {
      // 백엔드 관리자 권한 확인만 사용
      try {
        const backendResult = await verifyAdminApi()
        if (backendResult.status === 'success' && backendResult.data) {
          setIsAuthenticated(true)
          setAdminUser(backendResult.data.nickname || backendResult.data.email)
          console.log('백엔드 관리자 인증 성공:', backendResult.data)

          // 인증 성공 후 통계 데이터 로드
          await loadStats()
        } else {
          window.location.href = '/admin/login'
          return
        }
      } catch (error) {
        console.log('백엔드 관리자 인증 실패:', error)
        window.location.href = '/admin/login'
        return
      }
      setLoading(false)
    }

    const loadStats = async () => {
      try {
        const [noticeData, suggestionData] = await Promise.all([
          getNoticeStatsApi(),
          getSuggestionStatsApi()
        ])

        if (noticeData.status === 'success' && noticeData.data) {
          setNoticeStats({
            totalCount: noticeData.data.totalCount,
            recentCount: noticeData.data.recentCount
          })
        }

        if (suggestionData.status === 'success' && suggestionData.data) {
          setSuggestionStats({
            totalCount: suggestionData.data.totalCount,
            recentCount: suggestionData.data.recentCount
          })
        }
      } catch (error) {
        console.error('통계 데이터 로드 실패:', error)
      }
    }

    checkAuth()
  }, [])

  const handleLogout = () => {
    if (confirm('로그아웃 하시겠습니까?')) {
      // 백엔드 로그아웃 처리 (쿠키 삭제)
      fetch('/api/v1/auth/logout', {
        method: 'POST',
        credentials: 'include'
      }).finally(() => {
        window.location.href = '/admin/login'
      })
    }
  }

  if (loading) {
    return (
      <div style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontFamily: 'Arial, sans-serif'
      }}>
        <div>로딩 중...</div>
      </div>
    )
  }

  if (!isAuthenticated) {
    return null
  }

  return (
    <div style={{
      minHeight: '100vh',
      padding: '20px',
      fontFamily: 'Arial, sans-serif',
      backgroundColor: '#f5f5f5'
    }}>
      <div style={{
        maxWidth: '1200px',
        margin: '0 auto',
        backgroundColor: 'white',
        padding: '30px',
        borderRadius: '8px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)'
      }}>
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '30px'
        }}>
          <h1 style={{ color: '#333', margin: '0' }}>
            🔧 관리자 페이지
          </h1>
          <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
            <span style={{ color: '#666' }}>
              👤 {adminUser}님 환영합니다
            </span>
            <span style={{
              color: '#28a745',
              fontSize: '14px',
              padding: '4px 8px',
              backgroundColor: '#d4edda',
              borderRadius: '4px'
            }}>
              🔒 관리자 인증됨
            </span>
            <button
              onClick={handleLogout}
              style={{
                padding: '8px 16px',
                backgroundColor: '#dc3545',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
            >
              로그아웃
            </button>
          </div>
        </div>

        <div style={{ marginBottom: '20px' }}>
          <p style={{ color: '#666', fontSize: '16px' }}>
            관리자 전용 페이지입니다. 여기서 시스템을 관리할 수 있습니다.
          </p>
        </div>

        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
          gap: '20px',
          marginTop: '30px'
        }}>
          <div style={{
            padding: '20px',
            border: '1px solid #ddd',
            borderRadius: '6px',
            backgroundColor: '#f9f9f9'
          }}>
            <h3 style={{ color: '#444', marginBottom: '15px' }}>📊 시스템 현황</h3>
            <ul style={{ color: '#666', lineHeight: '1.6' }}>
              <li>총 공지사항: {noticeStats.totalCount}개</li>
              <li>최근 7일 공지사항: {noticeStats.recentCount}개</li>
              <li>총 건의사항: {suggestionStats.totalCount}개</li>
              <li>최근 7일 건의사항: {suggestionStats.recentCount}개</li>
              <li>서버 상태: 정상</li>
            </ul>
          </div>

          <div style={{
            padding: '20px',
            border: '1px solid #ddd',
            borderRadius: '6px',
            backgroundColor: '#f9f9f9'
          }}>
            <h3 style={{ color: '#444', marginBottom: '15px' }}>🛠️ 관리 기능</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              <button
                style={{
                  padding: '10px 15px',
                  backgroundColor: '#007bff',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer'
                }}
                onClick={() => window.location.href = '/admin/notices'}
              >
                공지사항 관리
              </button>
              <button
                style={{
                  padding: '10px 15px',
                  backgroundColor: '#28a745',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer'
                }}
                onClick={() => alert('기능 준비 중입니다')}
              >
                사용자 관리
              </button>
            </div>
          </div>
        </div>

        <div style={{
          marginTop: '40px',
          padding: '15px',
          backgroundColor: '#e9ecef',
          borderRadius: '4px',
          borderLeft: '4px solid #007bff'
        }}>
          <strong style={{ color: '#495057' }}>참고:</strong>
          <span style={{ color: '#6c757d', marginLeft: '10px' }}>
            이 페이지는 관리자만 접근할 수 있습니다. 일반 사용자 페이지와는 완전히 분리되어 있습니다.
          </span>
        </div>
      </div>
    </div>
  )
}