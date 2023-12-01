package ru.loolzaaa.siemens.logopresetter.scan;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.loolzaaa.siemens.logopresetter.config.DeviceType;

import java.util.Objects;

@Getter
@Setter
@ToString
public class DeviceInfo {

    private int cmdId;
    private int srcId;
    private int sessionId;
    private String deviceName;
    private int hwId;
    private String ip;
    private String mask;
    private String gateway;
    private String mac;
    private int fwSeries;
    private DeviceType deviceType;
    private int deviceState;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceInfo that = (DeviceInfo) o;
        return Objects.equals(mac, that.mac);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mac);
    }
}
