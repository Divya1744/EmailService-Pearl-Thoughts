
# 📧 Email Service — PearlThoughts Assignment

This is a mock email sending microservice built using **Java + Spring Boot**. It demonstrates:
- Email sending with **fallback** provider
- **Circuit Breaker** logic
- Basic **rate limiting**
- Simple **in-memory queue**
- Clean code with **SOLID principles**
- **Unit tests**
- 📦 Deployed live on Render

---

## ✅ Live Demo

**Base URL:**  
`https://emailservice-pearl-thoughts.onrender.com`

**Health Check:**  
`GET /api/mail` → `✅ Email Service is running!`

---

## 📌 API Endpoints

### 1. **Send Email**
**POST** `/api/mail/send`

#### Request Body:
```json
{
  "emailId": "test-1",
  "to": "john@gmail.com",
  "subject": "Test Subject",
  "body": "Hello from the cloud!"
}
````

#### Response:

```json
{
  "emailId": "test-1",
  "to": "john@gmail.com",
  "status": "SENT",
  "provider": "Provider1",
  "totalAttempts": 1
}
```

### 2. **Check Email Status**

**GET** `/api/mail/status/{emailId}`

Example:
`GET /api/mail/status/test-1`

---

## 🧪 Testing Behavior with Mocks

Mock providers are used — actual emails are not sent.
You can simulate success/failure based on the `to` address:

| `to` Address         | Behavior                                         |
| -------------------- | ------------------------------------------------ |
| `abc@gmail.com`      | ✅ Success via Provider1                          |
| `fallback@gmail.com` | ❌ Fails in Provider1 → ✅ Uses Provider2          |
| `failAll@gmail.com`  | ❌ Both providers fail → Final status: `"FAILED"` |

---

## 🧱 Tech Stack

* Java 17 (Temurin JDK)
* Spring Boot 3.x
* Maven
* JUnit 5
* Render.com (deployment)
* No DB (in-memory store for simplicity)

---

## ⚙️ Running Locally

```bash
# Clone the repo
git clone https://github.com/Divya1744/EmailService-Pearl-Thoughts.git
cd EmailService-Pearl-Thoughts

# Build the project
./mvnw clean package

# Run the application
java -jar target/emailservice-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Unit Tests

Run tests with:

```bash
./mvnw test
```

Includes:

* Email sending
* Fallback switching
* Circuit breaking
* Rate limiting

---

## 📋 Assumptions

* Email requests are uniquely identified by `emailId`
* Statuses are stored in a map (in-memory)
* No external SMTP integration (mock providers used)
* Rate limit: Max 6 emails per recipient per hour
* Basic queue is simulated with a list and delay

---


## ✉️ Questions?

Ping me on GitHub or reach out via email.
divyasd174@mail.com

---


