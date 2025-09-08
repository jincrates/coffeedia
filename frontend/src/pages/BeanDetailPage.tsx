import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from 'react-query';
import { Calendar, Clock, Coffee, Factory, FileText, GitMerge, MapPin, Scale, Sparkles } from 'lucide-react';
import { beanService } from '@/services/beanService';
import EntityDetailLayout from '@/components/common/EntityDetailLayout';
import Card, { CardContent, CardHeader, CardTitle } from '@/components/common/Card';
import Button from '@/components/common/Button';
import { formatDate, formatRelativeDate, getBlendTypeKorean, getProcessTypeKorean, getRoastLevelKorean } from '@/utils/format';
import toast from 'react-hot-toast';

const BeanDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const beanId = id ? parseInt(id, 10) : null;

  const {
    data: bean,
    isLoading,
    error,
    refetch,
  } = useQuery(
    ['bean', beanId],
    () => beanId ? beanService.getBean(beanId) : Promise.reject('Invalid bean ID'),
    {
      enabled: !!beanId,
      retry: 1,
    }
  );

  // 원두 삭제 뮤테이션
  const deleteBeanMutation = useMutation(
    (beanId: number) => beanService.deleteBean(beanId),
    {
      onSuccess: () => {
        toast.success('원두가 삭제되었습니다.');
        navigate('/beans');
        queryClient.invalidateQueries(['beans']);
      },
      onError: (error: any) => {
        toast.error(error.response?.data?.message || '원두 삭제에 실패했습니다.');
      },
    }
  );

  const handleBack = () => {
    navigate('/beans');
  };

  const handleEdit = () => {
    navigate(`/beans/${beanId}/edit`);
  };

  const handleDelete = () => {
    if (bean) {
      deleteBeanMutation.mutate(bean.beanId);
    }
  };

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: `${bean?.name} - CoffeeMedia`,
        text: `${bean?.roaster}의 ${bean?.name} 원두 정보를 확인해보세요.`,
        url: window.location.href,
      });
    } else {
      navigator.clipboard.writeText(window.location.href);
      toast.success('링크가 클립보드에 복사되었습니다.');
    }
  };

  const handleFavorite = () => {
    toast.success('즐겨찾기 기능은 곧 구현됩니다!');
  };

  // 로스팅 신선도 계산
  const getFreshnessInfo = () => {
    if (!bean) return null;
    
    const roastDate = new Date(bean.roastDate);
    const today = new Date();
    const daysSinceRoast = Math.floor((today.getTime() - roastDate.getTime()) / (1000 * 60 * 60 * 24));

    if (daysSinceRoast <= 7) return { label: '매우 신선', variant: 'success', icon: '🔥' };
    if (daysSinceRoast <= 14) return { label: '신선', variant: 'primary', icon: '✨' };
    if (daysSinceRoast <= 30) return { label: '양호', variant: 'warning', icon: '👍' };
    return { label: '오래됨', variant: 'danger', icon: '⚠️' };
  };

  const freshnessInfo = getFreshnessInfo();

  const sidebarContent = bean ? (
    <div className="space-y-6">
      {/* 로스팅 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Coffee className="h-5 w-5 mr-2" />
            로스팅 정보
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="bg-gray-50 rounded-lg p-4">
            <div className="flex items-center mb-2">
              <Calendar className="h-4 w-4 text-gray-600 mr-2" />
              <span className="text-sm text-gray-500">로스팅 일자</span>
            </div>
            <p className="font-bold text-gray-900">
              {formatDate(bean.roastDate, 'yyyy.MM.dd')}
            </p>
            <p className="text-sm text-gray-500 mt-1">
              {formatRelativeDate(bean.roastDate)}
            </p>
          </div>

          <div className="bg-gray-50 rounded-lg p-4">
            <div className="flex items-center mb-2">
              <Scale className="h-4 w-4 text-gray-600 mr-2" />
              <span className="text-sm text-gray-500">무게</span>
            </div>
            <p className="font-bold text-gray-900 text-xl">{bean.grams}g</p>
          </div>

          {freshnessInfo && (
            <div className="bg-gray-50 rounded-lg p-4">
              <div className="flex items-center mb-2">
                <Clock className="h-4 w-4 text-gray-600 mr-2" />
                <span className="text-sm text-gray-500">신선도</span>
              </div>
              <div className="flex items-center gap-2">
                <span className="text-lg">{freshnessInfo.icon}</span>
                <span className="font-medium text-gray-900">{freshnessInfo.label}</span>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* 관리 정보 */}
      <Card>
        <CardHeader>
          <CardTitle>관리 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3 text-sm">
          <div>
            <span className="text-gray-500">등록일</span>
            <p className="font-medium text-gray-900 mt-1">
              {formatDate(bean.createdAt, 'yyyy.MM.dd HH:mm')}
            </p>
          </div>
          <div>
            <span className="text-gray-500">수정일</span>
            <p className="font-medium text-gray-900 mt-1">
              {formatDate(bean.updatedAt, 'yyyy.MM.dd HH:mm')}
            </p>
          </div>
        </CardContent>
      </Card>

      {/* 퀵 액션 */}
      <Card>
        <CardHeader>
          <CardTitle>퀵 액션</CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          <Button
            variant="outline"
            className="w-full justify-start"
            onClick={() => toast.success('브루잉 로그 기능은 곧 구현됩니다!')}
          >
            <Coffee className="h-4 w-4 mr-2" />
            브루잉 로그 작성
          </Button>
          <Button
            variant="outline"
            className="w-full justify-start"
            onClick={() => toast.success('재고 관리 기능은 곧 구현됩니다!')}
          >
            <Scale className="h-4 w-4 mr-2" />
            재고 관리
          </Button>
          <Button
            variant="outline"
            className="w-full justify-start"
            onClick={() => toast.success('평가 기능은 곧 구현됩니다!')}
          >
            <Sparkles className="h-4 w-4 mr-2" />
            평가하기
          </Button>
        </CardContent>
      </Card>
    </div>
  ) : null;

  const badges = [];
  if (bean) {
    if (bean.isDecaf) {
      badges.push({ label: '디카페인', variant: 'warning' as const });
    }
    if (freshnessInfo) {
      badges.push({ label: freshnessInfo.label, variant: freshnessInfo.variant as any });
    }
  }

  return (
    <EntityDetailLayout
      title={bean?.name || ''}
      subtitle={bean ? `${bean.roaster} • ${bean.origin.country}${bean.origin.region ? ` • ${bean.origin.region}` : ''}` : ''}
      status={bean ? {
        label: bean.status === 'ACTIVE' ? '활성' : '비활성',
        variant: bean.status === 'ACTIVE' ? 'success' : 'inactive'
      } : undefined}
      badges={badges}
      thumbnailUrl={bean?.thumbnailUrl}
      thumbnailAlt={bean?.name}
      fallbackIcon={<Coffee className="h-16 w-16 text-gray-300" />}
      onBack={handleBack}
      onEdit={handleEdit}
      onDelete={handleDelete}
      onShare={handleShare}
      onFavorite={handleFavorite}
      deleteConfirmTitle="원두 삭제 확인"
      deleteConfirmMessage="정말로 이 원두를 삭제하시겠습니까? 삭제된 원두는 복구할 수 없습니다."
      isDeleting={deleteBeanMutation.isLoading}
      isLoading={isLoading}
      error={error}
      sidebarContent={sidebarContent}
    >
      {bean && (
        <div className="space-y-6">
          {/* 원산지 정보 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center">
                <MapPin className="h-5 w-5 mr-2" />
                원산지
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-gray-50 rounded-lg p-4">
                  <span className="text-sm text-gray-500 block mb-1">국가</span>
                  <p className="font-semibold text-gray-900 text-lg">{bean.origin.country}</p>
                </div>
                {bean.origin.region && (
                  <div className="bg-gray-50 rounded-lg p-4">
                    <span className="text-sm text-gray-500 block mb-1">지역</span>
                    <p className="font-semibold text-gray-900 text-lg">{bean.origin.region}</p>
                  </div>
                )}
                {bean.origin.farm && (
                  <div className="bg-gray-50 rounded-lg p-4">
                    <span className="text-sm text-gray-500 block mb-1">농장</span>
                    <p className="font-semibold text-gray-900 text-lg">{bean.origin.farm}</p>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>

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
                <div className="bg-gradient-to-br from-coffee-50 to-coffee-100 rounded-lg p-4 border border-coffee-200">
                  <div className="flex items-center mb-2">
                    <Coffee className="h-5 w-5 text-coffee-600 mr-2" />
                    <span className="text-sm text-coffee-600 font-medium">로스팅 레벨</span>
                  </div>
                  <p className="font-bold text-coffee-900 text-lg">
                    {getRoastLevelKorean(bean.roastLevel)}
                  </p>
                </div>

                <div className="bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg p-4 border border-blue-200">
                  <div className="flex items-center mb-2">
                    <Factory className="h-5 w-5 text-blue-600 mr-2" />
                    <span className="text-sm text-blue-600 font-medium">가공법</span>
                  </div>
                  <p className="font-bold text-blue-900 text-lg">
                    {getProcessTypeKorean(bean.processType)}
                  </p>
                </div>

                <div className="bg-gradient-to-br from-green-50 to-green-100 rounded-lg p-4 border border-green-200">
                  <div className="flex items-center mb-2">
                    <GitMerge className="h-5 w-5 text-green-600 mr-2" />
                    <span className="text-sm text-green-600 font-medium">블렌드 타입</span>
                  </div>
                  <p className="font-bold text-green-900 text-lg">
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
                <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                  {bean.flavors.map((flavor) => (
                    <div
                      key={flavor.id}
                      className="bg-gradient-to-r from-purple-100 to-pink-100 border border-purple-200 rounded-lg p-3 text-center hover:shadow-md transition-shadow cursor-help"
                      title={flavor.description || flavor.name}
                    >
                      <Sparkles className="h-4 w-4 mx-auto mb-1 text-purple-600" />
                      <span className="text-sm font-medium text-purple-900">
                        {flavor.name}
                      </span>
                    </div>
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
        </div>
      )}
    </EntityDetailLayout>
  );
};

export default BeanDetailPage;
