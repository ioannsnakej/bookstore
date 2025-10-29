pipeline {
  agent any
  parameters {
    booleanParam(name: 'RUN_TEST', defaultValue: 'true')
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
          def notify = load 'notify.groovy'
          notify("Start build", TG_BOT_TOKEN, TG_CHAT_ID)
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
        def notify = load 'notify.groovy'
        notify("Success", TG_BOT_TOKEN, TG_CHAT_ID)
      }
    }

    failure {
      script {
        def notify = load 'notify.groovy'
        notify("Failed", TG_BOT_TOKEN, TG_CHAT_ID)
      }
    }
  }
}
