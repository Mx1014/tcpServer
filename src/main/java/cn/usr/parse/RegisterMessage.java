package cn.usr.parse;


import cn.usr.entity.enums.ConnectionType;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class RegisterMessage {

    /**
     * 连接类型
     */
//    public final ConnectionType connectionType;


    /**
     * 唯一标识，可以是devId、groupId、account
     */

    public final String deviceId;
    /**
     * 通信密码
     */
    public final String passCode;


}
