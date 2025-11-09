@Library('jenkins-shared-lib') _
def GIT_URL = "git@github.com:ioannsnakej/bookstore.git"

pipeline {
  agent {label 'docker'}
  parameters {
    gitParameter(type: 'PT_BRANCH', name: 'GIT_BRANCH', branchFilter: 'origin/(.*)', defaultValue: 'main', selectedValue: 'DEFAULT', sortMode: 'DESCENDING_SMART')
  }

  environment {
    DOCKER_REPO="ivankhodyrev/bookstore"
    DOCKER_TOKEN=credentials('docker_token')
    TG_BOT_TOKEN=credentials('bot_token')
    TG_CHAT_ID=credentials('chat_id')
  }

  stages {

    stage('Cheackout') {
      steps {
        git branch: "${params.GIT_BRANCH}", url: "${GIT_URL}", credentialsId: 'github_ssh'
      }
    }

    stage('Login to dockerhub') {
      steps {
        script {
          dockerBuild.login()
        }
      }
    }

    stage('Build docker image') {
      steps {
        script {
          def FULL_TAG = "${env.DOCKER_REPO}:bookstore-${BUILD_ID}"
          dockerBuild.build(${FULL_TAG})
        }
      }
    }

    stage('Push image to dockerhub') {
      steps {
        script {
          dockerBuild.push(${FULL_TAG})
        }
      }
    }
  }

  post {
    success {
      script {
        notifyTelegram("✅ Success build", env.TG_BOT_TOKEN, env.TG_CHAT_ID)
      }
    }

    failure {
      script {
        notifyTelegram("❌ Failed build!!!", env.TG_BOT_TOKEN, env.TG_CHAT_ID)
      }
    }
  }
}
