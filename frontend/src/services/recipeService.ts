import api from './api';
import {
  BaseResponse,
  PageResponse,
  RecipeResponse,
  RecipeSummaryResponse,
  CreateRecipeCommand,
  RecipeSearchQuery,
} from '@/types/api';

class RecipeService {
  private readonly BASE_URL = '/recipes';

  // Recipe 생성
  async createRecipe(command: CreateRecipeCommand): Promise<RecipeResponse> {
    const response = await api.post<BaseResponse<RecipeResponse>>(this.BASE_URL, command);
    return response.data.data;
  }

  // Recipe 목록 조회 (페이징) - Summary만 반환
  async getAllRecipes(query: RecipeSearchQuery = {}): Promise<PageResponse<RecipeSummaryResponse>> {
    const { page = 0, size = 10, sort } = query;
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    
    if (sort) {
      params.append('sort', sort);
    }

    const response = await api.get<BaseResponse<PageResponse<RecipeSummaryResponse>>>(
      `${this.BASE_URL}?${params.toString()}`
    );
    return response.data.data;
  }

  // Recipe 상세 조회 (향후 구현 예정)
  async getRecipe(recipeId: number): Promise<RecipeResponse> {
    const response = await api.get<BaseResponse<RecipeResponse>>(`${this.BASE_URL}/${recipeId}`);
    return response.data.data;
  }
}

export const recipeService = new RecipeService();
