package com.matsim.mapper;

import com.matsim.bean.WorkSpace;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by MingLU on 2018/4/20,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public interface WorkspaceMapper {

    @Insert("inset into saved_workspace values(   #{workSpace.blocks})")
    public int insertWorkspace(WorkSpace workSpace);

}
