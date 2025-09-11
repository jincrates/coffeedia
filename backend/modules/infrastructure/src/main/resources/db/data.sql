-- 사용자 테이블 샘플 데이터 추가
-- 비밀번호는 BCrypt로 암호화되어 있음 ("password" 원문)
INSERT INTO users (id, username, email, password, first_name, last_name, status, created_at,
                   updated_at)
VALUES ('bjorn-user-id', 'bjorn', 'bjorn@coffeedia.com',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Bjorn', 'Vinterberg',
        'ACTIVE', NOW(), NOW()),
       ('isabelle-user-id', 'isabelle', 'isabelle@coffeedia.com',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Isabelle', 'Dahl',
        'ACTIVE', NOW(), NOW()),
       ('admin-user-id', 'admin', 'admin@coffeedia.com',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User', 'ACTIVE',
        NOW(), NOW());

-- 사용자 역할 데이터 추가
INSERT INTO user_roles (id, role)
VALUES ('bjorn-user-id', 'customer'),
       ('isabelle-user-id', 'customer'),
       ('isabelle-user-id', 'employee'),
       ('admin-user-id', 'employee');

-- flavors 테이블 샘플 데이터 삽입
INSERT INTO flavors (id, name, created_at, updated_at)
VALUES (1, '블루베리', NOW(), NOW()),
       (2, '체리', NOW(), NOW()),
       (3, '다크초콜릿', NOW(), NOW()),
       (4, '캐러멜', NOW(), NOW()),
       (5, '바닐라', NOW(), NOW()),
       (6, '아몬드', NOW(), NOW()),
       (7, '헤이즐넛', NOW(), NOW()),
       (8, '레몬', NOW(), NOW()),
       (9, '계피', NOW(), NOW()),
       (10, '허니', NOW(), NOW()),
       (11, '오렌지', NOW(), NOW()),
       (12, '견과류', NOW(), NOW()),
       (13, '꽃향', NOW(), NOW()),
       (14, '와인', NOW(), NOW()),
       (15, '딸기', NOW(), NOW());

-- beans 테이블 샘플 데이터 삽입
INSERT INTO beans (id, user_id, name, origin_country, origin_region, roaster, roast_date, grams,
                   roast_level, process_type, blend_type, is_decaf, memo, status, created_at,
                   updated_at)
VALUES (1, 1, '에티오피아 예가체프', '에티오피아', '예가체프', '스페셜티 로스터', '2024-08-20', 250, 'MEDIUM', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '밝은 산미와 꽃향이 특징적인 원두', 'ACTIVE', NOW(), NOW()),
       (2, 1, '콜롬비아 수프리모', '콜롬비아', '나리뇨', '프리미엄 로스터', '2024-08-19', 200, 'MEDIUM', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '균형잡힌 바디감과 단맛', 'ACTIVE', NOW(), NOW()),
       (3, 1, '브라질 산토스', '브라질', '산토스', '클래식 로스터', '2024-08-18', 300, 'MEDIUM_DARK', 'NATURAL',
        'SINGLE_ORIGIN', FALSE, '견과류 향과 초콜릿 노트', 'ACTIVE', NOW(), NOW()),
       (4, 1, '케냐 AA', '케냐', '키암부', '아프리카 로스터', '2024-08-17', 180, 'MEDIUM', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '강한 산미와 와인 같은 바디감', 'ACTIVE', NOW(), NOW()),
       (5, 1, '과테말라 안티구아', '과테말라', '안티구아', '중앙아메리카 로스터', '2024-08-16', 220, 'MEDIUM_DARK', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '스파이시하고 풀바디', 'ACTIVE', NOW(), NOW()),
       (6, 1, '하우스 블렌드', '복합', '블렌드', '하우스 로스터', '2024-08-15', 500, 'MEDIUM', 'WASHED', 'BLEND',
        FALSE, '매일 마시기 좋은 균형잡힌 블렌드', 'ACTIVE', NOW(), NOW()),
       (7, 1, '디카페인 콜롬비아', '콜롬비아', '우일라', '디카페인 전문 로스터', '2024-08-14', 150, 'MEDIUM', 'WASHED',
        'SINGLE_ORIGIN', TRUE, '카페인 없이도 풍부한 맛', 'ACTIVE', NOW(), NOW()),
       (8, 1, '자메이카 블루마운틴', '자메이카', '블루마운틴', '프리미엄 로스터', '2024-08-13', 100, 'LIGHT', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '세계 최고급 원두 중 하나', 'ACTIVE', NOW(), NOW()),
       (9, 1, '하와이 코나', '미국', '하와이 코나', '태평양 로스터', '2024-08-12', 120, 'MEDIUM', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '부드럽고 달콤한 맛', 'ACTIVE', NOW(), NOW()),
       (10, 1, '예멘 모카', '예멘', '모카', '중동 로스터', '2024-08-11', 200, 'DARK', 'NATURAL', 'SINGLE_ORIGIN',
        FALSE, '와인 같은 바디와 초콜릿 향', 'INACTIVE', NOW(), NOW());

