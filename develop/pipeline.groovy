pipeline {
    agent any
    environment {
        DEST_IP = '31.97.67.40'
        PING_SCRIPT_PATH = './scripts/ping_connection.sh'
        REPO_NAME = 'insureok'
        GIT_APP_URL = 'https://bitbucket.org/yern/insure-ok.git'
        GIT_APP_BRANCH = 'develop'
        SSH_CREDENTIAL = credentials('SSH_INSUREOK_SERVER')
    }
    stages {
        stage('Test Ping Connection') {
            steps {
                script{
                    echo "🚀 Starting add chmod script ${env.PING_SCRIPT_PATH}"
                    def resultChmod = sh(script: "chmod +x ${env.PING_SCRIPT_PATH}", returnStatus: true)
                    if(resultChmod == 0){
                        echo "✅ chmod script success ${env.PING_SCRIPT_PATH}"
                    } else {
                        echo "❌ chmod script failed ${env.PING_SCRIPT_PATH}"
                        error("❌ chmod script failed ${env.PING_SCRIPT_PATH}")
                    }

                    echo "🚀 Starting ping ${env.PING_SCRIPT_PATH} ${env.DEST_IP}"
                    def resultPing = sh(script: "${env.PING_SCRIPT_PATH} ${env.DEST_IP}", returnStatus: true)
                    if(resultPing == 0){
                        echo "✅ Ping script success ${env.PING_SCRIPT_PATH} ${env.DEST_IP}"
                    } else {
                        echo "❌ Ping script failed ${env.PING_SCRIPT_PATH} ${env.DEST_IP}"
                        error("❌ Ping script failed ${env.PING_SCRIPT_PATH} ${env.DEST_IP}")
                    }
                }
            }
        }
        stage('Test SSH Connection') {
            steps {
                script {
                        echo "🚀 Starting SSH Connecting to ${env.DEST_IP}"
                        def status = sshagent(['SSH_INSUREOK_SERVER']) {
                            sh(
                                script: "ssh -o StrictHostKeyChecking=no ${env.SSH_CREDENTIAL_USR}@${env.DEST_IP} \"hostname -I\"",
                                returnStatus: true
                            )
                        }

                        if (status == 0) {
                            echo "✅ SSH test connection success IP: ${env.DEST_IP} User: ${env.SSH_CREDENTIAL_USR}"
                        } else {
                            echo "❌ SSH test connection failed IP: ${env.DEST_IP} User: ${env.SSH_CREDENTIAL_USR} with exit code ${status}"
                            error("❌ SSH test connection failed IP: ${env.DEST_IP} User: ${env.SSH_CREDENTIAL_USR} with exit code ${status}")
                        }
                }
            }
        }
        stage('Pull code app') {
            steps {
                script {
                    try{
                        echo "🚀 Starting Git pull code from ${env.GIT_APP_URL}"
                        git(
                            url: env.GIT_APP_URL,
                            branch: env.GIT_APP_BRANCH,
                            credentialsId: 'insureok-bitbucket'
                        )
                        echo "✅ Pull code from ${env.GIT_APP_URL} success"
                    } catch (Exception e) {
                        echo "❌ Pull code failed: ${e.getMessage()}"
                        error("❌ Pull code failed: ${e.getMessage()}")
                    }
                }
            }
        }
        stage('Build code app') {
            steps {
                script {
                    try{
                        echo "Starting build code from: ${env.REPO_NAME}"
                        docker.build(env.REPO_NAME)
                        echo "✅ Build code from: ${env.REPO_NAME} success"
                    } catch (Exception e) {
                        echo "❌ Build code from: ${env.REPO_NAME} failed ${e.getMessage()}"
                    }
                }
            }
        }
    }
}
