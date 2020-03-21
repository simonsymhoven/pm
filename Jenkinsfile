pipeline {
  agent any
  tools {
    maven 'Maven 3.6.3'
  }
  triggers {
    pollSCM '* * * * *'
  }
  stages {
    stage('Build') {
      steps {
        echo 'Build..'
        sh 'mvn clean verify'
      }
    }

    stage('Test') {
      steps {
        echo 'Testing..'
      }
    }

    stage('Deploy') {
      steps {
        echo 'Deploying..'
      }
    }

  }
}