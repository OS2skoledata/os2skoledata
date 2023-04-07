#define AppId "{{c6eab83d-bd08-42f1-aa10-f76137f2cb70}"
#define AppSourceDir "..\bin\Debug\net6.0"
#define AppName "OS2skoledata SQL Sync"
#define AppPublisher "Digital Identity"
#define AppURL "http://digital-identity.dk/"
#define ExeName "os2skoledata-sql-sync.exe"
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
Source: "os2skoledata-sql-sync.runtimeconfig.json"; DestDir: "{app}"; Flags: ignoreversion
Source: "os2skoledata-sql-sync.deps.json"; DestDir: "{app}"; Flags: ignoreversion
Source: "appsettings.json"; DestDir: "{app}"; Flags: ignoreversion onlyifdoesntexist

[Run]
Filename: "sc.exe"; Parameters: "create ""{#AppName}"" binpath= ""{app}\{#ExeName}"" displayname=""{#AppName}"""; Flags: runhidden
Filename: "sc.exe"; Parameters: "description ""{#AppName}"" ""{#AppName}"""; Flags: runhidden

[UninstallRun]
Filename: "sc.exe"; Parameters: "stop ""{#AppName}"""; Flags: runhidden
Filename: "sc.exe"; Parameters: "delete ""{#AppName}"""; Flags: runhidden