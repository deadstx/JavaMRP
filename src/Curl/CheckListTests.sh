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
# REGISTRIERUNG
# =========================
rm -f "$COOKIE_FILE"

#print_step "Neuen Account erstellen"
#post "$BASE_URL/users/register" '{
#  "username": "Max123",
#  "password": "TestPassword123!"
#}'
#
#print_step "Account erstellen, dessen USERNAME VERGEBEN ist"
#post "$BASE_URL/users/register" '{
#  "username": "TestUser1",
#  "password": "TestPassword123!"
#}'

# =========================
# AUTHENTIFIZIERUNG
# =========================
print_step "----- AUTH TESTS -----"

print_step "LOGIN mit ungültigen Zugangsdaten"
post "$BASE_URL/users/login" '{
  "username": "falscherUsername",
  "password": "test123"
}'

print_step "LOGIN mit gültigen Zugangsdaten"
post "$BASE_URL/users/login" '{
  "username": "TestUser1",
  "password": "TestPassword123!"
}'

# =========================
# MEDIA TESTS
# =========================
print_step "----- MEDIA TESTS -----"

print_step "Einen Media Eintrag hinzufügen, der schon existiert"
post "$BASE_URL/media/" '{
  "title": "Inception",
  "director": "Test",
  "description": "Test",
  "mediaType": "movie",
  "genre": "sci-fi",
  "ageRestriction": 16,
  "releaseYear": 2008
}'

print_step "Alle Media Einträge abrufen (Filme, Serien, Spiele)"
get "$BASE_URL/media"

print_step "Einen spezifischen Media Eintrag abrufen"
get "$BASE_URL/media/015b8377-2641-4892-aa9e-87d0ac86ce75"

print_step "Alle Filme abrufen"
get "$BASE_URL/media/movies"

print_step "Alle Spiele abrufen"
get "$BASE_URL/media/games"

print_step "Alle Serien abrufen"
get "$BASE_URL/media/series"

print_step "Einen Media Eintrag ändern"
put "$BASE_URL/media/015b8377-2641-4892-aa9e-87d0ac86ce75" '{
  "director": "Christopher Nolan",
  "description": "Amerikanischer Sci-Fi Film",
  "mediaType": "movie",
  "genre": "sci-fi",
  "ageRestriction": 16,
  "releaseYear": 2010
}'

# =========================
# MEDIA FILTER
# =========================
print_step "----- MEDIA FILTER -----"

print_step "Media Einträge nach Titel filtern"
get "$BASE_URL/media/filter/title/inception"

print_step "Media Einträge nach Genre filtern"
get "$BASE_URL/media/filter/genre/sci-fi"

print_step "Media Einträge nach Erscheinungsjahr filtern"
get "$BASE_URL/media/filter/release_year/2008"

print_step "Media Einträge nach Altersfreigabe filtern"
get "$BASE_URL/media/filter/age_restriction/16"

print_step "Media Einträge nach Mindestbewertung filtern"
get "$BASE_URL/media/filter/min_rating/2"

# =========================
# BENUTZERPROFIL
# =========================
print_step "----- BENUTZERPROFIL TESTS -----"

print_step "Eigenes Profil abrufen"
get "$BASE_URL/profile/users"

print_step "Profil eines bestimmten Users abrufen"
get "$BASE_URL/profile/users/a4fe28dc-fed2-43e7-8411-ef953cb1b869"

# =========================
# BEWERTUNGEN
# =========================
print_step "----- BEWERTUNG TESTS -----"

print_step "Einen Media Eintrag bewerten"
post "$BASE_URL/ratings/media/015b8377-2641-4892-aa9e-87d0ac86ce75" '{
  "stars": 3
}'

print_step "Alle Bewertungen eines bestimmten Media Eintrags abrufen"
get "$BASE_URL/ratings/media/015b8377-2641-4892-aa9e-87d0ac86ce75"

print_step "Alle eigenen Bewertungen abrufen"
get "$BASE_URL/ratings/users"

print_step "Alle Bewertungen eines bestimmten Users abrufen"
get "$BASE_URL/ratings/users/a4fe28dc-fed2-43e7-8411-ef953cb1b869"

print_step "Letzten X eigenen Bewertungen abrufen"
get "$BASE_URL/ratings/users/history/2"

print_step "Eine Bewertung löschen"
delete "$BASE_URL/ratings/media/015b8377-2641-4892-aa9e-87d0ac86ce75"

# =========================
# KOMMENTARE
# =========================
print_step "----- KOMMENTAR TESTS -----"

print_step "Einen Media Eintrag kommentieren"
post "$BASE_URL/comments/media/015b8377-2641-4892-aa9e-87d0ac86ce75" '{
  "comment_text": "Gefällt mir sehr gut! Spannend bis zum Ende."
}'

print_step "Einen Kommentar bestätigen (nur Ersteller darf)"
put "$BASE_URL/comments/media/015b8377-2641-4892-aa9e-87d0ac86ce75"

print_step "Alle bestätigten Kommentare eines Media Eintrags abrufen"
get "$BASE_URL/comments/media/015b8377-2641-4892-aa9e-87d0ac86ce75"

print_step "Alle eigenen Kommentare abrufen (auch unbestätigte)"
get "$BASE_URL/comments/users"

print_step "Einen Kommentar löschen"
delete "$BASE_URL/comments/media/015b8377-2641-4892-aa9e-87d0ac86ce75"

# =========================
# FAVORITEN
# =========================
print_step "----- FAVORITEN TESTS -----"

print_step "Einen Media Eintrag als Favorit hinzufügen"
post "$BASE_URL/favorites/media/015b8377-2641-4892-aa9e-87d0ac86ce75"

print_step "Alle Favoriten des angemeldeten Benutzers anzeigen"
get "$BASE_URL/favorites/users"

print_step "Einen Media Eintrag aus den Favoriten entfernen"
delete "$BASE_URL/favorites/media/015b8377-2641-4892-aa9e-87d0ac86ce75"

# =========================
# LEADERBOARD
# =========================
print_step "----- LEADERBOARD TESTS -----"

print_step "Top 5 Leaderboard anzeigen"
get "$BASE_URL/leaderboard/5"

# =========================
# EMPFEHLUNGEN
# =========================
print_step "----- EMPFEHLUNGEN TESTS -----"

print_step "Top 3 Empfehlungen (bezogen auf Lieblingsgenre)"
get "$BASE_URL/recommendations/media"