-- bean_flavors 테이블 샘플 데이터 삽입 (다대다 관계)
INSERT INTO bean_flavors (id, bean_id, flavor_id, created_at, updated_at)
VALUES
-- 에티오피아 예가체프: 블루베리, 꽃향, 레몬
(1, 1, 1, NOW(), NOW()),
(2, 1, 13, NOW(), NOW()),
(3, 1, 8, NOW(), NOW()),

-- 콜롬비아 수프리모: 캐러멜, 바닐라, 견과류
(4, 2, 4, NOW(), NOW()),
(5, 2, 5, NOW(), NOW()),
(6, 2, 12, NOW(), NOW()),

-- 브라질 산토스: 다크초콜릿, 아몬드, 헤이즐넛
(7, 3, 3, NOW(), NOW()),
(8, 3, 6, NOW(), NOW()),
(9, 3, 7, NOW(), NOW()),

-- 케냐 AA: 체리, 와인, 레몬
(10, 4, 2, NOW(), NOW()),
(11, 4, 14, NOW(), NOW()),
(12, 4, 8, NOW(), NOW()),

-- 과테말라 안티구아: 다크초콜릿, 계피, 오렌지
(13, 5, 3, NOW(), NOW()),
(14, 5, 9, NOW(), NOW()),
(15, 5, 11, NOW(), NOW()),

-- 하우스 블렌드: 캐러멜, 견과류, 바닐라
(16, 6, 4, NOW(), NOW()),
(17, 6, 12, NOW(), NOW()),
(18, 6, 5, NOW(), NOW()),

-- 디카페인 콜롬비아: 캐러멜, 허니
(19, 7, 4, NOW(), NOW()),
(20, 7, 10, NOW(), NOW()),

-- 자메이카 블루마운틴: 꽃향, 허니, 딸기
(21, 8, 13, NOW(), NOW()),
(22, 8, 10, NOW(), NOW()),
(23, 8, 15, NOW(), NOW()),

-- 하와이 코나: 바닐라, 허니, 견과류
(24, 9, 5, NOW(), NOW()),
(25, 9, 10, NOW(), NOW()),
(26, 9, 12, NOW(), NOW()),

-- 예멘 모카: 다크초콜릿, 와인, 체리
(27, 10, 3, NOW(), NOW()),
(28, 10, 14, NOW(), NOW()),
(29, 10, 2, NOW(), NOW());



-- 레시피 관련 샘플 데이터 (3가지 레시피만)
-- 실행 순서: 태그 → 레시피 → 재료 → 레시피 단계 → 레시피-태그 매핑

-- =============================================================================
-- 1. 태그 데이터 (tags 테이블)
-- =============================================================================
INSERT INTO tags (name)
VALUES ('초보자'),
       ('간단함'),
       ('진한맛'),
       ('부드러운맛'),
       ('산미'),
       ('에티오피아');

-- =============================================================================
-- 2. 레시피 메인 데이터 (recipes 테이블) - 3가지만
-- =============================================================================
INSERT INTO recipes (user_id, category, title, thumbnail_url, description, serving, tips, status,
                     created_at, updated_at)
VALUES (1, 'HAND_DRIP', 'V60으로 만드는 에티오피아 원두 핸드드립', 'https://example.com/v60-ethiopia.jpg',
        '에티오피아 원두의 꽃향기와 산미를 살린 클래식한 V60 핸드드립 레시피입니다.', 1, '물 온도는 92-94도가 적당하며, 원두는 중간 굵기로 분쇄하세요.',
        'ACTIVE', NOW(), NOW()),

       (2, 'ESPRESSO', '클래식 에스프레소 추출법', 'https://example.com/classic-espresso.jpg',
        '완벽한 에스프레소 추출을 위한 기본 레시피입니다. 크레마가 풍부하고 균형잡힌 맛을 만들어냅니다.', 1, '추출 시간 25-30초, 추출량 30ml 기준입니다.',
        'ACTIVE', NOW(), NOW()),

       (3, 'COLD_BREW', '12시간 콜드브루 농축액', 'https://example.com/12h-coldbrew.jpg',
        '12시간 저온 추출로 만드는 진한 콜드브루 농축액입니다.', 4, '원두는 굵게 분쇄하고, 1:8 비율로 12시간 냉장고에서 추출하세요.', 'ACTIVE',
        NOW(), NOW());

-- =============================================================================
-- 3. 재료 데이터 (ingredients 테이블)
-- =============================================================================

-- 레시피 1: V60 에티오피아 원두 핸드드립
INSERT INTO ingredients (recipe_id, name, amount, unit, buy_url)
VALUES (1, '에티오피아 원두 (예가체프)', 20.0, 'g', 'https://shop.example.com/ethiopia-yirgacheffe'),
       (1, '필터된 물', 300.0, 'ml', NULL),
       (1, 'V60 드리퍼', 1.0, '개', 'https://shop.example.com/v60-dripper'),
       (1, 'V60 필터', 1.0, '장', 'https://shop.example.com/v60-filter');

