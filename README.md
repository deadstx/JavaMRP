MRP_Hosnedl

Dieses ReadMe ist nur für das Setup gedacht und nicht für die Dokumentation. Im Projektordner gibt es MRP_Protocol. Dort ist die ganze Architektur meines Projektes beschrieben.

docker-compose.yml befindet sich im Hauptordner: Daten für meine DB sind:

  POSTGRES_DB: mrp
  POSTGRES_USER: mrp_user
  POSTGRES_PASSWORD: test123

  oder 
  
   Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5431/mrp",
                "mrp_user",
                "test123"
        );
Bei -> docker compose up -d sollte meine db/init.sql automatisch aufgerufen werden und die DB Struktur erstellen Mit den Curl Scripten, auf die ich später eingehe, werden dann Testdaten eingefügt.

Für JWT:

Im Hauptordner ist auch meine .env Datei. Falls nicht sichtbar: JWT_SECRET=super_secret_key_123_super_secret_key_super_secret_key Ist in .env drinnen, wird aber durch .gitignore nicht auf GitHub gepushed

Meine Main Datei startet zuerst die DB Connection und dann den Server auf Port 8080 Wenn Server und DB laufen können die Curl Scripts verwendet werden:

Ich habe mehrere Curl Scripte in /src/Curl erstellt.

Setup Account -> Erstellt Test user und Media Einträge
Danach müssen alle Media_ids aus der DB in die Lise in CreateRatings_Comments kopiert werden
CreateRatings_Comments ausführen
In CheckListTests können dann die gewünschten Media Ids reinkopiert werden und das Skript zeigt alle Server antworten


Link to Github REPO https://github.com/deadstx/JavaMRP
