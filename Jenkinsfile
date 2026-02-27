pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        SONARQUBE_URL = 'http://98.94.58.93:9000'
        NEXUS_URL = '3.88.185.106:8081'
        NEXUS_REPO = 'maven-snapshots'
        NEXUS_DOCKER_REPO = '3.88.185.106:8085'
        TOMCAT_URL = 'http://3.239.36.208:8080'
        DOCKER_SERVER = '44.203.206.111'
        APP_NAME = 'hello-world'
        IMAGE_NAME = 'hello-world-app'
        IMAGE_TAG = "v${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                echo '📥 Pulling latest code from GitHub...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Building with Maven...'
                sh 'mvn clean package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.war'
                }
            }
        }

        stage('Unit Tests') {
            steps {
                echo '🧪 Running unit tests...'
                sh 'mvn test'
            }
        }

        stage('SonarQube Scan') {
            steps {
                echo '🔍 Scanning code quality...'
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=hello-world -Dsonar.projectName=HelloWorld'
                }
            }
        }

        stage('Upload to Nexus') {
            steps {
                echo '📦 Uploading WAR to Nexus...'
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: "${NEXUS_URL}",
                    groupId: 'com.example',
                    version: '1.0-SNAPSHOT',
                    repository: "${NEXUS_REPO}",
                    credentialsId: 'nexus-credentials',
                    artifacts: [[
                        artifactId: "${APP_NAME}",
                        classifier: '',
                        file: "target/${APP_NAME}.war",
                        type: 'war'
                    ]]
                )
            }
        }

        stage('Docker Build') {
            steps {
                echo '🐳 Building Docker image...'
                sh """
                    docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                    docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest
                """
            }
        }

        stage('Docker Deploy') {
            steps {
                echo '🚀 Deploying Docker container...'
                sh """
                    # Stop and remove existing container if running
                    docker stop ${APP_NAME} 2>/dev/null || true
                    docker rm ${APP_NAME} 2>/dev/null || true

                    # Run new container
                    docker run -d \
                        --name ${APP_NAME} \
                        -p 8080:8080 \
                        --restart always \
                        ${IMAGE_NAME}:${IMAGE_TAG}

                    echo '✅ Container started successfully'
                    docker ps | grep ${APP_NAME}
                """
            }
        }
    }

    post {
        success {
            echo """
            ✅ Pipeline completed successfully!
            🐳 Docker container running on: http://${DOCKER_SERVER}:8080
            """
        }
        failure {
            echo '❌ Pipeline failed — check Console Output above'
        }
    }
}
