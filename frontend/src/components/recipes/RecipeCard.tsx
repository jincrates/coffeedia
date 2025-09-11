import React from 'react';
import {RecipeSummaryResponse} from '@/types/api';
import Card, {CardContent, CardFooter} from '@/components/common/Card';
import CardActions from '@/components/common/CardActions';
import useAuth from '@/hooks/useAuth';
import {ChefHat, Clock, Tag, Users} from 'lucide-react';
import {formatRelativeDate, getCategoryTypeKorean} from '@/utils/format';

interface RecipeCardProps {
  recipe: RecipeSummaryResponse;
  loading?: boolean;
  onView?: (recipe: RecipeSummaryResponse) => void;
  onEdit?: (recipe: RecipeSummaryResponse) => void;
  onDelete?: (recipe: RecipeSummaryResponse) => void;
  actionsPosition?: 'dropdown' | 'buttons';
}

const RecipeCard: React.FC<RecipeCardProps> = ({
                                                 recipe,
                                                 loading = false,
                                                 onView,
                                                 onEdit,
                                                 onDelete,
                                                 actionsPosition = 'dropdown'
                                               }) => {
  const {canPerformAction} = useAuth();

  const handleView = () => {
    if (!loading && onView) {
      onView(recipe);
    }
  };

  const actions = [];

  if (onView) {
    actions.push({
      type: 'view' as const,
      label: '상세보기',
      onClick: () => onView(recipe),
      disabled: loading,
    });
  }

  // 수정 버튼: 소유자만 표시
  if (onEdit && canPerformAction('edit', {userId: recipe.userId})) {
    actions.push({
      type: 'edit' as const,
      label: '수정',
      onClick: () => onEdit(recipe),
      disabled: loading,
    });
  }

  // 삭제 버튼: 소유자만 표시
  if (onDelete && canPerformAction('delete', {userId: recipe.userId})) {
    actions.push({
      type: 'delete' as const,
      label: '삭제',
      onClick: () => onDelete(recipe),
      variant: 'destructive' as const,
      disabled: loading,
    });
  }

  return (
      <Card hover={!!onView && !loading} onClick={onView ? handleView : undefined}
            className="h-full">
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
                <div
                    className="hidden aspect-video rounded-lg bg-gray-100 flex items-center justify-center">
                  <span className="text-gray-400 text-sm">이미지 없음</span>
                </div>
              </div>
          ) : (
              <div className="aspect-video rounded-lg bg-gray-100 flex items-center justify-center">
                <ChefHat className="h-16 w-16 text-gray-300"/>
              </div>
          )}

          {/* 제목과 카테고리 */}
          <div>
            <div className="flex justify-between items-start mb-2">
              <h3 className="font-semibold text-lg text-gray-900 line-clamp-2 flex-1">
                {recipe.title}
              </h3>
              <div className="flex items-center gap-2 ml-2">
              <span
                  className="inline-flex px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded-full flex-shrink-0">
                {getCategoryTypeKorean(recipe.category)}
              </span>
                {/* 액션 메뉴 */}
                {actions.length > 0 && actionsPosition === 'dropdown' && (
                    <CardActions actions={actions} position="dropdown"/>
                )}
              </div>
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
              <Users className="h-4 w-4 mr-1"/>
              <span>{recipe.serving}인분</span>
            </div>
            <div className="flex items-center">
              <Clock className="h-4 w-4 mr-1"/>
              <span>{formatRelativeDate(recipe.createdAt)}</span>
            </div>
          </div>

          {/* 태그 */}
          {recipe.tags && recipe.tags.length > 0 && (
              <div className="flex flex-wrap gap-1">
                <Tag className="h-3 w-3 text-gray-400 mt-1"/>
                {recipe.tags.slice(0, 3).map((tag, index) => (
                    <span
                        key={index}
                        className="inline-flex px-2 py-1 text-xs bg-gray-100 text-gray-700 rounded-full"
                    >
                {tag}
              </span>
                ))}
                {recipe.tags.length > 3 && (
                    <span
                        className="inline-flex px-2 py-1 text-xs bg-gray-100 text-gray-600 rounded-full">
                +{recipe.tags.length - 3}
              </span>
                )}
              </div>
          )}
        </CardContent>

        {actions.length > 0 && actionsPosition === 'buttons' && (
            <CardFooter>
              <CardActions actions={actions} position="buttons"/>
            </CardFooter>
        )}
      </Card>
  );
};

export default RecipeCard;
