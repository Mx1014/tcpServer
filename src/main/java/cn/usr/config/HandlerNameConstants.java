package cn.usr.config;

import io.netty.util.AttributeKey;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author liu
 * @date 2018-03-30.
 */
@Component
@Data
public class HandlerNameConstants {


    /**
     * 设备注册处理handle
     */
    public static final String DEV_REGISTER_DECODER = "DevRegisterDecoder";

    /**
     * 数据处理handle
     */
    public static final String DEV_DATA_PARSER_HANDLER = "DevDataPassHandler";

    /**
     * 流量监控处理handle
     */
    public static final String FLOW_TRAFFIC_HANDLER = "FlowTrafficHandler";


    /**
     * 异常设备处理 handle
     */
    public static final String INVALID_DEV_HANDLER = "InvalidDevHandler";

    public static final String DEV_IDLE_HANDLER = "Idle";

    public static final String TEMP_REGISTER_DECODER = "TempRegisterDecoder";

    public static final String TEMP_DATAPSS_HANDLER = "TempDataPassHandler";

    public static final String SSL_HANDLER = "ssl";


    public static final String BAD_COUNT_IDLE_HANDLER = "BadCountIdle";
    public static final String BAD_COUNT_INVALID_HANDLER = "BadCountInvalidHandler";

    public static final String TRAFFIC_SHAPPING_HANDLER = "TrafficShappingHandler";


    /**
     * Channel ID 和 设备ID 关联键
     */
    public static final String CHANNELID_DEVID = "channelId-deviceId";

    /**
     * 设备缓存hash key
     */
    public static final String DEVICE_CACHE_HASH_KEY = "TcpDeviceCache";


    /**
     * 设备在线列表总库
     */
    public static final String ONLINE_DEVICE = "online_dev";

    /**
     * 设备透传组列表hash key
     */
    public static final String DEVICE_GROUP_IDS_CACHE_HASH_KEY = "TcpDeviceToGroupIdsCache";

    /**
     * 设备透传组列表hash key
     */
    public static final String DEVICE_GROUP_DIDS_CACHE_HASH_KEY = "TcpDeviceToGroupDidsCache";

    /**
     * Redis 设备连接次数 key
     */
    public static final String DEVICE_CONNECTNUMBER_KEY = "TCP:Device:connectNumber:";

    /**
     * Redis 设备上传流量Key次数 key
     */
    public static final String DEVICE_READ_FLOW_KEY = "TcpDeviceReadFlow";

    /**
     * Redis 设备上传流量Key次数 key
     */
    public static final String DEVICE_WRITE_FLOW_KEY = "TcpDeviceWriteFlow";


    /**
     * 用户缓存hash key
     */
    public static final String USER_CACHE_HASH_KEY = "TcpUserCache";

    public static final AttributeKey<Object> objectAttributeKey = AttributeKey.valueOf(CHANNELID_DEVID);

    /**
     * 设备频繁连接次数限制，先读取配置文件，默认为20
     */
    @Value("${cloud.device.connect_number:20}")
    public int connectNumber;

    @Value("${cloud.flowmonitor:false}")
    public Boolean flowMonitor;

    /**
     * 设备主动读心跳设置
     */
    @Value("${cloud.device.reader_idle_time:600}")
    private int readerIdleTime;


    private HandlerNameConstants() {
    }
}
