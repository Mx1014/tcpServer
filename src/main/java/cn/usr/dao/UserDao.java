package cn.usr.dao;


import cn.usr.entity.User;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDao {


    /**
     * 查询用户信息根据ID
     *
     * @param uid
     * @return
     */
    User queryUserById(int uid);

}
