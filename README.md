# Sprintable

Sprintable is a small Minecraft 1.1 ModLoader mod that adds a dedicated sprint key binding.

The default key is Left Control. Pressing it triggers vanilla sprinting once, the same way double-tapping forward does. Releasing the key does not forcibly cancel sprinting; vanilla movement rules decide when sprinting stops.

## Features

- Adds a Sprint key binding to the Controls screen
- Saves the key binding through vanilla `options.txt`
- Supports English and Chinese labels in the Controls screen
- Does not edit vanilla source files
- Works as a ModLoader jar/zip mod for Minecraft 1.1

## Source Layout

```text
Sprintable/
├── src/
│   └── mod_Sprintable.java
├── build.ps1
├── build.sh
├── README.md
└── LICENSE
```

Minecraft 1.1 ModLoader loads runtime mod classes from the default package, so the source class is named `mod_Sprintable` and the reobfuscated output is `mod_Sprintable.class` at the root of the jar.

## Build Requirements

- A working MCP 5.6 / Minecraft 1.1 workspace with ModLoader and Forge already installed
- JDK 6 available on `PATH` (`javac` and `jar` commands)
- Windows PowerShell for `build.ps1`, or a POSIX shell for `build.sh`

The scripts do not include MCP, Minecraft, ModLoader, or Forge. They copy `src/mod_Sprintable.java` into an existing MCP workspace, run MCP recompile/reobfuscate, then package the reobfuscated class.

## Build

From this repository directory, with MCP located at `../mcp56`:

```powershell
./build.ps1
```

Or explicitly pass the MCP path:

```powershell
./build.ps1 -McpDir "C:\\path\\to\\mcp56"
```

Linux / macOS:

```sh
chmod +x ./build.sh
./build.sh ../mcp56
```

The output is:

```text
dist/Sprintable-1.0.0-mc1.1.jar
```

## Installation

Use a Minecraft 1.1 client with ModLoader installed.

If your launcher supports loading old jar mods from a separate classpath or `modify` directory, put the built jar there.

For a classic manual jar install, copy `mod_Sprintable.class` from the built jar into `minecraft.jar` after installing ModLoader / Forge, then remove `META-INF` if needed.

## Notes

This branch targets Minecraft 1.1 ModLoader. It is not the same codebase as newer Forge/FML versions of Sprintable.
