import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { CreateBeanCommand, UpdateBeanCommand, RoastLevel, ProcessType, BlendType, ActiveStatus, BeanResponse } from '@/types/api';
import Button from '@/components/common/Button';
import Input from '@/components/common/Input';
import ImagePreview from '@/components/common/ImagePreview';
import Card, { CardHeader, CardTitle, CardContent } from '@/components/common/Card';
import { getRoastLevelKorean, getProcessTypeKorean, getBlendTypeKorean } from '@/utils/format';

// 유효성 검사 스키마
const beanSchema = yup.object({
  name: yup.string().required('원두 이름은 필수입니다.'),
  thumbnailUrl: yup.string().url('올바른 URL 형식이 아닙니다.'),
  origin: yup.object({
    country: yup.string().required('원산지 국가는 필수입니다.'),
    region: yup.string(),
    farm: yup.string(),
  }).required('원산지는 필수입니다.'),
  roaster: yup.string().required('로스터는 필수입니다.'),
  roastDate: yup.date().required('로스팅 일자는 필수입니다.'),
  grams: yup.number().min(0, '그램은 0g 이상이어야 합니다.').required('그램은 필수입니다.'),
  roastLevel: yup.string().required('로스팅 레벨을 선택해주세요.'),
  processType: yup.string().required('가공법을 선택해주세요.'),
  blendType: yup.string().required('블렌드 타입을 선택해주세요.'),
  isDecaf: yup.boolean(),
  memo: yup.string().max(500, '메모는 500자까지만 가능합니다.'),
  status: yup.string().required('상태를 선택해주세요.'),
});

interface BeanFormProps {
  initialData?: BeanResponse;
  onSubmit: (data: CreateBeanCommand | UpdateBeanCommand) => void;
  onCancel: () => void;
  loading?: boolean;
  mode?: 'create' | 'edit';
}

