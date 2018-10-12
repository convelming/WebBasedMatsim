package com.matsim.controller;

import com.matsim.bean.Result;
import com.matsim.bean.SaveBean;
import com.matsim.user.SaveAndLoadMapper;
import com.matsim.util.XmlNetwork2GeoJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * Created by MingLU on 2018/9/30,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */

@RestController
@RequestMapping("/svg")
public class SvgController {
    private Logger log = Logger.getLogger("SvgController.class");

    @RequestMapping("/loadNetwork")
    public @ResponseBody Result loadSplitedNetwork(SaveBean saveBean) {
        System.out.println("teskjfkldsajfkljdsalkfjslak;j"+saveBean.getSaveName());
        Result result = new Result();
        result.setData( XmlNetwork2GeoJson.getOnlyLineCoords("/Users/convel/Desktop/test/testMatsimXml/sz180603.xml") );
//        System.out.println(resulttoString());
        return result;
    }
}
