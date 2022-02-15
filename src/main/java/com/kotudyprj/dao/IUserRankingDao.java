package com.kotudyprj.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IUserRankingDao {
	
	// user_ranking 테이블에 사용자의 정보 생성
	public void createRankingInfo(Object userId, Object nickName, Object image);
	
	// user_ranking 테이블 PRIMARY_KEY 중복 확인
	public String checkRankingUserId(Object userId);
	
	// user_ranking 테이블 정보 확인
	public int selectQuizRanking(@Param("_userId") String userId);
	
	// user_ranking 테이블 정보 변경
	public void getQuizResult(@Param("_userId") String userId, @Param("_point") int point);
	
	// user_ranking 10개 뽑아오기
	public List<Object> userRankingPoint();
    public List<Object> userRankingUserId();
    public List<Object> userRankingImage();
}
