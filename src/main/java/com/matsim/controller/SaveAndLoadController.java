package com.matsim.controller;

/**
 * Created by MingLU on 2018/5/22,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */

import com.matsim.bean.Result;
import com.matsim.bean.SaveBean;
import com.matsim.bean.WorkSpace;
import com.matsim.user.SaveAndLoad;
import com.matsim.user.SaveAndLoadMapper;
import com.matsim.user.User;
import com.matsim.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("saveAndLoad")
public class SaveAndLoadController {
    private Logger log = Logger.getLogger("SaveAndLoadController.class");

    @Autowired
    private SaveAndLoadMapper saveAndLoadMapper;

    @RequestMapping("/save")
    public @ResponseBody Result saveWorkspace(@RequestBody WorkSpace workSpace, HttpSession session){
        Result result = new Result();// save this json-format data in database
        // create save folders and move temp files to specified folders

        FileUtil fileUtil = new FileUtil();
        // create folders
        String saveFolder = FileUtil.userFilePath+session.getAttribute( "userName" )+"/"+workSpace.getName();
        System.out.println("getSaveName"+workSpace.getName());
        fileUtil.createSaveFolders( saveFolder );
        String tempFolder = FileUtil.userFilePath+session.getAttribute( "userName" )+"/temp";
//        String tempFolder = "src/main/resources/static/temp/users/"+session.getAttribute( "userName" )+"/temp";
        fileUtil.moveFiles2SpecifiedFolders(tempFolder,saveFolder,workSpace);
        session.setAttribute( "saveName",workSpace.getName() );
        SaveAndLoad saveAndLoad = new SaveAndLoad();
        saveAndLoad.setSaveName(workSpace.getName());
        saveAndLoad.setUserId((Integer)session.getAttribute("userId"));
        saveAndLoad.setSaveTime(new Timestamp(System.currentTimeMillis()));
        saveAndLoad.setSaveContent(workSpace.toString().replace( "'","\"" ));//.replace( "c:\\fakepath",""));

        List<SaveAndLoad> hasSaveName = saveAndLoadMapper.hasSaveName(saveAndLoad);
        if(hasSaveName.size()==1
                &&hasSaveName.get(0).getSaveName().equals(workSpace.getName())){
            saveAndLoadMapper.update(saveAndLoad);
            result.setInfo("另存成功！");
        }else {
            saveAndLoadMapper.save(saveAndLoad);
            result.setInfo("保存成功！");
        }
        result.setSuccess(true);
        result.setData(saveAndLoad);
//        result.setSuccess(false);
        return result;
    }

    @RequestMapping("/loadBySaveId")
    public @ResponseBody Result load(@RequestParam("saveId")Integer saveId) {
        Result result = new Result();
        List<SaveAndLoad> saveAndLoads = saveAndLoadMapper.loadBySaveId(saveId);
        if (saveAndLoads.size()==1){
            result.setSuccess(true);
            result.setData(saveAndLoads);
        }else{
            //todo update user login time
            result.setSuccess(false);
            result.setErrMsg("Something wrong with the saved document...");
        }
        return result;

    }

    @RequestMapping("/loadAll")
    public @ResponseBody Result loadAllSaves(HttpSession session) {
        Result result = new Result();
        System.out.println(session.getAttribute("userName")+", user id is:"+session.getAttribute("userId"));
//        System.out.println(session.getAttribute("userId")+" blablabla...");
//        System.out.println(allSaves.size());
        if(saveAndLoadMapper.displayAllSavesByUserId((Integer) session.getAttribute("userId")).isEmpty()){
            result.setSuccess(false);
            result.setErrMsg("There is no saved workspace, please build something first...");
        }else {
            result.setSuccess(true);
            result.setData(saveAndLoadMapper.displayAllSavesByUserId((Integer) session.getAttribute("userId")));
        }

        return result;

    }


    @RequestMapping("/loadExample")
    public @ResponseBody Result loadExample(HttpSession session) {
        Result result = new Result();

        System.out.println(session.getAttribute("userName")+", user id is:"+session.getAttribute("userId"));
//        System.out.println(session.getAttribute("userId")+" blablabla...");
//        System.out.println(allSaves.size());

        if(saveAndLoadMapper.displayAllSavesByUserId((Integer) session.getAttribute("userId")).isEmpty()){
            result.setSuccess(false);
            result.setErrMsg("There is no saved workspace, please build something first...");
        }else {
            result.setSuccess(true);
            result.setData(saveAndLoadMapper.displayAllSavesByUserId((Integer) session.getAttribute("userId")));
        }

        return result;

    }

    @RequestMapping("/deleteSaveName")
    public @ResponseBody Result deleteBySaveName(@RequestBody SaveBean saveBean,HttpSession session){
        Result result = new Result();
        System.out.println(saveBean.getSaveName()+ " klsdafksadkflksajfkl      askljfklsjklfjklsajklfjslka");
        SaveAndLoad saveAndLoad = new SaveAndLoad();
        saveAndLoad.setSaveName( saveBean.getSaveName() );
        saveAndLoad.setUserId( (Integer)session.getAttribute("userId") );
        saveAndLoadMapper.delete( saveAndLoad );
        result.setSuccess( true );
        System.out.println(result.isSuccess());
        return result;
    }

    @RequestMapping("/getSaveNameFolder")
    public @ResponseBody Result getSaveNameFolder(@RequestBody SaveBean saveBean,HttpSession session){
        Result result = new Result();
        System.out.println("jsklafjklsadjfkljaskljfklasjfkljklj12 139");
        System.out.println(saveBean.getSaveName());
        String test = "{\"networkFile\":\"testNetwork\"," +
                        "\"configFile\":\"testConfig\"," +
                        "\"planFile\":\"testNetwor21432k\"," +
                        "\"outputFile\":\"null\"" +
                      "}";
        result.setSuccess( true );
        result.setData( test );
        System.out.println(result.isSuccess());
        return result;
    }
}
