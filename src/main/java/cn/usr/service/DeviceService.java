package cn.usr.service;

import cn.usr.entity.Device;
import cn.usr.entity.Slave;
import cn.usr.entity.TGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author Administrator
 */
public interface DeviceService {


    /**
     * 业务获取设备信息对外接口
     *
     * @param devId
     * @return
     * @throws RuntimeException
     */
    Device getDeviceInfoByDeviceId(String devId);

    /**
     * 获取设备信息
     *
     * @param devId
     * @return
     */
    Device getDeviceByDeviceId(String devId);

    /**
     * 获取设备信息从缓存中
     *
     * @param devId
     * @return
     */
    Device getDeviceByDeviceIdForCache(String devId);

    /**
     * 设置设备信息到缓存
     *
     * @param devId
     * @param device
     * @return
     */
    Boolean putDeviceForCache(String devId, Device device);


    /**
     * 获取设备连接次数
     *
     * @param devId
     * @return
     */
    String getDeviceConnectionNumber(String devId);

    /**
     * 自增设备的连接次数
     *
     * @param devId
     * @return
     */
    void incrementDeviceConnectionNumber(String devId);




    /**
     * 查询设备所在分组的对应分组id
     *
     * @param did
     * @return
     */
    List<String> queryTargetGroupIdsByDid(String did);


    /**
     * 查询透传组内的设备ID列表
     *
     * @param gids
     * @return
     */
    Set<String> queryDidListByGroupIds(@Param("gids") List<String> gids);


    /**
     * 将设备对应的透传列表存到缓存中
     *
     * @param devId
     * @param targetGroupIds
     * @return
     */
    Boolean putTargetGroupIdsForCache(String devId, List<String> targetGroupIds);

    /**
     * 将单个设备对应的透传组中的设备存到缓存中
     *
     * @param devId
     * @param dids
     * @return
     */
    Boolean putTargetGroupDidListForCache(String devId, Set<String> dids);



    /**
     * 获取设备对应的透传列表存到缓存中
     *
     * @param devId
     * @return
     */
    List<String> getTargetGroupIdsForCache(String devId);

    /**
     * 获取单个设备对应的透传组中的设备列表
     *
     * @param devId
     * @return
     */
    List<String> getTargetGroupDidListForCache(String devId);


}
