window.ui = (function (parts, modules, pages, build, common, config) {
  var config = new config();
  var commonUtil = new common();
  var buildUtil = new build(commonUtil);
  var parts = new parts(buildUtil, commonUtil, config);
  var modules = new modules(buildUtil, commonUtil, parts, config);
  var pages = new pages(buildUtil, commonUtil, parts, modules, config);
  var custom = {};

  return {
    parts: parts,
    modules: modules,
    pages: pages,
    custom: custom,
    build: buildUtil,
    common: commonUtil,
    config: config,
  };
})(
  // parts obj -----------------------------------------------------------------------------------
  function (buildUtil, common, config) {
    var setting = config.part;
    var pageWidth = document.body.scrollWidth;
    var pageHeight = document.body.scrollHeight;
    var partCommon = {
      getCurr: function (id, partData) {
        var curr = partData["part" + id];
        if (curr) return curr;
      },
      getConfig: function (id, partData) {
        var curr = partData["part" + id];
        if (curr) return curr.params;
      },
      uuid: function () {
        var randomNum = parseInt(Math.random() * 100000);
        return randomNum + new Date().getTime();
      },
    };

    // window module ----------------------------------------------------------------------------
    function windowObj() {
      var index = 0;
      var idPrefix = setting.window.idPrefix;
      var bgIdPrefix = setting.window.bgIdPrefix;
      var bodyIdPrefix = setting.window.bodyIdPrefix;

      var windowData = {};
      var windowConfig = {
        name: "div",
        properties: { class: "p-window" },
        children: [
          {
            name: "div",
            properties: { class: "p-header" },
            children: [
              { name: "div", properties: { class: "p-left" }, text: "" },
              {
                name: "div",
                properties: { class: "p-right" },
                children: [{ name: "span", properties: {}, text: " x " }],
              },
            ],
          },
          { name: "div", properties: { class: "p-body" }, children: [] },
        ],
      };
      var windowBgConfig = { name: "div", properties: { class: "p-window-bg" } };

      var windowFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, windowData);
        },
        drag: function (id) {
          var curr = windowData["part" + id];
          if (curr) {
            var event = window.event || arguments.callee.caller.arguments[0];
            var pageX = event.pageX;
            var pageY = event.pageY;

            var curr = $("#" + idPrefix + id);
            var currMarginTop = parseInt(curr.css("top"));
            var currMarginLeft = parseInt(curr.css("left"));
            var currWidth = curr.width();
            var currHeight = curr.height();
            var currParent = $("#" + bgIdPrefix);
            var parentWidth = currParent.width();
            var parentHeight = currParent.height();

            var lastNewMarginTop = undefined;
            var lastNewMarginLeft = undefined;
            var mousemoveFn = function (e) {
              var moveX = e.pageX;
              var moveY = e.pageY;
              var newMarginTop = currMarginTop + (moveY - pageY);
              var newMarginLeft = currMarginLeft + (moveX - pageX);

              if (newMarginTop < 0) newMarginTop = 0;
              if (newMarginTop > parentHeight - currHeight) newMarginTop = parentHeight - currHeight;
              if (newMarginLeft < 0) newMarginLeft = 0;
              if (newMarginLeft > parentWidth - currWidth) newMarginLeft = parentWidth - currWidth;

              lastNewMarginTop = newMarginTop;
              lastNewMarginLeft = newMarginLeft;
              curr.css("top", newMarginTop);
              curr.css("left", newMarginLeft);
            };
            curr.bind("mousemove", mousemoveFn);
            currParent.bind("mousemove", mousemoveFn);

            var mouseupFn = function (e) {
              curr.unbind("mousemove mouseup");
              currParent.unbind("mousemove mouseup");
            };
            curr.bind("mouseup", mouseupFn);
            currParent.bind("mouseup", mouseupFn);
          }
        },
        open: function (id) {
          var curr = windowData["part" + id];
          if (!curr) {
            alert(id + " window is nonentity!");
          } else {
            var params = curr.params;
            var openCallFn = params.call.open.fn;
            var openCallFnParams = params.call.open.params;
            $("#" + idPrefix + id).css("display", "block");
            $("#" + bgIdPrefix).css("display", "block");
            common.callFu(window.ui, openCallFn, openCallFnParams, curr);
          }
        },
        verify: function (id) {
          var returnObj = false;
          var curr = windowData["part" + id];
          if (curr) {
            var params = curr.params;
            var callFn = params.call.verify.fn;
            var callFnParams = params.call.verify.params;
            common.callFu(window.ui, callFn, callFnParams, curr);
            returnObj = $("#" + idPrefix + id);
          }
          return returnObj;
        },
        close: function (id) {
          var curr = windowData["part" + id];
          if (!curr) {
            alert(id + " window is nonentity!");
          } else {
            var params = curr.params;
            var callFn = params.call.close.fn;
            var callFnParams = params.call.close.params;
            $("#" + idPrefix + id).css("display", "none");
            $("#" + bgIdPrefix).css("display", "none");
            common.callFu(window.ui, callFn, callFnParams, curr);
          }
        },
        change: function (id, properties, style, other) {
          var curr = windowData["part" + id];
          if (!curr) {
            alert(id + " window is nonentity!");
          } else {
            var params = curr.params;
            if (!params.properties) params.properties = {};
            if (!params.other) params.other = {};
            var windowDom = $("#" + idPrefix + params.id);
            if (properties) {
              for (var propertiesKey in properties) {
                if (properties.hasOwnProperty(propertiesKey)) {
                  var property = properties[propertiesKey];
                  windowDom.attr(propertiesKey, property);
                  params.properties[propertiesKey] = property;
                }
              }
            }
            if (other) {
              for (var otherKey in other) {
                if (other.hasOwnProperty(otherKey)) {
                  var otherValue = other[otherKey];
                  params.other[otherKey] = otherValue;
                }
              }
            }
            if (style && style.length > 0) {
              style = style.replace(/(\s*$)/g, "");
              var lastChar = style.charAt(style.length - 1);
              if (lastChar != ";") style += ";";

              var oldStyle = windowDom.attr("style");
              oldStyle = oldStyle ? oldStyle : "";

              windowDom.attr("style", style + oldStyle);
            }
          }
        },
        getConfig(id) {
          var curr = windowData["part" + id];
          if (curr) return curr.params;
        },
      };

      function build(params) {
        var id = params.id;
        var style = params.style;
        var title = params.title;
        var bodyConfig = params.body;

        if (common.isEmpty(id)) params.id = index = id = index + 1;
        if (common.isEmpty(title)) title = " ";
        if (!params.call) params.call = {};
        if (!params.call.open) params.call.open = {};
        if (!params.call.close) params.call.close = {};
        if (!params.call.verify) params.call.verify = {};

        var exterior = common.deepCopy(windowConfig);
        exterior.properties.id = idPrefix + id;
        if (style) exterior.properties.style = style;

        var header = exterior.children[0];
        header.properties.onmousedown = "ui.parts.window.drag('" + id + "')";
        header.children[0].text = title;
        header.children[1].children[0].properties.onclick = "ui.parts.window.close('" + id + "')";

        var body = exterior.children[1];
        body.properties.id = bodyIdPrefix + id;
        if (common.is.Array(bodyConfig)) body.children = bodyConfig;
        else body.text = bodyConfig;

        if (!document.getElementById(bgIdPrefix)) {
          var windowBgElement = common.deepCopy(windowBgConfig);
          windowBgElement.properties.id = bgIdPrefix;
          var windowBgHtml = buildUtil.buildModule(windowBgElement);
          $(document.body).append(windowBgHtml);
        }

        return (windowData["part" + id] = {
          id: id,
          params: params,
          config: exterior,
        });
      }

      return {
        build: build,
        drag: windowFn.drag,
        open: windowFn.open,
        verify: windowFn.verify,
        close: windowFn.close,
        change: windowFn.change,
        getConfig: windowFn.getConfig,
      };
    }

    // form module -----------------------------------------------------------------------------
    function formObj() {
      var index = 0;
      var helpMsgName = setting.form.helpMsgName;
      var errorMsgName = setting.form.errorMsgName;
      var idPrefix = setting.form.idPrefix;
      var elementIdPrefix = setting.form.elementIdPrefix;
      var fileNameSuffix = setting.form.fileNameSuffix;
      //var fileReturnDataPath = setting.form.fileReturnDataPath;
      var filePathNameSuffix = setting.form.filePathNameSuffix;
      var fileBrowseButtonName = setting.form.fileBrowseButtonName;
      var fileUploadButtonName = setting.form.fileUploadButtonName;

      var formData = {};
      var formElementData = {};
      var formsConfig = { name: "form", properties: {}, children: [] };
      var formColConfig = { name: "div", properties: { class: "p-col" }, children: [] };
      var formConfig = {
        name: "div",
        properties: { class: "p-form" },
        children: [
          { name: "div", properties: { class: "p-title" }, text: "" },
          { name: "div", properties: { class: "p-element" }, children: [] },
          { name: "div", properties: { class: "p-error-msg", name: errorMsgName, style: "display:none" }, text: "" },
          { name: "div", properties: { class: "p-help-msg", name: helpMsgName, style: "display:none" }, text: "" },
        ],
      };
      var elementsConfig = {
        input: { name: "input", properties: {}, text: "" },
        select: { name: "select", properties: {}, children: [] },
        option: { name: "option", properties: {}, text: "" },
        textarea: { name: "textarea", properties: {}, text: "" },
        span: { name: "span", properties: {}, text: "" },
        file: {
          name: "div",
          properties: { class: "p-element-file" },
          children: [
            { name: "input", properties: { type: "text" } },
            { name: "input", properties: { type: "file", style: "display:none" } },
            { name: "span", properties: {}, text: "" },
            { name: "span", properties: {}, text: "" },
          ],
        },
      };

      var formFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, formData);
        },
        submit: function (id, properties) {
          var formObj = formData["part" + id];
          if (formObj) {
            var dom = $("#" + idPrefix + id);
            if (properties) {
              for (var propertyKey in properties) if (properties.hasOwnProperty(propertyKey)) dom.attr(propertyKey, properties[propertyKey]);
            }
            dom.submit();
            common.callFu(window.ui, formObj.call.submit.fn, formObj.call.submit.params, curr);
          }
        },
        setData: function (id, data) {
          var formObj = formData["part" + id];
          var elements = formObj.params.elements;
          for (var index = 0; index < elements.length; index++) {
            var element = elements[index];
            setElementData(id, element, data[element.key]);
          }
        },
        getData: function (id) {
          var formObj = formData["part" + id];
          var elements = formObj.params.elements;
          var data = {};
          for (var index = 0; index < elements.length; index++) {
            var element = elements[index];
            getElementData(id, element, data);
          }
          return data;
        },
        removeData: function (id) {
          var formObj = formData["part" + id];
          var elements = formObj.params.elements;
          for (var index = 0; index < elements.length; index++) {
            var element = elements[index];
            removeElementData(id, element);
          }
        },
        initData: function (id) {
          var formObj = formData["part" + id];
          var elements = formObj.params.elements;
          for (var index = 0; index < elements.length; index++) {
            var element = elements[index];
            initElementData(id, element);
          }
        },
        verifyData: function (id) {
          var currData = this.getData(id);
          var result = {
            total: true,
            data: currData,
            verifys: {},
          };
          var formObj = formData["part" + id];
          var elements = formObj.params.elements;
          for (var index = 0; index < elements.length; index++) {
            var element = elements[index];

            var ifResult;
            if (element.if) {
              var ifResultData = ifCommandFn.getIfResult(id, element.key);
              ifResult = ifResultData[element.key];
            }

            if (!common.isEmpty(element.verify) && (ifResult == undefined || ifResult == true)) {
              var currResult = verify(element.verify, currData[element.key]);
              if (currResult == false) result.total = false;
              result.verifys[element.key] = currResult;

              if (element.error) {
                var tabName = elementIdPrefix + id + "_" + element.key;
                var errorDom = $("*[name=" + tabName + "]")
                  .parent()
                  .siblings("*[name='" + errorMsgName + "']");
                if (currResult == false) errorDom.css("display", "block");
                else errorDom.css("display", "none");
              }
            }
          }
          return result;
        },
        showHelpMsg: function (id, operationType) {
          var msgName = helpMsgName;

          var dom = $("#" + idPrefix + id).find("*[name='" + msgName + "']");
          if (operationType == true || operationType == "show") dom.css("display", "block");
          else if (operationType == false || operationType == "hide") dom.css("display", "none");
          else if (operationType == "auto") dom.css("display", dom.css("display") == "none" ? "block" : "none");
          else console.log("Your operation type param is wrong, Plase put in 'show' or 'hide'!");
        },
        command: function (id, commandkeys) {
          var formObj = formData["part" + id];
          var elements = formObj.params.elements;

          var results = ifCommandFn.getIfResult(id, commandkeys);
          for (var key in results) {
            if (results.hasOwnProperty(key)) {
              var result = results[key];
              var tabName = elementIdPrefix + id + "_" + key;
              if (result == true)
                $("*[name='" + tabName + "']")
                  .parents(".p-form")
                  .css("display", "inline-block");
              else
                $("*[name='" + tabName + "']")
                  .parents(".p-form")
                  .css("display", "none");
            }
          }
        },
      };
      var formElementFn = {
        setData: function (id, key, data) {
          var formObj = formData["part" + id];
          if (formObj) {
            var element = formObj.elements["element" + key];
            if (element && data) setElementData(id, element, data);
          }
        },
        initData: function (id, key) {
          var formObj = formData["part" + id];
          if (formObj) {
            var element = formObj.elements["element" + key];
            if (element && data) initElementData(id, element);
          }
        },
        getData: function (id, key) {
          var formObj = formData["part" + id];
          if (formObj) {
            var data = {};
            var element = formObj.elements["element" + key];
            if (element && data) getElementData(id, element, data);
            return data[key];
          }
        },
        removeData: function (id, key) {
          var formObj = formData["part" + id];
          if (formObj) {
            var data = {};
            var element = formObj.elements["element" + key];
            if (element && data) removeElementData(id, element);
          }
        },
        file: {
          checked: function (id, key) {
            var tabName = elementIdPrefix + id + "_" + key;
            var fileTabName = tabName + fileNameSuffix;
            var filePathTabName = tabName + filePathNameSuffix;
            $("input[name='" + filePathTabName + "']").val($("#" + fileTabName).val());
          },
          browse: function (id, key) {
            var tabName = elementIdPrefix + id + "_" + key + fileNameSuffix;
            $("#" + tabName).trigger("click");
          },
          upload: function (id, key, url, fileKey) {
            var curr = formData["part" + id];
            if (curr) {
              var currElement = curr.elements["element" + key];
              if (currElement) {
                if (!fileKey) fileKey = key;
                var tabName = elementIdPrefix + id + "_" + key + fileNameSuffix;
                var fileDom = $("#" + tabName);
                fileDom.attr("id", fileKey);
                fileDom.attr("name", fileKey);
                common.ajaxFileUpload(
                  fileKey,
                  undefined,
                  url,
                  function (data) {
                    if (!currElement.call) currElement.call = {};
                    var uploadCall = currElement.call.upload;
                    if (currElement.upload) uploadCall = currElement.upload;
                    if (uploadCall) {
                      if (!currElement.formId) currElement.formId = id;
                      common.callFu(window.ui, uploadCall, data, currElement);
                    }
                  },
                  function () {
                    console.log("FormID: " + id + " key: " + key + ": An error occurred while uploading.");
                  }
                );
                fileDom = $("#" + fileKey);
                fileDom.attr("id", tabName);
                fileDom.attr("name", tabName);
              }
            }
          },
        },
      };

      function verify(verify, data) {
        var result = false;
        var valueArr = verify.split("_");

        if (verify == "NO_NULL") {
          result = !common.isEmpty(data);
        } else if (valueArr.length == 2) {
          var type = valueArr[0];
          var value = valueArr[1];
          if (type == "GTE") result = parseInt(data) >= parseInt(value);
          else if (type == "GT") result = parseInt(data) > parseInt(value);
          else if (type == "LTE") result = parseInt(data) <= parseInt(value);
          else if (type == "LT") result = parseInt(data) < parseInt(value);
          else if (type == "E") result = data == value;
        } else if (valueArr.length == 3 && valueArr[0] == "LENGTH") {
          var type = valueArr[1];
          var value = valueArr[2];
          if (type == "GTE") result = data.length >= parseInt(value);
          else if (type == "GT") result = data.length > parseInt(value);
          else if (type == "LTE") result = data.length <= parseInt(value);
          else if (type == "LT") result = data.length < parseInt(value);
          else if (type == "E") result = data.length == parseInt(value);
        }
        return result;
      }

      function setElementData(id, element, elementValue) {
        if (element.isSubmit != false) {
          var tabName = elementIdPrefix + id + "_" + element.key;
          //var elementValue = data[element.key];
          if (elementValue) {
            if (element.type == "text" || element.type == "password" || element.type == "hidden") {
              $("input[name='" + tabName + "']").val(elementValue);
            } else if (element.type == "file") $("input[name='" + tabName + filePathNameSuffix + "']").val(elementValue);
            else if (element.type == "date" || element.type == "datetime" || element.type == "time") {
              if (isNumber(elementValue)) {
                var formatStr = "yyyy-MM-dd";
                if (element.type == "datetime") formatStr = "yyyy-MM-dd hh:mm:ss";
                else if (element.type == "time") formatStr = "hh:mm:ss";
                elementValue = dateFormat(new Date(elementValue), formatStr);
              }
              $("input[name='" + tabName + "']").val(elementValue);
            } else if (element.type == "radio") {
              elementValue = elementValue + "";
              $("input:radio[name='" + tabName + "']:checked").prop("checked", false);
              $("input:radio[name='" + tabName + "']").each(function () {
                if ($(this).val() == elementValue) {
                  $(this).prop("checked", true);
                }
              });
            } else if (element.type == "checkbox") {
              $("input:checkbox[name='" + tabName + "']:checked").prop("checked", false);
              $("input:checkbox[name='" + tabName + "']").each(function () {
                var checkboxValue = [];
                if (elementValue instanceof Array) checkboxValue = elementValue;
                else checkboxValue.push(elementValue.toString());
                if (checkboxValue.length > 0 && checkboxValue.indexOf(thisValue) != -1) $(this).prop("checked", true);
              });
            } else if (element.type == "select") {
              var selectKeyArr = element.key.split(",");
              elementValue = data[selectKeyArr[0]];
              $("select[name='" + tabName + "'] option:first").prop("selected", "selected");
              $("select[name='" + tabName + "'] option").each(function () {
                if ($(this).val() == elementValue) {
                  $(this).prop("selected", "selected");
                }
              });
              $("select[name='" + tabName + "']").trigger("change", []);
            } else if (element.type == "textarea") $("textarea[name='" + tabName + "']").val(elementValue);
            else $("span[name='" + tabName + "']").text(elementValue);

            if (element.commandOf) formFn.command(id, element.commandOf);
          }
        }
      }

      function getElementData(id, element, data) {
        if (element.isSubmit != false) {
          var dataValue = "";
          var tabName = elementIdPrefix + id + "_" + element.key;
          if (element.type == "text" || element.type == "date" || element.type == "password" || element.type == "datetime" || element.type == "time" || element.type == "hidden") {
            data[element.key] = $("input[name='" + tabName + "']").val();
          } else if (element.type == "file") {
            data[element.key] = $("input[name='" + tabName + filePathNameSuffix + "']").val();
          } else if (element.type == "radio") {
            data[element.key] = $("input:radio[name='" + tabName + "']:checked").val();
          } else if (element.type == "checkbox") {
            var dataValue = [];
            $("input:checkbox[name='" + tabName + "']:checked").each(function () {
              dataValue.push($(this).val());
            });
            data[element.key] = dataValue;
          } else if (element.type == "select") {
            var selectTabArr = element.key.split(",");
            if (selectTabArr.length == 1) {
              data[selectTabArr[0]] = $("select[name='" + tabName + "'] option:selected").val();
            } else if (selectTabArr.length == 2) {
              data[selectTabArr[0]] = $("select[name='" + tabName + "'] option:selected").val();
              data[selectTabArr[1]] = $("select[name='" + tabName + "'] option:selected").text();
            }
          } else if (element.type == "textarea") {
            data[element.key] = $("textarea[name='" + tabName + "']").val();
          } else {
            data[element.key] = $("*[name='" + tabName + "']").text();
          }
        }
      }

      function removeElementData(id, element) {
        if (element.isSubmit != false) {
          var tabName = elementIdPrefix + id + "_" + element.key;

          if (element.type == "text" || element.type == "date" || element.type == "password" || element.type == "datetime" || element.type == "time") $("input[name='" + tabName + "']").val("");
          else if (element.type == "file") {
            $("input[name='" + tabName + filePathNameSuffix + "']").val("");
            $("input[name='" + tabName + fileNameSuffix + "']").val("");
          } else if (element.type == "radio") $("input:radio[name='" + tabName + "']:checked").prop("checked", false);
          else if (element.type == "checkbox") {
            $("input:checkbox[name='" + tabName + "']:checked").each(function () {
              $(this).prop("checked", false);
            });
          } else if (element.type == "select") $("select[name='" + tabName + "'] option:first").prop("selected", "selected");
          else if (element.type == "textarea") $("textarea[name='" + tabName + "']").val("");
          else $("*[name='" + tabName + "']").text("");

          if (element.commandOf) formFn.command(id, element.commandOf);
        }
      }

      function initElementData(id, element) {
        var value = element.value;
        if (value) setElementData(id, element, value);
        else removeElementData(id, element);
      }

      var ifCommandFn = {
        data: {},
        init: function (id, elements) {
          var ifCommandObj = (this.data["ifCommand" + id] = {});
          var commandElement = (ifCommandObj.commands = {});
          var ifElement = (ifCommandObj.ifs = {});

          var commandArr = [];
          for (var index = 0; index < elements.length; index++) {
            var element = elements[index];
            if (element.if) {
              var elenemtIf = (element.if = this.ifAnaly(element.if));
              if (elenemtIf.left && elenemtIf.left.charAt(0) == "@") {
                var left = elenemtIf.left.replace("@", "");
                if (!commandArr[left]) commandArr[left] = [];
                commandArr[left].push(element.key);
              }
              if (elenemtIf.right && elenemtIf.right.charAt(0) == "@") {
                var right = elenemtIf.right.replace("@", "");
                if (!commandArr[right]) commandArr[right] = [];
                commandArr[right].push(element.key);
              }
              ifElement[element.key] = element;
            }
          }
          for (var index = 0; index < elements.length; index++) {
            var element = elements[index];
            if (commandArr[element.key]) {
              element.commandOf = commandArr[element.key];
              commandElement[element.key] = element;
            }
          }
        },
        getIfResult: function (id, keys) {
          var currObj = this.data["ifCommand" + id];
          var results = {};
          if (currObj) {
            var ifElement = currObj.ifs;
            var commandElement = currObj.commands;
            if (!common.is.Array(keys)) keys = keys.split(",");
            for (var index = 0; index < keys.length; index++) {
              var key = keys[index];
              var element = ifElement[key];

              var left = element.if.left;
              var condition = element.if.condition;
              var right = element.if.right;
              if (left.charAt(0) == "@") {
                var leftKey = left.replace("@", "");
                var leftElement = commandElement[leftKey];
                var elementData = {};
                getElementData(id, leftElement, elementData);
                left = elementData[leftKey];
              }
              if (right.charAt(0) == "@") {
                var rightKey = right.replace("@", "");
                var rightElement = commandElement[rightKey];
                var elementData = {};
                getElementData(id, rightElement, elementData);
                right = elementData[rightKey];
              }

              var resultFn = new Function("return ('" + left + "'" + condition + "'" + right + "');");
              results[key] = resultFn();
            }
          }
          return results;
        },
        ifAnaly: function (ifValue) {
          var ifArr;
          var currCondition, arg_1, arg_2;
          var conditionArr = ["==", "!=", ">=", "<=", ">", "<", ":"];
          for (var index = 0; index < conditionArr.length; index++) {
            var condition = conditionArr[index];
            if (ifValue.indexOf(condition) != -1) {
              currCondition = condition;
              ifArr = ifValue.split(condition);
              break;
            }
          }
          if (ifArr && ifArr.length > 0) {
            arg_1 = ifArr[0];
            if (ifArr.length > 1) arg_2 = ifArr[1];
          }
          return {
            left: arg_1,
            right: arg_2,
            condition: currCondition,
          };
        },
      };

      function buildElement(id, params) {
        var key = elementIdPrefix + id + "_" + params.key;
        var type = params.type;
        var value = params.value ? params.value : "";
        var defaultValue = params.default ? params.default : "";
        var children = params.children;
        var commandOf = params.commandOf;

        var elementArr = [];
        if (type == "text" || type == "password" || type == "hidden") {
          var element = common.deepCopy(elementsConfig.input);
          element.properties.name = key;
          element.properties.type = type;
          element.properties.value = value;
          element.properties.placeholder = defaultValue;
          if (commandOf) element.properties.onblur = "ui.parts.form.command('" + id + "','" + commandOf + "')";
          elementArr.push(element);
        } else if (type == "file") {
          var url = params.url;
          var fileParamName = params.fileName;
          var element = common.deepCopy(elementsConfig.file);
          element.properties.name = key;
          element.children[0].properties.name = key + filePathNameSuffix;
          element.children[0].properties.value = value;
          element.children[0].properties.placeholder = defaultValue;
          if (commandOf) element.children[0].properties.onblur = "ui.parts.from.command('" + id + "','" + commandOf + "')";
          element.children[1].properties.id = key + fileNameSuffix;
          element.children[1].properties.name = key + fileNameSuffix;
          element.children[1].properties.onchange = "ui.parts.form.element.file.checked('" + id + "','" + params.key + "')";
          element.children[2].properties.onclick = "ui.parts.form.element.file.browse('" + id + "','" + params.key + "')";
          element.children[2].text = fileBrowseButtonName;
          element.children[3].properties.onclick = "ui.parts.form.element.file.upload('" + id + "','" + params.key + "','" + url + "','" + fileParamName + "')";
          element.children[3].text = fileUploadButtonName;
          elementArr.push(element);
        } else if (type == "select") {
          var element = common.deepCopy(elementsConfig.select);
          element.properties.name = key;
          if (commandOf) element.properties.onchange = "ui.parts.form.command('" + id + "','" + commandOf + "')";

          var defaultOption = common.deepCopy(elementsConfig.option);
          defaultOption.properties.value = defaultValue.value;
          defaultOption.text = defaultValue.text;
          element.children.push(defaultOption);
          for (var childrenIndex = 0; childrenIndex < children.length; childrenIndex++) {
            var child = children[childrenIndex];
            var optionElement = common.deepCopy(elementsConfig.option);
            optionElement.properties.value = child.value;
            if (child.value == value) optionElement.properties.selected = "selected";
            optionElement.text = child.value;
            element.children.push(optionElement);
          }
          elementArr.push(element);
        } else if (type == "date" || type == "datetime" || type == "time") {
          var element = common.deepCopy(elementsConfig.input);
          element.properties.name = key;
          element.properties.type = type;
          element.properties.value = value;
          if (commandOf) element.properties.onblur = "ui.parts.form.command('" + id + "','" + commandOf + "')";

          elementArr.push(element);
        } else if (type == "radio" || type == "checkbox") {
          for (var childrenIndex = 0; childrenIndex < children.length; childrenIndex++) {
            var child = children[childrenIndex];
            var element = common.deepCopy(elementsConfig.input);
            element.properties.name = key;
            element.properties.type = type;
            if (commandOf) element.properties.onclick = "ui.parts.form.command('" + id + "','" + commandOf + "')";
            if ((type == "radio" && child.value == value) || (type == "checkbox" && value.indexOf(child.value))) element.properties.checked = "checked";

            var child = children[childrenIndex];
            element.properties.value = child.value;

            var radioText = { name: "label", text: buildUtil.tags.nbsps(2) + child.text + buildUtil.tags.nbsps(3) };
            elementArr.push(element);
            elementArr.push(radioText);
          }
        } else if (type == "textarea") {
          var element = common.deepCopy(elementsConfig.textarea);
          element.properties.name = key;
          element.text = value;
          if (commandOf) element.properties.onblur = "ui.parts.form.command('" + id + "','" + commandOf + "')";

          elementArr.push(element);
        } else {
          var element = common.deepCopy(elementsConfig.span);
          element.properties.name = key;
          element.text = value;
          if (commandOf) element.properties.onblur = "ui.parts.form.command('" + id + "','" + commandOf + "')";

          elementArr.push(element);
        }

        return elementArr;
      }

      function buildform(params) {
        var id = params.id;
        var col = params.col ? parseInt(params.col) : 1;
        var elements = params.elements;
        if (!params.call) params.call = {};
        if (!params.call.set) params.call.set = {};
        if (!params.call.get) params.call.get = {};
        if (!params.call.remove) params.call.remove = {};
        if (!params.call.verify) params.call.verify = {};

        if (common.isEmpty(id)) params.id = index = id = index + 1;
        var commandArr = ifCommandFn.init(id, elements);

        var elementsData = {};
        var formColArr = new Array(col);
        var formColCurrIndex = 0;
        for (var i = 0; i < elements.length; i++) {
          var element = elements[i];
          var title = element.title;
          var helpMsg = element.help ? element.help : "";
          var errorMsg = element.error ? element.error : "";
          var isShow = element.show == false ? false : true;

          var currFormCol = formColArr[formColCurrIndex];
          if (!currFormCol) {
            var formColElement = common.deepCopy(formColConfig);
            formColElement.properties.style = "width:" + parseInt(100 / col) + "%;";
            currFormCol = formColArr[formColCurrIndex] = formColElement;
          }
          var currFormColElementArr = currFormCol.children;

          if (formColCurrIndex == col - 1) formColCurrIndex = 0;
          else formColCurrIndex++;

          var formElement = common.deepCopy(formConfig);
          if (!isShow || element.if) formElement.properties.style = "display:none;";
          formElement.children[0].text = title;
          formElement.children[1].children = buildElement(id, element);
          formElement.children[2].text = errorMsg;
          formElement.children[3].text = helpMsg;

          elementsData["element" + element.key] = element;
          currFormColElementArr.push(formElement);
        }

        var formsElement = common.deepCopy(formsConfig);
        formsElement.properties.id = idPrefix + id;
        formsElement.children = formColArr;

        return (formData["part" + id] = {
          id: id,
          params: params,
          config: formsElement,
          elements: elementsData,
        });
      }

      return {
        build: buildform,
        getConfig: formFn.getConfig,
        submit: formFn.submit,
        setData: formFn.setData,
        getData: formFn.getData,
        removeData: formFn.removeData,
        verifyData: formFn.verifyData,
        initData: formFn.initData,
        showHelp: formFn.showHelpMsg,
        command: formFn.command,
        element: {
          setData: formElementFn.setData,
          initData: formElementFn.initData,
          getData: formElementFn.getData,
          removeData: formElementFn.removeData,
          file: {
            checked: formElementFn.file.checked,
            browse: formElementFn.file.browse,
            upload: formElementFn.file.upload,
          },
        },
      };
    }

    // grid module  ---------------------------------------------------------------------------
    function gridObj() {
      var index = 0;
      var idPrefix = setting.grid.idPrefix;

      var gridData = {};
      var gridConfig = {
        name: "table",
        properties: { class: "p-grid" },
        children: [],
      };

      var gridFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, gridData);
        },
        click: function (id, index) {
          var curr = gridData["part" + id];
          if (!curr) {
            alert("The call back function is nonentity!");
          } else {
            var elements = curr.params.elements;
            common.callFu(window.ui, elements[index].call.click.fn, elements[index].call.click.params, curr);
          }
        },
        dblclick: function (id, index) {
          var curr = gridData["part" + id];
          if (!curr) {
            alert("The call back function is nonentity!");
          } else {
            var elements = curr.params.elements;
            common.callFu(window.ui, elements[index].call.dblclick.fn, elements[index].call.dblclick.params, curr);
          }
        },
      };

      function build(params) {
        var id = params.id;
        var col = parseInt(params.col);
        var elements = params.elements;
        if (common.isEmpty(id)) params.id = index = id = index + 1;

        var gridElement = common.deepCopy(gridConfig);
        var currCol = 0;
        var currRow = 0;
        var occupiedCol = [0, 0];
        var trConfig = { name: "tr", properties: { class: "first" }, children: [] };
        for (var i = 0; i < elements.length; i++) {
          var element = elements[i];

          var key = idPrefix + id + "_" + element.key;
          var text = element.text;
          var style = element.style ? element.style : "";
          var width = parseInt(element.width ? element.width : 1);
          var height = parseInt(element.height ? element.height : 1);

          var tdConfig = { name: "td", properties: {}, text: "" };
          tdConfig.properties.id = key;
          tdConfig.text = text;

          if (!element.call) element.call = {};
          if (element.call.click) tdConfig.properties.onclick = "ui.parts.grid.click('" + id + "','" + i + "')";
          if (element.call.dblclick) tdConfig.properties.ondblclick = "ui.parts.grid.dblclick('" + id + "','" + i + "')";

          if (trConfig.properties) tdConfig.properties.style = "width:" + (100 / col) * width + "%" + style;
          if (width > 1) tdConfig.properties.colspan = width;
          if (height > 1) {
            tdConfig.properties.rowspan = height;
            for (var h = 1; h <= height - 1; h++) {
              if (!occupiedCol[currRow + h]) occupiedCol[currRow + h] = 0;
              occupiedCol[currRow + h] = occupiedCol[currRow + h] + width;
            }
          }

          currCol = currCol + width;
          trConfig.children.push(tdConfig);
          if (!occupiedCol[currRow]) occupiedCol[currRow] = 0;
          if (col - occupiedCol[currRow] <= currCol || i == elements.length - 1) {
            gridElement.children.push(trConfig);
            currCol = 0;
            currRow = currRow + 1;
            trConfig = { name: "tr", children: [] };
          }
        }

        return (gridData["part" + id] = {
          id: id,
          params: params,
          config: gridElement,
        });
      }

      return {
        build: build,
        click: gridFn.click,
        dblclick: gridFn.dblclick,
      };
    }

    // drawBoard module ----------------------------------------------------------------------
    function drawBoardObj() {
      var index = 0;
      //var connectIndex = 0;
      var connectSvgIdArr = [];
      var shapeName = "shape";
      var connectSvgName = "connect_svg";
      var tempToolDragId = "temp_tool_drag";

      var toolIdPrefix = setting.drawBoard.toolIdPrefix;
      var drawIdPrefix = setting.drawBoard.drawIdPrefix;
      var shapeIdPrefix = setting.drawBoard.shapeIdPrefix;

      var stratGap = setting.drawBoard.stratGap;
      var endGap = setting.drawBoard.endGap;
      var allowNum = 0 - setting.drawBoard.allowNum;
      var connectIdPrefix = setting.drawBoard.connectIdPrefix;
      var connectLineIdPrefix = setting.drawBoard.connectLineIdPrefix;
      var connectPointText = setting.drawBoard.connectPointText;

      var drawBoardData = {};
      var drawBoardConfig = {
        name: "div",
        properties: { class: "p-draw-board" },
        children: [
          {
            name: "div",
            properties: { class: "p-toolbar" },
            children: [
              { name: "div", properties: { class: "p-head" }, text: "" },
              { name: "div", properties: { class: "p-tool" }, children: [] },
            ],
          },
          { name: "div", properties: { class: "p-panel" }, children: [] },
        ],
      };
      var shapeExteriorConfig = { name: "div", properties: { class: "p-draw-exterior" }, children: [] };
      var shapeConfig = { name: "div", properties: { class: "p-draw-shape" }, children: [] };
      var connectConfig = {
        marker: {
          name: "div",
          properties: { name: connectSvgName, class: "p-draw-connect" },
          children: [
            {
              name: "svg",
              properties: { width: "100%", height: "100%", version: "1.1", xmlns: "http://www.w3.org/2000/svg" },
              children: [
                {
                  name: "defs",
                  children: [
                    {
                      name: "marker",
                      properties: { id: "dragArrow", markerWidth: "13", markerHeight: "13", refx: "4", refy: "4", orient: "auto" },
                      children: [{ name: "circle", properties: { cx: "4", cy: "4", r: "2", fill: "gray" }, text: "" }],
                    },
                    {
                      name: "marker",
                      properties: { id: "endArrow", markerWidth: "13", markerHeight: "13", refx: "4", refy: "4", orient: "auto" },
                      children: [{ name: "circle", properties: { cx: "4", cy: "4", r: "2", fill: "#990033" }, text: "" }],
                    },
                    {
                      name: "marker",
                      properties: { id: "checkedArrow", markerWidth: "13", markerHeight: "13", refx: "4", refy: "4", orient: "auto" },
                      children: [{ name: "circle", properties: { cx: "4", cy: "4", r: "2", fill: "gray" }, text: "" }],
                    },
                  ],
                },
              ],
            },
          ],
        },
        point: {
          name: "div",
          properties: { class: "" },
          children: [
            {
              name: "svg",
              properties: { width: "100%", height: "100%", version: "1.1", xmlns: "http://www.w3.org/2000/svg" },
              children: [
                { name: "circle", properties: { cx: "5", cy: "50%", r: "7", fill: "#990033" }, text: "" },
                { name: "text", properties: { x: "50%", y: "50%", dy: ".3em", fill: "white", "text-anchor": "middle", "font-size": "10px" }, text: "" },
              ],
            },
          ],
        },
        line: {
          name: "line",
          properties: { x1: "0", y1: "0", x2: "100", y2: "100", stroke: "gray", "stroke-width": "4", "marker-end": "", tabindex: "0" },
          text: "",
        },
      };
      var regionConfig = { name: "div", properties: { class: "p-draw-region" }, text: "" };

      var drawBoardFn = {
        click: function (id) {
          shapeFn.uncheckAll(id);
          connectFn.uncheckAll(id);
        },
        region: function (id) {
          var drawBoard = drawBoardData["part" + id];
          if (drawBoard) {
            this.click(id);

            var event = window.event || arguments.callee.caller.arguments[0];
            var pageX = event.pageX;
            var pageY = event.pageY;

            var drawDom = $("#" + drawIdPrefix + id);
            var regionElement = common.deepCopy(regionConfig);
            regionElement.properties.style = "left:" + pageX + "px;top:" + pageY + "px;width:0px;height:0px;";
            drawDom.append(buildUtil.buildModule(regionElement));
            var regionDom = drawDom.children(".p-draw-region");

            drawDom.bind("mousemove", function (e) {
              var moveX = parseInt(e.pageX);
              var moveY = parseInt(e.pageY);
              var regionX = Math.abs(moveX - pageX);
              var regionY = Math.abs(moveY - pageY);
              regionDom.css("width", regionX);
              if (moveX < pageX) regionDom.css("left", moveX);
              regionDom.css("height", regionY);
              if (moveY < pageY) regionDom.css("top", moveY);
            });
            drawDom.bind("mouseup", function (e) {
              var moveX = parseInt(e.pageX);
              var moveY = parseInt(e.pageY);
              regionDom.remove();

              var regionLeft = parseInt(regionDom.css("left"));
              var regionTop = parseInt(regionDom.css("top"));
              var regionRight = regionLeft + parseInt(regionDom.css("width"));
              var regionBottom = regionTop + parseInt(regionDom.css("height"));
              var currShapes = drawBoard.shape;
              for (var positionKey in currShapes) {
                if (currShapes.hasOwnProperty(positionKey)) {
                  var currShapeObj = currShapes[positionKey];
                  var positionObj = currShapeObj.position;

                  var left = positionObj.left;
                  var right = positionObj.right;
                  var top = positionObj.top;
                  var bottom = positionObj.bottom;
                  if (right > regionLeft && left < regionRight) if (bottom > regionTop && top < regionBottom) shapeFn.checked(id, currShapeObj.id);
                }
              }
              drawDom.unbind("mousemove mouseup");
            });
          }
        },
        zoom: function (id, addWidth, addHeight) {
          var drawBoard = drawBoardData["part" + id];
          if (drawBoard) {
            var dom = $("#" + drawIdPrefix + id).parent();
            var width = parseInt(dom.css("width"));
            var height = parseInt(dom.css("height"));
            var maxWidth = parseInt(dom.css("max-width"));
            var maxHeight = parseInt(dom.css("max-height"));
            var minWidth = parseInt(dom.css("min-width"));
            var minHeight = parseInt(dom.css("min-height"));

            if (addWidth.indexOf("%") != -1) addWidth = (width * parseInt(addWidth)) / 100;
            else addWidth = parseInt(addWidth);
            if (addHeight.indexOf("%") != -1) addHeight = (height * parseInt(addHeight)) / 100;
            else addHeight = parseInt(addHeight);

            var newWidth = width + addWidth;
            var newHeight = height + addHeight;
            if (newWidth < minWidth) newWidth = minWidth;
            if (newWidth > maxWidth) newWidth = maxWidth;
            if (newHeight < minHeight) newHeight = minHeight;
            if (newHeight > maxHeight) newHeight = maxHeight;
            dom.css("width", newWidth);
            dom.css("height", newHeight);
          }
        },
        getCurr: function (id) {
          return partCommon.getCurr(id, drawBoardData);
        },
      };
      var toolFn = {
        click: function (id, barIndex, key) {
          var drawBoard = drawBoardData["part" + id];
          if (drawBoard) {
            var params = drawBoard.params;
            var tools = params.tool.bars[barIndex].elements;
            for (var toolIndex = 0; toolIndex < tools.length; toolIndex++) {
              var tool = tools[toolIndex];
              if (tool.key == key) {
                common.callFu(window.ui, tool.call.click.fn, tool.call.click.params, drawBoard);
                break;
              }
            }
          }
        },
        drag: function (id, barIndex, key) {
          var drawBoard = drawBoardData["part" + id];
          if (drawBoard) {
            var params = drawBoard.params;
            var tools = params.tool.bars[barIndex].elements;
            for (var toolIndex = 0; toolIndex < tools.length; toolIndex++) {
              var tool = tools[toolIndex];
              if (tool.key == key) {
                var event = window.event || arguments.callee.caller.arguments[0];
                var pageX = event.pageX;
                var pageY = event.pageY;

                var panelDom = $("#" + drawIdPrefix + id);
                var panelWidth = panelDom.width();
                var panelHeight = panelDom.height();
                var panelMarginTop = parseInt(panelDom.css("marginTop"));
                var panelMarginLeft = parseInt(panelDom.css("marginLeft"));

                var toolDom = $("#" + toolIdPrefix + id + "_" + tool.key);
                var toolWidth = toolDom.width();
                var toolHeight = toolDom.height();
                var toolMarginTop = parseInt(toolDom.css("marginTop"));
                var toolBody = toolDom.html();

                var newToolConfig = { name: "div", properties: { id: tempToolDragId }, text: "" };
                newToolConfig.properties.style =
                  "margin-top:" + (pageY - toolHeight / 2) + "px; margin-left:" + (pageX - toolWidth / 2) + "px;" + "position: absolute; z-index:9999; width:" + toolWidth + "px; height:" + toolHeight + "px;" + "background-color:black;opacity:.5;";

                panelDom.append(buildUtil.buildModule(newToolConfig));
                var tempDargTool = $("#" + tempToolDragId);

                panelDom.bind("mousemove", function (e) {
                  var moveX = e.pageX;
                  var moveY = e.pageY;

                  var toolMarginTop = moveY - toolHeight / 2;
                  var toolMarginLeft = moveX - toolWidth / 2;

                  if (toolMarginTop < 0) toolMarginTop = 0;
                  if (toolMarginTop > panelHeight - toolHeight) toolMarginTop = panelHeight - toolHeight;
                  if (toolMarginLeft < 0) toolMarginLeft = 0;
                  if (toolMarginLeft > panelWidth - toolWidth) toolMarginLeft = panelWidth - toolWidth;

                  tempDargTool.css("marginTop", toolMarginTop);
                  tempDargTool.css("marginLeft", toolMarginLeft);
                });
                panelDom.bind("mouseup", function (e) {
                  tempDargTool.remove();
                  panelDom.unbind("mousemove mouseup");

                  tool.call.drag.params.top = tempDargTool.css("marginTop");
                  tool.call.drag.params.left = tempDargTool.css("marginLeft");
                  common.callFu(window.ui, tool.call.drag.fn, tool.call.drag.params, drawBoard);
                });
                break;
              }
            }
          }
        },
      };
      var shapeFn = {
        create: function (drawBoardId, params) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var shapeSet = drawBoard.params.shape;
            var currNum = shapeSet.currNum;
            var maxNum = parseInt(shapeSet.maxNum);
            var keyMaxNum = shapeSet.keyMaxNum;
            var keyCurrNum = shapeSet.keyCurrNum;

            var type = params.type;
            var currKeyNum, currKeyMaxNum;
            if (!common.isEmpty(type)) {
              if (keyMaxNum) currKeyMaxNum = parseInt(keyMaxNum[type]);
              currKeyNum = keyCurrNum[type];
              if (!currKeyNum) currKeyNum = keyCurrNum[type] = 0;
            }
            if ((!maxNum || currNum < maxNum) && (!currKeyMaxNum || currKeyNum < currKeyMaxNum)) {
              var id = params.id;
              var title = params.title ? params.title : "";
              var base = params.base;
              var body = params.body;
              var style = params.style;
              var leftPoint = params.leftPoint != undefined ? params.leftPoint : connectPointText;
              var rightPoint = params.rightPoint != undefined ? params.rightPoint : connectPointText;
              var isChecked = params.checked == false ? false : true;
              var isDblclick = params.dblclick == false ? false : true;
              var isDrag = params.drag == false ? false : true;
              var isConnect = params.connect == false ? false : true;
              var isZoom = params.zoom == false ? false : true;
              if (common.isEmpty(id)) id = params.id = partCommon.uuid();

              if (!params.call) params.call = {};
              if (!params.call.dblclick) params.call.dblclick = {};
              if (!params.call.drag) params.call.drag = {};
              if (!params.call.change) params.call.change = {};
              if (!params.call.delete) params.call.delete = {};

              var top = base.top ? base.top : "0px";
              var left = base.left ? base.left : "0px";
              var width = base.width ? base.width : "50px";
              var height = base.height ? base.height : "50px";
              var zIndex = base.zIndex ? base.zIndex : "1";

              var shapeExteriorElement = common.deepCopy(shapeExteriorConfig);
              shapeExteriorElement.properties.id = shapeIdPrefix + id;
              shapeExteriorElement.properties.tabindex = "0";
              shapeExteriorElement.properties.style = "margin-top:" + top + ";margin-left:" + left + ";z-index:" + zIndex + ";width:" + width + ";height:" + height + ";" + style;
              shapeExteriorElement.properties.onkeydown = "ui.parts.drawBoard.shape.delete('" + drawBoardId + "','" + id + "')";
              shapeExteriorElement.properties.title = title;

              if (isConnect == true) {
                if (leftPoint !== false) {
                  var leftPointElement = common.deepCopy(connectConfig.point);
                  leftPointElement.properties.class = "p-point-left";
                  leftPointElement.children[0].children[1].text = leftPoint;
                  shapeExteriorElement.children.push(leftPointElement);
                }
                if (rightPoint !== false) {
                  var rightPointElement = common.deepCopy(connectConfig.point);
                  rightPointElement.properties.class = "p-point-right";
                  rightPointElement.children[0].children[1].text = rightPoint;
                  rightPointElement.properties.onmousedown = "ui.parts.drawBoard.connect.create('" + drawBoardId + "','" + id + "')";
                  shapeExteriorElement.children.push(rightPointElement);
                }
              }

              var shapeElement = common.deepCopy(shapeConfig);
              if (isDblclick == true) shapeElement.properties.ondblclick = "ui.parts.drawBoard.shape.dblclick('" + drawBoardId + "','" + id + "')";
              if (isDrag == true) shapeElement.properties.onmousedown = "ui.parts.drawBoard.shape.drag('" + drawBoardId + "','" + id + "')";
              if (isChecked == true) shapeElement.properties.onclick = "ui.parts.drawBoard.shape.checked('" + drawBoardId + "','" + id + "')";
              if (common.is.Array(body)) shapeElement.children = body;
              else if (common.is.Object(body)) shapeElement.children.push(body);
              else if (common.is.String(body)) shapeElement.text = body;
              shapeExteriorElement.children.push(shapeElement);

              drawBoard.shape["shape" + id] = {
                id: id,
                params: params,
                config: shapeExteriorElement,
                position: {
                  id: id,
                  in: isConnect != true || leftPoint === false ? false : true,
                  out: isConnect != true || rightPoint === false ? false : true,
                  top: parseInt(top),
                  left: parseInt(left),
                  right: parseInt(left) + parseInt(width),
                  bottom: parseInt(top) + parseInt(height),
                  zIndex: parseInt(zIndex),
                },
              };

              shapeSet.currNum += 1; // shape count + 1
              if (type) keyCurrNum[type] += 1; // curr key's shape count + 1
              $("#" + drawIdPrefix + drawBoardId).append(buildUtil.buildModule(shapeExteriorElement));
            }
          }
        },
        dblclick: function (drawBoardId, id) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var curr = drawBoard.shape["shape" + id];
            var params = curr.params;
            common.callFu(window.ui, params.call.dblclick.fn, params.call.dblclick.params, curr);
          }
        },
        getConfig: function (drawBoardId, id) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var curr = drawBoard.shape["shape" + id];
            if (curr) return curr.params;
          }
        },
        change: function (drawBoardId, id, properties, style) {
          // var curr = shapeData["shape" + id];
          // var params = curr.params;
          // var shapeDom = $("#" + shapeIdPrefix + id + " *[name='" + shapeName + "']");
          // if (properties) {
          //     for (var propertiesKey in properties) {
          //         if (properties.hasOwnProperty(propertiesKey)) {
          //             var property = properties[propertiesKey];
          //             shapeDom.attr(propertiesKey, property);
          //         }
          //     }
          // }
          // if (style && style.length > 0) {
          //     style = style.replace(/(\s*$)/g, "");
          //     var lastChar = style.charAt(style.length - 1);
          //     if (lastChar != ";") style += ";";
          //     var oldStyle = shapeDom.attr("style");
          //     oldStyle = oldStyle ? oldStyle : "";
          //     shapeDom.attr("style", style + oldStyle);
          // }
          // common.callFu(window.ui, params.call.change.fn, params.call.change.params, curr);
        },
        drag: function (drawBoardId, id) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var currShapeObj = drawBoard.shape["shape" + id];

            var event = window.event || arguments.callee.caller.arguments[0];
            var pageX = event.pageX;
            var pageY = event.pageY;

            var curr = $("#" + shapeIdPrefix + id);
            var currWidth = curr.width();
            var currHeight = curr.height();
            var currMarginTop = parseInt(curr.css("marginTop"));
            var currMarginLeft = parseInt(curr.css("marginLeft"));

            var currParent = curr.parent();
            var parentWidth = currParent.width();
            var parentHeight = currParent.height();

            event.stopPropagation();

            var lastNewMarginTop = undefined;
            var lastNewMarginLeft = undefined;

            currParent.bind("mousemove", function (e) {
              var moveX = e.pageX;
              var moveY = e.pageY;
              var newMarginTop = currMarginTop + (moveY - pageY);
              var newMarginLeft = currMarginLeft + (moveX - pageX);

              if (newMarginTop < 0) newMarginTop = 0;
              if (newMarginTop > parentHeight - currHeight) newMarginTop = parentHeight - currHeight;
              if (newMarginLeft < 0) newMarginLeft = 0;
              if (newMarginLeft > parentWidth - currWidth) newMarginLeft = parentWidth - currWidth;

              lastNewMarginTop = newMarginTop;
              lastNewMarginLeft = newMarginLeft;
              curr.css("marginTop", newMarginTop);
              curr.css("marginLeft", newMarginLeft);

              connectFn.moveShape(drawBoardId, id, newMarginTop, newMarginLeft, currWidth, currHeight);
            });
            currParent.bind("mouseup", function (e) {
              if (lastNewMarginTop && lastNewMarginLeft) {
                var shapePositionObj = currShapeObj.position;
                shapePositionObj.top = lastNewMarginTop;
                shapePositionObj.left = lastNewMarginLeft;
                shapePositionObj.right = lastNewMarginLeft + currWidth;
                shapePositionObj.bottom = lastNewMarginTop + currHeight;
              }

              currParent.unbind("mousemove mouseup");
            });
          }
        },
        checked: function (drawBoardId, id) {
          var event = window.event || arguments.callee.caller.arguments[0];
          var shapeDom = $("#" + shapeIdPrefix + id);
          var status = shapeDom.attr("status");
          if (status == "1") {
            shapeDom.removeClass("p-draw-shape-focus");
            shapeDom.attr("status", "0");
          } else {
            shapeDom.addClass("p-draw-shape-focus");
            shapeDom.attr("status", "1");
          }
          event.stopPropagation();
        },
        uncheckAll: function (drawBoardId) {
          var shapeDom = $("#" + drawIdPrefix + drawBoardId).children(".p-draw-exterior");
          shapeDom.attr("status", "0");
          shapeDom.removeClass("p-draw-shape-focus");
        },
        delete: function (drawBoardId, id) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var curr = drawBoard.shape["shape" + id];
            var params = curr.params;
            var event = window.event || arguments.callee.caller.arguments[0];
            if (event.keyCode == 8) {
              // 删除键
              if (confirm("确认删除吗？")) {
                if (id) {
                  $("#" + shapeIdPrefix + id).remove();
                  connectFn.removeShape(drawBoardId, id);
                  delete drawBoard.shape["shape" + id];
                }
                common.callFu(window.ui, params.call.delete.fn, params.call.delete.params, curr);
              }
            }
          }
        },
        deleteAll: function (drawBoardId) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard && drawBoard.shape) {
            var shapes = drawBoard.shape;
            for (var shapeKey in shapes) {
              if (shapes.hasOwnProperty(shapeKey)) {
                var shape = shapes[shapeKey];
                var shapeDom = $("#" + shapeIdPrefix + shape.id);
                if (shapeDom.attr("status") == "1") {
                  shapeDom.remove();
                  connectFn.removeShape(drawBoardId, shape.id);
                  delete shapes["shape" + shape.id];
                }
              }
            }
          }
        },
        zoom: function (drawBoardId, id) {},
      };
      var connectFn = {
        create: function (drawBoardId, shapeId) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var event = window.event || arguments.callee.caller.arguments[0];
            event.stopPropagation();

            var pageX = event.pageX;
            var pageY = event.pageY;

            var connectSvgIndex = drawBoardId;
            var connectElement = common.deepCopy(connectConfig.line);

            var curr = $("#" + shapeIdPrefix + shapeId);
            var currWidth = parseInt(curr.width());
            var currHeight = parseInt(curr.height());
            var currMarginTop = parseInt(curr.css("marginTop"));
            var currMarginLeft = parseInt(curr.css("marginLeft"));

            var currParent = curr.parent();
            var parentWidth = currParent.width();
            var parentHeight = currParent.height();

            var connectIndex = partCommon.uuid();
            var strokeWidth = parseInt(connectElement.properties["stroke-width"]) / 2; // 上下居中偏移量
            connectElement.properties.id = connectLineIdPrefix + connectIndex;
            connectElement.properties.x1 = currMarginLeft + currWidth + stratGap;
            connectElement.properties.y1 = currMarginTop + currHeight / 2 + strokeWidth;
            connectElement.properties.x2 = currMarginLeft + currWidth + stratGap;
            connectElement.properties.y2 = currMarginTop + currHeight / 2 + strokeWidth;
            //connectElement.properties["marker-end"] = "url(#dragArrow)";
            connectElement.properties.onmousedown = "ui.parts.drawBoard.connect.checked('" + drawBoardId + "','" + connectIndex + "', true)";
            connectElement.properties.onkeydown = "ui.parts.drawBoard.connect.delete('" + drawBoardId + "','" + connectIndex + "')";

            if (connectSvgIdArr.length == 0 || connectSvgIdArr.indexOf(connectSvgIndex) == -1) {
              connectSvgIdArr.push(connectSvgIndex);
              var connectSvgElement = common.deepCopy(connectConfig.marker);
              connectSvgElement.properties.id = connectIdPrefix + connectSvgIndex;
              //connectSvgElement.properties.onclick = "ui.parts.drawBoard.connect.uncheckAll('" + connectSvgIndex + "')";
              currParent.append(buildUtil.buildModule(connectSvgElement));
            }
            var connectSvgObj = currParent.children("*[name='" + connectSvgName + "']").children("svg");
            connectSvgObj.html(connectSvgObj.html() + buildUtil.buildModule(connectElement));

            currParent.bind("mousemove", function (e) {
              var moveX = parseInt(e.pageX);
              var moveY = parseInt(e.pageY);

              var currParentMarginTop = parseInt(currParent.css("marginTop"));
              var currParentMarginLeft = parseInt(currParent.css("marginLeft"));

              var connect = $("#" + connectLineIdPrefix + connectIndex);
              connect.attr("x2", moveX);
              connect.attr("y2", moveY);
            });
            currParent.bind("mouseup", function (e) {
              var moveX = parseInt(e.pageX);
              var moveY = parseInt(e.pageY);

              var endMinDistance = undefined;
              var endShapePositionObj = undefined;
              var currShapes = drawBoard.shape;
              for (var positionKey in currShapes) {
                if (currShapes.hasOwnProperty(positionKey)) {
                  var currShapeObj = currShapes[positionKey];
                  var positionObj = currShapeObj.position;

                  if (positionObj.in == true) {
                    var leftDistance = moveX - positionObj.left;
                    var rightDistance = positionObj.right - moveX;
                    var topDistance = moveY - positionObj.top;
                    var bottomDistance = positionObj.bottom - moveY;

                    if (leftDistance > allowNum && rightDistance > allowNum && topDistance > allowNum && bottomDistance > allowNum) {
                      var minDistance = Math.min(Math.abs(leftDistance), Math.abs(rightDistance), Math.abs(topDistance), Math.abs(bottomDistance));
                      if (endMinDistance == undefined || endShapePositionObj == undefined || endMinDistance > minDistance) {
                        endMinDistance = minDistance;
                        endShapePositionObj = positionObj;
                      }
                    }
                  }
                }
              }

              var isAllow = false;
              if (!drawBoard.params.connect.verify) isAllow = true;
              else if (endMinDistance != undefined && endShapePositionObj != undefined) isAllow = connectFn.verify(drawBoardId, shapeId, endShapePositionObj.id);
              if (isAllow == true) {
                var connect = $("#" + connectLineIdPrefix + connectIndex);
                connect.attr("x2", endShapePositionObj.left - endGap);
                connect.attr("y2", endShapePositionObj.top + (endShapePositionObj.bottom - endShapePositionObj.top) / 2 + strokeWidth);
                connect.attr("stroke", "#990033");
                //connect.attr("marker-end", "url(#endArrow)");

                var connectsObj = drawBoardData["part" + drawBoardId].connects;
                connectsObj["connect" + connectIndex] = {
                  id: connectIndex,
                  shapes: [
                    { shapeId: shapeId, type: "start" },
                    { shapeId: endShapePositionObj.id, type: "end" },
                  ],
                };
              } else {
                $("#" + connectLineIdPrefix + connectIndex).remove();
              }

              //connectIndex += 1;
              currParent.unbind("mousemove mouseup");
            });
          }
        },
        add: function (drawBoardId, fromId, toId, connectId) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var fromShape = drawBoard.shape["shape" + fromId];
            var toShape = drawBoard.shape["shape" + toId];
            var connectsObj = drawBoardData["part" + drawBoardId].connects;

            if (fromShape && toShape) {
              var connectIndex = partCommon.uuid();
              if (!connectId) connectId = connectLineIdPrefix + connectIndex;
              if (!connectsObj["connect" + connectId]) {
                var connectSvgIndex = drawBoardId;
                var connectElement = common.deepCopy(connectConfig.line);

                var from = $("#" + shapeIdPrefix + fromId);
                var fromWidth = parseInt(from.width());
                var fromHeight = parseInt(from.height());
                var fromMarginTop = parseInt(from.css("marginTop"));
                var fromMarginLeft = parseInt(from.css("marginLeft"));

                var to = $("#" + shapeIdPrefix + toId);
                var toWidth = parseInt(to.width());
                var toHeight = parseInt(to.height());
                var toMarginTop = parseInt(to.css("marginTop"));
                var toMarginLeft = parseInt(to.css("marginLeft"));

                var parentDom = from.parent();

                var strokeWidth = parseInt(connectElement.properties["stroke-width"]) / 2; // 上下居中偏移量
                connectElement.properties.id = connectLineIdPrefix + connectId;
                connectElement.properties.x1 = fromMarginLeft + fromWidth + stratGap;
                connectElement.properties.y1 = fromMarginTop + fromHeight / 2 + strokeWidth;
                connectElement.properties.x2 = toMarginLeft - endGap;
                connectElement.properties.y2 = toMarginTop + toHeight / 2 + strokeWidth;
                connectElement.properties.stroke = "#990033";
                connectElement.properties.onmousedown = "ui.parts.drawBoard.connect.checked('" + drawBoardId + "','" + connectId + "', true)";
                connectElement.properties.onkeydown = "ui.parts.drawBoard.connect.delete('" + drawBoardId + "','" + connectId + "')";

                if (connectSvgIdArr.length == 0 || connectSvgIdArr.indexOf(connectSvgIndex) == -1) {
                  connectSvgIdArr.push(connectSvgIndex);
                  var connectSvgElement = common.deepCopy(connectConfig.marker);
                  connectSvgElement.properties.id = connectIdPrefix + connectSvgIndex;
                  parentDom.append(buildUtil.buildModule(connectSvgElement));
                }
                var connectSvgObj = parentDom.children("*[name='" + connectSvgName + "']").children("svg");
                connectSvgObj.html(connectSvgObj.html() + buildUtil.buildModule(connectElement));

                connectsObj["connect" + connectId] = {
                  id: connectId,
                  shapes: [
                    { shapeId: fromId, type: "start" },
                    { shapeId: toId, type: "end" },
                  ],
                };
              }
            }
          }
        },
        moveShape: function (drawBoardId, shapeId, top, left, width, height) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var connects = drawBoard.connects;
            for (var connectsKey in connects) {
              if (connects.hasOwnProperty(connectsKey)) {
                var connect = connects[connectsKey];
                var connectDom = $("#" + connectLineIdPrefix + connect.id);
                var strokeWidth = parseInt(connectDom.attr("stroke-width")) / 2;
                if (connect.shapes[0].shapeId == shapeId) {
                  // start
                  connectDom.attr("x1", parseInt(left) + parseInt(width) + stratGap);
                  connectDom.attr("y1", parseInt(top) + parseInt(width) / 2 + strokeWidth);
                }
                if (connect.shapes[1].shapeId == shapeId) {
                  // end
                  connectDom.attr("x2", parseInt(left) - endGap);
                  connectDom.attr("y2", parseInt(top) + parseInt(width) / 2 + strokeWidth);
                }
              }
            }
          }
        },
        checked: function (drawBoardId, connectLineId) {
          var event = window.event || arguments.callee.caller.arguments[0];
          var connectDom = $("#" + connectLineIdPrefix + connectLineId);
          var status = connectDom.attr("status");
          if (status == "1") {
            connectDom.attr("stroke", "#990033");
            connectDom.attr("status", "0");
          } else {
            connectDom.attr("stroke", "gray");
            connectDom.attr("status", "1");
          }
          event.stopPropagation();
        },
        uncheckAll: function (drawBoardId) {
          $("#" + connectIdPrefix + drawBoardId + " svg")
            .children()
            .each(function () {
              if ($(this).attr("status") == 1) {
                $(this).attr("stroke", "#990033");
                $(this).attr("status", "0");
              }
            });
        },
        delete: function (drawBoardId, connectLineId, verifyDeleteKey) {
          var event = window.event || arguments.callee.caller.arguments[0];
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard && (verifyDeleteKey != true || event.keyCode == 8)) {
            $("#" + connectLineIdPrefix + connectLineId).remove();
            // update connect verify data
            if (drawBoard.params.connect.verify) {
              var connectObj = drawBoard.connects["connect" + connectLineId];
              if (connectObj) {
                var connectShapes = connectObj.shapes;
                var connectStartShape, connectEndShape;
                for (var i = 0; i < connectShapes.length; i++) {
                  var connectShape = connectShapes[i];
                  if (connectShape.type == "start") connectStartShape = connectShape;
                  else connectEndShape = connectShape;
                }
                var startShape = drawBoard.shape["shape" + connectStartShape.shapeId];
                var endShape = drawBoard.shape["shape" + connectEndShape.shapeId];
                if (startShape && startShape.params.connect) {
                  startShape.params.connect.currOut -= 1;
                  if (startShape.params.connect.currTo[endShape.params.type] != undefined) startShape.params.connect.currTo[endShape.params.type] -= 1;
                }
                if (endShape && endShape.params.connect) {
                  endShape.params.connect.currIn -= 1;
                  if (endShape.params.connect.currFrom[startShape.type] != undefined) endShape.params.connect.currFrom[startShape.type] -= 1;
                }
              }
            }
            delete drawBoard.connects["connect" + connectLineId];
          }
        },
        deleteAll: function (drawBoardId) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var connects = drawBoard.connects;
            for (var connectsKey in connects) {
              if (connects.hasOwnProperty(connectsKey)) {
                var connect = connects[connectsKey];
                var connectDom = $("#" + connectLineIdPrefix + connect.id);
                if (connectDom.attr("status") == "1") {
                  this.delete(drawBoardId, connect.id);
                }
              }
            }
          }
        },
        removeShape: function (drawBoardId, shapeId) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var connects = drawBoard.connects;
            for (var connectsKey in connects) {
              if (connects.hasOwnProperty(connectsKey)) {
                var connect = connects[connectsKey];
                if (connect.shapes[0].shapeId == shapeId || connect.shapes[1].shapeId == shapeId) {
                  this.delete(drawBoardId, connect.id);
                }
              }
            }
          }
        },
        verify: function (drawBoardId, fromShapeId, toShapeId) {
          var drawBoard = drawBoardData["part" + drawBoardId];
          if (drawBoard) {
            var shapes = drawBoard.shape;
            var connectVerify = drawBoard.params.connect.verify;
            var fromShape = shapes["shape" + fromShapeId].params;
            var toShape = shapes["shape" + toShapeId].params;

            if (fromShape && toShape) {
              if (connectVerify) {
                if (connectVerify[fromShape.type]) {
                  if (!fromShape.connect) fromShape.connect = {};
                  if (!fromShape.connect.currOut) fromShape.connect.currOut = 0;
                  if (!fromShape.connect.currTo) fromShape.connect.currTo = {};

                  var fromShapeConnect = connectVerify[fromShape.type];
                  if (fromShapeConnect.maxOut) {
                    if (fromShape.connect.currOut >= fromShapeConnect.maxOut) return false;
                  }
                  if (fromShapeConnect.to) {
                    if (common.is.String(fromShapeConnect.to)) {
                      if (fromShapeConnect.to != toShape.type) return false;
                    } else if (common.is.Array(fromShapeConnect.to)) {
                      if (fromShapeConnect.to.indexOf(toShape.type) == -1) return false;
                    } else if (common.is.Object(fromShapeConnect.to)) {
                      if (!fromShapeConnect.to[toShape.type]) return false;
                      else {
                        var fromShapeConnectCurrTo = fromShape.connect.currTo;
                        var fromShapeConnectCurrToCount = fromShapeConnectCurrTo[toShape.type];
                        if (!fromShapeConnectCurrToCount) fromShapeConnectCurrToCount = fromShapeConnectCurrTo[toShape.type] = 0;
                        if (parseInt(fromShapeConnect.to[toShape.type]) <= fromShapeConnectCurrToCount) return false;
                      }
                    }
                  }
                }
                if (connectVerify[toShape.type]) {
                  if (!toShape.connect) toShape.connect = {};
                  if (!toShape.connect.currIn) toShape.connect.currIn = 0;
                  if (!toShape.connect.currFrom) toShape.connect.currFrom = {};

                  var toShapeConnect = connectVerify[toShape.type];
                  if (toShapeConnect.maxIn) {
                    if (toShape.connect.currIn >= toShapeConnect.maxIn) return false;
                  }
                  if (toShapeConnect.from) {
                    if (common.is.String(toShapeConnect.from)) {
                      if (toShapeConnect.from != fromShape.type) return false;
                    } else if (common.is.Array(toShapeConnect.from)) {
                      if (toShapeConnect.from.indexOf(fromShape.type) == -1) return false;
                    } else if (common.is.Object(toShapeConnect.from)) {
                      if (!toShapeConnect.from[fromShape.type]) return false;
                      else {
                        var toShapeConnectCurrFrom = toShape.connect.currFrom;
                        var toShapeConnectCurrFromCount = toShapeConnectCurrFrom[fromShape.type];
                        if (!toShapeConnectCurrFromCount) toShapeConnectCurrFromCount = toShapeConnectCurrFrom[fromShape.type] = 0;
                        if (parseInt(toShapeConnect.from[fromShape.type]) <= toShapeConnectCurrFromCount) return false;
                      }
                    }
                  }
                }

                if (toShape.connect) {
                  toShape.connect.currIn += 1;
                  if (toShape.connect.currFrom[fromShape.type] != undefined) toShape.connect.currFrom[fromShape.type] += 1;
                }
                if (fromShape.connect) {
                  fromShape.connect.currOut += 1;
                  if (fromShape.connect.currTo[toShape.type] != undefined) fromShape.connect.currTo[toShape.type] += 1;
                }
              }
              return true;
            }
          }
          return false;
        },
      };

      function build(params) {
        var id = params.id;
        var style = params.style;
        var tools = params.tool;
        var region = params.region == true ? true : false;
        if (common.isEmpty(id)) params.id = index = id = index + 1;

        // connect call
        var connect = params.connect;
        if (!connect) connect = params.connect = {};
        if (!connect.call) connect.call = {};
        if (!connect.call.create) connect.call.create = {};
        if (!connect.call.checked) connect.call.delete = {};
        if (!connect.call.delete) connect.call.delete = {};

        // shape config
        if (!params.shape) params.shape = {};
        params.shape.currNum = 0;
        params.shape.keyCurrNum = {};

        // build tool
        var toolsTitle = tools.title ? tools.title : "";
        var toolsStyle = tools.style ? tools.style : "";
        // var toolsDrag = tools.drag == false ? false : true;
        // var toolsDivide = tools.divide;
        // var toolsDirection = tools.direction;
        var toolsBars = tools.bars;

        var drawBoardElement = common.deepCopy(drawBoardConfig);
        var toolsElements = drawBoardElement.children[0].children[1].children;
        drawBoardElement.children[0].children[0].text = toolsTitle; // title
        if (!common.isEmpty(style)) drawBoardElement.properties.style = style;
        if (!common.isEmpty(toolsStyle)) drawBoardElement.children[0].properties.style = toolsStyle;

        for (var toolsBarsIndex = 0; toolsBarsIndex < toolsBars.length; toolsBarsIndex++) {
          var toolBar = toolsBars[toolsBarsIndex];

          var barCol = parseInt(toolBar.col ? toolBar.col : 2);
          var barStyle = toolBar.style;
          var barElements = toolBar.elements;

          // 计算宽度和 margin
          var toolWidthStyle;
          if (barCol == 1) toolWidthStyle = "width:80%;margin: 8px 10%;";
          else if (barCol == 2) toolWidthStyle = "width:40%;margin:8px 5%;";
          else if (barCol == 3) toolWidthStyle = "width:27%;margin:8px 3%;";
          else if (barCol > 3 && barCol <= 5) toolWidthStyle = "width:" + (100 - barCol * 4) / barCol + "%;margin:6px 2%;";
          else if (barCol > 5 && barCol <= 10) toolWidthStyle = "width:" + (100 - barCol * 2) / barCol + "%;margin:6px 1%;";
          else if (barCol > 10) toolWidthStyle = "width:" + (100 - barCol) / barCol + "%;margin:5px 0.5%;";
          else toolWidthStyle = "width:40%;margin:5%;";

          for (var barIndex = 0; barIndex < barElements.length; barIndex++) {
            var tool = barElements[barIndex];

            var toolKey = toolIdPrefix + id + "_" + tool.key;
            var toolText = tool.text;
            var toolTitle = tool.title ? tool.title : "";
            var toolIcon = tool.icon;
            var toolBgColor = tool.bgColor;
            var toolIsClick = tool.click == false ? false : true;
            var toolIsDrag = tool.drag == true ? true : false;
            if (!tool.call) tool.call = {};
            if (!tool.call.click) tool.call.click = {};
            if (!tool.call.drag) tool.call.drag = {};

            var bgStyle = "";
            if (!common.isEmpty(toolBgColor)) bgStyle = bgStyle + "background-color:" + toolBgColor + ";";
            var toolElement = { name: "span", properties: { id: toolKey, style: toolWidthStyle + bgStyle + barStyle, title: toolTitle } };

            if (toolIsDrag) toolElement.properties.onmousedown = "ui.parts.drawBoard.tool.drag('" + id + "','" + toolsBarsIndex + "','" + tool.key + "')";
            else if (toolIsClick) toolElement.properties.onclick = "ui.parts.drawBoard.tool.click('" + id + "','" + toolsBarsIndex + "','" + tool.key + "')";

            if (!common.isEmpty(toolIcon)) toolElement.children = [{ name: "img", properties: { src: toolIcon, width: "100%", height: "100%" } }];
            else toolElement.text = toolText;
            toolsElements.push(toolElement);
          }

          if (toolsBarsIndex != toolsBars.length - 1) {
            toolsElements.push(buildUtil.tags.brConfig[0]);
            toolsElements.push({ name: "div", properties: { class: "p-divide-line" }, text: "" });
          }
        }

        var panelElement = drawBoardElement.children[1];
        panelElement.properties.id = drawIdPrefix + id;
        if (region) panelElement.properties.onmousedown = "ui.parts.drawBoard.region('" + id + "')";
        else panelElement.properties.onclick = "ui.parts.drawBoard.click('" + id + "')";

        return (drawBoardData["part" + id] = {
          id: id,
          params: params,
          config: drawBoardElement,
          shape: {},
          connects: {},
        });
      }

      return {
        build: build,
        click: drawBoardFn.click,
        region: drawBoardFn.region,
        zoom: drawBoardFn.zoom,
        getCurr: drawBoardFn.getCurr,
        tool: {
          click: toolFn.click,
          drag: toolFn.drag,
        },
        shape: {
          create: shapeFn.create,
          dblclick: shapeFn.dblclick,
          getConfig: shapeFn.getConfig,
          change: shapeFn.change,
          drag: shapeFn.drag,
          checked: shapeFn.checked,
          uncheckAll: shapeFn.uncheckAll,
          delete: shapeFn.delete,
          deleteAll: shapeFn.deleteAll,
          zoom: shapeFn.zoom,
        },
        connect: {
          create: connectFn.create,
          add: connectFn.add,
          checked: connectFn.checked,
          uncheckAll: connectFn.uncheckAll,
          delete: connectFn.delete,
          deleteAll: connectFn.deleteAll,
        },
      };
    }

    // svg module ----------------------------------------------------------------------
    function svgObj() {
      var index = 0;
      var elementIndex = 0;
      var idPrefix = setting.svg.idPrefix;
      var elementIdPrefix = setting.svg.elementIdPrefix;

      var svgData = {};
      var svgElementData = {};
      var svgConfig = {
        name: "svg",
        properties: { width: "100%", height: "100%", version: "1.1", xmlns: "http://www.w3.org/2000/svg" },
        children: [],
      };
      var svgShapeConfig = {
        rect: { name: "rect", properties: { x: "0", y: "0", width: "50px", height: "50px", fill: "black", stroke: "black", "stroke-width": "2" }, text: "" },
        circle: { name: "circle", properties: { cx: "0", cy: "0", r: "", fill: "black", stroke: "black", "stroke-width": "2" }, text: "" },
        image: { name: "image", properties: { "xlink:href": "", x: "0", y: "0", height: "100%", width: "100%" }, text: "" },
        text: { name: "text", properties: { x: "50%", y: "50%", dy: ".3em", fill: "red", "text-anchor": "middle", "font-size": "12px" }, text: "" },
      };

      var svgFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, svgData);
        },
        click: function (id) {
          var curr = svgData["part" + id];
          if (curr) {
            var params = curr.params;
            common.callFu(window.ui, params.call.click.fn, params.call.click.params, curr);
          }
        },
        dblclick: function (id) {
          var curr = svgData["part" + id];
          if (curr) {
            var params = curr.params;
            common.callFu(window.ui, params.call.dblclick.fn, params.call.dblclick.params, curr);
          }
        },
      };
      var svgElementFn = {
        click: function (id, elementId) {
          var svgElementObj = svgElementData["svg" + id];
          if (svgElementObj) {
            var elementObj = svgElementObj["element" + elementId];
            if (elementObj) {
              var params = elementObj.params;
              common.callFu(window.ui, params.call.click.fn, params.call.click.params, elementObj.params);
            }
          }
        },
        dblclick: function (id, elementId) {
          var svgElementObj = svgElementData["svg" + id];
          if (svgElementObj) {
            var elementObj = svgElementObj["element" + elementId];
            if (elementObj) {
              var params = elementObj.params;
              common.callFu(window.ui, params.call.dblclick.fn, params.call.dblclick.params, elementObj.params);
            }
          }
        },
        get: function (id, elementId) {
          var params = undefined;
          var svgElementObj = svgElementData["svg" + id];
          if (svgElementObj) {
            if (elementId) {
              var elementObj = svgElementObj["element" + elementId];
              if (elementObj) params = elementObj.params;
            } else {
              params = svgElementObj;
            }
          }
          return params;
        },
        add: function (id, params) {
          var elementObj = buildElement(id, params);
          var elementHtml = ui.build.buildModule(elementObj.config);
          $("#" + idPrefix + id).html($("#" + idPrefix + id).html() + elementHtml);
          common.callFu(window.ui, params.call.add.fn, params.call.add.params, svgElementObj);
        },
        change: function (id, elementId, properties, style, other) {
          var svgElementObj = svgElementData["svg" + id];
          if (svgElementObj) {
            var elementObj = svgElementObj["element" + elementId];
            if (elementObj) {
              var params = elementObj.params;
              if (!params.properties) params.properties = {};
              if (!params.other) params.other = {};
              var shapeDom = $("#" + elementIdPrefix + elementId);
              if (properties) {
                for (var propertiesKey in properties) {
                  if (properties.hasOwnProperty(propertiesKey)) {
                    var property = properties[propertiesKey];
                    shapeDom.attr(propertiesKey, property);
                    params.properties[propertiesKey] = property;
                  }
                }
              }
              if (other) {
                for (var otherKey in other) {
                  if (other.hasOwnProperty(otherKey)) {
                    var otherValue = other[otherKey];
                    params.other[otherKey] = otherValue;
                  }
                }
              }
              if (style && style.length > 0) {
                style = style.replace(/(\s*$)/g, "");
                var lastChar = style.charAt(style.length - 1);
                if (lastChar != ";") style += ";";

                var oldStyle = shapeDom.attr("style");
                oldStyle = oldStyle ? oldStyle : "";

                shapeDom.attr("style", style + oldStyle);
              }
              common.callFu(window.ui, params.call.change.fn, params.call.change.params, svgElementObj);
            }
          }
        },
        remove: function (id, elementId) {
          $("#" + elementIdPrefix + elementId).remove();
          var svgElementObj = svgElementData["svg" + id];
          if (svgElementObj) {
            var elementObj = svgElementObj["element" + elementId];
            if (elementObj) {
              var params = elementObj.params;
              common.callFu(window.ui, params.call.remove.fn, params.call.remove.params, elementObj.params);
              delete svgElementObj["element" + elementId];
            }
          }
        },
      };

      function buildElement(id, element) {
        var elementId = element.id;
        var elementTitle = element.title ? element.title : "";
        var elementText = element.text;
        var elementType = element.type;
        var elementproperties = element.properties ? element.properties : {};
        var elementIsClick = element.click == true ? true : false;
        var elementIsDblclick = element.dblclick == true ? true : false;
        if (common.isEmpty(elementId)) elementIndex = elementId = element.id = elementIndex + 1;

        if (!element.call) element.call = {};
        if (!element.call.click) element.call.click = {};
        if (!element.call.dblclick) element.call.dblclick = {};
        if (!element.call.add) element.call.add = {};
        if (!element.call.change) element.call.change = {};
        if (!element.call.remove) element.call.remove = {};

        var svgshapeExteriorElement = common.deepCopy(svgShapeConfig[elementType]);
        svgshapeExteriorElement.properties.id = elementIdPrefix + elementId;
        svgshapeExteriorElement.properties.title = elementTitle;
        if (elementText) svgshapeExteriorElement.text = elementText;
        if (elementIsClick == true) svgshapeExteriorElement.properties.onclick = "ui.parts.svg.element.click('" + id + "','" + elementId + "')";
        if (elementIsDblclick == true) svgshapeExteriorElement.properties.ondblclick = "ui.parts.svg.element.dblclick('" + id + "','" + elementId + "')";

        for (var key in elementproperties) if (elementproperties.hasOwnProperty(key)) svgshapeExteriorElement.properties[key] = elementproperties[key];

        var elementData = svgElementData["svg" + id];
        if (!elementData) elementData = svgElementData["svg" + id] = {};
        elementData["element" + elementId] = { id: elementId, params: element };

        return {
          id: elementId,
          params: element,
          config: svgshapeExteriorElement,
        };
      }

      function build(params) {
        var id = params.id;
        var style = params.style;
        var elements = params.elements;
        var isClick = params.click == true ? true : false;
        var isDblclick = params.dblclick == true ? true : false;
        if (common.isEmpty(id)) params.id = index = id = index + 1;

        if (!params.call) params.call = {};
        if (!params.call.click) params.call.click = {};
        if (!params.call.dblclick) params.call.dblclick = {};

        var svgElement = common.deepCopy(svgConfig);
        svgElement.properties.id = idPrefix + id;
        svgElement.properties.style = style;
        if (isClick == true) svgElement.properties.onclick = "ui.parts.svg.click('" + id + "')";
        if (isDblclick == true) svgElement.properties.ondblclick = "ui.parts.svg.dblclick('" + id + "')";

        for (var i = 0; i < elements.length; i++) {
          var element = elements[i];
          var elementObj = buildElement(id, element);
          svgElement.children.push(elementObj.config);
        }

        return (svgData["part" + id] = {
          id: id,
          params: params,
          config: svgElement,
        });
      }

      return {
        build: build,
        getConfig: svgFn.getConfig,
        click: svgFn.click,
        dblclick: svgFn.dblclick,
        element: {
          click: svgElementFn.click,
          dblclick: svgElementFn.dblclick,
          get: svgElementFn.get,
          add: svgElementFn.add,
          change: svgElementFn.change,
          remove: svgElementFn.remove,
        },
      };
    }

    // buttons module ----------------------------------------------------------------------
    function buttonsObj() {
      var index = 0;
      var idPrefix = setting.buttons.idPrefix;

      var buttonsData = {};
      var buttonsDivConfig = { name: "div", properties: { class: "p-buttons" }, children: [] };
      var buttonConfig = { name: "div", properties: { class: "p-button" }, text: "" };

      var buttonsFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, buttonsData);
        },
        click: function (id, buttonId) {
          var buttonsObj = buttonsData["part" + id];
          var buttons = buttonsObj.params.buttons;

          for (var index = 0; index < buttons.length; index++) {
            var button = buttons[index];
            if (button.id == buttonId) {
              common.callFu(window.ui, button.call.click.fn, button.call.click.params, button);
              return;
            }
          }
        },
      };

      function build(params) {
        var id = params.id;
        var align = params.align;
        var buttons = params.buttons;
        if (common.isEmpty(id)) params.id = id = index = index + 1;

        var buttonsElement = common.deepCopy(buttonsDivConfig);
        if (align && align == "left") buttonsElement.properties.class += " p-left";
        else if (align && align == "right") buttonsElement.properties.class += " p-right";

        for (var buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
          var button = buttons[buttonIndex];
          var buttonElement = common.deepCopy(buttonConfig);

          var buttonId = button.id;
          var buttonText = button.text;

          if (!button.call) button.call = {};
          if (button.call.click) buttonElement.properties.onclick = "ui.parts.buttons.click('" + id + "','" + buttonId + "')";

          buttonElement.properties.id = idPrefix + id + "_" + buttonId;
          buttonElement.text = buttonText;

          buttonsElement.children.push(buttonElement);
        }

        params.id = id;
        return (buttonsData["part" + id] = {
          id: id,
          params: params,
          config: buttonsElement,
        });
      }

      return {
        build: build,
        getConfig: buttonsFn.getConfig,
        click: buttonsFn.click,
      };
    }

    // lineMap module ----------------------------------------------------------------------
    function lineMapObj() {
      var index = 0;
      var lineIndex = 0;
      var idPrefix = setting.lineMap.idPrefix;
      var lineIdPrefix = setting.lineMap.lineIdPrefix;

      var positionData = {};
      var lineMapData = {};
      var lineMapConfig = {
        name: "div",
        properties: { style: "width:100%;height:100%;" },
        children: [
          {
            name: "svg",
            properties: { width: "100%", height: "100%", version: "1.1", xmlns: "http://www.w3.org/2000/svg" },
            children: [],
          },
        ],
      };
      var lineConfig = {
        line: { name: "line", properties: { x1: "0", y1: "0", x2: "0", y2: "0", stroke: "black", "stroke-width": "2" }, text: "" },
        polyline: { name: "polyline", properties: { points: "0,0 0,0", stroke: "black", "stroke-width": "2" }, text: "" },
        path: { name: "path", properties: { d: "", stroke: "black", "stroke-width": "2" }, text: "" },
      };

      var positionsFn = {
        set: function (id, lineId, params) {
          var zIndex = params.z;
          var points = params.points;
          var direction = params.direction ? params.direction : {};
          var directionX = direction.x ? direction.x : "auto";
          var directionY = direction.y ? direction.y : "auto";

          var positionDataObj = positionData["position_" + id];
          if (!positionDataObj) {
            positionDataObj = positionData["position_" + id] = {
              id: id,
              xData: {},
              yData: {},
              lines: {},
            };
          }

          var positionLines = positionDataObj.lines;
          var positionLineObj = (positionLines["line_" + lineId] = {
            id: lineId,
            z: zIndex,
            points: [],
            next: function (x, y) {
              var position = x + "," + y;
              var points = this.points;
              var positionIndex = points.indexOf(position);
              if (positionIndex > -1 && positionIndex < points.length - 1) {
                var point = points[positionIndex + 1];
                var pointArr = point.split(",");
                return {
                  x: pointArr[0],
                  y: pointArr[1],
                };
              } else return undefined;
            },
          });
          var positionLinePoints = positionLineObj.points;

          var pointArr = points.split(" ");
          for (var i = 0; i < pointArr.length - 1; i++) {
            var startPoint = pointArr[i];
            var endPoint = pointArr[i + 1];

            var startXYArr = startPoint.split(",");
            var endXYArr = endPoint.split(",");

            if (startXYArr.length == 2 && endXYArr.length == 2) {
              var startX = parseInt(startXYArr[0]);
              var startY = parseInt(startXYArr[1]);
              var endX = parseInt(endXYArr[0]);
              var endY = parseInt(endXYArr[1]);

              var positionXDataObj = positionDataObj.xData;
              var positionYDataObj = positionDataObj.yData;

              var positionStartXObj = positionXDataObj["X" + startX];
              var positionEndXObj = positionXDataObj["X" + endX];
              if (!positionStartXObj) positionStartXObj = positionXDataObj["X" + startX] = { value: startX, yData: {} };
              if (!positionEndXObj) positionEndXObj = positionXDataObj["X" + endX] = { value: endX, yData: {} };

              var positionStartXYObj = positionStartXObj.yData["Y" + startY];
              var positionEndXYObj = positionEndXObj.yData["Y" + endY];
              if (!positionStartXYObj) positionStartXYObj = positionStartXObj.yData["Y" + startY] = { value: startY };
              if (!positionEndXYObj) positionEndXYObj = positionEndXObj.yData["Y" + endY] = { value: endY };
              positionStartXYObj["line" + lineId] = { id: lineId, type: "start", z: zIndex };
              positionEndXYObj["line" + lineId] = { id: lineId, type: "end", z: zIndex };

              if (directionX == "auto") {
                if (startX > endX) directionX = -1;
                else directionX = 1;
              }
              if (directionY == "auto") {
                if (startY > endY) directionY = -1;
                else directionY = 1;
              }
              var absDistanceX = Math.abs(endX - startX);
              var absDistanceY = Math.abs(endY - startY);
              var distance = absDistanceX >= absDistanceY ? absDistanceX : absDistanceY;
              for (var j = 0; j <= distance; j++) {
                var x = absDistanceX >= absDistanceY ? j : Math.round((absDistanceX * j) / absDistanceY);
                var y = absDistanceX >= absDistanceY ? Math.round((absDistanceY * j) / absDistanceX) : j;
                var fullX = startX + directionX * x;
                var fullY = startY + directionY * y;
                positionLinePoints.push(fullX + "," + fullY);
              }
            }
          }
        },
        get: function (id, params) {
          // var x = params.x;
          // var y = params.y;
          // var z = params.z;
          // var direction = params.direction ? params.direction : {};
          // var directionX = direction.x ? direction.x : 0;
          // var directionY = direction.y ? direction.y : 0;

          var positionObj = positionData["position_" + id];
          console.log("positionObj:", positionObj);
        },
        getNextPoints(id, currLineId, params, range) {
          var x = parseInt(params.x);
          var y = parseInt(params.y);
          var z = params.z ? parseInt(params.z) : undefined;
          range = range ? range : 1;

          var positionObj = positionData["position_" + id];
          var nextPosition = [];

          if (positionObj) {
            var includeCurrLineId = false;
            var currLines = positionObj.lines;
            var currXObj = positionObj.xData["X" + x];
            if (currXObj) {
              var currXYObj = currXObj.yData["Y" + y];
              if (currXYObj) {
                for (var currXYKey in currXYObj) {
                  if (currXYObj.hasOwnProperty(currXYKey) && currXYKey != "value") {
                    var currXYLine = currXYObj[currXYKey];
                    var currLineObj = currLines["line_" + currXYLine.id];
                    if (currXYLine.id == currLineId) includeCurrLineId = true;
                    if (currLineObj) {
                      if (currXYLine.type == "start" || currXYLine.type == "all") {
                        var next = currLineObj.next(currXObj.value, currXYObj.value);
                        if (next) nextPosition.push({ id: currXYLine.id, x: next.x, y: next.y, z: currXYLine.z });
                      }
                    }
                  }
                }
              }
            }
            if (currLineId && !includeCurrLineId) {
              var lineObj = currLines["line_" + currLineId];
              var next = lineObj.next(x, y);
              if (next) nextPosition.push({ id: lineObj.id, x: next.x, y: next.y, z: lineObj.z });
            }
          }
          return nextPosition;
        },
      };

      function build(params) {
        var id = params.id;
        var style = params.style;
        var elements = params.elements;
        if (common.isEmpty(id)) index = id = params.id = index + 1;

        var lineMapElement = common.deepCopy(lineMapConfig);

        for (var i = 0; i < elements.length; i++) {
          var element = elements[i];

          var elementId = element.id;
          var elementType = element.type ? element.type : "line";
          var elementZ = element.z;
          var elementDirection = element.direction;
          var elementproperties = element.properties;
          if (common.isEmpty(elementId)) lineIndex = elementId = element.id = lineIndex + 1;

          var lineElement = common.deepCopy(lineConfig[elementType]);
          lineElement.properties.id = lineIdPrefix + elementId;
          for (var key in elementproperties) if (elementproperties.hasOwnProperty(key)) lineElement.properties[key] = elementproperties[key];
          lineMapElement.children[0].children.push(lineElement);

          if ("line" == elementType)
            positionsFn.set(id, elementId, {
              points: elementproperties.x1 + "," + elementproperties.y1 + " " + elementproperties.x2 + "," + elementproperties.y2,
              direction: elementDirection,
              z: elementZ,
            });
          else if ("polyline" == elementType)
            positionsFn.set(id, elementId, {
              points: elementproperties.points,
              direction: elementDirection,
              z: elementZ,
            });
        }

        return (lineMapData["part" + id] = {
          id: id,
          params: params,
          config: lineMapElement,
        });
      }

      return {
        build: build,
        position: {
          get: positionsFn.get,
          getNextPoints: positionsFn.getNextPoints,
        },
      };
    }

    // custom module ------------------------------------------------------------------------
    function customObj() {
      var index = 0;
      var idPrefix = setting.custom.idPrefix;

      var customData = {};
      var customConfig = {
        div: { name: "div", properties: { style: "width:100%;height:100%;" } },
        iframe: { name: "iframe", properties: { frameborder: "0", width: "100%", height: "100%", src: "" }, text: "" },
      };

      customFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, customData);
        },
      };

      function build(params) {
        var id = params.id;
        var type = params.type;
        var content = params.content;
        var properties = params.properties;
        if (common.isEmpty(id)) params.id = index = id = index + 1;

        var customElement;
        if (type == "iframe") {
          customElement = common.deepCopy(customConfig.iframe);
        } else if (type == "include") {
          customElement = common.deepCopy(customConfig.div);
          common.ajax({
            method: "GET",
            url: content,
            async: false,
            success: function (tempText) {
              customElement.text = tempText;
            },
          });
        } else {
          // type == "temp"
          customElement = common.deepCopy(customConfig.div);
          if (common.is.Object(content)) customElement.children = [content];
          else if (common.is.Array(content)) customElement.children = content;
          else customElement.text = content;
        }

        for (var key in properties) if (properties.hasOwnProperty(key)) customElement.properties[key] = properties[key];

        return (customData["part" + id] = {
          id: id,
          params: params,
          config: customElement,
        });
      }

      return {
        build: build,
        getConfig: customFn.getConfig,
      };
    }

    // table module ------------------------------------------------------------------------
    function tableObj() {
      var index = 0;
      var idPrefix = setting.table.idPrefix;
      var checkIdPrefix = setting.table.checkIdPrefix;
      var checkAllIdPrefix = setting.table.checkAllIdPrefix;
      var rowNamePrefix = setting.table.rowNamePrefix;
      var colNamePrefix = setting.table.colNamePrefix;

      var tableData = {};
      var tableConfig = {
        name: "table",
        properties: { class: "p-table" },
        children: [
          { name: "thead", properties: {}, children: [] },
          { name: "tbody", properties: {}, children: [] },
        ],
      };
      var tableElementConfig = {
        tr: { name: "tr", properties: {}, children: [] },
        th: { name: "th", properties: {} },
        td: { name: "td", properties: {} },
        check: { name: "input", properties: { type: "checkbox" } },
      };

      tableFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, tableData);
        },
      };
      colFn = {
        add: function (tableId, arr, index) {
          // arr string function
        },
        remove: function (tableId, key) {
          var curr = tableData["part" + tableId];
          if (curr) {
            var header = curr.params.header;
            var keyIndex = header.indexOf(5);
            if (keyIndex > 0) {
              header.splice(keyIndex, 1);
              $("#" + idPrefix + tableId)
                .find(td["name='" + colNamePrefix + key + "'"])
                .remove();
            }
          }
        },
      };
      rowFn = {
        click: function (tableId, index) {
          var curr = tableData["part" + tableId];
          if (curr) {
            var call = curr.params.call;
            common.callFu(window.ui, call.click.fn, call.click.params, curr);
          }
        },
        check: function (tableId, status, index) {
          if (status == undefined) status = "auto";
          var tbodyDom = $("#" + idPrefix + tableId).children("tbody");
          if (index) {
            var willCheck = tbodyDom.find("input[name='" + checkIdPrefix + tableId + "']:eq('" + index + "')");
            if (status == true) willCheck.prop("checked", true);
            else if (status == false) willCheck.prop("checked", false);
            else willCheck.prop("checked", willCheck.prop("checked") == true ? false : true);
          } else {
            tbodyDom.find("input[name='" + checkIdPrefix + tableId + "']").each(function () {
              if (status == true) $(this).prop("checked", true);
              else if (status == false) $(this).prop("checked", false);
              else $(this).prop("checked", $(this).prop("checked") == true ? false : true);
            });
          }
        },
        add: function (tableId, data, index) {
          var curr = tableData["part" + tableId];
          if (curr) {
            if (!index || index < 0) index = 0;
            var header = curr.params.header;
            var showIndex = curr.params.showIndex;
            var showCheck = curr.params.showCheck;
            var trConfig = buildBody(tableId, header, data, showIndex, showCheck);
            var indexTr = $("#" + idPrefix + tableId)
              .children("tbody")
              .children("tr:eq(" + index + ")");
            indexTr.before(buildUtil.buildModule([trConfig]));
            curr.params.body.splice(index, 0, data);
          }
        },
        remove: function (tableId, index) {
          var curr = tableData["part" + tableId];
          if (curr) {
            var data = curr.params.body;
            delete data["row" + rowId];
            $("#" + idPrefix + tableId)
              .children("tbody")
              .children("tr:eq(" + index + ")")
              .remove();
          }
        },
        getData: function (tableId, index) {
          var curr = tableData["part" + tableId];
          if (curr) {
            var data = curr.params.body;
            return data[index];
          }
        },
        setData: function (tableId, data, index) {
          var curr = tableData["part" + tableId];
          if (curr) {
            var header = curr.params.header;
            var showIndex = curr.params.showIndex;
            var showCheck = curr.params.showCheck;
            if (!index) {
              curr.params.body = data;
              var trConfig = [];
              for (var dataIndex = 0; dataIndex < data.length; dataIndex++) {
                var currRowData = data[dataIndex];
                currRowData.$index = dataIndex + 1 + "";
                var dataTr = buildBody(tableId, header, currRowData, showIndex, showCheck);
                trConfig.push(dataTr);
              }
              var newDataHtml = buildUtil.buildModule(trConfig);
              $("#" + idPrefix + tableId)
                .children("tbody")
                .html(newDataHtml);
            } else {
              data.$index = index + 1 + "";
              var newDataHtml = buildBody(tableId, header, data, showIndex, showCheck);
              $("#" + idPrefix + tableId)
                .children("tbody")
                .children("tr:eq(" + index + ")")
                .html(newDataHtml);
              curr.params.body.splice(index, 0, data);
            }
          }
        },
      };

      function buildBody(id, header, data, showIndex, showCheck) {
        var dataTr = common.deepCopy(tableElementConfig.tr);
        if (showCheck == true) {
          var colTd = common.deepCopy(tableElementConfig.td);
          var checkElement = common.deepCopy(tableElementConfig.check);
          checkElement.properties.name = checkIdPrefix + id;
          colTd.children = [checkElement];
          dataTr.children.push(colTd);
        }
        if (showIndex == true) {
          var colTd = common.deepCopy(tableElementConfig.td);
          colTd.text = data.$index;
          dataTr.children.push(colTd);
        }
        for (var headerKey in header) {
          if (header.hasOwnProperty(headerKey)) {
            var colTd = common.deepCopy(tableElementConfig.td);
            var dataKey = headerKey;
            colTd.properties.name = colNamePrefix + dataKey;
            colTd.text = data[dataKey];
            dataTr.children.push(colTd);
          }
        }
        return dataTr;
      }

      function build(params) {
        var id = params.id;
        var showIndex = params.showIndex == true ? true : false;
        var showCheck = params.showCheck == true ? true : false;
        var header = params.header ? params.header : {};
        var body = params.body;
        if (common.isEmpty(id)) params.id = index = id = index + 1;
        if (!params.call) params.call = {};
        if (!params.call.click) params.call.click = {};

        tableElement = common.deepCopy(tableConfig);
        tableElement.properties.id = idPrefix + id;

        var headerTr = common.deepCopy(tableElementConfig.tr);
        if (showCheck == true) {
          var colTh = common.deepCopy(tableElementConfig.th);
          var checkElement = common.deepCopy(tableElementConfig.check);
          checkElement.properties.name = checkAllIdPrefix + id;
          checkElement.properties.onclick = "ui.parts.table.row.check('" + id + "', this.checked)";
          colTh.children = [checkElement];
          headerTr.children.push(colTh);
        }
        if (showIndex == true) {
          var colTh = common.deepCopy(tableElementConfig.th);
          colTh.text = "INDEX";
          headerTr.children.push(colTh);
        }
        for (var headerKey in header) {
          if (header.hasOwnProperty(headerKey)) {
            var colTh = common.deepCopy(tableElementConfig.th);
            var colText = header[headerKey];
            colTh.properties.name = colNamePrefix + colText;
            colTh.text = colText;
            headerTr.children.push(colTh);
          }
        }
        tableElement.children[0].children.push(headerTr);

        for (var rowIndex = 0; rowIndex < body.length; rowIndex++) {
          var data = body[rowIndex];
          data.$index = rowIndex + 1 + "";
          var dataTr = buildBody(id, header, data, showIndex, showCheck);
          tableElement.children[1].children.push(dataTr);
        }

        return (tableData["part" + id] = {
          id: id,
          params: params,
          config: tableElement,
        });
      }

      return {
        build: build,
        getConfig: tableFn.getConfig,
        col: {
          add: colFn.add,
          remove: colFn.remove,
        },
        row: {
          click: rowFn.click,
          check: rowFn.check,
          add: rowFn.add,
          remove: rowFn.remove,
          getData: rowFn.getData,
          setData: rowFn.setData,
        },
      };
    }

    // tabs module ------------------------------------------------------------------------
    function tabsObj() {
      var index = 0;
      var elementIndex = 0;
      var idPrefix = setting.tabs.idPrefix;
      var tabNamePrefix = setting.tabs.tabNamePrefix;

      var tabsData = {};
      var tabsConfig = {
        name: "div",
        properties: { class: "p-tabs" },
        children: [
          { name: "ul", properties: { class: "p-tabs-title" }, children: [] },
          { name: "div", properties: { class: "p-tabs-body" }, children: [] },
        ],
      };
      var tabConfig = {
        title: {
          name: "li",
          properties: {},
          children: [
            { name: "span", properties: { class: "p-left" }, text: "" },
            { name: "span", properties: { class: "p-right" }, text: "x" },
          ],
        },
        body: { name: "div", properties: { class: "p-tabs-tab" } },
      };

      tabsFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, tabsData);
        },
      };
      tabFn = {
        checked: function (id, elementId) {
          var currTabs = tabsData["part" + id];
          if (currTabs) {
            if (!elementId)
              for (var key in currTabs.tabs)
                if (currTabs.tabs.hasOwnProperty(key)) {
                  elementId = currTabs.tabs[key].id;
                  break;
                }
            var currTab = currTabs.tabs["tab" + elementId];
            if (currTab) {
              var titleDom = $("#" + idPrefix + id)
                .children("ul")
                .children("li[name='" + tabNamePrefix + elementId + "']");
              titleDom.addClass("p-checked");
              titleDom.siblings("li").removeClass("p-checked");
              var bodyDom = $("#" + idPrefix + id)
                .children("div")
                .children("div[name='" + tabNamePrefix + elementId + "']");
              bodyDom.addClass("p-checked");
              bodyDom.siblings("div").removeClass("p-checked");
              common.callFu(window.ui, currTab.params.call.checked.fn, currTab.params.call.checked.params, currTab);
            }
          }
        },
        open: function (id, params) {
          var currTabs = tabsData["part" + id];
          if (currTabs) {
            var currTab = currTabs.tabs["tab" + elementId];
            if (currTab) {
              var tabObj = buildTab(id, params);
              $("#" + idPrefix + id)
                .children("ul")
                .append(buildUtil.buildModule(tabObj.config.title));
              $("#" + idPrefix + id)
                .children("div")
                .append(buildUtil.buildModule(tabObj.config.body));
              currTabs.tabs["tab" + tabObj.id] = tabObj;
              tabFn.checked(id, tabObj.id);
              common.callFu(window.ui, currTab.params.call.open.fn, currTab.params.call.open.params, currTab);
            }
          }
        },
        close: function (id, elementId) {
          var currTabs = tabsData["part" + id];
          if (currTabs) {
            var currTab = currTabs.tabs["tab" + elementId];
            if (currTab) {
              var titleDom = $("#" + idPrefix + id)
                .children("ul")
                .children("li[name='" + tabNamePrefix + elementId + "']");
              var bodyDom = $("#" + idPrefix + id)
                .children("div")
                .children("div[name='" + tabNamePrefix + elementId + "']");
              titleDom.remove();
              bodyDom.remove();
              delete currTabs.tabs["tab" + elementId];

              var checkedLi = $("#" + idPrefix + id)
                .children("ul")
                .children(".p-checked");
              if (checkedLi.length == 0) this.checked(id);
              common.callFu(window.ui, currTab.params.call.close.fn, currTab.params.call.close.params, currTab);

              var event = window.event || arguments.callee.caller.arguments[0];
              event.stopPropagation();
            }
          }
        },
      };

      function buildTab(tabsId, params) {
        var id = params.id;
        var isDefault = params.default;
        var title = params.title;
        var body = params.body;
        var isClose = params.close == false ? false : true;
        if (common.isEmpty(id)) params.id = elementIndex = id = elementIndex + 1;

        if (!params.call) params.call = {};
        if (!params.call.checked) params.call.checked = {};
        if (!params.call.open) params.call.open = {};
        if (!params.call.close) params.call.close = {};

        var titleElement = common.deepCopy(tabConfig.title);
        if (title.style) titleElement.properties.style = title.style;
        titleElement.properties.onclick = "ui.parts.tabs.tab.checked('" + tabsId + "','" + id + "')";
        titleElement.properties.name = tabNamePrefix + id;
        titleElement.children[0].text = title.text ? title.text : "";
        titleElement.children[1].properties.onclick = "ui.parts.tabs.tab.close('" + tabsId + "','" + id + "')";
        if (isDefault) titleElement.properties.class = "p-checked";
        if (!isClose) titleElement.children.splice(1, 1); // 删除关闭按钮

        var bodyElement = common.deepCopy(tabConfig.body);
        if (isDefault) bodyElement.properties.class += " p-checked";
        if (body.style) bodyElement.properties.style = body.style;
        bodyElement.properties.name = tabNamePrefix + id;
        if (common.is.Array(body.content)) bodyElement.children = body.content;
        else if (common.is.String(body.content)) bodyElement.text = body.content;
        else if (common.is.Object(body.content)) bodyElement.children = [body.content];
        else bodyElement.text = "";

        return {
          id: id,
          params: params,
          config: { title: titleElement, body: bodyElement },
        };
      }

      function build(params) {
        var id = params.id;
        var elements = params.elements;
        if (common.isEmpty(id)) params.id = index = id = index + 1;

        var isHasDefault = false;
        tabsElement = common.deepCopy(tabsConfig);
        tabsElement.properties.id = idPrefix + id;

        var tabs = {};
        for (var index = 0; index < elements.length; index++) {
          var element = elements[index];
          // 只允许一个被选择
          if (element.default == true && isHasDefault == true) element.default = false;
          if (element.default == true) isHasDefault = true;

          var tabObj = buildTab(id, element);
          tabsElement.children[0].children.push(tabObj.config.title);
          tabsElement.children[1].children.push(tabObj.config.body);
          tabs["tab" + tabObj.id] = tabObj;
        }

        return (tabsData["part" + id] = {
          id: id,
          params: params,
          config: tabsElement,
          tabs: tabs,
        });
      }

      return {
        build: build,
        getConfig: tabsFn.getConfig,
        tab: {
          checked: tabFn.checked,
          open: tabFn.open,
          close: tabFn.close,
        },
      };
    }

    // page module ------------------------------------------------------------------------
    function pageObj() {
      var index = 0;
      var idPrefix = setting.page.idPrefix;
      var lastText = setting.page.lastText;
      var nextText = setting.page.nextText;
      var firstlyText = setting.page.firstlyText;
      var finallyText = setting.page.finallyText;
      var toText = setting.page.toText;

      var pageData = {};
      var pageConfig = { name: "div", properties: { class: "p-page" }, children: [] };
      var pageChildConfig = {
        page: { name: "span", properties: {}, text: "" },
        next: { name: "span", properties: {}, text: "" },
        last: { name: "span", properties: {}, text: "" },
        input: {
          name: "span",
          properties: {},
          children: [
            { name: "span", properties: {}, text: "" },
            { name: "input", properties: { type: "text" } },
          ],
        },
        firstly: { name: "span", properties: {}, text: "" },
        finally: { name: "span", properties: {}, text: "" },
      };

      pageFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, pageData);
        },
        getCurr: function (id) {
          var currPage = pageData["part" + id];
          return partCommon.getCurr(id, pageData);
        },
        click: function (id, page) {
          var currPage = pageData["part" + id];
          if (currPage) {
            var params = currPage.params;
            params.curr = page ? parseInt(page) : 1;
            if (page.isFlush != false) {
              var childrenHtml = buildUtil.buildModule(getPageChildren(id, params));
              $("#" + idPrefix + id).html(childrenHtml);
            }
            common.callFu(window.ui, params.call.click.fn, params.call.click.params, params);
          }
        },
        flush: function (id) {
          var currPage = pageData["part" + id];
          if (currPage) {
            var childrenHtml = buildUtil.buildModule(getPageChildren(id, currPage.params));
            $("#" + idPrefix + id).html(childrenHtml);
          }
        },
      };

      function getPageChildren(id, params) {
        var pageChildren = [];
        var curr = params.curr ? parseInt(params.curr) : 1;
        var maxPage = params.maxPage;
        var maxCount = params.maxCount;
        var showSize = params.showSize ? parseInt(params.showSize) : 10;
        var flushType = params.flushType == "manual";
        var isLast = (params.last = params.last == false ? false : true);
        var isNext = (params.next = params.next == false ? false : true);
        var isInput = (params.input = params.input == false ? false : true);
        var isFirstly = (params.firstly = params.firstly == false ? false : true);
        var isFinally = (params.finally = params.finally == false ? false : true);

        if (curr < 1) curr = 1;
        if (!maxPage || parseInt(maxPage) <= 0) {
          if (maxCount) {
            var maxCount = parseInt(maxCount);
            if (maxCount % showSize == 0) maxPage = maxCount / showSize;
            else maxPage = maxCount / showSize + 1;
          } else maxPage = 1;
        }

        if (isFirstly) {
          var firstlyElement = common.deepCopy(pageChildConfig.firstly);
          firstlyElement.text = firstlyText;
          if (curr == 1) firstlyElement.properties.class = "p-forbid";
          else firstlyElement.properties.onclick = "ui.parts.page.click('" + id + "', '1')";
          pageChildren.push(firstlyElement);
        }

        if (isLast) {
          var lastPage = curr == 1 ? 1 : curr - 1;
          var lastElement = common.deepCopy(pageChildConfig.last);
          lastElement.text = lastText;
          if (curr == 1) lastElement.properties.class = "p-forbid";
          else lastElement.properties.onclick = "ui.parts.page.click('" + id + "', '" + lastPage + "')";
          pageChildren.push(lastElement);
        }

        if (showSize > maxPage) showSize = maxPage;
        var isOdd = showSize % 2 == 1 ? true : false;
        var middleShowSize = parseInt(showSize / 2);
        var pageNum, maxPageSizeNum;
        if (curr <= middleShowSize) {
          pageNum = 1;
          maxPageSizeNum = showSize;
        } else if (curr > maxPage - middleShowSize) {
          pageNum = maxPage - showSize + 1;
          maxPageSizeNum = maxPage;
        } else {
          pageNum = curr - middleShowSize;
          if (!isOdd) pageNum = pageNum + 1;
          maxPageSizeNum = curr + middleShowSize;
        }
        for (; pageNum <= maxPageSizeNum; pageNum++) {
          var pageElement = common.deepCopy(pageChildConfig.page);
          if (curr == pageNum) pageElement.properties.class = "p-checked";
          else pageElement.properties.onclick = "ui.parts.page.click('" + id + "', '" + pageNum + "')";
          pageElement.text = pageNum;
          pageChildren.push(pageElement);
        }

        if (isInput) {
          var inputElement = common.deepCopy(pageChildConfig.input);
          inputElement.children[0].text = toText + " ";
          inputElement.children[1].properties.onblur = "ui.parts.page.click('" + id + "', this.value)";
          pageChildren.push(inputElement);
        }

        if (isNext) {
          var nextPage = curr >= maxPage ? maxPage : curr + 1;
          var nextElement = common.deepCopy(pageChildConfig.next);
          nextElement.text = nextText;
          if (curr == maxPage) nextElement.properties.class = "p-forbid";
          else nextElement.properties.onclick = "ui.parts.page.click('" + id + "', '" + nextPage + "')";
          pageChildren.push(nextElement);
        }

        if (isFinally) {
          var finallyElement = common.deepCopy(pageChildConfig.finally);
          finallyElement.text = finallyText;
          if (curr == maxPage) finallyElement.properties.class = "p-forbid";
          else finallyElement.properties.onclick = "ui.parts.page.click('" + id + "', '" + maxPage + "')";
          pageChildren.push(finallyElement);
        }

        return pageChildren;
      }

      function build(params) {
        var id = params.id;
        if (common.isEmpty(id)) params.id = index = id = index + 1;
        if (!params.call) params.call = {};
        if (!params.call.click) params.call.click = {};

        pageElement = common.deepCopy(pageConfig);
        pageElement.properties.id = idPrefix + id;
        pageElement.children = getPageChildren(id, params);

        return (pageData["part" + id] = {
          id: id,
          params: params,
          config: pageElement,
        });
      }

      return {
        build: build,
        getConfig: pageFn.getConfig,
        getCurr: pageFn.getCurr,
        flush: pageFn.flush,
        click: pageFn.click,
      };
    }

    // nav module ------------------------------------------------------------------------
    function navObj() {
      var index = 0,
        listIndex = 0,
        groupIndex = 0,
        nodeIndex = 0;
      var idPrefix = setting.nav.idPrefix;
      var listIdPrefix = setting.nav.listIdPrefix;
      var groupIdPrefix = setting.nav.groupIdPrefix;
      var nodeIdPrefix = setting.nav.nodeIdPrefix;

      var navData = {};
      var navConfig = {
        name: "div",
        properties: {},
        children: [
          { name: "div", properties: { class: "p-nav-main" }, children: [] },
          { name: "div", properties: { class: "p-nav-children" }, children: [] },
        ],
      };
      var navListConfig = {
        name: "div",
        properties: { class: "p-nav-list" },
        children: [],
      };
      var navGroupConfig = {
        name: "div",
        properties: { class: "p-nav-group" },
        children: [
          { name: "div", properties: { class: "p-title" }, text: "" },
          { name: "div", properties: { class: "p-children" }, children: [] },
        ],
      };
      var navNodeConfig = { name: "span", properties: { class: "p-nav-node" }, text: "" };

      navFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, navData);
        },
        checked: function (id, currListId, nodeId) {
          var currNav = navData["part" + id];
          if (currNav) {
            var currList = currNav.list["list" + currListId];
            if (currList) {
              var currNode = currList.nodes["node" + nodeId];
              if (currNode) {
                if (currNode.to && currNav.list["list" + currNode.to]) {
                  $("#" + listIdPrefix + id + "_" + currNode.to).css("display", "block");
                  $("#" + listIdPrefix + id + "_" + currNode.to)
                    .siblings()
                    .css("display", "none");
                }
                if (currNode) common.callFu(window.ui, currNode.fn, currNode.params, currNode);
              }
            }
          }
        },
        unCheckedAll: function (id) {
          var currNav = navData["part" + id];
          if (currNav)
            $("#" + idPrefix + id + " .p-nav-children")
              .children()
              .css("display", "none");
        },
      };

      function buildElement(id, element) {
        var listId = element.id;
        var listGread = element.gread;
        var groups = element.groups;
        if (common.isEmpty(listIndex)) element.id = listId = listIndex = listIndex + 1;

        var navListElement = common.deepCopy(navListConfig);
        navListElement.properties.id = listIdPrefix + id + "_" + listId;
        navListElement.properties.gread = listGread;

        // init groups params
        if (!groups) {
          groups = [];
          nodes = element.nodes;
          for (var nodeI = 0; nodeI < nodes.length; nodeI++) {
            var node = nodes[nodeI];
            groups.push(node);
          }
        }

        var nodesObj = {};
        for (var groupI = 0; groupI < groups.length; groupI++) {
          var group = groups[groupI];

          var groupId = group.id;
          var groupTitle = group.title;
          var nodes = group.nodes;
          if (common.isEmpty(groupId)) groupId = groupIndex = groupIndex + 1;

          // init nodes params
          if (!nodes) nodes = [group];
          else if (!common.is.Array(nodes)) nodes = [nodes];

          var navGroupElement = common.deepCopy(navGroupConfig);
          navGroupElement.properties.name = groupIdPrefix + groupId;
          if (groupTitle) navGroupElement.children[0].text = groupTitle;
          else navGroupElement.children.splice(0, 1);

          var groupChildren = navGroupElement.children[navGroupElement.children.length - 1];

          for (var nodeI = 0; nodeI < nodes.length; nodeI++) {
            var node = nodes[nodeI];
            var nodeId = node.id;
            var nodeText = node.text;
            if (common.isEmpty(nodeId)) nodeId = nodeIndex = nodeIndex + 1;

            var navNodeElement = common.deepCopy(navNodeConfig);
            navNodeElement.properties.name = nodeIdPrefix + nodeId;
            navNodeElement.properties.onclick = "ui.parts.nav.checked('" + id + "','" + listId + "','" + nodeId + "')";
            navNodeElement.text = nodeText;

            groupChildren.children.push(navNodeElement);
            nodesObj["node" + nodeId] = node;
          }
          navListElement.children.push(navGroupElement);
        }

        return {
          id: listId,
          params: element,
          config: navListElement,
          nodes: nodesObj,
        };
      }

      function build(params) {
        var id = params.id;
        var main = params.main;
        var list = params.list;
        if (common.isEmpty(id)) params.id = index = id = index + 1;

        if (common.is.Array(main)) main = { id: "main", gread: 0, groups: main };
        else if (!main.gread) main.gread = 0;
        navElement = common.deepCopy(navConfig);
        navElement.properties.id = idPrefix + id;
        navElement.properties.onmouseleave = "ui.parts.nav.unCheckedAll('" + id + "')";

        var listObj = {};
        var navMainElement = navElement.children[0];
        var navMainObj = buildElement(id, main);
        navMainElement.children.push(navMainObj.config);
        listObj["list" + navMainObj.id] = navMainObj;

        var navChildrenElement = navElement.children[1];
        for (var i = 0; i < list.length; i++) {
          var listItem = list[i];
          var navChildObj = buildElement(id, listItem);
          navChildrenElement.children.push(navChildObj.config);
          listObj["list" + navChildObj.id] = navChildObj;
        }

        return (navData["part" + id] = {
          id: id,
          params: params,
          config: navElement,
          list: listObj,
        });
      }

      return {
        build: build,
        getConfig: navFn.getConfig,
        checked: navFn.checked,
        unCheckedAll: navFn.unCheckedAll,
      };
    }

    // content module ------------------------------------------------------------------------
    function contentObj() {
      var index = 0;
      var idPrefix = setting.content.idPrefix;
      var operationNamePrefix = setting.content.operationNamePrefix;

      var contentData = {};
      var contentConfig = { name: "div", properties: { class: "p-content" }, children: [] };
      var contentChildrenConfig = {
        img: { name: "div", properties: { class: "p-content-img" }, children: [] },
        title: { name: "div", properties: { class: "p-content-title" }, children: [] },
        text: { name: "div", properties: { class: "p-content-text" }, children: [] },
        operations: { name: "div", properties: { class: "p-content-operations" }, children: [] },
        spanTab: { name: "span", properties: {}, children: [] },
        imgTab: { name: "img", properties: { src: "", width: "100%", height: "100%" } },
      };

      var contentFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, contentData);
        },
      };
      var titleFn = {
        click: function (id) {
          var currcontent = contentData["part" + id];
          if (currcontent) {
            var title = currcontent.params.title;
            common.callFu(window.ui, title.fn, title.params, currcontent.params);
          }
        },
      };
      var operationFn = {
        click: function (id, key) {
          var currcontent = contentData["part" + id];
          if (currcontent && key) {
            var elements = currcontent.params.operations.elements;
            for (var operationIndex = 0; operationIndex < elements.length; operationIndex++) {
              var operation = elements[operationIndex];
              if (operation.key == key) {
                common.callFu(window.ui, operation.fn, operation.params, operation);
                break;
              }
            }
          }
        },
      };

      function build(params) {
        var id = params.id;
        var img = params.img;
        var title = params.title;
        var text = params.text;
        var operations = params.operations;
        var style = params.style;
        if (common.isEmpty(id)) params.id = index = id = index + 1;

        var contentElement = common.deepCopy(contentConfig);
        contentElement.properties.id = idPrefix + id;
        if (style) contentElement.properties.style = style;

        if (img) {
          var imgUrl = img.url ? img.url : "";
          var imgStyle = img.style;

          var imgElement = common.deepCopy(contentChildrenConfig.img);
          imgElement.properties.name = "content_img";
          if (imgStyle) imgElement.properties.style = imgStyle;

          var imgTabElement = common.deepCopy(contentChildrenConfig.imgTab);
          imgTabElement.properties.src = imgUrl;
          imgElement.children.push(imgTabElement);

          contentElement.children.push(imgElement);
        }
        if (title) {
          var titleText = title.text ? title.text : "";
          var titleStyle = title.style;

          var titleElement = common.deepCopy(contentChildrenConfig.title);
          titleElement.properties.name = "content_title";
          titleElement.properties.onclick = "ui.parts.content.title.click('" + id + "')";
          if (titleStyle) titleElement.properties.style = titleStyle;
          titleElement.text = titleText;

          contentElement.children.push(titleElement);
        }
        if (operations) {
          var operationsStyle = operations.style;
          var operationsElements = operations.elements ? operations.elements : [];

          var operationsElement = common.deepCopy(contentChildrenConfig.operations);
          operationsElement.properties.name = "content_operations";
          if (operationsStyle) operationsElement.properties.style = operationsStyle;

          for (var operationIndex = 0; operationIndex < operationsElements.length; operationIndex++) {
            var operation = operationsElements[operationIndex];
            var operationKey = operation.key;
            var operationPosition = operation.position;
            var operationText = operation.text;
            var operationImg = operation.img;

            var operationElement = common.deepCopy(contentChildrenConfig.spanTab);
            operationElement.properties.name = operationNamePrefix + operationKey;
            operationElement.properties.onclick = "ui.parts.content.operation.click('" + id + "','" + operationKey + "')";
            if (operationPosition == "right") operationElement.properties.class = "p-operation p-right";
            else operationElement.properties.class = "p-operation p-left";
            if (operationImg) {
              var operationImgElement = common.deepCopy(contentChildrenConfig.imgTab);
              operationImgElement.properties.src = operationImg;
              operationElement.children.push(operationImgElement);
            }
            if (operationText) {
              var operationSpanElement = common.deepCopy(contentChildrenConfig.spanTab);
              operationSpanElement.text = operationText;
              operationElement.children.push(operationSpanElement);
            }
            operationsElement.children.push(operationElement);
          }
          contentElement.children.push(operationsElement);
        }
        if (text) {
          var textStyle = text.style;
          var textText = text.text;
          var textElements = text.elements;

          var textElement = common.deepCopy(contentChildrenConfig.text);
          textElement.properties.name = "content_text";
          if (textStyle) textElement.properties.style = textStyle;

          if (!common.isEmpty(textText)) textElements = [textText];
          for (var textIndex = 0; textIndex < textElements.length; textIndex++) {
            var textSpanElement = common.deepCopy(contentChildrenConfig.spanTab);
            textSpanElement.text = textElements[textIndex];
            textElement.children.push(textSpanElement);
          }
          contentElement.children.push(textElement);
        }

        return (contentData["part" + id] = {
          id: id,
          params: params,
          config: contentElement,
        });
      }

      return {
        build: build,
        getConfig: contentFn.getConfig,
        title: {
          click: titleFn.click,
        },
        operation: {
          click: operationFn.click,
        },
      };
    }

    // list module ------------------------------------------------------------------------
    function listObj() {
      var index = 0;
      var itemIndex = 0;
      var idPrefix = setting.list.idPrefix;
      var itemNamePrefix = setting.list.itemNamePrefix;

      var listData = {};
      var listConfig = { name: "div", properties: { class: "p-list" }, children: [] };
      var listItemConfig = { name: "div", properties: { class: "p-list-item" } };

      var listFn = {
        getConfig: function (id) {
          return partCommon.getConfig(id, listData);
        },
      };
      var itemFn = {
        click: function (id, key) {
          var currList = listData["part" + id];
          if (currList) {
            var items = currList.items;
            var item = items["item" + key];
            var call = item.call;
            if (item) common.callFu(window.ui, call.click.fn, call.click.params, item);
          }
        },
      };

      function build(params) {
        var id = params.id;
        var items = params.items ? params.items : [];
        var isClick = params.click == true ? true : false;
        var style = params.style;
        var publicItemStyle = params.itemStyle;
        if (common.isEmpty(id)) params.id = index = id = index + 1;
        if (!params.call) params.call = {};
        if (!params.call.click) params.call.click = {};

        var listElement = common.deepCopy(listConfig);
        listElement.properties.id = idPrefix + id;
        if (style) listElement.properties.style = style;

        var itemsData = {};
        for (var itemI = 0; itemI < items.length; itemI++) {
          var item = items[itemI];

          var itemKey = item.key;
          var itemContent = item.content;
          var itemIsClick = (item.click ? item.click : isClick) == true ? true : false;
          var itemStyle = item.style ? item.style : publicItemStyle;
          if (common.isEmpty(itemKey)) item.key = itemIndex = itemKey = itemIndex + 1;
          if (!item.call) item.call = params.call;
          if (!item.call.click) item.call.click = params.call.click;

          itemsData["item" + itemKey] = item;
          var listItemElement = common.deepCopy(listItemConfig);
          listItemElement.properties.name = itemNamePrefix + itemKey;
          if (itemStyle) listItemElement.properties.style = itemStyle;
          if (itemIsClick == true) listItemElement.properties.onclick = "ui.parts.list.item.click('" + id + "','" + itemKey + "')";
          if (itemContent) {
            if (common.is.String(itemContent)) listItemElement.text = itemContent;
            if (common.is.Array(itemContent)) listItemElement.children = itemContent;
            if (common.is.Object(itemContent)) listItemElement.children = [itemContent];
          }
          listElement.children.push(listItemElement);
        }

        return (listData["part" + id] = {
          id: id,
          params: params,
          config: listElement,
          items: itemsData,
        });
      }

      return {
        build: build,
        getConfig: listFn.getConfig,
        item: {
          click: itemFn.click,
        },
      };
    }

    return {
      window: new windowObj(),
      form: new formObj(),
      buttons: new buttonsObj(),
      grid: new gridObj(),
      drawBoard: new drawBoardObj(),
      svg: new svgObj(),
      lineMap: new lineMapObj(),
      custom: new customObj(),
      table: new tableObj(),
      tabs: new tabsObj(),
      page: new pageObj(),
      nav: new navObj(),
      content: new contentObj(),
      list: new listObj(),
    };
  },

  // modules obj -----------------------------------------------------------------------------------
  function (buildUtil, common, parts, config) {
    var modules;
    var setting = config.module;
    var moduleCommon = {
      ajax: function (load, page, currSetting) {
        if (!load.type) load.type = "POST";
        if (!load.params) load.params = {};
        if (page) {
          var paramsNameSetting = currSetting.paramsName;
          load.params[paramsNameSetting.currPage] = page.curr;
          load.params[paramsNameSetting.pageShowSize] = page.showSize;
        }
        console.log("Module load url: ", load.url, "; data:", load.params);
        $.ajax({
          url: load.url,
          type: load.type,
          data: load.params,
          success: function (result) {
            var resultSetting = currSetting.resultPath;
            if (common.getByPath(result, resultSetting.success) == true) {
              var msg = common.getByPath(result, resultSetting.msg);
              var data = common.getByPath(result, page ? resultSetting.listData : resultSetting.data);
              if (msg) alert(msg);
              if (page && page != null) {
                var resultCurrPage = common.getByPath(result, resultSetting.currPage);
                var resultMaxPage = common.getByPath(result, resultSetting.maxPage);
                var resultMaxCount = common.getByPath(result, resultSetting.maxCount);
                if (resultCurrPage) page.curr = parseInt(resultCurrPage);
                if (resultMaxPage || resultMaxCount) page.maxPage = resultMaxPage;
                if (resultMaxCount) page.maxCount = resultMaxCount;
                parts.page.flush(page.id);
              }
              if (data && load.success) common.callFu(load, "success", data, load.bind);
            } else {
              var errMsg = common.getByPath(result, resultSetting.errMsg);
              if (errMsg) alert(errMsg);
            }
          },
        });
      },
      replace: function (temp, data) {
        var paramReg = "@{(.*?)}";
        return common.replace(temp, data, paramReg);
      },
    };

    var listModule = function () {
      var moduleSetting = setting.list;
      var listModuleObj = {};
      var moduleConfig = {
        name: "div",
        properties: {},
        children: [
          { name: "div", properties: { part: "list" } },
          { name: "div", properties: { part: "page" } },
        ],
      };

      var moduleFn = {
        getConfig: function (moduleId) {
          var moduleObj = contentModuleObj["module" + moduleId];
          if (moduleObj) return moduleObj.params;
        },
      };
      var listFn = {
        build: function (moduleId, params) {
          var items = (params.items = []);
          var contents = params.contents ? params.contents : [];
          for (var contentIndex = 0; contentIndex < contents.length; contentIndex++) {
            var content = contents[contentIndex];
            var contentObj = parts.content.build(content);
            items.push({ content: contentObj.config });
          }
          var listObj = parts.list.build(params);
          return [listObj.config];
        },
        loadData: function (params) {
          var moduleId = params.moduleId;
          var moduleObj = params.module;
          if (!moduleObj) moduleObj = listModuleObj["module" + moduleId];
          if (moduleObj && moduleObj.params.list) {
            var loadParams = moduleObj.params.list.load;
            if (loadParams && loadParams.url && loadParams.temp) {
              if (!loadParams.success)
                loadParams.success = function (data) {
                  var listElement = [];
                  for (var index = 0; index < data.length; index++) {
                    var item = data[index];
                    var itemConfig = moduleCommon.replace(loadParams.temp, item);
                    var itemObj = parts.content.build(itemConfig);
                    listElement.push(itemObj.config);
                  }
                  var listHtml = ui.build.buildModule(listElement);
                  $("#" + moduleSetting.idPrefix + moduleId)
                    .find("div[part='list']")
                    .html(listHtml);
                };
              moduleCommon.ajax(loadParams, moduleObj.params.page, moduleSetting);
            }
          }
        },
      };
      var pageFn = {
        build: function (moduleId, params) {
          params.moduleId = moduleId;
          params.call = {
            click: {
              fn: pageFn.check,
              params: { moduleId: moduleId },
            },
          };
          var pageObj = parts.page.build(params);
          return [pageObj.config];
        },
        check: function (params) {
          listFn.loadData({ moduleId: params.moduleId });
        },
      };

      function build(config) {
        var id = config.id;
        var listConfig = config.list;
        var pageConfig = config.page;

        var listModuleElement = common.deepCopy(moduleConfig);
        listModuleElement.properties.id = moduleSetting.idPrefix + id;

        var listObj = listFn.build(id, listConfig);
        if (listObj) listModuleElement.children[0].children = listObj;
        var pageObj = pageFn.build(id, pageConfig);
        if (pageObj) listModuleElement.children[1].children = pageObj;

        return (listModuleObj["module" + id] = {
          id: id,
          params: config,
          config: listModuleElement,
        });
      }

      return {
        build: build,
        getConfig: moduleFn.getConfig,
        list: listFn,
        page: pageFn,
        setting: moduleSetting,
      };
    };

    var contentModule = function () {
      var moduleSetting = setting.content;
      var contentModuleObj = {};
      var moduleConfig = {
        name: "div",
        properties: {},
        children: [
          { name: "div", properties: { part: "content" } },
          { name: "div", properties: { part: "page" } },
        ],
      };

      var moduleFn = {
        getConfig: function (moduleId) {
          var moduleObj = contentModuleObj["module" + moduleId];
          if (moduleObj) return moduleObj.params;
        },
      };
      var contentFn = {
        build: function (moduleId, params) {
          if (params) {
            params.id = moduleId;
            var contentObj = parts.content.build(params);
            return [contentObj.config];
          }
        },
        loadData: function (params) {
          var moduleId = params.moduleId;
          var moduleObj = params.module;
          if (!moduleObj) moduleObj = contentModuleObj["module" + moduleId];
          if (moduleObj && moduleObj.params.load) {
            var loadParams = moduleObj.params.load;
            if (loadParams && loadParams.url && loadParams.temp) {
              if (!loadParams.success)
                loadParams.success = function (data) {
                  var contentConfig = moduleCommon.replace(loadParams.temp, data);
                  var contentObj = parts.content.build(contentConfig);
                  var contentHtml = ui.build.buildModule([contentObj.config]);
                  $("#" + moduleSetting.idPrefix + moduleId)
                    .find("div[part='content']")
                    .html(contentHtml);
                };
              moduleCommon.ajax(loadParams, moduleObj.params.page, moduleSetting);
            }
          }
        },
      };
      var pageFn = {
        build: function (moduleId, params) {
          params.moduleId = moduleId;
          params.call = {
            click: {
              fn: pageFn.check,
              params: moduleId,
            },
          };
          var pageObj = parts.page.build(params);
          return [pageObj.config];
        },
        check: function (params) {
          contentFn.loadData({ moduleId: params.moduleId });
        },
      };

      function build(config) {
        var id = config.id;
        var contentConfig = config.content;
        var pageConfig = config.page;

        var contentModuleElement = common.deepCopy(moduleConfig);
        contentModuleElement.properties.id = moduleSetting.idPrefix + id;

        var contentObj = contentFn.build(id, contentConfig);
        if (contentObj) contentModuleElement.children[0].children = contentObj;
        var pageObj = pageFn.build(id, pageConfig);
        if (pageObj) contentModuleElement.children[1].children = pageObj;

        return (contentModuleObj["module" + id] = {
          id: id,
          params: config,
          config: contentModuleElement,
        });
      }

      return {
        build: build,
        getConfig: moduleFn.getConfig,
        content: contentFn,
        page: pageFn,
        setting: moduleSetting,
      };
    };

    var formModule = function () {
      var moduleSetting = setting.form;
      var formModuleObj = {};
      var moduleConfig = {
        name: "div",
        properties: {},
        children: [
          { name: "div", properties: { part: "form" } },
          { name: "div", properties: { part: "button" } },
        ],
      };

      var moduleFn = {
        getConfig: function (moduleId) {
          var moduleObj = contentModuleObj["module" + moduleId];
          if (moduleObj) return moduleObj.params;
        },
      };
      var formFn = {
        build: function (moduleId, params) {
          params.id = moduleId;
          var contentObj = parts.form.build(params);
          return [contentObj.config];
        },
        loadData: function (params) {
          var moduleId = params.moduleId;
          var moduleObj = params.module;
          if (!moduleObj) moduleObj = formModuleObj["module" + moduleId];
          if (moduleObj && moduleObj.params.form) {
            var moduleParams = moduleObj.params;
            var loadParams = moduleParams.form.load;
            if (loadParams && loadParams.url) {
              if (!loadParams.success)
                loadParams.success = function (data) {
                  parts.form.setData(moduleParams.form.id, data);
                };
              moduleCommon.ajax(loadParams, undefined, moduleSetting);
            }
          }
        },
      };
      var buttonFn = {
        build: function (moduleId, params) {
          var buttonsParams = {
            id: moduleId,
            moduleId: moduleId,
            buttons: params,
          };
          for (var buttonIndex = 0; buttonIndex < buttonsParams.buttons.length; buttonIndex++) {
            var button = buttonsParams.buttons[buttonIndex];
            button.call = {};
            button.call.click = {};
            button.call.click.fn = buttonFn.click;
            button.call.click.params = { moduleId: moduleId };
          }
          var buttonsObj = parts.buttons.build(buttonsParams);
          return [buttonsObj.config];
        },
        click: function (params) {
          var call = this.call;
          if (call) {
            var moduleId = params.moduleId;
            var moduleObj = params.module;
            if (!moduleObj) moduleObj = formModuleObj["module" + moduleId];
            if (moduleObj) {
              var callBind = { button: this, module: moduleObj.params };
              common.callFn(buttonFn.call, call, undefined, callBind);
            }
          }
        },
        call: {
          linkTo: function () {
            var currButton = this.button;
            if (currButton.url)
              moduleCommon.ajax(
                {
                  url: currButton.url,
                  type: currButton.requestType,
                  params: currButton.params,
                  success: function () {
                    window.location.href = currButton.linkTo;
                  },
                },
                undefined,
                moduleSetting
              );
            else window.location.href = currButton.linkTo;
          },
          update: function () {
            var currButton = this.button;
            var currModule = this.module;
            if (currButton.url) {
              var submitParams = currButton.params;
              var updateData = parts.form.element.getData(currModule.form.id);
              if (submitParams) {
                for (var paramKey in submitParams) {
                  if (submitParams.hasOwnProperty(paramKey)) {
                    var paramValue = submitParams[paramKey];
                    updateData[paramKey] = paramValue;
                  }
                }
              }
              moduleCommon.ajax(
                {
                  url: currButton.url,
                  type: currButton.requestType,
                  params: updateData,
                  success: function () {
                    formFn.loadData(currModule.id);
                  },
                },
                undefined,
                moduleSetting
              );
            }
          },
        },
      };

      function build(config) {
        var id = config.id;
        var formConfig = config.form;
        var buttonsConfig = config.buttons;

        var formModuleElement = common.deepCopy(moduleConfig);
        formModuleElement.properties.id = moduleSetting.idPrefix + id;

        var formObj = formFn.build(id, formConfig);
        if (formObj) formModuleElement.children[0].children = formObj;
        var buttonObj = buttonFn.build(id, buttonsConfig);
        if (buttonObj) formModuleElement.children[1].children = buttonObj;

        return (formModuleObj["module" + id] = {
          id: id,
          params: config,
          config: formModuleElement,
        });
      }

      return {
        build: build,
        getConfig: moduleFn.getConfig,
        form: formFn,
        button: buttonFn,
        setting: moduleSetting,
      };
    };

    var windowModule = function () {
      var moduleSetting = setting.window;
      var windowModuleObj = {};
      var formFn = {
        build: function (moduleId, params) {
          if (common.is.Array(params)) params = { id: moduleId, elements: params };
          if (!params.id) params.id = moduleId;
          var formObj = parts.form.build(params);
          return [formObj.config];
        },
        initData: function (params) {
          var moduleId = params.moduleId;
          var moduleObj = params.module;
          if (!moduleObj) moduleObj = formModuleObj["module" + moduleId];
          if (moduleObj) {
            var data = params.data;
            var moduleParams = moduleObj.params;
            if (moduleParams.form) {
              if (data) ui.parts.form.setData(moduleParams.form.id, data);
              else ui.parts.form.initData(moduleParams.form.id);
            }
          }
        },
        getData: function (params) {
          var moduleId = params.moduleId;
          var moduleObj = params.module;
          if (!moduleObj) moduleObj = formModuleObj["module" + moduleId];
          if (moduleObj) {
            var moduleParams = moduleObj.params;
            if (moduleParams.form) return ui.parts.form.getData(moduleParams.form.id);
          }
        },
        removeData: function (params) {
          var moduleId = params.moduleId;
          var moduleObj = params.module;
          if (!moduleObj) moduleObj = formModuleObj["module" + moduleId];
          if (moduleObj) {
            var moduleParams = moduleObj.params;
            if (moduleParams.form) ui.parts.form.removeData(moduleParams.form.id);
          }
        },
        help: function (params) {
          var moduleId = params.moduleId;
          var moduleObj = params.module;
          if (!moduleObj) moduleObj = formModuleObj["module" + moduleId];
          if (moduleObj) {
            var moduleParams = moduleObj.params;
            parts.form.showHelp(moduleParams.form.id, "auto");
          }
        },
      };
      var buttonFn = {
        build: function (moduleId, params, isChild) {
          if (common.is.Array(params)) params = { id: moduleId, buttons: params };
          if (!params.id) params.id = moduleId;
          if (isChild != true) {
            for (var buttonIndex = 0; buttonIndex < params.buttons.length; buttonIndex++) {
              var button = params.buttons[buttonIndex];
              button.call = {};
              button.call.click = {};
              button.call.click.fn = buttonFn.click;
              button.call.click.params = { moduleId: moduleId };
            }
          }
          var buttonsObj = parts.buttons.build(params);
          return [buttonsObj.config];
        },
        click: function (params) {
          var call = this.call;
          if (call) {
            var moduleId = params.moduleId;
            var moduleObj = params.module;
            if (!moduleObj) moduleObj = formModuleObj["module" + moduleId];
            if (moduleObj) {
              var callBind = { button: this, module: moduleObj.params };
              common.callFn(buttonFn.call, call, undefined, callBind);
            }
          }
        },
        call: {},
      };

      function build(params) {
        var id = params.id;
        //var title = config.title
        var isChild = params.isChild == true ? true : false;
        var formConfig = params.form;
        var buttonsConfig = params.buttons;

        var bodyConfig = [];
        bodyConfig.push(ui.build.tags.brConfig[0]); // 添加换行
        if (formConfig) {
          var formElement = formFn.build(id, formConfig);
          bodyConfig.push(formElement[0]);
        }
        if (buttonsConfig) {
          var buttonElement = buttonFn.build(id, buttonsConfig, isChild);
          bodyConfig.push(buttonElement[0]);
        }
        bodyConfig.push(ui.build.tags.brConfig[0]); // 添加换行
        params.body = bodyConfig;
        var windowObj = parts.window.build(params);

        return (windowModuleObj["module" + id] = {
          id: id,
          params: params,
          config: windowObj.config,
        });
      }

      return {
        build: build,
        form: formFn,
        button: buttonFn,
        setting: moduleSetting,
      };
    };

    var tableModule = function () {
      var moduleSetting = setting.table;
      var tableModuleObj = {};
      var moduleConfig = {
        name: "div",
        properties: {},
        children: [
          { name: "div", properties: { part: "search" }, children: [] },
          { name: "div", properties: { part: "tool" }, children: [] },
          { name: "div", properties: { part: "table" }, children: [] },
          { name: "div", properties: { part: "page" }, children: [] },
          { name: "div", properties: { part: "windows" }, children: [] },
        ],
      };

      var moduleFn = {
        getConfig: function (moduleId) {
          var moduleObj = tableModuleObj["module" + moduleId];
          if (moduleObj) return moduleObj.params;
        },
      };
      var searchFn = {
        build: function (moduleId, params) {
          var id = params.id;
          var form = params.form;
          var buttons = params.buttons ? params.buttons : [];

          form.id = moduleId;
          var formsObj = parts.form.build(form);

          for (var buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
            var button = buttons[buttonIndex];
            button.call = {};
            button.call.click = {};
            button.call.click.fn = searchFn.click;
            button.call.click.params = { moduleId: moduleId };
          }
          var buttonsObj = parts.buttons.build({ id: id, buttons: buttons });

          if (formsObj.config && buttonsObj.config) return [formsObj.config, buttonsObj.config];
          else return [];
        },
        click: function (params) {
          var call = this.call;
          if (call) {
            var moduleId = params.moduleId;
            var moduleObj = params.module;
            if (!moduleObj) moduleObj = tableModuleObj["module" + moduleId];
            if (moduleObj) {
              var callBind = { button: this, module: moduleObj.params };
              common.callFn(searchFn.call, call, undefined, callBind);
            }
          }
        },
        call: {
          query: function () {
            var currButton = this.button;
            var currModule = this.module;
            common.callFn(tableFn, "loadData", undefined, this);
            //tableFn.loadData(params);
          },
        },
      };
      var toolFn = {
        build: function (moduleId, params) {
          if (common.is.Array(params)) params = { id: moduleId, buttons: params };
          params.moduleId = moduleId;
          params.align = "left";
          var id = params.id;
          var buttons = params.buttons;
          for (var buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
            var button = buttons[buttonIndex];
            button.moduleId = moduleId;
            button.call = {};
            button.call.click = {};
            button.call.click.fn = toolFn.click;
            button.call.click.params = { moduleId: moduleId };
          }
          var buttonsObj = parts.buttons.build(params);
          return [buttonsObj.config];
        },
        click: function (params) {
          var call = this.call;
          if (call) {
            var moduleId = params.moduleId;
            var moduleObj = params.module;
            if (!moduleObj) moduleObj = tableModuleObj["module" + moduleId];
            if (moduleObj) {
              var callBind = { button: this, module: moduleObj.params };
              common.callFn(toolFn.call, call, undefined, callBind);
            }
          }
        },
        call: {
          add: function () {
            var currButton = this.button;
            var currModule = this.module;
            var addWindowObj = currModule.window["add"];
            if (addWindowObj) {
              var windowId = addWindowObj.id;
              var formId = addWindowObj.form.id;
              parts.form.initData(formId);
              parts.window.open(windowId);
            }
          },
          checkedAll: function () {
            var currButton = this.button;
            var currModule = this.module;
            if (currModule) parts.table.row.checkAll(currModule.table.id);
          },
          deleteAll: function () {
            var currButton = this.button;
            var currModule = this.module;
            if (currModule) parts.table.row.removeData(currModule.table.id);
          },
        },
      };
      var tableFn = {
        build: function (moduleId, params) {
          params.moduleId = moduleId;
          params.id = moduleId;
          var tableObj = parts.table.build(params);
          return [tableObj.config];
        },
        loadData: function (params) {
          var moduleId = params.moduleId;
          var moduleObj = params.module;
          if (!moduleObj) moduleObj = tableModuleObj["module" + moduleId];
          if (moduleObj) {
            var moduleParams = moduleObj.params;
            if (moduleParams.table && moduleParams.table.load) {
              var dataLoad = moduleParams.table.load;
              var submitData = dataLoad.params;
              var search = parts.form.getData(moduleParams.search.form.id);
              search = search ? search : {};
              if (submitData) {
                for (var submitDataKey in submitData) {
                  if (submitData.hasOwnProperty(submitDataKey)) {
                    var submitDataVal = submitData[submitDataKey];
                    search[submitDataKey] = submitDataVal;
                  }
                }
              }
              moduleCommon.ajax(
                {
                  url: dataLoad.url,
                  type: dataLoad.type,
                  params: search,
                  success: function (data) {
                    parts.table.row.setData(moduleParams.table.id, data);
                  },
                },
                moduleParams.page,
                moduleSetting
              );
            } else console.log("Table module error: table's load params is undefined.");
          }
        },
        operation: function (params) {
          var call = this.call;
          if (call) {
            var moduleId = params.moduleId;
            var moduleObj = params.module;
            if (!moduleObj) moduleObj = tableModuleObj["module" + moduleId];
            if (moduleObj) {
              var callBind = { button: this, module: moduleObj.params };
              common.callFn(searchFn.call, call, undefined, callBind);
            }
          }
        },
        call: {
          openWindow: function () {
            var currButton = this.button;
            var isInit = this.init == false ? false : true;
            var toWindowId = currButton.toWindowId;
            if (toWindowId) {
              if (isInit) {
                var data = ui.parts.table.row.getData(toWindowId, currButton.index);
                windowsFn.init(toWindowId, data);
              }
              ui.parts.window.open(toWindowId);
            }
          },
          delData: function () {
            var currButton = this.button;
            var currModule = this.module;
            if (currButton.url) {
              var submitParamKeys = currButton.paramKeys;
              var submitParams = currButton.params ? currButton.params : {};
              var delData = parts.table.row.getData(currModule.form.id, currButton.index);
              if (submitParamKeys) {
                for (var submitParamKey in submitParamKeys) {
                  if (submitParamKeys.hasOwnProperty(submitParamKey)) submitParams[delDataKey] = delData[delDataKey];
                }
              } else {
                for (var delDataKey in delData) {
                  if (delData.hasOwnProperty(delDataKey)) if (!submitParams[delDataKey]) submitParams[delDataKey] = delData[delDataKey];
                }
              }
              moduleCommon.ajax(
                {
                  url: currButton.url,
                  type: currButton.requestType,
                  params: submitParams,
                  success: function () {
                    formFn.loadData({ moduleId: currModule.id });
                  },
                },
                undefined,
                moduleSetting
              );
              parts.table.row.remove(currModule.form.id, currButton.index);
            }
          },
          linkTo: function () {
            var currButton = this.button;
            var currModule = this.module;
            if (currButton.linkTo) {
              var linkToParams = currButton.params;
              var urlParams = "";
              if (linkToParams) {
                var currData = parts.table.row.getData(currModule.form.id, currButton.index);
                for (var linkToParamIndex = 0; linkToParamIndex < linkToParams.length; linkToParamIndex++) {
                  var dataPropertyKey = linkToParams[linkToParamIndex];
                  if (common.is.String(dataPropertyKey)) urlParams += "&" + dataPropertyKey + "=" + currData[dataPropertyKey];
                  else if (common.is.Object(dataPropertyKey)) urlParams += "&" + dataPropertyKey.name + "=" + currData[dataPropertyKey.key];
                  else console.log("This linkTo's 'params' param must 'String' or 'Object{name,key}'.");
                }
              }
              if (linkTo.indexOf("?") == -1) urlParams = "?" + urlParams.substring(1, urlParams.length - 1);
              window.location.href = currButton.linkTo + urlParams;
            }
          },
        },
      };
      var pageFn = {
        build: function (moduleId, params) {
          params.moduleId = moduleId;
          params.call = {
            click: {
              fn: pageFn.check,
              params: { moduleId: moduleId },
            },
          };
          var pageObj = parts.page.build(params);
          return [pageObj.config];
        },
        check: function (params) {
          tableFn.initData({ moduleId: params.moduleId });
        },
      };
      var windowsFn = {
        build: function (moduleId, params) {
          var windows = params ? params : [];
          var windowsConfig = [];
          for (var windowIndex = 0; windowIndex < windows.length; windowIndex++) {
            var window = windows[windowIndex];
            window.isChild = true;
            for (var buttonIndex = 0; buttonIndex < window.buttons.length; buttonIndex++) {
              var button = window.buttons[buttonIndex];
              button.call = {};
              button.call.click = {};
              button.call.click.fn = windowsFn.click;
              button.call.click.params = { moduleId: moduleId, currWindow: window };
            }
            var windowObj = modules.windows.build(window);
            windowsConfig.push(windowObj.config);
          }
          return windowsConfig;
        },
        init: function (params) {
          var moduleId = params.moduleId;
          var moduleObj = params.module;
          if (!moduleObj) tableModuleObj["module" + moduleId];
          if (moduleObj) {
            var moduleParams = moduleObj.params;
            if (moduleParams.window) modules.windows.form.initData(moduleParams.window.id, params.data);
          }
        },
        click: function () {
          var call = this.call;
          if (call) {
            var moduleId = params.moduleId;
            var moduleObj = params.module;
            if (!moduleObj) moduleObj = formModuleObj["module" + moduleId];
            if (moduleObj) {
              var currWindow = params.currWindow;
              var callBind = { button: this, window: currWindow, module: moduleObj.params };
              common.callFn(buttonFn.call, call, undefined, callBind);
            }
          }
        },
        call: {
          save: function () {
            var currButton = this.button;
            var currModule = this.module;
            if (currButton.url) {
              var submitParams = currButton.params;
              var saveData = modules.windows.form.getData(currModule.form.id);
              if (submitParams) {
                for (var paramKey in submitParams) {
                  if (submitParams.hasOwnProperty(paramKey)) {
                    var paramValue = submitParams[paramKey];
                    saveData[paramKey] = paramValue;
                  }
                }
              }
              moduleCommon.ajax(
                {
                  url: currButton.url,
                  type: currButton.requestType,
                  params: saveData,
                  success: currButton.success,
                },
                undefined,
                moduleSetting
              );
            }
          },
        },
      };

      function build(config) {
        var id = config.id;
        var searchConfig = config.search;
        var toolConfig = config.tool;
        var tableConfig = config.table;
        var pageConfig = config.page;
        var windowsConfig = config.windows;

        var tableModuleElement = common.deepCopy(moduleConfig);
        tableModuleElement.properties.id = moduleSetting.idPrefix + id;

        var searchObj = searchFn.build(id, searchConfig);
        if (searchObj) tableModuleElement.children[0].children = searchObj;
        var toolObj = toolFn.build(id, toolConfig);
        if (toolObj) tableModuleElement.children[1].children = toolObj;
        var tableObj = tableFn.build(id, tableConfig);
        if (tableObj) {
          tableObj.push(buildUtil.tags.brConfig[0]);
          tableModuleElement.children[2].children = tableObj;
        }
        var pageObj = pageFn.build(id, pageConfig);
        if (pageObj) {
          pageObj.push(buildUtil.tags.brConfig[0]);
          tableModuleElement.children[3].children = pageObj;
        }
        var windowsObj = windowsFn.build(id, windowsConfig);
        if (windowsObj) tableModuleElement.children[4].children = windowsObj;

        return (tableModuleObj["module" + id] = {
          id: id,
          params: config,
          config: tableModuleElement,
        });
      }

      return {
        build: build,
        getConfig: moduleFn.getConfig,
        search: searchFn,
        tool: toolFn,
        table: tableFn,
        page: pageFn,
        windows: windowsFn,
        setting: moduleSetting,
      };
    };

    return (modules = {
      table: new tableModule(),
      content: new contentModule(),
      list: new listModule(),
      form: new formModule(),
      windows: new windowModule(),
    });
  },

  // pages obj -----------------------------------------------------------------------------------
  function (buildUtil, common, parts, modules, config) {
    return {};
  },

  // build obj -----------------------------------------------------------------------------------
  function (common) {
    var symbol = { properties: "{properties}", html: "{html}" };

    function getTagTemp(tagName, tagType) {
      if (tagType == 1) return "<" + tagName + " " + symbol.properties + " />";
      else return "<" + tagName + " " + symbol.properties + " >" + symbol.html + "</" + tagName + ">";
    }

    function getTagType(tagName) {
      if (tagName == "input" || tagName == "br" || tagName == "hr") {
        return 1;
      }
      return 2;
    }

    function setSymbolText(tagTemp, content, symbol) {
      if (tagTemp.indexOf(symbol) != -1) {
        return tagTemp.replace(symbol, content);
      }
      return tagTemp;
    }

    function objToStr(obj) {
      var str = "";
      for (var key in obj) {
        str += key + '="' + obj[key] + '" ';
      }
      return str;
    }

    function styleFormat(style) {
      if (common.is.Object(style)) {
        var styleStr = "";
        for (var styleKey in style) {
          if (style.hasOwnProperty(styleKey)) {
            var styleVal = style[styleKey];
            styleStr += styleKey + ":" + styleVal + ";";
          }
        }
        return styleStr;
      } else {
        return style;
      }
    }

    function buildTag(tag) {
      if (!tag.name) throw "tag's name is undefined.";

      var tagType = getTagType(tag.name);
      var tagText = getTagTemp(tag.name, tagType);

      if (!tag.properties) tag.properties = {};
      if (!tag.html) tag.html = "";
      if (tag.properties.style) tag.properties.style = styleFormat(tag.properties.style);

      tagText = setSymbolText(tagText, objToStr(tag.properties), symbol["properties"]);
      if (tagType == 2) tagText = setSymbolText(tagText, tag.html, symbol["html"]);

      return tagText;
    }

    function buildModule(modules, html) {
      if (!html) html = "";
      if (common.is.Object(modules)) {
        modules = [modules];
      }
      if (common.is.Array(modules)) {
        for (var i = 0; i < modules.length; i++) {
          var childModule = modules[i];
          var childHtml = "";
          if (childModule.children && childModule.children.length > 0) {
            childHtml = buildModule(childModule.children);
          } else if (childModule.text) {
            childHtml = childModule.text;
          }
          html += buildTag({ name: childModule.name, properties: childModule.properties, html: childHtml });
        }
      }

      return html;
    }
    return {
      buildTag: buildTag,
      buildModule: buildModule,
      tags: {
        brConfig: [{ name: "br" }],
        hrConfig: [{ name: "hr" }],
        nbsps: function (num) {
          if (!num) num = 6;
          var nbsps = "";
          for (var i = 0; i < num; i++) nbsps += "&nbsp";
          return nbsps;
        },
      },
      modules: {
        br: function () {
          return buildTag({ name: "br" });
        },
      },
    };
  },

  // common obj -----------------------------------------------------------------------------------
  function () {
    function getJSON(path) {
      var json;
      if (isEmpty(path)) {
        throw "Load json error: json path is null! ";
      } else {
        path = getBasePath() + "/" + path;
        $.ajax({
          type: "get",
          url: path,
          async: false,
          dataType: "json",
          context: document.body,
          success: function (data) {
            json = data;
          },
          error: function () {
            throw "Load json error: json path " + path;
          },
        });
      }
      return json;
    }

    function jqAjax(params) {
      console.log("Request url: ", params.url, ", data:", params.data + ".");
      if (!params.error)
        error = function () {
          console.log("Function jqAjax request " + url + " is error!");
        };
      $.ajax(params);
    }

    function ajax(opt) {
      opt = opt || {};
      opt.method = opt.method.toUpperCase() || "POST";
      opt.url = opt.url || "";
      opt.async = opt.async || true;
      opt.data = opt.data || null;
      opt.success = opt.success || function () {};

      var xmlHttp = null;
      if (XMLHttpRequest) {
        xmlHttp = new XMLHttpRequest();
      } else {
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
      }

      var params = [];
      for (var key in opt.data) {
        params.push(key + "=" + opt.data[key]);
      }
      var postData = params.join("&");
      if (opt.method.toUpperCase() === "POST") {
        xmlHttp.open(opt.method, opt.url, opt.async);
        xmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        xmlHttp.send(postData);
      } else if (opt.method.toUpperCase() === "GET") {
        xmlHttp.open(opt.method, opt.url + "?" + postData, opt.async);
        xmlHttp.send(null);
      }
      xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
          opt.success(xmlHttp.responseText);
        }
      };
    }

    function ajaxFileUpload(fileId, suffixs, url, success, error) {
      var filePath = $("#" + fileId).val();
      var suffix = filePath.substring(filePath.length - 3).toLowerCase();

      //if (suffix == "rar") {
      jQuery.ajaxSettings.traditional = true;
      $.ajaxFileUpload({
        url: url,
        secureuri: false,
        fileElementId: fileId,
        type: "POST",
        dataType: "multipart/form-data;charset=utf-8",
        success: success ? success : function () {},
        error: error ? error : function () {},
      });
      //} else {
      //    alert('请上传 rar 格式文件!')
      //}
    }

    function getBasePath() {
      var strFullPath = window.document.location.href;
      var strPath = window.document.location.pathname;
      var pos = strFullPath.indexOf(strPath);
      var basePath = strFullPath.substring(0, pos) + strPath.substring(0, strPath.substr(1).indexOf("/") + 1);
      return basePath;
    }

    function dateFromat(dateObj, fromat) {
      var o = {
        "M+": dateObj.getMonth() + 1, //month
        "d+": dateObj.getDate(), //day
        "h+": dateObj.getHours(), //hour
        "m+": dateObj.getMinutes(), //minute
        "s+": dateObj.getSeconds(), //second
        "q+": Math.floor((dateObj.getMonth() + 3) / 3), //quarter
        S: dateObj.getMilliseconds(), //millisecond
      };
      if (/(y+)/.test(fromat)) {
        fromat = fromat.replace(RegExp.$1, (dateObj.getFullYear() + "").substr(4 - RegExp.$1.length));
      }
      for (var k in o) {
        if (new RegExp("(" + k + ")").test(fromat)) {
          fromat = fromat.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
      }
      return fromat;
    }

    function isEmpty(obj) {
      if (obj == undefined || obj == null || obj.toString() == "") return true;
      else return false;
    }

    var is = (function () {
      var isObj = {
        types: ["Array", "Boolean", "Date", "Number", "Object", "RegExp", "String", "Window", "HTMLDocument"],
      };
      for (var i = 0, c; (c = isObj.types[i++]); ) {
        isObj[c] = (function (type) {
          return function (param) {
            return Object.prototype.toString.call(param) == "[object " + type + "]";
          };
        })(c);
      }
      return isObj;
    })();

    function deepCopy(p, c) {
      c = c ? c : {};
      for (var i in p) {
        if (typeof p[i] === "object") {
          c[i] = p[i].constructor === Array ? [] : {};
          deepCopy(p[i], c[i]);
        } else {
          c[i] = p[i];
        }
      }
      return c;
    }

    function callFu(obj, fn, params, bindThis) {
      if (fn) {
        var result;
        if (typeof fn == "function") {
          if (bindThis) result = fn.call(bindThis, params);
          else result = fn(params);
        } else if (typeof fn == "string") {
          var arr = fn.split(".");
          for (var i = 0; i < arr.length; i++) obj = obj[arr[i]];
          if (bindThis) result = obj.call(bindThis, params);
          else result = obj(params);
        }
        return result;
      }
    }

    function getByPath(base, path) {
      if (base && path) {
        var obj = base;
        var arr = path.split(".");
        for (var i = 0; i < arr.length; i++) {
          if (!obj) return undefined;
          obj = obj[arr[i]];
        }
        return obj;
      }
    }

    function getUrlParams(temp, type, data) {
      var urlParams;
      return urlParams;
    }

    function getQueryString(name) {
      var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
      var r = window.location.search.substr(1).match(reg);
      if (r != null) return unescape(r[2]);
      return null;
    }

    function replace(temp, data, regStr, format) {
      var result = temp;
      if (is.Object(temp)) {
        result = {};
        for (var tempKey in temp) {
          if (temp.hasOwnProperty(tempKey)) {
            var tempVal = temp[tempKey];
            result[tempKey] = replace(tempVal, data, regStr, format);
          }
        }
      } else if (is.String(temp)) {
        var params = temp.match(new RegExp(regStr, "g"));
        if (is.Array(params)) {
          for (var paramIndex = 0; paramIndex < params.length; paramIndex++) {
            var paramPathKey = params[paramIndex];
            var paramPath = paramPathKey.match(new RegExp(regStr, "i"));
            result = result.replace(paramPathKey, getByPath(data, paramPath[1]));
          }
        }
      } else if (is.Array(temp)) {
        var tempArray = [];
        for (var tempIndex = 0; tempIndex < temp.length; tempIndex++) {
          var tempItem = temp[tempIndex];
          var tempVal = replace(tempItem, data, regStr, format);
          tempArray.push(tempVal);
        }
        result = tempArray;
      }
      return result;
    }

    return {
      ajax: ajax,
      jqAjax: jqAjax,
      getJSON: getJSON,
      ajaxFileUpload: ajaxFileUpload,
      getBasePath: getBasePath,
      is: is,
      isEmpty: isEmpty,
      dateFromat: dateFromat,
      deepCopy: deepCopy,
      callFu: callFu,
      getUrlParams: getUrlParams,
      getByPath: getByPath,
      getQueryString: getQueryString,
      replace: replace,
    };
  },

  // config obj -----------------------------------------------------------------------------------
  function () {
    var commonConfig = {
      paramsName: {
        currPage: "page",
        pageShowSize: "showSize",
      },
      resultPath: {
        success: "success",
        currPage: "data.page",
        maxPage: "data.maxPage",
        maxCount: "data.maxCount",
        data: "data",
        listData: "data.data",
        msg: "msg",
        errMsg: "msg",
      },
    };
    var partConfig = {
      window: {
        idPrefix: "p_window_",
        bgIdPrefix: "p_window_bg",
        bodyIdPrefix: "p_window_body_",
      },
      form: {
        idPrefix: "p_forms_",
        elementIdPrefix: "p_form_",
        helpMsgName: "p_form_help_msg",
        errorMsgName: "p_form_error_msg",
        fileNameSuffix: "File",
        fileReturnDataPath: "data",
        filePathNameSuffix: "Text",
        fileBrowseButtonName: "CHOOSE",
        fileUploadButtonName: "UPLOAD",
      },
      grid: {
        idPrefix: "p_grid_",
      },
      drawBoard: {
        toolIdPrefix: "p_draw_",
        drawIdPrefix: "p_draw_shapes_",
        shapeIdPrefix: "p_draw_shape_",
        connectIdPrefix: "p_draw_connect_",
        connectLineIdPrefix: "p_draw_connect_line_",
        stratGap: 6,
        endGap: 6,
        allowNum: 15,
        connectPointText: ">",
      },
      svg: {
        idPrefix: "p_svg_",
        elementIdPrefix: "p_svg_element_",
      },
      buttons: {
        idPrefix: "p_button_",
      },
      lineMap: {
        idPrefix: "p_lineMap_",
        lineIdPrefix: "p_lineMap_line_",
      },
      custom: {
        idPrefix: "p_custom_",
      },
      table: {
        idPrefix: "p_table_",
        checkIdPrefix: "p_tr_check_",
        checkAllIdPrefix: "p_tr_checkAll_",
        colNamePrefix: "p_table_key_",
        rowNamePrefix: "p_table_row_",
      },
      tabs: {
        idPrefix: "p_tabs_",
        tabNamePrefix: "p_tab_",
      },
      page: {
        idPrefix: "p_page_",
        lastText: " < ",
        nextText: " > ",
        firstlyText: "首页",
        finallyText: "末页",
        toText: "跳转到",
      },
      nav: {
        idPrefix: "p_nav_",
        listIdPrefix: "p_nav_list_",
        groupIdPrefix: "p_nav_group_",
        nodeIdPrefix: "p_nav_node_",
      },
      content: {
        idPrefix: "p_content_",
        operationNamePrefix: "p_operation_",
      },
      list: {
        idPrefix: "p_list_",
        itemNamePrefix: "p_list_item_",
      },
    };
    var moduleConfig = {
      list: {
        idPrefix: "m_list_",
        currPage: 1,
        pageSize: 10,
        paramsName: {
          currPage: commonConfig.paramsName.currPage,
          pageShowSize: commonConfig.paramsName.pageShowSize,
        },
        resultPath: {
          success: commonConfig.resultPath.success,
          currPage: commonConfig.resultPath.currPage,
          maxPage: commonConfig.resultPath.maxPage,
          maxCount: commonConfig.resultPath.maxCount,
          data: commonConfig.resultPath.data,
          listData: commonConfig.resultPath.listData,
          msg: commonConfig.resultPath.msg,
          errMsg: commonConfig.resultPath.errMsg,
        },
      },
      content: {
        idPrefix: "m_content_",
        currPage: 1,
        pageSize: 10,
        paramsName: {
          currPage: commonConfig.paramsName.currPage,
          pageShowSize: commonConfig.paramsName.pageShowSize,
        },
        resultPath: {
          success: commonConfig.resultPath.success,
          currPage: commonConfig.resultPath.currPage,
          maxPage: commonConfig.resultPath.maxPage,
          maxCount: commonConfig.resultPath.maxCount,
          data: commonConfig.resultPath.data,
          listData: commonConfig.resultPath.listData,
          msg: commonConfig.resultPath.msg,
          errMsg: commonConfig.resultPath.errMsg,
        },
      },
      form: {
        idPrefix: "m_form_",
        currPage: 1,
        pageSize: 10,
        paramsName: {
          currPage: commonConfig.paramsName.currPage,
          pageShowSize: commonConfig.paramsName.pageShowSize,
        },
        resultPath: {
          success: commonConfig.resultPath.success,
          currPage: commonConfig.resultPath.currPage,
          maxPage: commonConfig.resultPath.maxPage,
          maxCount: commonConfig.resultPath.maxCount,
          data: commonConfig.resultPath.data,
          listData: commonConfig.resultPath.listData,
          msg: commonConfig.resultPath.msg,
          errMsg: commonConfig.resultPath.errMsg,
        },
      },
      window: {
        idPrefix: "m_windows_",
        currPage: 1,
        pageSize: 10,
        paramsName: {
          currPage: commonConfig.paramsName.currPage,
          pageShowSize: commonConfig.paramsName.pageShowSize,
        },
        resultPath: {
          success: commonConfig.resultPath.success,
          currPage: commonConfig.resultPath.currPage,
          maxPage: commonConfig.resultPath.maxPage,
          maxCount: commonConfig.resultPath.maxCount,
          data: commonConfig.resultPath.data,
          listData: commonConfig.resultPath.listData,
          msg: commonConfig.resultPath.msg,
          errMsg: commonConfig.resultPath.errMsg,
        },
      },
      table: {
        idPrefix: "m_table_",
        addWindowName: "add",
        updateWindowName: "update",
        currPage: 1,
        pageSize: 10,
        paramsName: {
          currPage: commonConfig.paramsName.currPage,
          pageShowSize: commonConfig.paramsName.pageShowSize,
        },
        resultPath: {
          success: commonConfig.resultPath.success,
          currPage: commonConfig.resultPath.currPage,
          maxPage: commonConfig.resultPath.maxPage,
          maxCount: commonConfig.resultPath.maxCount,
          data: commonConfig.resultPath.data,
          listData: commonConfig.resultPath.listData,
          msg: commonConfig.resultPath.msg,
          errMsg: commonConfig.resultPath.errMsg,
        },
      },
    };
    var pageConfig = {};

    return {
      common: commonConfig,
      part: partConfig,
      module: moduleConfig,
      page: pageConfig,
    };
  }
);
