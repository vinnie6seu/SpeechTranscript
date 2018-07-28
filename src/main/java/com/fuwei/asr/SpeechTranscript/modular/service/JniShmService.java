package com.fuwei.asr.SpeechTranscript.modular.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.springframework.beans.factory.annotation.Autowired;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fuwei.asr.SpeechTranscript.config.SpeechProcessCfg;
import com.fuwei.asr.SpeechTranscript.constant.MrcpClientTypeEnum;
import com.fuwei.asr.SpeechTranscript.modular.entity.AsrShmResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JniShmService {
//	private static final Logger log = LoggerFactory.getLogger(JniShmService.class);
	
	@Autowired
	private SpeechProcessCfg speechProcessCfg;
	
	/**
	 * 加载动态库，虚拟机的生命周期类JniShmService只被加载一次，static代码块仅在类被加载的时候执行一次
	 */
	static {
		System.loadLibrary("JniShm");
	}
	
	/**
	 * 加载共享内存
	 */
	private native void JNI_shmInit(int shmSpeechToTextKey, int shmSpeechToTextNum, int mrcpClientType); 
	
	/**
	 * 动态库中读取共享内存语音数据，废弃
	 */
	private native String JNI_readSpeechRecordShm(int id);
	
	/**
	 * 动态库中读取共享内存语音数据，返回 byte[]，因为语言数据中可能有截断符
	 */
	private native byte[] JNI_readSpeechRecordByteArrShm(int id);
	
	/**
	 * 动态库中写入共享内存文本数据
	 */
	private native void JNI_writeTextRecordShm(int id, String text);
	
	/**
	 * 卸载共享内存
	 */
	private native void JNI_shmTerm(); 
	
	//////////////////////////////////////////////////////////////////////////////////
	
	private native byte[] JNI_shmSpeechPacketReceive(int id, boolean is_complete_send, int total_send_packet_num, Integer batch_num, Integer is_complete_receive);
	
	private native void JNI_shmTextPacketSend(int id, AsrShmResponse asrResponse);
	
	private native void JNI_shmTextPacketSend(int id, String asrResponseJsonStr);
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public void shmInit() {

		int shmSpeechToTextKey = speechProcessCfg.getShmSpeechToTextKey();
		int shmSpeechToTextNum = speechProcessCfg.getShmSpeechToTextNum();
		String speechToTextClientType = speechProcessCfg.getSpeechToTextClientType();
		
        if (shmSpeechToTextKey == 0 || shmSpeechToTextNum == 0 || speechToTextClientType.isEmpty()) {
        	log.error("in SpeechProcess.cfg can not find [SHM SPEECH_TO_TEXT_KEY] or [SHM SPEECH_TO_TEXT_NUM] or [SHM SPEECH_TO_TEXT_CLIENT_TYPE]");
        	return ;
        }
		
		// 通过 JNI 调用 c 代码装载共享内存
		log.info(String.format("start to load shm shmSpeechToTextKey:[%d] shmSpeechToTextNum:[%d]", shmSpeechToTextKey, shmSpeechToTextNum));
		
		if (MrcpClientTypeEnum.CLIENT_TYPE_ASR.toString().equals(speechToTextClientType)) {
			JNI_shmInit(shmSpeechToTextKey, shmSpeechToTextNum, MrcpClientTypeEnum.CLIENT_TYPE_ASR.value()); 
		} else if (MrcpClientTypeEnum.CLIENT_TYPE_ASR_PACKET.toString().equals(speechToTextClientType)) {
			JNI_shmInit(shmSpeechToTextKey, shmSpeechToTextNum, MrcpClientTypeEnum.CLIENT_TYPE_ASR_PACKET.value()); 
		}
		
	}

	public String readSpeechRecordShm(Integer id) {
		// TODO Auto-generated method stub
		log.info(String.format("start to read speech in shm id:[%d]", id));
		
		return JNI_readSpeechRecordShm(id);
	}
	
	public byte[] readSpeechRecordByteArrShm(Integer id) {
		// TODO Auto-generated method stub
		log.info(String.format("start to read speech in shm id:[%d]", id));
		
		return JNI_readSpeechRecordByteArrShm(id);
	}	

	public void writeTextRecordShm(Integer id, String text) {
		// TODO Auto-generated method stub
		log.info(String.format("start to write text in shm id:[%d] textLen:[%d]", id, text.length()));
		
		JNI_writeTextRecordShm(id, text);
	}
	
	//////////////////////////////////////////////////////////////////////////////////	
	
	public byte[] shmSpeechPacketReceive(Integer id, boolean is_complete_send, int total_send_packet_num, Integer batch_num, Integer is_complete_receive) {
		log.info(String.format("start to receive speech packet in shm id:[%d]", id));
		return JNI_shmSpeechPacketReceive(id, is_complete_send, total_send_packet_num, batch_num, is_complete_receive);
	}
	
	public void shmTextPacketSend(Integer id, AsrShmResponse asrResponse) {
		log.info(String.format("start to send text packet in shm id:[%d]", id));
		JNI_shmTextPacketSend(id, asrResponse);
	}
	
	public void shmTextPacketSend(Integer id, String asrResponseJsonStr) {
		log.info(String.format("start to send text packet in shm id:[%d]", id));
		JNI_shmTextPacketSend(id, asrResponseJsonStr);
	}	
	
	//////////////////////////////////////////////////////////////////////////////////	

	public void shmTerm() {
		log.info("start to delete shm");
		
		JNI_shmTerm(); 
	}
	
	public void printLogInfo(String info) {
		if (log == null) {
			System.out.println("log is null");
			return ;
		}
		if (info == null) {
			System.out.println("info is null");
			return ;
		}		
		log.info(info);
	}
	
	public void printLogError(String error) {
		if (log == null) {
			System.out.println("log is null");
			return ;
		}
		if (error == null) {
			System.out.println("error is null");
			return ;
		}			
		log.error(error);
	}	
}
