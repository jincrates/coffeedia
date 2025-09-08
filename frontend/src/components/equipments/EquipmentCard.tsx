import React from 'react';
import { EquipmentResponse } from '@/types/api';
import { getEquipmentTypeKorean, getActiveStatusKorean, formatDate } from '@/utils/format';
import Card, { CardHeader, CardContent } from '@/components/common/Card';
import { Settings, Calendar, ExternalLink, Edit, Trash2 } from 'lucide-react';
import Button from '@/components/common/Button';

interface EquipmentCardProps {
  equipment: EquipmentResponse;
  onEdit: (equipment: EquipmentResponse) => void;
  onDelete: (equipmentId: number) => void;
  onView: (equipmentId: number) => void;
}

const EquipmentCard: React.FC<EquipmentCardProps> = ({
  equipment,
  onEdit,
  onDelete,
  onView,
}) => {
  const handleCardClick = () => {
    onView(equipment.id);
  };

  const handleEditClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit(equipment);
  };

  const handleDeleteClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onDelete(equipment.id);
  };

  const handleBuyUrlClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (equipment.buyUrl) {
      window.open(equipment.buyUrl, '_blank');
    }
  };

  return (
    <Card className="cursor-pointer hover:shadow-lg transition-shadow duration-200" onClick={handleCardClick}>
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex items-center space-x-2">
            <Settings className="h-5 w-5 text-coffee-600" />
            <span className="text-sm font-medium text-coffee-600 bg-coffee-50 px-2 py-1 rounded-full">
              {getEquipmentTypeKorean(equipment.type)}
            </span>
          </div>
          <div className="flex items-center space-x-1">
            {equipment.buyUrl && (
              <Button
                size="sm"
                variant="ghost"
                onClick={handleBuyUrlClick}
                className="h-8 w-8 p-0"
                title="구매 링크"
              >
                <ExternalLink className="h-4 w-4" />
              </Button>
            )}
            <Button
              size="sm"
              variant="ghost"
              onClick={handleEditClick}
              className="h-8 w-8 p-0"
              title="수정"
            >
              <Edit className="h-4 w-4" />
            </Button>
            <Button
              size="sm"
              variant="ghost"
              onClick={handleDeleteClick}
              className="h-8 w-8 p-0 text-red-600 hover:text-red-700 hover:bg-red-50"
              title="삭제"
            >
              <Trash2 className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardHeader>
      
      <CardContent className="space-y-3">
        <div>
          <h3 className="font-semibold text-lg text-gray-900 line-clamp-1">
            {equipment.name}
          </h3>
          <p className="text-sm text-gray-600 mt-1">
            {equipment.brand}
          </p>
        </div>

        {equipment.description && (
          <p className="text-sm text-gray-600 line-clamp-2">
            {equipment.description}
          </p>
        )}

        <div className="flex items-center justify-between text-xs text-gray-500">
          <div className="flex items-center space-x-1">
            <Calendar className="h-3 w-3" />
            <span>
              {equipment.buyDate ? formatDate(equipment.buyDate, 'yyyy.MM.dd') : '구매일 미등록'}
            </span>
          </div>
          <span className={`px-2 py-1 rounded-full text-xs font-medium ${
            equipment.status === 'ACTIVE' 
              ? 'bg-green-100 text-green-800' 
              : 'bg-gray-100 text-gray-800'
          }`}>
            {getActiveStatusKorean(equipment.status)}
          </span>
        </div>
      </CardContent>
    </Card>
  );
};

export default EquipmentCard;
