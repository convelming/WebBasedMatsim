package com.matsim.util;

import com.matsim.bean.GeoJsonFeature;
import com.matsim.bean.GeoJsonGeometry;
import com.matsim.bean.GeoJsonProperties;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.network.NetworkImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MingLU on 2018/9/28,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class XmlNetwork2GeoJson {
    public String geoJson;
    public List<GeoJsonFeature> links = new ArrayList<>();
    public double[] boundingBox;

    public XmlNetwork2GeoJson(String xmlNetworkFile){

        NetworkImpl network = LoadNetwork.LoadNetwork( xmlNetworkFile );
        for (Link link:network.getLinks().values()
             ) {
            GeoJsonFeature linkFeature = new GeoJsonFeature();
            GeoJsonGeometry geometry = new GeoJsonGeometry();
            String coords = "[["+link.getFromNode().getCoord().getX()+","+link.getFromNode().getCoord().getY()+"],["
                    +link.getToNode().getCoord().getX()+","+link.getToNode().getCoord().getY()+"]]";
            geometry.setCoordinates( coords );
            geometry.setType( "LineString" );
            linkFeature.setGeometry( geometry );

            GeoJsonProperties properties = new GeoJsonProperties();
            properties.setId( link.getId().toString() );
            properties.setName( "link" );
            properties.setCapacity(link.getCapacity());
            properties.setFreeSpeed( link.getFreespeed() );
            properties.setNumLanes( link.getNumberOfLanes() );
            properties.setLength( link.getLength() );
            properties.setType( link.getAllowedModes().toString().replace( "[","" ).replace( "]","" ) );
            properties.setFromNodeId( link.getFromNode().getId().toString() );
            properties.setToNodeId( link.getToNode().getId().toString() );
            linkFeature.setProperties( properties );
            links.add( linkFeature );
//            System.out.println(linkFeature.toString());
        }
        geoJson = links.toString();
//        System.out.println( Arrays.toString(links.toArray()));
        boundingBox = LoadNetwork.getBoudningBox( network );

    }


    /**
     *
     * @param linkSize number of links in each String in the returned list
     * @return each String in the list contains linkSize(number of ) links
     *
     */
    public String[] splitLinks(int linkSize){
        int length = (int)Math.ceil( this.links.size()/linkSize);
        String[] splitedLinks = new String[length];
        for (int i = 0; i < length ; i++) {
            List<GeoJsonFeature> tempList = new ArrayList( );
            for(int j=0;j<linkSize;j++){
                if((i*linkSize+j)<links.size()) {
                    tempList.add( links.get( i * linkSize + j ) );
                }
            }
            splitedLinks[i] = Arrays.toString( tempList.toArray(  ) );
            System.out.println(splitedLinks[i]);
        }
        return splitedLinks;
    }

    /**
     * for simplication and data transfer, all coords are minus min lng and lat times 1000
     * record in
     * @return
     */
    public static int[][] getOnlyLineCoords(String xmlNetworkFile){
        NetworkImpl network = LoadNetwork.LoadNetwork( xmlNetworkFile );
        List<Link> links = new ArrayList<>();
        for (Link l: network.getLinks().values()
             ) {
            links.add( l );
        }
        int[][] coords = new int[links.size()][4];

        double boundingBox[] = LoadNetwork.getBoudningBox( network );
        System.out.println(boundingBox[2]-boundingBox[0]);
        System.out.println(boundingBox[3]-boundingBox[1]);
//        System.out.println(boundingBox[0]+","+boundingBox[1]+","+boundingBox[2]+","+boundingBox[3]+",");
        for (int i = 0; i <links.size(); i++) {
            coords[i][0] = (int) (1000*(links.get( i ).getFromNode().getCoord().getX()-boundingBox[0]));
            coords[i][1] = (int) (1000*(links.get( i ).getFromNode().getCoord().getY()-boundingBox[1]));
            coords[i][2] = (int) (1000*(links.get( i ).getToNode().getCoord().getX()-boundingBox[0]));
            coords[i][3] = (int) (1000*(links.get( i ).getToNode().getCoord().getY()-boundingBox[1]));
//            System.out.println(coords[i][0]+","+coords[i][1]+","+coords[i][2]+","+coords[i][3]);
        }
        return  coords;

    }


    public static void main(String[] args) {
//        XmlNetwork2GeoJson test = new XmlNetwork2GeoJson("/Users/convel/Desktop/zhengzhou0926.xml");
        int[][] test =XmlNetwork2GeoJson.getOnlyLineCoords( "/Users/convel/Desktop/zhengzhou0926.xml" );
        System.out.println(test.length);
    }
}
