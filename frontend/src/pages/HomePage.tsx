import React from 'react';
import { Link } from 'react-router-dom';
import { Coffee, Book, Settings, ArrowRight } from 'lucide-react';
import Button from '@/components/common/Button';
import Card, { CardHeader, CardTitle, CardDescription, CardContent } from '@/components/common/Card';

const HomePage: React.FC = () => {
  const features = [
    {
      icon: Coffee,
      title: '원두 관리',
      description: '나만의 원두 컬렉션을 체계적으로 관리하고 기록해보세요.',
      path: '/beans',
      color: 'text-coffee-600',
      bgColor: 'bg-coffee-50',
    },
    {
      icon: Book,
      title: '레시피 북',
      description: '다양한 커피 레시피를 저장하고 공유해보세요.',
      path: '/recipes',
      color: 'text-blue-600',
      bgColor: 'bg-blue-50',
    },
    {
      icon: Settings,
      title: '장비 관리',
      description: '커피 장비들을 등록하고 관리해보세요.',
      path: '/equipments',
      color: 'text-green-600',
      bgColor: 'bg-green-50',
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-coffee-50 to-orange-50">
      {/* Hero Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="text-center">
          <div className="flex justify-center mb-6">
            <Coffee className="h-16 w-16 text-coffee-600" />
          </div>
          <h1 className="text-4xl font-bold text-gray-900 sm:text-5xl md:text-6xl mb-6">
            <span className="text-coffee-600">Coffee</span>dia
          </h1>
          <p className="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
            커피를 사랑하는 사람들을 위한 완벽한 동반자
            <br />
            원두부터 레시피, 장비까지 모든 커피 경험을 한 곳에서 관리하세요
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Button size="lg" asChild>
              <Link to="/beans">
                시작하기
                <ArrowRight className="ml-2 h-5 w-5" />
              </Link>
            </Button>
            <Button variant="outline" size="lg">
              더 알아보기
            </Button>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-16">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-bold text-gray-900 mb-4">
            모든 커피 경험을 한 곳에서
          </h2>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Coffeedia와 함께 더욱 체계적이고 즐거운 커피 라이프를 시작해보세요
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {features.map((feature) => {
            const Icon = feature.icon;
            return (
              <Card key={feature.path} hover className="h-full">
                <CardHeader className="text-center">
                  <div className={`w-16 h-16 ${feature.bgColor} rounded-lg flex items-center justify-center mx-auto mb-4`}>
                    <Icon className={`h-8 w-8 ${feature.color}`} />
                  </div>
                  <CardTitle>{feature.title}</CardTitle>
                  <CardDescription>{feature.description}</CardDescription>
                </CardHeader>
                <CardContent className="text-center">
                  <Button variant="outline" asChild className="w-full">
                    <Link to={feature.path}>
                      시작하기
                      <ArrowRight className="ml-2 h-4 w-4" />
                    </Link>
                  </Button>
                </CardContent>
              </Card>
            );
          })}
        </div>
      </div>

      {/* Statistics Section */}
      <div className="bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 text-center">
            <div>
              <div className="text-4xl font-bold text-coffee-600 mb-2">50+</div>
              <div className="text-gray-600">등록된 원두 종류</div>
            </div>
            <div>
              <div className="text-4xl font-bold text-coffee-600 mb-2">100+</div>
              <div className="text-gray-600">다양한 레시피</div>
            </div>
            <div>
              <div className="text-4xl font-bold text-coffee-600 mb-2">30+</div>
              <div className="text-gray-600">지원하는 장비</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
