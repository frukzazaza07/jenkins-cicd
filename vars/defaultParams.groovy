def call() {
    println "[DEBUG] pipelineParams.groovy is loaded"
    
    return {
        println "[DEBUG] pipelineParams closure is invoked"
        string(name: 'Branch', defaultValue: 'develop', description: 'Enter your branch (required)')
        choice(name: 'ENV', choices: ['develop', 'staging'], description: 'Choose environment')
        string(name: 'AppVersion', defaultValue: 'develop', description: 'Enter your app version (required)')
    }
}
