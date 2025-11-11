package com.matsim.controller;

/**
 * Created by MingLu on 2018/4/24.
 * This utility class
 */

import com.matsim.bean.Result;
import com.matsim.util.FileUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Scope("prototype")
@RestController
@RequestMapping("/upload")
public class UploadFileController {

    private Logger log = LoggerFactory.getLogger(UploadFileController.class);

    // for od files
    @RequestMapping(value = "/odMatrix", method = RequestMethod.POST)
    public Result upLoadOdFile(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {".txt", ".csv", ".xls", ".xlsx"};
        return this.uploadFile(fileBtn, session, "odFile", fileExtensions);
    }

    // for network .shp file
    @RequestMapping(value = "/networkShpFile", method = RequestMethod.POST)
    public Result upLoadNetworkShpFile(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {".shp", "SHP"};
        return this.uploadFile(fileBtn, session, "shpFile/network", fileExtensions);
    }

    @RequestMapping(value = "/networkShxFile", method = RequestMethod.POST)
    public Result upLoadNetworkShxFile(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {".shx", "SHX"};
        return this.uploadFile(fileBtn, session, "shpFile/network", fileExtensions);
    }

    @RequestMapping(value = "/networkDbfFile", method = RequestMethod.POST)
    public Result upLoadNetworkDbfFile(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {".dbf", "DBF"};
        return this.uploadFile(fileBtn, session, "shpFile/network", fileExtensions);
    }

    // for region .shp files
    @RequestMapping(value = "/regionShp", method = RequestMethod.POST)
    public Result upLoadRegionShpFile(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {"shp", "SHP"};
        return this.uploadFile(fileBtn, session, "shpFile/region", fileExtensions);
    }

    @RequestMapping(value = "/regionShx", method = RequestMethod.POST)
    public Result upLoadRegionShxFile(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {"shx", "SHX"};
        return this.uploadFile(fileBtn, session, "shpFile/region", fileExtensions);
    }

    @RequestMapping(value = "/regionDbf", method = RequestMethod.POST)
    public Result upLoadRegionDbfFile(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {"dbf", "DBF"};
        return this.uploadFile(fileBtn, session, "shpFile/region", fileExtensions);
    }

    // for matsim xml files
    @RequestMapping(value = "/mastimNetworkXml", method = RequestMethod.POST)
    public Result upLoadMastimNetworkXml(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {"xml", "xml.gz"};
        return this.uploadFile(fileBtn, session, "/matsimXml/network", fileExtensions);
    }

    @RequestMapping(value = "/mastimActivityXml", method = RequestMethod.POST)
    public Result upLoadMastimActivityXml(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {"xml", "xml.gz"};
        return this.uploadFile(fileBtn, session, "matsimXml/plans", fileExtensions);
    }

    @RequestMapping(value = "/mastimBusScheduleXml", method = RequestMethod.POST)
    public Result upLoadMastimBusScheduleXml(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {"xml", "xml.gz"};
        return this.uploadFile(fileBtn, session, "matsimXml/busSchedule", fileExtensions);
    }

    @RequestMapping(value = "/mastimVehicleXml", method = RequestMethod.POST)
    public Result upLoadMastimVehicleXml(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {"xml", "xml.gz"};
        return this.uploadFile(fileBtn, session, "matsimXml/busSchedule", fileExtensions);
    }

    @RequestMapping(value = "/mastimFacilityXml", method = RequestMethod.POST)
    public Result upLoadMastimFacilityXml(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {"xml", "xml.gz"};
        return this.uploadFile(fileBtn, session, "matsimXml/facility", fileExtensions);
    }

    @RequestMapping(value = "/mastimConfigXml", method = RequestMethod.POST)
    public Result upLoadMastimConfigXml(MultipartFile fileBtn, HttpSession session) {
        String[] fileExtensions = {"xml", "xml.gz"};
        return this.uploadFile(fileBtn, session, "matsimXml/config", fileExtensions);
    }

    public Result uploadFile(MultipartFile file, HttpSession session, String folder, String[] fileExtensions) {
        Result result = new Result();
        // 获得文件：
//        MultipartFile file = fileBtn.getFile("fileBtn");
//        MultipartFile file = fileBtn.getFile("fileBtn");
        if(file == null) {
            result.setSuccess(false);
            result.setInfo("请上传正确的文件...");
            return result;
        }

        // 获得文件名：
        String fileName = file.getOriginalFilename();
        // check if file  extensions are in the list
        boolean checkFileExt = false;
//        System.out.println(fileExtensions.length);
        for (String fileExt : fileExtensions) {
//            System.out.println("line109 "+ fileName+" "+fileName.endsWith(fileExt));
            checkFileExt = checkFileExt || fileName.endsWith(fileExt);
        }
//        System.out.println(checkFileExt);
        if (!checkFileExt) {
            result.setSuccess(false);
            result.setInfo("请上传正确的文件...");
            log.error(result.getErrMsg());
            return result;
        }

        // set save path and outputstream
//		String userNamePath = "/" + session.getAttribute("userName").toString()+"/"+folder+"/"+filename;
//		FileOutputStream fos = new FileOutputStream(new File(userNamePath));
        FileOutputStream fos;
        try {
//		    File tempFile = new File("/Users/convel/Desktop/"+user+"/"+folder);
            //todo have to change path settings after user control system is done
            File tempFile = new File(FileUtil.userFilePath + session.getAttribute("userName").toString() + "/temp/" + folder);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
                System.out.println(tempFile.getAbsolutePath() + " upload file ");
            }
            File destFile = new File(tempFile + "/" + fileName);
            if (destFile.exists()) {
                result.setSuccess(false);
                result.setErrMsg("There are files uploaded with same names, to avoid confusion, please rename and then upload again...");
            }
            fos = new FileOutputStream(destFile);

            log.info(destFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrMsg("FileNotFoundException");
            return result;
        }


        // 获得输入流：
        try {
            InputStream fs = file.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fs.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            fs.close();
            result.setSuccess(true);
            result.setInfo("上传成功！");
            result.setData(fileName);
//            System.out.println(result.getInfo() + "," + result.isSuccess() + result.getData().toString());
        } catch (IOException e) {
//            e.printStackTrace();
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrMsg("IOException");
        }
        return result;
    }


}
