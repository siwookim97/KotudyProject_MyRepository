package com.kotudyprj.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kotudyprj.dao.IVocabularyNoteDao;
import com.kotudyprj.dao.IWordRankingDao;
import com.kotudyprj.dto.VocabularyNoteDto;

@Service
public class VocabularyServiceImpl implements VocabularyService{

	@Autowired
	IVocabularyNoteDao iVocabularyNoteDao;
	
	@Autowired
	IWordRankingDao iWordRankingDao;
	
	// ("/myPage")
	@Override
	public List<VocabularyNoteDto> myPage(HttpSession loginId) {
		List<VocabularyNoteDto> vocabularyList = new ArrayList<>();
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();

		vocabularyList = iVocabularyNoteDao.showWord(userId);
		System.out.println("MyPage Service단 실행");
		return vocabularyList;
	}

	// ("/addToNote")
	@Override
	public void addToNote(HttpSession loginId, String q, String p) {
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();

		// 단어장에 단어가 이미 있는지 확인
		if (iVocabularyNoteDao.checkWord(userId, q) == 0) {
			iVocabularyNoteDao.addWord(userId, q, p);
			/* 나중에 단어장에 들어갔는지 안들어갔는지 중복값을 프론트에 전달하기 */
		} else {
			/* 나중에 단어장에 들어갔는지 안들어갔는지 중복값을 프론트에 전달하기 */
		}

		if (iWordRankingDao.wordRankingSelect(q) == 0) {
			iWordRankingDao.wordRankingInsert(q, p);
			iWordRankingDao.wordRankingUp(q);
		} else {
			iWordRankingDao.wordRankingUp(q);
		}
		System.out.println("AddToNote Service단 실행");
	}
	
	//("/deleteFromNote")
	@Override
	public List<VocabularyNoteDto> deleteFromNote(HttpSession loginId, String word) {
		List<VocabularyNoteDto> vocabularyList = new ArrayList<>();
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();

		iVocabularyNoteDao.deleteWord(userId, word);

		vocabularyList = iVocabularyNoteDao.showWord(userId);

		if (iWordRankingDao.wordRankingSelect(word) == 1) {
			iWordRankingDao.wordRankingDelete(word);
		} else {
			iWordRankingDao.wordRankingDown(word);
		}
		System.out.println("DeleteFromNote Service단 실행");
		return vocabularyList;
	}
	
	
}
