package com.vis.svg;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by MingLU on 2018/10/9,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class SplitShapeBusLines {

    public static void main(String[] args) throws Exception{
        Set<String> shapeIds0 = getShapeIds( "/Users/convel/Downloads/GTFSzurich/shapes.txt",0);
        Set<String> shapeIds1 = getShapeIds( "/Users/convel/Downloads/GTFSzurich/trips.txt",3);
        splitIntoMultiFiles("/Users/convel/Downloads/GTFSzurich/shapes.txt",
                "/Users/convel/Desktop/testGTFSsplitFile",0);

    }
    public static Set<String> getShapeIds(String file,int index) throws Exception{
        BufferedReader br = new BufferedReader( new FileReader( file ) );
        br.readLine();
        String line;
        Set<String> shapeIds = new HashSet<>();

        while((line=br.readLine())!=null){

            String[] splitted = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            shapeIds.add( splitted[index].replace( "\"","" ) );

        }
//        System.out.println(shapeIds.size());
        br.close();
        return shapeIds;
    }

    public static void splitIntoMultiFiles(String inputFile,String outputFolder,int index ) throws Exception{

        BufferedReader br = new BufferedReader( new FileReader( inputFile ) );
        br.readLine();
        String line;
        Set<String> shapeIds = new HashSet<>();
        if(!new File(outputFolder).exists()) new File(outputFolder).mkdirs();

        while((line=br.readLine())!=null){

            String[] splitted = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            String tempShapeId =splitted[index].replace( "\"","" );
            File tempFile = new File(outputFolder+"/"+tempShapeId+".txt");
            BufferedWriter bw = new BufferedWriter( new FileWriter( tempFile,true ) );
            bw.append( line );
            bw.newLine();
            bw.flush();
            bw.close();
            tempFile.mkdirs();

        }

    }

}
