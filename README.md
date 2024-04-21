# Auth service Documentation
***
### Proiect IDP 2024
### Catruc Ionel 343C3 & Veaceslav Cazanov 343C3
***

## Description

Auth service. This service will handle
- User:
  - register
  - authentication/login
  - refresh access token
  - get user information

This service, at the register/login will issue an access token and a refresh token used to access protected endpoint and resource.
Access token will have an expiration of 24H, refresh token: 1 week. 

This service communicates with IO service in order to store, receive, verify user data. Also, token operations (save/revoke/verify/refresh), will be done by communicating with the IO service.

**_NOTE:_** All communication is done via HTTP protocol, using application/json as Content-Type.
***
## Default users:
- USER (password: 123)
    - johndoe@example.com
    - janesmith@example.com
    - michaeljohnson@example.com
    - emilybrown@example.com
    - williamjones@example.com
    - sarahdavis@example.com
    - davidmiller@example.com
    - emmawilson@example.com
    - alexandertaylor@example.com
    - samanthaanderson@example.com
- MANAGER (password: manager)
    - manager@manager.com
- ADMIN (password: admin)
    - admin@admin.com
***
## Required environmental variables for application to run
You can look at .env file.

- IO_SERVICE_URL - URL of the IO service. 
- AUTH_SERVICE_PORT


- IO_SERVICE_USERS_ENDPOINT=`/api/v1/users` 
- IO_SERVICE_USERS_FIND_BY_EMAIL=`/email`
- IO_SERVICE_USERS_REGISTER=`/register` 
- IO_SERVICE_USERS_VALIDATE_LOGIN=`/validate-login`

  
- IO_SERVICE_TOKENS_ENDPOINT=`/api/v1/tokens`
- IO_SERVICE_TOKENS_LOGOUT=`/logout`
- IO_SERVICE_TOKENS_REVOKE=`/revoke` 
- IO_SERVICE_TOKENS_IS_REFRESH=`/is-refresh`

## Errors handling
In case of an error occurred on the service, a message of type ErrorMessage will be returned, which contains:
- status HTTP status
- timestamp
- errorCode (of type `ro.idp.upb.ioservice.exception.handle.ErrorMessage`)
- debugMessage
- validationErrors - list of validation errors (in case provided request DTO is invalid)
- path - API path at which error occurred

***
## Exposed endpoints
- Auth: base `/api/v1/auth` - public allowed endpoints
  - POST `/register` - user registration (after successful registration, tokens will be issued to user)
    - required valid body with these fields:
      - firstName
      - lastName
      - email
      - password
    - Response:
      - accessToken
      - refreshToken
  - POST `/authenticate` - login
    - required valid body with these fields:
      - password
      - email
    - Response:
      - accessToken
      - refreshToken
  - POST `/refresh-token` - refresh tokens using refresh token
    - required HTTP request header:
      - `Authentication: Bearer <token_value>`
    - Response:
      - accessToken
      - refreshToken
- User: base `/api/v1/users` - <b>private, secured endpoints. In order to authorize access, access token issued at registration/login should be placed inside HTTP Header `Authentication: Bearer <accessToken>`.</b>
  - GET `/me` - get user info
    - required HTTP request header:
      - `Authentication: Bearer <accessToken>`
    - Response:
      - id - userId
      - email
      - firstName
      - lastName
      - role


