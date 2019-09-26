package cn.usr.service.impl;

import cn.usr.dao.DeviceDao;
import cn.usr.dto.*;
import cn.usr.entity.Device;
import cn.usr.service.CloudService;
import cn.usr.service.DeviceService;
import cn.usr.service.IConnectionManager;
import cn.usr.util.ProtoStuffSerializerUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.*;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static cn.usr.config.HandlerNameConstants.*;

/**
 * @Package: cn.usr.service.impl
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-12-21 12:00
 */
@Service
@Slf4j
public class CloudServiceImpl implements CloudService {

    @Value("${mq.exchange.exchange_tcp:exchange_tcp}")
    private String exchangeTcp;

    @Value("${mq.routingkey.to_center_data:to_center_data}")
    private String toCenterData;

    @Value("${mq.routingkey.online:online}")
    private String online;

    @Value("${mq.routingkey.offline:offline}")
    private String offline;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IConnectionManager<Channel> iConnectionManager;

    @Autowired
    private CloudService cloudService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceDao deviceDao;


    /**
     * 定长线程池 用来执行插入数据库的操作
     */
    private static volatile ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(4));


    @Override
    public void sendDeviceDataToRabbitMQ(String deviceId, byte[] data) {
        DeviceDTO deviceDTO = getDeviceInfoForCache(deviceId);
        if (deviceDTO == null) {
            log.error("设备信息为空直接返回,设备ID为：{}", deviceId);
            return;
        }

        UserDTO userDTO = getUserInfoForCache(deviceDTO);
        if (userDTO == null) {
            log.error("用户信息为空直接返回,设备ID为：{}", deviceId);
            return;
        }

        // 组装message
        DeviceToCenterDataMessage toCenterDataMessage = new DeviceToCenterDataMessage(data, deviceDTO, userDTO, System.currentTimeMillis());

        log.info("[发送设备数据到RabbitMQ]:{}", deviceId);

        // 发送到rabbitmq
        rabbitTemplate.convertAndSend(
                exchangeTcp,
                toCenterData,
                ProtoStuffSerializerUtil.serialize(toCenterDataMessage));

    }


    @Override
    public void sendDeviceOfflineStateToRabbitMQ(String deviceId) {
        DeviceDTO deviceDTO = getDeviceInfoForCache(deviceId);
        if (deviceDTO == null) {
            log.error("设备信息为空直接返回,设备ID为：{}", deviceId);
            return;
        }

        UserDTO userDTO = getUserInfoForCache(deviceDTO);
        if (userDTO == null) {
            log.error("用户信息为空直接返回,设备ID为：{}", deviceId);
            return;
        }

        // 组装message
        DeviceToCenterStateMessage toCenterStateMessage = new DeviceToCenterStateMessage(deviceDTO, userDTO, System.currentTimeMillis(), DeviceStateEnum.OFFLINE);

        log.info("[将设备的下线信息推送到rabbitmq] ,设备id为：{}", deviceId);
        // 发送到rabbitmq
        rabbitTemplate.convertAndSend(
                exchangeTcp,
                offline,
                ProtoStuffSerializerUtil.serialize(toCenterStateMessage));
    }

    @Override
    public void sendDeviceOnlineStateToRabbitMQ(String deviceId) {
        DeviceDTO deviceDTO = getDeviceInfoForCache(deviceId);
        if (deviceDTO == null) {
            log.error("设备信息为空直接返回,设备ID为：{}", deviceId);
            return;
        }

        UserDTO userDTO = getUserInfoForCache(deviceDTO);
        if (userDTO == null) {
            log.error("用户信息为空直接返回,设备ID为：{}", deviceId);
            return;
        }

        // 组装message
        DeviceToCenterStateMessage toCenterStateMessage = new DeviceToCenterStateMessage(deviceDTO, userDTO, System.currentTimeMillis(), DeviceStateEnum.ONLINE);

        log.info("[将设备的上线信息推送到rabbitmq] ,设备id为：{}", deviceId);
        // 发送到rabbitmq
        rabbitTemplate.convertAndSend(
                exchangeTcp,
                online,
                ProtoStuffSerializerUtil.serialize(toCenterStateMessage));
    }

    @Override
    public void sendDataToDevice(byte[] data) {
        CenterToDeviceDataMessage centerToDeviceDataMessage = null;
        try {
            centerToDeviceDataMessage =
                    ProtoStuffSerializerUtil.deserialize(data, CenterToDeviceDataMessage.class);
        } catch (Exception e) {
            log.error("[序列化来自RabbitMq 的数据失败] {}", e);
        }
        log.info("[Rabbitmq 收到云端发送的数据] 设备为：{}，数据长度为：{})", centerToDeviceDataMessage.getDeviceId(), centerToDeviceDataMessage.getData().length);
        if (centerToDeviceDataMessage == null || centerToDeviceDataMessage.getDeviceId() == null) {
            return;
        }
        Channel channel = iConnectionManager.getConnection(centerToDeviceDataMessage.getDeviceId());
        if (channel != null) {
            if (channel.isWritable()) {
                channel.writeAndFlush(Unpooled.wrappedBuffer(centerToDeviceDataMessage.getData()));
            }
        }
    }

    @Override
    public void clearDeviceCache(byte[] data) {

        // 反序列化数据
        String deserialize = ProtoStuffSerializerUtil.deserialize(data, String.class);

        if (deserialize == null) {
            return;
        }
        // 获取设备ID集合
        List<String> deviceList = JSONObject.parseArray(deserialize, String.class);

        for (String deviceId : deviceList) {
            // 查询设备是不是有透传组的缓存
            if (hasKeyTargetGroupDidListForCache(deviceId)) {
                Boolean aBoolean = removeTargetGroupDidListForCache(deviceId);
                log.info("[清除设备透传组缓存]清楚设备关联的透传组:操作结果为： {},设备ID为：{}", aBoolean, deviceId);
            }
            Boolean aBoolean = deleteDeviceCacheAndCloseConnection(deviceId);

            log.info("[清除设备缓存]清楚设备缓存并将设备踢下线操作结果: {},设备ID为：{}", aBoolean, deviceId);
        }

    }

    @Override
    public Boolean deleteDeviceCacheAndCloseConnection(String deviceId) {
        Channel connection = iConnectionManager.getConnection(deviceId);
        if (connection == null) {
            log.info("[API获取设备连接Channel为空,设备不在线,删除设备缓存]设备ID为：{}", deviceId);
            // 如果设备不在线那就直接清楚缓存
            return deleteDeviceCache(deviceId);
        }

        try {

            // 踢掉设备
            connection.close().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("踢掉设备,关闭设备连接异常：{}", e);
        }

        return true;
    }

    @Override
    public Boolean deleteDeviceCache(String deviceId) {
        Long result = stringRedisTemplate.opsForHash().delete(DEVICE_CACHE_HASH_KEY, deviceId);
        if (result != 1) {
            log.error("[API删除设备缓存失败]设备ID为：{}", deviceId);
            return false;
        }
        return true;
    }

    @Override
    public void deleteDeviceCache(byte[] data) {
        // 反序列化数据
        String deserialize = ProtoStuffSerializerUtil.deserialize(data, String.class);

        if (deserialize == null) {
            return;
        }
        // 获取设备ID集合
        List<String> deviceList = JSONObject.parseArray(deserialize, String.class);

        for (String deviceId : deviceList) {
            // 查询设备是不是有透传组的缓存
            if (hasKeyTargetGroupDidListForCache(deviceId)) {
                Boolean aBoolean = removeTargetGroupDidListForCache(deviceId);
                log.info("[清除设备透传组缓存]清楚设备关联的透传组:操作结果为： {},设备ID为：{}", aBoolean, deviceId);
            }
            Boolean aBoolean = deleteDeviceCache(deviceId);

            log.info("[清除设备缓存]清楚设备缓存,不踢设备掉线。操作结果: {},设备ID为：{}", aBoolean, deviceId);
        }
    }

    @Override
    public Long getDeviceConnectNumberForRedis() {
        return stringRedisTemplate.opsForHash().size(DEVICE_CACHE_HASH_KEY);
    }

    @Override
    public Long getDeviceConnectNumberForHashMap() {
        return iConnectionManager.getConnectionCount();
    }

    @Override
    public Device getDeviceInfoForRedis(String deviceId) {
        Object o = stringRedisTemplate.opsForHash().get(DEVICE_CACHE_HASH_KEY, deviceId);
        if (o != null) {
            return JSONObject.parseObject(o.toString(), Device.class);
        }
        return null;

    }


    @Override
    public Boolean removeTargetGroupDidListForCache(String devId) {

        try {
            // 删除设备对应的透传组缓存
            stringRedisTemplate.opsForHash().delete(DEVICE_GROUP_IDS_CACHE_HASH_KEY, devId);
            // 设备设备对应的透传组里面的设备关系
            stringRedisTemplate.opsForHash().delete(DEVICE_GROUP_DIDS_CACHE_HASH_KEY, devId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除设备的透传组关系异常：{},", e);
            return false;
        }
        return true;
    }

    @Override
    public Boolean hasKeyTargetGroupDidListForCache(String devId) {
        return stringRedisTemplate.opsForHash().hasKey(DEVICE_GROUP_IDS_CACHE_HASH_KEY, devId);
    }

    @Override
    public Boolean addDeviceOnlineRecordToRedis(String deviceId, Integer uid) {
        return stringRedisTemplate.opsForZSet().add(ONLINE_DEVICE, deviceId, uid);
    }

    @Override
    public void removeDeviceOnlineRecordToRedis(String deviceId) {
        stringRedisTemplate.opsForZSet().remove(ONLINE_DEVICE, deviceId);
    }

    @Override
    public void addDeviceStateRecordToDB(String deviceId, int state) {
        ListenableFuture<Boolean> listenableFuture = executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                int result = deviceDao.addDeviceStateHistory(deviceId, state, "normal", System.currentTimeMillis());
                return result > 0;
            }
        });

        // 执行操作数据库的一个回调，但是此处没有什么意思，没有加上对失败操作的处理，后续可以增加
        Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean.equals(Boolean.TRUE)) {
                    log.info("插入数据库操作成功，设备ID为：{},插入数据库的动作为：{}", deviceId, (state == 1) ? "上线" : "下线");
                } else {
                    log.info("插入数据库操作失败，设备ID为：{},插入数据库的动作为：{}", deviceId, (state == 1) ? "上线" : "下线");
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.info("插入数据库操作失败 伴随异常，设备ID为：{},插入数据库的动作为：{}，异常为：{}",
                        deviceId, (state == 1) ? "上线" : "下线", throwable);

            }
        });
    }


    /**
     * 从缓存中获取用户信息
     *
     * @param deviceDTO
     * @return
     */
    private UserDTO getUserInfoForCache(DeviceDTO deviceDTO) {
        Integer userId = deviceDTO.getUserId();
        if (userId == null) {
            log.error("[获取设备中的用户ID失败]设备信息为:{}", deviceDTO);
            return null;
        }
        // 获取用户缓存
        Object obj = stringRedisTemplate.opsForHash().get(USER_CACHE_HASH_KEY, userId.toString());
        if (obj == null) {
            log.error("[获取用户缓存信息失败]设备信息为:{}", deviceDTO);
            return null;
        }
        // 序列化用户缓存
        UserDTO userDTO = null;
        try {
            userDTO = JSONObject.parseObject(obj.toString(), UserDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[序列化用户缓存失败]用户信息为:{}", obj.toString());
        }
        log.info("[获取用户缓存信息成功]设备信息为：{}", deviceDTO);
        return userDTO;
    }

    /**
     * 从缓存中获取设备信息/ 注意增加缓存替换操作
     *
     * @param deviceId
     * @return
     */
    private DeviceDTO getDeviceInfoForCache(String deviceId) {
        // 获取设备缓存
        if (deviceId == null) {
            return null;
        }
        Object o = stringRedisTemplate.opsForHash().get(DEVICE_CACHE_HASH_KEY, deviceId);
        if (o != null) {
            log.info("[获取设备缓存信息失败]接下来去数据库继续更新这个缓存,设备ID为:{}", deviceId);
            Device device = deviceDao.queryDeviceByDevId(deviceId);
            if (device == null) {
                log.error("不存这个这个设备的信息,这个设备属于非法设备,设备ID为:{}", deviceId);
                return null;
            }

            deviceService.putDeviceForCache(deviceId, device);

            log.info("[更新这个设备的缓存信息]设备ID为:{},最新的缓存更新为:{}", deviceId, device);

            return JSONObject.parseObject(JSONObject.toJSONString(device), DeviceDTO.class);
        }


        DeviceDTO deviceDTO = null;
        try {
            // 序列化设备缓存
            deviceDTO = JSONObject.parseObject(o.toString(), DeviceDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[序列化设备缓存失败]设备信息为:{}", o.toString());
        }
        log.debug("[获取设备缓存信息成功]设备ID为：{}", deviceId);
        return deviceDTO;
    }
}
