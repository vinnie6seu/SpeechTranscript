package com.fuwei.asr.SpeechTranscript.modular.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.fuwei.asr.SpeechTranscript.common.controller.BaseController;
import com.fuwei.asr.SpeechTranscript.common.form.HttpJson;
import com.fuwei.asr.SpeechTranscript.common.form.User;
import com.fuwei.asr.SpeechTranscript.constant.CodeMsgEnum;
import com.fuwei.asr.SpeechTranscript.modular.service.JniShmService;
import com.fuwei.asr.SpeechTranscript.util.ResultVoUtil;
//Imports the Google Cloud client library
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

/**
 * 整合 Google 提供的 ASR API 为 MRCP 提供调用服务
 * 
 * @author yaoqiaobing
 */

// @RestController 要求返回报文均是 json 格式
@RestController
@RequestMapping("/AsrGoogle")
public class AsrGoogleController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(AsrGoogleController.class);
	
	@Autowired
	private JniShmService jniShmService;
	
	@PostMapping("/speechToText")
	public Object speechToText(@RequestBody HttpJson requestHttpJson) {
		
		log.info(String.format("request json is :[%s]", requestHttpJson.toString()));
		
//		// 装载共享内存
//		jniShmService.shmInit();
		
		// 读取共享内存中的语音数据
//		String speech = jniShmService.readSpeechRecordShm(requestHttpJson.getId());
//
//		log.info(String.format("shmId:[%d] speechLen:[%d] ", requestHttpJson.getId(), speech.length()));
		
        // 语音数据中有截断符，使用 byte[] 存储
		byte[] speech = jniShmService.readSpeechRecordByteArrShm(requestHttpJson.getId());
		log.info(String.format(" success to read speech from shmId:[%d] speechLen:[%d] ", requestHttpJson.getId(), speech.length));
		
		// 调用谷歌 asr 接口完成语音转文本
		String text = "";
		// Instantiates a client
		try (SpeechClient speechClient = SpeechClient.create()) {
            // 组成音频数据
			ByteString audioBytes = ByteString.copyFrom(speech);

			log.info(String.format("audioBytes size is: %d\n", audioBytes.size()));

			// Builds the sync recognize request
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
					.setSampleRateHertz(8000).setLanguageCode("en-US").build();

			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

			log.info("success to builds the sync recognize request");

			// Performs speech recognition on the audio file
			RecognizeResponse response = speechClient.recognize(config, audio);
			List<SpeechRecognitionResult> results = response.getResultsList();

			log.info("success to performs speech recognition on the audio file");

			for (SpeechRecognitionResult result : results) {
				// There can be several alternative transcripts for a given chunk of speech.
				// Just use the
				// first (most likely) one here.
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				
//				log.info("Transcription: %s%n", alternative.getTranscript());

				text += alternative.getTranscript();
			}
		} catch (IOException e) {
			log.error(ERROR.toString());
		}	
		
		// 写入共享内存文本数据
		jniShmService.writeTextRecordShm(requestHttpJson.getId(), text);
		
//		// 卸载共享内存
//		jniShmService.shmTerm();
		
		// 告知客户端文本数据写入位置
		HttpJson responseHttpJson = new HttpJson();
		responseHttpJson.setId(requestHttpJson.getId());
		
		return ResultVoUtil.success(CodeMsgEnum.SERVER_SUCCESS, JSONObject.toJSONString(responseHttpJson));
	}	

	/**
	 * Demonstrates using the Speech API to transcribe an audio file.
	 * 
	 * @throws IOException
	 */
