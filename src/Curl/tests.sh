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