package cn.usr.service;

import cn.usr.entity.Device;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceServiceTest {


    @Autowired
    DeviceService deviceService;

    @Test
    public void getDeviceByDeviceId() {
        Device device = deviceService.getDeviceByDeviceId("00012391000000000004");
        System.out.println(device);
    }


    @Test
    public void getDeviceInfoByDeviceId() {
        Device device = deviceService.getDeviceInfoByDeviceId("00012391000000000004");
        System.out.println(device);
    }

    @Test
    public void getDeviceConnectionNumber() {
        String s = deviceService.getDeviceConnectionNumber("00005876000000000002");
        System.out.println(s);
    }

    @Test
    public void incrementDeviceConnectionNumber() {
        for (int i = 0; i < 5; i++) {
            deviceService.incrementDeviceConnectionNumber("00005876000000000002");
        }
    }

    @Test
    public void queryTargetGroupIdsByDid() {
        List<String> strings = deviceService.queryTargetGroupIdsByDid("00008318000000000014");
        assert strings.size() != 0;
        System.out.println(strings);
    }

    @Test
    public void getTargetGroupIdsForCache() {
        List<String> strings = deviceService.getTargetGroupIdsForCache("00008318000000000014");
        System.out.println(strings);
    }

    @Test
    public void getTargetGroupDidListForCache() {
        List<String> strings = deviceService.getTargetGroupDidListForCache("00008318000000000017");
        System.out.println(strings);
    }

    @Test
    public void putTargetGroupIdsForCache() {
        Boolean aBoolean = deviceService.putTargetGroupIdsForCache("00008318000000000014",
                deviceService.queryTargetGroupIdsByDid("00008318000000000014"));
        System.out.println(aBoolean);
    }

    @Test
    public void putTargetGroupDidListForCache() {
        List<String> targetGroupDidListForCache = deviceService.getTargetGroupDidListForCache("00008318000000000014");

//        Boolean aBoolean = deviceService.putTargetGroupDidListForCache("00008318000000000014", );
//        System.out.println(aBoolean);
    }


}