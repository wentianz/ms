package cn.wen.ms.service;

import cn.wen.ms.error.BusinessException;
import cn.wen.ms.service.model.UserModel;

public interface UserService {

    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;

    /**
     *
     * @param telphone
     * @param password 为用户加密后的密码
     * @throws BusinessException
     */
    UserModel validateLogin(String telphone , String password) throws BusinessException;
}
