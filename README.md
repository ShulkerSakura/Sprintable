# Sprintable

Sprintable is a client-side mod for Minecraft 1.2.5 Forge 3.4.9.171 that adds a dedicated sprint key to the game.

## Features

- Registers a `Sprint` key binding in the Controls menu, defaulting to Left Ctrl.
- Starts sprinting while the key is held and the player is moving forward.
- Includes `en_US`, `zh_CN`, and `zh_TW` localizations.
- Uses the ModLoader/BaseMod compatibility path and FML tick handlers available in Minecraft 1.2.5.
- Does not require changes to vanilla Minecraft source files.

## Repository Layout

```text
Sprintable/
├── src/main/java/neko/shulker/sprintable/
│   ├── mod_Sprintable.java
│   └── SprintableTickHandler.java
├── src/main/resources/
│   ├── mcmod.info
│   ├── logo.png
│   └── assets/sprintable/lang/
│       ├── en_US.lang
│       ├── zh_CN.lang
│       └── zh_TW.lang
├── build.ps1
├── build.sh
├── .gitignore
├── LICENSE
└── README.md
```

MCP, Minecraft, Forge, runtime logs, and build output are not included in this repository. The build scripts use an adjacent `mcp62` directory by default.

## Requirements

Prepare an MCP 6.2 workspace with Minecraft 1.2.5 Forge 3.4.9.171 already installed.

- Windows: PowerShell, a JDK, and the `jar` command must be available.
- Linux: a POSIX shell, a JDK, and the `jar` command must be available.
- The MCP workspace must provide `recompile` and `reobfuscate` scripts.
- Minecraft and Forge files must be obtained and prepared legally by the user.

This repository does not distribute Minecraft, Forge, MCP, or their dependencies.

## Build on Windows

Default directory layout:

```text
workspace/
├── Sprintable/
└── mcp62/
```

From the `Sprintable` directory, run:

```powershell
.\build.ps1
```

To specify an MCP directory and mod version:

```powershell
.\build.ps1 -McpDir "C:\path\to\mcp62" -Version "1.0.0"
```

If PowerShell blocks script execution, bypass the policy for the current process only:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\build.ps1
```

## Build on Linux

Use the same default directory layout. From the `Sprintable` directory, run:

```sh
chmod +x build.sh
./build.sh
```

To specify an MCP directory and mod version:

```sh
VERSION=1.0.0 ./build.sh /path/to/mcp62
```

## Build Process

The build scripts perform the following steps:

1. Copy the repository Java sources to MCP's `src/minecraft/neko/shulker/sprintable/` directory.
2. Run MCP `recompile`.
3. Run MCP `reobfuscate`.
4. Assemble the reobfuscated classes and resources into a complete mod jar.
5. Write the result to `dist/Sprintable-1.0.0-mc1.2.5.jar`.

The resulting jar has this structure:

```text
mcmod.info
logo.png
assets/sprintable/lang/*.lang
neko/shulker/sprintable/mod_Sprintable.class
neko/shulker/sprintable/SprintableTickHandler.class
```

## Installation

Place the jar generated in `dist/` into the `mods` directory used by a Minecraft 1.2.5 Forge client, then start the client. Sprintable's name, version, authors, description, and icon should appear in the Forge Mod List.

## Development

1. Edit files in `src/main/java` or `src/main/resources`.
2. Run the appropriate build script again.
3. Do not commit generated files from the external MCP workspace, including `bin`, `reobf`, logs, and runtime data.

## License

This project is licensed under the terms in [LICENSE](LICENSE).
