import api from './api';
import {
  BaseResponse,
  PageResponse,
  BeanResponse,
  CreateBeanCommand,
  UpdateBeanCommand,
  BeanSearchQuery,
} from '@/types/api';

class BeanService {
  private readonly BASE_URL = '/beans';

  // Bean 생성
  async createBean(command: CreateBeanCommand): Promise<BeanResponse> {
    const response = await api.post<BaseResponse<BeanResponse>>(this.BASE_URL, command);
    return response.data.data;
  }

  // Bean 목록 조회 (페이징)
  async getAllBeans(query: BeanSearchQuery = {}): Promise<PageResponse<BeanResponse>> {
    const { page = 0, size = 10, sort } = query;
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    
    if (sort) {
      params.append('sort', sort);
    }

    const response = await api.get<BaseResponse<PageResponse<BeanResponse>>>(
      `${this.BASE_URL}?${params.toString()}`
    );
    return response.data.data;
  }

  // Bean 상세 조회
  async getBean(beanId: number): Promise<BeanResponse> {
    const response = await api.get<BaseResponse<BeanResponse>>(`${this.BASE_URL}/${beanId}`);
    return response.data.data;
  }

  // Bean 수정
  async updateBean(beanId: number, command: UpdateBeanCommand): Promise<BeanResponse> {
    const response = await api.put<BaseResponse<BeanResponse>>(`${this.BASE_URL}/${beanId}`, command);
    return response.data.data;
  }

  // Bean 삭제
  async deleteBean(beanId: number): Promise<void> {
    await api.delete(`${this.BASE_URL}/${beanId}`);
  }
}

export const beanService = new BeanService();
