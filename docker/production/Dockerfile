FROM clojure:lein AS builder

COPY . /app
WORKDIR /app

RUN ["lein", "uberjar"]

FROM openjdk:15-alpine

COPY --from=builder /app/target/name-service-*-SNAPSHOT-standalone.jar /app/name-service.jar

WORKDIR /app

CMD ["java", "-jar", "/app/name-service.jar"]
