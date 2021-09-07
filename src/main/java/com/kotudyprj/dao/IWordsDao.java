package com.kotudyprj.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kotudyprj.dto.WordsDto;

@Mapper
public interface IWordsDao {
   public List<WordsDto> selectWordsDao();
}