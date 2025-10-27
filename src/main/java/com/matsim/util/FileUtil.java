package com.matsim.util;

import com.matsim.bean.Block;
import com.matsim.bean.WorkSpace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by MingLU on 2018/6/8,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
@Component
public class FileUtil {

    private Logger log = Logger.getLogger("FileUtil.class");


    //    public static final String userFilePath = "src/main/resources/static/userData/"; //"/Users/convel/Desktop/newTest/
    public static String userFilePath; //"/Users/convel/Desktop/newTest/

    public static String userHtmlFilePath;// for html files

    public FileUtil() {
    }

    //
    @Value("${user.file.path}")
    public void setUserFilePath(String userFilePath) {
        FileUtil.userFilePath = userFilePath;
    }

    @Value("${user.file.htmlPath}")
    public void setUserFileHtmlPath(String userHtmlFilePath) {
        FileUtil.userHtmlFilePath = userHtmlFilePath;
    }


    /**
     * 新建目录
     *
     * @param folderPath String  如  c:/fqf
     * @return boolean
     */
    public void newFolder(String folderPath) {
        try {
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.mkdirs();
            }
        } catch (Exception e) {
            System.out.println("新建目录操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 新建文件
     *
     * @param filePathAndName String  文件路径及名称  如c:/fqf.txt
     * @param fileContent     String  文件内容
     * @return boolean
     */
    public void newFile(String filePathAndName, String fileContent) {

        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();  //取的路径及文件名
            File myFilePath = new File(filePath);
            /**如果文件不存在就建一个新文件*/
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            FileWriter resultFile = new FileWriter(myFilePath);  //用来写入字符文件的便捷类, 在给出 File 对象的情况下构造一个 FileWriter 对象
            PrintWriter myFile = new PrintWriter(resultFile);  //向文本输出流打印对象的格式化表示形式,使用指定文件创建不具有自动行刷新的新 PrintWriter。
            String strContent = fileContent;
            myFile.println(strContent);
            resultFile.close();

        } catch (Exception e) {
            System.out.println("新建文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 删除文件
     *
     * @param filePathAndName String  文件路径及名称  如c:/fqf.txt
     * @return boolean
     */
    public void delFile(String filePathAndName) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            java.io.File myDelFile = new java.io.File(filePath);
            myDelFile.delete();

        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 删除文件夹
     *
     * @param folderPath String  文件夹路径及名称  如c:/fqf
     * @return boolean
     */
    public void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);  //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete();  //删除空文件夹

        } catch (Exception e) {
            System.out.println("删除文件夹操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path String  文件夹路径  如  c:/fqf
     */
    public void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String  原文件路径  如：c:/fqf.txt
     * @param newPath String  复制后路径  如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
//           int  bytesum  =  0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
                newFile.createNewFile();
            }
            if (oldfile.exists()) {  //文件存在时
                InputStream inStream = new FileInputStream(oldPath);  //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
//               int  length;
                while ((byteread = inStream.read(buffer)) != -1) {
//                   bytesum  +=  byteread;  //字节数  文件大小
//                   System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String  原文件路径  如：c:/fqf
     * @param newPath String  复制后路径  如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs();  //如果文件夹不存在  则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath String  如：c:/fqf.txt
     * @param newPath String  如：d:/fqf.txt
     */
    public void moveFile(String oldPath, String newPath) {
        File newFile = new File(newPath);
        System.out.println(oldPath + " <<<old, >>> new " + newPath);
        if (newFile.exists()) {
            System.out.println("File already exsists");
            System.out.println();
            delFile(newPath);
            copyFile(oldPath, newPath);
//            delFile( oldPath );
        } else {
            if (newFile.getParentFile().isFile()) {
                delFolder(newFile.getParentFile().getAbsolutePath());
            }
            copyFile(oldPath, newPath);
//            delFile( oldPath );
        }
    }

    /**
     * 移动文件到指定目录
     *
     * @param oldPath String  如：c:/fqf.txt
     * @param newPath String  如：d:/fqf.txt
     */
    public void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);

    }


    // 拷贝文件
    private void copyFile2(String source, String dest) {
        try {
            File in = new File(source);
            File out = new File(dest);
            FileInputStream inFile = new FileInputStream(in);
            FileOutputStream outFile = new FileOutputStream(out);
            byte[] buffer = new byte[10240];
            int i = 0;
            while ((i = inFile.read(buffer)) != -1) {
                outFile.write(buffer, 0, i);
            }//end while
            inFile.close();
            outFile.close();
        }//end try
        catch (Exception e) {

        }//end catch
    }//end copyFile

    /**
     * This method create user folders
     * /userFolderTemplate
     * ├── saveName
     * │   ├── GTFS
     * │   ├── csvTxtFile
     * │   │   ├── array
     * │   │   └── matrix
     * │   ├── excel
     * │   │   ├── array
     * │   │   └── matrix
     * │   ├── matsimXml
     * │   │   ├── busSchedule
     * │   │   ├── config
     * │   │   ├── facility
     * │   │   ├── network
     * │   │   └── plans
     * │   ├── others
     * │   ├── output
     * │   └── shpFile
     * │       ├── network
     * │       └── region
     * └── temp // this folder is same heirachy as the save name
     *
     * @param path - user folder
     */
    public void createSaveFolders(String path) {
        this.newFolder(path);
        this.newFolder(path);
        this.newFolder(path + "/GTFS");
        this.newFolder(path + "/csvTxtFile");
        System.out.println(path + "/csvTxtFile");
        this.newFolder(path + "/csvTxtFile/array");
        this.newFolder(path + "/csvTxtFile/matrix");
        this.newFolder(path + "/excel");
        this.newFolder(path + "/excel/array");
        this.newFolder(path + "/excel/matrix");
        this.newFolder(path + "/matsimXml");
        this.newFolder(path + "/matsimXml/busSchedule");
        this.newFolder(path + "/matsimXml/config");
        this.newFolder(path + "/matsimXml/facility");
        this.newFolder(path + "/matsimXml/network");
        this.newFolder(path + "/matsimXml/plans");
        this.newFolder(path + "/others");
        this.newFolder(path + "/output");
        this.newFolder(path + "/shpFile");
        this.newFolder(path + "/shpFile/network");
        this.newFolder(path + "/saveName/shpFile/region");
//        this.newFolder( path+"/saveName/temp" );

    }

    /**
     * This method moves uploaded files to specified folders. At the moment the temp file structure is as follows:
     * ├── matsimXml
     * │   ├── busSchedule
     * │   ├── config
     * │   ├── facility
     * │   ├── network
     * │   └── plans
     * ├── odFile
     * └── shpFile
     * ├── network
     * └── region
     * files are moved to userFolderTemplate (see method instruction of createSaveFolders() )
     * File
     *
     * @param originTempPath
     * @param saveNamePath
     */
    public void moveFiles2SpecifiedFolders(String originTempPath, String saveNamePath, WorkSpace workspace) {

        // move matsimXml block file
        if (workspace.getMatsimXmlsBlock() != null) {
            Block matsimXmlBlock = workspace.getMatsimXmlsBlock();
            // move network xml file
            String networkXml = matsimXmlBlock.getNetworkXml().replace("C:\\fakepath\\", "");
            System.out.println(originTempPath + "/matsimXml/network/" + networkXml + " to: " + saveNamePath + "/matsimXml/network/" + networkXml);
            this.moveFile(originTempPath + "/matsimXml/network/" + networkXml, saveNamePath + "/matsimXml/network/" + networkXml);
            // move activity file
            String activityXml = matsimXmlBlock.getActivityXml().replace("C:\\fakepath\\", "");
            System.out.println(activityXml);
            this.moveFile(originTempPath + "/matsimXml/plans/" + activityXml, saveNamePath + "/matsimXml/plans/" + activityXml);
            // move bus schedule file
            String busScheduleXml = matsimXmlBlock.getBusScheduleXml().replace("C:\\fakepath\\", "");
            System.out.println(busScheduleXml);
            this.moveFile(originTempPath + "/matsimXml/busSchedule/" + busScheduleXml, saveNamePath + "/matsimXml/busSchedule/" + busScheduleXml);
            // move bus vehicle xml file
            String vehicleXml = matsimXmlBlock.getVehicleXml().replace("C:\\fakepath\\", "");
            System.out.println(vehicleXml);
            this.moveFile(originTempPath + "/matsimXml/busSchedule/" + vehicleXml, saveNamePath + "/matsimXml/busSchedule/" + vehicleXml);
            // move bus vehicle xml file
            String facilityXml = matsimXmlBlock.getFacilityXml().replace("C:\\fakepath\\", "");
            System.out.println(facilityXml);
            this.moveFile(originTempPath + "/matsimXml/facility/" + facilityXml, saveNamePath + "/matsimXml/facility/" + facilityXml);
        }
        // move config file
        if (workspace.getMatsimBlock() != null) {

            Block matsimBlock = workspace.getMatsimBlock();
            String configXml = matsimBlock.getConfigXml().replace("C:\\fakepath\\", "");
            System.out.println(configXml);
            this.moveFile(originTempPath + "/matsimXml/config/" + configXml, saveNamePath + "/matsimXml/config/" + configXml);
        }

        // move network shp file
        if (workspace.getNetworkBlock() != null) {

            Block networkBlock = workspace.getNetworkBlock();
            String networkShp = networkBlock.getNetworkShpFile().replace("C:\\fakepath\\", "");
            String networkShx = networkBlock.getNetworkShxFile().replace("C:\\fakepath\\", "");
            String networkDbf = networkBlock.getNetworkDbfFile().replace("C:\\fakepath\\", "");
            System.out.println(networkShp + "," + networkShx + "," + networkDbf);
            this.moveFile(originTempPath + "/shpFile/network/" + networkShp, saveNamePath + "/shpFile/network/" + networkShp);
            this.moveFile(originTempPath + "/shpFile/network/" + networkShx, saveNamePath + "/shpFile/network/" + networkShx);
            this.moveFile(originTempPath + "/shpFile/network/" + networkDbf, saveNamePath + "/shpFile/network/" + networkDbf);
        }

        // move region shp file
        if (workspace.getRegionBlock() != null) {
            Block regionBlock = workspace.getRegionBlock();
            String regionShp = regionBlock.getRegionShpFile().replace("C:\\fakepath\\", "");
            String regionShx = regionBlock.getRegionShxFile().replace("C:\\fakepath\\", "");
            String regionDbf = regionBlock.getRegionDbfFile().replace("C:\\fakepath\\", "");
            System.out.println(regionShp + "," + regionShx + "," + regionDbf);
            this.moveFile(originTempPath + "/shpFile/region/" + regionShp, saveNamePath + "/shpFile/region/" + regionShp);
            this.moveFile(originTempPath + "/shpFile/region/" + regionShx, saveNamePath + "/shpFile/region/" + regionShx);
            this.moveFile(originTempPath + "/shpFile/region/" + regionDbf, saveNamePath + "/shpFile/region/" + regionDbf);
        }

        // move od matrix fileS, note matrix may not be unique, there might be several matrices
        if (workspace.getBlocksByType("odMatrix") != null) {
            List<Block> odMatrixBlocks = workspace.getBlocksByType("odMatrix");
            for (Block odMatrixBlock : odMatrixBlocks) {
                String odMatrixFile = odMatrixBlock.getOdFile();
                if (odMatrixFile.endsWith(".txt") || odMatrixFile.endsWith(".csv")) {
                    if (odMatrixBlock.getOdFileType().equalsIgnoreCase("array")) {
                        this.moveFile(originTempPath + "/odFile/" + odMatrixFile, saveNamePath + "/csvTxtFile/array/" + odMatrixFile);
                    } else if (odMatrixBlock.getOdFileType().equalsIgnoreCase("squareMatrix")) {
                        this.moveFile(originTempPath + "/odFile/" + odMatrixFile, saveNamePath + "/csvTxtFile/matrix/" + odMatrixFile);
                    }
                } else if (odMatrixFile.endsWith(".xls") || odMatrixFile.endsWith(".xlsx")) {
                    if (odMatrixBlock.getOdFileType().equalsIgnoreCase("array")) {
                        this.moveFile(originTempPath + "/odFile/" + odMatrixFile, saveNamePath + "/excel/array/" + odMatrixFile);
                    } else if (odMatrixBlock.getOdFileType().equalsIgnoreCase("squareMatrix")) {
                        this.moveFile(originTempPath + "/odFile/" + odMatrixFile, saveNamePath + "/excel/matrix/" + odMatrixFile);
                    }
                }
            }
        }

    }
//
//     │   ├── GTFS
//     │   ├── csvTxtFile
//     │   │   ├── array
//     │   │   └── matrix
//     │   ├── excel
//     │   │   ├── array
//     │   │   └── matrix
//     │   ├── matsimXml
//     │   │   ├── busSchedule
//     │   │   ├── config
//     │   │   ├── facility
//     │   │   ├── network
//     │   │   └── plans
//     │   ├── others
//     │   ├── output
//     │   └── shpFile
//     │       ├── network
//     │       └── region

    public static String getUserGtfsPath(String userName, String saveName) {
        return userFilePath + userName + "/" + saveName + "/GTFS";
    }

    /**
     * @param userName
     * @param saveName
     * @param odmatrix
     * @param excelOrText   accept input "excel" or "text"
     * @param arrayOrMatrix accept input "array" or "matrix"
     * @return
     */
    public static String getUserCsvFile(String userName, String saveName, Block odmatrix, String excelOrText, String arrayOrMatrix) {
        if (excelOrText.equalsIgnoreCase("text")) {
            if (arrayOrMatrix.equalsIgnoreCase("array")) {
                return userFilePath + userName + "/" + saveName + "/csvTxtFile/array/" + odmatrix.getOdFile();
            } else if (arrayOrMatrix.equalsIgnoreCase("matrix")) {
                return userFilePath + userName + "/" + saveName + "/csvTxtFile/matrix/" + odmatrix.getOdFile();
            }
        } else if (excelOrText.equalsIgnoreCase("excel")) {
            if (arrayOrMatrix.equalsIgnoreCase("array")) {
                return userFilePath + userName + "/" + saveName + "/excel/array/" + odmatrix.getOdFile();
            } else if (arrayOrMatrix.equalsIgnoreCase("matrix")) {
                return userFilePath + userName + "/" + saveName + "/excel/matrix/" + odmatrix.getOdFile();
            }
        }
        return null;
    }

    public static String getUserBusScheduleFile(String userName, String saveName, Block matsimXmls) {
        return userFilePath + userName + "/" + saveName + "/matsimXml/busSchedule/" + matsimXmls.getBusScheduleXml();
    }

    public static String getUseConfigFile(String userName, String saveName, Block matsim) {
        return userFilePath + userName + "/" + saveName + "/matsimXml/config/" + matsim.getConfigXml();
    }

    public static String getUserNetworkXmlFile(String userName, String saveName, Block matsimXmls) {
        return userFilePath + userName + "/" + saveName + "/matsimXml/network/" + matsimXmls.getNetworkXml();
    }

    public static String getUserPlansFile(String userName, String saveName, Block matsimXmls) {
        return userFilePath + userName + "/" + saveName + "/matsimXml/plans/" + matsimXmls.getActivityXml();
    }

    public static String getUserOutputPath(String userName, String saveName) {
        return userFilePath + userName + "/" + saveName + "/output/";
    }

    public static String getUserNetworkShpFile(String userName, String saveName, Block shpNetwork) {
        return userFilePath + userName + "/" + saveName + "/shpFile/network/" + shpNetwork.getNetworkShpFile();
    }

    public static String getUserRegionShpFile(String userName, String saveName, Block regionShp) {
        return userFilePath + userName + "/" + saveName + "/shpFile/region/" + regionShp.getRegionShpFile();
    }


    public static FilenameFilter getFileExtensionFilter(String extension) {// 指定扩展名过滤
        final String _extension = extension;
        return new FilenameFilter() {
            public boolean accept(File file, String name) {
                boolean ret = name.endsWith(_extension);
                return ret;
            }
        };
    }

    public static boolean hasXmlFile(String path) {
        File f = new File(path);
        if (!f.isDirectory()) {
            return false;
        } else {
            File[] childFiles = f.listFiles();
            for (File childFile : childFiles
            ) {
                if (childFile.isFile() && childFile.getName().endsWith(".xml")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasExtFile(String path, String fileExt) {
        File f = new File(path);
        if (!f.isDirectory()) {
            return false;
        } else {
            File[] childFiles = f.listFiles();
            for (File childFile : childFiles
            ) {
                if (childFile.isFile() && childFile.getName().endsWith(fileExt)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getXmlFileInFolder(String path) {
        File f = new File(path);
        if (!f.isDirectory()) {
            return null;
        } else {
            File[] childFiles = f.listFiles();
            for (File childFile : childFiles
            ) {
                if (childFile.isFile() && childFile.getName().endsWith(".xml")) {
                    return childFile.getAbsolutePath();
                }
            }
        }
        return null;
    }

    public static FileFilter getNotDirFileFilter() { // 文件还是目录过滤

        return new FileFilter() {

            public boolean accept(File file) {

                return file.isDirectory();//关键判断点

            }

        };

    }

}
