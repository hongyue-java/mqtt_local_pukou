package mqtt_receive.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class findTableName {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Object getTableName(String id){
        String sql="select tablename from sensor_table where sensorno='"+id+"'";
        //System.out.println(sql);
        Map<String,Object> map = jdbcTemplate.queryForMap(sql);
       // System.out.println(map);

        Object tablename = map.get("tablename");
        return tablename;
    }

    //获取传感器名称
    public Object getDeviceName(String id){
        String sql="select devicename from sensor_table where sensorno='"+id+"'";
        //System.out.println(sql);
        Map<String,Object> map = jdbcTemplate.queryForMap(sql);
        // System.out.println(map);
        Object devicename = map.get("devicename");
        return devicename;
    }

    public List<Map<String, Object>> getSensorId(String col){

        String sql="select sensorno from sensor_table where tablename in (select sstable from attribute_sensor where attribute = '"+col+"')";
      //  System.out.println(sql);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        //System.out.println(list.size());
        return list;
    }
}
