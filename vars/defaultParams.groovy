// vars/commonParams.groovy
def call(Map config = [:]) {
    return [
        string(name: 'ENVIRONMENT', defaultValue: 'dev', description: 'Deployment environment (dev, staging, prod)'),
        choice(name: 'BRANCH', choices: ['main', 'develop', 'feature/*'], description: 'Git branch to build'),
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: 'Run unit tests?'),
        string(name: 'APP_VERSION', defaultValue: '1.0.0', description: 'Application version')
    ]
}