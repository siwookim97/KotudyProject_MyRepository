package com.kotudyprj.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.kotudyprj.dto.OpenWordDto;

public interface OpenWordService {
	
	void addToOpen(HttpSession loginId, String word, String mean, String morpheme, String category);
	void deleteFromOpen(Map<String, Integer> body);
	List<OpenWordDto> loadtoOpen(HttpSession loginId);
	List<OpenWordDto> loadAllOpen();
	void deletetoOpen(HttpSession loginId, String category);
}
