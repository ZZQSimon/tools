/**
 * Created by Administrator on 2018/6/26.
 *
 */
(function($){

    function loadInputSelect(data, callBack, node){
        postJson('/widget/inputSelect/widgetFilter.do', data, function (data) {
            callBack(data);
            if (!isEmpty(node)){
                $(node).data('inputSelectData', data);
            }
        });
    }
    function initFeildInput(node, param){
        var desc = getColumnCache(param.tableName, param.columnName);
        if (desc.data_type == 1 || desc.data_type == 13) {
            if (desc.dic_id) {
                dxDictSelect(node, desc, param);
            } else if (desc.ref_table_name && desc.is_multiple == 0)
                dxInputSelect(node, desc, param);
            else
                dxInput(node, desc, param);
        }else
            dxInput(node, desc, param);
        //}else if (desc.data_type == 2){
        //    dxDigits(desc, "$class dx-digits");
        //}else if (desc.data_type == 3){
        //    dxNumber(desc, "$class dx-number");
        //}else if (desc.data_type == 4){
        //    dxDate(desc, "$class dx-date");
        //}else if (desc.data_type == 5){
        //    dxCheckbox(desc, "$class dx-checkbox");
        //}else if (desc.data_type == 6){
        //    dxPassword(desc, "$class dx-password");
        //}else if (desc.data_type == 7){
        //    dxPicture(desc, "$class dx-pic");
        //}else if (desc.data_type == 8){
        //    dxFile(desc, "$class dx-file");
        //}else if (desc.data_type == 9){
        //    dxLink(desc, "$class dx-link");
        //}else if (desc.data_type == 10){
        //    dxEMail(desc, "$class dx-email");
        //}else if (desc.data_type == 11){
        //    dxTime(desc, "$class dx-time");
        //}else if (desc.data_type == 12){
        //    dxDatetime(desc, "$class dx-datetime");
        //}else if (desc.data_type == 14){
        //    dxTextArea(desc, "$class dx-text-area");
        //}else if (desc.data_type == 15){
        //    dxEditorInput(desc, "$class dx-editor-input");
        //}else {
        //    dxLog(desc.column_name + 'field type:' + desc.data_type + 'not found')
        //}

    }

    $.fn.extend({
        g1Widget : function(param){
            var that = this;

        }
    });
})(jQuery);