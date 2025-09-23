import { createFileRoute } from '@tanstack/react-router'
import { useState, useEffect } from 'react'
import { verifyAdminApi, getAllNoticesApi, createNoticeApi, deleteNoticeApi } from '../../../services/adminApi'
import type { Notice } from '../../../services/adminApi'

// Mock 데이터 제거 - 백엔드 API 사용

export const Route = createFileRoute('/admin/notices/')({
  component: AdminNoticesPage,
})

function AdminNoticesPage() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [loading, setLoading] = useState(true)
  const [adminUser, setAdminUser] = useState('')
  const [notices, setNotices] = useState<Notice[]>([])
  const [newNotice, setNewNotice] = useState({
    title: '',
    content: '',
    type: 'NOTICE'
  })
  const [showAddForm, setShowAddForm] = useState(false)

  const loadNotices = async () => {
    try {
      const result = await getAllNoticesApi()
      if (result.status === 'success' && result.data) {
        setNotices(result.data)
      }
    } catch (error) {
      console.error('공지사항 로드 실패:', error)
    }
  }

  useEffect(() => {
    const checkAuth = async () => {
      // 백엔드 관리자 권한 확인만 사용
      try {
        const backendResult = await verifyAdminApi()
        if (backendResult.status === 'success' && backendResult.data) {
          setIsAuthenticated(true)
          setAdminUser(backendResult.data.nickname || backendResult.data.email)
          console.log('백엔드 관리자 인증 성공:', backendResult.data)

          // 인증 성공 후 공지사항 데이터 로드
          await loadNotices()
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

  const handleAddNotice = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!newNotice.title || !newNotice.content) {
      alert('제목과 내용을 입력해주세요.')
      return
    }

    try {
      const result = await createNoticeApi(newNotice)
      if (result.status === 'success') {
        // 공지사항 목록 새로고침
        await loadNotices()
        setNewNotice({ title: '', content: '', type: 'NOTICE' })
        setShowAddForm(false)
        alert('공지사항이 추가되었습니다.')
      }
    } catch (error) {
      console.error('공지사항 추가 실패:', error)
      alert('공지사항 추가에 실패했습니다.')
    }
  }

  const handleDeleteNotice = async (id: number) => {
    if (confirm('정말로 삭제하시겠습니까?')) {
      try {
        const result = await deleteNoticeApi(id)
        if (result.status === 'success') {
          // 공지사항 목록 새로고침
          await loadNotices()
          alert('공지사항이 삭제되었습니다.')
        }
      } catch (error) {
        console.error('공지사항 삭제 실패:', error)
        alert('공지사항 삭제에 실패했습니다.')
      }
    }
  }

  const getTypeLabel = (type: string) => {
    switch (type) {
      case 'NOTICE': return '일반공지'
      case 'HOTFIX': return '핫픽스'
      case 'EVENT': return '이벤트'
      case 'UPDATE': return '업데이트'
      default: return type
    }
  }

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'NOTICE': return '#007bff'
      case 'HOTFIX': return '#dc3545'
      case 'EVENT': return '#28a745'
      case 'UPDATE': return '#ffc107'
      default: return '#6c757d'
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
          <div>
            <h1 style={{ color: '#333', margin: '0' }}>📢 공지사항 관리</h1>
            <div style={{ marginTop: '10px', display: 'flex', gap: '10px' }}>
              <button
                style={{
                  padding: '8px 16px',
                  backgroundColor: '#6c757d',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer'
                }}
                onClick={() => window.location.href = '/admin'}
              >
                ← 관리자 메인으로
              </button>
              <span style={{ color: '#666', padding: '8px 0' }}>
                👤 {adminUser}님
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
          <button
            style={{
              padding: '12px 20px',
              backgroundColor: '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '16px'
            }}
            onClick={() => setShowAddForm(!showAddForm)}
          >
            {showAddForm ? '취소' : '+ 새 공지사항'}
          </button>
        </div>

        {showAddForm && (
          <div style={{
            backgroundColor: '#f8f9fa',
            padding: '20px',
            borderRadius: '6px',
            marginBottom: '30px',
            border: '1px solid #dee2e6'
          }}>
            <h3 style={{ color: '#495057', marginBottom: '20px' }}>새 공지사항 작성</h3>
            <form onSubmit={handleAddNotice}>
              <div style={{ marginBottom: '15px' }}>
                <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                  제목
                </label>
                <input
                  type="text"
                  value={newNotice.title}
                  onChange={(e) => setNewNotice({...newNotice, title: e.target.value})}
                  style={{
                    width: '100%',
                    padding: '10px',
                    border: '1px solid #ced4da',
                    borderRadius: '4px',
                    fontSize: '16px'
                  }}
                  placeholder="공지사항 제목을 입력하세요"
                />
              </div>
              <div style={{ marginBottom: '15px' }}>
                <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                  타입
                </label>
                <select
                  value={newNotice.type}
                  onChange={(e) => setNewNotice({...newNotice, type: e.target.value})}
                  style={{
                    padding: '10px',
                    border: '1px solid #ced4da',
                    borderRadius: '4px',
                    fontSize: '16px'
                  }}
                >
                  <option value="NOTICE">일반공지</option>
                  <option value="HOTFIX">핫픽스</option>
                  <option value="EVENT">이벤트</option>
                  <option value="UPDATE">업데이트</option>
                </select>
              </div>
              <div style={{ marginBottom: '20px' }}>
                <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                  내용
                </label>
                <textarea
                  value={newNotice.content}
                  onChange={(e) => setNewNotice({...newNotice, content: e.target.value})}
                  style={{
                    width: '100%',
                    height: '120px',
                    padding: '10px',
                    border: '1px solid #ced4da',
                    borderRadius: '4px',
                    fontSize: '16px',
                    resize: 'vertical'
                  }}
                  placeholder="공지사항 내용을 입력하세요"
                />
              </div>
              <button
                type="submit"
                style={{
                  padding: '12px 24px',
                  backgroundColor: '#007bff',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontSize: '16px'
                }}
              >
                공지사항 추가
              </button>
            </form>
          </div>
        )}

        <div style={{ marginBottom: '20px' }}>
          <h3 style={{ color: '#495057' }}>전체 공지사항 ({notices.length}개)</h3>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          {notices.map((notice) => (
            <div
              key={notice.id}
              style={{
                border: '1px solid #dee2e6',
                borderRadius: '6px',
                padding: '20px',
                backgroundColor: '#fff'
              }}
            >
              <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'flex-start',
                marginBottom: '10px'
              }}>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '5px' }}>
                    <span
                      style={{
                        padding: '4px 8px',
                        backgroundColor: getTypeColor(notice.type),
                        color: 'white',
                        borderRadius: '12px',
                        fontSize: '12px',
                        fontWeight: 'bold'
                      }}
                    >
                      {getTypeLabel(notice.type)}
                    </span>
                    <span style={{ color: '#6c757d', fontSize: '14px' }}>
                      {new Date(notice.createdAt).toLocaleDateString('ko-KR')}
                    </span>
                  </div>
                  <h4 style={{ color: '#333', margin: '0 0 10px 0', fontSize: '18px' }}>
                    {notice.title}
                  </h4>
                  <p style={{ color: '#666', margin: '0', lineHeight: '1.5' }}>
                    {notice.content}
                  </p>
                </div>
                <button
                  style={{
                    padding: '8px 12px',
                    backgroundColor: '#dc3545',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    marginLeft: '15px'
                  }}
                  onClick={() => handleDeleteNotice(notice.id)}
                >
                  삭제
                </button>
              </div>
            </div>
          ))}
        </div>

        {notices.length === 0 && (
          <div style={{
            textAlign: 'center',
            padding: '60px',
            color: '#6c757d'
          }}>
            <div style={{ fontSize: '48px', marginBottom: '20px' }}>📭</div>
            <p>등록된 공지사항이 없습니다.</p>
          </div>
        )}
      </div>
    </div>
  )
}