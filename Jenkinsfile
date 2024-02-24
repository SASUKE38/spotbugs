pipeline {
    agent any
    stages {
        stage('Build') { 
            steps {
                sh 'gradlew spotlessApply'
                sh 'gradlew assemble' 
            }
        }
    }
}