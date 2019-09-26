package cn.usr.dto;

import lombok.Data;

/**
 * @Package: cn.usr.dto
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018/10/22 11:11
 */
@Data
public class DeviceToCenterDataMessage {

    /**
     * 数据
     */
    private final byte[] data;

    /**
     * 设备信息
     */
    private final DeviceDTO deviceDTO;

    /**
     * 用户信息
     */
    private final UserDTO userDTO;

    /**
     * 时间
     */
    private final Long millTime;
}
