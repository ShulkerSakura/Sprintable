[CmdletBinding()]
param(
    [string]$McpDir = "..\mcp62",
    [string]$Version = "1.0.0"
)

$ErrorActionPreference = "Stop"

$Root = (Resolve-Path (Join-Path $PSScriptRoot ".")).Path
$McpRoot = (Resolve-Path (Join-Path $Root $McpDir)).Path
$SourceRoot = Join-Path $Root "src\main"
$McpSourceRoot = Join-Path $McpRoot "src\minecraft"
$Package = "neko\shulker\sprintable"
$SourcePackage = Join-Path $SourceRoot "java\$Package"
$McpPackage = Join-Path $McpSourceRoot $Package
$ReobfPackage = Join-Path $McpRoot "reobf\minecraft\$Package"
$Dist = Join-Path $Root "dist"
$Build = Join-Path $Root "build"
$Stage = Join-Path $Build "stage"
$Jar = Join-Path $Dist "Sprintable-$Version-mc1.2.5.jar"

function Require-Path([string]$Path, [string]$Description) {
    if (!(Test-Path $Path)) {
        throw "$Description not found: $Path"
    }
}

Require-Path $McpRoot "MCP directory"
Require-Path (Join-Path $McpRoot "recompile.bat") "MCP recompile script"
Require-Path (Join-Path $McpRoot "reobfuscate.bat") "MCP reobfuscate script"
Require-Path $SourcePackage "Java source package"
Require-Path (Join-Path $SourceRoot "resources\mcmod.info") "mcmod.info"
Require-Path (Join-Path $SourceRoot "resources\logo.png") "logo.png"

if (Test-Path $McpPackage) {
    Remove-Item $McpPackage -Recurse -Force
}
New-Item -ItemType Directory -Force $McpPackage | Out-Null
Copy-Item (Join-Path $SourcePackage "*.java") $McpPackage -Force

Push-Location $McpRoot
try {
    & ".\recompile.bat"
    if ($LASTEXITCODE -ne 0) { throw "recompile.bat failed with exit code $LASTEXITCODE" }

    & ".\reobfuscate.bat"
    if ($LASTEXITCODE -ne 0) { throw "reobfuscate.bat failed with exit code $LASTEXITCODE" }
}
finally {
    Pop-Location
}

Require-Path (Join-Path $ReobfPackage "mod_Sprintable.class") "Reobfuscated mod class"
Require-Path (Join-Path $ReobfPackage "SprintableTickHandler.class") "Reobfuscated tick handler"

if (Test-Path $Stage) { Remove-Item $Stage -Recurse -Force }
New-Item -ItemType Directory -Force (Join-Path $Stage $Package) | Out-Null
New-Item -ItemType Directory -Force (Join-Path $Stage "assets\sprintable\lang") | Out-Null
New-Item -ItemType Directory -Force $Dist | Out-Null

Copy-Item (Join-Path $ReobfPackage "*.class") (Join-Path $Stage $Package) -Force
Copy-Item (Join-Path $SourceRoot "resources\mcmod.info") $Stage -Force
Copy-Item (Join-Path $SourceRoot "resources\logo.png") $Stage -Force
Copy-Item (Join-Path $SourceRoot "resources\assets\sprintable\lang\*.lang") (Join-Path $Stage "assets\sprintable\lang") -Force

if (Test-Path $Jar) { Remove-Item $Jar -Force }
jar cf $Jar -C $Stage .
Require-Path $Jar "Built jar"

Write-Host "Built $Jar"
Get-FileHash $Jar -Algorithm SHA256
