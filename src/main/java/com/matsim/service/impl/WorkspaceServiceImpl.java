package com.matsim.service.impl;

import com.matsim.bean.Result;
import com.matsim.bean.WorkSpace;
import com.matsim.mapper.WorkspaceMapper;
import com.matsim.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.WatchService;

/**
 * Created by MingLU on 2018/4/20,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
@Service
public class WorkspaceServiceImpl implements WorkspaceService{

    @Autowired
    WorkspaceMapper workspaceMapper;

    @Override
    public Result addWorkspace(WorkSpace workSpace) {
        Result result = new Result();
        try {
           int num= workspaceMapper.insertWorkspace(workSpace);
            result.setData(num);
            result.setSuccess(true);
        }catch (Exception e){
            result.setSuccess(false);
            result.setErrMsg("cha ru shi mbai");
        }
        return result;
    }
}
