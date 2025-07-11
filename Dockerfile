FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy everything
COPY . .

# Give permission to mvnw (in case Linux complains)
RUN chmod +x mvnw

# Build the project (skip tests to speed it up)
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/emailservice-0.0.1-SNAPSHOT.jar"]
