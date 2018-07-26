package com.fuwei.asr.SpeechTranscript.constant;

public enum SpeechPacketStatusEnum {
	MSP_AUDIO_INIT(0), MSP_AUDIO_CONTINUE(1), MSP_AUDIO_LAST(2);

	private int value = 0;

	private SpeechPacketStatusEnum(int value) { // 必须是private的，否则编译错误
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}
