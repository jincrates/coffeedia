import React from 'react';
import { ArrowLeft, Plus, Search } from 'lucide-react';
import Button from '@/components/common/Button';
import Input from '@/components/common/Input';

interface PageLayoutProps {
  title: string;
  subtitle?: string;
  searchPlaceholder?: string;
  onAddNew?: () => void;
  onSearch?: (query: string) => void;
  onBack?: () => void;
  backLabel?: string;
  addButtonLabel?: string;
  showSearch?: boolean;
  children: React.ReactNode;
  extraActions?: React.ReactNode;
}

const PageLayout: React.FC<PageLayoutProps> = ({
  title,
  subtitle,
  searchPlaceholder,
  onAddNew,
  onSearch,
  onBack,
  backLabel = '목록으로 돌아가기',
  addButtonLabel = '추가',
  showSearch = true,
  children,
  extraActions,
}) => {
  const handleSearch = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && onSearch) {
      onSearch(e.currentTarget.value);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
        {/* 뒤로가기 버튼 */}
        {onBack && (
          <Button
            variant="ghost"
            onClick={onBack}
            leftIcon={<ArrowLeft className="h-4 w-4" />}
            className="mb-6"
          >
            {backLabel}
          </Button>
        )}

        {/* 헤더 */}
        <div className="mb-4 md:mb-8">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 gap-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">{title}</h1>
              {subtitle && (
                <p className="text-gray-600 mt-1">{subtitle}</p>
              )}
            </div>
            <div className="flex items-center gap-3">
              {extraActions}
              {onAddNew && (
                <Button
                  onClick={onAddNew}
                  leftIcon={<Plus className="h-4 w-4" />}
                  className="w-full sm:w-auto"
                >
                  {addButtonLabel}
                </Button>
              )}
            </div>
          </div>

          {/* 검색 바 */}
          {showSearch && onSearch && searchPlaceholder && (
            <div className="w-full sm:max-w-md">
              <Input
                placeholder={searchPlaceholder}
                leftIcon={<Search className="h-4 w-4" />}
                onKeyPress={handleSearch}
              />
            </div>
          )}
        </div>

        {/* 컨텐츠 */}
        {children}
      </div>
    </div>
  );
};

export default PageLayout;
