# Wallet Service

---

A simple service to save transaction and get total balance based on saved transaction.

## Technologies Used

---
- **Java**
- **Spring Boot**
- **H2 in-memory database**
- **Kafka (via Docker Compose)**
- **JUnit 5 + Mockito**

## Requirements

---

- **JDK 17**
- **Docker**

## Running the Server

---

### **1. Clone the repository**

```bash
git clone https://github.com/paranody/wallet-service.git
cd wallet-service
```

### **2. Start Kafka**

```bash
docker compose up -d
```

### **3. Run the Spring Boot app**

```bash
./gradlew bootRun
```

Or build & run:

```bash
./gradlew clean build
java -jar build/libs/wallet-service.jar
```

Server runs at:

```
http://localhost:8080
```
## Note

---



- All timestamps stored as **Instant (UTC)** in DB.
- API input/output uses **OffsetDateTime**.
- Outbox pattern guarantees reliable event publishing even during failures.

## License

---



This project is for backend assessment and demonstration purposes.
