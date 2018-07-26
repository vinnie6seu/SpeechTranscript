package com.fuwei.asr.SpeechTranscript.modular.entity;

import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1p1beta1.StreamingRecognizeResponse;

public class AsrShmRequest {
	private SpeechClient _speechClient;
	
	private boolean _is_send_complete;

	private int _total_send_packet_num;
	
	private ApiStreamObserver<StreamingRecognizeRequest> _requestObserver;
	
	private ResponseApiStreamingObserver<StreamingRecognizeResponse> _responseObserver;

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
