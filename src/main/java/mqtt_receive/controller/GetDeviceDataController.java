package mqtt_receive.controller;

import mqtt_receive.Util.Pagination;
import mqtt_receive.Util.findTableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials="true",maxAge = 3600)
//@RequestMapping("/test")
public class GetDeviceDataController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private findTableName findTableName;


    @Autowired
    Pagination pagination;

    /*下拉框1*/
    @GetMapping("/get/deviceData/select1")
    public  List<Map<String, Object>> getSelect1Data() throws Exception {
        String sql = "select distinct deviceName from sensor_device";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    /*下拉框2*/
    @GetMapping("/get/deviceData/select2/{deviceName}")
    public  List<Map<String, Object>> getSelect2Data(@PathVariable String deviceName) throws Exception {
        String sql = "select distinct sensortype from sensor_table where sensorno in(select sbid from sensor_device where deviceName='"+deviceName+"')";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    /*下拉框3*/
    @GetMapping("/get/deviceData/select3/{deviceName}/{sensortype}")
    public  List getSelect3Data(@PathVariable String deviceName,@PathVariable String sensortype) throws Exception {
        String sql = "select devicename,tablename,sensorno from sensor_table where sensorno in(select sbid from sensor_device where deviceName='"+deviceName+"') and sensortype ='"+sensortype+"'";
        //System.out.println(sql);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List l = new ArrayList();
        for(int i=0;i<list.size();i++){
            String s = String.valueOf(list.get(i).get("devicename"));
            String t = String.valueOf(list.get(i).get("tablename"));
            String sen = String.valueOf(list.get(i).get("sensorno"));
            Map map = new HashMap();
            map.put("sensorname",s);
            map.put("tablename",t);
            map.put("sensorno",sen);
            l.add(map);
        }

        return l;
    }

    /*下拉框4*/
    @GetMapping("/get/deviceData/select4/{tablename}")
    public List<Map<String, Object>> getAttributename(@PathVariable String tablename) throws Exception {
       String sql = "select chineseName,attribute from attribute_sensor where sstable='"+tablename+"'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
       return list;
    }


    //按下拉框条件搜索数据
    @GetMapping("/get/deviceData/table/{sensorno}/{beginTime}/{endTime}/{type}/{currentPage}/{numPerPage}")
    public  Object getTableData(@PathVariable String sensorno,@PathVariable String beginTime,@PathVariable String endTime,@PathVariable String type,@PathVariable Integer currentPage,@PathVariable Integer numPerPage) throws Exception {
        Object list =  getSensorData(sensorno,beginTime,endTime,type,currentPage,numPerPage);

        return list;
    }

    public Object getSensorData(String deviceId,String beginTime,String endTime,String type,Integer currentPage,Integer numPerPage) {
        //String getTableName = "select tablename from sntoname";
        Object tableName = findTableName.getTableName(deviceId);
//        String sql = "SELECT column_name FROM information_schema.columns WHERE table_name='" + tableName + "' and column_name <> 'id' " +
//                "and column_name <> 'receiveTime' and column_name <> 'sensorId' and column_name <> 'State' and column_name <> 'PowerSupplyType'" +
//                "and column_name <> 'PowerCapacity' and column_name <> 'CommunicationType' and column_name <> 'RSSI' and column_name <> 'Vendor'"+
//                "and column_name <> 'SersorId' and column_name <> 'SersorModel' and column_name <> 'SersorHWVersion' and column_name <> 'SersorSWVersion'";
        String sql = "select attribute from attribute_sensor where sstable = '"+tableName+"'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        String body = "";
        Map ret = new HashMap();
        List dgz = new ArrayList();
        if (type.equals("all")) {
            for (int i = 0; i < list.size(); i++) {
                String typet = "";
                Object colum = list.get(i).get("attribute");
                //System.out.println(colum);
                //自动补全没有数据的行，使用最近日期的数据
                if(String.valueOf(colum).contains("-")){
                    typet="`"+colum+"`";
                }else{
                    typet = String.valueOf(colum);
                }
                body += "IFNULL(CASE \n" +
                        "WHEN (ISNULL("+typet+")=1)||(LENGTH(trim("+typet+"))=0)  \n" +
                        "THEN (select "+typet+"  from "+tableName+"  as  child  where  child.id<parent.id  and  child."+typet+"  <>  ''  order  by    child.id  desc  LIMIT  1)\n" +
                        "ELSE "+typet+"\n" +
                        "END,0) as "+typet+",";
                String caseWhen = "select receiveTime," + body.substring(0, body.length() - 1) + " from " + tableName + " as  parent where sensorId='" + deviceId + "' and DATE_FORMAT(receiveTime,'%Y-%m-%d')>='" + beginTime + "' and DATE_FORMAT(receiveTime,'%Y-%m-%d')<='" + endTime + "' order by receiveTime asc";
                List<Map<String, Object>> lista = jdbcTemplate.queryForList(caseWhen);
                List lll = new ArrayList<>();
                for(int z=0;z<lista.size();z++){
                    Map map = new HashMap();
                    map=lista.get(z);
                    //System.out.println(map);
                    Object et = map.get(colum);
                    Object rt = map.get("receiveTime");
                    List t = new ArrayList();
                    t.add(String.valueOf(rt));
                    t.add(String.valueOf(et));
                    lll.add(t);
                };

                ret.put(coln(colum),lll);

            }
            dgz.add(ret);
        } else {
            String typet = "";
            if(String.valueOf(type).contains("-")){
                typet="`"+type+"`";
            }else{
                typet = type;
            }
            body += "IFNULL(CASE \n" +
                    "WHEN (ISNULL("+typet+")=1)||(LENGTH(trim("+typet+"))=0)  \n" +
                    "THEN (select "+typet+"  from "+tableName+"  as  child  where  child.id<parent.id  and  child."+typet+"  <>  ''  order  by    child.id  desc  LIMIT  1)\n" +
                    "ELSE "+typet+"\n" +
                    "END,0) as "+typet+",";
            String caseWhen = "select receiveTime," + body.substring(0, body.length() - 1) + " from " + tableName + " as  parent where sensorId='" + deviceId + "' and DATE_FORMAT(receiveTime,'%Y-%m-%d')>='" + beginTime + "' and DATE_FORMAT(receiveTime,'%Y-%m-%d')<='" + endTime + "' order by receiveTime desc";
            String caseWhen1 = "select count(*) count," + body.substring(0, body.length() - 1) + " from " + tableName + " as  parent where sensorId='" + deviceId + "' and DATE_FORMAT(receiveTime,'%Y-%m-%d')>='" + beginTime + "' and DATE_FORMAT(receiveTime,'%Y-%m-%d')<='" + endTime + "' order by receiveTime asc";
            Integer countsql = Integer.parseInt(String.valueOf(jdbcTemplate.queryForList(caseWhen1).get(0).get("count")));
            List<Map<String, Object>> sensorData = pagination.Pagination(countsql, caseWhen, currentPage, numPerPage, jdbcTemplate);
            List list2 = new ArrayList();
            for(int i=0;i<sensorData.size();i++){
                Map m = new HashMap();
                Map map1 = new HashMap();
                m=sensorData.get(i);
                //System.out.println(map);
                Object et = m.get(type);
                Object rt = m.get("receiveTime");
                map1.put("name",et);
                map1.put("time",rt);
                list2.add(map1);
            };


            Map map = new HashMap();
            map.put("count", countsql);
            map.put("datalist", list2);
           dgz.add(map);
        }

            return dgz;
    }

    public String coln(Object o){
        if(String.valueOf(o).contains("-")){
            o="`"+o+"`";
        }
        String sql = "select chineseName from attribute_sensor where attribute='"+o+"'";
        String co = String.valueOf(jdbcTemplate.queryForList(sql).get(0).get("chineseName"));
        return co;
    }

}
