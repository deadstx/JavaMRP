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
  f4170005-8415-46d0-9fea-d31e83560e5d
  b51ea323-c0ec-48cd-89f7-ff55c266776f
  46b7a2fe-72e6-469c-a985-a168811cb653
  7db621ca-77d3-47c1-923e-7b4569de1f5f
  f390b658-1adc-4ad4-9238-3d03024f908b
  efa48211-9caa-406b-b6fd-daf91b676ea8
  35e33634-518f-4935-815a-3463e6f2d938
  7f9f76f4-ee67-4ee5-b5ed-189b0f7dc4c2
  98170eb0-6cb5-44d8-959f-b9f47d6238df
  47653553-f397-4ba7-9489-a1b54a43ba13
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
