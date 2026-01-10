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
  "username": "Max123",
  "password": "TestPassword123!"
}'

print_step "Account erstellen, dessen USERNAME VERGEBEN ist"
post "$BASE_URL/users/register" '{
  "username": "Max123",
  "password": "TestPassword123!"
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
  "username": "user1",
  "password": "test123"
}'

# =========================
# MEDIA TESTS
# =========================
print_step "Alle Media Einträge abrufen (Filme, Serien und Spiele)"
get "$BASE_URL/media"

print_step "Einen SPEZIFISCHEN Media Eintrag abrufen"
get "$BASE_URL/media/c56a6273-fc47-42e9-b72e-d54f90c5e88a"

print_step "ALLE FILME"
get "$BASE_URL/media/movies"

print_step "ALLE SPIELE"
get "$BASE_URL/media/games"

print_step "ALLE SERIEN"
get "$BASE_URL/media/series"

# =========================
# MEDIA TESTS (FILTERN)
# =========================

print_step "Media Einträge FILTERN nach TITEL"
get "$BASE_URL/media/filter/title/inception"

print_step "Media Einträge FILTERN nach GENRE"
get "$BASE_URL/media/filter/genre/sci-fi"

print_step "Media Einträge FILTERN nach ERSCHEINUNGSJAHR"
get "$BASE_URL/media/filter/release_year/2008"

print_step "Media Einträge FILTERN nach ALTERSFREIGABE"
get "$BASE_URL/media/filter/age_restriction/16"

print_step "Media Einträge FILTERN nach MINDESTE BEWERTUNG"
get "$BASE_URL/media/filter/min_rating/5"

# =========================
# BENUTZERPROFIL TESTS
# =========================
print_step "Eigenes Profil abrufen"
get "$BASE_URL/profile/users"

print_step "Profil eines bestimmten Users abrufen"
get "$BASE_URL/profile/users/57a41ed7-9b9d-442e-b972-7ab035cffc0a"



# =========================
# BEWERTUNG TESTS
# =========================
print_step "Einen Media Eintrag bewerten"
post "$BASE_URL/ratings/media/c56a6273-fc47-42e9-b72e-d54f90c5e88a" '{
  "stars": 3
}'

print_step "Alle Bewertungen eines bestimmten Media Eintrages abrufen"
get "$BASE_URL/ratings/media/c56a6273-fc47-42e9-b72e-d54f90c5e88a"

print_step "Alle Bewertungen abrufen, die man selbst abgegeben hat"
get "$BASE_URL/ratings/users"

print_step "Alle Bewertungen abrufen, die ein bestimmter User abgegeben hat"
get "$BASE_URL/ratings/users/130695af-3d51-45c4-b889-c1f1865afdae"

print_step "Letzten X Bewertungen abrufen, die man selbst abgegeben hat"
get "$BASE_URL/ratings/users/history/2"

print_step "Eine Bewertung löschen"
delete "$BASE_URL/ratings/media/ac43d4c1-d0ea-4458-9b90-8968edf90e80"



# =========================
# KOMMENTAR TESTS ( GEHEN NOCH NICHT ALLE)
# =========================

print_step "Einen Media Eintrag kommentieren"
post "$BASE_URL/comments/media/ac43d4c1-d0ea-4458-9b90-8968edf90e80" '{
  "comment_text": "Gefällt mir sehr gut! Spannend bis zum Ende."
}'

print_step "Einen Kommentar bestätigen (DARF NUR DER ERSTELLER DES MEDIUMS"
put "$BASE_URL/comments/media/ac43d4c1-d0ea-4458-9b90-8968edf90e80"

print_step "Alle Kommentare eines bestimmten Media Eintrages abrufen (NUR BESTÄTIGTE)"
get "$BASE_URL/comments/media/ac43d4c1-d0ea-4458-9b90-8968edf90e80"

print_step "Alle Kommentare abrufen, die man selbst geschrieben hat (AUCH UNBESTÄTIGTE)"
get "$BASE_URL/comments/users"

print_step "Alle Kommentare abrufen, die man selbst geschrieben hat (AUCH UNBESTÄTIGTE)"
delete "$BASE_URL/comments/media/ac43d4c1-d0ea-4458-9b90-8968edf90e80"


# =========================
# FAVORITEN TESTS
# =========================

print_step "Einen Media Eintrag als Favorit hinzufügen"
post "$BASE_URL/favorites/media/ac43d4c1-d0ea-4458-9b90-8968edf90e80"

print_step "Alle Favoriten des angemeldeten Benutzers anzeigen"
get "$BASE_URL/favorites/users"

print_step "Einen Media Eintrag aus den Favoriten entfernen"
delete "$BASE_URL/favorites/media/e75e9532-f21c-47ae-92d7-b508cd785247"


# =========================
# LEADERBOARD TESTS
# =========================

print_step "Top 5 Leaderboard anzeigen "
get "$BASE_URL/leaderboard/5"


# =========================
# EMPFEHLUNGEN TESTS
# =========================

print_step "Top 3 Empfehlungen (bezogen auf fav Genre) "
get "$BASE_URL/recommendations/media"














