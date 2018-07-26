package com.fuwei.asr.SpeechTranscript.modular.entity;

public class AsrShmResponse {
	private AsrShmResponseItem _cur_result;
	private AsrShmResponseItem _cur_predict;

	private boolean is_exception;
	private String exception_str;

	public AsrShmResponseItem get_cur_result() {
		return _cur_result;
	}

	public void set_cur_result(AsrShmResponseItem _cur_result) {
		this._cur_result = _cur_result;
	}

	public AsrShmResponseItem get_cur_predict() {
		return _cur_predict;
	}

	public void set_cur_predict(AsrShmResponseItem _cur_predict) {
		this._cur_predict = _cur_predict;
	}

	public boolean isIs_exception() {
		return is_exception;
	}

	public void setIs_exception(boolean is_exception) {
		this.is_exception = is_exception;
	}

	public String getException_str() {
		return exception_str;
	}

	public void setException_str(String exception_str) {
		this.exception_str = exception_str;
	}

	@Override
	public String toString() {
		return "AsrResponse [_cur_result=" + _cur_result + ", _cur_predict=" + _cur_predict + ", is_exception="
				+ is_exception + ", exception_str=" + exception_str + "]";
	}

}