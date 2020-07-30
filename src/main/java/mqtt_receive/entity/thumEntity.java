package mqtt_receive.entity;
import java.math.BigDecimal;
import java.sql.Timestamp;


/*@Entity
@DynamicInsert
@Table(name="thum")
@EntityListeners(AuditingEntityListener.class)*/
public class thumEntity {
   /* @Id
    @Column(name="id")*/
    public Integer id;

    //@Column(name = "EnvironmentTemperature")
    public BigDecimal environmentTemperature;

   // @Column(name = "EnvironmentHumidity")
    public BigDecimal environmentHumidity;

    //@Column(name = "State")
    public String State;

   // @Column(name = "PowerSupplyType")
    public String powerSupplyType;

   // @Column(name = "PowerCapacity")
    public BigDecimal powerCapacity;

    //@Column(name = "CommunicationType")
    public String communicationType;

    //@Column(name = "RSSI")
    public BigDecimal RSSI;

    //@Column(name = "SersorHWVersion")
    public String sersorHWVersion;

    //@Column(name = "SersorSWVersion")
    public String sersorSWVersion;

    //@Column(name = "sssbid")
    public Integer sssbId;

    //@Column(name = "receiveTime")
    public Timestamp receiveTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getGetEnvironmentTemperature() {
        return environmentTemperature;
    }

    public void setGetEnvironmentTemperature(BigDecimal getEnvironmentTemperature) {
        this.environmentTemperature = getEnvironmentTemperature;
    }

    public BigDecimal getEnvironmentHumidity() {
        return environmentHumidity;
    }

    public void setEnvironmentHumidity(BigDecimal environmentHumidity) {
        this.environmentHumidity = environmentHumidity;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPowerSupplyType() {
        return powerSupplyType;
    }

    public void setPowerSupplyType(String powerSupplyType) {
        this.powerSupplyType = powerSupplyType;
    }

    public BigDecimal getPowerCapacity() {
        return powerCapacity;
    }

    public void setPowerCapacity(BigDecimal powerCapacity) {
        this.powerCapacity = powerCapacity;
    }

    public String getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType(String communicationType) {
        this.communicationType = communicationType;
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
        return "thumEntity{" +
                "id='" + id + '\'' +
                ", environmentTemperature=" + environmentTemperature +
                ", environmentHumidity=" + environmentHumidity +
                ", State='" + State + '\'' +
                ", powerSupplyType='" + powerSupplyType + '\'' +
                ", powerCapacity=" + powerCapacity +
                ", communicationType='" + communicationType + '\'' +
                ", RSSI=" + RSSI +
                ", sersorHWVersion='" + sersorHWVersion + '\'' +
                ", sersorSWVersion='" + sersorSWVersion + '\'' +
                ", sssbId=" + sssbId +
                ", receiveTime=" + receiveTime +
                '}';
    }
}
