package com.fuwei.asr.SpeechTranscript.modular.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import com.fuwei.asr.SpeechTranscript.common.bean.SpringContextHolder;
import com.fuwei.asr.SpeechTranscript.modular.entity.AsrShmRequest;
import com.fuwei.asr.SpeechTranscript.modular.entity.AsrShmResponse;
import com.fuwei.asr.SpeechTranscript.modular.entity.ResponseApiStreamingObserver;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShmPacketService {

	@Autowired
	private JniShmService jniShmService;	
	
	/**
	 * 
	 * @return
	 */
    public static ShmPacketService me() {
        return SpringContextHolder.getBean(ShmPacketService.class);
    }	
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	@CachePut(value = "asrCacheMap", key = "#id")
	public AsrShmRequest requestIdCreate(Integer id) throws IOException {
		log.info(String.format("step into new create request id:[%d]", id));
		
		// 1.创建客户端
		SpeechClient speechClient = SpeechClient.create();
		
		// 2.创建请求音频的配置
		RecognitionConfig recConfig = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
				.setLanguageCode("en-US").setSampleRateHertz(8000).setModel("default").build();
		// 配置 setInterimResults 决定了在发送语音流的过程中能收到转义结果
		StreamingRecognitionConfig config = StreamingRecognitionConfig.newBuilder().setConfig(recConfig).setInterimResults(true).build();

		// 3.创建 responseObserver 
		ResponseApiStreamingObserver<StreamingRecognizeResponse> responseObserver = new ResponseApiStreamingObserver<>();
		// 在谷歌回调传回转义结果的时候也让其知道该写入的位置 id
		responseObserver.setId(id);

		// 4.创建 requestObserver
		BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable = speechClient.streamingRecognizeCallable();
		ApiStreamObserver<StreamingRecognizeRequest> requestObserver = callable.bidiStreamingCall(responseObserver);

		// 5.发送配置请求
		requestObserver.onNext(StreamingRecognizeRequest.newBuilder().setStreamingConfig(config).build());		
		
		// 6.保存相应请求 id 的处理内容
		AsrShmRequest asrShmRequest = new AsrShmRequest();
		asrShmRequest.set_responseObserver(responseObserver);
		asrShmRequest.set_requestObserver(requestObserver);
		asrShmRequest.set_speechClient(speechClient);
		
		return asrShmRequest;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@CachePut(value = "asrCacheMap", key = "#id")
	public AsrShmRequest requestIdGet(Integer id) {
		return null;
	}	
	
	/**
	 * 
	 * @param id
	 * @param is_complete_send
	 * @param total_send_packet_num
	 * @param batch_num
	 * @param is_complete_receive
	 * @return
	 */
	public byte[] speechPacketReceive(Integer id, boolean is_complete_send, int total_send_packet_num, Integer batch_num, Boolean is_complete_receive) {
		return jniShmService.shmSpeechPacketReceive(id, is_complete_send, total_send_packet_num, batch_num, is_complete_receive);
	}
	
	/**
	 * 
	 * @param id
	 * @param asrResponse
	 */
	public void textPacketSend(Integer id, AsrShmResponse asrResponse) {
		jniShmService.shmTextPacketSend(id, asrResponse);
	}
	
	/**
	 * 
	 * @param id
	 */
	@CacheEvict(value = "asrCacheMap")
	public void requestIdDelete(Integer id) {
		
	}
}
