FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /app
COPY . .
RUN mvn --batch-mode clean package -Dmaven.test.skip=true

FROM wiremock/wiremock:latest AS wiremock
COPY --from=build /app/target/wiremock-graphql-extension-*-jar-with-dependencies.jar /var/wiremock/extensions/wiremock-graphql-extension-jar-with-dependencies.jar
