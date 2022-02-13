package com.kotudyprj.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kotudyprj.dto.KakaoDto;

@Mapper
public interface IKakaoDao {
	public void registerDao(Object userId, Object nickName, Object image);

	public List<KakaoDto> loginDao(@Param("_userId") Object userId);

	public List<KakaoDto> getNickName(@Param("_userId") Object userId);

	public List<KakaoDto> getImage(@Param("_userId") Object userId);
}