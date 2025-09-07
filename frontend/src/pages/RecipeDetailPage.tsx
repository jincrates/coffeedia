import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from 'react-query';
import { ArrowLeft, Loader2, Users, Clock, Tag, ChefHat, ShoppingCart, ExternalLink, Lightbulb } from 'lucide-react';
import { recipeService } from '@/services/recipeService';
import Button from '@/components/common/Button';
import Card, { CardHeader, CardTitle, CardContent } from '@/components/common/Card';
import { formatDateTime, getCategoryTypeKorean } from '@/utils/format';

const RecipeDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

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
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
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
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <div className="flex items-center gap-3 mb-4">
            <h1 className="text-3xl font-bold text-gray-900">{recipe.title}</h1>
            <span className="inline-flex px-3 py-1 text-sm font-medium bg-blue-100 text-blue-800 rounded-full">
              {getCategoryTypeKorean(recipe.category)}
            </span>
          </div>
          <div className="flex items-center text-sm text-gray-500 space-x-4">
            <div className="flex items-center">
              <Users className="h-4 w-4 mr-1" />
              <span>{recipe.serving}인분</span>
            </div>
            <div className="flex items-center">
              <Clock className="h-4 w-4 mr-1" />
              <span>{formatDateTime(recipe.createdAt)}</span>
            </div>
          </div>
        </div>

        {/* 컨텐츠 */}
        <div className="space-y-6">
          {/* 썸네일과 설명 */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* 썸네일 */}
            <Card>
              <CardContent>
                {recipe.thumbnailUrl ? (
                  <div className="aspect-video rounded-lg overflow-hidden bg-gray-100">
                    <img
                      src={recipe.thumbnailUrl}
                      alt={recipe.title}
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.currentTarget.style.display = 'none';
                        e.currentTarget.nextElementSibling?.classList.remove('hidden');
                      }}
                    />
                    <div className="hidden aspect-video rounded-lg bg-gray-100 flex items-center justify-center">
                      <span className="text-gray-400">이미지 없음</span>
                    </div>
                  </div>
                ) : (
                  <div className="aspect-video rounded-lg bg-gray-100 flex items-center justify-center">
                    <ChefHat className="h-16 w-16 text-gray-300" />
                  </div>
                )}
              </CardContent>
            </Card>

            {/* 설명과 태그 */}
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
          </div>

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
      </div>
    </div>
  );
};

export default RecipeDetailPage;
