import React from 'react';
import {EquipmentResponse} from '@/types/api';
import Card, {CardContent, CardFooter} from '@/components/common/Card';
import CardActions from '@/components/common/CardActions';
import useAuth from '@/hooks/useAuth';
import {Calendar, ExternalLink, Settings, Tag} from 'lucide-react';
import {formatRelativeDate, getActiveStatusKorean, getEquipmentTypeKorean} from '@/utils/format';

interface EquipmentCardProps {
  equipment: EquipmentResponse;
  loading?: boolean;
  onEdit: (equipment: EquipmentResponse) => void;
  onDelete: (equipment: EquipmentResponse) => void;
  onView: (equipmentId: number) => void;
  actionsPosition?: 'dropdown' | 'buttons';
}

const EquipmentCard: React.FC<EquipmentCardProps> = ({
                                                       equipment,
                                                       loading = false,
                                                       onEdit,
                                                       onDelete,
                                                       onView,
                                                       actionsPosition = 'dropdown',
                                                     }) => {
  const {canPerformAction} = useAuth();

  const handleView = () => {
    if (!loading) {
      onView(equipment.id);
    }
  };

  const handleEditClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit(equipment);
  };

  const handleDeleteClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onDelete(equipment);
  };

  const handleBuyUrlClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (equipment.buyUrl) {
      window.open(equipment.buyUrl, '_blank');
    }
  };

  const actions = [];

  actions.push({
    type: 'view' as const,
    label: '상세보기',
    onClick: () => onView(equipment.id),
    disabled: loading,
  });

  // 수정 버튼: 로그인한 사용자만 표시 (Equipment에 작성자 정보가 없어서 일단 로그인만 체크)
  if (onEdit && canPerformAction('edit')) {
    actions.push({
      type: 'edit' as const,
      label: '수정',
      onClick: () => onEdit(equipment),
      disabled: loading,
    });
  }

  // 삭제 버튼: 로그인한 사용자만 표시 (Equipment에 작성자 정보가 없어서 일단 로그인만 체크)
  if (onDelete && canPerformAction('delete')) {
    actions.push({
      type: 'delete' as const,
      label: '삭제',
      onClick: () => onDelete(equipment),
      variant: 'destructive' as const,
      disabled: loading,
    });
  }

  return (
      <Card
          hover={!loading}
          onClick={handleView}
          className="h-full cursor-pointer"
      >
        <CardContent className="space-y-3">
          {/* 아이콘 영역 */}
          <div
              className="aspect-video rounded-lg bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center">
            <Settings className="h-16 w-16 text-gray-300"/>
          </div>

          {/* 제목과 상태 */}
          <div>
            <div className="flex justify-between items-start mb-2">
              <h3 className="font-semibold text-lg text-gray-900 line-clamp-2 flex-1">
                {equipment.name}
              </h3>
              <div className="flex items-center gap-2 ml-2">
              <span
                  className={`inline-flex px-2 py-1 text-xs font-medium rounded-full flex-shrink-0 ${
                      equipment.status === 'ACTIVE'
                          ? 'bg-green-100 text-green-800'
                          : 'bg-gray-100 text-gray-800'
                  }`}>
                {getActiveStatusKorean(equipment.status)}
              </span>
                {/* 액션 메뉴 */}
                {actions.length > 0 && actionsPosition === 'dropdown' && (
                    <CardActions actions={actions} position="dropdown"/>
                )}
              </div>
            </div>

            <p className="text-sm text-gray-600 font-medium mb-1">{equipment.brand}</p>

            {/* 설명 */}
            {equipment.description && (
                <p className="text-sm text-gray-600 line-clamp-2">
                  {equipment.description}
                </p>
            )}
          </div>

          {/* 메타 정보 */}
          <div className="flex items-center text-sm text-gray-500 space-x-4">
            <div className="flex items-center">
              <Settings className="h-4 w-4 mr-1"/>
              <span>{getEquipmentTypeKorean(equipment.type)}</span>
            </div>
            {equipment.buyDate && (
                <div className="flex items-center">
                  <Calendar className="h-4 w-4 mr-1"/>
                  <span>{formatRelativeDate(equipment.buyDate)}</span>
                </div>
            )}
          </div>

          {/* 타입 태그 */}
          <div className="flex flex-wrap gap-1">
            <Tag className="h-3 w-3 text-gray-400 mt-1"/>
            <span className="inline-flex px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full">
            {getEquipmentTypeKorean(equipment.type)}
          </span>
            {equipment.buyUrl && (
                <span
                    className="inline-flex items-center px-2 py-1 text-xs bg-green-100 text-green-800 rounded-full cursor-pointer hover:bg-green-200 transition-colors"
                    onClick={handleBuyUrlClick}
                    title="구매 링크 열기"
                >
              <ExternalLink className="h-3 w-3 mr-1"/>
              구매링크
            </span>
            )}
          </div>

          {/* 생성일 */}
          <div className="text-xs text-gray-400">
            등록일: {formatRelativeDate(equipment.createdAt)}
          </div>
        </CardContent>

        {actions.length > 0 && actionsPosition === 'buttons' && (
            <CardFooter>
              <CardActions actions={actions} position="buttons"/>
            </CardFooter>
        )}
      </Card>
  );
};

export default EquipmentCard;
