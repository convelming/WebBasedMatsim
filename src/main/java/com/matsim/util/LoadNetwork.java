package com.matsim.util;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkImpl;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.logging.Logger;

/**
 * Created by MingLU on 2018/9/25,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class LoadNetwork {

    private final static Logger log = Logger.getLogger( "LoadNetwork.class" );
    public final static NetworkImpl LoadNetwork(String networkFile){
//        log.info("Loading network...");
        Config config = ConfigUtils.createConfig();
        config.network().setInputFile(networkFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        NetworkImpl network = (NetworkImpl)scenario.getNetwork();
//        log.info("Loading network is done.");
        return network;
    }

    /**
     * get the min and max lng, and lat in the network,
     * not it is a boundbing box, may not actually on the network
     * @param network
     * @return
     */
    public static double[] getBoudningBox(NetworkImpl network){
        double minLng = Double.MAX_VALUE;
        double minLat = Double.MAX_VALUE;
        double maxLng = Double.MIN_VALUE;
        double maxLat = Double.MIN_VALUE;
        for (Link link:network.getLinks().values()
             ) {
            if (minLng>link.getFromNode().getCoord().getX()) minLng = link.getFromNode().getCoord().getX();
            if (minLng>link.getToNode().getCoord().getX()) minLng = link.getToNode().getCoord().getX();

            if (maxLng<link.getFromNode().getCoord().getX()) maxLng = link.getFromNode().getCoord().getX();
            if (maxLng<link.getToNode().getCoord().getX()) maxLng = link.getToNode().getCoord().getX();

            if (minLat>link.getFromNode().getCoord().getY()) minLat = link.getFromNode().getCoord().getY();
            if (minLat>link.getToNode().getCoord().getY()) minLat = link.getToNode().getCoord().getY();

            if (maxLat<link.getFromNode().getCoord().getY()) maxLat = link.getFromNode().getCoord().getY();
            if (maxLat<link.getToNode().getCoord().getY()) maxLat = link.getToNode().getCoord().getY();

        }
        double boundingbox[]={minLng,minLat,maxLng,maxLat};
        return boundingbox;
    }
}
