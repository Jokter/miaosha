package com.miaoshaproject;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */

@SpringBootApplication(scanBasePackages = {"com.miaoshaproject"})
@RestController
@MapperScan("com.miaoshaproject.dao")
public class App {

    @Autowired
    private UserDOMapper userDoMapper;


    @RequestMapping("/")
    public String home(){
        UserDO userDo = userDoMapper.selectByPrimaryKey(1);
        if(userDo==null){
            return "用户不存在";
        }else{
            return userDo.getName();
        }
    }

    public static void main( String[] args ) {
//        System.out.println( "Hello World!" );
//        SpringApplication.run(App.class,args);

        HashMap<String, String> map = new HashMap<>();
        map.put("1", "2");
        map.put("2", "3");
        System.out.println(map);
        map.remove("1");
        System.out.println(map);

    }
}
