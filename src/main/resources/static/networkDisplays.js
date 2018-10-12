

// create svg
function createSvg(tag,tagAttr){
    var svgNS = "http://www.w3.org/2000/svg";
    var oTag = document.createElementNS(svgNS,tag);
    for(var attr in tagAttr){
        oTag.setAttribute(attr,tagAttr[attr]);
    };
    return oTag;
}
function parseXmlNetwork(networkFile){
    var data ={};
    var nodes = {};
    var links = {};
    $.ajax({
        type: "get",
        url: networkFile,//这里通过设置url属性来获取xml
        dataType: "xml",
        timeout: 1000,      //设定超时
        cache: true,       //禁用缓存
        async: false,
        success: function (xml) {//这里是解析xml
            var minX = Number.MAX_VALUE;
            var maxX = Number.MIN_VALUE;
            var minY = Number.MAX_VALUE;
            var maxY = Number.MIN_VALUE;

            var xmldom = $(xml);
            xmldom.find("node").each(function (i) {
                nodes[$(this).attr("id")] = {"x": $(this).attr("x"), "y": $(this).attr("y")};
                if (minX > $(this).attr("x")) { minX = $(this).attr("x");                }
                if (maxX < $(this).attr("x")) { maxX = $(this).attr("x");                }
                if (minY > $(this).attr("y")) { minY = $(this).attr("y");                }
                if (maxY < $(this).attr("y")) { maxY = $(this).attr("y");                }

            });
             console.log(minX + "," + minY + "," + maxX + "," + maxY);
            //
            xmldom.find("link").each(function (i) {
                // <link id="40h1" from="40" to="41" length="100.0" freespeed="27.8" capacity="800.0" permlanes="2.0" oneway="1" modes="bus,car" />
                console.log(i);
                links[$(this).attr("id")] = {
                    "fromNode": $(this).attr("from"), "fromNode": $(this).attr("from"),
                    "toNode": $(this).attr("to"), "length": $(this).attr("length"),
                    "freespeed": $(this).attr("freespeed"), "capacity": $(this).attr("capacity"),
                    "permlanes": $(this).attr("permlanes"), "modes": $(this).attr("modes")
                };
            });
            data = {
                "nodes":nodes,"links":links,
                "minX":minX,"minY":minY,"maxX":maxX,"maxY":maxY};
        }

    });
    return data;
}
// network.xml to svg this method transfer network.xml to svg and append to specified element id
function xmlNetwork2Svg(networkData) {

    // alert(networkData.maxY);
    // create svg network
    var svgHeight = networkData.maxY-networkData.minY;
    var svgWidth = networkData.maxX-networkData.minX;
    var scaleWidth = 2000/svgWidth;

    // alert(svgHeight +","+svgWidth);
    // var scaleFactorWidth = document.getElementById(appenedElmentId).offsetWidth/svgWidth;
    // var scaleFactorHeight = document.getElementById(appenedElmentId).offsetHeight/svgHeight;
    var networkSvg = createSvg("svg",{"id":"svgNetwork",
        "width":"100%",// svgWidth,
        "height": "100%",//svgHeight,
        // "width": svgWidth,
        // "height": svgHeight,
        "viewBox":"0 0 " +svgWidth*scaleWidth+ " "+svgHeight*scaleWidth,
        // "transform":"scale("+winX/svgWidth+","+winX/svgWidth+")",
       // "preserveAspectRatio":"xMinYMin meet",
        "xmlns":"http://www.w3.org/2000/svg",
        "xmlns:xlink":"http://www.w3.org/1999/xlink",
        //"transform":"scale("+scaleFactorWidth+","+scaleFactorHeight+")",
        // "viewbox":"0 0 400 400",
        //  "preserveAspectRatio":"xMidYMid meet"
    });


    // networkSvg.setAttribute("transform",1000);
    // networkSvg.setAttribute("height",800);
    // create backgroud color
    var backGround = createSvg("rect",{"id":"backGround",
        // "x":0,//-svgWidth*.05,
        // "y":0,//-svgHeight*.05,
        "width":"100%",//svgWidth,
        "height":"100%",//svgHeight,
        // "width":svgWidth,
        // "height":svgHeight,
          "viewBox":"0 0 " +svgWidth*scaleWidth+ " "+svgHeight*scaleWidth,
        // "transform":"scale("+winX/svgWidth+","+winX/svgWidth+")",
        // "preserveAspectRatio":"xMinYMin meet",
        "fill":"#000000"});
    // var networkGroup = document.createElement('networkGroup');
    networkSvg.append(backGround);

    for (var link in networkData.links){
        // console.log(links[link].fromNode)
        var fromX = (networkData.nodes[networkData.links[link].fromNode].x-networkData.minX);
        var fromY = (networkData.nodes[networkData.links[link].fromNode].y-networkData.minY);
        var toX = (networkData.nodes[networkData.links[link].toNode].x-networkData.minX);
        var toY = (networkData.nodes[networkData.links[link].toNode].y-networkData.minY);
        // console.log(link+fromX+", "+fromY+" ; "+toX+", "+toY);
        var tempLine = createSvg("line",{"id":link,"x1":fromX*scaleWidth,"y1":fromY*scaleWidth,"x2":toX*scaleWidth,"y2":toY*scaleWidth,"stroke":"orange","stroke-width":.3});
        // networkGroup.append(tempLine);
        networkSvg.append(tempLine);
    }
    return networkSvg;
}

