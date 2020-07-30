package mqtt_receive.entity;

public class dataJson {
    private String name;
    private String val;
    private String quality;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    @Override
    public String toString() {
        return "dataJson{" +
                "name='" + name + '\'' +
                ", val='" + val + '\'' +
                ", quality='" + quality + '\'' +
                '}';
    }
}
