package com.fuwei.asr.SpeechTranscript.modular.entity;

import java.io.IOException;

import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeResponse;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;

public class AsrShmRequest {

	private boolean _is_send_complete;

	private int _total_send_packet_num;
	
	private SpeechClient _speechClient;
	
	private ApiStreamObserver<StreamingRecognizeRequest> _requestObserver;
	
	private ResponseApiStreamingObserver<StreamingRecognizeResponse> _responseObserver;
	
	/**
	 * 显式构造函数
	 * @throws IOException 
	 */
	public AsrShmRequest(Integer id) throws IOException {
		// 1.创建客户端
		_speechClient = SpeechClient.create();
		
		// 2.创建请求音频的配置
		RecognitionConfig recConfig = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
				.setLanguageCode("en-US").setSampleRateHertz(8000).setModel("default").build();
		// 配置 setInterimResults 决定了在发送语音流的过程中能收到转义结果
		StreamingRecognitionConfig config = StreamingRecognitionConfig.newBuilder().setConfig(recConfig).setInterimResults(true).build();

		// 3.创建 responseObserver 
		_responseObserver = new ResponseApiStreamingObserver<>();
		// 在谷歌回调传回转义结果的时候也让其知道该写入的位置 id
		_responseObserver.setId(id);

		// 4.创建 requestObserver
		BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable = _speechClient.streamingRecognizeCallable();
		_requestObserver = callable.bidiStreamingCall(_responseObserver);

		// 5.发送配置请求
		_requestObserver.onNext(StreamingRecognizeRequest.newBuilder().setStreamingConfig(config).build());			
	}
	
	public void continueOnNext(byte[] data) {
		_requestObserver.onNext(StreamingRecognizeRequest.newBuilder().setAudioContent(ByteString.copyFrom(data)).build());	
	}
	
	public void completeOnNext() {
		_requestObserver.onCompleted();
	}
	

	public SpeechClient get_speechClient() {
		return _speechClient;
	}

	public void set_speechClient(SpeechClient _speechClient) {
		this._speechClient = _speechClient;
	}

	public boolean is_is_send_complete() {
		return _is_send_complete;
	}

	public void set_is_send_complete(boolean _is_send_complete) {
		this._is_send_complete = _is_send_complete;
	}

	public int get_total_send_packet_num() {
		return _total_send_packet_num;
	}

	public void set_total_send_packet_num(int _total_send_packet_num) {
		this._total_send_packet_num = _total_send_packet_num;
	}

	public ApiStreamObserver<StreamingRecognizeRequest> get_requestObserver() {
		return _requestObserver;
	}

	public void set_requestObserver(ApiStreamObserver<StreamingRecognizeRequest> _requestObserver) {
		this._requestObserver = _requestObserver;
	}

	public ResponseApiStreamingObserver<StreamingRecognizeResponse> get_responseObserver() {
		return _responseObserver;
	}

	public void set_responseObserver(ResponseApiStreamingObserver<StreamingRecognizeResponse> _responseObserver) {
		this._responseObserver = _responseObserver;
	}

	@Override
	public String toString() {
		return "AsrShmRequest [_speechClient=" + _speechClient + ", _is_send_complete=" + _is_send_complete
				+ ", _total_send_packet_num=" + _total_send_packet_num + ", _requestObserver=" + _requestObserver
				+ ", _responseObserver=" + _responseObserver + "]";
	}
}
