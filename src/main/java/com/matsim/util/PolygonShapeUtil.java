package com.matsim.util;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.Filter;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.matsim.api.core.v01.Coord;


import java.awt.Polygon;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;


/**
 * Created by MingLU on 2018/4/18,
 * Life is short, so get your fat ass moving and chase your damn dream.
 *
 * This class transfer polygon shape file into polygons, generate random points in polygons
 * Polygons are as traffic zones normally as administration districts.
 * NOTE: at the moment there are no match in different coord system, meaning compare points in polygons with different
 * coord is pointless.
 */
public class PolygonShapeUtil {

    public Map<String, List<List<Polygon>>> regions;
    private final static int scaleFactor = 1000000;

    public static void main(String[] args) throws Exception{
//        String file = "/Users/convel/Desktop/testMp.shp";
        String file1 = "/Users/convel/Documents/GIS数据/深圳市地图/深圳行政区域细分/行政区.shp";
//        String file2 = "/Users/convel/Documents/GIS数据/深圳市地图/面状水系.shp";
        PolygonShapeUtil psu = new PolygonShapeUtil(file1,"NAME");

//        System.out.println(psu.regions.size());
        for (Map.Entry district:psu.regions.entrySet()) {
            Coord[] tempRanCoordsInDis = psu.genCoordsInPolygon(
                    2000,(List<List<Polygon>>)district.getValue());
//            System.out.println( district.getKey());
            for (Coord coord:tempRanCoordsInDis) {
                System.out.println(district.getKey()+", "+coord.getX()+", "+coord.getY());
            }
        }
//        PolygonShapeUtil psu = new PolygonShapeUtil( "/Users/convel/Desktop/newTest/a/regionTest/shpFile/region/行政区.shp","NAME");
//        Coord homeCoord = psu.genCoordFromShapFile( 1,
//               "福田区")[0];

    }

    /**
     *
     * @param shpFile
     * @param regionIdField this indicates the field column in the dbf file
     * @throws Exception
     */
    public PolygonShapeUtil(String shpFile,String regionIdField) throws Exception{
        this.regions = shp2awtPolygon(shpFile,regionIdField );
    }

    public Coord[] genCoordFromShapFile(int coordNum, String targetRegionId) {

        return this.genCoordsInPolygon(
                coordNum,regions.get( targetRegionId ) );

    }

