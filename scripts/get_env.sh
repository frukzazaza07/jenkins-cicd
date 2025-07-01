#!/bin/bash
set -e # ถ้าเกิด error ที่คำสั่งได้คำสั่งหนึ่งจะ return exit code 1

# ตรวจสอบว่า jq ถูกติดตั้งหรือไม่
if ! command -v jq &> /dev/null
then
    echo "❌ jq ไม่พบในระบบ กรุณาติดตั้ง jq เพื่อประมวลผล JSON" >&2
    echo "   สำหรับ Debian/Ubuntu: sudo apt-get install jq" >&2
    echo "   สำหรับ CentOS/RHEL: sudo yum install jq" >&2
    echo "   สำหรับ macOS (Homebrew): brew install jq" >&2
    exit 1
fi

# กำหนดค่าเริ่มต้นของตัวแปร
envSecretUrl="http://host.docker.internal:8200/v1/cubbyhole"
envSecretPath=""
envSecretAuth="" # ตัวแปรนี้จะเก็บค่า SECRET_TOKEN
envSecretMethod="GET" # กำหนด method เริ่มต้นเป็น GET เพื่อความยืดหยุ่น

# Parse options: อ่านและกำหนดค่าให้กับตัวแปรจาก command-line arguments
while [[ $# -gt 0 ]]; do
  key="$1" # เก็บชื่อ option (เช่น --url, --token)

  case $key in
    --url)
      envSecretUrl="$2" # กำหนดค่า envSecretUrl จาก argument ถัดไป
      shift 2 # เลื่อนตำแหน่งไป 2 (ข้าม option และ value)
      ;;
    --token)
      envSecretAuth="$2" # กำหนดค่า envSecretAuth จาก argument ถัดไป
      shift 2
      ;;
    --path)
      envSecretPath="$2" # กำหนดค่า envSecretPath จาก argument ถัดไป
      shift 2
      ;;
    --method) # เพิ่ม option สำหรับกำหนด HTTP method
      envSecretMethod="$2"
      shift 2
      ;;
    *) # กรณีที่ไม่รู้จัก option
      echo "❌ Unknown option: $1"
      exit 1 # ออกจากสคริปต์ด้วยสถานะผิดพลาด
      ;;
  esac
done

# ✅ Validate required parameters: ตรวจสอบว่า parameter ที่จำเป็นถูกส่งมาครบถ้วน
if [[ -z "$envSecretPath" ]]; then
  echo "❌ --path is required"
  exit 1
fi

if [[ -z "$envSecretAuth" ]]; then
  echo "❌ --token is required"
  exit 1
fi

# ทำการเรียกใช้ curl เพื่อดึงข้อมูลจาก Vault
# -s: Silent mode (ไม่แสดง progress meter หรือ error message)
# -w "%{http_code}": พิมพ์ HTTP status code ไปยัง stdout หลังจากการโอนย้ายข้อมูล
# -H "X-Vault-Token: ...": กำหนด HTTP header สำหรับ Vault Token
# -X $envSecretMethod: กำหนด HTTP method (เช่น GET, POST)
# -o tmp_response.json: เขียน response body ลงในไฟล์ tmp_response.json
httpResponseCode=$(curl -s -w "%{http_code}" -H "X-Vault-Token: $envSecretAuth" -X "$envSecretMethod" "$envSecretUrl/$envSecretPath" -o tmp_response.json )

# อ่าน response body จากไฟล์ชั่วคราว
responseBody=$(cat tmp_response.json)
# ตรวจสอบ HTTP response code
if [[ "$httpResponseCode" != "200" ]]; then
  echo "$responseBody"
  echo "❌ Failed with status code: $httpResponseCode"
  exit 1
fi

if ! echo "$responseBody" | jq -e . > /dev/null 2>&1; then
    echo "❌ JSON data ที่ได้รับมาไม่ถูกต้องหรือมีรูปแบบผิดพลาด" >&2
    exit 1
fi

echo "$responseBody" | jq .data
# echo "$responseBody" | jq '.data -r | to_entries[] | "\(.key)=\(.value)"' > .env
echo "$responseBody" | jq -r '.data | to_entries | .[] | .key + "=" + (.value | @sh)' > .env


cat .env

# ลบไฟล์ชั่วคราวเพื่อทำความสะอาด
rm tmp_response.json
