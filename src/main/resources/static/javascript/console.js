$(function () {
  ui.custom.console.init(configs.drawBoard);
  $("#p-nav").append($("#nav"));
  window.i18n.init({
    cn: "./javascript/i18n/console.cn.json",
    en: "./javascript/i18n/console.en.json",
  });
});
