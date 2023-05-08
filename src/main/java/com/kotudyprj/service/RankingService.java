package com.kotudyprj.service;

import java.util.List;

import com.kotudyprj.dto.KakaoDto;
import com.kotudyprj.dto.UserRankingDto;
import com.kotudyprj.dto.WordRankingDto;

public interface RankingService {
	
	List<WordRankingDto> wordRank();
	List<UserRankingDto> userRank(KakaoDto kakoDto); // 사용자 랭킹 띄워주기
}
