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
                    def target = "31.97.67.40"
                    def status = sh(script: "ping -c 1 -W 2 ${target} > /dev/null 2>&1", returnStatus: true)

                    if (status == 0) {
                        echo "✅ Ping to ${target} success"
                    } else {
                        echo "❌ Ping to ${target} failed"
                        error("Ping failed")
                    }
                }
            }
        }
    }
}