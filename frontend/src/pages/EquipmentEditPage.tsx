import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import toast from 'react-hot-toast';
import { equipmentService } from '@/services/equipmentService';
import { UpdateEquipmentCommand } from '@/types/api';
import EquipmentForm from '@/components/equipments/EquipmentForm';
import FormLayout from '@/components/common/FormLayout';

const EquipmentEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const equipmentId = id ? parseInt(id, 10) : null;

  const {
    data: equipment,
    isLoading,
    error,
  } = useQuery(
    ['equipment', equipmentId],
    () => equipmentId ? equipmentService.getEquipment(equipmentId) : Promise.reject('Invalid equipment ID'),
    {
      enabled: !!equipmentId,
      retry: 1,
    }
  );

  const updateMutation = useMutation(
    (data: UpdateEquipmentCommand) => equipmentService.updateEquipment(Number(id), data),
    {
      onSuccess: (updatedEquipment) => {
        toast.success('장비가 성공적으로 수정되었습니다!');
        queryClient.invalidateQueries(['equipment', id]);
        queryClient.invalidateQueries(['equipments']);
        navigate(`/equipments/${updatedEquipment.id}`);
      },
      onError: (error: any) => {
        console.error('장비 수정 실패:', error);
        toast.error(error.response?.data?.message || '장비 수정에 실패했습니다.');
      },
    }
  );

  const handleSubmit = (data: UpdateEquipmentCommand) => {
    updateMutation.mutate(data);
  };

  const handleCancel = () => {
    navigate(`/equipments/${id}`);
  };

  const handleBack = () => {
    navigate('/equipments');
  };

  if (error || !equipment) {
    return (
      <FormLayout
        title="장비를 찾을 수 없습니다"
        subtitle="요청하신 장비가 존재하지 않거나 삭제되었습니다."
        onBack={handleBack}
        onCancel={handleBack}
        onSubmit={() => {}}
        submitLabel="목록으로"
      >
        <div className="text-center py-12">
          <p className="text-gray-600">
            다른 장비를 찾아보세요.
          </p>
        </div>
      </FormLayout>
    );
  }

  if (isLoading) {
    return (
      <FormLayout
        title="장비 수정"
        subtitle="장비 정보를 불러오는 중..."
        onBack={handleBack}
        onCancel={handleCancel}
        onSubmit={() => {}}
        submitLabel="저장"
      >
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-coffee-600 mx-auto mb-4"></div>
            <p className="text-gray-600">장비를 불러오는 중...</p>
          </div>
        </div>
      </FormLayout>
    );
  }

  return (
    <FormLayout
      title="장비 수정"
      subtitle={`${equipment.name}의 정보를 수정해보세요`}
      onBack={handleBack}
      onCancel={handleCancel}
      onSubmit={() => {}} // 폼 내부에서 처리
      submitLabel="장비 수정"
      isLoading={updateMutation.isLoading}
    >
      <EquipmentForm
        initialData={equipment}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        loading={updateMutation.isLoading}
        mode="edit"
        showButtons={false}
      />
    </FormLayout>
  );
};

export default EquipmentEditPage;
