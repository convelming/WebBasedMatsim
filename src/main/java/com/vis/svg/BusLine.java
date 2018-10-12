package com.vis.svg;

import java.util.Arrays;

/**
 * Created by MingLU on 2018/9/10,
 * this is a simple java bean only have line geo infor
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class BusLine {
    private String name = "";
    private Double[][] value;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double[][] getLine() {
        return value;
    }

    public void setLine(Double[][] line) {
        this.value = line;
    }

    @Override
    public String toString() {


        return "{" +
                "name='" + name + '\'' +
                ", value=" + Arrays.toString( value ) +
                '}';
    }
}
