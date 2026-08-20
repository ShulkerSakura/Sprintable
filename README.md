# Sprintable

Sprintable is a small Forge/FML mod for Minecraft 1.3.2 that adds a dedicated sprint key binding.

The default key is Left Control. Holding it while moving forward triggers vanilla sprinting, the same way double-tapping forward does. Vanilla movement rules still decide when sprinting is allowed or cancelled.

## Features

- Adds a Sprint key binding to the Controls screen
- Saves the key binding through vanilla `options.txt`
- Supports English, Simplified Chinese, and Traditional Chinese labels
- Loads translations from bundled `.lang` files
- Does not edit vanilla source files or ship official Minecraft classes

## Repository Layout

```text
Sprintable/
├── assets/
│   └── logo.png
├── jars/
│   └── server.properties
├── lib/
│   ├── argo-2.25.jar
│   ├── asm-all-4.0.jar
│   └── guava-12.0.1.jar
├── src/
│   ├── common/neko/shulker/sprintable/
│   │   ├── lang/*.lang
│   │   ├── mcmod.info
│   │   ├── CommonProxy.java
│   │   ├── SprintTickHandler.java
│   │   └── sprintableMod.java
│   └── minecraft/neko/shulker/sprintable/
│       └── ClientProxy.java
├── build.ps1
└── README.md
```

`lib/` is intentionally vendored for this legacy MCP/Forge target. Official Minecraft jars, resources, generated classes, and decompiled vanilla sources are excluded by `.gitignore`.

## Build Requirements

- A prepared MCP 7.2 + Forge 4.3.5.318 workspace for Minecraft 1.3.2
- JDK 6 or JDK 8 available for MCP scripts
- Windows PowerShell

By default the build script expects the MCP workspace at `../mcp72`, next to this repository.

## Build

```powershell
cd C:\path\to\Sprintable
.\build.ps1
```

Use a custom MCP path:

```powershell
.\build.ps1 -McpDir "C:\path\to\mcp72"
```

If `reobfuscate.bat` has already been run and you only want to package the jar:

```powershell
.\build.ps1 -SkipMcpBuild
```

The output is written to:

```text
dist/sprintable_1.3.2_1.0.0_forge.jar
```

The jar contains only:

```text
mcmod.info
logo.png
assets/sprintable/lang/*.lang
neko/shulker/sprintable/*.class
```

## Installation

Use a Minecraft 1.3.2 client with Forge 4.3.5.318 installed. Copy the built jar into `.minecraft/mods/` and start the game.

## Notes

This branch targets Minecraft 1.3.2 Forge/FML. It replaces the older Minecraft 1.1 ModLoader implementation from previous versions.
