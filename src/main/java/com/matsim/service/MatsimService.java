package com.matsim.service;

import com.matsim.bean.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MingLU on 2018/4/14,
 * Life is short, so get your fat ass moving and chase your damn dream.
 *
 *  This class deals with the function of blocks linked to the matsim block;
 *  there are two types of activities needed to be translated into activity plans:
 *  od matrix and person activity chain, both may have multiples in a work space.
 *  For od Matrix, the activity plan will be generated with other linked blocks tracked backwards,
 *  while for person activity chain, it will be parsed start from the activity block.
 *
 */
public class MatsimService {

    public  List<List<Edge>> matsimEdges;// all edges in the workspace
    public  List<OdMatrixRelatedBlocks> odMatrixRelatedBlocks;
    public  List<ActivityRelatedBlocks> activityRelatedBlocks;
    public  WorkSpace workSpace;
    public MatsimService(WorkSpaceTopology workSpaceTopology, String userName, String saveName){

//        this.matsimEdges = workSpaceTopology.getMatsimEdges();
        this.workSpace = workSpaceTopology.workSpace;
        getActivityRelatedBlocks(matsimEdges);
        System.out.println(activityRelatedBlocks.size());
        getOdMatrixRelatedBlocks(matsimEdges);



    }

    private void getOdMatrixRelatedBlocks(List<List<Edge>> matsimEdges){
        for (List<Edge> tempEdgeList:matsimEdges
             ) {
           if(hasOdMatrix(tempEdgeList)){
               OdMatrixRelatedBlocks odMatrixRelatedBlocks = new OdMatrixRelatedBlocks();
               List<Block> tempBlocks = new ArrayList<>();
                for(Edge tempEdge:tempEdgeList){
                    if (tempEdge.getFromBlockId()!=null){
                        tempBlocks.add(workSpace.getBlockById(tempEdge.getFromBlockId()));
                    }
                }
               odMatrixRelatedBlocks.setOdMatrixRelatedBlocksList(tempBlocks);
           }
        }
    }
    private void getActivityRelatedBlocks(List<List<Edge>> matsimEdges){
        for (List<Edge> tempEdgeList:matsimEdges
                ) {
            if(hasOdMatrix(tempEdgeList)){
                ActivityRelatedBlocks activityRelatedBlocks = new ActivityRelatedBlocks();
                List<Block> tempBlocks = new ArrayList<>();
                for(Edge tempEdge:tempEdgeList){
                    if (tempEdge.getFromBlockId()!=null){
                        tempBlocks.add(workSpace.getBlockById(tempEdge.getFromBlockId()));
                    }
                }
                activityRelatedBlocks.setActivityRelatedBlocks(tempBlocks);
            }
        }
    }

    private boolean hasOdMatrix(List<Edge> edges){
        boolean hasOdMatrix = false;
        for (Edge edge:edges
             ) {
            if(edge.getFromBlockType().equalsIgnoreCase("odmatrix")||
                    edge.getToBlockType().equalsIgnoreCase("odmatrix")){
                hasOdMatrix =  true;
            }
        }
        return hasOdMatrix;
    }
    private boolean hasActivity(List<Edge> edges){
        boolean hasActivity = false;
        for (Edge edge:edges
                ) {
            if(edge.getFromBlockType().equalsIgnoreCase("activity")||
                    edge.getToBlockType().equalsIgnoreCase("activity")){
                hasActivity =  true;
            }
        }
        return hasActivity;
    }

}
