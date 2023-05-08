package com.kotudyprj.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kotudyprj.dao.IWordsDao;
import com.kotudyprj.dto.WordsDto;

@Service
public class DailyWordServiceImpl implements DailyWordService {

	@Autowired
	IWordsDao iWordsDao;

	// ("dailyWords")
	@Override
	public List<WordsDto> dailyWords(WordsDto wordsDto) {
		List<WordsDto> list = new ArrayList<>();
		list = iWordsDao.selectWordsDao();
		System.out.println("DailyWords Service단 실행");
		return list;
	}

}
