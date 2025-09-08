import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { EquipmentResponse, UpdateEquipmentCommand } from '@/types/api';
import { equipmentService } from '@/services/equipmentService';
import EquipmentForm from '@/components/equipments/EquipmentForm';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import MobileHeader from '@/components/common/MobileHeader';
import toast from 'react-hot-toast';

const EquipmentEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  
  const [equipment, setEquipment] = useState<EquipmentResponse | null>(
    location.state?.equipment || null
  );
  const [loading, setLoading] = useState(!equipment);
  const [saving, setSaving] = useState(false);

  const equipmentId = id ? parseInt(id, 10) : 0;

  useEffect(() => {
    // 상태로 전달된 장비 정보가 없으면 API에서 조회
    if (!equipment && equipmentId) {
      const fetchEquipment = async () => {
        try {
          setLoading(true);
          const data = await equipmentService.getEquipment(equipmentId);
          setEquipment(data);
        } catch (error) {
          console.error('장비 정보 조회 실패:', error);
          toast.error('장비 정보를 불러오는데 실패했습니다.');
          navigate('/equipments');
        } finally {
          setLoading(false);
        }
      };

      fetchEquipment();
    }
  }, [equipment, equipmentId, navigate]);

  const handleBack = () => {
    navigate(`/equipments/${equipmentId}`);
  };

  const handleSubmit = async (data: UpdateEquipmentCommand) => {
    if (!equipment) return;

    try {
      setSaving(true);
      await equipmentService.updateEquipment(equipment.id, data);
      toast.success('장비 정보가 성공적으로 수정되었습니다.');
      navigate(`/equipments/${equipment.id}`);
    } catch (error) {
      console.error('장비 수정 실패:', error);
      toast.error('장비 수정에 실패했습니다.');
    } finally {
      setSaving(false);
    }
  };

  if (!equipmentId) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-semibold text-gray-900">잘못된 접근입니다</h2>
          <button
            onClick={() => navigate('/equipments')}
            className="mt-4 text-coffee-600 hover:text-coffee-700"
          >
            목록으로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <MobileHeader title="장비 수정" onBack={handleBack} />
        <div className="flex justify-center items-center h-64">
          <LoadingSpinner size="lg" />
        </div>
      </div>
    );
  }

  if (!equipment) {
    return (
      <div className="min-h-screen bg-gray-50">
        <MobileHeader title="장비 수정" onBack={handleBack} />
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <h2 className="text-xl font-semibold text-gray-900">장비를 찾을 수 없습니다</h2>
            <button
              onClick={handleBack}
              className="mt-4 text-coffee-600 hover:text-coffee-700"
            >
              뒤로 가기
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <MobileHeader title="장비 수정" onBack={handleBack} />
      
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">장비 수정</h1>
          <p className="text-gray-600 mt-1">{equipment.name} 정보를 수정하세요</p>
        </div>
        
        <EquipmentForm
          initialData={equipment}
          onSubmit={handleSubmit}
          onCancel={handleBack}
          loading={saving}
          mode="edit"
        />
      </div>
    </div>
  );
};

export default EquipmentEditPage;
