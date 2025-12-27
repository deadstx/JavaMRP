-- USERS
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- MEDIA
CREATE TABLE media (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       media_type VARCHAR(20) NOT NULL, -- MOVIE, SERIES, GAME
                       release_year INT,
                       age_restriction INT,
                       creator_user_id INT NOT NULL,
                       CONSTRAINT fk_media_creator
                           FOREIGN KEY (creator_user_id)
                               REFERENCES users(id)
                               ON DELETE CASCADE
);

-- GENRES
CREATE TABLE genres (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(50) UNIQUE NOT NULL
);

-- MEDIA <-> GENRES (n:m)
CREATE TABLE media_genres (
                              media_id INT NOT NULL,
                              genre_id INT NOT NULL,
                              PRIMARY KEY (media_id, genre_id),
                              FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
                              FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- RATINGS
CREATE TABLE ratings (
                         id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL,
                         media_id INT NOT NULL,
                         stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5),
                         comment TEXT,
                         confirmed BOOLEAN DEFAULT FALSE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
                         UNIQUE (user_id, media_id)
);

-- RATING LIKES
CREATE TABLE rating_likes (
                              rating_id INT NOT NULL,
                              user_id INT NOT NULL,
                              PRIMARY KEY (rating_id, user_id),
                              FOREIGN KEY (rating_id) REFERENCES ratings(id) ON DELETE CASCADE,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- FAVORITES
CREATE TABLE favorites (
                           user_id INT NOT NULL,
                           media_id INT NOT NULL,
                           PRIMARY KEY (user_id, media_id),
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);
