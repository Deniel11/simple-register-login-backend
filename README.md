# Simple Register-Login Backend Project

This project is a simple register-login for users with token based authentication and admin roles, with email verification.

## Implementations:
Java Gradle Project

- Spring Boot Security
- Spring Boot Security Test
- Spring Boot Starter Web
- Spring Boot Starter JPA
- Spring Boot Starter Mail
- Spring Boot Devtools
- Spring Boot Starter Test
- JSON Web Token
- Flyway Core
- Flyway MYSQL
- Model Mapper
- Hibernate
- Mysql Connector
- JUnit Vintage Engine 

## Project Specifications:
- Token Based Authentication
- Admin and User Based Authorization
- Flyway Database Migration
- Centralized Error Handling
- Customized Exceptions
- Request Logging
- Mapper Service
- GitHub Actions
  - Check Style
  - Tests Check
- Environment Variables
  - Domain Name
  - Database Connection
  - Secret Key
  - Email Connection
- Read Texts From File
- User APIs
  - Register
  - Login
  - Edit User
  - GET User
  - GET Users
- FakeUser For Tests
- SQL Annotation For Tests
- Email Validation with Verification API
- Forgot Password with Forgot Password API

### Test Coverage:
- Class: 100%
- Method: 96%
- Line: 93%

## Guides

Before you use this project, you need to set up your environment variables.


Or you can use env file ".env.sample".


For example:
```
DOMAIN=[YOUR DOMAIN]
DB_HOST=[YOUR MYSQL DOMAIN]
DB_PORT=[YOUR MYSQL PORT]
DB_DATABASE=[YOUR DATABASE NAME]
DB_USERNAME=[YOUR MYSQL USERNAME]
DB_PASSWORD=[YOUR MYSQL PASSWORD]
HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect
HIBERNATE_DDL_AUTO=validate
SHOW_SQL=true
JWT_SECRET_KEY=[SELECT YOUR SECRET KEY]
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=[YOUR GMAIL USERNAME]
MAIL_PASSWORD=[YOUR GMAIL APPLICATION PASSWORD]
```

I use gmail for email service in my application, if you would like to use different one, then change the environment variables.

After that, you can use these endpoints:

The first registration is validated, and you get admin rights. (First ID)

-----
- **POST** - *[YOUR DOMAIN]*/api/user/registration
  

  **Request** Body:
  ```
  {
    "username": "Alexander",
    "email": "alexander@email.com",
    "password": "password",
    "dateOfBirth": "11-11-2000"
  }
  ```

  **Response**:


  Status code: 201

  Body:
  ```
  {
    "id": 1,
    "username": "Alexander",
    "email": "alexander@email.com",
    "dateOfBirth": "11-11-2000",
    "admin": false,
    "verified": false,
    "enabled": false
  }
  ```

-----
- **POST** - *[YOUR DOMAIN]*/api/user/login


  **Request** Body:
  ```
  {
    "username": "Alexander",
    "password": "password"
  }
  ```

  **Response**:


  Status code: 200

  Body:
  ```
  {
    "status": "ok",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
  }
  ```

-----
- **PUT** - *[YOUR DOMAIN]*/api/user/{id}
  - To use this, you need the token from login.
  - You can change only one parameter like "username" or "password".
  - You need admin role to change other user or to change admin role or validate, enabled parameter.
  - You can change multiple parameter like this:


  **Request**:
    

  Header:
  ```
  {
    "key": "Authorization",
    "value": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
  }
  ```

  Body:
  ```
  {
    "username": "Big Alexander",
    "email": "alexander@email.com",
    "password": "newPassword",
    "dateOfBirth": "11-11-2000",
    "admin": true,
    "verified": true,
    "enabled": true
  }
  ```

  **Response**:
  

  Status code: 200 
  

  Body:
  ```
  {
    "id": 1,
    "username": "Big Alexander",
    "email": "alexander@email.com",
    "dateOfBirth": "11-11-2000",
    "admin": true,
    "verified": true,
    "enabled": true
  }
  ```

-----
- **GET** - *[YOUR DOMAIN]*/api/user/
  - To use this, you need the token from login.


  **Request**:


  Header:
  ```
  {
    "key": "Authorization",
    "value": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
  }
  ```

  **Response**:
    

  Status code: 200
  

  Body:
  ```
  {
    "id": 1,
    "username": "Big Alexander",
    "email": "alexander@email.com",
    "dateOfBirth": "11-11-2000",
    "admin": true,
    "verified": true,
    "enabled": true
  }
  ```

-----
- **GET** - *[YOUR DOMAIN]*/api/user/{id}
  - To use this, you need the token from login.


  **Request**:
  
  
  Header:

  ```
  {
    "key": "Authorization",
    "value": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
  }
  ```

  **Response**:
  
  
  Status code: 200
  
  
  Body:
  ```
  {
    "id": 1,
    "username": "Big Alexander",
    "email": "alexander@email.com",
    "dateOfBirth": "11-11-2000",
    "admin": true,
    "verified": true,
    "enabled": true
  }
  ```


-----
- **GET** - *[YOUR DOMAIN]*/api/user/users
  - To use this, you need the token from login.


  **Request**:


  Header:
  ```
  {
    "key": "Authorization",
    "value": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
  }
  ```

  **Response**:
    

  Status code: 200
  

  Body:
  ```
  {
    "users": [
      {
        "id": 1,
        "username": "Big Alexander",
        "email": "alexander@email.com",
        "dateOfBirth": "11-11-2000",
        "admin": true,
        "verified": true,
        "enabled": true
      },
      {
        "id": 2,
        "username": "Sanyi",
        "email": "sanyi@email.com",
        "dateOfBirth": "11-11-2000",
        "admin": false,
        "verified": false,
        "enabled": false
      }
    ]
  }
  ```