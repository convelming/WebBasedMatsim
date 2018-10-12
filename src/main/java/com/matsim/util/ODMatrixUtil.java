package com.matsim.util;

import com.matsim.bean.Destination;
import com.matsim.bean.OdChain;
import com.matsim.bean.Origin;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.matsim.api.core.v01.Coord;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Created by MingLu on 2018/4/17.
 * This class reads from excel or text od files and parse them into OD array
 *
 */
public class ODMatrixUtil {
//    private List<OD> ods;
//    private File file;
    private static Logger log = Logger.getLogger("ODMatrixUtil.class");

    public static void main(String[] args) throws Exception {
//        File file = new File("/Users/convel/Desktop/test/odArrayTest.csv");
//        List<OdChain> odChains = getOdChains( file,"array","asRegion" );
//        System.out.println(odChains.size());

        new ODMatrixUtil().getOriginalDataFromArrayCsv("/Users/convel/Desktop/test/odArrayTest.csv");

    }

    //TODO change exception to try catch syntax

    /**
     * It is annoying that .xls and .xlsx need different class to parse, so this method is quite annoying
     *
     * @param file            support comma separated file but file extension must be .csv,.txt or any original .xls or .xlsx file
     *                        All files have headers.
     * @param type            - choose between 'array' or 'squareMatrix'
     * @param asRegionOrCoord - choose between 'asRegion' or 'asCoord'
     *                        <p>
     *                        So depend on combination of type and regionOrCoord, each data header is as follows
     *                        if it is array the data format should be
     *                        (1)  id(0)|count(1)|origionRegion(2)|originCoordX(3)|originCoordY(4)
     *                        (2)   departureTime1(5)|desiredMode1(6)|tripPurpose1(7)|desFacilityId1(8)|facilityChangable1(9)|desRegionId1(10)|desCoordX1(11)|desCoordY1(12)
     *                        while (1) is followed by (2), if there is multiple destinations,which is often the case, more columns ara followed  as
     *                        the same format of (2). The first destination is the second destination's origin, so on so forth...
     *                        for example if there is a second destination the index will be start from 13-20, index of the third one is 21-28
     *                        the n-th will be (5+n*8)-->(12+n*8)
     *                        <p>
     *                        squareMatrix: can only record asRegion data, firstRow is regionId on each column,
     *                        firttCol is also regionId,
     *                        each cell is the count from regionRow-->regionCol
     *                        count should be normally 1 for array data
     * @throws Exception NOTE: all data must be the right format, or there will be strange error that wont give any warning.
     *                   for comma separated files, make sure there are no chinese comma, it would handle that much dramMa.
     *                   There are some problems in this version will change it in the future maybe.
     */
    public static List<OdChain> getOdChains(File file, String type, String asRegionOrCoord) throws Exception {
        List<OdChain> ods = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        if (file.getName().toLowerCase().endsWith( ".xlsx" )) {
            XSSFWorkbook workbook = new XSSFWorkbook( file );
            XSSFSheet sheet = workbook.getSheetAt( 0 );
            ods = getOdChainsFromXlsx( file, sheet, type, asRegionOrCoord );
            workbook.close();
        } else if (file.getName().toLowerCase().endsWith( ".xls" )) {
            HSSFWorkbook workbook = new HSSFWorkbook( new FileInputStream( file ) );
            HSSFSheet sheet = workbook.getSheetAt( 0 );
            ods = getOdChainsFromXls( file, sheet, type, asRegionOrCoord );
        } else if (file.getName().toLowerCase().endsWith( ".csv" ) || file.getName().toLowerCase().endsWith( ".txt" )) {
            ods = getOdDataFromCsv( file, type, asRegionOrCoord );
        }
        return ods;
    }

