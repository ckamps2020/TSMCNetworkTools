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
    stage('1.12') {
      steps {
        archiveArtifacts(artifacts: 'StaffTools1.12R1/target/TSMCNetworkTools.jar', allowEmptyArchive: true, fingerprint: true, onlyIfSuccessful: true)
      }
    }
    stage('deploy 1.8') {
      parallel {
        stage('deploy 1.8') {
          steps {
            sh 'cd StaffTools1.8R3'
            sh 'mvn clean deploy'
          }
        }
        stage('deploy 1.12') {
          steps {
            dir(path: 'StaffTools1.12R1') {
              sh 'mvn clean deploy'
            }

          }
        }
      }
    }
  }
  tools {
    maven 'M3'
  }
}