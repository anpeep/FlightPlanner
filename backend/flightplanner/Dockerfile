# Use the official Maven image to build the application
FROM jelastic/maven:3.9.5-openjdk-21 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and source code
COPY ./pom.xml .
COPY ./src ./src
# Build the application
RUN mvn clean package -DskipTests
RUN ls -al /app/target

# Check if the JAR is created (for debugging)
RUN ls -al /app/target
RUN find /app/target -name "*.jar"

# Use the official OpenJDK image to run the application
FROM openjdk:21-jdk

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage (ensure the path is correct)
COPY --from=build /app/target/flightplanner-0.0.1-SNAPSHOT.jar app.jar
# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
