; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
AppName=Columba
AppVerName=Columba 0.12.1
AppPublisherURL=http://columba.sourceforge.net/
AppSupportURL=http://columba.sourceforge.net/
AppUpdatesURL=http://columba.sourceforge.net/
DefaultDirName={pf}\Columba
DefaultGroupName=Columba
LicenseFile=LICENSE
AlwaysShowComponentsList=false
InfoAfterFile=CHANGES
OutputBaseFilename=ColumbaSetup
Compression=bzip
; we are in dest/win32/
SourceDir=..\..\
OutputDir=.
; uncomment the following line if you want your installation to run on NT 3.51 too.
; MinVersion=4,3.51

[Tasks]
Name: desktopicon; Description: Create a &desktop icon; GroupDescription: Additional icons:; MinVersion: 4,4

[Files]
Source: lib\jakarta-oro-2.0.6.jar; DestDir: {app}\lib\
Source: lib\jargs.jar; DestDir: {app}\lib\
Source: lib\log4j.jar; DestDir: {app}\lib\
Source: lib\lucene-1.3-rc1.jar; DestDir: {app}\lib\
Source: lib\jwizz-0.1.1.jar; DestDir: {app}\lib\
Source: lib\plastic.jar; DestDir: {app}\lib\
Source: AUTHORS; DestDir: {app}
Source: CHANGES; DestDir: {app}
Source: native\win32\launcher\bin\columba.exe; DestDir: {app}; DestName: columba.exe
Source: columba.jar; DestDir: {app}
Source: LICENSE; DestDir: {app}
Source: README; DestDir: {app}
Source: run.bat; DestDir: {app}

[Icons]
Name: {group}\Columba; Filename: {app}\columba.exe; IconIndex: 0; WorkingDir: {app}
Name: {userdesktop}\Columba; Filename: {app}\columba.exe; MinVersion: 4,4; Tasks: desktopicon; WorkingDir: {app}; IconIndex: 0
Name: {group}\AUTHORS; Filename: notepad.exe; Parameters: AUTHORS; WorkingDir: {app}; IconIndex: 0
Name: {group}\CHANGES; Filename: notepad.exe; Parameters: CHANGES; WorkingDir: {app}; IconIndex: 0
Name: {group}\LICENSE; Filename: notepad.exe; Parameters: LICENSE; WorkingDir: {app}; IconIndex: 0
Name: {group}\README; Filename: notepad.exe; Parameters: README; WorkingDir: {app}; IconIndex: 0

[Run]
Filename: {app}\columba.exe; Description: Launch Columba; Flags: nowait postinstall skipifsilent; WorkingDir: {app}

[_ISTool]
EnableISX=true
UseAbsolutePaths=true

[Dirs]

[Registry]
Root: HKLM; SubKey: SOFTWARE\Clients\Mail\Columba; ValueType: string; ValueName: ; ValueData: Columba
Root: HKLM; SubKey: SOFTWARE\Clients\Mail\Columba\FingerPrint; ValueType: string; ValueName: ColumbaHome; ValueData: {app}
Root: HKLM; SubKey: SOFTWARE\Clients\Mail\Columba\FingerPrint; ValueType: string; ValueName: MakeDefault; ValueData: YES
Root: HKLM; SubKey: SOFTWARE\Clients\Mail\Columba\Protocols\mailto; ValueType: string; ValueName: URL Protocol; ValueData: 
Root: HKLM; SubKey: SOFTWARE\Clients\Mail\Columba\Protocols\mailto; ValueType: string; ValueName: ; ValueData: URL:MailTo-Protokoll
Root: HKLM; SubKey: SOFTWARE\Clients\Mail\Columba\Protocols\mailto\shell\open\command; ValueType: string; ValueData: {app}\columba --mailurl %1


[Code]
//* Getting Java version from registry *//
function getJavaVersion(): String;
var
     javaVersion: String;
begin
     javaVersion := '';
     RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', javaVersion);
     GetVersionNumbersString(javaVersion, javaVersion);
     Result := javaVersion;
end;
