#!/bin/bash

echo "GET /movies (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/movies
echo -e "\n"

echo "GET /movies/4 (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/movies/1
echo -e "\n"

