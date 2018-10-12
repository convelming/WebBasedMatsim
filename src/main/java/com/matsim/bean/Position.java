package com.matsim.bean;

import java.io.Serializable;

/**
 * Created by MingLU on 2018/4/6,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class Position implements Serializable {
    // "top":"10","left":"100", "zIndex":"1"
    private String id ="";
    private int top;
    private int left;
    private int zIndex;

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "'position':{" +
                "'id':'" + id + "'" +
                ", 'top':'" + top +"'" +
                ", 'left':'" + left +"'" +
                ", 'zIndex':'" + zIndex+"'}";
    }
}
