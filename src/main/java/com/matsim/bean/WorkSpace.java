package com.matsim.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.matsim.util.FileUtil;
import com.matsim.util.PolygonShapeUtil;
import com.matsim.util.TimeFormatUtil;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.ActivityImpl;
import org.matsim.core.population.PlanImpl;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacility;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by MingLu on 2018/4/11.
 */
public class WorkSpace implements Serializable {
    private Logger log = Logger.getLogger("WorkSpace.class");

    private Block[] blocks;
    private String name = "";
    private Integer id;

    public void setBlocks(Block[] blocks) {
        this.blocks = blocks;
    }

    public WorkSpace(Block[] blocks) {
        this.blocks = blocks;
    }
    public WorkSpace(){

    }

    public List<List<Block>> getActivityRelatedBlocks(){
        List<List<Block>> actRelatedBlocks = new ArrayList<>(  );
        List<Block> tempActToMatsimBlocks = new ArrayList<>(  );
        Block matsimBlock = this.getMatsimBlock();
        if(matsimBlock!=null){
            Connection[] connects = matsimBlock.getConnects();
            for (Connection  con:connects
                 ) { // this loop finds out activity or facility block connected to matsi block
                if(!con.getFrom().isEmpty()) {
                    Block fromBlock = getBlockById( con.getFrom() );
                    if (fromBlock.getType().equalsIgnoreCase( "activity" )||
                            fromBlock.getType().equalsIgnoreCase( "facility" )){
                        tempActToMatsimBlocks.add( fromBlock );
                    }
                }
            }
        }


        for (Block actBlock:tempActToMatsimBlocks
             ) { // this loop track back block connected to activity or facility block
            List<Block> tempActBlocks = new ArrayList<>(  );
            Block fromBlock = actBlock;
            tempActBlocks.add( actBlock );
            while(hasFromBlock(fromBlock.getConnects())){
                fromBlock = getFromBlock( fromBlock.getConnects() );
                tempActBlocks.add( fromBlock );
            }
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>"+tempActBlocks.toString()+">>>>>>>>>>>>>>>>>>>>>>>>>");
            Collections.reverse(tempActBlocks);
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>"+tempActBlocks.toString()+">>>>>>>>>>>>>>>>>>>>>>>>>");
            actRelatedBlocks.add( tempActBlocks );
        }

        return actRelatedBlocks;
    }

