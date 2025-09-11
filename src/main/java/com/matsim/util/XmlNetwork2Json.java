package com.matsim.util;
import com.matsim.bean.EchartNetworkBean;
import com.matsim.bean.Result;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class XmlNetwork2Json {
    private Logger log = Logger.getLogger("XmlNetwork2Json.class");

    public static void main(String[] args) {

//        System.out.println( result.getData().toString() );

    }

    /**
     *
     * @param xmlNetwork
     * @param linkSize
     * @param absPath  this is the absolute file
     * @param webPath   this is fake or static files, as web content files using only ralative path
     * @return
     * @throws IOException
     */
    public EchartNetworkBean xmlNetwork2SubJsonFiles(String xmlNetwork, int linkSize, String absPath,String webPath) throws IOException {
        EchartNetworkBean echartNetworkBean = new EchartNetworkBean();
        Config config = ConfigUtils.createConfig();
        Scenario sc = ScenarioUtils.createScenario(config);
        new MatsimNetworkReader(sc.getNetwork()).readFile(xmlNetwork);
        Network network = sc.getNetwork();
        Map<Id<Link>,Link> linkMap = (Map<Id<Link>, Link>) network.getLinks();

        double[] bounding = CutNetworkUtil.getNetworkBounding(network);
        double xMin = bounding[0];
        double xMax = bounding[2];
        double yMin = bounding[1];
        double yMax = bounding[3];
        double avgX = (xMax+xMin)/2;
        double avgY = (yMax+yMin)/2;

        List<String> linkCoords = new ArrayList<>();
        for (Map.Entry link:linkMap.entrySet()) {
            Link tempLink = (Link) link.getValue();
            linkCoords.add((tempLink.getFromNode().getCoord().getX()-avgX)+","+(tempLink.getFromNode().getCoord().getY()-avgY)+","+
                    (tempLink.getToNode().getCoord().getX()-avgX)+","+(tempLink.getToNode().getCoord().getY()-avgY));
        }

        int CHUNK_COUNT = (int) Math.ceil( linkMap.size()/linkSize);
        Random random = new Random();
        File file = new File(absPath);
        if (!file.exists()){
            file.mkdirs();
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+absPath);
        for (int i = 0; i <CHUNK_COUNT ; i++) {
            int iLink = 0;
            String fileName = absPath+"/subnet_"+i+".json";
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            bufferedWriter.write( "["+ avgX+","+avgY);
            while(iLink<linkSize){
                int rndLink = random.nextInt(linkCoords.size());
                bufferedWriter.write(",2,"+linkCoords.get(rndLink));
                linkCoords.remove(rndLink);
                iLink++;
            }
            bufferedWriter.write( "]" );
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        echartNetworkBean.setCHUNK_COUNT( CHUNK_COUNT );
        log.info( echartNetworkBean.getCHUNK_COUNT()+"<><><><><><><><><><><><." );
        echartNetworkBean.setCenterX( avgX );
        echartNetworkBean.setCenterY( avgY );
        echartNetworkBean.setDataFolder(  webPath );
        log.info( echartNetworkBean.toString() );
        return echartNetworkBean;

    }
}
