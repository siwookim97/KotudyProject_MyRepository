package com.kotudyprj.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;

import com.kotudyprj.dto.QuizTemplateDto;

public interface QuizService {
	List<QuizTemplateDto> wordQuiz();
	void getQuizResult(HttpSession loginId ,Map<String, Integer> body);
}
