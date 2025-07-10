def call(List parameterDefinitions): [booleanParam, choice, credentials, file, text, password, run, string] {
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