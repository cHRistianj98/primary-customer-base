pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install maven') {
            steps {
                sh 'curl -s "https://get.sdkman.io" | bash'
                sh 'source "$HOME/.sdkman/bin/sdkman-init.sh"'
                sh 'sdk install maven'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }
}