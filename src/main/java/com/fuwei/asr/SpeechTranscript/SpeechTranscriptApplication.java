package com.fuwei.asr.SpeechTranscript;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动入口
 */
@SpringBootApplication(scanBasePackages = {"com.fuwei.asr.SpeechTranscript"})
public class SpeechTranscriptApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeechTranscriptApplication.class, args);
	}
}
