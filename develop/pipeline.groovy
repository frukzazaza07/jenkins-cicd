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
        stage('Check ENV') {
            steps {
                sh 'env'
            }
        }
        stage('Test SSH Connection') {
            steps {
                script {
                        echo "testtttttttttttttt"
                        ssh{
                            command "hostname",  host: "${env.DEST_IP}", credentialsId: 'SSH_INSUREOK_SERVER'
                            command "whoami", host: "${env.DEST_IP}", credentialsId: 'SSH_INSUREOK_SERVER'
                        }
                }

                // withCredentials([sshUserPrivateKey(credentialsId: 'SSH_INSUREOK_SERVER', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                //     sh """ echo "Connecting to ${env.DEST_IP} as ${SSH_KEY}"  """
                //     sh 'whoami'
                //     sh """ chmod 600 $SSH_KEY  """
                //     sh """
                //         ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no ${SSH_USER}@${env.DEST_IP}
                //     """
                // }
            }
        }
    }
}
