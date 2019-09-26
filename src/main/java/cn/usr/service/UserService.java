package cn.usr.service;


import cn.usr.entity.User;


/**
 * @author Administrator
 */
public interface UserService {


    /**
     * 获取用户信息
     *
     * @param uid
     * @return
     */
    User getUserInfoByuId(Integer uid);


    /**
     * 获取用户信息
     *
     * @param uid
     * @return
     */
    User getUserById(Integer uid);


    /**
     * 获取用户信息 从缓存中
     *
     * @param uid
     * @return
     */
    User getUserByIdForCache(Integer uid);


    /**
     * 存放用户信息到缓存
     *
     * @param uid
     * @param user
     * @return
     */
    boolean putUserByIdForCache(Integer uid, User user);


}
