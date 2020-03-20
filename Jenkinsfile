pipeline {
  agent {
    docker {
       image 'maven:3.6.3'
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