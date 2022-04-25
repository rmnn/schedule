# Proof of Concept of Scheduler Service

## Current State

This is POC of scheduler service presented as single application with in-memory database and provided schedule api with Open Api Spec presented below

## Possible next steps

1. Migrate from in-memory db MySQL/Postgres
2. Introduce authentication as separate service with OAuth 2.0
3. Introduce Service Registry and api-gateway
4. 
## Quick start

```
./gradlew build
```

```
docker-compose up
```

## Curls examples 

### Sign up
```
curl --location --request POST 'http://localhost:8080/api/auth/signup' \
--header 'Content-Type: application/json' \
--data-raw '{
"username": "dageevvv",
"email": "dasd@a9sdddasm.com",
"password": "asd12123",
"role": ["admin"]
}'
```

### Login
```
curl --location --request POST 'http://localhost:8080/api/auth/signin' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "dageevvv",
    "password": "asd12123"
}'
```

### Create schedule
```
curl --location --request POST 'http://localhost:8080/api/schedules' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkYWdlZXZ2diIsImlhdCI6MTY1MDkwMzU3NCwiZXhwIjoxNjUwOTg5OTc0fQ.ZzFBSlSoLMsdS705LWCla0XH7qY5-TR-rIEq4hBUmeIcP6Vi1u-lCHZ6EO_IqzTHpaGEgPXdATKs_tntpe6H2w' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "dageevvv",
    "lengthInHours": 10,
    "startTimeSeconds": 1650903312
}'
```

### Get schedules
```
curl --location --request GET 'http://localhost:8080/api/schedules' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkYWdlZXZ2dnYiLCJpYXQiOjE2NTA5MDQwMzEsImV4cCI6MTY1MDk5MDQzMX0.cec6gfjsPD9AO2IA4M_Ex2n5KW_ZDq4I0gpx1Am_R7xQgRsAOJsFFMTo31DW-QSDLbOADEYtp5Pyab9yPgmk4g' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "dageevvv"
}'
```

<details>
  <summary>Open Api 3.0 Specification</summary>

```
openapi: 3.0.1
info:
  title: Scheduler Service
  version: v0.1
servers:
- url: http://localhost:8080
paths:
  /api/schedules:
    get:
      tags:
      - schedule-controller
      operationId: schedules
      parameters:
      - name: request
        in: query
        required: true
        schema:
          $ref: '#/components/schemas/ScheduleRequest'
      responses:
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
    post:
      tags:
      - schedule-controller
      operationId: create
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ScheduleRequest'
        required: true
      responses:
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
  /api/auth/signup:
    post:
      tags:
      - auth-controller
      operationId: registerUser
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/SignupRequest'
        required: true
      responses:
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
  /api/auth/signin:
    post:
      tags:
      - auth-controller
      operationId: authenticateUser
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/LoginRequest'
        required: true
      responses:
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
  /api/schedules/accumulates:
    get:
      tags:
      - schedule-controller
      operationId: accumulatedUserSchedule
      parameters:
      - name: request
        in: query
        required: true
        schema:
          $ref: '#/components/schemas/AccumulatedUserScheduleRequest'
      responses:
        "200":
          description: OK
          content:
            '*/*':
              schema:
                $ref: '#/components/schemas/AccumulatedUserScheduleResponse'
  /api/schedules/{scheduleId}:
    delete:
      tags:
      - schedule-controller
      operationId: delete
      parameters:
      - name: scheduleId
        in: path
        required: true
        schema:
          type: integer
          format: int64
      responses:
        "200":
          description: OK
          content:
            '*/*':
              schema:
                type: object
components:
  schemas:
    ScheduleRequest:
      required:
      - username
      type: object
      properties:
        username:
          type: string
        startTimeSeconds:
          type: integer
          format: int64
        lengthInHours:
          type: integer
          format: int32
    SignupRequest:
      required:
      - email
      - password
      - username
      type: object
      properties:
        username:
          maxLength: 20
          minLength: 3
          type: string
        email:
          maxLength: 50
          minLength: 0
          type: string
        role:
          uniqueItems: true
          type: array
          items:
            type: string
        password:
          maxLength: 40
          minLength: 6
          type: string
    LoginRequest:
      required:
      - password
      - username
      type: object
      properties:
        username:
          type: string
        password:
          type: string
    AccumulatedUserScheduleRequest:
      required:
      - startTimeSeconds
      type: object
      properties:
        startTimeSeconds:
          type: integer
          format: int64
    AccumulatedUserScheduleResponse:
      type: object
      properties:
        users:
          type: array
          items:
            $ref: '#/components/schemas/User'
    User:
      type: object
      properties:
        username:
          type: string
        totalTimeSchedule:
          type: integer
          format: int32
```
<details>
