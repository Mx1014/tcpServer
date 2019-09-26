package cn.usr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Package: cn.usr.dto
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-12-21 14:17
 */
@Data
@AllArgsConstructor
public class DeviceToCenterStateMessage {

    /**
     * 设备信息
     */
    private final DeviceDTO deviceDTO;

    /**
     * 用户信息
     */
    private final UserDTO userDTO;

    /**
     * 时间戳
     */
    private final long timeMillis;

    /**
     * 设备状态
     */
    private final DeviceStateEnum deviceStateEnum;
}
