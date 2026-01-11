#!/bin/bash
# =========================
# KONFIGURATION
# =========================
BASE_URL="http://localhost:8080"
COOKIE_FILE="cookies.txt"

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

rm -f "$COOKIE_FILE"

# =========================
# USERNAMES UND PASSWORT
# =========================
users=(TestUser1 TestUser2 TestUser3 TestUser4 TestUser5)
password="TestPassword123!"

# =========================
# MEDIA IDs einfügen
# =========================
media_ids=(
  2e508197-8a52-4afa-9a97-521e6a7e8826
  3d1e5ed4-7483-47e7-85ca-92280ec6ccac
  897436d1-d5e5-4f67-abf5-d19afca07845
  9f8c9315-9ad1-4059-9960-b0ac9980ed4a
  89f7c276-5432-4747-871f-a5374a2b916a
  9a985190-cb3e-4475-84b9-235166af9f1e
  a022fb62-3a07-4f98-96b5-dc841faf3c0d
  79c5661d-ff27-4c86-b6d3-2475b6dc404d
  0d58131b-a3da-48f7-8a39-8e0f5017ebc7
  6a2b3efc-25ea-4755-b75c-735e8fb0bb56
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

# =========================
# BEWERTUNGEN, KOMMENTARE, FAVORITEN
# =========================
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

  # Bewertung
  stars=$((RANDOM % 5 + 1))
  print_step "Bewerte Media $media_id mit $stars Sternen"
  post "$BASE_URL/ratings/media/$media_id" "{
    \"stars\": $stars
  }"

  # Kommentar (1 pro User & Media)
  if (( RANDOM % 2 )); then
    if (( stars >= 3 )); then
      comment=${comments_positive[$RANDOM % ${#comments_positive[@]}]}
    else
      comment=${comments_negative[$RANDOM % ${#comments_negative[@]}]}
    fi
    print_step "Kommentiere Media $media_id"
    post "$BASE_URL/comments/media/$media_id" "{
      \"comment_text\": \"$comment\"
    }"
  fi

  # Favorit (nicht alle)
  if (( RANDOM % 3 == 0 )); then
    print_step "Füge Media $media_id zu Favoriten hinzu"
    post "$BASE_URL/favorites/media/$media_id" "{}"
  fi
done
