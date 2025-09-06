import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Coffee, Book, Settings } from 'lucide-react';
import { cn } from '@/utils/cn';

const Navigation: React.FC = () => {
  const location = useLocation();

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
    <nav className="bg-white shadow-sm border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex">
            {/* Logo */}
            <Link
              to="/"
              className="flex items-center px-4 text-lg font-bold text-coffee-600"
            >
              <Coffee className="h-6 w-6 mr-2" />
              Coffeedia
            </Link>

            {/* Navigation Links */}
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
                    <Icon className="h-4 w-4 mr-2" />
                    {item.name}
                  </Link>
                );
              })}
            </div>
          </div>

          {/* Mobile menu button - 향후 구현 */}
          <div className="flex items-center sm:hidden">
            <button className="text-gray-400 hover:text-gray-500">
              <svg
                className="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth="1.5"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navigation;
