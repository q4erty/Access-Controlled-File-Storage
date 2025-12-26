# Access-Controlled File Storage

A modern backend system for secure file storage with role-based access control, hierarchical folders, and a built-in support workflow.

Designed as a production-ready REST API using Spring Boot and JWT authentication.

---

## ✨ What is this project?

Access-Controlled File Storage is a backend application that solves the problem of centralized file management in a multi-user environment.  
It allows users to securely upload, organize, and share files while enforcing strict access rules based on ownership, roles, and explicit permissions.

The system is built with scalability and clean architecture in mind and demonstrates real-world backend design practices.

---

## 🧩 Key Capabilities

- JWT-based authentication (stateless, secure)
- Role-based access control (USER, SUPPORT, ADMIN)
- Hierarchical folder structure
- File upload, download, rename, move, and delete
- Fine-grained permissions (READ, WRITE, DELETE)
- Support request system with role separation
- Clean layered architecture

---

## 🛠 Tech Stack

- **Java 21**
- **Spring Boot 3**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **MapStruct**
- **Lombok**
- **Validation**
- **Maven**
- **Docker compose**
- **Postman (API testing)**

---

## 🚀 Getting Started

### Requirements
- Java 21
- PostgreSQL
- Maven

### Run Locally

```bash
git clone https://github.com/q4erty/Access-Controlled-File-Storage
cd Access-Controlled-File-Storage

docker-compose up -d

mvn clean install
mvn spring-boot:run
````

Configure database and JWT settings via environment variables or `application.yml`.

---

## 🔐 Roles Overview

* **USER** — manages own files and folders, creates support requests
* **SUPPORT** — handles and answers support requests
* **ADMIN** — full system access, user and role management

---

## 🔌 API Testing

A ready-to-use **Postman collection** is included in the repository for testing all endpoints and authorization flows.

---

## 📊 Presentation

Project presentation:
👉 **(https://www.canva.com/design/DAG8pcfhNhI/H8IDeaV8zitx5DlJl5Et8A/edit?utm_content=DAG8pcfhNhI&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)**

