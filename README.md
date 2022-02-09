# <p align=center>Subscription REST Service</p>

<div align="center">

[![Build Maven Package](https://github.com/qbicsoftware/subscription-service/actions/workflows/build_package.yml/badge.svg)](https://github.com/qbicsoftware/spring-boot-rest-service-template/actions/workflows/build_package.yml)
[![Run Maven Tests](https://github.com/qbicsoftware/subscription-service/actions/workflows/run_tests.yml/badge.svg)](https://github.com/qbicsoftware/spring-boot-rest-service-template/actions/workflows/run_tests.yml)
[![CodeQL](https://github.com/qbicsoftware/subscription-service/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/qbicsoftware/spring-boot-rest-service-template/actions/workflows/codeql-analysis.yml)
[![release](https://img.shields.io/github/v/release/qbicsoftware/subscription-service?include_prereleases)](https://github.com/qbicsoftware/spring-boot-rest-service-template/releases)

![license](https://img.shields.io/github/license/qbicsoftware/spring-boot-rest-service-template)
![language](https://img.shields.io/badge/language-java-blue.svg)
![framework](https://img.shields.io/badge/framework-spring-blue.svg)

</div>

Small REST service to handle sample status tracking subscription cancellations.
This service allows to securely generate cancellation tokens and triggers cancellation of subscriptions

## How to run

First compile the project and build an executable java archive:

```
mvn clean package
```

The JAR file will be created in the ``/target`` folder, for example:

```
|-target
|---subscription-service-1.0.0-SNAPSHOT.jar
|---...
```

Just change into the folder and run the REST service app with:

```
java -jar subscription-service-1.0.0-SNAPSHOT.jar
```

### Configuration

#### Properties

The default configuration of the app binds to the local port 8080 to the systems localhost:

```
http://localhost:8080
```

If you want to change the server port, let's say `8085`, you can configure it by setting the
`server.port` property explicitly:

```
java -jar -Dserver.port=8085 subscription-service-1.0.0-SNAPSHOT.jar
```

#### Environment Variables
The env variables contain information about the salt and the secret. Both of them are used to encrypt and decrypt user information.

| environment variable       | description               |
|----------------------------|---------------------------|
| `USER_DB_DIALECT`          | The database dialect      |
| `USER_DB_DRIVER`           | The database driver       |
| `USER_DB_URL`              | The database host address |
| `USER_DB_USER_NAME`        | The database user name    |
| `USER_DB_USER_PW`          | The database password     |
| `ENCRYPTION_KEY`           | The encryption key        |
| `ENCRYPTION_SALT`          | The encryption salt       |
| `SERVICE_OFFICER_NAME`     | The office name           |
| `SERVICE_OFFICER_PASSWORD` | The officer password      |

The application properties file could look like the following:
```properties
databases.users.database.dialect=${:org.hibernate.dialect.MariaDBDialect}
databases.users.database.driver=${USER_DB_DRIVER:com.mysql.cj.jdbc.Driver}
databases.users.database.url=${USER_DB_URL:localhost}
databases.users.user.name=${USER_DB_USER_NAME:myusername}
databases.users.user.password=${USER_DB_USER_PW:astrongpassphrase!}
encryption.secret=${ENCRYPTION_KEY:12345678901234}
encryption.salt=${ENCRYPTION_SALT:123456789!1234567}
service.officer.name=${SERVICE_OFFICER_NAME:ChuckNorris}
service.officer.password=${SERVICE_OFFICER_PASSWORD:astrongpassphrase!}
```

## How to use

### Endpoints

#### POST /subscriptions/tokens

To cancel a subscription a token needs to be generated in order to allow unsubscription.
The cancellation token is returned as String.

To generate a token a valid cancellation request needs to be send:
```json
{
    "project": "string",
    "userId": "string"
}
```

A valid request triggers the generation of a token based on the cancellation request.
The service answers with 201 and the token in plain text:

```
For_lfbnS9iTi4Nmwnei4LA_f8SHga1Rdz4yw6aT8zz0V8PaHm1QEbKQTv1jGCEA
```

Invalid requests are plain text requests. The service answers with 400 or 401.

#### DELETE subscriptions/{token}
Cancels a subscription for the given token. The token encodes the project and the user id for the unsubscription.
A token can look like this:
```
For_lfbnS9iTi4Nmwnei4LA_f8SHga1Rdz4yw6aT8zz0V8PaHm1QEbKQTv1jGCEA
```
If the unsubscription was successful the service answers with 204 and no content.
Unsuccessful removal of a subscription, e.g. due to invalid tokens, result answer with 400. 
In case of correctly formatted tokens but unsuccessful unsubscription, the answer will be 422 (Unprocessable Entity).

## License

This work is licensed under the [MIT license](https://mit-license.org/).

**Note**: This work uses the [Spring Framework](https://github.com/spring-projects) and derivatives from the Spring framework family, which are licensed under [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0).

