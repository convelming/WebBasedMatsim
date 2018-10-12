package com.matsim.util;

/* *********************************************************************** *
 * project: org.matsim.*
 * ScenarioCut.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */


import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.utils.gis.matsim2esri.network.Links2ESRIShape;
import org.opengis.geometry.BoundingBox;

/**
 * Created by MingLU on 2018/6/10,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class CutNetworkUtil {


        public Network reduceNetwork(Network network, Coord center, double radius) {
            // this method is keep the network but cut out all links out of the
            System.out.println("removing links outside of circle ("+center.toString()+";"+radius+""+")... " + (new Date()));
            // filtering network links
            Set<Id> removeLinks = new HashSet<Id>();
            for (Link links : network.getLinks().values()) {
                Coord fc = links.getFromNode().getCoord();
                Coord tc = links.getToNode().getCoord();
                if (calDistance(fc,center) > radius) {
                    removeLinks.add(links.getId());
                }else if (calDistance(tc,center) > radius) {
                    removeLinks.add(links.getId()); }
            }
            System.out.println("=> "+removeLinks.size()+" links to remove.");
            for (Id id : removeLinks) {
                network.removeLink(id);
            }
            System.out.println("=> "+network.getLinks().size()+" links left.");

            // filtering network nodes
            Set<Id> removeNodes = new HashSet<Id>();

            for (Node nodes : network.getNodes().values()) {
                Coord nc = nodes.getCoord();
                if (calDistance(nc,center) > radius) {
                    removeNodes.add(nodes.getId());
                }
            }
            System.out.println("=> "+removeNodes.size()+" nodes to remove.");
            for (Id id : removeNodes) {
                network.removeNode(id);
            }
            System.out.println("=> "+network.getNodes().size()+" nodes left.");
            // cleaning the network see if there are some isolated links or nodes
            System.out.println("Removing links and nodes are done. Beginning to clean network... " + (new Date()));
            new NetworkCleaner().run(network);
            return network;

        }

	public static Network reduceNetwork(Network network, Coord min, Coord max) {
		System.out.println("removing links outside of rectangle ("+min.toString()+";"+max.toString()+""+")... " + (new Date()));
		Set<Id> toRemove = new HashSet<Id>();
		for (Link l : network.getLinks().values()) {
			Coord fc = l.getFromNode().getCoord();
			if (fc.getX() < min.getX()) { toRemove.add(l.getId()); continue; }
			if (fc.getX() > max.getX()) { toRemove.add(l.getId()); continue; }
			if (fc.getY() < min.getY()) { toRemove.add(l.getId()); continue; }
			if (fc.getY() > max.getY()) { toRemove.add(l.getId()); continue; }
			Coord tc = l.getToNode().getCoord();
			if (tc.getX() < min.getX()) { toRemove.add(l.getId()); continue; }
			if (tc.getX() > max.getX()) { toRemove.add(l.getId()); continue; }
			if (tc.getY() < min.getY()) { toRemove.add(l.getId()); continue; }
			if (tc.getY() > max.getY()) { toRemove.add(l.getId()); continue; }
		}
		System.out.println("=> "+toRemove.size()+" links to remove.");
		for (Id id : toRemove) { network.removeLink(id); }
		System.out.println("=> "+network.getLinks().size()+" links left.");
		System.out.println("done. " + (new Date()));

//		System.out.println("cleaning network... " + (new Date()));
//		new NetworkCleaner().run(network);
		System.out.println("done. " + (new Date()));
		return network;
	}

    public static void main(String[] args) {

//        double[] test = getNetworkBounding( network );
//        System.out.println(test[0]+", "+test[1]+", "+test[2]+", "+test[3]);
//        //        Links2ESRIShape links2ESRIShape = new Links2ESRIShape( network,"/users/convel/Desktop/testLink2Shp.shp","WGS84");
//
//        links2ESRIShape.write();
//           new NetworkWriter(new CutNetworkUtil().reduceNetwork( network,new Coord( 114.1158,22.2790 ),0.3 )).write( "/Users/convel/Desktop/cuttedNetwork.xml" );
        cutNetwork("/Users/convel/Desktop/sz180603.xml","/Users/convel/Desktop/testCutnetwork","testCutNetwork",2,3);
    }
    public static double calDistance(Coord coord1,Coord coord2){
            return Math.sqrt( (coord1.getX()-coord2.getX())*(coord1.getX()-coord2.getX()) +
                    (coord1.getX()-coord2.getX())*(coord1.getX()-coord2.getX()));
    }
    public static double[] getNetworkBounding(Network network){
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        for (Map.Entry node:network.getNodes().entrySet()) {
            Node tempNode = (Node)node.getValue();
            Coord tempCoord = tempNode.getCoord();
            if (minX>tempCoord.getX()) minX = tempCoord.getX();
            if (minY>tempCoord.getY()) minY = tempCoord.getY();
            if (maxX<tempCoord.getX()) maxX = tempCoord.getX();
            if (maxY<tempCoord.getY()) maxY = tempCoord.getY();
        }
        return new double[]{minX, minY, maxX, maxY};
    }

    /**
     * This method split network into numX * numY sub-network
     * @param xmlNetwork
     * @param path
     * @param networkName- prefix of the cutted network
     * @param numX--number of split in x-axis
     * @param numY--number of split in y-axis
     */
    public static void cutNetwork(String xmlNetwork,String path,String networkName,int numX,int numY){
        Config config = ConfigUtils.createConfig();
        Scenario sc = ScenarioUtils.createScenario(config);
        new MatsimNetworkReader(sc.getNetwork()).readFile(xmlNetwork);
        Network network = sc.getNetwork();

        double[] bounding = getNetworkBounding( network );
        double tempXInterval = (bounding[2]-bounding[0])/(numX-1);
        double tempYInterval = (bounding[3]-bounding[1])/(numY-1);
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        for (int i = 1; i < numX; i++) {
            for (int j = 1; j < numY; j++) {
                Config tempConfig = ConfigUtils.createConfig();
                Scenario tempScenario = ScenarioUtils.createScenario(tempConfig);
                new MatsimNetworkReader(tempScenario.getNetwork()).readFile(xmlNetwork);
                Network tempNetwork = tempScenario.getNetwork();
                System.out.println(tempNetwork.getLinks().size()+" =====================");
                Coord minCoord = new Coord( bounding[0] + i * tempXInterval,bounding[1] + j * tempYInterval);
                Coord maxCoord =new Coord( bounding[0] + (i+1) * tempXInterval,bounding[1] + (j+1) * tempYInterval);
                System.out.println(minCoord+","+maxCoord);
                NetworkWriter networkWriter = new NetworkWriter(reduceNetwork(tempNetwork, minCoord,maxCoord));
                networkWriter.write( path+"/"+networkName+"_"+i+"_"+j+".xml" );
            }

        }
    }


}
