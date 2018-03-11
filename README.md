# Simple social network written in Kotlin and Spring Boot 2

This project is an example of microservices-based application using Kotlin, Spring Boot 2, Spring Cloud and Spring OAuth2.

# Application architecture

The application incudes 7 services:
 * auth-service - Authorization server that provides OAuth2 authorization in password and code flow
 * hashtag-service - Hashtag service
 * user-service - User service
 * post-service - Post service
 * aggregation-service - Aggregation service that aggregates data from user, hashtag and post microservices
 * gateway-service - Zuul API gateway that is endpoint
 * eureka-service - Eureka server


# How to run application

### Gradle:
```
./gradlew build
```

### Docker compose:
```
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

### Front-end app:
```
npm install
npm run dev
```
