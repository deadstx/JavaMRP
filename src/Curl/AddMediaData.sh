#!/bin/bash

# =========================
# KONFIGURATION
# =========================
BASE_URL="http://localhost:8080"
COOKIE_FILE="cookies.txt"

# =========================
# HILFSFUNKTIONEN
# =========================
print_step() {
  echo
  echo "======================================"
  echo "$1"
  echo "======================================"
}

post() {
  curl -s -w "\nHTTP STATUS: %{http_code}\n" \
    -b "$COOKIE_FILE" -c "$COOKIE_FILE" \
    -H "Content-Type: application/json" \
    -X POST "$1" \
    -d "$2"
}

# =========================
# START
# =========================
rm -f "$COOKIE_FILE"

print_step "LOGIN"
post "$BASE_URL/users/login" '{
  "username": "admin",
  "password": "test123"
}'

# ======================================================
# USER 1 – 2 MOVIES | 1 SERIES | 3 GAMES
# ======================================================
print_step "USER 1 MEDIAS"

post "$BASE_URL/media/movies/" '{
  "title": "Interstellar",
  "director": "Christopher Nolan",
  "description": "Eine Reise durch Raum und Zeit.",
  "mediaType": "movie",
  "releaseYear": 2014,
  "genre": "Sci-Fi",
  "ageRestriction": "12"
}'

post "$BASE_URL/media/movies/" '{
  "title": "Dune",
  "director": "Denis Villeneuve",
  "description": "Machtkämpfe auf dem Wüstenplaneten Arrakis.",
  "mediaType": "movie",
  "releaseYear": 2021,
  "genre": "Sci-Fi",
  "ageRestriction": "12"
}'

post "$BASE_URL/media/series/" '{
  "title": "Chernobyl",
  "director": "Johan Renck",
  "description": "Die Katastrophe von Tschernobyl.",
  "mediaType": "series",
  "releaseYear": 2019,
  "genre": "Drama",
  "ageRestriction": "16"
}'

post "$BASE_URL/media/games/" '{
  "title": "Half-Life 2",
  "director": "Valve",
  "description": "Sci-Fi Shooter Klassiker.",
  "mediaType": "game",
  "releaseYear": 2004,
  "genre": "Shooter",
  "ageRestriction": "16"
}'

post "$BASE_URL/media/games/" '{
  "title": "Portal 2",
  "director": "Valve",
  "description": "Puzzle-Spiel mit schwarzem Humor.",
  "mediaType": "game",
  "releaseYear": 2011,
  "genre": "Puzzle",
  "ageRestriction": "6"
}'

post "$BASE_URL/media/games/" '{
  "title": "Stellaris",
  "director": "Paradox",
  "description": "Weltraum-Strategie Spiel.",
  "mediaType": "game",
  "releaseYear": 2016,
  "genre": "Strategy",
  "ageRestriction": "12"
}'

# ======================================================
# USER 2 – 1 MOVIE | 2 SERIES | 1 GAME
# ======================================================
print_step "USER 2 MEDIAS"

post "$BASE_URL/media/movies/" '{
  "title": "The Matrix",
  "director": "Wachowski",
  "description": "Was ist Realität?",
  "mediaType": "movie",
  "releaseYear": 1999,
  "genre": "Sci-Fi, Action",
  "ageRestriction": "16"
}'

post "$BASE_URL/media/series/" '{
  "title": "Dark",
  "director": "Baran bo Odar",
  "description": "Zeitreisen in Winden.",
  "mediaType": "series",
  "releaseYear": 2017,
  "genre": "Mystery",
  "ageRestriction": "16"
}'

post "$BASE_URL/media/series/" '{
  "title": "The Boys",
  "director": "Eric Kripke",
  "description": "Superhelden ohne Moral.",
  "mediaType": "series",
  "releaseYear": 2019,
  "genre": "Action",
  "ageRestriction": "18"
}'

post "$BASE_URL/media/games/" '{
  "title": "DOOM Eternal",
  "director": "id Software",
  "description": "Schneller Action-Shooter.",
  "mediaType": "game",
  "releaseYear": 2020,
  "genre": "Shooter",
  "ageRestriction": "18"
}'

# ======================================================
# USER 3 – 3 MOVIES | 1 GAME
# ======================================================
print_step "USER 3 MEDIAS"

post "$BASE_URL/media/movies/" '{
  "title": "Gladiator",
  "director": "Ridley Scott",
  "description": "Rache im alten Rom.",
  "mediaType": "movie",
  "releaseYear": 2000,
  "genre": "Action",
  "ageRestriction": "16"
}'

post "$BASE_URL/media/movies/" '{
  "title": "Braveheart",
  "director": "Mel Gibson",
  "description": "Freiheitskampf der Schotten.",
  "mediaType": "movie",
  "releaseYear": 1995,
  "genre": "Drama",
  "ageRestriction": "16"
}'

post "$BASE_URL/media/movies/" '{
  "title": "Troy",
  "director": "Wolfgang Petersen",
  "description": "Der Trojanische Krieg.",
  "mediaType": "movie",
  "releaseYear": 2004,
  "genre": "History",
  "ageRestriction": "16"
}'

post "$BASE_URL/media/games/" '{
  "title": "God of War",
  "director": "Santa Monica Studio",
  "description": "Mythologisches Actionspiel.",
  "mediaType": "game",
  "releaseYear": 2018,
  "genre": "Action",
  "ageRestriction": "18"
}'

# ======================================================
# USER 4 – 1 MOVIE | 3 SERIES | 2 GAMES
# ======================================================
print_step "USER 4 MEDIAS"

post "$BASE_URL/media/movies/" '{
  "title": "Avatar",
  "director": "James Cameron",
  "description": "Der Planet Pandora.",
  "mediaType": "movie",
  "releaseYear": 2009,
  "genre": "Fantasy",
  "ageRestriction": "12"
}'

post "$BASE_URL/media/series/" '{
  "title": "The Mandalorian",
  "director": "Jon Favreau",
  "description": "Kopfgeldjäger im Star-Wars-Universum.",
  "mediaType": "series",
  "releaseYear": 2019,
  "genre": "Sci-Fi",
  "ageRestriction": "12"
}'

post "$BASE_URL/media/series/" '{
  "title": "Loki",
  "director": "Kate Herron",
  "description": "Der Gott des Schabernacks.",
  "mediaType": "series",
  "releaseYear": 2021,
  "genre": "Fantasy",
  "ageRestriction": "12"
}'

post "$BASE_URL/media/series/" '{
  "title": "House of the Dragon",
  "director": "Ryan Condal",
  "description": "Der Tanz der Drachen.",
  "mediaType": "series",
  "releaseYear": 2022,
  "genre": "Fantasy",
  "ageRestriction": "16"
}'

post "$BASE_URL/media/games/" '{
  "title": "Red Dead Redemption 2",
  "director": "Rockstar Games",
  "description": "Der Wilde Westen.",
  "mediaType": "game",
  "releaseYear": 2018,
  "genre": "Adventure",
  "ageRestriction": "18"
}'

post "$BASE_URL/media/games/" '{
  "title": "GTA V",
  "director": "Rockstar Games",
  "description": "Open-World Crime Game.",
  "mediaType": "game",
  "releaseYear": 2013,
  "genre": "Action",
  "ageRestriction": "18"
}'
