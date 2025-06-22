#!/bin/bash
set -e

IP_DESTINATION="$1"

if [ -z "$IP_DESTINATION" ]; then
    echo "❌ No IP address provided"
    exit 1
fi

echo "📡 Pinging IP_DESTINATION: $IP_DESTINATION ..."

if ping -c 2 -W 1 "$IP_DESTINATION" >/dev/null 2>&1; then
    echo "✅ Ping to $IP_DESTINATION success"
    exit 0
else
    echo "❌ Ping to $IP_DESTINATION failed"
    exit 1
fi
