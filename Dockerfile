FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/name-service-0.0.1-SNAPSHOT-standalone.jar /name-service/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/name-service/app.jar"]
