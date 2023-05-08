package com.kotudyprj.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kotudyprj.dao.IOpenWordDao;
import com.kotudyprj.dto.OpenWordDto;

@Service
public class OpenWordServiceImpl implements OpenWordService{

	@Autowired
	private IOpenWordDao iOpenWordDao;
	
	// ("/addToOpen")
	@Override
	public void addToOpen(HttpSession loginId, String word, String mean, String morpheme, String category) {
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();
		iOpenWordDao.addtoOpendic(userId, word, mean, morpheme, category);
		System.out.println("AddToOpen Service단 실행");
	}

	// ("deleteFromOpen")
	@Override
	public void deleteFromOpen(Map<String, Integer> body) {
		int id = body.get("id");
		iOpenWordDao.deletetoOpendic(id);
		System.out.println("DeleteFromOpen Service단 실행");
	}

	// ("loadFromOpen")
	@Override
	public List<OpenWordDto> loadtoOpen(HttpSession loginId) {
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();
		List<OpenWordDto> openwordList = new ArrayList<>();
		openwordList = iOpenWordDao.loadtoOpendic(userId);
		System.out.println("LoadToOpen Service단 실행");
		return openwordList;
	}

	// ("loadAllOpen")
	@Override
	public List<OpenWordDto> loadAllOpen() {
		List<OpenWordDto> openwordList = new ArrayList<>();
		openwordList = iOpenWordDao.loadAllOpendic();
		System.out.println("LoadAllOpen Service단 실행");
		return openwordList;
	}

	@Override
	public void deletetoOpen(HttpSession loginId, String category) {
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();
		iOpenWordDao.deletetoOpencategory(userId, category);
		System.out.println("DeleteOpenCategory Service단 실행");
	}
}
