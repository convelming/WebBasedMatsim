var custom = ui.custom;

// common 公共回调函数
custom.common = {};
custom.common.drawBoardId = "";
custom.common.zIndex = 1;
custom.common.drawBoardToolClick = function (params) { // 工具栏点击回调函数
    var rectParams = {
        elements: [{
            type: "rect",
            property: { width: "46px", height: "46px", x: "2", y: "2", fill: "#FFF", stroke: "#33CCCC", "stroke-width": "3" }
        }]
    }
    var circleParams = {
        elements: [{ type: "circle",
            property: { r: "23", cx: "25", cy: "25", zIndex: custom.common.zIndex + "", fill: "#FFF", stroke: "#33CCCC", "stroke-width": "3" }
        }]
    }

    var svgParams;
    if (params.shape == "circle") svgParams = circleParams;
    else svgParams = rectParams;

    if (params.icon) svgParams.elements.push(
        { type: "image", text: params.text, property: { "xlink:href": params.icon, x: "15%", y: "15%", width: "70%", height: "70%" } });
    else svgParams.elements.push({ type: "text", text: params.text, property: { fill: "#FFF" } });

    var svgObj = ui.modules.svg.build(svgParams);
    if (parseInt(params.left) > 80) {
        var shapeParams = {
            // id: "", title: "",
            base: { top: params.top, left: params.left, width: "50px", height: "50px", zIndex: custom.common.zIndex + "" },
            body: svgObj.config,
            call: { dblclick: { fn: custom.common.shapeClick, params: { key: params.key } } },
            other: { svgId: svgObj.id, svgShapeId: svgObj.params.elements[0].id, status: 0, type:params.key }
        }
        if (params.leftPoint) shapeParams.leftPoint = params.leftPoint;
        if (params.rightPoint) shapeParams.rightPoint = params.rightPoint;
        ui.modules.drawBoard.shape.create(custom.common.drawBoardId, shapeParams);
        custom.common.zIndex = custom.common.zIndex + 1;
    }
}
custom.common.toolClick = function (params) {   // 画板工具条的工具点击回调函数
    if (params.type == "start") {
        var topo = ui.modules.drawBoard.getTopo(custom.common.drawBoardId);
        topo.shift(); // 删除第一个元素，工具条元素
        console.log("topo:", topo);
        for (var index = 0; index < topo.length; index++) {
            if (topo[index].status != 1) {
                alert("请设置好所有模块再运行！");
                return;
            }
        }
        $.post(params.url, topo, function (data) {
            alert("提交成功！");
            console.log("data",data);
        });
    } else if (params.type == "save") {
        var topo = ui.modules.drawBoard.getTopo(custom.common.drawBoardId);
        topo.shift(); // 删除第一个元素，工具条元素
        console.log("topo:", topo);
        $.post(params.url, topo, function (data) {
            alert("保存成功！");
            console.log("data",data);
        });
    } else if (params.type == "delete") {
        ui.modules.drawBoard.connect.deleteAll(params.id);
    }
}
custom.common.shapeClick = function (params) {   // 画板图形点击回调函数
    if (ui.modules.window.verify(params.key) == false) {
        if (configs.formWindows[params.key]) ui.custom.formWindow(configs.formWindows[params.key]);
        else ui.custom.customWindow(configs.customWindow[params.key]);
    }
    ui.modules.window.change(params.key, undefined, undefined, { shapeId: this.id })
    ui.modules.window.open(params.key);
}
custom.common.windowOpenCall = function () {   // 打开form window回调函数
    var formId = this.params.other.formId;
    var shapeId = this.params.other.shapeId;
    var shapeConfig = ui.modules.drawBoard.shape.getConfig(custom.common.drawBoardId, shapeId);
    ui.modules.form.removeData(formId);
    if (shapeConfig) {
        var other = shapeConfig.other;
        ui.modules.form.setData(formId, other);
    }
}
custom.common.shapeFormSubmitFn = function (params) { // 画板图形点击完成回调函数
    var windowParams = ui.modules.window.getConfig(params.windowId);
    var formId = windowParams.other.formId;
    var shapeId = windowParams.other.shapeId;
    var result = ui.modules.form.verifyData(formId);
    //console.log(result);

    var shapeConfig = ui.modules.drawBoard.shape.getConfig(custom.common.drawBoardId, shapeId);
    var svgId = shapeConfig.other.svgId;
    var svgShapeId = shapeConfig.other.svgShapeId;
    for (var key in result.data)
        if (result.data.hasOwnProperty(key))
            shapeConfig.other[key] = result.data[key];

    if (result.total) {
        var svgShapeFill = "#01DF3A";
        shapeConfig.other.status = 1;
    } else {
        svgShapeFill = "#EE0000";
        shapeConfig.other.status = -1;
    }
    ui.modules.svg.element.change(svgId, svgShapeId, { fill: svgShapeFill });
    ui.modules.window.close(params.windowId);
}
custom.common.shapeFormhelpMsgFn = function (formId) { // 画板图形点击帮助回调函数
    ui.modules.form.elementHelpMsg(formId, "auto");
}

