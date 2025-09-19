import { Outlet, createRootRouteWithContext } from '@tanstack/react-router'
// import { TanStackRouterDevtoolsPanel } from '@tanstack/react-router-devtools'
// import { TanstackDevtools } from '@tanstack/react-devtools'
import { useEffect } from 'react'

import { useAuthStore } from '../store/authStore'
import { LoadingScreen } from '@/components/common/LoadingScreen'

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
    return <LoadingScreen />;
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
