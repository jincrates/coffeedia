import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { ArrowLeft } from 'lucide-react';
import { beanService } from '@/services/beanService';
import { UpdateBeanCommand } from '@/types/api';
import BeanForm from '@/components/beans/BeanForm';
import Button from '@/components/common/Button';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import toast from 'react-hot-toast';

const BeanEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const beanId = id ? parseInt(id, 10) : null;

  const {
    data: bean,
    isLoading,
    error,
  } = useQuery(
    ['bean', beanId],
    () => beanId ? beanService.getBean(beanId) : Promise.reject('Invalid bean ID'),
    {
      enabled: !!beanId,
      retry: 1,
    }
  );

  // 원두 수정 뮤테이션
  const updateBeanMutation = useMutation(
    (data: UpdateBeanCommand) => 
      beanId ? beanService.updateBean(beanId, data) : Promise.reject('Invalid bean ID'),
    {
      onSuccess: () => {
        toast.success('원두가 성공적으로 수정되었습니다!');
        navigate(`/beans/${beanId}`);
        queryClient.invalidateQueries(['beans']);
        queryClient.invalidateQueries(['bean', beanId]);
      },
      onError: (error: any) => {
        toast.error(error.response?.data?.message || '원두 수정에 실패했습니다.');
      },
    }
  );

  const handleBack = () => {
    navigate(`/beans/${beanId}`);
  };

  const handleFormSubmit = (data: UpdateBeanCommand) => {
    updateBeanMutation.mutate(data);
  };

  const handleFormCancel = () => {
    navigate(`/beans/${beanId}`);
  };

  // 에러 상태
  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-semibold text-gray-900 mb-2">
            원두 정보를 불러올 수 없습니다
          </h2>
          <p className="text-gray-600 mb-4">
            요청하신 원두가 존재하지 않거나 접근할 수 없습니다.
          </p>
          <Button onClick={() => navigate('/beans')}>
            목록으로 돌아가기
          </Button>
        </div>
      </div>
    );
  }

  // 로딩 상태
  if (isLoading || !bean) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
          <div className="mb-6">
            <Button
              variant="ghost"
              onClick={handleBack}
              leftIcon={<ArrowLeft className="h-4 w-4" />}
            >
              상세 페이지로 돌아가기
            </Button>
          </div>
          <div className="flex items-center justify-center h-64">
            <LoadingSpinner text="원두 정보를 불러오는 중..." />
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
        {/* 헤더 */}
        <div className="mb-4 md:mb-8">
          <Button
            variant="ghost"
            onClick={handleBack}
            leftIcon={<ArrowLeft className="h-4 w-4" />}
            className="mb-4"
          >
            상세 페이지로 돌아가기
          </Button>
          <h1 className="text-2xl font-bold text-gray-900">원두 수정</h1>
          <p className="text-gray-600 mt-1">
            {bean.name}의 정보를 수정해보세요
          </p>
        </div>

        {/* 원두 폼 */}
        <BeanForm
          initialData={bean}
          onSubmit={handleFormSubmit}
          onCancel={handleFormCancel}
          loading={updateBeanMutation.isLoading}
          mode="edit"
        />
      </div>
    </div>
  );
};

export default BeanEditPage;
