pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh 'mvn verify'
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