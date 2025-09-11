import React, {createContext, ReactNode, useContext, useEffect, useState} from 'react';
import authService from '@/services/authService';
import {LoginRequest, SignupRequest, UserResponse} from '@/types/auth';
import toast from 'react-hot-toast';

interface AuthContextType {
  isAuthenticated: boolean;
  user: UserResponse | null;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  signup: (data: SignupRequest & { confirmPassword?: string }) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({children}) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [user, setUser] = useState<UserResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // 초기화 - 토큰이 있으면 사용자 정보 가져오기
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        // 저장된 토큰으로 인증 헤더 초기화
        const hasValidToken = authService.initializeAuth();

        if (hasValidToken) {
          // 토큰이 만료되었는지 확인
          if (authService.isAccessTokenExpired()) {
            // 토큰이 만료되었으면 리프레시 시도
            const refreshToken = authService.getRefreshToken();
            if (refreshToken) {
              try {
                await authService.refreshToken(refreshToken);
              } catch (error) {
                // 리프레시도 실패하면 로그아웃
                authService.clearAuth();
                return;
              }
            } else {
              authService.clearAuth();
              return;
            }
          }

          // 토큰이 유효하면 사용자 정보 가져오기
          try {
            const userData = await authService.getCurrentUser();
            setUser(userData);
            setIsAuthenticated(true);
          } catch (error) {
            // 사용자 정보를 가져올 수 없으면 토큰이 유효하지 않음
            console.error('Failed to get user info:', error);
            authService.clearAuth();
          }
        }
      } catch (error) {
        console.error('Auth initialization error:', error);
        authService.clearAuth();
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  // 로그인 - 올바르게 authService.login 호출
  const login = async (credentials: LoginRequest) => {
    try {
      // 1. 로그인 API 호출 (토큰 받아오기)
      const loginResponse = await authService.login(credentials);

      // 2. 사용자 정보 가져오기
      const userData = await authService.getCurrentUser();

      setUser(userData);
      setIsAuthenticated(true);

      toast.success('로그인되었습니다.');
    } catch (error: any) {
      const message = error.response?.data?.message || error.message || '로그인에 실패했습니다.';
      toast.error(message);
      throw error;
    }
  };

  // 회원가입
  const signup = async (data: SignupRequest & { confirmPassword?: string }) => {
    try {
      await authService.signup(data);
      toast.success('회원가입이 완료되었습니다. 로그인해주세요.');
    } catch (error: any) {
      const message = error.response?.data?.message || error.message || '회원가입에 실패했습니다.';
      toast.error(message);
      throw error;
    }
  };

  // 로그아웃
  const logout = async () => {
    try {
      await authService.logout();
      setUser(null);
      setIsAuthenticated(false);
      toast.success('로그아웃되었습니다.');
    } catch (error) {
      console.error('Logout error:', error);
      // 에러가 발생해도 로컬 상태는 클리어
      setUser(null);
      setIsAuthenticated(false);
    }
  };

  // 사용자 정보 새로고침
  const refreshUser = async () => {
    try {
      const userData = await authService.getCurrentUser();
      setUser(userData);
    } catch (error) {
      console.error('Failed to refresh user:', error);
    }
  };

  // 토큰 자동 갱신 설정
  useEffect(() => {
    if (!isAuthenticated) return;

    // 토큰 만료 5분 전에 자동 갱신
    const checkAndRefreshToken = async () => {
      if (authService.shouldRefreshToken()) {
        const success = await authService.autoRefreshToken();
        if (!success) {
          // 토큰 갱신 실패 시 로그아웃
          await logout();
        }
      }
    };

    // 1분마다 토큰 만료 시간 체크
    const interval = setInterval(checkAndRefreshToken, 60 * 1000);

    return () => clearInterval(interval);
  }, [isAuthenticated]);

  // 토큰 만료 시 자동 로그아웃 감지
  useEffect(() => {
    if (!isAuthenticated) return;

    const checkTokenExpiry = () => {
      if (authService.isAccessTokenExpired() && !authService.getRefreshToken()) {
        logout();
      }
    };

    // 30초마다 토큰 만료 체크
    const interval = setInterval(checkTokenExpiry, 30 * 1000);

    return () => clearInterval(interval);
  }, [isAuthenticated]);

  return (
      <AuthContext.Provider
          value={{
            isAuthenticated,
            user,
            isLoading,
            login,
            signup,
            logout,
            refreshUser,
          }}
      >
        {children}
      </AuthContext.Provider>
  );
};
