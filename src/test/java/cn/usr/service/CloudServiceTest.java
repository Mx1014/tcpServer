package cn.usr.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Package: cn.usr.service
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-12-21 14:31
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudServiceTest {


    @Autowired
    CloudService cloudService;

    String testdata = "123";

    @Test
    public void sendDeviceDataToRabbitMQ() {
        cloudService.sendDeviceDataToRabbitMQ("00008318000000000037", testdata.getBytes());
    }

    @Test
    public void sendDeviceOfflineStateToRabbitMQ() {
    }

    @Test
    public void sendDeviceOnlineStateToRabbitMQ() {
    }
}