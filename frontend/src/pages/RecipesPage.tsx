import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { Plus, Search, ArrowLeft } from 'lucide-react';
import { recipeService } from '@/services/recipeService';
import { CreateRecipeCommand, RecipeSummaryResponse, RecipeSearchQuery, RecipeResponse } from '@/types/api';
import RecipeList from '@/components/recipes/RecipeList';
import RecipeForm from '@/components/recipes/RecipeForm';
import RecipeDetail from '@/components/recipes/RecipeDetail';
import Button from '@/components/common/Button';
import Input from '@/components/common/Input';
import DeleteConfirmModal from '@/components/common/DeleteConfirmModal';
import toast from 'react-hot-toast';

const RecipesPage: React.FC = () => {
  const [showForm, setShowForm] = useState(false);
  const [selectedRecipe, setSelectedRecipe] = useState<RecipeResponse | null>(null);
  const [loadingRecipeId, setLoadingRecipeId] = useState<number | null>(null);
  const [deleteRecipe, setDeleteRecipe] = useState<RecipeSummaryResponse | null>(null);
  const [searchQuery, setSearchQuery] = useState<RecipeSearchQuery>({
    page: 0,
    size: 12,
  });

  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // 레시피 목록 조회
  const {
    data: recipesData,
    isLoading,
    error,
    refetch,
  } = useQuery(
    ['recipes', searchQuery],
    () => recipeService.getAllRecipes(searchQuery),
    {
      keepPreviousData: true,
    }
  );

  // 레시피 생성 뮤테이션
  const createRecipeMutation = useMutation(
    (data: CreateRecipeCommand) => recipeService.createRecipe(data),
    {
      onSuccess: () => {
        toast.success('레시피가 성공적으로 등록되었습니다!');
        setShowForm(false);
        queryClient.invalidateQueries(['recipes']);
      },
      onError: (error: any) => {
        toast.error(error.response?.data?.message || '레시피 등록에 실패했습니다.');
      },
    }
  );

  // 레시피 삭제 뮤테이션
  const deleteRecipeMutation = useMutation(
    (recipeId: number) => recipeService.deleteRecipe(recipeId),
    {
      onSuccess: () => {
        toast.success('레시피가 성공적으로 삭제되었습니다!');
        queryClient.invalidateQueries(['recipes']);
        setDeleteRecipe(null);
      },
      onError: (error: any) => {
        toast.error(error.response?.data?.message || '레시피 삭제에 실패했습니다.');
      },
    }
  );

  const handleCreateRecipe = () => {
    setShowForm(true);
  };

  const handleFormSubmit = (data: CreateRecipeCommand) => {
    createRecipeMutation.mutate(data);
  };

  const handleFormCancel = () => {
    setShowForm(false);
  };

  const handleViewRecipe = async (recipeSummary: RecipeSummaryResponse) => {
    try {
      setLoadingRecipeId(recipeSummary.id);
      
      // 옵션 1: 모달로 표시 (기본)
      const recipe = await recipeService.getRecipe(recipeSummary.id);
      setSelectedRecipe(recipe);
      
      // 옵션 2: 새 페이지로 이동 (주석 해제하여 사용 가능)
      // navigate(`/recipes/${recipeSummary.id}`);
    } catch (error) {
      toast.error('레시피를 불러오는데 실패했습니다.');
    } finally {
      setLoadingRecipeId(null);
    }
  };

  const handleCloseDetail = () => {
    setSelectedRecipe(null);
  };

  const handleEditRecipe = (recipe: RecipeSummaryResponse) => {
    navigate(`/recipes/${recipe.id}/edit`);
  };

  const handleDeleteRecipe = (recipe: RecipeSummaryResponse) => {
    setDeleteRecipe(recipe);
  };

  const confirmDelete = () => {
    if (deleteRecipe) {
      deleteRecipeMutation.mutate(deleteRecipe.id);
    }
  };

  const cancelDelete = () => {
    setDeleteRecipe(null);
  };

  const handleSearch = (query: string) => {
    // TODO: 검색 기능 구현
    toast.success('검색 기능은 곧 구현됩니다!');
  };

  // 폼 표시 중일 때
  if (showForm) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* 헤더 */}
          <div className="mb-8">
            <Button
              variant="ghost"
              onClick={handleFormCancel}
              leftIcon={<ArrowLeft className="h-4 w-4" />}
              className="mb-4"
            >
              목록으로 돌아가기
            </Button>
            <h1 className="text-2xl font-bold text-gray-900">새 레시피 등록</h1>
            <p className="text-gray-600 mt-1">
              나만의 특별한 커피 레시피를 공유해보세요
            </p>
          </div>

          {/* 레시피 폼 */}
          <RecipeForm
            onSubmit={handleFormSubmit}
            onCancel={handleFormCancel}
            loading={createRecipeMutation.isLoading}
          />
        </div>
      </div>
    );
  }

  // 에러 처리
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

  // 메인 레시피 목록 화면
  return (
    <>
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* Header */}
          <div className="mb-8">
            <div className="flex justify-between items-center mb-6">
              <div>
                <h1 className="text-2xl font-bold text-gray-900">레시피 북</h1>
                <p className="text-gray-600 mt-1">
                  나만의 커피 레시피를 저장하고 관리해보세요
                </p>
              </div>
              <Button
                onClick={handleCreateRecipe}
                leftIcon={<Plus className="h-4 w-4" />}
              >
                레시피 추가
              </Button>
            </div>

            {/* Search Bar */}
            <div className="max-w-md">
              <Input
                placeholder="레시피 제목이나 태그를 검색하세요..."
                leftIcon={<Search className="h-4 w-4" />}
                onKeyPress={(e) => {
                  if (e.key === 'Enter') {
                    handleSearch(e.currentTarget.value);
                  }
                }}
              />
            </div>
          </div>

          {/* Recipe List */}
          <RecipeList
            recipes={recipesData?.content || []}
            loading={isLoading}
            onView={handleViewRecipe}
            onEdit={handleEditRecipe}
            onDelete={handleDeleteRecipe}
            showActions={true}
            loadingRecipeId={loadingRecipeId}
          />

          {/* Pagination - TODO: 구현 */}
          {recipesData && recipesData.content.length > 0 && (
            <div className="mt-8 flex justify-center">
              <div className="text-sm text-gray-500">
                총 {recipesData.content.length}개의 레시피
              </div>
            </div>
          )}
        </div>
      </div>

      {/* 레시피 상세 모달 */}
      {selectedRecipe && (
        <RecipeDetail
          recipe={selectedRecipe}
          onClose={handleCloseDetail}
        />
      )}

      {/* 삭제 확인 모달 */}
      <DeleteConfirmModal
        isOpen={!!deleteRecipe}
        title="레시피 삭제 확인"
        message={`정말로 "${deleteRecipe?.title}"를 삭제하시겠습니까? 삭제된 레시피는 복구할 수 없습니다.`}
        onConfirm={confirmDelete}
        onCancel={cancelDelete}
        loading={deleteRecipeMutation.isLoading}
      />
    </>
  );
};

export default RecipesPage;
