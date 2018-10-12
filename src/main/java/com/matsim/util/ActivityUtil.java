package com.matsim.util;

import com.matsim.bean.Block;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by MingLU on 2018/7/10,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class ActivityUtil {


        private Set<String> activities;

        public ActivityUtil(Block[] blocks) {

            activities = new HashSet<>();

            for (Block block : blocks
                    ) {
                if (block.getType().equalsIgnoreCase("tripPurpose")) {
                    if (block.getMode().equalsIgnoreCase("userDefined")){
                        activities.add(block.getOtherMode());
                    }else{
                        activities.add(block.getMode());
                    }
                }
            }
        }

    public Set<String> getActivities() {
        return activities;
    }

    public void setActivities(Set<String> activities) {
        this.activities = activities;
    }

    public static String[] set2Array(Set<String> set){
            String array[] = new String[set.size()];
            int i = 0;
            for (String s:set
                    ) {
                array[i] = s;
                i++;
            }
            return array;
        }


}
