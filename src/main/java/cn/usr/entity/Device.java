package cn.usr.entity;

import lombok.Data;

import java.util.List;


/**
 * @author Administrator
 */
@Data
public class Device {

    /**
     * 自增ID
     */
    private Integer id;
    /**
     * 设备ID号
     */
    private String deviceId;
    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备所属用户ID==owner_uid
     */
    private Integer userId;

    /**
     * 設備類型
     */
    private Integer type;

    /**
     * 通讯密码
     */
    private String pass;
    /**
     * 通讯协议
     */
    private Integer protocol;
    /**
     * 轮询时间
     */
    private Integer pollingInterval;



    public Device() {
    }


    public Device(Integer id, String deviceId, String name, Integer userId, String pass, Integer protocol, Integer pollingInterval) {
        this.id = id;
        this.deviceId = deviceId;
        this.name = name;
        this.userId = userId;
        this.pass = pass;
        this.protocol = protocol;
        this.pollingInterval = pollingInterval;
    }

}
