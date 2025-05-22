# Bank Microservices Application

A comprehensive banking application built with a microservices architecture, containerized with Docker, and deployable via Docker Compose or Jenkins CI/CD pipeline.

## Architecture Overview

This application consists of 5 core microservices:

1. **Account Service**: Manages user accounts and balances
2. **Transaction Service**: Processes financial transactions between accounts
3. **Authentication Service**: Handles user login, registration, and authorization
4. **Notification Service**: Sends alerts and updates to users
5. **User Service**: Manages customer profiles and personal information

An API Gateway routes all external requests to the appropriate microservices.

## Deployment Options

### Option 1: Docker Compose

To deploy using Docker Compose:

```bash
docker-compose up -d
```

The application will be accessible at http://localhost:9999

### Option 2: Jenkins Pipeline

The included Jenkinsfile provides a complete CI/CD pipeline with the following stages:

1. Git Clone: Fetch the latest code
2. Build Docker Images: Build all microservice images
3. Push Images to DockerHub: Push images to the configured DockerHub account
4. Deploy with Docker Compose: Deploy the application using Docker Compose
5. Verify Deployment: Ensure all services are running correctly

## Service Endpoints

- API Gateway: http://localhost:9999
- Account Service: http://localhost:8081
- Transaction Service: http://localhost:8082
- Authentication Service: http://localhost:8083
- Notification Service: http://localhost:8084
- User Service: http://localhost:8085

## Development

Each microservice is developed with Spring Boot and follows RESTful API principles.

To add new features or modify existing ones, update the relevant microservice code and rebuild the Docker images.

## Deployment to AWS EC2

For deployment to an Ubuntu 24.04 EC2 instance on AWS:

1. Install Docker and Docker Compose
2. Clone the repository
3. Run `docker-compose up -d`

The application will be accessible at http://your-ec2-public-ip:9999