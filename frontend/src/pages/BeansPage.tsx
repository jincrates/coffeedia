import React, {useState} from 'react';
import {useQuery} from 'react-query';
import {Plus, Search} from 'lucide-react';
import {beanService} from '@/services/beanService';
import {BeanResponse, BeanSearchQuery} from '@/types/api';
import BeanList from '@/components/beans/BeanList';
import Button from '@/components/common/Button';
import Input from '@/components/common/Input';
import toast from 'react-hot-toast';

const BeansPage: React.FC = () => {
  const [searchQuery] = useState<BeanSearchQuery>({
    page: 0,
    size: 12,
  });

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

  const handleCreateBean = () => {
    // TODO: 원두 생성 모달 또는 페이지로 이동
    toast.success('원두 생성 기능은 곧 구현됩니다!');
  };

  const handleEditBean = (bean: BeanResponse) => {
    // TODO: 원두 수정 모달 또는 페이지로 이동
    toast.success(`${bean.name} 수정 기능은 곧 구현됩니다!`);
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
    // TODO: 원두 상세 모달 또는 페이지로 이동
    toast.success(`${bean.name} 상세 기능은 곧 구현됩니다!`);
  };

  const handleSearch = () => {
    // TODO: 검색 기능 구현
    toast.success('검색 기능은 곧 구현됩니다!');
  };

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
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* Header */}
          <div className="mb-8">
            <div className="flex justify-between items-center mb-6">
              <div>
                <h1 className="text-2xl font-bold text-gray-900">원두 관리</h1>
                <p className="text-gray-600 mt-1">
                  나만의 원두 컬렉션을 관리해보세요
                </p>
              </div>
              <Button
                  onClick={handleCreateBean}
                  leftIcon={<Plus className="h-4 w-4"/>}
              >
                원두 추가
              </Button>
            </div>

            {/* Search Bar */}
            <div className="max-w-md">
              <Input
                  placeholder="원두 이름이나 로스터를 검색하세요..."
                  leftIcon={<Search className="h-4 w-4"/>}
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      handleSearch();
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
