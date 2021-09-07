package com.kotudyprj.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IRegisterDao {

	public void registerDao(String userName, String birth, String work, String userId, String userPassword);

	public String loginDao(@Param("_userId") String userId, @Param("_userPassword") String userPassword);
}