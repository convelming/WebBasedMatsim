package com.matsim.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by MingLu on 2018/4/13.
 * This class gets the workspace data and parse connects into edges and picks up the matsim sub-block and edges
 */
public class WorkSpaceTopology implements Serializable {

    public List<Edge> edges;
    public WorkSpace workSpace;
    public WorkSpaceTopology(WorkSpace workSpace) {
        this.workSpace = workSpace;

    }
    public void getEdges(){
        Block[] blocks = workSpace.getBlocks();
        List<Edge> edges = new ArrayList<>();
        Set<String> edgeIds = new HashSet<>();

        for (int i = 0; i < blocks.length; i++) {
            String blockId = blocks[i].getId();
            String blockType = blocks[i].getType();
            Connection[] connects = blocks[i].getConnects();
            int iEdge = 0;
            if (connects != null) {
                for (int j = 0; j < connects.length; j++) {

                    if (blocks[i].getConnects()[j].getFrom() != null && blocks[i].getConnects()[j].getTo() == null) { // from
                        String tempFromEdgeId = blocks[i].getConnects()[j].getFrom()+">>"+blocks[i].getId();
                        if (!edgeIds.contains(tempFromEdgeId)){
                            edgeIds.add(tempFromEdgeId);
                            Edge tempEdge = new Edge(blocks[i].getId() + "_" + iEdge);
                            tempEdge.setFromBlockId(blocks[i].getConnects()[j].getFrom());
                            tempEdge.setFromBlockType(workSpace.getBlockTypeById(blocks[i].getConnects()[j].getFrom()));
                            tempEdge.setToBlockId(blockId);
                            tempEdge.setToBlockType(blockType);
                            iEdge += 1;
                            edges.add(tempEdge);
                        }
                    } else if (blocks[i].getConnects()[j].getFrom() == null && blocks[i].getConnects()[j].getTo() != null) {
                        String tempToEdgeId = blocks[i].getId()+">>"+blocks[i].getConnects()[j].getTo();
                        if (!edgeIds.contains(tempToEdgeId)){
                            edgeIds.add(tempToEdgeId);
                            Edge tempEdge = new Edge(blocks[i].getId() + "_" + iEdge);
                            tempEdge.setToBlockId(blocks[i].getConnects()[j].getTo());
                            tempEdge.setToBlockType(workSpace.getBlockTypeById(blocks[i].getConnects()[j].getTo()));
                            tempEdge.setFromBlockId(blockId);
                            tempEdge.setFromBlockType(blockType);
                            iEdge += 1;
                            edges.add(tempEdge);
                        }
                    } else if (blocks[i].getConnects()[j].getFrom() != null && blocks[i].getConnects()[j].getTo() != null) {
                        String tempFromEdgeId = blocks[i].getConnects()[j].getFrom()+">>"+blocks[i].getId();
                        if (!edgeIds.contains(tempFromEdgeId)){
                            edgeIds.add(tempFromEdgeId);
                            Edge tempEdge = new Edge(blocks[i].getId() + "_" + iEdge);
                            tempEdge.setFromBlockId(blocks[i].getConnects()[j].getFrom());
                            tempEdge.setFromBlockType(workSpace.getBlockTypeById(blocks[i].getConnects()[j].getFrom()));
                            tempEdge.setToBlockId(blockId);
                            tempEdge.setToBlockType(blockType);
                            iEdge += 1;
                            edges.add(tempEdge);
                        }
                        String tempToEdgeId = blocks[i].getId()+">>"+blocks[i].getConnects()[j].getTo();
                        if (!edgeIds.contains(tempToEdgeId)){
                            edgeIds.add(tempToEdgeId);
                            Edge tempEdge = new Edge(blocks[i].getId() + "_" + iEdge);
                            tempEdge.setToBlockId(blocks[i].getConnects()[j].getTo());
                            tempEdge.setToBlockType(workSpace.getBlockTypeById(blocks[i].getConnects()[j].getTo()));
                            tempEdge.setFromBlockId(blockId);
                            tempEdge.setFromBlockType(blockType);
                            iEdge += 1;
                            edges.add(tempEdge);
                        }
                    }
                }
            }
        }
        for (Edge e:edges
                ) {
            System.out.println(e.toString());
        }
        this.edges = edges;

    }


    /**
     * This method returns from links to the matsim block,
     * there are roughly four types connected to matsim block: networks, regions, odMatrix, and activity chains
     * as networks,regions and od
     * this method gets all
     * @return
     */
    public List<Block> getToMatsimBlocks(){
        List<Block> toMatsimBlocks = new ArrayList<>();
        Block matsimBlock = workSpace.getMatsimBlock();
        Connection[] cons = matsimBlock.getConnects();
        for (int i = 0; i < cons.length; i++) {
            if (cons[i].getFrom()!=null) {
//                System.out.println(workSpace.getBlockById(cons[i].getFrom()).toString());
                toMatsimBlocks.add(workSpace.getBlockById(cons[i].getFrom()));
            }
//            System.out.println(""+ cons[i].getFrom().toString());
        }
        return toMatsimBlocks;
    }

    /**
     * this method returns activity chains connected to matsim
     * There could be multiple activity chains; first delete all other blocks allowed to connect to matsim block
     * @return
     */
    public List<List<Block>> getActivitySubBlocks(){
        List<List<Block>> activitySubBlocks = new ArrayList<>();

        // get all block connected to matsim Block and delete other blocks like network, openstreet,region and odmatrix
        for (Block block:getToMatsimBlocks()) {
            if (!((block.getType().equalsIgnoreCase("network"))||
                    (block.getType().equalsIgnoreCase("openstreet"))||
                        (block.getType().equalsIgnoreCase("region"))||
                            (block.getType().equalsIgnoreCase("odmatrix")))){
                activitySubBlocks.add(getFromBlock(block));
            }
        }

        return activitySubBlocks;
    }

    /**
     * this method returns sub-network ending at @param block
     * @param block
     * @return
     */

    public List<Block> getFromBlock(Block block){
        Set<Block> fromBlocks = new HashSet<>();
        Connection[] connections = block.getConnects();
        for (Connection connection:connections) {
            if(connection.getFrom()!=null){
                Block fromBlock = workSpace.getBlockById(connection.getFrom());
                fromBlocks.add(fromBlock);
                fromBlocks.addAll(getFromBlock(fromBlock));
            }
        }
        List<Block> returnedBlocks = new ArrayList<>();
        returnedBlocks.addAll(fromBlocks);
        return returnedBlocks;
    }

}
