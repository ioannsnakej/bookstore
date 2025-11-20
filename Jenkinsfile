@Library('jenkins-shared-lib') _
def GIT_URL = "git@github.com:ioannsnakej/bookstore.git"

pipeline {
  agent {label 'docker'}
  parameters {
    gitParameter(type: 'PT_BRANCH', name: 'GIT_BRANCH', branchFilter: 'origin/(.*)', defaultValue: 'main', selectedValue: 'DEFAULT', sortMode: 'DESCENDING_SMART')
    booleanParam(name: 'RUN_DEPLOY', defaultValue: 'true')
  }

  environment {
    DOCKER_REPO="ivankhodyrev/bookstore"
    TG_BOT_TOKEN=credentials('bot_token')
    TG_CHAT_ID=credentials('chat_id')
    DB_USER = credentials('db_user')
    DB_NAME = credentials('db_name')
    DB_HOST = credentials('db_host')
    DB_PASS = credentials('db_pass')
  }

  stages {

    stage('Checkout') {
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
          dockerBuild.build(env.DOCKER_REPO)
        }
      }
    }

    stage('Test image') {
      steps {
        script {
          writeFile file: "./.env", text: """
            DB_USER=${DB_USER}
            DB_NAME=${DB_NAME}
            DB_HOST=${DB_HOST}
            DB_PASS=${DB_PASS}
          """

          sh """
            docker compose up -d 
            sleep 30
            curl -f localhost:8080/books
          """
        }
      }
      post {
        always {
          sh """
            docker compose down -v
          """
        }
      }
    }

    stage('Push image to dockerhub') {
      steps {
        script {
          dockerBuild.push(env.DOCKER_REPO)
        }
      }
    }

    stage('Call Deploy') {
      when {
        expression { return params.RUN_DEPLOY }
      }
      steps {
        script {
          build quietPeriod: 5, wait: false, job: 'deploy-app', parameters: [string(name: 'DOCKER_IMAGE', value: "${env.DOCKER_REPO}")]
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
