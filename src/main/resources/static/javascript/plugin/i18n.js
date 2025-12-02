window.i18n = (function () {
  var s_languageObj = {};
  var s_language = null;

  function init(options, defaultLanguage) {
    var _options = $.extend({}, options);
    var language = getCookie("language") || defaultLanguage || Object.keys(options)[0];
    for (const key in _options) {
      $.getJSON(_options[key], function (object) {
        s_languageObj[key] = object;
        if (language == key) {
          changeLanguage(language);
        }
      });
    }
    return language;
  }

  function getCookie(name) {
    var arr = document.cookie.split("; ");
    for (var i = 0; i < arr.length; i++) {
      var arr1 = arr[i].split("=");
      if (arr1[0] == name) {
        return arr1[1];
      }
    }
    return "";
  }

  function setCookie(name, value, myDay) {
    var oDate = new Date();
    oDate.setDate(oDate.getDate() + myDay);
    document.cookie = name + "=" + value + "; expires=" + oDate;
  }

  function changeLanguage(language) {
    console.log(language);
    s_language = language;
    setCookie("language", language, 30);
    $("[i18n]").each(function (i, v) {
      var i18nStr = $(v).attr("i18n") || "";
      var list1 = i18nStr.split(";");
      try {
        for (const item1 of list1) {
          var list2 = item1.split(".");
          var key1 = list2[0];
          var key2 = list2[1];
          var pos = "";
          var value = "";
          if (list2[2]) {
            value = s_languageObj[s_language][key1][key2];
            pos = list2[2];
          } else {
            value = s_languageObj[s_language][key1];
            pos = key2;
          }
          if (pos == "text") {
            $(v).text(value);
          } else if (!!pos) {
            $(v).attr(pos, value);
          }
        }
      } catch (error) {
        console.log(i18nStr);
      }
    });
  }

  function getStr(key, language) {
    try {
      var _language = language || s_language;
      var i18n = key.split(".");
      var key = i18n[0];
      var key2 = i18n[1];
      return s_languageObj[_language][key][key2];
    } catch (error) {
      return "";
    }
  }

  return {
    init: init,
    changeLanguage: changeLanguage,
    getStr: getStr,
  };
})();
