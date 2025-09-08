import api from './api';
import {
  BaseResponse,
  PageResponse,
  EquipmentResponse,
  CreateEquipmentCommand,
  UpdateEquipmentCommand,
  DeleteEquipmentResponse,
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

  // Equipment 상세 조회
  async getEquipment(equipmentId: number): Promise<EquipmentResponse> {
    const response = await api.get<BaseResponse<EquipmentResponse>>(`${this.BASE_URL}/${equipmentId}`);
    return response.data.data;
  }

  // Equipment 수정
  async updateEquipment(equipmentId: number, command: UpdateEquipmentCommand): Promise<EquipmentResponse> {
    const response = await api.put<BaseResponse<EquipmentResponse>>(`${this.BASE_URL}/${equipmentId}`, command);
    return response.data.data;
  }

  // Equipment 삭제
  async deleteEquipment(equipmentId: number): Promise<DeleteEquipmentResponse> {
    const response = await api.delete<BaseResponse<DeleteEquipmentResponse>>(`${this.BASE_URL}/${equipmentId}`);
    return response.data.data;
  }
}

export const equipmentService = new EquipmentService();
