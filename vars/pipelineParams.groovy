def call() {
    // generate params build with params
    // config on jenkins website manage jenkins -> config system -> find Global Trusted Pipeline Libraries
    string(name: 'Branch', defaultValue: 'develop', description: 'Enter your branch (required)')
    choice(name: 'ENV', choices: ['develop', 'staging'], description: 'Choose environment')
    string(name: 'App Version', defaultValue: '', description: 'Enter your app version (required)')
}
