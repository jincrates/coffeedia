import {format, parseISO} from 'date-fns';

// 날짜 포맷팅
export const formatDate = (dateString: string, formatStr = 'yyyy년 MM월 dd일'): string => {
  return format(parseISO(dateString), formatStr);
};

// 날짜시간 포맷팅
export const formatDateTime = (dateString: string, formatStr = 'yyyy년 MM월 dd일 HH:mm'): string => {
  return format(parseISO(dateString), formatStr);
};

// 상대적 날짜 포맷팅 (예: 3일 전, 1주일 전)
export const formatRelativeDate = (dateString: string): string => {
  const date = parseISO(dateString);
  const now = new Date();
  const diffInMs = now.getTime() - date.getTime();
  const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));

  if (diffInDays === 0) return '오늘';
  if (diffInDays === 1) return '어제';
  if (diffInDays < 7) return `${diffInDays}일 전`;
  if (diffInDays < 30) return `${Math.floor(diffInDays / 7)}주일 전`;
  if (diffInDays < 365) return `${Math.floor(diffInDays / 30)}개월 전`;
  return `${Math.floor(diffInDays / 365)}년 전`;
};

// 한국어 enum 변환
export const getRoastLevelKorean = (level: string): string => {
  const roastLevels: Record<string, string> = {
    LIGHT: '라이트 로스팅',
    MEDIUM_LIGHT: '미디움 라이트',
    MEDIUM: '미디움',
    MEDIUM_DARK: '미디움 다크',
    DARK: '다크 로스팅',
  };
  return roastLevels[level] || level;
};

export const getProcessTypeKorean = (type: string): string => {
  const processTypes: Record<string, string> = {
    WASHED: '워시드',
    NATURAL: '내추럴',
    HONEY: '허니',
    WET_HULLED: '웻 헐드',
    ANAEROBIC: '혐기발효',
  };
  return processTypes[type] || type;
};

export const getBlendTypeKorean = (type: string): string => {
  const blendTypes: Record<string, string> = {
    SINGLE_ORIGIN: '싱글 오리진',
    BLEND: '블렌드',
  };
  return blendTypes[type] || type;
};

export const getCategoryTypeKorean = (type: string): string => {
  const categoryTypes: Record<string, string> = {
    HAND_DRIP: '핸드드립',
    ESPRESSO: '에스프레소',
    COLD_BREW: '콜드브루',
    MOCHA_POT: '모카포트',
  };
  return categoryTypes[type] || type;
};

export const getEquipmentTypeKorean = (type: string): string => {
  const equipmentTypes: Record<string, string> = {
    GRINDER: '그라인더',
    DRIPPER: '드리퍼',
    MACHINE: '머신',
    SCALE: '저울',
    KETTLE: '케틀',
    ACCESSORY: '액세서리',
  };
  return equipmentTypes[type] || type;
};
