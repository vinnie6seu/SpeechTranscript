package com.fuwei.asr.SpeechTranscript.modular.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.fuwei.asr.SpeechTranscript.common.controller.BaseController;
import com.fuwei.asr.SpeechTranscript.common.exception.GlobalException;
import com.fuwei.asr.SpeechTranscript.common.form.HttpJson;
import com.fuwei.asr.SpeechTranscript.common.form.User;
import com.fuwei.asr.SpeechTranscript.constant.CodeMsgEnum;
import com.fuwei.asr.SpeechTranscript.constant.SpeechPacketStatusEnum;
import com.fuwei.asr.SpeechTranscript.modular.entity.AsrShmRequestAndRpcCall;
import com.fuwei.asr.SpeechTranscript.modular.service.JniShmService;
import com.fuwei.asr.SpeechTranscript.modular.service.ShmPacketService;
import com.fuwei.asr.SpeechTranscript.util.ResultVoUtil;
//Imports the Google Cloud client library
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeResponse;
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

	@Autowired
	private ShmPacketService shmPacketService;	
	
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
//			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
//					.setSampleRateHertz(8000).setLanguageCode("en-US").build();
			
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
					.setSampleRateHertz(8000).setLanguageCode("id-ID").build();			

			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

			log.info("success to builds the sync recognize request");
			
			long startTime = System.nanoTime();

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
			
			long endTime = System.nanoTime();
			
			text = requestHttpJson.getId() + " speech size:" + audioBytes.size();
			
			log.info("speech size:" + audioBytes.size() + " text:["+ text + "]" + " consume time:" + (endTime - startTime) + "ns");
			
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
	
	@PostMapping("/speechToTextPacket")
	public Object speechToTextPacket(@RequestBody HttpJson requestHttpJson) throws IOException, InterruptedException, ExecutionException {

		log.info(String.format("request json is :[%s]", requestHttpJson.toString()));

		if (SpeechPacketStatusEnum.MSP_AUDIO_INIT.value() == requestHttpJson.getAsrSpeechPackStatus()) {

			// 1.init，新建键值对<id, [speechClient, 两个观察者, 已收到包数量, 发送完成标识, 总发送包数量]>，发送配置
			shmPacketService.requestIdCreate(requestHttpJson.getId());

		} else if (SpeechPacketStatusEnum.MSP_AUDIO_CONTINUE.value() == requestHttpJson.getAsrSpeechPackStatus()) {

			/**
			 * 2.continue
			 * 2.1  没有找到相应 id 的缓存信息返回报错
			 * 2.2  收取非阻塞队列中的（如果有数据包）数据调用Request.onNext
			 * 2.3 按照is_complete_receive，调用onComplete
			 */
			AsrShmRequestAndRpcCall asrShmRequestAndRpcCall = shmPacketService.requestIdGet(requestHttpJson.getId());
			if (asrShmRequestAndRpcCall != null) {

				// 查看记录标识
				Integer id = requestHttpJson.getId();
				boolean is_complete_send = asrShmRequestAndRpcCall.is_is_send_complete();
				int total_send_packet_num = asrShmRequestAndRpcCall.get_total_send_packet_num();
								
//				asrShmRequest.set_is_send_complete(is_complete_send);
//				asrShmRequest.set_total_send_packet_num(total_send_packet_num);				
				
				Integer batch_num = 0;
				Boolean is_complete_receive = false;
				// 得到语音数据、读到的语音包数、是否全部读完了[发送完成标志comlete && 已收到包数量 == 总发送包数量]
				byte[] speechData = shmPacketService.speechPacketReceive(id, is_complete_send, total_send_packet_num, batch_num, is_complete_receive);

				if (batch_num != 0) {
					
					log.info(String.format("id:[%d] send speech packet len:[%d]", requestHttpJson.getId(), speechData.length));
					
					// 调用谷歌 api 发送数据
					asrShmRequestAndRpcCall.continueOnNext(speechData);
					
					log.info(String.format("success to call onNext id:[%d] send speech packet len:[%d]", requestHttpJson.getId(), speechData.length));
				}

				if (is_complete_receive == true) {
					
					log.info(String.format("id:[%d] call onCompleted", requestHttpJson.getId()));
					
					// 调用谷歌 api 确认发送完毕完成发送
					asrShmRequestAndRpcCall.completeOnNext();
					
					// 调用谷歌 api 收取最终结果					
					log.info(String.format("id:[%d] Transcript: %s\n", requestHttpJson.getId(), asrShmRequestAndRpcCall.getTranscriptResult()));					
				}
			} else {
				
				// 删除键值对		
				shmPacketService.requestIdDelete(requestHttpJson.getId());						
				
				log.error(String.format("not create query id:[%d] info", requestHttpJson.getId()));
				
				throw new GlobalException(CodeMsgEnum.NO_CREATE_QUERY_ID, requestHttpJson.getId());
			}

		} else if (SpeechPacketStatusEnum.MSP_AUDIO_LAST.value() == requestHttpJson.getAsrSpeechPackStatus()) {

			/**
			 * 3.last
			 * 3.1  没有找到相应 id 的缓存信息返回报错
			 * 3.2  更新记录，得到已经发送完毕，总的发送包数量
			 * 3.3  收取非阻塞队列中的（如果有数据包）数据调用Request.onNext
			 * 3.4 按照is_complete_receive，调用onComplete
			 */
			AsrShmRequestAndRpcCall asrShmRequestAndRpcCall = shmPacketService.requestIdGet(requestHttpJson.getId());
			if (asrShmRequestAndRpcCall != null) {

				Integer id = requestHttpJson.getId();
				boolean is_complete_send = (SpeechPacketStatusEnum.MSP_AUDIO_LAST.value() == requestHttpJson.getAsrSpeechPackStatus());
				int total_send_packet_num = requestHttpJson.getTotalSendPacketNum();
				
				// 更新记录，得到已经发送完毕，总的发送包数量
				asrShmRequestAndRpcCall.set_is_send_complete(is_complete_send);
				asrShmRequestAndRpcCall.set_total_send_packet_num(total_send_packet_num);
				shmPacketService.requestIdUpdate(id, asrShmRequestAndRpcCall);
				
				Integer batch_num = 0;
				Boolean is_complete_receive = false;
				// 得到语音数据、读到的语音包数、是否全部读完了[发送完成标志comlete && 已收到包数量 == 总发送包数量]
				byte[] speechData = shmPacketService.speechPacketReceive(id, is_complete_send, total_send_packet_num, batch_num, is_complete_receive);

				if (batch_num != 0) {
					
					log.info(String.format("id:[%d] send speech packet len:[%d]", requestHttpJson.getId(), speechData.length));
					
					// 调用谷歌 api 发送数据
					asrShmRequestAndRpcCall.continueOnNext(speechData);
					
					log.info(String.format("success to call onNext id:[%d] send speech packet len:[%d]", requestHttpJson.getId(), speechData.length));
				}

				if (is_complete_receive == true) {

					log.info(String.format("id:[%d] call onCompleted", requestHttpJson.getId()));
					
					// 调用谷歌 api 确认发送完毕完成发送
					asrShmRequestAndRpcCall.completeOnNext();
					
					// 调用谷歌 api 收取最终结果					
					log.info(String.format("id:[%d] Transcript: %s\n", requestHttpJson.getId(), asrShmRequestAndRpcCall.getTranscriptResult()));					
				}
			} else {
				
				// 删除键值对		
				shmPacketService.requestIdDelete(requestHttpJson.getId());						
				
				log.error(String.format("not create query id:[%d] info", requestHttpJson.getId()));
				
				throw new GlobalException(CodeMsgEnum.NO_CREATE_QUERY_ID, requestHttpJson.getId());
			}

		}

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
