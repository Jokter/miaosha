package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.BaseController;
import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.Configuration;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户获取otp短信接口
    @RequestMapping("/getotp")
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone) {
        //需要按照一定的规则生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将otp验证码和用户手机号关联,使用httpsession的方式绑定它的手机号和otp
        httpServletRequest.getSession().setAttribute(telphone,otpCode);

        //将otp验证码通过短信发送给用户，省略
        System.out.println("telphone = " + telphone + " & otpCode = "+otpCode);

        return CommonReturnType.create(null);
    }


    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id")Integer id) throws BusinessException {
        //调用service获取服务对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        if(userModel == null) {
//            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
            userModel.setEncrptPassword("123");
        }

        UserVO userVO = convertFromModel(userModel);

        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);

        return userVO;
    }

}
