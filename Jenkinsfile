pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }
    stage('1.8') {
      steps {
        archiveArtifacts(artifacts: 'StaffTools1.8R3/target/TSMCNetworkTools.jar', allowEmptyArchive: true, onlyIfSuccessful: true, fingerprint: true)
      }
    }
    stage('') {
      steps {
        archiveArtifacts(artifacts: 'StaffTools1.12R1/target/TSMCNetworkTools.jar', allowEmptyArchive: true, fingerprint: true, onlyIfSuccessful: true)
      }
    }
  }
  tools {
    maven 'M3'
  }
}