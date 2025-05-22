#!/bin/bash

# Script to deploy Bank Microservices to Ubuntu 24.04 EC2 instance

# Update system
echo "Updating system packages..."
sudo apt-get update
sudo apt-get upgrade -y

# Install Docker
echo "Installing Docker..."
sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get install -y docker-ce

# Install Docker Compose
echo "Installing Docker Compose..."
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Add current user to docker group
sudo usermod -aG docker ${USER}
echo "You may need to log out and back in for docker group changes to take effect"

# Deploy with Docker Compose
echo "Deploying with Docker Compose..."
docker-compose up -d

echo "Deployment complete. The application should be accessible at http://localhost:9999"
echo "If you're accessing from outside the EC2 instance, make sure port 9999 is open in your security group"