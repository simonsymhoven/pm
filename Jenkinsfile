pipeline {
  agent {
    docker {
      image 'maven:3.3.3'
       args '-privileged -v $HOME/.m2:/home/jenkins/.m2 -ti -u 496 -e MAVEN_CONFIG=/home/jenkins/.m2 -e MAVEN_OPTS=-Xmx2048m'
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