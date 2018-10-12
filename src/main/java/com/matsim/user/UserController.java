//package com.matsim.user;
//
//import com.matsim.bean.Result;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpSession;
//
//@Controller
//@RequestMapping("/verify")
//public class UserController {
//	@Autowired
//	private UserDao userDao ;
//
//
//	@RequestMapping(value = "/login")
//	public @ResponseBody Result login(HttpSession session, @ModelAttribute("user") User user){
//        Result result = new Result();
//        if (userDao.hasUserName(user)!=null&&userDao.hasUserName(user).size()==1){ // redirect to previous or
//            result.setSuccess(true);
//            System.out.println("successfully ");
//        }else{
//            result.setSuccess(false);
//            result.setErrMsg("User login failed");
//        }
//
//
//		return result;
//	}
//
//	@RequestMapping(value = "/register")
//	public @ResponseBody Result register(HttpSession session, @ModelAttribute("tUser") User user){
//        Result result = new Result();
//        if (userDao.hasUserName(user)!=null&&userDao.hasUserName(user).size()==1){ //check if username already exists
//            result.setSuccess(false);
//            result.setErrMsg("User name is already existed!");
//        } else{
//            userDao.add(user);
//        }
//		return result;
//	}
//
//}