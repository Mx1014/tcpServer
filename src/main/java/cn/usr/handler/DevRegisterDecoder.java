package cn.usr.handler;

import java.net.SocketAddress;
import java.util.List;


import cn.usr.config.HandlerNameConstants;
import cn.usr.dto.DeviceRegisterDTO;
import cn.usr.entity.Device;

import cn.usr.entity.User;
import cn.usr.entity.enums.RegisterErrCode;
import cn.usr.parse.RegisterDataDecoder;
import cn.usr.parse.RegisterMessage;
import cn.usr.service.CloudService;
import cn.usr.service.DeviceService;
import cn.usr.service.IConnectionManager;
import cn.usr.service.UserService;
import cn.usr.service.impl.DevConnectionsStore;
import cn.usr.util.SpringUtil;
import cn.usr.util.Tools;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import static cn.usr.config.HandlerNameConstants.objectAttributeKey;


/**
 * @author 石志远
 */
@Component
@Scope("prototype")
@Slf4j
public class DevRegisterDecoder extends ByteToMessageDecoder {

    /**
     * 是否是收到的第一个数据包
     */
    private boolean firstData = true;


    /**
     * 注册包解析类
     */
    private final RegisterDataDecoder registerCode = new RegisterDataDecoder();


    /**
     * 设备业务逻辑处理
     */

    private DeviceService deviceService = SpringUtil.getBean(DeviceService.class);


    /**
     * 常量类
     */
    private HandlerNameConstants handlerNameConstants = SpringUtil.getBean(HandlerNameConstants.class);


    /**
     * 用户业务逻辑处理
     */

    private UserService userService = SpringUtil.getBean(UserService.class);


    private CloudService cloudService = SpringUtil.getBean(CloudService.class);

    /**
     * 设备连接缓存管理
     */
    private final IConnectionManager<Channel> connectionsStore = (IConnectionManager) SpringUtil.getBean(DevConnectionsStore.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //decode只解析接收的第一个数据包，如果不是第一个数据包
        //那么设置readIndex为writeIndex，即没有可读的数据
        if (!firstData) {
            in.readerIndex(in.writerIndex());
            return;
        }
        firstData = false;

        RegisterMessage registerMessage = readRegisterPackage(in);
        if (registerMessage == null) {
            // 注册失败向设备返回错误码
            handelInvalidReg(ctx, in, RegisterErrCode.REG_DATA_INVALID.getRespCode());
            return;
        }

//        // 上线成功记录设备短期的上线次数
//        deviceService.incrementDeviceConnectionNumber(registerMessage.deviceId);
//
//        //频繁尝试连接验证
//        String connectionNumber = deviceService.getDeviceConnectionNumber(registerMessage.deviceId);
//        if (connectionNumber != null) {
//            // 如果超出一定时间内的连接的次数，关闭连接释放数据
//            // 这里的机制由之前的凭相同设备ID超过连接次数后，挂起连接但不关闭连接改为 ----> 超出连接数量后释放掉之前的连接，在规定时间内凭借相同设备ID无法再次建立连接。
//            // 原因为硬件设备的连接判断机制不统一，就算挂起后设备收不到透传云的回应，就会不管之前的连接再发起一条新的连接的向云端注册，导致我们这的挂起无意义，实则挂起的是一条死链。
//            if (Integer.valueOf(connectionNumber) > handlerNameConstants.getConnectNumber()) {
//                handelInvalidReg(ctx, in, RegisterErrCode.REG_DEVICE_BAD_CONNECT.getRespCode());
//                return;
//            }
//        }


        // 查询设备信息
        Device device = deviceService.getDeviceInfoByDeviceId(registerMessage.deviceId);
        if (device == null) {
            SocketAddress socketAddress = ctx.channel().remoteAddress();
            log.error("设备不存在,设备连接的IP为：{}", socketAddress);
            // 返回设备不存在，关闭连接
            handelInvalidReg(ctx, in, RegisterErrCode.REG_DEVICE_NOT_EXIST.getRespCode());
            return;
        }
        log.info("[设备的信息为]{}", device);

        // 验证通讯密码
        if (!Tools.formatComPassword(device.getPass()).equals(registerMessage.passCode)) {
            log.info("[设备的通讯密码不对]{}", device.getDeviceId());
            handelInvalidReg(ctx, in, RegisterErrCode.REG_COMPASS_ERR.getRespCode());
            return;
        }

        if (device.getUserId() == null) {
            handelInvalidReg(ctx, in, RegisterErrCode.REG_USER_NOT_EXIST.getRespCode());
            return;
        }
        User user = userService.getUserInfoByuId(device.getUserId());
        if (user == null) {
            log.error("设备的用户信息为空, 设备的ID是{}", device.getDeviceId());
            handelInvalidReg(ctx, in, RegisterErrCode.REG_USER_NOT_EXIST.getRespCode());
            return;
        }
        log.info("[设备的用户信息为]{}", user);

        //如果设备已经在线，向原连接发送消息,并断开连接
        Channel connectionChannel = connectionsStore.getConnection(device.getDeviceId());
        if (connectionChannel != null) {
            if (connectionChannel.isWritable()) {
                try {
                    connectionChannel.writeAndFlush(Unpooled.wrappedBuffer(RegisterErrCode.REG_DEVICE_REPLACE.getRespCode()));
                } catch (Exception e) {
                    e.printStackTrace();
                    ctx.close();
                } finally {
                    if (in.isReadable()) {
                        ReferenceCountUtil.release(in);
                    }
                }
            }
            log.info("[DevRegisterDecoder] 这个设备号已经被占用了,向之前被占用的设备发送错误码 , 并断开其连接 {}", device.getDeviceId());
            connectionsStore.removeConnection(device.getDeviceId(), connectionChannel);
            connectionChannel.pipeline().remove(HandlerNameConstants.INVALID_DEV_HANDLER);
            // 发送设备下线信息到上层应用
            cloudService.sendDeviceOfflineStateToRabbitMQ(device.getDeviceId());

            //删除设备缓存
            cloudService.deleteDeviceCache(device.getDeviceId());

            // 添加设备下线记录到数据库中
            cloudService.addDeviceStateRecordToDB(device.getDeviceId(), 0);

            connectionChannel.close();
        }


        //记录设备上线信息
        if (!connectionsStore.addConnection(device.getDeviceId(), ctx.channel())) {
            log.info("[记录设备上线失败]{}", device.getDeviceId());
            ctx.channel().close();
            return;
        }

        // 上线成功更新设备属性缓存
        deviceService.putDeviceForCache(device.getDeviceId(), device);

        // 上线成功更新用户缓存
        userService.putUserByIdForCache(user.getUserId(), user);

        // 将设备ID注入channel Attribute 中，在Netty中将设备ID和Channel关联起来

        Attribute<Object> attr = ctx.channel().attr(objectAttributeKey);
        attr.setIfAbsent(new StringBuilder().append(device.getDeviceId()));

        log.info("[设备上线成功]{}", device.getDeviceId());

        //向设备发送注册成功数据流

        if (ctx.channel().isWritable()) {
            try {
                ctx.writeAndFlush(Unpooled.wrappedBuffer(RegisterErrCode.REG_SUCC.getRespCode()));
            } catch (Exception e) {
                e.printStackTrace();
                ctx.close();
            }
        }

        log.info("[设备注册成功 向设备发送数据流]{}", device.getDeviceId());

        // 向下传递
        out.add(new DeviceRegisterDTO(device, user));
    }


