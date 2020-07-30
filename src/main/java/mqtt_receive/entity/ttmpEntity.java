package mqtt_receive.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class ttmpEntity {

    public String id;


    public BigDecimal TargetTemperature;


    public BigDecimal ProbeTemperature1;


    public BigDecimal ProbeTemperature2;


    public BigDecimal ProbeTemperature3;


    public BigDecimal ProbeTemperature4;

    public String State;

    public String PowerSupplyType;

    public BigDecimal PowerCapacity;

    public String CommunicationType;

    public BigDecimal RSSI;

    public String sersorHWVersion;

    public String sersorSWVersion;

    public Integer sssbId;

    public Timestamp receiveTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getTargetTemperature() {
        return TargetTemperature;
    }

    public void setTargetTemperature(BigDecimal targetTemperature) {
        TargetTemperature = targetTemperature;
    }

    public BigDecimal getProbeTemperature1() {
        return ProbeTemperature1;
    }

    public void setProbeTemperature1(BigDecimal probeTemperature1) {
        ProbeTemperature1 = probeTemperature1;
    }

    public BigDecimal getProbeTemperature2() {
        return ProbeTemperature2;
    }

    public void setProbeTemperature2(BigDecimal probeTemperature2) {
        ProbeTemperature2 = probeTemperature2;
    }

    public BigDecimal getProbeTemperature3() {
        return ProbeTemperature3;
    }

    public void setProbeTemperature3(BigDecimal probeTemperature3) {
        ProbeTemperature3 = probeTemperature3;
    }

    public BigDecimal getProbeTemperature4() {
        return ProbeTemperature4;
    }

    public void setProbeTemperature4(BigDecimal probeTemperature4) {
        ProbeTemperature4 = probeTemperature4;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPowerSupplyType() {
        return PowerSupplyType;
    }

    public void setPowerSupplyType(String powerSupplyType) {
        PowerSupplyType = powerSupplyType;
    }

    public BigDecimal getPowerCapacity() {
        return PowerCapacity;
    }

    public void setPowerCapacity(BigDecimal powerCapacity) {
        PowerCapacity = powerCapacity;
    }

    public String getCommunicationType() {
        return CommunicationType;
    }

    public void setCommunicationType(String communicationType) {
        CommunicationType = communicationType;
    }

    public BigDecimal getRSSI() {
        return RSSI;
    }

    public void setRSSI(BigDecimal RSSI) {
        this.RSSI = RSSI;
    }

    public String getSersorHWVersion() {
        return sersorHWVersion;
    }

    public void setSersorHWVersion(String sersorHWVersion) {
        this.sersorHWVersion = sersorHWVersion;
    }

    public String getSersorSWVersion() {
        return sersorSWVersion;
    }

    public void setSersorSWVersion(String sersorSWVersion) {
        this.sersorSWVersion = sersorSWVersion;
    }

    public Integer getSssbId() {
        return sssbId;
    }

    public void setSssbId(Integer sssbId) {
        this.sssbId = sssbId;
    }

    public Timestamp getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Timestamp receiveTime) {
        this.receiveTime = receiveTime;
    }

    @Override
    public String toString() {
        return "ttmpEntity{" +
                "id='" + id + '\'' +
                ", TargetTemperature=" + TargetTemperature +
                ", ProbeTemperature1=" + ProbeTemperature1 +
                ", ProbeTemperature2=" + ProbeTemperature2 +
                ", ProbeTemperature3=" + ProbeTemperature3 +
                ", ProbeTemperature4=" + ProbeTemperature4 +
                ", State='" + State + '\'' +
                ", PowerSupplyType='" + PowerSupplyType + '\'' +
                ", PowerCapacity=" + PowerCapacity +
                ", CommunicationType='" + CommunicationType + '\'' +
                ", RSSI=" + RSSI +
                ", sersorHWVersion='" + sersorHWVersion + '\'' +
                ", sersorSWVersion='" + sersorSWVersion + '\'' +
                ", sssbId=" + sssbId +
                ", receiveTime=" + receiveTime +
                '}';
    }
}
