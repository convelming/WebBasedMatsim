//package com.matsim.user;
//
//import com.matsim.bean.Result;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//	@Autowired
//	private UserDao userDao;
//
//	@Override
//	public Result add(User user){
//
//		Result result = new Result();
//		try {
//			userDao.add(user);
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setSuccess(false);
//			result.setErrMsg("Cannot add user please check ");
//		}
//
//		return result;
//	}
//
//	@Override
//	public Result update(User user){
//
//		Result result = new Result();
//
//		try {shu
//		userDao.update(user);
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setSuccess(false);
//			result.setErrMsg("Cannot update user please check ");
//		}
//
//		return result;
//	}
//
//	@Override
//	public Result delete(User user){
//
//		Result result = new Result();
//
//		try {
//			userDao.delete(user);
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setSuccess(false);
//			result.setErrMsg("Cannot delete user, please check");
//		}
//
//		return result;
//	}
//
//	@Override
//	public Result hasUserName(String userName){
//
//		Result result = new Result();
//
//		try {
//			userDao.hasUserName();
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setSuccess(false);
//		}
//		return result;
//	}
//
//	@Override
//	public Result find(TUserParam tUser){
//
//		Result result = new Result();
//
//		try {
//			Page pageData = new Page();
//			List<TUser> list = userDao.find(tUser);
//
//			pageData.setCurrPage(tUser.getPage());
//			pageData.setPageSize(tUser.getPageSize());
//			pageData.setMaxCount(userDao.findCount(tUser));
//			pageData.setData(list);
//
//			result.setData(pageData);
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setSuccess(false);
//			result.setErrCode(ErrCanstant.E304);
//		}
//
//		return result;
//	}
//
//	@Override
//	public Result findAll(TUserParam tUser){
//
//		Result result = new Result();
//
//		try {
//			List<TUser> list = userDao.findAll(tUser);
//			result.setData(list);
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setSuccess(false);
//			result.setErrCode(ErrCanstant.E304);
//		}
//
//		return result;
//	}
//
//}