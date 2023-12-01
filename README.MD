## Siemens LOGO! Presetter
This application allows you to scan the network for the presence of a Logo device,
find out the current network and access parameters of each device.
Among other things, it is possible to change the network address of the device.

### Usage
Show help message:
```shell
java -jar siemens-logo-presetter-0.0.1.jar --help
```

Detect all devices in network (**you must connect to device DIRECTLY**):
```shell
java -jar siemens-logo-presetter-0.0.1.jar scan
```

Show information about device with specified `--ip` address:
```shell
java -jar siemens-logo-presetter-0.0.1.jar show --ip 192.168.1.2
```

Update network parameter for device with specified `--ip` address:
```shell
java -jar siemens-logo-presetter-0.0.1.jar set --ip 192.168.1.2 --set-ip 192.168.1.20 --set-mask 255.255.255.0 --set-gateway 192.168.1.1
```