//	@GetMapping("/testSdkApi")
//	public Object testSdkApi(User user) throws IOException {
//		
//		
//		log.debug("test testSdkApi debug log ....");
//		log.info("test testSdkApi info log ....");
//		log.error("test testSdkApi error log ....");
//				
//		String text = "语音转换文本成功" + user.toString();
//
//		Map<String, String> map = System.getenv();
//		if (map.containsKey("GOOGLE_APPLICATION_CREDENTIALS")) {
//			String key = "GOOGLE_APPLICATION_CREDENTIALS";
//			System.out.println(key + "=" + map.get(key));
//		}
//
////		// Instantiates a client
////		try (SpeechClient speechClient = SpeechClient.create()) {
////
////			// The path to the audio file to transcribe
////			// String fileName =
////			// "E:\\java_dev\\sts_project\\SpeechTranscript\\src\\main\\resources\\audio.raw";
////			String fileName = "/root/java-docs-samples/speech/cloud-client/resources/audio.raw";
////
////			// Reads the audio file into memory
////			Path path = Paths.get(fileName);
////			byte[] data = Files.readAllBytes(path);
////			ByteString audioBytes = ByteString.copyFrom(data);
////
////			System.out.printf("audioBytes size is: %d\n", audioBytes.size());
////
////			// Builds the sync recognize request
////			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
////					.setSampleRateHertz(16000).setLanguageCode("en-US").build();
////
////			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();
////
////			System.out.println("success to builds the sync recognize request");
////
////			// Performs speech recognition on the audio file
////			RecognizeResponse response = speechClient.recognize(config, audio);
////			List<SpeechRecognitionResult> results = response.getResultsList();
////
////			System.out.println("success to performs speech recognition on the audio file");
////
////			
////			
////			
////			for (SpeechRecognitionResult result : results) {
////				// There can be several alternative transcripts for a given chunk of speech.
////				// Just use the
////				// first (most likely) one here.
////				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
////				System.out.printf("Transcription: %s%n", alternative.getTranscript());
////				
////				text += alternative.getTranscript();
////			}
////		}
//
//		return ResultVoUtil.success(CodeMsgEnum.SERVER_SUCCESS, text);
//	}

	@PostMapping("/testSdkApi")
	public Object testSdkApi(@RequestBody User user) throws IOException {
		
		
//		log.debug("test testSdkApi debug log ....");
//		log.info("test testSdkApi info log ....");
//		log.error("test testSdkApi error log ....");
				
//		String text = "语音转换文本成功" + user.toString();

		String text = "";
				
		Map<String, String> map = System.getenv();
		if (map.containsKey("GOOGLE_APPLICATION_CREDENTIALS")) {
			String key = "GOOGLE_APPLICATION_CREDENTIALS";
			System.out.println(key + "=" + map.get(key));
		}

		// Instantiates a client
		try (SpeechClient speechClient = SpeechClient.create()) {

			// The path to the audio file to transcribe
			// String fileName =
			// "E:\\java_dev\\sts_project\\SpeechTranscript\\src\\main\\resources\\audio.raw";
//			String fileName = "/root/java-docs-samples/speech/cloud-client/resources/audio.raw";
			
			String fileName = user.getDomain();

			// Reads the audio file into memory
			Path path = Paths.get(fileName);
			byte[] data = Files.readAllBytes(path);
			ByteString audioBytes = ByteString.copyFrom(data);

			System.out.printf("audioBytes size is: %d\n", audioBytes.size());

			// Builds the sync recognize request
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
					.setSampleRateHertz(8000).setLanguageCode("en-US").build();

			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

			System.out.println("success to builds the sync recognize request");

			// Performs speech recognition on the audio file
			RecognizeResponse response = speechClient.recognize(config, audio);
			List<SpeechRecognitionResult> results = response.getResultsList();

			System.out.println("success to performs speech recognition on the audio file");

			
			
			
			for (SpeechRecognitionResult result : results) {
				// There can be several alternative transcripts for a given chunk of speech.
				// Just use the
				// first (most likely) one here.
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				System.out.printf("Transcription: %s%n", alternative.getTranscript());
				
				text += alternative.getTranscript();
			}
		}
		
		
		log.info("request:[" + user.toString() + "] response:[" + text + "]");
		

		return ResultVoUtil.success(CodeMsgEnum.SERVER_SUCCESS, text);
	}	
}