function xmlEvents2Svg(eventFile,networkData,appendElementId){

    var networkSvg = xmlNetwork2Svg(networkData,appendElementId);
    var svgHeight = networkData.maxY-networkData.minY;
    var svgWidth = networkData.maxX-networkData.minX;
    var scaleWidth = 2000/svgWidth;
    var vehicleEvents = [];
    $.ajax({
        type:"get",
        url:eventFile,//这里通过设置url属性来获取xml
        dataType:"xml",
        timeout:1000,      //设定超时
        cache:true,
        async: false,
        success:function(xml){//这里是解析xml
            $(xml).find("event").each(function(i){

                // console.log($(this));
                // if (type=="left link"||type=="entered link"){
                //     // console.log(time+", "+type+","+i);
                // }

                if ($(this).attr("vehicle")!="undefined"&&
                    (($(this).attr("type")=="left link"||$(this).attr("type")=="entered link"
                        ||$(this).attr("type")=="vehicle leaves traffic")||$(this).attr("type")=="vehicle enters traffic")){
                    if (vehicleEvents[$(this).attr("vehicle")] instanceof Array) {
                        // console.log(vehicleEvents[$(this).attr("vehicle")]);
                        vehicleEvents[$(this).attr("vehicle")].push({
                            "actTime":$(this).attr("time"),"actType":$(this).attr("type"),"linkId":$(this).attr("link")
                        });

                    } else  {
                        vehicleEvents[$(this).attr("vehicle")] = [{
                            "actTime": $(this).attr("time"), "actType": $(this).attr("type"), "linkId": $(this).attr("link")
                        }];
                    }
                }
            });
        }
    });
    /*{"icon":"svg circle",
       "mpath":"mx1,y1 x2,y2 ... xEnd,yEnd",
       "beginTime":xxx,
       "during":xxx
    }
    <animateMotion
           xlink:href="#car"
           dur="3s"
           begin="0s"
           fill="freeze"
           keyPoints="0;0.25;0.5;0.75;1"
           keyTimes="0;0.1;0.7;0.72;1"
           calcMode="linear"
           rotate="auto">
    <mpath xlink:href="#motionPath" />
    You also need to explicitly set calcMode="linear" for the key values to have an effect.
    https://css-tricks.com/guide-svg-animations-smil/
    */
    function sortByTimeThenType(a, b) {   //先排序a 字段   然后排序b 字段
        const first = a.actTime - b.actTime;
        var order = 1;
        if (first !== 0) {
            return first;
        }else  if (a.actType > b.actType) {
                return 0-order;
            } else if (a.actType < b.actType) {
                return order;
            } else {
                return 0;
            }
    }
    var vehiclesSvg = [];// [{vehicleId:{begin:begintime,
                                    //     path:[linkFromCoord1,....link],
                                    //     linkLengths:[link1,]
                                    // linktravelTime:[],// }},]

    for(var vehicle in vehicleEvents){
        vehicleEvents[vehicle].sort(sortByTimeThenType);
        vehiclesSvg[vehicle]= {};
        vehiclesSvg[vehicle].begin = vehicleEvents[vehicle][0].actTime;

        // // console.log(vehicle + ", "+JSON.stringify(orderedVehicleEvent));
        // for (var i = 1;i<vehicleEvents[vehicle].length;i++) {
        //     // if (i%2==0) {
        //         console.log(vehicle + "," + JSON.stringify(vehicleEvents[vehicle][i]));
        // }

        for (var i = 1;i<vehicleEvents[vehicle].length;i++) {

        // console.log(vehicleEvents[vehicle][i].actType+", "+vehicleEvents[vehicle][i-1].actType+", "+vehicleEvents[vehicle][i-2].actType+", "+
        //     vehicleEvents[vehicle][i-1].time+", "+vehicleEvents[vehicle][i-2].time);
            if((vehicleEvents[vehicle][i].actType=="left link"||vehicleEvents[vehicle][i].actType=="vehicle leaves traffic")
                &&(vehicleEvents[vehicle][i-1].actType=="entered link"||vehicleEvents[vehicle][i-1].actType=="vehicle enters traffic")
                    &&vehicleEvents[vehicle][i].linkId==vehicleEvents[vehicle][i-1].linkId){

                // console.log("vehicle:"+ vehicle + " travels " + (vehicleEvents[vehicle][i].actTime -vehicleEvents[vehicle][i-1].actTime)+ "s on link: " + vehicleEvents[vehicle][i].linkId);

                if (vehiclesSvg[vehicle].path instanceof Array) {
                    vehiclesSvg[vehicle].path.push(vehicleEvents[vehicle][i].linkId);
                } else  {
                    vehiclesSvg[vehicle].path = [vehicleEvents[vehicle][i].linkId];
                }

                var fromNode = networkData.nodes[networkData.links[vehicleEvents[vehicle][i].linkId].fromNode];
                var toNode = networkData.nodes[networkData.links[vehicleEvents[vehicle][i].linkId].toNode];
                if (vehiclesSvg[vehicle].points instanceof Array) {

                    if (vehiclesSvg[vehicle].points[vehiclesSvg[vehicle].points.length-1]==(fromNode.x+","+fromNode.y)){
                        // alert(vehiclesSvg[vehicle].points[vehiclesSvg[vehicle].points.length-1]+">>>>"+fromNode.x+","+fromNode.y);
                        vehiclesSvg[vehicle].points.push(toNode.x*scaleWidth+","+toNode.y*scaleWidth);

                        //console.log("218" + JSON.stringify(vehiclesSvg[vehicle].points[vehiclesSvg[vehicle].points.length-1])+","+JSON.stringify(fromNode))
                    }
                    else{
                        vehiclesSvg[vehicle].points.push(fromNode.x*scaleWidth+","+fromNode.y*scaleWidth);
                        vehiclesSvg[vehicle].points.push(toNode.x*scaleWidth+","+toNode.y*scaleWidth);
                        //alert(vehicle+","+vehiclesSvg[vehicle].points[vehiclesSvg[vehicle].points.length-1]+">>>>"+fromNode.x+","+fromNode.y);
                    }
                } else  {
                    vehiclesSvg[vehicle].points = [fromNode.x*scaleWidth+","+fromNode.y*scaleWidth];
                    vehiclesSvg[vehicle].points.push(toNode.x*scaleWidth+","+toNode.y*scaleWidth);
                }

                if (vehiclesSvg[vehicle].linkTravelTime instanceof Array) {
                    vehiclesSvg[vehicle].linkTravelTime.push(parseFloat((vehicleEvents[vehicle][i].actTime -vehicleEvents[vehicle][i-1].actTime)));
                } else  {
                    vehiclesSvg[vehicle].linkTravelTime = [parseFloat(vehicleEvents[vehicle][i].actTime -vehicleEvents[vehicle][i-1].actTime)];
                }

                if (vehiclesSvg[vehicle].linkLengths instanceof Array) {
                    vehiclesSvg[vehicle].linkLengths.push(parseFloat(networkData.links[vehicleEvents[vehicle][i].linkId].length));
                } else  {
                    vehiclesSvg[vehicle].linkLengths = [parseFloat(networkData.links[vehicleEvents[vehicle][i].linkId].length)];
                }
            }
             // console.log( vehicle + ": "+vehiclesSvg[vehicle].points.join(" ")) ;
        }

    }
    // create svg
    var winX = document.body.clientWidth;
    // alert(winX/svgWidth);

    var eventSvg = createSvg("svg",{"id":"eventSvg",
        // "width": svgWidth,
        // "height": svgHeight,
         "width": "100%",
         "height": "100%",
        "viewBox":"0 0 " +svgWidth*scaleWidth+ " "+svgHeight*scaleWidth,
        // "transform":"scale("+winX/svgWidth+","+winX/svgWidth+")",
        "preserveAspectRatio":"xMinYMin meet",
        "xmlns":"http://www.w3.org/2000/svg",
        "xmlns:xlink":"http://www.w3.org/1999/xlink",
        });
    document.getElementById(appendElementId).appendChild(eventSvg);
    // console.log("blablabalbalbablalb "+ eventSvg.xmlns);
    var ind =0;
    var g = document.createElement('g');
    for (var vehicle in vehiclesSvg){
        var vehicleIcon = createSvg("circle",{"id":"icon_"+vehicle,"r":3,"fill":"green"})
        // var vehiclePath = createSvg("path",{"id":"path_"+vehicle,
        //                                     "d":"M"+vehiclesSvg[vehicle].points.join(" "),
        //                                     "fill":"none",
        //                                     "stroke":"#000000",
        //     })
        var vehicleAnimation = createSvg("animateMotion",{
                                        "xlink:href":"#icon_"+vehicle,
                                        "dur":getSum(vehiclesSvg[vehicle].linkTravelTime)+"s",
                                        "begin":vehiclesSvg[vehicle].begin+"s",
            //"begin":"0s",
                                        "path":"M"+vehiclesSvg[vehicle].points.join(" "),
                                        "keyPoints":calKeyPointsOrTime(vehiclesSvg[vehicle].linkLengths).join(";"),
                                        "keyTimes":calKeyPointsOrTime(vehiclesSvg[vehicle].linkTravelTime).join(";"),
                                        "calcMode":"linear",
                                        "rotate":"auto",
                                        "fill":"freeze",
                                        });

         // var vehicleMpath = createSvg("mpath",{"xlink:href":"#path_"+vehicle});
         //console.log( vehicleAnimation);
        // eventSvg.appendChild(vehicleIcon);
        //  eventSvg.appendChild(vehiclePath);
        // eventSvg.appendChild(vehicleAnimation);
        //  vehicleAnimation.appendChild(vehicleMpath);
        g.appendChild(vehicleIcon);
        //g.appendChild(vehiclePath);
        g.appendChild(vehicleAnimation);
        // vehicleAnimation.appendChild(vehicleMpath);
        ind++;
        // if (ind>200){
        //     break;
        // }
    }
    // console.log(g);
    eventSvg.innerHTML = networkSvg.innerHTML + g.innerHTML;

    // var test =[1,2,3,4,2,5];
    //console.log(eventSvg);
    return eventSvg;

}

