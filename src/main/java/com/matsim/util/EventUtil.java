package com.matsim.util;

import com.matsim.bean.PersonActivity;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkImpl;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by MingLU on 2018/9/27,
 * This class transfer event file according requested range into
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class EventUtil {
    public static Logger log = Logger.getLogger(EventUtil.class);
    NetworkImpl network;
//    List<Element> eventsList;
    List<Element> linkEvents = new ArrayList<>(  );

    EventUtil(String eventFile,String networkFile){
        Config config = ConfigUtils.createConfig();
        Scenario sc = ScenarioUtils.loadScenario(config);
        new MatsimNetworkReader(sc.getNetwork()).readFile(networkFile);
        this.network = (NetworkImpl) sc.getNetwork();

        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File(eventFile));
            Element ele = document.getRootElement();
            List<Element> eventsList = ele.elements();
//            String tempVehicleId = "";
            for (Element event:eventsList){
//                List<Attribute> attrList = event.attributes();
                if (event.attribute( "link" )!=null){
//                    System.out.println(event.attribute( "link" ).getValue());
                    linkEvents.add( event );
                }

            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     *
     * @param minLng
     * @param minLat
     * @param maxLng
     * @param maxLat
     * @return
     */
    public Set<Id<Link>> getLinksInRange(double minLng,double minLat,double maxLng,double maxLat){

        Set<Id<Link>> linkIdSet = new HashSet<>();

        for (Link link : network.getLinks().values()) {
            Coord fc = link.getFromNode().getCoord();
//            System.out.print(fc.getX() +", "+ minLng+", "+fc.getX() +", "+ maxLng+", "+
//                    fc.getY() +", "+ minLat+", "+fc.getY() +", "+ maxLat+", ");
            if (fc.getX() >= minLng && fc.getX() <= maxLng
                    && fc.getY() >= minLat && fc.getY() <= maxLng) {
//                System.out.println("__________________________________________");
                linkIdSet.add(link.getId());
            }
            Coord tc = link.getToNode().getCoord();
            if (tc.getX() >= minLng && tc.getX() > maxLng
                    && tc.getY() >= minLat && tc.getY() <= maxLat ) {
                linkIdSet.add(link.getId());
//                System.out.println("__________________________________________");
            }
//            System.out.println();
        }
//        System.out.println("There"+network.getLinks().size()+" links");
//        System.out.println("there are "+linkIdSet.size()+" links in range.");
//            new NetworkCleaner().run(network);
        return linkIdSet;//(List<Link>)tempNetwork.getLinks().values();
    }

    /**
     * This method filters out these events out of specified range
     * @param linkIdSet links in range
     * @param startTime for
     */
    public List<Element> getEventsByRange(Set<Id<Link>> linkIdSet, double startTime){
        List<Element> eventsInRange = new ArrayList<>(  );
        for(Element element:linkEvents){
            if(linkIdSet.contains( Id.createLinkId( element.attribute( "link" ).getValue() ) )
                    && Double.parseDouble( element.attribute( "time" ).getValue())>=startTime){
                eventsInRange.add( element );

//                System.out.println(element.getText());
            }
        }
        System.out.println("There are "+linkEvents.size()+ " total events and "+ eventsInRange.size()+" events in range.");
        return eventsInRange;
    }
    public static void main(String[] args) {
        EventUtil eu = new EventUtil( "/Users/convel/Desktop/test/testMatsimXml/output/output_events.xml",
                "/Users/convel/Desktop/test/testMatsimXml/output/output_network.xml");
        Set<Id<Link>> linkidSet = eu.getLinksInRange( 113.9180,22.5437,113.9666,22.5561 );
        eu.getEventsByRange( linkidSet,3600*16 );
    }

}
