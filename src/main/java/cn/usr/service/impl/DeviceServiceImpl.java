package cn.usr.service.impl;

import cn.usr.dao.DeviceDao;
import cn.usr.dao.TGroupDao;
import cn.usr.entity.Device;
import cn.usr.entity.Slave;
import cn.usr.service.DeviceService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.usr.config.HandlerNameConstants.*;

/**
 * @author Administrator
 */
@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    @Value("${cloud.device.connect_number_interval_reset:300}")
    private int connectNumberInterval;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private TGroupDao groupDao;


    @Override
    public Device getDeviceInfoByDeviceId(String devId) {
        Device device = null;
        try {
            device = getDeviceByDeviceIdForCache(devId);
            if (device == null)
                device = getDeviceByDeviceId(devId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[获取设备信息异常]：{}", e);
        }
        return device;
    }

    @Override
    public Device getDeviceByDeviceId(String devId) {
        return deviceDao.queryDeviceByDevId(devId);
    }

    @Override
    public Device getDeviceByDeviceIdForCache(String devId) {
        Object o = stringRedisTemplate.opsForHash().get(DEVICE_CACHE_HASH_KEY, devId);
        if (o != null)
            return JSONObject.parseObject(o.toString(), Device.class);
        return null;
    }

    @Override
    public Boolean putDeviceForCache(String devId, Device device) {
        // 仅当hashKey不存在时才设置散列hashKey的值
        return stringRedisTemplate.opsForHash().putIfAbsent(DEVICE_CACHE_HASH_KEY, devId, JSONObject.toJSONString(device));
    }

    @Override
    public String getDeviceConnectionNumber(String devId) {
        return stringRedisTemplate.opsForValue().get(DEVICE_CONNECTNUMBER_KEY + devId);
    }

    @Override
    public void incrementDeviceConnectionNumber(String devId) {
        if (stringRedisTemplate.hasKey(DEVICE_CONNECTNUMBER_KEY + devId)) {
            stringRedisTemplate.boundValueOps(DEVICE_CONNECTNUMBER_KEY + devId).increment(1);
        } else {
            stringRedisTemplate.opsForValue().set(DEVICE_CONNECTNUMBER_KEY + devId, "1", connectNumberInterval, TimeUnit.SECONDS);
        }
    }



    /**
     * 基础逻辑
     * <p>
     * 1.查询出设备所在分组
     * 2.查询出设备所在分组的对应分组列表
     *
     * @param did
     * @return
     */
    @Override
    public List<String> queryTargetGroupIdsByDid(String did) {
        List<String> targetGroupDidListForCache = getTargetGroupIdsForCache(did);
        if (targetGroupDidListForCache == null) {
            return groupDao.queryTargetGroupIdsByDid(did);
        }
        return targetGroupDidListForCache;

    }

    @Override
    public Set<String> queryDidListByGroupIds(List<String> gids) {
        return groupDao.queryDidListByGroupIds(gids);
    }

    @Override
    public Boolean putTargetGroupIdsForCache(String devId, List<String> targetGroupIds) {
        // 仅当hashKey不存在时才设置散列hashKey的值
        return stringRedisTemplate.opsForHash().putIfAbsent(DEVICE_GROUP_IDS_CACHE_HASH_KEY, devId, JSONObject.toJSONString(targetGroupIds));
    }

    @Override
    public Boolean putTargetGroupDidListForCache(String devId, Set<String> dids) {
        return stringRedisTemplate.opsForHash().putIfAbsent(DEVICE_GROUP_DIDS_CACHE_HASH_KEY, devId, JSONObject.toJSONString(dids));
    }



    @Override
    public List<String> getTargetGroupIdsForCache(String devId) {
        Object o = stringRedisTemplate.opsForHash().get(DEVICE_GROUP_IDS_CACHE_HASH_KEY, devId);
        if (o == null) {
            return null;
        }
        return JSONObject.parseArray(o.toString(), String.class);
    }

    @Override
    public List<String> getTargetGroupDidListForCache(String devId) {
        Object o = stringRedisTemplate.opsForHash().get(DEVICE_GROUP_DIDS_CACHE_HASH_KEY, devId);
        if (o == null) {
            return null;
        }
        return JSONObject.parseArray(o.toString(), String.class);
    }


}
