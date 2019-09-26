package cn.usr.entity;

import cn.usr.entity.enums.ConnectionType;
import cn.usr.entity.enums.RegisterErrCode;
import lombok.Data;

/**
 * 注册结果类
 * @author shizhiyuan
 */
@Data
public class ValidResult {

    /**
     * 注册返回码
     */
    public final RegisterErrCode registerCode;
    /**
     * 连接类型
     */
    public final ConnectionType connectionType;

    /**
     * 设备ID
     */
    public final String devId;


}