function scaleByMouseWheel(obj) {
//obj是一个对象，初始时obj并没有zoom属性，所以给zoom赋值为100；
var zoom = parseInt(obj.style.zoom)||100;
//每次滚动鼠标时，改变zoom的大小
//event.wheelDelta有两个值，120，-120，取值情况取决于滚动鼠标的方向；
zoom += event.wheelDelta/12;//每次滚动加减10；
if (zoom > 0) {
    obj.style.zoom = zoom+"%";//更改后的zoom赋值给obj
    // console.log(obj.style.zoom);
}
return false;
}


/*
* 功能：对JSON对象字符串数组进行多字段（多列）排序
* 参数：
*   objArr: 目标数组
*   keyArr: 排序字段，以数组形式传递
*   type：排序方式，undefined以及asc都是按照升序排序，desc按照降序排序
* */
function sortObjectArray(objArr, keyArr, type) {
    if (type != undefined && type != 'asc' && type != 'desc') {
        return 'error';
    }
    var order = 1;
    if (type != undefined && type == 'desc') {
        order = -1;
    }
    var key = keyArr[0];
    objArr.sort(function (objA, objB) {
        if (objA[key] > objB[key]) {
            return order;
        } else if (objA[key] < objB[key]) {
            return 0 - order;
        } else {
            return 0;
        }
    })
    for (var i = 1; i < keyArr.length; i++) {
        var key = keyArr[i];
        objArr.sort(function (objA, objB) {
            for (var j = 0; j < i; j++) {
                if (objA[keyArr[j]] != objB[keyArr[j]]) {
                    return 0;
                }
            }
            if (objA[key] > objB[key]) {
                return order;
            } else if (objA[key] < objB[key]){
                return 0 - order;
            } else {
                return 0;
            }
        })
    }
    return objArr;
}

function getSum(array){
    var sum = 0;
    for (var i = 0; i < array.length; i++){
        sum += array[i];
    }
    return sum;
}

function calKeyPointsOrTime(arrayData){
    var rArray = [0];
    var sumData = getSum(arrayData);
    for(var i=1;i<arrayData.length;i++){
        rArray[i] = rArray[i-1]+ arrayData[i]/sumData;
    }
    rArray[arrayData.length] = 1.0;
    return rArray;
}


