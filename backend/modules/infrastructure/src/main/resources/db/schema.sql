DROP TABLE IF EXISTS bean_flavors;
DROP TABLE IF EXISTS beans;
DROP TABLE IF EXISTS flavors;

-- flavors 테이블 생성
CREATE TABLE IF NOT EXISTS flavors
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- beans 테이블 생성
CREATE TABLE IF NOT EXISTS beans
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255)             NOT NULL,
    origin_country VARCHAR(40)              NOT NULL,
    origin_region  VARCHAR(40)              NULL,
    roaster        VARCHAR(255)             NOT NULL,
    roast_date     DATE,
    grams          INTEGER                  NOT NULL DEFAULT 0,
    roast_level    VARCHAR(20)              NOT NULL,
    process_type   VARCHAR(20)              NOT NULL,
    blend_type     VARCHAR(20)              NOT NULL,
    is_decaf       BOOLEAN                  NOT NULL DEFAULT FALSE,
    memo           VARCHAR(200),
    status         VARCHAR(20)              NOT NULL,
    access_type    VARCHAR(20)              NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- bean_flavors 테이블 생성
CREATE TABLE IF NOT EXISTS bean_flavors
(
    id        BIGSERIAL PRIMARY KEY,
    bean_id   BIGINT NOT NULL,
    flavor_id BIGINT NOT NULL,
    CONSTRAINT fk_bean_flavors_bean FOREIGN KEY (bean_id) REFERENCES beans (id) ON DELETE CASCADE,
    CONSTRAINT fk_bean_flavors_flavor FOREIGN KEY (flavor_id) REFERENCES flavors (id) ON DELETE CASCADE,
    UNIQUE (bean_id, flavor_id)
);
