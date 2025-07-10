def call() {
    return [
        [
            $class: 'StringParameterDefinition',
            name: 'Branch',
            defaultValue: 'develop',
            description: 'Enter your branch (required)'
        ],
        [
            $class: 'ChoiceParameterDefinition',
            name: 'ENV',
            choices: ['develop', 'staging'],
            description: 'Choose environment'
        ],
        [
            $class: 'StringParameterDefinition',
            name: 'AppVersion',
            defaultValue: '',
            description: 'Enter your app version (required)'
        ]
    ]
}