// 绘制自定义弹窗
custom.customWindow = function (config) {
    var customParam = config.custom;
    var testCustomObj = ui.modules.custom.build(customParam);

    var windowParam = config.window;
    windowParam.body = [testCustomObj.config];
    var testWindowObj = ui.modules.window.build(windowParam);
    var testWindowHtml = ui.build.buildModule(testWindowObj.config);

    $("#windows").append(testWindowHtml);
}

// 绘制路网弹窗
custom.gridWindow = function (config) {
    var gridParam = config.grid;
    for (var index = 0; index < gridParam.elements.length; index++) {
        var gridElement = gridParam.elements[index];
        gridElement.call = {};
        gridElement.call.click = {};
        gridElement.call.click.fn = custom.common.drawBoardToolClick;
        gridElement.call.click.params = gridElement;
    }

    var testGridObj = ui.modules.grid.build(gridParam);
    testGridObj.config = [ui.build.tags.brConfig[0], testGridObj.config]; // 添加换行

    var windowParam = config.window;
    windowParam.body = testGridObj.config;
    var testWindowObj = ui.modules.window.build(windowParam);
    var testWindowHtml = ui.build.buildModule(testWindowObj.config);

    $("#windows").append(testWindowHtml);
}

// 绘制form弹窗
custom.formWindow = function (config) {

    var bodyConfig = [];
    bodyConfig.push(ui.build.tags.brConfig[0]); // 添加换行

    var formParam = { id: config.window.id, elements: config.form };
    var testformObj = ui.modules.form.build(formParam);
    bodyConfig.push(testformObj.config);

    var buttonsParam = { buttons: config.buttons };
    for (var buttonsIndex = 0; buttonsIndex < buttonsParam.buttons.length; buttonsIndex++) {
        var button = buttonsParam.buttons[buttonsIndex];
        if (button.type) {
            button.call = {};
            button.call.click = {};
            button.call.click.fn = button.type == "submit" ? custom.common.shapeFormSubmitFn : custom.common.shapeFormhelpMsgFn;
            button.call.click.params = { windowId: config.window.id };
        }
    }
    var testbuttonsObj = ui.modules.buttons.build(buttonsParam);
    bodyConfig.push(ui.build.tags.brConfig[0]);
    bodyConfig.push(testbuttonsObj.config);

    var windowParam = config.window;
    windowParam.body = bodyConfig;
    windowParam.other = { formId: config.window.id }
    windowParam.call = { open: { fn: custom.common.windowOpenCall } }
    var testWindowObj = ui.modules.window.build(windowParam);
    var testWindowHtml = ui.build.buildModule(testWindowObj.config);

    $("#windows").append(testWindowHtml);
}

