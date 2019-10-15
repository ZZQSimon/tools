registerInit('UrlInterface', function (form) {
    function ajaxLoadGroup(form, callBack){
        postJson('/urlInterface/selectUrlInterface.do', {type : form.type}, function (result) {
            callBack(result);
        });
    }

    form.get().find(".deploy-url-left-tree").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"}); // First scrollable DIV

  //初始化国际化控件
    function international(divClass, internationalId, menuName, isShow,onSubmintCallback,onSelectCallback) {
        $(divClass).internationalControl({
            internationalId: internationalId,
            menuName: menuName,
            isShow: isShow,
            onSubmit: function (data) {
            },
            onSelect: function (data) {
            }
        })
    }
    //初始化左侧树
    function initLeftTree(){
        $tableTree.jstree({
            'core' : {
                "multiple" : false,
                'data' : null,
                'dblclick_toggle': true,          //tree的双击展开
                "check_callback" : true        //
            },
            "plugins" : ["search", "contextmenu"]
        });
    }
    //左侧输赋值函数
    function callback(result){
        if(!isEmpty(result))
            if(isEmpty(result)){
                $tableTree.jstree(true).settings.core.data = null;
                $tableTree.jstree(true).refresh();
            }else{
                $tableTree.jstree(true).settings.core.data = result;
                $tableTree.jstree(true).refresh();
            }
    }
    var $tableTree = form.get().find('.deploy-url-left-tree');
    initLeftTree();
    //左侧search
    var to;
    var leftSearch = form.get().find('.deploy-url-left-tree-search');
    leftSearch.keyup(function () {
        if(to) {
            clearTimeout(to);
        }
        to = setTimeout(function () {
            $tableTree.jstree(true).search(leftSearch.val());
        }, 250);
    });
    ajaxLoadGroup(form, callback);

    //排序
    function dataOrder(data){
        var orderData = [];
        if (isEmpty(data))
            return;
        else if (typeof data == 'object'){
            for (var key in data){
                orderData.push(data[key]);
            }
            var tmp;
            for (var i=0; i<orderData.length; i++){
                for (var j = i + 1; j<orderData.length; j++){
                    if (orderData[i].seq > orderData[j].seq){
                        tmp = orderData[i];
                        orderData[i] = orderData[j];
                        orderData[j] = tmp;
                    }
                }
            }
        }
        return orderData;
    }
    //字典下拉框
    function gridDiceSelect(dictId){
        var dict = dx.dict[dictId];
        var OptionsData = [];
        OptionsData.push({key:'', productname:''});
        for(var key in dict){
            OptionsData.push({
                key : key,
                productname : dict[key][dx.user.language_id]
            });
        }
        return OptionsData;
    }
    //字段下拉框
    function getColumnOptionsData(urlInterface){
        if (isEmpty(urlInterface))
            return;
        var columnOptionsData = [];
        columnOptionsData.push({column_name:'', productname:''});
        if (!isEmpty(urlInterface)){
            var columns = urlInterface.urlParam;
            for (var key in columns){
                columnOptionsData.push({
                    column_name : columns[key].column_name,
                    productname : columns[key].i18n[dx.user.language_id]
                });
            }
        }else{
            var rows = form.get().find('.deploy-table-grid .deploy-column').datagrid('getRows');
            for (var i=0; i<rows.length; i++){
                columnOptionsData.push({
                    column_name : rows[i].column_name,
                    productname : rows[i].column_name_I18N
                });
            }
        }
        return columnOptionsData;
    }
    function initDataGrid($node, columns, data, event, rownumbers){
        $node.datagrid({
            height:"100%",
            columns : columns,
            rownumbers : rownumbers ? true : false,
            //data : data,
            fitColumns: true,
            onDblClickCell: event ? (event.onDblClickCell ? event.onDblClickCell : function(){}) : function(){},
            onAfterEdit : event ? (event.onAfterEdit ? event.onAfterEdit : function(){}) : function(){},
            onLoadSuccess : event ? (event.onLoadSuccess ? event.onLoadSuccess : function(){}) : function(){},
            onClickCell : event ? (event.onClickCell ? event.onClickCell : function(){}) : function(){},
            onCheck : event ? (event.onCheck ? event.onCheck : function(){}) : function(){},
            onUncheck : event ? (event.onUncheck ? event.onUncheck :
                function (rowIndex, rowData) {//取消选中时取消编辑行
                    endEditing(this, rowIndex);}) :
                function(rowIndex, rowData){//取消选中时取消编辑行
                    endEditing(this, rowIndex);},
            onDblClickRow : event ? (event.onDblClickRow ? event.onDblClickRow : function(){}) : function(){},
            onClickRow : event ? (event.onClickRow ? event.onClickRow :
                function (rowIndex, rowData) {
                    $(this).datagrid('unselectAll');
                    $(this).datagrid('selectRow', rowIndex);
                    endAllRowEdit($(this));
                }) :
                function (rowIndex, rowData) {
                    $(this).datagrid('unselectAll');
                    $(this).datagrid('selectRow', rowIndex);
                    endAllRowEdit($(this));
                }
        });
        $node.datagrid('loadData',data);
    }
    //判断列是否修改
    function checkIsUpdate(source, targer){
        if (isEmpty(source)){
            return false;
        }
        if (isEmpty(targer)){
            return false;
        }
        for (var key in source){
            if (source[key] != null && (typeof source[key] == 'object')){
                checkIsUpdate(source[key], targer[key]);
            }else if (source[key] != null && (typeof source[key] != 'object') && targer.hasOwnProperty(key)){
                if ((isEmpty(source[key]) ?  '' : source[key]) !=
                    (isEmpty(targer[key]) ?  '' : targer[key])){
                    return true;
                }
            }
        }
        return false;
    }
    //设置URL数据
    function setUrlInterfaceData(urlInterface){
    	//移除国际化控件
    	var deployUrlNameI18N = form.get().find(".deployUrlNameI18N");
    	form.get().find(".deployUrlNameI18N").html("");
        if (isEmpty(urlInterface) || isEmpty(urlInterface.i18n)){
        	//初始化国际化控件
        	if(dx.user.id!="Super" && (urlInterface.type==1 || urlInterface.type==2)){
        		international(deployUrlNameI18N, "", "","isShow");
        	}else{
        		international(deployUrlNameI18N, "", "");
        	}
            //form.get().find('.deploy_url_name input').val('');
            form.get().find('.deploy_url_url input').val('');
            form.get().find('.deploy_url_summary textarea').val('');
        }else {
        	 //初始化国际化控件
        	if(dx.user.id!="Super" && (urlInterface.type==1 || urlInterface.type==2)){
        		international(deployUrlNameI18N, urlInterface.i18n.international_id,
                		(urlInterface.i18n ? urlInterface.i18n[dx.user.language_id] : '') ? (urlInterface.i18n ? urlInterface.i18n[dx.user.language_id] : '') : urlInterface.name,"isShow");
        	}else{
        		international(deployUrlNameI18N, urlInterface.i18n.international_id,
                		(urlInterface.i18n ? urlInterface.i18n[dx.user.language_id] : '') ? (urlInterface.i18n ? urlInterface.i18n[dx.user.language_id] : '') : urlInterface.name);
        	}
            form.get().find('.deploy_url_url input').val(urlInterface.url);
            form.get().find('.deploy_url_summary textarea').val(urlInterface.summary);
        }
    }
    // 获取column 页面值
    function getColumnData(){
        var columnDatas = {};
        columnDatas.column_name = form.get().find('.deploy_column_name').find('input').val();
        columnDatas.virtual = form.get().find('.deploy_virtual').find('input').is(':checked') ? 1 : 0;
        columnDatas.tab_name = form.get().find('.deploy_tab_name').find('select').val();
        columnDatas.group_name = form.get().find('.deploy_group_name').find('select').val();
        columnDatas.data_type = form.get().find('.deploy_data_type').find('select').val();
        columnDatas.data_format = form.get().find('.deploy_data_format').find('input').val();
        columnDatas.min_len = form.get().find('.deploy_min_len').find('input').val();
        columnDatas.max_len = form.get().find('.deploy_max_len').find('input').val();
        columnDatas.is_condition = form.get().find('.deploy_is_condition').find('input').is(':checked') ? 1 : 0;
        columnDatas.dic_id = form.get().find('.deploy_dic_id').find('select').val();
        columnDatas.ref_table_name = form.get().find('.deploy_ref_table_name').find('select').val();
        columnDatas.ref_table_cols = form.get().find('.deploy_ref_table_cols').find('input').val();
        columnDatas.ref_table_sql = form.get().find('.deploy_ref_table_sql').find('textarea').val();
        columnDatas.ref_table_display = form.get().find('.deploy_ref_table_display').find('input').is(':checked') ? 1 : 0;
        //columnDatas.ref_table_filter = '';
        //columnDatas.quick_filter = '';
        columnDatas.is_multiple = form.get().find('.deploy_is_multiple').find('input').is(':checked') ? 1 : 0;
        columnDatas.formula = form.get().find('.deploy_formula').find('textarea').val();
        columnDatas.default_value = form.get().find('.deploy_default_value').find('input').val();
        columnDatas.prefix = form.get().find('.deploy_prefix').find('input').val();
        columnDatas.suffix = form.get().find('.deploy_suffix').find('input').val();
        if(form.get().find('.deploy_sum_flag').find('input').is(':checked')){
            columnDatas.sum_flag = 1;
        }else {
            delete columnDatas.sum_flag;
        }
        columnDatas.ro_insert = form.get().find('.deploy_ro_insert').find('input').is(':checked') ? 1 : 0;
        columnDatas.ro_update = form.get().find('.deploy_ro_update').find('input').is(':checked') ? 1 : 0;
        columnDatas.read_only_condition = form.get().find('.deploy_read_only_condition').find('input').val();
        columnDatas.hidden = form.get().find('.deploy_hidden').find('input').is(':checked') ? 1 : 0;
        columnDatas.wrap = form.get().find('.deploy_wrap').find('input').is(':checked') ? 1 : 0;
        columnDatas.view_style = form.get().find('.deploy_view_style').find('input').val();
        columnDatas.cell_cnt = form.get().find('.deploy_cell_cnt').find('input').val();
        //columnDatas.seq = $('.deploy_seq').find('input').val();
        //columnDatas.level = '';
        //columnDatas.memo = '';
        columnDatas.read_only_clear = form.get().find('.deploy_read_only_clear').find('input').is(':checked') ? 'true' : 'False';
        columnDatas.ref_table_new = '';
        columnDatas.ref_table_tree = form.get().find('.deploy_ref_table_tree').find('input').val();
        columnDatas.link_json = form.get().find('.deploy_link_json').find('input').val();
        //columnDatas.international_id = '';
        columnDatas.is_id_column = 0;
        //columnDatas.module = '';
        return columnDatas;
    }
    //设置列表格数据
    function setColumnGridData($node){
        var columnData = [];
        if (urlInterface){
            var columns = urlInterface.urlParam;
            var orderColumns = dataOrder(columns);
            if (!isEmpty(orderColumns))
                for (var i=0; i<orderColumns.length; i++){
                    if (urlInterface.cRUDColumns.hasOwnProperty(orderColumns[i].column_name)){
                        columnData.push({
                            seq : urlInterface.cRUDColumns[orderColumns[i].column_name].seq,
                            column_name : urlInterface.cRUDColumns[orderColumns[i].column_name].column_name,
                            column_name_I18N : urlInterface.cRUDColumns[orderColumns[i].column_name].i18n,
                            data_type : urlInterface.cRUDColumns[orderColumns[i].column_name].data_type,
                            is_id_column : urlInterface.cRUDColumns[orderColumns[i].column_name].is_id_column
                        });
                    }else{
                        columnData.push({
                            seq : orderColumns[i].seq,
                            column_name : orderColumns[i].column_name,
                            column_name_I18N : orderColumns[i].i18n,
                            data_type : orderColumns[i].data_type,
                            is_id_column : orderColumns[i].is_id_column
                        });
                    }
                }
        }
        $node.datagrid('loadData',columnData);
    }
    //设置校验规则数据
    function setCheckRuleGridData($node){
        if ($node.length != 1)
            return
        var columnData = [];
        if (urlInterface){
            var checkRules = urlInterface.urlCheck;
            dataOrder(checkRules);
            if (!isEmpty(checkRules))
                for (var i=0; i<checkRules.length; i++){
                    columnData.push({
                        table_id : checkRules[i].table_id,
                        seq : checkRules[i].seq,
                        type : checkRules[i].type,
                        column_name : checkRules[i].column_name,
                        formula : checkRules[i].formula,
                        check_level : checkRules[i].check_level,
                        error_msg_id : checkRules[i].error_msg_id,
                        msgI18n: checkRules[i].msgI18n,
                        error_msg_param : checkRules[i].error_msg_param
                    });
                }
        }
        $node.datagrid('loadData',columnData);
    }
    // 设置column 页面值
    function setColumnData(columnDatas){
        if (isEmpty(columnDatas)){
            //columnDatas.table_id = table ? table.id : '';
            form.get().find('.deploy_column_name').find('input').val('');
            form.get().find('.deploy_virtual').find('input').prop("checked",false);
            form.get().find('.deploy_tab_name').find('select').val('');
            form.get().find('.deploy_group_name').find('select').val('');
            form.get().find('.deploy_data_type').find('select').val('');
            form.get().find('.deploy_data_format').find('input').val('');
            form.get().find('.deploy_min_len').find('input').val('');
            form.get().find('.deploy_max_len').find('input').val('');
            form.get().find('.deploy_is_condition').find('input').prop("checked",false);
            form.get().find('.deploy_dic_id').find('select').val('');
            form.get().find('.deploy_ref_table_name').find('select').val('');
            form.get().find('.deploy_ref_table_cols').find('input').val('');
            form.get().find('.deploy_ref_table_sql').find('textarea').val('');
            form.get().find('.deploy_ref_table_display').find('input').prop("checked",false);
            //columnDatas.ref_table_filter = '';
            //columnDatas.quick_filter = '';
            form.get().find('.deploy_is_multiple').find('input').prop("checked",false);
            form.get().find('.deploy_formula').find('textarea').val('');
            form.get().find('.deploy_default_value').find('input').val('');
            form.get().find('.deploy_prefix').find('input').val('');
            form.get().find('.deploy_suffix').find('input').val('');
            form.get().find('.deploy_sum_flag').find('input').prop("checked",false);
            form.get().find('.deploy_ro_insert').find('input').prop("checked",false);
            form.get().find('.deploy_ro_update').find('input').prop("checked",false);
            form.get().find('.deploy_read_only_condition').find('input').val('');
            form.get().find('.deploy_hidden').find('input').prop("checked",false);
            form.get().find('.deploy_wrap').find('input').prop("checked",false);
            form.get().find('.deploy_view_style').find('input').val('');
            form.get().find('.deploy_cell_cnt').find('input').val(3);
            //$('.deploy_seq').find('input').val('');
            //columnDatas.level = '';
            //columnDatas.memo = '';
            form.get().find('.deploy_read_only_clear').find('input').prop("checked",false);
            //columnDatas.ref_table_new = '';
            form.get().find('.deploy_ref_table_tree').find('input').val('');
            form.get().find('.deploy_link_json').find('input').val('');
            //columnDatas.international_id = '';
            //columnDatas.is_id_column = 0;
            //columnDatas.module = '';
        }else{
            //columnDatas.table_id = table ? table.id : '';
            form.get().find('.deploy_column_name').find('input').val(columnDatas.column_name);
            if (isEmpty(columnDatas.virtual) || columnDatas.virtual == 0)
                form.get().find('.deploy_virtual').find('input').prop("checked",false);
            else
                form.get().find('.deploy_virtual').find('input').prop("checked",true);
            form.get().find('.deploy_tab_name').find('select').val(columnDatas.tab_name);
            form.get().find('.deploy_group_name').find('select').val(columnDatas.group_name);
            form.get().find('.deploy_data_type').find('select').val(columnDatas.data_type);
            form.get().find('.deploy_data_format').find('input').val(columnDatas.data_format);
            form.get().find('.deploy_min_len').find('input').val(columnDatas.min_len);
            form.get().find('.deploy_max_len').find('input').val(columnDatas.max_len ? columnDatas.max_len : 20);
            if (isEmpty(columnDatas.is_condition) || columnDatas.is_condition == 0)
                form.get().find('.deploy_is_condition').find('input').prop("checked",false);
            else
                form.get().find('.deploy_is_condition').find('input').prop("checked",true);
            form.get().find('.deploy_dic_id').find('select').val(columnDatas.dic_id);
            form.get().find('.deploy_ref_table_name').find('select').val(columnDatas.ref_table_name);
            form.get().find('.deploy_ref_table_cols').find('input').val(columnDatas.ref_table_cols);
            form.get().find('.deploy_ref_table_sql').find('textarea').val(columnDatas.ref_table_sql);
            if (isEmpty(columnDatas.ref_table_display) || columnDatas.ref_table_display == 0)
                form.get().find('.deploy_ref_table_display').find('input').prop("checked",false);
            else
                form.get().find('.deploy_ref_table_display').find('input').prop("checked",true);
            //columnDatas.ref_table_filter = '';
            //columnDatas.quick_filter = '';
            if (isEmpty(columnDatas.is_multiple) || columnDatas.is_multiple == 0)
                form.get().find('.deploy_is_multiple').find('input').prop("checked",false);
            else
                form.get().find('.deploy_is_multiple').find('input').prop("checked",true);
            form.get().find('.deploy_formula').find('textarea').val(columnDatas.formula);
            form.get().find('.deploy_default_value').find('input').val(columnDatas.default_value);
            form.get().find('.deploy_prefix').find('input').val(columnDatas.prefix);
            form.get().find('.deploy_suffix').find('input').val(columnDatas.suffix);
            if (isEmpty(columnDatas.sum_flag) || columnDatas.sum_flag == 0)
                form.get().find('.deploy_sum_flag').find('input').prop("checked",false);
            else
                form.get().find('.deploy_sum_flag').find('input').prop("checked",true);
            if (isEmpty(columnDatas.ro_insert) || columnDatas.ro_insert == 0)
                form.get().find('.deploy_ro_insert').find('input').prop("checked",false);
            else
                form.get().find('.deploy_ro_insert').find('input').prop("checked",true);
            if (isEmpty(columnDatas.ro_update) || columnDatas.ro_update == 0)
                form.get().find('.deploy_ro_update').find('input').prop("checked",false);
            else
                form.get().find('.deploy_ro_update').find('input').prop("checked",true);
            form.get().find('.deploy_read_only_condition').find('input').val(columnDatas.read_only_condition);
            if (isEmpty(columnDatas.hidden) || columnDatas.hidden == 0)
                form.get().find('.deploy_hidden').find('input').prop("checked",false);
            else
                form.get().find('.deploy_hidden').find('input').prop("checked",true);
            if (isEmpty(columnDatas.wrap) || columnDatas.wrap == 0)
                form.get().find('.deploy_wrap').find('input').prop("checked",false);
            else
                form.get().find('.deploy_wrap').find('input').prop("checked",true);
            form.get().find('.deploy_view_style').find('input').val(columnDatas.view_style);
            form.get().find('.deploy_cell_cnt').find('input').val(columnDatas.cell_cnt ? columnDatas.cell_cnt : 3);
            //$('.deploy_seq').find('input').val(columnDatas.seq);
            //columnDatas.level = '';
            //columnDatas.memo = '';
            if (isEmpty(columnDatas.read_only_clear) || columnDatas.read_only_clear == 'False')
                form.get().find('.deploy_read_only_clear').find('input').prop("checked",false);
            else
                form.get().find('.deploy_read_only_clear').find('input').prop("checked",true);
            //columnDatas.ref_table_new = '';
            form.get().find('.deploy_ref_table_tree').find('input').val(columnDatas.ref_table_tree);
            form.get().find('.deploy_link_json').find('input').val(columnDatas.link_json);
            //columnDatas.international_id = '';
            //columnDatas.is_id_column = 0;
            //columnDatas.module = '';
        }
    }
    //修改URL数据
    function changeUrlInterfaceData(){
        if (isEmpty(urlInterface))
            return;
        if (isEmpty(urlInterface.i18n)){
            urlInterface.i18n = {};
        }
        var getInternational = form.get().find(".deployUrlNameI18N").internationalControl(true).getData();
        if (!isEmpty(getInternational.international_id)) {
        	urlInterface.i18n=getInternational;
        	urlInterface.name=getInternational.international_id;
        }else{
        	urlInterface.i18n={};
        	urlInterface.i18n[dx.user.language_id]=getInternational.interValue;
        	urlInterface.i18n.international_id="";
        	urlInterface.name="";
        }
        //urlInterface.i18n[dx.user.language_id] = form.get().find('.deploy_url_name input').val();
        urlInterface.url = form.get().find('.deploy_url_url input').val();
        urlInterface.summary = form.get().find('.deploy_url_summary textarea').val();
    }
    //修改列数据
    function changeColumnGridData(rowData, rowIndex){
        function cloneI18N(i18n){
            if (isEmpty(i18n))
                return {};
            else{
                var newI18N = {};
                for (var key in i18n){
                    newI18N[key] = i18n[key];
                }
                return newI18N;
            }
        }
        if (isEmpty(urlInterface))
            return;
        if (!isEmpty(rowData)){
            if (isEmpty(rowData.column_name))
                return;
            if (isEmpty(urlInterface.cRUDColumns)){
                urlInterface.cRUDColumns = {};
            }
            if (isEmpty(urlInterface.urlParam)){
                urlInterface.urlParam = {};
            }
            var columnData = getColumnData();
            columnData.column_name = rowData.column_name;
            columnData.i18n = cloneI18N(urlInterface.urlParam[rowData.column_name] ? urlInterface.urlParam[rowData.column_name].i18n : {});
            columnData.i18n = rowData.column_name_I18N;
            columnData.data_type = rowData.data_type;
            columnData.is_id_column = rowData.is_id_column;
            columnData.url_id = urlInterface.id;
            columnData.seq = rowIndex;
            //默认字段长度
            if (isEmpty(columnData.max_len)){
                columnData.max_len == 20;
            }
            columnData.international_id = urlInterface.urlParam[rowData.column_name] ? urlInterface.urlParam[rowData.column_name].international_id : '';
            if (!isEmpty(urlInterface.cRUDColumns[columnData.column_name])){
                columnData.columnChange = urlInterface.cRUDColumns[columnData.column_name].columnChange;
            }
            urlInterface.cRUDColumns[columnData.column_name] = columnData;

            if (!isEmpty(urlInterface.urlParam[columnData.column_name]) &&
                urlInterface.urlParam[columnData.column_name].columnChange != 2)
                if (checkIsUpdate(urlInterface.urlParam[rowData.column_name], columnData)){
                    urlInterface.cRUDColumns[columnData.column_name].columnChange = 1;
                }

            if (!urlInterface.urlParam.hasOwnProperty(rowData.column_name)){
                urlInterface.cRUDColumns[columnData.column_name].columnChange = 2;
                urlInterface.urlParam[columnData.column_name] = urlInterface.cRUDColumns[columnData.column_name];
            }else{
            	urlInterface.cRUDColumns[columnData.column_name].columnChange = 1;
            	urlInterface.urlParam[columnData.column_name] = urlInterface.cRUDColumns[columnData.column_name];
            }
        }

    }
    //修改校验规则数据
    function changeCheckRuleGridData($node){
        if (isEmpty(urlInterface))
            return;
        var updateRows = $node.datagrid('getChanges');
        if (!isEmpty(updateRows)){
            var rows = $node.datagrid('getRows');
            urlInterface.urlCheck = rows;
        }
    }
    //结束所有行编辑
    function endAllRowEdit($node){
        var rows = $node.datagrid('getRows');
        if (isEmpty(rows))
            return;
        for (var i=0; i<rows.length; i++){
            $($node).datagrid('endEdit', $($node).datagrid('getRowIndex', rows[i]));
        }
    }
    //保存当前tab页的数据。切换tab页。以及点击保存按钮时
    function saveTabData(){
        var tabs = form.get().find('.deploy-grid-title li');
        for (var i=0; i<tabs.length; i++){
            if ($(tabs[i]).hasClass('active')){
                var tabType = $(tabs[i]).find('a').attr('class');
                switch (tabType){
                    case 'deploy-column' :
                        endAllRowEdit(form.get().find('.deploy-url-table-grid .deploy-column'));
                        var dataGrid = form.get().find('.deploy-url-table-grid .deploy-column');
                        var rowData=dataGrid.datagrid("getSelected");
                        var rowIndex=dataGrid.datagrid("getRowIndex",rowData);
                        if(rowIndex >=0){
                        	changeColumnGridData(rowData, rowIndex);
                        }
                        return;
                    case 'deploy-rule' :
                        endAllRowEdit(form.get().find('.deploy-url-table-grid .deploy-rule'));
                        changeCheckRuleGridData(form.get().find('.deploy-url-table-grid .deploy-rule'));
                        return;
                    case 'deploy-prod' :
                        changeProdCode(prodSqlEdit);
                        return;
                }
            }
        }
    }
    //结束编辑行
    function endEditing(node, index) {
        $(node).datagrid('endEdit', index);
    }
    // 初始化列表格
    function initColumnGrid(){
    	//表格文本编辑器
        $.extend($.fn.datagrid.defaults.editors, {    
        	interColumn: {    
                init: function(container, options){    
                    var input = $('<div class="column_name_I18N"></div>').appendTo(container);    
                    return input;    
                },     
                getValue: function(target){    
                	var getInternational=form.get().find(".column_name_I18N").internationalControl(true).getData();
                	var index=dataGrid.datagrid('getRowIndex',getInterRow);
                	var row=dataGrid.datagrid('getRows')[index];
                	if(isEmpty(getInternational.international_id)){
                		row.column_name_I18N={};
                		row.column_name_I18N[dx.user.language_id]=getInternational.interValue;
                		row.column_name_I18N.international_id=""
                		var columns = urlInterface.urlParam;
        	            if (!isEmpty(columns)){
        	            	if(!isEmpty(columns[row.column_name])){
        	            		var column=columns[row.column_name].i18n;
        	            		if(column.columnChange==0 || isEmpty(column.columnChange)){
    	                			column.columnChange==1;
    	                		}
        	            	}
        	            }
                	}else{
            			row.column_name_I18N=getInternational;
            			var columns = urlInterface.urlParam;
        	            if (!isEmpty(columns)){
        	            	if(!isEmpty(columns[row.column_name])){
        	            		columns[row.column_name].i18n=getInternational;
        	            		var column=columns[row.column_name].i18n;
    	                		var flag=false;
    	                		if(isEmpty(column.international_id) || column.international_id != getInternational.international_id){
    	                			flag=true;
    	                		}else if(!flag || column.cn != getInternational.cn){
    	                			flag=true;
    	                		}else if(!flag || column.en != getInternational.en){
    	                			flag=true;
    	                		}else if(!flag || column.jp != getInternational.jp){
    	                			flag=true;
    	                		}else if(!flag || column.other1 != getInternational.other1){
    	                			flag=true;
    	                		}else if(!flag || column.other2 != getInternational.other2){
    	                			flag=true;
    	                		}
    	                		if(flag){
    	                			if(column.columnChange==0 || isEmpty(column.columnChange)){
        	                			column.columnChange==1;
        	                		}
    	                		}
        	            	}
        	            }
            		}
                	return row.column_name_I18N;
                },    
                setValue: function(target, value){
                	getInterRow=dataGrid.datagrid('getSelected');
                	if(isEmpty(getInterRow)){
                		international(form.get().find(".column_name_I18N"),"","");
                	}else{
                		if(isEmpty(getInterRow.column_name_I18N.international_id)){
                    		international(form.get().find(".column_name_I18N"),"",value[dx.user.language_id]);
                    	}else{
                    		international(form.get().find(".column_name_I18N"),getInterRow.column_name_I18N.international_id,value[dx.user.language_id]);
                    	}
                	}
                } 
            }    
        });  
        var dataGrid = form.get().find('.deploy-url-table-grid .deploy-column');
        //字段类型字典
        var optionsData = gridDiceSelect('d_datatype');
        var columnColumns = [[
            {field:'checkbox',title:'复选框',width:100, checkbox : true},
            {field:'seq',title:'顺序',width:100,hidden : true},
            {field:'column_name',title:'字段名',width:100, editor:{type:'text'}},
            {field:'data_type',title:'类型',width:100,
                formatter: function(value,row,index){
                    var options = this.editor.options.data;
                    for (var i=0; i<options.length; i++){
                        if (options[i].key == value){
                            return options[i].productname
                        }
                    }
                },
                editor:{
                    type:'combobox',
                    options:{
                        valueField:'key',
                        textField:'productname',
                        data : optionsData
                    }
                }
            },
            {field: 'column_name_I18N', title: '显示名', width: 100, editor: {type:'interColumn'},
	           	 formatter: function(value,row,index){
	        		 if(!isEmpty(value)){
	        			 return row.column_name_I18N[dx.user.language_id];
	        		 }
	        	 }
	         }
        ]];
        var columnData = [];
        if (urlInterface){
            var columns = urlInterface.urlParam;
            var orderColumns = dataOrder(columns);
            if (!isEmpty(orderColumns))
                for (var i=0; i<orderColumns.length; i++){
                    columnData.push({
                        seq : orderColumns[i].seq,
                        column_name : orderColumns[i].column_name,
                        column_name_I18N : orderColumns[i].i18n,
                        data_type : orderColumns[i].data_type,
                        is_id_column : orderColumns[i].is_id_column
                    });
                }
        }
        initDataGrid(dataGrid, columnColumns, columnData, {
            onClickRow : function (rowIndex, rowData) {
                if (checkisReadOnly(urlInterface))
                	$(this).datagrid('unselectAll');
                	$(this).datagrid('selectRow', rowIndex);
                    return;
                endAllRowEdit($(this));
                //点击一行时。先保存上一行的数据。
                var rows = $(this).datagrid('getSelections');
                if (!isEmpty(rows) && rows.length == 2){
                    for (var i=0; i<rows.length; i++){
                        var oldRowIndex = $(this).datagrid('getRowIndex', rows[i]);
                        if (oldRowIndex != rowIndex){
                            changeColumnGridData(rows[i], oldRowIndex);
                        }
                    }
                }
                $(this).datagrid('unselectAll');
                $(this).datagrid('selectRow', rowIndex);
                //给列右侧赋值
                if (!isEmpty(rowData)) {
                    if (!isEmpty(rowData.column_name)) {
                        var columnData;
                        if (!isEmpty(urlInterface.cRUDColumns) && !isEmpty(urlInterface.cRUDColumns[rowData.column_name]))
                            columnData = urlInterface.cRUDColumns[rowData.column_name];
                        else
                            columnData = urlInterface.urlParam[rowData.column_name];
                        setColumnData(columnData);
                    }
                }
            },
            onCheck : function (rowIndex, rowData) {
                //if (!isEmpty(rowData)){
                //    if (!isEmpty(rowData.column_name)){
                //        var columnData;
                //        if (!isEmpty(table.cRUDColumns) && !isEmpty(table.cRUDColumns[rowData.column_name]))
                //            columnData = table.cRUDColumns[rowData.column_name];
                //        else
                //            columnData = table.columnMap[rowData.column_name];
                //        setColumnData(columnData);
                //    }
                //}
            },
            onUncheck : function (rowIndex, rowData) {
                //取消选中时取消编辑行
                endEditing(this, rowIndex);
            },
            onDblClickRow : function(rowIndex, rowData){
                if (checkisReadOnly(urlInterface))
                    return;
                var columnData;
                if (!isEmpty(urlInterface.cRUDColumns) && !isEmpty(urlInterface.cRUDColumns[rowData.column_name]))
                    columnData = urlInterface.cRUDColumns[rowData.column_name];
                else
                    columnData = urlInterface.urlParam[rowData.column_name];
                setColumnData(columnData);
            },
            onAfterEdit : function(rowIndex, rowData, changes){
                if (checkisReadOnly(urlInterface))
                    return;
                if (isEmpty(urlInterface)){
                    return;
                }
                //结束编辑时。若列名无值。取右侧列名
                if (isEmpty(rowData))
                    rowData = {};
                if (isEmpty(rowData.column_name))
                    rowData.column_name = form.get().find('.deploy_column_name input').val();
                if (isEmpty(rowData.data_type))
                    rowData.data_type = form.get().find('.deploy_data_type select').val();
                //结束编辑检查是否修改了列名。
                var urlParam = urlInterface.urlParam;
                for (var key in urlParam){
                    if (urlParam[key].seq == rowIndex &&
                        urlParam[key].column_name != rowData.column_name){
                        //修改了列名删除原先列。
                        if (isEmpty(urlInterface.deleteColumns))
                            urlInterface.deleteColumns = [];
                        urlInterface.deleteColumns.push(urlInterface.urlParam[key]);
                        delete urlInterface.urlParam[key];
                        if (!isEmpty(urlInterface.cRUDColumns))
                            delete urlInterface.cRUDColumns[key];
                        break;
                    }
                }
                changeColumnGridData(rowData, rowIndex);

                setColumnGridData($(this));
            },
            onLoadSuccess : function (data) {
                if (checkisReadOnly(urlInterface))
                    return;
                //启用dnd支持
                //$(this).datagrid('enableDnd');
                //可选-绑定dnd的触发事件
                $(this).datagrid({
                    onDrop:function(targetRow, sourceRow, point) {
                        //拖拽某行到指定位置后触发
                        var rows = $(this).datagrid('getRows');
                        if (!isEmpty(rows) && rows.length > 0)
                            for (var i=0; i<rows.length; i++){
                                if (urlInterface.urlParam.hasOwnProperty(rows[i].column_name)){
                                    urlInterface.urlParam[rows[i].column_name].seq = i;
                                    if (urlInterface.urlParam[rows[i].column_name].columnChange != 2)
                                        urlInterface.urlParam[rows[i].column_name].columnChange = 1;
                                }
                            }
                    }
                })
            },
            onDblClickCell : function(index, field){
                if (checkisReadOnly(urlInterface))
                    return;
                var $that = $(this);
                $that.datagrid('unselectAll');
                endAllRowEdit($that);
                $that.datagrid('selectRow', index).datagrid('beginEdit', index);
                var ed = $that.datagrid('getEditor', {index: index, field: field});
                if (ed) {
                    ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
                }
            }
        }, true);
        dataGrid.datagrid("selectRow",'0');
        var rows=dataGrid.datagrid("getRows");
        if (!isEmpty(rows)) {
            if (!isEmpty(rows[0].column_name)) {
                var columnData;
                if (!isEmpty(urlInterface.cRUDColumns) && !isEmpty(urlInterface.cRUDColumns[rows[0].column_name]))
                    columnData = urlInterface.cRUDColumns[rows[0].column_name];
                else
                    columnData = urlInterface.urlParam[rows[0].column_name];
                setColumnData(columnData);
                isReadOnly(false,"tableRowIsNull")
            }
        }else{
        	setColumnData();
        	isReadOnly(true,"tableRowIsNull")
        }
    }
    //初始化校验规则表格
    function initCheckRuleGrid(){
    	//表格文本编辑器
        $.extend($.fn.datagrid.defaults.editors, {    
        	interErrorMsg: {    
                init: function(container, options){    
                    var input = $('<div class="error_msg_I18N"></div>').appendTo(container);    
                    return input;    
                },     
                getValue: function(target){    
                	var getInternational=form.get().find(".error_msg_I18N").internationalControl(true).getData();
                	var index=dataGrid.datagrid('getRowIndex',getInterRow);
                	var row=dataGrid.datagrid('getRows')[index];
                	if(isEmpty(getInternational.international_id)){
                		row.msgI18n={};
                		row.msgI18n[dx.user.language_id]=getInternational.interValue;
                		row.error_msg_id=""
                	}else{
            			row.msgI18n=getInternational;
            			row.error_msg_id=getInternational.international_id;
            		}
                	return row.msgI18n;
                },    
                setValue: function(target, value){
                	getInterRow=dataGrid.datagrid('getSelected');
                	if(isEmpty(getInterRow)){
                		international(form.get().find(".error_msg_I18N"),"","");
                	}else{
                		if(isEmpty(getInterRow.error_msg_id)){
                    		international(form.get().find(".error_msg_I18N"),"",getInterRow.msgI18n[dx.user.language_id]);
                    	}else{
                    		international(form.get().find(".error_msg_I18N"),getInterRow.error_msg_id,getInterRow.msgI18n[dx.user.language_id]);
                    	}
                	}
                } 
            }    
        });  
        var dataGrid = form.get().find('.deploy-url-table-grid .deploy-rule');
        //字段字典。传入了table 则取 table中的字段。没传入table 则取前一个页面的字段
        var columnOptionsData = getColumnOptionsData(urlInterface);
        //输入时机字典
        var checkTypeOptionsData = gridDiceSelect('checktype');
        //校验级别字典
        var checkLevelOptionsData = gridDiceSelect('check_level');
        var checkRuleColumns = [[
            {field:'checkbox',title:'复选框',width:100, checkbox : true},
            {field:'table_id',title:'表名',width:100,  hidden : true},
            {field:'seq',title:'顺序', width:100, hidden : true},
            {field:'type',title:'输入时机',width:100,
                formatter: function (value, row, index) {
                    var options = this.editor.options.data;
                    for (var i = 0; i < options.length; i++) {
                        if (options[i].key == value) {
                            return options[i].productname
                        }
                    }
                },
                editor: {
                    type: 'combobox',
                    options: {
                        valueField: 'key',
                        textField: 'productname',
                        data: checkTypeOptionsData
                    }
                }
            },
            {field:'column_name',title:'字段',width:100,
                formatter: function (value, row, index) {
                    var options = this.editor.options.data;
                    for (var i = 0; i < options.length; i++) {
                        if (options[i].column_name == value) {
                            return options[i].productname
                        }
                    }
                },
                editor: {
                    type: 'combobox',
                    options: {
                        valueField: 'column_name',
                        textField: 'productname',
                        data: columnOptionsData
                    }
                }
            },
            {field:'formula',title:'校验条件',width:100,editor:{type:'text'}},
            {field:'check_level',title:'校验级别',width:100,
                formatter: function (value, row, index) {
                    var options = this.editor.options.data;
                    for (var i = 0; i < options.length; i++) {
                        if (options[i].key == value) {
                            return options[i].productname
                        }
                    }
                },
                editor: {
                    type: 'combobox',
                    options: {
                        valueField: 'key',
                        textField: 'productname',
                        data: checkLevelOptionsData
                    }
                }
            },
            {field: 'error_msg_id', title: '违反提示id', width: 100, hidden: true},
            {field: 'msgI18n', title: '违反提示(公式不成立时)', width: 100, editor: {type:'interErrorMsg'},
	           	 formatter: function(value,row,index){
	        		 if(!isEmpty(value)){
	        			 return row.msgI18n[dx.user.language_id];
	        		 }
	           	 }
            },
            {field:'error_msg_param',title:'信息参数', width:100, editor : {type: 'text'}}
        ]];

        var columnData = [];
        if (urlInterface){
            var checkRules = urlInterface.urlCheck;
            if (!isEmpty(checkRules))
                for (var i=0; i<checkRules.length; i++){
                    columnData.push({
                        table_id : checkRules[i].table_id,
                        seq : checkRules[i].seq,
                        type : checkRules[i].type,
                        column_name : checkRules[i].column_name,
                        formula : checkRules[i].formula,
                        check_level : checkRules[i].check_level,
                        error_msg_id : checkRules[i].error_msg_id,
                        msgI18n: checkRules[i].msgI18n,
                        error_msg_param : checkRules[i].error_msg_param
                    });
                }
        }
        initDataGrid(dataGrid, checkRuleColumns, columnData, {
        	onDblClickCell : function(index, field){
                if (checkisReadOnly(urlInterface))
                    return;
                var $that = $(this);
                $that.datagrid('unselectAll');
                endAllRowEdit($that);
                $that.datagrid('selectRow', index).datagrid('beginEdit', index);
                var ed = $that.datagrid('getEditor', {index: index, field: field});
                if (ed) {
                    ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
                }
            },
            onUncheck : function (rowIndex, rowData) {
                if (checkisReadOnly(urlInterface))
                    return;
                //取消选中时取消编辑行
                endEditing(this, rowIndex);
            },
            onAfterEdit : function(rowIndex, rowData, changes){
                if (checkisReadOnly(urlInterface))
                    return;
                if (isEmpty(urlInterface)){
                    return;
                }
                changeCheckRuleGridData($(this));
                setCheckRuleGridData($(this));
            },
            onLoadSuccess : function (data) {
                if (checkisReadOnly(urlInterface))
                    return;
                //启用dnd支持
                $(this).datagrid('enableDnd');
                //可选-绑定dnd的触发事件
                $(this).datagrid({
                    onDrop:function(targetRow, sourceRow, point) {
                        //拖拽某行到指定位置后触发
                        var rows = $(this).datagrid('getRows');
                        var checkRules = urlInterface.urlCheck;
                        if (!isEmpty(rows) && rows.length > 0 && !isEmpty(checkRules) && checkRules.length > 0)
                            for (var i=0; i<rows.length; i++){
                                for (var j=0; j<checkRules.length; j++){
                                    if (rows[i].column_name == checkRules[j].column_name){
                                        checkRules[j].seq = i;
                                    }
                                }
                            }
                    }
                })
            }
        }, false);
    }
    //新增行
    function addGridRow($node, row){
        if (checkisReadOnly(urlInterface))
            return;
        if (isEmpty(row)){
            endAllRowEdit($node);
            $($node).datagrid('appendRow', {});
            var rows = $node.datagrid('getRows');
            var lastIndex = rows.length - 1;
            $($node).datagrid('unselectAll');
            $($node).datagrid('selectRow', lastIndex);
            $($node).datagrid('beginEdit', lastIndex);
            //如果为列新增。清空右侧值。
            if ($($node).hasClass('deploy-column')){
                setColumnData();
            }
        }
    }
    function changeUrlStart(urlInterface){
        if (isEmpty(urlInterface)){
            form.get().find('.deploy_url_url').hide();
            return;
        }
        switch (urlInterface.type){
            case 1 :
                form.get().find('.deploy_url_url').hide();
                break;
            case 2 :
                //form.get().find('.deploy_url_url').hide();
            	form.get().find('.deploy_url_url').show();
                break;
            case 3 :
                form.get().find('.deploy_url_url .name').text(msg('prod name'));
                form.get().find('.deploy_url_url').show();
                break;
            case 4 :
                form.get().find('.deploy_url_url .name').text(msg('url'));
                form.get().find('.deploy_url_url').show();
                break;
            case 5 :
                form.get().find('.deploy_url_url .name').text(msg('url_tree_out_url'));
                form.get().find('.deploy_url_url').show();
                break;
            default :
                form.get().find('.deploy_url_url').hide();
                break;
        }
    }
    function checkisReadOnly(urlInterface){
    	 if(dx.user.id=="Super"){
    		 return false;
    	 }
        if (isEmpty(urlInterface)){
            isReadOnly(true);
            return true;
        }else{
            switch (urlInterface.type){
                case 1 :
                    isReadOnly(true);
                    return true;
                case 2 :
                    isReadOnly(true);
                    return true;
                case 3 :
                    isReadOnly(false);
                    return false;
                case 4 :
                    isReadOnly(false);
                    return false;
                case 5 :
                    isReadOnly(false);
                    return false;
                default :
                    isReadOnly(true);
                    return true;
            }
        }
    }
    //只读状态
    function isReadOnly(flag,tableRowIsNull){
        if (flag){
        	if(isEmpty(tableRowIsNull)){
        		form.get().find('.deploy_url_name input').attr('readonly', 'readonly');
                form.get().find('.deploy_url_summary textarea').attr('readonly', 'readonly');
                form.get().find('.deploy_url_url input').attr('readonly', 'readonly');
        	}
            form.get().find('.deploy_column_name input').attr('readonly', 'readonly');
            form.get().find('.deploy_virtual input').attr('disabled', 'disabled');
            form.get().find('.deploy_is_condition input').attr('disabled', 'disabled');
            form.get().find('.deploy_name_expression input').attr('disabled', 'disabled');
            form.get().find('.deploy_tab_name select').attr("disabled",true);
            form.get().find('.deploy_group_name select').attr("disabled",true);
            form.get().find('.deploy_data_type select').attr("disabled",true);
            form.get().find('.deploy_data_format input').attr('readonly', 'readonly');
            form.get().find('.deploy_min_len input').attr('readonly', 'readonly');
            form.get().find('.deploy_max_len input').attr('readonly', 'readonly');
            form.get().find('.deploy_ref_table_display input').attr('disabled', 'disabled');
            form.get().find('.deploy_is_multiple input').attr('disabled', 'disabled');
            form.get().find('.deploy_dic_id select').attr("disabled",true);
            form.get().find('.deploy_ref_table_name select').attr("disabled",true);
            form.get().find('.deploy_ref_table_cols input').attr('readonly', 'readonly');
            form.get().find('.deploy_ref_table_sql textarea').attr('readonly', 'readonly');
            form.get().find('.deploy_sum_flag input').attr('disabled', 'disabled');
            form.get().find('.deploy_ro_insert input').attr('disabled', 'disabled');
            form.get().find('.deploy_ro_update input').attr('disabled', 'disabled');
            form.get().find('.deploy_read_only_clear input').attr('disabled', 'disabled');
            form.get().find('.deploy_default_value input').attr('readonly', 'readonly');
            form.get().find('.deploy_prefix input').attr('readonly', 'readonly');
            form.get().find('.deploy_suffix input').attr('readonly', 'readonly');
            form.get().find('.deploy_read_only_condition input').attr('readonly', 'readonly');
            form.get().find('.deploy_formula textarea').attr('readonly', 'readonly');
            form.get().find('.deploy_hidden input').attr('disabled', 'disabled');
            form.get().find('.deploy_wrap input').attr('disabled', 'disabled');
            form.get().find('.deploy_cell_cnt input').attr('readonly', 'readonly');
            form.get().find('.deploy_ textarea').attr('readonly', 'readonly');
            form.get().find('.deploy_ref_table_tree input').attr('readonly', 'readonly');
            form.get().find('.deploy_link_json input').attr('readonly', 'readonly');
            form.get().find('.deploy_ input').attr('readonly', 'readonly');
        }else{
        	if(isEmpty(tableRowIsNull)){
        		form.get().find('.deploy_url_name input').removeAttr('readonly');
                form.get().find('.deploy_url_summary textarea').removeAttr('readonly');
                form.get().find('.deploy_url_url input').removeAttr('readonly');
        	}
            form.get().find('.deploy_column_name input').removeAttr('readonly');
            form.get().find('.deploy_virtual input').removeAttr('disabled');
            form.get().find('.deploy_is_condition input').removeAttr('disabled');
            form.get().find('.deploy_name_expression input').removeAttr('disabled');
            form.get().find('.deploy_tab_name select').attr("disabled",false);
            form.get().find('.deploy_group_name select').attr("disabled",false);
            form.get().find('.deploy_data_type select').attr("disabled",false);
            form.get().find('.deploy_data_format input').removeAttr('readonly');
            form.get().find('.deploy_min_len input').removeAttr('readonly');
            form.get().find('.deploy_max_len input').removeAttr('readonly');
            form.get().find('.deploy_ref_table_display input').removeAttr('disabled');
            form.get().find('.deploy_is_multiple input').removeAttr('disabled');
            form.get().find('.deploy_dic_id select').attr("disabled",false);
            form.get().find('.deploy_ref_table_name select').attr("disabled",false);
            form.get().find('.deploy_ref_table_cols input').removeAttr('readonly');
            form.get().find('.deploy_ref_table_sql textarea').removeAttr('readonly');
            form.get().find('.deploy_sum_flag input').removeAttr('disabled');
            form.get().find('.deploy_ro_insert input').removeAttr('disabled');
            form.get().find('.deploy_ro_update input').removeAttr('disabled');
            form.get().find('.deploy_read_only_clear input').removeAttr('disabled');
            form.get().find('.deploy_default_value input').removeAttr('readonly');
            form.get().find('.deploy_prefix input').removeAttr('readonly');
            form.get().find('.deploy_suffix input').removeAttr('readonly');
            form.get().find('.deploy_read_only_condition input').removeAttr('readonly');
            form.get().find('.deploy_formula textarea').removeAttr('readonly');
            form.get().find('.deploy_hidden input').removeAttr('disabled');
            form.get().find('.deploy_wrap input').removeAttr('disabled');
            form.get().find('.deploy_cell_cnt input').removeAttr('readonly');
            form.get().find('.deploy_ textarea').removeAttr('readonly');
            form.get().find('.deploy_ref_table_tree input').removeAttr('readonly');
            form.get().find('.deploy_link_json input').removeAttr('readonly');
            form.get().find('.deploy_ input').removeAttr('readonly');
        }
    }
    //删除行
    function deleteGridRows($node, index){
        $($node).datagrid('deleteRow', index);
    }


    //初始化代码编辑器
    function initProdCode(urlInterface){
        function buildCodeHtml(){
            // function buildProdParamHtml(){
                if (isEmpty(urlInterface.urlParam))
                    return "";
                var html;
                //UUID
                html = '<div class="prod-column">' +
                    '<span class="column-name">UUID</span>'+
                    '<div><span class="column-label">' + msg('uuid') + '</span></div></div>';
                var orderColumns = dataOrder(urlInterface.urlParam);
                for (var i=0; i<orderColumns.length; i++){
                    html += '<div class="prod-column">' +
                    '<span class="column-name">' + orderColumns[i].column_name + '</span>' +
                    '<div><span class="column-label">' + orderColumns[i].i18n[dx.user.language_id] + '</span></div></div>';
                }
                return html;
            // }
            // var Html;
            // Html =
                // buildProdParamHtml() +
                // '<div class="prod-sql">' +
                // '<textarea class="code" name="code" rows="5" >' +
                // (urlInterface.prodSql ? urlInterface.prodSql : '')
                // + '</textarea>' +
                // '</div>';
            // return Html;
        }
        if (isEmpty(urlInterface))
            return;
        form.get().find('.prod-sql').empty();
        form.get().find('.prod-sql').append('<textarea class="code" name="code" rows="5" ></textarea>');
        form.get().find('.prod-param').html(buildCodeHtml());
        $(".prod-sql textarea").html(urlInterface.prodSql ? urlInterface.prodSql : '');
        // form.get().find('.url-prod-code').append(buildCodeHtml());
        //加载代码编辑器
        CodeMirror.keywords = "server software ";
        CodeMirror.tableKeywords = "server.ip server.cache software.conf software.version software.tags.count ";
        var prodCodeTextArea = form.get().find('.url-prod-code .code');
        var editor = CodeMirror.fromTextArea(prodCodeTextArea[0], {
            lineNumbers: false,
            extraKeys: {
                "Ctrl": "autocomplete"
            }, //输入s然后ctrl就可以弹出选择项
            mode: {
                name: "text/x-mysql"
            }
            //theme: "3024-day" //主题
        });
        editor.on('change', function() {
            editor.showHint(); //满足自动触发自动联想功能
        });
        //获取代码编辑器的值
        prodSqlEdit = editor;
    }
    //获取编辑器值
    function changeProdCode(prodSqlEdit){
        if (isEmpty(urlInterface) || isEmpty(prodSqlEdit))
            return;
        var sql = prodSqlEdit.getValue();
        if (urlInterface.prodSql != sql)
            urlInterface.sqlChange = 1;
        urlInterface.prodSql = sql;
    }
    //左侧树新增节点
    form.get().find('.deploy-url-table-add').on('click', function () {
        if (isEmpty(nodeForAdd)){
            alert(msg('no url selected'));
            return;
        }
        if (nodeForAdd.parent != '#'){
            alert(msg('can not add children'));
            return;
        }
        //the parent node (to create a root node use either "#" (string) or `null`)
        var par = nodeForAdd.id;
        //the data for the new node (a valid JSON object, or a simple string with the name)
        var node = form.get().find('.deploy-url-table-add-input').val();
        // the index at which to insert the node, "first" and "last" are also supported, default is "last"
        var pos = "last";
        // a function to be called once the node is created
        var callback = function (node) {
            //新增成功更改新增表集合数据。
            addUrlInterfaceMap[node.id] = {};
            addUrlInterfaceMap[node.id].updateType = 2;
            addUrlInterfaceMap[node.id].i18n = {};
            addUrlInterfaceMap[node.id].i18n[dx.user.language_id] = node.text;
            //设置URL type
            switch (nodeForAdd.id){
                case 'url_tree_text' :
                    addUrlInterfaceMap[node.id].type = 1;
                    break;
                case 'java_api_tree_text' :
                    addUrlInterfaceMap[node.id].type = 2;
                    break;
                case 'proc_api_tree_text' :
                    addUrlInterfaceMap[node.id].type = 3;
                    break;
                case 'outside_interface_tree_text' :
                    addUrlInterfaceMap[node.id].type = 4;
                    break;
                case 'url_tree_out_url' :
                    addUrlInterfaceMap[node.id].type = 5;
                    break;
            }
            addUrlInterfaceMap[node.id].urlParam = {};
            addUrlInterfaceMap[node.id].urlCheck = [];

            //添加成功后选中该节点
            $tableTree.jstree("select_node", node.id);
        };
        // internal argument indicating if the parent node was succesfully loaded
        var is_loaded = true;
        $tableTree.jstree("create_node", par, node, pos, callback, is_loaded);
    });
    //左侧输删除节点
    form.get().find('.deploy-url-left-tree-delete').on('click', function () {
        var deleteUrlsNodeId = [];
        function getDeleteTables(node){
            var childs = ref.get_children_dom(node);
            for (var i=0; i<childs.length; i++){
                var nodes = $tableTree.jstree('get_node',childs[i]);
                deleteUrlsNodeId.push(nodes.id);
                if (!isEmpty(nodes.children)){
                    for (var j=0; j<nodes.children.length; j++){
                        deleteUrlsNodeId.push(nodes.children[j]);
                        getDeleteTables(nodes.children[j]);
                    }
                }
            }
        }
        var flag = confirm(msg('delete real?'));
        if (flag){
            var ref = $tableTree.jstree(true);
            getDeleteTables(nodeForAdd);
            deleteUrlsNodeId.push(nodeForAdd.id);
            if (!isEmpty(deleteUrlsNodeId)){
                for (var i=0; i<deleteUrlsNodeId.length; i++){
                    //删除的不为新增的才记录到删除里
                    if (isEmpty(deleteUrlInterfaceMap[deleteUrlsNodeId[i]])){
                        var tableNode = $tableTree.jstree('get_node',deleteUrlsNodeId[i]);
                        deleteUrlInterfaceMap[deleteUrlsNodeId[i]] = dx.urlInterface[tableNode.id];
                    }
                }
            }
            ref.delete_node(nodeForAdd);
        }
    });

    var prodSqlEdit;
    var urlInterface; // updateType 1 修改。 2 新增。 3 删除
    var nodeForAdd = {}; //for新增节点。
    var editUrlInterfaceMap = {};
    var addUrlInterfaceMap = {}; //新增表集合。树ID 为key
    var deleteUrlInterfaceMap = {};  //删除表集合
    //树节点点击事件。点击后给table赋值。
    $tableTree.bind("changed.jstree",function(e, data){
    	var dataGrid = form.get().find('.deploy-url-table-grid .deploy-column');
        var rowData=dataGrid.datagrid("getSelected");
        var rowIndex=dataGrid.datagrid("getRowIndex",rowData);
        if(rowIndex >=0){
        	changeColumnGridData(rowData, rowIndex);
        }
        if (!isEmpty(urlInterface)){
            saveTabData();
            changeUrlInterfaceData();
        }
//        if (!isEmpty(urlInterface) && isEmpty(urlInterface.urlParam)){
//        	alert("参数不能为空！");
//        }else{
        	form.get().find(".deploy-left-tree-delete").show();
        	//点击树的时候保存点击节点
            if (!isEmpty(data) && !isEmpty(data.node))
                nodeForAdd = data.node;
            else
                form.get().find('.deploy-prod-li').hide();
            if(!isEmpty(data.node)){
                var url_id = data.node.id;
                if (!isEmpty(addUrlInterfaceMap[data.node.id])){
                    urlInterface = addUrlInterfaceMap[data.node.id];
                }else{
                    urlInterface = dx.urlInterface[url_id];
                    editUrlInterfaceMap = {};
                    if (!isEmpty(urlInterface))
                        editUrlInterfaceMap[data.node.id] = urlInterface;
                }
                if (!isEmpty(urlInterface)){
                    form.get().find('.deploy-main-table-data').show();
                    form.get().find('.empty-form').hide();
                    if (urlInterface.type == 3){
                        form.get().find('.deploy-prod-li').show();
                    }else{
                        form.get().find('.deploy-prod-li').hide();
                    }
                }else{
                    form.get().find('.deploy-prod-li').hide();
                    form.get().find('.empty-form').show();
                    form.get().find('.deploy-main-table-data').hide();
                    return;
                }
               
                checkisReadOnly(urlInterface);
                //修改url字段名称。
                changeUrlStart(urlInterface);
                setUrlInterfaceData(urlInterface);
                var tabs = form.get().find('.deploy-grid-title li');
                for (var i=0; i<tabs.length; i++){
                    if ($(tabs[i]).is('.active')){
                        var tabType = $(tabs[i]).find('a').attr('class');
                        switch (tabType){
                            case 'deploy-column' :
                                initColumnGrid(urlInterface);
                                return;
                            case 'deploy-rule' :
                                initCheckRuleGrid(urlInterface);
                                return;
                            case 'deploy-prod' :
                                initProdCode(urlInterface);
                                return;
                        }
                    }
                }
          //  }
        }
    });

    //初始化列表格。
    initColumnGrid();
    form.get().find('.tb_deploy_tab a[href="#deploy-column"]').on('shown.bs.tab', function (e) {
        initColumnGrid(urlInterface);
    });
    form.get().find('.tb_deploy_tab a[href="#deploy-rule"]').on('shown.bs.tab', function (e) {
        initCheckRuleGrid(urlInterface);
    });
    form.get().find('.tb_deploy_tab a[href="#deploy-prod"]').on('shown.bs.tab', function (e) {
        initProdCode(urlInterface);
    });

    //点击tab切换时。把上一个tab的值保存进table
    form.get().find('.deploy-grid-title li').on('click', function () {
        saveTabData();
    });

    //表格新增行
    form.get().find('.deploy-url-column-add').on('click', function () {
        if (checkisReadOnly(urlInterface))
            return;
        var grid = $(this).parent().parent().find('.deploy-grid');
        addGridRow(grid);
    });
    //表格删除行
    form.get().find('.deploy-url-column-delete').on('click', function () {
        if (checkisReadOnly(urlInterface))
            return;
        var grid = $(this).parent().parent().find('.deploy-grid');
        var selectRows = grid.datagrid('getSelections');
        //删除column的列。要删除table中的列。并删除CRUDColumn中的列
        if ($(grid).hasClass('deploy-column')){
            if (!isEmpty(urlInterface)){
                for (var i=0; i<selectRows.length; i++){
                    if (!isEmpty(selectRows[i]) && !isEmpty(selectRows[i].column_name)){
                        if (!isEmpty(urlInterface.urlParam[selectRows[i].column_name])){
                            if (isEmpty(urlInterface.deleteColumns))
                                urlInterface.deleteColumns = [];
                            urlInterface.deleteColumns.push(urlInterface.urlParam[selectRows[i].column_name])
                        }
                        if (!isEmpty(urlInterface.urlParam))
                            delete urlInterface.urlParam[selectRows[i].column_name];
                        if (!isEmpty(urlInterface.cRUDColumns))
                            delete urlInterface.cRUDColumns[selectRows[i].column_name];
                    }
                }
            }
        }
        for (var i=0; i<selectRows.length; i++){
            deleteGridRows(grid, grid.datagrid('getRowIndex', selectRows[i]));
        }
    });

    function buildUrlInterfaceColumn(urlInterfaces){
        if (isEmpty(urlInterfaces))
            return;
        for(var key in urlInterfaces){
            var urlInterface = urlInterfaces[key];
            if (isEmpty(urlInterface.urlParam))
                continue;
            var CRUDColumns = urlInterface.cRUDColumns;
            if (isEmpty(CRUDColumns))
                continue;
            //修改的在原先的中不存在为新增
            for(var columnKey in CRUDColumns){
                if (isEmpty(columnKey)){
                    alert(msg('column name is null'));
                    return;
                }
                //修改的在原先的中不存在为新增
                if (!urlInterface.urlParam.hasOwnProperty(columnKey)){
                    CRUDColumns[columnKey].seq = 100;
                    CRUDColumns[columnKey].cell_cnt = 3;
                    urlInterface.cRUDColumns[columnKey].columnChange = 2;
                }
            }
        }
    }
    //表总保存按钮
    form.get().find('.deploy-table-submit').on('click', function () {
    	var dataGrid = form.get().find('.deploy-url-table-grid .deploy-column');
        var rowData=dataGrid.datagrid("getSelected");
        var rowIndex=dataGrid.datagrid("getRowIndex",rowData);
        if(rowIndex >=0){
            changeColumnGridData(rowData, rowIndex);
        }
        if (isEmpty(urlInterface)){
            alert(msg('have no data save'));
        }else{
            //点击保存时保存当前tab页的数据。
            saveTabData();
            //点击保存时 保存table数据。
            changeUrlInterfaceData();
            for(var key in editUrlInterfaceMap){
                if (addUrlInterfaceMap.hasOwnProperty(key) || deleteUrlInterfaceMap.hasOwnProperty(key))
                    editUrlInterfaceMap = {};
            }
            dx.processing.open();
            buildUrlInterfaceColumn(editUrlInterfaceMap);
        	postJson('/urlInterface/CRUDUrlInterface.do',
                    {editUrlInterfaceMap : editUrlInterfaceMap, addUrlInterfaceMap : addUrlInterfaceMap, deleteUrlInterfaceMap : deleteUrlInterfaceMap}, function (data) {
                        //保存成功后刷新页面。同时清空三个map
                        editUrlInterfaceMap = {};
                        addUrlInterfaceMap = {}; //新增表集合。树ID 为key
                        deleteUrlInterfaceMap = {};  //删除表集合
                        postJson('/data/cache/reload.do', function(){
                        	dxReload(function(){
                        		dxToastAlert(msg('Saved successfully!'));
                        		 ajaxLoadGroup(form, callback);
                                 dx.processing.close();
                        	});
                        })
                    });
        }
    });
});