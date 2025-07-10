@Library('default-parameters-shared-library') _

pipeline {
    agent any

    stages {
        stage('Init parameters first time') {
            steps {
                script{
                    
                    echo "Init parameters"
                    properties([
                        parameters(initDefaultParams.getDefaultParams())
                    ])
                    echo "Init parameters success"
                    currentBuild.result = 'SUCCESS'
                }
            }
        }
    }
}