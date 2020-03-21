pipeline {
  agent any
  triggers {
    scm '* * * * *'
  }
  stages {
    stage('build') {
      steps {
        sh 'mvn clean verify'
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