package com.kotudyprj.service;

import java.util.List;

import javax.servlet.http.HttpSession;


import com.kotudyprj.dto.VocabularyNoteDto;

public interface VocabularyService {
	List<VocabularyNoteDto> myPage(HttpSession loginId);
	void addToNote(HttpSession loginId, String q, String p);
	public List<VocabularyNoteDto> deleteFromNote(HttpSession loginId, String word);
}
