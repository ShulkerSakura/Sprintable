param(
    [string]$McpDir = "..\mcp56",
    [string]$Version = "1.0.0"
)

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$Source = Join-Path $Root "src\mod_Sprintable.java"
$McpSource = Join-Path $McpDir "src\minecraft\net\minecraft\src\mod_Sprintable.java"
$ReobfClass = Join-Path $McpDir "reobf\minecraft\mod_Sprintable.class"
$Dist = Join-Path $Root "dist"
$Stage = Join-Path $Root "build\stage"
$Jar = Join-Path $Dist "Sprintable-$Version-mc1.1.jar"

if (!(Test-Path $Source)) {
    throw "Source file not found: $Source"
}
if (!(Test-Path $McpDir)) {
    throw "MCP directory not found: $McpDir"
}

Copy-Item $Source $McpSource -Force

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

if (!(Test-Path $ReobfClass)) {
    throw "Reobfuscated class not found: $ReobfClass"
}

if (Test-Path $Stage) {
    Remove-Item $Stage -Recurse -Force
}
New-Item -ItemType Directory -Force $Stage | Out-Null
New-Item -ItemType Directory -Force $Dist | Out-Null

Copy-Item $ReobfClass (Join-Path $Stage "mod_Sprintable.class") -Force
jar cf $Jar -C $Stage .

Write-Host "Built $Jar"
Get-FileHash $Jar -Algorithm SHA1
