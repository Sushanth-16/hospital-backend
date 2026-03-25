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

This is a Spring Boot app. If Render does not offer Java in the runtime list, deploy it as a Docker service.

- Runtime: `Docker`
- Render will build from the repo `Dockerfile`

Required environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`
