package com.fuwei.asr.SpeechTranscript.common.form;

public class HttpJson {
	Integer id;                   
    Integer speechOffset;        
    Integer speechLen;            
    Integer asrFlag;             
    String fileName;
    
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
	
	@Override
	public String toString() {
		return "HttpJson [id=" + id + ", speechOffset=" + speechOffset + ", speechLen=" + speechLen + ", asrFlag="
				+ asrFlag + ", fileName=" + fileName + "]";
	}     
}
