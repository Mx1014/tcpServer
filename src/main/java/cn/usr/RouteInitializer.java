package cn.usr;

import cn.usr.config.HandlerNameConstants;
import cn.usr.handler.DevDataPassHandler;
import cn.usr.handler.DevRegisterDecoder;
import cn.usr.handler.FlowTrafficHandler;
import cn.usr.handler.InvalidDevHandler;
import cn.usr.util.SpringUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * @author shizhiyuan
 * pipeline 初始化类
 */

@Component
public class RouteInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 常量类
     */
    private HandlerNameConstants handlerNameConstants = SpringUtil.getBean(HandlerNameConstants.class);



    /**
     * 给pipeline 注入handler
     * - 流量控制
     * - 心跳检测
     * - 设备状态检测
     * - 注册解码
     * - 数据处理
     *
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        //	在IdleStateEvent其状态IdleState.READER_IDLE 时的指定时间段没有执行读操作将被触发。指定0禁用。
        // 在IdleStateEvent其状态IdleState.WRITER_IDLE 时的指定时间段没有执行写操作将被触发。指定0禁用。
        // 一个IdleStateEvent其状态IdleState.ALL_IDLE 时的时间在规定的时间进行读取和写入都将被触发。指定0禁用
        ch.pipeline().addLast(new IdleStateHandler(handlerNameConstants.getReaderIdleTime(), 0, 0, TimeUnit.SECONDS));
        ch.pipeline().addLast(HandlerNameConstants.DEV_REGISTER_DECODER, new DevRegisterDecoder());
        ch.pipeline().addLast(HandlerNameConstants.INVALID_DEV_HANDLER, new InvalidDevHandler());
        if (handlerNameConstants.flowMonitor) {
            ch.pipeline().addLast(HandlerNameConstants.FLOW_TRAFFIC_HANDLER, new FlowTrafficHandler(1000));
        }
        ch.pipeline().addLast(HandlerNameConstants.DEV_DATA_PARSER_HANDLER, new DevDataPassHandler());
    }
}
