package cn.usr.service;

import cn.usr.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {


    @Autowired
    UserService userService;

    @Test
    public void getUserInfoByuId() {
        User user = userService.getUserInfoByuId(8318);
        System.out.println(user);
    }

    @Test
    public void getUserById() {
    }

    @Test
    public void getUserByIdForCache() {
    }

    @Test
    public void putUserByIdForCache() {
    }
}