package com.matsim.user;
import java.util.List;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * Created by MingLU on 2018/5/22,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public interface SaveAndLoadMapper {
    @Options(useGeneratedKeys=true, keyProperty="saveId",keyColumn = "save_id")
    int save(@Param("saveAndLoad") SaveAndLoad saveAndLoad);

    int update(@Param("saveAndLoad") SaveAndLoad saveAndLoad);

    int delete(@Param("saveAndLoad") SaveAndLoad saveAndLoad);

    List<SaveAndLoad> hasSaveName(@Param("saveAndLoad") SaveAndLoad saveAndLoad);

    List<SaveAndLoad> loadBySaveId(@Param("saveId") int saveId);

    List<SaveAndLoad> loadByUserIdAndSaveName(@Param("saveAndLoad") SaveAndLoad saveAndLoad);

    List<SaveAndLoad> displayAllSavesByUserId(@Param("userId") int userId);


}
