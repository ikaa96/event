# Event Backend

Spring Boot REST API projekat sa PostgreSQL bazom podataka.

## Tehnologije

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL**
- **Spring Data JPA / Hibernate**
- **Maven**
- **Lombok**
- **JUnit 5** (za testiranje)
- **Testcontainers** (za integration testove)

## Struktura projekta

```
src/
├── main/
│   ├── java/com/event/
│   │   ├── controller/     # REST API endpointi
│   │   ├── service/         # Business logika
│   │   ├── repository/      # Data access sloj
│   │   ├── model/           # Entiteti (JPA)
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Exception handling
│   │   └── config/           # Konfiguracije
│   └── resources/
│       └── application.yml   # Konfiguracija aplikacije
└── test/
    └── java/com/event/      # Testovi
```

## Setup

1. **Kloniraj projekat**
   ```bash
   git clone https://github.com/ikaa96/event-backend.git
   cd event-backend
   ```

2. **Kreiraj PostgreSQL bazu**
   ```sql
   CREATE DATABASE event;
   ```

3. **Konfiguriši `application.yml`**
   - Promeni `username` i `password` za PostgreSQL
   - Proveri da li je `url` tačan

4. **Pokreni aplikaciju**
   ```bash
   mvn spring-boot:run
   ```
   Ili kroz IDE: pokreni `EventApplication.java`

## API Endpointi

### Users

- `GET /api/users` - Vraća sve korisnike
- `GET /api/users/{id}` - Vraća korisnika po ID-u
- `GET /api/users/username/{username}` - Vraća korisnika po username-u
- `POST /api/users` - Kreira novog korisnika
- `DELETE /api/users/{id}` - Briše korisnika

### Primer zahteva

**Kreiranje korisnika:**
```json
POST /api/users
{
  "username": "jovan",
  "email": "jovan@example.com",
  "password": "password123",
  "role": "USER"
}
```

## Testiranje

```bash
# Pokreni sve testove
mvn test

# Pokreni samo unit testove
mvn test -Dtest=UserServiceTest
```

## Autor

ikaa96
