#!/bin/bash

rm cookies.txt

echo "Try /series without Login"
curl -X GET http://localhost:8080/series
echo -e "\n"

echo "Try Login with wrong credentials"
curl -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"false","password":"falsePassword"}'
echo -e "\n"

echo "Try Login with correct credentials"
curl -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password1"}'
echo -e "\n"

