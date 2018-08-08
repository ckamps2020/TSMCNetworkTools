pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package -s /opt/maven/3.5.3/conf/settings.xml'
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
            sh 'mvn clean deploy -s /opt/maven/3.5.3/conf/settings.xml'
          }
        }
        stage('deploy 1.12') {
          steps {
            dir(path: 'StaffTools1.12R1') {
              sh 'mvn deploy -s /opt/maven/3.5.3/conf/settings.xml'
            }

          }
        }
      }
    }
    stage('Notify') {
      steps {
        slackSend(failOnError: true, botUser: true, channel: 'dev-notifications', teamDomain: 'thesquadmc', token: 'Yd7UWIwrGc8ibjPOBHYzMgGT', tokenCredentialId: 'Yd7UWIwrGc8ibjPOBHYzMgGT', baseUrl: 'https://thesquadmc.slack.com/services/hooks/jenkins-ci/', message: '[TSMCNetworkTools] Build was triggered!')
      }
    }
  }
  tools {
    maven 'M3'
  }
  triggers {
    pollSCM('H/1 * * * *')
  }
}