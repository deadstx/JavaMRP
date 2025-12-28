#!/bin/bash

rm cookies.txt

echo "GET /movies"
curl -b cookies.txt -X GET http://localhost:8080/movies
echo -e "\n"

echo "GET /movies/4 (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/movies/1
echo -e "\n"


echo "Try Login with correct credentials"
curl -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'
echo -e "\n"

echo "GET /movies"
curl -b cookies.txt -X GET http://localhost:8080/movies
echo -e "\n"

echo "GET /movies/4 (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/movies/1
echo -e "\n"

