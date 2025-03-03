# Maven build
FROM jelastic/maven:3.9.5-openjdk-21 AS maven_builder
WORKDIR /app
COPY backend/test/pom.xml .
COPY backend/test/src ./src
RUN mvn clean package -DskipTests

# JAR
RUN ls -al /app/target/

# Spring Boot layertools
FROM openjdk:21-jdk AS builder
WORKDIR /app
COPY --from=maven_builder /app/target/*.jar application.jar
RUN ls -al /app
RUN java -Djarmode=layertools -jar application.jar extract

# image
FROM openjdk:21-jdk
WORKDIR /app
COPY --from=builder /app/dependencies/ ./dependencies/
COPY --from=builder /app/spring-boot-loader/ ./spring-boot-loader/
COPY --from=builder /app/snapshot-dependencies/ ./snapshot-dependencies/
COPY --from=builder /app/application/ ./application/

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
