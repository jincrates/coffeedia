import React, {useEffect, useRef, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {useAuth} from '@/contexts/AuthContext';
import {ChevronDown, Coffee, LogOut, Settings, User, UserCircle} from 'lucide-react';
import {clsx} from 'clsx';

const UserMenu: React.FC = () => {
  const {user, logout} = useAuth();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // 외부 클릭 감지
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  if (!user) return null;

  return (
      <div className="relative" ref={dropdownRef}>
        {/* 사용자 버튼 */}
        <button
            onClick={() => setIsOpen(!isOpen)}
            className={clsx(
                "flex items-center space-x-2 px-3 py-2 rounded-lg text-sm font-medium",
                "hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-coffee-500",
                "transition-colors duration-200"
            )}
        >
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-coffee-100 rounded-full flex items-center justify-center">
              <UserCircle className="w-5 h-5 text-coffee-600"/>
            </div>
            <span className="hidden md:block text-gray-700">
            {user.firstName} {user.lastName}
          </span>
            <ChevronDown
                className={clsx(
                    "w-4 h-4 text-gray-500 transition-transform duration-200",
                    isOpen && "transform rotate-180"
                )}
            />
          </div>
        </button>

        {/* 드롭다운 메뉴 */}
        {isOpen && (
            <div className={clsx(
                "absolute right-0 mt-2 w-56 rounded-lg bg-white shadow-lg",
                "ring-1 ring-black ring-opacity-5",
                "transform opacity-100 scale-100",
                "transition ease-out duration-100",
                "z-50"
            )}>
              <div className="py-1">
                {/* 사용자 정보 */}
                <div className="px-4 py-3 border-b border-gray-100">
                  <p className="text-sm font-medium text-gray-900">
                    {user.firstName} {user.lastName}
                  </p>
                  <p className="text-xs text-gray-500 mt-0.5">
                    @{user.username}
                  </p>
                  <p className="text-xs text-gray-500 mt-0.5">
                    {user.email}
                  </p>
                </div>

                {/* 메뉴 아이템들 */}
                <div className="py-1">
                  <Link
                      to="/profile"
                      onClick={() => setIsOpen(false)}
                      className={clsx(
                          "flex items-center px-4 py-2 text-sm text-gray-700",
                          "hover:bg-gray-100 transition-colors duration-200"
                      )}
                  >
                    <User className="w-4 h-4 mr-3"/>
                    내 프로필
                  </Link>

                  <Link
                      to="/my-beans"
                      onClick={() => setIsOpen(false)}
                      className={clsx(
                          "flex items-center px-4 py-2 text-sm text-gray-700",
                          "hover:bg-gray-100 transition-colors duration-200"
                      )}
                  >
                    <Coffee className="w-4 h-4 mr-3"/>
                    내 원두
                  </Link>

                  <Link
                      to="/settings"
                      onClick={() => setIsOpen(false)}
                      className={clsx(
                          "flex items-center px-4 py-2 text-sm text-gray-700",
                          "hover:bg-gray-100 transition-colors duration-200"
                      )}
                  >
                    <Settings className="w-4 h-4 mr-3"/>
                    설정
                  </Link>
                </div>

                {/* 로그아웃 */}
                <div className="border-t border-gray-100">
                  <button
                      onClick={handleLogout}
                      className={clsx(
                          "flex items-center w-full px-4 py-2 text-sm text-red-600",
                          "hover:bg-red-50 transition-colors duration-200"
                      )}
                  >
                    <LogOut className="w-4 h-4 mr-3"/>
                    로그아웃
                  </button>
                </div>
              </div>
            </div>
        )}
      </div>
  );
};

export default UserMenu;
