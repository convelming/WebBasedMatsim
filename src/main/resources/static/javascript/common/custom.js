ui.custom.console = {
  id: "",
  saveId: undefined,
  saveName: undefined,
  zIndex: 1,
  tools: {},
  outTools: false,
  init: function (config) {
    // 初始化控制台
    // 绘制画板
    config.region = true;
    var bars = config.tool.bars;
    for (var barsIndex = 0; barsIndex < bars.length; barsIndex++) {
      var tools = bars[barsIndex].elements;
      for (var toolsIndex = 0; toolsIndex < tools.length; toolsIndex++) {
        var tool = tools[toolsIndex];
        tool.call = {};
        if (barsIndex <= 1) {
          tool.drag = true;
          tool.call.drag = {};
          tool.call.drag.fn = ui.custom.console.call.leftToolClick;
          tool.call.drag.params = tool;
        } else if (barsIndex == 2) {
          tool.call.click = {};
          tool.call.click.fn = ui.custom.console.call.loadClick;
          tool.call.click.params = tool;
        } else if (barsIndex == 3) {
          tool.call.click = {};
          tool.call.click.fn = ui.custom.console.call.linkTo;
          tool.call.click.params = tool;
        }
        ui.custom.console.tools[tool.key] = tool;
      }
    }
    var drawBoardObj = ui.parts.drawBoard.build(config);
    ui.custom.console.id = drawBoardObj.id;
    ui.custom.console.statusColor = config.shape.statusColor;

    drawBoardObj.config.children[0].properties.mouseout = "ui.custom.console.call.mouseoutTools()";
    drawBoardObj.config.children[0].properties.mouseover = "ui.custom.console.call.mouseoverTools()";

    var drawBoardHtml = ui.build.buildModule(drawBoardObj.config);
    console.log(drawBoardObj.config, drawBoardHtml);
    $("#draw_board").append(drawBoardHtml);

    // 绘制悬浮工具条
    var imageParams = {
      id: "tools",
      style: "cursor:pointer;",
      elements: [
        {
          id: "start",
          type: "image",
          click: true,
          properties: { "xlink:href": "icon/start.svg", width: "30px", x: "0" },
          url: config.runUrl,
          call: { click: { fn: ui.custom.console.call.topToolClick.save, params: { callType: "run", callParams: config.runUrl } } },
          // call: { click: { fn: ui.custom.console.call.topToolClick.start } }
        },
        {
          id: "save",
          type: "image",
          click: true,
          properties: { "xlink:href": "icon/save.svg", width: "30px", x: "33" },
          url: config.saveUrl,
          call: { click: { fn: ui.custom.console.call.topToolClick.save } },
          // }, {
          //     id: "saveAs", type: "image", click: true, properties: { "xlink:href": "icon/saveAs.svg", width: "30px", x: "67" },
          //     call: { click: { fn: ui.custom.console.call.topToolClick.save, params: { id: drawBoardObj.id, url: config.saveUrl } } }
          // }, {
          //     id: "plus", type: "image", click: true, properties: { "xlink:href": "icon/larger.svg", width: "30px", x: "133" },
          //     call: { click: { fn: ui.custom.console.call.topToolClick.zoom, params: { id: drawBoardObj.id, width:"100", height:"100" } } }
        },
        {
          id: "load",
          type: "image",
          click: true,
          properties: { "xlink:href": "icon/load.svg", width: "30px", x: "67" },
          call: { click: { fn: ui.custom.console.call.loadClick, params: { key: "loadAll" } } },
        },
        {
          id: "delete",
          type: "image",
          click: true,
          properties: { "xlink:href": "icon/delete.svg", width: "30px", x: "100" },
          call: { click: { fn: ui.custom.console.call.topToolClick.delete, params: { id: drawBoardObj.id } } },
        },
        {
          id: "user",
          type: "image",
          click: true,
          properties: { "xlink:href": "icon/user.svg", width: "30px", x: "130" },
          call: { click: { fn: ui.custom.console.call.topToolClick.open, params: "login/admin.html" } },
        },
      ],
    };
    var imageObj = ui.parts.svg.build(imageParams);
    var toolsParams = {
      base: { top: "0px", left: "80%", width: "200px", height: "30px", zIndex: "9" },
      body: imageObj.config,
      checked: false,
      dblclick: false,
      drag: true,
      connect: false,
      zoom: false,
    };
    ui.parts.drawBoard.shape.create(drawBoardObj.id, toolsParams);

    var saveId = ui.common.getQueryString("saveId");
    console.log("saveId", saveId);

    if (saveId) {
      $.ajax({
        type: "POST",
        url: config.loadUrl,
        // dataType: 'json',
        // contentType: 'application/json',
        data: { saveId: saveId },
        success: function (data) {
          if (data.success == true) {
            var item = data.data[0];
            ui.custom.console.saveId = item.saveId;
            ui.custom.console.saveName = item.saveName;
            var topo = JSON.parse(item.saveContent);
            ui.custom.console.loadTopo(topo.workspace.blocks);
          } else alert(data.errMsg);
        },
      });
    }
  },
  getTopo: function () {
    var drawBoard = ui.parts.drawBoard.getCurr(ui.custom.console.id);
    var returnData = [];
    if (drawBoard) {
      var shapes = drawBoard.shape;
      var connects = drawBoard.connects;
      for (var shapesKey in shapes) {
        if (shapes.hasOwnProperty(shapesKey)) {
          var shapeObj = shapes[shapesKey];
          var shapeTopo = {};
          shapeTopo.id = shapeObj.id;
          shapeTopo.position = {
            left: shapeObj.position.left,
            top: shapeObj.position.top,
            zIndex: shapeObj.position.zIndex,
          };
          shapeTopo.connects = [];
          for (var connectsKey in connects) {
            if (connects.hasOwnProperty(connectsKey)) {
              var connect = connects[connectsKey];
              if (connect.shapes[0].shapeId == shapeObj.id) shapeTopo.connects.push({ id: connect.id, to: connect.shapes[1].shapeId });
              if (connect.shapes[1].shapeId == shapeObj.id) {
                shapeTopo.connects.push({ id: connect.id, from: connect.shapes[0].shapeId });
              }
            }
          }
          if (shapeObj.params.other) for (var otherKey in shapeObj.params.other) if (shapeObj.params.other.hasOwnProperty(otherKey)) shapeTopo[otherKey] = shapeObj.params.other[otherKey];
          returnData.push(shapeTopo);
        }
      }
      return returnData;
    }
  },
  loadTopo: function (topo) {
    console.log("load topo:", topo);
    var copyPrefix = "";
    var connectsArr = [];
    for (var index = 0; index < topo.length; index++) {
      var shapeObj = topo[index];
      var shapeConfig = ui.custom.console.tools[shapeObj.type];
      if (shapeConfig) {
        //var shapeId = (isNaN(shapeObj.id)?"N":"") + shapeObj.id;
        ui.custom.console.call.leftToolClick({
          id: shapeObj.id,
          darg: true,
          key: shapeObj.type,
          status: shapeObj.status,
          top: shapeObj.position.top + "px",
          left: shapeObj.position.left + "px",
          text: shapeConfig.text,
          icon: shapeConfig.icon,
          shape: shapeConfig.shape,
          title: shapeConfig.title,
          leftPoint: shapeConfig.leftPoint,
          rightPoint: shapeConfig.rightPoint,
          linkTo: shapeConfig.linkTo,
          data: shapeObj,
        });
        var connects = shapeObj.connects;
        if (connects && connects.length > 0) connectsArr.push({ id: shapeObj.id, connects: connects });
      }
    }

    for (var connectsIndex = 0; connectsIndex < connectsArr.length; connectsIndex++) {
      var connectsObj = connectsArr[connectsIndex];
      var shapeId = connectsObj.id;
      var connects = connectsObj.connects;
      for (var connectIndex = 0; connectIndex < connects.length; connectIndex++) {
        var connect = connects[connectIndex];
        var fromId = shapeId;
        var toId = shapeId;
        if (connect.from && connect.from != null && connect.from != "" && connect.from != "null") fromId = connect.from;
        else if (connect.to && connect.to != null && connect.to != "" && connect.to != "null") toId = connect.to;
        ui.parts.drawBoard.connect.add(ui.custom.console.id, copyPrefix + fromId, copyPrefix + toId, copyPrefix + connect.id);
      }
    }
  },
  window: {
    custom: function (config) {
      //绘制自定义弹窗
      var customParam = config.custom;
      var testCustomObj = ui.parts.custom.build(customParam);

      var windowParam = config.window;
      windowParam.body = [testCustomObj.config];
      var testWindowObj = ui.parts.window.build(windowParam);
      var testWindowHtml = ui.build.buildModule(testWindowObj.config);

      $("#windows").append(testWindowHtml);
    },
    form: function (config) {
      //绘制form弹窗
      var bodyConfig = [];
      bodyConfig.push(ui.build.tags.brConfig[0]); // 添加换行

      var formParam = { id: config.window.id, elements: config.form };
      var testformObj = ui.parts.form.build(formParam);
      bodyConfig.push(testformObj.config);

      var buttonsParam = { buttons: config.buttons };
      for (var buttonsIndex = 0; buttonsIndex < buttonsParam.buttons.length; buttonsIndex++) {
        var button = buttonsParam.buttons[buttonsIndex];
        if (button.type) {
          button.call = {};
          button.call.click = {};
          button.call.click.fn = ui.custom.console.call.form[button.type];
          button.call.click.params = { windowId: config.window.id, button: button };
        }
      }
      var testbuttonsObj = ui.parts.buttons.build(buttonsParam);
      bodyConfig.push(ui.build.tags.brConfig[0]);
      bodyConfig.push(testbuttonsObj.config);

      var windowParam = config.window;
      windowParam.body = bodyConfig;
      windowParam.other = { formId: config.window.id };
      windowParam.call = { open: { fn: ui.custom.console.call.windowOpen } };
      var testWindowObj = ui.parts.window.build(windowParam);
      var testWindowHtml = ui.build.buildModule(testWindowObj.config);

      $("#windows").append(testWindowHtml);
    },
    grid: function (config) {
      //绘制网格弹窗
      var gridParam = config.grid;
      // { key: "1", text: "1", width: 1, height: 1 },
      if (gridParam.elements instanceof Array) {
        for (var index = 0; index < gridParam.elements.length; index++) {
          var gridElement = gridParam.elements[index];
          gridElement.call = {};
          gridElement.call.click = {};
          gridElement.call.click.fn = ui.custom.console.call.gridClick;
          gridElement.call.click.params = gridElement;
        }
        build(config);
      } else {
        var url = gridParam.elements.url;
        var params = gridParam.elements.params ? gridParam.elements.params : {};
        var width = gridParam.elements.width ? gridParam.elements.width : 1;
        var height = gridParam.elements.height ? gridParam.elements.height : 1;
        $.ajax({
          type: "POST",
          url: url,
          dataType: "json",
          contentType: "application/json",
          data: params,
          success: function (data) {
            if (data.success == true) {
              var list = data.data;
              gridParam.elements = [];
              for (var listIndex = 0; listIndex < list.length; listIndex++) {
                var item = list[listIndex];
                var element = {
                  key: item.saveId,
                  text: item.saveName,
                  width: item.width ? item.width : width,
                  height: item.height ? item.height : height,
                  call: {
                    click: {
                      fn: ui.custom.console.call.gridClick,
                      params: { key: item.saveId },
                    },
                  },
                };
                gridParam.elements.push(element);
              }
              build(config);
              ui.parts.window.open(config.window.id);
            } else {
              alert(data.errMsg);
            }
          },
        });
      }

      function build(gridConfig) {
        var config = gridConfig;
        var gridParam = gridConfig.grid;

        var testGridObj = ui.parts.grid.build(gridParam);
        testGridObj.config = [ui.build.tags.brConfig[0], testGridObj.config]; // 添加换行

        var windowParam = config.window;
        windowParam.body = testGridObj.config;
        var testWindowObj = ui.parts.window.build(windowParam);
        var testWindowHtml = ui.build.buildModule(testWindowObj.config);

        $("#windows").append(testWindowHtml);
      }
    },
  },
  call: {
    // 回调函数
    linkTo: function (params) {
      if (params.linkTo) {
        // window.open(params.linkTo+"?saveName=" + ui.custom.console.saveName+"&shapeId="+params.shapeId);
        var a = document.createElement("a");
        a.setAttribute("href", params.linkTo + "?saveName=" + ui.custom.console.saveName + "&shapeId=" + params.shapeId);
        a.setAttribute("target", "_blank");
        a.setAttribute("id", "shapeOpenTagId");
        if (!document.getElementById("shapeOpenTagId")) {
          document.body.appendChild(a);
        }
        a.click();
      }
    },
    gridClick: function (params) {
      window.open("./console.html?saveId=" + params.key, "_blank");
    },
    mouseoutTools: function (params) {
      ui.custom.console.outTools = true;
    },
    mouseoverTools: function (params) {
      ui.custom.console.outTools = false;
    },
    leftToolClick: function (params) {
      // 左边工具栏回调函数
      var statusColor = ui.custom.console.statusColor;
      var svgBgColor = statusColor[1];
      var svgFromData = params.data;
      if (params.status) svgBgColor = statusColor[parseInt(params.status) + 1];

      var shapeClickFn, shapeClickParams;
      if (params.linkTo) {
        shapeClickFn = ui.custom.console.call.topToolClick.save;
        shapeClickParams = { shapeId: params.key, callType: "linkTo", callParams: params.linkTo };
      } else {
        shapeClickFn = ui.custom.console.call.shapeClick;
        shapeClickParams = { key: params.key };
      }

      var rectParams = {
        elements: [
          {
            type: "rect",
            properties: { width: "46px", height: "46px", x: "2", y: "2", fill: svgBgColor, stroke: "#FF9966", "stroke-width": "3" },
          },
        ],
      };
      var circleParams = {
        elements: [
          {
            type: "circle",
            properties: {
              r: "23",
              cx: "25",
              cy: "25",
              zIndex: ui.custom.console.zIndex + "",
              fill: svgBgColor,
              stroke: "#FF9966",
              "stroke-width": "3",
            },
          },
        ],
      };

      var svgParams;
      if (params.shape == "circle") svgParams = circleParams;
      else svgParams = rectParams;

      if (params.icon) svgParams.elements.push({ type: "image", text: params.text, properties: { "xlink:href": params.icon, x: "15%", y: "15%", width: "70%", height: "70%", i18n: params.i18n + ".text.text" } });
      else svgParams.elements.push({ type: "text", text: params.text, properties: { fill: "#FFF", i18n: params.i18n + ".text.text" } });

      var svgObj = ui.parts.svg.build(svgParams);
      // if (parseInt(params.left) > 80) {
      if (!ui.custom.console.outTools) {
        var shapeParams = {
          type: params.key,
          base: { top: params.top, left: params.left, width: "50px", height: "50px", zIndex: ui.custom.console.zIndex + "" },
          body: svgObj.config,
          call: { dblclick: { fn: shapeClickFn, params: shapeClickParams } },
          other: { svgId: svgObj.id, svgShapeId: svgObj.params.elements[0].id, status: 0, type: params.key },
          //connect: params.connect?params.connect:undefined
        };
        if (params.id) shapeParams.id = params.id;
        if (params.status) shapeParams.other.status = params.status;
        if (params.leftPoint != undefined) shapeParams.leftPoint = params.leftPoint;
        if (params.rightPoint != undefined) shapeParams.rightPoint = params.rightPoint;
        if (svgFromData) {
          for (var svgFromDataKey in svgFromData) {
            if (svgFromData.hasOwnProperty(svgFromDataKey)) {
              var dataItem = svgFromData[svgFromDataKey];
              if (["id", "connects", "position", "type"].indexOf(svgFromDataKey) == -1) {
                shapeParams.other[svgFromDataKey] = dataItem;
              }
            }
          }
        }

        ui.parts.drawBoard.shape.create(ui.custom.console.id, shapeParams);
        ui.custom.console.zIndex = ui.custom.console.zIndex + 1;
      }
    },
    topToolClick: {
      start: function (params) {
        $("#wait").css("display", "block");
        var topo = ui.custom.console.getTopo();
        topo.shift(); // 删除第一个元素，工具条元素
        console.log("Submit topo:", topo);
        //ui.custom.console.loadTopo(topo);
        for (var index = 0; index < topo.length; index++) {
          if (topo[index].status < 1) {
            alert("请设置好所有模块再运行！");
            return;
          }
        }
        var submitData = {
          id: ui.custom.console.saveId,
          name: ui.custom.console.saveName,
          blocks: topo,
        };
        var url = params.url ? params.url : this.url;
        $.ajax({
          type: "POST",
          url: url,
          dataType: "json",
          contentType: "application/json",
          data: JSON.stringify(submitData),
          success: function (data) {
            // 返回的RequestResult的json对象
            alert(data.info);
            $("#wait").css("display", "none");
            // $("#draw_board").attr("style","display:show();");
            // $("#windows").attr("style","display:show();");
            console.log("data", data);
          },
        });
      },
      save: function (params) {
        if (ui.parts.window.verify("saveConsole") == false) {
          ui.custom.console.window.form(configs.saveConsole);
        }
        var saveId = ui.custom.console.saveId;
        var saveName = ui.custom.console.saveName;
        ui.parts.form.setData("saveConsole", {
          id: saveId ? saveId : "",
          name: saveName ? saveName : "",
          shapeId: params && params.shapeId ? params.shapeId : "",
          callType: params && params.callType ? params.callType : "none",
          callParams: params && params.callParams ? params.callParams : "none",
        });
        ui.parts.window.open("saveConsole");
      },
      delete: function (params) {
        if (confirm("确认删除吗？")) {
          ui.parts.drawBoard.connect.deleteAll(ui.custom.console.id);
          ui.parts.drawBoard.shape.deleteAll(ui.custom.console.id);
        }
      },
      zoom: function (params) {
        ui.parts.drawBoard.zoom(params.id, params.width, params.height);
      },
      open: function (params) {
        window.open(params);
      },
    },
    shapeClick: function (params) {
      // 图形点击回调函数
      console.log(2221);
      // 弹窗不存在则创建弹窗
      if (ui.parts.window.verify(params.key) == false) {
        if (configs.formWindows && configs.formWindows[params.key]) {
          ui.custom.console.window.form(configs.formWindows[params.key]);
        } else if (configs.customWindows && configs.customWindows[params.key]) {
          ui.custom.console.window.custom(configs.customWindows[params.key]);
        }
      }
      // form 点击时设置数据
      if (configs.formWindows && configs.formWindows[params.key]) {
        ui.parts.window.change(params.key, undefined, undefined, { shapeId: this.id });
      }
      // 打开弹窗
      ui.parts.window.open(params.key);
    },
    loadClick: function (params) {
      if (ui.parts.window.verify(params.key) == false) {
        ui.custom.console.window.grid(configs.gridWindows[params.key]);
        if (ui.parts.window.verify(params.key) == true)
          // 如果grid数据请求成功 打开window
          ui.parts.window.open(params.key);
      } else ui.parts.window.open(params.key);
    },
    windowOpen: function () {
      // 打开弹窗回调函数
      var formId = this.params.other.formId;
      var shapeId = this.params.other.shapeId;
      if (formId != "saveConsole") ui.parts.form.initData(formId);
      if (shapeId) {
        var shapeConfig = ui.parts.drawBoard.shape.getConfig(ui.custom.console.id, shapeId);
        if (shapeConfig) {
          var other = shapeConfig.other;
          ui.parts.form.setData(formId, other);
        }
      }
    },
    form: {
      saveConsole: function (params) {
        var topo = ui.custom.console.getTopo();
        console.log("Save topo:", topo);
        topo.shift(); // 删除第一个元素，工具条元素
        var result = ui.parts.form.verifyData("saveConsole");
        console.log(result.data);
        var callType = result.data.callType;
        var callParams = result.data.callParams;
        var submitData = { name: result.data.name, blocks: topo };
        if (ui.custom.console.saveId != null) submitData.id = ui.custom.console.saveId;
        console.log("Save form data: ", result);
        console.log("Save submit data:", submitData);
        $.ajax({
          type: "POST",
          url: this.url,
          dataType: "json",
          contentType: "application/json",
          data: JSON.stringify(submitData),
          success: function (data) {
            // 返回的RequestResult的json对象
            if (data.success) {
              alert(data.info);
              if (data.data.id) ui.custom.console.saveId = data.data.id;
              ui.custom.console.saveName = result.data.name;
              console.log("data", data);
              if (callType && callType != null && callType != "") {
                //console.log("-call:", callType, callParams);
                if (callType == "run") ui.custom.console.call.topToolClick.start({ url: callParams });
                else if (callType == "linkTo") ui.custom.console.call.linkTo({ shapeId: result.data.shapeId, linkTo: callParams });
              }
            } else alert(data.errMsg);
          },
        });
        ui.parts.window.close("saveConsole");
      },
      finish: function (params) {
        // 表单按钮点击回调函数
        var windowParams = ui.parts.window.getConfig(params.windowId);
        var formId = windowParams.other.formId;
        var shapeId = windowParams.other.shapeId;
        var result = ui.parts.form.verifyData(formId);
        console.log("Finish data:", result);

        var shapeConfig = ui.parts.drawBoard.shape.getConfig(ui.custom.console.id, shapeId);
        var svgId = shapeConfig.other.svgId;
        var svgShapeId = shapeConfig.other.svgShapeId;
        for (var key in result.data) if (result.data.hasOwnProperty(key)) shapeConfig.other[key] = result.data[key];

        if (result.total) shapeConfig.other.status = 1;
        else shapeConfig.other.status = -1;
        var svgShapeFill = ui.custom.console.statusColor[shapeConfig.other.status + 1];

        ui.parts.svg.element.change(svgId, svgShapeId, { fill: svgShapeFill });
        if (result.total) ui.parts.window.close(params.windowId);
      },
      help: function (params) {
        var formId = params.windowId;
        ui.parts.form.showHelp(formId, "auto");
      },
    },
    upload: function (data) {
      if (data && data.success == true) {
        console.log("Upload return data:", data);
        alert(data.info);
        var fileName = data.data;
        ui.parts.form.element.setData(this.formId, this.key, fileName);
      } else if (data && data.success != true) alert(data.errMsg);
      else alert("上传异常!");
    },
  },
};
