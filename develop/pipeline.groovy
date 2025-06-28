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
                        sshagent(['SSH_INSUREOK_SERVER']) {
                            sh """ssh -o StrictHostKeyChecking=no ${env.SSH_CREDENTIAL_USR}@${env.DEST_IP} "hostname -I" """
                        }
                }
            }
        }
    }
}
