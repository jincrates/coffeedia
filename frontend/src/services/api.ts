import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { BaseResponse } from '@/types/api';

// Axios 인스턴스 생성
const api: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    // 필요한 경우 Authorization 헤더 추가
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
api.interceptors.response.use(
  (response: AxiosResponse<BaseResponse<any>>) => {
    return response;
  },
  (error) => {
    // 공통 에러 처리
    if (error.response?.status === 401) {
      // 인증 오류 처리
      console.error('인증이 필요합니다.');
    } else if (error.response?.status === 500) {
      // 서버 오류 처리
      console.error('서버 오류가 발생했습니다.');
    }
    return Promise.reject(error);
  }
);

export default api;
