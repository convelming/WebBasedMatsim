package com.matsim.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.matsim.bean.Block;
import com.matsim.bean.Result;
import com.matsim.bean.SaveBean;
import com.matsim.bean.WorkSpace;
import com.matsim.user.SaveAndLoad;
import com.matsim.user.SaveAndLoadMapper;
import com.matsim.util.FileUtil;
import com.matsim.util.ODMatrixUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by MingLU on 2018/6/27,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
@RestController
@CrossOrigin
@RequestMapping("/odFile")
public class TableController {
    private Logger log = Logger.getLogger("TableController.class");

    @Autowired
    private SaveAndLoadMapper saveAndLoadMapper;

    @RequestMapping("/loadOriginalTable")
    public @ResponseBody Result getOdMarix(@RequestBody SaveBean saveBean, HttpSession session) throws Exception {
        Result result = new Result();
        StringBuffer data = null;
        WorkSpace workSpace = getWorkSpace(saveBean, session);
        saveBean.setOtherInfo(workSpace.getBlocksByType("editTable").get(0).getId());
        Block block = workSpace.getFromBlock(saveBean.getOtherInfo());
        String preFile = FileUtil.userFilePath + session.getAttribute("userName") + "/" + saveBean.getSaveName();
        if (block.getType().equalsIgnoreCase("odMatrix")) {
            String odFile = "";
            if (block.getOdFileType().equalsIgnoreCase("squareMatrix")) {
                odFile = preFile + "/csvTxtFile/matrix/" + block.getOdFile();
            }
            if (block.getOdFileType().equalsIgnoreCase("array")) {
                odFile = preFile + "/csvTxtFile/array/" + block.getOdFile();
            }
            data = new ODMatrixUtil().getOriginalDataFromArrayCsv(odFile);
        }
        result.setData(data.toString());
        return result;

    }

    @RequestMapping("/validateTable")
    public @ResponseBody Result validateArrayData(@RequestBody SaveBean saveBean, HttpSession session) throws Exception {
        Result result = new Result();

        StringBuffer data = null;
        WorkSpace workSpace = getWorkSpace(saveBean, session);
        saveBean.setOtherInfo(workSpace.getBlocksByType("editTable").get(0).getId());
        Block block = workSpace.getFromBlock(saveBean.getOtherInfo());
        String preFile = FileUtil.userFilePath + session.getAttribute("userName") + "/" + saveBean.getSaveName();
        if (block.getType().equalsIgnoreCase("odMatrix")) {
            String odFile = "";
            if (block.getOdFileType().equalsIgnoreCase("squareMatrix")) {
                odFile = preFile + "/csvTxtFile/matrix/" + block.getOdFile();
            }
            if (block.getOdFileType().equalsIgnoreCase("array")) {
                odFile = preFile + "/csvTxtFile/array/" + block.getOdFile();
            }
            data = new ODMatrixUtil().validateOriginalDataFromArrayCsv(odFile);
        }
        result.setData(data.toString());
        return result;

    }

    public WorkSpace getWorkSpace(SaveBean saveBean, HttpSession session) {
        WorkSpace workSpace = new WorkSpace();
        SaveAndLoad saveAndLoad = new SaveAndLoad();
        saveAndLoad.setUserId((Integer) session.getAttribute("userId"));
        saveAndLoad.setSaveName(saveBean.getSaveName());
        List<SaveAndLoad> tempSaveAndLoads = saveAndLoadMapper.loadByUserIdAndSaveName(saveAndLoad);
        if (tempSaveAndLoads.size() == 1) {
            System.out.println(saveAndLoadMapper.loadByUserIdAndSaveName(saveAndLoad).get(0).getSaveContent());
            JSONObject jsonObj = (JSONObject) JSON.parse(saveAndLoadMapper.loadByUserIdAndSaveName(saveAndLoad).get(0).getSaveContent());
            JSONObject workspaceJson = jsonObj.getJSONObject("workspace");
            workSpace = JSONObject.toJavaObject(workspaceJson, WorkSpace.class);
        }
        return workSpace;
    }
}
