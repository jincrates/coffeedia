import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Coffee, ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const MobileHeader: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // 페이지별 타이틀 매핑
  const getPageTitle = () => {
    const path = location.pathname;
    
    if (path === '/') return 'Coffeedia';
    if (path.startsWith('/beans')) {
      if (path.includes('/edit')) return '원두 수정';
      if (path.match(/\/beans\/\d+$/)) return '원두 상세';
      return '원두 관리';
    }
    if (path.startsWith('/recipes')) {
      if (path.match(/\/recipes\/\d+$/)) return '레시피 상세';
      return '레시피';
    }
    if (path.startsWith('/equipments')) return '장비';
    
    return 'Coffeedia';
  };

  // 뒤로가기 버튼이 필요한 페이지인지 확인
  const showBackButton = () => {
    const path = location.pathname;
    return path.includes('/edit') || 
           path.match(/\/beans\/\d+$/) || 
           path.match(/\/recipes\/\d+$/);
  };

  const handleBack = () => {
    navigate(-1);
  };

  return (
    <header className="bg-white shadow-sm border-b md:hidden sticky top-0 z-40">
      <div className="px-4 h-14 flex items-center justify-between">
        {/* 왼쪽: 뒤로가기 또는 로고 */}
        <div className="flex items-center">
          {showBackButton() ? (
            <button
              onClick={handleBack}
              className="p-2 -ml-2 text-gray-600 hover:text-gray-900 transition-colors"
            >
              <ArrowLeft className="h-5 w-5" />
            </button>
          ) : (
            <Link
              to="/"
              className="flex items-center text-coffee-600"
            >
              <Coffee className="h-6 w-6 mr-2" />
              <span className="font-bold text-lg">Coffeedia</span>
            </Link>
          )}
        </div>

        {/* 중앙: 페이지 타이틀 (뒤로가기 버튼이 있을 때만) */}
        {showBackButton() && (
          <div className="absolute left-1/2 transform -translate-x-1/2">
            <h1 className="text-lg font-semibold text-gray-900">
              {getPageTitle()}
            </h1>
          </div>
        )}

        {/* 오른쪽: 여유 공간 */}
        <div className="w-8">
          {/* 필요시 추가 버튼들 */}
        </div>
      </div>
    </header>
  );
};

export default MobileHeader;
