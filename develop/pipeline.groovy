pipeline {
                        agent any
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
        stage('Test SSH Connection') {
            steps {
                script {
                    // ทดสอบ ssh ไปยัง host (ไม่ต้องสั่งคำสั่งอะไร แค่เชื่อมแล้วออก)
                    def remoteHost = "ssh root@31.97.67.40"

                    sh """
                        echo "Testing SSH to ${remoteHost}..."
                        ping 31.97.67.40  "echo SSH connection successful"
                    """
                    echo 'Connect Success'
                }
            }
        }
    }
}