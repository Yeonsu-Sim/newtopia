import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'

export const Route = createFileRoute('/admin/login')({
  component: AdminLoginPage,
})

// 기존 하드코딩된 관리자 계정 제거
// 이제 백엔드 인증을 사용합니다

function AdminLoginPage() {
  // 입력 폼 관련 state 제거 - 이제 백엔드 인증만 사용

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: '#f8f9fa',
      fontFamily: 'Arial, sans-serif'
    }}>
      <div style={{
        backgroundColor: 'white',
        padding: '40px',
        borderRadius: '8px',
        boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
        width: '100%',
        maxWidth: '400px'
      }}>
        <div style={{ textAlign: 'center', marginBottom: '40px' }}>
          <h1 style={{ color: '#333', margin: '0 0 15px 0' }}>🔐 관리자 페이지 접근</h1>
          <p style={{ color: '#666', margin: '0', fontSize: '18px' }}>
            관리자 인증이 필요합니다
          </p>
        </div>

        <div style={{
          backgroundColor: '#e7f3ff',
          border: '1px solid #b8daff',
          borderRadius: '8px',
          padding: '25px',
          marginBottom: '30px'
        }}>
          <h3 style={{ color: '#004085', margin: '0 0 15px 0', fontSize: '18px' }}>
            📌 관리자 인증 방법
          </h3>
          <ol style={{ color: '#004085', margin: '0', paddingLeft: '20px', lineHeight: '1.8' }}>
            <li>메인 페이지에서 관리자 계정으로 로그인</li>
            <li>로그인 완료 후 관리자 페이지로 접근</li>
            <li>백엔드에서 관리자 권한을 자동으로 확인합니다</li>
          </ol>
        </div>

        <div style={{ textAlign: 'center', marginBottom: '20px' }}>
          <button
            onClick={() => window.location.href = '/'}
            style={{
              padding: '14px 28px',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              fontSize: '16px',
              fontWeight: 'bold',
              cursor: 'pointer',
              boxShadow: '0 2px 4px rgba(0,123,255,0.2)'
            }}
          >
            메인 페이지로 이동 (로그인)
          </button>
        </div>

        <div style={{
          marginTop: '20px',
          padding: '15px',
          backgroundColor: '#fff3cd',
          borderRadius: '6px',
          border: '1px solid #ffeaa7'
        }}>
          <strong style={{ color: '#856404' }}>💡 참고:</strong>
          <div style={{ color: '#856404', fontSize: '14px', marginTop: '8px' }}>
            관리자 계정으로 로그인하면 자동으로 백엔드에서<br />
            관리자 권한을 확인하여 접근을 허용합니다.
          </div>
        </div>

        <div style={{
          marginTop: '20px',
          textAlign: 'center'
        }}>
          <button
            style={{
              backgroundColor: 'transparent',
              color: '#6c757d',
              border: 'none',
              textDecoration: 'underline',
              cursor: 'pointer'
            }}
            onClick={() => window.location.href = '/'}
          >
            메인 페이지로 돌아가기
          </button>
        </div>
      </div>
    </div>
  )
}