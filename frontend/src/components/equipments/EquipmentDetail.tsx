import React, { useState, useEffect } from 'react';
import { EquipmentResponse } from '@/types/api';
import { equipmentService } from '@/services/equipmentService';
import { getEquipmentTypeKorean, getActiveStatusKorean, formatDate, formatDateTime } from '@/utils/format';
import Card, { CardHeader, CardTitle, CardContent } from '@/components/common/Card';
import Button from '@/components/common/Button';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import DeleteConfirmModal from '@/components/common/DeleteConfirmModal';
import { 
  ArrowLeft, 
  Edit, 
  Trash2, 
  Calendar, 
  ExternalLink, 
  Settings,
  Clock
} from 'lucide-react';
import toast from 'react-hot-toast';

interface EquipmentDetailProps {
  equipmentId: number;
  onBack: () => void;
  onEdit: (equipment: EquipmentResponse) => void;
  onDelete: () => void;
}

const EquipmentDetail: React.FC<EquipmentDetailProps> = ({
  equipmentId,
  onBack,
  onEdit,
  onDelete,
}) => {
  const [equipment, setEquipment] = useState<EquipmentResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    const fetchEquipment = async () => {
      try {
        setLoading(true);
        const data = await equipmentService.getEquipment(equipmentId);
        setEquipment(data);
      } catch (error) {
        console.error('장비 상세 조회 실패:', error);
        toast.error('장비 정보를 불러오는데 실패했습니다.');
        onBack();
      } finally {
        setLoading(false);
      }
    };

    fetchEquipment();
  }, [equipmentId, onBack]);

  const handleEdit = () => {
    if (equipment) {
      onEdit(equipment);
    }
  };

  const handleDeleteClick = () => {
    setDeleteModalOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!equipment) return;

    try {
      setDeleting(true);
      await equipmentService.deleteEquipment(equipment.id);
      toast.success('장비가 삭제되었습니다.');
      onDelete();
    } catch (error) {
      console.error('장비 삭제 실패:', error);
      toast.error('장비 삭제에 실패했습니다.');
    } finally {
      setDeleting(false);
      setDeleteModalOpen(false);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteModalOpen(false);
  };

  const handleBuyUrlClick = () => {
    if (equipment?.buyUrl) {
      window.open(equipment.buyUrl, '_blank');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (!equipment) {
    return (
      <div className="text-center py-12">
        <Settings className="h-12 w-12 text-gray-400 mx-auto mb-4" />
        <h3 className="text-lg font-medium text-gray-900 mb-2">
          장비를 찾을 수 없습니다
        </h3>
        <Button onClick={onBack} leftIcon={<ArrowLeft className="h-4 w-4" />}>
          목록으로 돌아가기
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Button
            variant="ghost"
            onClick={onBack}
            leftIcon={<ArrowLeft className="h-4 w-4" />}
          >
            목록으로
          </Button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{equipment.name}</h1>
            <p className="text-gray-600">{equipment.brand}</p>
          </div>
        </div>
        <div className="flex items-center space-x-2">
          {equipment.buyUrl && (
            <Button
              variant="outline"
              onClick={handleBuyUrlClick}
              leftIcon={<ExternalLink className="h-4 w-4" />}
            >
              구매 링크
            </Button>
          )}
          <Button
            variant="outline"
            onClick={handleEdit}
            leftIcon={<Edit className="h-4 w-4" />}
          >
            수정
          </Button>
          <Button
            variant="destructive"
            onClick={handleDeleteClick}
            leftIcon={<Trash2 className="h-4 w-4" />}
          >
            삭제
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 메인 정보 */}
        <div className="lg:col-span-2 space-y-6">
          {/* 기본 정보 */}
          <Card>
            <CardHeader>
              <CardTitle>기본 정보</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    장비 타입
                  </label>
                  <div className="flex items-center space-x-2">
                    <Settings className="h-4 w-4 text-coffee-600" />
                    <span className="text-sm font-medium text-coffee-600 bg-coffee-50 px-2 py-1 rounded-full">
                      {getEquipmentTypeKorean(equipment.type)}
                    </span>
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    상태
                  </label>
                  <span className={`inline-flex px-2 py-1 rounded-full text-xs font-medium ${
                    equipment.status === 'ACTIVE' 
                      ? 'bg-green-100 text-green-800' 
                      : 'bg-gray-100 text-gray-800'
                  }`}>
                    {getActiveStatusKorean(equipment.status)}
                  </span>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  장비명
                </label>
                <p className="text-gray-900 font-medium">{equipment.name}</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  브랜드
                </label>
                <p className="text-gray-900">{equipment.brand}</p>
              </div>

              {equipment.description && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    설명
                  </label>
                  <p className="text-gray-900 whitespace-pre-wrap">{equipment.description}</p>
                </div>
              )}
            </CardContent>
          </Card>

          {/* 구매 정보 */}
          <Card>
            <CardHeader>
              <CardTitle>구매 정보</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    구매일
                  </label>
                  <div className="flex items-center space-x-2 text-gray-900">
                    <Calendar className="h-4 w-4 text-gray-500" />
                    <span>
                      {equipment.buyDate 
                        ? formatDate(equipment.buyDate, 'yyyy년 MM월 dd일') 
                        : '미등록'
                      }
                    </span>
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    구매 링크
                  </label>
                  {equipment.buyUrl ? (
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={handleBuyUrlClick}
                      className="p-0 h-auto text-coffee-600 hover:text-coffee-700"
                    >
                      <ExternalLink className="h-4 w-4 mr-1" />
                      구매 페이지 보기
                    </Button>
                  ) : (
                    <span className="text-gray-500">미등록</span>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* 사이드바 */}
        <div className="space-y-6">
          {/* 등록 정보 */}
          <Card>
            <CardHeader>
              <CardTitle>등록 정보</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  등록일
                </label>
                <div className="flex items-center space-x-2 text-sm text-gray-600">
                  <Clock className="h-4 w-4" />
                  <span>{formatDateTime(equipment.createdAt)}</span>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  수정일
                </label>
                <div className="flex items-center space-x-2 text-sm text-gray-600">
                  <Clock className="h-4 w-4" />
                  <span>{formatDateTime(equipment.updatedAt)}</span>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* 빠른 액션 */}
          <Card>
            <CardHeader>
              <CardTitle>빠른 액션</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <Button
                variant="outline"
                onClick={handleEdit}
                leftIcon={<Edit className="h-4 w-4" />}
                className="w-full justify-start"
              >
                장비 정보 수정
              </Button>
              {equipment.buyUrl && (
                <Button
                  variant="outline"
                  onClick={handleBuyUrlClick}
                  leftIcon={<ExternalLink className="h-4 w-4" />}
                  className="w-full justify-start"
                >
                  구매 페이지 방문
                </Button>
              )}
              <Button
                variant="destructive"
                onClick={handleDeleteClick}
                leftIcon={<Trash2 className="h-4 w-4" />}
                className="w-full justify-start"
              >
                장비 삭제
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* 삭제 확인 모달 */}
      <DeleteConfirmModal
        isOpen={deleteModalOpen}
        onConfirm={handleDeleteConfirm}
        onCancel={handleDeleteCancel}
        loading={deleting}
        title="장비 삭제"
        message={`정말로 "${equipment.name}"을(를) 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.`}
      />
    </div>
  );
};

export default EquipmentDetail;
