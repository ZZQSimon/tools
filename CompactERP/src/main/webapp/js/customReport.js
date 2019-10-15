/**
 * Created by Administrator on 2017/12/14.
 */
registerInit('customReport', function (form) {
    form.get = function () {
        return $('#' + form.id);
    };
    form.filter.get = function () {
        return $('#' + form.filter.id);
    };
    initFilter(form, function (a, filter, c) {
        //report按钮。
        postJson('/customReport/search.do', {filterTable: form.tableName, filter: filter}, function (result) {
            $("body").append("<iframe src='" + makeUrl(result) + "' style='display: none;' ></iframe>");
        })
    }, function (a, b, c) {
        //重置按钮回调。
        var bbb = 2;
    });
});
