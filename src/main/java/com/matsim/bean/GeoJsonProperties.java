package com.matsim.bean;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkImpl;

/**
 * Created by MingLU on 2018/9/28,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class GeoJsonProperties {
    private String name;
    private String id;
    private String fromNodeId;
    private String toNodeId;
    private double length;
    private double freeSpeed;
    private double capacity;
    private double numLanes;
    private String origId;
    private String type;
    private String other;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromNodeId() {
        return fromNodeId;
    }

    public void setFromNodeId(String fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    public String getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getFreeSpeed() {
        return freeSpeed;
    }

    public void setFreeSpeed(double freeSpeed) {
        this.freeSpeed = freeSpeed;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getNumLanes() {
        return numLanes;
    }

    public void setNumLanes(double numLanes) {
        this.numLanes = numLanes;
    }

    public String getOrigId() {
        return origId;
    }

    public void setOrigId(String origId) {
        this.origId = origId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"" +
                ", \"id\":\"" + id + "\"" +
                ", \"fromNodeId\":\"" + fromNodeId + "\"" +
                ", \"toNodeId\":\"" + toNodeId + "\"" +
                ", \"length\":" + length +
                ", \"freeSpeed\":" + freeSpeed +
                ", \"capacity\":" + capacity +
                ", \"numLanes\":" + numLanes +
                ", \"origId\":\"" + origId + "\"" +
                ", \"type\":\"" + type + "\"" +
                ", \"other\":\"" + other + "\"" +
                '}';
    }
}
