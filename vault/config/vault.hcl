ui            = true
cluster_addr  = "https://127.0.0.1:8201"
api_addr      = "https://127.0.0.1:8200"
disable_mlock = true

storage "raft" {
  path = "/vault/raft"
  node_id = "raft_node_id"
}

listener "tcp" {
  address       = "127.0.0.1:8200"
  tls_disable = 1  # ใช้ TLS จริงใน production
  #tls_cert_file = "/path/to/full-chain.pem"
  #tls_key_file  = "/path/to/private-key.pem"
}

