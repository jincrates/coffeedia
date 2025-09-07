import React from 'react';
import { useForm, useFieldArray } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Plus, Minus, Upload } from 'lucide-react';
import { CreateRecipeCommand, UpdateRecipeCommand, CategoryType, RecipeResponse } from '@/types/api';
import Button from '@/components/common/Button';
import Input from '@/components/common/Input';
import Card, { CardHeader, CardTitle, CardContent } from '@/components/common/Card';
import { getCategoryTypeKorean } from '@/utils/format';

// 유효성 검사 스키마
const recipeSchema = yup.object({
  category: yup.string().required('카테고리를 선택해주세요'),
  title: yup.string().required('제목을 입력해주세요'),
  description: yup.string().max(500, '상세 설명은 500자까지만 가능합니다'),
  serving: yup.number().min(1, '1인분 이상이어야 합니다').required('인분을 입력해주세요'),
  tags: yup.array().of(yup.string()).max(5, '태그는 최대 5개까지만 가능합니다'),
  ingredients: yup.array().of(
    yup.object({
      name: yup.string().required('재료명을 입력해주세요'),
      amount: yup.number().min(0, '양은 0 이상이어야 합니다').required('양을 입력해주세요'),
      unit: yup.string().required('단위를 입력해주세요'),
      buyUrl: yup.string().url('올바른 URL을 입력해주세요'),
    })
  ).min(1, '재료를 최소 1개 이상 입력해주세요'),
  steps: yup.array().of(
    yup.object({
      description: yup.string().required('단계 설명을 입력해주세요'),
      imageUrl: yup.string().url('올바른 URL을 입력해주세요'),
    })
  ).min(1, '단계를 최소 1개 이상 입력해주세요'),
  tips: yup.string().max(200, '레시피 팁은 200자까지만 가능합니다'),
});

interface RecipeFormProps {
  onSubmit: (data: CreateRecipeCommand | UpdateRecipeCommand) => void;
  onCancel: () => void;
  loading?: boolean;
  initialData?: RecipeResponse;
  isEdit?: boolean;
}

