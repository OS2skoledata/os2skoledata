#define AppId "{{4a44f9d8-e6d1-48cf-80be-06fb4e034eb6}"
#define AppSourceDir "..\bin\publish"
#define AppName "OS2skoledataADSync"
#define AppPublisher "Digital Identity"
#define AppURL "http://digital-identity.dk/"
#define ExeName "os2skoledata-ad-sync.exe"
#define AppVersion "1.25.0"
#define AppVersionOut "1.25.0"

[Setup]
AppId={#AppId}
AppName={#AppName}
AppVersion={#AppVersionOut}
AppPublisher={#AppPublisher}
AppPublisherURL={#AppURL}
AppSupportURL={#AppURL}
AppUpdatesURL={#AppURL}
DefaultDirName={pf}\{#AppPublisher}\{#AppName}
DefaultGroupName={#AppName}
DisableProgramGroupPage=yes
OutputBaseFilename={#AppName}
Compression=lzma
SolidCompression=yes
SourceDir= {#SourcePath}\{#AppSourceDir}
OutputDir={#SourcePath}

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "*.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "*.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "*.pdb"; DestDir: "{app}"; Flags: ignoreversion
Source: "os2skoledata-ad-sync.runtimeconfig.json"; DestDir: "{app}"; Flags: ignoreversion
Source: "os2skoledata-ad-sync.deps.json"; DestDir: "{app}"; Flags: ignoreversion
Source: "appsettings.json"; DestDir: "{app}"; Flags: ignoreversion onlyifdoesntexist
Source: "appsettings.json"; DestDir: "{app}"; DestName: "appsettings.json.default"; Flags: ignoreversion
Source: "Services\PowerShellRunner\*.ps1"; DestDir: "{app}\PowerShell"; Flags: ignoreversion onlyifdoesntexist

[Run]
Filename: "sc.exe"; Parameters: "create ""{#AppName}"" binpath= ""{app}\{#ExeName}"" displayname=""{#AppName}"""; Flags: runhidden
Filename: "sc.exe"; Parameters: "description ""{#AppName}"" ""{#AppName}"""; Flags: runhidden

[UninstallRun]
Filename: "sc.exe"; Parameters: "stop ""{#AppName}"""; Flags: runhidden
Filename: "sc.exe"; Parameters: "delete ""{#AppName}"""; Flags: runhidden