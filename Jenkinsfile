pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }
    stage('Archive ') {
      parallel {
        stage('1.8') {
          steps {
            archiveArtifacts(artifacts: 'StaffTools1.8R3/target/TSMCNetworkTools.jar', allowEmptyArchive: true, onlyIfSuccessful: true, fingerprint: true)
          }
        }
        stage('1.12') {
          steps {
            archiveArtifacts(artifacts: 'StaffTools1.12R1/target/TSMCNetworkTools.jar', fingerprint: true, onlyIfSuccessful: true)
          }
        }
      }
    }
  }
  tools {
    maven 'M3'
  }
}