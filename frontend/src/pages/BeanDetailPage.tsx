import React, {useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {useMutation, useQuery, useQueryClient} from 'react-query';
import {
  ArrowLeft,
  Calendar,
  Clock,
  Coffee,
  Edit,
  Factory,
  FileText,
  GitMerge,
  Heart,
  Image as ImageIcon,
  MapPin,
  Scale,
  Share2,
  Sparkles,
  Trash2
} from 'lucide-react';
import {beanService} from '@/services/beanService';
import Card, {CardContent, CardHeader, CardTitle} from '@/components/common/Card';
import Button from '@/components/common/Button';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import {
  formatDate,
  formatRelativeDate,
  getBlendTypeKorean,
  getProcessTypeKorean,
  getRoastLevelKorean
} from '@/utils/format';
import toast from 'react-hot-toast';

const BeanDetailPage: React.FC = () => {
  const {id} = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [imageError, setImageError] = useState(false);

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

  const handleDelete = async () => {
    if (!bean || !window.confirm('정말로 이 원두를 삭제하시겠습니까?')) {
      return;
    }
    deleteBeanMutation.mutate(bean.beanId);
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
    // TODO: 즐겨찾기 기능 구현
    toast.success('즐겨찾기 기능은 곧 구현됩니다!');
  };

  const handleImageError = () => {
    setImageError(true);
  };

  // 에러 상태
  if (error) {
    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center">
          <div className="text-center">
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              원두 정보를 불러올 수 없습니다
            </h2>
            <p className="text-gray-600 mb-4">
              요청하신 원두가 존재하지 않거나 접근할 수 없습니다.
            </p>
            <div className="space-x-2">
              <Button variant="outline" onClick={() => refetch()}>
                다시 시도
              </Button>
              <Button onClick={handleBack}>
                목록으로 돌아가기
              </Button>
            </div>
          </div>
        </div>
    );
  }

  // 로딩 상태
  if (isLoading || !bean) {
    return (
        <div className="min-h-screen bg-gray-50">
          <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
            <div className="mb-6">
              <Button
                  variant="ghost"
                  onClick={handleBack}
                  leftIcon={<ArrowLeft className="h-4 w-4"/>}
              >
                목록으로 돌아가기
              </Button>
            </div>
            <div className="flex items-center justify-center h-64">
              <LoadingSpinner/>
            </div>
          </div>
        </div>
    );
  }

  // 로스팅 신선도 계산
  const roastDate = new Date(bean.roastDate);
  const today = new Date();
  const daysSinceRoast = Math.floor((today.getTime() - roastDate.getTime()) / (1000 * 60 * 60 * 24));

  const getFreshnessStatus = (days: number) => {
    if (days <= 7) return {label: '매우 신선', color: 'bg-green-100 text-green-800', icon: '🔥'};
    if (days <= 14) return {label: '신선', color: 'bg-blue-100 text-blue-800', icon: '✨'};
    if (days <= 30) return {label: '양호', color: 'bg-yellow-100 text-yellow-800', icon: '👍'};
    return {label: '오래됨', color: 'bg-red-100 text-red-800', icon: '⚠️'};
  };

  const freshnessStatus = getFreshnessStatus(daysSinceRoast);

  return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-4 md:py-8">
          {/* Header */}
          <div className="mb-4 md:mb-8">
            <div className="flex items-center justify-between mb-6">
              <Button
                  variant="ghost"
                  onClick={handleBack}
                  leftIcon={<ArrowLeft className="h-4 w-4"/>}
              >
                목록으로 돌아가기
              </Button>

              <div className="flex items-center space-x-2">
                <Button
                    variant="ghost"
                    size="sm"
                    onClick={handleShare}
                    leftIcon={<Share2 className="h-4 w-4"/>}
                >
                  공유
                </Button>
                <Button
                    variant="ghost"
                    size="sm"
                    onClick={handleFavorite}
                    leftIcon={<Heart className="h-4 w-4"/>}
                >
                  즐겨찾기
                </Button>
                <Button
                    variant="outline"
                    size="sm"
                    onClick={handleEdit}
                    leftIcon={<Edit className="h-4 w-4"/>}
                >
                  수정
                </Button>
                <Button
                    variant="destructive"
                    size="sm"
                    onClick={handleDelete}
                    loading={deleteBeanMutation.isLoading}
                    leftIcon={<Trash2 className="h-4 w-4"/>}
                >
                  삭제
                </Button>
              </div>
            </div>

            {/* Bean Title & Status */}
            <div className="bg-white rounded-xl shadow-sm border p-8">
              <div className="flex flex-col lg:flex-row lg:items-center justify-between">
                <div className="flex-1 mb-4 lg:mb-0">
                  <div className="flex items-center gap-3 mb-3">
                    <h1 className="text-3xl font-bold text-gray-900">{bean.name}</h1>
                    <span className={`inline-flex px-3 py-1 text-sm font-medium rounded-full ${
                        bean.status === 'ACTIVE'
                            ? 'bg-green-100 text-green-800'
                            : 'bg-gray-100 text-gray-800'
                    }`}>
                    {bean.status === 'ACTIVE' ? '활성' : '비활성'}
                  </span>
                    {bean.isDecaf && (
                        <span
                            className="inline-flex px-3 py-1 text-sm font-medium bg-orange-100 text-orange-800 rounded-full">
                      디카페인
                    </span>
                    )}
                  </div>

                  <div className="flex flex-wrap items-center gap-4 text-sm text-gray-600">
                    <span className="font-semibold text-coffee-600 text-lg">{bean.roaster}</span>
                    <div className="flex items-center">
                      <Calendar className="h-4 w-4 mr-1"/>
                      <span>{formatRelativeDate(bean.createdAt)} 등록</span>
                    </div>
                  </div>
                </div>

                {/* 신선도 상태 */}
                <div className="flex flex-col items-end space-y-2">
                  <div
                      className={`inline-flex items-center px-4 py-2 rounded-full text-sm font-medium ${freshnessStatus.color}`}>
                    <span className="mr-2">{freshnessStatus.icon}</span>
                    {freshnessStatus.label}
                  </div>
                  <div className="flex items-center text-sm text-gray-500">
                    <Clock className="h-4 w-4 mr-1"/>
                    <span>로스팅 후 {daysSinceRoast}일</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Content */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Main Content */}
            <div className="lg:col-span-2 space-y-6">
              {/* 썸네일 이미지 */}
              {bean.thumbnailUrl && (
                  <Card>
                    <CardContent className="p-0">
                      <div className="relative h-64 md:h-80 bg-gray-100 overflow-hidden rounded-lg">
                        {!imageError ? (
                            <img
                                src={bean.thumbnailUrl}
                                alt={bean.name}
                                className="w-full h-full object-cover"
                                onError={handleImageError}
                            />
                        ) : (
                            <div
                                className="w-full h-full flex items-center justify-center bg-gradient-to-br from-coffee-100 to-coffee-200">
                              <div className="text-center">
                                <ImageIcon className="h-16 w-16 text-coffee-400 mx-auto mb-2"/>
                                <p className="text-coffee-600">이미지를 불러올 수 없습니다</p>
                              </div>
                            </div>
                        )}
                      </div>
                    </CardContent>
                  </Card>
              )}

              {/* 원산지 정보 */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center">
                    <MapPin className="h-5 w-5 mr-2"/>
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
                    <Factory className="h-5 w-5 mr-2"/>
                    가공 정보
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div
                        className="bg-gradient-to-br from-coffee-50 to-coffee-100 rounded-lg p-4 border border-coffee-200">
                      <div className="flex items-center mb-2">
                        <Coffee className="h-5 w-5 text-coffee-600 mr-2"/>
                        <span className="text-sm text-coffee-600 font-medium">로스팅 레벨</span>
                      </div>
                      <p className="font-bold text-coffee-900 text-lg">
                        {getRoastLevelKorean(bean.roastLevel)}
                      </p>
                    </div>

                    <div
                        className="bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg p-4 border border-blue-200">
                      <div className="flex items-center mb-2">
                        <Factory className="h-5 w-5 text-blue-600 mr-2"/>
                        <span className="text-sm text-blue-600 font-medium">가공법</span>
                      </div>
                      <p className="font-bold text-blue-900 text-lg">
                        {getProcessTypeKorean(bean.processType)}
                      </p>
                    </div>

                    <div
                        className="bg-gradient-to-br from-green-50 to-green-100 rounded-lg p-4 border border-green-200">
                      <div className="flex items-center mb-2">
                        <GitMerge className="h-5 w-5 text-green-600 mr-2"/>
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
                        <Sparkles className="h-5 w-5 mr-2"/>
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
                              <Sparkles className="h-4 w-4 mx-auto mb-1 text-purple-600"/>
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
                        <FileText className="h-5 w-5 mr-2"/>
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

            {/* Sidebar */}
            <div className="space-y-6">
              {/* 로스팅 정보 */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center">
                    <Coffee className="h-5 w-5 mr-2"/>
                    로스팅 정보
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="bg-gray-50 rounded-lg p-4">
                    <div className="flex items-center mb-2">
                      <Calendar className="h-4 w-4 text-gray-600 mr-2"/>
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
                      <Scale className="h-4 w-4 text-gray-600 mr-2"/>
                      <span className="text-sm text-gray-500">무게</span>
                    </div>
                    <p className="font-bold text-gray-900 text-xl">
                      {bean.grams}g
                    </p>
                  </div>
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
                    <Coffee className="h-4 w-4 mr-2"/>
                    브루잉 로그 작성
                  </Button>
                  <Button
                      variant="outline"
                      className="w-full justify-start"
                      onClick={() => toast.success('재고 관리 기능은 곧 구현됩니다!')}
                  >
                    <Scale className="h-4 w-4 mr-2"/>
                    재고 관리
                  </Button>
                  <Button
                      variant="outline"
                      className="w-full justify-start"
                      onClick={() => toast.success('평가 기능은 곧 구현됩니다!')}
                  >
                    <Sparkles className="h-4 w-4 mr-2"/>
                    평가하기
                  </Button>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </div>
  );
};

export default BeanDetailPage;
