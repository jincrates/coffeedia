import React from 'react';
import { ArrowLeft } from 'lucide-react';
import Button from '@/components/common/Button';

interface FormLayoutProps {
  title: string;
  subtitle?: string;
  onBack: () => void;
  onCancel: () => void;
  onSubmit: () => void;
  backLabel?: string;
  submitLabel?: string;
  cancelLabel?: string;
  isLoading?: boolean;
  children: React.ReactNode;
}

const FormLayout: React.FC<FormLayoutProps> = ({
  title,
  subtitle,
  onBack,
  onCancel,
  onSubmit,
  backLabel = '목록으로 돌아가기',
  submitLabel = '저장',
  cancelLabel = '취소',
  isLoading = false,
  children,
}) => {
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
        {/* 뒤로가기 버튼 */}
        <Button
          variant="ghost"
          onClick={onBack}
          leftIcon={<ArrowLeft className="h-4 w-4" />}
          className="mb-6"
        >
          {backLabel}
        </Button>

        {/* 헤더 */}
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">{title}</h1>
          {subtitle && (
            <p className="text-gray-600 mt-1">{subtitle}</p>
          )}
        </div>

        {/* 폼 컨텐츠 */}
        <form onSubmit={onSubmit} className="space-y-6">
          {children}
          
          {/* 액션 버튼 */}
          <div className="flex justify-end space-x-3 pt-6 border-t border-gray-200">
            <Button
              type="button"
              variant="outline"
              onClick={onCancel}
              disabled={isLoading}
            >
              {cancelLabel}
            </Button>
            <Button
              type="submit"
              loading={isLoading}
            >
              {submitLabel}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default FormLayout;
