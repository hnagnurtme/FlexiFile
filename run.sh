#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
TOMCAT_DIR=~/Downloads/tomcat-10
WAR_NAME=FlexiFile-0.1.0-SNAPSHOT.war
CONTEXT_NAME=FlexiFile

# Hàm dọn dẹp khi nhấn Ctrl+C
cleanup() {
    echo ""
    echo "==> Stopping Tomcat 10..."
    "$TOMCAT_DIR/bin/shutdown.sh"
    echo "Tomcat stopped."
    exit 0
}

# Bắt Ctrl+C
trap cleanup SIGINT

echo "==> Building project..."
cd "$ROOT_DIR"
mvn clean package -DskipTests

echo ""
echo "==> Deploying WAR to Tomcat 10..."
cp "target/$WAR_NAME" "$TOMCAT_DIR/webapps/$CONTEXT_NAME.war"

echo ""
echo "==> Starting Tomcat 10..."
"$TOMCAT_DIR/bin/startup.sh"
echo "App running at http://localhost:8080/$CONTEXT_NAME"
echo "Press Ctrl+C to stop Tomcat..."

# Giữ script chạy để bắt Ctrl+C
while true; do
    sleep 1
done
