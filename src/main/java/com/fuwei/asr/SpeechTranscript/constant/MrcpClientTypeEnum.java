package com.fuwei.asr.SpeechTranscript.constant;

/**
 * asr 语音请求的模式
 */
public enum MrcpClientTypeEnum {

	CLIENT_TYPE_TTS(0), CLIENT_TYPE_ASR(1), CLIENT_TYPE_ASR_PACKET(2);

	private int value = 0;

	private MrcpClientTypeEnum(int value) { // 必须是private的，否则编译错误
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}
