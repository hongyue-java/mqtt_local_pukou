package mqtt_receive.entity;

import java.util.List;

public class jsonEntity {
    public String token;

    public String timestamp;

    public String data_row;

    public List<dataJson> body;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getData_row() {
        return data_row;
    }

    public void setData_row(String data_row) {
        this.data_row = data_row;
    }

    public List<dataJson> getBody() {
        return body;
    }

    public void setBody(List<dataJson> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "jsonEntity{" +
                "token='" + token + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", data_row='" + data_row + '\'' +
                ", body=" + body +
                '}';
    }
}
