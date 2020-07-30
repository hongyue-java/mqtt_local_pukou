package mqtt_receive.mqtt_receiveMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import mqtt_receive.Util.findTableName;
import mqtt_receive.Util.vue_springboot_websocket.websocketUtil.MyWebSocket;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.OutputStream;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * 〈一句话功能简述〉<br>
 * 〈MQTT接收消息处理〉
 *
 * @author lenovo
 * @create 2018/6/4
 * @since 1.0.0
 */
@Configuration
@IntegrationComponentScan
public class MqttReceiveConfig {

    @Value("${spring.mqtt.username}")
    private String username;

    @Value("${spring.mqtt.password}")
    private String password;

    @Value("${spring.mqtt.url}")
    private String hostUrl;

    @Value("receiveId")
    private String clientId;

    @Value("${spring.mqtt.default.topic}")
    private String defaultTopic;

    @Value("${spring.mqtt.completionTimeout}")
    private int completionTimeout ;   //连接超时

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private findTableName findTableName;

    @Autowired
    private MyWebSocket myWebSocket;

    @Autowired
    private mqtt_receive.Util.warning warning;

    @Bean
    public MqttConnectOptions getMqttConnectOptions(){
        MqttConnectOptions mqttConnectOptions=new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{hostUrl});
        mqttConnectOptions.setKeepAliveInterval(2);
        return mqttConnectOptions;
    }
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttConnectOptions());
        return factory;
    }

    //接收通道
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    //配置client,监听的topic
    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId+"_inbound", mqttClientFactory(),
                        "localdisplay/notify/remotedisplay/format/+","+/notify/event/database/+/+","/sys/+/thing/event","/sys/+/thing/event_media");
        adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

   /* @Autowired
    private StringRedisTemplate stringRedisTemplate;*/
    //通过通道获取数据
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
                String JsonStr =message.getPayload().toString();
                String type = "";
                String tableName = "";
                System.out.println("*********"+JsonStr+"*******");
               // System.out.println("***********************"+topic);
                if(topic.contains("VIDEO")){
                    type = topic.substring(topic.lastIndexOf("/")+1, topic.length());
                    String channel = type.substring(type.length()-3,type.length());
                    tableName = "video";
                    //String JsonStr =message.getPayload().toString();
                    JSONObject jsonObject = JSON.parseObject(JsonStr);
                    String jsonTimeStamp = jsonObject.getString("timestamp");
                    String UTOG = utcToLocal(jsonTimeStamp);
                    String jsonBody = jsonObject.getString("body");
                    JSONArray jsonArray=JSON.parseArray(jsonBody);
                    //String cc = jsonArray.getJSONObject(0).getString("name");

                    String sql = "receiveTime,channel";
                    String values = "'"+UTOG+"','"+channel+"号通道'";
                    // String buffer = "";
                    String valuer = "";
                    String events = "";
                   // List keylist = new ArrayList();
                    for(int i=0;i<jsonArray.size();i++){
                        JSONObject    jsonObject1 =  jsonArray.getJSONObject(i) ;
                        String colum = jsonObject1.getString("name");
                        String val = jsonObject1.getString("val");
                        //String colum = name.substring(0,name.length()-2);
                        if(colum.equals("channelStatus")){
                            sql+=","+colum;
                            values+=",'"+val+"'";
                        }else{
                            if(val.equals("1")||val=="1"){
                                String behavior = getDescription(colum);
                                valuer+=behavior+",";

                            }if(!val.equals("1")&&val!="1"){
                                events+="|"+colum+","+val;
                            }
                            sql+=","+colum;
                            values+=",'"+val+"'";
                        }
                    }
                    if(valuer!=""&&!valuer.equals("")){
                        sql+=",description";
                        values+=",'"+valuer.substring(0,valuer.length()-1)+"'";
                    }if(events!=""&&!events.equals("")){
                        sql+=",eventId";
                        values+=",'"+events.substring(1,events.length())+"'";
                    }
                    try {
                        if(!sql.contains("channelStatus")){
                            turnSql(tableName,sql,values);
                            myWebSocket.sendInfo("video");
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                else {
                    type = topic.substring(topic.lastIndexOf("/")+1, topic.length());
                   // System.out.println("sensorid--------------"+type);
                    tableName = topic.split("/")[4];
                    System.out.println(tableName);
                    //String JsonStr =message.getPayload().toString();
                    JSONObject jsonObject = JSON.parseObject(JsonStr);
                    String jsonTimeStamp = jsonObject.getString("timestamp");
                    String UTOG = utcToLocal(jsonTimeStamp);
                    String jsonBody = jsonObject.getString("body");
                    JSONArray jsonArray=JSON.parseArray(jsonBody);
                    String colums = "receiveTime,sensorId";
                    String values = "'"+UTOG+"','"+type+"'";
                    List li = new ArrayList();
                    li.add("thum");
                    li.add("ttmp");
                    li.add("tlea");
                    li.add("tule");
                    li.add("tsmk");
                    li.add("tiem");
                    li.add("twsr");
                    List l1 = new ArrayList();
                    List l2 = new ArrayList();
                    //10-17修改
                    List stateList = new ArrayList();


                    for(int i=0;i<jsonArray.size();i++){
                        JSONObject    jsonObject1 =  jsonArray.getJSONObject(i) ;
                        String key = jsonObject1.getString("name");
                        String val = jsonObject1.getString("val");
                        stateList.add(key);
                        Map war = new HashMap();
                        if(key.contains("-")){
                            String key1="`"+key+"`";
                            colums+=","+key1;
                        }else{
                            colums+=","+key;
                        }
//                        colums+=","+key;
                        values+=",'"+val+"'";

                        if(li.contains(tableName.toLowerCase())&&!key.equals("State")&&!key.equals("PowerSupplyType")&&!key.equals("PowerCapacity")&&!key.equals("CommunicationType")&&!key.equals("RSSI")&&!key.equals("Vendor")&&!key.equals("SersorId")&&!key.equals("SersorModel")&&!key.equals("SersorHWVersion")&&!key.equals("SersorSWVersion")){
                            warning.warningDetail(type,key,val,UTOG);
                        }
                    }
                    try {

                        // MyWebSocket myWebSocket = new MyWebSocket();
                        List redinfo = new ArrayList();
                        redinfo.add("tsmk");
                        redinfo.add("tiem");
                        redinfo.add("twsr");
                        redinfo.add("tgtm");
                        redinfo.add("tofd");

                        List sensorinfo = new ArrayList();
                        sensorinfo.add("thum");
                        sensorinfo.add("tnsp");
                        sensorinfo.add("ttmp");
                        sensorinfo.add("tule");
                        sensorinfo.add("tsfg");

                        /*if(!redinfo.contains(tableName.toLowerCase())&&!stateList.contains("State")){
                            turnSql(tableName,colums,values);
                            //myWebSocket.sendInfo("charts");
                        }*/
                        if(sensorinfo.contains(tableName.toLowerCase())){
                            turnSql(tableName,colums,values);
                            //myWebSocket.sendInfo("charts");
                        }else if(redinfo.contains(tableName.toLowerCase())&&!stateList.contains("PowerCapacity")){
                            System.out.println(tableName);
                            System.out.println(colums);
                            System.out.println(values);
                            turnSql(tableName,colums,values);
                            myWebSocket.sendInfo("nocharts");
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /**
     *
     * <p>Description:UTC秒数转化为北京时间 </p>
     * @param utcTime
     * @return
     * @author hy
     *
     */
    public static String utcToLocal(String utcTime) {
        ZonedDateTime zdt  = ZonedDateTime.parse(utcTime);
        LocalDateTime localDateTime = zdt.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // System.out.println("北京时间："+formatter.format(localDateTime.plusHours(8)));
        return formatter.format(localDateTime.plusHours(0));
    }


    //执行插入sql操作的方法
    public  void turnSql(String tablename,String sql,String values){
        String insert = "insert into "+ tablename+" ("+sql+") values ("+values+")";
        //System.out.println(insert);
        jdbcTemplate.update(insert);
    }

    public String getDescription(String key) {
        String sql = "select description from warning where behavior = '"+key+"'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        String behavior = (String)list.get(0).get("description");
        return behavior;
    }

    //base64处理图片
    public void savePic(String paramStr) throws Exception {
        String base64Pic = "";
        String picName = "";
        //String picName = "";
        JSONObject jsonObject = (JSONObject) JSON.parse(paramStr);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        base64Pic = jsonObject.getString("base64Data");
        picName = jsonObject.getString("eventId");
        //picName = jsonObject.getString("picName");
        if (base64Pic == null) { // 图像数据为空
            resultMap.put("resultCode", 0);
            resultMap.put("msg", "图片为空");
        } else {
            BASE64Decoder decoder = new BASE64Decoder();
            String baseValue = base64Pic.replaceAll(" ", "+");//前台在用Ajax传base64值的时候会把base64中的+换成空格，所以需要替换回来。
            byte[] b = decoder.decodeBuffer(baseValue.replace("data:image/jpeg;base64,", ""));//去除base64中无用的部分
            base64Pic = base64Pic.replace("base64,", "");
           // System.out.println(b);
            String imgFilePath = "D:\\mqtt_imag\\"+picName+".jpg";
            try {
                for (int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {// 调整异常数据
                        b[i] += 256;
                    }
                };
                // 生成jpeg图片
                OutputStream out = new FileOutputStream(imgFilePath);
                out.write(b);
                out.flush();
                out.close();
                resultMap.put("resultCode", 1);
                resultMap.put("msg", "存储成功");
            } catch (Exception e) {
                resultMap.put("resultCode", 0);
                resultMap.put("msg", "存储异常");
            }

        }
        //System.out.println(resultMap);
        //return resultMap;
    }
}

