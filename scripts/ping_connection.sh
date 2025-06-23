#!/bin/bash
set -e

IP_DESTINATION="$1"

if [ -z "$IP_DESTINATION" ]; then
    echo "❌ No IP address provided"
    exit 1
fi

echo "📡 Pinging IP_DESTINATION: $IP_DESTINATION ..."

# 👇 เพิ่ม timeout 5 วินาที หากระบบช้า
ping -c 4 "$IP_DESTINATION" > ping.log 2>&1
if [ $? -eq 0 ]; then
    echo "✅ Ping to $IP_DESTINATION success"
    exit 0
else
    echo "❌ Ping to $IP_DESTINATION failed. Check ping.log for details"
    cat ping.log
    exit 1
fi
