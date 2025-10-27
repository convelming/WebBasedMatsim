var ipPort = "localhost:23103";
// var ipPort = "119.23.251.152:7077";
var configs = window.configs = {

    drawBoard: {   // 操作面板配置
        id: "traffic",
        loadUrl: "/saveAndLoad/loadBySaveId",
        runUrl: "/workspace/start",
        tool: {
            title: "",
            style: "width: 80px;color:black;",
            bars: [{
                col: 2,
                style: "font-size:9px;margin:0px 2px;",
                elements: [
                    { key: "matsimXMLs", text: "xml", title: "Upload MATSim .xml inputs", icon: "icon/xml.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
                    { key: "matsim", text: "MATSim", title: "MATSim", icon: "icon/matsim.svg", shape: "circle", leftPoint: ">", rightPoint: ">" },
                    { key: "network", text: "network", title: "upload .shp network files", icon: "icon/map.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
                    { key: "openStreet", text: "open", title: "download open street map", icon: "icon/openMap.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
                    { key: "vehicle", text: "GTFS", title: "not working, coming soon",icon: "icon/text_GTFS.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
                    { key: "region", text: "region", title: "upload traffic zones", icon: "icon/region.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
                    { key: "facility", text: "Facility", title: "set up facility info", icon: "icon/facility.svg", shape: "circle", leftPoint: ">", rightPoint: ">" },
                    { key: "odMatrix", text: "OD matrix", title: "upload OD files", icon: "icon/table.svg", shape: "rect", leftPoint: ">", rightPoint: ">" },
                    { key: "editTable", title: "edit OD tables",status: 2, text: "Table", icon: "icon/edit.svg", shape: "rect", leftPoint: ">", rightPoint: false, linkTo:"./dataTable.html"  },
                    { key: "activity", text: "activity", title: "set up activity", icon: "icon/activity.svg", shape: "circle", leftPoint: ">", rightPoint: ">" },
                    { key: "timer", text: "Timer", title: "set up travel time info", icon: "icon/clock.svg", shape: "circle", leftPoint: ">", rightPoint: ">" },
                    { key: "person", text: "Person", title: "set up person plans", icon: "icon/person.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
                    { key: "mode", text: "Modes", title: "choose travel mode", icon: "icon/vehicle.svg", shape: "circle", leftPoint: ">", rightPoint: ">" },
                    { key: "tripPurpose", text: "Trip purpose", title: "choose trip purpose", icon: "icon/tripPurpose.svg", shape: "rect", leftPoint: ">", rightPoint: ">" }
                ]
            }, {
                col: 2,
                style: "font-size:9px;margin:0px 2px;",
                elements: [
                    { key: "networkDisplay", status: 2, title: "display network", text: "Network", icon: "icon/display.svg", shape: "circle", leftPoint: ">", rightPoint: false, linkTo:"./echartsNetwork.html" },
                    { key: "OTFvis", status: 2, title: "display simulation result", text: "OTFvis", icon: "icon/otfvis.svg", shape: "rect", leftPoint: ">", rightPoint: false, linkTo:"./eventQuery.html" },
                    { key: "centroid", status: 2, title: "centroids", text: "centroids", icon: "icon/movingStar.svg", shape: "rect", leftPoint: ">", rightPoint: false, linkTo:"./LinkStatusTest.html" },
                    { key: "chord", status: 2, title: "display OD chord", text: "chord", icon: "image/globe5s.gif", shape: "circle", leftPoint: ">", rightPoint: false, linkTo:"./viaTest.html" },
                    // 设置图形左右连接点示例
                    //{ key: "aaaa", title: "显示OD弦图", text:"aaaa", shape:"circle", leftPoint:"", rightPoint:">" }
                    // status 设置为1 或以上  拖进来默认就是绿的 1 以上的数字对应的是statusColor

                ]
            },  {
                col: 1,
                style: "font-size:12px;margin:0px 2px;",
                elements: [
                    { key: "example", text: "<strong>example<strong>", icon: "" },
                    { key: "trafficData", text: "opendata", icon: "" }
                ]
            },  {
                col: 2,
                style: "font-size:12px;margin:0px 2px;width:40px",
                elements: [{ key: "help", text: "HELP", linkTo: "help/index.html" },  ]
            },  ]
        },
        shape: {
            //maxNum: 4,
            keyMaxNum: {matsimXMLs:1, matsim: 1,region: 1, network: 1, openStreet:1,OTFvis:1},//设置哪个模块所允许对拖入workspace的最大数量
            statusColor: ["#EE0000","#FFF","#01DF3A","#FFFF77"]//status=-1的    第二个对应status=0   第三个对应status=1   第四个对应status=2    也可以设置第五个元素   对应的是status=3
        },
        // 配置连线验证
        connect: {
            verify: {
                // matsimXMLs的配置表示：matsimXMLs模块只能连接matsim（最多连接1个）和vehicle（最多连接2个）
                activity: {
                    maxIn: 1, maxOut: 1,
                    to: { "timer":1,"activity": 1, "facility": 1,"matsim":1,"tripPurpose":1,"mode": 1 }
                },
                facility: {
                    maxIn: 1, maxOut: 1,
                    // from 表示允许被哪些模块连接，如果不设置该属性则无限制
                    // from 配置规则同to一样
                    to: { "timer":1,"activity": 1, "facility": 1,"matsim": 1,"tripPurpose":1,"mode": 1 }
                },
                matsimXMLs: {
                    maxIn: 0,   // maxIn 最大允许几个模块连接到当前模块，如果不设置该属性则无限制
                    maxOut: 10,     // maxIn 最大允许连接几个模块，如果不设置该属性则无限制
                    // to 表示允许连接到哪些类型的模块，如果不设置该属性则无限制
                    // to 配置示例：
                    // 第一种：to: "matsim" 表示只能连接matsim类型模块，无次数限制
                    // 第二种：to: ["matsim", "vehicle"] 表示只能连接matsim和vehicle类型模块，无次数限制
                    // 第三种：to: {"matsim":1, "vehicle":2} 表示只能连接matsim和vehicle类型模块，最多只能连接1个matsim模块，最多只能连接2个vehicle模块
                    to: { "matsim": 1 ,"networkDisplay": 1}
                },
                matsim: {
                    maxIn: 10, maxOut: 1,
                    // from 表示允许被哪些模块连接，如果不设置该属性则无限制
                    // from 配置规则同to一样
                    to: {  "OTFvis": 1,"chord":1,}
                },
                mode: {
                    maxIn: 1, maxOut: 1,
                    to: { "timer":1,"activity": 1, "facility": 1,"tripPurpose":1,"mode": 1 }
                },
                network: {
                    maxOut: 10,
                    // from: {},
                    to: { "matsim": 1, "networkDisplay": 1 ,"chord":1,"centroid":1}
                },
                odMatrix: {
                    maxIn: 10, maxOut: 10,
                    to: { "matsim": 1, "editTable": 1, "odMatrix": 1,"chord":1,"centroid":1}
                },
                openStreet: {
                    maxOut: 10,
                    to: { "matsim": 1, "networkDisplay": 1 }
                },
                person: {
                    maxOut: 1,
                    to: { "activity": 1, "facility": 1,"mode":1,"tripPurpose":1,"timer":1 }
                },
                region: {
                    maxOut: 3,
                    to: { "matsim": 1, "networkDisplay": 1,"chord":1}
                },
                timer: {
                    maxIn: 1, maxOut: 1,
                    to: { "activity": 1, "facility": 1,"tripPurpose":1,"mode": 1 }
                },
                tripPurpose: {
                    maxIn: 1, maxOut: 1,
                    to: { "timer":1,"activity": 1, "facility": 1,"mode": 1 }
                },
            }
        }
    },

    saveConsole: {
        window: {
            id: "saveConsole",
            title: "saveConsole"
        },
        form: [
            { key: "id", title: "ID", type: "text", show: false },
            { key: "name", title: "saveName", type: "text", default: "input save name", verify: "NO_NULL", error: "Error: not NULL!" },
            { key: "callType", title: "CallType", type: "text", show: false },
            { key: "callParams", title: "CallParas", type: "text", show: false },
            { key: "shapeId", title: "shapeId", type: "text", show: false }
        ],
        buttons: [
            { id: "save", type: "saveConsole", text: "sure?", url: "/saveAndLoad/save" },
        ]
    },

    formWindows: {
        // 验证示例 添加 verify 属性
        // verify属性参数：不设置 verify 表示此属性为非必须字段
        //      NO_NULL：不为空
        //      GT_?：大于?，其中'?'可以是任意整数，如:GT_0 表示值必须大于0
        //      GTE_?：大于等于?，其中'?'可以是任意整数，如:GTE_0 表示值必须大于等于0
        //      LT_?：小于?，其中'?'可以是任意整数，如:LTE_1 表示值必须小于1
        //      LTE_?：小于等于?，其中'?'可以是任意整数，如:LT_1 表示值必须小于等于1
        //      E_?：等于?,注意：在等于中'?'可以为任意整数或字符串，如：E_0 表示值必须等于0、E_TEST 表示值必须等于TEST
        //      LENGTH_GT_?：长度大于?，其中'?'可以是任意整数，如:LENGTH_GT_1 表示字符串长度或check-box值的个数必须大于1
        //      LENGTH_GTE_?、LENGTH_、LENGTH_GT_?、LENGTH_LTE_?、LENGTH_LT_?、LENGTH_E_? 同理
        // { key: "verifyTest", title: "验证示例", type: "text", verify:"NO_NULL", error:"Error: not NULL!" },
        // // help HELP信息显示 和 error 输入错误信息显示
        // //      help 属性： 点击 help 按钮的提示信息（显示在输入框下面）
        // //      error 属性： 验证输入框输入的数据与 verify 属性规定的不符时  显示的错误信息（显示在输入框下面）
        // { key: "hintMsgTest", title: "提示信息示例", type: "text", verify:"NO_NULL",
        //     help:"Hint:这是一个点击HELP按钮的提示信息示例！",
        //     error:"Error: not NULL!"
        // },
        timer: {
            window: {
                id: "timer",
                title: "Timer"
            },
            form: [

                { key: "durTime", title: "activity duration", type: "text", value: "08:00:00", default: "hh:mm:ss",verify: "NO_NULL", help: "hint：input activity duration，format as hh:mm:ss", error: "Error: not NULL!" },
                { key: "endTime", title: "activity end time", type: "text", value: "18:00:00", default: "hh:mm:ss", verify: "NO_NULL", help: "hint：input activity end time，format as hh:mm:ss", error: "Error: not NULL!" }
            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "<a href='http://www.baidu.com'>HELP</a>" }]
        },
        facility: {
            window: {
                id: "facility",
                title: "facility"
            },
            form: [
                { key: "facilityId", title: "facility ID", type: "text", default: "HIL", verify: "NO_NULL", help: "Hint:this can be used as activity block, but not recommended if you don't upload facility file ", error: "Error: not NULL!" },
                { key: "actType", title: "trip purpose", type: "text", value: "work", help: "Hint:trip purpose, will dominate tripPurpose block", error: "error:" },
                { key: "facilityCoord", title: "facility location", type: "text", default: "47.408765, 8.507441", help: "Hint:location as longitude,latitude if no facility.xml file", error: "error:" },
                { key: "facilityOpenDay", title: "open days", type: "text", value: "weekday", help: "Hint: need to modify ", error: "Error: not NULL!" },
                { key: "facilityOpenTime", title: "open time", type: "text", value: "08:00:00", help: "Hint: need to modify, format as hh:mm:ss", error: "Error: not NULL!" },
                { key: "facilityCloseTime", title: "close time", type: "text", value: "20:00:00", help: "Hint: need to modify, format as hh:mm:ss", error: "Error: not NULL!" }

            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },

        openStreet: {
            window: {
                id: "openStreet",
                title: "openStreet"
            },
            form: [
                { key: "minLong", title: "min longitude", type: "text", value: "120.1910", verify: "NO_NULL", help: "Hint:please go to  <a href=\"https://www.openstreetmap.org/export#map=4/36.99/119.58\" target=\"_blank\">OPEN STREET MAP</a> to choose the range"},
                { key: "maxLong", title: "max longitude", type: "text", value: "120.7831", verify: "NO_NULL", help: "max longitude：[0,180]！" },
                { key: "minLati", title: "min latitude", type: "text", value: "36.2279", verify: "NO_NULL", help: "[0,180]" },
                { key: "maxLati", title: "max latitude", type: "text", value: "36.5478", verify: "NO_NULL", help: "[0,180]" }
            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },
        network: {
            window: {
                id: "network",
                title: "network"
            },
            form: [
                { key: "networkDefaultEPSG", title: "default proj", type: "span", value: "EPSG：4326", default: "EPSG code", help: "Hint:please turn to <a href=\http://epsg.io\" target=\"_blank\">EPSG </a> , not working at the moment, only support WGS 84" },
                { key: "networkDesiredEPSG", title: "desired proj", type: "span", value: "EPSG：4326", default: "EPSG code", help: "Hint:not working at the moment"},
                { key: "linkDir", title: "direction", type: "text", value: "null", verify: "NO_NULL", help: "Hint: input directions of the link, use GIS software to formatted as: 0-double way(default if null or empty)，<0 from end node to start node，>0 from start to end", error: "Error: field not found!" },
                { key: "linkSpeed", title: "max speed", type: "text", value: "null", verify: "NO_NULL", help: "Hint: m/s，as double", error: "Error: field not found!" },
                { key: "linkLength", title: "link length", type: "text", value: "null", verify: "NO_NULL", help: "Hint: meter, as double", error: "Error: field not found!" },
                { key: "linkLane", title: "lanes", type: "text", value: "null", verify: "NO_NULL", help: "Hint: lane number for single direction, as int", error: "Error: field not found!" },
                { key: "linkCapacity", title: "capacity", type: "text", value: "null", verify: "NO_NULL", help: "Hint: capacity for single direction", error: "Error: field not found!" },
                { key: "linkMode", title: "modes", type: "text", value: "null", verify: "NO_NULL", help: "Hint: modes allowed on the link, splitted by ','", error: "Error: field not found!" },
                { key: "networkShpFile", title: "upload .shp file", type: "file", value: "", verify: "NO_NULL", help: "Hint: a complete shp file includes .shp,.shx,.prj, .dbf and etc, must be the same prefix name", url: "/upload/networkShpFile", fileName: "fileBtn", upload:"custom.console.call.upload" },
                { key: "networkShxFile", title: "upload .shx file", type: "file", value: "", verify: "NO_NULL", help: "Hint:upload .shx file", url: "/upload/networkShxFile", fileName: "fileBtn", upload:"custom.console.call.upload" },
                { key: "networkDbfFile", title: "upload .dbf file", type: "file", value: "", verify: "NO_NULL", help: "Hint:upload .dbf file", url: "/upload/networkDbfFile", fileName: "fileBtn", upload:"custom.console.call.upload" }

            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },
        region: {
            window: {
                id: "region",
                title: "region"
            },
            form: [
                { key: "regionDefaultEPSG", title: "default proj", type: "span", value: "EPSG：4326", default: "EPSG code", help: "Hint:<a href=http://epsg.io\" target=\"_blank\">OPEN STREET MAP</a>  not working" },
                { key: "regionDesiredEPSG", title: "desired proj", type: "span", value: "EPSG：4326", default: "EPSG code", help: "Hint:not working", error: "Error: not NULL!" },
                { key: "regionId", title: "regionID field", type: "text", value: "EnglishID", verify: "NO_NULL", help: "Hint:id field from the shp file, case sensitive", error: "Error: not NULL!" },
                { key: "regionShpFile", title: "upload .shp file", type: "file", value: "", verify: "NO_NULL", help: "Hint: traffic zone shp file, with polygon or multipolygon", url: "/upload/regionShp", fileName: "fileBtn", upload:"custom.console.call.upload" },
                { key: "regionShxFile", title: "upload .shx file", type: "file", value: "", verify: "NO_NULL", help: "Hint:.shx file",  url: "/upload/regionShx", fileName: "fileBtn", upload:"custom.console.call.upload" },
                { key: "regionDbfFile", title: "upload .dbf file", type: "file", value: "", verify: "NO_NULL", help: "Hint:.prj file", url: "/upload/regionDbf", fileName: "fileBtn", upload:"custom.console.call.upload" }

            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },
        odMatrix: {
            window: {
                id: "odMatrix",
                title: "odMatrix"
            },
            form: [
                {
                    key: "odFileType", title: "od file type", type: "radio", value: "array", children: [
                        { text: "square matrix", value: "squareMatrix" },
                        { text: "array", value: "array" }
                    ], verify: "NO_NULL", help: "Hint:first row is the destination id, first column is the origin id, the very first col|row can be anything, normally not-null", error: "Error: please read the help lines and check."
                },
                {
                    key: "odAsRegionOrCoord", title: "OD location type", type: "radio", value: "asRegion", children: [
                        { text: "traffic zone id", value: "asRegion" },
                        { text: "coord", value: "asCoord" }
                    ], verify: "NO_NULL", help: "Hint:if square matrix is chosen, only traffic zone id is allowed！", error: "Error: please read the help lines and check."
                },

                {
                    key: "odFile", title: "activity(plan,od)file", type: "file", default: "",
                    help: "Hint:only support csv or txt file at the moment, if od array is uploaded, please check the format as:" +
                    "id(0)|count(1)|oriDepatrueTime(2)|origionRegion(3)|originCoordX(4)|originCoordY(5)|departureTime1(6+8*i)|desiredMode1(7+8*i)|tripPurpose1(8+8*i)|desFacilityId1(9+8*i)|facilityChangable1(10+8*i)|desRegionId1(11+8*i)|desCoordX1(12+8*i)|desCoordY1(13+8*i) where i>=1",
                    error: "Error: please read the help lines and check.", url: "/upload/odMatrix", fileName: "fileBtn", upload:"custom.console.call.upload"
                },
            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },
        mode: {
            window: {
                id: "mode",
                title: "mode"
            },
            form: [
                {
                    key: "mode", title: "mode", type: "radio", value: "car", children: [
                        { text: "walk", value: "walk" }, { text: "bus", value: "bus" }, { text: "subway", value: "subway" },
                        { text: "bike", value: "bike" }, { text: "car", value: "car" }, { text: "userDefined", value: "userDefined" }
                    ], verify: "NO_NULL", help: "", error: "Error: not NULL!"
                },
                // == != >= <= > < 
                { key: "otherMode", title: "user defined", type: "text", if: "@mode==userDefined", help: "Hint:input specific mode, have to be existed in the network file", error: "Error: please read the help lines and check." },
            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },
        tripPurpose: {
            window: {
                id: "tripPurpose",
                title: "tripPurpose"
            },
            form: [
                {
                    key: "tripPurpose", title: "trip purpose", type: "radio", value: "work", children: [
                        { text: "work", value: "work" }, { text: "entertainment", value: "entertainment" }, { text: "shopping", value: "shopping" },
                        { text: "sport", value: "sport" }, { text: "other", value: "userDefined" }
                    ], verify: "NO_NULL", help: "", error: "Error: please read the help lines and check."
                },
                { key: "otherPurpose", title: "other", type: "text", default: "input purpose", if: "@tripPurpose==userDefined", help: "hit: user-defined trip purpose", error: "Error: please read the help lines and check." },

                {
                    key: "tripPurposeDuration", title: "activity duration", type: "text", value: "8:00:00",
                    help: "hint: typical activity duration, formatted as hh:mm:ss", error: "Error: please read the help lines and check."
                },
                // {
                //     key: "tripPurposeType", title: "是否刚性出行", type: "radio", value: "necessary", children: [
                //         { text: "Yes", value: "necessary" }, { text: "No", value: "unnecessary" }
                //     ], verify: "NO_NULL", help: "Hint:刚性出行是目的地和时间有限制的出行", error: "Error: please read the help lines and check."
                // },
            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },
        activity: {
            window: {
                id: "activity",
                title: "activity"
            },
            form: [
                {
                    key: "actDestinationType", title: "type of destination", type: "radio", value: "location", children:
                    [{ text: "coord", value: "location" }, { text: "traffic zone id", value: "regionId" }], verify: "NO_NULL", help: "Hint:choose type of the destination", error: "Error: not NULL!"
                },
                { key: "actLocation", title: "activity coord", type: "text", default: "121.5066,31.2827", verify: "NO_NULL", if: "@actDestinationType==location", help: "Hint:longitude, latitude）", error: "Error: please read the help lines and check." },
                { key: "actRegionId", title: "activity zone", type: "text", default: "Tongji University", verify: "NO_NULL", if: "@actDestinationType==regionId", help: "Hint:region block is needed.", error: "Error: please read the help lines and check." }
            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },
        person: {
            window: {
                id: "person",
                title: "person"
            },
            form: [
                { key: "personId", title: "person ID", type: "text", value: "007", verify: "NO_NULL", help: "Hint:if there are more than one person, this will be a prefix", error: "Error: please read the help lines and check." },
                { key: "personNum", title: "number", type: "text", value: "1", verify: "NO_NULL", help: "Hint:input how many persons", error: "Error: please read the help lines and check." },
                { key: "personAge", title: "age ", type: "span", value: "forever 23", verify: "NO_NULL", help: "Hint:age, not working at the moment", error: "Error: please read the help lines and check." },
                { key: "personHouseholdSize", title: "family members", type: "span", value: "1", verify: "", help: "Hint: how many person in the family", error: "Error: please read the help lines and check." },
                { key: "personCarNum", title: "cars", type: "span", value: "1", verify: "", help: "Hint:how many cars the person(s) own", error: "Error: please read the help lines and check." },
                { key: "personDepTime", title: "desired departure time", type: "text", value: "08:00:00", verify: "", help: "Hint:format as HH:MM:SS, or hh:mm:ssAM/PM", error: "Error: please read the help lines and check." },
                {
                    key: "personLocationType", title: "", type: "radio", value: "homeCoord", children:
                    [{ text: "home coord", value: "homeCoord" }, { text: "traffic zoneID", value: "regionId" }], verify: "NO_NULL", help: "Hint:input home location", error: "Error: please read the help lines and check."
                },

                { key: "personLocationRegion", title: "traffic zone id", type: "text", default: "ETH Zürich", verify: "NO_NULL", if: "@personLocationType==regionId",help: "Hint:region block needed if this is used", error: "Error: please read the help lines and check." },
                { key: "personLocationLocation", title: "location", type: "text", default: "47.37639, 8.54806", verify: "NO_NULL",if: "@personLocationType==homeCoord", help: "Hint:only surport wgs84, as 0.00，0.00（longitude，latitude），0-180, precise to 0.000000", error: "Error: please read the help lines and check." }

                ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },
        matsimXMLs: {
            window: {
                id: "matsimXMLs",
                title: "matsimXMLs"
            },
            form: [
                { key: "networkXml", title: "network", type: "file", default: "", verify: "", help: "Hint:upload matsim supported .xml network", error: "Error: please read the help lines and check.", url: "/upload/mastimNetworkXml", fileName: "fileBtn", upload:"custom.console.call.upload" },
                { key: "activityXml", title: "plans(population)", type: "file", default: "", verify: "", help: "Hint:upload plans(population) .xml ", error: "Error: please read the help lines and check.", url: "/upload/mastimActivityXml", fileName: "fileBtn", upload:"custom.console.call.upload" },
                { key: "busScheduleXml", title: "bus schedules", type: "file", default: "", verify: "", help: "Hint:upload bus schedule .xml", error: "Error: please read the help lines and check.", url: "/upload/mastimBusScheduleXml", fileName: "fileBtn", upload:"custom.console.call.upload" },
                { key: "vehicleXml", title: "vehicles", type: "file", default: "", verify: "", help: "Hint:upload vehicle .xml", error: "Error: please read the help lines and check.", url: "/upload/mastimVehicleXml", fileName: "fileBtn", upload:"custom.console.call.upload" },
                { key: "facilityXml", title: "facilities", type: "file", default: "", verify: "", help: "Hint:upload facility .xml", error: "Error: please read the help lines and check.", url: "/upload/mastimFacilityXml", fileName: "fileBtn", upload:"custom.console.call.upload" }
            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },
        matsim: {
            window: {
                id: "matsim",
                title: "matsim",
                //style: "width:20%;margin-left:40%"
            },
            form: [
                { key: "iteration", title: "iterations", type: "text", value: "1", default: "1", verify: "", help: "Hint: 10 would be a good input.", error: "Error: please read the help lines and check." },
                {
                    key: "hasBusScheduleXml", title: "has bus schedule xml file?", type: "radio", value: "false", children: [
                        { text: "Yes", value: "true" }, { text: "No", value: "false" }
                    ], verify: "NO_NULL", error: "Error: not NULL!"
                },
                {
                    key: "hasVehicleXml", title: "has vehicle .xml file?", type: "radio", value: "false", children: [
                        { text: "Yes", value: "true" }, { text: "No", value: "false" }
                    ], verify: "NO_NULL", error: "Error: not NULL!"
                },
                {
                    key: "hasFacilityXml", title: "has facility .xml file?", type: "radio", value: "false", children: [
                        { text: "Yes", value: "true" }, { text: "No", value: "false" }
                    ], verify: "NO_NULL", error: "Error: not NULL!"
                },
                {
                    key: "hasConfigXml", title: "has config.xml? ", type: "radio", value: "false", children: [
                        { text: "Yes", value: "true" }, { text: "No", value: "false" }
                    ], verify: "NO_NULL", error: "Error: not NULL!"
                },
                {
                    key: "configXml", title: "config file", type: "file", default: "", verify: "", help: "Hint:upload the config file", if: "@hasConfigXml==true",
                    error: "Error: please read the help lines and check.", url: "/upload/mastimConfigXml", fileName: "fileBtn", upload:"custom.console.call.upload"
                }

            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        },
        vehicle: {
            window: {
                id: "vehicle",
                title: "vehicle"
            },
            form: [
                { key: "vehicleType", title: "车辆类型", type: "text", value: "bus", default: "", verify: "", help: "Hint:输入车辆类型", error: "Error: please read the help lines and check." },
                { key: "vehicleSeatNum", title: "座位数", type: "text", default: "", value: "50", verify: "", help: "Hint:车辆座位数", error: "Error: please read the help lines and check." },
                { key: "vehicleStandNum", title: "可站人数", type: "text", default: "", value: "25", verify: "", help: "Hint:可站立的人数", error: "Error: please read the help lines and check." },
                { key: "vehicleLength", title: "车辆长度", type: "text", default: "", value: "15", verify: "", help: "Hint:车辆长度（米）", error: "Error: please read the help lines and check." },
                { key: "vehicleWidth", title: "车辆宽度", type: "text", default: "", value: "2.5", verify: "", help: "Hint:车辆长度（米）", error: "Error: please read the help lines and check." },
                { key: "vehicleMaxSpeed", title: "最大速度", type: "text", default: "km/h", value: "60", verify: "", help: "Hint:最大车速（km/h）", error: "Error: please read the help lines and check." },
                { key: "vehicleDes", title: "车辆描述", type: "text", default: "", value: "This is a bus", verify: "", help: "Hint:车辆用途，描述", error: "Error: please read the help lines and check." },
                { key: "vehicleAsCar", title: "当量值", type: "text", default: "", value: "1.0", verify: "", help: "Hint:转化成当量小汽车的值", error: "Error: please read the help lines and check." }
            ],
            buttons: [{ id: "finish", type: "finish", text: "DONE" }, { id: "help", type: "help", text: "HELP" }]
        }
    },

    gridWindows: {
        loadAll: {
            window: {
                id: "loadAll",
                title: "loadAll"
            },
            grid: {
                col: 5,
                elements: {
                    url: "/saveAndLoad/loadAll",
                    width: 1,
                    height: 1
                }
            }
        },
        example: {
            window: {
                id: "example",
                title: "example"
            },
            grid: {
                col: 5,
                elements: {
                    url: "/saveAndLoad/loadExample",
                    width: 1,
                    height: 1
                }
            }
        },
        trafficData: {
            window: {
                id: "trafficData",
                title: "trafficData"
            },
            grid: {
                col: 5,
                elements: {
                    url: "/saveAndLoad/loadExample",
                    width: 1,
                    height: 1
                }
            },
        }
        // trafficData: {
        //     window: {
        //         id: "trafficData",
        //         title: "trafficData"
        //     },
        //     grid: {
        //         col: 5,
        //         elements: [
        //             { key: "1", text: "1", width: 1, height: 1 },
        //             { key: "2", text: "2", width: 1, height: 1 },
        //             { key: "3", text: "3", width: 1, height: 1 },
        //             { key: "4", text: "4", width: 1, height: 1 },
        //             { key: "5", text: "5", width: 1, height: 1 },
        //             { key: "6", text: "6", width: 1, height: 1 },
        //             { key: "7", text: "7", width: 1, height: 1 },
        //             { key: "8", text: "8", width: 1, height: 1 },
        //             { key: "9", text: "9", width: 1, height: 1 },
        //             { key: "10", text: "10", width: 1, height: 1 },
        //             { key: "11", text: "11", width: 1, height: 1 },
        //             { key: "12", text: "12", width: 1, height: 1 },
        //             { key: "13", text: "13", width: 1, height: 1 },
        //             { key: "14", text: "14", width: 1, height: 1 },
        //         ]
        //     },
        // }
    }
};


        // xxxx: {
        //     window: {
        //         id: "xxxx",
        //         title: "xxxx"
        //     },
        //     form: [
        // 验证示例 添加 verify 属性
        // verify属性参数：不设置 verify 表示此属性为非必须字段
        //      NO_NULL：不为空
        //      GT_?：大于?，其中'?'可以是任意整数，如:GT_0 表示值必须大于0
        //      GTE_?：大于等于?，其中'?'可以是任意整数，如:GTE_0 表示值必须大于等于0
        //      LT_?：小于?，其中'?'可以是任意整数，如:LTE_1 表示值必须小于1
        //      LTE_?：小于等于?，其中'?'可以是任意整数，如:LT_1 表示值必须小于等于1
        //      E_?：等于?,注意：在等于中'?'可以为任意整数或字符串，如：E_0 表示值必须等于0、E_TEST 表示值必须等于TEST
        //      LENGTH_GT_?：长度大于?，其中'?'可以是任意整数，如:LENGTH_GT_1 表示字符串长度或check-box值的个数必须大于1
        //      LENGTH_GTE_?、LENGTH_、LENGTH_GT_?、LENGTH_LTE_?、LENGTH_LT_?、LENGTH_E_? 同理
        //{ key: "verifyTest", title: "验证示例", type: "text", verify:"NO_NULL", error:"Error: not NULL!" },
        // help HELP信息显示 和 error 输入错误信息显示
        //      help 属性： 点击 help 按钮的提示信息（显示在输入框下面）
        //      error 属性： 验证输入框输入的数据与 verify 属性规定的不符时  显示的错误信息（显示在输入框下面）
        //         { key: "a", title: "出发时间", type: "text"  }, 
        //         { key: "b", title: "到达时间", type: "text"  }, 
        //         { key: "c", title: "test-3", type: "radio", value: "1", children: [
        //             { text: "一", value: "1" }, { text: "二", value: "2" }
        //         ]}, 
        //         { key: "d", title: "test-4", type: "checkbox", value: ["1", "2"], children: [
        //             { text: "一", value: "1" }, { text: "二", value: "2" }, { text: "三", value: "3" }
        //         ]}, 
        //         { key: "e", title: "test-5", type: "select", value: "1", default: { value: "0", text: "请选择" }, children: [
        //             { text: "一", value: "1" }, { text: "二", value: "2" }, { text: "三", value: "3" }
        //         ]},
        //         { key: "f", title: "test-6", type: "text", value: "", default: "" },
        //         { key: "g", title: "test-7", type: "textarea", value: "11111" }, 
        //         { key: "h", title: "test-8", type: "p", value: "22222"}
        //     ]
        // },

        //
