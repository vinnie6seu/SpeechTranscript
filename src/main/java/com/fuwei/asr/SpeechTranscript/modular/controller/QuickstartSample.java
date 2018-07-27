/*
 * Copyright 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fuwei.asr.SpeechTranscript.modular.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fuwei.asr.SpeechTranscript.constant.CodeMsgEnum;
import com.fuwei.asr.SpeechTranscript.modular.entity.AsrShmRequest;
import com.fuwei.asr.SpeechTranscript.modular.entity.ResponseApiStreamingObserver;
import com.fuwei.asr.SpeechTranscript.modular.service.ShmPacketService;
import com.fuwei.asr.SpeechTranscript.util.ResultVoUtil;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

@RestController
@RequestMapping("/QuickstartSample")
public class QuickstartSample {
	private static final Logger log = LoggerFactory.getLogger(QuickstartSample.class);

	enum AUDIO_FLAG {
		MSP_AUDIO_SAMPLE_INIT, MSP_AUDIO_SAMPLE_FIRST, MSP_AUDIO_SAMPLE_CONTINUE, MSP_AUDIO_SAMPLE_LAST
	};

	@Autowired
	private ShmPacketService shmPacketService;		
	
	public Integer getId() {
		return 999;
	}
	
	private AsrShmRequest asrShmRequest = null;
	
	public void getResultText() throws InterruptedException, ExecutionException {

		ShmPacketService shmPacketService = ShmPacketService.me();
		
		StreamingRecognizeResponse responses = shmPacketService.requestIdGet(getId()).get_responseObserver().future().get();

		while (responses == null || responses.getResultsList().isEmpty()) {
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				
			}	
			
			log.info("result is empty");
		}
		
		// for (StreamingRecognizeResponse response : responses) {
		// For streaming recognize, the results list has one is_final result (if
		// available) followed
		// by a number of in-progress results (if iterim_results is true) for subsequent
		// utterances.
		// Just print the first result here.
		StreamingRecognitionResult result = responses.getResultsList().get(0);
		// There can be several alternative transcripts for a given chunk of speech.
		// Just use the
		// first (most likely) one here.
		SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
		log.info(String.format("Transcript : %s\n", alternative.getTranscript()));
		// }
	}

	/**
	 * Performs streaming speech recognition on raw PCM audio data.
	 *
	 * @param fileName
	 *            the path to a PCM audio file to transcribe.
	 */
	public void streamingRecognizeFile(SpeechClient speech, byte[] data, AUDIO_FLAG audioFlg, HttpSession httpSession)
			throws Exception, IOException {

		try {
			// 初始化
			if (AUDIO_FLAG.MSP_AUDIO_SAMPLE_INIT == audioFlg) {
				log.info(String.format("current status : %s\n", AUDIO_FLAG.MSP_AUDIO_SAMPLE_INIT.toString()));

				// 1.init，新建键值对<id, [speechClient, 两个观察者, 已收到包数量, 发送完成标识, 总发送包数量]>，发送配置
//				AsrShmRequest asrShmRequest = shmPacketService.requestIdCreate(getId());	
				
				asrShmRequest = shmPacketService.requestIdCreate(getId());	
				
			} else if (AUDIO_FLAG.MSP_AUDIO_SAMPLE_CONTINUE == audioFlg) {

//				AsrShmRequest asrShmRequest = shmPacketService.requestIdGet(getId());
				
				// 2.
				asrShmRequest.continueOnNext(data);
				
				log.info(String.format("current status : %s\n", AUDIO_FLAG.MSP_AUDIO_SAMPLE_CONTINUE.toString()));

			} else if (AUDIO_FLAG.MSP_AUDIO_SAMPLE_LAST == audioFlg) {
		
//				AsrShmRequest asrShmRequest = shmPacketService.requestIdGet(getId());
		 
				// 3.
				asrShmRequest.completeOnNext();

				log.info(String.format("current status : %s\n", AUDIO_FLAG.MSP_AUDIO_SAMPLE_LAST.toString()));
				
				getResultText();
			}

		} finally {
			log.info("success to call streamingRecognizeFile\n");
		}
	}

	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	/**
	 * Demonstrates using the Speech API to transcribe an audio file.
	 */
	@GetMapping("/handle")
	public Object handle(HttpSession httpSession) throws Exception {

		// 初始化客户端
		try (SpeechClient speechClient = SpeechClient.create()) {

			// 文件位置
			String fileName = "/root/qbyao/my_project/SpeechTranscript/src/main/resources/audio.raw";

			// 读取文件数据
			Path path = Paths.get(fileName);
			byte[] data = Files.readAllBytes(path);

			data = byteMerger(data, data);
			// data = byteMerger(data, data);

			// streamingRecognizeFile(speechClient, Arrays.copyOfRange(data, 0,
			// data.length));

			long startTime = System.currentTimeMillis();// 记录开始时间

			// 初始化
			streamingRecognizeFile(speechClient, null, AUDIO_FLAG.MSP_AUDIO_SAMPLE_INIT, httpSession);

			int FRAME_LEN = 3200;
			int len = 0;
			while (len * FRAME_LEN <= data.length) {
				if ((len + 1) * FRAME_LEN > data.length) {

					log.info(String.format("speech from[%d] to[%d]\n", len * FRAME_LEN, data.length));
					streamingRecognizeFile(speechClient, Arrays.copyOfRange(data, len * FRAME_LEN, data.length),
							AUDIO_FLAG.MSP_AUDIO_SAMPLE_CONTINUE, httpSession);

				} else {

					log.info(String.format("speech from[%d] to[%d]\n", len * FRAME_LEN, (len + 1) * FRAME_LEN));
					// 持续发送包数据
					streamingRecognizeFile(speechClient,
							Arrays.copyOfRange(data, len * FRAME_LEN, (len + 1) * FRAME_LEN),
							AUDIO_FLAG.MSP_AUDIO_SAMPLE_CONTINUE, httpSession);

				}
				len++;
				try {
					Thread.sleep(200);
				} catch (Exception e) {
				}
			}

			// 完成
			streamingRecognizeFile(speechClient, null, AUDIO_FLAG.MSP_AUDIO_SAMPLE_LAST, httpSession);

			long endTime = System.currentTimeMillis();// 记录结束时间

			float excTime = (float) (endTime - startTime) / 1000;

			log.info("执行时间：" + excTime + "s");

		}
		
		
		
		return ResultVoUtil.success(CodeMsgEnum.SERVER_SUCCESS);

	}
}