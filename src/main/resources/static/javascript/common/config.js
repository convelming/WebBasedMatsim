// var ipPort = "localhost:7077";
var ipPort = "119.23.251.152:7077";
var configs = (window.configs = {
  drawBoard: {
    // 操作面板配置
    id: "traffic",
    loadUrl: "/saveAndLoad/loadBySaveId",
    runUrl: "/workspace/start",
    tool: {
      title: "",
      style: "width: 80px;color:black;",
      bars: [
        {
          col: 2,
          style: "font-size:9px;margin:0px 2px;",
          elements: [
            { key: "matsimXMLs", i18n: "matsimXMLs_bar", text: "xml", title: "上传MATSim标准输入文件", icon: "icon/xml.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
            { key: "matsim", i18n: "matsim_bar", text: "MATSim", title: "MATSim", icon: "icon/matsim.svg", shape: "circle", leftPoint: ">", rightPoint: ">" },
            { key: "network", i18n: "network_bar", text: "network", title: "上传Shp格式路网文件", icon: "icon/map.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
            { key: "openStreet", i18n: "openStreet_bar", text: "open", title: "从openstreetmap下载", icon: "icon/openMap.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
            { key: "vehicle", i18n: "vehicle_bar", text: "GTFS", title: "设置车辆信息", icon: "icon/text_GTFS.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
            { key: "region", i18n: "region_bar", text: "region", title: "选择或上传交通小区文件", icon: "icon/region.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
            { key: "facility", i18n: "facility_bar", text: "Facility", title: "设置建筑信息", icon: "icon/facility.svg", shape: "circle", leftPoint: ">", rightPoint: ">" },
            { key: "odMatrix", i18n: "odMatrix_bar", text: "OD matrix", title: "设置OD矩阵", icon: "icon/table.svg", shape: "rect", leftPoint: ">", rightPoint: ">" },
            { key: "editTable", i18n: "editTable_bar", text: "Table", title: "修改OD表数据", status: 2, icon: "icon/edit.svg", shape: "rect", leftPoint: ">", rightPoint: false, linkTo: "./dataTable.html" },
            { key: "activity", i18n: "activity_bar", text: "activity", title: "设置出行活动", icon: "icon/activity.svg", shape: "circle", leftPoint: ">", rightPoint: ">" },
            { key: "timer", i18n: "timer_bar", text: "Timer", title: "设置出行时间", icon: "icon/clock.svg", shape: "circle", leftPoint: ">", rightPoint: ">" },
            { key: "person", i18n: "person_bar", text: "Person", title: "设置出行者信息", icon: "icon/person.svg", shape: "rect", leftPoint: false, rightPoint: ">" },
            { key: "mode", i18n: "mode_bar", text: "Modes", title: "选择出行模式", icon: "icon/vehicle.svg", shape: "circle", leftPoint: ">", rightPoint: ">" },
            { key: "tripPurpose", i18n: "tripPurpose_bar", text: "Trip purpose", title: "选择出行目的", icon: "icon/tripPurpose.svg", shape: "rect", leftPoint: ">", rightPoint: ">" },
          ],
        },
        {
          col: 2,
          style: "font-size:9px;margin:0px 2px;",
          elements: [
            { key: "networkDisplay", i18n: "networkDisplay_bar", status: 2, title: "显示路网", text: "Network", icon: "icon/display.svg", shape: "circle", leftPoint: ">", rightPoint: false, linkTo: "./echartsNetwork.html" },
            { key: "OTFvis", i18n: "OTFvis_bar", status: 2, title: "显示仿真结果", text: "OTFvis", icon: "icon/otfvis.svg", shape: "rect", leftPoint: ">", rightPoint: false, linkTo: "./eventQuery.html" },
            { key: "centroid", i18n: "centroid_bar", status: 2, title: "显示形心连杆", text: "centroids", icon: "icon/star.svg", shape: "rect", leftPoint: ">", rightPoint: false, linkTo: "./LinkStatusTest.html" },
            { key: "chord", i18n: "chord_bar", status: 2, title: "显示OD弦图", text: "chord", icon: "image/globe.gif", shape: "circle", leftPoint: ">", rightPoint: false, linkTo: "./viaTest.html" },
            // 设置图形左右连接点示例
            //{ key: "aaaa", title: "显示OD弦图", text:"aaaa", shape:"circle", leftPoint:"", rightPoint:">" }
            // status 设置为1 或以上  拖进来默认就是绿的 1 以上的数字对应的是statusColor
          ],
        },
        {
          col: 1,
          style: "font-size:12px;margin:0px 2px;",
          elements: [
            { key: "example", i18n: "example_bar", text: "案例", icon: "" },
            { key: "trafficData", i18n: "trafficData_bar", text: "交通数据", icon: "" },
          ],
        },
        {
          col: 2,
          style: "font-size:12px;margin:0px 2px;",
          elements: [{ key: "help", i18n: "help_bar", text: "help", icon: "" }],
        },
      ],
    },
    shape: {
      //maxNum: 4,
      keyMaxNum: { matsimXMLs: 1, matsim: 1, region: 1, network: 1, openStreet: 1, OTFvis: 1 }, //设置哪个模块所允许对拖入workspace的最大数量
      statusColor: ["#EE0000", "#FFF", "#01DF3A", "#FFFF77"], //status=-1的    第二个对应status=0   第三个对应status=1   第四个对应status=2    也可以设置第五个元素   对应的是status=3
    },
    // 配置连线验证
    connect: {
      verify: {
        // matsimXMLs的配置表示：matsimXMLs模块只能连接matsim（最多连接1个）和vehicle（最多连接2个）
        activity: {
          maxIn: 1,
          maxOut: 1,
          to: { timer: 1, activity: 1, facility: 1, matsim: 1, tripPurpose: 1, mode: 1 },
        },
        facility: {
          maxIn: 1,
          maxOut: 1,
          // from 表示允许被哪些模块连接，如果不设置该属性则无限制
          // from 配置规则同to一样
          to: { timer: 1, activity: 1, matsim: 1, odMatrix: 1, tripPurpose: 1, mode: 1 },
        },
        matsimXMLs: {
          maxIn: 0, // maxIn 最大允许几个模块连接到当前模块，如果不设置该属性则无限制
          maxOut: 10, // maxIn 最大允许连接几个模块，如果不设置该属性则无限制
          // to 表示允许连接到哪些类型的模块，如果不设置该属性则无限制
          // to 配置示例：
          // 第一种：to: "matsim" 表示只能连接matsim类型模块，无次数限制
          // 第二种：to: ["matsim", "vehicle"] 表示只能连接matsim和vehicle类型模块，无次数限制
          // 第三种：to: {"matsim":1, "vehicle":2} 表示只能连接matsim和vehicle类型模块，最多只能连接1个matsim模块，最多只能连接2个vehicle模块
          to: { matsim: 1, networkDisplay: 1 },
        },
        matsim: {
          maxIn: 10,
          maxOut: 1,
          // from 表示允许被哪些模块连接，如果不设置该属性则无限制
          // from 配置规则同to一样
          to: { OTFvis: 1 },
        },
        mode: {
          maxIn: 1,
          maxOut: 1,
          to: { timer: 1, activity: 1, facility: 1, tripPurpose: 1, mode: 1 },
        },
        network: {
          maxOut: 10,
          // from: {},
          to: { matsim: 1, networkDisplay: 1, chord: 1 },
        },
        odMatrix: {
          maxIn: 10,
          maxOut: 10,
          to: { matsim: 1, editTable: 1 },
        },
        openStreet: {
          maxOut: 10,
          to: { matsim: 1, networkDisplay: 1 },
        },
        person: {
          maxOut: 1,
          to: { activity: 1, facility: 1, mode: 1, tripPurpose: 1, timer: 1 },
        },
        region: {
          maxOut: 3,
          to: { matsim: 1, networkDisplay: 1, chord: 1 },
        },
        timer: {
          maxIn: 1,
          maxOut: 1,
          to: { timer: 1, activity: 1, facility: 1, tripPurpose: 1, mode: 1 },
        },
        tripPurpose: {
          maxIn: 1,
          maxOut: 1,
          to: { timer: 1, activity: 1, facility: 1, tripPurpose: 1, mode: 1 },
        },
      },
    },
  },

  saveConsole: {
    window: {
      id: "saveConsole",
      i18n: "saveConsole_title",
      title: "保存工作空间",
    },
    form: [
      { key: "id", i18n: "saveConsole_id", title: "ID", type: "text", show: false },
      { key: "name", i18n: "saveConsole_name", title: "保存名称", type: "text", default: "请输入保存名称", verify: "NO_NULL", error: "错误：此输入框不能为空！" },
      { key: "callType", i18n: "saveConsole_callType", title: "Call类型", type: "text", show: false },
      { key: "callParams", i18n: "saveConsole_callParams", title: "Call参数", type: "text", show: false },
      { key: "shapeId", i18n: "saveConsole_shapeId", title: "图形ID", type: "text", show: false },
    ],
    buttons: [{ id: "save", i18n: "confirmSave_btn", type: "saveConsole", text: "确认保存", url: "/saveAndLoad/save" }],
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
    // { key: "verifyTest", title: "验证示例", type: "text", verify:"NO_NULL", error:"错误：此输入框不能为空！" },
    // // help 帮助信息显示 和 error 输入错误信息显示
    // //      help 属性： 点击 help 按钮的提示信息（显示在输入框下面）
    // //      error 属性： 验证输入框输入的数据与 verify 属性规定的不符时  显示的错误信息（显示在输入框下面）
    // { key: "hintMsgTest", title: "提示信息示例", type: "text", verify:"NO_NULL",
    //     help:"提示：这是一个点击帮助按钮的提示信息示例！",
    //     error:"错误：此输入框不能为空！"
    // },
    timer: {
      window: {
        id: "timer",
        i18n: "timer_title",
        title: "Timer",
      },
      form: [
        { key: "durTime", i18n: "timer_durTime", title: "活动持续时间", type: "text", value: "08:00:00", default: "hh:mm:ss", verify: "NO_NULL", help: "提示：输入期望出发时间，格式为hh:mm:ss", error: "错误：此输入框不能为空！" },
        { key: "endTime", i18n: "timer_endTime", title: "活动结束时间", type: "text", value: "18:00:00", default: "hh:mm:ss", verify: "NO_NULL", help: "提示：输入期望到达时间，格式为hh:mm:ss", error: "错误：此输入框不能为空！" },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    facility: {
      window: {
        id: "facility",
        i18n: "facility_title",
        title: "facility",
      },
      form: [
        {
          key: "facilityId",
          i18n: "facility_facilityId",
          title: "建筑ID",
          type: "text",
          default: "HIL",
          verify: "NO_NULL",
          help: "提示：输入建筑ID,此模块可作为activity模块+tripPurpose模块来用，如果没有facility设置文件请尽量使用activity模块代替",
          error: "错误：此输入框不能为空！",
        },
        {
          key: "actType",
          i18n: "facility_actType",
          title: "活动类型",
          type: "text",
          value: "work",
          help: "提示：输入在指定建筑的活动类型，需要与出行目的对应，指定此项后如再接入出行目的模块，将以此处添入但为准",
          error: "错误：活动类型需要与出行目的对应！",
        },
        {
          key: "facilityCoord",
          i18n: "facility_facilityCoord",
          title: "建筑位置",
          type: "text",
          default: "47.408765, 8.507441",
          help: "提示：建筑的地理坐标（经纬度），以逗号分割,如果没有上传facility文件则必须指定坐标",
          error: "错误：此输入框需输入经纬度，浮点类型",
        },
        { key: "facilityOpenDay", i18n: "facility_facilityOpenDay", title: "开放时间", type: "text", value: "weekday", help: "提示：建筑开放的周期时间，默认为工作日，可不填", error: "错误：此输入框不能为空！" },
        { key: "facilityOpenTime", i18n: "facility_facilityOpenTime", title: "开门时间", type: "text", value: "08:00:00", help: "提示：建筑的开门时间，格式为hh:mm:ss", error: "错误：此输入框不能为空！" },
        { key: "facilityCloseTime", i18n: "facility_facilityCloseTime", title: "关门时间", type: "text", value: "20:00:00", help: "提示：建筑关门时间，默认为hh:mm:ss", error: "错误：此输入框不能为空！" },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },

    openStreet: {
      window: {
        id: "openStreet",
        i18n: "openStreet_title",
        title: "openStreet",
      },
      form: [
        {
          key: "minLong",
          i18n: "openStreet_minLong",
          title: "最小经度",
          type: "text",
          value: "120.1910",
          verify: "NO_NULL",
          help: '提示：请到 <a href="https://www.openstreetmap.org/export#map=4/36.99/119.58" target="_blank">OPEN STREET MAP</a> 获取并选择经纬度信息',
          error: "错误：输入经度，double类型类型,范围：[0,180]！",
        },
        { key: "maxLong", i18n: "openStreet_maxLong", title: "最大经度", type: "text", value: "120.7831", verify: "NO_NULL", error: "错误：输入经度，double类型,范围：[0,180]！" },
        { key: "minLati", i18n: "openStreet_minLati", title: "最小纬度", type: "text", value: "36.2279", verify: "NO_NULL", error: "错误：输入纬度，double类型类型,范围：[0,180]！" },
        { key: "maxLati", i18n: "openStreet_maxLati", title: "最大纬度", type: "text", value: "36.5478", verify: "NO_NULL", error: "错误：输入纬度，double类型类型,范围：[0,180]！" },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    network: {
      window: {
        id: "network",
        i18n: "network_title",
        title: "network",
      },
      form: [
        {
          key: "networkDefaultEPSG",
          i18n: "network_networkDefaultEPSG",
          title: "默认坐标系",
          type: "span",
          value: "EPSG：4326",
          default: "EPSG code",
          help: '提示：将投影坐标系通过<a href=http://epsg.io" target="_blank">EPSG </a> 查找相应code 填入，暂时不可修改',
          error: "错误：请输入EPSG code！",
        },
        { key: "networkDesiredEPSG", i18n: "network_networkDesiredEPSG", title: "期望坐标系", type: "span", value: "EPSG：4326", default: "EPSG code", help: "提示：现阶段只支持WGS 84，暂时不可修改", error: "错误：请输入EPSG code！" },
        {
          key: "linkDir",
          i18n: "network_linkDir",
          title: "指定方向字段名",
          type: "text",
          value: "null",
          verify: "NO_NULL",
          help: "提示：输入路段方向字段名（字段名区分大小写，字段内容为整数格式）请统一利用GIS软件按照 0-双向道路(默认)，<0 反向单行，>0正向单行 修改后上传",
          error: "错误：未找到相应字段！",
        },
        { key: "linkSpeed", i18n: "network_linkSpeed", title: "指定最大速度字段名", type: "text", value: "null", verify: "NO_NULL", help: "提示：输入路段限速字段名 （米／秒），字段内容为浮点类型", error: "错误：未找到相应字段！" },
        { key: "linkLength", i18n: "network_linkLength", title: "指定路线长度名", type: "text", value: "null", verify: "NO_NULL", help: "提示：输入路段长度（米），浮点类型", error: "错误：未找到相应字段！" },
        { key: "linkLane", i18n: "network_linkLane", title: "指定单向车道数字段名", type: "text", value: "null", verify: "NO_NULL", help: "提示：输入路段单向车道数字段名，整数类型", error: "错误：未找到相应字段！" },
        { key: "linkCapacity", i18n: "network_linkCapacity", title: "指定单向总通行能力字段名", type: "text", value: "null", verify: "NO_NULL", help: "提示：输入路段单向总通行能力字段名，浮点类型", error: "错误：未找到相应字段！" },
        {
          key: "linkMode",
          i18n: "network_linkMode",
          title: "指定交通方式字段名",
          type: "text",
          value: "null",
          verify: "NO_NULL",
          help: "提示：输入路段允许的交通方式字段名，与mode模块对应，字符串，内容有多个字段时用'，'分割",
          error: "错误：未找到相应字段！",
        },
        {
          key: "networkShpFile",
          i18n: "network_networkShpFile",
          title: "上传shp文件",
          type: "file",
          value: "",
          verify: "NO_NULL",
          help: "提示：shp文件通常有.shp,.shx,.prj和.dbf文件组成。由于路网格式转换时对数据要求比较高，指定的上述字段中不能处理空值或异常值，否则即使格式转化成功，在后续的处理中也会出现异常，请使用GIS软件进行批处理后再上传",
          error: "错误：未找到其他文件！",
          url: "/upload/networkShpFile",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
        {
          key: "networkShxFile",
          i18n: "network_networkShxFile",
          title: "上传shx文件",
          type: "file",
          value: "",
          verify: "NO_NULL",
          help: "提示：上传.shx文件",
          error: "错误：未找到其他文件！",
          url: "/upload/networkShxFile",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
        {
          key: "networkDbfFile",
          i18n: "network_networkDbfFile",
          title: "上传dbf文件",
          type: "file",
          value: "",
          verify: "NO_NULL",
          help: "提示：上传.dbf文件",
          error: "错误：未找到其他文件！",
          url: "/upload/networkDbfFile",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    region: {
      window: {
        id: "region",
        i18n: "region_title",
        title: "region",
      },
      form: [
        {
          key: "regionDefaultEPSG",
          i18n: "region_regionDefaultEPSG",
          title: "默认坐标系",
          type: "span",
          value: "EPSG：4326",
          default: "EPSG code",
          help: '提示：将投影坐标系通过<a href=http://epsg.io" target="_blank">OPEN STREET MAP</a>  查找EPSG code 填入，暂时不可修改',
          error: "错误：请输入EPSG code！",
        },
        { key: "regionDesiredEPSG", i18n: "region_regionDesiredEPSG", title: "期望坐标系", type: "span", value: "EPSG：4326", default: "EPSG code", help: "提示：输入期望转成的坐标系，必须使用投影坐标系", error: "错误：此输入框不能为空！" },
        {
          key: "regionId",
          i18n: "region_regionId",
          title: "指定regionID字段",
          type: "text",
          value: "EnglishID",
          verify: "NO_NULL",
          help: "提示：输入路网唯一ID标识，尽量使用英文（区分大小写！），解析时容易出错，同时路网文件应该由多边形组成",
          error: "错误：此输入框不能为空！",
        },
        {
          key: "regionShpFile",
          i18n: "region_regionShpFile",
          title: "上传shp文件",
          type: "file",
          value: "",
          verify: "NO_NULL",
          help: "提示：提示：shp文件通常有.shp,.shx,.prj和.dbf文件组成",
          error: "错误：未找到其他文件！",
          url: "/upload/regionShp",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
        {
          key: "regionShxFile",
          i18n: "region_regionShxFile",
          title: "上传shp附属文件",
          type: "file",
          value: "",
          verify: "NO_NULL",
          help: "提示：提示：shp文件通常有.shp,.shx,.prj和.dbf文件组成",
          error: "错误：未找到其他文件！",
          url: "/upload/regionShx",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
        {
          key: "regionDbfFile",
          i18n: "region_regionDbfFile",
          title: "上传shp附属文件",
          type: "file",
          value: "",
          verify: "NO_NULL",
          help: "提示：提示：shp文件通常有.shp,.shx,.prj和.dbf文件组成",
          error: "错误：未找到其他文件！",
          url: "/upload/regionDbf",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    odMatrix: {
      window: {
        id: "odMatrix",
        i18n: "odMatrix_title",
        title: "odMatrix",
      },
      form: [
        {
          key: "odFileType",
          i18n: "odMatrix_odFileType",
          title: "矩阵类型",
          type: "radio",
          value: "array",
          children: [
            { text: "方阵式", i18n: "odMatrix_odFileType_squareMatrix", value: "squareMatrix" },
            { text: "行列式", i18n: "odMatrix_odFileType_array", value: "array" },
          ],
          verify: "NO_NULL",
          help: "提示：方阵式第一行出行小区讫点ID，第一列为出行小区起点ID，第一行第一列为空",
          error: "错误：格式有误！",
        },
        {
          key: "odAsRegionOrCoord",
          i18n: "odMatrix_odAsRegionOrCoord",
          title: "OD格式",
          type: "radio",
          value: "asRegion",
          children: [
            { text: "交通小区", i18n: "odMatrix_odAsRegionOrCoord_asRegion", value: "asRegion" },
            { text: "坐标", i18n: "odMatrix_odAsRegionOrCoord_asCoord", value: "asCoord" },
          ],
          verify: "NO_NULL",
          help: "提示：如按照交通小区获取的OD则矩阵ID应为小区ID，如按照坐标输入则选择，如选择OD方阵，则此项只能选择交通小区！",
          error: "错误：格式有误！",
        },

        {
          key: "odFile",
          i18n: "odMatrix_odFile",
          title: "出行活动",
          type: "file",
          default: "",
          help: "提示：可为excel或逗号分割的txt或csv格式文件，建议使用文本文件,以逗号（非中文逗号！）分隔，请严格按照格式整理数据，否则后台解析会报错！！！",
          error: "错误：格式有误！",
          url: "/upload/odMatrix",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    mode: {
      window: {
        id: "mode",
        i18n: "mode_title",
        title: "mode",
      },
      form: [
        {
          key: "mode",
          i18n: "mode_mode",
          title: "mode",
          type: "radio",
          value: "car",
          children: [
            { text: "步行", i18n: "mode_mode_walk", value: "walk" },
            { text: "公交", i18n: "mode_mode_bus", value: "bus" },
            { text: "地铁", i18n: "mode_mode_subway", value: "subway" },
            { text: "自行车", i18n: "mode_mode_bike", value: "bike" },
            { text: "小汽车", i18n: "mode_mode_car", value: "car" },
            { text: "自定义", i18n: "mode_mode_userDefined", value: "userDefined" },
          ],
          verify: "NO_NULL",
          help: "",
          error: "错误：此输入框不能为空！",
        },
        // == != >= <= > <
        { key: "otherMode", i18n: "mode_otherMode", title: "自定义", type: "text", default: "请输入自定义值", if: "@mode==userDefined", help: "提示：输入自定义出行方式", error: "错误：格式有误！" },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    tripPurpose: {
      window: {
        id: "tripPurpose",
        i18n: "tripPurpose_title",
        title: "tripPurpose",
      },
      form: [
        {
          key: "tripPurpose",
          i18n: "tripPurpose_tripPurpose",
          title: "出行目的",
          type: "radio",
          value: "work",
          children: [
            { text: "工作", i18n: "tripPurpose_tripPurpose_work", value: "work" },
            { text: "娱乐", i18n: "tripPurpose_tripPurpose_entertainment", value: "entertainment" },
            { text: "购物", i18n: "tripPurpose_tripPurpose_shopping", value: "shopping" },
            { text: "运动", i18n: "tripPurpose_tripPurpose_sport", value: "sport" },
            { text: "其他", i18n: "tripPurpose_tripPurpose_userDefined", value: "userDefined" },
          ],
          verify: "NO_NULL",
          help: "",
          error: "错误：格式有误！",
        },
        { key: "otherPurpose", i18n: "tripPurpose_otherPurpose", title: "其他", type: "text", default: "请输入自定义值", if: "@tripPurpose==userDefined", help: "提示:出行目的", error: "错误：格式有误！" },

        {
          key: "tripPurposeDuration",
          i18n: "tripPurpose_tripPurposeDuration",
          title: "出行期望时长",
          type: "text",
          value: "8:00:00",
          help: "提示:此次出行持续时间，不计出行花费在路上的时间，如工作出行通常持续8小时，则填入8:00:00",
          error: "错误：格式有误！",
        },
        // {
        //     key: "tripPurposeType", title: "是否刚性出行", type: "radio", value: "necessary", children: [
        //         { text: "Yes", value: "necessary" }, { text: "No", value: "unnecessary" }
        //     ], verify: "NO_NULL", help: "提示：刚性出行是目的地和时间有限制的出行", error: "错误：格式有误！"
        // },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    activity: {
      window: {
        id: "activity",
        i18n: "activity_title",
        title: "activity",
      },
      form: [
        {
          key: "actDestinationType",
          i18n: "activity_actDestinationType",
          title: "出行目的地类型",
          type: "radio",
          value: "location",
          children: [
            { text: "出行目的地坐标", i18n: "activity_actDestinationType_location", value: "location" },
            { text: "出行交通小区ID", i18n: "activity_actDestinationType_regionId", value: "regionId" },
          ],
          verify: "NO_NULL",
          help: "提示：选择出行目的地信息",
          error: "错误：此输入框不能为空！",
        },
        {
          key: "actLocation",
          i18n: "activity_actLocation",
          title: "出行活动坐标",
          type: "text",
          default: "121.5066,31.2827",
          verify: "NO_NULL",
          if: "@actDestinationType==location",
          help: "提示：坐标格式为0.00(经度)，0.00（纬度）",
          error: "错误：请查看帮助并检查输入！",
        },
        {
          key: "actRegionId",
          i18n: "activity_actRegionId",
          title: "出行活动小区或建筑ID",
          type: "text",
          default: "Tongji University",
          verify: "NO_NULL",
          if: "@actDestinationType==regionId",
          help: "提示：小区ID或建筑ID需与上传的交通小区或建筑ID对应",
          error: "错误：请查看帮助并检查输入！",
        },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    person: {
      window: {
        id: "person",
        i18n: "person_title",
        title: "person",
      },
      form: [
        { key: "personId", i18n: "person_personId", title: "出行者ID", type: "text", value: "007", verify: "NO_NULL", help: "提示：需唯一，如果人数>1，则此输入作为前缀", error: "错误：请查看帮助并检查输入！" },
        { key: "personNum", i18n: "person_personNum", title: "出行者数量", type: "text", value: "1", verify: "NO_NULL", help: "提示：输入整型", error: "错误：输入格式有误！" },
        { key: "personAge", i18n: "person_personAge", title: "年龄/范围", type: "span", value: "forever 23", verify: "NO_NULL", help: "提示：如果出行者数量为一，则直接指定年龄，如果数量>1则输入年龄范围", error: "错误：请查看帮助并检查输入！" },
        { key: "personHouseholdSize", i18n: "person_personHouseholdSize", title: "家庭成员数", type: "span", value: "1", verify: "", help: "提示：输入成员数量", error: "错误：请查看帮助并检查输入！" },
        { key: "personCarNum", i18n: "person_personCarNum", title: "拥有车辆数", type: "span", value: "1", verify: "", help: "提示：输入拥有的小汽车数量", error: "错误：请查看帮助并检查输入！" },
        { key: "personDepTime", i18n: "person_personDepTime", title: "期望出发时间", type: "text", value: "08:00:00", verify: "", help: "提示：格式HH:MM:SS, or hh:mm:ssAM/PM", error: "错误：请查看帮助并检查输入！" },
        {
          key: "personLocationType",
          i18n: "person_personLocationType",
          title: "",
          type: "radio",
          value: "homeCoord",
          children: [
            { text: "家庭坐标", i18n: "person_personLocationType_homeCoord", value: "homeCoord" },
            { text: "小区ID", i18n: "person_personLocationType_regionId", value: "regionId" },
          ],
          verify: "NO_NULL",
          help: "提示：输入家庭所在位置",
          error: "错误：请查看帮助并检查输入！",
        },

        {
          key: "personLocationRegion",
          i18n: "person_personLocationRegion",
          title: "位置信息",
          type: "text",
          default: "ETH Zürich",
          verify: "NO_NULL",
          if: "@personLocationType==regionId",
          help: "提示：小区ID或建筑ID需与上传的交通小区或建筑ID对应",
          error: "错误：请查看帮助并检查输入！",
        },
        {
          key: "personLocationLocation",
          i18n: "person_personLocationLocation",
          title: "位置信息",
          type: "text",
          default: "47.37639, 8.54806",
          verify: "NO_NULL",
          if: "@personLocationType==homeCoord",
          help: "提示：坐标格式为0.00，0.00（longitude，latitude），范围0-180，精度通常至少要小数点后4位",
          error: "请查看帮助并检查输入！",
        },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    matsimXMLs: {
      window: {
        id: "matsimXMLs",
        i18n: "matsimXMLs_title",
        title: "matsimXMLs",
      },
      form: [
        {
          key: "networkXml",
          i18n: "matsimXMLs_networkXml",
          title: "路网文件",
          type: "file",
          default: "",
          verify: "",
          help: "提示：输入matsim路网xml文件",
          error: "错误：请查看帮助并检查输入！",
          url: "/upload/mastimNetworkXml",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
        {
          key: "activityXml",
          i18n: "matsimXMLs_activityXml",
          title: "出行活动",
          type: "file",
          default: "",
          verify: "",
          help: "提示：输入matsim活动xml文件",
          error: "错误：请查看帮助并检查输入！",
          url: "/upload/mastimActivityXml",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
        {
          key: "busScheduleXml",
          i18n: "matsimXMLs_busScheduleXml",
          title: "公交时刻表",
          type: "file",
          default: "",
          verify: "",
          help: "提示：输入matsim公交相关时刻表xml文件",
          error: "错误：请查看帮助并检查输入！",
          url: "/upload/mastimBusScheduleXml",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
        {
          key: "vehicleXml",
          i18n: "matsimXMLs_vehicleXml",
          title: "车辆配置",
          type: "file",
          default: "",
          verify: "",
          help: "提示：输入matsim车辆配置xml文件",
          error: "错误：请查看帮助并检查输入！",
          url: "/upload/mastimVehicleXml",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
        {
          key: "facilityXml",
          i18n: "matsimXMLs_facilityXml",
          title: "建筑设施配置",
          type: "file",
          default: "",
          verify: "",
          help: "提示：输入matsim建筑xml文件",
          error: "错误：请查看帮助并检查输入！",
          url: "/upload/mastimFacilityXml",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    matsim: {
      window: {
        id: "matsim",
        i18n: "matsim_title",
        title: "matsim",
        //style: "width:20%;margin-left:40%"
      },
      form: [
        { key: "iteration", i18n: "matsim_iteration", title: "迭代次数", type: "text", value: "1", default: "1", verify: "", help: "提示：建议设置为1或者10", error: "错误：请查看帮助并检查输入！" },
        {
          key: "hasBusScheduleXml",
          i18n: "matsim_hasBusScheduleXml",
          title: "是否配置公交信息",
          type: "radio",
          value: "false",
          children: [
            { text: "Yes", i18n: "matsim_hasBusScheduleXml_true", value: "true" },
            { text: "No", i18n: "matsim_hasBusScheduleXml_false", value: "false" },
          ],
          verify: "NO_NULL",
          error: "错误：此输入框不能为空！",
        },
        {
          key: "hasVehicleXml",
          i18n: "matsim_hasVehicleXml",
          title: "车辆",
          type: "radio",
          value: "false",
          children: [
            { text: "Yes", i18n: "matsim_hasVehicleXml_true", value: "true" },
            { text: "No", i18n: "matsim_hasVehicleXml_false", value: "false" },
          ],
          verify: "NO_NULL",
          error: "错误：此输入框不能为空！",
        },
        {
          key: "hasFacilityXml",
          i18n: "matsim_hasFacilityXml",
          title: "建筑设施功能",
          type: "radio",
          value: "false",
          children: [
            { text: "Yes", i18n: "matsim_hasFacilityXml_true", value: "true" },
            { text: "No", i18n: "matsim_hasFacilityXml_false", value: "false" },
          ],
          verify: "NO_NULL",
          error: "错误：此输入框不能为空！",
        },
        {
          key: "hasConfigXml",
          i18n: "matsim_hasConfigXml",
          title: "是否有运行配置文件",
          type: "radio",
          value: "false",
          children: [
            { text: "Yes", i18n: "matsim_hasConfigXml_true", value: "true" },
            { text: "No", i18n: "matsim_hasConfigXml_false", value: "false" },
          ],
          verify: "NO_NULL",
          error: "错误：此输入框不能为空！",
        },
        {
          key: "configXml",
          i18n: "matsim_configXml",
          title: "运行配置文件",
          type: "file",
          default: "",
          verify: "",
          help: "提示：上传config文件,无需设置输入及输出文件路径",
          if: "@hasConfigXml==true",
          error: "错误：请查看帮助并检查输入！",
          url: "/upload/mastimConfigXml",
          fileName: "fileBtn",
          upload: "custom.console.call.upload",
        },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
    vehicle: {
      window: {
        id: "vehicle",
        i18n: "vehicle_title",
        title: "vehicle",
      },
      form: [
        { key: "vehicleType", i18n: "vehicle_vehicleType", title: "车辆类型", type: "text", value: "bus", default: "", verify: "", help: "提示：输入车辆类型", error: "错误：请查看帮助并检查输入！" },
        { key: "vehicleSeatNum", i18n: "vehicle_vehicleSeatNum", title: "座位数", type: "text", default: "", value: "50", verify: "", help: "提示：车辆座位数", error: "错误：请查看帮助并检查输入！" },
        { key: "vehicleStandNum", i18n: "vehicle_vehicleStandNum", title: "可站人数", type: "text", default: "", value: "25", verify: "", help: "提示：可站立的人数", error: "错误：请查看帮助并检查输入！" },
        { key: "vehicleLength", i18n: "vehicle_vehicleLength", title: "车辆长度", type: "text", default: "", value: "15", verify: "", help: "提示：车辆长度（米）", error: "错误：请查看帮助并检查输入！" },
        { key: "vehicleWidth", i18n: "vehicle_vehicleWidth", title: "车辆宽度", type: "text", default: "", value: "2.5", verify: "", help: "提示：车辆长度（米）", error: "错误：请查看帮助并检查输入！" },
        { key: "vehicleMaxSpeed", i18n: "vehicle_vehicleMaxSpeed", title: "最大速度", type: "text", default: "km/h", value: "60", verify: "", help: "提示：最大车速（km/h）", error: "错误：请查看帮助并检查输入！" },
        { key: "vehicleDes", i18n: "vehicle_vehicleDes", title: "车辆描述", type: "text", default: "", value: "This is a bus", verify: "", help: "提示：车辆用途，描述", error: "错误：请查看帮助并检查输入！" },
        { key: "vehicleAsCar", i18n: "vehicle_vehicleAsCar", title: "当量值", type: "text", default: "", value: "1.0", verify: "", help: "提示：转化成当量小汽车的值", error: "错误：请查看帮助并检查输入！" },
      ],
      buttons: [
        { id: "finish", i18n: "finish_btn", type: "finish", text: "完成" },
        { id: "help", i18n: "help_btn", type: "help", text: "帮助" },
      ],
    },
  },

  gridWindows: {
    loadAll: {
      window: {
        id: "loadAll",
        i18n: "loadAll_title",
        title: "加载存档",
      },
      grid: {
        col: 5,
        elements: {
          url: "/saveAndLoad/loadAll",
          width: 1,
          height: 1,
        },
      },
    },
    example: {
      window: {
        id: "example",
        i18n: "example_title",
        title: "example",
      },
      grid: {
        col: 5,
        elements: {
          url: "/saveAndLoad/loadExample",
          width: 1,
          height: 1,
        },
      },
    },
    trafficData: {
      window: {
        id: "trafficData",
        i18n: "trafficData_title",
        title: "trafficData",
      },
      grid: {
        col: 5,
        elements: {
          url: "/saveAndLoad/loadExample",
          width: 1,
          height: 1,
        },
      },
    },
    // example: {
    //     window: {
    //         id: "example",
    //         title: "案例模块"
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
    //             { key: "14", text: "14", width: 1, height: 1 }
    //         ]
    //     }
    // }
  },
});

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
//{ key: "verifyTest", title: "验证示例", type: "text", verify:"NO_NULL", error:"错误：此输入框不能为空！" },
// help 帮助信息显示 和 error 输入错误信息显示
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

function getI18nByConfig() {
  let list = [configs];
  let i18nList = [];
  while (list.length > 0) {
    let item = list.shift();
    for (let key in item) {
      let item2 = item[key];
      if (typeof item2 === "object") {
        delete item[key];
        list.unshift(item2);
      }
    }
    if (item.i18n) {
      var value = {}
      if(item.text) value.text = item.text;
      if(item.title) value.title = item.title;
      if(item.default) value.default = item.default;
      if(item.help) value.help = item.help;
      if(item.error) value.error = item.error;
      i18nList.push([item.i18n, value]);
    }
  }
  i18nList.reverse();
  var i18n = {};
  for (const [key, value] of i18nList) {
    i18n[key] = value;
  }
  return i18n;
}
