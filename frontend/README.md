# Coffeedia Frontend

Coffee + Encyclopedia - 커피를 사랑하는 사람들을 위한 완벽한 동반자

## 🚀 기술 스택

- **React 18** - 사용자 인터페이스 라이브러리
- **TypeScript** - 타입 안전성
- **Vite** - 빠른 빌드 도구
- **Tailwind CSS** - 유틸리티 퍼스트 CSS 프레임워크
- **React Router** - 클라이언트 사이드 라우팅
- **React Query** - 서버 상태 관리
- **Axios** - HTTP 클라이언트
- **React Hook Form** - 폼 관리
- **Lucide React** - 아이콘 라이브러리
- **React Hot Toast** - 알림 시스템

## 📁 프로젝트 구조

```
src/
├── components/          # 재사용 가능한 컴포넌트
│   ├── common/         # 공통 컴포넌트 (Button, Card, Input 등)
│   ├── beans/          # 원두 관련 컴포넌트
│   ├── recipes/        # 레시피 관련 컴포넌트
│   └── equipments/     # 장비 관련 컴포넌트
├── pages/              # 페이지 컴포넌트
├── services/           # API 서비스
├── types/              # TypeScript 타입 정의
├── utils/              # 유틸리티 함수
├── hooks/              # 커스텀 React 훅
├── App.tsx             # 메인 앱 컴포넌트
└── main.tsx            # 애플리케이션 진입점
```

## 🎯 주요 기능

### 1. 원두 관리

- 원두 목록 조회 (페이징 지원)
- 원두 상세 정보 (로스팅 레벨, 가공법, 원산지 등)
- 원두 CRUD 기능
- 검색 및 필터링

### 2. 레시피 관리

- 레시피 목록 조회
- 레시피 생성 및 관리
- 카테고리별 분류 (브루잉, 에스프레소, 디저트)

### 3. 장비 관리

- 커피 장비 등록 및 관리
- 장비 타입별 분류
- 구매 정보 관리

## 🛠️ 컴포넌트 설계 원칙

### 공통 컴포넌트

모든 화면에서 재사용할 수 있도록 설계된 컴포넌트들:

- **Button** - 다양한 variant와 size 지원
- **Card** - 일관된 카드 레이아웃
- **Input** - 폼 입력 필드
- **Navigation** - 전역 네비게이션

### 도메인별 컴포넌트

각 도메인(Bean, Recipe, Equipment)별로 특화된 컴포넌트:

- **BeanCard** - 원두 정보를 표시하는 카드
- **BeanList** - 원두 목록과 로딩/빈 상태 처리
- 향후 확장: **RecipeCard**, **EquipmentCard** 등

### 상태 관리

- **React Query** - 서버 상태 관리 및 캐싱
- **useState** - 로컬 컴포넌트 상태
- **React Hook Form** - 폼 상태 관리

## 🎨 디자인 시스템

### 색상 팔레트

```css
coffee: {
  50: '#fdf7f0',
  100: '#fbeee0',
  200: '#f6d9bd',
  300: '#f0c094',
  400: '#e8a067',
  500: '#e18a47',
  600: '#d3713c', /* Primary */
  700: '#af5a34',
  800: '#8c4a31',
  900: '#723e2a',
}
```

### 컴포넌트 변형

- **Button**: primary, secondary, outline, ghost, destructive
- **Size**: sm, md, lg
- **Shadow**: none, sm, md, lg

## 🚀 시작하기

### 설치

```bash
cd frontend
npm install
```

### 개발 서버 실행

```bash
npm run dev
```

### 빌드

```bash
npm run build
```

### 타입 체크

```bash
npm run lint
```

## 🔧 환경 설정

### Vite 프록시

개발 환경에서 백엔드 API 연동을 위한 프록시 설정:

```typescript
server: {
  proxy: {
    '/api'
  :
    {
      target: 'http://localhost:8090',
          changeOrigin
    :
      true,
    }
  ,
  }
,
}
```

### TypeScript 경로 매핑

```json
"paths": {
"@/*": ["src/*"]
}
```

## 📡 API 연동

### 서비스 레이어

각 도메인별로 API 서비스 클래스를 제공:

- **beanService** - 원두 관련 API
- **recipeService** - 레시피 관련 API
- **equipmentService** - 장비 관련 API

### 타입 안전성

백엔드 API 응답과 완전히 일치하는 TypeScript 타입 정의

### 에러 핸들링

- Axios 인터셉터를 통한 전역 에러 처리
- React Query의 에러 상태 활용
- Toast 알림을 통한 사용자 피드백

## 🎯 향후 계획

### 단기 계획

- [ ] 원두 생성/수정 모달 구현
- [ ] 레시피 상세 화면 구현
- [ ] 장비 관리 기능 구현
- [ ] 검색 및 필터링 기능
- [ ] 페이지네이션 구현

### 중기 계획

- [ ] 다크 모드 지원
- [ ] 반응형 디자인 개선
- [ ] PWA 기능 추가
- [ ] 이미지 업로드 기능
- [ ] 소셜 공유 기능

### 장기 계획

- [ ] 사용자 인증 시스템
- [ ] 실시간 알림
- [ ] 커뮤니티 기능
- [ ] 다국어 지원
