package com.matsim.controller;

import com.matsim.bean.Result;
import com.matsim.service.WebConfig;
import com.matsim.user.User;
import com.matsim.user.UserDao;
import com.matsim.util.SendMailUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.List;


/**
 * Created by MingLU on 2018/5/18,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
@Controller
@RequestMapping("/verify")
public class LoginController {

    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserDao userDao;

    // tempUrl
    @Value("${user.temp.url}")
    private String userTempUrl;

    @RequestMapping("/login")
    public String login(@RequestParam("username") String userName, @RequestParam("password") String password, HttpSession session) {
        Result result = new Result();
        //todo here the if have too many queries, need to change that
        List<User> userList = userDao.hasUserName(userName);
        if (userList != null && userList.size() == 1 &&
                userList.getFirst().getName().equalsIgnoreCase(userName)
                && userList.getFirst().getPassword().equals(password)) { // redirect to previous or
            result.setSuccess(true);
            session.setAttribute("userName", userName);
            session.setAttribute("userId", userDao.hasUserName(userName).get(0).getId());
            session.setMaxInactiveInterval(3600);
            return "redirect:/static/console.html";
        } else {
            //todo update user login time
            result.setSuccess(false);
            result.setErrMsg("User login failed");
            return "redirect:/static/login/loginPage.html";
        }

    }

    @RequestMapping(value = "/register")
    public String register(@RequestParam("username") String userName, @RequestParam("password") String password,
                           @RequestParam("confirmedPassword") String confirmedPassword,
                           @RequestParam("email") String email, HttpSession session) {
        System.out.println(" in register!");
        List<User> userList = userDao.hasUserName(userName);
        String tempUrl = userTempUrl + userName + "/temp";
        if (userList != null && userList.size() == 1 &&
                userList.get(0).getName().equalsIgnoreCase(userName)
                && userList.get(0).getPassword().equals(password)) {
            return "redirect:/static/login/loginPage.html";

        } else if (password.equals(confirmedPassword)) {
            User user = new User();
            user.setName(userName);
            user.setPassword(password);
            user.setEmail(email);
            user.setTempUrl(tempUrl);
            user.setLevel(1);
            user.setRegisterTime(new Timestamp(System.currentTimeMillis()));
            user.setSaveBlockNum(Integer.MAX_VALUE);
            userDao.add(user);
            return "redirect:/static/login/loginPage.html";
        }
        return "redirect:/static/login/loginPage.html";
    }

    @RequestMapping("/getUserInfo")
    public @ResponseBody User getUserInfo(HttpSession session) {

        User user = new User();
        String userName = session.getAttribute("userName").toString();
        List<User> userList = userDao.hasUserName(userName);
        if (userList != null && userList.size() == 1 &&
                userList.get(0).getName().equalsIgnoreCase(userName)) { // redirect to previous or
            user.setName(userName);
            user.setEmail(userList.get(0).getEmail());
            user.setPassword(userList.get(0).getPassword());
        }
        return user;
    }

    @RequestMapping("/alterUserInfo")
    public @ResponseBody Result alterUserInfo(@RequestParam("userName") String userName, @RequestParam("password") String password,
                                              @RequestParam("verifiedPassword") String confirmedPassword,
                                              @RequestParam("email") String email) {
        Result result = new Result();
        List<User> userList = userDao.hasUserName(userName);
        User user = userList.get(0);
        user.setName(userName);
        user.setPassword(password);
        user.setEmail(email);
        userDao.update(user);
        result.setSuccess(true);
        return result;
    }


    @RequestMapping("/sendEmail")
    public @ResponseBody Result alterUserInfo(@RequestParam("userName") String userName,
                                              @RequestParam("email") String email) {
        Result result = new Result();
        List<User> userList = userDao.hasUserName(userName);
        User user = userList.get(0);
        if (user.getEmail().equals(email)) {
            String mailContent = "Dear " + userName + ", your password is " + user.getPassword() + ", please try to login with it. And if you have any problems, please contact us. Wish you have a nice day! Cheers. ";
            try {
                SendMailUtil.sendMail(email, mailContent);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
//                throw new RuntimeException(e);
            }
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
            result.setErrMsg("User name and Email address do not match, please check!");
        }
        return result;
    }


    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // 移除session
        session.removeAttribute(WebConfig.SESSION_KEY);
        session.invalidate();

        return "redirect:/static/console.html";
    }


}
