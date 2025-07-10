def getDefaultParams() {
    return [
        choice(name: 'Repository', choices: ['fe-fo', 'fe-bo', 'b-api', 's-api', 'new-homepage'], description: 'Choose environment'),
        string(name: 'Branch', defaultValue: 'develop', description: 'Enter your branch (required)'),
        choice(name: 'ENV', choices: ['develop', 'staging'], description: 'Choose environment'),
        string(name: 'AppVersion', defaultValue: '', description: 'Enter your app version (required)'),
    ]
}