package cn.usr.dto;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Package: cn.usr.dto
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018/10/22 11:05
 */
@Data
@AllArgsConstructor
public class DeviceDTO {

    /**
     * 设备ID
     */
    private final String deviceId;

    /**
     * 设备名称
     */
    private final String name;

    /**
     * 所属用户ID
     */
    private final Integer userId;

    /**
     * 通讯协议
     */
    private final Integer protocol;

    /**
     * 设备类型
     */
    private final Integer type;

}
