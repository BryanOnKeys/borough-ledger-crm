FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /src
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --no-create-home crm
COPY --from=build /src/target/crm-1.0.0-SNAPSHOT.jar /app/app.jar
USER crm
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
