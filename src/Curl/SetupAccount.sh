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

media_ids=(
  c56a6273-fc47-42e9-b72e-d54f90c5e88a
  0fb1c40e-fc70-414b-a209-9f64cf601802
  e45ba185-4ca4-43b0-98ef-3bc6c3c8c6a3
  5f6bd727-c6af-4a61-88e4-e37d644f75d1
  167bd34f-0ea2-4a8e-8420-f8f4759d1a5a
  e75e9532-f21c-47ae-92d7-b508cd785247
  ac43d4c1-d0ea-4458-9b90-8968edf90e80
  1a7202da-4527-4435-b1dd-f914a594e5a5
  3c0bab8f-3da9-4fa3-b9f3-2c516134e4b7
  f14f4a64-3dc6-4b00-bd59-0fc734f5b1a7
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



#print_step "10 Media Einträge mit wechselnden Accounts erstellen"
#
#for i in {0..9}; do
#  user_index=$((i % 5))
#  current_user=${users[$user_index]}
#
#  echo "==== LOGIN als $current_user ===="
#
#  # Cookies löschen, um neuen Login zu erzwingen
#  rm -f "$COOKIE_FILE"
#
#  post "$BASE_URL/users/login" "{
#    \"username\": \"$current_user\",
#    \"password\": \"$password\"
#  }"
#
#  echo "Erstelle Media Eintrag $((i+1)) als $current_user"
#
#  post "$BASE_URL/media/" "{
#    \"title\": \"${titles[$i]}\",
#    \"director\": \"${directors[$i]}\",
#    \"description\": \"${descriptions[$i]}\",
#    \"mediaType\": \"${mediaTypes[$i]}\",
#    \"genre\": \"${genres[$i]}\",
#    \"ageRestriction\": ${age_restrictions[$i]},
#    \"releaseYear\": ${release_years[$i]}
#  }"
#
#done

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















