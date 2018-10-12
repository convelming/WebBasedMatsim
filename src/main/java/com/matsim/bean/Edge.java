package com.matsim.bean;

import java.io.Serializable;

/**
 * Created by MingLu on 2018/4/13.
 */
public class Edge implements Serializable {
    private String id ="";
    private String fromBlockId ="";
    private String fromBlockType ="";
    private String toBlockId ="";
    private String toBlockType ="";

    public Edge(String id) {
        this.id = id;
    }
    public Edge() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromBlockId() {
        return fromBlockId;
    }

    public void setFromBlockId(String fromBlockId) {
        this.fromBlockId = fromBlockId;
    }

    public String getFromBlockType() {
        return fromBlockType;
    }

    public void setFromBlockType(String fromBlockType) {
        this.fromBlockType = fromBlockType;
    }

    public String getToBlockId() {
        return toBlockId;
    }

    public void setToBlockId(String toBlockId) {
        this.toBlockId = toBlockId;
    }

    public String getToBlockType() {
        return toBlockType;
    }

    public void setToBlockType(String toBlockType) {
        this.toBlockType = toBlockType;
    }


    @Override
    public String toString() {
        return "'Edge':{" +
                "'id':" + id + "'" +
                ", 'fromBlockId':'" + fromBlockId + "'" +
                ", 'fromBlockType':'" + fromBlockType + "'" +
                ", 'toBlockId':'" + toBlockId +  "'" +
                ", 'toBlockType':'" + toBlockType + "'}";
    }
}