// 绘制图形面板
custom.drawBoard = function (config) {
    // 绘制画板
    var bars = config.tool.bars;
    for (var barsIndex = 0; barsIndex < bars.length; barsIndex++) {
        var tools = bars[barsIndex].elements;
        for (var toolsIndex = 0; toolsIndex < tools.length; toolsIndex++) {
            var tool = tools[toolsIndex];
            // if (barsIndex == 0) {
            //     tool.call = {};
            //     tool.call.click = {};
            //     tool.call.click.fn = custom.common.shapeClick;
            //     tool.call.click.params = { key: tool.key };
            // } else
            if (barsIndex <= 2) {
                tool.drag = true;
                tool.call = {};
                tool.call.drag = {};
                tool.call.drag.fn = custom.common.drawBoardToolClick;
                tool.call.drag.params = tool;
            }
            else { }
        }
    }
    var drawBoardObj = ui.modules.drawBoard.build(config);
    custom.common.drawBoardId = drawBoardObj.id;
    var drawBoardHtml = ui.build.buildModule(drawBoardObj.config);
    $("#draw_board").append(drawBoardHtml);

    // 绘制悬浮工具条
    var imageParams = {
        id: "tools",
        style: "cursor:pointer;",
        elements: [{
            id: "start", type: "image", click: true, property: { "xlink:href": "icon/start.svg", width: "30px", x: "0" },
            call: { click: { fn: custom.common.toolClick, params: { id: drawBoardObj.id, type: "start", url: config.submitUrl } } }
        }, {
            id: "save", type: "image", click: true, property: { "xlink:href": "icon/save.svg", width: "30px", x: "33" },
            call: { click: { fn: custom.common.toolClick, params: { id: drawBoardObj.id, type: "save", url: config.saveUrl } } }
        }, {
            id: "delete", type: "image", click: true, property: { "xlink:href": "icon/delete.svg", width: "30px", x: "67" },
            call: { click: { fn: custom.common.toolClick, params: { id: drawBoardObj.id, type: "delete" } } }
        }]
    }
    var imageObj = ui.modules.svg.build(imageParams);
    var toolsParams = {
        base: { top: "0px", left: "85%", width: "100px", height: "30px", zIndex: "9999" },
        body: imageObj.config,
        dblclick: false, drag: true, connect: false, zoom: false
    }
    ui.modules.drawBoard.shape.create(drawBoardObj.id, toolsParams);
}

