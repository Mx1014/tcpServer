package cn.usr.service.impl;

import cn.usr.service.IConnectionManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Administrator
 */
@Slf4j
@Service
public class DevConnectionsStore implements IConnectionManager<Channel> {
    /**
     * 设备连接channel缓存
     */
    private final ConcurrentMap<String, Channel> devChannelMap = new ConcurrentHashMap<>(10000);


    @Override
    public boolean addConnection(String key, Channel channel) {
        try {
            devChannelMap.put(key, channel);
            return true;
        } catch (Exception e) {
            log.error("[更新设备连接缓存失败]:{}", e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Channel getConnection(String key) {
        return devChannelMap.get(key);
    }

    @Override
    public boolean containConnection(String key) {
        return devChannelMap.containsKey(key);
    }

    @Override
    public boolean removeConnection(String key, Channel connection) {
        return devChannelMap.remove(key, connection);
    }

    @Override
    public long getConnectionCount() {

        return devChannelMap.size();
    }

}
