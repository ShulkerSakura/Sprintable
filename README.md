# Sprintable

Sprintable is a small Forge/FML mod for Minecraft 1.4.7 that adds a dedicated sprint key binding.

The default key is Left Control. Holding it while moving forward triggers vanilla sprinting, the same way double-tapping forward does. Vanilla movement rules still decide when sprinting is allowed or cancelled.

## Features

- Adds a Sprint key binding to the Controls screen
- Saves the key binding through vanilla `options.txt`
- Supports English, Simplified Chinese, and Traditional Chinese labels
- Loads bundled `.lang` files as UTF-8 and refreshes the language table at startup
- Does not edit vanilla source files or ship official Minecraft classes

## Repository Layout

```text
Sprintable/
├── jars/
│   └── server.properties
├── lib/
│   ├── argo-2.25.jar
│   ├── asm-all-4.0.jar
│   ├── bcprov-jdk15on-147.jar
│   └── guava-12.0.1.jar
├── src/
│   └── minecraft/neko/shulker/sprintable/
│       ├── assets/sprintable/lang/*.lang
│       ├── logo.png
│       ├── mcmod.info
│       ├── SprintableMod.java
│       └── SprintableTickHandler.java
├── build.ps1
└── README.md
```

MCP 726a merges client and server into a single `src/minecraft` tree, so this version has no separate `src/common` directory.

`lib/` is intentionally vendored for this legacy MCP/Forge target. Official Minecraft jars, resources, generated classes, and decompiled vanilla sources are excluded by `.gitignore`.

## Build Requirements

- A prepared MCP 726a + Forge 6.6.2.534 workspace for Minecraft 1.4.7
- JDK 6 available for MCP scripts
- Windows PowerShell

By default the build script expects the MCP workspace at `../mcp726a`, next to this repository.

FML resolves its runtime libraries relative to the launcher working directory. When starting through MCP's `startclient.bat`, that directory is `mcp726a/jars`, so the four core libraries must exist in `mcp726a/jars/lib/`:

```text
argo-2.25.jar
asm-all-4.0.jar
bcprov-jdk15on-147.jar
guava-12.0.1.jar
```

Copies of these jars are vendored in `lib/` so the workspace can be set up without downloading them.

## Build

```powershell
cd C:\path\to\Sprintable
.\build.ps1
```

Use a custom MCP path:

```powershell
.\build.ps1 -McpDir "C:\path\to\mcp726a"
```

If `reobfuscate.bat` has already been run and you only want to package the jar:

```powershell
.\build.ps1 -SkipMcpBuild
```

The script copies `src/minecraft/neko/shulker/sprintable` into the MCP workspace, runs `recompile.bat` and `reobfuscate.bat`, then packages the reobfuscated classes.

The output is written to:

```text
dist/sprintable_1.4.7_1.0.0_forge.jar
```

The jar contains only:

```text
mcmod.info
logo.png
assets/sprintable/lang/*.lang
neko/shulker/sprintable/*.class
```

## Installation

Use a Minecraft 1.4.7 client with Forge 6.6.2.534 installed. Copy the built jar into `.minecraft/mods/` and start the game. The Sprint binding shows up in Options -> Controls.

## Notes

This branch targets Minecraft 1.4.7 Forge/FML. It replaces the earlier Minecraft 1.3.2 implementation.

On 1.4.7 FML, `@Mod.PreInit`, `@Mod.Init`, and `@Mod.PostInit` methods must accept their matching event parameter (`FMLPreInitializationEvent`, `FMLInitializationEvent`, `FMLPostInitializationEvent`). Methods without the parameter are silently skipped during loading.
