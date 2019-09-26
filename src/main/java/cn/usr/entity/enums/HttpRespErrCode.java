package cn.usr.entity.enums;

/**
 * http api 操作返回的错误码
 * 
 * @author liu
 *
 */
public enum HttpRespErrCode {
	SUCCESS(0,"OK"),//操作成功
	
	//返回公共错误
	ERR_404(404,"404"),
	
	//-------------账号、密码、用户相关-------------------	
	//账号无效，不符合格式
	ACCOUNT_INVALID(1001,"account invalid"),
	//密码无效
	PWD_INVALID(1002,"password invalid"),
	//账号不存在
	ERROR_ACCOUNT(1003,"account not found"),
	//密码不正确
	ERROR_PWD(1004,"password error"),
	//没有缓存该用户
	USER_NOT_CATCH(1005,"not catch this user"),
	//没有权限
	USER_NO_AUTHORITY(1006,"No Authority"),
	//----------------泛指、公共的错误码---------------
	//泛指参数不正确
	PARAM_INVALID(2001,"parameter invalid"),
	
	
	//----------------设备操作相关------------------
	//没有一个设备
	DEV_NONE(3001,"none devices"),
	//没有缓存该设备
	DEV_NOT_CATCH(3002,"not catch this device");

	
	private int errCode;
	private String errInfo;

	private HttpRespErrCode(int errCode, String errInfo) {
		this.errCode = errCode;
		this.errInfo = errInfo;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public int getErrCode() {
		return errCode;
	}
	
	public void setErrInfo(String errInfo) {
		this.errInfo = errInfo;
	}
	
	public String getErrInfo() {
		return errInfo;
	}
}