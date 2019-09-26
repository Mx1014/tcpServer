package cn.usr.handler;


import cn.usr.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static cn.usr.config.HandlerNameConstants.*;


/**
 * @author: Rock 【shizhiyuan@usr.cn】
 * <p>
 * 请注意，此处理程序的管道覆盖率为“1”，这意味着必须为每个新通道创建新的处理程序，因为计数器无法在所有通道之间共享。
 */
@Slf4j
public class FlowTrafficHandler extends ChannelTrafficShapingHandler {

    @Autowired
    private StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 是否是收到的第一个数据包
     */
    private boolean firstData = true;

    /**
     * 构造函数详细信息
     *
     * @param writeLimit    - 0或以字节/秒为单位的限制
     * @param readLimit     - 0或以字节/秒为单位的限制
     * @param checkInterval - 两个通道性能计算之间的延迟，如果不计算统计数，则为0。 毫秒
     * @param maxTime       maxTime等待的最大延迟。
     */
    public FlowTrafficHandler(long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super(writeLimit, readLimit, checkInterval, maxTime);
    }

    /**
     * 使用默认最大时间创建一个新实例，延迟允许值为15000毫秒且无限制。
     *
     * @param checkInterval
     */
    public FlowTrafficHandler(long checkInterval) {
        super(checkInterval);
    }


    /**
     * 计算速率限制流量的读取和写入字节数
     *
     * @param counter
     */
    @Override
    protected void doAccounting(TrafficCounter counter) {

        if (deviceId == null) {
            return;
        }

        // 记录设备读流量
        stringRedisTemplate.opsForZSet().incrementScore(DEVICE_READ_FLOW_KEY, deviceId, counter.lastReadBytes());

        // 记录设备写流量
        stringRedisTemplate.opsForZSet().incrementScore(DEVICE_WRITE_FLOW_KEY, deviceId, counter.lastWrittenBytes());
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (firstData) {
            // 取出对应的设备ID
            AttributeKey<Object> objectAttributeKey = AttributeKey.valueOf(CHANNELID_DEVID);
            Attribute<Object> attr = ctx.channel().attr(objectAttributeKey);
            Object o = attr.get();
            if (o != null) {
                deviceId = o.toString();
            }
            firstData = false;
        }

        // 向下传递
        super.channelRead(ctx, msg);
    }


}
