package com.matsim.bean;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.geometry.CoordinateTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MingLU on 2018/6/22,
 * Life is short, so get your fat ass moving and chase your damn dream.
 * This class actually describes a person's journey between two activities
 * According to the manual of Matsim, person activity is recorded in plan.xml
 * a person have several plans but only one is marked selected, under each plan there are PlanElements, which maybe leg or act.
 * Leg or Act can be parsed using instanceOf class, with respect to LegImpl and ActivityImpl class.
 * All these are parsed in
 * This class is a bean class tries to record all information above
 */
public class PersonActivity {
    // time stamp of all those events

    private List<Double> actXs =  new ArrayList<>();
    private List<Double> actYs =  new ArrayList<>();
    private List<String> actInfo = new ArrayList<>();
    private String defaultContent;
    private Double centerX,centerY;

    public List<Double> getActXs() {
        return actXs;
    }

    public void setActXs(List<Double> actXs) {
        this.actXs = actXs;
    }

    public List<Double> getActYs() {
        return actYs;
    }

    public void setActYs(List<Double> actYs) {
        this.actYs = actYs;
    }

    public List<String> getActivityInfo() {
        return actInfo;
    }

    public void setActivityInfo(List<String> activityInfo) {
        this.actInfo = activityInfo;
    }

    public String getDefaultContent() {
        return defaultContent;
    }

    public void setDefaultContent(String defaultContent) {
        this.defaultContent = defaultContent;
    }

    public Double getCenterX() {
        return centerX;
    }

    public void setCenterX(Double centerX) {
        this.centerX = centerX;
    }

    public Double getCenterY() {
        return centerY;
    }

    public void setCenterY(Double centerY) {
        this.centerY = centerY;
    }
    public double calAvgList(List<Double> list){
        double avg,sum=0;
        for (Double d:list) {
            sum += d;
        }
        avg = sum/list.size();
        return avg;
    }
    @Override
    public String toString() {
        return "PersonActivity{" +
                "activityXs=" + actXs +
                ", activityYs=" + actYs +
                ", activityInfo=" + actInfo +
                ", defaultContent='" + defaultContent + '\'' +
                ", centerX=" + centerX +
                ", centerY=" + centerY +
                '}';
    }
}
