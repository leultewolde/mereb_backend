pipeline {
    agent any

    environment {
        //SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_TOKEN = credentials('SONAR_TOKEN') // replace with the actual credential ID
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Unit Test') {
            steps {
                sh './mvnw clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Deploy to Local Docker') {
                    steps {
                        sh 'docker-compose -f docker-compose.dev.yml up -d --build'
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
