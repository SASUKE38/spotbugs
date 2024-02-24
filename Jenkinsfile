pipeline {
    agent any
    stages {
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