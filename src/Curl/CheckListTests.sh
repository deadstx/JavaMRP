#!/bin/bash

# =========================
# KONFIGURATION
# =========================
BASE_URL="http://localhost:8080"
COOKIE_FILE="cookies.txt"

# =========================
# DYNAMISCHE VARIABLEN
# =========================
USER="TestUser1"
PASSWORD="TestPassword123!"

# Media Einträge (IDs für Bewertungen, Kommentare, Favoriten)
media_ids=(
  "2e508197-8a52-4afa-9a97-521e6a7e8826"
   "3d1e5ed4-7483-47e7-85ca-92280ec6ccac"
   "897436d1-d5e5-4f67-abf5-d19afca07845"

)

# Sternebewertungen pro Media
ratings=(3 5 2)

# Kommentare pro Media
comments=(
  "Gefällt mir sehr gut! Spannend bis zum Ende."
  "Sehr empfehlenswert."
  "Leider enttäuschend."
)

# =========================
# HILFSFUNKTIONEN
# =========================
print_step() {
  echo
  echo "======================================"
  echo "$1"
  echo "======================================"
}

get() {
  curl -s -w "\nHTTP STATUS: %{http_code}\n" \
    -b "$COOKIE_FILE" \
    -X GET "$1"
}

post() {
  curl -s -w "\nHTTP STATUS: %{http_code}\n" \
    -b "$COOKIE_FILE" -c "$COOKIE_FILE" \
    -H "Content-Type: application/json" \
    -X POST "$1" \
    -d "$2"
}

put() {
  curl -s -w "\nHTTP STATUS: %{http_code}\n" \
    -b "$COOKIE_FILE" \
    -H "Content-Type: application/json" \
    -X PUT "$1" \
    -d "$2"
}

delete() {
  curl -s -w "\nHTTP STATUS: %{http_code}\n" \
    -b "$COOKIE_FILE" \
    -X DELETE "$1"
}

# =========================
# COOKIE RESET UND LOGIN
# =========================
rm -f "$COOKIE_FILE"

print_step "LOGIN als $USER"
post "$BASE_URL/users/login" "{
  \"username\": \"$USER\",
  \"password\": \"$PASSWORD\"
}"

# =========================
# MEDIA ROUTEN
# =========================
print_step "Alle Media Einträge abrufen"
get "$BASE_URL/media"

print_step "Einen spezifischen Media Eintrag abrufen"
for media_id in "${media_ids[@]}"; do
  get "$BASE_URL/media/$media_id"
done


print_step "Einen Eintrag bearbeiten"
put "$BASE_URL/media/2e508197-8a52-4afa-9a97-521e6a7e8826" '{
  "director": "Changes director",
  "description": "Changed description",
  "mediaType": "movie",
  "genre": "action",
  "ageRestriction": 12,
  "releaseYear": 2000
}'


print_step "Alle Filme abrufen"
get "$BASE_URL/media/movies"

print_step "Alle Serien abrufen"
get "$BASE_URL/media/series"

print_step "Alle Spiele abrufen"
get "$BASE_URL/media/games"

# =========================
# MEDIA FILTER
# =========================
print_step "Media Einträge nach Titel filtern"
get "$BASE_URL/media/filter/title/Inception"

print_step "Media Einträge nach Genre filtern"
get "$BASE_URL/media/filter/genre/sci-fi"

print_step "Media Einträge nach Erscheinungsjahr filtern"
get "$BASE_URL/media/filter/release_year/2008"

print_step "Media Einträge nach Altersfreigabe filtern"
get "$BASE_URL/media/filter/age_restriction/16"

print_step "Media Einträge nach Mindestbewertung filtern"
get "$BASE_URL/media/filter/min_rating/2"

# =========================
# RATINGS, KOMMENTARE, FAVORITEN
# =========================
for i in "${!media_ids[@]}"; do
  media_id="${media_ids[$i]}"

  print_step "Bewerte Media $media_id"
  post "$BASE_URL/ratings/media/$media_id" "{
    \"stars\": ${ratings[$i]}
  }"

  print_step "Kommentiere Media $media_id"
  post "$BASE_URL/comments/media/$media_id" "{
    \"comment_text\": \"${comments[$i]}\"
  }"

  print_step "Füge Media $media_id zu Favoriten hinzu"
  post "$BASE_URL/favorites/media/$media_id" "{}"
done

# =========================
# BENUTZERPROFIL
# =========================
print_step "Eigenes Profil abrufen"
get "$BASE_URL/profile/users"

print_step "Alle eigenen Bewertungen abrufen"
get "$BASE_URL/ratings/users"

print_step "Alle eigenen Kommentare abrufen"
get "$BASE_URL/comments/users"

print_step "Alle Favoriten abrufen"
get "$BASE_URL/favorites/users"

# =========================
# LEADERBOARD
# =========================
print_step "Top 5 Leaderboard anzeigen"
get "$BASE_URL/leaderboard/5"

# =========================
# EMPFEHLUNGEN
# =========================
print_step "Top 3 Empfehlungen (bezogen auf Lieblingsgenre)"
get "$BASE_URL/recommendations/media"
