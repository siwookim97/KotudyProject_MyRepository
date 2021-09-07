package com.kotudyprj.dto;

import lombok.Data;

public class WordSenseDto {
	
	private int sense_order; // sense의 순서
	private String definition; // sense의 의미
	
	public int getSense_order() {
		return sense_order;
	}
	public void setSense_order(int sense_order) {
		this.sense_order = sense_order;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
}