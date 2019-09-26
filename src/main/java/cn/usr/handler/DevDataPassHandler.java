package cn.usr.handler;

import cn.usr.config.HandlerNameConstants;
import cn.usr.dto.*;
import cn.usr.entity.*;
import cn.usr.service.*;
import cn.usr.service.impl.DevConnectionsStore;

import cn.usr.util.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;


import static cn.usr.config.HandlerNameConstants.DEV_REGISTER_DECODER;
import static cn.usr.util.Tools.getByteArrayFromByteBuf;


/**
 * @author ljq
 */
@Component
@Scope("prototype")
@Slf4j
public class DevDataPassHandler extends ChannelInboundHandlerAdapter {


    /**
     * 云业务逻辑
     */
    private CloudService cloudService = SpringUtil.getBean(CloudService.class);

    /**
     * 设备业务逻辑处理
     */
    private DeviceService deviceService = SpringUtil.getBean(DeviceService.class);


    /**
     * 设备连接缓存管理
     */
    private final IConnectionManager<Channel> connectionsStore = (IConnectionManager<Channel>) SpringUtil.getBean(DevConnectionsStore.class);

    /**
     * 透传组设备列表
     */
    private Set<String> didListByGroupIds;

    /**
     * 設備信息
     */
    private Device device;

    /**
     * 用户信息
     */
    private User user;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof DeviceRegisterDTO) {
            DeviceRegisterDTO deviceRegisterDTO = (DeviceRegisterDTO) msg;


            // 初始化设备属性
            device = deviceRegisterDTO.getDevice();

            // 初始化用户属性
            user = deviceRegisterDTO.getUser();

            log.info("[推送设备上线]设备ID为：{}  设备连接地址为：{}", device.getDeviceId(), ctx.channel().remoteAddress());

            // 先找到设备的对应的透传组的gid列表
            List<String> targetGroupIds = deviceService.queryTargetGroupIdsByDid(device.getDeviceId());
            if (targetGroupIds != null && targetGroupIds.size() > 0) {
                // 将设备和透传组ID对应关系存到缓存中
                deviceService.putTargetGroupIdsForCache(device.getDeviceId(), targetGroupIds);
                // 获取设备对应的透传组里面的设备列表
                didListByGroupIds = deviceService.queryDidListByGroupIds(targetGroupIds);
                if (didListByGroupIds != null && didListByGroupIds.size() > 0) {
                    // 去重
                    Set<String> set = new HashSet<>(didListByGroupIds);
                    // 更新设备对应的透传组的里面的设备列表到缓存
                    deviceService.putTargetGroupDidListForCache(device.getDeviceId(), set);
                }
            }

            if (ctx.pipeline().get(DEV_REGISTER_DECODER) != null) {
                // 删除注册解码coder
                ctx.pipeline().remove(DEV_REGISTER_DECODER);
            }

            cloudService.sendDeviceOnlineStateToRabbitMQ(device.getDeviceId());

            cloudService.addDeviceOnlineRecordToRedis(device.getDeviceId(), device.getUserId());

            cloudService.addDeviceStateRecordToDB(device.getDeviceId(), 1);

        } else {

            ByteBuf byteBuf = (ByteBuf) msg;

            // 发送数据到透传组
            if (didListByGroupIds != null && didListByGroupIds.size() > 0) {
                sendDevDataToGroup(byteBuf.retainedDuplicate());
            }

            try {
                log.debug("[收到设备发送上来的数据]，设备的ID为:{};", device.getDeviceId());

                // 发送数据到透RabbitMQ
                cloudService.sendDeviceDataToRabbitMQ(device.getDeviceId(), getByteArrayFromByteBuf(byteBuf));
            } catch (Exception e) {
                log.error("[将设备的数据发送到上层应用异常捕获]{}", e);
            } finally {
                byteBuf.release();
            }
        }
    }

    private void sendDevDataToGroup(ByteBuf byteBuf) {
        try {
            log.debug("[发送数据到对应的透传组]：{}", didListByGroupIds);
            // 向对应透传组的设备透传消息
            for (String s : didListByGroupIds) {
                Channel connection = connectionsStore.getConnection(s);
                if (connection != null) {
                    if (connection.isWritable()) {
                        connection.writeAndFlush(byteBuf.retainedDuplicate());
                    }
                }
            }
        } catch (Exception e) {
            log.error("[将设备的数据发送到对应的透传组异常捕获]{}", e);
        } finally {
            byteBuf.release();
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}
