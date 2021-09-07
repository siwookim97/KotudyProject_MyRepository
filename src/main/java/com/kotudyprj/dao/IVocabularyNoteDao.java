package com.kotudyprj.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IVocabularyNoteDao {
	
	public List<String> showWord(@Param(value = "_userId") String userId);
	public void addWord(@Param(value = "_userId") String userId ,@Param(value = "_word") String word);
	public void deleteWord(@Param(value = "_userId") String userId, @Param(value = "_word") String word);
}
