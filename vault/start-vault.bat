@echo off
REM Start Hashi vault container

docker run -d --name vault-container --cap-add=IPC_LOCK --env-file .env -p 8200:8200 hashicorp/vault server
