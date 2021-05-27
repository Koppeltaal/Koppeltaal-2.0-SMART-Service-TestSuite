# Koppeltaal 2.0 SMART Backend Service Test Suite
This test suite is intended for developers to support the implementation of registering their 
SMART backend service to Koppeltaal 2.0.

## Running the software

### Running using java and maven

#### Prerequisites
* Java version 11 or newer
* Maven 3.6 or newer

```shell script
mvn clean spring-boot:run
```

### Running using docker

```shell script
docker build . -t smart_testsuite && docker run --name smart_testsuite -p 8080:8080 smart_testsuite
```

## Using the software

Navigate to the url [http://localhost:8080](http://localhost:8080) to open the test suite
