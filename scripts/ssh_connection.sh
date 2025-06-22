#!/bin/bash
set -e

SSH_USER="$1"
SSH_IP="$2"

if [ -z "$SSH_USER" ] || [ -z "$SSH_IP" ]; then
    echo "❌ No IP address provided"
    exit 1
fi

echo "📡 SSH USER: $SSH_USER ..."
echo "📡 SSH IP: $SSH_USER ..."

if ping -c 1 -W 2 "$SSH_USER" >/dev/null 2>&1; then
    echo "✅ Ping to $SSH_USER success"
    exit 0
else
    echo "❌ Ping to $SSH_USER failed"
    exit 1
fi
