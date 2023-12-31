# openapi-java-client

simple_register_login API
- API version: 1.0.0
  - Build date: 2023-09-28T20:21:58.973895300+02:00[Europe/Budapest]

simple_register_login API


*Automatically generated by the [OpenAPI Generator](https://openapi-generator.tech)*


## Requirements

Building the API client library requires:
1. Java 1.7+
2. Maven/Gradle

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn clean install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn clean deploy
```

Refer to the [OSSRH Guide](http://central.sonatype.org/pages/ossrh-guide.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>org.openapitools</groupId>
  <artifactId>openapi-java-client</artifactId>
  <version>1.0.0</version>
  <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "org.openapitools:openapi-java-client:1.0.0"
```

### Others

At first generate the JAR by executing:

```shell
mvn clean package
```

Then manually install the following JARs:

* `target/openapi-java-client-1.0.0.jar`
* `target/lib/*.jar`

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://simple_register_login");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String token = "token_example"; // String | 
    PasswordDTO passwordDTO = new PasswordDTO(); // PasswordDTO | 
    try {
      MessageDTO result = apiInstance.changePassword(token, passwordDTO);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#changePassword");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://simple_register_login*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**changePassword**](docs/DefaultApi.md#changePassword) | **PATCH** /api/user/change-password | PATCH api/user/change-password
*DefaultApi* | [**createAuthenticationToken**](docs/DefaultApi.md#createAuthenticationToken) | **POST** /api/user/login | POST api/user/login
*DefaultApi* | [**forgotPassword**](docs/DefaultApi.md#forgotPassword) | **GET** /api/user/forgot-password | GET api/user/forgot-password
*DefaultApi* | [**getOwnUser**](docs/DefaultApi.md#getOwnUser) | **GET** /api/user | GET api/user
*DefaultApi* | [**getUser**](docs/DefaultApi.md#getUser) | **GET** /api/user/{id} | GET api/user/{id}
*DefaultApi* | [**getUsers**](docs/DefaultApi.md#getUsers) | **GET** /api/user/users | GET api/user/users
*DefaultApi* | [**registration**](docs/DefaultApi.md#registration) | **POST** /api/user/registration | POST api/user/registration
*DefaultApi* | [**updateUser**](docs/DefaultApi.md#updateUser) | **PUT** /api/user/{id} | PUT api/user/{id}
*DefaultApi* | [**verifyEmailAddress**](docs/DefaultApi.md#verifyEmailAddress) | **GET** /api/user/verify-email | GET api/user/verify-email


## Documentation for Models

 - [AuthenticationRequestDTO](docs/AuthenticationRequestDTO.md)
 - [AuthenticationResponseDTO](docs/AuthenticationResponseDTO.md)
 - [EmailDTO](docs/EmailDTO.md)
 - [MessageDTO](docs/MessageDTO.md)
 - [PasswordDTO](docs/PasswordDTO.md)
 - [RegisteredUserDTO](docs/RegisteredUserDTO.md)
 - [RegisteredUserDTOList](docs/RegisteredUserDTOList.md)
 - [UpdateUserDTO](docs/UpdateUserDTO.md)
 - [UserDTO](docs/UserDTO.md)



