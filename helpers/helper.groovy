def jsonToEnv(Map envData) {
    return envData.collect { k, v -> "${k}=${v}" }.join('\n')
}
