FROM maven:3.6.3-openjdk-11 AS build
RUN mkdir -p /build
WORKDIR /build
COPY pom-default.xml /build/pom.xml
#Download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins
COPY src /build/src
RUN mvn package
