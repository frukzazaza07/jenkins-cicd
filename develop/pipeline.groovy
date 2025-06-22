pipeline {
    agent any
    environment {
        DEST_IP = '31.97.67.40'
        SSH_CREDENTIAL = credentials('ssh_31.97.67.40')
    }
    options {
        // Timeout counter starts AFTER agent is allocated
        timeout(time: 1, unit: 'SECONDS')
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
                withCredentials(bindings: [sshUserPrivateKey(credentialsId: 'ssh_31.97.67.40', \
                                             keyFileVariable: 'SSH_KEY', \
                                             passphraseVariable: 'SSH_PASS', \
                                             usernameVariable: 'SSH_USER')]) {
                    sh """ echo "Connecting to ${env.DEST_IP} as ${SSH_USER} ${SSH_KEY}"  """
                    sh """
                        ssh -i ${$SSH_KEY} -o StrictHostKeyChecking=no ${SSH_USER}@${env.DEST_IP}"
                    """
                }
            }
        }
    }
}