import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { 
  CreateEquipmentCommand, 
  UpdateEquipmentCommand, 
  EquipmentResponse, 
  EquipmentType,
  ActiveStatus 
} from '@/types/api';
import Button from '@/components/common/Button';
import Input from '@/components/common/Input';
import Card, { CardHeader, CardTitle, CardContent } from '@/components/common/Card';
import { getEquipmentTypeKorean } from '@/utils/format';

// 유효성 검사 스키마
const equipmentSchema = yup.object({
  type: yup.string().required('장비 타입을 선택해주세요.'),
  name: yup.string().required('장비명은 필수입니다.'),
  brand: yup.string().required('브랜드명은 필수입니다.'),
  description: yup.string().max(500, '설명은 500자까지만 가능합니다.'),
  buyDate: yup.date().nullable(),
  buyUrl: yup.string().url('올바른 URL 형식이 아닙니다.'),
});

interface EquipmentFormProps {
  initialData?: EquipmentResponse;
  onSubmit: (data: CreateEquipmentCommand | UpdateEquipmentCommand) => void;
  onCancel: () => void;
  loading?: boolean;
  mode?: 'create' | 'edit';
  showButtons?: boolean;
}

const EquipmentForm: React.FC<EquipmentFormProps> = ({
  initialData,
  onSubmit,
  onCancel,
  loading = false,
  mode = 'create',
  showButtons = true,
}) => {
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<CreateEquipmentCommand>({
    resolver: yupResolver(equipmentSchema),
    defaultValues: initialData
      ? {
          type: initialData.type,
          name: initialData.name,
          brand: initialData.brand,
          description: initialData.description || '',
          buyDate: initialData.buyDate || '',
          buyUrl: initialData.buyUrl || '',
        }
      : {
          type: EquipmentType.GRINDER,
          name: '',
          brand: '',
          description: '',
          buyDate: '',
          buyUrl: '',
        },
  });

  const selectedType = watch('type');

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* 기본 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>기본 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* 장비 타입 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              장비 타입 *
            </label>
            <select
              {...register('type')}
              className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
            >
              {Object.values(EquipmentType).map((type) => (
                <option key={type} value={type}>
                  {getEquipmentTypeKorean(type)}
                </option>
              ))}
            </select>
            {errors.type && (
              <p className="mt-1 text-sm text-red-600">{errors.type.message}</p>
            )}
          </div>

          {/* 장비명 */}
          <Input
            label="장비명 *"
            {...register('name')}
            error={errors.name?.message}
            placeholder="예: V60 드리퍼, 바라짜 엔코어"
          />

          {/* 브랜드 */}
          <Input
            label="브랜드 *"
            {...register('brand')}
            error={errors.brand?.message}
            placeholder="예: 하리오, 바라짜, 칼리타"
          />

          {/* 설명 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              설명 (선택사항)
            </label>
            <textarea
              {...register('description')}
              rows={4}
              className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-coffee-500 focus:ring-coffee-500"
              placeholder="장비에 대한 상세 설명이나 특징을 적어보세요..."
            />
            {errors.description && (
              <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>
            )}
          </div>
        </CardContent>
      </Card>

      {/* 구매 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>구매 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* 구매일 */}
          <Input
            label="구매일"
            type="date"
            {...register('buyDate')}
            error={errors.buyDate?.message}
          />

          {/* 구매 링크 */}
          <Input
            label="구매 링크"
            {...register('buyUrl')}
            error={errors.buyUrl?.message}
            placeholder="https://example.com/product"
            helperText="구매한 온라인 쇼핑몰 링크를 저장할 수 있습니다."
          />
        </CardContent>
      </Card>

      {/* 미리보기 */}
      {selectedType && (
        <Card>
          <CardHeader>
            <CardTitle>미리보기</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="bg-gray-50 p-4 rounded-lg">
              <div className="flex items-center space-x-2 mb-2">
                <span className="text-sm font-medium text-coffee-600 bg-coffee-50 px-2 py-1 rounded-full">
                  {getEquipmentTypeKorean(selectedType)}
                </span>
              </div>
              <h3 className="font-semibold text-lg text-gray-900">
                {watch('name') || '장비명'}
              </h3>
              <p className="text-sm text-gray-600">
                {watch('brand') || '브랜드명'}
              </p>
              {watch('description') && (
                <p className="text-sm text-gray-600 mt-2">
                  {watch('description')}
                </p>
              )}
            </div>
          </CardContent>
        </Card>
      )}

      {/* 버튼 */}
      {showButtons && (
        <div className="flex justify-end space-x-3">
          <Button type="button" variant="outline" onClick={onCancel}>
            취소
          </Button>
          <Button type="submit" loading={loading}>
            {mode === 'create' ? '장비 저장' : '장비 수정'}
          </Button>
        </div>
      )}
    </form>
  );
};

export default EquipmentForm;
