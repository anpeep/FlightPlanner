FROM jelastic/maven:3.9.5-openjdk-21 AS maven_builder
WORKDIR /app
COPY backend/test/pom.xml .
COPY backend/test/src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk
WORKDIR /app
COPY --from=maven_builder /app/target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]
