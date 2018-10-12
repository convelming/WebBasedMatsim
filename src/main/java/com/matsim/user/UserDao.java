package com.matsim.user;

import java.util.List;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

public interface UserDao {
	@Options(useGeneratedKeys=true, keyProperty="id",keyColumn = "id")

	public int add(@Param("user") User user);

	public int update(@Param("user") User user);

	public int delete(@Param("user") User user);

	public List<User> hasUserName(@Param("userName") String userName);

	public List<User> hasUserId(@Param("id") Integer id);

	public List<User> getUserIdByName(@Param("userName") String userName);

//	public List<User> find(@Param("tUser") TUserParam tUser);
//
//	public int findCount(@Param("tUser") TUserParam tUser);
//
//	public List<User> findAll(@Param("tUser") TUserParam tUser);

}