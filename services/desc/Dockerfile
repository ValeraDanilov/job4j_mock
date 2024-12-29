FROM openjdk:21-slim AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /desc

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

WORKDIR /desc

COPY --from=build /desc/target/*.jar desc.jar

CMD ["java", "-jar", "desc.jar"]
