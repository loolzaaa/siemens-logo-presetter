@echo off
java -jar siemens-logo-presetter-0.0.1.jar set --ip 192.168.1.1 --set-ip 192.168.2.123 --set-mask 255.255.255.0 --set-gateway 192.168.1.1
pause