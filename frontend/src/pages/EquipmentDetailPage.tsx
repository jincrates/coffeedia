import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { Settings, Calendar, ExternalLink } from 'lucide-react';
import toast from 'react-hot-toast';
import { equipmentService } from '@/services/equipmentService';
import EntityDetailLayout from '@/components/common/EntityDetailLayout';
import Card, { CardHeader, CardTitle, CardContent } from '@/components/common/Card';
import Button from '@/components/common/Button';
import { getEquipmentTypeKorean, getActiveStatusKorean, formatDate } from '@/utils/format';

const EquipmentDetailPage: React.FC = () => {
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

  // 장비 삭제 뮤테이션
  const deleteEquipmentMutation = useMutation(
    (equipmentId: number) => equipmentService.deleteEquipment(equipmentId),
    {
      onSuccess: () => {
        toast.success('장비가 삭제되었습니다.');
        navigate('/equipments');
        queryClient.invalidateQueries(['equipments']);
      },
      onError: (error: any) => {
        toast.error(error.response?.data?.message || '장비 삭제에 실패했습니다.');
      },
    }
  );

  const handleBack = () => {
    navigate('/equipments');
  };

  const handleEdit = () => {
    navigate(`/equipments/${equipmentId}/edit`);
  };

  const handleDelete = () => {
    if (equipment) {
      deleteEquipmentMutation.mutate(equipment.id);
    }
  };

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: `${equipment?.name} - CoffeeMedia`,
        text: `${equipment?.brand}의 ${equipment?.name} 장비 정보를 확인해보세요.`,
        url: window.location.href,
      });
    } else {
      navigator.clipboard.writeText(window.location.href);
      toast.success('링크가 클립보드에 복사되었습니다.');
    }
  };

  const handleFavorite = () => {
    toast.success('즐겨찾기 기능은 곧 구현됩니다!');
  };

  const sidebarContent = equipment ? (
    <div className="space-y-6">
      {/* 장비 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Settings className="h-5 w-5 mr-2" />
            장비 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="bg-gray-50 rounded-lg p-4">
            <span className="text-sm text-gray-500 block mb-1">타입</span>
            <p className="font-semibold text-gray-900 text-lg">
              {getEquipmentTypeKorean(equipment.type)}
            </p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <span className="text-sm text-gray-500 block mb-1">브랜드</span>
            <p className="font-semibold text-gray-900 text-lg">{equipment.brand}</p>
          </div>

          {equipment.buyDate && (
            <div className="bg-gray-50 rounded-lg p-4">
              <div className="flex items-center mb-2">
                <Calendar className="h-4 w-4 text-gray-600 mr-2" />
                <span className="text-sm text-gray-500">구매일</span>
              </div>
              <p className="font-medium text-gray-900">
                {formatDate(equipment.buyDate, 'yyyy.MM.dd')}
              </p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 구매 링크 */}
      {equipment.buyUrl && (
        <Card>
          <CardHeader>
            <CardTitle>구매 정보</CardTitle>
          </CardHeader>
          <CardContent>
            <Button
              variant="outline"
              className="w-full justify-start"
              onClick={() => window.open(equipment.buyUrl, '_blank')}
              rightIcon={<ExternalLink className="h-4 w-4" />}
            >
              구매 링크 보기
            </Button>
          </CardContent>
        </Card>
      )}

      {/* 퀵 액션 */}
      <Card>
        <CardHeader>
          <CardTitle>퀵 액션</CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          <Button
            variant="outline"
            className="w-full justify-start"
            onClick={() => toast.success('사용 기록 기능은 곧 구현됩니다!')}
          >
            <Settings className="h-4 w-4 mr-2" />
            사용 기록 추가
          </Button>
          <Button
            variant="outline"
            className="w-full justify-start"
            onClick={() => toast.success('유지보수 기능은 곧 구현됩니다!')}
          >
            <Calendar className="h-4 w-4 mr-2" />
            유지보수 일정
          </Button>
        </CardContent>
      </Card>
    </div>
  ) : null;

  return (
    <EntityDetailLayout
      title={equipment?.name || ''}
      subtitle={equipment ? `${equipment.brand} • ${getEquipmentTypeKorean(equipment.type)}` : ''}
      status={equipment ? {
        label: getActiveStatusKorean(equipment.status),
        variant: equipment.status === 'ACTIVE' ? 'success' : 'inactive'
      } : undefined}
      fallbackIcon={<Settings className="h-16 w-16 text-gray-300" />}
      onBack={handleBack}
      onEdit={handleEdit}
      onDelete={handleDelete}
      onShare={handleShare}
      onFavorite={handleFavorite}
      deleteConfirmTitle="장비 삭제 확인"
      deleteConfirmMessage="정말로 이 장비를 삭제하시겠습니까? 삭제된 장비는 복구할 수 없습니다."
      isDeleting={deleteEquipmentMutation.isLoading}
      isLoading={isLoading}
      error={error}
      sidebarContent={sidebarContent}
    >
      {equipment && (
        <div className="space-y-6">
          {/* 설명 */}
          {equipment.description && (
            <Card>
              <CardHeader>
                <CardTitle>장비 설명</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                  {equipment.description}
                </p>
              </CardContent>
            </Card>
          )}

          {/* 관리 정보 */}
          <Card>
            <CardHeader>
              <CardTitle>관리 정보</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="bg-gray-50 rounded-lg p-4">
                  <span className="text-sm text-gray-500 block mb-1">등록일</span>
                  <p className="font-medium text-gray-900">
                    {formatDate(equipment.createdAt, 'yyyy.MM.dd HH:mm')}
                  </p>
                </div>
                <div className="bg-gray-50 rounded-lg p-4">
                  <span className="text-sm text-gray-500 block mb-1">수정일</span>
                  <p className="font-medium text-gray-900">
                    {formatDate(equipment.updatedAt, 'yyyy.MM.dd HH:mm')}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </EntityDetailLayout>
  );
};

export default EquipmentDetailPage;
