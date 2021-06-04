FROM maven:3.6.3-jdk-11 AS build

ADD pom.xml /

ARG SETTINGS_XML_USERNAME
ARG SETTINGS_XML_PASSWORD

ENV SETTINGS_XML_USERNAME=$SETTINGS_XML_USERNAME
ENV SETTINGS_XML_PASSWORD=$SETTINGS_XML_PASSWORD

ADD src /src
ADD settings.xml /settings.xml

RUN mvn --quiet clean package -s settings.xml

FROM openjdk:11.0.11-jre

COPY --from=build /target/smart-testsuite.jar /smart-testsuite.jar

ENV TZ="Europe/Amsterdam"

ENTRYPOINT [ "sh", "-c", "java -jar /smart-testsuite.jar" ]
