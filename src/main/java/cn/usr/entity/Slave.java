package cn.usr.entity;


import lombok.Data;

@Data
public class Slave {


    /**
     * 自增ID
     */
    private long id;

    /**
     * 从机名称
     */
    private String slaveName;

    /**
     * 从机序号
     */
    private String slaveIndex;

    /**
     * 从机地址
     */
    private String slaveAddress;


    /**
     * 数据模版id
     */
    private String templateId;


    /**
     * 用户id
     */
    private Integer uId;


}
