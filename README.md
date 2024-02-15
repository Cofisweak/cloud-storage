# Cloud storage app

### About project
This is a multi-user application in which any user can store their files. The user can create folders, store files, delete files, rename files, etc.

### Overview
The application has several pages. Using the switch at the top of the page you can switch the theme of the page.

#### Login page
<img src="./images/login_dark.png " alt="image" width="50%" height="auto"><img src="./images/login_light.png " alt="image" width="50%" height="auto">

#### Home page
<img src="./images/home_dark.png " alt="image" width="50%" height="auto"><img src="./images/home_light.png " alt="image" width="50%" height="auto">

#### Upload 
<img src="./images/upload_dark.png " alt="image" width="50%" height="auto"><img src="./images/upload_light.png " alt="image" width="50%" height="auto">

#### File page
<img src="./images/file_dark.png " alt="image" width="50%" height="auto"><img src="./images/file_light.png " alt="image" width="50%" height="auto">

#### Search page
<img src="./images/search_dark.png " alt="image" width="50%" height="auto"><img src="./images/search_light.png " alt="image" width="50%" height="auto">


### Used technologies

- Web-server: Spring Boot
- Database: PostgreSQL, Liquibase, Hibernate
- File storage (S3): MinIO
- Frontend: Bootstrap 5
- Tests: JUnit 5, AssertJ, Testcontainers
- Deployment: Docker

### How to start application

1. Install JRE
2. Install Docker
3. Configure .env file
4. Run containers via docker-compose.yml file
5. Build project via mvnw file
6. Place .env file near .jar file
7. Run .jar file

### Environment
Environment variables must be specified in .env file. This file is required to configure the Docker container and launch the Spring Boot application.

| Variable          |       Description        |
|:------------------|:------------------------:|
| CONTEXT_PATH      | Application context path |
| PORT              |     Application port     |
| POSTGRES_HOST     |     PostgreSQL host      |
| POSTGRES_USERNAME |   PostgreSQL username    |
| POSTGRES_PASSWORD |   PostgreSQL password    |
| POSTGRES_DATABASE |   PostgreSQL database    |
| POSTGRES_PORT     |     PostgreSQL port      |
| MINIO_URL         |        MinIO url         |
| MINIO_BUCKET      |       MinIO bucket       |
| MINIO_ACCESS_KEY  |     MinIO access key     |
| MINIO_SECRET_KEY  |     MinIO secret key     |
| REDIS_HOST        |        Redis host        |
| REDIS_PASSWORD    |      Redis password      |