const RecipeForm: React.FC<RecipeFormProps> = ({ 
  onSubmit, 
  onCancel, 
  loading = false, 
  initialData,
  isEdit = false 
}) => {
  const {
    register,
    control,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<CreateRecipeCommand | UpdateRecipeCommand>({
    resolver: yupResolver(recipeSchema),
    defaultValues: initialData ? {
      category: initialData.category,
      title: initialData.title,
      thumbnailUrl: initialData.thumbnailUrl || '',
      description: initialData.description || '',
      serving: initialData.serving,
      tags: initialData.tags || [],
      ingredients: initialData.ingredients.length > 0 ? initialData.ingredients : [{ name: '', amount: 0, unit: '', buyUrl: '' }],
      steps: initialData.steps.length > 0 ? initialData.steps : [{ description: '', imageUrl: '' }],
      tips: initialData.tips || '',
    } : {
      category: CategoryType.BREW,
      serving: 1,
      tags: [],
      ingredients: [{ name: '', amount: 0, unit: '', buyUrl: '' }],
      steps: [{ description: '', imageUrl: '' }],
    },
  });

  const {
    fields: ingredientFields,
    append: appendIngredient,
    remove: removeIngredient,
  } = useFieldArray({
    control,
    name: 'ingredients',
  });

  const {
    fields: stepFields,
    append: appendStep,
    remove: removeStep,
  } = useFieldArray({
    control,
    name: 'steps',
  });

  const [tagInput, setTagInput] = React.useState('');
  const tags = watch('tags') || [];

  const handleAddTag = () => {
    if (tagInput.trim() && tags.length < 5 && !tags.includes(tagInput.trim())) {
      setValue('tags', [...tags, tagInput.trim()]);
      setTagInput('');
    }
  };

  const handleRemoveTag = (index: number) => {
    setValue('tags', tags.filter((_, i) => i !== index));
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleAddTag();
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* 기본 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>기본 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* 카테고리 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              카테고리 *
            </label>
            <select
              {...register('category')}
              className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
            >
              {Object.values(CategoryType).map((category) => (
                <option key={category} value={category}>
                  {getCategoryTypeKorean(category)}
                </option>
              ))}
            </select>
            {errors.category && (
              <p className="mt-1 text-sm text-red-600">{errors.category.message}</p>
            )}
          </div>

          {/* 제목 */}
          <Input
            label="제목 *"
            {...register('title')}
            error={errors.title?.message}
            placeholder="레시피 제목을 입력하세요"
          />

          {/* 썸네일 URL */}
          <Input
            label="썸네일 이미지 URL"
            {...register('thumbnailUrl')}
            error={errors.thumbnailUrl?.message}
            placeholder="https://example.com/image.jpg"
            rightIcon={<Upload className="h-4 w-4" />}
          />

          {/* 설명 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              상세 설명
            </label>
            <textarea
              {...register('description')}
              rows={3}
              className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
              placeholder="레시피에 대한 설명을 입력하세요"
            />
            {errors.description && (
              <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>
            )}
          </div>

          {/* 인분 */}
          <Input
            label="인분 *"
            type="number"
            min="1"
            {...register('serving', { valueAsNumber: true })}
            error={errors.serving?.message}
            placeholder="1"
          />

          {/* 태그 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              태그 (최대 5개)
            </label>
            <div className="flex gap-2 mb-2">
              <Input
                value={tagInput}
                onChange={(e) => setTagInput(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="태그를 입력하고 Enter를 누르세요"
                className="flex-1"
              />
              <Button type="button" onClick={handleAddTag} disabled={tags.length >= 5}>
                추가
              </Button>
            </div>
            {tags.length > 0 && (
              <div className="flex flex-wrap gap-2">
                {tags.map((tag, index) => (
                  <span
                    key={index}
                    className="inline-flex items-center px-2 py-1 text-xs bg-coffee-100 text-coffee-800 rounded-full"
                  >
                    {tag}
                    <button
                      type="button"
                      onClick={() => handleRemoveTag(index)}
                      className="ml-1 text-coffee-600 hover:text-coffee-800"
                    >
                      ×
                    </button>
                  </span>
                ))}
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 재료 */}
      <Card>
        <CardHeader>
          <div className="flex justify-between items-center">
            <CardTitle>재료</CardTitle>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => appendIngredient({ name: '', amount: 0, unit: '', buyUrl: '' })}
              leftIcon={<Plus className="h-4 w-4" />}
            >
              재료 추가
            </Button>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {ingredientFields.map((field, index) => (
            <div key={field.id} className="border rounded-lg p-4">
              <div className="flex justify-between items-center mb-3">
                <h4 className="font-medium">재료 {index + 1}</h4>
                {ingredientFields.length > 1 && (
                  <Button
                    type="button"
                    variant="destructive"
                    size="sm"
                    onClick={() => removeIngredient(index)}
                    leftIcon={<Minus className="h-4 w-4" />}
                  >
                    삭제
                  </Button>
                )}
              </div>
              <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
                <Input
                  label="재료명 *"
                  {...register(`ingredients.${index}.name`)}
                  error={errors.ingredients?.[index]?.name?.message}
                  placeholder="예: 원두"
                />
                <Input
                  label="양 *"
                  type="number"
                  step="0.1"
                  min="0"
                  {...register(`ingredients.${index}.amount`, { valueAsNumber: true })}
                  error={errors.ingredients?.[index]?.amount?.message}
                  placeholder="20"
                />
                <Input
                  label="단위 *"
                  {...register(`ingredients.${index}.unit`)}
                  error={errors.ingredients?.[index]?.unit?.message}
                  placeholder="g"
                />
                <Input
                  label="구매 URL"
                  {...register(`ingredients.${index}.buyUrl`)}
                  error={errors.ingredients?.[index]?.buyUrl?.message}
                  placeholder="https://..."
                />
              </div>
            </div>
          ))}
        </CardContent>
      </Card>

      {/* 조리 단계 */}
      <Card>
        <CardHeader>
          <div className="flex justify-between items-center">
            <CardTitle>조리 단계</CardTitle>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => appendStep({ description: '', imageUrl: '' })}
              leftIcon={<Plus className="h-4 w-4" />}
            >
              단계 추가
            </Button>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {stepFields.map((field, index) => (
            <div key={field.id} className="border rounded-lg p-4">
              <div className="flex justify-between items-center mb-3">
                <h4 className="font-medium">단계 {index + 1}</h4>
                {stepFields.length > 1 && (
                  <Button
                    type="button"
                    variant="destructive"
                    size="sm"
                    onClick={() => removeStep(index)}
                    leftIcon={<Minus className="h-4 w-4" />}
                  >
                    삭제
                  </Button>
                )}
              </div>
              <div className="space-y-3">
                <Input
                  label="이미지 URL"
                  {...register(`steps.${index}.imageUrl`)}
                  error={errors.steps?.[index]?.imageUrl?.message}
                  placeholder="https://example.com/step-image.jpg"
                  rightIcon={<Upload className="h-4 w-4" />}
                />
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    단계 설명 *
                  </label>
                  <textarea
                    {...register(`steps.${index}.description`)}
                    rows={3}
                    className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
                    placeholder="이 단계에서 할 일을 자세히 설명해주세요"
                  />
                  {errors.steps?.[index]?.description && (
                    <p className="mt-1 text-sm text-red-600">
                      {errors.steps[index]?.description?.message}
                    </p>
                  )}
                </div>
              </div>
            </div>
          ))}
        </CardContent>
      </Card>

      {/* 팁 */}
      <Card>
        <CardHeader>
          <CardTitle>레시피 팁</CardTitle>
        </CardHeader>
        <CardContent>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              요리 팁 (선택사항)
            </label>
            <textarea
              {...register('tips')}
              rows={3}
              className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
              placeholder="레시피를 만들 때 도움이 되는 팁이나 주의사항을 적어주세요"
            />
            {errors.tips && (
              <p className="mt-1 text-sm text-red-600">{errors.tips.message}</p>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 버튼 */}
      <div className="flex justify-end space-x-3">
        <Button type="button" variant="outline" onClick={onCancel}>
          취소
        </Button>
        <Button type="submit" loading={loading}>
          {isEdit ? '레시피 수정' : '레시피 저장'}
        </Button>
      </div>
    </form>
  );
};

export default RecipeForm;
