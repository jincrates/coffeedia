import React, {useState} from 'react';
import {Link, useLocation, useNavigate} from 'react-router-dom';
import {useForm} from 'react-hook-form';
import {yupResolver} from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {useAuth} from '@/contexts/AuthContext';
import {LoginRequest} from '@/types/auth';
import {Coffee, Eye, EyeOff, Loader2} from 'lucide-react';
import {clsx} from 'clsx';

// 유효성 검사 스키마
const loginSchema = yup.object({
  username: yup.string()
  .required('사용자명을 입력해주세요')
  .min(3, '사용자명은 최소 3자 이상이어야 합니다'),
  password: yup.string()
  .required('비밀번호를 입력해주세요')
  .min(6, '비밀번호는 최소 6자 이상이어야 합니다'),
});

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const {login} = useAuth();
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const from = location.state?.from?.pathname || '/';

  const {
    register,
    handleSubmit,
    formState: {errors},
    setError
  } = useForm<LoginRequest>({
    resolver: yupResolver(loginSchema),
    defaultValues: {
      username: '',
      password: ''
    }
  });

  const onSubmit = async (data: LoginRequest) => {
    setIsSubmitting(true);
    try {
      await login(data);
      navigate(from, {replace: true});
    } catch (error: any) {
      // 서버 에러 메시지에 따른 처리
      if (error.response?.status === 404) {
        setError('username', {
          type: 'manual',
          message: '사용자를 찾을 수 없습니다'
        });
      } else if (error.response?.status === 401) {
        setError('password', {
          type: 'manual',
          message: '비밀번호가 올바르지 않습니다'
        });
      } else {
        setError('root', {
          type: 'manual',
          message: '로그인 중 오류가 발생했습니다'
        });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
      <div
          className="min-h-screen flex items-center justify-center bg-gradient-to-br from-coffee-50 to-coffee-100 px-4">
        <div className="max-w-md w-full space-y-8">
          {/* 로고 및 타이틀 */}
          <div className="text-center">
            <div className="flex justify-center mb-4">
              <div className="bg-coffee-600 p-3 rounded-full">
                <Coffee className="h-12 w-12 text-white"/>
              </div>
            </div>
            <h2 className="text-3xl font-bold text-gray-900">
              Coffeedia
            </h2>
            <p className="mt-2 text-sm text-gray-600">
              계정에 로그인하세요
            </p>
          </div>

          {/* 로그인 폼 */}
          <div className="bg-white py-8 px-6 shadow-xl rounded-lg sm:px-10">
            <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
              {/* Root 에러 메시지 */}
              {errors.root && (
                  <div
                      className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md text-sm">
                    {errors.root.message}
                  </div>
              )}

              {/* 사용자명 입력 */}
              <div>
                <label htmlFor="username" className="block text-sm font-medium text-gray-700">
                  사용자명
                </label>
                <div className="mt-1">
                  <input
                      {...register('username')}
                      type="text"
                      autoComplete="username"
                      className={clsx(
                          "appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400",
                          "focus:outline-none focus:ring-coffee-500 focus:border-coffee-500 sm:text-sm",
                          errors.username
                              ? "border-red-300 text-red-900"
                              : "border-gray-300"
                      )}
                      placeholder="사용자명을 입력하세요"
                  />
                  {errors.username && (
                      <p className="mt-2 text-sm text-red-600">
                        {errors.username.message}
                      </p>
                  )}
                </div>
              </div>

              {/* 비밀번호 입력 */}
              <div>
                <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                  비밀번호
                </label>
                <div className="mt-1 relative">
                  <input
                      {...register('password')}
                      type={showPassword ? "text" : "password"}
                      autoComplete="current-password"
                      className={clsx(
                          "appearance-none block w-full px-3 py-2 pr-10 border rounded-md shadow-sm placeholder-gray-400",
                          "focus:outline-none focus:ring-coffee-500 focus:border-coffee-500 sm:text-sm",
                          errors.password
                              ? "border-red-300 text-red-900"
                              : "border-gray-300"
                      )}
                      placeholder="비밀번호를 입력하세요"
                  />
                  <button
                      type="button"
                      className="absolute inset-y-0 right-0 pr-3 flex items-center"
                      onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? (
                        <EyeOff className="h-4 w-4 text-gray-400"/>
                    ) : (
                        <Eye className="h-4 w-4 text-gray-400"/>
                    )}
                  </button>
                  {errors.password && (
                      <p className="mt-2 text-sm text-red-600">
                        {errors.password.message}
                      </p>
                  )}
                </div>
              </div>

              {/* 기억하기 & 비밀번호 찾기 */}
              <div className="flex items-center justify-between">
                <div className="flex items-center">
                  <input
                      id="remember-me"
                      name="remember-me"
                      type="checkbox"
                      className="h-4 w-4 text-coffee-600 focus:ring-coffee-500 border-gray-300 rounded"
                  />
                  <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900">
                    로그인 상태 유지
                  </label>
                </div>

                <div className="text-sm">
                  <a href="#" className="font-medium text-coffee-600 hover:text-coffee-500">
                    비밀번호를 잊으셨나요?
                  </a>
                </div>
              </div>

              {/* 로그인 버튼 */}
              <div>
                <button
                    type="submit"
                    disabled={isSubmitting}
                    className={clsx(
                        "w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white",
                        "focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-coffee-500",
                        isSubmitting
                            ? "bg-coffee-400 cursor-not-allowed"
                            : "bg-coffee-600 hover:bg-coffee-700"
                    )}
                >
                  {isSubmitting ? (
                      <>
                        <Loader2 className="animate-spin -ml-1 mr-2 h-4 w-4"/>
                        로그인 중...
                      </>
                  ) : (
                      '로그인'
                  )}
                </button>
              </div>
            </form>

            {/* 구분선 */}
            <div className="mt-6">
              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-gray-300"/>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-2 bg-white text-gray-500">또는</span>
                </div>
              </div>
            </div>

            {/* 회원가입 링크 */}
            <div className="mt-6">
              <div className="text-center">
              <span className="text-sm text-gray-600">
                아직 계정이 없으신가요?{' '}
                <Link
                    to="/signup"
                    className="font-medium text-coffee-600 hover:text-coffee-500"
                >
                  회원가입
                </Link>
              </span>
              </div>
            </div>
          </div>
        </div>
      </div>
  );
};

export default LoginPage;
