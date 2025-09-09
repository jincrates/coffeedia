import React from 'react';
import { RecipeSummaryResponse } from '@/types/api';
import RecipeCard from './RecipeCard';

interface RecipeListProps {
  recipes: RecipeSummaryResponse[];
  loading?: boolean;
  loadingRecipeId?: number | null;
  onView?: (recipe: RecipeSummaryResponse) => void;
  onEdit?: (recipe: RecipeSummaryResponse) => void;
  onDelete?: (recipe: RecipeSummaryResponse) => void;
  actionsPosition?: 'dropdown' | 'buttons';
}

const RecipeList: React.FC<RecipeListProps> = ({
  recipes,
  loading = false,
  loadingRecipeId = null,
  onView,
  onEdit,
  onDelete,
  actionsPosition = 'dropdown',
}) => {
  if (loading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {Array.from({ length: 6 }).map((_, index) => (
          <div
            key={index}
            className="animate-pulse bg-gray-200 rounded-lg h-80"
          />
        ))}
      </div>
    );
  }

  if (recipes.length === 0) {
    return (
      <div className="text-center py-12">
        <div className="text-gray-400 text-lg mb-2">등록된 레시피가 없습니다</div>
        <div className="text-gray-500 text-sm">
          첫 번째 레시피를 등록해보세요!
        </div>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {recipes.map((recipe) => (
        <RecipeCard
          key={recipe.id}
          recipe={recipe}
          onView={onView}
          onEdit={onEdit}
          onDelete={onDelete}
          actionsPosition={actionsPosition}
          loading={loadingRecipeId === recipe.id}
        />
      ))}
    </div>
  );
};

export default RecipeList;
