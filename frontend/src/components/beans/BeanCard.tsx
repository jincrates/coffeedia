import React from 'react';
import { BeanResponse } from '@/types/api';
import Card, { CardContent, CardFooter } from '@/components/common/Card';
import Button from '@/components/common/Button';
import { Edit, Trash2, Eye, MapPin } from 'lucide-react';
import { formatDate, formatRelativeDate, getRoastLevelKorean, getProcessTypeKorean, getBlendTypeKorean } from '@/utils/format';

interface BeanCardProps {
  bean: BeanResponse;
  onEdit?: (bean: BeanResponse) => void;
  onDelete?: (beanId: number) => void;
  onView?: (bean: BeanResponse) => void;
}

const BeanCard: React.FC<BeanCardProps> = ({
  bean,
  onEdit,
  onDelete,
  onView,
}) => {
  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit?.(bean);
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    onDelete?.(bean.beanId);
  };

  const handleView = () => {
    onView?.(bean);
  };

  return (
    <Card 
      hover={!!onView} 
      onClick={handleView}
      className="transition-all duration-200"
    >
      <CardContent className="space-y-3">
        {/* Header */}
        <div className="flex justify-between items-start">
          <div className="flex-1">
            <h3 className="font-semibold text-lg text-gray-900 truncate">
              {bean.name}
            </h3>
            <p className="text-sm text-gray-600">{bean.roaster}</p>
          </div>
          <div className="text-right">
            <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full ${
              bean.status === 'ACTIVE' 
                ? 'bg-green-100 text-green-800' 
                : 'bg-gray-100 text-gray-800'
            }`}>
              {bean.status === 'ACTIVE' ? '활성' : '비활성'}
            </span>
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
        <CardFooter className="justify-end space-x-2">
          {onView && (
            <Button
              variant="ghost"
              size="sm"
              onClick={handleView}
              leftIcon={<Eye className="h-4 w-4" />}
            >
              상세
            </Button>
          )}
          {onEdit && (
            <Button
              variant="outline"
              size="sm"
              onClick={handleEdit}
              leftIcon={<Edit className="h-4 w-4" />}
            >
              수정
            </Button>
          )}
          {onDelete && (
            <Button
              variant="destructive"
              size="sm"
              onClick={handleDelete}
              leftIcon={<Trash2 className="h-4 w-4" />}
            >
              삭제
            </Button>
          )}
        </CardFooter>
      )}
    </Card>
  );
};

export default BeanCard;
