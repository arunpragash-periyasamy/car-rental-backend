# Use an OpenJDK image for Java 21 to run the application
FROM openjdk:21-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/car-rental-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
