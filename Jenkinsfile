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
                    steps {
                        script {
                            def ceTaskId = sh(script: "grep ceTaskId .scannerwork/report-task.txt | cut -d'=' -f2", returnStdout: true).trim()
                    echo "Polling SonarQube for task ID: ${ceTaskId}"

                    def status = "PENDING"
                    def retries = 30

                    for (int i = 0; i < retries; i++) {
                                sleep 5
                        def taskJson = sh(script: "curl -s -u ${SONAR_TOKEN}: https://sonarqube:9000/api/ce/task?id=${ceTaskId}", returnStdout: true).trim()
                        status = sh(script: "echo '${taskJson}' | jq -r '.task.status'", returnStdout: true).trim()
                        echo "Current SonarQube task status: ${status}"

                        if (status == "SUCCESS") {
                                    break
                        } else if (status == "FAILED" || i == retries - 1) {
                                    error "SonarQube task failed or timed out"
                        }
                    }

                    def analysisId = sh(script: "echo '${taskJson}' | jq -r '.task.analysisId'", returnStdout: true).trim()
                    def gateJson = sh(script: "curl -s -u ${SONAR_TOKEN}: https://sonarqube:9000/api/qualitygates/project_status?analysisId=${analysisId}", returnStdout: true).trim()
                    def qualityStatus = sh(script: "echo '${gateJson}' | jq -r '.projectStatus.status'", returnStdout: true).trim()
                    echo "Quality Gate result: ${qualityStatus}"

                    if (qualityStatus != "OK") {
                                error "Quality Gate failed: ${qualityStatus}"
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
