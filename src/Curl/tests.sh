#!/bin/bash

# Cookies-Datei löschen
rm -f cookies.txt

# Benutzer registrieren
echo "Register User"
RESPONSE=$(curl -s -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"test123\"}")

echo "Response: $RESPONSE"
echo -e "\n"

rm cookies.txt

echo "Try Login with correct credentials"
curl -i -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"test123\"}"
echo -e "\n"