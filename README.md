# Event Backend

Spring Boot REST API projekat sa PostgreSQL bazom podataka.

> 📚 **Za detaljnu dokumentaciju sa objašnjenjem svih pojmova i koncepata, pogledaj [DOCUMENTATION.md](./DOCUMENTATION.md)**

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
   git clone https://github.com/ikaa96/event.git
   cd event
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

Baza URL: `http://localhost:8080`

---

### Users (`/api/users`)

| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/users` | Svi korisnici |
| GET | `/api/users/{id}` | Korisnik po ID-u |
| GET | `/api/users/username/{username}` | Korisnik po username-u |
| GET | `/api/users/exists/{username}` | Da li username postoji (true/false) |
| POST | `/api/users` | Kreiraj korisnika |
| DELETE | `/api/users/{id}` | Obriši korisnika |

**Primeri:**

```http
# Svi korisnici
GET http://localhost:8080/api/users

# Korisnik po ID
GET http://localhost:8080/api/users/1

# Kreiranje korisnika
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "username": "jovan",
  "email": "jovan@example.com",
  "password": "password123",
  "role": "USER"
}
```

---

### Events (`/api/events`)

| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/api/events` | Lista sa paginacijom i filterima |
| GET | `/api/events/{id}` | Događaj po ID-u |
| GET | `/api/events/user/{userId}` | Događaji korisnika |
| GET | `/api/events/status/{status}` | Događaji po statusu (DRAFT, PUBLISHED, CANCELLED, COMPLETED) |
| GET | `/api/events/upcoming` | Budući objavljeni događaji |
| POST | `/api/events?userId={id}` | Kreiraj događaj |
| PUT | `/api/events/{id}?userId={id}` | Ažuriraj događaj |
| PATCH | `/api/events/{id}/status?status={status}&userId={id}` | Promeni status |
| DELETE | `/api/events/{id}?userId={id}` | Obriši događaj |

**Query parametri za GET /api/events:**

- `page` (default: 0), `size` (default: 10)
- `sortBy` (default: id), `sortDir` (asc/desc)
- `title`, `location`, `status`, `fromDate`, `toDate` (opciono)

**Primeri:**

```http
# Svi događaji (prva strana)
GET http://localhost:8080/api/events

# Sa filterima
GET http://localhost:8080/api/events?page=0&size=10&status=PUBLISHED&sortBy=eventDate&sortDir=asc

# Budući objavljeni
GET http://localhost:8080/api/events/upcoming

# Kreiranje događaja (userId obavezan u query-ju)
POST http://localhost:8080/api/events?userId=1
Content-Type: application/json

{
  "title": "Python Konferencija",
  "description": "Konferencija o programiranju",
  "eventDate": "2026-10-15T10:00:00",
  "location": "Beograd",
  "status": "PUBLISHED"
}

# Promena statusa
PATCH http://localhost:8080/api/events/1/status?status=PUBLISHED&userId=1
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
