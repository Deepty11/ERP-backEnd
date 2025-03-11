# Use a base image that has Java installed
FROM openjdk:17-jdk-slim as build

# Set working directory for the container
WORKDIR /app

# Copy the Gradle wrapper files into the container
COPY gradlew gradlew.bat /app/
COPY gradle /app/gradle

# Copy the source code of your Spring Boot application
COPY . /app/

# Make the Gradle wrapper executable (necessary for Unix-based systems)
RUN chmod +x gradlew

# Build the application using the Gradle wrapper
RUN ./gradlew clean build -x test

# Now, use a smaller image to run the app
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/build/libs/*.jar /app/erp-backend-app.jar

# Expose the application port (change it according to your configuration)
EXPOSE 8000

# Set the command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/erp-backend-app.jar"]