import React from 'react';
import Button from '@/components/common/Button';

interface DeleteConfirmModalProps {
  isOpen: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
  loading?: boolean;
}

const DeleteConfirmModal: React.FC<DeleteConfirmModalProps> = ({
  isOpen,
  title,
  message,
  onConfirm,
  onCancel,
  loading = false,
}) => {
  if (!isOpen) return null;

  const handleBackgroundClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onCancel();
    }
  };

  return (
    <div 
      className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
      onClick={handleBackgroundClick}
    >
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          {title}
        </h3>
        <p className="text-gray-600 mb-6">
          {message}
        </p>
        <div className="flex justify-end space-x-3">
          <Button
            variant="outline"
            onClick={onCancel}
            disabled={loading}
          >
            취소
          </Button>
          <Button
            variant="destructive"
            onClick={onConfirm}
            loading={loading}
          >
            삭제
          </Button>
        </div>
      </div>
    </div>
  );
};

export default DeleteConfirmModal;
