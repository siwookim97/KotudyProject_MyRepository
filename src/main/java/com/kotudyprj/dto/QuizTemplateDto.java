package com.kotudyprj.dto;

public class QuizTemplateDto {

	private String word;
	private String word_mean;
	private String wrong_answer1;
	private String wrong_answer2;
	private String wrong_answer3;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getWord_mean() {
		return word_mean;
	}
	public void setWord_mean(String word_mean) {
		this.word_mean = word_mean;
	}
	public String getWrong_answer1() {
		return wrong_answer1;
	}
	public void setWrong_answer1(String wrong_answer1) {
		this.wrong_answer1 = wrong_answer1;
	}
	public String getWrong_answer2() {
		return wrong_answer2;
	}
	public void setWrong_answer2(String wrong_answer2) {
		this.wrong_answer2 = wrong_answer2;
	}
	public String getWrong_answer3() {
		return wrong_answer3;
	}
	public void setWrong_answer3(String wrong_answer3) {
		this.wrong_answer3 = wrong_answer3;
	}
}
