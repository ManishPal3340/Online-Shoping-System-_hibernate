# Online-Shoping-System-_hibernate
A console-based Online Shopping Management System built using Java, Hibernate ORM, and MySQL, implementing real-world e-commerce features with clean architecture and CRUD operations.


# 🛒 Online Shopping Management System (Console-Based)

## 📌 Project Description

The **Online Shopping Management System** is a **console-based Java application** developed using **Hibernate ORM** and **MySQL**. This project simulates the core functionalities of an online shopping platform such as managing users, products, categories, orders, and payments.

It is designed to help Java developers understand **Hibernate CRUD operations**, **entity relationships**, **session management**, and **database interaction** in a real-world scenario using a clean layered architecture.

This project is suitable for:

* Java & Hibernate learners
* Fresher-level interview preparation
* Academic / mini-project submissions

---

## 🎯 Key Features

* User Registration & Login
* Product & Category Management
* Add to Cart & Place Orders
* Order History Tracking
* Payment Status Management
* Hibernate-based CRUD operations
* Console-based menu-driven UI

---

## 🛠️ Technologies Used

* **Java (JDK 8+)**
* **Hibernate (ORM)**
* **MySQL Database**
* **Maven**
* **JDBC**
* **Console Application**

---

## 📂 Project Folder Structure

```
OnlineShoppingManagementSystem/
│
├── pom.xml                         # Maven dependencies
│
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── onlineshop/
        │           ├── config/
        │           │   └── HibernateUtil.java
        │           │
        │           ├── entity/
        │           │   ├── User.java
        │           │   ├── Product.java
        │           │   ├── Category.java
        │           │   ├── Order.java
        │           │   └── Cart.java
        │           │
        │           ├── dao/
        │           │   ├── UserDao.java
        │           │   ├── ProductDao.java
        │           │   ├── OrderDao.java
        │           │   └── CategoryDao.java
        │           │
        │           ├── service/
        │           │   ├── UserService.java
        │           │   ├── ProductService.java
        │           │   └── OrderService.java
        │           │
        │           ├── util/
        │           │   └── InputUtil.java
        │           │
        │           └── app/
        │               └── OnlineShoppingApp.java
        │
        └── resources/
            ├── hibernate.cfg.xml
            └── application.properties
```

---

## 🧩 Hibernate Entity Relationships

* **User → Orders** (One-to-Many)
* **Category → Products** (One-to-Many)
* **Order → Products** (Many-to-Many)

---

## 🗄️ Database Schema (MySQL SQL Queries)

### 1️⃣ User Table

```sql
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(100),
    role VARCHAR(50)
);
```

### 2️⃣ Category Table

```sql
CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100)
);
```

### 3️⃣ Product Table

```sql
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100),
    price DOUBLE,
    quantity INT,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);
```

### 4️⃣ Orders Table

```sql
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    order_date DATE,
    total_amount DOUBLE,
    status VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

### 5️⃣ Order-Product Mapping Table

```sql
CREATE TABLE order_products (
    order_id INT,
    product_id INT,
    quantity INT,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
```

---

## ▶️ How to Run the Project

1. Clone the repository
2. Create database `onlineshop_db` in MySQL
3. Update DB credentials in `hibernate.cfg.xml`
4. Run `OnlineShoppingApp.java`
5. Use console menu to perform operations

---

## 📘 Sample Console Menu

```
===== ONLINE SHOPPING SYSTEM =====
1. Register User
2. Login User
3. View Products
4. Add Product to Cart
5. Place Order
6. View Order History
0. Exit
```

---

## 🚀 Future Enhancements

* Admin dashboard
* Discount & coupon system
* Product reviews
* Spring Boot migration
* REST API integration

---

## 👨‍💻 Author

**Manish Pal**
Java Developer | Hibernate | MySQL

---

## ⭐ GitHub Description (Short)

A console-based Online Shopping Management System built using Java, Hibernate ORM, and MySQL, implementing real-world e-commerce features with clean architecture and CRUD operations.
