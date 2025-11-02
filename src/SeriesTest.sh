#!/bin/bash



echo "GET /series (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/series
echo -e "\n"

echo "GET /series/1 (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/series/1
echo -e "\n"

echo "ADD NEW SERIES (authenticated)"
curl -b cookies.txt -X POST http://localhost:8080/series \
  -H "Content-Type: application/json" \
  -d '{"id":28,"title":"NewlyAdded","director":  "JTest","genre":  "Test","year":1999, "rating": 9.8}'
echo -e "\n"

echo "GET /series (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/series
echo -e "\n"
