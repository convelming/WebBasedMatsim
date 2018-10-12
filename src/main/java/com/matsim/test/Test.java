package com.matsim.test;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.matsim.bean.Block;
import com.matsim.bean.Result;
import com.matsim.bean.WorkSpace;

import java.io.File;
import java.util.*;

/**
 * Created by MingLu on 2018/4/13.
 */
public class Test {
    public static void main(String[] args) {

        File file = new File("/users/convel/desktop/whats/the/fuck");
        String testJson ="{\"workspace\":{\"name\":\"aer d\", \"id\":\"null\",\"blocks\":[{\"type\":\"matsimXMLs\",\"id\":\"2\",\"position\":{\"id\":\"\", \"top\":\"51\", \"left\":\"171\", \"zIndex\":\"1\"},\"status\":\"1\",\"connects\":[],\"networkXml\":\"output_network.xml.gz\",\"activityXml\":\"\",\"busScheduleXml\":\"\",\"vehicleXml\":\"\",\"facilityXml\":\"\"}]}}";
        WorkSpace ws = new Test().getEntity( testJson );
        System.out.println();
    }
    public static WorkSpace getEntity(String resp){
        JSONObject jsonObj = (JSONObject) JSON.parse(resp);
        JSONObject wsObj = jsonObj.getJSONObject( "workspace" );
        WorkSpace workSpace = new WorkSpace();
        workSpace.setName( wsObj.getString("name") );
        workSpace.setId(wsObj.getInteger("id"));
        JSONArray blocksJa = wsObj.getJSONArray( "blocks" );
        Block[] blocks = new Block[blocksJa.size()];
        for (int i = 0; i < blocksJa.size(); i++) {
            Block tempBlock = JSONObject.toJavaObject(blocksJa.getJSONObject( i ),Block.class);

            System.out.println(tempBlock.getId());
        }
        workSpace.setBlocks( blocks );
        return workSpace;
    }
}
