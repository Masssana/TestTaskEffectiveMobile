
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/bank_rest-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
