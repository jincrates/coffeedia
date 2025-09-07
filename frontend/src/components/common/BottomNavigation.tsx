import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Home, Coffee, Book, Wrench } from 'lucide-react';
import { cn } from '@/utils/cn';

interface BottomNavigationProps {
  className?: string;
}

const BottomNavigation: React.FC<BottomNavigationProps> = ({ className }) => {
  const location = useLocation();
  const navigate = useNavigate();

  const navItems = [
    {
      path: '/',
      icon: Home,
      label: '홈',
      isActive: location.pathname === '/',
    },
    {
      path: '/beans',
      icon: Coffee,
      label: '원두',
      isActive: location.pathname.startsWith('/beans'),
    },
    {
      path: '/recipes',
      icon: Book,
      label: '레시피',
      isActive: location.pathname.startsWith('/recipes'),
    },
    {
      path: '/equipments',
      icon: Wrench,
      label: '장비',
      isActive: location.pathname.startsWith('/equipments'),
    },
  ];

  const handleNavigation = (path: string) => {
    navigate(path);
  };

  return (
    <nav
      className={cn(
        'fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 z-50',
        'md:hidden', // 모바일에서만 표시
        className
      )}
    >
      <div className="grid grid-cols-4 h-16">
        {navItems.map((item) => {
          const IconComponent = item.icon;
          return (
            <button
              key={item.path}
              onClick={() => handleNavigation(item.path)}
              className={cn(
                'flex flex-col items-center justify-center space-y-1 transition-colors duration-200',
                'active:bg-gray-100',
                item.isActive
                  ? 'text-coffee-600'
                  : 'text-gray-500 hover:text-gray-700'
              )}
            >
              <IconComponent
                className={cn(
                  'h-5 w-5',
                  item.isActive ? 'text-coffee-600' : 'text-gray-500'
                )}
              />
              <span
                className={cn(
                  'text-xs font-medium',
                  item.isActive ? 'text-coffee-600' : 'text-gray-500'
                )}
              >
                {item.label}
              </span>
            </button>
          );
        })}
      </div>
    </nav>
  );
};

export default BottomNavigation;
