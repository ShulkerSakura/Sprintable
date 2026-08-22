param(
    [string]$McpDir = "..\mcp726a",
    [string]$Version = "1.0.0",
    [string]$JavaHome = $env:JAVA_HOME,
    [switch]$SkipMcpBuild
)

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$McpDir = [System.IO.Path]::GetFullPath((Join-Path $Root $McpDir))
$ModPackage = "neko\shulker\sprintable"
$JarPackage = "neko/shulker/sprintable"
$Dist = Join-Path $Root "dist"
$Jar = Join-Path $Dist "sprintable_1.4.7_$Version`_forge.jar"

$Source = Join-Path $Root "src\minecraft\$ModPackage"
$McpSource = Join-Path $McpDir "src\minecraft\$ModPackage"
$ReobfPackage = Join-Path $McpDir "reobf\minecraft\$ModPackage"

$Classes = @(
    "SprintableMod.class",
    "SprintableTickHandler.class"
)

function Require-File([string]$Path) {
    if (!(Test-Path $Path)) {
        throw "Required file not found: $Path"
    }
}

function Copy-CleanDirectory([string]$From, [string]$To) {
    if (Test-Path $To) {
        Remove-Item $To -Recurse -Force
    }
    New-Item -ItemType Directory -Force (Split-Path -Parent $To) | Out-Null
    Copy-Item $From $To -Recurse -Force
}

function Add-ZipFile($Zip, [string]$SourcePath, [string]$EntryName) {
    [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($Zip, $SourcePath, $EntryName) | Out-Null
}

if (!(Test-Path $McpDir)) {
    throw "MCP directory not found: $McpDir"
}

if ($JavaHome) {
    if (!(Test-Path (Join-Path $JavaHome "bin\java.exe"))) {
        throw "JavaHome does not contain bin\java.exe: $JavaHome"
    }
    $env:JAVA_HOME = $JavaHome
    $env:PATH = (Join-Path $JavaHome "bin") + ";" + $env:PATH
}

Require-File (Join-Path $Source "SprintableMod.java")
Require-File (Join-Path $Source "SprintableTickHandler.java")
Require-File (Join-Path $Source "mcmod.info")
Require-File (Join-Path $Source "logo.png")

# MCP 726a 已由 FML 合并为单一 src/minecraft 源码树，客户端与服务端共用
Copy-CleanDirectory $Source $McpSource

if (!$SkipMcpBuild) {
    Push-Location $McpDir
    try {
        & ".\recompile.bat"
        if ($LASTEXITCODE -ne 0) {
            throw "recompile.bat failed with exit code $LASTEXITCODE"
        }

        & ".\reobfuscate.bat"
        if ($LASTEXITCODE -ne 0) {
            throw "reobfuscate.bat failed with exit code $LASTEXITCODE"
        }
    }
    finally {
        Pop-Location
    }
}

foreach ($class in $Classes) {
    Require-File (Join-Path $ReobfPackage $class)
}

New-Item -ItemType Directory -Force $Dist | Out-Null
if (Test-Path $Jar) {
    Remove-Item $Jar -Force
}

Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::Open($Jar, [System.IO.Compression.ZipArchiveMode]::Create)
try {
    Add-ZipFile $zip (Join-Path $Source "mcmod.info") "mcmod.info"
    Add-ZipFile $zip (Join-Path $Source "logo.png") "logo.png"

    foreach ($class in $Classes) {
        Add-ZipFile $zip (Join-Path $ReobfPackage $class) "$JarPackage/$class"
    }

    foreach ($lang in Get-ChildItem (Join-Path $Source "assets\sprintable\lang") -Filter "*.lang") {
        Add-ZipFile $zip $lang.FullName ("assets/sprintable/lang/" + $lang.Name)
    }
}
finally {
    $zip.Dispose()
}

Write-Host "Built $Jar"
Get-FileHash $Jar -Algorithm SHA1
