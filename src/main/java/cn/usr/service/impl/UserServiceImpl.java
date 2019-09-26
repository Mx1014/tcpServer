package cn.usr.service.impl;

import cn.usr.dao.DeviceDao;
import cn.usr.dao.UserDao;
import cn.usr.entity.User;
import cn.usr.service.UserService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static cn.usr.config.HandlerNameConstants.USER_CACHE_HASH_KEY;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;


    @Autowired
    DeviceDao deviceDao;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public User getUserInfoByuId(Integer uid) {

        User user = getUserByIdForCache(uid);
        if (user == null)
            user = getUserById(uid);
        return user;
    }

    @Override
    public User getUserById(Integer uid) {
        return userDao.queryUserById(uid);
    }

    @Override
    public User getUserByIdForCache(Integer uid) {
        if (uid == null) {
            log.error("[用户ID为空]");
            return null;
        }
        Object obj = stringRedisTemplate.opsForHash().get(USER_CACHE_HASH_KEY, uid.toString());
        if (obj != null)
            return JSONObject.parseObject(obj.toString(), User.class);
        return null;
    }

    @Override
    public boolean putUserByIdForCache(Integer uid, User user) {
        return stringRedisTemplate.opsForHash().putIfAbsent(USER_CACHE_HASH_KEY, uid.toString(), JSONObject.toJSONString(user));
    }


}
