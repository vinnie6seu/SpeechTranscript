package com.fuwei.asr.SpeechTranscript.modular.entity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fuwei.asr.SpeechTranscript.modular.service.ShmPacketService;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionResult;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeResponse;
import com.google.common.util.concurrent.SettableFuture;

public class ResponseApiStreamingObserver<T> implements ApiStreamObserver<T> {
	private static final Logger log = LoggerFactory.getLogger(ResponseApiStreamingObserver.class);
	
	private /* final */ SettableFuture<T> future = SettableFuture.create();
	private /* final */ T messages = null;
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 谷歌 api 将中间结果正常回调返回
	 */
	@Override
	public void onNext(T message) {
		messages = message;
		
		log.info(String.format("id:[%d] response onNext message:[%s]", this.getId(), message.toString()));

		
		StreamingRecognizeResponse responses = (StreamingRecognizeResponse) message;
		if (responses.getResultsList().size() > 0) {
			
			ShmPacketService shmPacketService = ShmPacketService.me();
			
			// 1.写结果
			AsrShmResponse asrShmResponse = new AsrShmResponse();
			
			List<StreamingRecognitionResult> streamingRecognitionResultList = responses.getResultsList();
			Collections.sort(streamingRecognitionResultList, new Comparator<StreamingRecognitionResult>() {
				@Override
				public int compare(StreamingRecognitionResult arg0, StreamingRecognitionResult arg1) {
					// 按照Stability进行降序排列
					if (arg0.getStability() > arg1.getStability()) {
						return -1;
					}
					if (arg0.getStability() == arg1.getStability()) {
						return 0;
					}
					return 1;
				}
			});	
			
			int len = 0;
			for (StreamingRecognitionResult result : streamingRecognitionResultList) {

				AsrShmResponseItem asrResponseItem = new AsrShmResponseItem();
				asrResponseItem.setTranscript(result.getAlternativesList().get(0).getTranscript());
				asrResponseItem.setStability(result.getStability());
				asrResponseItem.setIs_final(result.getIsFinal());
				asrResponseItem.setConfidence(result.getAlternativesList().get(0).getConfidence());

				log.info(String.format("id:[%d] response item onNext message:[%s]", this.getId(), asrResponseItem.toString()));

				if (len == 0) {
					asrShmResponse.set_cur_result(asrResponseItem);
				}
				if (len == 1) {
					asrShmResponse.set_cur_predict(asrResponseItem);
				}
				
				if (result.getIsFinal() == true) {
					// 2.删除键值对
					shmPacketService.requestIdDelete(this.getId());

					asrShmResponse.set_cur_result(asrResponseItem);
				}
				
				len++;
			}	
			
			shmPacketService.textPacketSend(this.getId(), asrShmResponse);
		}
		
	}

	/**
	 * 出现异常谷歌结果进行回调
	 */
	@Override
	public void onError(Throwable t) {
		future.setException(t);
		
		log.error(String.format("id:[%d] error:[%s]", this.getId(), t.getMessage()));
		
		ShmPacketService shmPacketService = ShmPacketService.me();
		// 1.写异常
		AsrShmResponse asrShmResponse = new AsrShmResponse();
		asrShmResponse.setIs_exception(true);
		asrShmResponse.setException_str(t.getMessage());
		
		shmPacketService.textPacketSend(this.getId(), asrShmResponse);
		
		// 2.删除键值对
		shmPacketService.requestIdDelete(this.getId());
	}

	/**
	 * 将最终结果保存，java 代码可以获取最终结果
	 */
	@Override
	public void onCompleted() {
		future.set(messages);
	}

	// Returns the SettableFuture object to get received messages / exceptions.
	public SettableFuture<T> future() {
		return future;
	}
}