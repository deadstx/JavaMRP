-- ======================================================
-- Init Script für Media Ratings Platform (MRP)
-- PostgreSQL (UUID + ENUM Version)
-- ======================================================

-- ======================================================
-- Extensions
-- ======================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ======================================================
-- ENUMs
-- ======================================================
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'media_type_enum') THEN
            CREATE TYPE media_type_enum AS ENUM ('movie', 'series', 'game');
        END IF;
    END$$;

-- ======================================================
-- Drop tables (clean start)
-- ======================================================
DROP TABLE IF EXISTS rating_likes;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS media;
DROP TABLE IF EXISTS users;

-- ======================================================
-- Users
-- ======================================================
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- Media
-- ======================================================
CREATE TABLE media (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       title VARCHAR(200) UNIQUE NOT NULL,
                       director VARCHAR(100) NOT NULL,
                       description TEXT NOT NULL,
                       media_type media_type_enum NOT NULL,
                       release_year INT NOT NULL,
                       genres VARCHAR(100) NOT NULL,
                       age_restriction INT NOT NULL,
                       creator_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- Ratings (stars only)
-- One rating per user per media
-- ======================================================
CREATE TABLE ratings (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         media_id UUID NOT NULL REFERENCES media(id) ON DELETE CASCADE,
                         stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         UNIQUE (user_id, media_id)
);

-- ======================================================
-- Rating Likes
-- ======================================================
CREATE TABLE rating_likes (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              rating_id UUID NOT NULL REFERENCES ratings(id) ON DELETE CASCADE,
                              user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              UNIQUE (rating_id, user_id)
);

-- ======================================================
-- Comments (separate from ratings)
-- ======================================================
CREATE TABLE comments (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          comment_text TEXT NOT NULL,
                          user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          media_id UUID NOT NULL REFERENCES media(id) ON DELETE CASCADE,
                          is_confirmed BOOLEAN DEFAULT FALSE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- Favorites
-- ======================================================
CREATE TABLE favorites (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           media_id UUID NOT NULL REFERENCES media(id) ON DELETE CASCADE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           UNIQUE (user_id, media_id)
);

-- ======================================================
-- Indexes (Performance)
-- ======================================================
CREATE INDEX idx_media_type ON media(media_type);
CREATE INDEX idx_ratings_media ON ratings(media_id);
CREATE INDEX idx_comments_media ON comments(media_id);
CREATE INDEX idx_favorites_user ON favorites(user_id);

