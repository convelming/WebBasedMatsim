package com.matsim.util;

import com.matsim.bean.PersonActivity;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.population.PopulationUtils;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by MingLU on 2018/4/30,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class PlanXmlUtil {
    public Set<String> actTypes;
    public Population population;
    public Set<String> modes;
    public Set<Id<Person>> personIds;
    private int selectedPlanIndex = -1;

    public static void main(String[] args) {
        PlanXmlUtil planXmlUtil = new PlanXmlUtil("/Users/convel/Desktop/newTest/a/arrayCoord/matsimXml/plans/plans.xml");
        System.out.println(planXmlUtil.population.getPersons().size());
        System.out.println(planXmlUtil.getPersons().size());
//        Id personId = Id.createPersonId( "1001" );
//        planXmlUtil.getPlansByPersonId( personId );
//        System.out.println("select plan is "+ planXmlUtil.selectedPlanIndex);
//        PersonActivity plan = planXmlUtil.getSelectPlanActByPersonId( personId );
//
//        seconds2Time( 43263 );
    }

    public List<Person> getPersons() {
        List<Person> personList = new ArrayList<>();
        for (Map.Entry person : population.getPersons().entrySet()) {
            personList.add((Person) person.getValue());
        }
        return personList;
    }

    public PersonActivity getSelectPlanActByPersonId(Id<Person> personId) {

        PersonActivity personActivity = new PersonActivity();
        Map<Id<Person>, Person> persons = (Map<Id<Person>, Person>) population.getPersons();
        Person person = persons.get(personId);
        List<PlanElement> planElements = person.getSelectedPlan().getPlanElements();

        List<Double> actXs = new ArrayList<>();
        List<Double> actYs = new ArrayList<>();
        List<String> actInfo = new ArrayList<>();

        for (PlanElement planElement : planElements) {
            if (planElement instanceof Activity act) {
                actXs.add(act.getCoord().getX());
                actYs.add(act.getCoord().getY());
                if (act.getEndTime().seconds() > 0 && act.getEndTime().seconds() <= (23 * 3600 + 60 * 59 + 60 * 60)) {
                    actInfo.add(act.getType() + " until " + seconds2Time(act.getEndTime().seconds()));
                    System.out.println(act.getType() + " until " + seconds2Time(act.getEndTime().seconds()) + "," + act.getEndTime());
                }
            }
        }
        System.out.println(actInfo.size());
        //if first and last are the same the last are the same, delete the last one info
        if ((actXs.get(actXs.size() - 1) - actXs.get(0)) < 0.0000001 && (actYs.get(actYs.size() - 1) - actYs.get(0)) < 0.0000001) {
            actXs.set(actXs.size() - 1, (actXs.get(0) + 0.00001));
            actYs.set(actYs.size() - 1, (actYs.get(0) + 0.00001));
        }
        System.out.println((actXs.get(actXs.size() - 1) - actXs.get(0)) + ", " + actXs.get(actXs.size() - 1) + 0.00001);
        System.out.println(actInfo.size());
        personActivity.setActivityInfo(actInfo);
        personActivity.setActXs(actXs);
        personActivity.setActYs(actYs);
        personActivity.setDefaultContent("PersonId: " + personId);
        personActivity.setCenterX(personActivity.calAvgList(actXs));
        personActivity.setCenterY(personActivity.calAvgList(actYs));

        return personActivity;
    }

    /**
     * Time in Matsim are recorded most of the time in seconds starting from 00:00:00
     * This method turns it back to
     *
     * @param seconds
     * @return
     */
    public static String seconds2Time(double seconds) {
        DecimalFormat df = new DecimalFormat("00");

        int hh = (int) (seconds / 3600);
        int mm = (int) (seconds % 3600) / 60;
        int ss = (int) (seconds % 3600) % 60;
        String hhmmss = "";
        if (hh > 12) {
            hhmmss = df.format(Integer.parseInt((hh - 12) + "")) + ":" + df.format(Integer.parseInt(mm + "")) + ":" + df.format(Integer.parseInt(ss + "")) + " pm";
        } else if (hh == 12) {
            hhmmss = df.format(Integer.parseInt(hh + "")) + ":" + df.format(Integer.parseInt(mm + "")) + ":" + df.format(Integer.parseInt(ss + "")) + " pm";
        } else {
            hhmmss = df.format(Integer.parseInt(hh + "")) + ":" + df.format(Integer.parseInt(mm + "")) + ":" + df.format(Integer.parseInt(ss + "")) + " am";
        }
        System.out.println(hhmmss);
        return hhmmss;
    }

    public List<? extends Plan> getPlansByPersonId(Id<Person> personId) {
        Map<Id<Person>, ? extends Person> persons = population.getPersons();
        Person person = persons.get(personId);
        Plan selectPlan = person.getSelectedPlan();
        System.out.println(selectPlan);
        List<? extends Plan> plans = person.getPlans();
        for (int i = 0; i < plans.size(); i++) {
            if (plans.get(i).equals(selectPlan)) {
                selectedPlanIndex = i;
            }
        }
        return plans;
    }

    public Plan getSelectPlansByPersonId(Id<Person> personId) {
        Map<Id<Person>, Person> persons = (Map<Id<Person>, Person>) population.getPersons();
        Person person = persons.get(personId);
        return person.getSelectedPlan();
    }

    public Person getPersonByPersonId(Id<Person> personId) {
        Map<Id<Person>, Person> persons = (Map<Id<Person>, Person>) population.getPersons();
        Person person = persons.get(personId);
        return person;
    }

    public PlanXmlUtil(String popOrPlanFile) {
        this.population = PopulationUtils.readPopulation(popOrPlanFile);
    }


    public static Set<String> getActTypes(Population population) {
        Set<String> actTypes = new HashSet<>();
        Map<Id<Person>, ? extends Person> persons = population.getPersons();
        for (Person p : persons.values()) {
            List<? extends Plan> plans = p.getPlans();
            for (Plan plan : plans) {
                List<PlanElement> pes = plan.getPlanElements();
                for (PlanElement pe : pes) {
                    if (pe instanceof Activity ai) {
                        actTypes.add(ai.getType());
//                        System.out.println(ai.getType()+ " is the activity type");
                    }

                }
            }
        }
        return actTypes;
    }

    public static Set<String> getmodes(Population population) {
        Set<String> modes = new HashSet<>();
        Map<Id<Person>, ? extends Person> persons = population.getPersons();
        for (Person p : persons.values()) {
            List<? extends Plan> plans = p.getPlans();
            for (Plan plan : plans) {
                List<PlanElement> pes = plan.getPlanElements();
                for (PlanElement pe : pes) {
                    if (pe instanceof Leg li) {
                        modes.add(li.getMode());
//                        System.out.println(li.getMode()+ " is the leg mode");
                    }
                }
            }
        }
        return modes;
    }

    public Set<Id<Person>> getPersonIds() {
        personIds = new HashSet<>();
        Map<Id<Person>, ? extends Person> persons = population.getPersons();
        for (Person person : persons.values()) {
            personIds.add(person.getId());
        }
        return personIds;
    }
}
