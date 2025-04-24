# 🛡️ TBP Auth Service

The `tbp-auth` service is a dedicated authentication and authorization microservice for the TBP (To be Played) platform. It handles user login, registration, and token issuance using both traditional username/password credentials and external SSO providers (Google, Steam, etc.).

---

## 🚀 Features

- ✅ JWT-based authentication
- 🔐 Password-based login and registration
- 🌐 SSO via OAuth2 (Google, Steam planned)
- 🗂️ User role and provider management
- 🐘 Ready for PostgreSQL (H2 in dev)
- ⚙️ Spring Boot 3, Spring Security, OAuth2

---

## 📦 Tech Stack

- Java 21
- Spring Boot
- Spring Security (JWT + OAuth2)
- JPA/Hibernate
- H2/PostgreSQL
- Maven

---

## 🧑‍💻 Endpoints (WIP)

| Method | Endpoint         | Description                        |
|--------|------------------|------------------------------------|
| POST   | `/auth/register` | Register new local user            |
| POST   | `/auth/login`    | Login with username/password       |
| GET    | `/auth/me`       | Returns currently authenticated user |
| GET    | `/oauth2/authorize/google` | Google SSO login start (WIP) |
| GET    | `/oauth2/authorize/steam`  | Steam login start (planned)  |

---

## Architecture Overview

             +------------------------+
             |       Frontend        |
             | (Login UI + Token Mgmt)|
             +------------------------+
                        |
                        v
             +------------------------+
             |      tbp-auth          |
             | (Spring Boot + JWT +   |
             |  OAuth2 + DB UserStore)|
             +------------------------+
                        |
                        v
             +------------------------+
             |     tbp-backend        |
             | (secured API,          |
             | validates JWT Tokens)  |
             +------------------------+

---

## 🔧 Setup (Dev)

```bash
# Clone the repo
git clone https://github.com/YOUR_ORG/tbp-auth.git
cd tbp-auth

# Run locally
./mvnw spring-boot:run


