import api from './api';
import {
  BaseResponse,
  LoginRequest,
  LoginResponse,
  RefreshTokenRequest,
  SignupRequest,
  UserResponse
} from '@/types/auth';

// 토큰 저장 인터페이스
interface TokenData {
  token: string;
  expiresAt: number; // timestamp
}

class AuthService {
  private readonly TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_KEY = 'user';

  // 로그인
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await api.post<BaseResponse<LoginResponse>>('/auth/login', credentials);

    if (response.data.success && response.data.data) {
      const {accessToken, refreshToken, expiresIn, username, roles} = response.data.data;

      // 토큰을 만료시간과 함께 저장
      this.setTokenWithExpiry(this.TOKEN_KEY, accessToken, expiresIn);
      this.setToken(this.REFRESH_TOKEN_KEY, refreshToken);

      // 사용자 정보 저장
      const user = {username, roles} as UserResponse;
      this.setUser(user);

      // Authorization 헤더 설정
      this.setAuthHeader(accessToken);

      return response.data.data;
    }

    throw new Error(response.data.message || '로그인에 실패했습니다.');
  }

  // 회원가입
  async signup(data: SignupRequest & { confirmPassword?: string }): Promise<UserResponse> {
    const response = await api.post<BaseResponse<UserResponse>>('/auth/signup', data);

    if (response.data.success && response.data.data) {
      return response.data.data;
    }

    throw new Error(response.data.message || '회원가입에 실패했습니다.');
  }

  // 토큰 갱신
  async refreshToken(refreshToken: string): Promise<LoginResponse> {
    const response = await api.post<BaseResponse<LoginResponse>>('/auth/refresh', {
      refreshToken
    } as RefreshTokenRequest);

    if (response.data.success && response.data.data) {
      const {accessToken, refreshToken: newRefreshToken, expiresIn} = response.data.data;

      // 새 토큰을 만료시간과 함께 저장
      this.setTokenWithExpiry(this.TOKEN_KEY, accessToken, expiresIn);
      this.setToken(this.REFRESH_TOKEN_KEY, newRefreshToken);

      // Authorization 헤더 갱신
      this.setAuthHeader(accessToken);

      return response.data.data;
    }

    throw new Error('토큰 갱신에 실패했습니다.');
  }

  // 현재 사용자 정보 조회
  async getCurrentUser(): Promise<UserResponse> {
    const response = await api.get<BaseResponse<UserResponse>>('/auth/me');

    if (response.data.success && response.data.data) {
      // 사용자 정보 업데이트
      this.setUser(response.data.data);
      return response.data.data;
    }

    throw new Error('사용자 정보를 가져올 수 없습니다.');
  }

  // 로그아웃
  async logout(): Promise<void> {
    try {
      await api.post('/auth/logout');
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // 로컬 스토리지 클리어
      this.clearAuth();
    }
  }

  // 토큰 검증
  async validateToken(): Promise<boolean> {
    try {
      const response = await api.post<BaseResponse<boolean>>('/auth/validate');
      return response.data.data || false;
    } catch (error) {
      return false;
    }
  }

  // Access Token 가져오기
  getAccessToken(): string | null {
    return this.getTokenWithExpiry(this.TOKEN_KEY);
  }

  // Refresh Token 가져오기
  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  // Access Token 만료 체크
  isAccessTokenExpired(): boolean {
    return this.getAccessToken() === null;
  }

  // Access Token 만료까지 남은 시간 (초)
  getTokenTimeToExpiry(): number {
    try {
      const stored = localStorage.getItem(this.TOKEN_KEY);
      if (!stored) return 0;

      const tokenData: TokenData = JSON.parse(stored);
      const timeToExpiry = Math.max(0, Math.floor((tokenData.expiresAt - Date.now()) / 1000));
      return timeToExpiry;
    } catch (error) {
      return 0;
    }
  }

  // 사용자 정보 관리
  setUser(user: UserResponse): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  getUser(): UserResponse | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  // 인증 정보 클리어
  clearAuth(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);

    // Authorization 헤더 제거
    delete api.defaults.headers.common['Authorization'];
  }

  // Bearer 토큰 헤더 설정
  setAuthHeader(token: string): void {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  }

  // 초기화 - 앱 시작 시 유효한 토큰이 있으면 헤더에 설정
  initializeAuth(): boolean {
    const token = this.getAccessToken();
    if (token) {
      this.setAuthHeader(token);
      return true;
    }
    return false;
  }

  // 인증 상태 확인
  isAuthenticated(): boolean {
    const token = this.getAccessToken();
    return !!token;
  }

  // 토큰 자동 갱신이 필요한지 확인 (만료 5분 전)
  shouldRefreshToken(): boolean {
    const timeToExpiry = this.getTokenTimeToExpiry();
    return timeToExpiry > 0 && timeToExpiry <= 300; // 5분 = 300초
  }

  // 자동 토큰 갱신
  async autoRefreshToken(): Promise<boolean> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return false;
    }

    try {
      await this.refreshToken(refreshToken);
      return true;
    } catch (error) {
      console.error('Auto refresh failed:', error);
      this.clearAuth();
      return false;
    }
  }

  // 토큰을 만료시간과 함께 저장
  private setTokenWithExpiry(key: string, token: string, expiresInSeconds: number): void {
    const expiresAt = Date.now() + (expiresInSeconds * 1000);
    const tokenData: TokenData = {
      token,
      expiresAt
    };
    localStorage.setItem(key, JSON.stringify(tokenData));
  }

  // 일반 토큰 저장 (리프레시 토큰용)
  private setToken(key: string, token: string): void {
    localStorage.setItem(key, token);
  }

  // 토큰 가져오기 (만료 체크 포함)
  private getTokenWithExpiry(key: string): string | null {
    try {
      const stored = localStorage.getItem(key);
      if (!stored) return null;

      const tokenData: TokenData = JSON.parse(stored);

      // 토큰이 만료되었는지 확인
      if (Date.now() >= tokenData.expiresAt) {
        localStorage.removeItem(key);
        return null;
      }

      return tokenData.token;
    } catch (error) {
      console.error('Token parsing error:', error);
      localStorage.removeItem(key);
      return null;
    }
  }
}

export default new AuthService();
