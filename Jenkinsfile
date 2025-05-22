pipeline {
    agent any
    
    environment {
        DOCKER_HUB_CRED = 'docker-cred'
        DOCKER_USERNAME = 'kastrov'
    }
    
    stages {
        stage('Git Clone') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    // Build all microservice images
                    sh 'docker build -t ${DOCKER_USERNAME}/bank-account-service:latest ./account-service'
                    sh 'docker build -t ${DOCKER_USERNAME}/bank-transaction-service:latest ./transaction-service'
                    sh 'docker build -t ${DOCKER_USERNAME}/bank-auth-service:latest ./auth-service'
                    sh 'docker build -t ${DOCKER_USERNAME}/bank-notification-service:latest ./notification-service'
                    sh 'docker build -t ${DOCKER_USERNAME}/bank-user-service:latest ./user-service'
                    sh 'docker build -t ${DOCKER_USERNAME}/bank-api-gateway:latest ./api-gateway'
                }
            }
        }
        
        stage('Push Images to DockerHub') {
            steps {
                script {
                    withCredentials([string(credentialsId: "${DOCKER_HUB_CRED}", variable: 'DOCKER_PWD')]) {
                        sh 'echo ${DOCKER_PWD} | docker login -u ${DOCKER_USERNAME} --password-stdin'
                        
                        // Push all images to DockerHub
                        sh 'docker push ${DOCKER_USERNAME}/bank-account-service:latest'
                        sh 'docker push ${DOCKER_USERNAME}/bank-transaction-service:latest'
                        sh 'docker push ${DOCKER_USERNAME}/bank-auth-service:latest'
                        sh 'docker push ${DOCKER_USERNAME}/bank-notification-service:latest'
                        sh 'docker push ${DOCKER_USERNAME}/bank-user-service:latest'
                        sh 'docker push ${DOCKER_USERNAME}/bank-api-gateway:latest'
                    }
                }
            }
        }
        
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    // Pull latest images
                    sh 'docker-compose pull'
                    
                    // Stop and remove existing containers
                    sh 'docker-compose down'
                    
                    // Start containers with new images
                    sh 'docker-compose up -d'
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                script {
                    // Check if all services are running
                    sh 'docker-compose ps'
                    
                    // Give services some time to start up
                    sh 'sleep 30'
                    
                    // Test API gateway endpoint
                    sh 'curl -s http://localhost:9999/api/health || true'
                }
            }
        }
    }
    
    post {
        always {
            echo 'Cleaning up workspace'
            cleanWs()
        }
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}