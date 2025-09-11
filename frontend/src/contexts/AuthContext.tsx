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
        authService.initializeAuth();

        if (authService.isAuthenticated()) {
          // 토큰 검증
          const isValid = await authService.validateToken();

          if (isValid) {
            // 사용자 정보 가져오기
            const userData = await authService.getCurrentUser();
            setUser(userData);
            setIsAuthenticated(true);
          } else {
            // 토큰이 유효하지 않으면 리프레시 시도
            const refreshToken = authService.getRefreshToken();
            if (refreshToken) {
              try {
                await authService.refreshToken(refreshToken);
                const userData = await authService.getCurrentUser();
                setUser(userData);
                setIsAuthenticated(true);
              } catch (error) {
                // 리프레시도 실패하면 로그아웃
                authService.clearAuth();
              }
            }
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

  // 로그인
  const login = async (credentials: LoginRequest) => {
    try {
      // 사용자 정보 가져오기
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

  // 토큰 자동 갱신 설정 (선택적)
  useEffect(() => {
    if (!isAuthenticated) return;

    // 50분마다 토큰 갱신 (토큰 만료 시간이 1시간이라고 가정)
    const interval = setInterval(async () => {
      const refreshToken = authService.getRefreshToken();
      if (refreshToken) {
        try {
          await authService.refreshToken(refreshToken);
        } catch (error) {
          console.error('Token refresh failed:', error);
          logout();
        }
      }
    }, 50 * 60 * 1000); // 50분

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
