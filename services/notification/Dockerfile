FROM openjdk:21-slim AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /notification

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

WORKDIR /notification

COPY --from=build /notification/target/*.jar notification.jar

CMD ["java", "-jar", "notification.jar"]
