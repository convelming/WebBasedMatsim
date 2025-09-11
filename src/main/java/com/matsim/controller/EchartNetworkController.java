package com.matsim.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.matsim.bean.*;
import com.matsim.user.SaveAndLoad;
import com.matsim.user.SaveAndLoadMapper;
import com.matsim.util.FileUtil;
import com.matsim.util.OpenstreetMapUtil;
import com.matsim.util.ShpNetwork2XmlUtil;
import com.matsim.util.XmlNetwork2Json;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by MingLU on 2018/6/13,
 * Life is short, so get your fat ass moving and chase your damn dream.
 * <p>
 * This controller first cut network into small pieces randomly, and then
 */

@RestController
@CrossOrigin
@RequestMapping("/static/display")
public class EchartNetworkController {
    private Logger log = Logger.getLogger("EchartNetworkController.class");

    @Autowired
    private SaveAndLoadMapper saveAndLoadMapper;

    @RequestMapping("/echartsNetwork")
    public @ResponseBody Result cutNetworks(@RequestBody SaveBean saveBean, HttpSession session) throws Exception {
        Result result = new Result();
        System.out.println(saveBean.getSaveName() + "," + saveBean.getOtherInfo());

        SaveAndLoad saveAndLoad = new SaveAndLoad();
        saveAndLoad.setUserId((Integer) session.getAttribute("userId"));
        saveAndLoad.setSaveName(saveBean.getSaveName());
        System.out.println("saveName: " + saveAndLoad.getSaveName());
        System.out.println("userId: " + saveAndLoad.getUserId());

        List<SaveAndLoad> tempSaveAndLoads = saveAndLoadMapper.loadByUserIdAndSaveName(saveAndLoad);
        WorkSpace workSpace = new WorkSpace();
        if (tempSaveAndLoads.size() == 1) {
            System.out.println(saveAndLoadMapper.loadByUserIdAndSaveName(saveAndLoad).get(0).getSaveContent());
            JSONObject jo = JSON.parseObject(saveAndLoadMapper.loadByUserIdAndSaveName(saveAndLoad).get(0).getSaveContent());
            workSpace = WorkSpace.getEntity(saveAndLoadMapper.loadByUserIdAndSaveName(saveAndLoad).get(0).getSaveContent());
        }

        Block block = workSpace.getFromBlock(saveBean.getOtherInfo());
        XmlNetwork2Json xmlNetwork2Json = new XmlNetwork2Json();
        EchartNetworkBean echartNetworkBean = null;
        String preFile = FileUtil.userFilePath + session.getAttribute("userName") + "/" + saveBean.getSaveName();
        String jsonFile = FileUtil.userFilePath + session.getAttribute("userName") + "/" + saveBean.getSaveName() + "/matsimXml/network/subNetworkJsons/";
        String webJsonFolder = FileUtil.userHtmlFilePath + session.getAttribute("userName") + "/" + saveBean.getSaveName() + "/matsimXml/network/subNetworkJsons/";
//        String jsonFile = FileUtil.userHtmlFilePath + session.getAttribute( "userName" ) + "/" + saveBean.getSaveName() + "/matsimXml/network/subNetworkJsons/";


        log.info(FileUtil.hasExtFile(jsonFile, ".json") + "");
        String xmlNetwork = null;
        if (block.getType().equalsIgnoreCase("matsimXMLs")) {
            xmlNetwork = preFile + "/matsimXml/network/" + block.getNetworkXml();
        } else if (block.getType().equalsIgnoreCase("network")) {
            String networkFolder = preFile + "/matsimXml/network/";
            if (FileUtil.hasXmlFile(networkFolder)) {
                xmlNetwork = FileUtil.getXmlFileInFolder(networkFolder);
            } else {
                ShpNetwork2XmlUtil shpNetwork2XmlUtil = new ShpNetwork2XmlUtil();
                String shpFile = preFile + "/shpFile/network/" + block.getNetworkShpFile();
                log.info(shpFile);
                shpNetwork2XmlUtil.shp2MastimNetwork(block, shpFile);
                FileUtil tempFileUtil = new FileUtil();
                xmlNetwork = preFile + "/matsimXml/network/" + block.getNetworkShpFile().replace(".shp", ".xml");
                tempFileUtil.moveFile(shpFile.replace(".shp", ".xml"), xmlNetwork);
            }
        } else if (block.getType().equalsIgnoreCase("openStreet")) {
            String networkFolder = preFile + "/matsimXml/network/";
            if (FileUtil.hasXmlFile(networkFolder)) {
                xmlNetwork = FileUtil.getXmlFileInFolder(networkFolder);
            } else {
                OpenstreetMapUtil open = new OpenstreetMapUtil();
                String url = open.getMap(block.getMinLong(), block.getMinLati(), block.getMaxLong(), block.getMaxLati(), networkFolder);
                xmlNetwork = open.osm2matsimXml(url);
            }

        } else if (block.getType().equalsIgnoreCase("region")) {
            // todo
        }
        echartNetworkBean = xmlNetwork2Json.xmlNetwork2SubJsonFiles(xmlNetwork, 500, jsonFile, webJsonFolder);

        result.setData(echartNetworkBean);
        return result;
    }


}
