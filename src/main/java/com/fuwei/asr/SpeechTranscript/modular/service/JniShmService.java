package com.fuwei.asr.SpeechTranscript.modular.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JniShmService {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 加载动态库
	 */
	static {
		System.loadLibrary("JniShm");
	}
	
	/**
	 * 加载共享内存
	 */
	private native void JNI_shmInit(int shmSpeechToTextKey, int shmSpeechToTextNum); 
	
	/**
	 * 动态库中读取共享内存语音数据
	 */
	private native String JNI_readSpeechRecordShm(int id);
	
	/**
	 * 动态库中写入共享内存文本数据
	 */
	private native void JNI_writeTextRecordShm(int id, String text);
	
	/**
	 * 卸载共享内存
	 */
	private native void JNI_shmTerm(); 
	
	/**
	 * 让 c 代码调用 printLogInfo 函数，输出 info 日志
	 */
	private native void JNI_callback_printLogInfo(String info);
	
	/**
	 * 让 c 代码调用 printLogError 函数，输出 error 日志
	 */
	private native void JNI_callback_printLogError(String error);
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public void shmInit() {
		JNI_shmInit(55997, 1000); 
	}

	public String readSpeechRecordShm(Integer id) {
		// TODO Auto-generated method stub
		return JNI_readSpeechRecordShm(id);
	}

	public void writeTextRecordShm(Integer id, String text) {
		// TODO Auto-generated method stub
		JNI_writeTextRecordShm(id, text);
	}

	public void shmTerm() {
		JNI_shmTerm(); 
	}
	
	public void printLogInfo(String info) {
		log.info(info);
	}
	
	public void printLogError(String error) {
		log.error(error);
	}	
}
