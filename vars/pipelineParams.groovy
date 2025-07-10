// def call() {
//     // generate params build with params
//     // config on jenkins website manage jenkins -> config system -> find Global Trusted Pipeline Libraries
//     return {
//         string(name: 'Branch', defaultValue: 'develop', description: 'Enter your branch (required)'),
//         choice(name: 'ENV', choices: ['develop', 'staging'], description: 'Choose environment'),
//         string(name: 'App Version', defaultValue: '', description: 'Enter your app version (required)')
//     }
// }

def call(List parameterDefinitions) {
    parameterDefinitions.each { paramDef ->
        switch (paramDef.$class) {
            case 'StringParameterDefinition':
                string(paramDef)
                break
            case 'ChoiceParameterDefinition':
                choice(paramDef)
                break
            case 'BooleanParameterDefinition':
                booleanParam(paramDef)
                break
            case 'TextParameterDefinition':
                text(paramDef)
                break
            case 'PasswordParameterDefinition':
                password(paramDef)
                break
            case 'FileParameterDefinition':
                file(paramDef)
                break
            case 'CredentialsParameterDefinition':
                credentials(paramDef)
                break
            default:
                error "Unsupported parameter type in Shared Library: ${paramDef.$class}"
        }
    }
}