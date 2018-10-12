package com.matsim.bean;

/**
 * Created by MingLU on 2018/6/28,
 * Life is short, so get your fat ass moving and chase your damn dream.
 *  *  originCoordX	originCoordY	origionRegion

 */
public class Origin {
    private double originCoordX = 0;
    private double originCoordY = 0;
    private double oriDepartureTime =0;
    private String oriRegionId="";

    public double getOriginCoordX() {
        return originCoordX;
    }

    public void setOriginCoordX(double originCoordX) {
        this.originCoordX = originCoordX;
    }

    public double getOriginCoordY() {
        return originCoordY;
    }

    public void setOriginCoordY(double originCoordY) {
        this.originCoordY = originCoordY;
    }

    public String getOriRegionId() {
        return oriRegionId;
    }

    public void setOriRegionId(String oriRegionId) {
        this.oriRegionId = oriRegionId;
    }

    public double getOriDepartureTime() {
        return oriDepartureTime;
    }

    public void setOriDepartureTime(double departureTime) {
        this.oriDepartureTime = departureTime;
    }

    @Override
    public String toString() {
        return "{'" +
                "originCoordX':" + originCoordX +
                ", 'originCoordY':" + originCoordY +
                ", 'origionRegion':'" + oriRegionId +
                ", 'oriDepatureTime':'" + oriDepartureTime +
                "'}";
    }
}
