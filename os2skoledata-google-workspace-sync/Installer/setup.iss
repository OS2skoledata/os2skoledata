#define AppId "{{53ba27f8-71fe-4eb6-aae0-11527f60cd57}"
#define AppSourceDir "..\bin\Debug\net6.0"
#define AppName "OS2skoledata Active Directory Sync"
#define AppPublisher "Digital Identity"
#define AppURL "http://digital-identity.dk/"
#define ExeName "os2skoledata_google_workspace_sync.exe"
#define AppVersion GetVersionNumbersString(AppSourceDir + "\" + ExeName)

[Setup]
AppId={#AppId}
AppName={#AppName}
AppVersion={#AppVersion}
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
Source: "os2skoledata_google_workspace_sync.runtimeconfig.json"; DestDir: "{app}"; Flags: ignoreversion
Source: "os2skoledata_google_workspace_sync.deps.json"; DestDir: "{app}"; Flags: ignoreversion
Source: "appsettings.json"; DestDir: "{app}"; Flags: ignoreversion onlyifdoesntexist

[Run]
Filename: "sc.exe"; Parameters: "create ""{#AppName}"" binpath= ""{app}\{#ExeName}"" displayname=""{#AppName}"""; Flags: runhidden
Filename: "sc.exe"; Parameters: "description ""{#AppName}"" ""{#AppName}"""; Flags: runhidden

[UninstallRun]
Filename: "sc.exe"; Parameters: "stop ""{#AppName}"""; Flags: runhidden
Filename: "sc.exe"; Parameters: "delete ""{#AppName}"""; Flags: runhidden