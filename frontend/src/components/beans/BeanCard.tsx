import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BeanResponse } from '@/types/api';
import Card, { CardContent, CardFooter } from '@/components/common/Card';
import CardActions from '@/components/common/CardActions';
import Button from '@/components/common/Button';
import { MapPin, Coffee, Calendar, Scale, Tag } from 'lucide-react';
import { formatDate, formatRelativeDate, getRoastLevelKorean, getProcessTypeKorean, getBlendTypeKorean } from '@/utils/format';

interface BeanCardProps {
  bean: BeanResponse;
  loading?: boolean;
  onEdit?: (bean: BeanResponse) => void;
  onDelete?: () => void;
  onView?: (bean: BeanResponse) => void;
  actionsPosition?: 'dropdown' | 'buttons';
}

const BeanCard: React.FC<BeanCardProps> = ({
  bean,
  loading = false,
  onEdit,
  onDelete,
  onView,
  actionsPosition = 'dropdown',
}) => {
  const navigate = useNavigate();
  const [imageError, setImageError] = useState(false);

  const handleView = () => {
    if (!loading && onView) {
      onView(bean);
    } else if (!loading) {
      navigate(`/beans/${bean.beanId}`);
    }
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
      disabled: loading,
    });
  }
  
  if (onEdit) {
    actions.push({
      type: 'edit' as const,
      label: '수정',
      onClick: () => onEdit(bean),
      disabled: loading,
    });
  }
  
  if (onDelete) {
    actions.push({
      type: 'delete' as const,
      label: '삭제',
      onClick: onDelete,
      variant: 'destructive' as const,
      disabled: loading,
    });
  }

  return (
    <Card 
      hover={!!onView && !loading || !onView} 
      onClick={handleView}
      className="h-full cursor-pointer"
    >
      <CardContent className="space-y-3">
        {/* 썸네일 이미지 */}
        {bean.thumbnailUrl ? (
          <div className="aspect-video rounded-lg overflow-hidden bg-gray-100">
            {!imageError ? (
              <img
                src={bean.thumbnailUrl}
                alt={bean.name}
                className="w-full h-full object-cover"
                onError={handleImageError}
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-coffee-100 to-coffee-200">
                <Coffee className="h-16 w-16 text-coffee-400" />
              </div>
            )}
          </div>
        ) : (
          <div className="aspect-video rounded-lg bg-gradient-to-br from-coffee-100 to-coffee-200 flex items-center justify-center">
            <Coffee className="h-16 w-16 text-coffee-300" />
          </div>
        )}

        {/* 제목과 상태 */}
        <div>
          <div className="flex justify-between items-start mb-2">
            <h3 className="font-semibold text-lg text-gray-900 line-clamp-2 flex-1">
              {bean.name}
            </h3>
            <div className="flex items-center gap-2 ml-2">
              <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full flex-shrink-0 ${
                bean.status === 'ACTIVE' 
                  ? 'bg-green-100 text-green-800' 
                  : 'bg-gray-100 text-gray-800'
              }`}>
                {bean.status === 'ACTIVE' ? '활성' : '비활성'}
              </span>
              {bean.isDecaf && (
                <span className="inline-flex px-2 py-1 text-xs font-medium bg-orange-100 text-orange-800 rounded-full flex-shrink-0">
                  디카페인
                </span>
              )}
              {/* 액션 메뉴 */}
              {actions.length > 0 && actionsPosition === 'dropdown' && (
                <CardActions actions={actions} position="dropdown" />
              )}
            </div>
          </div>
          
          <p className="text-sm text-coffee-600 font-medium mb-1">{bean.roaster}</p>
          
          {/* 원산지 */}
          <div className="flex items-center text-sm text-gray-600">
            <MapPin className="h-4 w-4 mr-1" />
            <span>
              {bean.origin.country}
              {bean.origin.region && ` • ${bean.origin.region}`}
            </span>
          </div>
        </div>

        {/* 메타 정보 */}
        <div className="flex items-center text-sm text-gray-500 space-x-4">
          <div className="flex items-center">
            <Scale className="h-4 w-4 mr-1" />
            <span>{bean.grams}g</span>
          </div>
          <div className="flex items-center">
            <Calendar className="h-4 w-4 mr-1" />
            <span>{formatRelativeDate(bean.roastDate)}</span>
          </div>
        </div>

        {/* 가공 정보 태그 */}
        <div className="flex flex-wrap gap-1">
          <Tag className="h-3 w-3 text-gray-400 mt-1" />
          <span className="inline-flex px-2 py-1 text-xs bg-coffee-100 text-coffee-800 rounded-full">
            {getRoastLevelKorean(bean.roastLevel)}
          </span>
          <span className="inline-flex px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full">
            {getProcessTypeKorean(bean.processType)}
          </span>
          <span className="inline-flex px-2 py-1 text-xs bg-green-100 text-green-800 rounded-full">
            {getBlendTypeKorean(bean.blendType)}
          </span>
        </div>

        {/* 향미 태그 */}
        {bean.flavors && bean.flavors.length > 0 && (
          <div className="flex flex-wrap gap-1">
            {bean.flavors.slice(0, 3).map((flavor) => (
              <span
                key={flavor.id}
                className="inline-flex px-2 py-1 text-xs bg-purple-100 text-purple-800 rounded-full"
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

        {/* 메모 */}
        {bean.memo && (
          <p className="text-sm text-gray-600 line-clamp-2">
            {bean.memo}
          </p>
        )}
      </CardContent>

      {actions.length > 0 && actionsPosition === 'buttons' && (
        <CardFooter>
          <CardActions actions={actions} position="buttons" />
        </CardFooter>
      )}
    </Card>
  );
};

export default BeanCard;
