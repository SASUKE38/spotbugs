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
    }
}