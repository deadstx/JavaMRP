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

media_ids=(
  015b8377-2641-4892-aa9e-87d0ac86ce75
  6a989018-22ea-4c4a-b227-818447dd0dc8
  cfe19d44-371b-4b18-8e54-5fdefbbe5f07
  68ae8da1-5933-4a9e-8be4-c8d7de1af751
  375f5ed5-f68f-4a13-a36c-55b9797bdd32
  ab776e09-1f0c-4d48-b4b7-b789010f7672
  38e9ae99-e8cf-4500-89eb-7b487e0229c5
  28525183-dcac-4126-a116-15ee9f5a0263
  758d011d-242d-48b7-ad49-d582fba71f38
  3139da90-cf0e-4207-9119-4daf97de67dc
)

  comments_positive=(
    "Absolut großartig!"
    "Sehr empfehlenswert."
    "Hat mir richtig gut gefallen."
    "Top Umsetzung."
  )

  comments_negative=(
    "Leider enttäuschend."
    "Hatte mehr erwartet."
    "Ziemlich langweilig."
    "Nicht mein Geschmack."
  )

print_step "5 Accounts erstellen"

for i in {0..4}; do
  current_user="TestUser$((i+1))"   # Neue Usernamen generieren
  current_password="TestPassword123!"  # Passwort kann gleich bleiben

  echo "==== ERSTELLE ACCOUNT: $current_user ===="

  # Cookies löschen für neuen Account (optional)
  rm -f "$COOKIE_FILE"

  # Account erstellen
  response=$(post "$BASE_URL/users/register" "{
    \"username\": \"$current_user\",
    \"password\": \"$current_password\"
  }")

  # Prüfen, ob die Registrierung erfolgreich war
  if echo "$response" | grep -q "200"; then
    echo "Account $current_user erfolgreich erstellt!"
  else
    echo "FEHLER: Account $current_user konnte nicht erstellt werden."
    echo "$response"
    continue
  fi

  # Direkt nach Registrierung einloggen, um Cookies zu erhalten
  response=$(post "$BASE_URL/users/login" "{
    \"username\": \"$current_user\",
    \"password\": \"$current_password\"
  }")

  if ! echo "$response" | grep -q "200"; then
    echo "FEHLER: Login für $current_user fehlgeschlagen!"
    echo "$response"
    continue
  fi

  echo "==== Media Eintrag für $current_user erstellen ===="
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

for i in {0..9}; do
  user_index=$((i % 5))
  current_user=${users[$user_index]}
  media_id=${media_ids[$i]}

  echo "==== LOGIN als $current_user ===="
  rm -f "$COOKIE_FILE"

  post "$BASE_URL/users/login" "{
    \"username\": \"$current_user\",
    \"password\": \"$password\"
  }"

  # =========================
  # BEWERTUNG
  # =========================
  stars=$((RANDOM % 5 + 1))

  print_step "Einen Media Eintrag bewerten ($media_id)"

  post "$BASE_URL/ratings/media/$media_id" "{
    \"stars\": $stars
  }"

  # =========================
  # KOMMENTAR (max. 1 pro User & Media)
  # =========================
  if (( RANDOM % 2 )); then
    if (( stars >= 3 )); then
      comment=${comments_positive[$RANDOM % ${#comments_positive[@]}]}
    else
      comment=${comments_negative[$RANDOM % ${#comments_negative[@]}]}
    fi

    print_step "Einen Media Eintrag kommentieren ($media_id)"

    post "$BASE_URL/comments/media/$media_id" "{
      \"comment_text\": \"$comment\"
    }"
  fi

  # =========================
  # FAVORIT (nicht bei allen Medien)
  # =========================
  if (( RANDOM % 3 == 0 )); then
    print_step "Einen Media Eintrag als Favorit hinzufügen ($media_id)"

    post "$BASE_URL/favorites/media/$media_id" "{}"
  fi
done















