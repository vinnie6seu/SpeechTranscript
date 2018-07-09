package com.fuwei.asr.SpeechTranscript.modular.service;

import org.springframework.stereotype.Service;

@Service
public class JniShmService {
	
	/**
	 * 加载动态库
	 */
	static {
		// System.setProperty("java.library.path", "/home/vinnie/my_project/SpeechProcess/lib");
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
}
