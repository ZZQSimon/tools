/**
 * Created by Administrator on 2018/5/15.
 * g1Formula
 * param:   参数为如下对象
 * {
 * table: 'xxx',
 * changeTargetColor: true/false,   default true
 * color: 'color'                   default red
 * checkCallBack: function(){}  //若无此回调。则控件alert校验回调。
 * check: function(){}
 * }
 * 例：
 * $('.deploy_read_only_condition_input').g1Formula({table: 't_holiday'});
 */
(function($){
    function isEmpty(val, keepSpace) {
        if (val === undefined) return true;
        if (val === null) return true;
        if (typeof val == 'object') {
            for (var name in val) {
                return false;
            }
            return true;
        }
        if (!keepSpace)
            val = $.trim(val);
        return val.length === 0;

    }
    function init(bom, param){
        setData(bom, param);
        buildEvent(bom);
    }
    function setData(bom, param){
        $(bom).data('formula-widget-date', param);
    }
    function getData(bom){
        return $(bom).data('formula-widget-date');
    }
    function buildEvent(bom){
        $(bom).off('blur');
        $(bom).on('blur', function (e) {
            var param = getData(bom);
            var formula = $(bom).val();
            var data = checkFormula(param.table, formula);
            if (!isEmpty(param) && !isEmpty(param.checkCallBack) && typeof param.checkCallBack === 'function'){
                param.checkCallBack(data);
            }else{
                alert(data.msg);
            }
            //color(bom, data);
        })
    }
    function color(bom, data){
        removeColor(bom, data);
        var param = getData(bom);
        if (data.status || isEmpty(param)){
            return;
        }
        if (!data.status){
            var color = isEmpty(param.color)? 'red': param.color;
            //修改目标框颜色
            if (isEmpty(param.changeTargetColor) || param.changeTargetColor){
                $(bom).css('background-color', color);
            }
        }
    }
    function removeColor(bom, data){
        if (data.status){
            //移除目标框颜色
            $(bom).css('background-color', '');
        }
    }
    function checkFormula(table, formula){
        var result;
        postJson('/data/checkFormula.do', {table: table, formula: formula}, function (data) {
            result = data;
        }, null, true);
        return result;
    }
    $.fn.extend({
        g1Formula : function(param){
            var that = this;
            if (param == true){
                return {check: function(table, formula){
                    return checkFormula(table, formula);
                }, removeColor: function(that){
                    removeColor(that, {status: true})
                }};
            }else{
                init(this, param);
            }
        }
    });
})(jQuery);