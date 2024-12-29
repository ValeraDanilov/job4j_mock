FROM openjdk:21-slim AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /mock

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

WORKDIR /mock

COPY --from=build /mock/target/*.jar mock.jar

CMD ["java", "-jar", "mock.jar"]
