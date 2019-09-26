package cn.usr.dao;

import cn.usr.entity.Device;
import cn.usr.entity.Slave;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author Administrator
 */
@Repository
public interface DeviceDao {

    /**
     * 根据设备ID 查询设备信息
     *
     * @param devId
     * @return
     */
    Device queryDeviceByDevId(String devId);


    /**
     * 添加设备状态变化记录到数据库
     *
     * @param devId
     * @param state        1:上线 0:下线
     * @param generateTime 设备上下线时间
     * @param reason       上下线原因
     * @return
     */
    int addDeviceStateHistory(String devId, int state, String reason, Long generateTime);


}
