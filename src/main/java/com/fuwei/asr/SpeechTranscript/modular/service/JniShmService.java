package com.fuwei.asr.SpeechTranscript.modular.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fuwei.asr.SpeechTranscript.util.ConfigReaderUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JniShmService {
//	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
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
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public void shmInit() {
		// 找到 SpeechProcess.cfg 全路径
		String configFullPath = "";
		Map<String, String> map = System.getenv();
		if (map.containsKey("CONFIG_PATH")) {
			String key = "CONFIG_PATH";
			configFullPath = map.get(key) + "SpeechProcess.cfg";
			
			log.info(String.format("SpeechProcess.cfg full path is [%s]", configFullPath));
		} else {
			log.error("can not find SpeechProcess.cfg");
			return ;
		}
		
		// 从配置文件 SpeechProcess.cfg 中读到 shmSpeechToTextKey、shmSpeechToTextNum
		ConfigReaderUtil configReaderUtil = new ConfigReaderUtil(configFullPath);
		
		Map<String, Map<String, List<String>>> cfg = configReaderUtil.get();
		for (Map.Entry<String, Map<String, List<String>>> entry : cfg.entrySet()) {
			log.info(String.format("section:", entry.getKey()));
			for (Map.Entry<String, List<String>> item : entry.getValue().entrySet()) {
				log.info(String.format("%s = %s", item.getKey(), item.getValue().toString()));
			}
		}
		
		List<String> keyList = configReaderUtil.get("SHM", "SPEECH_TO_TEXT_KEY");
		if (keyList.isEmpty()) {
			log.error("can not find [SHM SPEECH_TO_TEXT_KEY] in SpeechProcess.cfg");
			return ;			
		}
		int shmSpeechToTextKey = Integer.valueOf(keyList.get(0));
		
		List<String> numList = configReaderUtil.get("SHM", "SPEECH_TO_TEXT_NUM");
		if (numList.isEmpty()) {
			log.error("can not find [SHM SPEECH_TO_TEXT_NUM] in SpeechProcess.cfg");
			return ;			
		}		
		int shmSpeechToTextNum = Integer.valueOf(numList.get(0));
		
		// 通过 JNI 调用 c 代码装载共享内存
		log.info(String.format("start to load shm shmSpeechToTextKey:[%d] shmSpeechToTextNum:[%d]", shmSpeechToTextKey, shmSpeechToTextNum));
		JNI_shmInit(shmSpeechToTextKey, shmSpeechToTextNum); 
	}

	public String readSpeechRecordShm(Integer id) {
		// TODO Auto-generated method stub
		log.info(String.format("start to read speech in shm id:[%d]", id));
		
		return JNI_readSpeechRecordShm(id);
	}

	public void writeTextRecordShm(Integer id, String text) {
		// TODO Auto-generated method stub
		log.info(String.format("start to write text in shm id:[%d] textLen:[%d]", id, text.length()));
		
		JNI_writeTextRecordShm(id, text);
	}

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
