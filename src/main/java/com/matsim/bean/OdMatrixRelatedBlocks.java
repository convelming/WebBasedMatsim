package com.matsim.bean;

import com.matsim.bean.Block;

import java.util.List;

/**
 * Created by MingLU on 2018/4/15,
 * Life is short, so get your fat ass moving and chase your damn dream.
 *
 * This class defines block list can be linked to od matrix blocks, at the moment these blocks are:
 * mode, person, region, timer, tripPurpose.
 *
 *
 */
public class OdMatrixRelatedBlocks {

    private List<Block> odMatrixRelatedBlocksList;

    public List<Block> getOdMatrixRelatedBlocksList() {
        return odMatrixRelatedBlocksList;
    }

    public void setOdMatrixRelatedBlocksList(List<Block> odMatrixRelatedBlocksList) {
        this.odMatrixRelatedBlocksList = odMatrixRelatedBlocksList;
    }

    @Override
    public String toString() {
        return "OdMatrixRelatedBlocks{" +
                "odMatrixRelatedBlocksList=" + odMatrixRelatedBlocksList +
                '}';
    }
}
