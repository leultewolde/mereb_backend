pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('SONAR_TOKEN')
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            when {
                changeRequest(target: 'dev')
            }
            steps {
                sh './mvnw clean verify'
            }
        }

        stage('SonarQube') {
            when {
                changeRequest(target: 'dev')
            }
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    sh """
                        ./mvnw sonar:sonar \
                        -Dsonar.projectKey=your-project-key \
                        -Dsonar.host.url=$SONAR_HOST_URL \
                        -Dsonar.login=$SONAR_TOKEN
                    """
                }
            }
        }

        stage('Quality Gate') {
            when {
                changeRequest(target: 'dev')
            }
            steps {
                timeout(time: 1, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy (Optional)') {
            when {
                changeRequest(target: 'dev')
            }
            steps {
                sh 'docker-compose -f docker-compose.dev.yml down || true'
                sh 'docker-compose -f docker-compose.dev.yml build'
                sh 'docker-compose -f docker-compose.dev.yml up -d'
                sh './wait-for-health.sh'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
