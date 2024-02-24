pipeline {
    agent any
    stages {
        stage('Build') { 
            steps {
                powershell './gradlew assemble' 
            }
        }
    }
}