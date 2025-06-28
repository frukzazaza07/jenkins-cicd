pipeline {
    agent any
    environment {
        DEST_IP = '31.97.67.40'
        GIT_APP_URL = 'https://wanutpongbb@bitbucket.org/yern/insure-ok.git'
        SSH_CREDENTIAL = credentials('SSH_INSUREOK_SERVER')
    }
    stages {
        stage('Example') {
            steps {
                echo 'Hello World'
            }
        }
        stage('Test Ping Connection') {
            steps {
                sh 'chmod +x scripts/ping_connection.sh'
                sh "./scripts/ping_connection.sh ${env.DEST_IP}"
            }
        }
        stage('Test SSH Connection') {
            steps {
                script {
                        echo "SSH Connecting to ${env.DEST_IP}"
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
                            error("Stop pipeline because SSH failed.")
                        }
                }
            }
        }
        stage('Pull code app') {
            steps {
                script {
                    echo "Started Git pull code from ${env.GIT_APP_URL}"
                    git(
                        url: env.GIT_APP_URL,
                        branch: 'develop',
                        credentialsId: 'insureok-bitbucket'
                    )
                }
            }
        }
    }
}
