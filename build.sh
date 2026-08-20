#!/usr/bin/env sh
set -eu

MCP_DIR="${1:-../mcp56}"
VERSION="${VERSION:-1.0.0}"
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
SOURCE="$ROOT/src/mod_Sprintable.java"
MCP_SOURCE="$MCP_DIR/src/minecraft/net/minecraft/src/mod_Sprintable.java"
REOBF_CLASS="$MCP_DIR/reobf/minecraft/mod_Sprintable.class"
DIST="$ROOT/dist"
STAGE="$ROOT/build/stage"
JAR="$DIST/Sprintable-$VERSION-mc1.1.jar"

if [ ! -f "$SOURCE" ]; then
    echo "Source file not found: $SOURCE" >&2
    exit 1
fi
if [ ! -d "$MCP_DIR" ]; then
    echo "MCP directory not found: $MCP_DIR" >&2
    exit 1
fi

cp "$SOURCE" "$MCP_SOURCE"

(
    cd "$MCP_DIR"
    ./recompile.sh
    ./reobfuscate.sh
)

if [ ! -f "$REOBF_CLASS" ]; then
    echo "Reobfuscated class not found: $REOBF_CLASS" >&2
    exit 1
fi

rm -rf "$STAGE"
mkdir -p "$STAGE" "$DIST"
cp "$REOBF_CLASS" "$STAGE/mod_Sprintable.class"
jar cf "$JAR" -C "$STAGE" .

echo "Built $JAR"
if command -v sha1sum >/dev/null 2>&1; then
    sha1sum "$JAR"
elif command -v shasum >/dev/null 2>&1; then
    shasum -a 1 "$JAR"
fi
