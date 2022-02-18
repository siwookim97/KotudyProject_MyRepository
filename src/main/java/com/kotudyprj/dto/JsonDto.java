package com.kotudyprj.dto;

public class JsonDto {
	private String word;
	private int homonymCode;  // 동음이의어 코드 (1번일 때에만 가져오도록)
	private int polysemyCode;  // 다의어 코드
	private String definition;  // 의미정보
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getHomonymCode() {
		return homonymCode;
	}
	public void setHomonymCode(int homonymCode) {
		this.homonymCode = homonymCode;
	}
	public int getPolysemyCode() {
		return polysemyCode;
	}
	public void setPolysemyCode(int polysemyCode) {
		this.polysemyCode = polysemyCode;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	
}
