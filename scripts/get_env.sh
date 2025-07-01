
#!/bin/bash
set -x # ถ้าเกิด error ที่คำสั่งได้คำสั่งหนึ่งจะ return exit code 1

envSecretUrl="http://host.docker.internal:8200/v1/cubbyhole"
envSecretPath=""
envSecretAuth=""

# Parse options
while [[ $# -gt 0 ]]; do
  key="$1" 

  case $key in
    --url)
      envSecretUrl="$2"
      shift 2
      ;;
    --token)
      envSecretAuth="$2"
      shift 2
      ;;
    --path)
      envSecretPath="$2"
      shift 2
      ;;
    *)
      echo "❌ Unknown option: $1"
      exit 1
      ;;
  esac
done

# ✅ Validate required parameters
if [[ -z "$envSecretPath" ]]; then
  echo "❌ --path is required"
  exit 1
fi

if [[ -z "$envSecretAuth" ]]; then
  echo "❌ --token is required"
  exit 1
fi
echo $envSecretAuth
echo $envSecretUrl/$envSecretPath
httpResponseCode=$(curl -s -w "%{http_code}" -H "X-Vault-Token: $envSecretAuth" "$envSecretUrl/$envSecretPath" -o tmp_response.json )
responseBody=$(cat tmp_response.json)

if [[ "$httpResponseCode" == "200" ]]; then
  echo "✅ Success!"
  echo "$responseBody"
else
  echo "❌ Failed with status code: $httpResponseCode"
  echo "$responseBody"
fi