package com.kotudyprj.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kotudyprj.dao.IUserRankingDao;
import com.kotudyprj.dao.IWordRankingDao;
import com.kotudyprj.dto.KakaoDto;
import com.kotudyprj.dto.UserRankingDto;
import com.kotudyprj.dto.WordRankingDto;

@Service
public class RankingServiceImpl implements RankingService {

	@Autowired
	IWordRankingDao iWordRankingDao;

	@Autowired
	IUserRankingDao iUserRankingDao;

	@Override
	public  List<WordRankingDto> wordRank() {
		List<WordRankingDto> wordRanking = new ArrayList<>();
	    wordRanking = iWordRankingDao.wordall();
	    
		return wordRanking;
	}

	// ("/userRank")
	@Override
	public List<UserRankingDto> userRank(KakaoDto kakoDto) {
		List<List<Object>> userRankingList = new ArrayList<>();
		List<UserRankingDto> userRankingUserId = new ArrayList<>();
		List<Object> userRankingImage = new ArrayList<>();
		List<Object> userRankingPoint = new ArrayList<>();


		userRankingUserId = iUserRankingDao.userRankingUserId();
		System.out.println("UserRank Service단 실행");
		

		return userRankingUserId;
	}
}
