# ── Build stage ──────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

RUN mkdir -p /root/.m2 && echo '<settings><mirrors><mirror><id>aliyun</id><url>https://maven.aliyun.com/repository/public</url><mirrorOf>*</mirrorOf></mirror></mirrors></settings>' > /root/.m2/settings.xml

WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -B || mvn clean package -DskipTests -B || mvn clean package -DskipTests -B

# ── Runtime stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/target/*.jar app.jar

RUN chown spring:spring app.jar

USER spring

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
