package com.matsim.controller;

import com.matsim.bean.Block;
import com.matsim.bean.OdChain;
import com.matsim.bean.Result;
import com.matsim.bean.WorkSpace;
import com.matsim.util.*;
import jakarta.servlet.http.HttpSession;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.ReplanningConfigGroup;
import org.matsim.core.config.groups.RoutingConfigGroup;
import org.matsim.core.config.groups.ScoringConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by MingLU on 2018/4/6,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */


@RestController
@CrossOrigin
@RequestMapping("/workspace")
public class WorkSpaceController {
    private Logger log = Logger.getLogger("WorkSpaceController.class");

    /**
     * This method parses all the blocks, generate config.xml and then runs matsim
     * - Parsing blocks, parsing in order of matsimXMLs,network,region,odmatrix blocks, activity blocks
     * - matsimXMLs   first check settings in matsim blcok, then
     * check which files are uploaded, including network.xml,initialPlan.xml,facility.xml,busSchedule.xml,vehicle.xml etc
     * - network   check if networkfile is set up by shp block or openstreet block
     * - region  parse shp file, get map<regionId,multipolygon>
     * - odMatrix blocks  get all related blocks
     * - activity blocks  get all related blocks
     * <p>
     * - Writing initial plans file
     * - generate person IDs according to matsimXML-initialPlan, odMatrix blocks and activity blocks
     * - setting config file
     * - set iteration
     * - set network file path, initialPlan.xml,facility.xml,busSchedule.xml,vehicle.xml path if has any
     * - run scenario
     */
    private Result result = new Result();

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public Result runWorkSpace(@RequestBody WorkSpace workSpace, HttpSession session) throws Exception {
        log.info("start to run ...");
        Config config = ConfigUtils.createConfig();
        String userFolder = FileUtil.userFilePath + session.getAttribute("userName") + "/" + workSpace.getName();//session.getAttribute("userName").toString();

//    - Parsing blocks, parsing in order of matsimXMLs,network,region,odmatrix blocks, activity blocks
//          - matsimXMLs   first check settings in matsim blcok, then
//             check which files are uploaded, including network.xml,initialPlan.xml,facility.xml,busSchedule.xml,vehicle.xml etc

        Block xmlBlock = workSpace.getMatsimXmlsBlock();
        Block matsimBlock = workSpace.getMatsimBlock();
        Block openStreetBlock = workSpace.getOpenStreetBlock();
        Block networkBlock = workSpace.getNetworkBlock();
        Block regionBlock = workSpace.getRegionBlock();
        List<Block> odMatrixBlocks = workSpace.getBlocksByType("odMatrix");
        List<List<Block>> actChains = workSpace.getActivityRelatedBlocks();
        String networkFile = "";
        String planXml = userFolder + "/matsimXml/plans/plans.xml";
        String configFile = "";
        Controler controler = null;
        /**
         * Parsing and organising workspace is a pain in the ass, if there are some unexpected block joining together, it will be hard to
         * organise. Anyway, parsing starts from matsim block, according to the options received, check if the file exists and if it is valid.
         * This is done by method checkMatsimBlocks(), and it returns possible combinations of blocks each combination is Block list  .
         * possible combinations: [MatsimXmls]  with at least network.xml and plan.xml
         *                        [MatsimXmls] with plan.xml,etc [openStreet] network.xml
         *                        [MatsimXmls] with plan.xml,etc [network] with network.shp
         *
         *                        [odMatrix] with odArray and asCoord, [MatsimXmls] with network
         *                        [odMatrix] with odArray and asCoord, [openStreet] network.xml
         *                        [odMatrix] with odArray and asCoord, [network] with network.shp
         *                        [odMatrix] with odArray and asRegion, [network] with network.shp, [region] with region.shp
         *                        [odMatrix] with odMatrix            , [network] with network.shp, [region] with region.shp
         *
         *
         *  other blocks are not necessary blocks which means they can be left out, for example if mode is not specify for [odMatrix] block,
         *  default mode will be used (in this case, car).
         *  also note if region id is specified in blocks, then [region] block must be used; if facility Id is used, facility file must be
         *  specifed in the [matsimXmls] block
         *
         *  person block is an individual chain, which is not allowed to join [matrix] blocks, only end ups to [matsim]
         *
         *
         *
         */

//          - network   check if networkfile is set up by shp block or openstreet block
        String networkFolder = userFolder + "/matsimXml/network/";
        if (xmlBlock == null && !FileUtil.hasXmlFile(networkFolder)) { // if network is not specified in matsimXmls block

            if (openStreetBlock != null && !FileUtil.hasXmlFile(networkFolder)) {
                log.info("Get network from openstreetMap ...");
                //todo userName path needs to be specified here
                OpenstreetMapUtil open = new OpenstreetMapUtil();
                String url = open.getMap(openStreetBlock.getMinLong(), openStreetBlock.getMinLati(),
                        openStreetBlock.getMaxLong(), openStreetBlock.getMaxLati(), networkFolder
                );
                networkFile = open.osm2matsimXml(url);
            } else if (networkBlock != null && !FileUtil.hasXmlFile(networkFolder)) {
                ShpNetwork2XmlUtil shpNetwork2XmlUtil = new ShpNetwork2XmlUtil();
                String shpFile = userFolder + "/shpFile/network/"
                        + networkBlock.getNetworkShpFile();
                log.info("Get network from networkBlock");
                Result tempResult = shpNetwork2XmlUtil.shp2MastimNetwork(networkBlock, shpFile);
                FileUtil tempFileUtil = new FileUtil();
                networkFile = networkFolder
                        + networkBlock.getNetworkShpFile().replace(".shp", ".xml");
                tempFileUtil.moveFile(shpFile.replace(".shp", ".xml"), networkFile);

            }

        } else if (xmlBlock != null && xmlBlock.getNetworkXml() != null && !FileUtil.hasXmlFile(networkFolder)) {
            networkFile = networkFolder + xmlBlock.getNetworkXml();
            log.info("Get network from xmlBlcok.getNetworkXMl()");
        } else {
            result.setSuccess(false);
            result.setErrMsg("no network file found");
        }
//        // get region file
//        if (regionBlock==null){
//            if(xmlBlock==null||(xmlBlock.getActivityXml()==null)) {
//                result.setErrMsg( "There is no region shp files found!" );
//            }
//        }else{
//            File temp = new File(regionBlock.getRegionShpFile().replace("\\", "/"));
//            String regionShpFile =  userFolder+"/region/"+temp.getName();
//
//        }
//          - region  parse shp file, get map<regionId,multipolygon>

//          - odMatrix blocks  get all related blocks
//          - activity blocks  get all related blocks


        // get person and plans from csv files
        List<Person> allPersonsFromOdMatrix = new ArrayList<>();
        String regionShpFolder = userFolder + "/region";
        Boolean hasRegionFile = FileUtil.hasExtFile(regionShpFolder, ".shp") && FileUtil.hasExtFile(regionShpFolder, ".shx") && FileUtil.hasExtFile(regionShpFolder, ".dbf");
        if (odMatrixBlocks.size() >= 1) {
            for (int i = 0; i < odMatrixBlocks.size(); i++) {
                Block odMatrix = odMatrixBlocks.get(i);
                String tempOdFile = "";
                List<OdChain> odChains = new ArrayList<>();
                if (odMatrix.getOdFile().endsWith(".csv") || odMatrix.getOdFile().endsWith(".txt")) {
                    if (odMatrix.getOdFileType().equalsIgnoreCase("squareMatrix")) {
                        tempOdFile = userFolder + "/csvTxtFile/matrix/" + odMatrixBlocks.get(i).getOdFile();
                    } else if (odMatrix.getOdFileType().equalsIgnoreCase("array")) {
                        tempOdFile = userFolder + "/csvTxtFile/array/" + odMatrixBlocks.get(i).getOdFile();
                    }
                    odChains = ODMatrixUtil.getOdDataFromCsv(new File(tempOdFile), odMatrix.getOdFileType(), odMatrix.getOdAsRegionOrCoord());
                }

                if (hasRegionFile) {
                    allPersonsFromOdMatrix.addAll(workSpace.odChain2Plan(odChains, regionBlock, session));
                } else {
                    result.setSuccess(false);
                    result.setInfo("Required region shp file but not found!");
                }
            }
        }

        Scenario sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        Population population = sc.getPopulation();

        // get plans from activity related blocks
        for (Person person : allPersonsFromOdMatrix) {
            population.addPerson(person);
        }

        List<Person> actChainPersons = null;
        for (List<Block> actChain : actChains) {
            actChainPersons = workSpace.actList2Plan(actChain, regionBlock, session);
            for (Person person : actChainPersons) {
                population.addPerson(person);
            }
        }
        result.setInfo(population.getPersons().size() + " persons");
        PlanXmlUtil planXmlUtil = null;
        // if there is also a plan or activity .xml file in the matsimXml block
        MatsimWriter popWriter = new PopulationWriter(population, null);
        if (xmlBlock != null && xmlBlock.getActivityXml() != null) {
            planXmlUtil = new PlanXmlUtil(userFolder + "/matsimXml/plans/" + xmlBlock.getActivityXml());
            for (Person person : planXmlUtil.getPersons()) {
                population.addPerson(person);
            }
        }
        popWriter.write(planXml);

        // get all possible modes
        // get modes from od matrix and xmls
        Set<String> allActTypes = new HashSet<>();
        Set<String> allModes = new HashSet<>();
        if (!population.getPersons().isEmpty()) {
            allModes = PlanXmlUtil.getmodes(population);
            // get modes from blocks
            allModes.addAll(new ModeUtil(workSpace.getBlocks()).getModes());
            config.changeMode().setModes(ModeUtil.set2Array(allModes));
            // get all activity types(tripPurpose)
            allActTypes.addAll(PlanXmlUtil.getActTypes(population));
            allActTypes.addAll(new ActivityUtil(workSpace.getBlocks()).getActivities());
        }
        result.setInfo(population.getPersons().size() + " persons");
        // organize config files
        // if a config file is provided, then all input directories needs to be changed
        // input directories include: config file, network file, plan file , bus schedule, vehicle, facility and output folder

        // get matsim block inputs
        int iteration = 1;
        String busScheduleFile = null;
        String busVehicleFile = null;
        String vehicleFile = null;
        String facilityFile = null;
        if (matsimBlock != null) {
            iteration = matsimBlock.getIteration();
            if (matsimBlock.isHasConfigXml()) {
                File tempConfigFile = new File(matsimBlock.getConfigXml().replace("\\", "/"));
                configFile = userFolder + "/matsimXml/config/" + tempConfigFile;
                controler = new Controler(ConfigUtils.loadConfig(configFile));
            } else { // if config file is not specified  create one
                configFile = userFolder + "/matsimXml/config.xml";
                //todo
//                for (int i = 0; i < allActTypes.size(); i++) {
//                    config.planCalcScore().addParameterSet(  ).(  );
//                    ConfigGroup
//                }
//                config.strategy();
                (new ConfigWriter(config)).write(configFile);
            }
            if (matsimBlock.isHasBusScheduleXml() && xmlBlock != null && xmlBlock.getBusScheduleXml() != null) {
                busScheduleFile = userFolder + "/matsimXml/busSchedule/" + xmlBlock.getBusScheduleXml().replace("\\", "/");
                config.transit().setTransitScheduleFile(busScheduleFile);
                config.transit().setUseTransit(true);
                Set<String> transitModes = new HashSet<>();
                transitModes.add("pt");// by default is pt
                if (allModes.contains("bus")) {
                    transitModes.add("bus");
                }
                if (allModes.contains("subway")) {
                    transitModes.add("subway");
                }
                config.transit().setTransitModes(transitModes);
            }
            if (matsimBlock.isHasVehicleXml()) { // pt vehicle ?
                busVehicleFile = userFolder + "/matsimXml/busSchedule/" + xmlBlock.getVehicleXml().replace("\\", "/");
                config.transit().setVehiclesFile(busVehicleFile);
//                vehicleFile = userFolder + "/matsimXml/busSchedule/" + xmlBlock.getVehicleXml().replace("\\", "/");
//                config.vehicles().setVehiclesFile(vehicleFile);
            }

            if (matsimBlock.isHasFacilityXml()) {
                facilityFile = userFolder + "/matsimXml/facility/" + xmlBlock.getFacilityXml().replace("\\", "/");
                config.facilities().setInputFile(facilityFile);
            }
        }

        // set up plancalcScore
        if (matsimBlock.isHasConfigXml()) {
            config = ConfigUtils.loadConfig(configFile);
        } else {
            config.scoring().addParam("learningRate", "0.5");
            config.scoring().addParam("brainExpBeta", "2.0");
            config.scoring().addParam("lateArrival", "-18");
            config.scoring().addParam("earlyDeparture", "-0");
            config.scoring().addParam("performing", "+6");
            config.scoring().addParam("traveling", "-6");
            config.scoring().addParam("waiting", "-0");
            int iAct = 0;
            for (String act : allActTypes) {
                if (act.equalsIgnoreCase("home")) {
                    ScoringConfigGroup.ActivityParams home = new ScoringConfigGroup.ActivityParams();
                    home.addParam("activityType", act);
                    home.addParam("priority", "1");
                    home.addParam("typicalDuration", "12:00:00");
                    home.addParam("minimalDuration", "08:00:00");
                    config.scoring().addActivityParams(home);
                } else if (act.equalsIgnoreCase("work")) {
                    ScoringConfigGroup.ActivityParams work = new ScoringConfigGroup.ActivityParams();
                    work.addParam("activityType", act);
                    work.addParam("priority", "1");
                    work.addParam("typicalDuration", "08:00:00");
                    work.addParam("minimalDuration", "07:00:00");
                    config.scoring().addActivityParams(work);
                } else {
                    ScoringConfigGroup.ActivityParams other = new ScoringConfigGroup.ActivityParams();
                    other.addParam("activityType", act);
                    other.addParam("priority", "1");
                    other.addParam("typicalDuration", "02:00:00");
                    other.addParam("minimalDuration", "01:00:00");
                    config.scoring().addActivityParams(other);
                }
            }

            // set up strategy
            config.replanning().addParam("maxAgentPlanMemorySize", "5");
            ConfigGroup moduleProbability_1 = new ReplanningConfigGroup.StrategySettings();
            moduleProbability_1.addParam("strategyName", "ChangeTripMode");
            moduleProbability_1.addParam("weight", "0.9");
            config.replanning().addParameterSet(moduleProbability_1);
            ConfigGroup moduleProbability_2 = new ReplanningConfigGroup.StrategySettings();
            moduleProbability_2.addParam("strategyName", "ReRoute");
            moduleProbability_2.addParam("weight", "0.1");
            config.replanning().addParameterSet(moduleProbability_2);

            config.routing().setAccessEgressType(RoutingConfigGroup.AccessEgressType.accessEgressModeToLink);

            //               config.strategy().addParam("ModuleProbability_1", "0.9");
//            config.strategy().addParam("Module_1", "BestScore");
//            config.strategy().addParam("ModuleProbability_2", "0.1");
//            config.strategy().addParam("Module_2", "ReRoute");
        }

        config.controller().setLastIteration(iteration);
        config.network().setInputFile(networkFile);
        config.plans().setInputFile(userFolder + "/matsimXml/plans/plans.xml");
        config.controller().setOutputDirectory(userFolder + "/output");
        config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

        // 移除pt interaction
//        config.scoring().removeParameterSet()

        //
        config.vspExperimental().addParam("isAbleToOverwritePtInteractionParams", "true");
        config.global().setCoordinateSystem("EPSG:3857");
        config.qsim().setEndTime(3600 * 25); // 25:00:00
//        ConfigUtils.writeConfig(config, configFile);
        // 如果使用config.xml
        if (matsimBlock.isHasConfigXml()) {
            config = ConfigUtils.loadConfig(userFolder + "/matsimXml/config/" + matsimBlock.getConfigXml());
            config.controller().setOutputDirectory(userFolder + "/output");
            config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
            // 重新設置輸入文件目錄
            config.plans().setInputFile(userFolder + "/matsimXml/plans/plans.xml");
            config.network().setInputFile(userFolder + "/matsimXml/network/" + xmlBlock.getNetworkXml());
            if (matsimBlock.isHasBusScheduleXml()) {
                busScheduleFile = userFolder + "/matsimXml/busSchedule/" + xmlBlock.getBusScheduleXml().replace("\\", "/");
                config.transit().setTransitScheduleFile(busScheduleFile);
            }else{
                config.transit().setTransitScheduleFile(null);
            }
            if (matsimBlock.isHasVehicleXml()) {
                busVehicleFile = userFolder + "/matsimXml/busSchedule/" + xmlBlock.getVehicleXml().replace("\\", "/");
                config.transit().setVehiclesFile(busVehicleFile);
            }else{
                config.transit().setVehiclesFile(null);
            }
            if (matsimBlock.isHasFacilityXml()) {
                facilityFile = userFolder + "/matsimXml/facility/" + xmlBlock.getFacilityXml().replace("\\", "/");
                config.facilities().setInputFile(facilityFile);
            }else {
                config.facilities().setInputFile(null);
            }
        }
        controler = new Controler(ScenarioUtils.loadScenario(config));
//       result.setInfo( "MATSim is running. Depending on the size of your scenario , it may take from several minutes to a couple of hours..." +
//                " please wait patiently... " );


        System.out.println("MATSim is running...");
        controler.run();

        return result;
    }

    private static List<Block> getkMatsimBlockCombs(Block[] blocks) {
//        Block[]  blockCombs =  new ArrayList<>(  );
//        blocks
//        return blockCombs;
        return null;
    }

    public static String getUUID32() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return uuid;
//  return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
