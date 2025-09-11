import React, {useState} from 'react';
import {useMutation, useQuery, useQueryClient} from 'react-query';
import {useNavigate} from 'react-router-dom';
import {recipeService} from '@/services/recipeService';
import {
  CreateRecipeCommand,
  RecipeResponse,
  RecipeSearchQuery,
  RecipeSummaryResponse,
  UpdateRecipeCommand
} from '@/types/api';
import RecipeList from '@/components/recipes/RecipeList';
import RecipeForm from '@/components/recipes/RecipeForm';
import PageLayout from '@/components/common/PageLayout';
import FormLayout from '@/components/common/FormLayout';
import DeleteConfirmModal from '@/components/common/DeleteConfirmModal';
import Button from '@/components/common/Button';
import useAuth from '@/hooks/useAuth';
import toast from 'react-hot-toast';

const RecipesPage: React.FC = () => {
  const [showForm, setShowForm] = useState(false);
  const [editingRecipe, setEditingRecipe] = useState<RecipeResponse | null>(null);
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
          setEditingRecipe(null);
          queryClient.invalidateQueries(['recipes']);
        },
        onError: (error: any) => {
          toast.error(error.response?.data?.message || '레시피 등록에 실패했습니다.');
        },
      }
  );

  // 레시피 수정 뮤테이션
  const updateRecipeMutation = useMutation(
      ({id, data}: { id: number; data: UpdateRecipeCommand }) =>
          recipeService.updateRecipe(id, data),
      {
        onSuccess: () => {
          toast.success('레시피가 성공적으로 수정되었습니다!');
          setShowForm(false);
          setEditingRecipe(null);
          queryClient.invalidateQueries(['recipes']);
        },
        onError: (error: any) => {
          toast.error(error.response?.data?.message || '레시피 수정에 실패했습니다.');
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

  const {requireAuth, requireOwnership, canPerformAction} = useAuth();

  const handleCreateRecipe = () => {
    requireAuth(() => {
      setEditingRecipe(null);
      setShowForm(true);
    }, '레시피 등록은 로그인이 필요합니다.');
  };

  const handleFormSubmit = (data: CreateRecipeCommand | UpdateRecipeCommand) => {
    if (editingRecipe) {
      updateRecipeMutation.mutate({
        id: editingRecipe.id,
        data: data as UpdateRecipeCommand
      });
    } else {
      createRecipeMutation.mutate(data as CreateRecipeCommand);
    }
  };

  const handleFormCancel = () => {
    setShowForm(false);
    setEditingRecipe(null);
  };

  const handleViewRecipe = (recipeSummary: RecipeSummaryResponse) => {
    navigate(`/recipes/${recipeSummary.id}`);
  };


  const handleEditRecipe = (recipe: RecipeSummaryResponse) => {
    requireOwnership({userId: recipe.userId}, async () => {
      // 전체 데이터를 가져오기 위해 API 호출
      try {
        const fullRecipe = await recipeService.getRecipe(recipe.id);
        setEditingRecipe(fullRecipe);
        setShowForm(true);
      } catch (error) {
        toast.error('레시피 데이터를 불러오는데 실패했습니다.');
      }
    }, '본인이 등록한 레시피만 수정할 수 있습니다.');
  };

  const handleDeleteRecipe = (recipe: RecipeSummaryResponse) => {
    requireOwnership({userId: recipe.userId}, () => {
      setDeleteRecipe(recipe);
    }, '본인이 등록한 레시피만 삭제할 수 있습니다.');
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
        <FormLayout
            title={editingRecipe ? '레시피 수정' : '새 레시피 등록'}
            subtitle={editingRecipe ? '기존 레시피 정보를 수정할 수 있습니다.' : '나만의 특별한 커피 레시피를 공유해보세요'}
            onBack={handleFormCancel}
            onCancel={handleFormCancel}
            onSubmit={() => {
            }} // 폼 내부에서 처리
            submitLabel={editingRecipe ? '레시피 수정' : '레시피 저장'}
            isLoading={createRecipeMutation.isLoading || updateRecipeMutation.isLoading}
        >
          <RecipeForm
              initialData={editingRecipe || undefined}
              onSubmit={handleFormSubmit}
              onCancel={handleFormCancel}
              loading={createRecipeMutation.isLoading || updateRecipeMutation.isLoading}
              isEdit={!!editingRecipe}
              showButtons={false}
          />
        </FormLayout>
    );
  }

  // 에러 처리
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

  // 메인 레시피 목록 화면
  return (
      <>
        <PageLayout
            title="레시피 북"
            subtitle="나만의 커피 레시피를 저장하고 관리해보세요"
            searchPlaceholder="레시피 제목이나 태그를 검색하세요..."
            onAddNew={canPerformAction('create') ? handleCreateRecipe : undefined}
            onSearch={handleSearch}
            addButtonLabel="레시피 추가"
        >
          {/* Recipe List */}
          <RecipeList
              recipes={recipesData?.content || []}
              loading={isLoading}
              onView={handleViewRecipe}
              onEdit={handleEditRecipe}
              onDelete={handleDeleteRecipe}
              actionsPosition="dropdown"
              loadingRecipeId={null}
          />

          {/* Pagination */}
          {recipesData && recipesData.content.length > 0 && (
              <div className="mt-8 flex justify-center">
                <div className="text-sm text-gray-500">
                  총 {recipesData.content.length}개의 레시피
                </div>
              </div>
          )}
        </PageLayout>

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
