-- ======================================================
-- Init Script für Media Ratings Platform (MRP)
-- PostgreSQL (UUID Version)
-- ======================================================

-- UUID Extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ======================================================
-- Drop tables if they exist (clean start)
-- ======================================================
DROP TABLE IF EXISTS rating_likes;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS media;
DROP TABLE IF EXISTS users;

-- ======================================================
-- Users Table
-- ======================================================
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- Media Table
-- ======================================================
CREATE TABLE media (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       title VARCHAR(200) NOT NULL,
                       director VARCHAR(50) NOT NULL,
                       description TEXT,
                       media_type VARCHAR(20) NOT NULL, -- movie | series | game
                       release_year INT,
                       genres VARCHAR(100),
                       age_restriction INT,
                       creator_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- Ratings Table
-- ======================================================
CREATE TABLE ratings (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         media_id UUID NOT NULL REFERENCES media(id) ON DELETE CASCADE,
                         stars INT CHECK (stars BETWEEN 1 AND 5),
                         comment TEXT,
                         is_confirmed BOOLEAN DEFAULT FALSE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================================================
-- Rating Likes Table
-- ======================================================
CREATE TABLE rating_likes (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              rating_id UUID NOT NULL REFERENCES ratings(id) ON DELETE CASCADE,
                              user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              UNIQUE (rating_id, user_id)
);

-- ======================================================
-- Favorites Table
-- ======================================================
CREATE TABLE favorites (
                           id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           media_id UUID NOT NULL REFERENCES media(id) ON DELETE CASCADE,
                           UNIQUE (user_id, media_id)
);

-- ======================================================
-- Insert Test Users
-- ======================================================
INSERT INTO users (id, username, password) VALUES
                                               ('11111111-1111-1111-1111-111111111111', 'alice', 'password123'),
                                               ('22222222-2222-2222-2222-222222222222', 'bob', 'password123'),
                                               ('33333333-3333-3333-3333-333333333333', 'charlie', 'password123');

-- ======================================================
-- Insert Test Media
-- ======================================================
INSERT INTO media (
    id, title, director, description, media_type, release_year, genres, age_restriction, creator_id
) VALUES
      ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
       'The Matrix', 'Wachowski', 'Sci-fi action movie',
       'movie', 1999, 'Action,Sci-Fi', 16,
       '11111111-1111-1111-1111-111111111111'),

      ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
       'Breaking Bad', 'Vince Gilligan', 'Crime drama series',
       'series', 2008, 'Drama,Crime', 18,
       '22222222-2222-2222-2222-222222222222'),

      ('cccccccc-cccc-cccc-cccc-cccccccccccc',
       'The Witcher 3', 'CD Projekt', 'Fantasy RPG game',
       'game', 2015, 'RPG,Fantasy', 16,
       '33333333-3333-3333-3333-333333333333');

-- ======================================================
-- Insert Test Ratings
-- ======================================================
INSERT INTO ratings (
    id, user_id, media_id, stars, comment, is_confirmed
) VALUES
      ('aaaa1111-1111-1111-1111-111111111111',
       '11111111-1111-1111-1111-111111111111',
       'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
       5, 'Amazing movie!', TRUE),

      ('bbbb2222-2222-2222-2222-222222222222',
       '22222222-2222-2222-2222-222222222222',
       'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
       4, 'Great, but a bit confusing', TRUE),

      ('cccc3333-3333-3333-3333-333333333333',
       '33333333-3333-3333-3333-333333333333',
       'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
       5, 'Best series ever', TRUE);

-- ======================================================
-- Insert Test Favorites
-- ======================================================
INSERT INTO favorites (user_id, media_id) VALUES
                                              ('11111111-1111-1111-1111-111111111111',
                                               'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
                                              ('22222222-2222-2222-2222-222222222222',
                                               'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
                                              ('33333333-3333-3333-3333-333333333333',
                                               'cccccccc-cccc-cccc-cccc-cccccccccccc');

-- ======================================================
-- Insert Test Rating Likes
-- ======================================================
INSERT INTO rating_likes (rating_id, user_id) VALUES
                                                  ('aaaa1111-1111-1111-1111-111111111111',
                                                   '22222222-2222-2222-2222-222222222222'),

                                                  ('aaaa1111-1111-1111-1111-111111111111',
                                                   '33333333-3333-3333-3333-333333333333'),

                                                  ('cccc3333-3333-3333-3333-333333333333',
                                                   '11111111-1111-1111-1111-111111111111');