const BeanForm: React.FC<BeanFormProps> = ({ 
  initialData, 
  onSubmit, 
  onCancel, 
  loading = false,
  mode = 'create' 
}) => {
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<CreateBeanCommand>({
    resolver: yupResolver(beanSchema),
    defaultValues: initialData ? {
      name: initialData.name,
      thumbnailUrl: initialData.thumbnailUrl || '',
      origin: initialData.origin,
      roaster: initialData.roaster,
      roastDate: initialData.roastDate,
      grams: initialData.grams,
      roastLevel: initialData.roastLevel,
      processType: initialData.processType,
      blendType: initialData.blendType,
      isDecaf: initialData.isDecaf,
      memo: initialData.memo || '',
      status: initialData.status,
    } : {
      name: '',
      thumbnailUrl: '',
      origin: { country: '', region: '', farm: '' },
      roaster: '',
      roastDate: new Date().toISOString().split('T')[0],
      grams: 0,
      roastLevel: RoastLevel.MEDIUM,
      processType: ProcessType.WASHED,
      blendType: BlendType.SINGLE_ORIGIN,
      isDecaf: false,
      memo: '',
      status: ActiveStatus.ACTIVE,
    },
  });

  const isDecaf = watch('isDecaf');
  const thumbnailUrl = watch('thumbnailUrl');

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* 기본 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>기본 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* 원두 이름 */}
          <Input
            label="원두 이름 *"
            {...register('name')}
            error={errors.name?.message}
            placeholder="예: 에티오피아 예가체프"
          />

          {/* 썸네일 이미지 URL */}
          <div>
            <Input
              label="썸네일 이미지 URL"
              {...register('thumbnailUrl')}
              error={errors.thumbnailUrl?.message}
              placeholder="https://example.com/image.jpg"
            />
            {/* 이미지 미리보기 */}
            {thumbnailUrl && (
              <div className="mt-2">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  미리보기
                </label>
                <ImagePreview
                  url={thumbnailUrl}
                  alt={watch('name') || '원두 이미지'}
                  className="w-full h-48"
                />
              </div>
            )}
          </div>

          {/* 로스터 */}
          <Input
            label="로스터 *"
            {...register('roaster')}
            error={errors.roaster?.message}
            placeholder="예: 블루보틀, 스타벅스"
          />

          {/* 로스팅 일자 */}
          <Input
            label="로스팅 일자 *"
            type="date"
            {...register('roastDate')}
            error={errors.roastDate?.message}
          />

          {/* 그램 */}
          <Input
            label="무게 (g) *"
            type="number"
            min="0"
            step="1"
            {...register('grams', { valueAsNumber: true })}
            error={errors.grams?.message}
            placeholder="250"
          />

          {/* 디카페인 여부 */}
          <div className="flex items-center space-x-2">
            <input
              type="checkbox"
              id="isDecaf"
              {...register('isDecaf')}
              className="h-4 w-4 text-coffee-600 border-gray-300 rounded focus:ring-coffee-500"
            />
            <label htmlFor="isDecaf" className="text-sm font-medium text-gray-700">
              디카페인
            </label>
          </div>

          {/* 상태 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              상태 *
            </label>
            <select
              {...register('status')}
              className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
            >
              <option value={ActiveStatus.ACTIVE}>활성</option>
              <option value={ActiveStatus.INACTIVE}>비활성</option>
            </select>
            {errors.status && (
              <p className="mt-1 text-sm text-red-600">{errors.status.message}</p>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 원산지 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>원산지 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* 국가 */}
          <Input
            label="국가 *"
            {...register('origin.country')}
            error={errors.origin?.country?.message}
            placeholder="예: 에티오피아, 케냐, 브라질"
          />

          {/* 지역 */}
          <Input
            label="지역"
            {...register('origin.region')}
            error={errors.origin?.region?.message}
            placeholder="예: 예가체프, 시다모"
          />

          {/* 농장 */}
          <Input
            label="농장"
            {...register('origin.farm')}
            error={errors.origin?.farm?.message}
            placeholder="예: 코체레 농장"
          />
        </CardContent>
      </Card>

      {/* 가공 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>가공 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* 로스팅 레벨 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              로스팅 레벨 *
            </label>
            <select
              {...register('roastLevel')}
              className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
            >
              {Object.values(RoastLevel).map((level) => (
                <option key={level} value={level}>
                  {getRoastLevelKorean(level)}
                </option>
              ))}
            </select>
            {errors.roastLevel && (
              <p className="mt-1 text-sm text-red-600">{errors.roastLevel.message}</p>
            )}
          </div>

          {/* 가공법 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              가공법 *
            </label>
            <select
              {...register('processType')}
              className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
            >
              {Object.values(ProcessType).map((type) => (
                <option key={type} value={type}>
                  {getProcessTypeKorean(type)}
                </option>
              ))}
            </select>
            {errors.processType && (
              <p className="mt-1 text-sm text-red-600">{errors.processType.message}</p>
            )}
          </div>

          {/* 블렌드 타입 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              블렌드 타입 *
            </label>
            <select
              {...register('blendType')}
              className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
            >
              {Object.values(BlendType).map((type) => (
                <option key={type} value={type}>
                  {getBlendTypeKorean(type)}
                </option>
              ))}
            </select>
            {errors.blendType && (
              <p className="mt-1 text-sm text-red-600">{errors.blendType.message}</p>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 메모 */}
      <Card>
        <CardHeader>
          <CardTitle>메모</CardTitle>
        </CardHeader>
        <CardContent>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              메모 (선택사항)
            </label>
            <textarea
              {...register('memo')}
              rows={4}
              className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
              placeholder="이 원두에 대한 개인적인 메모나 테이스팅 노트를 적어보세요..."
            />
            {errors.memo && (
              <p className="mt-1 text-sm text-red-600">{errors.memo.message}</p>
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
          {mode === 'create' ? '원두 저장' : '원두 수정'}
        </Button>
      </div>
    </form>
  );
};

export default BeanForm;
