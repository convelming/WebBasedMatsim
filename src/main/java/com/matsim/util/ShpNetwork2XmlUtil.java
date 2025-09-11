package com.matsim.util;

import com.matsim.bean.Block;
import com.matsim.bean.Result;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;


public class ShpNetwork2XmlUtil {

    private Logger log = Logger.getLogger("WorkSpaceController.class");


    public static void main(String[] args) {
        String f = "/Users/convel/Documents/GIS数据/深圳市地图/百度导航地图/Streets.shp";
//        readDbfFile(f );
//        readShpFile(f);
        //shp2MastimNetwork(f,"4326","DIR",null,null,
        // null,null,null,"ID");
//        List<String> test = getDbfHeader(f);
//        for(String t:test){
//            System.out.println(t);
//        }
    }

    public static List<String> getDbfHeader(String file) {
        DbaseFileReader reader = null;
        List<String> fieldsNames = new ArrayList<>();
        try {
            reader = new DbaseFileReader(new ShpFiles(file), false, Charset.forName("GBK"));
            DbaseFileHeader header = reader.getHeader();
//            int f = header.getHeaderLength();
            for (int i = 0; i < header.getNumFields(); i++) {
                fieldsNames.add(header.getFieldName(i));
            }
//            System.out.println(header.getNumFields());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fieldsNames;
    }

    public static void readDbfFile(String file) {
        DbaseFileReader reader = null;
        try {
            reader = new DbaseFileReader(new ShpFiles(file), false, Charset.forName("GBK"));
            DbaseFileHeader header = reader.getHeader();
            int numFields = header.getNumFields();

            //迭代读取记录
            while (reader.hasNext()) {
                try {
                    Object[] entry = reader.readEntry();
                    for (int i = 0; i < numFields; i++) {
                        String title = header.getFieldName(i);
                        Object value = entry[i];
//                        System.out.println(title+"="+value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                //关闭
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }
    }


    /**
     * this method tries to transform shp file into a matsim-supported network. it has to specify
     * some fields so the program knows how to deal with certain attributes
     * Users have to do some modifications to their shp files using whichever GIS softwares
     *
     * @return
     */

    //todo need to specify all
//    public static Network shp2MastimNetwork(String shpFile, String epsg, String directionField,
//                                            String lengthInMeterField, String capacityField,
//                                            String numLanesField, String speedLimitField, String typeField, String origIdField){
    public Result shp2MastimNetwork(Block networkBlock, String shpFile) {
        Result result = new Result();
        //todo need to modify when user contrl system is done.
//        String shpFile = session.getAttribute("userName").toString()+"/network/"+(new File(networkBlock.getNetworkShpFile()).getName());
        // get block attribute
        // todo check epsg code input is in correct format
        String networkDefaultEPSG = networkBlock.getNetworkDefaultEPSG();
        String networkDesiredEPSG = networkBlock.getNetworkDesiredEPSG();
        String linkDir = networkBlock.getLinkDir();
        String linkLength = networkBlock.getLinkLength();
        String linkSpeed = networkBlock.getLinkSpeed();
        String linkLane = networkBlock.getLinkLane();
        String linkMode = networkBlock.getLinkMode();
        String linkCapacity = networkBlock.getLinkCapacity();


        Network network = NetworkUtils.createNetwork();
        try {
            ShapefileDataStore shpDataStore = null;
            shpDataStore = new ShapefileDataStore(new File(shpFile).toURI().toURL());
            shpDataStore.setCharset(Charset.forName("GBK"));
            String typeName = shpDataStore.getTypeNames()[0];
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;
            featureSource = shpDataStore.getFeatureSource(typeName);
            FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = featureSource.getFeatures();
            FeatureIterator<SimpleFeature> iterator = featureCollection.features();
            CoordinateTransformation ct = TransformationFactory
                    .getCoordinateTransformation(networkDefaultEPSG, networkDesiredEPSG);//"EPSG:32649");
//            xy = ct.transform(xy);

            while (iterator.hasNext()) {

                SimpleFeature feature = iterator.next();
                String the_goem = feature.getAttribute("the_geom").toString();
                if (the_goem.startsWith("MULTILINESTRING")) {
                    String lines[] = the_goem.replace
                                    ("MULTILINESTRING ((", "").
                            replace("))", "").split("\\) , \\(");
                    for (int i = 0; i < lines.length; i++) {
                        String[] points = lines[i].split(",");
                        for (int j = 0; j < points.length - 1; j++) {
                            // get node x y
                            String[] fromPoint = points[j].trim().split(" ");
                            String[] toPoint = points[j + 1].trim().split(" ");
                            double fromX = Double.parseDouble(fromPoint[0]);
                            double fromY = Double.parseDouble(fromPoint[1]);
                            double toX = Double.parseDouble(toPoint[0]);
                            double toY = Double.parseDouble(toPoint[1]);

                            // get link length
                            double length = 0;
                            if (!linkLength.equalsIgnoreCase("null") || !linkLength.isEmpty() || !linkLength.equals("")) {
//                                log.info( feature.getAttribute(networkBlock.getLinkLength()).toString() );
                                length = Double.parseDouble(feature.getAttribute(networkBlock.getLinkLength()).toString());

                            } else {
                                length = getDistance(fromX, fromY, toX, toY);
                            }
                            // get link speed
                            double speed = 16.67;
                            if (!linkSpeed.equalsIgnoreCase("null") || !linkSpeed.isEmpty() || !linkSpeed.equals("")) {
                                length = Double.parseDouble(feature.getAttribute(networkBlock.getLinkSpeed()).toString());
                            }
                            // get link capacity
                            double capacity = 600;
                            if (!linkCapacity.equalsIgnoreCase("null") || !linkCapacity.isEmpty() || !linkCapacity.equals("")) {
//                                log.info( networkBlock.getLinkCapacity() );
                                capacity = Double.parseDouble(feature.getAttribute(networkBlock.getLinkCapacity()).toString());
                            }
                            // get lane number
                            double lanes = 1;
                            if (!linkLane.equalsIgnoreCase("null") || !linkLane.isEmpty() || !linkLane.equals("")) {
                                length = Double.parseDouble(feature.getAttribute(networkBlock.getLinkLane()).toString());
                            }
                            // get link allowed mode
                            Set<String> allowedModes = new HashSet<>();
                            if (!linkMode.equalsIgnoreCase("null") || !linkMode.isEmpty() || !linkLane.equals("")) {
                                String tempMode = feature.getAttribute(networkBlock.getLinkMode()).toString();
                                if (!linkMode.equals("") || !linkMode.equalsIgnoreCase("null") || !tempMode.isEmpty()) {
                                    String modes[] = tempMode.split(",");
                                    for (int k = 0; k < modes.length; k++) {
                                        allowedModes.add(modes[k]);
                                    }
                                } else {
                                    allowedModes.add("car");
                                }
                            }


                            Id<Node> fromNodeKey = Id.createNodeId(format5(fromX) + "_" + format5(fromY));
                            Id<Node> toNodeKey = Id.createNodeId(format5(toX) + "_" + format5(toY));
                            Id<Link> fromToLinkKey = Id.createLinkId(format5(fromX) + "_" + format5(fromY) + ">>" + format5(toX) + "_" + format5(toY));
                            Id<Link> toFromLinkKey = Id.createLinkId(format5(toX) + "_" + format5(toY) + ">>" + format5(fromX) + "_" + format5(fromY));

                            if (!network.getNodes().containsKey(fromNodeKey)) {
                                Coord tempCoord = new Coord(fromX, fromY);
                                NetworkUtils.createAndAddNode(network, fromNodeKey, ct.transform(tempCoord));
                            }
                            if (!network.getNodes().containsKey(toNodeKey)) {
                                Coord tempCoord = new Coord(fromX, fromY);
                                NetworkUtils.createAndAddNode(network, toNodeKey, ct.transform(tempCoord));
                            }
                            // set up link attribute if specified
                            // direction
                            String linkDirField = feature.getAttribute(linkDir).toString();
                            if ((linkDirField.equalsIgnoreCase("null") || linkDirField.isEmpty() || linkDirField.equals("")) || Double.parseDouble(linkDirField) == 0) {
                                if (!network.getLinks().containsKey(fromToLinkKey)) {
                                    NetworkUtils.createAndAddLink(network, fromToLinkKey,
                                            network.getNodes().get(fromNodeKey), network.getNodes().get(toNodeKey),
                                            length, speed, capacity, lanes);
                                    network.getLinks().get(fromToLinkKey).setAllowedModes(allowedModes);
                                }
                                if (!network.getLinks().containsKey(toFromLinkKey)) {
                                    NetworkUtils.createAndAddLink(network, toFromLinkKey,
                                            network.getNodes().get(toNodeKey), network.getNodes().get(fromNodeKey),
                                            length, speed, capacity, lanes);
                                    network.getLinks().get(toFromLinkKey).setAllowedModes(allowedModes);

                                }

                            } else if (!(linkDirField.equalsIgnoreCase("null") || linkDirField.isEmpty() || linkDirField.equals("")) && Double.parseDouble(linkDirField) > 0) { // dirction is from start to end node
                                if (!network.getLinks().containsKey(fromToLinkKey)) {
                                    NetworkUtils.createAndAddLink(network, fromToLinkKey,
                                            network.getNodes().get(fromNodeKey), network.getNodes().get(toNodeKey),
                                            length, speed, capacity, lanes);
                                    log.info(allowedModes.toString() + ">><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");
                                    network.getLinks().get(fromToLinkKey).setAllowedModes(allowedModes);

                                }
                            } else if (!(linkDirField.equalsIgnoreCase("null") || linkDirField.isEmpty()) && Double.parseDouble(linkDirField) < 0) { // dirction is from end to start node
                                if (!network.getLinks().containsKey(toFromLinkKey)) {
                                    NetworkUtils.createAndAddLink(network, toFromLinkKey,
                                            network.getNodes().get(toNodeKey), network.getNodes().get(fromNodeKey),
                                            length, speed, capacity, lanes);
                                    network.getLinks().get(toFromLinkKey).setAllowedModes(allowedModes);

                                }
                            }
//                            iPoint++;
                        }
                    }
                } else {
                    //TODO
                }
//                System.out.println(iPoint+","+nodeIds.size());
//                System.out.println(feature.getAttribute("NAME"));
//                System.out.println(feature.getAttribute("AB_FF_TIME"));
//                System.out.println(feature.getAttribute("the_geom"));
            }
//            iterator.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(network.getLinks().size());
        new NetworkCleaner().run(network);
//        System.out.println(network.getLinks().size());
//
//        Links2ESRIShape links2ESRIShape = new Links2ESRIShape(network,"/Users/convel/Desktop/testMew.shp","EPSG:4326");
//        links2ESRIShape.write();
        new NetworkWriter
                (network).write(shpFile.replace(".shp", ".xml"));
        result.setSuccess(true);
        result.setData(shpFile.replace(".shp", ".xml"));
        return result;
    }

    public static void readShpFile(String file) {
        ShapefileDataStore shpDataStore = null;
        try {
            shpDataStore = new ShapefileDataStore(new File(file).toURI().toURL());
            shpDataStore.setCharset(Charset.forName("GBK"));
            String typeName = shpDataStore.getTypeNames()[0];
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;
            featureSource = shpDataStore.getFeatureSource(typeName);
            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures();
//            System.out.println(result.size());
            FeatureIterator<SimpleFeature> itertor = result.features();

            while (itertor.hasNext()) {
                SimpleFeature feature = itertor.next();

                Collection<Property> p = feature.getProperties();
                Iterator<Property> it = p.iterator();
//                while(it.hasNext()) {
//
//                    Property pro = it.next();
//                    System.out.println(pro.getName());
////                    System.out.println(pro.getValue());
//                    if (pro.getValue() instanceof Point) {
//                        System.out.println("PointX = " + ((Point)(pro.getValue())).getX());
//                        System.out.println("PointY = " + ((Point)(pro.getValue())).getY());
//                    } else if (pro.getValue() instanceof LineString) {
//                        System.out.println("LineString = " + ((LineString)(pro.getValue())).toText());
////                        System.out.println("PointY = " + ((Point)(pro.getValue())).getY());
//                    }else if (pro.getValue() instanceof MultiLineString){
//                        System.out.println(pro.getValue());
//                    }
//                }
            }
            itertor.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String format5(double value) {
//        double d = Double.parseDouble(value);
        return String.format("%.5f", value).toString();
    }

    /**
     * 根据两个位置的经纬度，来计算两地的距离（单位为KM）
     * 参数为String类型
     *
     * @return
     */
    public static double getDistance(double x1, double y1, double x2, double y2) {

        double radLat1 = rad(x1);
        double radLat2 = rad(x2);
        double difference = radLat1 - radLat2;
        double mdifference = rad(y1) - rad(y2);
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(mdifference / 2), 2)));
        distance = distance * EARTH_RADIUS;
//        distance = Math.round(distance * 10000) / 10000;
//        String distancng(0, distanceStr.indexOf("."));

        return distance;
    }

    private static double EARTH_RADIUS = 6378137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


}

