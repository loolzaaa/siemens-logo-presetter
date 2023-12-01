package ru.loolzaaa.siemens.logopresetter.hardware;

import ru.loolzaaa.siemens.logopresetter.net.DataTransfer;
import ru.loolzaaa.siemens.logopresetter.net.LogoRequest;
import ru.loolzaaa.siemens.logopresetter.net.LogoResponse;
import ru.loolzaaa.siemens.logopresetter.util.IPV4Utils;

import java.io.IOException;

public class BaseHardware implements Hardware {

    private final int hardwareId;

    public BaseHardware(int hardwareId) {
        this.hardwareId = hardwareId;
    }

    public IPConfig getIPConfig(DataTransfer dt) throws IOException {
        IPConfig ipconfig = new IPConfig();
        LogoRequest req = (new LogoRequest.Builder("GetIp")).build();

        dt.writeBytes(req.getRequestByte());
        LogoResponse resp = new LogoResponse(dt.readBytes(), true);
        if (resp.getLength() == 28 && resp.getResult() == 0) {
            int ip = resp.getIntFromParameter(0);
            int mask = resp.getIntFromParameter(4);
            int gate = resp.getIntFromParameter(8);
            ipconfig.ip = IPV4Utils.getInstance().formatToString(ip);
            ipconfig.mask = IPV4Utils.getInstance().formatToString(mask);
            ipconfig.gateway = IPV4Utils.getInstance().formatToString(gate);
        }

        return ipconfig;
    }

    public void setIPConfig(DataTransfer dt, IPConfig config, boolean changeImmediate) throws IOException {
        LogoRequest req = (new LogoRequest.Builder("SetIp")).build();
        req.setDoubleWordParameter(IPV4Utils.getInstance().formatToInt(config.ip));
        req.setDoubleWordParameter(IPV4Utils.getInstance().formatToInt(config.mask));
        req.setDoubleWordParameter(IPV4Utils.getInstance().formatToInt(config.gateway));
        req.setParameter(changeImmediate ? 0 : 1);

        dt.writeBytes(req.getRequestByte());
        dt.readBytes();
    }

    @Override
    public int getID() {
        return hardwareId;
    }

    public static class IPConfig {
        public String name;
        public String ip;
        public String mask;
        public String gateway;
        public String macaddr;
    }
}
