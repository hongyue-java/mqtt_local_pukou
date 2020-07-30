package mqtt_receive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials="true",maxAge = 3600)
//@RequestMapping("/test")
public class GetDebugDataController {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /*监测数据*/
    @GetMapping("/get/debugData")
    public List<Map<String, Object>> getDebugData() throws Exception{
        String sql = "select * from debug_data where receiveTime=(select max(receiveTime) from debug_data)";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

        return list;
    }

}
