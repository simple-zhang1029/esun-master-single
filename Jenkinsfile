pipeline {
    agent { docker 'maven:3.3.3'}
    stages {
        stages('Build'){
            steps{
                sh 'mvn --version'
            }
        }
        stages('Deploy'){
        }
        stages('Test'){
        }
    }
    post{
    }
}