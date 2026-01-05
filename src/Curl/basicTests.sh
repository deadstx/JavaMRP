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
# COMMENTS
# =========================
print_step "ALLE COMMENTS EINES MEDIUMS ABRUFEN"
get "$BASE_URL/comments/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"

print_step "ALLE COMMENTS EINES USERS ABRUFEN"
get "$BASE_URL/comments/users"





