pipeline {
  agent {
    docker {
      image 'maven:3.3.3'
       args '-privileged'
    }
  }
  stages {
    stage('build') {
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