package com.fuwei.asr.SpeechTranscript.modular.entity;

public class AsrShmResponseItem {
	private String transcript;
	private double stability;

	private boolean is_final;
	private double confidence;

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	public double getStability() {
		return stability;
	}

	public void setStability(double stability) {
		this.stability = stability;
	}

	public boolean isIs_final() {
		return is_final;
	}

	public void setIs_final(boolean is_final) {
		this.is_final = is_final;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	@Override
	public String toString() {
		return "AsrResponseItem [transcript=" + transcript + ", stability=" + stability + ", is_final=" + is_final
				+ ", confidence=" + confidence + "]";
	}
}
