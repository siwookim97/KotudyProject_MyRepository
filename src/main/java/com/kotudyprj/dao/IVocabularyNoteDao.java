package com.kotudyprj.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kotudyprj.dto.VocabularyNoteDto;


@Mapper
public interface IVocabularyNoteDao {
	
	public List<String> showWord(@Param(value = "_userId") String userId);
	public int checkWord(@Param(value = "_userId") String userId ,@Param(value = "_word") String q);
	public void addWord(@Param(value = "_userId") String userId ,@Param(value = "_word") String q, @Param(value = "_mean") String p);
	public void deleteWord(@Param(value = "_userId") String userId, @Param(value = "_word") String word);
	public List<VocabularyNoteDto> getVocabularynote();

}
