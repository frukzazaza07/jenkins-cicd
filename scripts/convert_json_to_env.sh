#!/bin/bash
set -e

# ตรวจสอบว่า jq ถูกติดตั้งหรือไม่
if ! command -v jq &> /dev/null
then
    echo "❌ jq ไม่พบในระบบ กรุณาติดตั้ง jq เพื่อประมวลผล JSON" >&2
    echo "   สำหรับ Debian/Ubuntu: sudo apt-get install jq" >&2
    echo "   สำหรับ CentOS/RHEL: sudo yum install jq" >&2
    echo "   สำหรับ macOS (Homebrew): brew install jq" >&2
    exit 1
fi

# ตัวอย่าง JSON input
# สมมติว่านี่คือ JSON ที่คุณได้รับจาก API หรือไฟล์
json_data="$1"

echo "--- Exporting to .env format ---"

# วิธีที่ 1: วนลูปผ่าน key-value pairs โดยตรง
# .[] | @json จะแปลงแต่ละ element เป็น JSON string
# jq -r 'to_entries[] | "\(.key)=\(.value)"' จะแปลงเป็นรูปแบบ KEY=VALUE
# แปลงเป็นรูปแบบ KEY=VALUE และส่งออกไปยังไฟล์ .env
# เหมาะสำหรับข้อมูลที่ต้องการนำไปใช้เป็น environment variables
# echo "$json_data" | jq -r 'to_entries[] | "\(.key)=\(.value)"' > .env

# cat .env

