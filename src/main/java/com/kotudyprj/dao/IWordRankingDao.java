package com.kotudyprj.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.kotudyprj.dto.WordRankingDto;
@Mapper
public interface IWordRankingDao {

   // insert into
   public void wordRankingInsert(@Param("_word") String word, @Param("_mean") String mean);
   
   // word_ranking테이블에 원하는 word있는지 확인
   public int wordRankingSelect(@Param("_word") String word);
   
   // point증가
   public void wordRankingUp(@Param("_word") String word);
   
   // point감소
   public void wordRankingDown(@Param("_word") String word);
   
   // delete 컬럼
   public void wordRankingDelete(@Param("_word") String word);
   
   // word rank보내기
   public List<Object> wordRankingWord();
   public List<Object> wordRankingPoint();
   public List<WordRankingDto> wordall();
}