package cn.usr.entity.enums;

import org.springframework.stereotype.Component;

/**
 * 连接类型
 * @author liu
 */

public enum ConnectionType {
	//分表表示  设备连接、临时设备连接、临时分组连接
	DEV_CONNECTION,TEMP_DEV_CONNECTION,TEMP_GROUP_CONNECTION;
}
