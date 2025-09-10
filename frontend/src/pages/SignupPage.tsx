import React, {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {useForm} from 'react-hook-form';
import {yupResolver} from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {useAuth} from '@/contexts/AuthContext';
import {SignupRequest} from '@/types/auth';
import {Check, Coffee, Eye, EyeOff, Loader2, X} from 'lucide-react';
import {clsx} from 'clsx';

// 유효성 검사 스키마
const signupSchema = yup.object({
  username: yup.string()
  .required('사용자명을 입력해주세요')
  .min(3, '사용자명은 최소 3자 이상이어야 합니다')
  .max(20, '사용자명은 최대 20자까지 가능합니다')
  .matches(/^[a-zA-Z0-9_]+$/, '사용자명은 영문, 숫자, 언더스코어만 사용 가능합니다'),
  email: yup.string()
  .required('이메일을 입력해주세요')
  .email('올바른 이메일 형식이 아닙니다'),
  firstName: yup.string()
  .required('이름을 입력해주세요')
  .min(1, '이름을 입력해주세요')
  .max(50, '이름은 최대 50자까지 가능합니다'),
  lastName: yup.string()
  .required('성을 입력해주세요')
  .min(1, '성을 입력해주세요')
  .max(50, '성은 최대 50자까지 가능합니다'),
  password: yup.string()
  .required('비밀번호를 입력해주세요')
  .min(8, '비밀번호는 최소 8자 이상이어야 합니다')
  .matches(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
      '비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다'
  ),
  confirmPassword: yup.string()
  .required('비밀번호 확인을 입력해주세요')
  .oneOf([yup.ref('password')], '비밀번호가 일치하지 않습니다'),
});

interface SignupFormData extends SignupRequest {
  confirmPassword: string;
}

const SignupPage: React.FC = () => {
  const navigate = useNavigate();
  const {signup} = useAuth();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: {errors},
    watch,
    setError
  } = useForm<SignupFormData>({
    resolver: yupResolver(signupSchema),
    defaultValues: {
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      password: '',
      confirmPassword: ''
    }
  });

  const password = watch('password');

  // 비밀번호 강도 체크
  const passwordStrength = {
    hasMinLength: password?.length >= 8,
    hasUpperCase: /[A-Z]/.test(password || ''),
    hasLowerCase: /[a-z]/.test(password || ''),
    hasNumber: /\d/.test(password || ''),
    hasSpecialChar: /[@$!%*?&]/.test(password || ''),
  };

  const onSubmit = async (data: SignupFormData) => {
    setIsSubmitting(true);
    try {
      const {confirmPassword, ...signupData} = data;
      await signup(signupData);
      navigate('/login', {
        state: {message: '회원가입이 완료되었습니다. 로그인해주세요.'}
      });
    } catch (error: any) {
      if (error.response?.status === 409) {
        setError('username', {
          type: 'manual',
          message: '이미 존재하는 사용자명입니다'
        });
      } else if (error.response?.data?.field === 'email') {
        setError('email', {
          type: 'manual',
          message: '이미 사용 중인 이메일입니다'
        });
      } else {
        setError('root', {
          type: 'manual',
          message: '회원가입 중 오류가 발생했습니다'
        });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
      <div
          className="min-h-screen flex items-center justify-center bg-gradient-to-br from-coffee-50 to-coffee-100 px-4 py-12">
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
              새로운 계정을 만들어보세요
            </p>
          </div>

          {/* 회원가입 폼 */}
          <div className="bg-white py-8 px-6 shadow-xl rounded-lg sm:px-10">
            <form className="space-y-5" onSubmit={handleSubmit(onSubmit)}>
              {/* Root 에러 메시지 */}
              {errors.root && (
                  <div
                      className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md text-sm">
                    {errors.root.message}
                  </div>
              )}

              {/* 이름 입력 (First Name & Last Name) */}
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label htmlFor="firstName" className="block text-sm font-medium text-gray-700">
                    이름
                  </label>
                  <div className="mt-1">
                    <input
                        {...register('firstName')}
                        type="text"
                        autoComplete="given-name"
                        className={clsx(
                            "appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400",
                            "focus:outline-none focus:ring-coffee-500 focus:border-coffee-500 sm:text-sm",
                            errors.firstName
                                ? "border-red-300 text-red-900"
                                : "border-gray-300"
                        )}
                        placeholder="이름"
                    />
                    {errors.firstName && (
                        <p className="mt-1 text-xs text-red-600">
                          {errors.firstName.message}
                        </p>
                    )}
                  </div>
                </div>

                <div>
                  <label htmlFor="lastName" className="block text-sm font-medium text-gray-700">
                    성
                  </label>
                  <div className="mt-1">
                    <input
                        {...register('lastName')}
                        type="text"
                        autoComplete="family-name"
                        className={clsx(
                            "appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400",
                            "focus:outline-none focus:ring-coffee-500 focus:border-coffee-500 sm:text-sm",
                            errors.lastName
                                ? "border-red-300 text-red-900"
                                : "border-gray-300"
                        )}
                        placeholder="성"
                    />
                    {errors.lastName && (
                        <p className="mt-1 text-xs text-red-600">
                          {errors.lastName.message}
                        </p>
                    )}
                  </div>
                </div>
              </div>

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
                      placeholder="영문, 숫자, 언더스코어 사용 가능"
                  />
                  {errors.username && (
                      <p className="mt-1 text-xs text-red-600">
                        {errors.username.message}
                      </p>
                  )}
                </div>
              </div>

              {/* 이메일 입력 */}
              <div>
                <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                  이메일
                </label>
                <div className="mt-1">
                  <input
                      {...register('email')}
                      type="email"
                      autoComplete="email"
                      className={clsx(
                          "appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400",
                          "focus:outline-none focus:ring-coffee-500 focus:border-coffee-500 sm:text-sm",
                          errors.email
                              ? "border-red-300 text-red-900"
                              : "border-gray-300"
                      )}
                      placeholder="example@coffeedia.com"
                  />
                  {errors.email && (
                      <p className="mt-1 text-xs text-red-600">
                        {errors.email.message}
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
                      autoComplete="new-password"
                      className={clsx(
                          "appearance-none block w-full px-3 py-2 pr-10 border rounded-md shadow-sm placeholder-gray-400",
                          "focus:outline-none focus:ring-coffee-500 focus:border-coffee-500 sm:text-sm",
                          errors.password
                              ? "border-red-300 text-red-900"
                              : "border-gray-300"
                      )}
                      placeholder="8자 이상, 대소문자, 숫자, 특수문자 포함"
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
                </div>

                {/* 비밀번호 강도 표시 */}
                {password && (
                    <div className="mt-2 space-y-1">
                      <div className="flex items-center text-xs">
                        {passwordStrength.hasMinLength ? (
                            <Check className="h-3 w-3 text-green-500 mr-1"/>
                        ) : (
                            <X className="h-3 w-3 text-gray-300 mr-1"/>
                        )}
                        <span
                            className={passwordStrength.hasMinLength ? 'text-green-600' : 'text-gray-400'}>
                      8자 이상
                    </span>
                      </div>
                      <div className="flex items-center text-xs">
                        {passwordStrength.hasUpperCase && passwordStrength.hasLowerCase ? (
                            <Check className="h-3 w-3 text-green-500 mr-1"/>
                        ) : (
                            <X className="h-3 w-3 text-gray-300 mr-1"/>
                        )}
                        <span
                            className={passwordStrength.hasUpperCase && passwordStrength.hasLowerCase ? 'text-green-600' : 'text-gray-400'}>
                      대소문자 포함
                    </span>
                      </div>
                      <div className="flex items-center text-xs">
                        {passwordStrength.hasNumber ? (
                            <Check className="h-3 w-3 text-green-500 mr-1"/>
                        ) : (
                            <X className="h-3 w-3 text-gray-300 mr-1"/>
                        )}
                        <span
                            className={passwordStrength.hasNumber ? 'text-green-600' : 'text-gray-400'}>
                      숫자 포함
                    </span>
                      </div>
                      <div className="flex items-center text-xs">
                        {passwordStrength.hasSpecialChar ? (
                            <Check className="h-3 w-3 text-green-500 mr-1"/>
                        ) : (
                            <X className="h-3 w-3 text-gray-300 mr-1"/>
                        )}
                        <span
                            className={passwordStrength.hasSpecialChar ? 'text-green-600' : 'text-gray-400'}>
                      특수문자 포함
                    </span>
                      </div>
                    </div>
                )}

                {errors.password && (
                    <p className="mt-1 text-xs text-red-600">
                      {errors.password.message}
                    </p>
                )}
              </div>

              {/* 비밀번호 확인 */}
              <div>
                <label htmlFor="confirmPassword"
                       className="block text-sm font-medium text-gray-700">
                  비밀번호 확인
                </label>
                <div className="mt-1 relative">
                  <input
                      {...register('confirmPassword')}
                      type={showConfirmPassword ? "text" : "password"}
                      autoComplete="new-password"
                      className={clsx(
                          "appearance-none block w-full px-3 py-2 pr-10 border rounded-md shadow-sm placeholder-gray-400",
                          "focus:outline-none focus:ring-coffee-500 focus:border-coffee-500 sm:text-sm",
                          errors.confirmPassword
                              ? "border-red-300 text-red-900"
                              : "border-gray-300"
                      )}
                      placeholder="비밀번호를 다시 입력하세요"
                  />
                  <button
                      type="button"
                      className="absolute inset-y-0 right-0 pr-3 flex items-center"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  >
                    {showConfirmPassword ? (
                        <EyeOff className="h-4 w-4 text-gray-400"/>
                    ) : (
                        <Eye className="h-4 w-4 text-gray-400"/>
                    )}
                  </button>
                  {errors.confirmPassword && (
                      <p className="mt-1 text-xs text-red-600">
                        {errors.confirmPassword.message}
                      </p>
                  )}
                </div>
              </div>

              {/* 약관 동의 */}
              <div className="flex items-start">
                <input
                    id="agree-terms"
                    name="agree-terms"
                    type="checkbox"
                    className="h-4 w-4 text-coffee-600 focus:ring-coffee-500 border-gray-300 rounded mt-0.5"
                    required
                />
                <label htmlFor="agree-terms" className="ml-2 block text-sm text-gray-900">
                  <Link to="/terms" className="text-coffee-600 hover:text-coffee-500">
                    이용약관
                  </Link>
                  {' '}및{' '}
                  <Link to="/privacy" className="text-coffee-600 hover:text-coffee-500">
                    개인정보처리방침
                  </Link>
                  에 동의합니다
                </label>
              </div>

              {/* 회원가입 버튼 */}
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
                        회원가입 중...
                      </>
                  ) : (
                      '회원가입'
                  )}
                </button>
              </div>
            </form>

            {/* 로그인 링크 */}
            <div className="mt-6">
              <div className="text-center">
              <span className="text-sm text-gray-600">
                이미 계정이 있으신가요?{' '}
                <Link
                    to="/login"
                    className="font-medium text-coffee-600 hover:text-coffee-500"
                >
                  로그인
                </Link>
              </span>
              </div>
            </div>
          </div>
        </div>
      </div>
  );
};

export default SignupPage;
