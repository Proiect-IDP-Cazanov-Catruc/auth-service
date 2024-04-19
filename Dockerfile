# First stage: build the application
FROM maven:3.9.6-amazoncorretto-17 as build
COPY . /auth-service
WORKDIR /auth-service
RUN mvn package -DskipTests

# Second stage: jar container
FROM maven:3.8.5-openjdk-17
COPY --from=build /auth-service/target/auth-service-0.0.1.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]