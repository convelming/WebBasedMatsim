package com.matsim.util;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class OpenstreetMapUtil {


    public static void main(String[] args) {
        //方法一getMap(120.0964,36.1423,121.1744,36.5852,"d:")
        //System.out.println((new Test()).getURLContent());
        OpenstreetMapUtil open= new OpenstreetMapUtil();
        String url = open.getMap(113.7126,22.4334,114.6459,22.8886,"/users/convel/desktop");//sz
//        String url = open.getMap(120.4964,36.0423,121.1744,36.5852,"/users/convel/desktop");//jimo
        System.out.println(url);
        String xml =  open.osm2matsimXml(url);
        System.out.println(xml);
    }

    /**
     * 下载网络文件
     */
    public String getMap(double minLong,double minLati,double maxLong,double maxLati,String savePath){

        URL url = null;
        InputStream in = null;
        FileOutputStream os = null;
        try {
            url = new URL("https://overpass-api.de/api/map?bbox="+
                            minLong+","+minLati+","+maxLong+","+maxLati);//120.0964,36.1423,121.1744,36.5852";
            in = url.openStream();
            //定义输出的路径
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();//创建多重目录
            }
            os = new FileOutputStream(saveDir+"/"+"temp"+minLong+"_"+minLati+"_"+maxLong+"_"+maxLati+".osm");

            //创建缓冲区
            byte buffer[] = new byte[10240];
            int len = 0;
            // 循环将输入流中的内容读取到缓冲区当中
            while ((len = in.read(buffer)) > 0) {
//                System.out.println("===len:"+len);
                os.write(buffer, 0, len);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return savePath+"/"+"temp"+minLong+"_"+minLati+"_"+maxLong+"_"+maxLati+".osm";
    }


    public String  osm2matsimXml(String osmFile) {
        //      String osm = "/Users/convel/Desktop/深圳.osm";

        Config config = ConfigUtils.createConfig();
        Scenario sc = ScenarioUtils.createScenario(config);
        Network net = sc.getNetwork();
        CoordinateTransformation ct =  TransformationFactory.getCoordinateTransformation(
                TransformationFactory.WGS84, "EPSG:4326");//utm 49n 32649
//        OsmNetworkReader onr = new OsmNetworkReader(net,ct,true);

        OsmNetworkReader onr = new OsmNetworkReader(net,ct,false);
        onr.setHighwayDefaults(1, "motorway",      2, 120.0/3.6, 1.0, 2000, true);
        onr.setHighwayDefaults(1, "motorway_link", 1,  80.0/3.6, 1.0, 1500, true);
        onr.setHighwayDefaults(2, "trunk",         1,  80.0/3.6, 1.0, 2000);
        onr.setHighwayDefaults(2, "trunk_link",    1,  50.0/3.6, 1.0, 1500);
        onr.setHighwayDefaults(3, "primary",       1,  80.0/3.6, 1.0, 1500);
        onr.setHighwayDefaults(3, "primary_link",  1,  60.0/3.6, 1.0, 1500);
        onr.setHighwayDefaults(4, "secondary",     1,  60.0/3.6, 1.0, 1000);
        onr.setHighwayDefaults(5, "tertiary",      1,  45.0/3.6, 1.0,  600);
        onr.setHighwayDefaults(6, "minor",         1,  45.0/3.6, 1.0,  600);
        onr.setHighwayDefaults(6, "unclassified",  1,  45.0/3.6, 1.0,  600);
        onr.setHighwayDefaults(6, "residential",   1,  30.0/3.6, 1.0,  600);
        onr.setHighwayDefaults(6, "living_street", 1,  15.0/3.6, 1.0,  300);
        onr.setHighwayDefaults(4, "secondary_link",     1,  60.0/3.6, 1.0, 1000);
        onr.setHighwayDefaults(5, "tertiary_link",      1,  45.0/3.6, 1.0,  600);
        onr.setHighwayDefaults(6, "road",         1,  45.0/3.6, 1.0,  600);
        onr.setHighwayDefaults(6, "service",         1,  45.0/3.6, 1.0,  600);
        onr.parse(osmFile);
        new NetworkCleaner().run(net);
        new NetworkWriter(net).write(osmFile.replace("osm","xml"));

        return osmFile.replace("osm","xml");
    }
}