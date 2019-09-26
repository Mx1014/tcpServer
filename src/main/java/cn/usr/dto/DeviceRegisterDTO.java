package cn.usr.dto;

import cn.usr.entity.Device;
import cn.usr.entity.User;
import lombok.Data;

/**
 * @Package: cn.usr.dto
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018/10/22 11:26
 */

@Data
public class DeviceRegisterDTO {

    /**
     * 设备信息
     */
    private final Device device;


    /**
     * 用户信息
     */
    private final User user;
}
