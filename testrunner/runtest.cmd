@echo off
set major=
set build=
set svn=
for /F %%I IN ('Findstr /R "major.number" version.properties') do set v=%%I
set major=%v:~13%
for /F %%I IN ('Findstr /R "build.number" version.properties') do set v=%%I
set build=%v:~13%
for /F %%I IN ('Findstr /R "svn.revision" version.properties') do set v=%%I
set svn=%v:~13%
@echo version#%major%.%build%.%svn%
@java.exe -jar %CD%/testrunner_%major%.%build%.%svn%.jar com.active.qa.automation.web.testrunner.date.TestDriver %1 %2 %3 %4 %5 %6 %7 %8 %9