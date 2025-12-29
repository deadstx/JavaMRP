#!/bin/bash

# Cookies-Datei löschen
rm -f cookies.txt

echo "Try Login with correct credentials"
curl -i -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"test123\"}"
echo -e "\n"


echo "Try GET Profile "

curl -b cookies.txt -X GET http://localhost:8080/movies
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
