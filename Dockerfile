FROM maven:3.6.3-jdk-11 AS build

ADD pom.xml /

ADD src /src

RUN mvn --quiet clean package

FROM openjdk:8u181-jre-alpine

RUN apk update && apk add bash openssl

COPY --from=build /target/gids-hti-testsuite.jar /gids-hti-testsuite.jar

ENV TZ="Europe/Amsterdam"

ADD entrypoint.bash /

RUN chmod +x entrypoint.bash

EXPOSE 8080

ENTRYPOINT [ "/bin/bash", "entrypoint.bash" ]
