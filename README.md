# Simple Register-Login backend project

This project is a simple register-login for users with token based authentication and admin roles.

### Project have:
- Token Based Authentication
- Admin and User based Authorize
- Flyway Database Migration
- Centralized Error Handling
- Custom Exceptions
- Logging
- Mapper Service
- GitHub Actions
  - CheckStyle
- Environment Variables
  - Database Connection
  - Secret Key
- User APIs
  - Register
  - Login
  - Edit user
  - GET user
  - GET users
- FakeUser for tests
- SQL annotation for tests

#### Test coverage:
- Class: 100%
- Method: 95%
- Line: 87%

## Guides

Before you want to use this project, you need setup your environment variables.


Or you can use env file ".env.sample".


This look like:
```
DB_URL=jdbc:mysql://[YOUR MYSQL DOMAIN AND PORT]/[YOUR TABLE NAME]
DB_USERNAME=[YOUR MYSQL USERNAME]
DB_PASSWORD=[YOUR MYSQL PASSWORD]
HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect
HIBERNATE_DDL_AUTO=validate
SHOW_SQL=true
JWT_SECRET_KEY=[SELECT YOUR SECRET KEY]
```

After that, you can use these endpoints:

-----
- **POST** - *[YOUR DOMAIN]*/api/user/registration
  

  **Request** Body:
  ```
  {
    "username": "Alexander",
    "email": "alexander@email.com",
    "password": "password",
    "birthdate": "11-11-2000"
  }
  ```

  **Response**:


  Status code: 201
  ```
  {
    "id": 1,
    "username": "Alexander",
    "email": "alexander@email.com",
    "birthdate": "11-11-2000",
    "admin": false,
    "valid": false
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
  ```
  {
    "status": "ok",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
  }
  ```

-----
- **PUT** - *[YOUR DOMAIN]*/api/user/{id}
  - For use these, you need token from login.
  - You can change only 1 parameter like "username" or "password".
  - You can change multiple parameter like this:


  **Request**:
    

  Header:
  ```
  {
    "key": "Authorization",
    "value": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
  }
  ```

  Body:
  ```
  {
    "username": "Big Alexander",
    "email": "alexander@email.com",
    "password": "newPassword",
    "birthdate": "11-11-2000",
    "admin": true,
    "valid": true
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
    "birthdate": "11-11-2000",
    "admin": true,
    "valid": true
  }
  ```

-----
- **GET** - *[YOUR DOMAIN]*/api/user/
  - For use these, you need token from login.


  **Request**:


  Header:
  ```
  {
    "key": "Authorization",
    "value": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
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
    "birthdate": "11-11-2000",
    "admin": true,
    "valid": true
  }
  ```

-----
- **GET** - *[YOUR DOMAIN]*/api/user/{id}
  - For use these, you need token from login.


  **Request**:
  
  
  Header:
    ```
    {
      "key": "Authorization",
      "value": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
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
      "birthdate": "11-11-2000",
      "admin": true,
      "valid": true
    }
    ```


-----
- **GET** - *[YOUR DOMAIN]*/api/user/users
  - For use these, you need token from login.


  **Request**:


  Header:
  ```
  {
    "key": "Authorization",
    "value": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTYW55aTIiLCJpc0FkbWluIjpmYWxzZSwiZXhwIjoxNjk0NDY4MDMyLCJpYXQiOjE2OTQ0MzIwMzJ9.-2dwWhCcuMKoD3RgNHt_LO1toXmbZdFhKlKV4EpltoM"
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
        "birthdate": "11-11-2000",
        "admin": true,
        "valid": true
      },
      {
        "id": 2,
        "username": "Sanyi",
        "email": "sanyi@email.com",
        "birthdate": "11-11-2000",
        "admin": false,
        "valid": false
        }
    ]
  }
  ```