package mqtt_receive.controller;

import mqtt_receive.Util.Pagination;
import mqtt_receive.Util.findTableName;
import mqtt_receive.Util.vue_springboot_websocket.websocketUtil.MyWebSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(allowCredentials="true",maxAge = 3600)
//@RequestMapping("/test")
public class GetHomepageDataController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private findTableName findTableName;


    Pagination pagination = new Pagination();

    //获取主页全部传感器数据
    @GetMapping("/get/homepageData")
    public Map<String, Object> getHomepageData() throws Exception{
       // List<Map<String, Object>> list = getSensorData("3007440032");
        //thum温湿度
        List<Map<String, Object>> list = getSensorData("3007440139");
       // List<Map<String, Object>> list1 = getSensorData("1000000023");
        //tnsp噪声
        List<Map<String, Object>> list1 = getSensorData("3007448313");
        //List<Map<String, Object>> list2 = getSensorData("1000000001");
        //sf6
        List<Map<String, Object>> list2 = getSensorData("1000000001");
       // List<Map<String, Object>> list3 = getSensorData("1000004064");
        //臭氧
        List<Map<String, Object>> list3 = getSensorData("1000004064");
        //List<Map<String, Object>> list4 = getSensorData("1000002000");
        //局放
        List<Map<String, Object>> list4 = getSensorData("1000002000");
        //List<Map<String, Object>> list3 = getSensorData("0000-0000-0000-0006");
        Map result = new HashMap();
        result.put("thum",list);
        result.put("tnsp",list1);
        result.put("tsog",list2);
        result.put("tozp",list3);
        result.put("tuhf",list4);
        return result;
    }

    /*主页配电房图表数据*/
    @GetMapping("/get/homepageData/{type}/{deviceId}")
    public Map<String, List<Map<String, Object>>> getLineData(@PathVariable String type,@PathVariable String deviceId) throws Exception{
       String typet = "";
        if(String.valueOf(type).contains("-")){
            typet="`"+type+"`";
        }else{
            typet = type;
        }
        //查询折线图数据
        Object tableName = findTableName.getTableName(deviceId);
        String body ="IFNULL(CASE \n" +
                "WHEN (ISNULL("+typet+")=1)||(LENGTH(trim("+typet+"))=0)  \n" +
                "THEN (select "+typet+"  from "+tableName+"  as  child  where  child.id<parent.id  and  child."+typet+"  <>  ''  order  by    child.id  desc  LIMIT  1)\n" +
                "ELSE "+typet+"\n" +
                "END,0) as "+typet+",";
        String caseWhen = "select receiveTime, \n " +body.substring(0,body.length()-1)+" from "+tableName+" as  parent where sensorId='"+deviceId+"' and DATE_FORMAT(receiveTime,'%Y-%m-%d') = DATE_SUB(curdate(),INTERVAL 0 DAY) order by receiveTime asc";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(caseWhen);
        List list2 = new ArrayList();
        for(int i=0;i<list.size();i++){
            Map map = new HashMap();
            Map map1 = new HashMap();
            map=list.get(i);
            //System.out.println(map);
            Object dt = map.get(type);
           // Double et = Double.valueOf(String.valueOf(dt));
            Object rt = map.get("receiveTime");
            map1.put("name",dt);
            map1.put("time",rt);
            list2.add(map1);
        };
        Map map = new HashMap();
        map.put("lineData",list2);
       // System.out.println(map);
        //myWebSocket.sendInfo(map);
        return map;
    }

    //红外水浸烟感明火
    @GetMapping("/get/homepageData/infraredData")
    public Map<String, Object> getInfraredData() {
        String sql = "select WaterSoak from TWSR a where receiveTime = (select max(receiveTime) from twsr where sensorId=a.sensorId) and a.WaterSoak is not null ORDER BY a.receiveTime";
        String sql1 = "select Smoke from TSMK a where receiveTime = (select max(receiveTime) from tsmk where sensorId=a.sensorId) and a.Smoke is not null ORDER BY a.receiveTime";
        String sql2 = "select InfraredEntry from TIEM a where receiveTime = (select max(receiveTime) from tiem where sensorId=a.sensorId) and a.InfraredEntry is not null ORDER BY a.receiveTime";
        String sql3 = "select Flames from TOFD a where receiveTime = (select max(receiveTime) from tofd where sensorId=a.sensorId) and a.Flames is not null ORDER BY a.receiveTime";
        String sql4 = "select Door,sensorId from TGTM a where receiveTime = (select max(receiveTime) from tgtm where sensorId=a.sensorId) and a.Door is not null ORDER BY a.receiveTime";

        List<Map<String, Object>> list = queryList(sql);
        List<Map<String, Object>> list1 = queryList(sql1);
        List<Map<String, Object>> list2 = queryList(sql2);
        List<Map<String, Object>> list3 = queryList(sql3);
        List<Map<String, Object>> list4 = queryList(sql4);
        Map map = new HashMap();
        map.put("shuijin",list);
        map.put("yangan",list1);
        map.put("hongwai",list2);
        map.put("minghuo",list3);
        map.put("menjin",list4);

       // myWebSocket.sendInfo(infrared);
        return map;
    }

    //主页告警时间倒序
    @GetMapping("/get/homepageData/warningData")
    public List<Map<String, Object>> getWarningData() throws Exception{
        String sql = "select receiveTime,description,eventId,channel from video order by receiveTime desc limit 5";
        List<Map<String, Object>> list = queryList(sql);
        return list;
    }


    //设备数量统计
    @GetMapping("/get/homepageData/deviceCount")
    public Map<String, Object> getDeviceCount() throws Exception{
        //设备类型数量统计
        String sql = "select typename,count(DISTINCT deviceName) count from sensor_device group by typename";
        List<Map<String, Object>> list = queryList(sql);
        //所有设备
        String count = "select count(DISTINCT deviceName) count from sensor_device";
        List<Map<String, Object>> countlist = queryList(count);
        Long all = Long.parseLong(String.valueOf(countlist.get(0).get("count")));
        //异常设备数量
        String sql1 = "select count(DISTINCT warningdevice) count from warningInfo_sensor";
        List<Map<String, Object>> list1 = queryList(sql1);
        Long exceptional = Long.valueOf(String.valueOf(list1.get(0).get("count")));
        //所有告警数量
        String warning = "select count(1) count from warningInfo_sensor";
        List<Map<String, Object>> warninglist = queryList(warning);
        Long warningcount = Long.valueOf(String.valueOf(warninglist.get(0).get("count")));
        Long nalmal = all-exceptional;
        Map map = new HashMap();
        map.put("chartData",list);//环形图数据
        map.put("nalmalCount",nalmal);//正常设备数量
        map.put("exceptional",exceptional);//异常设备数量
        map.put("warningcount",warningcount);//告警数
        return map;
    }



    //获取传感器数据，为空自动补全
    @GetMapping("/get/{deviceId}")
    public List<Map<String, Object>> getSensorData(@PathVariable String deviceId) {
        Object tableName = findTableName.getTableName(deviceId);
        String sq = "select attribute from attribute_sensor where sstable = '"+tableName+"'";

        List<Map<String, Object>> li = jdbcTemplate.queryForList(sq);
        String body = "";
        for (int i = 0; i < li.size(); i++) {
            Object colum = li.get(i).get("attribute");
           // System.out.println(colum);
            if(String.valueOf(colum).contains("-")){
                colum="`"+colum+"`";
            }
            //自动补全没有数据的行，使用最近日期的数据
            body+="IFNULL(CASE \n" +
                    "WHEN (ISNULL("+colum+")=1)||(LENGTH(trim("+colum+"))=0)  \n" +
                    "THEN (select "+colum+"  from "+tableName+"  as  child  where  child.id<parent.id  and  child."+colum+"  <>  ''  order  by    child.id  desc  LIMIT  1)\n" +
                    "ELSE "+colum+"\n" +
                    "END,0) as "+colum+",";
        };
        String caseWhen = "select  \n" +body.substring(0,body.length()-1)+" from "+tableName+" as  parent where sensorId='"+deviceId+"' order by receiveTime desc limit 1";

        List<Map<String, Object>> sensorData = jdbcTemplate.queryForList(caseWhen);
        return sensorData;
    }

    public  List<Map<String, Object>> queryList(String sql){
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

}
