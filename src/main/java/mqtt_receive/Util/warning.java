package mqtt_receive.Util;

import jdk.nashorn.internal.scripts.JD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class warning {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //超限告警
    public Map<String, Object> warningDetail(String id,String key,String value,String receivetime){
        System.out.println(id+key+value+receivetime);
        Map map = new HashMap();
        String max = "";
        String min = "";
        try {
            BigDecimal bd = new BigDecimal(value);
            Object realvalue = bd;
            if(key.equals("EnvironmentTemperature")||key.equals("TargetTemperature1")||key.equals("TargetTemperature2")||key.equals("TargetTemperature3")||key.equals("TargetTemperature0")){//环境温度
                if(bd.compareTo(new BigDecimal(120))>0||bd.compareTo(new BigDecimal(-40))<0){//环境温度超限
                    map=res(id,key);
                    max = "120";
                    min="-40";
                }
            }else if(key.equals("EnvironmentHumidity")||key.equals("Density")){
                if(bd.compareTo(new BigDecimal(100))>0||bd.compareTo(new BigDecimal(0))<0){//环境湿度超限
                    map=res(id,key);
                    max = "100";
                    min="0";
                }
            }else if(key.equals("UW-SamplingPeak")){
                if(bd.compareTo(new BigDecimal(8))>0){//超声波局放超限
                    map=res(id,key);
                    max = "8";
                    min="0";
                }
            }else if(key.equals("TEV-SamplingPeak")){
                if(bd.compareTo(new BigDecimal(20))>0){//地电波超限
                    map=res(id,key);
                    max = "20";
                    min="0";
                }
            }else if(key.equals("TargetTemperature")){
                if(bd.compareTo(new BigDecimal(40))>0){//局放温度超限
                    map=res(id,key);
                    max = "40";
                    min="0";
                }
            }else if(key.equals("UHF-SamplingPeak")){
                if(bd.compareTo(new BigDecimal(50))>0){//特高频超限
                    map=res(id,key);
                    max = "50";
                    min="0";
                }
            }else if(key.equals("Smoke")||key.equals("WaterSoak")||key.equals("InfraredEntry")){
                realvalue = value;
                if(value.equals("DETECTED")){//特高频超限
                    map=res(id,key);
                    max = "UNDETECTED";
                    min="DETECTED";
                }
            }else {
                return map;
            }
            //插表
            if(!map.isEmpty()){
                Object warningtype = map.get("warningtype");
                Object warningdevice = map.get("deviceName");
                Object warningdevicetype = map.get("typename");
                Object sensorname = map.get("ddd");
                String insert = "insert into warninginfo_sensor (warningtype,warningtime,warningdevice,warningdevicetype,max_value,minvalue,realvalue,sensorname) values ('"+warningtype+"','"+receivetime+"','"+warningdevice+"','"+warningdevicetype+"','"+max+"','"+min+"','"+realvalue+"','"+sensorname+"')";
              //  System.out.println(insert);
                jdbcTemplate.update(insert);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return map;
    }

    //查询属性关联
    public  Map res(String id,String key){
        if(key.contains("-")){
            key="`"+key+"`";
        }
        String sql="select a.deviceName,a.typename,b.devicename ddd from sensor_device a,sensor_table b where a.sbid = b.sensorno and a.sbid='"+id+"'";
        String sql1="select warningtype from attribute_sensor where attribute='"+key+"'";
       // System.out.println(sql);
       // System.out.println(sql1);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql1);
        Map resultmap1 = key_val(list,list1);
        return resultmap1;
    }

    public Map key_val(List<Map<String, Object>> list,List<Map<String, Object>> list1){
        Map map = list.get(0);
        Map map1 = list1.get(0);
        Set set = map.keySet();
        Set set1 = map1.keySet();
        Map mapset = new HashMap();

        for(Iterator iter = set.iterator(); iter.hasNext();)
        {

            String key = (String)iter.next();
            Object value = map.get(key);
            mapset.put(key,value);
           // System.out.println(key+"===="+value);
        }

        for(Iterator iter = set1.iterator(); iter.hasNext();)
        {

            String key = (String)iter.next();
            Object value = map1.get(key);
            mapset.put(key,value);
           // System.out.println(key+"===="+value);
        }
        return mapset;
    }
}
