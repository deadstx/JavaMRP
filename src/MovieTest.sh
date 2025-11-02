#!/bin/bash

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
