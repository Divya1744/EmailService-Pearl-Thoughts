# Use OpenJDK 17 as base image
FROM eclipse-temurin:17-jdk-alpine

# Set work directory
WORKDIR /app

# Copy jar file into container
COPY target/emailservice-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Spring Boot default)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
