FROM openjdk:21-slim AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /auth

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

WORKDIR /auth
COPY --from=build /auth/target/*.jar auth.jar

CMD ["java", "-jar", "auth.jar"]
