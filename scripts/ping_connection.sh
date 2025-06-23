#!/bin/bash
set -e

IP_DESTINATION="$1"

if [ -z "$IP_DESTINATION" ]; then
    echo "❌ No IP address provided"
    exit 1
fi

echo "📡 Pinging IP_DESTINATION: $IP_DESTINATION ..."

# 👇 เพิ่ม timeout 5 วินาที หากระบบช้า
if ping -c 4 "$IP_DESTINATION" >/dev/null 2>&1; then
    echo "✅ Ping to $IP_DESTINATION success"
    exit 0
else
    echo "❌ Ping to $IP_DESTINATION failed"
    # ยัง exit 1 เพราะต้องการให้ Jenkins รู้ว่า fail
    exit 1
fi
