# hospital-backend

## Run locally

Set these environment variables or add equivalent values in `application.properties`:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`

Then run:

```bash
./mvnw spring-boot:run
```

## Deploy on Render

This is a Spring Boot app, not a Node app. Render should use Maven to build and Java to start it.

- Build command: `./mvnw clean package -DskipTests`
- Start command: `java -jar target/backend-0.0.1-SNAPSHOT.jar`

Required environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`
