pipeline {
    agent any
    
    environment {
        // Si Jenkins est dans Docker, utiliser 'sonarqube', sinon 'localhost'
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_TOKEN = credentials('sonar-token')
        DOCKER_COMPOSE = 'docker-compose'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Cloning repository from GitHub...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building all microservices with Maven...'
                sh '''
                    mvn clean install -DskipTests
                '''
            }
        }
        
        stage('SonarQube Analysis - Car Service') {
            steps {
                echo 'Running SonarQube analysis for Car service...'
                script {
                    try {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                cd car
                                mvn sonar:sonar \
                                    -Dsonar.projectKey=car-service \
                                    -Dsonar.projectName=Car Service \
                                    -Dsonar.sources=src/main/java \
                                    -Dsonar.java.binaries=target/classes \
                                    -Dsonar.host.url=${SONAR_HOST_URL} \
                                    -Dsonar.login=${SONAR_TOKEN}
                            '''
                        }
                    } catch (Exception e) {
                        echo "SonarQube analysis failed for Car service: ${e.getMessage()}"
                        // Continue even if SonarQube fails
                    }
                }
            }
        }
        
        stage('SonarQube Analysis - Client Service') {
            steps {
                echo 'Running SonarQube analysis for Client service...'
                script {
                    try {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                cd client
                                mvn sonar:sonar \
                                    -Dsonar.projectKey=client-service \
                                    -Dsonar.projectName=Client Service \
                                    -Dsonar.sources=src/main/java \
                                    -Dsonar.java.binaries=target/classes \
                                    -Dsonar.host.url=${SONAR_HOST_URL} \
                                    -Dsonar.login=${SONAR_TOKEN}
                            '''
                        }
                    } catch (Exception e) {
                        echo "SonarQube analysis failed for Client service: ${e.getMessage()}"
                        // Continue even if SonarQube fails
                    }
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                echo 'Building Docker images for all microservices...'
                sh '''
                    docker-compose build --no-cache
                '''
            }
        }
        
        stage('Deploy with Docker Compose') {
            steps {
                echo 'Deploying microservices with Docker Compose...'
                sh '''
                    docker-compose down || true
                    docker-compose up -d
                '''
            }
        }
        
        stage('Health Check') {
            steps {
                echo 'Waiting for services to be ready...'
                sh '''
                    sleep 30
                    echo "Checking Eureka Server..."
                    curl -f http://localhost:8761/ || exit 1
                    echo "Checking Car Service..."
                    curl -f http://localhost:8081/api/cars/health || exit 1
                    echo "Checking Client Service..."
                    curl -f http://localhost:8082/api/clients/health || exit 1
                '''
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline executed successfully!'
            echo 'Services are deployed and running.'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
        always {
            echo 'Pipeline execution completed.'
        }
    }
}