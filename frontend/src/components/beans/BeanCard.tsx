import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BeanResponse } from '@/types/api';
import Card, { CardContent, CardFooter } from '@/components/common/Card';
import CardActions from '@/components/common/CardActions';
import Button from '@/components/common/Button';
import { MapPin, Coffee } from 'lucide-react';
import { formatDate, formatRelativeDate, getRoastLevelKorean, getProcessTypeKorean, getBlendTypeKorean } from '@/utils/format';

interface BeanCardProps {
  bean: BeanResponse;
  onEdit?: (bean: BeanResponse) => void;
  onDelete?: (beanId: number) => void;
  onView?: (bean: BeanResponse) => void;
  actionsPosition?: 'dropdown' | 'buttons';
}

const BeanCard: React.FC<BeanCardProps> = ({
  bean,
  onEdit,
  onDelete,
  onView,
  actionsPosition = 'buttons',
}) => {
  const navigate = useNavigate();
  const [imageError, setImageError] = useState(false);

  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit?.(bean);
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    onDelete?.(bean.beanId);
  };

  const handleView = () => {
    if (onView) {
      onView(bean);
    } else {
      navigate(`/beans/${bean.beanId}`);
    }
  };

  const handleCardClick = () => {
    navigate(`/beans/${bean.beanId}`);
  };

  const handleImageError = () => {
    setImageError(true);
  };

  const actions = [];
  
  if (onView) {
    actions.push({
      type: 'view' as const,
      label: '상세보기',
      onClick: () => onView(bean),
    });
  }
  
  if (onEdit) {
    actions.push({
      type: 'edit' as const,
      label: '수정',
      onClick: () => onEdit(bean),
    });
  }
  
  if (onDelete) {
    actions.push({
      type: 'delete' as const,
      label: '삭제',
      onClick: () => onDelete(bean.beanId),
      variant: 'destructive' as const,
    });
  }

  return (
    <Card 
      hover={true} 
      onClick={handleCardClick}
      className="transition-all duration-200 cursor-pointer overflow-hidden"
    >
        {/* 이미지가 있는 경우만 표시 */}
        {bean.thumbnailUrl && (
          <div className="relative h-48 bg-gray-100 overflow-hidden">
            {!imageError ? (
              <img
                src={bean.thumbnailUrl}
                alt={bean.name}
                className="w-full h-full object-cover transition-transform duration-300 hover:scale-105"
                onError={handleImageError}
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-coffee-100 to-coffee-200">
                <Coffee className="h-16 w-16 text-coffee-400" />
              </div>
            )}
            
            {/* 상태 배지 */}
            <div className="absolute top-3 right-3">
              <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full backdrop-blur-sm ${
                bean.status === 'ACTIVE' 
                  ? 'bg-green-100/80 text-green-800' 
                  : 'bg-gray-100/80 text-gray-800'
              }`}>
                {bean.status === 'ACTIVE' ? '활성' : '비활성'}
              </span>
            </div>
            
            {/* 디카페인 배지 */}
            {bean.isDecaf && (
              <div className="absolute top-3 left-3">
                <span className="inline-flex px-2 py-1 text-xs font-medium bg-orange-100/80 text-orange-800 rounded-full backdrop-blur-sm">
                  디카페인
                </span>
              </div>
            )}
          </div>
        )}
      <CardContent className={`space-y-3 ${bean.thumbnailUrl ? 'p-4' : 'p-6'}`}>
        {/* Header */}
        <div className="flex justify-between items-start">
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-1">
              <h3 className="font-semibold text-lg text-gray-900 truncate">
                {bean.name}
              </h3>
              {/* 이미지가 없을 때 상태 배지 */}
              {!bean.thumbnailUrl && (
                <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full ${
                  bean.status === 'ACTIVE' 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-gray-100 text-gray-800'
                }`}>
                  {bean.status === 'ACTIVE' ? '활성' : '비활성'}
                </span>
              )}
              {/* 이미지가 없을 때 디카페인 배지 */}
              {!bean.thumbnailUrl && bean.isDecaf && (
                <span className="inline-flex px-2 py-1 text-xs font-medium bg-orange-100 text-orange-800 rounded-full">
                  디카페인
                </span>
              )}
            </div>
            <p className="text-sm text-coffee-600 font-medium">{bean.roaster}</p>
          </div>
        </div>

        {/* Origin */}
        <div className="flex items-center text-sm text-gray-600">
          <MapPin className="h-4 w-4 mr-1" />
          <span>
            {bean.origin.country}
            {bean.origin.region && ` • ${bean.origin.region}`}
          </span>
        </div>

        {/* Bean Info */}
        <div className="grid grid-cols-2 gap-2 text-sm">
          <div>
            <span className="text-gray-500">로스팅:</span>
            <p className="font-medium">{getRoastLevelKorean(bean.roastLevel)}</p>
          </div>
          <div>
            <span className="text-gray-500">가공법:</span>
            <p className="font-medium">{getProcessTypeKorean(bean.processType)}</p>
          </div>
          <div>
            <span className="text-gray-500">타입:</span>
            <p className="font-medium">{getBlendTypeKorean(bean.blendType)}</p>
          </div>
          <div>
            <span className="text-gray-500">무게:</span>
            <p className="font-medium">{bean.grams}g</p>
          </div>
        </div>

        {/* Roast Date */}
        <div className="text-sm">
          <span className="text-gray-500">로스팅 일자: </span>
          <span className="font-medium">
            {formatDate(bean.roastDate, 'yyyy.MM.dd')}
          </span>
          <span className="text-gray-400 ml-2">
            ({formatRelativeDate(bean.roastDate)})
          </span>
        </div>

        {/* Flavors */}
        {bean.flavors && bean.flavors.length > 0 && (
          <div className="flex flex-wrap gap-1">
            {bean.flavors.slice(0, 3).map((flavor) => (
              <span
                key={flavor.id}
                className="inline-flex px-2 py-1 text-xs bg-coffee-100 text-coffee-800 rounded-full"
              >
                {flavor.name}
              </span>
            ))}
            {bean.flavors.length > 3 && (
              <span className="inline-flex px-2 py-1 text-xs bg-gray-100 text-gray-600 rounded-full">
                +{bean.flavors.length - 3}
              </span>
            )}
          </div>
        )}

        {/* Memo */}
        {bean.memo && (
          <p className="text-sm text-gray-600 line-clamp-2">
            {bean.memo}
          </p>
        )}
      </CardContent>

      {(onEdit || onDelete || onView) && (
        <CardFooter className="justify-end">
          <CardActions actions={actions} position={actionsPosition} />
        </CardFooter>
      )}
    </Card>
  );
};

export default BeanCard;
