import React from 'react';
import { ArrowLeft, Edit, Trash2, Share2, Heart } from 'lucide-react';
import Button from '@/components/common/Button';
import Card, { CardHeader, CardTitle, CardContent } from '@/components/common/Card';
import DeleteConfirmModal from '@/components/common/DeleteConfirmModal';

interface EntityDetailLayoutProps {
  // 기본 정보
  title: string;
  subtitle?: string;
  
  // 상태 및 배지
  status?: {
    label: string;
    variant: 'active' | 'inactive' | 'success' | 'warning' | 'danger';
  };
  badges?: Array<{
    label: string;
    variant: 'primary' | 'secondary' | 'success' | 'warning' | 'danger';
  }>;
  
  // 썸네일
  thumbnailUrl?: string;
  thumbnailAlt?: string;
  fallbackIcon?: React.ReactNode;
  
  // 액션
  onBack: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
  onShare?: () => void;
  onFavorite?: () => void;
  
  // 삭제 관련
  deleteConfirmTitle?: string;
  deleteConfirmMessage?: string;
  isDeleting?: boolean;
  
  // 레이아웃
  children: React.ReactNode;
  sidebarContent?: React.ReactNode;
  
  // 로딩
  isLoading?: boolean;
  error?: any;
}

const EntityDetailLayout: React.FC<EntityDetailLayoutProps> = ({
  title,
  subtitle,
  status,
  badges = [],
  thumbnailUrl,
  thumbnailAlt,
  fallbackIcon,
  onBack,
  onEdit,
  onDelete,
  onShare,
  onFavorite,
  deleteConfirmTitle = '삭제 확인',
  deleteConfirmMessage = '정말로 삭제하시겠습니까? 삭제된 데이터는 복구할 수 없습니다.',
  isDeleting = false,
  children,
  sidebarContent,
  isLoading = false,
  error,
}) => {
  const [showDeleteModal, setShowDeleteModal] = React.useState(false);

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

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
          <Button
            variant="ghost"
            onClick={onBack}
            leftIcon={<ArrowLeft className="h-4 w-4" />}
            className="mb-6"
          >
            목록으로 돌아가기
          </Button>
          <div className="flex items-center justify-center h-64">
            <div className="text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-coffee-600 mx-auto mb-4"></div>
              <p className="text-gray-600">데이터를 불러오는 중...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
          <Button
            variant="ghost"
            onClick={onBack}
            leftIcon={<ArrowLeft className="h-4 w-4" />}
            className="mb-6"
          >
            목록으로 돌아가기
          </Button>
          <div className="text-center py-12">
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              데이터를 찾을 수 없습니다
            </h2>
            <p className="text-gray-600 mb-4">
              요청하신 데이터가 존재하지 않거나 삭제되었습니다.
            </p>
          </div>
        </div>
      </div>
    );
  }

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
            목록으로 돌아가기
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

        {/* 헤더 카드 */}
        <Card className="mb-6">
          <CardContent className="p-6">
            <div className="flex flex-col lg:flex-row gap-6">
              {/* 썸네일 */}
              {(thumbnailUrl || fallbackIcon) && (
                <div className="flex-shrink-0">
                  {thumbnailUrl ? (
                    <div className="w-32 h-32 rounded-lg overflow-hidden bg-gray-100">
                      <img
                        src={thumbnailUrl}
                        alt={thumbnailAlt || title}
                        className="w-full h-full object-cover"
                        onError={(e) => {
                          e.currentTarget.style.display = 'none';
                          e.currentTarget.nextElementSibling?.classList.remove('hidden');
                        }}
                      />
                      <div className="hidden w-full h-full flex items-center justify-center">
                        {fallbackIcon}
                      </div>
                    </div>
                  ) : (
                    <div className="w-32 h-32 rounded-lg bg-gray-100 flex items-center justify-center">
                      {fallbackIcon}
                    </div>
                  )}
                </div>
              )}

              {/* 제목과 정보 */}
              <div className="flex-1">
                <div className="flex flex-wrap items-start gap-3 mb-3">
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
                
                {/* 부제목 */}
                {subtitle && (
                  <p className="text-gray-600 text-lg">{subtitle}</p>
                )}
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 메인 컨텐츠 */}
        <div className={`grid gap-6 ${sidebarContent ? 'grid-cols-1 lg:grid-cols-3' : 'grid-cols-1'}`}>
          {/* 주요 콘텐츠 */}
          <div className={sidebarContent ? 'lg:col-span-2' : 'col-span-1'}>
            {children}
          </div>

          {/* 사이드바 */}
          {sidebarContent && (
            <div className="lg:col-span-1">
              {sidebarContent}
            </div>
          )}
        </div>

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

export default EntityDetailLayout;
