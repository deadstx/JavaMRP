#!/bin/bash

# Cookies-Datei löschen
rm -f cookies.txt

echo "Try Login with correct credentials"
curl -i -c cookies.txt -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"test123\"}"
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


echo "RATING EINES MEDIUMS LÖSCHEN"

curl -b cookies.txt -i -X DELETE \
  http://localhost:8080/ratings/media/4b9e28d4-5c0a-495b-80f5-e4e38b5e08e4

echo -e "\n"


echo "RATING EINES MEDIUMS LÖSCHEN"

curl -b cookies.txt -i -X UPDATE \
  http://localhost:8080/ratings/media/5ca5c924-f829-43d6-a790-335387cd72ce

echo -e "\n"