-- 레시피 2: 클래식 에스프레소
INSERT INTO ingredients (recipe_id, name, amount, unit, buy_url)
VALUES (2, '에스프레소 블렌드 원두', 18.0, 'g', 'https://shop.example.com/espresso-blend'),
       (2, '필터된 물', 30.0, 'ml', NULL);

-- 레시피 3: 12시간 콜드브루
INSERT INTO ingredients (recipe_id, name, amount, unit, buy_url)
VALUES (3, '콜드브루용 원두 (굵은 분쇄)', 100.0, 'g', 'https://shop.example.com/coldbrew-beans'),
       (3, '차가운 물', 800.0, 'ml', NULL),
       (3, '콜드브루 추출기', 1.0, '개', 'https://shop.example.com/coldbrew-maker');

-- =============================================================================
-- 4. 레시피 단계 데이터 (recipe_steps 테이블)
-- =============================================================================

-- 레시피 1: V60 에티오피아 원두 핸드드립 단계
INSERT INTO recipe_steps (recipe_id, sort_order, image_url, description)
VALUES (1, 1, 'https://example.com/steps/v60-step1.jpg', '에티오피아 원두 20g을 중간 굵기로 분쇄합니다.'),
       (1, 2, 'https://example.com/steps/v60-step2.jpg',
        'V60 드리퍼에 필터를 설치하고, 92-94도의 뜨거운 물로 필터를 린싱합니다.'),
       (1, 3, 'https://example.com/steps/v60-step3.jpg', '분쇄된 원두를 필터에 넣고 가운데를 살짝 눌러 우물을 만듭니다.'),
       (1, 4, 'https://example.com/steps/v60-step4.jpg',
        '30초간 블루밍: 가운데부터 시작해서 40ml의 물을 부어 30초간 기다립니다.'),
       (1, 5, 'https://example.com/steps/v60-step5.jpg', '2차 푸어링: 총 150ml가 되도록 천천히 물을 부어줍니다.'),
       (1, 6, 'https://example.com/steps/v60-step6.jpg', '3차 푸어링: 나머지 110ml를 부어 총 300ml를 완성합니다.');

-- 레시피 2: 클래식 에스프레소 단계
INSERT INTO recipe_steps (recipe_id, sort_order, image_url, description)
VALUES (2, 1, 'https://example.com/steps/espresso-step1.jpg', '에스프레소용 원두 18g을 곱게 분쇄합니다.'),
       (2, 2, 'https://example.com/steps/espresso-step2.jpg', '포터필터에 원두를 넣고 평평하게 고르게 펴줍니다.'),
       (2, 3, 'https://example.com/steps/espresso-step3.jpg', '탬퍼를 사용해 30파운드 압력으로 눌러 평평하게 탬핑합니다.'),
       (2, 4, 'https://example.com/steps/espresso-step4.jpg', '에스프레소 머신에 포터필터를 장착하고 추출을 시작합니다.'),
       (2, 5, 'https://example.com/steps/espresso-step5.jpg', '25-30초 동안 30ml의 에스프레소가 추출되도록 합니다.');

-- 레시피 3: 12시간 콜드브루 단계
INSERT INTO recipe_steps (recipe_id, sort_order, image_url, description)
VALUES (3, 1, 'https://example.com/steps/coldbrew-step1.jpg', '콜드브루용 원두 100g을 굵게 분쇄합니다.'),
       (3, 2, 'https://example.com/steps/coldbrew-step2.jpg', '콜드브루 메이커에 분쇄된 원두를 넣습니다.'),
       (3, 3, 'https://example.com/steps/coldbrew-step3.jpg', '차가운 물 800ml를 천천히 부어 원두가 잠기도록 합니다.'),
       (3, 4, 'https://example.com/steps/coldbrew-step4.jpg', '뚜껑을 덮고 냉장고에서 12시간 동안 추출합니다.'),
       (3, 5, 'https://example.com/steps/coldbrew-step5.jpg', '필터나 체를 사용해 원두를 걸러낸 후 농축액을 완성합니다.');

-- =============================================================================
-- 5. 레시피-태그 매핑 데이터 (recipe_tags 테이블)
-- =============================================================================

-- V60 에티오피아 레시피 태그
INSERT INTO recipe_tags (recipe_id, tag_id)
VALUES (1, 1), -- 초보자
       (1, 5), -- 산미
       (1, 6);
-- 에티오피아

-- 클래식 에스프레소 태그
INSERT INTO recipe_tags (recipe_id, tag_id)
VALUES (2, 3);
-- 진한맛

-- 12시간 콜드브루 태그
INSERT INTO recipe_tags (recipe_id, tag_id)
VALUES (3, 1), -- 초보자
       (3, 2); -- 간단함
