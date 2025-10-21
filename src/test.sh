#!/bin/bash

echo "Try /movies without Login"
curl -X GET http://localhost:8080/movies
echo -e "\n"

echo "Try Login with wrong credentials"
curl -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"false","password":"falsePassword"}'
echo -e "\n"

echo "Try Login with correct credentials"
curl -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
echo -e "\n"

echo "GET /movies (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/movies
echo -e "\n"

echo "GET /movies/1 (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/movies/1
echo -e "\n"

echo "ADD NEW MOVIE (authenticated)"
curl -b cookies.txt -X POST http://localhost:8080/movies \
  -H "Content-Type: application/json" \
  -d '{"id":28,"title":"NewlyAdded","director":  "JTest","genre":  "Test","year":1999, "rating": 9.8}'
echo -e "\n"

echo "GET /movies (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/movies
echo -e "\n"
