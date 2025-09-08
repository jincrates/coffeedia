import React, { useState, useEffect } from 'react';
import { EquipmentResponse, EquipmentSearchQuery, PageResponse } from '@/types/api';
import { equipmentService } from '@/services/equipmentService';
import EquipmentCard from './EquipmentCard';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import Button from '@/components/common/Button';
import { Plus, Settings } from 'lucide-react';
import toast from 'react-hot-toast';

interface EquipmentListProps {
  onAddEquipment: () => void;
  onEditEquipment: (equipment: EquipmentResponse) => void;
  onViewEquipment: (equipmentId: number) => void;
  onDeleteEquipment: (equipment: EquipmentResponse) => void;
  refreshTrigger?: number;
}

const EquipmentList: React.FC<EquipmentListProps> = ({
  onAddEquipment,
  onEditEquipment,
  onViewEquipment,
  onDeleteEquipment,
  refreshTrigger = 0,
}) => {
  const [equipments, setEquipments] = useState<EquipmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const pageSize = 12;

  const fetchEquipments = async (page = 0, reset = false) => {
    try {
      setLoading(true);
      const query: EquipmentSearchQuery = {
        page,
        size: pageSize,
        sort: 'createdAt,desc',
      };

      const response: PageResponse<EquipmentResponse> = await equipmentService.getAllEquipments(query);
      
      if (reset || page === 0) {
        setEquipments(response.content);
      } else {
        setEquipments(prev => [...prev, ...response.content]);
      }
      
      setCurrentPage(response.page);
      setTotalPages(Math.ceil(response.content.length / pageSize));
    } catch (error) {
      console.error('장비 목록 조회 실패:', error);
      toast.error('장비 목록을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEquipments(0, true);
  }, [refreshTrigger]);

  const handleLoadMore = () => {
    if (currentPage + 1 < totalPages) {
      fetchEquipments(currentPage + 1);
    }
  };

  const handleDeleteClick = (equipment: EquipmentResponse) => {
    onDeleteEquipment(equipment);
  };

  const filteredEquipments = equipments.filter(equipment =>
    equipment.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    equipment.brand.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (loading && equipments.length === 0) {
    return (
      <div className="flex justify-center items-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 장비 그리드 */}
      {filteredEquipments.length === 0 ? (
        <div className="text-center py-12">
          <Settings className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">
            {searchQuery ? '검색 결과가 없습니다' : '등록된 장비가 없습니다'}
          </h3>
          <p className="text-gray-500 mb-6">
            {searchQuery ? '다른 키워드로 검색해보세요' : '첫 번째 커피 장비를 등록해보세요'}
          </p>
          {!searchQuery && (
            <Button onClick={onAddEquipment} leftIcon={<Plus className="h-4 w-4" />}>
              장비 추가
            </Button>
          )}
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {filteredEquipments.map((equipment) => (
              <EquipmentCard
                key={equipment.id}
                equipment={equipment}
                onEdit={onEditEquipment}
                onDelete={handleDeleteClick}
                onView={onViewEquipment}
                actionsPosition="dropdown"
                loading={loading}
              />
            ))}
          </div>

          {/* 더 보기 버튼 */}
          {!searchQuery && currentPage + 1 < totalPages && (
            <div className="text-center">
              <Button
                variant="outline"
                onClick={handleLoadMore}
                loading={loading}
              >
                더 보기
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default EquipmentList;
