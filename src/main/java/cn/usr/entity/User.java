package cn.usr.entity;


import lombok.Data;

import java.util.List;

/**
 * @author shizhiyuan
 */
@Data
public class User {


    /**
     * 自增ID
     */
    private Integer userId;


    /**
     * 用户名
     */
    private String account;


    /**
     * 用户类型 一级、二级
     */
    private Integer typeId;


    /**
     * vip级别
     */
    private Integer vipLevel;

    /**
     * 自动创建工单
     */
    private Integer autoWorkOrder;


    /**
     * 是否有资源限制
     */
    private Integer ifHaveResources;

    /**
     * 父用户ID
     */
    private Integer pid;


}
