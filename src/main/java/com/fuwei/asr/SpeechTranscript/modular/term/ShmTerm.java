package com.fuwei.asr.SpeechTranscript.modular.term;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import com.fuwei.asr.SpeechTranscript.modular.service.JniShmService;

@Component
public class ShmTerm implements DisposableBean, ExitCodeGenerator {

	@Autowired
	private JniShmService jniShmService;

	@Override
	public void destroy() throws Exception {
		// 卸载共享内存
		jniShmService.shmTerm();
	}

	@Override
	public int getExitCode() {

		return 0;
	}
}
