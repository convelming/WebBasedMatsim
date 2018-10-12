package com.matsim.bean;

import com.matsim.util.PolygonShapeUtil;
import org.matsim.api.core.v01.Coord;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by MingLU on 2018/6/28,
 * Life is short, so get your fat ass moving and chase your damn dream.
 * This class defines an odChain
 *    id	count(no this variable) oriDepatureTime	originCoordX	originCoordY	origionRegion
 *    departureTime1	desiredMode1	tripPurpose1	desFacilityId1	facilityChangable1	desRegion1	desCoordX1	desCoordY1
 * all these files are difine in comma(",") separated files, so if there are special characters in these variables,
 * better replace them first to avoid further parsing failures.
 * Note that there might be multiple agents share same od chain so eventhough there is an id for this class
 * there still might be several individuals, variable count captures this. In this case there will be multiple chains
 * each od chain only describe on agent at a time!
 *
 */
public class OdChain {
    private String id = "";
    private Origin origin;
    private Destination[] destinations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public Destination[] getDestinations() {
        return destinations;
    }

    public void setDestinations(Destination[] destinations) {
        this.destinations = destinations;
    }

    @Override
    public String toString() {
        return "{'id':'" + id +
                "', 'origin':" + origin.toString() +
                ", 'destinationList':" + Arrays.toString(destinations) +
                "}";
    }



}
