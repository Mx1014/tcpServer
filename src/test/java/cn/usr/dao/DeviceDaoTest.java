package cn.usr.dao;

import cn.usr.entity.Device;
import cn.usr.entity.Slave;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceDaoTest {


    @Autowired
    DeviceDao deviceDao;

    @Test
    public void queryDeviceByDevId() {
        Device device = deviceDao.queryDeviceByDevId("00012391000000000004");
        System.out.println(device);
    }

}