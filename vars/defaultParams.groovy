def call() {
    println "[DEBUG] pipelineParams.groovy is loaded"
    echo "zzzzzzzzzzzzzzzzz"
    return {
         script {
            echo "[DEBUG] pipelineParams closure invoked ✅"
        }
        string(name: 'Branch', defaultValue: 'develop', description: 'Enter your branch (required)')
        choice(name: 'ENV', choices: ['develop', 'staging'], description: 'Choose environment')
        string(name: 'AppVersion', defaultValue: 'develop', description: 'Enter your app version (required)')
    }
}
