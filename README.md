# 🛍️ EasyShop - Capstone E-Commerce API

This is a Spring Boot + MySQL REST API backend for the **EasyShop** e-commerce app. It supports user authentication, product browsing, admin management of categories/products, and shopping cart features.

## 🚀 Technologies Used
- Java 17
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- MySQL
- JUnit (for unit testing)
- Postman (for API testing)
- Bootstrap (frontend)
- Axios + Mustache (frontend JS)

## 🛠️ Setup Instructions

### 1. Backend
- Clone this repo
- Import into IntelliJ or VSCode
- Run `create_database.sql` using MySQL Workbench
- Set your DB credentials in `application.properties`
- Run the Spring Boot app

### 2. Frontend
- Clone frontend repo: [capstone-client-web-application](https://github.com/Bilenie/capstone-client-web-application)
- Open `index.html` in your browser
- You can test login using:
  - username: `admin`
  - password: `password`

## 🔐 Authentication

All users must login to receive a **JWT token**.

### Login
```http
POST /login
{
  "username": "admin",
  "password": "password"
}
```
✅ Response includes token (JWT)

Use that token in Authorization header:

makefile
```
Authorization: Bearer YOUR_TOKEN_HERE
Register
http
POST /register
{
  "username": "admin",
  "password": "password",
  "confirmPassword": "password",
  "role": "ADMIN"
}
```
## ✅ API Features
```
Categories
GET /categories

GET /categories/{id}

POST /categories (admin only)

PUT /categories/{id} (admin only)

DELETE /categories/{id} (admin only)
```
Products
```
Search by category, color, price

CRUD for admins

Shopping Cart (Optional Phase 3)
GET /cart

POST /cart/products/{id}

PUT /cart/products/{id}

DELETE /cart
```
## 🧪 Testing

✅ Postman collection included for login, JWT testing, and category/product endpoints

✅ Unit tests written for DAO and Controller classes

## 🙏 Credits

Eric Shwartze – instructor and peers
YearUp – tutor and peers
OpenAI’s ChatGPT – AI assistance in planning, code explanations, and README drafting
Spring Boot, MySQL, Bootstrap, Axios, Mustache, and other open-source libraries

##👩‍💻 Author
Bilenie Mekbib – Capstone Project (YearUp / Java Back-End Track
