import React from 'react';
import {BrowserRouter as Router, Outlet, Route, Routes} from 'react-router-dom';
import {QueryClient, QueryClientProvider} from 'react-query';
import {Toaster} from 'react-hot-toast';
import {AuthProvider} from '@/contexts/AuthContext';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import Navigation from '@/components/common/Navigation';
import MobileHeader from '@/components/common/MobileHeader';
import BottomNavigation from '@/components/common/BottomNavigation';

// Auth Pages
import LoginPage from '@/pages/LoginPage';
import SignupPage from '@/pages/SignupPage';

// Main Pages
import HomePage from '@/pages/HomePage';
import BeansPage from '@/pages/BeansPage';
import RecipesPage from '@/pages/RecipesPage';
import RecipeDetailPage from '@/pages/RecipeDetailPage';
import RecipeEditPage from '@/pages/RecipeEditPage';
import BeanDetailPage from '@/pages/BeanDetailPage';
import BeanEditPage from '@/pages/BeanEditPage';
import EquipmentsPage from '@/pages/EquipmentsPage';
import EquipmentDetailPage from '@/pages/EquipmentDetailPage';
import EquipmentEditPage from '@/pages/EquipmentEditPage';

// React Query 클라이언트 설정
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000, // 5분
    },
  },
});

// 메인 레이아웃 컴포넌트
const MainLayout: React.FC = () => {
  return (
      <>
        {/* 데스크톱 네비게이션 */}
        <Navigation/>

        {/* 모바일 헤더 */}
        <MobileHeader/>

        <main className="pb-16 md:pb-0">
          <Outlet/>
        </main>

        {/* 모바일 하단 네비게이션 */}
        <BottomNavigation/>
      </>
  );
};

const App: React.FC = () => {
  return (
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <Router>
            <div className="min-h-screen bg-gray-50">
              <Routes>
                {/* 인증 페이지 - 레이아웃 없음 */}
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/signup" element={<SignupPage/>}/>

                {/* 메인 레이아웃이 적용되는 페이지들 */}
                <Route element={<MainLayout/>}>
                  {/* 홈페이지는 인증 불필요 */}
                  <Route path="/" element={<HomePage/>}/>

                  {/* 보호된 라우트들 - 인증 필요 */}
                  <Route element={<ProtectedRoute><Outlet/></ProtectedRoute>}>
                    <Route path="/beans" element={<BeansPage/>}/>
                    <Route path="/beans/:id" element={<BeanDetailPage/>}/>
                    <Route path="/beans/:id/edit" element={<BeanEditPage/>}/>
                    <Route path="/recipes" element={<RecipesPage/>}/>
                    <Route path="/recipes/:id" element={<RecipeDetailPage/>}/>
                    <Route path="/recipes/:id/edit" element={<RecipeEditPage/>}/>
                    <Route path="/equipments" element={<EquipmentsPage/>}/>
                    <Route path="/equipments/:id" element={<EquipmentDetailPage/>}/>
                    <Route path="/equipments/:id/edit" element={<EquipmentEditPage/>}/>
                  </Route>

                  {/* 404 페이지 */}
                  <Route path="*" element={
                    <div className="min-h-screen flex items-center justify-center">
                      <div className="text-center">
                        <h1 className="text-4xl font-bold text-gray-900 mb-4">404</h1>
                        <p className="text-gray-600 mb-8">페이지를 찾을 수 없습니다.</p>
                        <a
                            href="/"
                            className="text-coffee-600 hover:text-coffee-700 font-medium"
                        >
                          홈으로 돌아가기
                        </a>
                      </div>
                    </div>
                  }/>
                </Route>
              </Routes>

              {/* Toast 알림 */}
              <Toaster
                  position="top-right"
                  toastOptions={{
                    duration: 3000,
                    style: {
                      background: '#363636',
                      color: '#fff',
                    },
                    success: {
                      style: {
                        background: '#059669',
                      },
                    },
                    error: {
                      style: {
                        background: '#DC2626',
                      },
                    },
                  }}
              />
            </div>
          </Router>
        </AuthProvider>
      </QueryClientProvider>
  );
};

export default App;
