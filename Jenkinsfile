pipeline {
  agent any
  tools {
    maven 'Maven 3.6.3'
    jdk 'adoptopenjdk-11.jdk'
  }
  options {
    skipStagesAfterUnstable()
  }
  triggers {
    pollSCM '* * * * *'
  }
  stages {
    stage('Build') {
      steps {
        echo 'Build..'
        sh 'mvn clean package -DskipTests'
      }
    }
    stage('Checkstyle') {
        steps {
            sh "mvn checkstyle:check"
        }
    }

    stage('Test') {
      steps {
        echo 'Testing..'
        sh 'mvn test'
      }
    }
  }
  post {
      always {
          junit 'target/surefire-reports/*.xml'
      }
  }
}