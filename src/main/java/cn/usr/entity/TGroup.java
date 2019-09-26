package cn.usr.entity;

import lombok.Data;

import java.util.List;


@Data
public class TGroup {

    /**
     * 主键ID
     */
    private int id;


    /**
     *
     */
    private int pairGid;

    /**
     * // 组下面包含的设备
     */
    private List<String> dids;
    private int uid;
    /**
     * 分组中是否含有MQTT即Ver2的设备
     */
    private boolean hasV2Device;


}
