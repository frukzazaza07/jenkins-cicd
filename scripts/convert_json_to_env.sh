#!/bin/bash
set -euo pipefail # ออกเมื่อเจอ error, ใช้ตัวแปรที่ไม่ได้ประกาศ, และ pipe failures

# ตรวจสอบว่า jq ถูกติดตั้งหรือไม่
if ! command -v jq &> /dev/null
then
    echo "❌ jq ไม่พบในระบบ โปรดตรวจสอบว่า jq ได้รับการติดตั้งแล้วในสภาพแวดล้อมนี้" >&2
    exit 1
fi

# รับ JSON string จาก argument แรก
# *** สำคัญ: JSON string จะอยู่ใน $1 ทั้งหมด ***
json_data="$1"

# ตรวจสอบว่าได้รับ JSON data มาหรือไม่
if [[ -z "$json_data" ]]; then
    echo "❌ ไม่ได้รับ JSON data เป็น argument" >&2
    exit 1
fi

echo "--- JSON Input Received ---" >&2
# ใช้ jq เพื่อ pretty-print JSON ที่ได้รับเพื่อ debug
# การใช้ 'jq .' จะตรวจสอบความถูกต้องของ JSON ไปในตัวด้วย
echo "$json_data" | jq '.' >&2
echo "---------------------------" >&2

echo "" >&2
echo "--- Exporting to .env format ---" >&2

# ตรวจสอบว่า JSON ที่ได้รับมานั้นถูกต้องหรือไม่ ก่อนที่จะพยายาม parse ด้วย jq
# jq -e . จะคืนค่า 0 ถ้า JSON ถูกต้อง และ 1 ถ้าไม่ถูกต้อง
if ! echo "$json_data" | jq -e . > /dev/null 2>&1; then
    echo "❌ JSON data ที่ได้รับมาไม่ถูกต้องหรือมีรูปแบบผิดพลาด" >&2
    exit 1
fi

# ตรวจสอบว่ามี .data field และเป็น object หรือไม่
# ถ้าไม่มีหรือไม่ใช่ object, jq '.data | type' จะคืนค่าอื่นที่ไม่ใช่ "object"
# และ jq -e จะทำให้คำสั่งนี้ล้มเหลวถ้า .data ไม่มีอยู่
if ! echo "$json_data" | jq -e '.data | type == "object"' > /dev/null 2>&1; then
    echo "⚠️ JSON data ไม่มี 'data' field ที่เป็น object หรือโครงสร้างไม่ตรงกับที่คาดหวัง (Vault secret)." >&2
    echo "   จะพยายามแปลง JSON ทั้งหมดโดยไม่ใช้ '.data' filter." >&2
    # แปลง JSON ทั้งหมดเป็น .env format โดยตรง
    echo "$json_data" | jq -r 'to_entries[] | "\(.key)=\(.value)"' > .env
else
    # แปลงเฉพาะ .data field เป็น .env format
    echo "$json_data" | jq -r '.data | to_entries[] | "\(.key)=\(.value)"' > .env
fi


echo "✅ ไฟล์ .env ถูกสร้างขึ้นแล้ว:" >&2
cat .env # แสดงเนื้อหาของไฟล์ .env ไปยัง stdout เพื่อให้ Jenkins จับได้หากต้องการ

echo "------------------------------------------------" >&2
echo "สคริปต์ทำงานเสร็จสิ้น" >&2
