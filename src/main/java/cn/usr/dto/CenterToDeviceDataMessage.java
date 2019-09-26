package cn.usr.dto;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Package: cn.usr.dto
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-12-24 11:57
 */
@Data
@AllArgsConstructor
public class CenterToDeviceDataMessage {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 透传数据
     */
    private byte[] data;


}
