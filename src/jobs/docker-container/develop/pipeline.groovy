@Library('default-parameters-shared-library') _

properties([
    parameters(paramsList)
])

pipeline {
    agent any

    parameters pipelineParams()

    environment {
        PING_SCRIPT_PATH = 'resources/scripts/ping_connection.sh'
        ENV_SCRIPT_PATH = 'resources/scripts/get_env.sh'
        CONVERT_JSON_TO_ENV_SCRIPT_PATH = 'resources/scripts/convert_json_to_env.sh'
        SSH_CREDENTIAL = credentials('SSH_INSUREOK_SERVER')
    }

    stages {
        stage('Inti Scripts permission') {
            steps {
                script{
                    echo "🚀 Starting add chmod script"
                    sh(script: "chmod +x ${env.PING_SCRIPT_PATH}", returnStatus: true)
                    sh(script: "chmod +x ${env.ENV_SCRIPT_PATH}", returnStatus: true)
                    sh(script: "chmod +x ${env.CONVERT_JSON_TO_ENV_SCRIPT_PATH}", returnStatus: true)
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
                                script: "ssh -o StrictHostKeyChecking=no -p ${env.SSH_PORT} ${env.SSH_CREDENTIAL_USR}@${env.DEST_IP} \"hostname -I\"",
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
                        dir('application'){
                            withCredentials([string(credentialsId: 'ENV_SECRET_AUTH', variable: 'SECRET_TOKEN')]) {
                                def resultGetEnv = sh(
                                    script: """
                                        ${env.WORKSPACE}/${env.ENV_SCRIPT_PATH} --url ${env.ENV_SECRET_URL} --path ${env.ENV_SECRET_PATH} --token "\$SECRET_TOKEN"
                                    """,
                                    returnStdout: true
                                )
                                    echo "✅ Get ENV success."
                                    echo "$resultGetEnv"
                            }
                            
                            // def jsonSlurper = new groovy.json.JsonSlurper()
                            // def envData = jsonSlurper.parseText(resultGetEnv)
                            // def jsonStringForEnv = JsonOutput.toJson(envData.data)
                            // def resultCreateEnv = sh(script: """${env.WORKSPACE}/${env.CONVERT_JSON_TO_ENV_SCRIPT_PATH} '$jsonStringForEnv' """, returnStatus: true)
                            // if(resultCreateEnv == 0){
                            //     echo "GGG"
                            // }
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
                            sh(script: "ls -la")
                            docker.withRegistry('https://31.97.67.40:5000', 'DOCKER-LOGIN-REGISTRY') {
                                // docker.image(env.REPO_NAME).push('latest')
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



