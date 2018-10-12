package com.matsim.bean;

import java.io.Serializable;

/**
 * Created by MingLU on 2018/4/9,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class Connection implements Serializable {
    private String id ="";
    private String from ="";
    private String to ="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "{" +
                "'id':'" + id + "'" +
                ", 'from':'" + from + "'" +
                ", 'to':'" + to + "'}";
    }
}
