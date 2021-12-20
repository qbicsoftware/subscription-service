# Subscription service
[![Run Maven Tests](https://github.com/qbicsoftware/subscription-service/actions/workflows/run_tests.yml/badge.svg?branch=main)](https://github.com/qbicsoftware/subscription-service/actions/workflows/run_tests.yml)
[![CodeQL](https://github.com/qbicsoftware/subscription-service/actions/workflows/codeql-analysis.yml/badge.svg?branch=main)](https://github.com/qbicsoftware/subscription-service/actions/workflows/codeql-analysis.yml)

Small REST service to handle sample status tracking subscription cancellations.
This service allows to securely generate cancellation tokens and triggers cancellation of subscriptions

## Endpoints

### GET subscription/cancel

To cancel a subscription a secure hash needs to be generated in order to allow secure unsubscription.
The cancellation hash will be returned in the form of a string

To generate a token a valid cancellation request needs to be send:
```
{
    "project": "string",
    "userId": "string"
}
```

A valid request triggers the generation of a token based on the cancellation request.
The service answers with 200 and the token in plain text:

```
For_lfbnS9iTi4Nmwnei4LA_f8SHga1Rdz4yw6aT8zz0V8PaHm1QEbKQTv1jGCEA
```

Invalid requests are plain text requests. The service answers with 400 or 401.

### POST subscription/cancel/{token}
Cancels a subscription for the given token. The token encodes the project and the user id for the unsubscription.
A token can look like this:
```
For_lfbnS9iTi4Nmwnei4LA_f8SHga1Rdz4yw6aT8zz0V8PaHm1QEbKQTv1jGCEA
```
If the unsubscription was successful the service answers with 202 and the original cancellation request.
Unsuccessful unsubscriptions, e.g. due to invalid tokens, result answer with 400.

## Env vars
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
```
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

## How to start the service 
To run the service you first need to package it. You need Java 17 to do that.

```
mvn clean package
```

Deploy the generated jar and start the service.


