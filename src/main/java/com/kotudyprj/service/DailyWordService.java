package com.kotudyprj.service;

import java.util.List;

import com.kotudyprj.dto.WordsDto;

public interface DailyWordService {
	List<WordsDto> dailyWords(WordsDto wordsDto);
}
