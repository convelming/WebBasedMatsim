package com.matsim.util;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class actually transfer matsim supported network file into spark graphX like
 * triplets. Basically each line have a link, a from node and a to node, it is redundant
 * the privilege of doing that is same as GraphX, lost some saving space in return of
 * independency while cut into sub-networks.
 */
public class XmlNetwork2Triplets {

    public static List<Triplets> tripletsListxmlNetwork2Triplets(String networkFile){
        List<Triplets> tripletsList = new ArrayList<>(  );

        Network network = LoadNetwork.LoadNetwork( networkFile );
        for (Link link:network.getLinks().values()
             ) {
            Node fromNode = network.getNodes().get( link.getFromNode().getId() );
            Node toNode = network.getNodes().get( link.getToNode().getId() );
            Triplets tempTriplet = new Triplets(fromNode,toNode,link);
            tripletsList.add( tempTriplet );
        }

        return tripletsList;
    }
    public static void splitTripletsNetwork(List<Triplets> linkTriplets,String folder,int linkFileSize) throws Exception{

        for(int i=0;i<linkTriplets.size();i++){
            System.out.println(i+", "+ linkTriplets.get( i ));
        };

        System.out.println("there are "+linkTriplets.size()+" triplets");
        File file = new File(folder);
        if (!file.exists()){
            file.mkdirs();
        }else if(!file.isDirectory()){
            System.out.println("specified file is not a folder!");
        }
        int totalFiles = (int)Math.ceil( linkTriplets.size()/linkFileSize );
        System.out.println("There are "+linkTriplets.size()+" links and "+totalFiles+" files.");
        for (int i=0;i<totalFiles;i++){
//            System.out.println("the " +i+"th file...........");
            BufferedWriter bw = new BufferedWriter( new FileWriter( folder+"/subLinkTriplets_"+i+".json" ));
            bw.write( "[" );
            bw.newLine();
            for (int j = 0; j < linkFileSize; j++) {
                int tempIndex = i*linkFileSize+j;
                System.out.println(tempIndex + " of "+ linkTriplets.get( tempIndex ).toString());
                if(j<linkFileSize-1&&!linkTriplets.get( tempIndex ).equals( null )) {
//                    System.out.println(linkTriplets.get( tempIndex ).toString()+",");
                    bw.write(linkTriplets.get( tempIndex ).toString()+",");
                    bw.newLine();
                }else if(j==(linkFileSize-1)||linkTriplets.get( tempIndex ).equals( null )){
//                    System.out.println(linkTriplets.get( tempIndex ).toString()+"}");
                    bw.write(linkTriplets.get( tempIndex ).toString()+"]");
                }
            }
            bw.flush();
            bw.close();
        }

    }

    public static void main(String[] args) throws Exception {
        splitTripletsNetwork(tripletsListxmlNetwork2Triplets("/Users/convel/Desktop/test/testMatsimXml/sz180603.xml"),
                "/users/convel/Desktop/testTriplets",200);
    }
}
class Triplets{

    private Node fromNode;
    private Node toNode;
    private Link link;

    public Triplets(Node fromNode, Node toNode, Link link) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.link = link;
    }

    public Triplets() {
    }

    public Node getFromNode() {
        return fromNode;
    }

    public Node getToNode() {
        return toNode;
    }

    public Link getLink() {
        return link;
    }

    public void setFromNode(Node fromNode) {
        this.fromNode = fromNode;
    }

    public void setToNode(Node toNode) {
        this.toNode = toNode;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "{" +
                "\"fromNodeId\":\"" + fromNode.getId() +
                "\",\"fromNodeCoord\":[" + fromNode.getCoord().getX()+"," + fromNode.getCoord().getY()+
                "],\"toNodeId\":\"" + toNode.getId() +
                "\",\"toNodeCoord\":[" + toNode.getCoord().getX() +","+toNode.getCoord().getY() +
                "], \"linkId\":\"" + link.getId().toString()+
                "\", \"FreeSpeed\":\"" + link.getFreespeed() +
                "\", \"capacity\":\"" + link.getCapacity() +
                "\", \"length\":\"" + link.getLength() +
                "\", \"numOfLanes\":\"" + link.getNumberOfLanes() +
                "\", \"allowedMode\":[" + link.getAllowedModes().toString().replace( "[","\"" ).replace( "]","\"" ) +
                "]}";
    }
}