    /**
     * 读取并处理注册包
     *
     * @param in
     * @return
     */
    private RegisterMessage readRegisterPackage(ByteBuf in) {
        // 读取注册包
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);
        // 解析注册包
        return registerCode.decode(data);
    }


    /**
     * 注册连接失败
     *
     * @param channelHandlerContext
     * @param data
     */
    private void handelInvalidReg(ChannelHandlerContext channelHandlerContext, ByteBuf in, byte[] data) {
        try {
            if (channelHandlerContext.channel().isWritable()) {
                channelHandlerContext.writeAndFlush(Unpooled.wrappedBuffer(data));
            }
            channelHandlerContext.pipeline().remove(HandlerNameConstants.INVALID_DEV_HANDLER);
            channelHandlerContext.pipeline().remove(HandlerNameConstants.DEV_REGISTER_DECODER);
            channelHandlerContext.pipeline().remove(HandlerNameConstants.DEV_DATA_PARSER_HANDLER);
            if (channelHandlerContext.pipeline().get(HandlerNameConstants.FLOW_TRAFFIC_HANDLER) != null) {
                channelHandlerContext.pipeline().remove(HandlerNameConstants.FLOW_TRAFFIC_HANDLER);
            }
            channelHandlerContext.pipeline().remove(IdleStateHandler.class);
            channelHandlerContext.close();
        } catch (Exception e) {
            e.printStackTrace();

            channelHandlerContext.close();
        } finally {
            if (in.isReadable()) {
                ReferenceCountUtil.release(in);
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (cause.getMessage().equals("Connection reset by peer")) {
            log.error("[异常捕获]设备还没有注册完成就主动强制断开连接，无设备ID{},远端信息为：{}", ctx.channel().remoteAddress());
            cause.printStackTrace();
            return;
        }
        log.error("DevRegisterDecoder 异常捕捉：{}", cause);
        ctx.close();
    }
}
