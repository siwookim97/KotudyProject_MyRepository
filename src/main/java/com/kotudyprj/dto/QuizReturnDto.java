package com.kotudyprj.dto;

// 퀴즈 생성을 위한 결과값 가져오기
public class QuizReturnDto {
	private String word;
	private String mean;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getMean() {
		return mean;
	}
	public void setMean(String mean) {
		this.mean = mean;
	}
}
