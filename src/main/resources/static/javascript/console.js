$(function () {
  $.ajaxSetup({
    beforeSend: function (xhr) {
      xhr.setRequestHeader("Authorization", "Bearer " + $.cookie("Admin-Token"));
    },
  });
  ui.custom.console.init(configs.drawBoard);
  $("#p-nav").append($("#nav"));
  var lang = window.i18n.init({
    cn: "./javascript/i18n/console.cn.json",
    en: "./javascript/i18n/console.en.json",
  });
  $("#lang").val(lang);
});