custom.lineMap = {};
custom.lineMap.lines = function (config) {
    var config = {
        id: "map_lines",
        style: "width:100%;height:100%;",
        elements: [
            { property: { x1: "10", y1: "10", x2: "110", y2: "10" }, z: 0 },
            { property: { x1: "110", y1: "10", x2: "310", y2: "10" }, z: 0 },
            { property: { x1: "310", y1: "10", x2: "410", y2: "10" }, z: 0 },

            { property: { x1: "10", y1: "110", x2: "110", y2: "110" }, z: 0 },
            { property: { x1: "110", y1: "110", x2: "310", y2: "110" }, z: 0 },
            { property: { x1: "310", y1: "110", x2: "410", y2: "110" }, z: 0 },

            { property: { x1: "10", y1: "210", x2: "110", y2: "210" }, z: 0 },
            { property: { x1: "110", y1: "210", x2: "310", y2: "210" }, z: 0 },
            { property: { x1: "310", y1: "210", x2: "410", y2: "210" }, z: 0 },

            { property: { x1: "10", y1: "310", x2: "110", y2: "310" }, z: 0 },
            { property: { x1: "110", y1: "310", x2: "310", y2: "310" }, z: 0 },
            { property: { x1: "310", y1: "310", x2: "410", y2: "310" }, z: 0 },

            { property: { x1: "10", y1: "10", x2: "10", y2: "110" }, z: 0 },
            { property: { x1: "10", y1: "110", x2: "10", y2: "210" }, z: 0 },
            { property: { x1: "10", y1: "210", x2: "10", y2: "310" }, z: 0 },

            { property: { x1: "110", y1: "10", x2: "110", y2: "110" }, z: 0 },
            { property: { x1: "110", y1: "110", x2: "110", y2: "210" }, z: 0 },
            { property: { x1: "110", y1: "210", x2: "110", y2: "310" }, z: 0 },

            { property: { x1: "310", y1: "10", x2: "310", y2: "110" }, z: 0 },
            { property: { x1: "310", y1: "110", x2: "310", y2: "210" }, z: 0 },
            { property: { x1: "310", y1: "210", x2: "310", y2: "310" }, z: 0 },

            { property: { x1: "410", y1: "10", x2: "410", y2: "110" }, z: 0 },
            { property: { x1: "410", y1: "110", x2: "410", y2: "210" }, z: 0 },
            { property: { x1: "410", y1: "210", x2: "410", y2: "310" }, z: 0 }
        ]
    }

    var lineMapObj = ui.modules.lineMap.build(config);
    var lineMapHtml = ui.build.buildModule(lineMapObj.config);
    $("#line_map").append(lineMapHtml);
    ui.modules.lineMap.position.get("map_lines");
};
custom.lineMap.points = function (config) {
    var params = {
        id: "map_points", style: "",
        elements: [{
            id: "a",
            type: "circle",
            isClick: false,
            isDblclick: false,
            property: { r: "4", cx: 10, cy: 10, zIndex: "2", fill: "black", stroke: "black", "stroke-width": "2" },
            other: { path: [] }
        }, {
            id: "b",
            type: "circle",
            isClick: false,
            isDblclick: false,
            property: { r: "4", cx: 10, cy: 110, zIndex: "2", fill: "black", stroke: "black", "stroke-width": "2" },
            other: { path: [] }
        },
        {
            id: "c",
            type: "circle",
            isClick: false,
            isDblclick: false,
            property: { r: "4", cx: 10, cy: 210, zIndex: "2", fill: "black", stroke: "black", "stroke-width": "2" },
            other: { path: [] }
        }]
    }
    var svgObj = ui.modules.svg.build(params);
    var svgHtml = ui.build.buildModule(svgObj.config);
    $("#point_map").append(svgHtml);

    custom.lineMapTask = window.setInterval("ui.custom.lineMap.job()", 20); // 定时任务
};
//custom.lineMap.pointIndex = 1;
custom.lineMap.pointsObj = undefined;
custom.lineMap.job = function () {
    if (!custom.lineMap.pointsObj) custom.lineMap.pointsObj = ui.modules.svg.element.get("map_points");
    var pointsObj = custom.lineMap.pointsObj;
    for (var key in pointsObj) {
        if (pointsObj.hasOwnProperty(key)) {
            var pointObj = pointsObj[key];
            var currPoint = { x: pointObj.params.property.cx, y: pointObj.params.property.cy };
            var next = ui.modules.lineMap.position.getNextPoints("map_lines", pointObj.params.other.lineId, currPoint);
            if (next.length > 0) {
                var randomIndex = Math.round(Math.random() * 100) % next.length;
                pointObj.params.property.cx = next[randomIndex].x;
                pointObj.params.property.cy = next[randomIndex].y;
                pointObj.params.other.lineId = next[randomIndex].id;
                pointObj.params.other.path.push(next[randomIndex].id);
                custom.lineMap.chagngePoints("map_points", pointObj.id, next[randomIndex]);
                custom.lineMap.pointIndex += 1;
            } else {
                ui.modules.svg.element.remove("map_points", pointObj.id);
            }
        }
    }
}
custom.lineMap.jobStop = function () {
    window.clearInterval(custom.lineMapTask);
}
custom.lineMap.addPoint = function (point) {
    params = {
        type: "circle",
        isClick: false,
        isDblclick: false,
        property: { r: "4", cx: point.x, cy: point.y, zIndex: "2", fill: "black", stroke: "black", "stroke-width": "2" }
    }
    ui.modules.svg.element.add("map_points", params);
}
custom.lineMap.chagngePoints = function (id, elementId, point) {
    ui.modules.svg.element.change(id, elementId, { cx: point.x, cy: point.y });
}





