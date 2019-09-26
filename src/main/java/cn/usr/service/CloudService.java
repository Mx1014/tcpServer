package cn.usr.service;

import cn.usr.entity.Device;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Package: cn.usr.service
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-12-21 11:55
 */

public interface CloudService {

    /**
     * 发送设备数据到RabbitMQ
     *
     * @param deviceId
     * @param data
     */
    void sendDeviceDataToRabbitMQ(String deviceId, byte[] data);

    /**
     * 发送设备下线状态到RabbitMQ
     *
     * @param deviceId
     */
    void sendDeviceOfflineStateToRabbitMQ(String deviceId);

    /**
     * 发送设备上线状态到RabbitMQ
     *
     * @param deviceId
     */
    void sendDeviceOnlineStateToRabbitMQ(String deviceId);

    /**
     * 发送数据到设备
     *
     * @param data
     */
    void sendDataToDevice(byte[] data);

    /**
     * 清楚设备缓存,并踢下线
     *
     * @param data
     */
    void clearDeviceCache(byte[] data);

    /**
     * 删除设备缓存并且关闭设备连接
     *
     * @param deviceId
     * @return
     */
    Boolean deleteDeviceCacheAndCloseConnection(String deviceId);

    /**
     * 删除设备缓存
     *
     * @param deviceId
     * @return
     */
    Boolean deleteDeviceCache(String deviceId);

    /**
     * 删除设备缓存,不踢设备下线
     *
     * @param data 原始数据
     * @return
     */
    void deleteDeviceCache(byte[] data);

    /**
     * 从Redis中获取TCP设备连接数量
     *
     * @return
     */
    Long getDeviceConnectNumberForRedis();

    /**
     * 从内存中获取TCP设备连接数量
     *
     * @return
     */
    Long getDeviceConnectNumberForHashMap();


    /**
     * 从Redis中获取设备缓存信息
     *
     * @param deviceId
     * @return
     */
    Device getDeviceInfoForRedis(String deviceId);

    /**
     * 删除设备对应的透传组信息
     *
     * @param devId
     * @return
     */
    Boolean removeTargetGroupDidListForCache(String devId);

    /**
     * 设备是否存在透传组的缓存
     *
     * @param devId
     * @return
     */
    Boolean hasKeyTargetGroupDidListForCache(String devId);


    /**
     * 添加设备上线记录到Redis
     *
     * @param deviceId
     * @param uid
     * @return
     */
    Boolean addDeviceOnlineRecordToRedis(String deviceId, Integer uid);


    /**
     * 从Redis中删除设备在线记录，代表设备下线
     *
     * @param deviceId
     * @return
     */
    void removeDeviceOnlineRecordToRedis(String deviceId);


    /**
     * 添加设备上线下线记录到数据库中
     *
     * @param deviceId
     * @param state   1:上线 0:下线
     */
    void addDeviceStateRecordToDB(String deviceId, int state);


}
