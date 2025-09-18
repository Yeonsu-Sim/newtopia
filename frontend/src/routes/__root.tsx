import { Outlet, createRootRouteWithContext } from '@tanstack/react-router'
// import { TanStackRouterDevtoolsPanel } from '@tanstack/react-router-devtools'
// import { TanstackDevtools } from '@tanstack/react-devtools'
import { useEffect } from 'react'

import { useAuthStore } from '../store/authStore'

// import TanStackQueryDevtools from '../integrations/tanstack-query/devtools'

import type { QueryClient } from '@tanstack/react-query'

interface MyRouterContext {
  queryClient: QueryClient
}

function RootComponent() {
  const { initializeAuth, isInitialized } = useAuthStore();

  // 앱 시작시 서버에서 인증 상태 확인
  useEffect(() => {
    initializeAuth();
  }, [initializeAuth]);

  // 초기화가 완료되지 않았으면 로딩 화면 표시
  if (!isInitialized) {
    return (
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        height: '100vh',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        color: 'white',
        fontSize: '24px',
        fontFamily: 'DNFBitBitv2, Noto Sans KR, sans-serif'
      }}>
        뉴토피아 로딩 중...
      </div>
    );
  }

  return (
    <>
      {/* <Header /> */}
      <Outlet />
      {/* <TanstackDevtools
        config={{
          position: 'bottom-left',
        }}
        plugins={[
          {
            name: 'Tanstack Router',
            render: <TanStackRouterDevtoolsPanel />,
          },
          TanStackQueryDevtools,
        ]}
      /> */}
    </>
  );
}

export const Route = createRootRouteWithContext<MyRouterContext>()({
  component: RootComponent,
})
