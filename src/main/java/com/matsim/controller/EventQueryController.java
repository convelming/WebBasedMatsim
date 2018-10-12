package com.matsim.controller;

import com.matsim.bean.PersonActivity;
import com.matsim.bean.Result;
import com.matsim.bean.SaveBean;
import com.matsim.util.PlanXmlUtil;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by MingLU on 2018/6/23,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */

@RestController
@CrossOrigin
@RequestMapping("/static/display")
public class EventQueryController {


    @RequestMapping("/eventQuery")

    public Result cutNetworks(@RequestBody SaveBean saveBean, HttpSession session)  {
        Result result = new Result();
        Id<Person> personId = Id.createPersonId(saveBean.getOtherInfo());
        PlanXmlUtil planXmlUtil = new PlanXmlUtil("/Users/convel/Desktop/test/testMatsimXml/output/output_plans.xml");
        PersonActivity personActivity = planXmlUtil.getSelectPlanActByPersonId(personId);
        System.out.println(personActivity.getActivityInfo().size());
        result.setData( personActivity );
        return result;
    }
}
