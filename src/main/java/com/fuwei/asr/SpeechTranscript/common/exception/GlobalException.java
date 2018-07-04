package com.fuwei.asr.SpeechTranscript.common.exception;

import com.fuwei.asr.SpeechTranscript.constant.CodeMsgEnum;

public class GlobalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	// 错误码
	private Integer code;

	// 提示信息
	private String msg;

	public GlobalException(CodeMsgEnum cm) {
		super(cm.toString());
		this.code = cm.getCode();
		this.msg = cm.getMsg();
	}

	public GlobalException(CodeMsgEnum cm, Object... args) {
		super(cm.toString());
		this.code = cm.getCode();
		this.msg = String.format(cm.getMsg(), args);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
