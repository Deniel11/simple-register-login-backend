# Simple Register-Login Backend Project
[Open API Documentation](/gen/README.md)

This project is a simple register-login for users with token based authentication and admin roles, with email verification.

## Implementations:
Java Development Kit - JDK 17

Gradle Project with Spring Boot Framework

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

## Tests:
### Unit test: 93
### Integration test: 40
### Coverage:
- Class: 100%
- Method: 97%
- Line: 93%

## Guides
Before you use this project, you need to set up your environment variables

Or you can use env file ".env.sample".

<details>
<summary><b>Environment Variables</b></summary>

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
MAIL_SENDER_EMAIL=[YOUR_GMAIL]
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=[YOUR GMAIL USERNAME]
MAIL_PASSWORD=[YOUR GMAIL APPLICATION PASSWORD]
```
</details>

> I use gmail for email service in my application, if you would like to use different one, then change the environment variables.

> The first registration is validated, and you get admin rights. (First ID)

Require extra endpoints to the frontend:

---

- `/verify-email`

When the user registered, then gets an email which is included this endpoint. Where you need use `/api/user/verify-email`.

---

- `/change-password`

When the user want to use forgot password function, then gets an email which is included this endpoint. Where you need to use `/api/user/change-password`.


More description for the APIs:
## Endpoints

-----
<details>
<summary> <b>POST</b> - <i>[YOUR DOMAIN]</i>/api/user/registration</summary>
  
  **Request** 
  
  Body:
  ```
  {
    "username": "Alexander",
    "email": "alexander@email.com",
    "password": "password",
    "dateOfBirth": "11-11-2000"
  }
  ```

  **Response**:

  `Status code: 201`

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

  **Errors**:
- Invalid Parameter:
  ```
  Status code: 404
  Body:
  {
    "status": "error",
    "message": "[Parameter(s)] is required."
  }
  ```
- Low Password Length:
  ```
  Status code: 406
  Body:
  {
    "status": "error",
    "message": "Password must be [8] characters."
  }
  ```
- Parameter Taken:
  ```
  Status code: 409
  Body:
  {
    "status": "error",
    "message": "[Parameter] is already taken."
  }
  ```
- Wrong Email Format:
  ```
  Status code: 406
  Body:
  {
    "status": "error",
    "message": "Wrong email format."
  }
  ```
- Wrong Date Format:
  ```
  Status code: 406
  Body:
  {
    "status": "error",
    "message": "Accepted date format: [dd-mm-yyyy]."
  }
  ```
- Build Email Message Error:
  ```
  Status code: 503
  Body:
  {
    "status": "error",
    "message": "Build email message problem."
  }
  ```
- Send Email Message Error:
  ```
  Status code: 503
  Body:
  {
    "status": "error",
    "message": "Send email message problem."
  }
  ```

</details>

-----
<details>
<summary> <b>POST</b> - <i>[YOUR DOMAIN]</i>/api/user/login</summary>

  **Request** Body:
  ```
  {
    "username": "Alexander",
    "password": "password"
  }
  ```

  **Response**:


  `Status code: 200`

  Body:
  ```
  {
    "status": "ok",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
  }
  ```

  **Errors**:
- Invalid Parameter:
  ```
  Status code: 404
  Body:
  {
    "status": "error",
    "message": "[Parameter(s)] is required."
  }
  ```
- User Not Found:
  ```
  Status code: 404
  Body:
  {
    "status": "error",
    "message": "Username or password is incorrect."
  }
  ```
- User Not Activated:
  ```
  Status code: 401
  Body:
  {
    "status": "error",
    "message": "Your user is not activated."
  }
  ```
- User Not Enabled:
  ```
  Status code: 401
  Body:
  {
    "status": "error",
    "message": "Your user is not enabled."
  }
  ```

</details>

-----
<details>
<summary> <b>PUT</b> - <i>[YOUR DOMAIN]</i>/api/user/user/{id}</summary>

- To use this, you need the token from login.
- You can change only one parameter like "username" or "password".
- You need admin role to change other user or to change admin role or validate, enabled parameter. 
- You can change multiple parameter like this:


  **Request**:
    

  Header:
  ```
  {
    "key": "Authorization",
    "value": "Bearer [YOUR TOKEN]"
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


  `Status code: 200`
  

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

**Errors**:
- Custom Access Denied:
  ```
  Status code: 401
  Body:
  {
    "status": "error",
    "message": "Access Denied."
  }
  
  OR
  
  {
    "status": "error",
    "message": "Access Denied: Edit: admin, valid"
  }
  
  OR
  
  {
    "status": "error",
    "message": "Access Denied: Edit: other user"
  }
  ```
- User Not Found:
  ```
  Status code: 404
  Body:
  {
    "status": "error",
    "message": "User with id [id] is not found."
  }
  ```
- Parameter Match:
  ```
  Status code: 409
  Body:
  {
    "status": "error",
    "message": "[Parameter] parameter is already same."
  }
  ```
- Parameter Taken:
  ```
  Status code: 409
  Body:
  {
    "status": "error",
    "message": "[Parameter] is already taken."
  }
  ```
- Wrong Email Format:
  ```
  Status code: 406
  Body:
  {
    "status": "error",
    "message": "Wrong email format."
  }
  ```
- Wrong Date Format:
  ```
  Status code: 406
  Body:
  {
    "status": "error",
    "message": "Accepted date format: [dd-mm-yyyy]."
  }
  ```
- Low Password Length:
  ```
  Status code: 406
  Body:
  {
    "status": "error",
    "message": "Password must be [8] characters."
  }
  ```

</details>

-----
<details>
<summary> <b>GET</b> - <i>[YOUR DOMAIN]</i>/api/user/user/</summary>

  - To use this, you need the token from login.


  **Request**:


  Header:
  ```
  {
    "key": "Authorization",
    "value": "Bearer [YOUR TOKEN]"
  }
  ```

  **Response**:


  `Status code: 200`
  

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

  **Errors**:
- Custom Access Denied:
  ```
  Status code: 401
  Body:
  {
    "status": "error",
    "message": "Access Denied."
  }
  ```
- User Not Found:
  ```
  Status code: 404
  Body:
  {
    "status": "error",
    "message": "User with id [id] is not found."
  }
  ```

</details>

-----
<details>
<summary> <b>GET</b> - <i>[YOUR DOMAIN]</i>/api/user/user/{id}</summary>

  - To use this, you need the token from login.


  **Request**:
  
  
  Header:

  ```
  {
    "key": "Authorization",
    "value": "Bearer [YOUR TOKEN]"
  }
  ```

  **Response**:


  `Status code: 200`
  
  
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

  **Errors**:
- Custom Access Denied:
  ```
  Status code: 401
  Body:
  {
    "status": "error",
    "message": "Access Denied."
  }
  ```
- Invalid Parameter:
  ```
  Status code: 404
  Body:
  {
    "status": "error",
    "message": "[Parameter(s)] is required."
  }
  ```
- User Not Found:
  ```
  Status code: 404
  Body:
  {
    "status": "error",
    "message": "User with id [id] is not found."
  }
  ```

</details>

-----
<details>
<summary> <b>GET</b> - <i>[YOUR DOMAIN]</i>/api/user/user/users</summary>

  - To use this, you need the token from login.


  **Request**:


  Header:
  ```
  {
    "key": "Authorization",
    "value": "Bearer [YOUR TOKEN]"
  }
  ```

  **Response**:


  `Status code: 200`
  

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

  **Errors**:
- Custom Access Denied:
  ```
  Status code: 401
  Body:
  {
    "status": "error",
    "message": "Access Denied."
  }
  ```

</details>

-----
<details>
<summary> <b>GET</b> - <i>[YOUR DOMAIN]</i>/api/user/verify-email?token=[Verify email token]</summary>

  - To use this endpoint, you need path variable with verification token.

  Example:
  ```
    [YOUR DOMAIN]/api/user/verify-email?token=[YOUR VERIFICATION TOKEN]
  ```
  - When use registration, then gets an email with this link.
  - You can check it the verification token with MYSQL SELECT method.


  **Response**:


  `Status code: 202`
  
  
  Body:
  ```
    {
      "status": "ok",
      "message": "Your email has been verified."
    }
  ```

  **Errors**:
- User Already Verified:
  ```
  Status code: 406
  Body:
  {
    "status": "error",
    "message": "This user has already been verified!"
  }
  ```
- Invalid Token:
```
  Status code: 403
  Body:
  {
    "status": "error",
    "message": "Invalid token."
  }
  ```

</details>

-----
<details>
<summary> <b>GET</b> - <i>[YOUR DOMAIN]</i>/api/user/forgot-password?token=[Forgot password token]</summary>

- You can check it the forgot password token with MYSQL SELECT method.


**Request**:


- To use this endpoint, you need path variable with forgot password token.

Param example:
  ```
    [YOUR DOMAIN]/api/user/forgot-password?token=[YOUR FORGOT PASSWORD TOKEN]
  ```

Body:
  ```
  {
    "email": "[Requested email address]"
  }
  ```

**Response**:


`Status code: 202`


Body:
  ```
    {
      "status": "ok",
      "message": "Your password has been changed."
    }
  ```

**Errors**:
- Email Address Not Found:
  ```
  Status code: 404
  Body:
  {
    "status": "error",
    "message": "This email address [Email address] is not found."
  }
  ```

</details>

-----
<details>
<summary> <b>Patch</b> - <i>[YOUR DOMAIN]</i>/api/user/change-password?token=[Forgot password token]</summary>

- When use `/api/user/forgot-password`, then gets an email with this link.
- You can check it the forgot password token with MYSQL SELECT method.


**Request**:


- To use this endpoint, you need path variable with forgot password token.

Param example:
  ```
    [YOUR DOMAIN]/api/user/change-password?token=[YOUR FORGOT PASSWORD TOKEN]
  ```

Body:
  ```
  {
    "oldPassword": "[Old password]",
    "newPassword": "[New password]"
  }
  ```

**Response**:


`Status code: 202`


Body:
  ```
    {
      "status": "ok",
      "message": "Your password has been changed."
    }
  ```

**Errors**:
- Invalid Parameter:
  ```
  Status code: 404
  Body:
  {
    "status": "error",
    "message": "[Parameter(s)] is required."
  }
  ```
- Low Password Length:
  ```
  Status code: 406
  Body:
  {
    "status": "error",
    "message": "Password must be [8] characters."
  }
  ```
- Invalid Token:
  ```
  Status code: 403
  Body:
  {
    "status": "error",
    "message": "Invalid token."
  }
  ```
- Password Incorrect:
  ```
  Status code: 406
  Body:
  {
    "status": "error",
    "message": "Old password is incorrect."
  }
  ```

</details>

-----