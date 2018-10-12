package com.matsim.user;

import java.sql.Timestamp;

/**
 * Created by MingLU on 2018/5/20,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class SaveAndLoad {

     private int saveId;
     private int userId;
     private String saveName;
     private Timestamp saveTime;
     private String saveInfo;
     private String saveContent;
     private String other;

    public int getSaveId() {
        return saveId;
    }

    public void setSaveId(int saveId) {
        this.saveId = saveId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    public Timestamp getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(Timestamp saveTime) {
        this.saveTime = saveTime;
    }

    public String getSaveInfo() {
        return saveInfo;
    }

    public void setSaveInfo(String saveInfo) {
        this.saveInfo = saveInfo;
    }

    public String getSaveContent() {
        return saveContent;
    }

    public void setSaveContent(String saveContent) {
        this.saveContent = saveContent;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
