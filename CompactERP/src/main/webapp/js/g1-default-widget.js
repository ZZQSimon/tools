(function($){
    function S4() {
        return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    }
    function guid() {
        return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
    }

    function loadInputSelect(data, callBack, node){
        postJson('/widget/inputSelect/widgetFilter.do', data, function (data) {
            callBack(data);
            if (!isEmpty(node)){
                $(node).data('inputSelectData', data);
            }
        });
    }
    function dxLog(msg){
        console.log(msg);
    }
    function getTableCache(tableName){
        if (isEmpty(dx) || isEmpty(dx.table) || isEmpty(dx.table[tableName])){
            dxLog("no table cache")
        }else{
            return dx.table[tableName];
        }
    }
    function getColumnCache(tableName, columnName){
        var table = getTableCache(tableName);
        var column = table.columnMap[columnName];
        if (isEmpty(column)){
            dxLog("no column cache")
        }else{
            return column;
        }
    }
    function initFeildInput(node, param){
        var desc = getColumnCache(param.tableName, param.columnName);
        if (desc.data_type == 1 || desc.data_type == 13) {
            if (desc.dic_id) {
                dxDictSelect(node, desc, param);
            } else if (desc.ref_table_name && desc.is_multiple == 0)
                dxInputSelect(node, desc, param);
            //else if (desc.data_format)
            //dxInputWithFormat(desc, "$class dx-input-with-format", false);
            //else if (dx.isMapColumn(field))
            //dxMapField(desc, "$class dx-input-map");
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
    function initDxInput($this, desc, $class, callBacks){
        var html = '<input type="text" name="' + desc.column_name + '" value=""' +
            'class=" '+ $class + '"/>';
        $($this).append(html);
        buildInputEvent($this, callBacks)
    }
    function buildInputEvent($node, callBacks){
        $($node).find('input').on('blur', function () {
            callBacks.dxOnBlur($($node).find('input').val());
        });
        $($node).find('input').on('change', function () {
            callBacks.dxOnChange($($node).find('input').val());
        });
    }
    function getDate($node){
        var data = {};
        var columnCache = $node.data('widget');
        var table = getTableCache(columnCache.ref_table_name);
        switch (columnCache.data_type){
            case dx.dataType.string:
                if (columnCache.dic_id)
                    return getDicData($node);
                else if (columnCache.ref_table_name)
                    return getInputSelectData($node);
                else{
                    return getInputData($node);
                }
            case dx.dataType.link:
                return getInputData($node);
            default :
                return getInputData($node);
            //case dx.dataType.textArea:
            //case dx.dataType.editorInput:
            //case dx.dataType.email:
            //case dx.dataType.auto:
            //case dx.dataType.digits:
            //case dx.dataType.number:
            //case dx.dataType.date:
            //case dx.dataType.time:
            //case dx.dataType.datetime:
            //case dx.dataType.boolean:
            //case dx.dataType.pic:
            //case dx.dataType.file:
        }
        data[table.idColumns[table.idColumns.length-1]] = 'id111';
        data[table.name_column] = 'name222';
        return data;
    }
    function setDate($node, data){
        var columnCache = $node.data('widget');
        switch (columnCache.data_type){
            case dx.dataType.string:
                if (columnCache.dic_id)
                    return setDicData($node, data);
                else if (columnCache.ref_table_name)
                    return setInputSelectData($node, data);
                else{
                    setInputData($node, data);
                }
            default :
                setInputData($node, data);
            //case dx.dataType.link:
            //    return ;
            //case dx.dataType.textArea:
            //case dx.dataType.editorInput:
            //case dx.dataType.email:
            //case dx.dataType.auto:
            //case dx.dataType.digits:
            //case dx.dataType.number:
            //case dx.dataType.date:
            //case dx.dataType.time:
            //case dx.dataType.datetime:
            //case dx.dataType.boolean:
            //case dx.dataType.pic:
            //case dx.dataType.file:
        }
    }
    function getDicData(node){

    }
    function getInputSelectData(node){
        var data = $(node).data('inputSelectData');
        var dataId = $(node).find('.dx-input-select-id').val();
        var columnCache = $(node).data('widget');
        var refTableName = columnCache.ref_table_name;
        var refTable = getTableCache(refTableName);
        if (!isEmpty(data))
            for (var i=0; i<data.length; i++){
                var refTableId = data[i][refTable.idColumns[refTable.idColumns.length-1]];
                if (refTableId == dataId){
                    return data[i];
                }else{
                    var returnData = {};
                    returnData[refTable.idColumns[refTable.idColumns.length-1]] = $(node).find('.dx-input-select-id').val();
                    returnData[refTable.name_column] = $(node).find('.dx-input-select-name').val();
                    return returnData;
                }
            }
    }
    function setDicData($node, data){

    }
    function getInputData(node){
        var columnCache = $(node).data('widget');
        var returnData = {};
        returnData[columnCache.column_name] = $(node).find('input').val();
        return returnData;
    }
    function setInputData(node, data){
        if (typeof data == 'object'){
            for (var key in data){
                $(node).find('input').val(data[key]);
                break;
            }
        }else if (typeof data == 'string'){
            $(node).find('input').val(data);
        }
    }
    function setInputSelectData(node, data){
        var columnCache = node.data('widget');
        var table = getTableCache(columnCache.ref_table_name);
        if (isEmpty(data)){
            inputUpdate($(node).find('.dx-input-select-id'), '');
            inputUpdate($(node).find('.dx-input-select-name'), '');
        }else{
            inputUpdate($(node).find('.dx-input-select-id'), data[table.idColumns[table.idColumns.length-1]]);
            inputUpdate($(node).find('.dx-input-select-name'), data[table.name_column]);
        }
    }
    function inputUpdate(node, data){
        $(node).val(data);
    }

    function initDxInput($this, desc, $class, callBacks){
        var html = '<input type="text" name="' + desc.column_name + '" value=""' +
            'class=" '+ $class + '"/>';
        $($this).append(html);
        buildInputEvent($this, callBacks)
    }
    function buildInputEvent($node, callBacks){
        if (!isEmpty(callBacks) && !isEmpty(callBacks.dxOnBlur))
            $($node).find('input').on('blur', function () {
                callBacks.dxOnBlur($($node).find('input').val());
            });
        if (!isEmpty(callBacks) && !isEmpty(callBacks.dxOnChange))
            $($node).find('input').on('change', function () {
                callBacks.dxOnChange($($node).find('input').val());
            });
        if (!isEmpty(callBacks) && !isEmpty(callBacks.dxOnInit))
            callBacks.dxOnInit($node, callBacks);
    }
    //普通input
    function dxInput(node, desc, param){
        var html = buildInputHtml(param, desc);
        $(node).append(html);
        buildInputEvent(node, param);
    }
    //字典
    function dxDictSelect(node, desc, param){
        var html = buildDictHtml(param, desc);
        $(node).append(html);
        buildDictSelectEvent(node, param);
    }
    //下拉查询框
    function dxInputSelect(node, desc, param){
        var data = {};
        data.tableName = param.tableName;
        data.columnName = param.columnName;
        data.param = param.data;
        data.text = $(node).find('.dx-input-select-id').val();
        $(node).data('filterData', data);
        loadInputSelect(data, function (result) {
            var html = buildInputSelectHtml(desc, param, result)
            $(node).append(html);
            buildInputSelectEvent(node, param);
        }, node);
    }
    function buildInputHtml(param, desc){
        var html = '<input type="text" name="' + desc.column_name + '" value="" class="input-param"/>';
        return html;
    }
    function buildDictHtml(param, desc){
        var id = guid();
        var html = '<div class="dropdown ' + formatParam(param.class) + '">';
        html = html + '<input id="' + id + '" name="' + param.columnName + '" type="text" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dx-input-select-id"/>';
        html = html + '<ul class="dropdown-menu dx-widget-dropdown" aria-labelledby="' + id + '">';
        var item = dx.dict[desc.dic_id];
        for (var key in item){
            html = html + '<li value="' + key + '"><span class="dict-key">'+ key +'</span><span class="dict-value">' + getDictI18N(param, item[key]) + '</span></li>';
        }
        html = html + '</ul></div>';
        return html;
    }
    function buildDictSelectEvent(node, param){
        $(node).find('li').on('click', function () {
            $(node).find('input').val($(this).find('.dict-value').text());
        })
    }
    function buildInputSelectEvent(node, param){
        widgetBaseCallBack(param, getInputSelectData(node), node);
        $(node).find('li').on('click', function () {
            if ($(this).find('.dict-key').length > 0){
                inputUpdate($(node).find('.dx-input-select-id'), $(this).find('.dict-key').text());
                inputUpdate($(node).find('.dx-input-select-name'), $(this).find('.dict-value').text());
                if (!isEmpty(param) && !isEmpty(param.dxOnClick))
                    param.dxOnClick(getInputSelectData(node));
            }else if ($(this).find('.dx-input-select-more-select').length > 0){
                var filterData = $(node).data('filterData');
                var desc = getColumnCache(filterData.tableName, filterData.columnName);
                var table = getTableCache(desc.ref_table_name);
                showDialogForm({
                    url: '/list/table.view',
                    data: {
                        parent: filterData.tableName,
                        table: desc.ref_table_name,
                        action: 'select'
                        //defaultFilters: field.p('filters')
                    },
                    title: '',
                    class: 'dx-input-selector-dialog more-select-dialog',
                    needAutoExpand: true,
                    shown: function (listForm, dialog) {
                        listForm.close = function (data) {
                            dialog.close();
                            inputUpdate($(node).find('.dx-input-select-id'), data.ref_id);
                            inputUpdate($(node).find('.dx-input-select-name'), data.nameExpression);
                        }
                    }
                });
            }
        });
        var to;
        var keyUpSelect = $(node).find('.dx-input-select-id');
        keyUpSelect.keyup(function () {
            if(to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                var filterData = $(node).data('filterData');
                filterData.text = $(keyUpSelect).val();
                var desc = getColumnCache(filterData.tableName, filterData.columnName);
                reloadInputSelect(node, filterData, desc, param);
            }, 500);
        });
    }
    function widgetBaseCallBack(param, data, node){
        if (!isEmpty(param)){
            if (!isEmpty(param.dxOnBlur))
                param.dxOnBlur(data);
            if (!isEmpty(param.dxOnBlur))
                param.dxOnChange(data);
            if (!isEmpty(param.dxOnInit))
                param.dxOnInit(node, param);
        }
    }
    function getDictI18N(param, I18N){
        if (isEmpty(param.language)){
            return I18N.cn;
        }else if (isEmpty(I18N[param.language])){
            return I18N.cn;
        }else {
            return I18N[param.language];
        }
    }
    function buildInputSelectHtml(desc, param, data){
        var id = guid();
        var html = '<div class="dropdown ' + formatParam(param.class) + '">';
        html = html + '<input id="' + id + '" name="' + param.columnName + '" type="text" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="dx-input-select-id"/>' +
            '<input type="text" class="dx-input-select-name" disabled/>';
        html = html + '<ul class="dropdown-menu dx-widget-dropdown" aria-labelledby="' + id + '">';
        var refTable = getTableCache(desc.ref_table_name);
        html = html + buildUlHtml(data, refTable);
        html = html + '</ul></div>';
        return html;
    }
    function buildUlHtml(data, refTable){
        var html = '';
        for (var i=0; i<data.length; i++){
            html = html + '<li value="' + data[i][refTable.idColumns[refTable.idColumns.length-1]] + '"><span class="dict-key">'+ data[i][refTable.idColumns[refTable.idColumns.length-1]] +'</span><span class="dict-value">' + data[i][refTable.name_column] + '</span></li>';
        }
        html = html + '<li><span class="dx-input-select-more-select">'+msg("View_More")+'</span></li>';
        return html;
    }
    function reloadInputSelect(node, data, desc, param){
        var refTable = getTableCache(desc.ref_table_name);
        loadInputSelect(data, function (result) {
            $(node).find('.dx-widget-dropdown').empty();
            $(node).find('.dx-widget-dropdown').append(buildUlHtml(result, refTable));
            buildInputSelectEvent(node);
        }, node);
    }
    function formatParam(param){
        if (isEmpty(param))
            return '';
        else
            return param;
    }


    $.fn.extend({
        dxDefaultWidget : function(param){
            var that = this;
            if (param == true){
                return {
                    getDate : function (){
                        return getDate(that);},
                    setDate : function(data){
                        return setDate(that, data)}
                }
            }else{
                $(this).data('widget', getColumnCache(param.tableName, param.columnName));
                initFeildInput(this, param);
            }
        }
    });
})(jQuery);