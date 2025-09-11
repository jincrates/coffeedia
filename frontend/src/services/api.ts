import axios, {AxiosError, AxiosInstance, AxiosResponse} from 'axios';
import {BaseResponse} from '@/types/api';

// Axios 인스턴스 생성
const api: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 토큰 관련 상수
const TOKEN_KEY = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';

// 토큰 데이터 인터페이스
interface TokenData {
  token: string;
  expiresAt: number;
}

// 토큰 가져오기 함수
const getValidToken = (): string | null => {
  try {
    const stored = localStorage.getItem(TOKEN_KEY);
    if (!stored) return null;

    const tokenData: TokenData = JSON.parse(stored);

    // 토큰이 만료되었는지 확인
    if (Date.now() >= tokenData.expiresAt) {
      localStorage.removeItem(TOKEN_KEY);
      return null;
    }

    return tokenData.token;
  } catch (error) {
    localStorage.removeItem(TOKEN_KEY);
    return null;
  }
};

// 리프레시 토큰 가져오기
const getRefreshToken = (): string | null => {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
};

// 인증 정보 클리어
const clearAuth = (): void => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem('user');
  delete api.defaults.headers.common['Authorization'];
};

// 토큰 갱신 플래그 (중복 요청 방지)
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: any) => void;
  reject: (reason?: any) => void;
}> = [];

// 대기 중인 요청 처리
const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach(({resolve, reject}) => {
    if (error) {
      reject(error);
    } else {
      resolve(token);
    }
  });

  failedQueue = [];
};

// 요청 인터셉터 - Bearer 토큰 자동 추가
api.interceptors.request.use(
    (config) => {
      // 로그인, 회원가입, 토큰 갱신 요청은 토큰 없이 진행
      const authNotRequiredPaths = ['/auth/login', '/auth/signup', '/auth/refresh'];
      const isAuthNotRequired = authNotRequiredPaths.some(path =>
          config.url?.includes(path)
      );

      if (!isAuthNotRequired) {
        const token = getValidToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      }

      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
);

// 응답 인터셉터 - 토큰 만료 시 자동 갱신
api.interceptors.response.use(
    (response: AxiosResponse<BaseResponse<any>>) => {
      return response;
    },
    async (error: AxiosError) => {
      const originalRequest = error.config;

      // 401 에러이고 토큰 갱신 요청이 아닌 경우
      if (error.response?.status === 401 && originalRequest && !originalRequest.url?.includes('/auth/refresh')) {

        // 이미 토큰 갱신 중인 경우 대기열에 추가
        if (isRefreshing) {
          return new Promise((resolve, reject) => {
            failedQueue.push({resolve, reject});
          }).then((token) => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }
            return api(originalRequest);
          }).catch((err) => {
            return Promise.reject(err);
          });
        }

        // 토큰 갱신 시작
        isRefreshing = true;
        const refreshToken = getRefreshToken();

        if (refreshToken) {
          try {
            // 토큰 갱신 요청
            const response = await api.post<BaseResponse<{
              accessToken: string;
              refreshToken: string;
              expiresIn: number;
            }>>('/auth/refresh', {
              refreshToken
            });

            if (response.data.success && response.data.data) {
              const {accessToken, refreshToken: newRefreshToken, expiresIn} = response.data.data;

              // 새 토큰 저장
              const expiresAt = Date.now() + (expiresIn * 1000);
              const tokenData: TokenData = {
                token: accessToken,
                expiresAt
              };
              localStorage.setItem(TOKEN_KEY, JSON.stringify(tokenData));
              localStorage.setItem(REFRESH_TOKEN_KEY, newRefreshToken);

              // 기본 헤더 설정
              api.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

              // 대기 중인 요청들 처리
              processQueue(null, accessToken);

              // 원래 요청 재시도
              if (originalRequest.headers) {
                originalRequest.headers.Authorization = `Bearer ${accessToken}`;
              }
              return api(originalRequest);
            }
          } catch (refreshError) {
            // 토큰 갱신 실패
            processQueue(refreshError, null);
            clearAuth();

            // 로그인 페이지로 리다이렉트 (필요한 경우)
            if (typeof window !== 'undefined') {
              window.location.href = '/login';
            }

            return Promise.reject(refreshError);
          } finally {
            isRefreshing = false;
          }
        } else {
          // 리프레시 토큰이 없으면 로그아웃
          clearAuth();

          // 로그인 페이지로 리다이렉트 (필요한 경우)
          if (typeof window !== 'undefined') {
            window.location.href = '/login';
          }
        }
      }

      // 기타 에러 처리
      if (error.response?.status === 403) {
        console.error('접근 권한이 없습니다.');
      } else if (error.response?.status === 500) {
        console.error('서버 오류가 발생했습니다.');
      }

      return Promise.reject(error);
    }
);

export default api;
