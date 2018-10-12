package com.matsim.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by MingLU on 2018/4/8,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class Block implements Serializable {
    // public variables
    private String type ="";
    private String id  ="";
    private Position position;
    private String svgId ="";
    private int status; //
    private String svgShapeId ="";
    private Connection[] connects;

//    private int input;

//    private int output;Output;// -1 has only input, 0 both input and output, 1 has only output

    // block specific common variables

// block specific variables;

    // activity specific variable
    private String actDestinationType ="";
    private String actFillout = "";
    private String actLocation ="";
    private String actRegionId ="";

    // facility specific variables
    private String facilityId ="";
    private String actType ="";
    private String facilityCoord ="";
    private String facilityOpenDay ="";
    private String facilityOpenTime ="";
    private String facilityCloseTime ="";

    // matsimXMLs specific variables
    private String networkXml ="";
    private String activityXml ="";
    private String busScheduleXml ="";
    private String vehicleXml ="";
    private String facilityXml ="";

    // matsim specific variables
    private int iteration;
    private boolean hasBusScheduleXml;
    private boolean hasVehicleXml;
    private boolean hasFacilityXml;
    private boolean hasConfigXml;
    private String configXml ="";


    // mode specific variables
    private String mode ="";
    private String otherMode ="";

    // od matrix specific variables
    private String odFileType ="";
    private String odFile ="";
    private String odAsRegionOrCoord ="";

    // open street map specific variables
    private double minLong;// = 120.1910;
    private double maxLong;// = 120.7831;
    private double minLati;// = 36.5478;
    private double maxLati;// = 36.2279;

    // person specific variables
    private String personId ="";
    private String personAge ="";
    private int personNum;
    private String personHouseholdSize ="";
    private int personCarNum;
    private String personDepTime ="";
    private String personLocationType ="";
    private String personLocationRegion ="";
    private String personLocationLocation ="";

    // region specific variables
    private String regionDefaultEPSG ="";// = "EPSG:4326";
    private String regionDesiredEPSG ="";
    private String regionId ="";
    private String regionShpFile ="";
    private String regionShxFile ="";
    private String regionDbfFile ="";

    // network specific variables
    private String networkDefaultEPSG ="";// = "EPSG:4326";
    private String networkDesiredEPSG ="";
    private String linkDir ="";
    private String linkSpeed ="";
    private String linkLength ="";
    private String linkLane ="";
    private String linkCapacity ="";
    private String linkMode ="";
    private String networkShpFile ="";
    private String networkShxFile ="";
    private String networkDbfFile ="";

    // timer specific variables
    private String durTime ="";
    private String endTime ="";

    // trip purpose specific variables
    private String tripPurpose ="";
//    private String tripPurposeDuration =""; //necesseary or unnecessary
    private String otherPurpose ="";

    // vehicle specific variables
    private double vehicleAsCar;
    private String vehicleDes="";
    private double vehicleLength;
    private double vehicleMaxSpeed;
    private int vehicleSeatNum;
    private int vehicleStandNum;
    private String vehicleType="";
    private double vehicleWidth;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getSvgId() {
        return svgId;
    }

    public void setSvgId(String svgId) {
        this.svgId = svgId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSvgShapeId() {
        return svgShapeId;
    }

    public void setSvgShapeId(String svgShapeId) {
        this.svgShapeId = svgShapeId;
    }

    public Connection[] getConnects() {
        return connects;
    }

    public void setConnects(Connection[] connects) {
        this.connects = connects;
    }

    public String getActDestinationType() {
        return actDestinationType;
    }

    public void setActDestinationType(String actDestinationType) {
        this.actDestinationType = actDestinationType;
    }

    public String getActLocation() {
        return actLocation;
    }

    public void setActLocation(String actLocation) {
        this.actLocation = actLocation;
    }

    public String getActRegionId() {
        return actRegionId;
    }

    public void setActRegionId(String actRegionId) {
        this.actRegionId = actRegionId;
    }

    public String getActFillout() {
        return actFillout;
    }

    public void setActFillout(String actFillout) {
        this.actFillout = actFillout;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public String getFacilityCoord() {
        return facilityCoord;
    }

    public void setFacilityCoord(String facilityCoord) {
        this.facilityCoord = facilityCoord;
    }

    public String getFacilityOpenDay() {
        return facilityOpenDay;
    }

    public void setFacilityOpenDay(String facilityOpenDay) {
        this.facilityOpenDay = facilityOpenDay;
    }

    public String getFacilityOpenTime() {
        return facilityOpenTime;
    }

    public void setFacilityOpenTime(String facilityOpenTime) {
        this.facilityOpenTime = facilityOpenTime;
    }

    public String getFacilityCloseTime() {
        return facilityCloseTime;
    }

    public void setFacilityCloseTime(String facilityCloseTime) {
        this.facilityCloseTime = facilityCloseTime;
    }

    public String getNetworkXml() {return networkXml;  }

    public void setNetworkXml(String networkXml) { this.networkXml = networkXml;}

    public String getActivityXml() {
        return activityXml;
    }

    public void setActivityXml(String activityXml) {
        this.activityXml = activityXml;
    }

    public String getBusScheduleXml() {
        return busScheduleXml;
    }

    public void setBusScheduleXml(String busScheduleXml) {
        this.busScheduleXml = busScheduleXml;
    }

    public String getVehicleXml() {
        return vehicleXml;
    }

    public int getIteration() {        return iteration;    }

    public void setVehicleXml(String vehicleXml) {
        this.vehicleXml = vehicleXml;
    }

    public String getFacilityXml() {
        return facilityXml;
    }

    public void setFacilityXml(String facilityXml) {        this.facilityXml = facilityXml;    }

    public void setIteration(int iteration) {        this.iteration = iteration;    }

    public boolean isHasBusScheduleXml() {
        return hasBusScheduleXml;
    }

    public void setHasBusScheduleXml(boolean hasBusScheduleXml) {
        this.hasBusScheduleXml = hasBusScheduleXml;
    }

    public boolean isHasVehicleXml() {
        return hasVehicleXml;
    }

    public void setHasVehicleXml(boolean hasVehicleXml) {
        this.hasVehicleXml = hasVehicleXml;
    }

    public boolean isHasFacilityXml() {
        return hasFacilityXml;
    }

    public boolean isHasConfigXml() {        return hasConfigXml;    }

    public void setHasConfigXml(boolean hasConfigXml) {        this.hasConfigXml = hasConfigXml;    }

    public String getConfigXml() {        return configXml;    }

    public void setConfigXml(String configXml) {        this.configXml = configXml;    }

    public void setHasFacilityXml(boolean hasFacilityXml) {
        this.hasFacilityXml = hasFacilityXml;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getOtherMode() {
        return otherMode;
    }

    public void setOtherMode(String otherMode) {
        this.otherMode = otherMode;
    }

    public String getOdFileType() {
        return odFileType;
    }

    public void setOdFileType(String odFileType) {
        this.odFileType = odFileType;
    }

    public String getOdFile() {
        return odFile;
    }

    public void setOdFile(String odFile) {
        this.odFile = odFile;
    }

    public String getOdAsRegionOrCoord() {  return odAsRegionOrCoord;  }

    public void setOdAsRegionOrCoord(String odAsRegionOrCoord) { this.odAsRegionOrCoord = odAsRegionOrCoord; }

    public double getMinLong() {
        return minLong;
    }

    public void setMinLong(double minLong) {
        this.minLong = minLong;
    }

    public double getMaxLong() {
        return maxLong;
    }

    public void setMaxLong(double maxLong) {
        this.maxLong = maxLong;
    }

    public double getMinLati() {
        return minLati;
    }

    public void setMinLati(double minLati) {
        this.minLati = minLati;
    }

    public double getMaxLati() {
        return maxLati;
    }

    public void setMaxLati(double maxLati) {
        this.maxLati = maxLati;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonAge() {
        return personAge;
    }

    public void setPersonAge(String personAge) {
        this.personAge = personAge;
    }

    public int getPersonNum() {
        return personNum;
    }

    public void setPersonNum(int personNum) {
        this.personNum = personNum;
    }

    public String getPersonHouseholdSize() {
        return personHouseholdSize;
    }

    public void setPersonHouseholdSize(String personHouseholdSize) {
        this.personHouseholdSize = personHouseholdSize;
    }

    public int getPersonCarNum() {
        return personCarNum;
    }

    public void setPersonCarNum(int personCarNum) {
        this.personCarNum = personCarNum;
    }

    public String getPersonDepTime() {
        return personDepTime;
    }

    public void setPersonDepTime(String personDepTime) {
        this.personDepTime = personDepTime;
    }

    public String getPersonLocationType() {
        return personLocationType;
    }

    public void setPersonLocationType(String personLocationType) {
        this.personLocationType = personLocationType;
    }

    public String getPersonLocationRegion() {
        return personLocationRegion;
    }

    public void setPersonLocationRegion(String personLocation) {
        this.personLocationRegion = personLocation;
    }

    public String getPersonLocationLocation() {      return personLocationLocation;    }

    public void setPersonLocationLocation(String personLocationLocation) {        this.personLocationLocation = personLocationLocation;    }

    public String getRegionDefaultEPSG() {
        return regionDefaultEPSG;
    }

    public void setRegionDefaultEPSG(String regionDefaultEPSG) {
        this.regionDefaultEPSG = regionDefaultEPSG;
    }

    public String getRegionDesiredEPSG() {
        return regionDesiredEPSG;
    }

    public void setRegionDesiredEPSG(String regionDesiredEPSG) {
        this.regionDesiredEPSG = regionDesiredEPSG;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionShpFile() {
        return regionShpFile;
    }

    public void setRegionShpFile(String regionShpFile) {
        this.regionShpFile = regionShpFile;
    }

    public String getNetworkDefaultEPSG() {
        return networkDefaultEPSG;
    }

    public void setNetworkDefaultEPSG(String networkDefaultEPSG) {
        this.networkDefaultEPSG = networkDefaultEPSG;
    }

    public String getNetworkDesiredEPSG() {
        return networkDesiredEPSG;
    }

    public void setNetworkDesiredEPSG(String networkDesiredEPSG) {
        this.networkDesiredEPSG = networkDesiredEPSG;
    }

    public String getLinkDir() {        return linkDir;    }

    public void setLinkDir(String linkDir) {        this.linkDir = linkDir;    }

    public String getLinkSpeed() {        return linkSpeed;    }

    public void setLinkSpeed(String linkSpeed) {        this.linkSpeed = linkSpeed;    }

    public String getLinkLength() {        return linkLength;    }

    public void setLinkLength(String linkLength) {        this.linkLength = linkLength;    }

    public String getLinkLane() {        return linkLane;    }

    public void setLinkLane(String linkLane) {        this.linkLane = linkLane;    }

    public String getLinkCapacity() {        return linkCapacity;    }

    public void setLinkCapacity(String linkCapacity) {        this.linkCapacity = linkCapacity;    }

    public String getLinkMode() {        return linkMode;    }

    public void setLinkMode(String linkMode) {        this.linkMode = linkMode;    }

    public String getNetworkShpFile() {
        return networkShpFile;
    }

    public void setNetworkShpFile(String networkShpFile) {
        this.networkShpFile = networkShpFile;
    }

    public String getDurTime() {
        return durTime;
    }

    public void setDurTime(String durTime) {
        this.durTime = durTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTripPurpose() {
        return tripPurpose;
    }

    public void setTripPurpose(String tripPurpose) {
        this.tripPurpose = tripPurpose;
    }

    public String getOtherPurpose() {
        return otherPurpose;
    }

    public void setOtherPurpose(String otherPurpose) {
        this.otherPurpose = otherPurpose;
    }

//    public String getTripPurposeDuration() {        return tripPurposeDuration;    }
//
//    public void setTripPurposeDuration(String tripPurposeDuration) {        this.tripPurposeDuration = tripPurposeDuration;    }

    public double getVehicleAsCar() {
        return vehicleAsCar;
    }

    public void setVehicleAsCar(double vehicleAsCar) {
        this.vehicleAsCar = vehicleAsCar;
    }

    public String getVehicleDes() {
        return vehicleDes;
    }

    public void setVehicleDes(String vehicleDes) {
        this.vehicleDes = vehicleDes;
    }

    public double getVehicleLength() {
        return vehicleLength;
    }

    public void setVehicleLength(double vehicleLength) {
        this.vehicleLength = vehicleLength;
    }

    public double getVehicleMaxSpeed() {
        return vehicleMaxSpeed;
    }

    public void setVehicleMaxSpeed(double vehicleMaxSpeed) {
        this.vehicleMaxSpeed = vehicleMaxSpeed;
    }

    public int getVehicleSeatNum() {
        return vehicleSeatNum;
    }

    public void setVehicleSeatNum(int vehicleSeatNum) {
        this.vehicleSeatNum = vehicleSeatNum;
    }

    public int getVehicleStandNum() {
        return vehicleStandNum;
    }

    public void setVehicleStandNum(int vehicleStandNum) {
        this.vehicleStandNum = vehicleStandNum;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getVehicleWidth() {
        return vehicleWidth;
    }

    public void setVehicleWidth(double vehicleWidth) {
        this.vehicleWidth = vehicleWidth;
    }

    public String getRegionShxFile() {        return regionShxFile;    }

    public void setRegionShxFile(String regionShxFile) {        this.regionShxFile = regionShxFile;    }

    public String getRegionDbfFile() {        return regionDbfFile;    }

    public void setRegionDbfFile(String regionDbfFile) {        this.regionDbfFile = regionDbfFile;    }

    public String getNetworkShxFile() {        return networkShxFile;    }

    public void setNetworkShxFile(String networkShxFile) {        this.networkShxFile = networkShxFile;    }

    public String getNetworkDbfFile() {        return networkDbfFile;    }

    public void setNetworkDbfFile(String networkDbfFile) {        this.networkDbfFile = networkDbfFile;    }

    @Override
    public String toString() {


        String pre = "{" +
                "'type':'" + type + "'" +
                ",'id':'" + id + "'," +
                position.toString() + "," +
                "'status':'" + status + "','connects':";
//        for (Connection connection : connects) {
//            pre += connection.toString() + "},";
//        }
//            pre = pre.substring(0,pre.length()-1);
//            pre += "]";
            pre += Arrays.toString(connects);

//            pre.replace("},]","}]");
            if (type.equalsIgnoreCase("activity")) {
                return pre +
                        ",'actDestinationType':'" + actDestinationType + "'" +
                        ",'actLocation':'" + actLocation + "'" +
                        ",'actRegionId':'" + actRegionId + "'" +
                        ",'actFillout':'" + actFillout + "'}";
            } else if (type.equalsIgnoreCase("facility")) {
                return pre +
                        ",'facilityCoord':'" + facilityCoord + "'" +
                        ",'facilityOpenDay':'" + facilityOpenDay + "'" +
                        ",'facilityOpenTime':'" + facilityOpenTime + "'" +
                        ",'facilityCloseTime':'" + facilityCloseTime + "'" + "}";
            } else if (type.equalsIgnoreCase("matsimXMLs")) {
                return pre + ",'networkXml':'" + networkXml + "'" +
                        ",'activityXml':'" + activityXml + "'" +
                        ",'busScheduleXml':'" + busScheduleXml + "'" +
                        ",'vehicleXml':'" + vehicleXml + "'" +
                        ",'facilityXml':'" + facilityXml + "'" + "}";
            } else if (type.equalsIgnoreCase("matsim")) {
                return pre + ",'iteration':'" + iteration + "'" +
                        ",'hasBusScheduleXml':" + hasBusScheduleXml +
                        ",'hasVehicleXml':" + hasVehicleXml +
                        ",'hasFacilityXml':" + hasFacilityXml +
                        ",'hasConfigXml':" + hasConfigXml +
                        ",'configXml':'" + configXml + "'}";
            } else if (type.equalsIgnoreCase("mode")) {
                return pre +
                        ",'mode':'" + mode + "'" +
                        ",'otherMode':'" + otherMode + "'}";
            } else if (type.equalsIgnoreCase("odMatrix")) {
                return pre +
                        ",'odFileType':'" + odFileType + "'" +
                        ",'odAsRegionOrCoord':'" + odAsRegionOrCoord + "'" +
                        ",'odFile':'" + odFile + "'}";
            } else if (type.equalsIgnoreCase("openStreet")) {
                return pre +
                        ",'minLong':'" + minLong +"'" +
                        ",'maxLong':'" + maxLong +"'" +
                        ",'minLati':'" + minLati +"'" +
                        ",'maxLati':'" + maxLati + "'}";
            } else if (type.equalsIgnoreCase("person")) {
                return pre +
                        ",'personId':'" + personId + "'" +
                        ",'personAge':'" + personAge + "'" +
                        ",'personNum':'" + personNum + "'" +
                        ",'personHouseholdSize':'" + personHouseholdSize + "'" +
                        ",'personCarNum':'" + personCarNum + "'" +
                        ",'personDepTime':'" + personDepTime + "'" +
                        ",'personLocationType':'" + personLocationType + "'" +
                        ",'personLocationRegion':'" + personLocationRegion + "'" +
                        ",'personLocationLocation':'" + personLocationLocation + "'}";
            } else if (type.equalsIgnoreCase("region")) {
                return pre +
                        ",'regionDefaultEPSG':'" + regionDefaultEPSG + "'" +
                        ",'regionDesiredEPSG':'" + regionDesiredEPSG + "'" +
                        ",'regionId':'" + regionId + "'" +
                        ",'regionShpFile':'" + regionShpFile + "'" +
                        ",'regionShxFile':'" + regionShxFile + "'" +
                        ",'regionDbfFile':'" + regionDbfFile + "'}";
            } else if (type.equalsIgnoreCase("network")) {
                return pre +
                        ",'networkDefaultEPSG':'" + networkDefaultEPSG + "'" +
                        ",'networkDesiredEPSG':'" + networkDesiredEPSG + "'" +
                        ",'linkDir':'" + linkDir + "'" +
                        ",'linkLane':'" + linkLane + "'" +
                        ",'linkSpeed':'" + linkSpeed + "'" +
                        ",'linkLength':'" + linkLength + "'" +
                        ",'linkCapacity':'" + linkCapacity + "'" +
                        ",'linkMode':'" + linkMode + "'" +
                        ",'networkShpFile':'" + networkShpFile + "'" +
                        ",'networkShxFile':'" + networkShxFile + "'" +
                        ",'networkDbfFile':'" + networkDbfFile + "'}";
            } else if (type.equalsIgnoreCase("timer")) {
                return pre +
                        ",'durTime':'" + durTime + "'" +
                        ",'endTime':'" + endTime + "'}";
            } else if (type.equalsIgnoreCase("tripPurpose")) {
                return pre +
                        ",'tripPurpose':'" + tripPurpose + "'" +
//                        ",'tripPurposeDuration':'" + tripPurposeDuration + "'" +
                        ",'otherPurpose':'" + otherPurpose + "'}";
            } else if (type.equalsIgnoreCase("vehicle")) {
                return pre +
                        ",'vehicleAsCar':" + vehicleAsCar +"'" +
                        ",'vehicleDes':'" + vehicleDes + "'" +
                        ",'vehicleLength':" + vehicleLength +"'" +
                        ",'vehicleMaxSpeed':'" + vehicleMaxSpeed +"'" +
                        ",'vehicleSeatNum':'" + vehicleSeatNum +"'" +
                        ",'vehicleStandNum':'" + vehicleStandNum +"'" +
                        ",'vehicleType':'" + vehicleType + "'" +
                        ",'vehicleWidth':'" + vehicleWidth +"'" +
                        "}";
            } else {
                return pre + "}";
        }
    }

}