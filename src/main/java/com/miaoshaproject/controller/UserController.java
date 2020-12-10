package com.miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobject.BaseController;
import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private StringRedisTemplate strRedis;

    @Autowired
    private RedisOperator redis;

    //用户登录接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telphone)||
                StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户登录服务，查看用户登录是否合法
        UserModel userModel = userService.validateLogin(telphone, this.EncodeByMd5(password));

        //将登录凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);
    }


    //用户注册接口
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone")String telphone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name="age")Integer age,
                                     @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
         //验证手机号和对应的otp符合
//         String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
         String inSessionOtpCode = redis.get(telphone);
         if(!com.alibaba.druid.util.StringUtils.equals(otpCode, inSessionOtpCode)){
             throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");
         }

         //用户的注册流程
         UserModel userModel = new UserModel();
         userModel.setName(name);
         userModel.setGender(new Byte(String.valueOf(gender.intValue())));
         userModel.setAge(age);
         userModel.setTelphone(telphone);
         userModel.setRegisterMode("byphone");
         userModel.setEncrptPassword(this.EncodeByMd5(password));

         userService.register(userModel);
         return CommonReturnType.create(null);
    }

    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));

        return newStr;
    }



    //用户获取otp短信接口
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone) {
        //需要按照一定@RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
        //@ResponseBody的规则生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将otp验证码通过短信发送给用户，省略

        redis.set(telphone,otpCode);

        System.out.println("telphone = " + telphone + " & otpCode = "+redis.get(telphone));

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
