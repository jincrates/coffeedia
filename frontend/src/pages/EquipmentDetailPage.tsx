import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import EquipmentDetail from '@/components/equipments/EquipmentDetail';
import { EquipmentResponse } from '@/types/api';

const EquipmentDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const equipmentId = id ? parseInt(id, 10) : 0;

  const handleBack = () => {
    navigate('/equipments');
  };

  const handleEdit = (equipment: EquipmentResponse) => {
    navigate(`/equipments/${equipment.id}/edit`, { state: { equipment } });
  };

  const handleDelete = () => {
    navigate('/equipments');
  };

  if (!equipmentId) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-semibold text-gray-900">잘못된 접근입니다</h2>
          <button
            onClick={handleBack}
            className="mt-4 text-coffee-600 hover:text-coffee-700"
          >
            목록으로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <EquipmentDetail
          equipmentId={equipmentId}
          onBack={handleBack}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      </div>
    </div>
  );
};

export default EquipmentDetailPage;
