package com.matsim.bean;

/**
 * Created by MingLU on 2018/6/13,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class EchartNetworkBean {
    private String dataFolder = "";
    private Integer chunk_COUNT;
    private double centerX;
    private double centerY;

    public String getDataFolder() {
        return dataFolder;
    }

    public void setDataFolder(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    public Integer getCHUNK_COUNT() {
        return chunk_COUNT;
    }

    public void setCHUNK_COUNT(Integer CHUNK_COUNT) {
        this.chunk_COUNT = CHUNK_COUNT;
    }

    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    @Override
    public String toString() {
        return "EchartNetworkBean{" +
                "dataFolder='" + dataFolder + '\'' +
                ", chunk_COUNT=" + chunk_COUNT +
                ", centerX=" + centerX +
                ", centerY=" + centerY +
                '}';
    }
}
