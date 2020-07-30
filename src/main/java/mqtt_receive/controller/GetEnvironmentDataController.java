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
public class GetEnvironmentDataController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private findTableName findTableName;


    Pagination pagination = new Pagination();

    /*左侧点击筛选传感器分类*/
    @GetMapping("/get/environmentData/{type}")
    public List getDeviceName(@PathVariable String type) throws Exception{
        String sq = "select devicename,sensorno from sensor_table where tablename in (select sstable from attribute_sensor where attribute='"+type+"')";
        System.out.println(sq);
        //查询列所数传感器sn
        List<Map<String, Object>> snList =jdbcTemplate.queryForList(sq);
        List l = new ArrayList();
        for(int i = 0;i<snList.size();i++){
            String sn = String.valueOf(snList.get(i).get("sensorno"));
            String devicename = String.valueOf(snList.get(i).get("devicename"));

            Map<String,Object> map=new HashMap<>();
            map.put("sensorname",devicename);
            map.put("sn",sn);
            l.add(map);
        }
        return l;
    }

    /*获取不同传感器折线图监测数据*/
    @GetMapping("/get/environmentData/{type}/{ymd}/{sn}")
    public Object getLineData(@PathVariable String type,@PathVariable String ymd,@PathVariable String sn) throws Exception{
        //查询列所数传感器sn
        List<Map<String, Object>> snList = findTableName.getSensorId(type);
        List dgz = new ArrayList();
        //System.out.println(snList.size());
        Map ma = new HashMap();
        //循环获取不同传感器的数据
        List lll = new ArrayList<>();
        //通过不同的sn码获取对应表数据
        Object tableName = findTableName.getTableName(sn);
        String typet = "";
        if(String.valueOf(type).contains("-")){
            typet="`"+type+"`";
        }else{
            typet = type;
        }
        String body = "IFNULL(CASE \n" +
                "WHEN (ISNULL("+typet+")=1)||(LENGTH(trim("+typet+"))=0)  \n" +
                "THEN (select "+typet+"  from "+tableName+"  as  child  where  child.id<parent.id  and  child."+typet+"  <>  ''  order  by    child.id  desc  LIMIT  1)\n" +
                "ELSE "+typet+"\n" +
                "END,0) as "+typet+",";
        String caseWhen = "select receiveTime," +body.substring(0,body.length()-1)+" from "+tableName+" as  parent where sensorId='"+sn+"' and DATE_FORMAT(receiveTime,'%Y-%m-%d') = DATE_FORMAT('"+ymd+"','%Y-%m-%d') order by receiveTime asc";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(caseWhen);
        //
        for(int z=0;z<list.size();z++){
            Map map = new HashMap();
            map=list.get(z);
            //System.out.println(map);
            Object dt = map.get(type);
            //Double et = Double.valueOf(String.valueOf(dt));
            Object rt = map.get("receiveTime");
            List t = new ArrayList();
            t.add(String.valueOf(rt));
            t.add(dt);
            lll.add(t);
        };
        dgz.add(lll);

        return dgz;
    }

    /*环境监测数据*/
    @GetMapping("/get/environmentData/{type}/{ymd}")
    public Object getLineData(@PathVariable String type,@PathVariable String ymd) throws Exception{
        //查询列所数传感器sn
        List<Map<String, Object>> snList = findTableName.getSensorId(type);

        List dgz = new ArrayList();
        //System.out.println(snList.size());
        Map ma = new HashMap();
        for(int i = 0;i<snList.size();i++){
            List lll = new ArrayList<>();
            String sn = String.valueOf(snList.get(i).get("sensorno"));
            Object tableName = findTableName.getTableName(sn);
            String typet = "";
            if(String.valueOf(type).contains("-")){
                typet="`"+type+"`";
            }else{
                typet = type;
            }
            String body = "IFNULL(CASE \n" +
                    "WHEN (ISNULL("+typet+")=1)||(LENGTH(trim("+typet+"))=0)  \n" +
                    "THEN (select "+typet+"  from "+tableName+"  as  child  where  child.id<parent.id  and  child."+typet+"  <>  ''  order  by    child.id  desc  LIMIT  1)\n" +
                    "ELSE "+typet+"\n" +
                    "END,0) as "+typet+",";
            String caseWhen = "select receiveTime," +body.substring(0,body.length()-1)+" from "+tableName+" as  parent where sensorId='"+sn+"' and DATE_FORMAT(receiveTime,'%Y-%m-%d') = DATE_FORMAT('"+ymd+"','%Y-%m-%d') order by receiveTime asc";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(caseWhen);

            for(int z=0;z<list.size();z++){
                Map map = new HashMap();
                map=list.get(z);
                //System.out.println(map);
                Object et = map.get(type);
                Object rt = map.get("receiveTime");
                List t = new ArrayList();
                t.add(String.valueOf(rt));
                t.add(String.valueOf(et));
                lll.add(t);
            };
            dgz.add(lll);
        }
        return dgz;
    }

}
