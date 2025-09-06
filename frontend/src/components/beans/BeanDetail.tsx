import React from 'react';
import { BeanResponse } from '@/types/api';
import Card, { CardHeader, CardTitle, CardContent } from '@/components/common/Card';
import Button from '@/components/common/Button';
import { 
  X,
  MapPin,
  Calendar,
  Scale,
  Coffee,
  Factory,
  GitMerge,
  Sparkles,
  FileText,
  Edit,
  Trash2
} from 'lucide-react';
import { 
  formatDate, 
  formatRelativeDate, 
  getRoastLevelKorean, 
  getProcessTypeKorean, 
  getBlendTypeKorean 
} from '@/utils/format';

interface BeanDetailProps {
  bean: BeanResponse;
  onClose?: () => void;
  onEdit?: (bean: BeanResponse) => void;
  onDelete?: (beanId: number) => void;
}

const BeanDetail: React.FC<BeanDetailProps> = ({ bean, onClose, onEdit, onDelete }) => {
  const handleBackgroundClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose?.();
    }
  };

  const handleEdit = () => {
    onEdit?.(bean);
  };

  const handleDelete = () => {
    if (confirm('정말로 이 원두를 삭제하시겠습니까?')) {
      onDelete?.(bean.beanId);
    }
  };

  return (
    <div 
      className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
      onClick={handleBackgroundClick}
    >
      <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-hidden">
        {/* 헤더 */}
        <div className="flex justify-between items-center p-6 border-b border-gray-200">
          <div className="flex-1">
            <div className="flex items-center gap-3 mb-2">
              <h1 className="text-2xl font-bold text-gray-900">{bean.name}</h1>
              <span className={`inline-flex px-3 py-1 text-sm font-medium rounded-full ${
                bean.status === 'ACTIVE' 
                  ? 'bg-green-100 text-green-800' 
                  : 'bg-gray-100 text-gray-800'
              }`}>
                {bean.status === 'ACTIVE' ? '활성' : '비활성'}
              </span>
              {bean.isDecaf && (
                <span className="inline-flex px-3 py-1 text-sm font-medium bg-orange-100 text-orange-800 rounded-full">
                  디카페인
                </span>
              )}
            </div>
            <div className="flex items-center text-sm text-gray-500 space-x-4">
              <span className="font-medium text-coffee-600">{bean.roaster}</span>
              <div className="flex items-center">
                <Calendar className="h-4 w-4 mr-1" />
                <span>{formatRelativeDate(bean.createdAt)} 등록</span>
              </div>
            </div>
          </div>
          
          {/* 액션 버튼 */}
          <div className="flex items-center space-x-2">
            {onEdit && (
              <Button
                variant="outline"
                size="sm"
                onClick={handleEdit}
                leftIcon={<Edit className="h-4 w-4" />}
              >
                수정
              </Button>
            )}
            {onDelete && (
              <Button
                variant="destructive"
                size="sm"
                onClick={handleDelete}
                leftIcon={<Trash2 className="h-4 w-4" />}
              >
                삭제
              </Button>
            )}
            {onClose && (
              <Button
                variant="ghost"
                size="sm"
                onClick={onClose}
                leftIcon={<X className="h-4 w-4" />}
              >
                닫기
              </Button>
            )}
          </div>
        </div>

        {/* 컨텐츠 */}
        <div className="overflow-y-auto max-h-[calc(90vh-120px)]">
          <div className="p-6 space-y-6">
            {/* 기본 정보 */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* 원산지 정보 */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center">
                    <MapPin className="h-5 w-5 mr-2" />
                    원산지
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div>
                    <span className="text-sm text-gray-500">국가</span>
                    <p className="font-medium text-gray-900">{bean.origin.country}</p>
                  </div>
                  {bean.origin.region && (
                    <div>
                      <span className="text-sm text-gray-500">지역</span>
                      <p className="font-medium text-gray-900">{bean.origin.region}</p>
                    </div>
                  )}
                  {bean.origin.farm && (
                    <div>
                      <span className="text-sm text-gray-500">농장</span>
                      <p className="font-medium text-gray-900">{bean.origin.farm}</p>
                    </div>
                  )}
                </CardContent>
              </Card>

              {/* 로스팅 정보 */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center">
                    <Coffee className="h-5 w-5 mr-2" />
                    로스팅 정보
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div>
                    <span className="text-sm text-gray-500">로스팅 일자</span>
                    <p className="font-medium text-gray-900">
                      {formatDate(bean.roastDate, 'yyyy.MM.dd')}
                      <span className="text-gray-400 ml-2">
                        ({formatRelativeDate(bean.roastDate)})
                      </span>
                    </p>
                  </div>
                  <div>
                    <span className="text-sm text-gray-500">무게</span>
                    <p className="font-medium text-gray-900 flex items-center">
                      <Scale className="h-4 w-4 mr-1" />
                      {bean.grams}g
                    </p>
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* 가공 정보 */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <Factory className="h-5 w-5 mr-2" />
                  가공 정보
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div className="bg-gray-50 rounded-lg p-4">
                    <div className="flex items-center mb-2">
                      <Coffee className="h-4 w-4 text-coffee-600 mr-2" />
                      <span className="text-sm text-gray-500">로스팅 레벨</span>
                    </div>
                    <p className="font-medium text-gray-900">
                      {getRoastLevelKorean(bean.roastLevel)}
                    </p>
                  </div>
                  
                  <div className="bg-gray-50 rounded-lg p-4">
                    <div className="flex items-center mb-2">
                      <Factory className="h-4 w-4 text-blue-600 mr-2" />
                      <span className="text-sm text-gray-500">가공법</span>
                    </div>
                    <p className="font-medium text-gray-900">
                      {getProcessTypeKorean(bean.processType)}
                    </p>
                  </div>
                  
                  <div className="bg-gray-50 rounded-lg p-4">
                    <div className="flex items-center mb-2">
                      <GitMerge className="h-4 w-4 text-green-600 mr-2" />
                      <span className="text-sm text-gray-500">블렌드 타입</span>
                    </div>
                    <p className="font-medium text-gray-900">
                      {getBlendTypeKorean(bean.blendType)}
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* 향미 프로필 */}
            {bean.flavors && bean.flavors.length > 0 && (
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center">
                    <Sparkles className="h-5 w-5 mr-2" />
                    향미 프로필
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="flex flex-wrap gap-2">
                    {bean.flavors.map((flavor) => (
                      <span
                        key={flavor.id}
                        className="inline-flex items-center px-3 py-2 text-sm bg-coffee-100 text-coffee-800 rounded-full"
                        title={flavor.description}
                      >
                        <Sparkles className="h-3 w-3 mr-1" />
                        {flavor.name}
                      </span>
                    ))}
                  </div>
                </CardContent>
              </Card>
            )}

            {/* 메모 */}
            {bean.memo && (
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center">
                    <FileText className="h-5 w-5 mr-2" />
                    메모
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                    <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                      {bean.memo}
                    </p>
                  </div>
                </CardContent>
              </Card>
            )}

            {/* 등록/수정 정보 */}
            <Card>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm text-gray-500">
                  <div>
                    <span>등록일: </span>
                    <span className="font-medium">
                      {formatDate(bean.createdAt, 'yyyy.MM.dd HH:mm')}
                    </span>
                  </div>
                  <div>
                    <span>수정일: </span>
                    <span className="font-medium">
                      {formatDate(bean.updatedAt, 'yyyy.MM.dd HH:mm')}
                    </span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BeanDetail;
