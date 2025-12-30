#!/bin/bash

# Cookies-Datei löschen
rm -f cookies.txt

echo "Try Login with correct credentials"
curl -i -c cookies.txt -X POST http://localhost:8080/login \
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

curl -b cookies.txt -X GET http://localhost:8080/profile
echo -e "\n"


echo "Try GET RATING by ratingID "

curl -b cookies.txt -X GET http://localhost:8080/ratings/8bec9030-0981-41f2-b776-dcf5d8b49553
echo -e "\n"


echo "Try GET RATING by userID "

curl -b cookies.txt -X GET http://localhost:8080/ratings/user/1c100921-3c6e-4593-90d4-e63d40a02309
echo -e "\n"

echo "Try GET RATING by mediaID "

curl -b cookies.txt -X GET http://localhost:8080/ratings/media/57916e79-db50-43b1-b0b6-956cde2037fb
echo -e "\n"
