import React from 'react';
import { BeanResponse } from '@/types/api';
import BeanCard from './BeanCard';

interface BeanListProps {
  beans: BeanResponse[];
  loading?: boolean;
  onEdit?: (bean: BeanResponse) => void;
  onDelete?: (bean: BeanResponse) => void;
  onView?: (bean: BeanResponse) => void;
}

const BeanList: React.FC<BeanListProps> = ({
  beans,
  loading = false,
  onEdit,
  onDelete,
  onView,
}) => {
  if (loading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {Array.from({ length: 6 }).map((_, index) => (
          <div
            key={index}
            className="animate-pulse bg-gray-200 rounded-lg h-64"
          />
        ))}
      </div>
    );
  }

  if (beans.length === 0) {
    return (
      <div className="text-center py-12">
        <div className="text-gray-400 text-lg mb-2">등록된 원두가 없습니다</div>
        <div className="text-gray-500 text-sm">
          첫 번째 원두를 등록해보세요!
        </div>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {beans.map((bean) => (
        <BeanCard
          key={bean.beanId}
          bean={bean}
          onEdit={onEdit}
          onDelete={onDelete ? () => onDelete(bean) : undefined}
          onView={onView}
          actionsPosition="dropdown"
          loading={loading}
        />
      ))}
    </div>
  );
};

export default BeanList;
