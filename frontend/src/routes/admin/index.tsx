import { createFileRoute } from '@tanstack/react-router'
import { useEffect, useState } from 'react'

export const Route = createFileRoute('/admin/')({
  component: AdminPage,
})

function AdminPage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [loading, setLoading] = useState(true)
  const [adminUser, setAdminUser] = useState('')

  useEffect(() => {
    const authStatus = localStorage.getItem('adminAuth')
    const user = localStorage.getItem('adminUser')

    if (authStatus === 'true' && user) {
      setIsAuthenticated(true)
      setAdminUser(user)
    } else {
      window.location.href = '/admin/login'
      return
    }
    setLoading(false)
  }, [])

  const handleLogout = () => {
    if (confirm('로그아웃 하시겠습니까?')) {
      localStorage.removeItem('adminAuth')
      localStorage.removeItem('adminUser')
      window.location.href = '/admin/login'
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
              <li>총 공지사항: 24개</li>
              <li>활성 사용자: 156명</li>
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