    public List<Person> actList2Plan(List<Block> actBlocks, Block regionBlock, HttpSession session) throws Exception{
        // actBlocks are generated from getActivityRelatedBlocks(), so if it is a valid act chain,
        // it should be reversed first, and the first element should be PersonBlock
        Config config = ConfigUtils.createConfig();
        Scenario sc = ScenarioUtils.createScenario(config);
        Population population = sc.getPopulation();
        PopulationFactory populationFactory = population.getFactory();
        List<Person> persons = new ArrayList<>(  );

        // home based activity chain always start from home or person block
        if(actBlocks.get( 0 ).getType().equalsIgnoreCase( "person" ) ) {

            Block personBlock = actBlocks.get( 0 );
            int numOfPerson = personBlock.getPersonNum();

            for (int i = 0; i < numOfPerson; i++) {

                PlanImpl tempPlan = new PlanImpl();
                Person tempPerson = populationFactory.createPerson( Id.createPersonId( personBlock.getPersonId() + "_" + i ) );
                Coord homeCoord = null;
                if (personBlock.getPersonLocationType().equalsIgnoreCase( "regionId" )) {
                    if (regionBlock == null) {
                        break;
                    } else {
                        //TODO get region file here there must be an easy way.
                        String regionFile = FileUtil.getUserRegionShpFile( session.getAttribute( "userName" ).toString(), this.name, regionBlock );
                        try {
                            //todo this may cause problems and cannot trace back to, need to check again
                            System.out.println(regionBlock.getRegionId()+">>>>>>>>>>>>>>>>>>>>>>>."+personBlock.getPersonLocationRegion());
                            PolygonShapeUtil psu = new PolygonShapeUtil( regionFile, regionBlock.getRegionId() );
                            homeCoord = psu.genCoordFromShapFile( 1, personBlock.getPersonLocationRegion() )[0];

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else if (personBlock.getPersonLocationType().equalsIgnoreCase( "homeCoord" )) {
                    String personLocation[] = personBlock.getPersonLocationLocation().split( "," );
                    homeCoord = new Coord( Double.parseDouble( personLocation[0] ), Double.parseDouble( personLocation[1] ) );
                }
                Activity startActivity = populationFactory.createActivityFromCoord( "home", homeCoord );
                String startTime = personBlock.getPersonDepTime();
                startActivity.setEndTime( TimeFormatUtil.hhmmss2Seconds( startTime ) );
                double accTime =0;
                accTime += TimeFormatUtil.hhmmss2Seconds( startTime );
                tempPlan.addActivity( startActivity );


                // get activity or facility and add to the plan
                // find out activity index
                List<Integer> actIndex = new ArrayList<>(  );
                actIndex.add( 0 ); //
                for (int j = 0; j < actBlocks.size() ; j++) {
                    if(actBlocks.get( j ).getType().equalsIgnoreCase( "activity" )||
                            actBlocks.get( j ).getType().equalsIgnoreCase( "facility" )){
                        actIndex.add( j );
                    }
                }
                int actNum = actIndex.size()-1; // this does not account for home activity
                String returnHomeMode = "car";
                for (int j = 0; j < actNum; j++) {
//            get act type (trip purpose block)
                    String actType = "work";
                    Block tripPurposeBlock = getSpecifedBlockInRange( actBlocks,"tripPurpose", actIndex.get( j ),actIndex.get( j+1 ));
                    if(tripPurposeBlock!=null){
                        if (tripPurposeBlock.getTripPurpose().equalsIgnoreCase( "userDefined" )){
                            actType = tripPurposeBlock.getOtherPurpose();
                        }else{
                            actType = tripPurposeBlock.getTripPurpose();
                        }
                    }
                    // mode from work to


//            get act mode
                    String tempMode = "car";
                    Block modeBlock = getSpecifedBlockInRange( actBlocks,"mode", actIndex.get( j ),actIndex.get( j+1 ));
                    if(modeBlock!=null){
                        if (modeBlock.getMode().equalsIgnoreCase( "userDefined" )){
                            tempMode = modeBlock.getOtherMode();
                        }else{
                            tempMode = modeBlock.getMode();
                        }
                    }
                    if(j==0){
                        returnHomeMode = tempMode;
                    }
//            get act endTime
                    double endTime = 3600*8 + 3600*j;
                    double durTime = 3600;
                    accTime += durTime;
                    Block timerBlock = getSpecifedBlockInRange( actBlocks,"timer", actIndex.get( j ),actIndex.get( j+1 ));
                    if(timerBlock!=null){
                        endTime = TimeFormatUtil.hhmmss2Seconds( timerBlock.getEndTime() );
                        durTime = TimeFormatUtil.hhmmss2Seconds( timerBlock.getDurTime() );
                    }else{
                        endTime= accTime;
                    }
//            get act location
                    Coord actCoord = null;
                    ActivityImpl tempAct = new ActivityImpl(actType,actCoord);
                    Block actBlock = actBlocks.get( actIndex.get( j+1 ) );
                    if(actBlock.getType().equalsIgnoreCase( "activity" )){
                        if (actBlock.getActDestinationType().equalsIgnoreCase( "location" )){
                            String actLoctionXys[] = actBlock.getActLocation().split( "," );
                            actCoord = new Coord(Double.parseDouble( actLoctionXys[0] ),Double.parseDouble( actLoctionXys[1] ));
                        }else if(actBlock.getActDestinationType().equalsIgnoreCase( "regionId" )){
                            String regionFile = FileUtil.getUserRegionShpFile( session.getAttribute( "userName" ).toString(),this.name,regionBlock);
                            System.out.println(regionFile);
                            actCoord = new PolygonShapeUtil(regionFile,regionBlock.getRegionId()).genCoordFromShapFile( 1,//                            String regionFile = FileUtil.getUserRegionShpFile( session.getAttribute( "userName" ).toString(),workSpace.getSaveName(),regionBlock);
                                    actBlock.getActRegionId() )[0];
                        }
                    }else if(actBlock.getType().equalsIgnoreCase( "facility")){
                        String coord[] = actBlock.getFacilityCoord().split( "," );
                        actCoord = new Coord( Double.parseDouble(coord[0]),Double.parseDouble( coord[1] ));
                        actType = actBlock.getActType();
                        tempAct.setFacilityId(Id.create( actBlock.getFacilityId(), ActivityFacility.class ) );
                    }
                    tempPlan.addLeg( populationFactory.createLeg( tempMode ) );
                    System.out.println(tempMode+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    tempAct.setEndTime( endTime );
                    tempAct.setType( actType );
                    tempAct.setStartTime( endTime - durTime );
                    tempAct.setCoord( actCoord );
                    tempPlan.addActivity(tempAct);
//            plan.addLeg(populationFactory.createLeg("car"));
                }
                // add return home activ
                tempPlan.addLeg( populationFactory.createLeg(returnHomeMode) );
                Activity endActivity = populationFactory.createActivityFromCoord( "home", homeCoord );
                tempPlan.addActivity( endActivity );

                tempPerson.addPlan( tempPlan );
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<< "+tempPerson.toString()+"<<<<<<<<<<<<<<<<<<<<<<< ");
                persons.add( tempPerson );
//                population.addPerson(tempPerson);

            }
        }
//        Activity endActivity = populationFactory.createActivityFromCoord( "home", homeCoord );
//        tempPlan.addActivity( endActivity );

        return persons;
    }

    public List<Person> odChain2Plan(List<OdChain> odChains, Block regionBlock, HttpSession session) throws Exception{
        Config config = ConfigUtils.createConfig();
        Scenario sc = ScenarioUtils.createScenario(config);
        Population population = sc.getPopulation();
        PopulationFactory populationFactory = population.getFactory();
        List<Person> persons = new ArrayList<>(  );

        String regionFile = FileUtil.getUserRegionShpFile( session.getAttribute( "userName" ).toString(),this.name,regionBlock);
        PolygonShapeUtil polygonShapeUtil = new PolygonShapeUtil(regionFile,regionBlock.getRegionId());

        for(OdChain odChain:odChains){
            PlanImpl tempPlan = new PlanImpl();
            Person tempPerson = populationFactory.createPerson( Id.createPersonId( odChain.getId()) );
            // add home
            Coord homeCoord = null;
            if (odChain.getOrigin().getOriRegionId()!=""||!odChain.getOrigin().getOriRegionId().isEmpty()){
                homeCoord = polygonShapeUtil.genCoordsInPolygon(
                        1,polygonShapeUtil.regions.get( odChain.getOrigin().getOriRegionId() ))[0];
            }else if(odChain.getOrigin().getOriginCoordX()!=0&&odChain.getOrigin().getOriginCoordY()!=0){
                homeCoord = new Coord( odChain.getOrigin().getOriginCoordX(),odChain.getOrigin().getOriginCoordY() );
            }
            Activity startActivity = populationFactory.createActivityFromCoord( "home",homeCoord );
            startActivity.setEndTime( odChain.getOrigin().getOriDepartureTime() );
            tempPlan.addActivity(startActivity);
            Destination[] destinations = odChain.getDestinations();
            String returnHomeMode = "car";
            for (int i = 0; i < destinations.length; i++) {
                // add leg between
                String mode = destinations[i].getDesiredMode();
                if (i==0){returnHomeMode = mode;}
                tempPlan.addLeg( populationFactory.createLeg( mode ) );
                Coord tempCoord = null;
                if(!destinations[i].getDesRegionId().isEmpty()||destinations[i].getDesRegionId()!=""){
                    tempCoord = polygonShapeUtil.genCoordsInPolygon(
                            1,polygonShapeUtil.regions.get( destinations[i].getDesRegionId() ))[0];
                }else if(!destinations[i].getFacilityId().isEmpty()||destinations[i].getFacilityId()!=""){
                    //  todo need to read and parse facility file here to get the coord
                }else if(destinations[i].getDesCoordX()!=0&&destinations[i].getDesCoordY()!=0){
                    tempCoord = new Coord(destinations[i].getDesCoordX(),destinations[i].getDesCoordY());
                }
                String tripPurpose = destinations[i].getTripPurpose();
                double endTime = destinations[i].getDepartureTime();
                Activity tempActivity= populationFactory.createActivityFromCoord( tripPurpose,tempCoord  );
                tempActivity.setEndTime( endTime );
                tempPlan.addActivity( tempActivity );

            }
//            add return home activity, which is the same mode as the first journey
            tempPlan.addLeg( populationFactory.createLeg( returnHomeMode ) );
            Activity endActivity = populationFactory.createActivityFromCoord( "home",homeCoord  );
            tempPlan.addActivity( endActivity );
            tempPerson.addPlan( tempPlan );
            persons.add( tempPerson );
        }
        log.info( " ,<<<<<<<<<<<<<<<<<<"+persons.size()+ ", " );
        return persons;
    }


    /**
     * This method get specified type of block in specified range in a block array
     * @param actBlocks - Block[] start with person, and ends up with acitivity or facility block
     * @param start - start index, included
     * @param end - end index, included as well please make sure start and end index are in [0,blocks.length]
     * @param type - specified block, if multiple existing , only get the fisr
     * @return
     */
    public Block getSpecifedBlockInRange(List<Block> actBlocks,String type,int start,int end){
        for (int i = start; i <= end ; i++) {
            if(actBlocks.get( i ).getType().equalsIgnoreCase( type )){
                return actBlocks.get( i );
            }
        }
        return null;
    }

    public boolean hasFromBlock(Connection[] connections){
        boolean hasFromBlock = false;
        for (Connection con:connections
             ) {
            if(!con.getFrom().isEmpty()){
                return true;
            }
        }
        return hasFromBlock;
    }
    public boolean hasToBlock(Connection[] connections){
        boolean hasToBlock = false;
        for (Connection con:connections
                ) {
            if(!con.getTo().isEmpty()){
                return true;
            }
        }
        return hasToBlock;
    }

    /**
     * there should be only one from block for act related blocks.
     * Right now only matsim block are allowed to have multiple inputs.
     * @param connections
     * @return
     */
    public Block getFromBlock(Connection[] connections){
        for (Connection con:connections
                ) {
            if(!con.getFrom().isEmpty()){
                return getBlockById( con.getFrom() );
            }
        }
        return null;
    }

    /**
     * there should be only one from block for act related blocks.
     * Right now only matsim block are allowed to have multiple inputs.
     * @param connections
     * @return
     */
    public Block getToBlock(Connection[] connections){

        for (Connection con:connections
                ) {
            if(!con.getTo().isEmpty()){
                System.out.println(con.getTo());
                return getBlockById( con.getTo() );
            }
        }
        return null;
    }

    public  Block getBlockById(String id) {
        Block tempBlock = null;
        for (Block block : blocks
                ) {
            if (block.getId().equalsIgnoreCase(id)) {
                tempBlock =  block;
            }
        }
        return tempBlock;
    }

    public  Block getMatsimXmlsBlock() {
        Block tempBlock = null;
        for (Block block : blocks) {
            if (block.getType().contains("matsimXMLs")) {
                tempBlock =  block;
            }
        }
        return tempBlock;
    }
    public  Block getNetworkBlock() {
        Block tempBlock = null;
        for (Block block : blocks) {
            if (block.getType().equalsIgnoreCase("network")) {
                tempBlock =  block;
            }
        }
        return tempBlock;
    }



    public  Block getOpenStreetBlock() {
        Block tempBlock = null;
        for (Block block : blocks) {
            if (block.getType().equalsIgnoreCase("openStreet")) {
                tempBlock =  block;
            }
        }
        return tempBlock;
    }

    public  Block getRegionBlock() {
        Block tempBlock = null;
        for (Block block : blocks   ) {
            if (block.getType().equalsIgnoreCase("region")) {
                tempBlock =  block;
            }
        }
        return tempBlock;
    }

    public  Block getMatsimBlock() {
        Block tempBlock = null;
        for (Block block : blocks
                ) {
            if (block.getType().equalsIgnoreCase("matsim")) {
                tempBlock =  block;
            }
        }
        return tempBlock;
    }


    public List<Block> getBlocksByType(String id) {
        List<Block> typeBlocks = new ArrayList<>();
        for (Block block : blocks
                ) {
            if (block.getType().equalsIgnoreCase(id)) {
                typeBlocks.add(block);
            }
        }
        return typeBlocks;
    }
    public  String getBlockTypeById(String id) {
        String blockType = null;
        for (Block block : blocks
                ) {
            if (block.getId().equalsIgnoreCase(id)) {
                blockType = block.getType();
            }
        }
        return blockType;
    }

    public Block[] getBlocks() {
        return blocks;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "{'workspace':{'name':'" + name + "'" +
                ", 'id':'" + id +"'" +
                ",'blocks':" + Arrays.toString(blocks) +
                "}}";
    }

    /**
     * this method is just for those blocks, which only allows one block to connect to
     * @param blockId
     * @return
     */
    public Block getFromBlock(String blockId){
        Connection[] tempConnects = getBlockById( blockId ).getConnects();
        for (Connection connect:tempConnects
             ) {
            if(!connect.getFrom().isEmpty()){
                return getBlockById( connect.getFrom() );
            }
        }
        return null;
    }


    public static WorkSpace getEntity(String resp){
//        {"workspace":{"name":"shpNetwork1", "id":"91","blocks":[{"type":"matsim","id":"1531204385867","position":{"id":"", "top":"142", "left":"598", "zIndex":"1"},"status":"1","connects":[{"id":"1531213083345", "from":"1531213082162", "to":""}],"iteration":"1","hasBusScheduleXml":false,"hasVehicleXml":false,"hasFacilityXml":false,"hasConfigXml":false,"configXml":""}, {"type":"network","id":"1531213082162","position":{"id":"", "top":"169", "left":"233", "zIndex":"2"},"status":"1","connects":[{"id":"1531213083345", "from":"", "to":"1531204385867"}, {"id":"1531388253200", "from":"", "to":"1531388252446"}],"networkDefaultEPSG":"EPSG：4326","networkDesiredEPSG":"EPSG：4326","linkDir":"DIR","linkLane":"AB_LANES","linkSpeed":"speedLimit","linkLength":"linkLength","linkCapacity":"AB_CAPACIT","linkMode":"mode","networkShpFile":"sz.shp","networkShxFile":"sz.shx","networkDbfFile":"sz.dbf"}, {"type":"networkDisplay","id":"1531388252446","position":{"id":"", "top":"255", "left":"549", "zIndex":"3"},"status":"2","connects":[{"id":"1531388253200", "from":"1531213082162", "to":""}]}, {"type":"networkDisplay","id":"1531393944024","position":{"id":"", "top":"412", "left":"573", "zIndex":"4"},"status":"2","connects":[]}]}}

        JSONObject jsonObj = (JSONObject) JSON.parse(resp);
        JSONObject wsObj = jsonObj.getJSONObject( "workspace" );
        WorkSpace workSpace = new WorkSpace();
        workSpace.setName( wsObj.getString("name") );
        workSpace.setId(wsObj.getInteger("id"));
        JSONArray blocksJa = wsObj.getJSONArray( "blocks" );
        Block[] blocks = new Block[blocksJa.size()];
        for (int i = 0; i < blocksJa.size(); i++) {
            Block tempBlock = JSONObject.toJavaObject(blocksJa.getJSONObject( i ),Block.class);
            blocks[i] = tempBlock;
        }
        workSpace.setBlocks( blocks );
        return workSpace;
    }

}
