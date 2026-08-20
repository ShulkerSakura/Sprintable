param(
    [string]$McpDir = "..\mcp72",
    [string]$Version = "1.0.0",
    [string]$JavaHome = $env:JAVA_HOME,
    [switch]$SkipMcpBuild
)

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$McpDir = [System.IO.Path]::GetFullPath((Join-Path $Root $McpDir))
$ModPackage = "neko\shulker\sprintable"
$Dist = Join-Path $Root "dist"
$Jar = Join-Path $Dist "sprintable_1.3.2_$Version`_forge.jar"

$SourceCommon = Join-Path $Root "src\common\$ModPackage"
$SourceClient = Join-Path $Root "src\minecraft\$ModPackage"
$McpSourceCommon = Join-Path $McpDir "src\common\$ModPackage"
$McpSourceClient = Join-Path $McpDir "src\minecraft\$ModPackage"
$ReobfPackage = Join-Path $McpDir "reobf\minecraft\$ModPackage"

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

Require-File (Join-Path $SourceCommon "sprintableMod.java")
Require-File (Join-Path $SourceCommon "CommonProxy.java")
Require-File (Join-Path $SourceCommon "SprintTickHandler.java")
Require-File (Join-Path $SourceCommon "mcmod.info")
Require-File (Join-Path $SourceClient "ClientProxy.java")
Require-File (Join-Path $Root "assets\logo.png")

Copy-CleanDirectory $SourceCommon $McpSourceCommon
Copy-CleanDirectory $SourceClient $McpSourceClient

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

Require-File (Join-Path $ReobfPackage "ClientProxy.class")
Require-File (Join-Path $ReobfPackage "CommonProxy.class")
Require-File (Join-Path $ReobfPackage "sprintableMod.class")
Require-File (Join-Path $ReobfPackage "SprintTickHandler.class")

New-Item -ItemType Directory -Force $Dist | Out-Null
if (Test-Path $Jar) {
    Remove-Item $Jar -Force
}

Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::Open($Jar, [System.IO.Compression.ZipArchiveMode]::Create)
try {
    Add-ZipFile $zip (Join-Path $SourceCommon "mcmod.info") "mcmod.info"
    Add-ZipFile $zip (Join-Path $Root "assets\logo.png") "logo.png"

    Add-ZipFile $zip (Join-Path $ReobfPackage "ClientProxy.class") "neko/shulker/sprintable/ClientProxy.class"
    Add-ZipFile $zip (Join-Path $ReobfPackage "CommonProxy.class") "neko/shulker/sprintable/CommonProxy.class"
    Add-ZipFile $zip (Join-Path $ReobfPackage "sprintableMod.class") "neko/shulker/sprintable/sprintableMod.class"
    Add-ZipFile $zip (Join-Path $ReobfPackage "SprintTickHandler.class") "neko/shulker/sprintable/SprintTickHandler.class"

    foreach ($lang in Get-ChildItem (Join-Path $SourceCommon "lang") -Filter "*.lang") {
        Add-ZipFile $zip $lang.FullName ("assets/sprintable/lang/" + $lang.Name)
    }
}
finally {
    $zip.Dispose()
}

Write-Host "Built $Jar"
Get-FileHash $Jar -Algorithm SHA1
