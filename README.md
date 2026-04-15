# Order Service

Backend сервис для управления заказами (Order Service), реализованный на Spring Boot.

Проект демонстрирует полный цикл разработки backend-приложения:

* REST API
* Unit и Integration тесты
* Testcontainers
* Docker (multistage build)
* Kubernetes (Minikube + Helm)
* Нагрузочное тестирование (k6 через Docker)

---

## 🚀 Стек технологий

* Java 21
* Spring Boot 4
* Spring Data JPA
* PostgreSQL
* Testcontainers
* JUnit 5 / Mockito
* Maven
* Docker
* Kubernetes (Minikube)
* Helm
* k6 (Docker)
* Swagger / OpenAPI

---

## 📦 Функциональность

* CRUD операции с заказами
* Фильтрация и пагинация
* Soft delete
* Валидация данных
* Глобальная обработка ошибок
* API документация (Swagger)

---

## ⚙️ Запуск локально

### 1. Клонирование проекта

```bash id="1"
git clone https://github.com/UNF3x/order-service.git
cd order-service
```

### 2. Запуск приложения

```bash id="2"
./mvnw spring-boot:run
```

---

## 🧪 Тесты

### Запуск всех тестов

```bash id="3"
./mvnw clean test
```

В проекте используются:

* Unit тесты (Service слой)
* Integration тесты (Controller + DB)
* Testcontainers (PostgreSQL)

---

## 📄 Swagger

После запуска приложение доступно:

```id="4"
http://localhost:8080/swagger-ui.html
```

---

## 🐳 Docker

### Сборка образа

```bash id="5"
docker build -t order-service:latest .
```

### Запуск контейнера

```bash id="6"
docker run -p 8080:8080 order-service:latest
```

---

## ☸️ Kubernetes (Minikube)

### 1. Запуск Minikube

```bash id="7"
minikube start
```

### 2. Переключение Docker окружения

```bash id="8"
eval $(minikube docker-env)
```

### 3. Сборка образа внутри Minikube

```bash id="9"
docker build -t order-service:latest .
```

---

## 📦 Деплой через Helm

### 1. Создание namespace

```bash id="10"
kubectl create namespace order-unf3x
```

### 2. Деплой приложения

```bash id="11"
helm upgrade --install order-service ./helm/order-service \
  --namespace order-unf3x \
  --set image.tag=latest
```

---

## 🔎 Проверка работы

```bash id="12"
kubectl port-forward svc/order-service 8080:8080 -n order-unf3x
```

После этого сервис доступен:

```id="13"
http://localhost:8080
```

---

## 📊 Нагрузочное тестирование (k6 через Docker)

### 📁 Структура

```bash id="14"
k6/
 ├── smoke.js
 └── load.js
```

---

### 🚀 Запуск smoke теста

```bash id="15"
docker run --rm -i \
  -v ${PWD}/k6:/scripts \
  grafana/k6 run /scripts/smoke.js
```

---

### 🚀 Запуск нагрузочного теста

```bash id="16"
docker run --rm -i \
  -v ${PWD}/k6:/scripts \
  grafana/k6 run /scripts/load.js
```

---

### 🌐 Тестирование Kubernetes сервиса

```bash id="17"
kubectl port-forward svc/order-service 8080:8080 -n order-unf3x
```

k6 будет отправлять запросы на:

```id="18"
http://localhost:8080
```

---

## 🧠 CIOps подход (Sandbox деплой)

Проект реализует подход **CIOps (Continuous Integration Operations)**:

1. Локальная разработка
2. Unit и Integration тесты
3. Docker сборка
4. Ручной деплой в sandbox namespace (Minikube)
5. Smoke тесты
6. Нагрузочное тестирование (k6)
7. Только после этого код готов к Merge Request

---

## 📁 Структура проекта

```id="19"
order-service/
 ├── src/
 ├── helm/
 │    └── order-service/
 ├── k6/
 │    ├── smoke.js
 │    └── load.js
 ├── Dockerfile
 ├── pom.xml
 └── README.md
```

---

## 🧹 Best Practices

* Слоистая архитектура (Controller → Service → Repository)
* Централизованная обработка ошибок
* Изолированные тесты через Testcontainers
* Документированное API (Swagger)
* Контейнеризация (Docker)
* Деплой через Helm
* Нагрузочное тестирование как часть разработки

---

## 👤 Автор

UNF3x
