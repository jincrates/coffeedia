import {useAuth as useAuthContext} from '@/contexts/AuthContext';
import {useNavigate} from 'react-router-dom';
import toast from 'react-hot-toast';

/**
 * 작성자 정보를 포함한 엔티티 인터페이스
 */
interface EntityWithAuthor {
  createdBy?: {
    id: number;
    username: string;
    email: string;
  };
  // 또는 userId, authorId 등의 필드가 있을 수 있음
  userId?: number;
  authorId?: number;
}

/**
 * 인증 관련 액션과 상태를 제공하는 커스텀 훅
 */
export const useAuth = () => {
  const {isAuthenticated, user, isLoading, login, signup, logout, refreshUser} = useAuthContext();
  const navigate = useNavigate();

  /**
   * 인증이 필요한 작업을 수행하기 전에 로그인 상태를 확인하고,
   * 비로그인 시 로그인 페이지로 이동시키는 함수
   */
  const requireAuth = (action?: () => void, message?: string) => {
    if (!isAuthenticated) {
      toast.error(message || '로그인이 필요한 기능입니다.');
      navigate('/login');
      return false;
    }

    if (action) {
      action();
    }
    return true;
  };

  /**
   * 사용자가 특정 엔티티의 소유자인지 확인하는 함수
   */
  const isOwner = (entity: EntityWithAuthor): boolean => {
    if (!user || !isAuthenticated) {
      return false;
    }

    // createdBy 객체로 작성자 정보가 있는 경우
    if (entity.createdBy) {
      return entity.createdBy.id === user.id;
    }

    // userId 필드로 작성자 정보가 있는 경우
    if (entity.userId !== undefined) {
      return entity.userId === user.id;
    }

    // authorId 필드로 작성자 정보가 있는 경우
    if (entity.authorId !== undefined) {
      return entity.authorId === user.id;
    }

    return false;
  };

  /**
   * 소유권이 필요한 작업을 수행하기 전에 소유권을 확인하는 함수
   */
  const requireOwnership = (entity: EntityWithAuthor, action?: () => void, message?: string) => {
    if (!requireAuth(undefined, '로그인이 필요한 기능입니다.')) {
      return false;
    }

    if (!isOwner(entity)) {
      toast.error(message || '본인이 등록한 콘텐츠만 수정/삭제할 수 있습니다.');
      return false;
    }

    if (action) {
      action();
    }
    return true;
  };

  /**
   * 로그인이 필요한 액션인지 확인하는 함수
   */
  const isAuthRequiredAction = (actionType: 'view' | 'create' | 'edit' | 'delete'): boolean => {
    return ['create', 'edit', 'delete'].includes(actionType);
  };

  /**
   * 액션 타입에 따라 실행 가능 여부를 반환하는 함수
   */
  const canPerformAction = (actionType: 'view' | 'create' | 'edit' | 'delete', entity?: EntityWithAuthor): boolean => {
    if (actionType === 'view') {
      return true; // 조회는 항상 가능
    }

    if (actionType === 'create') {
      return isAuthenticated; // 생성은 로그인 필요
    }

    if (actionType === 'edit' || actionType === 'delete') {
      return isAuthenticated && entity ? isOwner(entity) : false; // 수정/삭제는 로그인 + 소유권 필요
    }

    return false;
  };

  return {
    // 인증 상태
    isAuthenticated,
    user,
    isLoading,

    // 인증 액션
    login,
    signup,
    logout,
    refreshUser,

    // 권한 체크 함수들
    requireAuth,
    requireOwnership,
    isOwner,
    isAuthRequiredAction,
    canPerformAction,
  };
};

export default useAuth;
