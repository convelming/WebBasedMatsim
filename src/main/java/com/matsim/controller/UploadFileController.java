package com.matsim.controller;

/**
 * Created by MingLu on 2018/4/24.
 * This utility class
 */
import com.matsim.bean.Result;
import com.matsim.util.FileUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

@RestController
@CrossOrigin
@RequestMapping("/upload")
public class UploadFileController {
    private Logger log = Logger.getLogger("UploadFileController.class");

	// for od files
	@RequestMapping(value = "/odMatrix", method = RequestMethod.POST)
	public Result upLoadOdFile(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {".txt",".csv",".xls",".xlsx"};
        return this.uploadFile(multipartReq,session,"odFile",fileExtensions);
	}

	// for network .shp file
	@RequestMapping(value = "/networkShpFile", method = RequestMethod.POST)
	public Result upLoadNetworkShpFile(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {".shp","SHP"};
        return this.uploadFile(multipartReq,session,"shpFile/network",fileExtensions);
	}
	@RequestMapping(value = "/networkShxFile", method = RequestMethod.POST)
	public Result upLoadNetworkShxFile(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {".shx","SHX"};
        return this.uploadFile(multipartReq,session,"shpFile/network",fileExtensions);
	}
	@RequestMapping(value = "/networkDbfFile", method = RequestMethod.POST)
	public Result upLoadNetworkDbfFile(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {".dbf","DBF"};
        return this.uploadFile(multipartReq,session,"shpFile/network",fileExtensions);
	}

	// for region .shp files
    @RequestMapping(value = "/regionShp", method = RequestMethod.POST)
    public Result upLoadRegionShpFile(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {"shp","SHP"};
        return this.uploadFile(multipartReq,session,"shpFile/region",fileExtensions);
    }
    @RequestMapping(value = "/regionShx", method = RequestMethod.POST)
    public Result upLoadRegionShxFile(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {"shx","SHX"};
        return this.uploadFile(multipartReq,session,"shpFile/region",fileExtensions);
    }
    @RequestMapping(value = "/regionDbf", method = RequestMethod.POST)
    public Result upLoadRegionDbfFile(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {"dbf","DBF"};
        return this.uploadFile(multipartReq,session,"shpFile/region",fileExtensions);
    }
    // for matsim xml files
    @RequestMapping(value = "/mastimNetworkXml", method = RequestMethod.POST)
    public Result upLoadMastimNetworkXml(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {"xml","xml.gz"};
        return this.uploadFile(multipartReq,session,"/matsimXml/network",fileExtensions);
    }
    @RequestMapping(value = "/mastimActivityXml", method = RequestMethod.POST)
    public Result upLoadMastimActivityXml(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {"xml","xml.gz"};
        return this.uploadFile(multipartReq,session,"matsimXml/plans",fileExtensions);
    }
    @RequestMapping(value = "/mastimBusScheduleXml", method = RequestMethod.POST)
    public Result upLoadMastimBusScheduleXml(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {"xml","xml.gz"};
        return this.uploadFile(multipartReq,session,"matsimXml/busSchedule",fileExtensions);
    }
    @RequestMapping(value = "/mastimVehicleXml", method = RequestMethod.POST)
    public Result upLoadMastimVehicleXml(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {"xml","xml.gz"};
        return this.uploadFile(multipartReq,session,"matsimXml/busSchedule",fileExtensions);
    }
    @RequestMapping(value = "/mastimFacilityXml", method = RequestMethod.POST)
    public Result upLoadMastimFacilityXml(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {"xml","xml.gz"};
        return this.uploadFile(multipartReq,session,"matsimXml/facility",fileExtensions);
	}

    @RequestMapping(value = "/mastimConfigXml", method = RequestMethod.POST)
    public Result upLoadMastimConfigXml(MultipartHttpServletRequest multipartReq, HttpSession session) {
        String[] fileExtensions = {"xml","xml.gz"};
        return this.uploadFile(multipartReq,session,"matsimXml/config",fileExtensions);
    }

	public @ResponseBody Result uploadFile(MultipartHttpServletRequest multipartReq, HttpSession session,String folder,String[] fileExtensions){
		Result result = new Result();
		// 获得文件：
		MultipartFile file = multipartReq.getFile("fileBtn");
		// 获得文件名：
		String fileName = file.getOriginalFilename();
        System.out.println("fileName: "+fileName);
		// check if file  extensions are in the list
        boolean checkFileExt = false;
//        System.out.println(fileExtensions.length);
        for (String fileExt:fileExtensions) {
            System.out.println("originalFile: "+fileName+ " fileExt: "+ fileExt);
//            System.out.println("line109 "+ fileName+" "+fileName.endsWith(fileExt));
            checkFileExt = checkFileExt || fileName.endsWith(fileExt);
        }
//        System.out.println(checkFileExt);
        if (!checkFileExt){
            result.setSuccess(false);
            result.setInfo("请上传正确的文件...");
            System.out.println(result.getErrMsg());
            return result;
        }

		// set save path and outputstream
//		String userNamePath = "/" + session.getAttribute("userName").toString()+"/"+folder+"/"+filename;
//		FileOutputStream fos = new FileOutputStream(new File(userNamePath));
		FileOutputStream fos = null;
		try {
//		    File tempFile = new File("/Users/convel/Desktop/"+user+"/"+folder);
            //todo have to change path settings after user control system is done
            File tempFile = new File(FileUtil.userFilePath + session.getAttribute("userName").toString()+"/temp/"+folder);
		    if(!tempFile.exists()){
                tempFile.mkdirs();
                System.out.println(tempFile.getAbsolutePath()+" upload file ");
            }
            File destFile = new File(tempFile+"/" + fileName);
		    if(destFile.exists()){
		        result.setSuccess( false );
		        result.setErrMsg( "There are files uploaded with same names, to avoid confusion, please rename and then upload again..." );
            }
			fos = new FileOutputStream(destFile);

            log.info(destFile.getAbsolutePath() );

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setErrMsg("FileNotFoundException");
		}


		// 获得输入流：
		try {
			InputStream input = file.getInputStream();
			FileInputStream fs = (FileInputStream) multipartReq.getFile("fileBtn").getInputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fs.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			fs.close();
            result.setSuccess(true);
            result.setInfo( "上传成功！" );
            result.setData(fileName);
            System.out.println(result.getInfo()+","+result.isSuccess()+result.getData().toString());
		} catch (IOException e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setErrMsg("IOException");
		}
		return result;
	}


}
