package com.matsim.bean;

/**
 * Created by MingLU on 2018/6/28,
 * Life is short, so get your fat ass moving and chase your damn dream.
 * departureTime1	desiredMode1	tripPurpose1	desFacilityId1	facilityChangable1	desRegion1	desCoordX1	desCoordY1
 * NOTE:depature time ,mode, is between the last act!!!
 *
 */
public class Destination {
    private double departureTime =0;
    private String desiredMode= "car";
    private String tripPurpose = "work";
    private String facilityId = "";
    private boolean facilityChangable = false;
    private String desRegionId = "";
    private double desCoordX = 0;
    private double desCoordY = 0;

    public double getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(double departureTime) {
        this.departureTime = departureTime;
    }

    public String getDesiredMode() {
        return desiredMode;
    }

    public void setDesiredMode(String desiredMode) {
        this.desiredMode = desiredMode;
    }

    public String getTripPurpose() {
        return tripPurpose;
    }

    public void setTripPurpose(String tripPurpose) {
        this.tripPurpose = tripPurpose;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public boolean isFacilityChangable() {
        return facilityChangable;
    }

    public void setFacilityChangable(boolean facilityChangable) {
        this.facilityChangable = facilityChangable;
    }

    public String getDesRegionId() {
        return desRegionId;
    }

    public void setDesRegionId(String desRegionId) {
        this.desRegionId = desRegionId;
    }

    public double getDesCoordX() {
        return desCoordX;
    }

    public void setDesCoordX(double desCoordX) {
        this.desCoordX = desCoordX;
    }

    public double getDesCoordY() {
        return desCoordY;
    }

    public void setDesCoordY(double desCoordY) {
        this.desCoordY = desCoordY;
    }

    @Override
    public String toString() {
        return "{'" +
                "departureTime':" + departureTime +
                ", 'desiredMode':'" + desiredMode +
                "', 'tripPurpose':'" + tripPurpose +
                "', 'facilityId':'" + facilityId +
                "', 'facilityChangable':" + facilityChangable +
                ", 'desRegionId':'" + desRegionId +
                "', 'desCoordX':" + desCoordX +
                ", 'desCoordY':" + desCoordY +
                "}";
    }
}
