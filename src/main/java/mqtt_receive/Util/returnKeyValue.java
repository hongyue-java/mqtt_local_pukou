package mqtt_receive.Util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class returnKeyValue {

    public void back(String filename) throws  Exception{
        String path = returnKeyValue.class.getClassLoader().getResource(filename+".json").getPath();
        String s = readJson.readJsonFile(path);
        JSONObject jobj = JSON.parseObject(s);
        System.out.println("name"+jobj.get("name"));
        JSONObject address1 = jobj.getJSONObject("address");
        String street = (String) address1.get("street");
        String city = (String) address1.get("city");
        String country = (String) address1.get("country");

        System.out.println("street :" + street);
        System.out.println("city :" + city);
        System.out.println("country :" + country);

        JSONArray links = jobj.getJSONArray("links");

        for (int i = 0 ; i < links.size();i++){
            JSONObject key1 = (JSONObject)links.get(i);
            String name = (String)key1.get("name");
            String url = (String)key1.get("url");
            System.out.println(name);
            System.out.println(url);
        }
    }
}
