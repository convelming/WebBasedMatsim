package com.matsim.bean;

/**
 * Created by MingLU on 2018/9/28,
 * "geometry": {
     "type": "Point",
     "coordinates": [125.6, 10.1]
     },
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class GeoJsonGeometry {
    private String type;
    private String coordinates; // there might be multi arrays
    private String geometries;
//    { "type": "Point", "coordinates": [100.0, 0.0] }
//    { "type": "LineString", "coordinates": [ [100.0, 0.0], [101.0, 1.0] ]    }
//
//    { "type": "Polygon", "coordinates": [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ] ]    }// no holes
//        // polygon with holes
//    { "type": "Polygon",  "coordinates": [    [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ],    [ [100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2] ]    ] }
//    { "type": "MultiPoint", "coordinates": [ [100.0, 0.0], [101.0, 1.0] ]    }
//    { "type": "MultiLineString", "coordinates": [ [ [100.0, 0.0], [101.0, 1.0] ],  [ [102.0, 2.0], [103.0, 3.0] ]   }
//
//    { "type": "MultiPolygon",  "coordinates": [    [[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]]],    [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]],     [[100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2]]]    ]  }
//
//    { "type": "GeometryCollection",  "geometries": [    { "type": "Point",      "coordinates": [100.0, 0.0]      },    { "type": "LineString",      "coordinates": [ [101.0, 0.0], [102.0, 1.0] ]      }  ]}


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getGeometries() {
        return geometries;
    }

    public void setGeometries(String geometries) {
        this.geometries = geometries;
    }

    @Override
    public String toString() {
        if (!type.equals( "GeometryCollection" )) {
            return "{\"type\":\"" + type + "\"" +
                    ", \"coordinates\":" + coordinates +
//                ", \"geometries\"" + geometries +
                    '}';
        }else{
            return "{\"type\":\"GeometryCollection\"" +
//                    ", \"coordinates\":" + coordinates +
                ", \"geometries\"" + geometries +
                    '}';
        }
    }
}
