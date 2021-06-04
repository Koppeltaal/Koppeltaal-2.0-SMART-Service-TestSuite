FROM maven:3.8.1-openjdk-11 AS build

ADD pom.xml /

ARG SETTINGS_XML_USERNAME
ARG SETTINGS_XML_PASSWORD

ENV SETTINGS_XML_USERNAME=$SETTINGS_XML_USERNAME
ENV SETTINGS_XML_PASSWORD=$SETTINGS_XML_PASSWORD

ADD src /src
ADD settings.xml /settings.xml

RUN mvn --quiet clean package -s settings.xml

FROM openjdk:8u181-jre-alpine

RUN apk update && apk add bash openssl

COPY --from=build /target/smart-testsuite.jar /smart-testsuite.jar

ENV TZ="Europe/Amsterdam"

ADD entrypoint.bash /

RUN chmod +x entrypoint.bash

EXPOSE 8080

ENTRYPOINT [ "/bin/bash", "entrypoint.bash" ]
