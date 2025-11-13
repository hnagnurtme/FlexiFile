#!/usr/bin/env bash
set -euo pipefail

# run.sh - build project and run Tomcat
# Usage: ./run.sh

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
MVN="${MVN:-mvn}"

echo "==> Building project..."
cd "$ROOT_DIR"
"$MVN" clean package -DskipTests

echo ""
echo "==> Starting Tomcat..."
echo "    (Press Ctrl+C to stop)"
echo ""
"$MVN" org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run
