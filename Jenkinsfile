pipeline {
    agent { docker { image 'maven:3.3.3' } }
    stages {
        stage('build') {
            git url: 'https://github.com/simonsymhoven/pm'
            steps {
                sh 'mvn clean package'
            }
        }
        stage('test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}