def jsonToEnv(Map envData) {
    return envData.collect { k, v -> "${k}=${v}" }.join('\n')
}

// You can also return a map of functions/methods
return this