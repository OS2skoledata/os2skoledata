#define AppId "{{1203532b-2fa9-4e1a-917e-60c00bf8ba76}"
#define AppSourceDir "..\bin\Debug\net6.0\publish\win-x64"
#define AppName "OS2skoledata affiliation checker"
#define AppPublisher "Digital Identity"
#define AppURL "http://digital-identity.dk/"
#define ExeName "os2skoledata-affiliation-checker.exe"
#define AppVersion "1.1.0"

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
Source: "os2skoledata-affiliation-checker.runtimeconfig.json"; DestDir: "{app}"; Flags: ignoreversion
Source: "os2skoledata-affiliation-checker.deps.json"; DestDir: "{app}"; Flags: ignoreversion
Source: "appsettings.json"; DestDir: "{app}"; Flags: ignoreversion onlyifdoesntexist

[Run]
Filename: "sc.exe"; Parameters: "create ""{#AppName}"" binpath= ""{app}\{#ExeName}"" displayname=""{#AppName}"""; Flags: runhidden
Filename: "sc.exe"; Parameters: "description ""{#AppName}"" ""{#AppName}"""; Flags: runhidden

[UninstallRun]
Filename: "sc.exe"; Parameters: "stop ""{#AppName}"""; Flags: runhidden
Filename: "sc.exe"; Parameters: "delete ""{#AppName}"""; Flags: runhidden
