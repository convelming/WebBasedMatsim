package com.matsim.controller;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.LinkImpl;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkImpl;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

/**
 * Created by MingLU on 2018/7/30,
 * Life is short, so get your fat ass moving and chase your damn dream.
 * This file
 */

public class LinkStatsController {

    /**
     *
     * @param input linksStats file, normally matsim generate it as .gz
     */
    public void getAvgSpeedFromLinkStats(String input,String output, NetworkImpl network) throws Exception{

        InputStream in = null;
        try {
            in = new GZIPInputStream(new FileInputStream(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner sc=new Scanner(in);
        List<String> lines=new ArrayList();
        System.out.println(sc.nextLine());
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] temp = line.split( "\t" );
//            lines.add(sc.nextLine());
//            System.out.println(line);
//            double freeSpeed = Double.parseDouble( temp[5] )+0.0001;
            lines.add(temp[0]+","+temp[2]+","+temp[3]+","+temp[5]+","+
                    temp[8]+","+temp[11]+","+temp[14]+","+temp[17]+","+
                    temp[20]+","+temp[23]+","+temp[26]+","+temp[29]+","+
                    temp[32]+","+temp[35]+","+temp[38]+","+temp[41]+","+
                    temp[44]+","+temp[47]+","+temp[50]+","+temp[53]+","+
                    temp[56]+","+temp[59]+","+temp[62]+","+temp[65]+","+
                    temp[68]+","+temp[71]+","+temp[74]+","+temp[77]);
        }
        // output is a folder, if not, it will be created
        File file = new File(output);
        if(!file.exists()){
            file.mkdirs();
        }
        // create json file, each hour is a single file
        BufferedWriter bws [] = new BufferedWriter[24];
        Map<Id<Node>,Node> nodes = network.getNodes();
        Map<Id<Link>,Link> links = network.getLinks();
//        for (Map.Entry link:links.entrySet()
//             ) {
//            System.out.println("Link capacity: "+link.getKey()+": "+((LinkImpl)link.getValue()).getCapacity());
//        }
        for (int i = 0; i < 24; i++) {
            bws[i] = new BufferedWriter( new FileWriter( output + "/subLinkStat_" + i + ".json" ) );
            bws[i].write( "[" );
        }
        // for each moving line the format is as follows:
        //  [{coords:[[x1,y1],[x2,y2],...,[xn,yn]],lineStyle:{normal:{color:"green,yellow,orange or red"}}},...{}]
        // and for this particular case there is only x1,y1],[x2,y2], color is only four
        // green if avgSpeed/freeSpeed < 0.4, yellow if [0.4,0.6], orange if [0.6,0.8] and red if >0.8
        for (String line:lines) {
            String[] tempLine = line.split( "," );
            Id<Link> linkId = Id.createLinkId( tempLine[0] );
            Link tempLink = links.get( linkId );
            if(tempLink.getCapacity()>=1500) {

                Coord fromCoord = nodes.get( Id.createNodeId( tempLine[1] ) ).getCoord();
                Coord toCoord = nodes.get( Id.createNodeId( tempLine[2] ) ).getCoord();
                double freeSpeed = Double.parseDouble( tempLine[3] ) + 0.0001;
                for (int i = 0; i < 24; i++) {
//                    double speedRatio = Double.parseDouble( tempLine[4 + i] ) / freeSpeed;
                    int color = 0;
                    double speedRatio = Math.random();//todo needs to change back
                    if (speedRatio > 0.75 && speedRatio <= 0.8) {
                        color = 1;
                    } else if (speedRatio > 0.8 && speedRatio <= 0.9) {
                        color = 2;
                    } else if (speedRatio > 0.9) {
                        color = 3;
                    }
                    bws[i].write( "[" + (int) Math.floor( fromCoord.getX() * 10000 )
                            + "," + (int) Math.floor( fromCoord.getY() * 10000 )
                            + "," + (int) (Math.floor( toCoord.getX() * 10000 ) - (int) Math.floor( fromCoord.getX() * 10000 ))
                            + "," + (int) (Math.floor( toCoord.getY() * 10000 ) - (int) Math.floor( fromCoord.getY() * 10000 ))
                            + "," + color + "]," );
                    bws[i].newLine();

                }
            }
        }
        for (int i = 0; i < 24; i++) {
            bws[i].write( "]" );
            bws[i].flush();
            bws[i].close();
        }

    }


    public static void main(String[] args) throws Exception{
        LinkStatsController lsc = new LinkStatsController();
        Config config = ConfigUtils.createConfig();
        Scenario sc = ScenarioUtils.createScenario(config);
        new MatsimNetworkReader(sc.getNetwork()).readFile("/Users/convel/Desktop/output/output_network.xml.gz");
        NetworkImpl network = (NetworkImpl)sc.getNetwork();
        lsc.getAvgSpeedFromLinkStats( "/Users/convel/Desktop/output/ITERS/it.10/10.linkstats.txt.gz",
                "/Users/convel/Desktop/busLine",network );
    }
}
