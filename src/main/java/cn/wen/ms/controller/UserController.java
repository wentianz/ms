package cn.wen.ms.controller;

import cn.wen.ms.controller.viewobject.UserVO;
import cn.wen.ms.error.BusinessException;
import cn.wen.ms.error.EmBusinessError;
import cn.wen.ms.response.CommonReturnType;
import cn.wen.ms.service.UserService;
import cn.wen.ms.service.model.UserModel;
import io.netty.handler.codec.base64.Base64Encoder;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;
import sun.plugin2.message.Message;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class UserController extends BaseController{
    @Autowired
    UserService userservice;

    @Autowired
    RedisTemplate redisTemplate;
    //用戶獲取otp短信接口

    @Autowired
    HttpServletRequest httpServletRequest;
    @PostMapping("/login")
    public CommonReturnType register(@RequestParam(name="telphone")String telPhone,
                                     @RequestParam(name="password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if(telPhone.isEmpty()||password.isEmpty()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //校验用户登录是否合法
        UserModel userModel = userservice.validateLogin(telPhone, this.EncodeByMd5(password));
        //将用户登录凭证传入用户登录的session中
        this.httpServletRequest.getSession().setAttribute("LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("USER",userModel);
        return CommonReturnType.create(null);
    }
    @PostMapping("/register")
    public CommonReturnType register(@RequestParam(name="telphone")String telPhone,
                                     @RequestParam(name="otpCode")String otpCode,
                                     @RequestParam(name="password")String password,
                                     @RequestParam(name="name")String name,
                                     @RequestParam(name="gender")Integer gender,
                                     @RequestParam(name="age")Integer age) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpCode是否一样
        String  inCode = (String) redisTemplate.opsForValue().get(telPhone.toString());
        if(!com.alibaba.druid.util.StringUtils.equals(inCode,otpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");
        }
        //用户注册流程
        UserModel userModel = new UserModel();
        userModel.setAge(age);
        userModel.setGender(gender.byteValue());
        userModel.setName(name);
        userModel.setTelphone(telPhone);
        userModel.setRegisterMode("byPhone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userservice.register(userModel);
        return CommonReturnType.create("注册成功 ");
    }
    @PostMapping("/getotp")
    public  CommonReturnType getOpt(@RequestParam(name="telphone")String telPhone){
        //按槼則生成opt验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt+=10000;
        String optCode=String.valueOf(randomInt);
        //将opt验证码同用户手机号关联  使用redis最合适
        redisTemplate.opsForValue().set(telPhone.toString(),optCode);
        redisTemplate.expire(telPhone,600, TimeUnit.SECONDS);
        //将otp验证码通过短信通道发送给用户省略
        System.out.println("telphont="+telPhone+"otpCode="+optCode);
        return  CommonReturnType.create(null);
    }


    @RequestMapping("/getuser")
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException {
        UserModel userModel = userservice.getUserById(id);
        //若获取的对应用户信息不存在
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        //返回可供客户端UI使用的viewobject
        UserVO userVO = convertFromModel(userModel);
        //返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return  userVO;
    }

    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定加密方式
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String encode = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return encode;
    }

}
