import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { Users, Clock, Tag, ChefHat, ShoppingCart, ExternalLink, Lightbulb } from 'lucide-react';
import toast from 'react-hot-toast';
import { recipeService } from '@/services/recipeService';
import EntityDetailLayout from '@/components/common/EntityDetailLayout';
import Button from '@/components/common/Button';
import Card, { CardHeader, CardTitle, CardContent } from '@/components/common/Card';
import { formatDateTime, getCategoryTypeKorean } from '@/utils/format';

const RecipeDetailPage: React.FC = () => {
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

  const deleteMutation = useMutation(
    () => recipeService.deleteRecipe(Number(id)),
    {
      onSuccess: () => {
        toast.success('레시피가 성공적으로 삭제되었습니다!');
        queryClient.invalidateQueries(['recipes']);
        navigate('/recipes');
      },
      onError: (error: any) => {
        console.error('레시피 삭제 실패:', error);
        toast.error(error.response?.data?.message || '레시피 삭제에 실패했습니다.');
      },
    }
  );

  const handleBack = () => {
    navigate('/recipes');
  };

  const handleEdit = () => {
    navigate(`/recipes/${id}/edit`);
  };

  const handleDelete = () => {
    deleteMutation.mutate();
  };

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: `${recipe?.title} - CoffeeMedia`,
        text: `${recipe?.title} 레시피를 확인해보세요.`,
        url: window.location.href,
      });
    } else {
      navigator.clipboard.writeText(window.location.href);
      toast.success('링크가 클립보드에 복사되었습니다.');
    }
  };

  const handleFavorite = () => {
    toast.success('즐겨찾기 기능은 곧 구현됩니다!');
  };

  const sidebarContent = recipe ? (
    <div className="space-y-6">
      {/* 레시피 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>레시피 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="bg-gray-50 rounded-lg p-4">
            <div className="flex items-center mb-2">
              <Users className="h-4 w-4 text-gray-600 mr-2" />
              <span className="text-sm text-gray-500">인분</span>
            </div>
            <p className="font-bold text-gray-900 text-xl">{recipe.serving}인분</p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <div className="flex items-center mb-2">
              <Clock className="h-4 w-4 text-gray-600 mr-2" />
              <span className="text-sm text-gray-500">등록일</span>
            </div>
            <p className="font-medium text-gray-900">
              {formatDateTime(recipe.createdAt)}
            </p>
          </div>
        </CardContent>
      </Card>

      {/* 퀵 액션 */}
      <Card>
        <CardHeader>
          <CardTitle>퀵 액션</CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          <Button
            variant="outline"
            className="w-full justify-start"
            onClick={() => toast.success('요리 시작 기능은 곧 구현됩니다!')}
          >
            <ChefHat className="h-4 w-4 mr-2" />
            요리 시작하기
          </Button>
          <Button
            variant="outline"
            className="w-full justify-start"
            onClick={() => toast.success('장바구니 기능은 곧 구현됩니다!')}
          >
            <ShoppingCart className="h-4 w-4 mr-2" />
            재료 장바구니에 추가
          </Button>
        </CardContent>
      </Card>
    </div>
  ) : null;

  return (
    <EntityDetailLayout
      title={recipe?.title || ''}
      subtitle={recipe ? `${recipe.serving}인분 • ${formatDateTime(recipe.createdAt)} 등록` : ''}
      status={recipe ? {
        label: recipe.status === 'ACTIVE' ? '활성' : '비활성',
        variant: recipe.status === 'ACTIVE' ? 'success' : 'inactive'
      } : undefined}
      badges={recipe ? [
        {
          label: getCategoryTypeKorean(recipe.category),
          variant: 'primary'
        }
      ] : []}
      thumbnailUrl={recipe?.thumbnailUrl}
      thumbnailAlt={recipe?.title}
      fallbackIcon={<ChefHat className="h-16 w-16 text-gray-300" />}
      onBack={handleBack}
      onEdit={handleEdit}
      onDelete={handleDelete}
      onShare={handleShare}
      onFavorite={handleFavorite}
      deleteConfirmTitle="레시피 삭제 확인"
      deleteConfirmMessage="정말로 이 레시피를 삭제하시겠습니까? 삭제된 레시피는 복구할 수 없습니다."
      isDeleting={deleteMutation.isLoading}
      isLoading={isLoading}
      error={error}
      sidebarContent={sidebarContent}
    >
      {recipe && (
        <div className="space-y-6">
          {/* 설명과 태그 */}
          {(recipe.description || (recipe.tags && recipe.tags.length > 0)) && (
            <Card>
              <CardContent className="space-y-4">
                {recipe.description && (
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">설명</h3>
                    <p className="text-gray-700 leading-relaxed">{recipe.description}</p>
                  </div>
                )}

                {recipe.tags && recipe.tags.length > 0 && (
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-3">태그</h3>
                    <div className="flex flex-wrap gap-2">
                      {recipe.tags.map((tag, index) => (
                        <span
                          key={index}
                          className="inline-flex items-center px-3 py-1 text-sm bg-gray-100 text-gray-700 rounded-full"
                        >
                          <Tag className="h-3 w-3 mr-1" />
                          {tag}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>
          )}

          {/* 재료 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center">
                <ShoppingCart className="h-5 w-5 mr-2" />
                재료 ({recipe.ingredients.length}개)
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                {recipe.ingredients.map((ingredient, index) => (
                  <div
                    key={index}
                    className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                  >
                    <div className="flex-1">
                      <span className="font-medium text-gray-900">
                        {ingredient.name}
                      </span>
                      <div className="text-sm text-gray-600">
                        {ingredient.amount} {ingredient.unit}
                      </div>
                    </div>
                    {ingredient.buyUrl && (
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => window.open(ingredient.buyUrl, '_blank')}
                        rightIcon={<ExternalLink className="h-3 w-3" />}
                      >
                        구매
                      </Button>
                    )}
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* 조리 단계 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center">
                <ChefHat className="h-5 w-5 mr-2" />
                조리 단계 ({recipe.steps.length}단계)
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                {recipe.steps.map((step, index) => (
                  <div key={index} className="flex gap-4">
                    {/* 단계 번호 */}
                    <div className="flex-shrink-0">
                      <div className="w-8 h-8 bg-coffee-600 text-white rounded-full flex items-center justify-center font-semibold text-sm">
                        {index + 1}
                      </div>
                    </div>

                    {/* 단계 내용 */}
                    <div className="flex-1 space-y-3">
                      {step.imageUrl && (
                        <div className="w-full max-w-md">
                          <img
                            src={step.imageUrl}
                            alt={`단계 ${index + 1}`}
                            className="w-full h-48 object-cover rounded-lg"
                            onError={(e) => {
                              e.currentTarget.style.display = 'none';
                            }}
                          />
                        </div>
                      )}
                      <div className="prose prose-sm max-w-none">
                        <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                          {step.description}
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* 팁 */}
          {recipe.tips && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <Lightbulb className="h-5 w-5 mr-2" />
                  레시피 팁
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                  <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                    {recipe.tips}
                  </p>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      )}
    </EntityDetailLayout>
  );
};

export default RecipeDetailPage;
