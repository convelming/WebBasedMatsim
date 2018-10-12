package com.matsim.bean;

import com.matsim.util.FileUtil;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.NetworkImpl;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by MingLu on 2018/4/11.
 */
public class Test {

    public static void main(String[] args) {

        Config config = ConfigUtils.createConfig();

//                ?ConfigUtils.loadConfig(//("/Users/convel/Desktop/matsim-0.9.0/examples/pt-tutorial/0.config.xml""gridInput/gridConfig.xml");
        Controler controler = new Controler(config);
        config.network().setInputFile( "/Users/convel/Desktop/newTest/a/shpNetwork1/matsimXml/network/sz.xml" );
//        NetworkImpl net = (NetworkImpl)        ScenarioUtils.loadScenario( config ).getNetwork();
//        config.transit().setUseTransit( true );
//        System.out.println(net.getLinks().size());
//        new NetworkCleaner().run( net );
//        System.out.println(net.getLinks().size());
        config.controler().setOutputDirectory("/Users/convel/Desktop/output");
        config.controler().setLastIteration( 10 );
//        config.ps
        // set up plan utility
        config.planCalcScore().addParam("learningRate","0.5");
        config.planCalcScore().addParam("BrainExpBeta","2.0" );//

        config.planCalcScore().addParam("lateArrival","-18" );//
        config.planCalcScore().addParam("earlyDeparture","-0" );//
        config.planCalcScore().addParam("performing","+6" );//
        config.planCalcScore().addParam("traveling","-6" );//
        config.planCalcScore().addParam("waiting","-0" );//

        // set up acitvity
        config.planCalcScore().addParam( "activityType_0","home" );
        config.planCalcScore().addParam( "activityPriority_0","1");
        config.planCalcScore().addParam("activityTypicalDuration_0","12:00:00" );//
        config.planCalcScore().addParam("activityMinimalDuration_0","08:00:00" );//

        config.planCalcScore().addParam( "activityType_1","work" );
        config.planCalcScore().addParam( "activityPriority_1","1");
        config.planCalcScore().addParam("activityTypicalDuration_1","08:00:00" );//
        config.planCalcScore().addParam("activityMinimalDuration_1","06:00:00" );//

        config.planCalcScore().addParam( "activityType_2","entertainment" );
        config.planCalcScore().addParam( "activityPriority_2","1");
        config.planCalcScore().addParam("activityTypicalDuration_2","02:00:00" );//
        config.planCalcScore().addParam("activityMinimalDuration_2","01:00:00" );//

        config.planCalcScore().addParam( "activityType_3","sport" );
        config.planCalcScore().addParam( "activityPriority_3","1");
        config.planCalcScore().addParam("activityTypicalDuration_3","02:00:00" );//
        config.planCalcScore().addParam("activityMinimalDuration_3","01:00:00" );//

        config.qsim().setEndTime( 23.99*3600 );
//        config.planCalcScore().addParam( "activityClosingTime_4","23:59:59");

        // setup strategy
        config.strategy().addParam("maxAgentPlanMemorySize","5");
        config.strategy().addParam("ModuleProbability_1","0.9");
        config.strategy().addParam("Module_1","BestScore");

        config.strategy().addParam("ModuleProbability_2","0.1");
        config.strategy().addParam("Module_2","ReRoute");
        Set<String> transitModes = new HashSet<>(  );
//        config.transit().setUseTransit( true );
//        transitModes.add( "bus" );
        config.transit().setTransitModes( transitModes );
//        String[] modes = {"bus","car"};
//        config.changeLegMode().setModes( modes );

        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles);
        config.plans().setInputFile( "/Users/convel/Desktop/newTest/a/xmlArrayRegionArray/matsimXml/plans/plans.xml" );
//        config.transit().setVehiclesFile( "" );
//        config.transit().setTransitScheduleFile( "" );
        controler.run();


    }

}
