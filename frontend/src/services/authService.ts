import api from './api';
import {
  BaseResponse,
  LoginRequest,
  LoginResponse,
  RefreshTokenRequest,
  SignupRequest,
  UserResponse
} from '@/types/auth';

class AuthService {
  private readonly TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_KEY = 'user';

  // 로그인
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await api.post<BaseResponse<LoginResponse>>('/auth/login', credentials);

    if (response.data.success && response.data.data) {
      const {accessToken, refreshToken, username, roles} = response.data.data;

      // 토큰 저장
      this.setTokens(accessToken, refreshToken);

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
  async signup(data: SignupRequest): Promise<UserResponse> {
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
      const {accessToken, refreshToken: newRefreshToken} = response.data.data;

      // 새 토큰 저장
      this.setTokens(accessToken, newRefreshToken);

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

  // 토큰 관리 메서드들
  setTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(this.TOKEN_KEY, accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  setUser(user: UserResponse): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  getUser(): UserResponse | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  clearAuth(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);

    // Authorization 헤더 제거
    delete api.defaults.headers.common['Authorization'];
  }

  setAuthHeader(token: string): void {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  }

  // 초기화 - 앱 시작 시 토큰이 있으면 헤더에 설정
  initializeAuth(): void {
    const token = this.getAccessToken();
    if (token) {
      this.setAuthHeader(token);
    }
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }
}

export default new AuthService();
