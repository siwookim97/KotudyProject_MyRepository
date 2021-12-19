package com.kotudyprj.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IUserRankingDao {
	
	// user_ranking 테이블에 사용자의 정보 생성
	public void createRankingInfo(@Param("_userId") String userId);
	
	// user_rankgin 테이블 정보 확인
	public int selectQuizRanking(@Param("_userId") String userId);
	
	// user_ranking 테이블 정보 변경
	public void getQuizResult(@Param("_userId") String userId, @Param("_point") int point);
}
