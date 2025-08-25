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
INSERT INTO beans (id, name, origin_country, origin_region, roaster, roast_date, grams, roast_level,
                   process_type, blend_type, is_decaf, memo, status, access_type, created_at,
                   updated_at)
VALUES (1, '에티오피아 예가체프', '에티오피아', '예가체프', '스페셜티 로스터', '2024-08-20', 250, 'MEDIUM', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '밝은 산미와 꽃향이 특징적인 원두', 'ACTIVE', 'PUBLIC', NOW(), NOW()),
       (2, '콜롬비아 수프리모', '콜롬비아', '나리뇨', '프리미엄 로스터', '2024-08-19', 200, 'MEDIUM', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '균형잡힌 바디감과 단맛', 'ACTIVE', 'PUBLIC', NOW(), NOW()),
       (3, '브라질 산토스', '브라질', '산토스', '클래식 로스터', '2024-08-18', 300, 'MEDIUM_DARK', 'NATURAL',
        'SINGLE_ORIGIN', FALSE, '견과류 향과 초콜릿 노트', 'ACTIVE', 'PRIVATE', NOW(), NOW()),
       (4, '케냐 AA', '케냐', '키암부', '아프리카 로스터', '2024-08-17', 180, 'MEDIUM', 'WASHED', 'SINGLE_ORIGIN',
        FALSE, '강한 산미와 와인 같은 바디감', 'ACTIVE', 'PUBLIC', NOW(), NOW()),
       (5, '과테말라 안티구아', '과테말라', '안티구아', '중앙아메리카 로스터', '2024-08-16', 220, 'MEDIUM_DARK', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '스파이시하고 풀바디', 'ACTIVE', 'PUBLIC', NOW(), NOW()),
       (6, '하우스 블렌드', '복합', '블렌드', '하우스 로스터', '2024-08-15', 500, 'MEDIUM', 'WASHED', 'BLEND', FALSE,
        '매일 마시기 좋은 균형잡힌 블렌드', 'ACTIVE', 'PUBLIC', NOW(), NOW()),
       (7, '디카페인 콜롬비아', '콜롬비아', '우일라', '디카페인 전문 로스터', '2024-08-14', 150, 'MEDIUM', 'WASHED',
        'SINGLE_ORIGIN', TRUE, '카페인 없이도 풍부한 맛', 'ACTIVE', 'PUBLIC', NOW(), NOW()),
       (8, '자메이카 블루마운틴', '자메이카', '블루마운틴', '프리미엄 로스터', '2024-08-13', 100, 'LIGHT', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '세계 최고급 원두 중 하나', 'ACTIVE', 'PRIVATE', NOW(), NOW()),
       (9, '하와이 코나', '미국', '하와이 코나', '태평양 로스터', '2024-08-12', 120, 'MEDIUM', 'WASHED',
        'SINGLE_ORIGIN', FALSE, '부드럽고 달콤한 맛', 'ACTIVE', 'PRIVATE', NOW(), NOW()),
       (10, '예멘 모카', '예멘', '모카', '중동 로스터', '2024-08-11', 200, 'DARK', 'NATURAL', 'SINGLE_ORIGIN',
        FALSE, '와인 같은 바디와 초콜릿 향', 'INACTIVE', 'PUBLIC', NOW(), NOW());

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
