-- ============================================================
-- Pelts & Paws — PostgreSQL Schema
-- All statements are idempotent (CREATE TABLE IF NOT EXISTS)
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50)  UNIQUE NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS pets (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    species     VARCHAR(255) NOT NULL,
    breed       VARCHAR(255),
    age         INTEGER,
    gender      VARCHAR(50),
    description TEXT,
    image_url   TEXT,
    owner_id    BIGINT NOT NULL REFERENCES users(id)      ON DELETE CASCADE,
    category_id BIGINT          REFERENCES categories(id) ON DELETE SET NULL,
    gems        INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS pet_health (
    id           BIGSERIAL PRIMARY KEY,
    pet_id       BIGINT UNIQUE NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    weight       DOUBLE PRECISION,
    height       DOUBLE PRECISION,
    last_checkup DATE,
    notes        TEXT,
    created_at   TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS pet_vaccinations (
    pet_health_id BIGINT      NOT NULL REFERENCES pet_health(id) ON DELETE CASCADE,
    vaccination   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS gems (
    id        BIGSERIAL PRIMARY KEY,
    pet_id    BIGINT NOT NULL REFERENCES pets(id)   ON DELETE CASCADE,
    user_id   BIGINT NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    type      VARCHAR(20) NOT NULL,
    amount    INTEGER NOT NULL,
    earned_at TIMESTAMP DEFAULT NOW()
);
