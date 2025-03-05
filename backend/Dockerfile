# Use the official Maven image to build the application
FROM jelastic/maven:3.9.5-openjdk-21 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and source code to the container

COPY ./airplane/pom.xml .
COPY ./airplane/src ./src

# Build the application
RUN mvn clean package -DskipTests
# After the build command
RUN ls -al /app/target

# Use the official OpenJDK image to run the application
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/FlightPlanner-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on (adjust as needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
