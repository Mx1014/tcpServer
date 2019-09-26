package cn.usr.dto;


import lombok.Getter;

/**
 * @Package: cn.usr.dto
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-12-21 14:17
 */
@Getter
public enum DeviceStateEnum {
    ONLINE(1),
    OFFLINE(0);

    private int state;

    DeviceStateEnum(int state) {
        this.state = state;
    }


}
