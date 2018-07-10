package com.fuwei.asr.SpeechTranscript.modular.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fuwei.asr.SpeechTranscript.modular.service.JniShmService;

@Component
public class ShmInit implements CommandLineRunner {

	@Autowired
	private JniShmService jniShmService;

	@Override
	public void run(String... args) throws Exception {
		// 装载共享内存
		jniShmService.shmInit();
	}

}
