package cn.usr.entity.enums;


/**
 * @author liu
 */
public enum RegisterErrCode {
    // 注册成功
    REG_SUCC("register success", new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x06, (byte) 0xF9}),

    // 注册包不合法
    REG_DATA_INVALID("register packet invalid",
            new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x31, (byte) 0xCE}),
    // 通信密码错误
    REG_COMPASS_ERR("communicate password error",
            new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x32, (byte) 0xCD}),
    // 设备不存在
    REG_DEVICE_NOT_EXIST("device not exist",
            new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x33, (byte) 0xCC}),

    // 设备被顶替
    REG_DEVICE_REPLACE("device replaced", new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x34, (byte) 0xCB}),

    // 无权限
    REG_WITHOUT_PERMISSION("without permission",
            new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x35, (byte) 0xCA}),

    // 用户不存在
    REG_USER_NOT_EXIST("user not exist", new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x36, (byte) 0xC9}),

    // 设备已经在线，不允许被顶替
    REG_DEVICE_HAS_ONLINE("device already connected",
            new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x37, (byte) 0xC8}),

    // 超过心跳时间无数据发送
    REG_DEVICE_LOST_KEEPLIVE("device lost keepalive",
            new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x37, (byte) 0xC6}),

    //设备连接次数过于频繁
    REG_DEVICE_BAD_CONNECT("device bad connect", new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x38, (byte) 0xC7}),


    // 目标设备已经离线
    REG_TARGET_DEVICE_OFFLINE("target device offline",
            new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x25, (byte) 0xDA}),
    // 目标透传组不存在
    REG_TERGET_GROUP_NOT_EXIST("targer group not exist",
            new byte[]{(byte) 0xE3, (byte) 0x8E, 0x38, 0x00, 0x01, 0x26, (byte) 0xD9});


    private byte[] respCode;
    private String errInfo;


    RegisterErrCode(String errInfo, byte[] respCode) {
        this.respCode = respCode;
        this.errInfo = errInfo;
    }



    public String getErrInfo() {
        return errInfo;
    }

    public byte[] getRespCode() {
        return respCode;
    }
}
