package com.fuwei.asr.SpeechTranscript.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.springframework.stereotype.Component;

import com.fuwei.asr.SpeechTranscript.common.bean.SpringContextHolder;
import com.fuwei.asr.SpeechTranscript.modular.service.ShmPacketService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SpeechProcessCfg {
	
	private Config cfg;
	private Ini ini;
	
	private int shmSpeechToTextKey;
	private int shmSpeechToTextNum;
	private String speechToTextClientType;
	
	private String languageCode;
	private int sampleRate;
	
	/**
	 * 
	 * @return
	 */
    public static SpeechProcessCfg me() {
        return SpringContextHolder.getBean(SpeechProcessCfg.class);
    }	

	public SpeechProcessCfg() {
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
		
        cfg = new Config();  
        // 设置Section允许出现重复  
        cfg.setMultiSection(true);  
        ini = new Ini();  
        ini.setConfig(cfg);  
        try {  
        	
            // 1.加载配置文件  
            ini.load(new File(configFullPath));  

            // 2.SHM - SPEECH_TO_TEXT_KEY, SPEECH_TO_TEXT_NUM, SPEECH_TO_TEXT_CLIENT_TYPE
            Section section = ini.get("SHM");  
            String shmSpeechToTextKeyStr = section.get("SPEECH_TO_TEXT_KEY");  
            String shmSpeechToTextNumStr = section.get("SPEECH_TO_TEXT_NUM"); 
            speechToTextClientType = section.get("SPEECH_TO_TEXT_CLIENT_TYPE"); 
            
            if (shmSpeechToTextKeyStr.isEmpty() || shmSpeechToTextNumStr.isEmpty() || speechToTextClientType.isEmpty()) {
            	log.error("in SpeechProcess.cfg can not find [SHM SPEECH_TO_TEXT_KEY] or [SHM SPEECH_TO_TEXT_NUM] or [SHM SPEECH_TO_TEXT_CLIENT_TYPE]");
            	return ;
            }
            
    		shmSpeechToTextKey = Integer.valueOf(shmSpeechToTextKeyStr);
    		shmSpeechToTextNum = Integer.valueOf(shmSpeechToTextNumStr);
    		
    		// 3.SPEECH - LANGUAGE_CODE, SAMPLE_RATE
    		section = ini.get("SPEECH");
    		languageCode = section.get("LANGUAGE_CODE");
    		String sampleRateNumStr = section.get("SAMPLE_RATE"); 
    		
            if (languageCode.isEmpty() || sampleRateNumStr.isEmpty()) {
            	log.error("in SpeechProcess.cfg can not find [SHM LANGUAGE_CODE] or [SHM SAMPLE_RATE]");
            	return ;
            }    		
    		
    		sampleRate = Integer.valueOf(sampleRateNumStr);
    		
        } catch (IOException e) {  
            log.error(e.toString());
            return ;
        } 			
	}

	public Config getCfg() {
		return cfg;
	}

	public void setCfg(Config cfg) {
		this.cfg = cfg;
	}

	public Ini getIni() {
		return ini;
	}

	public void setIni(Ini ini) {
		this.ini = ini;
	}

	public int getShmSpeechToTextKey() {
		return shmSpeechToTextKey;
	}

	public void setShmSpeechToTextKey(int shmSpeechToTextKey) {
		this.shmSpeechToTextKey = shmSpeechToTextKey;
	}

	public int getShmSpeechToTextNum() {
		return shmSpeechToTextNum;
	}

	public void setShmSpeechToTextNum(int shmSpeechToTextNum) {
		this.shmSpeechToTextNum = shmSpeechToTextNum;
	}

	public String getSpeechToTextClientType() {
		return speechToTextClientType;
	}

	public void setSpeechToTextClientType(String speechToTextClientType) {
		this.speechToTextClientType = speechToTextClientType;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public static org.slf4j.Logger getLog() {
		return log;
	}

	@Override
	public String toString() {
		return "SpeechProcessCfg [cfg=" + cfg + ", ini=" + ini + ", shmSpeechToTextKey=" + shmSpeechToTextKey
				+ ", shmSpeechToTextNum=" + shmSpeechToTextNum + ", speechToTextClientType=" + speechToTextClientType
				+ ", languageCode=" + languageCode + ", sampleRate=" + sampleRate + "]";
	}
}
