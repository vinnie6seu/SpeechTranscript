package com.fuwei.asr.SpeechTranscript.modular.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fuwei.asr.SpeechTranscript.common.bean.SpringContextHolder;
import com.fuwei.asr.SpeechTranscript.modular.entity.AsrShmRequestAndRpcCall;
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
	 * @CachePut和@Cacheable这两个标签可以结合使用，当需要根据请求改变值的时候，利用@CachePut将值改变并写入到缓存中，而@Cacheable标签除了第一次之外，一直是取的缓存的值。
	 * 
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	@CachePut(value = "asrCacheMap", key = "#id")
	public AsrShmRequestAndRpcCall requestIdCreate(Integer id) throws IOException {
		log.info(String.format("step into new create request id:[%d]", id));
		
		// 创建
		AsrShmRequestAndRpcCall asrShmRequest = new AsrShmRequestAndRpcCall(id);

		return asrShmRequest;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Cacheable(value = "asrCacheMap", key = "#id")
	public AsrShmRequestAndRpcCall requestIdGet(Integer id) {
		return null;
	}	
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@CachePut(value = "asrCacheMap", key = "#id")
	public AsrShmRequestAndRpcCall requestIdUpdate(Integer id, AsrShmRequestAndRpcCall asrShmRequest) throws IOException {

		return asrShmRequest;
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
	 * 会报错！！！！！！！！！ 
	 * @param id
	 * @param asrResponse
	 */
	public void textPacketSend(Integer id, AsrShmResponse asrResponse) {
		jniShmService.shmTextPacketSend(id, asrResponse);
	}
	
	public void textPacketSend(Integer id, String asrResponseJsonStr) {
		jniShmService.shmTextPacketSend(id, asrResponseJsonStr);
	}	
	
	/**
	 * 
	 * @param id
	 */
	@CacheEvict(value = "asrCacheMap")
	public void requestIdDelete(Integer id) {
		
	}
}
