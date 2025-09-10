import React from 'react';
import {Navigate, useLocation} from 'react-router-dom';
import {useAuth} from '@/contexts/AuthContext';
import {Loader2} from 'lucide-react';

interface ProtectedRouteProps {
  children: React.ReactNode;
  redirectTo?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
                                                         children,
                                                         redirectTo = '/login'
                                                       }) => {
  const {isAuthenticated, isLoading} = useAuth();
  const location = useLocation();

  // 로딩 중일 때
  if (isLoading) {
    return (
        <div className="min-h-screen flex items-center justify-center">
          <Loader2 className="w-8 h-8 animate-spin text-coffee-600"/>
        </div>
    );
  }

  // 인증되지 않은 경우 로그인 페이지로 리다이렉트
  if (!isAuthenticated) {
    return <Navigate to={redirectTo} state={{from: location}} replace/>;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
