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
        archiveArtifacts(artifacts: 'NetworkTools-1.8/target/TSMCNetworkTools.jar', allowEmptyArchive: true, onlyIfSuccessful: true, fingerprint: true)
      }
    }
    stage('1.12') {
      steps {
        archiveArtifacts(artifacts: 'NetworkTools-1.12/target/TSMCNetworkTools.jar', allowEmptyArchive: true, fingerprint: true, onlyIfSuccessful: true)
      }
    }

    stage('deploy') {
      when {
        branch 'master'
      }

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
  }

  post {
    success {
      slackSend(color: 'good', message: "`${env.BRANCH_NAME}` Build ${env.BUILD_NUMBER} was successful: ${env.BUILD_URL}")
    }

    failure {
      slackSend(color: 'danger', message: "`${env.BRANCH_NAME}` Build ${env.BUILD_NUMBER} was a failure: ${env.BUILD_URL}")
    }

    aborted {
      slackSend(color: 'warning', message: "`${env.BRANCH_NAME}` Build ${env.BUILD_NUMBER} was aborted: ${env.BUILD_URL}")
    }
  }

  tools {
    maven 'M3'
  }

  triggers {
    pollSCM('H/1 * * * *')
  }
}