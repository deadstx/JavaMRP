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
# USERS UND MEDIA
# =========================
rm -f "$COOKIE_FILE"

users=(TestUser1 TestUser2 TestUser3 TestUser4 TestUser5)
password="TestPassword123!"

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

# =========================
# 5 Accounts erstellen
# =========================
print_step "5 Accounts erstellen"

for i in {0..4}; do
  current_user=${users[$i]}

  echo "==== ERSTELLE ACCOUNT: $current_user ===="

  # Account erstellen
  response=$(post "$BASE_URL/users/register" "{
    \"username\": \"$current_user\",
    \"password\": \"$password\"
  }")

  if echo "$response" | grep -q "201"; then
    echo "Account $current_user erfolgreich erstellt!"
  else
    echo "FEHLER: Account $current_user konnte nicht erstellt werden."
    echo "$response"
  fi
done

# =========================
# 10 Media Einträge erstellen
# =========================
print_step "10 Media Einträge erstellen"

for i in {0..9}; do
  user_index=$((i % 5))
  current_user=${users[$user_index]}

  # Login
  rm -f "$COOKIE_FILE"
  post "$BASE_URL/users/login" "{
    \"username\": \"$current_user\",
    \"password\": \"$password\"
  }"

  # Media Eintrag erstellen
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
