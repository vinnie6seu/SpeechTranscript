package com.fuwei.asr.SpeechTranscript.modular.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JniShmService {
//	private static final Logger log = LoggerFactory.getLogger(JniShmService.class);
	
	
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
		int shmSpeechToTextKey;
		int shmSpeechToTextNum;
		
        Config cfg = new Config();  
        // 设置Section允许出现重复  
        cfg.setMultiSection(true);  
        Ini ini = new Ini();  
        ini.setConfig(cfg);  
        try {  
            // 加载配置文件  
            ini.load(new File(configFullPath));  

            Section section = ini.get("SHM");  
            String shmSpeechToTextKeyStr = section.get("SPEECH_TO_TEXT_KEY");  
            String shmSpeechToTextNumStr = section.get("SPEECH_TO_TEXT_NUM"); 
            
            if (shmSpeechToTextKeyStr.isEmpty() || shmSpeechToTextNumStr.isEmpty()) {
            	log.error("in SpeechProcess.cfg can not find [SHM SPEECH_TO_TEXT_KEY] or [SHM SPEECH_TO_TEXT_NUM]");
            	return ;
            }
            
    		shmSpeechToTextKey = Integer.valueOf(shmSpeechToTextKeyStr);
    		shmSpeechToTextNum = Integer.valueOf(shmSpeechToTextNumStr);
        } catch (IOException e) {  
            log.error(e.toString());
            return ;
        } 		

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
