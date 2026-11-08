#!/usr/bin/env bash
set -euo pipefail

# Keep this version in sync with deploy.maven.pomchecker.version in jreleaser.yml.
version=1.15.0
# SHA-256 of the official pomchecker-cli-1.15.0-all.jar release asset.
checksum=a89d00f8d5a694f599814ee65b124cf73661d51f7e4f8056714facd82ca4e3d0

: "${RUNNER_TEMP:?RUNNER_TEMP must point to a temporary directory}"
: "${GITHUB_PATH:?GITHUB_PATH must point to the Actions path file}"

trap 'echo "::error::Could not prepare PomChecker. Publication has not started."' ERR
tool_dir=$(mktemp -d "$RUNNER_TEMP/pomchecker.XXXXXX")

# Use Maven Central and the existing Maven cache instead of JReleaser's
# automatic GitHub ZIP download. Retry transient HTTP failures only.
./mvnw -B -ntp -N dependency:copy \
  -Dartifact="org.kordamp.maven:pomchecker-cli:${version}:jar:all" \
  -DoutputDirectory="$tool_dir" \
  -Daether.connector.http.retryHandler.count=3 \
  -Daether.connector.http.retryHandler.serviceUnavailable=429,502,503,504

jar_file="$tool_dir/pomchecker-cli-${version}-all.jar"
printf '%s  %s\n' "$checksum" "$jar_file" | sha256sum --check --strict
mv "$jar_file" "$tool_dir/pomchecker.jar"

cat > "$tool_dir/pomchecker" <<'EOF'
#!/usr/bin/env bash
set -euo pipefail
exec "${JAVA_HOME:?JAVA_HOME must be set}/bin/java" -jar "$(dirname "$0")/pomchecker.jar" "$@"
EOF
chmod +x "$tool_dir/pomchecker"
"$tool_dir/pomchecker" --version

# JReleaser discovers the prepared CLI on PATH and keeps its POM validation.
echo "$tool_dir" >> "$GITHUB_PATH"
