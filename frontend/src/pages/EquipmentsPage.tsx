import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { EquipmentResponse, CreateEquipmentCommand } from '@/types/api';
import { equipmentService } from '@/services/equipmentService';
import EquipmentList from '@/components/equipments/EquipmentList';
import EquipmentForm from '@/components/equipments/EquipmentForm';
import PageLayout from '@/components/common/PageLayout';
import FormLayout from '@/components/common/FormLayout';
import DeleteConfirmModal from '@/components/common/DeleteConfirmModal';
import Button from '@/components/common/Button';
import toast from 'react-hot-toast';

const EquipmentsPage: React.FC = () => {
  const navigate = useNavigate();
  const [showForm, setShowForm] = useState(false);
  const [deleteEquipment, setDeleteEquipment] = useState<EquipmentResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleAddEquipment = () => {
    setShowForm(true);
  };

  const handleEditEquipment = (equipment: EquipmentResponse) => {
    navigate(`/equipments/${equipment.id}/edit`);
  };

  const handleViewEquipment = (equipmentId: number) => {
    navigate(`/equipments/${equipmentId}`);
  };

  const handleFormCancel = () => {
    setShowForm(false);
  };

  const handleCreateSubmit = async (data: CreateEquipmentCommand) => {
    try {
      setLoading(true);
      await equipmentService.createEquipment(data);
      toast.success('장비가 성공적으로 등록되었습니다.');
      setShowForm(false);
      setRefreshTrigger(prev => prev + 1);
    } catch (error) {
      console.error('장비 등록 실패:', error);
      toast.error('장비 등록에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteEquipment = (equipment: EquipmentResponse) => {
    setDeleteEquipment(equipment);
  };

  const confirmDelete = async () => {
    if (!deleteEquipment) return;
    
    try {
      await equipmentService.deleteEquipment(deleteEquipment.id);
      toast.success('장비가 삭제되었습니다.');
      setRefreshTrigger(prev => prev + 1);
      setDeleteEquipment(null);
    } catch (error) {
      toast.error('장비 삭제에 실패했습니다.');
    }
  };

  const cancelDelete = () => {
    setDeleteEquipment(null);
  };

  const handleSearch = (query: string) => {
    // TODO: 검색 기능 구현
    toast.success('검색 기능은 곧 구현됩니다!');
  };

  // 폼 표시 중일 때
  if (showForm) {
    return (
      <FormLayout
        title="새 장비 등록"
        subtitle="새로운 커피 장비를 등록하세요"
        onBack={handleFormCancel}
        onCancel={handleFormCancel}
        onSubmit={() => {}} // 폼 내부에서 처리
        submitLabel="장비 등록"
        isLoading={loading}
      >
        <EquipmentForm
          onSubmit={handleCreateSubmit}
          onCancel={handleFormCancel}
          loading={loading}
          mode="create"
          showButtons={false}
        />
      </FormLayout>
    );
  }

  return (
    <>
      <PageLayout
        title="장비 관리"
        subtitle="커피 장비를 관리하고 추적하세요"
        searchPlaceholder="장비명 또는 브랜드로 검색..."
        onAddNew={handleAddEquipment}
        onSearch={handleSearch}
        addButtonLabel="장비 추가"
      >
        <EquipmentList
          onAddEquipment={handleAddEquipment}
          onEditEquipment={handleEditEquipment}
          onViewEquipment={handleViewEquipment}
          onDeleteEquipment={handleDeleteEquipment}
          refreshTrigger={refreshTrigger}
        />
      </PageLayout>

      {/* 삭제 확인 모달 */}
      <DeleteConfirmModal
        isOpen={!!deleteEquipment}
        title="장비 삭제 확인"
        message={`정말로 "${deleteEquipment?.name}"을 삭제하시겠습니까? 삭제된 장비는 복구할 수 없습니다.`}
        onConfirm={confirmDelete}
        onCancel={cancelDelete}
        loading={false}
      />
    </>
  );
};

export default EquipmentsPage;
