del "OS2skoledata affiliation checker.exe"
"C:\Program Files (x86)\Inno Setup 6\ISCC.exe" setup.iss
"C:\Program Files (x86)\Windows Kits\10\bin\10.0.22621.0\x64\signtool.exe" ^
  sign /td SHA256 /fd SHA256 ^
  /f ..\..\..\codesigning\codesigning.pfx /p Test1234 ^
  /tr http://timestamp.globalsign.com/tsa/r6advanced1 ^
  "OS2skoledata affiliation checker.exe"

pause
