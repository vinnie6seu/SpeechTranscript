package com.fuwei.asr.SpeechTranscript.modular.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fuwei.asr.SpeechTranscript.common.controller.BaseController;
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
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@GetMapping("/speechToText")
	public Object speechToText() {
		
		log.debug("test speechToText debug log ....");
		log.info("test speechToText info log ....");
		log.error("test speechToText error log ....");
		
		return null;
	}	

	/**
	 * Demonstrates using the Speech API to transcribe an audio file.
	 * 
	 * @throws IOException
	 */
	@GetMapping("/testSdkApi")
	public Object testSdkApi() throws IOException {
		
		
		log.debug("test testSdkApi debug log ....");
		log.info("test testSdkApi info log ....");
		log.error("test testSdkApi error log ....");
				
		

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
			String fileName = "/root/java-docs-samples/speech/cloud-client/resources/audio.raw";

			// Reads the audio file into memory
			Path path = Paths.get(fileName);
			byte[] data = Files.readAllBytes(path);
			ByteString audioBytes = ByteString.copyFrom(data);

			System.out.printf("audioBytes size is: %d\n", audioBytes.size());

			// Builds the sync recognize request
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
					.setSampleRateHertz(16000).setLanguageCode("en-US").build();

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
			}
		}

		return null;
	}

}
