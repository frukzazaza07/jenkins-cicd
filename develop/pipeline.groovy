pipeline {
    agent any
    environment {
        DEST_IP = '31.97.67.40'
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
                            echo "✅ SSH test connection success"
                        } else {
                            echo "❌ SSH test connection failed with exit code ${status}"
                            error("Stop pipeline because SSH failed.")
                        }
                }
            }
        }
        // stage('Pull code app') {
        //     steps {
        //         scripts{
        //             git(
        //                 url: 'https://wanutpongbb@bitbucket.org/yern/insure-ok.git',
        //                 branch: 'master',
        //                 credentialsId: 'insureok-bitbucket'
        //             )
        //         }
        //     }
        // }
    }
}
