import api from './api';
import {
  BaseResponse,
  PageResponse,
  EquipmentResponse,
  CreateEquipmentCommand,
  EquipmentSearchQuery,
} from '@/types/api';

class EquipmentService {
  private readonly BASE_URL = '/equipments';

  // Equipment 생성
  async createEquipment(command: CreateEquipmentCommand): Promise<EquipmentResponse> {
    const response = await api.post<BaseResponse<EquipmentResponse>>(this.BASE_URL, command);
    return response.data.data;
  }

  // Equipment 목록 조회 (페이징)
  async getAllEquipments(query: EquipmentSearchQuery = {}): Promise<PageResponse<EquipmentResponse>> {
    const { page = 0, size = 10, sort } = query;
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    
    if (sort) {
      params.append('sort', sort);
    }

    const response = await api.get<BaseResponse<PageResponse<EquipmentResponse>>>(
      `${this.BASE_URL}?${params.toString()}`
    );
    return response.data.data;
  }

  // Equipment 상세 조회 (향후 구현 예정)
  async getEquipment(equipmentId: number): Promise<EquipmentResponse> {
    const response = await api.get<BaseResponse<EquipmentResponse>>(`${this.BASE_URL}/${equipmentId}`);
    return response.data.data;
  }
}

export const equipmentService = new EquipmentService();
