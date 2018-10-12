package com.matsim.test;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.api.internal.MatsimWriter;
import org.matsim.core.config.Config;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.config.ConfigUtils;
public class CreatePlans {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        /*
         * We enter coordinates in the WGS84 reference system, but we want them to appear in the population file
         * projected to UTM33N, because we also generated the network that way.
         */
//        CoordinateTransformation ct =
//                 TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, TransformationFactory.WGS84_UTM33N);

        /*
         * First, create a new Config and a new Scenario.
         */
        Config config = ConfigUtils.createConfig();
        Scenario sc = ScenarioUtils.createScenario(config);

        /*
         * Pick the Network and the Population out of the Scenario for convenience.
         */
        Network network = sc.getNetwork();
        Population population = sc.getPopulation();

        /*
         * Pick the PopulationFactory out of the Population for convenience.
         * It contains methods to create new Population items.
         */
        PopulationFactory populationFactory = population.getFactory();

        /*
         * Create a Person designated "1" and add it to the Population.
         */
//        write txt file for ods
        String testFile = "/Users/convel/Desktop/test/randampoints.csv";
        BufferedReader br = new BufferedReader( new FileReader( testFile ) );
        br.readLine();
        String line;
        List<Coord> randomOds = new ArrayList<>();
        while((line=br.readLine())!=null){
            String[] tempLine = line.split( "," );
            Coord tempCoord = new Coord(Double.parseDouble( tempLine[1] ),Double.parseDouble( tempLine[2] ));
            randomOds.add(tempCoord);
        }
        for (int i=0;i<50;i++){
            Person person = populationFactory.createPerson(Id.createPersonId(Integer.toString(i)));
            population.addPerson(person);
            /*
             * Create a Plan for the Person
             */
            Plan plan = populationFactory.createPlan();
            /*
             * Create a "home" Activity for the Person. In order to have the Person end its day at the same location,
             * we keep the home coordinates for later use (see below).
             * Note that we use the CoordinateTransformation created above.
             */
//	        bw1.write(i+",home: "+xHome+", "+yHome+",work: "+xWork+", "+yWork);
	        Coord homeCoord = randomOds.get( new Random().nextInt( randomOds.size()-1 ));

            Activity activity1 = populationFactory.createActivityFromCoord("home", homeCoord );
            int endTime = 3600 * 9;
            activity1.setEndTime(endTime); // leave at a o'clock
            plan.addActivity(activity1); // add the Activity to the Plan

            /*
             * Create a Leg. A Leg initially hasn't got many attributes. It just says that a car will be used.
             */

            plan.addLeg(populationFactory.createLeg("car"));


            /*
             * Create a "work" Activity, at a different location.
             */

            Activity activity2 = populationFactory.createActivityFromCoord("work", randomOds.get( new Random().nextInt( randomOds.size()-1 )));
            endTime +=  3600 * 8;
            activity2.setEndTime(endTime); // leave at 4 p.m.
            plan.addActivity(activity2);
            plan.addLeg(populationFactory.createLeg("car"));

           /*
             * End the day with another Activity at home. Note that it gets the same coordinates as the first activity.
             */
            Activity activityEnd = populationFactory.createActivityFromCoord("home",homeCoord);
            plan.addActivity(activityEnd);
            person.addPlan(plan);

            /*
             * Write the population (of 1 Person) to a file.
             */
//	        System.out.println("home,"+xHome+","+yHome+",work:,"+xWork+","+yWork);
//            bw1.write("LINESTRING("+xHome+" "+yHome+", "+xWork+" "+yWork+") \n");

        }
        MatsimWriter popWriter = new org.matsim.api.core.v01.population.PopulationWriter(population, network);

        popWriter.write("/Users/convel/Desktop/test/testPopulation50.xml");

    }

}
