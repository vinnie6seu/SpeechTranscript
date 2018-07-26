package com.fuwei.asr.SpeechTranscript.common.form;

public class HttpJson {
	Integer id;
	Integer speechOffset;
	Integer speechLen;
	Integer asrFlag;
	String fileName;
	Integer asrSpeechPackStatus;
	Integer curSendPacketNum;
	Integer totalSendPacketNum;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSpeechOffset() {
		return speechOffset;
	}
	public void setSpeechOffset(Integer speechOffset) {
		this.speechOffset = speechOffset;
	}
	public Integer getSpeechLen() {
		return speechLen;
	}
	public void setSpeechLen(Integer speechLen) {
		this.speechLen = speechLen;
	}
	public Integer getAsrFlag() {
		return asrFlag;
	}
	public void setAsrFlag(Integer asrFlag) {
		this.asrFlag = asrFlag;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getAsrSpeechPackStatus() {
		return asrSpeechPackStatus;
	}
	public void setAsrSpeechPackStatus(Integer asrSpeechPackStatus) {
		this.asrSpeechPackStatus = asrSpeechPackStatus;
	}
	public Integer getCurSendPacketNum() {
		return curSendPacketNum;
	}
	public void setCurSendPacketNum(Integer curSendPacketNum) {
		this.curSendPacketNum = curSendPacketNum;
	}
	public Integer getTotalSendPacketNum() {
		return totalSendPacketNum;
	}
	public void setTotalSendPacketNum(Integer totalSendPacketNum) {
		this.totalSendPacketNum = totalSendPacketNum;
	}
	
	@Override
	public String toString() {
		return "HttpJson [id=" + id + ", speechOffset=" + speechOffset + ", speechLen=" + speechLen + ", asrFlag="
				+ asrFlag + ", fileName=" + fileName + ", asrSpeechPackStatus=" + asrSpeechPackStatus
				+ ", curSendPacketNum=" + curSendPacketNum + ", totalSendPacketNum=" + totalSendPacketNum + "]";
	}    	
}
