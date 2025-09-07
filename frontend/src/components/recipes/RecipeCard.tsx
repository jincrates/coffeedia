import React from 'react';
import { RecipeSummaryResponse } from '@/types/api';
import Card, { CardContent, CardFooter } from '@/components/common/Card';
import Button from '@/components/common/Button';
import { Eye, Users, Clock, Tag, Loader2, Edit, Trash2, MoreVertical } from 'lucide-react';
import { formatRelativeDate, getCategoryTypeKorean } from '@/utils/format';

interface RecipeCardProps {
  recipe: RecipeSummaryResponse;
  loading?: boolean;
  onView?: (recipe: RecipeSummaryResponse) => void;
  onEdit?: (recipe: RecipeSummaryResponse) => void;
  onDelete?: (recipe: RecipeSummaryResponse) => void;
  showActions?: boolean;
}

const RecipeCard: React.FC<RecipeCardProps> = ({ 
  recipe, 
  loading = false, 
  onView,
  onEdit,
  onDelete,
  showActions = false 
}) => {
  const [showActionMenu, setShowActionMenu] = React.useState(false);

  React.useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (showActionMenu) {
        setShowActionMenu(false);
      }
    };

    if (showActionMenu) {
      document.addEventListener('click', handleClickOutside);
      return () => document.removeEventListener('click', handleClickOutside);
    }
  }, [showActionMenu]);

  const handleView = () => {
    if (!loading) {
      onView?.(recipe);
    }
  };

  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit?.(recipe);
    setShowActionMenu(false);
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    onDelete?.(recipe);
    setShowActionMenu(false);
  };

  const toggleActionMenu = (e: React.MouseEvent) => {
    e.stopPropagation();
    setShowActionMenu(!showActionMenu);
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
            <h3 className="font-semibold text-lg text-gray-900 line-clamp-2 flex-1">
              {recipe.title}
            </h3>
            <div className="flex items-center gap-2 ml-2">
              <span className="inline-flex px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded-full flex-shrink-0">
                {getCategoryTypeKorean(recipe.category)}
              </span>
              {/* 액션 메뉴 */}
              {showActions && (onEdit || onDelete) && (
                <div className="relative">
                  <button
                    onClick={toggleActionMenu}
                    className="p-1 hover:bg-gray-100 rounded-full transition-colors"
                  >
                    <MoreVertical className="h-4 w-4 text-gray-500" />
                  </button>
                  {showActionMenu && (
                    <div className="absolute right-0 top-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg z-10 min-w-[120px]">
                      {onEdit && (
                        <button
                          onClick={handleEdit}
                          className="w-full px-3 py-2 text-left text-sm text-gray-700 hover:bg-gray-50 flex items-center gap-2"
                        >
                          <Edit className="h-3 w-3" />
                          수정
                        </button>
                      )}
                      {onDelete && (
                        <button
                          onClick={handleDelete}
                          className="w-full px-3 py-2 text-left text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                        >
                          <Trash2 className="h-3 w-3" />
                          삭제
                        </button>
                      )}
                    </div>
                  )}
                </div>
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
          <div className="flex gap-2 w-full">
            <Button
              variant="outline"
              size="sm"
              onClick={handleView}
              disabled={loading}
              leftIcon={loading ? <Loader2 className="h-4 w-4 animate-spin" /> : <Eye className="h-4 w-4" />}
              className="flex-1"
            >
              {loading ? '로딩 중...' : '레시피 보기'}
            </Button>
            {showActions && onEdit && (
              <Button
                variant="ghost"
                size="sm"
                onClick={handleEdit}
                disabled={loading}
                leftIcon={<Edit className="h-4 w-4" />}
              >
                수정
              </Button>
            )}
          </div>
        </CardFooter>
      )}
    </Card>
  );
};

export default RecipeCard;
