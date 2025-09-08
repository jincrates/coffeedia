import React, { useState } from 'react';
import { EquipmentResponse, CreateEquipmentCommand, UpdateEquipmentCommand } from '@/types/api';
import { equipmentService } from '@/services/equipmentService';
import EquipmentList from '@/components/equipments/EquipmentList';
import EquipmentForm from '@/components/equipments/EquipmentForm';
import EquipmentDetail from '@/components/equipments/EquipmentDetail';
import MobileHeader from '@/components/common/MobileHeader';
import toast from 'react-hot-toast';

type ViewMode = 'list' | 'create' | 'edit' | 'detail';

const EquipmentsPage: React.FC = () => {
  const [viewMode, setViewMode] = useState<ViewMode>('list');
  const [selectedEquipment, setSelectedEquipment] = useState<EquipmentResponse | null>(null);
  const [selectedEquipmentId, setSelectedEquipmentId] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleAddEquipment = () => {
    setSelectedEquipment(null);
    setViewMode('create');
  };

  const handleEditEquipment = (equipment: EquipmentResponse) => {
    setSelectedEquipment(equipment);
    setViewMode('edit');
  };

  const handleViewEquipment = (equipmentId: number) => {
    setSelectedEquipmentId(equipmentId);
    setViewMode('detail');
  };

  const handleBackToList = () => {
    setViewMode('list');
    setSelectedEquipment(null);
    setSelectedEquipmentId(null);
    setRefreshTrigger(prev => prev + 1);
  };

  const handleCreateSubmit = async (data: CreateEquipmentCommand) => {
    try {
      setLoading(true);
      await equipmentService.createEquipment(data);
      toast.success('장비가 성공적으로 등록되었습니다.');
      handleBackToList();
    } catch (error) {
      console.error('장비 등록 실패:', error);
      toast.error('장비 등록에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateSubmit = async (data: UpdateEquipmentCommand) => {
    if (!selectedEquipment) return;

    try {
      setLoading(true);
      await equipmentService.updateEquipment(selectedEquipment.id, data);
      toast.success('장비 정보가 성공적으로 수정되었습니다.');
      handleBackToList();
    } catch (error) {
      console.error('장비 수정 실패:', error);
      toast.error('장비 수정에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteFromDetail = () => {
    handleBackToList();
  };

  const getPageTitle = () => {
    switch (viewMode) {
      case 'create':
        return '장비 추가';
      case 'edit':
        return '장비 수정';
      case 'detail':
        return '장비 상세';
      default:
        return '장비 관리';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 모바일 헤더 */}
      {viewMode !== 'list' && (
        <MobileHeader
          title={getPageTitle()}
          onBack={handleBackToList}
        />
      )}

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {viewMode === 'list' && (
          <EquipmentList
            onAddEquipment={handleAddEquipment}
            onEditEquipment={handleEditEquipment}
            onViewEquipment={handleViewEquipment}
            refreshTrigger={refreshTrigger}
          />
        )}

        {viewMode === 'create' && (
          <div className="max-w-2xl mx-auto">
            <div className="mb-6">
              <h1 className="text-2xl font-bold text-gray-900">장비 추가</h1>
              <p className="text-gray-600 mt-1">새로운 커피 장비를 등록하세요</p>
            </div>
            <EquipmentForm
              onSubmit={handleCreateSubmit}
              onCancel={handleBackToList}
              loading={loading}
              mode="create"
            />
          </div>
        )}

        {viewMode === 'edit' && selectedEquipment && (
          <div className="max-w-2xl mx-auto">
            <div className="mb-6">
              <h1 className="text-2xl font-bold text-gray-900">장비 수정</h1>
              <p className="text-gray-600 mt-1">{selectedEquipment.name} 정보를 수정하세요</p>
            </div>
            <EquipmentForm
              initialData={selectedEquipment}
              onSubmit={handleUpdateSubmit}
              onCancel={handleBackToList}
              loading={loading}
              mode="edit"
            />
          </div>
        )}

        {viewMode === 'detail' && selectedEquipmentId && (
          <EquipmentDetail
            equipmentId={selectedEquipmentId}
            onBack={handleBackToList}
            onEdit={handleEditEquipment}
            onDelete={handleDeleteFromDetail}
          />
        )}
      </div>
    </div>
  );
};

export default EquipmentsPage;
