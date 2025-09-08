import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import toast from 'react-hot-toast';
import { recipeService } from '@/services/recipeService';
import { UpdateRecipeCommand } from '@/types/api';
import RecipeForm from '@/components/recipes/RecipeForm';
import FormLayout from '@/components/common/FormLayout';

const RecipeEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const {
    data: recipe,
    isLoading,
    error,
  } = useQuery(
    ['recipe', id],
    () => recipeService.getRecipe(Number(id)),
    {
      enabled: !!id,
    }
  );

  const updateMutation = useMutation(
    (data: UpdateRecipeCommand) => recipeService.updateRecipe(Number(id), data),
    {
      onSuccess: (updatedRecipe) => {
        toast.success('레시피가 성공적으로 수정되었습니다!');
        queryClient.invalidateQueries(['recipe', id]);
        queryClient.invalidateQueries(['recipes']);
        navigate(`/recipes/${updatedRecipe.id}`);
      },
      onError: (error: any) => {
        console.error('레시피 수정 실패:', error);
        toast.error(error.response?.data?.message || '레시피 수정에 실패했습니다.');
      },
    }
  );

  const handleSubmit = (data: UpdateRecipeCommand) => {
    updateMutation.mutate(data);
  };

  const handleCancel = () => {
    navigate(`/recipes/${id}`);
  };

  const handleBack = () => {
    navigate('/recipes');
  };

  if (error || !recipe) {
    return (
      <FormLayout
        title="레시피를 찾을 수 없습니다"
        subtitle="요청하신 레시피가 존재하지 않거나 삭제되었습니다."
        onBack={handleBack}
        onCancel={handleBack}
        onSubmit={() => {}}
        submitLabel="목록으로"
      >
        <div className="text-center py-12">
          <p className="text-gray-600">
            다른 레시피를 찾아보세요.
          </p>
        </div>
      </FormLayout>
    );
  }

  if (isLoading) {
    return (
      <FormLayout
        title="레시피 수정"
        subtitle="레시피 정보를 불러오는 중..."
        onBack={handleBack}
        onCancel={handleCancel}
        onSubmit={() => {}}
        submitLabel="저장"
      >
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-coffee-600 mx-auto mb-4"></div>
            <p className="text-gray-600">레시피를 불러오는 중...</p>
          </div>
        </div>
      </FormLayout>
    );
  }

  return (
    <FormLayout
      title="레시피 수정"
      subtitle={`${recipe.title}의 정보를 수정해보세요`}
      onBack={handleBack}
      onCancel={handleCancel}
      onSubmit={() => {}} // 폼 내부에서 처리
      submitLabel="레시피 수정"
      isLoading={updateMutation.isLoading}
    >
      <RecipeForm
        initialData={recipe}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        loading={updateMutation.isLoading}
        isEdit={true}
        showButtons={false}
      />
    </FormLayout>
  );
};

export default RecipeEditPage;
