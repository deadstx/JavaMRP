#!/bin/bash


echo "Try Login with wrong Credentials"
curl -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"false","password":"falsePassword"}'

echo "Try Login"
curl -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'


echo "GET /movies"
curl -b cookies.txt http://localhost:8080/movies
echo -e "\n"


echo "GET /movies/1"
curl -b cookies.txt http://localhost:8080/movies/1
echo -e "\n"

