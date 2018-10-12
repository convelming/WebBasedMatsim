package com.matsim.bean;

/**
 * Created by MingLU on 2018/9/28,
 * typical GeoJson format:
 *
 *  {
        "type": "Feature",
        "geometry": {
             "type": "Point",
             "coordinates": [125.6, 10.1]
         },
         "properties": {
            "name": "Dinagat Islands"
         }
     }
 *
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class GeoJsonFeature {
    private String type = "Feature";
    private GeoJsonGeometry geometry;
    private GeoJsonProperties properties;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GeoJsonGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoJsonGeometry geometry) {
        this.geometry = geometry;
    }

    public GeoJsonProperties getProperties() {
        return properties;
    }

    public void setProperties(GeoJsonProperties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "{\"type\":\"" + type + "\""+
                ", \"geometry\":" + geometry.toString() +
                ", \"properties\":" + properties.toString() +
                '}';
    }
}
