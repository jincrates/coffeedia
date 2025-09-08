import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { beanService } from '@/services/beanService';
import { BeanResponse, BeanSearchQuery, CreateBeanCommand, UpdateBeanCommand } from '@/types/api';
import BeanList from '@/components/beans/BeanList';
import BeanForm from '@/components/beans/BeanForm';
import PageLayout from '@/components/common/PageLayout';
import FormLayout from '@/components/common/FormLayout';
import DeleteConfirmModal from '@/components/common/DeleteConfirmModal';
import Button from '@/components/common/Button';
import toast from 'react-hot-toast';

const BeansPage: React.FC = () => {
  const navigate = useNavigate();
  const [showForm, setShowForm] = useState(false);
  const [editingBean, setEditingBean] = useState<BeanResponse | null>(null);
  const [deleteBean, setDeleteBean] = useState<BeanResponse | null>(null);
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

  const handleDeleteBean = (bean: BeanResponse) => {
    setDeleteBean(bean);
  };

  const confirmDelete = async () => {
    if (!deleteBean) return;
    
    try {
      await beanService.deleteBean(deleteBean.beanId);
      toast.success('원두가 삭제되었습니다.');
      refetch();
      setDeleteBean(null);
    } catch (error) {
      toast.error('원두 삭제에 실패했습니다.');
    }
  };

  const cancelDelete = () => {
    setDeleteBean(null);
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
      <FormLayout
        title={editingBean ? '원두 수정' : '새 원두 등록'}
        subtitle={editingBean ? '원두 정보를 수정해보세요' : '나만의 특별한 원두를 등록해보세요'}
        onBack={handleFormCancel}
        onCancel={handleFormCancel}
        onSubmit={() => {}} // 폼 내부에서 처리
        submitLabel={editingBean ? '원두 수정' : '원두 등록'}
        isLoading={createBeanMutation.isLoading || updateBeanMutation.isLoading}
      >
        <BeanForm
          initialData={editingBean || undefined}
          onSubmit={handleFormSubmit}
          onCancel={handleFormCancel}
          loading={createBeanMutation.isLoading || updateBeanMutation.isLoading}
          mode={editingBean ? 'edit' : 'create'}
        />
      </FormLayout>
    );
  }

  if (error) {
    return (
      <PageLayout title="오류 발생" showSearch={false}>
        <div className="text-center py-12">
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
      </PageLayout>
    );
  }

  return (
    <>
      <PageLayout
        title="원두 관리"
        subtitle="나만의 원두 컬렉션을 관리해보세요"
        searchPlaceholder="원두 이름이나 로스터를 검색하세요..."
        onAddNew={handleCreateBean}
        onSearch={handleSearch}
        addButtonLabel="원두 추가"
      >
        {/* Bean List */}
        <BeanList
          beans={beansData?.content || []}
          loading={isLoading}
          onEdit={handleEditBean}
          onDelete={handleDeleteBean}
          onView={handleViewBean}
        />

        {/* Pagination */}
        {beansData && beansData.content.length > 0 && (
          <div className="mt-8 flex justify-center">
            <div className="text-sm text-gray-500">
              총 {beansData.content.length}개의 원두
            </div>
          </div>
        )}
      </PageLayout>

      {/* 삭제 확인 모달 */}
      <DeleteConfirmModal
        isOpen={!!deleteBean}
        title="원두 삭제 확인"
        message={`정말로 "${deleteBean?.name}"을 삭제하시겠습니까? 삭제된 원두는 복구할 수 없습니다.`}
        onConfirm={confirmDelete}
        onCancel={cancelDelete}
        loading={false}
      />
    </>
  );
};

export default BeansPage;
