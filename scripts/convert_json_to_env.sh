#!/bin/bash
set -euo pipefail # ออกเมื่อเจอ error, ใช้ตัวแปรที่ไม่ได้ประกาศ, และ pipe failures

# ตัวอย่าง JSON input
# สมมติว่านี่คือ JSON ที่คุณได้รับจาก API หรือไฟล์
json_data="$1"

echo "--- JSON Input ---"
echo "$json_data"
echo "------------------"

echo ""
echo "--- Looping through JSON key-value pairs ---"

# วิธีที่ 1: วนลูปผ่าน key-value pairs โดยตรง
# .[] | @json จะแปลงแต่ละ element เป็น JSON string
# jq -r 'to_entries[] | "\(.key)=\(.value)"' จะแปลงเป็นรูปแบบ KEY=VALUE
echo "$json_data" | jq -r 'to_entries[] | "Key: \(.key), Value: \(.value)"'

echo ""
echo "--- Exporting to .env format ---"

# แปลงเป็นรูปแบบ KEY=VALUE และส่งออกไปยังไฟล์ .env
# เหมาะสำหรับข้อมูลที่ต้องการนำไปใช้เป็น environment variables
echo "$json_data" | jq -r 'to_entries[] | "\(.key)=\(.value)"' > .env

echo "✅ ไฟล์ .env ถูกสร้างขึ้นแล้ว:"
cat .env
