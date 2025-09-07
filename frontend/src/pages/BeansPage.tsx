import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { Plus, Search, ArrowLeft } from 'lucide-react';
import { beanService } from '@/services/beanService';
import { BeanResponse, BeanSearchQuery, CreateBeanCommand, UpdateBeanCommand } from '@/types/api';
import BeanList from '@/components/beans/BeanList';
import BeanForm from '@/components/beans/BeanForm';
import Button from '@/components/common/Button';
import Input from '@/components/common/Input';
import toast from 'react-hot-toast';

const BeansPage: React.FC = () => {
  const navigate = useNavigate();
  const [showForm, setShowForm] = useState(false);
  const [editingBean, setEditingBean] = useState<BeanResponse | null>(null);
  const [searchQuery, setSearchQuery] = useState<BeanSearchQuery>({
    page: 0,
    size: 12,
  });

  const queryClient = useQueryClient();

  const {
    data: beansData,
    isLoading,
    error,
    refetch,
  } = useQuery(
    ['beans', searchQuery],
    () => beanService.getAllBeans(searchQuery),
    {
      keepPreviousData: true,
    }
  );

  // 원두 생성 뮤테이션
  const createBeanMutation = useMutation(
    (data: CreateBeanCommand) => beanService.createBean(data),
    {
      onSuccess: () => {
        toast.success('원두가 성공적으로 등록되었습니다!');
        setShowForm(false);
        queryClient.invalidateQueries(['beans']);
      },
      onError: (error: any) => {
        toast.error(error.response?.data?.message || '원두 등록에 실패했습니다.');
      },
    }
  );

  // 원두 수정 뮤테이션
  const updateBeanMutation = useMutation(
    ({ beanId, data }: { beanId: number; data: UpdateBeanCommand }) => 
      beanService.updateBean(beanId, data),
    {
      onSuccess: () => {
        toast.success('원두가 성공적으로 수정되었습니다!');
        setEditingBean(null);
        setShowForm(false);
        queryClient.invalidateQueries(['beans']);
      },
      onError: (error: any) => {
        toast.error(error.response?.data?.message || '원두 수정에 실패했습니다.');
      },
    }
  );

  const handleCreateBean = () => {
    setEditingBean(null);
    setShowForm(true);
  };

  const handleEditBean = (bean: BeanResponse) => {
    setEditingBean(bean);
    setShowForm(true);
  };

  const handleFormSubmit = (data: CreateBeanCommand | UpdateBeanCommand) => {
    if (editingBean) {
      updateBeanMutation.mutate({ 
        beanId: editingBean.beanId, 
        data: data as UpdateBeanCommand 
      });
    } else {
      createBeanMutation.mutate(data as CreateBeanCommand);
    }
  };

  const handleFormCancel = () => {
    setShowForm(false);
    setEditingBean(null);
  };

  const handleDeleteBean = async (beanId: number) => {
    if (!confirm('정말로 이 원두를 삭제하시겠습니까?')) {
      return;
    }

    try {
      await beanService.deleteBean(beanId);
      toast.success('원두가 삭제되었습니다.');
      refetch();
    } catch (error) {
      toast.error('원두 삭제에 실패했습니다.');
    }
  };

  const handleViewBean = (bean: BeanResponse) => {
    navigate(`/beans/${bean.beanId}`);
  };

  const handleSearch = (query: string) => {
    // TODO: 검색 기능 구현
    toast.success('검색 기능은 곧 구현됩니다!');
  };

  // 폼 표시 중일 때
  if (showForm) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
          {/* 헤더 */}
          <div className="mb-4 md:mb-8">
            <Button
              variant="ghost"
              onClick={handleFormCancel}
              leftIcon={<ArrowLeft className="h-4 w-4" />}
              className="mb-4"
            >
              목록으로 돌아가기
            </Button>
            <h1 className="text-2xl font-bold text-gray-900">
              {editingBean ? '원두 수정' : '새 원두 등록'}
            </h1>
            <p className="text-gray-600 mt-1">
              {editingBean 
                ? '원두 정보를 수정해보세요' 
                : '나만의 특별한 원두를 등록해보세요'
              }
            </p>
          </div>

          {/* 원두 폼 */}
          <BeanForm
            initialData={editingBean || undefined}
            onSubmit={handleFormSubmit}
            onCancel={handleFormCancel}
            loading={createBeanMutation.isLoading || updateBeanMutation.isLoading}
            mode={editingBean ? 'edit' : 'create'}
          />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-semibold text-gray-900 mb-2">
            데이터를 불러오는 중 오류가 발생했습니다
          </h2>
          <p className="text-gray-600 mb-4">
            잠시 후 다시 시도해주세요.
          </p>
          <Button onClick={() => refetch()}>
            다시 시도
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
        {/* Header */}
        <div className="mb-4 md:mb-8">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 gap-4">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">원두 관리</h1>
              <p className="text-gray-600 mt-1">
                나만의 원두 컬렉션을 관리해보세요
              </p>
            </div>
            <Button
              onClick={handleCreateBean}
              leftIcon={<Plus className="h-4 w-4" />}
              className="w-full sm:w-auto"
            >
              원두 추가
            </Button>
          </div>

          {/* Search Bar */}
          <div className="w-full sm:max-w-md">
            <Input
              placeholder="원두 이름이나 로스터를 검색하세요..."
              leftIcon={<Search className="h-4 w-4" />}
              onKeyPress={(e) => {
                if (e.key === 'Enter') {
                  handleSearch(e.currentTarget.value);
                }
              }}
            />
          </div>
        </div>

        {/* Bean List */}
        <BeanList
          beans={beansData?.content || []}
          loading={isLoading}
          onEdit={handleEditBean}
          onDelete={handleDeleteBean}
          onView={handleViewBean}
        />

        {/* Pagination - TODO: 구현 */}
        {beansData && beansData.content.length > 0 && (
          <div className="mt-8 flex justify-center">
            <div className="text-sm text-gray-500">
              총 {beansData.content.length}개의 원두
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default BeansPage;
