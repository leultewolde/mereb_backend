pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'SonarQube' // Match your configured SonarQube server name
        DOCKER_COMPOSE_FILE = 'docker-compose.dev.yml'
        SONAR_TOKEN = credentials('SONAR_TOKEN')
    }

    tools {
        maven 'Maven 3.9.10'
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
                    sh 'mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=leultewolde_mereb_backend'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to a quality gate failure: ${qg.status}"
                        }
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
