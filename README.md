# GIDS HTI Test Suite
This test suite is intended for developers to support the implementation of the HTI protocol.

This suite supports both the development of portal applications as module applications.

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
docker build . -t hti_testsuite && docker run --rm -name hti_testsuite -p 8080:8080 hti_testsuite
```

## Using the software

Navigate to the url [http://localhost:8080](http://localhost:8080) to open the portal side of the 
test suite.    
