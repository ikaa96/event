# Event Backend - Kompletna Dokumentacija

## 📚 Sadržaj

1. [Rezime Projekta](#rezime-projekta)
2. [Objašnjenje Pojmova](#objašnjenje-pojmova)
3. [Arhitektura Aplikacije](#arhitektura-aplikacije)
4. [Detaljni Opis Komponenti](#detaljni-opis-komponenti)
5. [API Dokumentacija](#api-dokumentacija)
6. [Git i GitHub](#git-i-github)

---

## 📋 Rezime Projekta

### Šta smo uradili:

#### 1. **Setup Projekta**
- ✅ Kreiran Maven projekat sa Spring Boot 3.2.0
- ✅ Konfigurisan `pom.xml` sa svim potrebnim dependency-jima
- ✅ Konfigurisan `application.yml` za PostgreSQL bazu

#### 2. **Baza Podataka**
- ✅ Kreirana PostgreSQL baza `event`
- ✅ Hibernate automatski kreira tabele iz entiteta

#### 3. **Model Sloj (Entiteti)**
- ✅ `User` entitet sa svim potrebnim poljima
- ✅ Automatsko postavljanje timestamp-ova

#### 4. **Repository Sloj**
- ✅ `UserRepository` sa custom metodama
- ✅ Spring Data JPA automatski generiše SQL upite

#### 5. **Service Sloj**
- ✅ `UserService` sa business logikom
- ✅ Provera duplikata pre kreiranja

#### 6. **DTO Sloj**
- ✅ `UserRequest` - za kreiranje (sa validacijom)
- ✅ `UserResponse` - za vraćanje (bez password-a)

#### 7. **Controller Sloj (REST API)**
- ✅ `UserController` sa svim CRUD endpointima
- ✅ Pravilni HTTP status kodovi

#### 8. **Error Handling**
- ✅ Global exception handler
- ✅ Strukturisani error response

#### 9. **Testiranje**
- ✅ Unit testovi sa Mockito
- ✅ DataInitializer za testiranje

#### 10. **Git i GitHub**
- ✅ Projekat push-ovan na GitHub

---

## 📖 Objašnjenje Pojmova

### Spring Boot Koncepti

#### **@SpringBootApplication**
- Kombinacija tri anotacije:
  - `@Configuration` - klasa je izvor bean definicija
  - `@EnableAutoConfiguration` - Spring Boot automatski konfiguriše sve
  - `@ComponentScan` - skenira pakete za komponente
- Kada se pokrene, Spring Boot automatski:
  - Pokreće embedded Tomcat server (port 8080)
  - Učitava sve komponente (@Service, @Repository, @Controller)
  - Konfiguriše sve na osnovu dependency-ja

#### **Dependency Injection (DI)**
- Spring automatski "ubacuje" dependency-je u klase
- Primer:
  ```java
  @Service
  public class UserService {
      private final UserRepository userRepository; // Spring automatski injektuje
  }
  ```
- Ne moraš ručno da kreiraš objekte - Spring to radi za tebe

#### **@Service, @Repository, @Controller**
- `@Service` - označava business logiku sloj
- `@Repository` - označava data access sloj
- `@Controller` - označava REST API sloj
- Spring automatski kreira instance ovih klasa

---

### Hibernate/JPA Koncepti

#### **ORM (Object-Relational Mapping)**
- Mapira Java objekte na tabele u bazi
- Ne pišeš SQL ručno - Hibernate ga generiše

#### **@Entity**
- Označava da je klasa entitet koji treba mapirati na tabelu
- Hibernate automatski kreira tabelu na osnovu klase

#### **@Table(name = "users")**
- Ime tabele u bazi podataka
- Bez ove anotacije bi bilo "user" (jednina)

#### **@Id i @GeneratedValue**
- `@Id` - označava primarni ključ
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` - auto-generisanje ID-a
- IDENTITY znači da baza podataka sama generiše ID

#### **@Column**
- Definiše kolone u tabeli
- `nullable = false` - kolona ne može biti NULL
- `unique = true` - vrednost mora biti jedinstvena
- `name = "created_at"` - eksplicitno ime kolone

#### **@PrePersist i @PreUpdate**
- `@PrePersist` - poziva se PRE nego što se entitet sačuva (INSERT)
- `@PreUpdate` - poziva se PRE nego što se entitet ažurira (UPDATE)
- Koristimo za automatsko postavljanje timestamp-ova

#### **ddl-auto Opcije**
- `none` - ne radi ništa
- `validate` - samo proverava da li tabele odgovaraju entitetima
- `update` - kreira/ažurira tabele automatski (koristimo ovo)
- `create` - briše i kreira tabele svaki put
- `create-drop` - briše tabele kada se aplikacija zatvori

---

### Spring Data JPA Koncepti

#### **JpaRepository<User, Long>**
- Interface koji nasleđuje JpaRepository
- `User` - tip entiteta
- `Long` - tip ID-a (primarnog ključa)
- Spring automatski kreira implementaciju!

#### **Automatske Metode**
- `save(User user)` - čuva ili ažurira
- `findById(Long id)` - pronalazi po ID-u
- `findAll()` - vraća sve
- `delete(User user)` - briše
- `count()` - broji

#### **Query Methods**
- Spring automatski generiše SQL na osnovu imena metode:
  - `findByUsername(String username)` → `SELECT * FROM users WHERE username = ?`
  - `existsByEmail(String email)` → `SELECT COUNT(*) > 0 FROM users WHERE email = ?`
  - `findByRoleOrderByCreatedAtDesc(Role role)` → sortiranje

---

### REST API Koncepti

#### **HTTP Metode**
- `GET` - čitanje podataka
- `POST` - kreiranje novog resursa
- `PUT` - ažuriranje resursa
- `DELETE` - brisanje resursa

#### **HTTP Status Kodovi**
- `200 OK` - uspešan zahtev
- `201 CREATED` - resurs kreiran
- `204 NO CONTENT` - uspešno brisanje (bez body-ja)
- `400 BAD REQUEST` - loš zahtev (validacija ne prođe)
- `404 NOT FOUND` - resurs nije pronađen
- `409 CONFLICT` - resurs već postoji
- `500 INTERNAL SERVER ERROR` - greška na serveru

#### **@RestController**
- Kombinacija `@Controller` + `@ResponseBody`
- Automatski konvertuje return vrednost u JSON

#### **@RequestMapping("/api/users")**
- Svi endpointi počinju sa `/api/users`
- Primer: `GET /api/users/1`

#### **@PathVariable**
- Izvlači vrednost iz URL-a
- Primer: `/api/users/{id}` → `@PathVariable Long id`

#### **@RequestBody**
- Konvertuje JSON iz body-ja u Java objekat
- Primer: `@RequestBody UserRequest request`

#### **@Valid**
- Spring automatski validira podatke
- Ako validacija ne prođe → 400 Bad Request

---

### DTO Pattern

#### **Zašto DTO?**
1. **Bezbednost** - ne vraćamo password!
2. **Kontrola** - vraćamo samo ono što želimo
3. **Fleksibilnost** - možemo dodati izračunata polja
4. **Odvajanje** - odvajanje API sloja od entiteta

#### **UserRequest vs UserResponse**
- `UserRequest` - za kreiranje/ažuriranje (sa validacijom)
- `UserResponse` - za vraćanje (bez password-a)

---

### Error Handling Koncepti

#### **@RestControllerAdvice**
- Primenjuje se na sve REST kontrolere
- Hvata sve exception-e u aplikaciji

#### **@ExceptionHandler**
- Hvata specifične tipove exception-a
- Primer: `@ExceptionHandler(DataIntegrityViolationException.class)`

#### **Custom Exception**
- Kreiranje sopstvenih exception klasa
- Primer: `ResourceAlreadyExistsException`

---

### Testiranje Koncepti

#### **Unit Testovi**
- Testiraju jednu jedinicu koda (npr. jednu metodu)
- Mock-uju dependency-je (npr. UserRepository)
- Brzi, izolovani testovi

#### **Mockito**
- Framework za mock-ovanje objekata
- `@Mock` - kreira mock objekat
- `when(...).thenReturn(...)` - definiše šta mock vraća
- `verify(...)` - proverava da li je metoda pozvana

#### **CommandLineRunner**
- Interface koji ima `run()` metodu
- Spring automatski poziva `run()` nakon pokretanja aplikacije
- Koristi se za testiranje ili inicijalizaciju podataka

---

## 🏗️ Arhitektura Aplikacije

### Layered Architecture (Slojevita Arhitektura)

```
┌─────────────────────────────────────────┐
│         Controller Layer                │  ← REST API endpointi
│      (@RestController)                  │
└──────────────┬──────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────┐
│          Service Layer                  │  ← Business logika
│         (@Service)                      │
└──────────────┬──────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────┐
│        Repository Layer                 │  ← Data access
│       (@Repository)                     │
└──────────────┬──────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────┐
│         Database                        │  ← PostgreSQL
│      (PostgreSQL)                       │
└─────────────────────────────────────────┘
```

### Flow Request-a

```
HTTP Request
    ↓
Controller (@RestController)
    ↓
Service (@Service) - business logika
    ↓
Repository (@Repository) - data access
    ↓
Database (PostgreSQL)
    ↓
Repository - vraća entitet
    ↓
Service - vraća entitet
    ↓
Controller - konvertuje u DTO
    ↓
HTTP Response (JSON)
```

---

## 🔍 Detaljni Opis Komponenti

### 1. EventApplication.java
**Lokacija:** `src/main/java/com/event/EventApplication.java`

**Šta radi:**
- Glavna klasa Spring Boot aplikacije
- Pokreće aplikaciju kada se izvrši `main()` metoda

**Ključne anotacije:**
- `@SpringBootApplication` - kombinacija tri anotacije

---

### 2. User.java (Entitet)
**Lokacija:** `src/main/java/com/event/model/User.java`

**Šta radi:**
- Predstavlja korisnika u bazi podataka
- Hibernate automatski kreira tabelu `users` na osnovu ove klase

**Polja:**
- `id` - primarni ključ (auto-generisan)
- `username` - jedinstveno korisničko ime
- `email` - jedinstveni email
- `password` - lozinka (kasnije ćemo hash-ovati)
- `role` - uloga korisnika (USER, ADMIN)
- `createdAt` - datum kreiranja (automatski)
- `updatedAt` - datum ažuriranja (automatski)

**Ključne anotacije:**
- `@Entity` - označava entitet
- `@Table(name = "users")` - ime tabele
- `@Id` + `@GeneratedValue` - primarni ključ
- `@PrePersist` - postavlja createdAt i updatedAt

---

### 3. UserRepository.java
**Lokacija:** `src/main/java/com/event/repository/UserRepository.java`

**Šta radi:**
- Interface za pristup podacima
- Spring automatski kreira implementaciju

**Metode:**
- `findByUsername(String username)` - pronalazi po username-u
- `findByEmail(String email)` - pronalazi po email-u
- `existsByUsername(String username)` - proverava postojanje
- `existsByEmail(String email)` - proverava postojanje

**Ključne anotacije:**
- `@Repository` - označava repository sloj
- `extends JpaRepository<User, Long>` - nasleđuje osnovne metode

---

### 4. UserService.java
**Lokacija:** `src/main/java/com/event/service/UserService.java`

**Šta radi:**
- Business logika za rad sa korisnicima
- Proverava duplikate pre kreiranja

**Metode:**
- `findById(Long id)` - pronalazi po ID-u
- `findByUsername(String username)` - pronalazi po username-u
- `createUser(User user)` - kreira sa proverom duplikata
- `save(User user)` - čuva korisnika
- `deleteById(Long id)` - briše korisnika

**Ključne anotacije:**
- `@Service` - označava service sloj
- `@Transactional` - sve metode su transakcione

---

### 5. UserRequest.java (DTO)
**Lokacija:** `src/main/java/com/event/dto/UserRequest.java`

**Šta radi:**
- DTO za kreiranje/ažuriranje korisnika
- Validira podatke pre nego što stignu do Controller-a

**Polja:**
- `username` - sa validacijom (@NotBlank, @Size)
- `email` - sa validacijom (@Email)
- `password` - sa validacijom (@Size)
- `role` - sa validacijom (@NotNull)

**Metode:**
- `toEntity()` - konvertuje DTO u User entitet

---

### 6. UserResponse.java (DTO)
**Lokacija:** `src/main/java/com/event/dto/UserResponse.java`

**Šta radi:**
- DTO za vraćanje korisnika kroz API
- **Ne sadrži password!** (bezbednost)

**Polja:**
- `id`, `username`, `email`, `role`, `createdAt`, `updatedAt`

**Metode:**
- `from(User user)` - konvertuje User entitet u DTO

---

### 7. UserController.java
**Lokacija:** `src/main/java/com/event/controller/UserController.java`

**Šta radi:**
- REST API endpointi za rad sa korisnicima
- Prima HTTP zahteve i vraća HTTP odgovore

**Endpointi:**
- `GET /api/users` - svi korisnici
- `GET /api/users/{id}` - korisnik po ID-u
- `GET /api/users/username/{username}` - korisnik po username-u
- `POST /api/users` - kreiranje korisnika
- `DELETE /api/users/{id}` - brisanje korisnika

**Ključne anotacije:**
- `@RestController` - REST controller
- `@RequestMapping("/api/users")` - base path
- `@GetMapping`, `@PostMapping`, `@DeleteMapping` - HTTP metode

---

### 8. GlobalExceptionHandler.java
**Lokacija:** `src/main/java/com/event/exception/GlobalExceptionHandler.java`

**Šta radi:**
- Hvata sve exception-e u aplikaciji
- Vraća strukturisane error response-e

**Exception Handleri:**
- `DataIntegrityViolationException` → 409 Conflict
- `ResourceAlreadyExistsException` → 409 Conflict
- `Exception` → 500 Internal Server Error

**Ključne anotacije:**
- `@RestControllerAdvice` - primenjuje se na sve kontrolere
- `@ExceptionHandler` - hvata specifične exception-e

---

### 9. application.yml
**Lokacija:** `src/main/resources/application.yml`

**Šta radi:**
- Konfiguracija aplikacije
- Podešavanja za bazu podataka, server, itd.

**Sekcije:**
- `spring.datasource` - PostgreSQL konfiguracija
- `spring.jpa` - Hibernate konfiguracija
- `server.port` - port na kom radi aplikacija (8080)

---

## 📡 API Dokumentacija

### Base URL
```
http://localhost:8080/api/users
```

### Endpointi

#### 1. Vrati sve korisnike
```
GET /api/users
```

**Response:**
```json
[
  {
    "id": 1,
    "username": "jovan",
    "email": "jovan@example.com",
    "role": "USER",
    "createdAt": "2026-02-05T10:00:00",
    "updatedAt": "2026-02-05T10:00:00"
  }
]
```

**Status kodovi:**
- `200 OK` - uspešno

---

#### 2. Vrati korisnika po ID-u
```
GET /api/users/{id}
```

**Path parametri:**
- `id` - ID korisnika (Long)

**Response:**
```json
{
  "id": 1,
  "username": "jovan",
  "email": "jovan@example.com",
  "role": "USER",
  "createdAt": "2026-02-05T10:00:00",
  "updatedAt": "2026-02-05T10:00:00"
}
```

**Status kodovi:**
- `200 OK` - korisnik pronađen
- `404 NOT FOUND` - korisnik ne postoji

---

#### 3. Vrati korisnika po username-u
```
GET /api/users/username/{username}
```

**Path parametri:**
- `username` - korisničko ime (String)

**Response:** Isti kao GET /api/users/{id}

**Status kodovi:**
- `200 OK` - korisnik pronađen
- `404 NOT FOUND` - korisnik ne postoji

---

#### 4. Kreiraj novog korisnika
```
POST /api/users
```

**Request Body:**
```json
{
  "username": "novi",
  "email": "novi@example.com",
  "password": "password123",
  "role": "USER"
}
```

**Validacija:**
- `username` - obavezan, 3-50 karaktera
- `email` - obavezan, validan email format
- `password` - obavezan, minimum 6 karaktera
- `role` - obavezan, mora biti "USER" ili "ADMIN"

**Response:**
```json
{
  "id": 2,
  "username": "novi",
  "email": "novi@example.com",
  "role": "USER",
  "createdAt": "2026-02-05T11:00:00",
  "updatedAt": "2026-02-05T11:00:00"
}
```

**Status kodovi:**
- `201 CREATED` - korisnik kreiran
- `400 BAD REQUEST` - validacija ne prođe
- `409 CONFLICT` - korisnik sa istim email-om/username-om već postoji

---

#### 5. Obriši korisnika
```
DELETE /api/users/{id}
```

**Path parametri:**
- `id` - ID korisnika (Long)

**Response:** Nema body-ja

**Status kodovi:**
- `204 NO CONTENT` - korisnik obrisan
- `404 NOT FOUND` - korisnik ne postoji

---

### Error Response Format

```json
{
  "timestamp": "2026-02-05T15:23:11.7701021",
  "status": 409,
  "error": "Conflict",
  "message": "Korisnik sa email-om 'novi@test.com' već postoji"
}
```

---

## 🔧 Git i GitHub

### Git Komande - Objašnjenje

#### **git init**
- Inicijalizuje novi Git repozitorijum
- Kreira `.git` folder (skriven)
- Prati sve promene u projektu

#### **git add .**
- Dodaje sve fajlove u "staging area"
- `.` znači "sve fajlove u trenutnom folderu"
- Priprema fajlove za commit

#### **git commit -m "poruka"**
- Snima trenutno stanje fajlova u Git istoriju
- `-m` = message (poruka koja opisuje promene)
- Kreira "snimak" projekta

#### **git remote add origin <URL>**
- Povezuje lokalni repo sa remote repo-om na GitHub-u
- `origin` = ime za remote (obično se koristi "origin")
- Jednom kada kreiraš repo na GitHub-u

#### **git push -u origin main**
- Šalje lokalne commit-e na GitHub
- `-u` = set upstream (povezuje lokalni branch sa remote)
- `origin` = ime remote-a
- `main` = ime branch-a

### GitHub Repozitorijum

**URL:** https://github.com/ikaa96/event

### Workflow za Buduće Promene

```bash
# 1. Dodaj promene
git add .

# 2. Snimi promene
git commit -m "Opis promena"

# 3. Pošalji na GitHub
git push
```

---

## 📝 Napomene

### Zašto PostgreSQL, a ne MySQL sa XAMPP?

- **MySQL:** XAMPP je paket koji uključuje MySQL, Apache, PHP
- **PostgreSQL:** Instalira se standalone, ima svoj installer
- **Razlika:** PostgreSQL nije deo XAMPP paketa, ali se može instalirati odvojeno

### Zašto Code-First pristup?

- **Code-First:** Prvo definišemo entitet u Javi, Hibernate kreira tabelu
- **SQL-First:** Prvo kreiramo tabelu u SQL-u, zatim mapiramo u Java
- **Prednost Code-First:** Sve na jednom mestu, lakše održavanje

### Zašto DTO Pattern?

- **Bezbednost:** Ne vraćamo password u API odgovorima
- **Kontrola:** Vraćamo samo ono što želimo
- **Fleksibilnost:** Možemo dodati izračunata polja
- **Odvajanje:** Odvajanje API sloja od entiteta

---

## 🎯 Sledeći Koraci (Za Budućnost)

1. **JWT Autentifikacija**
   - Login/Register endpointi
   - Token generisanje i validacija
   - Zaštita endpointa

2. **Pagination i Filtering**
   - Paginacija za liste
   - Filtering po različitim kriterijumima
   - Sorting

3. **Event Entitet**
   - Kreiranje Event entiteta
   - CRUD operacije za događaje
   - Veza sa User entitetom

4. **Integration Testovi**
   - Testcontainers za testiranje sa pravom bazom
   - End-to-end testovi

5. **CI/CD**
   - GitHub Actions
   - Automatsko testiranje
   - Automatski deploy

---

## 📚 Korisni Linkovi

- [Spring Boot Dokumentacija](https://spring.io/projects/spring-boot)
- [Spring Data JPA Dokumentacija](https://spring.io/projects/spring-data-jpa)
- [Hibernate Dokumentacija](https://hibernate.org/orm/documentation/)
- [PostgreSQL Dokumentacija](https://www.postgresql.org/docs/)
- [Git Dokumentacija](https://git-scm.com/doc)

---

**Poslednje ažuriranje:** 5. februar 2026
