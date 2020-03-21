pipeline {
  agent any
  tools {
    maven 'Maven 3.6.3'
    jdk 'adoptopenjdk-11.jdk'
  }
  triggers {
    pollSCM '* * * * *'
  }
  stages {
    stage('Build') {
      steps {
        echo 'Build..'
        sh 'mvn clean verify -DskipTests'
      }
    }

    stage('Test') {
      steps {
        echo 'Testing..'
        sh 'mvn test'
      }
    }

    stage('Deploy') {
      steps {
        echo 'Deploying..'
      }
    }

  }
}