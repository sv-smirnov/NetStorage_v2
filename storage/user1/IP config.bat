@echo off
chcp 1251
:begin
cls
set /p settings="Choose connection (1 - Default, 2 - SDK, 3 - R&S, 4 - VSAT, 5 - Other, 6 - Ping, 7 - StreamLabs):"
echo Selected option - %settings%

if %settings% equ 1 goto default
if %settings% equ 2 goto sdk
if %settings% equ 3 goto R&S
if %settings% equ 4 goto VSAT
if %settings% equ 5 goto Other
if %settings% equ 6 goto Ping
if %settings% equ 7 goto StreamLabs

:default
netsh interface ip set address "Ethernet" dhcp
set ITYPE=On
set IP=Auto
set MASK=Auto
set GATEWAY=Auto
goto dalee

:SDK
netsh interface ip set address "Ethernet" static 192.168.250.222 255.255.255.0
set ITYPE=Off
set IP=192.168.250.222
set MASK=255.255.255.0
set GATEWAY=none
goto dalee

:R&S
netsh interface ip set address "Ethernet" static 192.168.58.222 255.255.255.0
set ITYPE=Off
set IP=192.168.58.222
set MASK=255.255.255.0
set GATEWAY=none
goto dalee

:VSAT
netsh interface ip set address "Ethernet" static 10.2.218.109 255.255.255.0 10.2.218.1
set ITYPE=Off
set IP=10.2.218.109
set MASK=255.255.255.0
set GATEWAY=10.2.218.1
goto dalee

:Other
set /p OtherIP="IP address:"
set /p OtherMASK="Subnet Mask:"
set /p OtherGATEWAY="Default Gateway:"
netsh interface ip set address "Ethernet" static %OtherIP% %OtherMASK% %OtherGATEWAY% 
set ITYPE=Off
set IP=%OtherIP%
set MASK=%OtherMASK%
set GATEWAY=%OtherGATEWAY%
goto dalee

:Ping
set /p OtherIP="IP address:"
ping %OtherIP% -n 3
pause
goto begin

:StreamLabs
netsh interface ip set address "Ethernet" static 192.168.10.5 255.255.255.0
set ITYPE=Off
set IP=192.168.10.5
set MASK=255.255.255.0
set GATEWAY=none
goto dalee

:dalee
echo Curent settings:
echo DHCP: %ITYPE%
echo IP address: %IP%
echo Subnet Mask: %MASK%
echo Default Gateway: %GATEWAY%

pause

goto begin
