pipeline {
    agent any
    stages {
        stage('Clean') { 
            steps {
                bat './gradlew clean' 
            }
        }
        stage('Build') { 
            steps {
                withGradle {
                    bat './gradlew spotlessApply'
                    bat './gradlew assemble' 
                }
            }
        }
        stage('Test') { 
            steps {
                bat './gradlew test --rerun' 
            }
        }
    }
}