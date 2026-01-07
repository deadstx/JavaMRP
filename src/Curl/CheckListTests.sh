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
# REGISTRIEREN
# =========================
rm -f "$COOKIE_FILE"

print_step "NEUEN Account erstellen"
post "$BASE_URL/users/register" '{
  "username": "larshosnedl",
  "password": "test123"
}'

print_step "Account erstellen, dessen USERNAME VERGEBEN ist"
post "$BASE_URL/users/register" '{
  "username": "larshosnedl",
  "password": "test123"
}'


# =========================
# ANMELDEN
# =========================

print_step "LOGIN mit UNGÜLTIGEN Zugangsdaten"
post "$BASE_URL/users/login" '{
  "username": "falscherUsername",
  "password": "test123"
}'

print_step "LOGIN mit GÜLTIGEN Zugangsdaten"
post "$BASE_URL/users/login" '{
  "username": "admin",
  "password": "test123"
}'

# =========================
# MEDIA TESTS
# =========================
print_step "Alle Media Einträge abrufen (Filme, Serien und Spiele)"
get "$BASE_URL/media"

print_step "Einen SPEZIFISCHEN Media Eintrag abrufen"
get "$BASE_URL/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"

print_step "ALLE FILME"
get "$BASE_URL/media/movies"

print_step "ALLE SPIELE"
get "$BASE_URL/media/games"

print_step "ALLE SERIEN"
get "$BASE_URL/media/series"

print_step "Media Einträge FILTERN nach Altersfreigabe"
get "$BASE_URL/media/filter/age_restriction/16"

print_step "Media Einträge FILTERN nach Erscheinungsjahr"
get "$BASE_URL/media/filter/release_year/2008"



# =========================
# KOMMENTAR TESTS
# =========================
print_step "Alle Kommentare zu einem Media Eintrag abrufen"
get "$BASE_URL/comments/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"

print_step "Alle Kommentare eines Benutzers abrufen"
get "$BASE_URL/comments/users"



# =========================
# BENUTZERPROFIL TESTS
# =========================
print_step "Eigenes Profil abrufen"
get "$BASE_URL/profile/users"

print_step "Profil eines bestimmten Users abrufen"
get "$BASE_URL/profile/users/11111111-1111-1111-1111-111111111111"



# =========================
# BEWERTUNG TESTS
# =========================
print_step "Einen Media Eintrag bewerten"
post "$BASE_URL/ratings/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" '{
  "stars": 3
}'

print_step "Alle Bewertungen eines bestimmten Media Eintrages abrufen"
get "$BASE_URL/ratings/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"

print_step "Alle Bewertungen abrufen, die man selbst abgegeben hat"
get "$BASE_URL/ratings/users"

print_step "Alle Bewertungen abrufen, die ein bestimmter User abgegeben hat"
get "$BASE_URL/ratings/users/22222222-2222-2222-2222-222222222222"

print_step "Eine Bewertung löschen"
delete "$BASE_URL/ratings/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"



# =========================
# KOMMENTAR TESTS ( GEHEN NOCH NICHT ALLE)
# =========================

print_step "Einen Media Eintrag kommentieren"
post "$BASE_URL/comments/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" '{
  "comment_text": "Gefällt mir sehr gut! Spannend bis zum Ende."
}'

print_step "Einen Kommentar bestätigen"
post "$BASE_URL/comments/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"

print_step "Alle Kommentare eines bestimmten Media Eintrages abrufen"
get "$BASE_URL/comments/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"

print_step "Alle Kommentare abrufen, die man selbst geschrieben hat"
get "$BASE_URL/comments/users"


# =========================
# FAVORITEN TESTS
# =========================

print_step "Einen Media Eintrag als Favorit hinzufügen"
post "$BASE_URL/favorites/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"

print_step "Alle Favoriten des angemeldeten Benutzers anzeigen"
get "$BASE_URL/favorites/users"

print_step "Einen Media Eintrag aus den Favoriten entfernen"
delete "$BASE_URL/favorites/media/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"












