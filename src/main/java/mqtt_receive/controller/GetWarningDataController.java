package mqtt_receive.controller;

import mqtt_receive.Util.Pagination;
import mqtt_receive.Util.findTableName;
import mqtt_receive.Util.vue_springboot_websocket.websocketUtil.MyWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials="true",maxAge = 3600)
//@RequestMapping("/test")
public class GetWarningDataController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private findTableName findTableName;

    @Autowired
    private MyWebSocket myWebSocket;

    Pagination pagination = new Pagination();

    //List<Map<String, Object>> list = pagination.Pagination(sql,currentPage,numPerPage,jdbcTemplate);


    /*下拉框1*/
    @GetMapping("/get/warningData/select1")
    public  List<Map<String, Object>> getSelect1Data() throws Exception {
        String sql = "select distinct warningtype from warninginfo_sensor";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    /*下拉框2*/
    @GetMapping("/get/warningData/select2/{warningtype}")
    public  List<Map<String, Object>> getSelect2Data(@PathVariable String warningtype) throws Exception {
        String sql ="";
        if(warningtype.equals("视频告警")){
           sql="select distinct  channel from video";
       }else{
            sql= "select distinct warningdevice from warninginfo_sensor where warningtype ='"+warningtype+"'";

        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

   /* *//*下拉框3*//*
    @GetMapping("/get/warnigData/select3/{deviceName}/{sensortype}")
    public  Map getSelect3Data(@PathVariable String deviceName,@PathVariable String sensortype) throws Exception {
        String sql = "select devicename,tablename,sensorno from sntoname where sensorno in(select sbid from devicetosensor where deviceName='"+deviceName+"') and sensortype ='"+sensortype+"'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        String s = String.valueOf(list.get(0).get("devicename"));
        String devicename = s.split("_")[1];
        String t = String.valueOf(list.get(0).get("tablename"));
        String sen = String.valueOf(list.get(0).get("sensorno"));
        Map map = new HashMap();
        map.put("sensorname",s);
        map.put("devicename",devicename);
        map.put("tablename",t);
        map.put("sensorno",sen);
        return map;
    }
*/
   //跳转告警
   @GetMapping("/jump/{typename}/{currentPage}/{numPerPage}")
   public Map<String,Object> jumpToWarning(@PathVariable String typename,@PathVariable Integer currentPage,@PathVariable Integer numPerPage) throws Exception{
       String sql = "select * from warninginfo_sensor  where warningdevicetype='"+typename+"'";
        String sql1 ="select count(*) count from warninginfo_sensor where warningdevicetype='"+typename+"'";
       Integer countsql = Integer.parseInt(String.valueOf(jdbcTemplate.queryForList(sql1).get(0).get("count")));
       List<Map<String, Object>> list =  pagination.Pagination(countsql,sql,currentPage,numPerPage,jdbcTemplate);
       Map map = new HashMap();
       map.put("count",countsql);
       map.put("datalist",list);
       return map;
   }

    //按下拉框条件搜索数据
    @GetMapping("/get/warningData/table/{warningtype}/{warningdevice}/{ifview}/{beginTime}/{endTime}/{currentPage}/{numPerPage}")
    public  Map<String, Object> getTableData(@PathVariable String warningtype,@PathVariable String warningdevice,@PathVariable Integer ifview,@PathVariable String beginTime,@PathVariable String endTime,@PathVariable Integer currentPage,@PathVariable Integer numPerPage) throws Exception {
        String sql="";
        String sql1="";
        if(warningtype.equals("视频告警")){
            sql = "select receiveTime,description,eventId from video where DATE_FORMAT(receiveTime,'%Y-%m-%d')>='"+beginTime+"' and DATE_FORMAT(receiveTime,'%Y-%m-%d')<='"+endTime+"' and channel = '"+warningdevice+"' and ifview="+ifview+" and eventId is not null order by receiveTime desc";
            sql1 ="select count(*) count from video where DATE_FORMAT(receiveTime,'%Y-%m-%d')>='"+beginTime+"' and DATE_FORMAT(receiveTime,'%Y-%m-%d')<='"+endTime+"' and channel = '"+warningdevice+"' and ifview="+ifview +" and eventId is not null";
        }else{
            sql ="select * from warninginfo_sensor where warningtype='"+warningtype+"' and warningdevice='"+warningdevice+"' and ifview="+ifview+" and DATE_FORMAT(warningtime,'%Y-%m-%d')>='"+beginTime+"' and DATE_FORMAT(warningtime,'%Y-%m-%d')<='"+endTime+"' order by warningtime desc";
            sql1 ="select *,count(*) count from warninginfo_sensor where warningtype='"+warningtype+"' and warningdevice='"+warningdevice+"' and ifview="+ifview+" and DATE_FORMAT(warningtime,'%Y-%m-%d')>='"+beginTime+"' and DATE_FORMAT(warningtime,'%Y-%m-%d')<='"+endTime+"' order by warningtime asc";

        }
       // System.out.println("000099"+sql);
       //  System.out.println("888"+sql1);
        Integer countsql = Integer.parseInt(String.valueOf(jdbcTemplate.queryForList(sql1).get(0).get("count")));
        //System.out.println(countsql);
        List<Map<String, Object>> list =  pagination.Pagination(countsql,sql,currentPage,numPerPage,jdbcTemplate);
        Map map = new HashMap();
        map.put("count",countsql);
        map.put("datalist",list);
        return map;
    }

    //按周搜索告警数据左
    @GetMapping("/get/warningData/left/{beginTime}/{endTime}")
    public  List getLeftData(@PathVariable String beginTime,@PathVariable String endTime) throws Exception {
        String view0 = "select DATE_FORMAT(warningtime,'%Y-%m-%d') warning ,count(*) count from warninginfo_sensor where DATE_FORMAT(warningtime,'%Y-%m-%d')>='"+beginTime+"' and DATE_FORMAT(warningtime,'%Y-%m-%d')<='"+endTime+"' group by warning";
        String view1 = "select DATE_FORMAT(warningtime,'%Y-%m-%d') warning ,count(*) count from warninginfo_sensor where DATE_FORMAT(warningtime,'%Y-%m-%d')>='"+beginTime+"' and DATE_FORMAT(warningtime,'%Y-%m-%d')<='"+endTime+"' and ifview = 1 group by warning";
        String videocount = "select DATE_FORMAT(receiveTime,'%Y-%m-%d') warning,count(*) count from video where DATE_FORMAT(receiveTime,'%Y-%m-%d')>='"+beginTime+"' and DATE_FORMAT(receiveTime,'%Y-%m-%d')<='"+endTime+"' group by warning";

        List<Map<String, Object>> list1 = jdbcTemplate.queryForList(view0);
        List<Map<String, Object>> list2 = jdbcTemplate.queryForList(view1);
        List<Map<String, Object>> list3 = jdbcTemplate.queryForList(videocount);
        //Map map = new HashMap();
        List lll = new ArrayList<>();
        List llll = new ArrayList<>();

        List ll = new ArrayList<>();
        Map mma = new HashMap();
       // Object videoc = list3.get(0).get("count");
      //  Integer i = Integer.parseInt(videoc.toString());
        for(int z=0;z<list3.size();z++){
            Map map = new HashMap();
            map=list3.get(z);
            //System.out.println(map);
            Object wt = map.get("warning");
            Object wc = map.get("count");
           // System.out.println(wc);
            Integer icount = Integer.parseInt(wc.toString());
            for(int x=0;x<list1.size();x++){
                Object xw = list1.get(x).get("warning");
                if(String.valueOf(xw).equals(String.valueOf(wt))){
                    icount+=Integer.parseInt(list1.get(x).get("count").toString());
                }
            }
            List t = new ArrayList();
           // System.out.println(String.valueOf(wt)+"-----------"+String.valueOf(icount));
            t.add(String.valueOf(wt));
            t.add(String.valueOf(icount));
            lll.add(t);
        };
        mma.put("all",lll);
       // System.out.println(mma);

        for(int z=0;z<list2.size();z++){
            Map map = new HashMap();
            map=list2.get(z);
            //System.out.println(map);
            Object wt = map.get("warning");
            Object wc = map.get("count");
            List t = new ArrayList();
            t.add(String.valueOf(wt));
            t.add(String.valueOf(wc));
            llll.add(t);
        };
        mma.put("view",llll);
        ll.add(mma);
       // ll.add(lll);


       // map.put("all",list1);
       // map.put("view",list2);
        return ll;
    }

    //按周搜索告警数据右
    @GetMapping("/get/warningData/right/{beginTime}/{endTime}")
    public List getRightData(@PathVariable String beginTime,@PathVariable String endTime) throws Exception {
        String view0 = "select warningtype,count(*) count from warninginfo_sensor where DATE_FORMAT(warningtime,'%Y-%m-%d')>='"+beginTime+"' and DATE_FORMAT(warningtime,'%Y-%m-%d')<='"+endTime+"' group by warningtype";
        String wt = "select DISTINCT warningtype, 0 as count from attribute_sensor where warningtype not in (select DISTINCT warningtype from warninginfo_sensor where DATE_FORMAT(warningtime,'%Y-%m-%d')>='"+beginTime+"' and DATE_FORMAT(warningtime,'%Y-%m-%d')<='"+endTime+"' group by warningtype)";
        String videocount = "select '视频告警' as warningtype,count(*) count from video where DATE_FORMAT(receiveTime,'%Y-%m-%d')>='"+beginTime+"' and DATE_FORMAT(receiveTime,'%Y-%m-%d')<='"+endTime+"'";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(view0);
        List<Map<String, Object>> list1 = jdbcTemplate.queryForList(wt);
        List<Map<String, Object>> list2 = jdbcTemplate.queryForList(videocount);
        List ll = new ArrayList();
        addlist(list,ll);
        addlist(list1,ll);
        addlist(list2,ll);
       /* Map m = new HashMap();
        m.put("count",list);
        m.put("warningtype",list1);*/
        //List ll = new ArrayList();
        //ll.add(m);
        return ll;
    }

    //修改告警状态
    @PutMapping("/put/warningData/{warningtype}/{warningdevice}/{warningtime}")
    public Integer putWarningData(@PathVariable String warningtype,@PathVariable String warningdevice,@PathVariable String warningtime) throws Exception {
        String updateData = "update warninginfo_sensor set ifview = 1 where warningtype='"+warningtype+"' and warningdevice='"+warningdevice+"' and warningtime='"+warningtime+"'";
        Integer i =jdbcTemplate.update(updateData);
        return i;
    }

    public void addlist(List list,List l){
        for(int i = 0;i<list.size();i++){
            l.add(list.get(i));
        }

    }

    @RequestMapping("/getImage/{eventId}")
    @ResponseBody
    public void getImagesId(HttpServletResponse rp, @PathVariable String  eventId) {
        String filePath = "D:\\mqtt_imag\\"+eventId+".jpg";
        File imageFile = new File(filePath);

        if (imageFile.exists()) {
            FileInputStream fis = null;
            OutputStream os = null;
            try {
                fis = new FileInputStream(imageFile);
                os = rp.getOutputStream();
                int count = 0;
                byte[] buffer = new byte[1024 * 8];
                while ((count = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                    os.flush();
                }

                //System.out.println("8888");

            } catch (Exception e) {
                // System.out.println("0000");

                e.printStackTrace();

            } finally {
                try {
                    fis.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            // return 200;
        }
    }


    @RequestMapping("/checkImage/{eventId}")
    @ResponseBody
    public int checkImage(@PathVariable String  eventId) {
        String filePath = "D:\\mqtt_imag\\"+eventId+".jpg";
        File imageFile = new File(filePath);

        if (imageFile.exists()) {

            return 200;
        }else{
            return 500;
        }
    }

}
