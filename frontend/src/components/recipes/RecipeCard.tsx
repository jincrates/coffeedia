import React from 'react';
import { RecipeSummaryResponse } from '@/types/api';
import Card, { CardContent, CardFooter } from '@/components/common/Card';
import Button from '@/components/common/Button';
import { Eye, Users, Clock, Tag, Loader2 } from 'lucide-react';
import { formatRelativeDate, getCategoryTypeKorean } from '@/utils/format';

interface RecipeCardProps {
  recipe: RecipeSummaryResponse;
  loading?: boolean;
  onView?: (recipe: RecipeSummaryResponse) => void;
}

const RecipeCard: React.FC<RecipeCardProps> = ({ recipe, loading = false, onView }) => {
  const handleView = () => {
    if (!loading) {
      onView?.(recipe);
    }
  };

  return (
    <Card hover={!!onView && !loading} onClick={handleView} className="h-full">
      <CardContent className="space-y-3">
        {/* 썸네일 이미지 */}
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
              <span className="text-gray-400 text-sm">이미지 없음</span>
            </div>
          </div>
        ) : (
          <div className="aspect-video rounded-lg bg-gray-100 flex items-center justify-center">
            <span className="text-gray-400 text-sm">이미지 없음</span>
          </div>
        )}

        {/* 제목과 카테고리 */}
        <div>
          <div className="flex justify-between items-start mb-2">
            <h3 className="font-semibold text-lg text-gray-900 line-clamp-2">
              {recipe.title}
            </h3>
            <span className="inline-flex px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded-full ml-2 flex-shrink-0">
              {getCategoryTypeKorean(recipe.category)}
            </span>
          </div>
          
          {recipe.description && (
            <p className="text-sm text-gray-600 line-clamp-2">
              {recipe.description}
            </p>
          )}
        </div>

        {/* 메타 정보 */}
        <div className="flex items-center text-sm text-gray-500 space-x-4">
          <div className="flex items-center">
            <Users className="h-4 w-4 mr-1" />
            <span>{recipe.serving}인분</span>
          </div>
          <div className="flex items-center">
            <Clock className="h-4 w-4 mr-1" />
            <span>{formatRelativeDate(recipe.createdAt)}</span>
          </div>
        </div>

        {/* 태그 */}
        {recipe.tags && recipe.tags.length > 0 && (
          <div className="flex flex-wrap gap-1">
            <Tag className="h-3 w-3 text-gray-400 mt-1" />
            {recipe.tags.slice(0, 3).map((tag, index) => (
              <span
                key={index}
                className="inline-flex px-2 py-1 text-xs bg-gray-100 text-gray-700 rounded-full"
              >
                {tag}
              </span>
            ))}
            {recipe.tags.length > 3 && (
              <span className="inline-flex px-2 py-1 text-xs bg-gray-100 text-gray-600 rounded-full">
                +{recipe.tags.length - 3}
              </span>
            )}
          </div>
        )}
      </CardContent>

      {onView && (
        <CardFooter>
          <Button
            variant="outline"
            size="sm"
            onClick={handleView}
            disabled={loading}
            leftIcon={loading ? <Loader2 className="h-4 w-4 animate-spin" /> : <Eye className="h-4 w-4" />}
            className="w-full"
          >
            {loading ? '로딩 중...' : '레시피 보기'}
          </Button>
        </CardFooter>
      )}
    </Card>
  );
};

export default RecipeCard;
