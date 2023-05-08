package com.kotudyprj.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kotudyprj.dao.IUserRankingDao;
import com.kotudyprj.dao.IVocabularyNoteDao;
import com.kotudyprj.dto.QuizReturnDto;
import com.kotudyprj.dto.QuizTemplateDto;

@Service
public class QuizServiceImpl implements QuizService{
	
	@Autowired
	IVocabularyNoteDao iVocabularyNoteDao;
	
	@Autowired
	IUserRankingDao iUserRankingDao;

	// ("/wordQuiz")
	@Override
	public List<QuizTemplateDto> wordQuiz() {
		List<QuizTemplateDto> quizTemplateList = new ArrayList<>();
		QuizTemplateDto quizTemplate = new QuizTemplateDto(); // 여기 왜 ? null안되나 ?

		List<QuizReturnDto> vocabularyNoteList = null;
		vocabularyNoteList = iVocabularyNoteDao.getVocabularynote();
		for (int n = 0; n < 40; n++) {
			if (n % 4 == 0) {
				quizTemplate = new QuizTemplateDto(); // 여기 또 선언 ?
				quizTemplate.setWord(vocabularyNoteList.get(n).getWord());
				quizTemplate.setWord_mean(vocabularyNoteList.get(n).getMean());
			} else if (n % 4 == 1) {
				quizTemplate.setWrong_answer1(vocabularyNoteList.get(n).getMean());
			} else if (n % 4 == 2) {
				quizTemplate.setWrong_answer2(vocabularyNoteList.get(n).getMean());
			} else {
				quizTemplate.setWrong_answer3(vocabularyNoteList.get(n).getMean());
				quizTemplateList.add(quizTemplate);
			}
		}
		System.out.println("WordQuiz Service단 실행");

		return quizTemplateList;
	}

	// ("postQuizResult")
	@Override
	public void getQuizResult(HttpSession loginId, Map<String, Integer> body) {
		Object id = (String) loginId.getAttribute("userId");
		String userId = (String) id;

		int point = body.get("score");
		System.out.println("score : " + point);

		if (point > 0)
			iUserRankingDao.getQuizResult(userId, point);
		System.out.println("GetQuizResult Service단 실행");
	}

}
