pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }
    stage('') {
      steps {
        archiveArtifacts(onlyIfSuccessful: true, artifacts: '*.jar', fingerprint: true)
      }
    }
  }
}