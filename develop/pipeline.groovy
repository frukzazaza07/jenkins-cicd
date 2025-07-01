pipeline {
    agent any
    environment {
        DEST_IP = '31.97.67.40'
        PING_SCRIPT_PATH = 'scripts/ping_connection.sh'
        ENV_SCRIPT_PATH = 'scripts/get_env.sh'
        REPO_NAME = 'insureok'
        GIT_APP_URL = 'https://bitbucket.org/yern/insure-ok.git'
        GIT_APP_BRANCH = 'develop'
        SSH_CREDENTIAL = credentials('SSH_INSUREOK_SERVER')
        ENV_SECRET_URL='http://host.docker.internal:8200/v1/cubbyhole'
        ENV_SECRET_PATH='insureok-dev'
    }
    stages {
        stage('Inti Scripts permission') {
            steps {
                script{
                    echo "🚀 Starting add chmod script"
                    sh(script: "chmod +x ${env.PING_SCRIPT_PATH}", returnStatus: true)
                    sh(script: "chmod +x ${env.ENV_SCRIPT_PATH}", returnStatus: true)
                }
            }
        }
        stage('Test Ping Connection') {
            steps {
                script{
                    sh 'pwd'
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
                        dir('application') { // ไม่งั้น git จะ clean folder
                            git(
                                url: env.GIT_APP_URL,
                                branch: env.GIT_APP_BRANCH,
                                credentialsId: 'insureok-bitbucket'
                            )
                        }
                        echo "✅ Pull code from ${env.GIT_APP_URL} success"
                    } catch (Exception e) {
                        echo "❌ Pull code failed: ${e.getMessage()}"
                        error("❌ Pull code failed: ${e.getMessage()}")
                    }
                }
            }
        }
        stage('Before Build get ENV') {
            steps {
                script {
                    try{
                        echo "Starting get ENV from: ${env.REPO_NAME}"
                        
                        withCredentials([string(credentialsId: 'ENV_SECRET_AUTH', variable: 'SECRET_TOKEN')]) {
                            def resultGetEnv = sh(
                                script: """
                                    ${env.WORKSPACE}/${env.ENV_SCRIPT_PATH} --url ${env.ENV_SECRET_URL} --path ${env.ENV_SECRET_PATH} --token "\$SECRET_TOKEN"
                                """,
                                returnStatus: true
                            )
                            if(resultGetEnv == 0){
                                echo "✅ Before Build script success."
                            } else {
                                echo "❌ Before Build script failed."
                                error("❌ Before Build script failed.")
                            }

                        }

                        echo "✅ Before Build get ENV from: ${env.REPO_NAME} success"
                    } catch (Exception e) {
                        error "❌ ${e.getMessage()}"
                    }
                }
            }
        }
        stage('Build and Push app from docker image to registry') {
            steps {
                script {
                    try{
                        echo "Starting push app to: ${env.REPO_NAME}"
                        dir('application') { 
                            docker.withRegistry('https://31.97.67.40:5000', 'DOCKER-LOGIN-REGISTRY') {
                                docker.image(env.REPO_NAME).push('latest')
                                docker.build("${env.REPO_NAME}:latest").push()
                            }
                        }
                        echo "✅ Push app to: ${env.REPO_NAME} success"
                    } catch (Exception e) {
                        echo "❌ Push app to registry failed"
                        error "❌ Push app to registry failed ${e.getMessage()}"
                    }
                }
            }
        }
    }
}



