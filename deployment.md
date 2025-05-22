# Bank Microservices Application - EC2 Deployment Guide

This guide will help you deploy the Bank Microservices Application on an Ubuntu 24.04 EC2 instance.

## Prerequisites

- An AWS EC2 instance running Ubuntu 24.04
- Git installed on the instance
- Access to the instance via SSH
- Port 9999 open in the security group

## Step-by-Step Deployment Guide

1. **Connect to your EC2 instance**
   ```bash
   ssh -i your-key.pem ubuntu@your-ec2-ip
   ```

2. **Update System Packages**
   ```bash
   sudo apt-get update
   sudo apt-get upgrade -y
   ```

3. **Install Docker**
   ```bash
   sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
   curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
   sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
   sudo apt-get update
   sudo apt-get install -y docker-ce
   ```

4. **Install Docker Compose**
   ```bash
   sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
   sudo chmod +x /usr/local/bin/docker-compose
   ```

5. **Add Current User to Docker Group**
   ```bash
   sudo usermod -aG docker ${USER}
   ```
   
   *Important: Log out and log back in for the group changes to take effect*

6. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/bank-microservices.git
   cd bank-microservices
   ```

7. **Build and Start the Services**
   ```bash
   docker-compose up -d
   ```

8. **Verify Deployment**
   ```bash
   docker-compose ps
   ```

   All services should be in the "Up" state.

## Accessing the Application

- The application will be accessible at: `http://your-ec2-ip:9999`
- You can test the API Gateway health check at: `http://your-ec2-ip:9999/api/health`

## Service Endpoints

All services are accessible through the API Gateway (port 9999):

- Account Service: `/api/accounts`
- Transaction Service: `/api/transactions`
- Auth Service: `/api/auth`
- Notification Service: `/api/notifications`
- User Service: `/api/users`

## Monitoring and Logs

To view logs for all services:
```bash
docker-compose logs -f
```

To view logs for a specific service:
```bash
docker-compose logs -f service-name
```

Available service names:
- account-service
- transaction-service
- auth-service
- notification-service
- user-service
- api-gateway

## Troubleshooting

1. If services fail to start:
   ```bash
   docker-compose down
   docker-compose up -d
   ```

2. To check service logs for errors:
   ```bash
   docker-compose logs service-name
   ```

3. To restart a specific service:
   ```bash
   docker-compose restart service-name
   ```

## Stopping the Application

To stop all services:
```bash
docker-compose down
```

To stop a specific service:
```bash
docker-compose stop service-name
```
