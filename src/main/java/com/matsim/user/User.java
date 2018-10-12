package com.matsim.user;

import java.sql.Timestamp;

/**
 * Created by MingLU on 2018/5/18,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class User {
    private int id;
    private String name;
    private int level;
    private String password;
    private String email;
    private int saveBlockNum;
    private String allowedBlockList;
    private String saveUrl;
    private String tempUrl;
    private String other;
    private Timestamp loginTime;
    private Timestamp registerTime;
    //getter and setter


    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public Timestamp getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Timestamp registerTime) {
        this.registerTime = registerTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSaveBlockNum() {
        return saveBlockNum;
    }

    public void setSaveBlockNum(int saveBlockNum) {
        this.saveBlockNum = saveBlockNum;
    }

    public String getAllowedBlockList() {
        return allowedBlockList;
    }

    public void setAllowedBlockList(String allowedBlockList) {
        this.allowedBlockList = allowedBlockList;
    }

    public String getSaveUrl() {
        return saveUrl;
    }

    public void setSaveUrl(String saveUrl) {
        this.saveUrl = saveUrl;
    }

    public String getTempUrl() {
        return tempUrl;
    }

    public void setTempUrl(String tempUrl) {
        this.tempUrl = tempUrl;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }


}