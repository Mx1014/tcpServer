package cn.usr;

import cn.usr.dto.DeviceDTO;
import cn.usr.dto.DeviceToCenterDataMessage;
import cn.usr.dto.UserDTO;
import cn.usr.util.ProtoStuffSerializerUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudTcpApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void contextLoads() {
        stringRedisTemplate.opsForValue().set("device", "1", 10, TimeUnit.SECONDS);
    }

    @Test
    public void contextLoads2() {
        Boolean device = stringRedisTemplate.hasKey("device");
        if (device == true)
            stringRedisTemplate.boundValueOps("device").increment(2);
    }

    @Test
    public void contextLoads3() {
//        stringRedisTemplate.opsForHash().put("device", "123", "1");
        Set<String> onine = stringRedisTemplate.opsForZSet().range("online_dev", 0, 9999);
        Long online_dev = stringRedisTemplate.opsForZSet().size("online_dev");
        System.out.println(online_dev);
        System.out.println(onine);
    }

    @Test
    public void contextLoads4() {
        DeviceDTO deviceDTO = new DeviceDTO("00008318000000000135",
                "未命名_设备名称_57", 8318, 2, 0);

        UserDTO userDTO = new UserDTO("rock", 8318, null, null);

        String data = "fkladsfjalsdgjfakldsgjlkdfshgdfshglsdfhglkjsdfhjgksdfgjkhsghsrfdjgjhjkdfsgjksdhfgjhsdfgjgh" +
                "fkladsfjalsdgjfakldsgjlkdfshgdfshglsdfhglkjsdfhjgksdfgjkhsghsrfdjgjhjkdfsgjksdhfgjhsdfgjgh" +
                "fkladsfjalsdgjfakldsgjlkdfshgdfshglsdfhglkjsdfhjgksdfgjkhsghsrfdjgjhjkdfsgjksdhfgjhsdfgjgh" +
                "fkladsfjalsdgjfakldsgjlkdfshgdfshglsdfhglkjsdfhjgksdfgjkhsghsrfdjgjhjkdfsgjksdhfgjhsdfgjgh";
        // 组装message
        DeviceToCenterDataMessage toCenterDataMessage = new DeviceToCenterDataMessage(data.getBytes(), deviceDTO, userDTO, System.currentTimeMillis());


        while (true) {

            rabbitTemplate.convertAndSend("exchange_v19_v20", "to_center_data", ProtoStuffSerializerUtil.serialize(toCenterDataMessage));
//            try {
////                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

    }


    @Test
    public void contextLoads23() {

        DeviceDTO deviceDTO = new DeviceDTO("00008318000000000135",
                "未命名_设备名称_57", 8318, 2, 0);

        UserDTO userDTO = new UserDTO("rock", 8318, null, null);

        String data = "fkladsfjalsdgjfakldsgjlkdfshgdfshglsdfhglkjsdfhjgksdfgjkhsghsrfdjgjhjkdfsgjksdhfgjhsdfgjgh" +
                "fkladsfjalsdgjfakldsgjlkdfshgdfshglsdfhglkjsdfhjgksdfgjkhsghsrfdjgjhjkdfsgjksdhfgjhsdfgjgh" +
                "fkladsfjalsdgjfakldsgjlkdfshgdfshglsdfhglkjsdfhjgksdfgjkhsghsrfdjgjhjkdfsgjksdhfgjhsdfgjgh" +
                "fkladsfjalsdgjfakldsgjlkdfshgdfshglsdfhglkjsdfhjgksdfgjkhsghsrfdjgjhjkdfsgjksdhfgjhsdfgjgh";
        // 组装message
        DeviceToCenterDataMessage toCenterDataMessage = new DeviceToCenterDataMessage(data.getBytes(), deviceDTO, userDTO, System.currentTimeMillis());


        while (true) {

            rabbitTemplate.convertAndSend("exchange_v19_v20", "to_center_data", ProtoStuffSerializerUtil.serialize(toCenterDataMessage));
//            try {
////                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

}
