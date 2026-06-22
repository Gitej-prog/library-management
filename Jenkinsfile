pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Fetching code from GitHub...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building project...'
		sh 'chmod +x mvnw'
                sh './mvnw clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                sh './mvnw test'
            }
        }

        stage('Package') {
            steps {
                echo 'Creating JAR...'
                sh './mvnw clean package -DskipTests'
            }
        }
    }
}