    public String[] strList2Array(List<String> list) {
        String[] l = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            l[i] = list.get( i );
        }
        return l;

    }

    public int[] intList2Array(List<Integer> list) {
        int[] l = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            l[i] = list.get( i );
        }
        return l;

    }

    public Coord[] coordList2Array(List<Coord> list) {
        Coord[] l = new Coord[list.size()];
        for (int i = 0; i < list.size(); i++) {
            l[i] = list.get( i );
        }
        return l;

    }

    public static List<OdChain> getOdChainsFromXlsx(File file, XSSFSheet sheet, String type, String asRegionOrCoord) {
        List<OdChain> ods = new ArrayList<>();

        if (type.equalsIgnoreCase( "array" )) {
            //System.out.println(file.getAbsolutePath());


            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header
                // XSSFRow 代表一行数据
                OdChain tempOd = new OdChain();//file.getName().replace(".xlsx","")+"_"+type+"_"+asRegionOrCoord+"_"+i);
                XSSFRow row = sheet.getRow( i );
                String tempId = "";
                if (row.getCell( 0 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                    tempId = row.getCell( 0 ).toString();
                } else if (row.getCell( 0 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                    tempId = "" + (int) row.getCell( 0 ).getNumericCellValue();
                }
                // get count
                int tempCount = (int) row.getCell( 1 ).getNumericCellValue();

                Origin tempOrigin = new Origin();
                // get origin region id
                String oriRegionId = "";
                if (row.getCell( 2 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                    tempOrigin.setOriRegionId( row.getCell( 2 ).toString() );
                } else if (row.getCell( 2 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                    tempOrigin.setOriRegionId( "" + (int) row.getCell( 2 ).getNumericCellValue() );
                }
                double oriCoordX;
                if (row.getCell( 3 ) != null) {
                    tempOrigin.setOriginCoordX( row.getCell( 3 ).getNumericCellValue() );
                }
                double oriCoordY;
                if (row.getCell( 4 ) != null) {
                    tempOrigin.setOriginCoordX( row.getCell( 4 ).getNumericCellValue() );
                }

                int desNum = (row.getLastCellNum() - 5) / 8;
                for (int iCount = 0; iCount < tempCount; iCount++) {
                    OdChain tempOdChain = new OdChain();
                    tempOdChain.setOrigin( tempOrigin );
                    tempOdChain.setId( tempId + "_" + iCount );
                    Destination[] destinations = new Destination[desNum];
                    for (int j = 0; j < desNum; j++) {
                        // departureTime1(5+8*j)|desiredMode1(6+8*j)|tripPurpose1(7+8*j)|desFacilityId1(8+8*j)|
                        // facilityChangable1(9+8*j)|desRegionId1(10+8*j)|desCoordX1(11+8*j)|desCoordY1(12+8*j)
                        Destination tempDest = new Destination();
                        if (row.getCell( j * 8 + 5 ) != null) {
                            tempDest.setDepartureTime( row.getCell( j * 8 + 5 ).getNumericCellValue() );
                        }
                        if (row.getCell( j * 8 + 6 ) != null) {
                            if (row.getCell( j * 8 + 6 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                tempDest.setDesiredMode( row.getCell( j * 8 + 6 ).getStringCellValue() );
                            } else if (row.getCell( j * 8 + 6 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                                tempDest.setDesiredMode( row.getCell( j * 8 + 6 ).getNumericCellValue() + "" );
                            }
                        }
                        if (row.getCell( j * 8 + 7 ) != null) {
//                                    tempDest.setTripPurpose(row.getCell(j*8+7).getStringCellValue());
                            if (row.getCell( j * 8 + 7 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                tempDest.setTripPurpose( row.getCell( j * 8 + 7 ).getStringCellValue() );
                            } else if (row.getCell( j * 8 + 7 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                                tempDest.setTripPurpose( row.getCell( j * 8 + 7 ).getNumericCellValue() + "" );
                            }
                        }
                        if (row.getCell( j * 8 + 8 ) != null) {
//                                    tempDest.setFacilityId(row.getCell(j*8+8).getStringCellValue());
                            if (row.getCell( j * 8 + 8 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                tempDest.setFacilityId( row.getCell( j * 8 + 8 ).getStringCellValue() );
                            } else if (row.getCell( j * 8 + 8 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                                tempDest.setFacilityId( row.getCell( j * 8 + 8 ).getNumericCellValue() + "" );
                            }
                        }
                        if (row.getCell( j * 8 + 9 ) != null) {
                            if (row.getCell( j * 8 + 9 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                String temp = row.getCell( j * 8 + 9 ).getStringCellValue();
                                if (temp.equalsIgnoreCase( "yes" ) || temp.equalsIgnoreCase( "true" ) || temp.equalsIgnoreCase( "1" )) {
                                    tempDest.setFacilityChangable( true );
                                } else {
                                    tempDest.setFacilityChangable( false );
                                }
                            } else if (row.getCell( j * 8 + 9 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" ) && row.getCell( j * 8 + 9 ).getNumericCellValue() == 1.0) {
                                tempDest.setFacilityChangable( true );
                            } else {
                                tempDest.setFacilityChangable( false );
                            }

                        }
                        if (row.getCell( j * 8 + 10 ) != null) {
//                                    tempDest.setDesRegionId(row.getCell(j*8+10).getStringCellValue());
                            if (row.getCell( j * 8 + 10 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                tempDest.setDesRegionId( row.getCell( j * 8 + 10 ).getStringCellValue() );
                            } else if (row.getCell( j * 8 + 10 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                                tempDest.setDesRegionId( row.getCell( j * 8 + 10 ).getNumericCellValue() + "" );
                            }
                        }
                        if (row.getCell( j * 8 + 11 ) != null) {
                            tempDest.setDepartureTime( row.getCell( j * 8 + 11 ).getNumericCellValue() );
                        }
                        if (row.getCell( j * 8 + 12 ) != null) {
                            tempDest.setDepartureTime( row.getCell( j * 8 + 12 ).getNumericCellValue() );
                        }
                        destinations[j] = tempDest;
                    }
                    tempOdChain.setDestinations( destinations );
                    ods.add( tempOdChain );
                }
            }

        } else if (type.equalsIgnoreCase( "squareMatrix" )) {

            XSSFRow header = sheet.getRow( 0 );
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header
                // XSSFRow 代表一行数据
                String preId = file.getName().replace( ".xlsx", "" ) + "_" + type + "_" + asRegionOrCoord + "_";
                XSSFRow row = sheet.getRow( i );
                Origin tempOri = new Origin();
                if (row.getCell( 0 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                    tempOri.setOriRegionId( row.getCell( 0 ).getStringCellValue() );
                } else if (row.getCell( 0 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                    tempOri.setOriRegionId( "" + (int) row.getCell( 0 ).getNumericCellValue() );
                }

                List<String> desRegions = new ArrayList<>();
                List<Integer> odRegionCount = new ArrayList<>();

                for (int j = 1; j < row.getLastCellNum(); j++) {
                    if (row.getCell( j ) != null) {
                        if ((int) row.getCell( j ).getNumericCellValue() > 0) {
                            int tempOdCounts = (int) row.getCell( i ).getNumericCellValue();
                            for (int k = 0; k < tempOdCounts; k++) {
                                OdChain tempOdChain = new OdChain();
                                tempOdChain.setId( preId + k );
                                tempOdChain.setOrigin( tempOri );
                                Destination tempDes = new Destination();
                                if (header.getCell( j ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                    tempDes.setDesRegionId( row.getCell( j ).getStringCellValue() );
                                } else if (header.getCell( j ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                                    tempDes.setDesRegionId( "" + (int) row.getCell( j ).getNumericCellValue() );
                                }
                                Destination[] tempDestinations = new Destination[1];
                                tempDestinations[0] = tempDes;
                                tempOdChain.setDestinations( tempDestinations );
                                ods.add( tempOdChain );
                            }
                        }
                    }
                }

            }
        }

        return ods;
    }

    public static List<OdChain> getOdChainsFromXls(File file, HSSFSheet sheet, String type, String asRegionOrCoord) {
        List<OdChain> ods = new ArrayList<>();

        if (type.equalsIgnoreCase( "array" )) {
            //System.out.println(file.getAbsolutePath());


            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header
                // XSSFRow 代表一行数据
                OdChain tempOd = new OdChain();//file.getName().replace(".xlsx","")+"_"+type+"_"+asRegionOrCoord+"_"+i);
                HSSFRow row = sheet.getRow( i );
                String tempId = "";
                if (row.getCell( 0 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                    tempId = row.getCell( 0 ).toString();
                } else if (row.getCell( 0 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                    tempId = "" + (int) row.getCell( 0 ).getNumericCellValue();
                }
                // get count
                int tempCount = (int) row.getCell( 1 ).getNumericCellValue();

                Origin tempOrigin = new Origin();
                // get origin region id
                String oriRegionId = "";
                if (row.getCell( 2 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                    tempOrigin.setOriRegionId( row.getCell( 2 ).toString() );
                } else if (row.getCell( 2 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                    tempOrigin.setOriRegionId( "" + (int) row.getCell( 2 ).getNumericCellValue() );
                }
                double oriCoordX;
                if (row.getCell( 3 ) != null) {
                    tempOrigin.setOriginCoordX( row.getCell( 3 ).getNumericCellValue() );
                }
                double oriCoordY;
                if (row.getCell( 4 ) != null) {
                    tempOrigin.setOriginCoordX( row.getCell( 4 ).getNumericCellValue() );
                }

                int desNum = (row.getLastCellNum() - 5) / 8;
                for (int iCount = 0; iCount < tempCount; iCount++) {
                    OdChain tempOdChain = new OdChain();
                    tempOdChain.setOrigin( tempOrigin );
                    tempOdChain.setId( tempId + "_" + iCount );
                    Destination[] destinations = new Destination[desNum];
                    for (int j = 0; j < desNum; j++) {
                        // departureTime1(5+8*j)|desiredMode1(6+8*j)|tripPurpose1(7+8*j)|desFacilityId1(8+8*j)|
                        // facilityChangable1(9+8*j)|desRegionId1(10+8*j)|desCoordX1(11+8*j)|desCoordY1(12+8*j)
                        Destination tempDest = new Destination();
                        if (row.getCell( j * 8 + 5 ) != null) {
                            tempDest.setDepartureTime( row.getCell( j * 8 + 5 ).getNumericCellValue() );
                        }
                        if (row.getCell( j * 8 + 6 ) != null) {
                            if (row.getCell( j * 8 + 6 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                tempDest.setDesiredMode( row.getCell( j * 8 + 6 ).getStringCellValue() );
                            } else if (row.getCell( j * 8 + 6 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                                tempDest.setDesiredMode( row.getCell( j * 8 + 6 ).getNumericCellValue() + "" );
                            }
                        }
                        if (row.getCell( j * 8 + 7 ) != null) {
//                                    tempDest.setTripPurpose(row.getCell(j*8+7).getStringCellValue());
                            if (row.getCell( j * 8 + 7 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                tempDest.setTripPurpose( row.getCell( j * 8 + 7 ).getStringCellValue() );
                            } else if (row.getCell( j * 8 + 7 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                                tempDest.setTripPurpose( row.getCell( j * 8 + 7 ).getNumericCellValue() + "" );
                            }
                        }
                        if (row.getCell( j * 8 + 8 ) != null) {
//                                    tempDest.setFacilityId(row.getCell(j*8+8).getStringCellValue());
                            if (row.getCell( j * 8 + 8 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                tempDest.setFacilityId( row.getCell( j * 8 + 8 ).getStringCellValue() );
                            } else if (row.getCell( j * 8 + 8 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                                tempDest.setFacilityId( row.getCell( j * 8 + 8 ).getNumericCellValue() + "" );
                            }
                        }
                        if (row.getCell( j * 8 + 9 ) != null) {
                            if (row.getCell( j * 8 + 9 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                String temp = row.getCell( j * 8 + 9 ).getStringCellValue();
                                if (temp.equalsIgnoreCase( "yes" ) || temp.equalsIgnoreCase( "true" ) || temp.equalsIgnoreCase( "1" )) {
                                    tempDest.setFacilityChangable( true );
                                } else {
                                    tempDest.setFacilityChangable( false );
                                }
                            } else if (row.getCell( j * 8 + 9 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" ) && row.getCell( j * 8 + 9 ).getNumericCellValue() == 1.0) {
                                tempDest.setFacilityChangable( true );
                            } else {
                                tempDest.setFacilityChangable( false );
                            }

                        }
                        if (row.getCell( j * 8 + 10 ) != null) {
//                                    tempDest.setDesRegionId(row.getCell(j*8+10).getStringCellValue());
                            if (row.getCell( j * 8 + 10 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                tempDest.setDesRegionId( row.getCell( j * 8 + 10 ).getStringCellValue() );
                            } else if (row.getCell( j * 8 + 10 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                                tempDest.setDesRegionId( row.getCell( j * 8 + 10 ).getNumericCellValue() + "" );
                            }
                        }
                        if (row.getCell( j * 8 + 11 ) != null) {
                            tempDest.setDepartureTime( row.getCell( j * 8 + 11 ).getNumericCellValue() );
                        }
                        if (row.getCell( j * 8 + 12 ) != null) {
                            tempDest.setDepartureTime( row.getCell( j * 8 + 12 ).getNumericCellValue() );
                        }
                        destinations[j] = tempDest;
                    }
                    tempOdChain.setDestinations( destinations );
                    ods.add( tempOdChain );
                }
            }

        } else if (type.equalsIgnoreCase( "squareMatrix" )) {

            HSSFRow header = sheet.getRow( 0 );
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header
                // XSSFRow 代表一行数据
                String preId = file.getName().replace( ".xlsx", "" ) + "_" + type + "_" + asRegionOrCoord + "_";
                HSSFRow row = sheet.getRow( i );
                Origin tempOri = new Origin();
                if (row.getCell( 0 ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                    tempOri.setOriRegionId( row.getCell( 0 ).getStringCellValue() );
                } else if (row.getCell( 0 ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                    tempOri.setOriRegionId( "" + (int) row.getCell( 0 ).getNumericCellValue() );
                }


                for (int j = 1; j < row.getLastCellNum(); j++) {
                    if (row.getCell( j ) != null) {
                        if ((int) row.getCell( j ).getNumericCellValue() > 0) {
                            int tempOdCounts = (int) row.getCell( i ).getNumericCellValue();
                            for (int k = 0; k < tempOdCounts; k++) {
                                OdChain tempOdChain = new OdChain();
                                tempOdChain.setId( preId + k );
                                tempOdChain.setOrigin( tempOri );
                                Destination tempDes = new Destination();
                                if (header.getCell( j ).getCellTypeEnum().toString().toLowerCase().equals( "string" )) {
                                    tempDes.setDesRegionId( row.getCell( j ).getStringCellValue() );
                                } else if (header.getCell( j ).getCellTypeEnum().toString().toLowerCase().equals( "numeric" )) {
                                    tempDes.setDesRegionId( "" + (int) row.getCell( j ).getNumericCellValue() );
                                }
                                Destination[] tempDestinations = new Destination[1];
                                tempDestinations[0] = tempDes;
                                tempOdChain.setDestinations( tempDestinations );
                                ods.add( tempOdChain );
                            }
                        }
                    }
                }

            }
        }

        return ods;
    }

    public static List<OdChain> getOdDataFromCsv(File file, String type, String asRegionOrCoord) throws Exception {
        List<OdChain> ods = new ArrayList<>();

        BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( file ), "utf-8" ) );
        String[] header = br.readLine().replace( " ", "" ).split( "" ); // read header and delete all unnecessary spaces
        String line;
        if (type.equalsIgnoreCase( "array" )) {
            while ((line = br.readLine()) != null) { // skip header
                String[] tempLine = line.replace( " ", "" ).split( "," );
//  * (1)  id(0)|count(1)|oriDepartureTime(2)|origionRegion(3)|originCoordX(4)|originCoordY(5)
//  * (2)   departureTime1(6)|desiredMode1(7)|tripPurpose1(8)|desFacilityId1(9)|facilityChangable1(10)|desRegionId1(11)|desCoordX1(12)|desCoordY1(13)
                String preId = tempLine[0];
                int count = Integer.parseInt( tempLine[1] );
                Origin tempOri = new Origin();
                if (!tempLine[2].isEmpty()){
                    tempOri.setOriDepartureTime( TimeFormatUtil.hhmmss2Seconds( tempLine[2] ) );
                }
                if (!tempLine[3].isEmpty()) {
                    tempOri.setOriRegionId( tempLine[3] );
                }
                if (!tempLine[4].isEmpty()) {
                    tempOri.setOriginCoordX( Double.parseDouble( tempLine[4] ) );
                }
                if (!tempLine[5].isEmpty()) {
                    tempOri.setOriginCoordY( Double.parseDouble( tempLine[5] ) );
                }

                int desNum = (tempLine.length - 6) / 8;
                for (int i = 0; i < count; i++) {
                    OdChain odChain = new OdChain();
                    odChain.setOrigin( tempOri );
                    Destination[] destinations = new Destination[desNum];
                    odChain.setId( preId + "_" + i );
                    for (int j = 0; j < desNum; j++) {
                        // departureTime1(6+8*j)|desiredMode1(7+8*j)|tripPurpose1(8+8*j)|desFacilityId1(9+8*j)|
                        // facilityChangable1(10+8*j)|desRegionId1(11+8*j)|desCoordX1(12+8*j)|desCoordY1(13+8*j)
                        destinations[j] = new Destination();
                        if (!tempLine[6 + 8 * j].isEmpty()) {
                            destinations[j].setDepartureTime( TimeFormatUtil.hhmmss2Seconds( tempLine[6 + 8 * j] ) );
//                            System.out.println(TimeFormatUtil.hhmmss2Seconds( tempLine[6 + 8 * j] ));
                        }
                        if (!tempLine[7 + 8 * j].isEmpty()) {
                            destinations[j].setDesiredMode( tempLine[7 + 8 * j] );
                        } else {
                            destinations[j].setDesiredMode( "car" );
                        }
                        if (!tempLine[8 + 8 * j].isEmpty()) {
                            destinations[j].setTripPurpose( tempLine[8 + 8 * j] );
                        }
                        if (!tempLine[9 + 8 * j].isEmpty()) {
                            destinations[j].setFacilityId( tempLine[9 + 8 * j] );
                        }
                        if (!tempLine[10 + 8 * j].isEmpty()) {
                            String temp = tempLine[10 + 8 * j];
                            if (temp.equalsIgnoreCase( "yes" ) || temp.equalsIgnoreCase( "true" ) || temp.equalsIgnoreCase( "1" )) {
                                destinations[j].setFacilityChangable( true );
                            } else {
                                destinations[j].setFacilityChangable( false );
                            }
                        } else {
                            destinations[j].setFacilityChangable( false );
                        }
                        if (!tempLine[11 + 8 * j].isEmpty()) {
                            destinations[j].setDesRegionId( tempLine[11 + 8 * j] );
                        }
                        if (!tempLine[12 + 8 * j].isEmpty()) {
                            destinations[j].setDesCoordX( Double.parseDouble( tempLine[12 + 8 * j] ) );
                        }
                        if (!tempLine[13 + 8 * j].isEmpty()) {
                            destinations[j].setDesCoordY( Double.parseDouble( tempLine[13 + 8 * j] ) );
                        }

                    }
                    odChain.setDestinations( destinations );
                    ods.add( odChain );
                }
            }
            br.close();

        } else if (type.equalsIgnoreCase( "squareMatrix" )) {
            int iPerson = 0;
            while ((line = br.readLine()) != null) { // skip header
                String preId = file.getName().replace( ".csv", "" ).replace( ".txt", "" ) + "_" + type + "_" + asRegionOrCoord + "_";
                String[] tempLine = line.replace( " ", "" ).split( "," );
                Origin tempOri = new Origin();
                tempOri.setOriRegionId( tempLine[0] );
//                log.info( line );
                for (int i = 1; i < tempLine.length; i++) {
                    int tempOdCount = Integer.parseInt( tempLine[i] );
                    String desRegionId = header[i];
                    for (int j = 0; j < tempOdCount; j++) {
                        OdChain tempOdChain = new OdChain();
                        tempOdChain.setId( preId + iPerson + "_" + i + "_" + j );
                        tempOdChain.setOrigin( tempOri );
                        Destination tempDes = new Destination();
                        tempDes.setDesRegionId( desRegionId );
                        tempDes.setTripPurpose( "work" );
                        tempDes.setDesiredMode( "car" );
                        tempDes.setFacilityChangable( false );
                        tempDes.setDepartureTime( 18*3600 );
                        Destination[] destinations = new Destination[1];
                        destinations[0] = tempDes;
                        tempOdChain.setDestinations( destinations );
                        ods.add( tempOdChain );
                    }
                }
                iPerson++;
            }
            br.close();
        }
//        log.info( ods.toString() +" 531");

        return ods;
    }


    public StringBuffer getOriginalDataFromArrayCsv(String file) throws Exception{
        StringBuffer data = new StringBuffer("[");
        BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
        String header =br.readLine() ;
        data.append( "['"+ header.replace( " ","" ).replace( ";","," ).replace( ",","','" )+"']," );
        String line;
        while ((line = br.readLine()) != null) { // skip header
            String[] tempLine = line.replace( " ","" ).replace( ";","," ).split( "," );
            String modifiedLine = "[";
            for (int i=0;i<tempLine.length;i++){
                if (!tempLine[i].isEmpty()) {
                    modifiedLine += ("'" + tempLine[i] + "',");
                }else{
                    modifiedLine += ("'null',");
                }
            }

            data.append( modifiedLine.substring( 0,modifiedLine.length()-1 )+"]," );
        }
        data.deleteCharAt( data.length()-1 );
        data.append( "]" );
        return data;
    }

    public List<String> error = new ArrayList<>();
    public Set<String> modes = new HashSet<>(  );
    public Set<String> tripPurposes = new HashSet<>(  );
    public StringBuffer validateOriginalDataFromArrayCsv(String file) throws Exception{
        StringBuffer data = new StringBuffer("[");
        BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream( file ), "utf-8" ) );
        String header =br.readLine() ;
        data.append( "['"+ header.replace( " ","" ).replace( ";","," ).replace( ",","','" )+"']," );
        String line;
        int index = 0;
        Set<String> idCheck = new HashSet<>(  );
        while ((line = br.readLine()) != null) { // skip header
            String[] tempLine = line.replace( " ","" ).replace( ";","," ).split( "," );
            int desNum = (tempLine.length - 5) / 8;
            // replace null values
            //  * (1)  id(0)|count(1)|oriDepatrueTime(2)|origionRegion(3)|originCoordX(4)|originCoordY(5)
            if (tempLine[0].isEmpty()){error.add("row: "+(index+1)+" col: "+1+",id shouldn't be empty");break;}
            if (idCheck.contains( tempLine[0] ) && Integer.parseInt( tempLine[1] )==1){error.add("row: "+(index+1)+" col: "+1+",there is some other agent using the same id");}else{idCheck.add( tempLine[0] );}
            if (tempLine[1].isEmpty()){tempLine[1] = "1";error.add("row: "+(index+1)+" col: "+2+",id shouldn't be empty");}
            if (tempLine[2].isEmpty()){tempLine[2] = "null";error.add("row: "+(index+1)+" col: "+3+",id shouldn't be empty");}
            if (!TimeFormatUtil.isHHMMSSformat( tempLine[2])){tempLine[2]+="*";error.add("row: "+(index+1)+" col: "+(2)+"; not HH:MM:SS format");}
            if (tempLine[3].isEmpty()&&tempLine[4].isEmpty()&&tempLine[5].isEmpty()){tempLine[3] = "*";tempLine[4] += "*";tempLine[5] = "*";error.add("row: "+(index+1)+" col: 4,5,6; regionId or coords should be filled");}
            else if(tempLine[3].isEmpty()&&tempLine[4].isEmpty()&&!tempLine[5].isEmpty()){tempLine[3] = "null";tempLine[4] = "*";error.add("row: "+(index+1)+" col:5; coordXEmpty");}
            else if(tempLine[3].isEmpty()&&!tempLine[4].isEmpty()&&tempLine[5].isEmpty()){tempLine[3] = "null";tempLine[5] = "*";error.add("row: "+(index+1)+" col:6; coordXEmpty");}
            else if(tempLine[3].isEmpty()&&!tempLine[4].isEmpty()&&!tempLine[5].isEmpty()){tempLine[3] = "null";}
            else if(!tempLine[3].isEmpty()&&tempLine[4].isEmpty()&&tempLine[5].isEmpty()){tempLine[4] = "null";tempLine[5] = "null";}
            else if(!tempLine[3].isEmpty()&&tempLine[4].isEmpty()&&!tempLine[5].isEmpty()){tempLine[3] += "*";tempLine[5] += "*";error.add("row: "+(index+1)+" col: 4,6; duplicatedInput");}
            else if(!tempLine[3].isEmpty()&&!tempLine[4].isEmpty()&&tempLine[5].isEmpty()){tempLine[3] += "*";tempLine[4] += "*";
                error.add("row: "+(index+1)+" col: 4,5; duplicatedInput");}
            else if(!tempLine[3].isEmpty()&&!tempLine[4].isEmpty()&&!tempLine[5].isEmpty()){tempLine[3] += "*";tempLine[4] += "*";tempLine[5] += "*";error.add("row: "+(index+1)+" col:4,5,6; duplicatedInput");}
            if(!isInteger(tempLine[1])&&!tempLine[1].isEmpty()){tempLine[1]+="*";error.add("row: "+(index+1)+" col: 2; int required");}
            if(!isDouble(tempLine[4])&&!tempLine[4].equalsIgnoreCase( "null" )&&!tempLine[4].isEmpty()){tempLine[4]+="*";error.add("row: "+(index+1)+" col: 5; double required");}
            if(!isDouble(tempLine[5])&&!tempLine[5].equalsIgnoreCase( "null" )&&!tempLine[5].isEmpty()){tempLine[5]+="*";error.add("row: "+(index+1)+" col: 6; double required");}
            if(isDouble(tempLine[4])&&(Double.parseDouble(tempLine[4])>180||Double.parseDouble(tempLine[4])<=0)){tempLine[4]+="*";error.add("row: "+(index+1)+" col: 5; longitude range:0-180");}
            if(isDouble(tempLine[5])&&(Double.parseDouble(tempLine[5])>180||Double.parseDouble(tempLine[5])<=0)){tempLine[5]+="*";error.add("row: "+(index+1)+" col: 6; longitude range:0-180");}
            if (tempLine.length>14) {
                for (int i = 0; i < 1; i++) {
                    // departureTime1(6+8*i)|desiredMode1(7+8*i)|tripPurpose1(8+8*i)|desFacilityId1(9+8*i)|
                    // facilityChangable1(10+8*i)|desRegionId1(11+8*i)|desCoordX1(12+8*i)|desCoordY1(13+8*i)
                    if (!TimeFormatUtil.isHHMMSSformat( tempLine[6 + 8 * i] )) {
                        tempLine[6 + 8 * i] += "*";
                        error.add( "row: " + (index + 1) + " col: " + (7 + 8 * i) + "; not HH:MM:SS format" );
                    }
                    if ((tempLine[6 + 8 * i]).isEmpty()) {
                        tempLine[6 + 8 * i] = "null";
                        error.add( "row: " + (index + 1) + " col: " + (7 + 8 * i) + "; should not be empty" );
                    }
                    if (!tempLine[7 + 8 * i].isEmpty()) {
                        modes.add( tempLine[7 + 8 * i].toLowerCase() );
                    } else {
                        tempLine[7 + 8 * i] = "null";
                    }
                    if (!tempLine[8 + 8 * i].isEmpty()) {
                        tripPurposes.add( tempLine[8 + 8 * i].toLowerCase() );
                    } else {
                        tempLine[8 + 8 * i] = "null";
                    }
                    //todo facility is also needed to validate with coord and
                    log.info( tempLine.length + "" );
                    if (tempLine[9 + 8 * i].isEmpty() && tempLine[11 + 8 * i].isEmpty() && tempLine[12 + 8 * i].isEmpty() && tempLine[13 + 8 * i].isEmpty()) {
                        tempLine[9 + 8 * i] += "*";
                        tempLine[11 + 8 * i] += "*";
                        tempLine[12 + 8 * i] += "*";
                        tempLine[13 + 8 * i] += "*";
                        error.add( "row: " + (index + 1) + " col: " + (10 + 8 * i) + "; at least one destination is needed" );
                    }
                    if (tempLine[10 + 8 * i].toLowerCase().isEmpty()) {
                        tempLine[10 + 8 * i] = "null";
                    } else if (!(tempLine[10 + 8 * i].toLowerCase().equals( "yes" ) || tempLine[10 + 8 * i].toLowerCase().equals( "true" ) || tempLine[10 + 8 * i].toLowerCase().equals( "1" )
                            || tempLine[10 + 8 * i].toLowerCase().equals( "no" ) || tempLine[10 + 8 * i].toLowerCase().equals( "false" ) || tempLine[10 + 8 * i].toLowerCase().equals( "0" ))) {
                        error.add( "row: " + (index + 1) + " col: " + (11 + 8 * i) + "; not in{yes,no,true,false,0,1}" );
                    }
                    if (!tempLine[9 + 8 * i].isEmpty() && tempLine[11 + 8 * i].isEmpty() && tempLine[12 + 8 * i].isEmpty() && tempLine[13 + 8 * i].isEmpty()) {
                        tempLine[11 + 8 * i] = "*";
                        tempLine[12 + 8 * i] += "*";
                        tempLine[13 + 8 * i] = "*";
                    } else if (tempLine[11 + 8 * i].isEmpty() && tempLine[12 + 8 * i].isEmpty() && !tempLine[13 + 8 * i].isEmpty()) {
                        tempLine[11 + 8 * i] = "null";
                        tempLine[12 + 8 * i] = "*";
                        error.add( "row: " + (index + 1) + " col:" + "," + (13 + 8 * i) + "; coordXEmpty" );
                    } else if (tempLine[11 + 8 * i].isEmpty() && !tempLine[12 + 8 * i].isEmpty() && tempLine[13 + 8 * i].isEmpty()) {
                        tempLine[11 + 8 * i] = "null";
                        tempLine[13 + 8 * i] = "*";
                        error.add( "row: " + (index + 1) + " col:" + "," + (14 + 8 * i) + "; coordXEmpty" );
                    } else if (tempLine[11 + 8 * i].isEmpty() && !tempLine[12 + 8 * i].isEmpty() && !tempLine[13 + 8 * i].isEmpty()) {
                        tempLine[11 + 8 * i] = "null";
                    } else if (!tempLine[11 + 8 * i].isEmpty() && tempLine[12 + 8 * i].isEmpty() && tempLine[13 + 8 * i].isEmpty()) {
                        tempLine[12 + 8 * i] = "null";
                        tempLine[13 + 8 * i] = "null";
                    } else if (!tempLine[11 + 8 * i].isEmpty() && tempLine[12 + 8 * i].isEmpty() && !tempLine[13 + 8 * i].isEmpty()) {
                        tempLine[11 + 8 * i] += "*";
                        tempLine[13 + 8 * i] += "*";
                        error.add( "row: " + (index + 1) + " col: " + (12 + 8 * i) + "," + (14 + 8 * i) + "," + "; duplicatedInput" );
                    } else if (!tempLine[11 + 8 * i].isEmpty() && !tempLine[12 + 8 * i].isEmpty() && tempLine[13 + 8 * i].isEmpty()) {
                        tempLine[11 + 8 * i] += "*";
                        tempLine[12 + 8 * i] += "*";
                        error.add( "row: " + (index + 1) + " col: " + (12 + 8 * i) + "," + (13 + 8 * i) + "; duplicatedInput" );
                    } else if (!tempLine[11 + 8 * i].isEmpty() && !tempLine[12 + 8 * i].isEmpty() && !tempLine[13 + 8 * i].isEmpty()) {
                        tempLine[11 + 8 * i] += "*";
                        tempLine[12 + 8 * i] += "*";
                        tempLine[13 + 8 * i] += "*";
                        error.add( "row: " + (index + 1) + " col:" + (12 + 8 * i) + "," + (13 + 8 * i) + "," + (14 + 8 * i) + "; duplicatedInput" );
                    }
                    if (!isDouble( tempLine[12 + 8 * i] ) && !tempLine[12 + 8 * i].equalsIgnoreCase( "null" )&&!tempLine[12 + 8 * i].isEmpty()) {
                        tempLine[12 + 8 * i] += "*";
                        error.add( "row: " + (index + 1) + " col: " + (13 + 8 * i) + "; double required" );
                    }
                    if (!isDouble( tempLine[13 + 8 * i] ) && !tempLine[13 + 8 * i].equalsIgnoreCase("null")&&!tempLine[13 + 8 * i].isEmpty()) {
                        tempLine[13 + 8 * i] += "*";
                        error.add( "row: " + (index + 1) + " col: " + (14 + 8 * i) + "; double required" );
                    }
                    if (isDouble( tempLine[12 + 8 * i] ) && (Double.parseDouble( tempLine[12 + 8 * i] ) > 180 || Double.parseDouble( tempLine[12 + 8 * i] ) <= 0)) {
                        tempLine[12 + 8 * i] += "*";
                        error.add( "row: " + (index + 1) + " col: " + (13 + 8 * i) + "; longitude range:0-180" );
                    }
                    if (isDouble( tempLine[13 + 8 * i] ) && (Double.parseDouble( tempLine[13 + 8 * i] ) > 180 || Double.parseDouble( tempLine[13 + 8 * i] ) <= 0)) {
                        tempLine[13 + 8 * i] += "*";
                        error.add( "row: " + (index + 1) + " col: " + (14 + 8 * i) + "; longitude range:0-180" );
                    }

                }
                for (int i = 1; i < desNum; i++) {
                    boolean notEmpty = false;
                    for (int j = 5; j < 13; j++) {
                        notEmpty = (notEmpty || (!tempLine[j + i * 8].isEmpty()));
                    }
                    if (notEmpty) {
                        // departureTime1(5+8*i)|desiredMode1(6+8*i)|tripPurpose1(7+8*i)|desFacilityId1(8+8*i)|
                        // facilityChangable1(9+8*i)|desRegionId1(10+8*i)|desCoordX1(11+8*i)|desCoordY1(12+8*i)
                        if (!TimeFormatUtil.isHHMMSSformat( tempLine[6 + 8 * i] )) {
                            tempLine[6 + 8 * i] += "*";
                            error.add( "row: " + (index + 1) + " col: " + (7 + 8 * i) + "; not HH:MM:SS format" );
                        }
                        if ((tempLine[6 + 8 * i]).isEmpty()) {
                            tempLine[6 + 8 * i] = "null";
                            error.add( "row: " + (index + 1) + " col: " + (7 + 8 * i) + "; should not be empty" );
                        }
                        if (!tempLine[7 + 8 * i].isEmpty()) {
                            modes.add( tempLine[7 + 8 * i].toLowerCase() );
                        } else {
                            tempLine[7 + 8 * i] = "null";
                        }
                        if (!tempLine[8 + 8 * i].isEmpty()) {
                            tripPurposes.add( tempLine[8 + 8 * i].toLowerCase() );
                        } else {
                            tempLine[8 + 8 * i] = "null";
                        }
                        //todo facility is also needed to validate with coord and
                        if (tempLine[9 + 8 * i].isEmpty() && tempLine[11 + 8 * i].isEmpty() && tempLine[12 + 8 * i].isEmpty() && tempLine[13 + 8 * i].isEmpty()) {
                            tempLine[9 + 8 * i] += "*";
                            tempLine[11 + 8 * i] += "*";
                            tempLine[12 + 8 * i] += "*";
                            tempLine[13 + 8 * i] += "*";
                            error.add( "row: " + (index + 1) + " col: " + (10 + 8 * i) + "; at least one destination is needed" );
                        }
                        if (tempLine[10 + 8 * i].toLowerCase().isEmpty()) {
                            tempLine[10 + 8 * i] = "null";
                        } else if (!(tempLine[10 + 8 * i].toLowerCase().equals( "yes" ) || tempLine[10 + 8 * i].toLowerCase().equals( "true" ) || tempLine[10 + 8 * i].toLowerCase().equals( "1" )
                                || tempLine[10 + 8 * i].toLowerCase().equals( "no" ) || tempLine[10 + 8 * i].toLowerCase().equals( "false" ) || tempLine[10 + 8 * i].toLowerCase().equals( "0" ))) {
                            error.add( "row: " + (index + 1) + " col: " + (11 + 8 * i) + "; not in{yes,no,true,false,0,1}" );
                        }
                        if (!tempLine[9 + 8 * i].isEmpty() && tempLine[11 + 8 * i].isEmpty() && tempLine[12 + 8 * i].isEmpty() && tempLine[13 + 8 * i].isEmpty()) {
                            tempLine[11 + 8 * i] = "*";
                            tempLine[12 + 8 * i] += "*";
                            tempLine[13 + 8 * i] = "*";
                        } else if (tempLine[11 + 8 * i].isEmpty() && tempLine[12 + 8 * i].isEmpty() && !tempLine[13 + 8 * i].isEmpty()) {
                            tempLine[11 + 8 * i] = "null";
                            tempLine[12 + 8 * i] = "*";
                            error.add( "row: " + (index + 1) + " col:" + "," + (13 + 8 * i) + "; coordXEmpty" );
                        } else if (tempLine[11 + 8 * i].isEmpty() && !tempLine[12 + 8 * i].isEmpty() && tempLine[13 + 8 * i].isEmpty()) {
                            tempLine[11 + 8 * i] = "null";
                            tempLine[13 + 8 * i] = "*";
                            error.add( "row: " + (index + 1) + " col:" + "," + (14 + 8 * i) + "; coordXEmpty" );
                        } else if (tempLine[11 + 8 * i].isEmpty() && !tempLine[12 + 8 * i].isEmpty() && !tempLine[13 + 8 * i].isEmpty()) {
                            tempLine[11 + 8 * i] = "null";
                        } else if (!tempLine[11 + 8 * i].isEmpty() && tempLine[12 + 8 * i].isEmpty() && tempLine[13 + 8 * i].isEmpty()) {
                            tempLine[12 + 8 * i] = "null";
                            tempLine[13 + 8 * i] = "null";
                        } else if (!tempLine[11 + 8 * i].isEmpty() && tempLine[12 + 8 * i].isEmpty() && !tempLine[13 + 8 * i].isEmpty()) {
                            tempLine[11 + 8 * i] += "*";
                            tempLine[13 + 8 * i] += "*";
                            error.add( "row: " + (index + 1) + " col: " + (12 + 8 * i) + "," + (14 + 8 * i) + "," + "; duplicatedInput" );
                        } else if (!tempLine[11 + 8 * i].isEmpty() && !tempLine[12 + 8 * i].isEmpty() && tempLine[13 + 8 * i].isEmpty()) {
                            tempLine[11 + 8 * i] += "*";
                            tempLine[12 + 8 * i] += "*";
                            error.add( "row: " + (index + 1) + " col: " + (12 + 8 * i) + "," + (13 + 8 * i) + "; duplicatedInput" );
                        } else if (!tempLine[11 + 8 * i].isEmpty() && !tempLine[12 + 8 * i].isEmpty() && !tempLine[13 + 8 * i].isEmpty()) {
                            tempLine[11 + 8 * i] += "*";
                            tempLine[12 + 8 * i] += "*";
                            tempLine[13 + 8 * i] += "*";
                            error.add( "row: " + (index + 1) + " col:" + (12 + 8 * i) + "," + (13 + 8 * i) + "," + (14 + 8 * i) + "; duplicatedInput" );
                        }
                        if (!isDouble( tempLine[12 + 8 * i] ) && !tempLine[12 + 8 * i].equalsIgnoreCase( "null" )&&!tempLine[12 + 8 * i].isEmpty()) {
                            tempLine[12 + 8 * i] += "*";
                            error.add( "row: " + (index + 1) + " col: " + (13 + 8 * i) + "; double required" );
                        }
                        if (!isDouble( tempLine[13 + 8 * i] ) && !tempLine[13 + 8 * i].equalsIgnoreCase("null")&&!tempLine[13 + 8 * i].isEmpty()) {
                            tempLine[13 + 8 * i] += "*";
                            error.add( "row: " + (index + 1) + " col: " + (14 + 8 * i) + "; double required" );
                        }
                        if (isDouble( tempLine[12 + 8 * i] ) && (Double.parseDouble( tempLine[12 + 8 * i] ) > 180 || Double.parseDouble( tempLine[12 + 8 * i] ) <= 0)) {
                            tempLine[12 + 8 * i] += "*";
                            error.add( "row: " + (index + 1) + " col: " + (13 + 8 * i) + "; longitude range:0-180" );
                        }
                        if (isDouble( tempLine[13 + 8 * i] ) && (Double.parseDouble( tempLine[13 + 8 * i] ) > 180 || Double.parseDouble( tempLine[13 + 8 * i] ) <= 0)) {
                            tempLine[13 + 8 * i] += "*";
                            error.add( "row: " + (index + 1) + " col: " + (14 + 8 * i) + "; longitude range:0-180" );
                        }

                    }
                }
            }else{
                tempLine[0] += "* No destination?!";
            }

            String modifiedLine = "[";
            for (int i=0;i<tempLine.length;i++){
                modifiedLine += ("'"+ tempLine[i]+"',");
            }

            data.append( modifiedLine.substring( 0,modifiedLine.length()-1 )+"]," );
            index ++;
        }
        data.deleteCharAt( data.length()-1 );
        data.append( "]" );
//        System.out.println(data);
//        System.out.println(error.toString());

        return data;
    }



    //判断整数（int）
    public static boolean isInteger(String str) {
        if (null == str || "".equals( str )) {
            return false;
        }
        Pattern pattern = Pattern.compile( "^[-\\+]?[\\d]*$" );
        return pattern.matcher( str ).matches();
    }

    //判断浮点数（double和float）
    public static boolean isDouble(String str) {
        if (null == str || "".equals( str )) {
            return false;
        }
        Pattern pattern = Pattern.compile( "^[-\\+]?[.\\d]*$" );
        return pattern.matcher( str ).matches();
    }

    // if string is
    public static boolean isNumeric(String str) {
        if (null == str || "".equals( str )) { 
            return false;                      
        }
        Pattern patternInt = Pattern.compile( "^[-\\+]?[\\d]*$" );
        Pattern patternDouble = Pattern.compile( "^[-\\+]?[.\\d]*$" );
        return patternInt.matcher( str ).matches()||patternDouble.matcher( str ).matches();
    }
}