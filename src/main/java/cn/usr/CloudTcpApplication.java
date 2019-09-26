package cn.usr;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.management.ManagementFactory;

/**
 * 2018年12月透传云TCP类型设备通讯服务
 *
 * @author 石志远
 * <p>
 * TCP服务器文档资料地址：http://showdoc.usr.cn/web/#/74?page_id=4050
 */
@SpringBootApplication(scanBasePackages = {"cn.usr"})
@MapperScan("cn.usr.dao")
@Slf4j
public class CloudTcpApplication {


    public static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {

        applicationContext = SpringApplication.run(CloudTcpApplication.class, args);
        log.info("透传云TCP Server 开始启动");

        String name = ManagementFactory.getRuntimeMXBean().getName();
        // get pid
        String pid = name.split("@")[0];

        log.info("[透传云 TCP Server 的进程号为]:===========>{}", pid);


        // bossGroup用来监控tcp链接,boss执行 server.accept()操作
        // 使用建议在监听一个端口的情况下使用一个线程足以
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        // workerGroup用来处理io事件,处理事件的读写到业务逻辑处理等后续操作
        // 见源码 MultithreadEventLoopGroup 默认线程数是 cpu核心数的2倍
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 需要设置操作系统的keepAlive，不使用TCP底层的心跳，应由上层应用来检测
                    .childOption(ChannelOption.SO_KEEPALIVE, false)
                    // 禁用Negla算法，有数据立即发送
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 设备写高低水位
//                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
//                            // 流量控制 采用默认
//                            // Netty参数，写高水位标记，默认值64KB。如果Netty的写缓冲区中的字节超过该值，Channel的isWritable()返回False。
//                            // Netty参数，写低水位标记，默认值32KB。当Netty的写缓冲区中的字节超过高水位之后若下降到低水位，则Channel的isWritable()返回True。写高低水位标记使用户可以控制写入数据速度，从而实现流量控制。推荐做法是：每次调用channl.write(msg)方法首先调用channel.isWritable()判断是否可写
//                            new WriteBufferWaterMark(65536, 32769)
//                    )
                    // 初始化Handler
                    // 服务器设备接入处理流程图：https://www.processon.com/view/link/5c1c4191e4b095ccfee9701c
                    .childHandler(new RouteInitializer())
                    // 用于临时存放已完成三次握手的请求的队列的最大长度
                    // Socket参数，服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。
                    .option(ChannelOption.SO_BACKLOG, 1024);

            // 监听15000端口
            bootstrap.bind(15000).sync();

            log.info("[透传云 Server] DEVICE TcpServer 启动成功 端口为port:{}", 15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
        }

    }
}
