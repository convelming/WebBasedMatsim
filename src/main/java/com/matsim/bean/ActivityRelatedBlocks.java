package com.matsim.bean;

import com.matsim.bean.Block;

import java.util.List;

/**
 * Created by MingLU on 2018/4/15,
 * Life is short, so get your fat ass moving and chase your damn dream.
 *
 * This class defines block list can be linked to the activity block, at the moment these blocks are:
 * facility, mode, person, region, timer, tripPurpose.
 *
 *
 */
public class ActivityRelatedBlocks {
    private List<Block> activityRelatedBlocks;

    public List<Block> getActivityRelatedBlocks() {
        return activityRelatedBlocks;
    }

    public void setActivityRelatedBlocks(List<Block> activityRelatedBlocks) {
        this.activityRelatedBlocks = activityRelatedBlocks;
    }

    @Override
    public String toString() {
        return "ActivityRelatedBlocks{" +
                "activityRelatedBlocks=" + activityRelatedBlocks +
                '}';
    }
}
