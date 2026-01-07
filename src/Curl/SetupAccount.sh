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

rm -f "$COOKIE_FILE"

# =========================
# MEDIA ERSTELLEN MIT WECHSELNDEN ACCOUNTS
# =========================

users=(user1 user2 user3 user4 user5)
password="test123"

titles=(
  "Inception" "The Last of Us" "The Witcher 3" "Breaking Bad" "God of War Ragnarök"
  "Interstellar" "Stranger Things" "Red Dead Redemption 2" "The Dark Knight" "Cyberpunk 2077"
)
directors=(
  "Christopher Nolan" "Neil Druckmann" "Adam Badowski" "Vince Gilligan" "Eric Williams"
  "Christopher Nolan" "The Duffer Brothers" "Dan Houser" "Christopher Nolan" "Adam Badowski"
)
descriptions=(
  "Ein Dieb stiehlt Informationen aus den Träumen anderer."
  "Eine postapokalyptische Reise durch die USA."
  "Ein Monsterjäger sucht seinen Platz in einer dunklen Welt."
  "Ein Chemielehrer wird zum Drogenbaron."
  "Ein nordischer Gott kämpft gegen sein Schicksal."
  "Eine Reise durch Raum und Zeit zur Rettung der Menschheit."
  "Mysteriöse Ereignisse in einer Kleinstadt."
  "Ein Outlaw kämpft ums Überleben im Wilden Westen."
  "Ein maskierter Held bekämpft das Verbrechen in Gotham."
  "Eine düstere Zukunftsvision voller Technologie und Chaos."
)
mediaTypes=(movie series game series game movie series game movie game)
genres=(sci-fi drama rpg crime action sci-fi mystery western action rpg)
age_restrictions=(12 16 18 16 18 12 16 18 12 18)
release_years=(1999 2003 1989 2010 2022 2004 2018 1999 2000 2008)

print_step "10 Media Einträge mit wechselnden Accounts erstellen"

for i in {0..9}; do
  user_index=$((i % 5))
  current_user=${users[$user_index]}

  echo "==== LOGIN als $current_user ===="

  # Cookies löschen, um neuen Login zu erzwingen
  rm -f "$COOKIE_FILE"

  post "$BASE_URL/users/login" "{
    \"username\": \"$current_user\",
    \"password\": \"$password\"
  }"

  echo "Erstelle Media Eintrag $((i+1)) als $current_user"

  post "$BASE_URL/media/" "{
    \"title\": \"${titles[$i]}\",
    \"director\": \"${directors[$i]}\",
    \"description\": \"${descriptions[$i]}\",
    \"mediaType\": \"${mediaTypes[$i]}\",
    \"genre\": \"${genres[$i]}\",
    \"ageRestriction\": ${age_restrictions[$i]},
    \"releaseYear\": ${release_years[$i]}
  }"

done














