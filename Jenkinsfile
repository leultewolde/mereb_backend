pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'SonarQube' // Match your configured SonarQube server name
        DOCKER_COMPOSE_FILE = 'docker-compose.dev.yml'
        SONAR_TOKEN = credentials('SONAR_TOKEN')
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

        stage('SonarQube Analysis') {
                    environment {
                        SONAR_HOST_URL = 'http://sonarqube:9000'
            }
            steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'TOKEN')]) {
                            sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=mereb_backend \
                          -Dsonar.host.url=$SONAR_HOST_URL \
                          -Dsonar.login=$TOKEN
                    """

                    echo 'Waiting for SonarQube task to finish...'

                    sh '''
                        SONAR_TASK_ID=$(grep ceTaskId .scannerwork/report-task.txt | cut -d= -f2)
                        SONAR_URL="$SONAR_HOST_URL/api/ce/task?id=$SONAR_TASK_ID"
                        echo "Polling \$SONAR_URL"
                        while true; do
                            STATUS=$(curl -s -u "$TOKEN:" "$SONAR_URL" | jq -r .task.status)
                            if [ "$STATUS" == "SUCCESS" ]; then
                                echo "SonarQube analysis succeeded."
                                break
                            elif [ "$STATUS" == "FAILED" ]; then
                                echo "SonarQube analysis failed."
                                exit 1
                            else
                                echo "Status: $STATUS. Waiting..."
                                sleep 5
                            fi
                        done
                    '''
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
