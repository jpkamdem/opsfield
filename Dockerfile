FROM node:lts-bookworm-slim AS base
WORKDIR /app
RUN apt update
RUN apt install -y curl wget fontconfig
RUN rm -rf /var/lib/apt/lists/*

FROM base AS web-deps
WORKDIR /app
COPY web/package*.json ./
RUN npm ci

FROM base AS web-production-deps
WORKDIR /app
COPY web/package*.json ./
RUN npm ci --omit=dev

FROM base AS web-build
WORKDIR /app
COPY --from=web-deps /app/node_modules/ ./node_modules/
COPY web/ ./
RUN npm run build

FROM base AS angular
WORKDIR /app
COPY --from=web-production-deps /app/node_modules/ ./
COPY --from=web-build /app/dist/* ./
CMD [ "node", "./server/server.mjs" ]

FROM maven:amazoncorretto AS server-build
WORKDIR /app
COPY server/*/pom.xml ./
RUN mvn dependency:go-offline -B
COPY server/*/src ./src
RUN mvn clean package -DskipTests

FROM openjdk:28-ea-slim-bookworm AS spring
RUN apt-get -y update; apt-get -y install curl
WORKDIR /app
COPY --from=server-build /app/target/*.jar app.jar
EXPOSE 3000
CMD ["java", "-jar", "app.jar"]