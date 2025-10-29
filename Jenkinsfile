pipeline {
  agent any
  parameters {
    booleanParam(name: 'RUN_TESTS', defaultValue: 'true')
    string(name: 'TAG', defaultValue: 'latest')
    gitParameter(type: 'PT-BRANCH', name: 'REVISION', branchFilter: 'origin/(.*)', defaultValue: 'main', selectedValue: 'DEFAULT')
  }

  environment {
    GIT_NAME="ivankhodyrev"
    PRJ_NAME="bookstore"
    GIT_URL="https://github.com/ioannsnakej/bookstore.git"
    TOKEN=credentials('docker_token')
    TG_BOT_TOKEN=credentials('bot_token')
    TG_CHAT_ID=credentials('chat_id')
  }

  stages {

    stage('Clone repo') {
      steps {
        script {
          sh """
            if [ -d ${env.PRJ_NAME}/.git ]; then
              echo "Repo exists. Updating..."
              cd ${env.PRJ_NAME}
              git checkout ${params.REVISION}
              git pull
            else
              echo "Clone repo"
              git clone ${env.GIT_URL}
            fi
          """
        }
      }
    }

    stage('Build image') {
      steps {
        script {
           sh """
              curl -s -X POST https://api.telegram.org/bot${env.TG_BOT_TOKEN}/sendMessage \
              -d chat_id=${env.TG_CHAT_ID} \
              -d parse_mode=Markdown \
              -d text="🏃Начата сборка проекта ${env.PRJ_NAME}"
            """
        }

        script {
          sh """
            cd ${env.PRJ_NAME}
            docker build -t ${env.GIT_NAME}/${env.PRJ_NAME}:${params.TAG} .
            docker login -u ${env.GIT_NAME} -p ${env.TOKEN}
            docker push ${env.GIT_NAME}/${env.PRJ_NAME}:${params.TAG}
            docker logout
          """
        }
      }
    }

    stage('Run test') {
      when {
        expression { params.RUN_TESTS }
      }
      steps {
        script {
          sh """
            cd ${env.PRJ_NAME}
            echo "App ready"
          """
        }
      }
    }
  }

  post {
    success {
      script {
        sh """
          curl -s -X POST https://api.telegram.org/bot${env.TG_BOT_TOKEN}/sendMessage \
          -d chat_id=${env.TG_CHAT_ID} \
          -d parse_mode=Markdown \
          -d text="✅Success! Проект:${env.PRJ_NAME}"
        """
      }
    }

    failure {
      script {
        sh """
              curl -s -X POST https://api.telegram.org/bot${env.TG_BOT_TOKEN}/sendMessage \
              -d chat_id=${env.TG_CHAT_ID} \
              -d parse_mode=Markdown \
              -d text="❌Failed! Проект:${env.PRJ_NAME}"
            """
      }
    }
  }
}
