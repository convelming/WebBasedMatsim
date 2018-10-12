package com.matsim.util;

import com.matsim.bean.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by MingLU on 2018/4/27,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class ModeUtil {

    private Set<String> modes;

    public ModeUtil(Block[] blocks) {

        modes = new HashSet<>();

        for (Block block : blocks
                ) {
            if (block.getType().equalsIgnoreCase("mode")) {
                if (block.getMode().equalsIgnoreCase("userDefined")){
                    modes.add(block.getOtherMode());
                }else{
                    modes.add(block.getMode());
                }
            }
        }
    }

    public Set<String> getModes() {
        return modes;
    }

    public void setModes(Set<String> modes) {
        this.modes = modes;
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

    public static void main(String[] args) {
        HashSet<String> set1 = new HashSet<>(  );
        HashSet<String> set2 = new HashSet<>(  );
        HashSet<String> set3 = new HashSet<>(  );
        set2.add( "convel" );
        set2.add( "ming" );
        set2.add( "lu" );
        set2.add( "MingLu" );
        set2.add( "lu" );
        set2.add( "convel" );

        set3.add( "Lu" );
        set3.add( "ming" );
        set3.add( "lu" );
        set3.add( "MingLu" );
        set3.add( "lu" );
        set3.add( "Convel" );

        set1.add( "convel" );
        set1.addAll( set2 );
        set1.addAll( set3);

        for (String s:set1
             ) {
            System.out.println(s);
        }
    }
}
