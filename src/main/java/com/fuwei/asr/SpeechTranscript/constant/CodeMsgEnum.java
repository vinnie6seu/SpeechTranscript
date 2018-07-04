package com.fuwei.asr.SpeechTranscript.constant;

/**
 * 显示给前端的信息
 */
public enum CodeMsgEnum {

	SERVER_SUCCESS(0, "成功"), 
	QUERY_SUCCESS(0, "查询成功"), 
	ADD_SUCCESS(0, "添加成功"), 
	UPDATE_SUCCESS(0, "更新成功"), 
	DELETE_SUCCESS(0, "删除成功"),

	SERVER_ERROR(500, "未知服务端错误"),

	// ================================= 登录模块 =================================
	LOGIN_SUCCESS(0, "登录成功"),

	LOGIN_PARA_ERROR(500100, "登录参数校验异常：%s"), 
	LOGIN_NO_PHONE(500101, "手机号【%s】未注册"), 
	LOGIN_NO_PASSWD(500102, "密码【%s】错误"), 
	LOGIN_NEED(500103, "请先到登录页面完成登录再进行操作"), 
	LOGIN_SESSION_LOST(500104, "会话已经失效，请重新登录"),

	// ================================= 注销模块 =================================
	LOGOUT_SUCCESS(0, "注销成功"),

	// ================================= 分布式session =================================
	SESSION_SUCCESS(0, "获取session成功"),

	SESSION_ERROR(500301, "未能与请求建立起session"),

	// ================================= websocket异步通知 =================================
	WEB_SOCKET_SUCCESS(0, "websocket异步通知成功"),

	// ================================= 日志模块 =================================
	LOG_SUCCESS(0, "日志输出成功"),

	LOG_WARPPER_FAIL(500501, "日志包裹类创建失败");

	// 错误码
	private final Integer code;

	// 提示信息
	private final String msg;

	private CodeMsgEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	// public void fillArgs(Object... args) {
	// this.msg = String.format(this.msg, args);
	// }

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}
