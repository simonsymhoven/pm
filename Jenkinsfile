pipeline {
  agent any
  triggers {
    pollSCM '* * * * *'
  }
  stages {
    stage('build') {
      steps {
        echo 'Build..'
      }
    }

    stage('test') {
      steps {
        echo 'Testing..'
      }
    }

    stage('deploy') {
      steps {
        echo 'Deploying..'
      }
    }

  }
}