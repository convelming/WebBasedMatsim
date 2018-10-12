package com.vis.svg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by MingLU on 2018/9/10,
 * the requested json format is:
 * {"name":"M532","value":[[113.953598,22.764132],[113.953514,22.764362],...,[]]}
 * this is the bus line crawled in busLineCrawlingSz.html
 *  It transformated into path :
 *   id="world" d="M47
 */
public class Json2SvgPath {

    static double maxX = Double.MIN_VALUE;
    static double maxY = Double.MIN_VALUE;
    static double minX = Double.MAX_VALUE;
    static double minY = Double.MAX_VALUE;

    public static BusLine json2BusLine(String jsonFile) throws Exception{
        BufferedReader br = new BufferedReader( new FileReader( jsonFile ) );
        String svgPath ="";
        String tempLine;
        String jsonStr ="";
        while((tempLine = br.readLine())!=null){
            jsonStr += tempLine;
        }
        jsonStr = jsonStr.substring( 0,jsonStr.length()-1 );
        System.out.println(jsonStr);
        JSONObject jo = JSON.parseObject( jsonStr );
        String name = jo.getString( "name" );
        JSONArray ja = jo.getJSONArray( "value" );
        Double points[][] = new Double[ja.size()][];
//        double maxX = Double.MIN_VALUE;
//        double maxY = Double.MIN_VALUE;
//        double minX = Double.MAX_VALUE;
//        double minY = Double.MAX_VALUE;
        for (int i = 0; i < ja.size(); i++) {

            Double point [] = new Double[2];
            point[0] = 10000*Double.parseDouble( ja.getJSONArray( i ).get( 0 ).toString() );
            point[1] = 10000*Double.parseDouble( ja.getJSONArray( i ).get( 1 ).toString() );
            if(maxX<point[0]) maxX = point[0];
            if(maxY<point[1]) maxY = point[1];
            if(minX>point[0]) minX = point[0];
            if(minY>point[1]) minY = point[1];

            points[i] =point;

        }
        BusLine bl = new BusLine();
        bl.setName( name );
        bl.setLine( points );
//        System.out.println(minX+","+maxX+";"+minY+","+maxY);
//        System.out.println((maxX-minX) +";"+(maxY-minY));
        br.close();
        return bl;
    }
    public static String busLine2SvgPath(BusLine busLine){
        String path = "<path id=\""+busLine.getName()+"\" d=\"" ;
        Double[][] points = busLine.getLine();
//        System.out.println(points.length);
        for (int i = 0; i < points.length; i++) {
            if(i==0){
                path += "M"+points[i][0] +","+points[i][1]+" ";
//                path += "M"+(1139393.84+1139969.94)/2 +","+(227294.97999999998+227649.96)/2+" ";

            }else {
                path += "L"+points[i][0] +","+points[i][1]+" ";
//                path += "l"+(points[i][0]-(1139393.84+1139969.94)/2) +","+(points[i][1]-(227294.97999999998+227649.96)/2)+" ";

            }
        }
        path += "M"+points[points.length-1][0] +","+points[points.length-1][1]+"\" stroke=\"black\" fill=\"transparent\" stroke-width=\"1\"/>";
        System.out.println(path);
        return path;
    }
    public static void main(String[] args) throws Exception {
        BusLine bl1 = json2BusLine( "/Users/convel/Downloads/M532Line.json" );
        BusLine bl2 = json2BusLine( "/Users/convel/Downloads/B965Line.json" );
        BusLine bl3 = json2BusLine( "/Users/convel/Downloads/B963Line.json" );
        BusLine bl4 = json2BusLine( "/Users/convel/Downloads/B960Line.json" );
        BusLine bl5 = json2BusLine( "/Users/convel/Downloads/B959Line.json" );
        BusLine bl6 = json2BusLine( "/Users/convel/Downloads/B949Line.json" );
        busLine2SvgPath(bl1);
        busLine2SvgPath(bl2);
        busLine2SvgPath(bl3);
        busLine2SvgPath(bl4);
        busLine2SvgPath(bl5);
        busLine2SvgPath(bl6);
        System.out.println(minX+","+maxX+";"+minY+","+maxY);
        System.out.println((maxX-minX) +";"+(maxY-minY));
    }


}
