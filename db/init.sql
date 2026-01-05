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
                       title VARCHAR(200) NOT NULL,
                       director VARCHAR(100) NOT NULL,
                       description TEXT,
                       media_type VARCHAR(20) NOT NULL,
                       release_year INT,
                       genres VARCHAR(100),
                       age_restriction INT,
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

-- ======================================================
-- Test Users
-- ======================================================
INSERT INTO users (id, username, password) VALUES
                                               ('11111111-1111-1111-1111-111111111111', 'alice',   'hashed_pw_1'),
                                               ('22222222-2222-2222-2222-222222222222', 'bob',     'hashed_pw_2'),
                                               ('33333333-3333-3333-3333-333333333333', 'charlie', 'hashed_pw_3');

-- ======================================================
-- Test Media
-- ======================================================
INSERT INTO media (
    id, title, director, description, media_type,
    release_year, genres, age_restriction, creator_id
) VALUES
      (
          'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
          'The Matrix',
          'Wachowski Sisters',
          'Sci-Fi action classic',
          'movie',
          1999,
          'Action,Sci-Fi',
          16,
          '11111111-1111-1111-1111-111111111111'
      ),
      (
          'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
          'Breaking Bad',
          'Vince Gilligan',
          'Crime drama series',
          'series',
          2008,
          'Drama,Crime',
          18,
          '22222222-2222-2222-2222-222222222222'
      ),
      (
          'cccccccc-cccc-cccc-cccc-cccccccccccc',
          'The Witcher 3',
          'CD Projekt Red',
          'Open world fantasy RPG',
          'game',
          2015,
          'RPG,Fantasy',
          16,
          '33333333-3333-3333-3333-333333333333'
      );

-- ======================================================
-- Test Ratings
-- ======================================================
INSERT INTO ratings (id, user_id, media_id, stars) VALUES
                                                       (
                                                           'aaaa1111-1111-1111-1111-111111111111',
                                                           '11111111-1111-1111-1111-111111111111',
                                                           'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
                                                           5
                                                       ),
                                                       (
                                                           'bbbb2222-2222-2222-2222-222222222222',
                                                           '22222222-2222-2222-2222-222222222222',
                                                           'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
                                                           4
                                                       ),
                                                       (
                                                           'cccc3333-3333-3333-3333-333333333333',
                                                           '33333333-3333-3333-3333-333333333333',
                                                           'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
                                                           5
                                                       );

-- ======================================================
-- Test Comments
-- ======================================================
INSERT INTO comments (comment_text, user_id, media_id, is_confirmed) VALUES
                                                                         (
                                                                             'Amazing movie, still holds up!',
                                                                             '11111111-1111-1111-1111-111111111111',
                                                                             'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
                                                                             TRUE
                                                                         ),
                                                                         (
                                                                             'Great series, intense characters.',
                                                                             '33333333-3333-3333-3333-333333333333',
                                                                             'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
                                                                             TRUE
                                                                         ),
                                                                         (
                                                                             'One of the best RPGs ever made.',
                                                                             '22222222-2222-2222-2222-222222222222',
                                                                             'cccccccc-cccc-cccc-cccc-cccccccccccc',
                                                                             FALSE
                                                                         );

-- ======================================================
-- Test Favorites
-- ======================================================
INSERT INTO favorites (user_id, media_id) VALUES
                                              (
                                                  '11111111-1111-1111-1111-111111111111',
                                                  'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
                                              ),
                                              (
                                                  '22222222-2222-2222-2222-222222222222',
                                                  'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'
                                              ),
                                              (
                                                  '33333333-3333-3333-3333-333333333333',
                                                  'cccccccc-cccc-cccc-cccc-cccccccccccc'
                                              );

-- ======================================================
-- Test Rating Likes
-- ======================================================
INSERT INTO rating_likes (rating_id, user_id) VALUES
                                                  (
                                                      'aaaa1111-1111-1111-1111-111111111111',
                                                      '22222222-2222-2222-2222-222222222222'
                                                  ),
                                                  (
                                                      'aaaa1111-1111-1111-1111-111111111111',
                                                      '33333333-3333-3333-3333-333333333333'
                                                  ),
                                                  (
                                                      'cccc3333-3333-3333-3333-333333333333',
                                                      '11111111-1111-1111-1111-111111111111'
                                                  );
