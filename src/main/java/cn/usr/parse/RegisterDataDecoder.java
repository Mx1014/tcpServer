package cn.usr.parse;

import cn.usr.util.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author Administrator
 */
@Slf4j
@Component
public final class RegisterDataDecoder {

    /**
     * 注册包长度
     */
    private static final int REGISTER_PACKET_LENGTH = 50;

    /**
     * 1开头的代表为MAC
     */
    private static final String REG_PACKET_TYPE_MAC = "1";

    /**
     * 2开头的代表为IMEI
     */
    private static final String REG_PACKET_TYPE_IMEI = "2";

    private static final String EMPTY_STR = "";


    /**
     * 解析注册包
     *
     * @param data
     * @return
     */
    public RegisterMessage decode(byte[] data) {

        // PC1 解密数据
        PC1Tool.decrypt(data);
//        pc1Tool4.decrypt(data);

        // 2.长度限制条件判断
        if (data.length < REGISTER_PACKET_LENGTH) {
            return null;
        }

        byte[] temp = new byte[data.length - 2];

        System.arraycopy(data, 2, temp, 0, temp.length);
        String regStr = new String(temp, CharsetUtil.UTF_8);

        log.info("[设备注册码]：{}", regStr);


        // devId不会因为用户分配而修改，不在验证用户字段,
        // int userId = Integer.parseInt(regStr.substring(2, 10));// 去掉uid前面不足8位补的0
        String passCode = regStr.substring(20, 28);

        RegisterMessage registerMessage = null;

        // 如果是设备、临时设备连接id代表设备DevId
        String id = regStr.substring(0, 20);

        // 解析MAC地址或者IMEI
        id = getMacOrIMEI(id);
        if (EMPTY_STR.equals(id)) {
            return null;
        }

        registerMessage = new RegisterMessage(id, passCode);

        log.info("[设备注册完成信息]：{}", registerMessage);
        return registerMessage;
    }

    /**
     * 解析网络io注册包，获取MAC地址或者IMEI或者原有的设备id
     * <p>
     * 解析规则：
     * MAC：第一个字节是1，后面7个0补位，然后12个字节MAC地址
     * IMEI：第一个字节是2，后面4个0补位，然后15个字节IMEI
     * 默认：第一个字节是0
     *
     * @param id
     * @return
     */
    private String getMacOrIMEI(String id) {
        String deviceId;
        String packetType = id.substring(0, 1);
        if (REG_PACKET_TYPE_MAC.equals(packetType)) {
            deviceId = id.substring(8, 20);
        } else if (REG_PACKET_TYPE_IMEI.equals(packetType)) {
            deviceId = id.substring(5, 20);
        } else {
            deviceId = id;
        }
        return deviceId;
    }

}
