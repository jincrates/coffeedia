import React, { useState } from 'react';
import { ArrowLeft, Edit, Trash2, Share2, Heart } from 'lucide-react';
import Button from '@/components/common/Button';
import DeleteConfirmModal from '@/components/common/DeleteConfirmModal';

interface DetailPageLayoutProps {
  title: string;
  subtitle?: string;
  status?: {
    label: string;
    variant: 'active' | 'inactive' | 'success' | 'warning' | 'danger';
  };
  badges?: Array<{
    label: string;
    variant: 'primary' | 'secondary' | 'success' | 'warning' | 'danger';
  }>;
  onBack: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
  onShare?: () => void;
  onFavorite?: () => void;
  backLabel?: string;
  deleteConfirmTitle?: string;
  deleteConfirmMessage?: string;
  isDeleting?: boolean;
  children: React.ReactNode;
  extraActions?: React.ReactNode;
}

const DetailPageLayout: React.FC<DetailPageLayoutProps> = ({
  title,
  subtitle,
  status,
  badges = [],
  onBack,
  onEdit,
  onDelete,
  onShare,
  onFavorite,
  backLabel = '목록으로 돌아가기',
  deleteConfirmTitle = '삭제 확인',
  deleteConfirmMessage = '정말로 삭제하시겠습니까? 삭제된 데이터는 복구할 수 없습니다.',
  isDeleting = false,
  children,
  extraActions,
}) => {
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  const handleDelete = () => {
    setShowDeleteModal(true);
  };

  const confirmDelete = () => {
    onDelete?.();
    setShowDeleteModal(false);
  };

  const cancelDelete = () => {
    setShowDeleteModal(false);
  };

  const getStatusClass = (variant: string) => {
    switch (variant) {
      case 'active':
      case 'success':
        return 'bg-green-100 text-green-800';
      case 'inactive':
        return 'bg-gray-100 text-gray-800';
      case 'warning':
        return 'bg-yellow-100 text-yellow-800';
      case 'danger':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-blue-100 text-blue-800';
    }
  };

  const getBadgeClass = (variant: string) => {
    switch (variant) {
      case 'primary':
        return 'bg-blue-100 text-blue-800';
      case 'secondary':
        return 'bg-gray-100 text-gray-800';
      case 'success':
        return 'bg-green-100 text-green-800';
      case 'warning':
        return 'bg-yellow-100 text-yellow-800';
      case 'danger':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
        {/* 상단 네비게이션 */}
        <div className="flex items-center justify-between mb-6">
          <Button
            variant="ghost"
            onClick={onBack}
            leftIcon={<ArrowLeft className="h-4 w-4" />}
          >
            {backLabel}
          </Button>

          <div className="flex items-center space-x-2">
            {onShare && (
              <Button
                variant="ghost"
                size="sm"
                onClick={onShare}
                leftIcon={<Share2 className="h-4 w-4" />}
              >
                공유
              </Button>
            )}
            {onFavorite && (
              <Button
                variant="ghost"
                size="sm"
                onClick={onFavorite}
                leftIcon={<Heart className="h-4 w-4" />}
              >
                즐겨찾기
              </Button>
            )}
            {extraActions}
            {onEdit && (
              <Button
                variant="outline"
                size="sm"
                onClick={onEdit}
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
          </div>
        </div>

        {/* 헤더 */}
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <div className="flex justify-between items-start mb-4">
            <div className="flex items-center gap-3 flex-1">
              <h1 className="text-3xl font-bold text-gray-900">{title}</h1>
              
              {/* 상태 배지 */}
              {status && (
                <span className={`inline-flex px-3 py-1 text-sm font-medium rounded-full ${getStatusClass(status.variant)}`}>
                  {status.label}
                </span>
              )}
              
              {/* 추가 배지들 */}
              {badges.map((badge, index) => (
                <span
                  key={index}
                  className={`inline-flex px-3 py-1 text-sm font-medium rounded-full ${getBadgeClass(badge.variant)}`}
                >
                  {badge.label}
                </span>
              ))}
            </div>
          </div>
          
          {/* 부제목 */}
          {subtitle && (
            <p className="text-gray-600 text-lg">{subtitle}</p>
          )}
        </div>

        {/* 컨텐츠 */}
        {children}

        {/* 삭제 확인 모달 */}
        <DeleteConfirmModal
          isOpen={showDeleteModal}
          title={deleteConfirmTitle}
          message={deleteConfirmMessage}
          onConfirm={confirmDelete}
          onCancel={cancelDelete}
          loading={isDeleting}
        />
      </div>
    </div>
  );
};

export default DetailPageLayout;
