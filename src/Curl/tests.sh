#!/bin/bash

# Cookies-Datei löschen
rm -f cookies.txt

echo "Try Login with correct credentials"
curl -i -c cookies.txt -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"testUser1\",\"password\":\"test123\"}"
echo -e "\n"


echo "Try GET media "

curl -b cookies.txt -X GET http://localhost:8080/media
echo -e "\n"

echo "Try GET Specific media "

curl -b cookies.txt -X GET http://localhost:8080/media/57916e79-db50-43b1-b0b6-956cde2037fb
echo -e "\n"

echo "Try GET ALL movies "

curl -b cookies.txt -X GET http://localhost:8080/media/movies
echo -e "\n"

echo "Try GET ALL games "

curl -b cookies.txt -X GET http://localhost:8080/media/games
echo -e "\n"

echo "Try GET Profile "

curl -b cookies.txt -X GET http://localhost:8080/users/profile
echo -e "\n"


echo "ALLE RATINGS EINES MEDIUMS "

curl -b cookies.txt -X GET http://localhost:8080/ratings/media/57916e79-db50-43b1-b0b6-956cde2037fb
echo -e "\n"


echo "ALLE RATINGS EINES USERS "

curl -b cookies.txt -X GET http://localhost:8080/ratings/users/1c100921-3c6e-4593-90d4-e63d40a02309
echo -e "\n"


echo "NEUES RATING HINZUFÜGEN"

curl -b cookies.txt -i -X POST \
  http://localhost:8080/ratings/media/145e2998-b308-4ee4-87e5-3c5994392877 \
  -H "Content-Type: application/json" \
  -d '{
    "stars": 5,
    "comment": "TEST DELETE"
  }'

echo -e "\n"

echo "ALLE RATINGS EINES USERS "

curl -b cookies.txt -X GET http://localhost:8080/ratings/users/1c100921-3c6e-4593-90d4-e63d40a02309
echo -e "\n"


echo "STATUS EINES RATINGS UPDATEN"

curl -b cookies.txt -i -X PUT \
  http://localhost:8080/ratings/media/08276ea9-3f37-4dfb-a6f8-4dbfce805d49

echo -e "\n"




MEDIA_ID="145e2998-b308-4ee4-87e5-3c5994392877"
BASE_URL="http://localhost:8080"

echo "== MEDIA VOR UPDATE =="
curl -b cookies.txt -X GET "$BASE_URL/media/$MEDIA_ID"
echo -e "\n"

echo "== MEDIA UPDATE (PUT) =="
curl -i -b cookies.txt -X PUT "$BASE_URL/media/$MEDIA_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "'"$MEDIA_ID"'",
    "title": "Updated Media Title",
    "description": "Beschreibung wurde geändert",
    "mediaType": "MOVIE",
    "releaseYear": 2024
  }'
echo -e "\n"

echo "== MEDIA NACH UPDATE =="
curl -b cookies.txt -X GET "$BASE_URL/media/$MEDIA_ID"
echo -e "\n"


echo "== FAVORITES EINES USERS =="
curl -b cookies.txt -X GET "$BASE_URL/favorites/users"
echo -e "\n"





