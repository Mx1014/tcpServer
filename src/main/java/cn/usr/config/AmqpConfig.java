package cn.usr.config;


import cn.usr.service.CloudService;
import cn.usr.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;


/**
 * @Package: cn.usr.config
 * @Description: RabbitMQ测试
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-05-09 09:27
 */
@Configuration
@Slf4j
public class AmqpConfig {


    @Value("${mq.exchange.exchange_tcp}")
    private String exchangeTcp;

    @Value("${mq.exchange.exchange_cache}")
    private String exchangeCache;

    @Value("${mq.queue.center_tcp}")
    private String centerToTcp;

    @Value("${mq.queue.tcp_cache}")
    private String TCPUpdateCache;

    @Value("${mq.routingkey.to_device_data}")
    private String toDeviceData;

    @Value("${mq.routingkey.cache_device}")
    private String cacheDevice;

    @Value("${mq.routingkey.cache_device_online}")
    private String cacheDeviceOnline;


    @Value("${mq.addresses}")
    private String addresses;

    @Value("${mq.port}")
    private int port;

    @Value("${mq.username}")
    private String username;

    @Value("${mq.password}")
    private String password;

    private CloudService cloudService;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        // 设置虚拟主机
        connectionFactory.setVirtualHost("/");
        // 设置发布确认
        connectionFactory.setPublisherConfirms(false);
        return connectionFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public DirectExchange tcpExchange() {
        return new DirectExchange(exchangeTcp, false, false);
    }


    @Bean
    public FanoutExchange Exchange() {
        return new FanoutExchange(exchangeCache, false, false);
    }


    @Bean
    public Binding bindingQueue() {
        return BindingBuilder.bind(Queue()).to(Exchange());
    }

    @Bean
    public Queue Queue() {
        return new Queue(TCPUpdateCache, false, false, false, null);
    }


    @Bean
    public Queue centerToTcpQueue() {
        return new Queue(centerToTcp, false, false, false, null);
    }

    @Bean
    public Binding bindingTcpQueue() {
        return BindingBuilder.bind(centerToTcpQueue()).to(tcpExchange()).with(toDeviceData);
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(centerToTcpQueue(), Queue());
        container.setExposeListenerChannel(true);
        container.setConsumerStartTimeout(500);
        container.setMaxConcurrentConsumers(4);
        container.setConcurrentConsumers(4);
        container.setAcknowledgeMode(AcknowledgeMode.NONE);
        container.setMessageListener(message -> {
            try {
                if (cloudService == null) {
                    cloudService = SpringUtil.getBean(CloudService.class);
                }
                String receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();

                log.info("[RabbitMq ]消息 RoutingKey :{}", receivedRoutingKey);
                // 比较是删除设备的消息还是设备传输的key
                if (receivedRoutingKey.equals(cacheDevice)) {
                    cloudService.clearDeviceCache(message.getBody());
                } else if (receivedRoutingKey.equals(toDeviceData)) {
                    cloudService.sendDataToDevice(message.getBody());
                } else if (receivedRoutingKey.equals(cacheDeviceOnline)) {
                    cloudService.deleteDeviceCache(message.getBody());
                }
            } catch (Exception e) {
                log.error("接受来自rabbitmq的数据异常总catch :{}", e);
            }

        });
        return container;
    }


}


