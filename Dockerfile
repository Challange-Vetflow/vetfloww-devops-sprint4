FROM eclipse-temurin:17-jre-jammy

RUN groupadd -r vetflow && useradd -r -g vetflow -m vetflow

WORKDIR /app
COPY --chown=vetflow:vetflow vetflow.jar /app/vetflow.jar

ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
ENV SPRING_H2_CONSOLE_ENABLED=false
ENV SPRING_CACHE_TYPE=simple

EXPOSE 8080
USER vetflow

CMD ["sh", "-c", "echo 'Container rodando como usuario:' $(whoami) && exec java -jar /app/vetflow.jar"]
