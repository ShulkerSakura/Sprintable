#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MCP_DIR=${1:-"$SCRIPT_DIR/../mcp62"}
VERSION=${VERSION:-1.0.0}
MCP_DIR=$(CDPATH= cd -- "$MCP_DIR" && pwd)
SOURCE_ROOT="$SCRIPT_DIR/src/main"
SOURCE_PACKAGE="$SOURCE_ROOT/java/neko/shulker/sprintable"
MCP_PACKAGE="$MCP_DIR/src/minecraft/neko/shulker/sprintable"
REOBF_PACKAGE="$MCP_DIR/reobf/minecraft/neko/shulker/sprintable"
DIST="$SCRIPT_DIR/dist"
STAGE="$SCRIPT_DIR/build/stage"
JAR="$DIST/Sprintable-$VERSION-mc1.2.5.jar"

require_path() {
    if [ ! -e "$1" ]; then
        echo "$2 not found: $1" >&2
        exit 1
    fi
}

require_path "$MCP_DIR/recompile.sh" "MCP recompile script"
require_path "$MCP_DIR/reobfuscate.sh" "MCP reobfuscate script"
require_path "$SOURCE_PACKAGE" "Java source package"
require_path "$SOURCE_ROOT/resources/mcmod.info" "mcmod.info"
require_path "$SOURCE_ROOT/resources/logo.png" "logo.png"

rm -rf "$MCP_PACKAGE"
mkdir -p "$MCP_PACKAGE"
cp "$SOURCE_PACKAGE"/*.java "$MCP_PACKAGE"/

(
    cd "$MCP_DIR"
    ./recompile.sh
    ./reobfuscate.sh
)

require_path "$REOBF_PACKAGE/mod_Sprintable.class" "Reobfuscated mod class"
require_path "$REOBF_PACKAGE/SprintableTickHandler.class" "Reobfuscated tick handler"

rm -rf "$STAGE"
mkdir -p "$STAGE/neko/shulker/sprintable" "$STAGE/assets/sprintable/lang" "$DIST"
cp "$REOBF_PACKAGE"/*.class "$STAGE/neko/shulker/sprintable/"
cp "$SOURCE_ROOT/resources/mcmod.info" "$STAGE/"
cp "$SOURCE_ROOT/resources/logo.png" "$STAGE/"
cp "$SOURCE_ROOT/resources/assets/sprintable/lang"/*.lang "$STAGE/assets/sprintable/lang/"

rm -f "$JAR"
(
    cd "$STAGE"
    jar cf "$JAR" .
)

require_path "$JAR" "Built jar"
echo "Built $JAR"
if command -v sha256sum >/dev/null 2>&1; then
    sha256sum "$JAR"
elif command -v shasum >/dev/null 2>&1; then
    shasum -a 256 "$JAR"
fi
