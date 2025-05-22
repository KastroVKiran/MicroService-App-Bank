#!/bin/bash

# Script to build all microservices for the Bank Application

echo "Building Account Service..."
cd account-service
mvn clean package -DskipTests
cd ..

echo "Building Transaction Service..."
cd transaction-service
mvn clean package -DskipTests
cd ..

echo "Building Auth Service..."
cd auth-service
mvn clean package -DskipTests
cd ..

echo "Building Notification Service..."
cd notification-service
mvn clean package -DskipTests
cd ..

echo "Building User Service..."
cd user-service
mvn clean package -DskipTests
cd ..

echo "Building API Gateway..."
cd api-gateway
mvn clean package -DskipTests
cd ..

echo "All services built successfully!"
