import React from 'react';
import {Link, useLocation} from 'react-router-dom';
import {Book, Coffee, LogIn, Settings} from 'lucide-react';
import {cn} from '@/utils/cn';
import {useAuth} from '@/contexts/AuthContext';
import UserMenu from './UserMenu';

const Navigation: React.FC = () => {
  const location = useLocation();
  const {isAuthenticated,} = useAuth();

  const navItems = [
    {
      name: '원두',
      path: '/beans',
      icon: Coffee,
    },
    {
      name: '레시피',
      path: '/recipes',
      icon: Book,
    },
    {
      name: '장비',
      path: '/equipments',
      icon: Settings,
    },
  ];

  return (
      <nav className="bg-white shadow-sm border-b md:block hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex">
              {/* Logo */}
              <Link
                  to="/"
                  className="flex items-center px-4 text-lg font-bold text-coffee-600"
              >
                <Coffee className="h-6 w-6 mr-2"/>
                Coffeedia
              </Link>

              {/* Navigation Links - 인증된 사용자만 표시 */}
              {isAuthenticated && (
                  <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
                    {navItems.map((item) => {
                      const Icon = item.icon;
                      const isActive = location.pathname.startsWith(item.path);

                      return (
                          <Link
                              key={item.path}
                              to={item.path}
                              className={cn(
                                  'inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors',
                                  isActive
                                      ? 'border-coffee-500 text-coffee-600'
                                      : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
                              )}
                          >
                            <Icon className="h-4 w-4 mr-2"/>
                            {item.name}
                          </Link>
                      );
                    })}
                  </div>
              )}
            </div>

            {/* 우측 메뉴 */}
            <div className="flex items-center space-x-4">
              {isAuthenticated ? (
                  // 로그인된 상태: 사용자 메뉴 표시
                  <UserMenu/>
              ) : (
                  // 로그아웃 상태: 로그인/회원가입 버튼 표시
                  <>
                    <Link
                        to="/login"
                        className="inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 hover:text-coffee-600 transition-colors"
                    >
                      <LogIn className="h-4 w-4 mr-2"/>
                      로그인
                    </Link>
                    <Link
                        to="/signup"
                        className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-coffee-600 hover:bg-coffee-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-coffee-500 transition-colors"
                    >
                      회원가입
                    </Link>
                  </>
              )}
            </div>
          </div>
        </div>
      </nav>
  );
};

export default Navigation;
