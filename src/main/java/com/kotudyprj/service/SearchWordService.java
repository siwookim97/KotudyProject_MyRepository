package com.kotudyprj.service;

import java.util.List;
import java.util.Map;

import com.kotudyprj.dto.WordItemDto;

public interface SearchWordService {
	List<String> paraphraseCheck(Map<String, String> body);
	List<WordItemDto> oneWord(String q);
}
