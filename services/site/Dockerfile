FROM openjdk:21-slim AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /site

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

WORKDIR /site

COPY --from=build /site/target/*.jar site.jar

CMD ["java", "-jar", "site.jar"]
