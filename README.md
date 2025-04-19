## 📚 Library Management System

A simple Library Management System I've built using **Java** and **MySQL**, featuring user and admin modules with book management, registration, login, and more. This system helps track books issued/returned and maintain organized records.

---

### 🚀 Features

#### 👨‍🎓 User Module
- Register and Login
- Search books
- View available books
- Issue and return books
- View personal book history

#### 🛠️ Admin Module
- Admin Login
- Add/Remove books
- View all users
- Track issued/returned books
- View database statistics

---

### 🛠️ Tech Stack

- **Java** (Core + JDBC)
- **MySQL** for backend database
- **SQL** for queries and data manipulation

---

### 🗂️ Project Structure

```
LIBRARY/
├── src/
│   ├── User.java
│   ├── Admin.java
│   ├── Book.java
│   ├── DBConnection.java
│   └── ...
├── resources/
│   ├── library_db.sql
│   └── README.md
```

> 📁 Note: Replace or update the folder structure based on your actual file organization.

---

### 🛠️ Setup Instructions

1. **Clone the Repository**

```bash
git clone https://github.com/yourusername/library-management-system.git
cd library-management-system
```

2. **Import the Database**

- Open MySQL or any SQL interface (e.g., phpMyAdmin, MySQL Workbench)
- Run the SQL file:

```sql
SOURCE path_to/library_db.sql;
```

3. **Configure Database Connection**

In `DBConnection.java`, set your MySQL database credentials:

```java
String url = "jdbc:mysql://localhost:3306/library_db";
String user = "yourUsername";
String password = "yourPassword";
```

4. **Compile and Run the Project**

Use any Java IDE (Eclipse, IntelliJ, NetBeans) or compile using terminal:

```bash
javac *.java
java Main
```

---

### 📝 Future Enhancements

- GUI using JavaFX or Swing
- Fine calculation for late returns
- Email notifications
- Book recommendations based on usage

---
