#!/bin/bash

# =========================
# KONFIGURATION
# =========================
BASE_URL="http://localhost:8080"
COOKIE_FILE="cookies.txt"

MEDIA_ID="145e2998-b308-4ee4-87e5-3c5994392877"
MEDIA_ID_2="57916e79-db50-43b1-b0b6-956cde2037fb"

USER_ID="1c100921-3c6e-4593-90d4-e63d40a02309"
RATING_ID="08276ea9-3f37-4dfb-a6f8-4dbfce805d49"

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
# START
# =========================
rm -f "$COOKIE_FILE"

print_step "LOGIN mit gültigen Zugangsdaten"
post "$BASE_URL/users/login" '{
  "username": "admin",
  "password": "test123"
}'

# =========================
# MEDIA
# =========================
print_step "ALLE MEDIA ABRUFEN"
get "$BASE_URL/media"

print_step "SPEZIFISCHES MEDIUM ABRUFEN"
get "$BASE_URL/media/$MEDIA_ID_2"

print_step "ALLE FILME"
get "$BASE_URL/media/movies"

print_step "ALLE SPIELE"
get "$BASE_URL/media/games"

print_step "ALLE SERIEN"
get "$BASE_URL/media/series"

# =========================
# USER
# =========================
print_step "USER PROFIL"
get "$BASE_URL/users/profile"

# =========================
# RATINGS
# =========================
print_step "ALLE RATINGS EINES MEDIUMS"
get "$BASE_URL/ratings/media/$MEDIA_ID_2"

print_step "ALLE RATINGS EINES USERS"
get "$BASE_URL/ratings/users/$USER_ID"

print_step "NEUES RATING HINZUFÜGEN"
post "$BASE_URL/ratings/media/$MEDIA_ID" '{
  "stars": 5,
  "comment": "TEST DELETE"
}'

print_step "RATINGS DES USERS NACH DEM HINZUFÜGEN"
get "$BASE_URL/ratings/users/$USER_ID"

print_step "RATING STATUS UPDATEN"
put "$BASE_URL/ratings/media/$RATING_ID" '{}'

# =========================
# MEDIA UPDATE
# =========================
print_step "MEDIUM VOR UPDATE"
get "$BASE_URL/media/$MEDIA_ID"

print_step "MEDIUM AKTUALISIEREN"
put "$BASE_URL/media/$MEDIA_ID" "{
  \"id\": \"$MEDIA_ID\",
  \"title\": \"Updated Media Title\",
  \"description\": \"Beschreibung wurde geändert\",
  \"mediaType\": \"MOVIE\",
  \"releaseYear\": 2024
}"

print_step "MEDIUM NACH UPDATE"
get "$BASE_URL/media/$MEDIA_ID"

# =========================
# FAVORITEN
# =========================
print_step "FAVORITEN DES USERS"
get "$BASE_URL/favorites/users"

print_step "FAVORIT HINZUFÜGEN"
post "$BASE_URL/favorites/media/$MEDIA_ID_2"

print_step "FAVORIT ENTFERNEN"
delete "$BASE_URL/favorites/media/$MEDIA_ID_2"

echo
echo "API-Tests abgeschlossen"
