FROM openjdk:8-jdk-alpine

ADD target/machinist-0.0.1-SNAPSHOT.jar machinist.jar
ENTRYPOINT ["java","-jar","/machinist.jar"]