-- 테스트용 사용자 데이터
INSERT INTO users (user_id, username, email, first_name, last_name, status, created_at, updated_at)
VALUES ('test-bjorn-id', 'bjorn', 'bjorn@coffeedia.com', 'Bjorn', 'Vinterberg', 'ACTIVE', NOW(),
        NOW()),
       ('test-isabelle-id', 'isabelle', 'isabelle@coffeedia.com', 'Isabelle', 'Dahl', 'ACTIVE',
        NOW(), NOW()),
       ('test-inactive-id', 'inactive', 'inactive@coffeedia.com', 'Inactive', 'User', 'INACTIVE',
        NOW(), NOW());

-- 테스트용 사용자 역할 데이터
INSERT INTO user_roles (user_id, role)
VALUES ('test-bjorn-id', 'customer'),
       ('test-isabelle-id', 'customer'),
       ('test-isabelle-id', 'employee'),
       ('test-inactive-id', 'customer');
