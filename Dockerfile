FROM maven:latest AS build


WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src src
COPY .mvn .mvn
RUN mvn package

FROM openjdk:11-jdk

WORKDIR /app
COPY --from=build /build/target/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
