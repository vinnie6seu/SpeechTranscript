package com.fuwei.asr.SpeechTranscript;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 启动入口
 */
@SpringBootApplication(scanBasePackages = {"com.fuwei.asr.SpeechTranscript"})
@EnableCaching
public class SpeechTranscriptApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeechTranscriptApplication.class, args);
	}
}
