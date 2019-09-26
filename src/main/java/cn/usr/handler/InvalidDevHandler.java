package cn.usr.handler;

import cn.usr.config.HandlerNameConstants;
import cn.usr.entity.enums.RegisterErrCode;
import cn.usr.service.CloudService;
import cn.usr.service.IConnectionManager;
import cn.usr.service.impl.DevConnectionsStore;
import cn.usr.util.SpringUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static cn.usr.config.HandlerNameConstants.*;


/**
 * @author shizhiyuan
 * 无效设备处理类
 */
@Component
@Scope("prototype")
@Slf4j
public class InvalidDevHandler extends ChannelInboundHandlerAdapter {

    /**
     * 设备ID
     */
    private String deviceId;


    /**
     * 业务逻辑操作类
     */
    private CloudService cloudService;

    /**
     * 设备连接缓存管理
     */
    private final IConnectionManager<Channel> connectionsStore = (IConnectionManager<Channel>) SpringUtil.getBean(DevConnectionsStore.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        deviceId = getDeviceIdByAttribute(ctx);
        if (cloudService == null) {
            cloudService = SpringUtil.getBean(CloudService.class);
        }

        super.channelRead(ctx, msg);
    }


    /**
     * channel断开检测
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        log.info("[设备断开连接] 此处推送下线，设备的ID为：{}", deviceId);

        // 此处
        if (deviceId == null) {
            // 取出对应的设备ID
            deviceId = getDeviceIdByAttribute(ctx);
        }

        if (deviceId == null) {
            return;
        }

        Channel connection = connectionsStore.getConnection(deviceId);
        if (connection == null) {
            if (deviceId != null) {
                log.error("[设备断开检测] 设备Channel 不存在，设备ID为：{}", deviceId);
            } else {
                log.error("[设备断开检测] 设备Channel 不存在，设备也不存在ID为：{},发起这个连接的地址：{}", ctx.channel().remoteAddress());
            }
            return;
        }

        connectionsStore.removeConnection(deviceId, connection);
        log.info("[连接缓存池里还有] {} 设备", connectionsStore.getConnectionCount());

        // 发送设备下线信息到上层应用
        cloudService.sendDeviceOfflineStateToRabbitMQ(deviceId);

        //删除设备缓存
        cloudService.deleteDeviceCache(deviceId);

        // 删除设备在线记录
        cloudService.removeDeviceOnlineRecordToRedis(deviceId);

        // 添加设备下线记录到数据库中
        cloudService.addDeviceStateRecordToDB(deviceId, 0);

    }


    /**
     * 心跳检测
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("[设备心跳检测] 超时还没发数据,设备ID为：{}", deviceId);

                // 先移除此handler 避免触发channelInactive()
                ctx.pipeline().remove(HandlerNameConstants.INVALID_DEV_HANDLER);

                if (connectionsStore == null) {
                    log.info("设备缓存连接池为空");
                    return;
                }
                // 踢掉设备
                Channel connection = connectionsStore.getConnection(deviceId);
                if (connection == null) {
                    log.error("[设备心跳检测] 设备Channel 不存在，设备ID为：{}", deviceId);
                    return;
                }
                if (connection.isWritable()) {
                    connection.writeAndFlush(Unpooled.wrappedBuffer(RegisterErrCode.REG_DEVICE_LOST_KEEPLIVE.getRespCode()));
                }
                connection.close().sync();
                connectionsStore.removeConnection(deviceId, connection);

                // 发送设备下线信息到上层应用
                cloudService.sendDeviceOfflineStateToRabbitMQ(deviceId);

                //删除设备缓存
                cloudService.deleteDeviceCache(deviceId);

                // 删除设备在线记录
                cloudService.removeDeviceOnlineRecordToRedis(deviceId);

                // 添加设备下线记录到数据库中
                cloudService.addDeviceStateRecordToDB(deviceId, 0);
            }
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause.getMessage().equals("Connection reset by peer")) {
            log.error("[异常捕获]设备主动强制断开连接，设备ID为{}", deviceId);
            cause.printStackTrace();
            return;
        }
        log.error("[InvalidDevHandler异常捕捉]class Name: {} ", cause);
        cause.printStackTrace();
    }

    private synchronized String getDeviceIdByAttribute(ChannelHandlerContext ctx) {
        String deviceid = null;
        // 取出对应的设备ID
        Attribute<Object> attr = ctx.channel().attr(objectAttributeKey);
        Object o = attr.get();
        if (o != null) {
            deviceid = o.toString();
        }
        return deviceid;
    }
}
