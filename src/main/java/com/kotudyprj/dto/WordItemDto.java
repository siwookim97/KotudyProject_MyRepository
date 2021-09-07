package com.kotudyprj.dto;

import java.util.List;

import lombok.Data;

public class WordItemDto {
	
	private int target_code; // 식별 코드
	private String word; // 표제어
	private String pronunciation; // 발음
	private String pos; // 품사
	public List<WordSenseDto> sense; // 의미의 리스트
	
	public int getTarget_code() {
		return target_code;
	}
	public void setTarget_code(int target_code) {
		this.target_code = target_code;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getPronunciation() {
		return pronunciation;
	}
	public void setPronunciation(String pronunciation) {
		this.pronunciation = pronunciation;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public List<WordSenseDto> getSense() {
		return sense;
	}
	public void setSense(List<WordSenseDto> sense) {
		this.sense = sense;
	}
}
