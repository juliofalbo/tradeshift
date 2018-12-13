[![Build Status](https://travis-ci.org/juliofalbo/tradeshift.svg?branch=master)](https://travis-ci.org/juliofalbo/tradeshift)

# Tradeshift Challenge - Backend Position
 This application was developed following level 3 of the [Richardson Maturity Model](https://github.com/juliofalbo/poc-restful-api) (Restful + HATEOAS)

## Used Stack 

   * Spring Boot 2.1.1
   * Java 8
   * Gradle
   * RestAssured
   * MongoDB

### Build Project

```
./gradlew build
```

### Tests
The strategy used for the tests was that of integration tests, using the [RestAssured](http://rest-assured.io/)

### Swagger
To access the swagger, simply run the application and enter the **/swagger-ui.html** path.
Example running local
```
http://localhost:8080/swagger-ui.html
```

##### To run the tests
```
./gradlew test
```

### Run app with docker-compose
```
docker-compose up
```

_____

###### By: Júlio Falbo
