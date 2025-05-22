#!/bin/bash

# Script to build all microservices for the Bank Application

echo "Building Account Service..."
cd account-service
./mvnw clean package -DskipTests
cd ..

echo "Building Transaction Service..."
cd transaction-service
./mvnw clean package -DskipTests
cd ..

echo "Building Auth Service..."
cd auth-service
./mvnw clean package -DskipTests
cd ..

echo "Building Notification Service..."
cd notification-service
./mvnw clean package -DskipTests
cd ..

echo "Building User Service..."
cd user-service
./mvnw clean package -DskipTests
cd ..

echo "Building API Gateway..."
cd api-gateway
./mvnw clean package -DskipTests
cd ..

echo "All services built successfully!"