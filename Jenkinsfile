pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        SONARQUBE_URL = 'http://54.224.219.29:9000'
        NEXUS_URL = '52.23.187.164:8081'
        NEXUS_REPO = 'maven-snapshots'
        TOMCAT_URL = 'http://100.48.223.225:8080'
        APP_NAME = 'hello-world'
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
                    echo '✅ Build successful!'
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
                echo '🔍 Scanning code quality with SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=hello-world -Dsonar.projectName=HelloWorld'
                }
            }
        }

        stage('Upload to Nexus') {
            steps {
                echo '📦 Uploading artifact to Nexus...'
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

        stage('Deploy to Tomcat') {
            steps {
                echo '🚀 Deploying to Tomcat...'
                deploy(
                    adapters: [tomcat9(
                        credentialsId: 'tomcat-credentials',
                        path: '',
                        url: "${TOMCAT_URL}"
                    )],
                    contextPath: "${APP_NAME}",
                    war: "target/${APP_NAME}.war"
                )
            }
        }
    }

    post {
        success {
            echo """
            ✅ Pipeline completed successfully!
            🌐 App is live at: ${TOMCAT_URL}/${APP_NAME}
            """
        }
        failure {
            echo '❌ Pipeline failed — check the Console Output above'
        }
    }
}
