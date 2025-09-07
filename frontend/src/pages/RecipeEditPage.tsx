import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { ArrowLeft, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { recipeService } from '@/services/recipeService';
import { UpdateRecipeCommand } from '@/types/api';
import Button from '@/components/common/Button';
import RecipeForm from '@/components/recipes/RecipeForm';

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

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="h-8 w-8 animate-spin mx-auto mb-4 text-coffee-600" />
          <p className="text-gray-600">레시피를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  if (error || !recipe) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-semibold text-gray-900 mb-2">
            레시피를 찾을 수 없습니다
          </h2>
          <p className="text-gray-600 mb-4">
            요청하신 레시피가 존재하지 않거나 삭제되었습니다.
          </p>
          <Button onClick={handleBack} leftIcon={<ArrowLeft className="h-4 w-4" />}>
            목록으로 돌아가기
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* 뒤로가기 버튼 */}
        <Button
          variant="ghost"
          onClick={handleBack}
          leftIcon={<ArrowLeft className="h-4 w-4" />}
          className="mb-6"
        >
          레시피 목록으로
        </Button>

        {/* 헤더 */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">레시피 수정</h1>
          <p className="text-gray-600 mt-2">
            기존 레시피 정보를 수정할 수 있습니다.
          </p>
        </div>

        {/* 수정 폼 */}
        <RecipeForm
          initialData={recipe}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          loading={updateMutation.isLoading}
          isEdit={true}
        />
      </div>
    </div>
  );
};

export default RecipeEditPage;