    /**
     * this method parses polygon shp-file and tranfers into polygons
     * @param shpFile
     * @return
     */
    public Map<String, List<List<Polygon>>> shp2awtPolygon(String shpFile,String regionId) throws Exception{

        Map<String, List<List<Polygon>>> polygons = new HashMap<>();

        File file = new File(shpFile);
        Map<String, Object> map = new HashMap<>();
        map.put("url", file.toURI().toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        ((ShapefileDataStore) dataStore).setCharset(Charset.forName("GBK"));
        FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore
                .getFeatureSource(dataStore.getTypeNames()[0]);
        Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")

        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
//                System.out.println(feature.getAttribute(regionId).toString());
//                System.out.print(": ");
//                System.out.println(feature.getAttribute("the_geom"));
                polygons.put(feature.getAttribute(regionId).toString(),wktPolygon2AwtPolygon(feature.getAttribute("the_geom").toString()));

            }
        }
        return polygons;
    }



    /**
     * This method generate specified number of Coords in multipolygon
     * @param numOfCoords number of coords
     * @param polygonses
     * @return
     */
    public Coord[] genCoordsInPolygon(int numOfCoords,List<List<Polygon>> polygonses){

        Coord[] coords = new Coord[numOfCoords];
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        // get bounding box of each polygon;
         for (List<Polygon> polygons:polygonses) {
            for (Polygon polygon : polygons) {
                for(int i = 0;i<polygon.xpoints.length;i++){
//                    System.out.println(polygon.xpoints[i]+","+polygon.ypoints[i]);
                    if (minX>polygon.xpoints[i]) minX = polygon.xpoints[i];
                    if (maxX<polygon.xpoints[i]) maxX = polygon.xpoints[i];

                    if (minY>polygon.ypoints[i]) minY = polygon.ypoints[i];
                    if (maxY<polygon.ypoints[i]) maxY = polygon.ypoints[i];
                }
            }
        }

        for (int i = 0; i < numOfCoords; i++) {
//            System.out.println(maxX+","+minX);
            int x,y;
            x = minX + new Random().nextInt(maxX-minX);
            y = minY + new Random().nextInt(maxY-minY);
//            System.out.println(x+","+y);
             while (!pointInPolygon(x,y,polygonses)){
//                 System.out.println("random point is not in polygons");
                 x = minX + new Random().nextInt(maxX-minX);
                 y = minY + new Random().nextInt(maxY-minY);
             }
             coords[i] =  new Coord((double)x/scaleFactor,(double)y/scaleFactor);

        }
//        System.out.println("line 145!");
        return coords;
    }



    /**
     *  This method uses inner method of java.awt.polygon to find out if the point in the polygon
     *  as some polygons have hollow polygons inside, this method finds out if the point is in the hollow polygons
     *  and no overlapped polygons are considered.
     * @param x point x
     * @param y point y
     * @param polygonses in the first layer of the list are several independent polygons, this method finds out if the point
     *                is inside any of these polygons,but not in the hollow polygons inside the first polygon
     *                in the second list layer
     *
     * @return
     */
    public boolean pointInPolygon(double x,double y,List<List<Polygon>> polygonses){

        boolean inPolygon = false;
        for (List<Polygon> polygons:polygonses) {
            if (polygons.size() == 1){
                // as long as point in one of the polygons but not in the hollow polygons
                inPolygon = inPolygon || polygons.get(0).contains(x,y);

            }else if(polygons.size() > 1){
                boolean inFirstPoly = polygons.get(0).contains(x,y);
//                System.out.println("line127 "+inFirstPoly);
                boolean inOtherPoly = false;
                for (int i=1;i<polygons.size();i++){
                    //
                    inOtherPoly = inOtherPoly || polygons.get(i).contains(x,y);
//                    System.out.println("line132 "+inOtherPoly);
                }
                inPolygon = inPolygon || (inFirstPoly&&!inOtherPoly);
            }
        }
        return inPolygon;
    }

    /**
     * this method transfer wkt polygon or multi-polygon into list of java.awt polygon
     * for a single polygon ,the first one in the list is the exterior polygon while others is hollow polygons inside
     * the first one, see more details at
     * https://www.cnblogs.com/marsprj/archive/2013/02/08/2909452.html
     * @param polygonStr
     * @return
     */
    public List<List<Polygon>> wktPolygon2AwtPolygon(String polygonStr){
        List<List<Polygon>> polygons = new ArrayList<>();
        //wkt polygon have polygon((x y, x1 y2,...),...,(x3 y3, x4 y4,...)) and multipolygon((polygon1),(polygon2)) where
//         polygon1 and polygon2 have same format as polygon((),...())

        //MULTIPOLYGON(((0 0,12 0,12 12,0 12,0 0),(4 4,8 4,8 8,4 8,4 4)),((10 10,112 10,112 112,10 112,10 10),(14 14,18 14,18 18,14 18,14 14)))
        if(polygonStr.toLowerCase().startsWith("multipolygon")){
            String [] tempPolygons = polygonStr.toLowerCase().replace("multipolygon (((","")
                    .replace(")))","").split("\\)\\), \\(\\(");
            for (int i = 0; i < tempPolygons.length; i++) {
                List<Polygon> temp = new ArrayList<>();
//                System.out.println(tempPolygons[i]);
                String [] tempPolygon = tempPolygons[i].split("\\), \\(");
                for (int j = 0; j < tempPolygon.length; j++) {
//                    System.out.print(tempPolygon[j]+" ===== ");
                    Polygon testP = str2Polygon(tempPolygon[j]);
                    temp.add(str2Polygon(tempPolygon[j]));
                }
//                System.out.println();
                polygons.add(temp);
            }
//            System.out.println();
//            MULTIPOLYGON (((
        }else if(polygonStr.toLowerCase().startsWith("polygon")){
            String[] temp = polygonStr.toLowerCase().replace("\\)\\)","")
                    .replace("polygon ((","").split("\\), \\(");
            List<Polygon> tempPolygon = new ArrayList<>();
            for (String t:temp) {
                tempPolygon.add(str2Polygon(t));
            }
            polygons.add(tempPolygon);
        }
        return polygons;
    }

    /**
     * this method transfer formatted string to awt.polygon
     * NOTE!!!!: points in awt.polygon are Integer, so all points are enlarged for 1000000 times to keep precision
     * @param polyStr as "x1 y1,x2 y2,...,xn yn"
     * @return
     */
    public Polygon str2Polygon(String polyStr){
        Polygon polygon;
        String[] points = polyStr.split(", ");
        int[] x = new int[points.length-1];
        int[] y = new int[points.length-1];

        for (int i = 0; i < points.length-1; i++) {
//            System.out.println(points[i]);
            String[] tmpPoint = points[i].split(" ");

            x[i] = (int)(Double.parseDouble(tmpPoint[0])*scaleFactor);
            y[i] = (int)(Double.parseDouble(tmpPoint[1])*scaleFactor);

        }
        polygon = new Polygon(x,y,points.length-1);
        return polygon;
    }
}
