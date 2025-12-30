

# Benutzer registrieren
echo "Register User"
RESPONSE=$(curl -s -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"testUser3\",\"password\":\"test123\"}")

echo "Response: $RESPONSE"
echo -e "\n"

# Benutzer einloggen

rm cookies.txt


echo "Try Login with CORRECT credentials"
curl -c cookies.txt -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testUser3","password":"test123"}'
echo -e "\n"
