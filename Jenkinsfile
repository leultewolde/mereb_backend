pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'SonarQube' // Match your configured SonarQube server name
        DOCKER_COMPOSE_FILE = 'docker-compose.dev.yml'
    }

    options {
        skipDefaultCheckout()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Sonar + Build + Test') {
            when {
                changeRequest(target: 'dev')
            }
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    sh './mvnw clean verify sonar:sonar -Djacoco.skip=false'
                }
            }
        }

        stage('Quality Gate') {
            when {
                changeRequest(target: 'dev')
            }
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    //waitForQualityGate abortPipeline: true
                    script {
                        def qualityGate = waitForQualityGate()
                        echo "Quality Gate status: ${qualityGate.status}"
                    }
                }
            }
        }




        stage('Deploy (Dev Docker)') {
            steps {
                script {
                    sh 'docker-compose -f ${DOCKER_COMPOSE_FILE} down || true'
                    sh 'docker compose -f ${DOCKER_COMPOSE_FILE} up -d --build'
                    sh 'docker compose -f ${DOCKER_COMPOSE_FILE} ps'

                    echo 'Waiting for app health...'
                    sh '''
                        for i in {1..10}; do
                          if curl -sf http://localhost:8080/actuator/health; then
                            echo "App is healthy!"
                            exit 0
                          fi
                          echo "Waiting for app..."
                          sleep 5
                        done
                        echo "App failed to start in time"
                        exit 1
                    '''
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
