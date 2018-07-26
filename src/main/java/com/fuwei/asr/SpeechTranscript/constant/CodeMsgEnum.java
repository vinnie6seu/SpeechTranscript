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

	// ================================= asr 处理 =================================

	NO_CREATE_QUERY_ID(500100, "没有在服务端创建请求id:[%d]的缓存信息");

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
