

# Benutzer registrieren
echo "Register User"
RESPONSE=$(curl -s -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"test123\"}")

echo "Response: $RESPONSE"
echo -e "\n"


# FALSCHES PASSWORT

rm cookies.txt

echo "Try Login with WRONG credentials"
curl -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"test12"}'
echo -e "\n"


# Benutzer einloggen


echo "Try Login with CORRECT credentials"
curl -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"test123"}'
echo -e "\n"


# Film abrufen

echo "GET /movies (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/movies
echo -e "\n"

echo "GET /movies/5b0b20d6-3d4d-4ca2-bdcb-22a65af6cc76 (authenticated)"
curl -b cookies.txt -X GET http://localhost:8080/movies/5b0b20d6-3d4d-4ca2-bdcb-22a65af6cc76
echo -e "\n"