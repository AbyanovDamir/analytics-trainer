FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/analytics-trainer-1.0.0-jar-with-dependencies.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
