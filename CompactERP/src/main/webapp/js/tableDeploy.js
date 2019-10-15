/**
 * Created by Administrator on 2017/8/30.
 */
/*
 继承 column 类需要新增的属性。 1,columnChange 2,oldColumnName 列名修改后的原列名
 基础 table 类需要新增的属性。 1,deleteTrigger Map<String, trigger>
 1 修改。 2 新增。 3 删除
 table.updateType  table字段
 table.allUpdateTyle table 全局是否修改状态
 table.columnChange table column字段
 table.orderRuleChange table 排序   全删再新增
 table.checkRuleChange table 校验规则
 table.buttonActionChange table action
 table.rendersChange table 显示样式
 table.autoGenChange table 批量操作
 table.conditionChange  按钮条件
 table.apiChange        按钮api
 */
registerInit('tableDeploy', function (form) {
    //$('.testaaa').g1DefaultWidget({
    //    tableName: 't_order_contract',
    //    columnName: 'client_name',
    //    token: '0fb6e827-331e-4e8a-9d6f-e30618a2b511',
    //    onChange: function (data, revertFunc) {
    //        var aaa = 1;
    //    }
    //});
    function getForm() {
        return $('#' + form.id);
    }
    
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
    $(".deploy-left-table-data").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"}); // First scrollable DIV
    // getForm().find(".deploy-top-table-data-more-btn span").on("click",function () {
    //     if($(this).hasClass("moreClose")){
    //         $(this).find("i").addClass("fa-rotate-180");
    //         var curHeight = getForm().find(".deploy-top-table-data").height();
    //         getForm().find(".deploy-top-table-data-more").css("display","block");
    //         getForm().find(".deploy-top-table-data").css('height', 'auto');
    //         var autoHeight = getForm().find(".deploy-top-table-data").height();
    //         getForm().find(".deploy-top-table-data").height(curHeight).animate({height: autoHeight}, 300);
    //
    //         $(this).removeClass("moreClose");
    //     }else {
    //         $(this).find("i").removeClass("fa-rotate-180");
    //         var curHeight = getForm().find(".deploy-top-table-data").height();
    //         getForm().find(".deploy-top-table-data-more").css("display","none");
    //         getForm().find(".deploy-top-table-data").css('height', '40%');
    //         var autoHeight = getForm().find(".deploy-top-table-data").height();
    //         getForm().find(".deploy-top-table-data").height(curHeight).animate({height: autoHeight}, 300);
    //
    //         $(this).addClass("moreClose");
    //     }
    // });
    //事件名称下拉框
    function getUrlSelect(type) {
        var url = dx.urlInterface;
        var OptionsData = [];
        OptionsData.push({key: '', productname: ''});
        for (var key in url) {
            OptionsData.push({
                key: key,
                productname: url[key].i18n ? url[key].i18n[dx.user.language_id] : url[key].name
            });
        }
        return OptionsData;
    }

    //字典下拉框
    function gridDiceSelect(dictId) {
        var dict = dx.dict[dictId];
        var OptionsData = [];
        OptionsData.push({key: '', productname: ''});
        for (var key in dict) {
            OptionsData.push({
                key: key,
                productname: dict[key][dx.user.language_id]
            });
        }
        return OptionsData;
    }

    //字段下拉框
    function getColumnOptionsData(table) {
        var columnOptionsData = [];
        columnOptionsData.push({column_name: '', productname: ''});
        if (!isEmpty(table)) {
            var columns = table.columns;
            for (var i = 0; i < columns.length; i++) {
                if (!isEmpty(table.CRUDColumns) &&
                    table.CRUDColumns.hasOwnProperty(columns[i].column_name)) {
                    columnOptionsData.push({
                        column_name: table.CRUDColumns[columns[i].column_name].column_name,
                        productname: table.CRUDColumns[columns[i].column_name].i18n[dx.user.language_id]
                    });
                } else {
                    columnOptionsData.push({
                        column_name: columns[i].column_name,
                        productname: columns[i].i18n[dx.user.language_id]
                    });
                }
            }
        } else {
            var rows = getForm().find('.deploy-table-grid .deploy-column').datagrid('getRows');
            for (var i = 0; i < rows.length; i++) {
                columnOptionsData.push({
                    column_name: rows[i].column_name,
                    productname: rows[i].column_name_I18N
                });
            }
        }
        return columnOptionsData;
    }
    //同步到日历的点击事件
    getForm().find(".deploy_sync_calendar").on("click",".input-bt-Switcher",function () {
    	if(getForm().find('.deploy_sync_calendar').find('input').is(':checked')){
    		getForm().find('.deploy_calender_color').show();
    	}else{
    		 getForm().find('.deploy_calender_color')
    		 getForm().find('.deploy_calender_color').find('input').val('');
    		 getForm().find('.deploy_calender_color').hide();
    	}
    });
    // 获取comlmu 页面值
    function getColumnData() {
        var columnDatas = {};
        columnDatas.table_id = table ? table.id : '';
        columnDatas.column_name = getForm().find('.deploy_column_name').find('input').val();
        columnDatas.virtual = getForm().find('.deploy_virtual').find('input').is(':checked') ? 1 : 0;
        columnDatas.tab_name = getForm().find('.deploy_tab_name').find('select').val();
        //columnDatas.group_name = getForm().find('.deploy_group_name').find('select').val();
        columnDatas.data_type = getForm().find('.deploy_data_type').find('select').val();
        columnDatas.data_format = getForm().find('.deploy_data_format').find('input').val();
        columnDatas.min_len = getForm().find('.deploy_min_len').find('input').val();
        columnDatas.max_len = getForm().find('.deploy_max_len').find('input').val();
        columnDatas.is_condition = getForm().find('.deploy_is_condition').find('input').is(':checked') ? 1 : 0;
        columnDatas.is_auth = getForm().find('.deploy_is_auth').find('input').is(':checked') ? 1 : 0;
        columnDatas.is_encrypt = getForm().find('.deploy_is_encrypt').find('input').is(':checked') ? 1 : 0;
        columnDatas.dic_id = getForm().find('.deploy_dic_id').find('select').val();
        columnDatas.ref_table_name = getForm().find('.deploy_ref_table_name').find('select').val();
        columnDatas.ref_table_cols = getForm().find('.deploy_ref_table_cols').find('input').val();
        columnDatas.ref_table_sql = getForm().find('.deploy_ref_table_sql').find('textarea').val();
        columnDatas.ref_table_display = getForm().find('.deploy_ref_table_display').find('input').is(':checked') ? 1 : 0;
        //columnDatas.ref_table_filter = '';
        //columnDatas.quick_filter = '';
        columnDatas.is_multiple = getForm().find('.deploy_is_multiple').find('input').is(':checked') ? 1 : 0;
        columnDatas.formula = getForm().find('.deploy_formula').find('textarea').val();
        columnDatas.default_value = getForm().find('.deploy_default_value').find('input').val();
        columnDatas.prefix = getForm().find('.deploy_prefix').find('input').val();
        columnDatas.suffix = getForm().find('.deploy_suffix').find('input').val();
        if (getForm().find('.deploy_sum_flag').find('input').is(':checked')) {
            columnDatas.sum_flag = 1;
        } else {
            delete columnDatas.sum_flag;
        }
        columnDatas.ro_insert = getForm().find('.deploy_ro_insert').find('input').is(':checked') ? 1 : 0;
        columnDatas.ro_update = getForm().find('.deploy_ro_update').find('input').is(':checked') ? 1 : 0;
        columnDatas.read_only_condition = getForm().find('.deploy_read_only_condition').find('input').val();
        columnDatas.hidden = getForm().find('.deploy_hidden').find('input').is(':checked') ? 1 : 0;
        columnDatas.wrap = getForm().find('.deploy_wrap').find('input').is(':checked') ? 1 : 0;
        columnDatas.view_style = getForm().find('.deploy_view_style').find('input').val();
        columnDatas.cell_cnt = getForm().find('.deploy_cell_cnt').find('input').val();
        //columnDatas.seq = $('.deploy_seq').find('input').val();
        //columnDatas.level = '';
        //columnDatas.memo = '';
        columnDatas.read_only_clear = getForm().find('.deploy_read_only_clear').find('input').is(':checked') ? 'true' : 'False';
        //columnDatas.ref_table_new = '';
        columnDatas.ref_table_tree = getForm().find('.deploy_ref_table_tree').find('input').val();
        columnDatas.link_json = getForm().find('.deploy_link_json').find('input').val();
        //columnDatas.international_id = '';
        columnDatas.is_id_column = 0;
        //columnDatas.module = '';
        columnDatas.isCalendarEvent = getForm().find('.deploy_sync_calendar').find('input').is(':checked') ? 1 : 0;
        columnDatas.calendarEventDefaultColor = getForm().find('.deploy_calender_color').find('input').val();
        return columnDatas;
    }

    // 设置column 页面值
    function setColumnData(columnDatas) {
        if (isEmpty(columnDatas)) {
            //名称列
            getForm().find('.deploy_name_expression').find('input').prop("checked", false);

            //columnDatas.table_id = table ? table.id : '';
            getForm().find('.deploy_column_name').find('input').val('');
            getForm().find('.deploy_virtual').find('input').prop("checked", false);
            getForm().find('.deploy_tab_name').find('select').val('');
            getForm().find('.deploy_group_name').find('select').val('');
            getForm().find('.deploy_data_type').find('select').val('');
            getForm().find('.deploy_data_format').find('input').val('');
            getForm().find('.deploy_min_len').find('input').val('');
            getForm().find('.deploy_max_len').find('input').val('');
            getForm().find('.deploy_is_condition').find('input').prop("checked", false);
            getForm().find('.deploy_is_auth').find('input').prop("checked", false);
            getForm().find('.deploy_is_encrypt').find('input').prop("checked", false);
            getForm().find('.deploy_dic_id').find('select').val('');
            getForm().find('.deploy_ref_table_name').find('select').val('');
            getForm().find('.deploy_ref_table_cols').find('input').val('');
            getForm().find('.deploy_ref_table_sql').find('textarea').val('');
            getForm().find('.deploy_ref_table_display').find('input').prop("checked", false);
            //columnDatas.ref_table_filter = '';
            //columnDatas.quick_filter = '';
            getForm().find('.deploy_is_multiple').find('input').prop("checked", false);
            getForm().find('.deploy_formula').find('textarea').val('');
            getForm().find('.deploy_default_value').find('input').val('');
            getForm().find('.deploy_prefix').find('input').val('');
            getForm().find('.deploy_suffix').find('input').val('');
            getForm().find('.deploy_sum_flag').find('input').prop("checked", false);
            getForm().find('.deploy_ro_insert').find('input').prop("checked", false);
            getForm().find('.deploy_ro_update').find('input').prop("checked", false);
            getForm().find('.deploy_read_only_condition').find('input').val('');
            getForm().find('.deploy_hidden').find('input').prop("checked", false);
            getForm().find('.deploy_wrap').find('input').prop("checked", false);
            getForm().find('.deploy_view_style').find('input').val('');
            getForm().find('.deploy_cell_cnt').find('input').val(1);
            //$('.deploy_seq').find('input').val('');
            //columnDatas.level = '';
            //columnDatas.memo = '';
            getForm().find('.deploy_read_only_clear').find('input').prop("checked", false);
            //columnDatas.ref_table_new = '';
            getForm().find('.deploy_ref_table_tree').find('input').val('');
            getForm().find('.deploy_link_json').find('input').val('');
            
            getForm().find('.deploy_sync_calendar').find('input').prop("checked", false);
            getForm().find('.deploy_calender_color').find('input').val('');
            getForm().find('.deploy_calender_color').hide();
            //columnDatas.international_id = '';
            //columnDatas.is_id_column = 0;
            //columnDatas.module = '';
        } else {
            //columnDatas.table_id = table ? table.id : '';
            //名称列
            if (isEmpty(table)) {
                getForm().find('.deploy_name_expression').find('input').prop("checked", false);
            } else {
                if (isEmpty(table.name_column) || table.name_column != columnDatas.column_name) {
                    getForm().find('.deploy_name_expression').find('input').prop("checked", false);
                } else {
                    getForm().find('.deploy_name_expression').find('input').prop("checked", true);
                }
            }

            getForm().find('.deploy_column_name').find('input').val(columnDatas.column_name);
            if (isEmpty(columnDatas.virtual) || columnDatas.virtual == 0)
                getForm().find('.deploy_virtual').find('input').prop("checked", false);
            else
                getForm().find('.deploy_virtual').find('input').prop("checked", true);
            getForm().find('.deploy_tab_name').find('select').val(columnDatas.tab_name);
            getForm().find('.deploy_group_name').find('select').val(columnDatas.group_name);
            getForm().find('.deploy_data_type').find('select').val(columnDatas.data_type);
            getForm().find('.deploy_data_format').find('input').val(columnDatas.data_format);
            getForm().find('.deploy_min_len').find('input').val(columnDatas.min_len);
            getForm().find('.deploy_max_len').find('input').val(columnDatas.max_len ? columnDatas.max_len : 20);
            if (isEmpty(columnDatas.is_condition) || columnDatas.is_condition == 0)
                getForm().find('.deploy_is_condition').find('input').prop("checked", false);
            else
                getForm().find('.deploy_is_condition').find('input').prop("checked", true);
            if (isEmpty(columnDatas.is_auth) || columnDatas.is_auth == 0)
                getForm().find('.deploy_is_auth').find('input').prop("checked", false);
            else
                getForm().find('.deploy_is_auth').find('input').prop("checked", true);
            if (isEmpty(columnDatas.is_encrypt) || columnDatas.is_encrypt == 0)
                getForm().find('.deploy_is_encrypt').find('input').prop("checked", false);
            else
                getForm().find('.deploy_is_encrypt').find('input').prop("checked", true);
            getForm().find('.deploy_dic_id').find('select').val(columnDatas.dic_id);
            getForm().find('.deploy_ref_table_name').find('select').val(columnDatas.ref_table_name);
            getForm().find('.deploy_ref_table_cols').find('input').val(columnDatas.ref_table_cols);
            getForm().find('.deploy_ref_table_sql').find('textarea').val(columnDatas.ref_table_sql);
            if (isEmpty(columnDatas.ref_table_display) || columnDatas.ref_table_display == 0)
                getForm().find('.deploy_ref_table_display').find('input').prop("checked", false);
            else
                getForm().find('.deploy_ref_table_display').find('input').prop("checked", true);
            //columnDatas.ref_table_filter = '';
            //columnDatas.quick_filter = '';
            if (isEmpty(columnDatas.is_multiple) || columnDatas.is_multiple == 0)
                getForm().find('.deploy_is_multiple').find('input').prop("checked", false);
            else
                getForm().find('.deploy_is_multiple').find('input').prop("checked", true);
            getForm().find('.deploy_formula').find('textarea').val(formatSql(columnDatas.formula));
            getForm().find('.deploy_default_value').find('input').val(formatSql(columnDatas.default_value));
            getForm().find('.deploy_prefix').find('input').val(columnDatas.prefix);
            getForm().find('.deploy_suffix').find('input').val(columnDatas.suffix);
            if (isEmpty(columnDatas.sum_flag) || columnDatas.sum_flag == 0)
                getForm().find('.deploy_sum_flag').find('input').prop("checked", false);
            else
                getForm().find('.deploy_sum_flag').find('input').prop("checked", true);
            if (isEmpty(columnDatas.ro_insert) || columnDatas.ro_insert == 0)
                getForm().find('.deploy_ro_insert').find('input').prop("checked", false);
            else
                getForm().find('.deploy_ro_insert').find('input').prop("checked", true);
            if (isEmpty(columnDatas.ro_update) || columnDatas.ro_update == 0)
                getForm().find('.deploy_ro_update').find('input').prop("checked", false);
            else
                getForm().find('.deploy_ro_update').find('input').prop("checked", true);
            getForm().find('.deploy_read_only_condition').find('input').val(formatSql(columnDatas.read_only_condition));
            if (isEmpty(columnDatas.hidden) || columnDatas.hidden == 0)
                getForm().find('.deploy_hidden').find('input').prop("checked", false);
            else
                getForm().find('.deploy_hidden').find('input').prop("checked", true);
            if (isEmpty(columnDatas.wrap) || columnDatas.wrap == 0)
                getForm().find('.deploy_wrap').find('input').prop("checked", false);
            else
                getForm().find('.deploy_wrap').find('input').prop("checked", true);
            getForm().find('.deploy_view_style').find('input').val(columnDatas.view_style);
            getForm().find('.deploy_cell_cnt').find('input').val(columnDatas.cell_cnt ? columnDatas.cell_cnt : 3);
            //$('.deploy_seq').find('input').val(columnDatas.seq);
            //columnDatas.level = '';
            //columnDatas.memo = '';
            if (isEmpty(columnDatas.read_only_clear) || columnDatas.read_only_clear == 'False')
                getForm().find('.deploy_read_only_clear').find('input').prop("checked", false);
            else
                getForm().find('.deploy_read_only_clear').find('input').prop("checked", true);
            //columnDatas.ref_table_new = '';
            getForm().find('.deploy_ref_table_tree').find('input').val(columnDatas.ref_table_tree);
            getForm().find('.deploy_link_json').find('input').val(columnDatas.link_json);
            if (isEmpty(columnDatas.isCalendarEvent) || columnDatas.isCalendarEvent == 0){
            	getForm().find('.deploy_sync_calendar').find('input').prop("checked", false);
                getForm().find('.deploy_calender_color').find('input').val("");
                getForm().find('.deploy_calender_color').hide();
            }else{
            	getForm().find('.deploy_calender_color').show();
                getForm().find('.deploy_sync_calendar').find('input').prop("checked", true);
            	getForm().find('.deploy_calender_color').find('input').val(columnDatas.calendarEventDefaultColor);
            }
            
            //columnDatas.international_id = '';
            //columnDatas.is_id_column = 0;
            //columnDatas.module = '';
        }
    }

    function initDataGrid($node, columns, data, event, rownumbers) {
        $node.datagrid({
            height: "100%",
            columns: columns,
            fitColumns: true,
            rownumbers: rownumbers ? true : false,
            //data : data,
            onDblClickCell: onDbClickCell,
            onAfterEdit: event ? (event.onAfterEdit ? event.onAfterEdit : function () {
            }) : function () {
            },
            onLoadSuccess: event ? (event.onLoadSuccess ? event.onLoadSuccess : function () {
            }) : function () {
            },
            onClickCell: event ? (event.onClickCell ? event.onClickCell : function () {
            }) : function () {
            },
            onCheck: event ? (event.onCheck ? event.onCheck : function () {
            }) : function () {
            },
            onUncheck: event ? (event.onUncheck ? event.onUncheck :
                function (rowIndex, rowData) {//取消选中时取消编辑行
                    endEditing(this, rowIndex);
                }) :
                function (rowIndex, rowData) {//取消选中时取消编辑行
                    endEditing(this, rowIndex);
                },
            onDblClickRow: event ? (event.onDblClickRow ? event.onDblClickRow : function () {
            }) : function () {
            },
            onDrop: event ? (event.onDrop ? event.onDrop : function () {
            }) : function () {
            },
            onClickRow: event ? (event.onClickRow ? event.onClickRow :
                function (rowIndex, rowData) {
                    $(this).datagrid('unselectAll');
                    $(this).datagrid('selectRow', rowIndex);
                    endAllRowEdit($(this));
                }) :
                function (rowIndex, rowData) {
                    $(this).datagrid('unselectAll');
                    $(this).datagrid('selectRow', rowIndex);
                    endAllRowEdit($(this));
                },
            onRowContextMenu: event ? (event.onRowContextMenu ? event.onRowContextMenu : function () {
            }) : function () {
            }
        });
        $node.datagrid('loadData', data);
    }

    //设置表数据
    function setTableData() {
    	//移除国际化控件
    	var table_name_I18N = form.get().find(".table_name_I18N");
    	form.get().find(".table_name_I18N").html("");
        if (isEmpty(table)) {
        	 //初始化国际化控件
            international(table_name_I18N, "", "");
            getForm().find('.deploy_table_table_id').find('input').val('');
            //getForm().find('.deploy_table_table_name_I18N').find('input').val('');
            getForm().find('.deploy_table_readonly').find('input').prop("checked", false);
            getForm().find('.deploy_table_detail_disp_cols').find('input').val('');
            getForm().find('.deploy_table_table_type').find('select').val(5);
            getForm().find('.deploy_table_seq').find('input').val('');
            getForm().find('.deploy_table_parent_id').find('select').val('');
            getForm().find('.deploy_table_view_main_table').find('select').val('');
            getForm().find('.deploy_table_key_formula').find('input').val('');
            getForm().find('.deploy_table_parent_id_column').find('input').val('');
            getForm().find('.deploy_table_children_id_column').find('input').val('');
            getForm().find('.deploy_table_logable').find('input').prop("checked", false);
            getForm().find('.deploy_table_view_style').find('input').val('');
            getForm().find('.deploy_table_is_approve').find('input').prop("checked", false);
            getForm().find('.deploy_table_is_approve_select').find('input').prop("checked", false);
            getForm().find('.deploy_table_is_auth').find('input').prop("checked", false);
            getForm().find('.deploy_table_auto_gen_sql').find('input').val('');
            getForm().find('.name_expression_publicity').find('input').val('');
            getForm().find('.deploy_table_default_group_column').find('select').val('');
        } else {
        	 //初始化国际化控件
            international(table_name_I18N, table.i18n.international_id, table.i18n ? table.i18n[dx.user.language_id] : '');
            getForm().find('.deploy_table_table_id').find('input').val(table.id);
           // getForm().find('.deploy_table_table_name_I18N').find('input').val(table.i18n ? table.i18n[dx.user.language_id] : '');
            if (isEmpty(table.readonly) || table.readonly == 0)
                getForm().find('.deploy_table_readonly').find('input').prop("checked", false);
            else
                getForm().find('.deploy_table_readonly').find('input').prop("checked", true);
            getForm().find('.deploy_table_detail_disp_cols').find('input').val(table.detail_disp_cols);
            getForm().find('.deploy_table_table_type').find('select').val(table.table_type ? table.table_type : 5);
            getForm().find('.deploy_table_seq').find('input').val(table.seq);
            getForm().find('.deploy_table_parent_id').find('select').val(table.parent_id);
            getForm().find('.deploy_table_view_main_table').find('select').val(table.view_main_table);
            getForm().find('.deploy_table_key_formula').find('input').val(table.key_formula);
            getForm().find('.deploy_table_parent_id_column').find('input').val(table.parent_id_column);
            getForm().find('.deploy_table_children_id_column').find('input').val(table.children_id_column);
            if (isEmpty(table.ogable) || table.ogable == 0)
                getForm().find('.deploy_table_logable').find('input').prop("checked", false);
            else
                getForm().find('.deploy_table_logable').find('input').prop("checked", true);
            getForm().find('.deploy_table_view_style').find('input').val(table.view_style);
            if (isEmpty(table.is_approve) || table.is_approve == 0)
                getForm().find('.deploy_table_is_approve').find('input').prop("checked", false);
            else
                getForm().find('.deploy_table_is_approve').find('input').prop("checked", true);
            if (isEmpty(table.is_approve_select) || table.is_approve_select == 0)
                getForm().find('.deploy_table_is_approve_select').find('input').prop("checked", false);
            else
                getForm().find('.deploy_table_is_approve_select').find('input').prop("checked", true);
            if (isEmpty(table.is_auth) || table.is_auth == 0)
                getForm().find('.deploy_table_is_auth').find('input').prop("checked", false);
            else
                getForm().find('.deploy_table_is_auth').find('input').prop("checked", true);
            getForm().find('.deploy_table_auto_gen_sql').find('input').val(table.auto_gen_sql);
            getForm().find('.deploy_table_name_expression_publicity').find('input').val(table.name_expression_publicity);
            getForm().find('.deploy_table_default_group_column').find('select').val(table.default_group_column);
        }
    }

    //修改表数据
    function changeTableData() {
        if (isEmpty(table))
            return;
        table.id = getForm().find('.deploy_table_table_id').find('input').val();
        if (isEmpty(table.i18n))
            table.i18n = {};
        var getInternational = form.get().find(".table_name_I18N").internationalControl(true).getData();
        if (!isEmpty(getInternational.international_id)) {
        	table.i18n=getInternational;
        }else{
        	table.i18n={};
        	table.i18n[dx.user.language_id]=getInternational.interValue;
        	table.i18n.international_id="";
        }
        //table.i18n[dx.user.language_id] = getForm().find('.deploy_table_table_name_I18N').find('input').val();
        table.readonly = getForm().find('.deploy_table_readonly').find('input').is(':checked') ? 1 : 0;
        table.detail_disp_cols = getForm().find('.deploy_table_detail_disp_cols').find('input').val();
        table.table_type = getForm().find('.deploy_table_table_type').find('select').val();
        table.seq = getForm().find('.deploy_table_seq').find('input').val();
        table.parent_id = getForm().find('.deploy_table_parent_id').find('select').val();
        table.view_main_table = getForm().find('.deploy_table_view_main_table').find('select').val();
        table.key_formula = getForm().find('.deploy_table_key_formula').find('input').val();
        table.parent_id_column = getForm().find('.deploy_table_parent_id_column').find('input').val();
        table.children_id_column = getForm().find('.deploy_table_children_id_column').find('input').val();
        table.logable = getForm().find('.deploy_table_logable').find('input').is(':checked') ? 1 : 0;
        table.view_style = getForm().find('.deploy_table_view_style').find('input').val();
        table.is_approve = getForm().find('.deploy_table_is_approve').find('input').is(':checked') ? 1 : 0;
        table.is_approve_select = getForm().find('.deploy_table_is_approve_select').find('input').is(':checked') ? 1 : 0;
        table.is_auth = getForm().find('.deploy_table_is_auth').find('input').is(':checked') ? 1 : 0;
        table.auto_gen_sql = getForm().find('.deploy_table_auto_gen_sql').find('input').val();
        table.name_expression_publicity = getForm().find('.deploy_table_name_expression_publicity').find('input').val();
        var groupColumnName=getForm().find('.deploy_table_default_group_column').find('select').val();
		var column=table.columns;
		for(var i=0;i<column.length;i++){
			if(column[i].column_name == groupColumnName){
				table.default_group_column = groupColumnName;
				break;
			}
		}
    }

    //判断列是否修改
    function checkIsUpdate(source, targer) {
        if (isEmpty(source)) {
            return false;
        }
        if (isEmpty(targer)) {
            return false;
        }
        for (var key in source) {
            if (source[key] != null && (typeof source[key] == 'object')) {
                checkIsUpdate(source[key], targer[key]);
            } else if (source[key] != null && (typeof source[key] != 'object') && targer.hasOwnProperty(key)) {
                if ((isEmpty(source[key]) ? '' : source[key]) !=
                    (isEmpty(targer[key]) ? '' : targer[key])) {
                    return true;
                }
            }
        }
        return false;
    }

    //排序
    function dataOrder(data) {
        if (isEmpty(data))
            return;
        else if (typeof data == 'object') {
            var tmp;
            for (var i = 0; i < data.length; i++) {
                for (var j = i + 1; j < data.length; j++) {
                    if (data[i].seq > data[j].seq) {
                        tmp = data[i];
                        data[i] = data[j];
                        data[j] = tmp;
                    }
                }
            }
        }
        return data;
    }

    //设置列表格数据
    function setColumnGridData($node) {
        var columnData = [];
        if (table) {
            var columns = table.columns;
            var orderColumns = dataOrder(columns);
            if (!isEmpty(orderColumns))
                for (var i = 0; i < orderColumns.length; i++) {
                    columnData.push({
                        rowid: orderColumns[i].rowid,
                        seq: orderColumns[i].seq,
                        column_name: orderColumns[i].column_name,
                        column_name_I18N: orderColumns[i].i18n,
                        data_type: orderColumns[i].data_type,
                        is_id_column: orderColumns[i].is_id_column
                    });
                }
        }
        $node.datagrid('loadData', columnData);
    }
  //设置名称列
    getForm().find('.deploy_name_expression').on("click",".input-bt-Switcher",function () {
    	if (getForm().find('.deploy_name_expression').find('input').is(':checked')) {
    		var dataGrid = getForm().find('.deploy-table-grid .deploy-column');
            table.name_column = dataGrid.datagrid('getSelected').column_name;
        }else{
        	table.name_column="";
        }
    });
    
    //修改列数据
    function changeColumnGridData(rowData, rowIndex, isIDColumn) {
        function cloneI18N(i18n) {
            if (isEmpty(i18n))
                return {};
            else {
                var newI18N = {};
                for (var key in i18n) {
                    newI18N[key] = i18n[key];
                }
                return newI18N;
            }
        }


        if (isEmpty(table))
            return;
        if (!isEmpty(rowData)) {
            if (isEmpty(rowData.column_name))
                return;
            if (isEmpty(table.cRUDColumns)) {
                table.cRUDColumns = {};
            }
            if (isEmpty(table.columnMap))
                table.columnMap = {};

            var columnData = getColumnData();
            columnData.column_name = rowData.column_name;
            columnData.i18n = cloneI18N(table.columnMap[rowData.column_name] ? table.columnMap[rowData.column_name].i18n : {});
            columnData.i18n = rowData.column_name_I18N;
            columnData.data_type = rowData.data_type;
            if (isEmpty(table.columnMap) || isEmpty(table.columnMap[rowData.column_name]))
                columnData.is_id_column = 0;
            else {
                columnData.is_id_column = table.columnMap[rowData.column_name].is_id_column;
            }
            columnData.seq = rowIndex;
            //默认字段长度
            if (isEmpty(columnData.max_len)) {
                columnData.max_len = 20;
            }
            if (!isEmpty(table.columnMap[columnData.column_name])) {
                columnData.oldColumnName = table.columnMap[columnData.column_name].oldColumnName;
                columnData.columnChange = table.columnMap[columnData.column_name].columnChange;
                columnData.rowid = table.columnMap[columnData.column_name].rowid;
            }
            //rowid为空则为新增。
            if (isEmpty(rowData.rowid)) {
                if (table.columnMap.hasOwnProperty(columnData.column_name)) {
                    alert(msg('columnName exists'));
                    return;
                }
                var rowid = guid();
                table.columnMap[columnData.column_name] = columnData;
                table.columnMap[columnData.column_name].columnChange = 2;
                table.columnMap[columnData.column_name].rowid = rowid;
                if (isEmpty(table.columns))
                    table.columns = [];
                table.columns.push(table.columnMap[columnData.column_name]);
            } else { //否则为修改了参数。
                //if (checkIsUpdate(table.columnMap[rowData.column_name], columnData)){
                if (table.columnMap[rowData.column_name].columnChange != 2)
                    columnData.columnChange = 1;
                table.columnMap[columnData.column_name] = columnData;
                for (var i = 0; i < table.columns.length; i++) {
                    if (table.columns[i].column_name == columnData.column_name) {
                        table.columns[i] = columnData;
                    }
                }
                //}
            }
        }

    }

    //设置排序规则数据
    function setOrderRuleGrid($node) {
        if ($node.length != 1)
            return;
        var columnData = [];
        if (table) {
            var orderBy = table.orderBy;
            dataOrder(orderBy);
            if (!isEmpty(orderBy))
                for (var i = 0; i < orderBy.length; i++) {
                    columnData.push({
                        table_id: orderBy[i].table_id,
                        seq: orderBy[i].seq,
                        column_name: orderBy[i].column_name,
                        order_rule: orderBy[i].order_rule
                    });
                }
        }
        $node.datagrid('loadData', columnData);
    }

    //修改排序规则数据
    function changeOrderRuleGrid($node) {
        if (isEmpty(table))
            return;
        var updateRows = $node.datagrid('getChanges');
        if (!isEmpty(updateRows)) {
            var rows = $node.datagrid('getRows');
            if (!isEmpty(rows) && rows.length > 0) {
                for (var i = 0; i < rows.length; i++) {
                    rows[i].seq = i;
                    delete rows[i]._selected;
                }
            }
            table.orderBy = rows;
            table.orderRuleChange = 1;
        }
    }

    //设置校验规则数据
    function setCheckRuleGridData($node) {
        if ($node.length != 1)
            return
        var columnData = [];
        if (table) {
            var checkRules = table.checkRules;
            dataOrder(checkRules);
            if (!isEmpty(checkRules))
                for (var i = 0; i < checkRules.length; i++) {
                    columnData.push({
                        table_id: checkRules[i].table_id,
                        seq: checkRules[i].seq,
                        type: checkRules[i].type,
                        column_name: checkRules[i].column_name,
                        formula: formatSql(checkRules[i].formula),
                        check_level: checkRules[i].check_level,
                        error_msg_id: checkRules[i].error_msg_id,
                        msgI18n: checkRules[i].msgI18n,
                        error_msg_param: checkRules[i].error_msg_param
                    });
                }
        }
        $node.datagrid('loadData', columnData);
    }

    //修改校验规则数据
    function changeCheckRuleGridData($node) {
        if (isEmpty(table))
            return;
        var updateRows = $node.datagrid('getChanges');
        if (!isEmpty(updateRows)) {
            var rows = $node.datagrid('getRows');
            if (!isEmpty(rows) && rows.length > 0) {
                for (var i = 0; i < rows.length; i++) {
                    if (rows[i].type == 1) {
                        rows[i].create_submit = 1;
                    } else if (rows[i].type == 2) {
                        rows[i].edit_submit = 1;
                    } else if (rows[i].type == 3) {
                        rows[i].input_blur = 1;
                    }
                    rows[i].seq = i;
                    delete rows[i]._selected;
                }
            }
            table.checkRules = rows;
            table.checkRuleChange = 1;
        }
    }

    //设置数据处理数据
    function setButtonActionGridData(table, trigger, flag) {
        //重新加载按钮
        if (flag)
            initActionButon(table);
        //input框赋值
      //移除国际化控件
    	var action_name_I18N = form.get().find(".action_name_I18N");
    	form.get().find(".action_name_I18N").html("");
        if (!isEmpty(trigger)) {
            getForm().find('.deploy-data-rule .action-system-type').val(trigger.system_type);
            getForm().find('.deploy-data-rule .action_name_international').val(trigger.action_name_international);
            //初始化国际化控件
            international(action_name_I18N, trigger.action_name_I18N.international_id, trigger.action_name_I18N[dx.user.language_id]);
            //getForm().find('.deploy-data-rule .action_name_I18N').val(trigger.action_name_I18N[dx.user.language_id]);
            if (trigger.is_using == 1) {
                getForm().find('.deploy-data-rule .is_using').prop("checked", true);
            } else {
                getForm().find('.deploy-data-rule .is_using').prop("checked", false);
            }
            getForm().find('.deploy-data-rule .is_one_data').val(trigger.is_one_data);
            if(trigger.system_type!="operation"){
            	//getForm().find('.deploy-data-rule .is_one_data').prop("checked", false);
            	getForm().find('.deploy-data-rule .is_one_data').parent().hide();
            }else{
            	getForm().find('.deploy-data-rule .is_one_data').parent().show();
//            	if (trigger.is_one_data == 1) {
//                    getForm().find('.deploy-data-rule .is_one_data').prop("checked", true);
//                } else {
//                    getForm().find('.deploy-data-rule .is_one_data').prop("checked", false);
//                }
            }
        } else {
            getForm().find('.deploy-data-rule .action_name_international').val('');
            //初始化国际化控件
            international(action_name_I18N, "", "");
            //getForm().find('.deploy-data-rule .action_name_I18N').val('');
            getForm().find('.deploy-data-rule .action-system-type').val('');
            getForm().find('.deploy-data-rule .is_using').prop("checked", false);
            getForm().find('.deploy-data-rule .is_one_data').val(0);
           // getForm().find('.deploy-data-rule .is_one_data').prop("checked", false);
        	getForm().find('.deploy-data-rule .is_one_data').parent().hide();
        }
        //处理前提赋值
        var columnData = [];
        if (trigger) {
            var condition = trigger.condition;
            dataOrder(condition);
            if (!isEmpty(condition))
                for (var i = 0; i < condition.length; i++) {
                    columnData.push({
                        table_action_id: condition[i].table_action_id,
                        seq: condition[i].seq,
                        check_condition: formatSql(condition[i].check_condition),
                        level: condition[i].level,
                        violate_msg_international_id: condition[i].violate_msg_international_id,
                        violate_msg_I18N : condition[i].violate_msg_I18N,
                        violate_msg_param: condition[i].violate_msg_param,
                        is_using: condition[i].is_using
                    });
                }
        }
        getForm().find('.deploy-table-grid .deploy_action_prerequistie').datagrid('loadData', columnData);
        //触发事件赋值
        columnData = [];
        if (trigger) {
            var apis = trigger.api;
            dataOrder(apis);
            if (!isEmpty(apis))
                for (var i = 0; i < apis.length; i++) {
                    columnData.push({
                        event_id: apis[i].event_id,
                        table_action_id: apis[i].table_action_id,
                        seq: apis[i].seq,
                        event_type: apis[i].event_type,
                        event_name: apis[i].event_name,
                        event_param: formatSql(apis[i].event_param),
                        is_using: apis[i].is_using
                    });
                }
        }
        getForm().find('.deploy-table-grid .deploy_action_event').datagrid('loadData', columnData);
    }

    //点击数据处理 增/删/改/查/操作 按钮  或者切换tab页。需要保存当前按钮的修改。
    function changeButtonActionGridData(prerequistieIndex, prerequistieUsing, eventIndex, eventUsing) {
        //获得处理前提数据
        function getActionPrerequistieData(prerequistieIndex, prerequistieUsing) {
            var dataGrid = $('.deploy-table-grid .deploy_action_prerequistie');
            endAllRowEdit(dataGrid);
            if (isEmpty(table))
                return;
            var rows = dataGrid.datagrid('getRows');
            if (!isEmpty(rows) && rows.length > 0) {
                for (var i = 0; i < rows.length; i++) {
                    rows[i].seq = i;
                    if (!isEmpty(prerequistieIndex) && prerequistieIndex == i)
                        rows[i].is_using = prerequistieUsing;
                    delete rows[i]._selected;
                }
            }
            return rows;
        }

        //获得触发事件数据
        function getActionEventData(eventIndex, eventUsing) {
            var dataGrid = $('.deploy-table-grid .deploy_action_event');
            endAllRowEdit(dataGrid);
            if (isEmpty(table))
                return;
            var rows = dataGrid.datagrid('getRows');
            if (!isEmpty(rows) && rows.length > 0) {
                for (var i = 0; i < rows.length; i++) {
                    rows[i].seq = i;
                    if (!isEmpty(eventIndex) && eventIndex == i)
                        rows[i].is_using = eventUsing;
                    delete rows[i]._selected;
                }
            }
            return rows;
        }

        //修改按钮数据
        function changeButtonData(actionId) {
            if (isEmpty(actionId))
                return;
            var actionPrerequistieData = getActionPrerequistieData(prerequistieIndex, prerequistieUsing);
            var actionEventData = getActionEventData(eventIndex, eventUsing);
            table.triggers[actionId].condition = actionPrerequistieData;
            table.triggers[actionId].conditionChange = 1;
            table.triggers[actionId].api = actionEventData;
            table.triggers[actionId].apiChange = 1;
            ////保存报表
            //var reportId = getForm().find('.deploy-data-rule .deploy_action_report').val();
            //if (!isEmpty(reportId)){
            //    if (reportId != table.triggers[actionId].report_id){
            //        table.triggers[actionId].report_id = reportId;
            //    }
            //}
            if (!isEmpty(table.triggers[actionId]) &&
                !isEmpty(table.triggers[actionId].buttonActionChange) &&
                table.triggers[actionId].buttonActionChange != 2)
                table.triggers[actionId].buttonActionChange = 1;
            else if (!isEmpty(table.triggers[actionId]) &&
                isEmpty(table.triggers[actionId].buttonActionChange))
                table.triggers[actionId].buttonActionChange = 1;
            //保存处理名称
            var getInternational=form.get().find(".action_name_I18N").internationalControl(true).getData();
            if (isEmpty(table.triggers[actionId].action_name_I18N)) {
                table.triggers[actionId].action_name_I18N = {};
            }
            if(isEmpty(getInternational.international_id)){
            	table.triggers[actionId].action_name_I18N = {};
            	table.triggers[actionId].action_name_I18N[dx.user.language_id]=getInternational.interValue;
            	getForm().find('.deploy-action-item-ul li[action_id="'+actionId+'"]').text(getInternational.interValue);
            	table.triggers[actionId].action_name_I18N.international_id="";
            }else{
            	table.triggers[actionId].action_name_I18N=getInternational;
            	getForm().find('.deploy-action-item-ul li[action_id="'+actionId+'"]').text(getInternational[dx.user.language_id]);
            }
            //保存启用处理
            var is_using = getForm().find('.deploy-data-rule .is_using').is(":checked");
            if (is_using) {
                table.triggers[actionId].is_using = 1;
            } else {
                table.triggers[actionId].is_using = 0;
            }
            //保存is_one_data
            table.triggers[actionId].is_one_data = getForm().find('.deploy-data-rule .is_one_data').val();
//            var is_one_data = getForm().find('.deploy-data-rule .is_one_data').is(":checked");
//            if (is_one_data) {
//                table.triggers[actionId].is_one_data = 1;
//            } else {
//                table.triggers[actionId].is_one_data = 0;
//            }
        }

        var buttons = getForm().find('.deploy-action-button .deploy-action-item');
        for (var i = 0; i < buttons.length; i++) {
            var selectButton = $(buttons[i]).hasClass('deploy-selected-button');
            if (selectButton) {
                changeButtonData($(buttons[i]).attr('action_id'));
                break;
            }
        }
    }

    //设置显示样式数据
    function setRendersGridData($node) {
        if ($node.length != 1)
            return
        var columnData = [];
        if (table) {
            var renders = table.renders;
            dataOrder(renders);
            if (!isEmpty(renders))
                for (var i = 0; i < renders.length; i++) {
                    columnData.push({
                        table_id: renders[i].table_id,
                        seq: renders[i].seq,
                        formula: formatSql(renders[i].formula),
                        level: renders[i].level,
                        column: renders[i].column,
                        color: renders[i].color
                    });
                }
        }
        $node.datagrid('loadData', columnData);
    }

    //修改显示样式数据
    function changeRendersGridData($node) {
        if (isEmpty(table))
            return;
        var updateRows = $node.datagrid('getChanges');
        if (table.rendersChange == 1) {
            var rows = $node.datagrid('getRows');
            table.renders = rows;
        } else {
            if (!isEmpty(updateRows)) {
                var rows = $node.datagrid('getRows');
                if (!isEmpty(rows) && rows.length > 0) {
                    for (var i = 0; i < rows.length; i++) {
                        rows[i].seq = i;
                        delete rows[i]._selected;
                    }
                }
                table.renders = rows;
                table.rendersChange = 1;
            }
        }
    }

    //设置批量操作数据
    function setAutoGenGrid($node, table) {
        if ($node.length != 1)
            return;
        var columnData = [];
        if (table) {
            var autoGens = table.autoGens;
            if (!isEmpty(autoGens))
                for (var key in autoGens) {
                    columnData.push({
                        id: autoGens[key].id,
                        id_I18N: dx.i18n.message[autoGens[key].id],
                        table_id: autoGens[key].table_id,
                        ref_view: autoGens[key].ref_view,
                        exec_condition: autoGens[key].exec_condition,
                        gen_sql: autoGens[key].gen_sql
                    });
                }
        }
        $node.datagrid('loadData', columnData);
    }

    //修改批量操作数据
    function changeAutoGenGrid($node) {
        if (isEmpty(table))
            return;
        var updateRows = $node.datagrid('getChanges');
        if (!isEmpty(updateRows)) {
            var rows = $node.datagrid('getRows');
            var autogens={};
            for(var i=0;i<rows.length;i++){
            	autogens[rows[i].id]=rows[i];
            }
            table.autoGens = autogens;
            table.autoGenChange = 1;
        }
    }
  //修改自定义布局数据
    function changeCustomLayout($node) {
       
    }


    // 初始化列表格
    function initColumnGrid(tableByColumn) {
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
                		var columns = tableByColumn.columns;
        	            if (!isEmpty(columns)){
        	            	for (var i = 0; i < columns.length; i++) {
        	                	if(columns[i].column_name==row.column_name){
        	                		if(columns[i].columnChange==0 || isEmpty(columns[i].columnChange)){
        	                			columns[i].columnChange==1;
        	                		}
        	                	}
        	            	}
        	            }
                	}else{
            			row.column_name_I18N=getInternational;
            			var columns = tableByColumn.columns;
        	            if (!isEmpty(columns)){
        	            	for (var i = 0; i < columns.length; i++) {
        	                	if(columns[i].column_name==row.column_name){
        	                		var column=columns[i].i18n;
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
        	                			if(columns[i].columnChange==0 || isEmpty(columns[i].columnChange)){
            	                			columns[i].columnChange==1;
            	                		}
        	                		}
        	                		columns[i].i18n=getInternational;
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
        var dataGrid = getForm().find('.deploy-table-grid .deploy-column');
        //字段类型字典
        var optionsData = gridDiceSelect('d_datatype');
        var columnColumns = [[
            {field: 'rowid', title: 'rowid', width: 100, hidden: true},
            {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
            {field: 'seq', title: '顺序', width: 100, hidden: true},
            {field: 'column_name', title: msg('c_column_column_name'), width: 100, editor: {type: 'text'}},
            {field: 'data_type', title: msg('type'), width: 100,
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
                        data: optionsData
                    }
                }
            },
            {field: 'column_name_I18N', title: msg('cntablename'), width: 100, editor: {type:'interColumn'},
	           	 formatter: function(value,row,index){
	        		 if(!isEmpty(value)){
	        			 return row.column_name_I18N[dx.user.language_id];
	        		 }
	        	 }
	         },
            {field: 'is_id_column', title: msg('00000000000000000639'), width: 100,
                formatter: function (value, row, index) {
                    if (value == "1") {
                        return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                            '<input type="checkbox" columnName = ' + row.column_name + ' rowIndex = "' + index + '" value="' + value + '" checked/><label></label></div></div>';
                    } else {
                        return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                            '<input type="checkbox" columnName = ' + row.column_name + ' rowIndex = "' + index + '" value="' + value + '" /><label></label></div></div>';
                    }
                }
            },
            {field: 'group_name', title: msg('group'), width: 100}
        ]];
        var columnData = [];
        if (tableByColumn) {
            var columns = tableByColumn.columns;
            var orderColumns = dataOrder(columns);
            if (!isEmpty(orderColumns))
                for (var i = 0; i < orderColumns.length; i++) {
                    columnData.push({
                        rowid: orderColumns[i].rowid,
                        seq: orderColumns[i].seq,
                        column_name: orderColumns[i].column_name,
                        column_name_I18N: orderColumns[i].i18n,
                        data_type: orderColumns[i].data_type,
                        is_id_column: orderColumns[i].is_id_column
                    });
                }
        }
        initDataGrid(dataGrid, columnColumns, columnData, {
            onClickRow: function (rowIndex, rowData) {
                endAllRowEdit($(this));
                //点击一行时。先保存上一行的数据。
                var rows = $(this).datagrid('getSelections');
                if (!isEmpty(rows) && rows.length == 2) {
                    for (var i = 0; i < rows.length; i++) {
                        var oldRowIndex = $(this).datagrid('getRowIndex', rows[i]);
                        if (oldRowIndex != rowIndex) {
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
                        if (!isEmpty(table.cRUDColumns) && !isEmpty(table.cRUDColumns[rowData.column_name]))
                            columnData = table.cRUDColumns[rowData.column_name];
                        else
                            columnData = table.columnMap[rowData.column_name];
                        setColumnData(columnData);
                    }
                }
            },
            onCheck: function (rowIndex, rowData) {
            	var rows = $(this).datagrid('getSelections');
            	if (rows.length>0 && rows.length == 1) {
            		if (!isEmpty(rowData.column_name) && rowData.column_name == rows[0].column_name) {
            			endAllRowEdit($(this));
                        var columnData;
                        if (!isEmpty(table.cRUDColumns) && !isEmpty(table.cRUDColumns[rowData.column_name]))
                            columnData = table.cRUDColumns[rowData.column_name];
                        else
                            columnData = table.columnMap[rowData.column_name];
                        setColumnData(columnData);
                    }
            	}
            },
            onUncheck: function (rowIndex, rowData) {
                var rows = $(this).datagrid('getSelections');
            	if (rows.length>0) {
            		if(rows.length == 1 && !isEmpty(rows[0].column_name)){
                        var columnData;
                        if (!isEmpty(table.cRUDColumns) && !isEmpty(table.cRUDColumns[rows[0].column_name]))
                            columnData = table.cRUDColumns[rows[0].column_name];
                        else
                            columnData = table.columnMap[rows[0].column_name];
                        setColumnData(columnData);
            		}
            	}else{
            		 setColumnData("");
            	}
            },
            onDblClickRow: function (rowIndex, rowData) {
                var columnData;
                if (!isEmpty(table.cRUDColumns) && !isEmpty(table.cRUDColumns[rowData.column_name]))
                    columnData = table.cRUDColumns[rowData.column_name];
                else
                    columnData = table.columnMap[rowData.column_name];
                setColumnData(columnData);
            },
            onAfterEdit: function (rowIndex, rowData, changes) {
                if (isEmpty(table)) {
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
                if (!isEmpty(table.columns))
                    for (var i = 0; i < table.columns.length; i++) {
                        if (table.columns[i].rowid == rowData.rowid
                            && table.columns[i].column_name != rowData.column_name) {
                            //修改了列名
                            var oldColumnName = table.columns[i].column_name;
                            table.columns[i].column_name = rowData.column_name;
                            table.columns[i].oldColumnName = oldColumnName;
                            if (table.columns[i].columnChange != 2)
                                table.columns[i].columnChange = 1;
                            delete table.columnMap[oldColumnName];
                            table.columnMap[rowData.column_name] = table.columns[i];
                        }
                    }
                changeColumnGridData(rowData, rowIndex);

                setColumnGridData($(this));
            },
            onLoadSuccess: function (data) {
                //启用dnd支持
               // $(this).datagrid('enableDnd');
            },
            //可选-绑定dnd的触发事件
            onDrop: function (targetRow, sourceRow, point) {
                //拖拽某行到指定位置后触发
                var rows = $(this).datagrid('getRows');
                if (!isEmpty(rows) && rows.length > 0) {
                    for (var i = 0; i < rows.length; i++) {
                        if (table.columnMap.hasOwnProperty(rows[i].column_name)) {
                            table.columnMap[rows[i].column_name].seq = i;
                            if (table.columnMap[rows[i].column_name].columnChange != 2)
                                table.columnMap[rows[i].column_name].columnChange = 1;
                        }
                    }
                    for (var i = 0; i < table.columns.length; i++) {
                        table.columns[i].seq = table.columnMap[table.columns[i].column_name].seq;
                    }
                }
            },
            onRowContextMenu: function (e, rowIndex, rowData) {
                var top, left;
                if (!isEmpty($(e.target).parents("tr").position())) {
                    top = $(e.target).parents("tr").position().top;
                }
                if (!isEmpty($(e.target).parents("td")) && !isEmpty($(e.target).parents("td").position())) {
                    left = $(e.target).parents("td").position().left + e.offsetX;
                }
                $(e.target).parents(".datagrid-body").append('<ul class="dx-datagrid-select-group" style="position:absolute;top:' +
                    top + 'px;left:' + left + 'px;z-index:1;"><li>aaa</li><li>bbb</li></ul>');

                getForm().find('.deploy-table-grid .deploy-column').oncontextmenu = function (e) {
                    if (window.event) e = window.event;

                    return false;
                };
                var rows = $(this).datagrid('getSelections');
                var aaa = 1;
            }
        }, true);
        form.get().find(".deploy-column-grid").off("click", ".input-bt-Switcher");
        form.get().find(".deploy-column-grid").on("click", ".input-bt-Switcher", function () {
            if (isEmpty(table))
                return;
            var columnName = $(this).find("input").attr('columnName');
            var column;
            if (!isEmpty(table.cRUDColumns) && !isEmpty(table.cRUDColumns[columnName])) {
                column = table.cRUDColumns[columnName]
            } else {
                column = table.columnMap[columnName];
            }
            if (column.is_id_column == 0) {
                if (!isEmpty(table.cRUDColumns) && !isEmpty(table.cRUDColumns[columnName])) {
                    table.cRUDColumns[columnName].is_id_column = 1;
                }
                if (!isEmpty(table.columnMap) && !isEmpty(table.columnMap[columnName])) {
                    table.columnMap[columnName].is_id_column = 1;
                }
                if (!isEmpty(table.columns)) {
                    for (var i = 0; i < table.columns.length; i++) {
                        if (table.columns[i].column_name == columnName) {
                            table.columns[i].is_id_column = 1;
                            if (isEmpty(table.columns[i].columnChange) ||
                                table.columns[i].columnChange != 2)
                                table.columns[i].columnChange = 1;
                            break;
                        }
                    }
                }
            } else {
                if (!isEmpty(table.cRUDColumns) && !isEmpty(table.cRUDColumns[columnName])) {
                    table.cRUDColumns[columnName].is_id_column = 0;
                }
                if (!isEmpty(table.columnMap) && !isEmpty(table.columnMap[columnName])) {
                    table.columnMap[columnName].is_id_column = 0;
                }
                if (!isEmpty(table.columns)) {
                    for (var i = 0; i < table.columns.length; i++) {
                        if (table.columns[i].column_name == columnName) {
                            table.columns[i].is_id_column = 0;
                            if (isEmpty(table.columns[i].columnChange) ||
                                table.columns[i].columnChange != 2)
                                table.columns[i].columnChange = 1;
                            break;
                        }
                    }
                }
            }
            if (!isEmpty(table.columnMap) && !isEmpty(table.columnMap[columnName]))
                if (isEmpty(table.columnMap[columnName].columnChange) ||
                    table.columnMap[columnName].columnChange != 2)
                    table.columnMap[columnName].columnChange = 1;
            if (!isEmpty(table.cRUDColumns) && !isEmpty(table.cRUDColumns[columnName]))
                if (isEmpty(table.cRUDColumns[columnName].columnChange) ||
                    table.cRUDColumns[columnName].columnChange != 2)
                    table.cRUDColumns[columnName].columnChange = 1;
        });
        dataGrid.datagrid("selectRow",'0');
        var rowData=dataGrid.datagrid("getSelected");
        //给列右侧赋值
        if (!isEmpty(rowData)) {
            if (!isEmpty(rowData.column_name)) {
                var columnData;
                if (!isEmpty(table.cRUDColumns) && !isEmpty(table.cRUDColumns[rowData.column_name]))
                    columnData = table.cRUDColumns[rowData.column_name];
                else
                    columnData = table.columnMap[rowData.column_name];
                setColumnData(columnData);
            }
        }else{
        	 setColumnData('');
        }
    }

    //初始化排序规则表格
    function initOrderRuleGrid(table) {
        var dataGrid = getForm().find('.deploy-table-grid .deploy-order');
        //排序规则字典
        var orderOptionsData = gridDiceSelect('order_rule');
        //字段字典。传入了table 则取 table中的字段。没传入table 则取前一个页面的字段
        var columnOptionsData = getColumnOptionsData(table);
        var orderRuleColumns = [[
            {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
            {field: 'table_id', title: '表名', width: 100, hidden: true},
            {field: 'seq', title: '优先级', hidden: true, width: 100, editor: {type: 'text'}},
            {field: 'column_name', title: msg('field'), width: 100,
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
            {
                field: 'order_rule', title: msg('order_rule'), width: 100,
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
                        data: orderOptionsData
                    }
                }
            }
        ]];
        var columnData = [];
        if (table) {
            var orderBy = table.orderBy;
            if (!isEmpty(orderBy))
                for (var i = 0; i < orderBy.length; i++) {
                    columnData.push({
                        table_id: orderBy[i].table_id,
                        seq: orderBy[i].seq,
                        column_name: orderBy[i].column_name,
                        order_rule: orderBy[i].order_rule
                    });
                }
        }
        initDataGrid(dataGrid, orderRuleColumns, columnData, {
            onUncheck: function (rowIndex, rowData) {
                //取消选中时取消编辑行
                endEditing(this, rowIndex);
            },
            onAfterEdit: function (rowIndex, rowData, changes) {
                if (isEmpty(table)) {
                    return;
                }
                changeOrderRuleGrid($(this));
                setOrderRuleGrid($(this));
            },
            onLoadSuccess: function (data) {
                //启用dnd支持
                //$(this).datagrid('enableDnd');
            },
            //可选-绑定dnd的触发事件
            onDrop: function (targetRow, sourceRow, point) {
                //拖拽某行到指定位置后触发
                var rows = $(this).datagrid('getRows');
                var orderBy = table.orderBy;
                if (!isEmpty(rows) && rows.length > 0 && !isEmpty(orderBy) && orderBy.length > 0)
                    for (var i = 0; i < rows.length; i++) {
                        for (var j = 0; j < orderBy.length; j++) {
                            if (rows[i].column_name == orderBy[j].column_name) {
                                orderBy[j].seq = i;
                            }
                        }
                    }
            }
        }, false);
    }

    //初始化校验规则表格
    function initCheckRuleGrid(table) {
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
        	            table.checkRuleChange = 1;
                	}else{
            			row.msgI18n=getInternational;
            			row.error_msg_id=getInternational.international_id;
            			table.checkRuleChange = 1;
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
        var dataGrid = getForm().find('.deploy-table-grid .deploy-rule');
        //字段字典。传入了table 则取 table中的字段。没传入table 则取前一个页面的字段
        var columnOptionsData = getColumnOptionsData(table);
        //输入时机字典
        var checkTypeOptionsData = gridDiceSelect('checktype');
        //校验级别字典
        var checkLevelOptionsData = gridDiceSelect('check_level');
        var checkRuleColumns = [[
            {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
            {field: 'table_id', title: '表名', width: 100, hidden: true},
            {field: 'seq', title: '顺序', hidden: true, width: 100, editor: {type: 'text'}},
            {field: 'type', title: msg('checktype'), width: 100,
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
            {field: 'column_name', title: msg('field'), width: 100,
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
            {field: 'formula', title: msg('c_table_check_condition'), width: 100, editor: {type: 'text'}},
            {field: 'check_level', title: msg('check_level'), width: 100,
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
            {field: 'msgI18n', title: msg('in violation of the tip'), width: 100, editor: {type:'interErrorMsg'},
	           	 formatter: function(value,row,index){
	        		 if(!isEmpty(value)){
	        			 return row.msgI18n[dx.user.language_id];
	        		 }
	           	 }
            },
            {field: 'error_msg_param', title: msg('c_api_result_msg_param'), width: 100, editor: {type: 'text'}}
        ]];

        var columnData = [];
        if (table) {
            var checkRules = table.checkRules;
            if (!isEmpty(checkRules))
                for (var i = 0; i < checkRules.length; i++) {
                    columnData.push({
                        table_id: checkRules[i].table_id,
                        seq: checkRules[i].seq,
                        type: checkRules[i].type,
                        column_name: checkRules[i].column_name,
                        formula: checkRules[i].formula,
                        check_level: checkRules[i].check_level,
                        error_msg_id: checkRules[i].error_msg_id,
                        msgI18n: checkRules[i].msgI18n,
                        error_msg_param: checkRules[i].error_msg_param
                    });
                }
        }
        initDataGrid(dataGrid, checkRuleColumns, columnData, {
            onUncheck: function (rowIndex, rowData) {
                //取消选中时取消编辑行
                endEditing(this, rowIndex);
            },
            onAfterEdit: function (rowIndex, rowData, changes) {
                if (isEmpty(table)) {
                    return;
                }
                changeCheckRuleGridData($(this));
                setCheckRuleGridData($(this));
            },
            onLoadSuccess: function (data) {
                //启用dnd支持
                //$(this).datagrid('enableDnd');
            },
            //可选-绑定dnd的触发事件
            onDrop: function (targetRow, sourceRow, point) {
                //拖拽某行到指定位置后触发
                var rows = $(this).datagrid('getRows');
                var checkRules = table.checkRules;
                if (!isEmpty(rows) && rows.length > 0 && !isEmpty(checkRules) && checkRules.length > 0)
                    for (var i = 0; i < rows.length; i++) {
                        for (var j = 0; j < checkRules.length; j++) {
                            if (rows[i].column_name == checkRules[j].column_name) {
                                checkRules[j].seq = i;
                            }
                        }
                    }
            }
        }, false);
    }

    //初始化数据处理 增/删/改/查/操作 按钮
    function initActionButon(table) {
        function buildButtonDrop() {
            getForm().find('.deploy-action-button .deploy-action-item-ul').sortable('destroy');
            getForm().find('.deploy-action-button .deploy-action-item-ul').sortable().bind('sortupdate', function (a, b, c, d) {
                var buttons = getForm().find('.deploy-action-button .deploy-action-item');
                for (var i = 0; i < buttons.length; i++) {
                    var actionId = $(buttons[i]).attr('action_id');
                    if (isEmpty(actionId))
                        continue;
                    table.triggers[actionId].seq = i;
                    table.triggers[actionId].buttonActionChange = 1;
                }
            });
            getForm().find('.deploy-action-button .deploy-action-item-ul').disableSelection();
        }

        //初始化的时候显示图片。点击时隐藏图片
        getForm().find('.empty-tab-form').show();
        getForm().find('.deploy-data-rule-warp').hide();

        var $node = getForm().find('.deploy-action-button');
        $node.empty();
        if (isEmpty(table))
            return;
        if(isEmpty(table.listTriggers)){
        	var list=[];
        	var map=table.triggers;
        	for(var key in map){
        		map[key].action_id=key;
        		list.push(map[key]);
        	}
        	table.listTriggers=list;
        }
        var actionButton = table.listTriggers;
        if (!isEmpty(actionButton)) {
            $node.append('<ul class="deploy-action-item-ul"></ul>');
            for (var i = 0; i < actionButton.length; i++) {
                var html = '<li class="deploy-action-item"  action_id="' + actionButton[i].action_id + '">' +
                    actionButton[i].action_name_I18N[dx.user.language_id] + '</li>';
                $node.find('.deploy-action-item-ul').append(html);
            }
        }
        //拖动
        buildButtonDrop();
        getForm().find('.deploy-data-rule .action_name_international').val('');
        getForm().find('.deploy-data-rule .action_name_I18N').val('');
        getForm().find('.deploy-data-rule .action-system-type').val('');
        getForm().find('.deploy-data-rule .is_using').prop("checked", false);
        //增/删/改/查/操作 按钮点击事件
        getForm().find('.deploy-action-button').off('click', '.deploy-action-item');
        getForm().find('.deploy-action-button').on('click', '.deploy-action-item', function () {
            //初始化的时候显示图片。点击时隐藏图片
            getForm().find('.empty-tab-form').hide();
            getForm().find('.deploy-data-rule-warp').show();
            //点击时要保存上一个按钮的内容。
            changeButtonActionGridData();
            //保存上一按钮内容后在改按钮上加class 并移除其它class
            getForm().find('.deploy-action-button .deploy-action-item').removeClass('deploy-selected-button');
            $(this).addClass('deploy-selected-button');

            var action_id = $(this).attr('action_id');
            getForm().find('.deploy-action-button .deploy-action-item-remove').remove();

            $('.deploy-action-button .deploy-action-item').removeClass("active");
            $(this).addClass("active");

            //增删改查禁止删除
            if(!isEmpty(table.triggers[action_id])){
            	  if (table.triggers[action_id].system_type != 'add' &&
                          table.triggers[action_id].system_type != 'view' &&
                          table.triggers[action_id].system_type != 'edit' &&
                          table.triggers[action_id].system_type != 'delete'){
                      $(this).append('<span class="deploy-action-item-remove"></span>');
            	  }
            }
            if (!isEmpty(table))
                setButtonActionGridData(table, table.triggers[action_id]);
        });
        //添加按钮保存事件
        getForm().find('.deploy-data-rule .deploy-action-button-add').off('click').on('click', function () {
            var buttonName = $(this).parent().find('.deploy-action-button-input').val();
            if (isEmpty(buttonName))
                return;
            else {
                var newActionId = guid();
                var html = '<li class="deploy-action-item"  action_id="' + newActionId + '">' +
                    buttonName + '</li>';
                $node.find('.deploy-action-item-ul').append(html);
                if (isEmpty(table.triggers)) {
                    table.triggers = {};
                    table.triggers[newActionId] = {};
                    table.triggers[newActionId].seq = 1;
                } else {
                    table.triggers[newActionId] = {};
                    var maxSeq = 0;
                    for (var key in table.triggers) {
                        if (maxSeq < table.triggers[key].seq) {
                            maxSeq = table.triggers[key].seq;
                        }
                    }
                    table.triggers[newActionId].seq = maxSeq + 1;
                }
                table.triggers[newActionId].action_id = newActionId;
                table.triggers[newActionId].system_type = 'operation';
                table.triggers[newActionId].buttonActionChange = 2;
                table.triggers[newActionId].action_name_I18N = {};
                table.triggers[newActionId].action_name_I18N[dx.user.language_id] = buttonName;
                table.listTriggers.push(table.triggers[newActionId]);
                //默认点击新增的按钮事件
                getForm().find('.deploy-action-button .deploy-action-item[action_id='+newActionId+']').click();
            }
            //拖动
            buildButtonDrop();
        });
        //垃圾筐删除点击事件
        getForm().find('.deploy-action-button').off('click', '.deploy-action-item-remove');
        getForm().find('.deploy-action-button').on('click', '.deploy-action-item-remove', function () {
            var action_id = $(this).parent().attr('action_id');
            $(this).parent().remove();
            var trigger = table.triggers[action_id];
            if (isEmpty(trigger)) {
                return;
            } else if (trigger.buttonActionChange == 2) { //等于2为新加的按钮删除。不需要记录删除
                delete table.triggers[action_id];
            } else {
                if (isEmpty(table.deleteTrigger)) {
                    table.deleteTrigger = {};
                }
                table.deleteTrigger[action_id] = table.triggers[action_id];
                delete table.triggers[action_id];
            }
            for (var i = 0; i < table.listTriggers.length; i++) {
                if (table.listTriggers[i].action_id == action_id) {
                    table.listTriggers.splice(i, 1);
                }
            }
        })
    }

    //初始化数据处理表格
    function getSelectedButton() {
        var buttons = getForm().find('.deploy-action-button .deploy-action-item');
        for (var i = 0; i < buttons.length; i++) {
            var selectButton = $(buttons[i]).hasClass('deploy-selected-button');
            if (selectButton) {
                var actionId = $(buttons[i]).attr('action_id');
                if (isEmpty(actionId))
                    return;
                return table.triggers[actionId];
            }
        }
    }
  //初始化数据处理表格
    function initButtonActionGrid(table) {
        //initActionButon(table);
        //初始化处理前提
        function initActionPrerequistie(trigger) {
        	//表格文本编辑器
            $.extend($.fn.datagrid.defaults.editors, {    
            	interViolateMsgI18N: {    
                    init: function(container, options){    
                        var input = $('<div class="violate_msg_I18N"></div>').appendTo(container);    
                        return input;    
                    },     
                    getValue: function(target){    
                    	var getInternational=form.get().find(".violate_msg_I18N").internationalControl(true).getData();
                    	var index=dataGrid.datagrid('getRowIndex',getInterRow);
                    	var row=dataGrid.datagrid('getRows')[index];
                    	if(isEmpty(getInternational.international_id)){
                    		row.violate_msg_I18N={};
                    		row.violate_msg_I18N[dx.user.language_id]=getInternational.interValue;
                    		row.violate_msg_international_id=""
                    	}else{
                			row.violate_msg_I18N=getInternational;
                			row.violate_msg_international_id=getInternational.international_id;
                		}
                    	return row.violate_msg_I18N;
                    },    
                    setValue: function(target, value){
                    	getInterRow=dataGrid.datagrid('getSelected');
                    	if(isEmpty(getInterRow)){
                    		international(form.get().find(".violate_msg_I18N"),"","");
                    	}else{
                    		if(isEmpty(getInterRow.violate_msg_international_id)){
                        		international(form.get().find(".violate_msg_I18N"),"",getInterRow.violate_msg_I18N[dx.user.language_id]);
                        	}else{
                        		international(form.get().find(".violate_msg_I18N"),getInterRow.violate_msg_international_id,getInterRow.violate_msg_I18N[dx.user.language_id]);
                        	}
                    	}
                    } 
                } ,
                interParam: {    
                    init: function(container, options){ 
                    	var str ='<div class="input-1 clearfix">'
                    			+'<input class="urlValue" type="text" name="urlValue" readonly disabled/>'
                    			+'<button class="btn parameterInput" type="button" ></button></div>'
                        var input = $(str).appendTo(container);    
                        return input;    
                    },     
                    getValue: function(target){
                    	return form.get().find(".urlValue").val();
                    },    
                    setValue: function(target, value){
                    	form.get().find(".urlValue").val(value);
                    	var getInterRow=dataGrid.datagrid('getSelected');
                    	var index=dataGrid.datagrid('getRowIndex',getInterRow);
                    	var id = form.get().find('tr[datagrid-row-index='+index+']').find("td[field=event_url_id]").find('input.textbox-text').val();
                    	//加载参数输入控件
                        form.get().find(".parameterInput").on("click", function () {
                            var josnstr = form.get().find(".urlValue").val();
                            dataGrid.datagrid('endEdit',index);
                            dataGrid.datagrid('selectRow', index).datagrid('beginEdit', index);
                            var row=dataGrid.datagrid('getRows')[index];
                            var urlId = row.event_url_id;
                            var modalX = "40";
                            var modalY = "40";
                            form.get().find(".parameterInput").parameterInputControl(true).setData("onSubmit", function (data) {
                                form.get().find(".urlValue").val(data);
                            }, josnstr, urlId, modalX, modalY, "aa");

                        });
                    } 
                }    
            });  
            var dataGrid = getForm().find('.deploy-table-grid .deploy_action_prerequistie');
            //验证级别字典
            var checkLevelOptionsData = gridDiceSelect('check_level');
            var actionPrerequistieColumns = [[
                {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
                {field: 'table_action_id', title: 'ID', width: 100, hidden: true},
                {field: 'seq', title: '顺序', hidden: true, width: 100, editor: {type: 'text'}},
                {field: 'check_condition', title: msg('check_condition'), width: 100, editor: {type: 'text'}},
                {field: 'level', title: msg('check_level'), width: 100,
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
                {field: 'violate_msg_international_id', title: '违反信息ID', width: 100, hidden: true},
                {field: 'violate_msg_I18N', title: msg('violate_msg'), width: 100, editor: {type:'interViolateMsgI18N'},
		           	 formatter: function(value,row,index){
		        		 if(!isEmpty(value)){
		        			 return row.violate_msg_I18N[dx.user.language_id];
		        		 }
		           	 }
	            },
                {field: 'violate_msg_param', title: msg('violate_msg_param'), width: 100, editor: {type: 'text'}},
                {field: 'is_using', title: msg('start'), width: 100,
                    formatter: function (value, row, index) {
                        if (value == "1") {
                            return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                                '<input type="checkbox"  rowIndex = "' + index + '" checked/><label></label></div></div>';
                        } else {
                            return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                                '<input type="checkbox"  rowIndex = "' + index + '" /><label></label></div></div>';
                        }
                    }
                }
            ]];
            var columnData = [];
            if (trigger) {
                var condition = trigger.condition;
                if (!isEmpty(condition))
                    for (var i = 0; i < condition.length; i++) {
                        columnData.push({
                            table_action_id: condition[i].table_action_id,
                            seq: condition[i].seq,
                            check_condition: condition[i].check_condition,
                            level: condition[i].level,
                            violate_msg_international_id: condition[i].violate_msg_international_id,
                            violate_msg_I18N : condition[i].violate_msg_I18N,
                            violate_msg_param: condition[i].violate_msg_param,
                            is_using: condition[i].is_using
                        });
                    }
            }
            initDataGrid(dataGrid, actionPrerequistieColumns, columnData, {
                onAfterEdit: function (rowIndex, rowData, changes) {
                    if (isEmpty(table)) {
                        return;
                    }
                    changeButtonActionGridData();
                    setButtonActionGridData(table, getSelectedButton());
                },
                onLoadSuccess: function (data) {
                    //启用dnd支持
                   // $(this).datagrid('enableDnd');
                },
                onDrop: function (targetRow, sourceRow, point) {
                    //拖拽某行到指定位置后触发
                    var rows = $(this).datagrid('getRows');
                    var condition = getSelectedButton().condition;
                    if (!isEmpty(rows) && rows.length > 0 && !isEmpty(condition) && condition.length > 0)
                        for (var i = 0; i < rows.length; i++) {
                            for (var j = 0; j < condition.length; j++) {
                                if (rows[i].check_condition == condition[j].check_condition
                                    && rows[i].level == condition[j].level
                                    && rows[i].violate_msg_international_id == condition[j].violate_msg_international_id
                                    && rows[i].violate_msg_param == condition[j].violate_msg_param
                                    && rows[i].is_using == condition[j].is_using) {
                                    condition[j].seq = i;
                                }
                            }
                        }
                }
            }, false);
            form.get().find(".deploy_action_prerequistie_div").off("click", ".input-bt-Switcher");
            form.get().find(".deploy_action_prerequistie_div").on("click", ".input-bt-Switcher", function () {
                if (isEmpty(table))
                    return;
                var isUsing = $(this).find('input').is(':checked');
                var rowIndex = $(this).find("input").attr('rowIndex');
                changeButtonActionGridData(rowIndex, isUsing ? 1 : 0, null, null)
            });
        }

        //初始化触发事件
        function initDeployActionEvent(trigger) {
        	//表格文本编辑器
            $.extend($.fn.datagrid.defaults.editors, {    
            	interParam: {    
                    init: function(container, options){ 
                    	var str ='<div class="input-4 clearfix" style="position: relative;width:100%">'
                    			+'<input class="urlValue" type="text" name="urlValue" readonly disabled/>'
                    			+'<button class="btn parameterInput" type="button" ></button></div>'
                        var input = $(str).appendTo(container);    
                        return input;    
                    },     
                    getValue: function(target){
                    	return form.get().find(".urlValue").val();
                    },    
                    setValue: function(target, value){
                    	form.get().find(".urlValue").val(value);
                    	var getInterRow=dataGrid.datagrid('getSelected');
                    	var index=dataGrid.datagrid('getRowIndex',getInterRow);
                    	var id = form.get().find('tr[datagrid-row-index='+index+']').find("td[field=event_url_id]").find('input.textbox-text').val();
                    	//加载参数输入控件
                        form.get().find(".parameterInput").on("click", function () {
                            var josnstr = form.get().find(".urlValue").val();
                            dataGrid.datagrid('endEdit',index);
                            dataGrid.datagrid('selectRow', index).datagrid('beginEdit', index);
                            var row=dataGrid.datagrid('getRows')[index];
                            var urlId = row.event_name;
                            var modalX = "40";
                            var modalY = "40";
                            form.get().find(".parameterInput").parameterInputControl(true).setData("onSubmit", function (data) {
                                form.get().find(".urlValue").val(data);
                            }, josnstr, urlId, modalX, modalY, "aa");

                        });
                    } 
                }    
            });  
            var dataGrid = getForm().find('.deploy-table-grid .deploy_action_event');
            var eventTypeOptionsData = gridDiceSelect('event_type');
            var urlSelectOptionsData = getUrlSelect();
            var actionEventColumns = [[
                {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
                {field: 'event_id', title: '事件ID', width: 100, hidden: true},
                {field: 'table_action_id', title: '父表ID', width: 100, hidden: true},
                {field: 'seq', title: '顺序', hidden: true, width: 100, editor: {type: 'text'}},
                {
                    field: 'event_type', title: msg('event_type'), width: 100,
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
                            data: eventTypeOptionsData
                        }
                    }
                },
                {
                    field: 'event_name', title: msg('event_name'), width: 100,
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
                            data: urlSelectOptionsData
                        }
                    }
                },
                {field: 'event_param', title: msg('event_parame'), width: 100, editor: {type: 'interParam'},
	            	formatter: function(value,row,index){
		        		 if(!isEmpty(value)){
		        			 return row.event_param;
		        		 }else{
		        			 return "";
		        		 }
		        	 }
	            },
                {
                    field: 'is_using', title: msg('start'), width: 100,
                    formatter: function (value, row, index) {
                        if (value == "1") {
                            return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                                '<input type="checkbox"  rowIndex = "' + index + '" checked/><label></label></div></div>';
                        } else {
                            return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                                '<input type="checkbox"  rowIndex = "' + index + '" /><label></label></div></div>';
                        }
                    }
                }
            ]];
            var columnData = [];
            if (trigger) {
                var apis = trigger.api;
                if (!isEmpty(apis))
                    for (var i = 0; i < apis.length; i++) {
                        columnData.push({
                            event_id: apis[i].event_id,
                            table_action_id: apis[i].table_action_id,
                            seq: apis[i].seq,
                            event_type: apis[i].event_type,
                            event_name: apis[i].event_name,
                            event_param: apis[i].event_param,
                            is_using: apis[i].is_using
                        });
                    }
            }
            initDataGrid(dataGrid, actionEventColumns, columnData, {
                onAfterEdit: function (rowIndex, rowData, changes) {
                    if (isEmpty(table)) {
                        return;
                    }
                    changeButtonActionGridData();
                    setButtonActionGridData(table, getSelectedButton());
                },
                onLoadSuccess: function (data) {
                    //启用dnd支持
                   // $(this).datagrid('enableDnd');
                },
                onDrop: function (targetRow, sourceRow, point) {
                    //拖拽某行到指定位置后触发
                    var rows = $(this).datagrid('getRows');
                    var condition = getSelectedButton().condition;
                    if (!isEmpty(rows) && rows.length > 0 && !isEmpty(condition) && condition.length > 0)
                        for (var i = 0; i < rows.length; i++) {
                            for (var j = 0; j < condition.length; j++) {
                                if (rows[i].event_id == condition[j].event_id
                                    && rows[i].event_type == condition[j].event_type
                                    && rows[i].event_name == condition[j].event_name
                                    && rows[i].event_param == condition[j].event_param
                                    && rows[i].is_using == condition[j].is_using) {
                                    condition[j].seq = i;
                                }
                            }
                        }
                }
            }, false);
            form.get().find(".deploy_action_event_div").off("click", ".input-bt-Switcher");
            form.get().find(".deploy_action_event_div").on("click", ".input-bt-Switcher", function () {
                if (isEmpty(table))
                    return;
                var isUsing = $(this).find('input').is(':checked');
                var rowIndex = $(this).find("input").attr('rowIndex');
                changeButtonActionGridData(null, null, rowIndex, isUsing ? 1 : 0)
            });
        }

        initActionButon(table);
        initActionPrerequistie();
        initDeployActionEvent();
    }
    //显示样式的整行应用的点击事件
    form.get().find(".deploy-render_div").off("click", ".input-bt-Switcher");
    form.get().find(".deploy-render_div").on("click", ".input-bt-Switcher", function () {
    	var dataGrid = getForm().find('.deploy-table-grid .deploy-render');
        var rows = dataGrid.datagrid('getRows');
        var level = $(this).find('input').is(':checked');
        var rowIndex = $(this).find("input").attr('rowIndex');
        if (level) {
            rows[rowIndex].level = 1;
        } else {
            rows[rowIndex].level = 0;
        }
        table.rendersChange = 1;
        table.renders = rows;
    });
    //初始化显示样式表格
    function initRendersGrid(table) {
        var dataGrid = getForm().find('.deploy-table-grid .deploy-render');
        //字段字典。传入了table 则取 table中的字段。没传入table 则取前一个页面的字段
        var columnOptionsData = getColumnOptionsData(table);
        var rendersColumns = [[
            {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
            {field: 'table_id', title: '表名', width: 100, hidden: true},
            {field: 'seq', title: '顺序', hidden: true, width: 100, editor: {type: 'text'}},
            {field: 'formula', title: msg('c_table_check_condition'), width: 100, editor: {type: 'text'}},
            {field: 'level', title: msg('line_apply'), width: 100,
                formatter: function (value, row, index) {
                    if (value == "1") {
                        return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                            '<input type="checkbox" columnName = ' + row.column_name + ' rowIndex = "' + index + '" value="' + value + '" checked/><label></label></div></div>';
                    } else {
                        return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                            '<input type="checkbox" columnName = ' + row.column_name + ' rowIndex = "' + index + '" value="' + value + '" /><label></label></div></div>';
                    }
                }
            },
            {field: 'column', title: msg('field'), width: 100,
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
            {field: 'color', title: msg('style_effect'), width: 100,
                styler: function (value, row, index) {
                    return value;
                },
                formatter: function (value, row, index) {
                    return '文字效果';
                }
            }
        ]];
        var columnData = [];
        if (table) {
            var renders = table.renders;
            if (!isEmpty(renders))
                for (var i = 0; i < renders.length; i++) {
                    columnData.push({
                        table_id: renders[i].table_id,
                        seq: renders[i].seq,
                        formula: renders[i].formula,
                        level: renders[i].level,
                        column: renders[i].column,
                        color: renders[i].color
                    });
                }
        }
        initDataGrid(dataGrid, rendersColumns, columnData, {
            onUncheck: function (rowIndex, rowData) {
                //取消选中时取消编辑行
                endEditing(this, rowIndex);
            },
            onAfterEdit: function (rowIndex, rowData, changes) {
                if (isEmpty(table)) {
                    return;
                }
                changeRendersGridData($(this));
                setRendersGridData($(this));
            },
            onLoadSuccess: function (data) {
                //启用dnd支持
               // $(this).datagrid('enableDnd');
            },
            onDrop: function (targetRow, sourceRow, point) {
                //拖拽某行到指定位置后触发
                var rows = $(this).datagrid('getRows');
                var renders = table.renders;
                if (!isEmpty(rows) && rows.length > 0 && !isEmpty(renders) && renders.length > 0)
                    for (var i = 0; i < rows.length; i++) {
                        for (var j = 0; j < renders.length; j++) {
                            if (rows[i].column == renders[j].column) {
                                renders[j].seq = i;
                            }
                        }
                    }
            },
            onClickCell: function (rowIndex, field, value) {
                if (field != 'color')
                    return;
                var that = this;
                BootstrapDialog.show({
                    title: '',
                    message: getColumnCorlorHtml(),
                    buttons: [
                        {
                            label: '取消',
                            cssClass: "button-color4",
                            action: function (dialog) {
                                dialog.close();
                            }
                        },{
                        label: '确认',
                        cssClass: "button-color3",
                        action: function (dialog) {
                            var colorValue = '';
                            if (!isEmpty(dialog.columnStyle)) {
                                for (var key in dialog.columnStyle)
                                    colorValue += key + ":" + (isEmpty(dialog.columnStyle[key]) ? '' : dialog.columnStyle[key]) + ";"
                            }
                            var rows = $(that).datagrid('getRows');
                            rows[rowIndex][field] = colorValue;
                            $(that).datagrid('updateRow', {
                                index: rowIndex,
                                row: rows[rowIndex]
                            });
                            $(that).datagrid('refreshRow', rowIndex);
                            table.rendersChange = 1;
                            dialog.close();
                        }
                    }],
                    animate: false,
                    cssClass: 'deploy-render-color',
                    onshown: function (dialog) {
                    	if(!isEmpty(value)){
                    		var str = value.split(";"); 
                    		for(var i=0;i<str.length-1;i++){
                    			var key=str[i].split(":"); 
                    			 switch (key[0]) {
                    				case "color":
                    					$(dialog.$modalDialog).find('.font-color').val(key[1]);
                    					if (isEmpty(dialog.columnStyle))
                                            dialog.columnStyle = {};
                                        dialog.columnStyle.color = key[1];
                    					break;
                    				case "background":
                    					$(dialog.$modalDialog).find('.background-color').val(key[1]);
                    					if (isEmpty(dialog.columnStyle))
                                            dialog.columnStyle = {};
                                        dialog.columnStyle.background = key[1];
                    					break;
                    				case "font-weight":
                    					if(key[1]=="bold"){
                    						$(".font-style-bold").addClass("isActive");
                    						 if (isEmpty(dialog.columnStyle))
                                                 dialog.columnStyle = {};
                    						 dialog.columnStyle['font-weight'] = "bold";
                    					}else{
                    						if (isEmpty(dialog.columnStyle))
                                                dialog.columnStyle = {};
                   						 dialog.columnStyle['font-weight'] = "normal";
                    					}
                    					break;
                    				case "font-style":
                    					if(key[1]=="italic"){
                    						$(".font-style-italic").addClass("isActive");
                    						if (isEmpty(dialog.columnStyle))
                                                dialog.columnStyle = {};
                    						dialog.columnStyle['font-style'] = "italic";
                    					}else{
                    						if (isEmpty(dialog.columnStyle))
                                                dialog.columnStyle = {};
                    						dialog.columnStyle['font-style'] = "normal";
                    					}
                    					break;
                    				default:
                    					break;
                    				}
                    		}
                    	}
                        $(dialog.$modalDialog).find('.demo-font').attr('style', value);
                        $(dialog.$modalDialog).find('.font-color').colorpicker({
                            strings: ",,,",
                            history: false
                        });
                        $(dialog.$modalDialog).find('.font-color').on("change.color", function (event, color) {
                            if (isEmpty(dialog.columnStyle))
                                dialog.columnStyle = {};
                            dialog.columnStyle.color = color;
                            $(dialog.$modalDialog).find('.demo-font').css({color: color});
                        });
                        $(dialog.$modalDialog).find('.background-color').colorpicker({
                            strings: ",,,",
                            history: false
                        });
                        $(dialog.$modalDialog).find('.background-color').on("change.color", function (event, color) {
                            if (isEmpty(dialog.columnStyle))
                                dialog.columnStyle = {};
                            dialog.columnStyle.background = color;
                            $(dialog.$modalDialog).find('.demo-font').css({background: color});
                        });

                        $(dialog.$modalDialog).find(".deploy-style-font").on("click",".btn",function () {
                            if($(this).hasClass("font-style-bold")){
                                if($(this).hasClass("isActive")){
                                    $(this).removeClass("isActive");
                                    if (isEmpty(dialog.columnStyle))
                                        dialog.columnStyle = {};
                                    dialog.columnStyle['font-weight'] = "normal";
                                    $(this).parents(".deploy-render-color").find(".demo-font").css("font-weight","normal").removeClass("isBold");
                                }else {
                                    $(this).addClass("isActive");
                                    if (isEmpty(dialog.columnStyle))
                                        dialog.columnStyle = {};
                                    dialog.columnStyle['font-weight'] = "bold";
                                    $(this).parents(".deploy-render-color").find(".demo-font").css("font-weight","bold").addClass("isBold");
                                }
                            }else if($(this).hasClass("font-style-italic")) {
                                if($(this).hasClass("isActive")){
                                    $(this).removeClass("isActive");
                                    if (isEmpty(dialog.columnStyle))
                                        dialog.columnStyle = {};
                                    dialog.columnStyle['font-style'] = "normal";
                                    $(this).parents(".deploy-render-color").find(".demo-font").css("font-style","normal").removeClass("isItalic")
                                }else {
                                    $(this).addClass("isActive");
                                    if (isEmpty(dialog.columnStyle))
                                        dialog.columnStyle = {};
                                    dialog.columnStyle['font-style'] = "italic";
                                    $(this).parents(".deploy-render-color").find(".demo-font").css("font-style","italic").addClass("isItalic");
                                }
                            }
                        })
                    },
                    onhidden: function (dialog) {

                    }
                });
            }
        }, false);
    }

    //初始化批量操作表格
    function initAutoGenGrid(table) {
    	//表格文本编辑器
        $.extend($.fn.datagrid.defaults.editors, {    
        	interColumn: {    
                init: function(container, options){    
                    var input = $('<div class="id_I18N_batch_operation"></div>').appendTo(container);    
                    return input;    
                },     
                getValue: function(target){   
                	var getInternational=form.get().find(".id_I18N_batch_operation").internationalControl(true).getData();
                	var getInterRow=dataGrid.datagrid('getSelected');
                	var index=dataGrid.datagrid('getRowIndex',getInterRow);
                	var row=dataGrid.datagrid('getRows')[index];
                	var getuuid="";
                	if(isEmpty(row)){
                		getuuid=uuid(10,16);
                		row.id = getuuid;
                	}
                	if(isEmpty(getInternational.international_id)){
                		row.id_I18N={};
                		row.id_I18N[dx.user.language_id]=getInternational.interValue;
                		row.id_I18N.international_id=""
                		var autoGens = table.autoGens;
                		if(isEmpty(autoGens[row.id])){
                			autoGens[row.id]={};
                			autoGens[row.id].id=getuuid;
                		}
                		row.international_id="";
                		autoGens[row.id].international_id="";
                		if(table.autoGenChange==0 || isEmpty(table.autoGenChange)){
             				table.autoGenChange = 1;
            			}
                	}else{
            			row.id_I18N=getInternational;
            			var autoGens = table.autoGens;
            			autoGens[row.id].international_id=getInternational.international_id;
            			if(table.autoGenChange==0 || isEmpty(table.autoGenChange)){
              				table.autoGenChange = 1;
              			}
//            			var autoGens = table.autoGens;
//            			if(!isEmpty(autoGens[row.id])){
//            				var autoGen=autoGens[row.id];
//            				var autoGenIdI18n=dx.i18n.message[autoGen.international_id];
//            				var flag=false;
//  	                		if(isEmpty(autoGen.international_id) || autoGenIdI18n.international_id != getInternational.international_id){
//  	                			flag=true;
//  	                		}else if(!flag || autoGenIdI18n.cn != getInternational.cn){
//  	                			flag=true;
//  	                		}else if(!flag || autoGenIdI18n.en != getInternational.en){
//  	                			flag=true;
//  	                		}else if(!flag || autoGenIdI18n.jp != getInternational.jp){
//  	                			flag=true;
//  	                		}else if(!flag || autoGenIdI18n.other1 != getInternational.other1){
//  	                			flag=true;
//  	                		}else if(!flag || autoGenIdI18n.other2 != getInternational.other2){
//  	                			flag=true;
//  	                		}
//  	                		if(flag){
//  	                			if(table.autoGenChange==0 || isEmpty(table.autoGenChange)){
//  	                				table.autoGenChange = 1;
//  	                			}
//  	                		}
//  	                		autoGens[row.id].international_id=getInternational.international_id;
//            			}
            		}
                	return row.id_I18N;
                },    
                setValue: function(target, value){
                	var getInterRow=dataGrid.datagrid('getSelected');
                	var interValue="";
                	if(!isEmpty(value)){
                		interValue=value[dx.user.language_id];
                	}
                	if(isEmpty(getInterRow)){
                		international(form.get().find(".id_I18N_batch_operation"),"","");
                	}else{
                		if(isEmpty(getInterRow.international_id)){
                			
                    		international(form.get().find(".id_I18N_batch_operation"),"",interValue);
                    	}else{
                    		international(form.get().find(".id_I18N_batch_operation"),getInterRow.international_id,interValue);
                    	}
                	}
                } 
            }    
        });  
        
        var dataGrid = getForm().find('.deploy-table-grid .deploy-auto-gen');
        //数据来源表下拉框,参照视图下拉框。
        var tables = dx.table;
        var tableOptionsData = [];
        var viewOptionsData = [];
        for (var key in tables) {
            if (tables[key].table_type == 4 || tables[key].table_type == 5) {
                tableOptionsData.push({
                    key: key,
                    productname: tables[key].i18n[dx.user.language_id]
                });
            } else if (tables[key].table_type == 1) {
                viewOptionsData.push({
                    key: key,
                    productname: tables[key].i18n[dx.user.language_id]
                });
            }
        }
        var autoGenColumns = [[
            {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
            {field: 'id', title: '批量操作id', width: 100, hidden: true},
            {field: 'id_I18N', title: msg('batch_operation_name'), width: 100, editor: {type:'interColumn'},
	           	 formatter: function(value,row,index){
	        		 if(!isEmpty(value)){
	        			 return row.id_I18N[dx.user.language_id];
	        		 }else{
	        			 return "";
	        		 }
	        	 }
	         },
            {field: 'table_id', title: msg('data_source_name'), width: 100, hidden: true},
            { field: 'ref_view', title: msg('refer_view'), width: 100,
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
                        data: viewOptionsData
                    }
                }
            },
            {field: 'exec_condition', title: msg('c_auto_gen_exec_condition'), width: 100, editor: {type: 'text'}},
            {field: 'international_id', title: '国际化Id', width: 100, hidden: true},
            {field: 'gen_sql', title: msg('sql'), width: 100, editor: {type: 'text'}}
        ]];
        var columnData = [];
        if (table) {
            var autoGens = table.autoGens;
            if (!isEmpty(autoGens))
                for (var key in autoGens) {
                    columnData.push({
                        id: autoGens[key].id,
                        id_I18N: dx.i18n.message[autoGens[key].international_id],
                        international_id:autoGens[key].international_id,
                        table_id: autoGens[key].table_id,
                        ref_view: autoGens[key].ref_view,
                        exec_condition: autoGens[key].exec_condition,
                        gen_sql: autoGens[key].gen_sql
                    });
                }
        }
        initDataGrid(dataGrid, autoGenColumns, columnData, {
            onUncheck: function (rowIndex, rowData) {
                //取消选中时取消编辑行
                endEditing(this, rowIndex);
            }
        }, false);


    }

    //审批事件的通过事件启用的点击事件
    form.get().find(".deploy_approve_pass_event_div").off("click", ".input-bt-Switcher");
    form.get().find(".deploy_approve_pass_event_div").on("click", ".input-bt-Switcher", function () {
        var dataGrid = getForm().find('.deploy-table-grid .deploy_approve_pass_event');
        var rows = dataGrid.datagrid('getRows');
        var isUsing = $(this).find('input').is(':checked');
        var rowIndex = $(this).find("input").attr('rowIndex');
        if (isUsing) {
            rows[rowIndex].is_using = 1;
        } else {
            rows[rowIndex].is_using = 0;
        }
        table.approveEvent["pass"] = rows;
    });
    //审批事件结束行编辑
    function changeApproveEvent(dataGridClass, eventType) {
        if (isEmpty(table.approveEvent)) {
            table.approveEvent = {}
        }
        var rows = dataGridClass.datagrid('getRows');
        table.approveEvent[eventType] = rows;
    }

    //初始化审批通过事件
    function initApprovePassEvent() {
        var dataGrid = getForm().find('.deploy-table-grid .deploy_approve_pass_event');
        var eventTypeOptionsData = gridDiceSelect('event_type');
        var urlSelectOptionsData = getUrlSelect();
        var actionEventColumns = [[
            {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
            {field: 'seq', title: '顺序', hidden: true, width: 100, editor: {type: 'text'}},
            {field: 'event_type', title: msg('event_type'), width: 100,
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
                        data: eventTypeOptionsData
                    }
                }
            },
            {field: 'event_id', title: msg('event_name'), width: 100,
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
                        data: urlSelectOptionsData
                    }
                }
            },
            {field: 'event_param', title: msg('event_parame'), width: 100, editor: {type: 'text'}},
            {field: 'is_using', title: msg('start'), width: 100,
                formatter: function (value, row, index) {
                    if (value == "1") {
                        return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                            '<input type="checkbox"  rowIndex = "' + index + '" checked/><label></label></div></div>';
                    } else {
                        return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                            '<input type="checkbox"  rowIndex = "' + index + '" /><label></label></div></div>';
                    }
                }
            }
        ]];
        var columnData = [];
        if (table.approveEvent) {
            for (var key in table.approveEvent) {
                if (key == "pass") {
                    var passEvent = table.approveEvent[key]
                    for (var i = 0; i < passEvent.length; i++) {
                        columnData.push({
                            event_id: passEvent[i].event_id,
                            seq: passEvent[i].seq,
                            event_type: passEvent[i].event_type,
                            event_param: formatSql(passEvent[i].event_param),
                            is_using: passEvent[i].is_using
                        });
                    }
                }
            }
        }
        initDataGrid(dataGrid, actionEventColumns, columnData, {
            onAfterEdit: function (rowIndex, rowData, changes) {
                //修改表的审批事件数据
                changeApproveEvent(dataGrid, "pass");
            },
            onLoadSuccess: function (data) {
                //启用dnd支持
               // $(this).datagrid('enableDnd');
            },
            onDrop: function (targetRow, sourceRow, point) {
                //拖拽某行到指定位置后触发
                var rows = $(this).datagrid('getRows');
                if (table.approveEvent) {
                    for (var key in table.approveEvent) {
                        if (key == "pass") {
                            table.approveEvent[key] = rows;
                        }
                    }
                } else {
                    table.approveEvent["pass"] = rows;
                }
            }
        }, false);
    }

    //审批事件的驳回事件启用的点击事件
    form.get().find(".deploy_approve_reject_event_div").off("click", ".input-bt-Switcher");
    form.get().find(".deploy_approve_reject_event_div").on("click", ".input-bt-Switcher", function () {
        var dataGrid = getForm().find('.deploy-table-grid .deploy_approve_reject_event');
        var rows = dataGrid.datagrid('getRows');
        var isUsing = $(this).find('input').is(':checked');
        var rowIndex = $(this).find("input").attr('rowIndex');
        if (isUsing) {
            rows[rowIndex].is_using = 1;
        } else {
            rows[rowIndex].is_using = 0;
        }
        table.approveEvent["reject"] = rows;
    });
    //初始化审批驳回事件
    function initApproveRejectEvent() {
        var dataGrid = getForm().find('.deploy-table-grid .deploy_approve_reject_event');
        var eventTypeOptionsData = gridDiceSelect('event_type');
        var urlSelectOptionsData = getUrlSelect();
        var actionEventColumns = [[
            {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
            {field: 'seq', title: '顺序', hidden: true, width: 100, editor: {type: 'text'}},
            {
                field: 'event_type', title: msg('event_type'), width: 100,
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
                        data: eventTypeOptionsData
                    }
                }
            },
            {
                field: 'event_id', title: msg('event_name'), width: 100,
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
                        data: urlSelectOptionsData
                    }
                }
            },
            {field: 'event_param', title: msg('event_param'), width: 100, editor: {type: 'text'}},
            {
                field: 'is_using', title: msg('start'), width: 100,
                formatter: function (value, row, index) {
                    if (value == "1") {
                        return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                            '<input type="checkbox"  rowIndex = "' + index + '" checked/><label></label></div></div>';
                    } else {
                        return '<div class="input-bt-Switcher-wrap"><div class="input-bt-Switcher">' +
                            '<input type="checkbox"  rowIndex = "' + index + '" /><label></label></div></div>';
                    }
                }
            }
        ]];
        var columnData = [];
        if (table.approveEvent) {
            for (var key in table.approveEvent) {
                if (key == "reject") {
                    var rejectEvent = table.approveEvent[key]
                    for (var i = 0; i < rejectEvent.length; i++) {
                        columnData.push({
                            table_id: rejectEvent[i].table_id,
                            event_id: rejectEvent[i].event_id,
                            approve_event_type: 1,
                            seq: rejectEvent[i].seq,
                            event_type: rejectEvent[i].event_type,
                            event_param: formatSql(rejectEvent[i].event_param),
                            is_using: rejectEvent[i].is_using
                        });
                    }
                }
            }
        }
        initDataGrid(dataGrid, actionEventColumns, columnData, {
            onAfterEdit: function (rowIndex, rowData, changes) {
                //修改表的审批事件数据
                changeApproveEvent(dataGrid, "reject");
            },
            onLoadSuccess: function (data) {
                //启用dnd支持
               // $(this).datagrid('enableDnd');
            },
            onDrop: function (targetRow, sourceRow, point) {
                //拖拽某行到指定位置后触发
                var rows = $(this).datagrid('getRows');
                if (table.approveEvent) {
                    for (var key in table.approveEvent) {
                        if (key == "pass") {
                            table.approveEvent[key] = rows;
                        }
                    }
                } else {
                    table.approveEvent["pass"] = rows;
                }
            }
        }, false);
    }
    //初始化自定义布局数据
    function initCustomLayout(){
    	getForm().find(".custom_layout_table_div").html("");
    	getForm().find(".column_count").val(table.customLayout[0].column_count);
    	getForm().find(".column_count_edit").val(table.customLayout[0].column_count);
    	CreateTable(50,table.customLayout[0].column_count);
    }
    function setCustomLayout(){
    	getForm().find(".border_line").html();
    }
    getForm().find(".save_column_count").on("click",function(){
    	var column_count = getForm().find(".column_count_edit").val();
    	for(var i=0;i<table.customLayout.length;i++){
    		table.customLayout[i].column_count = column_count;
    	}
    	initCustomLayout();
    }); 
    getForm().find(".again_custom_layout").on("click",function(){
    	 getForm().find(".custom_layout_table_div td").html("");
    	 getForm().find(".all_column").html("");
    	 var columns=table.columns;
    	 for(var c=0;c<columns.length;c++){
    		 getForm().find(".all_column").append('<div class="column_name_id"><span>'+msg(columns[c].international_id)+'</span></div>');
    	 }
    });
    getForm().find(".self_motion_custom_layout").on("click",function(){
    	getForm().find(".all_column").html("");
    	initCustomLayout();
    });
    
    //动态添加表格
    function CreateTable(rowCount,cellCount)
    {
	    var tableform=$("<table border=\"1\">");
	    tableform.appendTo(getForm().find(".custom_layout_table_div"));
	    for(var i=0;i<rowCount;i++){
		    var tr=$("<tr></tr>");
		    tr.appendTo(tableform);
		    for(var j=0;j<cellCount;j++){
			    var td=$("<td width='100px' height='80px'></td>");
			    td.appendTo(tr);
		    }
	    }
	    tableform.appendTo(tableform);
	    getForm().find(".custom_layout_table_div").append("</table>");
	    var i=0;
	    var d=0;
	    var columns=table.columns;
	    for(var c=0;c<columns.length;c++){
	    	if(c%cellCount==0 && c!=0){
	    		getForm().find(".custom_layout_table_div").find('table').find("tr").eq(i).find("td").eq(d).html('<div>'+
	    				msg(columns[c].international_id)+'</div>');
	    		d=0;
	    		i++;
	    	}else{
	    		getForm().find(".custom_layout_table_div").find('table').find("tr").eq(i).find("td").eq(d).html('<div>'+
	    				msg(columns[c].international_id)+'</div>');
	    		d++;
	    	}
    	}
	    // getForm().find(".custom_layout_table_div").find('table').find("tr").eq(3).find("td").eq(1).html('<div>aa:<input type="text"/></div>');
	    var aa=getForm().find(".custom_layout_table_div").find('table').find("tr").eq(3).find("td").eq(1).html();
    }

    //结束所有行编辑
    function endAllRowEdit($node) {
        var rows = $node.datagrid('getRows');
        if (isEmpty(rows))
            return;
        for (var i = 0; i < rows.length; i++) {
            $($node).datagrid('endEdit', $($node).datagrid('getRowIndex', rows[i]));
        }
    }

    //新增行
    function addGridRow($node, row) {
        if (isEmpty(row)) {
            endAllRowEdit($node);
            $($node).datagrid('appendRow', {});
            var rows = $node.datagrid('getRows');
            var lastIndex = rows.length - 1;
            $($node).datagrid('unselectAll');
            $($node).datagrid('selectRow', lastIndex);
            $($node).datagrid('beginEdit', lastIndex);
            //如果为列新增。清空右侧值。
            if ($($node).hasClass('deploy-column')) {
                setColumnData();
            }
        }
    }

    //删除行
    function deleteGridRows($node, index) {
        $($node).datagrid('deleteRow', index);
    }

    //结束编辑行
    function endEditing(node, index) {
        $(node).datagrid('endEdit', index);
    }

    function onDbClickCell(index, field) {
        var $that = $(this);
        $that.datagrid('unselectAll');
        $that.datagrid('checkRow', index);
        endAllRowEdit($that);
        $that.datagrid('selectRow', index).datagrid('beginEdit', index);
        var ed = $that.datagrid('getEditor', {index: index, field: field});
        if (ed) {
            ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
        }
        editIndex = index;
    }

    // 加载左侧表树的数据
    function getColumnSelectedRow() {
        var rows = getForm().find('.deploy-table-grid .deploy-column').datagrid('getSelections');
        if (isEmpty(rows))
            return null
        if (rows.length != 1)
            return null;
        return rows[0];
    }

    function ajaxLoadGroup(form, callBack) {
        postJson('/tableDeploy/selectTableDeploy.do', {}, function (result) {
            callBack(result);
        });
    }

    //初始化左侧树
    function initLeftTree() {
        $tableTree.jstree({
            'core': {
                "multiple": false,
                'data': null,
                'dblclick_toggle': true,          //tree的双击展开
                "check_callback": true        //
            },
            "plugins": ["search", "contextmenu"]
        });
    }

    //左侧输赋值函数
    function callback(result) {
        if (!isEmpty(result))
            if (isEmpty(result)) {
                $tableTree.jstree(true).settings.core.data = null;
                $tableTree.jstree(true).refresh();
            } else {
                $tableTree.jstree(true).settings.core.data = result;
                $tableTree.jstree(true).refresh();
            }
    }

    //保存当前tab页的数据。切换tab页。以及点击保存按钮时
    function saveTabData() {
        var tabs = form.get().find('.deploy-grid-title li');
        for (var i = 0; i < tabs.length; i++) {
            if ($(tabs[i]).hasClass('active')) {
                var tabType = $(tabs[i]).find('a').attr('class');
                switch (tabType) {
                    case 'deploy-column' :
                        endAllRowEdit(form.get().find('.deploy-table-grid .deploy-column'));
                        //点击保存时保存正在编辑的列。并且没有在编辑状态的列
                        var rows = form.get().find('.deploy-table-grid .deploy-column').datagrid('getSelections');
                        if (!isEmpty(rows) && rows.length == 1) {
                            changeColumnGridData(rows[0], form.get().find('.deploy-table-grid .deploy-column').datagrid('getRowIndex', rows[0]));
                        }
                        return;
                    case 'deploy-order' :
                        endAllRowEdit(form.get().find('.deploy-table-grid .deploy-order'));
                        changeOrderRuleGrid(form.get().find('.deploy-table-grid .deploy-order'));
                        return;
                    case 'deploy-rule' :
                        endAllRowEdit(form.get().find('.deploy-table-grid .deploy-rule'));
                        changeCheckRuleGridData(form.get().find('.deploy-table-grid .deploy-rule'));
                        return;
                    case 'deploy-data-rule' :
                        endAllRowEdit(form.get().find('.deploy-table-grid .deploy_action_prerequistie'));
                        endAllRowEdit(form.get().find('.deploy-table-grid .deploy_action_event'));
                        changeButtonActionGridData(table);
                        return;
                    case 'deploy-render' :
                        endAllRowEdit(form.get().find('.deploy-table-grid .deploy-render'));
                        changeRendersGridData(form.get().find('.deploy-table-grid .deploy-render'));
                        return;
                    case 'deploy-auto-gen' :
                        endAllRowEdit(form.get().find('.deploy-table-grid .deploy-auto-gen'));
                        changeAutoGenGrid(form.get().find('.deploy-table-grid .deploy-auto-gen'));
                        return;
                    case 'deploy-approve-event' :
                    	var aa=form.get().find('.deploy-table-grid .deploy_approve_pass_event');
                        endAllRowEdit(form.get().find('.deploy-table-grid .deploy_approve_pass_event'));
                        endAllRowEdit(form.get().find('.deploy-table-grid .deploy_approve_reject_event'));
                        var dataGrid_pass = getForm().find('.deploy-table-grid .deploy_approve_pass_event');
                        var dataGrid_reject = getForm().find('.deploy-table-grid .deploy_approve_reject_event');
                        changeApproveEvent(dataGrid_pass, "pass");
                        changeApproveEvent(dataGrid_reject, "reject");
                        return;
                    case 'deploy-custom-form' :
                    	//自定义表单
                    	changeCustomLayout(form.get().find('.deploy-table-grid .custom-form'));
                        return;
                }
            }
        }
    }

    //左侧树新增节点
    getForm().find('.deploy-table-add').on('click', function () {
        if (isEmpty(nodeForAdd)) {
            alert(msg('no table selected'));
            return;
        }
        //the parent node (to create a root node use either "#" (string) or `null`)
        var par = nodeForAdd.id;
        //the data for the new node (a valid JSON object, or a simple string with the name)
        var node = getForm().find('.deploy-table-add-input').val();
        // the index at which to insert the node, "first" and "last" are also supported, default is "last"
        var pos = "last";
        // a function to be called once the node is created
        var callback = function (node) {
            function getDefaultTableAction() {
                var triggers = {};
                var newActionId = guid();
                triggers[newActionId] = {};
                triggers[newActionId].system_type = 'add';
                triggers[newActionId].seq = 1;
                triggers[newActionId].buttonActionChange = 2;
                triggers[newActionId].action_name_I18N = {};
                triggers[newActionId].action_name_I18N[dx.user.language_id] = getButtolLanguageName('Add');
                triggers[newActionId].is_using = 1;

                newActionId = guid();
                triggers[newActionId] = {};
                triggers[newActionId].system_type = 'view';
                triggers[newActionId].seq = 2;
                triggers[newActionId].buttonActionChange = 2;
                triggers[newActionId].action_name_I18N = {};
                triggers[newActionId].action_name_I18N[dx.user.language_id] = getButtolLanguageName('View');
                triggers[newActionId].is_using = 1;

                newActionId = guid();
                triggers[newActionId] = {};
                triggers[newActionId].system_type = 'edit';
                triggers[newActionId].seq = 3;
                triggers[newActionId].buttonActionChange = 2;
                triggers[newActionId].action_name_I18N = {};
                triggers[newActionId].action_name_I18N[dx.user.language_id] = getButtolLanguageName('Edit');
                triggers[newActionId].is_using = 1;

                newActionId = guid();
                triggers[newActionId] = {};
                triggers[newActionId].system_type = 'delete';
                triggers[newActionId].seq = 4;
                triggers[newActionId].buttonActionChange = 2;
                triggers[newActionId].action_name_I18N = {};
                triggers[newActionId].action_name_I18N[dx.user.language_id] = getButtolLanguageName('Delete');
                triggers[newActionId].is_using = 1;

                return triggers;
            }

            function getButtolLanguageName(type) {
                // type add 新增  delete 删除  view 查看  edit 编辑
                switch (dx.user.language_id) {
                    case 'cn' :
                        switch (type) {
                            case 'Add' :
                                return '新增';
                            case 'Delete' :
                                return '删除';
                            case 'View' :
                                return '查看';
                            case 'Edit' :
                                return '编辑';
                        }
                    case 'en' :
                        switch (type) {
                            case 'Add' :
                                return 'add';
                            case 'Delete' :
                                return 'delete';
                            case 'View' :
                                return 'select';
                            case 'Edit' :
                                return 'edit';
                        }
                    case 'jp' :
                        switch (type) {
                            case 'Add' :
                                return 'add';
                            case 'Delete' :
                                return 'delete';
                            case 'View' :
                                return 'select';
                            case 'Edit' :
                                return 'edit';
                        }
                    case 'other1' :
                        switch (type) {
                            case 'Add' :
                                return 'add';
                            case 'Delete' :
                                return 'delete';
                            case 'View' :
                                return 'select';
                            case 'Edit' :
                                return 'edit';
                        }
                    case 'other2' :
                        switch (type) {
                            case 'Add' :
                                return 'add';
                            case 'Delete' :
                                return 'delete';
                            case 'View' :
                                return 'select';
                            case 'Edit' :
                                return 'edit';
                        }
                }
            }

            //新增成功更改新增表集合数据。
            addTableMap[node.id] = {};
            addTableMap[node.id].updateType = 2;
            addTableMap[node.id].detail_disp_cols = 3;
            addTableMap[node.id].i18n = {};
            addTableMap[node.id].i18n[dx.user.language_id] = node.text;
            //增加默认的增删改查按钮。
            addTableMap[node.id].triggers = getDefaultTableAction();
            //设置父表名
            if (!isEmpty(nodeForAdd.original) && nodeForAdd.original.id != 0 && nodeForAdd.original.id != 1
                && nodeForAdd.original.id != 2 && nodeForAdd.original.id != 3 && nodeForAdd.original.id != 4) {
                addTableMap[node.id].parent_id = nodeForAdd.original.table_id;
                //设置表类型
                var table = getTableDesc(nodeForAdd.original.table_id);
                if (isEmpty(table))
                    addTableMap[node.id].table_type = 5;
                else
                    addTableMap[node.id].table_type = table.table_type;
            } else {
                //设置表类型
                addTableMap[node.id].table_type = nodeForAdd.original.id;
            }
            var column=[];
            var map={};
            var defaultCloumn={};
            defaultCloumn.cell_cnt=3;
            defaultCloumn.columnChange=2;
            defaultCloumn.column_name="cre_user";
            defaultCloumn.data_type=1;
            defaultCloumn.hidden=false;
            var i18ns={};
            i18ns.cn="创建者";
            i18ns.en="cre_user";
            i18ns.hidden=0;
            i18ns.international_id="00000000000000002174";
        	i18ns.jp=null;
        	i18ns.other1=null;
        	i18ns.other2=null;
        	defaultCloumn.i18n=i18ns;
        	defaultCloumn.international_id="00000000000000002174";
        	defaultCloumn.max_len=20;
        	defaultCloumn.ref_table_name="s_user";
        	defaultCloumn.table_id=node.id;
        	defaultCloumn.rowid=guid();
        	defaultCloumn.seq=100;
        	column.push(defaultCloumn);
        	map["cre_user"]=defaultCloumn;
        	
        	var defaultCloumn={};
            defaultCloumn.cell_cnt=3;
            defaultCloumn.columnChange=2;
            defaultCloumn.column_name="cre_date";
            defaultCloumn.data_type=12;
            defaultCloumn.hidden=false;
            var i18ns={};
            i18ns.cn="创建时间";
            i18ns.en="cre_date";
            i18ns.hidden=0;
            i18ns.international_id="00000000000000002175";
        	i18ns.jp=null;
        	i18ns.other1=null;
        	i18ns.other2=null;
        	defaultCloumn.i18n=i18ns;
        	defaultCloumn.international_id="00000000000000002175";
        	defaultCloumn.max_len=20;
        	defaultCloumn.ref_table_name=null;
        	defaultCloumn.table_id=node.id;
        	defaultCloumn.rowid=guid();
        	defaultCloumn.seq=101;
        	column.push(defaultCloumn);
        	map["cre_date"]=defaultCloumn;
        	
        	var defaultCloumn={};
            defaultCloumn.cell_cnt=3;
            defaultCloumn.columnChange=2;
            defaultCloumn.column_name="upd_user";
            defaultCloumn.data_type=1;
            defaultCloumn.hidden=false;
            var i18ns={};
            i18ns.cn="更新者";
            i18ns.en="upd_user";
            i18ns.hidden=0;
            i18ns.international_id="00000000000000002176";
        	i18ns.jp=null;
        	i18ns.other1=null;
        	i18ns.other2=null;
        	defaultCloumn.i18n=i18ns;
        	defaultCloumn.international_id="00000000000000002176";
        	defaultCloumn.max_len=20;
        	defaultCloumn.ref_table_name="s_user";
        	defaultCloumn.table_id=node.id;
        	defaultCloumn.rowid=guid();
        	defaultCloumn.seq=102;
        	column.push(defaultCloumn);
        	map["upd_user"]=defaultCloumn;
        	
        	var defaultCloumn={};
            defaultCloumn.cell_cnt=3;
            defaultCloumn.columnChange=2;
            defaultCloumn.column_name="upd_date";
            defaultCloumn.data_type=12;
            defaultCloumn.hidden=false;
            var i18ns={};
            i18ns.cn="更新时间";
            i18ns.en="upd_date";
            i18ns.hidden=0;
            i18ns.international_id="00000000000000002177";
        	i18ns.jp=null;
        	i18ns.other1=null;
        	i18ns.other2=null;
        	defaultCloumn.i18n=i18ns;
        	defaultCloumn.international_id="00000000000000002177";
        	defaultCloumn.max_len=20;
        	defaultCloumn.ref_table_name=null;
        	defaultCloumn.table_id=node.id;
        	defaultCloumn.rowid=guid();
        	defaultCloumn.seq=103;
        	column.push(defaultCloumn);
        	map["upd_date"]=defaultCloumn;
            
          
        	addTableMap[node.id].columnMap=map;
            addTableMap[node.id].columns =column;
            addTableMap[node.id].autoGens = {};
            addTableMap[node.id].checkRules = [];
            addTableMap[node.id].renders = [];
            addTableMap[node.id].orderBy = [];

            //添加成功后选中该节点
            $tableTree.jstree("select_node", node.id);
        };
        // internal argument indicating if the parent node was succesfully loaded
        var is_loaded = true;
        $tableTree.jstree("create_node", par, node, pos, callback, is_loaded);
    });
    //左侧输删除节点
    getForm().find('.deploy-left-tree-delect').on('click', function () {
        var deleteTablesNodeId = [];

        function getDeleteTabless(node) {
            var childs = ref.get_children_dom(node);
            for (var i = 0; i < childs.length; i++) {
                var nodes = $tableTree.jstree('get_node', childs[i]);
                deleteTablesNodeId.push(nodes.id);
                if (!isEmpty(nodes.children)) {
                    for (var j = 0; j < nodes.children.length; j++) {
                        deleteTablesNodeId.push(nodes.children[j]);
                        getDeleteTabless(nodes.children[j]);
                    }
                }
            }
        }

        var flag = confirm(msg('delete real?'));
        if (flag) {
            var ref = $tableTree.jstree(true);
            getDeleteTabless(nodeForAdd);
            deleteTablesNodeId.push(nodeForAdd.id);
            if (!isEmpty(deleteTablesNodeId)) {
                for (var i = 0; i < deleteTablesNodeId.length; i++) {
                    //删除的不为新增的才记录到删除里
                    if (isEmpty(addTableMap[deleteTablesNodeId[i]])) {
                        var tableNode = $tableTree.jstree('get_node', deleteTablesNodeId[i]);
                        deleteTableMap[deleteTablesNodeId[i]] = getTableDesc(tableNode.original.table_id);
                    }
                }
            }
            ref.delete_node(nodeForAdd);
        }
        getForm().find('.empty-form').show();
        getForm().find('.deploy-main-table-data').hide();
    });
    var $tableTree = getForm().find('.deploy-left-table-data');
    initLeftTree();
    //左侧search
    var to;
    var leftSearch = form.get().find('.deploy-left-tree-search');
    leftSearch.keyup(function () {
        if (to) {
            clearTimeout(to);
        }
        to = setTimeout(function () {
            $tableTree.jstree(true).search(leftSearch.val());
        }, 250);
    });
    ajaxLoadGroup(form, callback);

    var table; // updateType 1 修改。 2 新增。 3 删除
    var nodeForAdd = {}; //for新增节点。
    var eidtTableMap = {};
    var addTableMap = {}; //新增表集合。树ID 为key
    var deleteTableMap = {};  //删除表集合
    //树节点点击事件。点击后给table赋值。
    $tableTree.bind("changed.jstree", function (e, data) {
        if (!isEmpty(table)) {
            saveTabData();
            changeTableData();
        }
        //点击树的时候保存点击节点
        if (!isEmpty(data) && !isEmpty(data.node))
            nodeForAdd = data.node;

        if (isEmpty(data.node) || data.node.id == 0 || data.node.id == 1
            || data.node.id == 2 || data.node.id == 3 || data.node.id == 4) {
            getForm().find('.empty-form').show();
            getForm().find('.deploy-main-table-data').hide();
        }
        if (!isEmpty(data.node) && data.node.id != 0 && data.node.id != 1
            && data.node.id != 2 && data.node.id != 3 && data.node.id != 4) {
            form.get().find(".deploy-left-tree-delete").show();

            var tableName = data.node.original.table_id;
            if (!isEmpty(addTableMap[data.node.id])) {
                table = addTableMap[data.node.id];
            } else {
                table = getTableDesc(tableName);
                eidtTableMap = {};
                eidtTableMap[data.node.id] = table;
            }
            //checkFormula(tableName);
            //查询自定义布局数据 
            if (!isEmpty(table)){
            	var table_id=table.id;
            	postJson('/tableDeploy/selectCustomLayout.do',{table_id : table_id}, function (result) {
            		table.customLayout=result;
                }); 
            }
            /*if (!isEmpty(table) && !isEmpty(table.autoGens)) {
                var autoGen = [];
                for (var key in table.autoGens) {
                    autoGen.push(table.autoGens[key]);
                }
                table.autoGens = autoGen;
            }*/
            buidUUIDS(table);
            if (!isEmpty(table)) {
                getForm().find('.deploy-main-table-data').show();
                getForm().find('.empty-form').hide();
            } else {
                getForm().find('.empty-form').show();
                getForm().find('.deploy-main-table-data').hide();
            }
            //判断是否为新增的表。是新增的表移除表名只读。
            if (!isEmpty(table) && table.updateType == 2) {
                getForm().find('.deploy_table_table_id input').removeAttr('readonly');
            } else {
                getForm().find('.deploy_table_table_id input').attr('readonly', 'readonly');
            }
            setTableData();
            var tabs = getForm().find('.deploy-grid-title li');
            for (var i = 0; i < tabs.length; i++) {
                if ($(tabs[i]).is('.active')) {
                    var tabType = $(tabs[i]).find('a').attr('class');
                    switch (tabType) {
                    	case 'data-origin' :
                    		initDefaultGroupColumn();
                        case 'deploy-column' :
                            initColumnGrid(table);
                            return;
                        case 'deploy-order' :
                            initOrderRuleGrid(table);
                            return;
                        case 'deploy-rule' :
                            initCheckRuleGrid(table);
                            return;
                        case 'deploy-data-rule' :
                            initButtonActionGrid(table);
                            return;
                        case 'deploy-render' :
                            initRendersGrid(table);
                            return;
                        case 'deploy-auto-gen' :
                            initAutoGenGrid(table);
                            return;
                        case 'deploy-approve-event' :
                            initApprovePassEvent();
                            initApproveRejectEvent();
                            return;
                        case 'deploy-custom-form' :
                        	initCustomLayout();
                            return;
                            
                    }
                }
            }
        }
    });

    var editIndex = undefined;

    //初始化列表格。
    initColumnGrid();
    getForm().find('.tb_deploy_tab a[href="#data-origin"]').on('shown.bs.tab', function (e) {
    	changeTableData();
    	initDefaultGroupColumn();
    });
    getForm().find('.tb_deploy_tab a[href="#deploy-column"]').on('shown.bs.tab', function (e) {
        initColumnGrid(table);
    });
    getForm().find('.tb_deploy_tab a[href="#deploy-order"]').on('shown.bs.tab', function (e) {
        initOrderRuleGrid(table);
    });
    getForm().find('.tb_deploy_tab a[href="#deploy-rule"]').on('shown.bs.tab', function (e) {
        initCheckRuleGrid(table);
    });
    getForm().find('.tb_deploy_tab a[href="#deploy-data-rule"]').on('shown.bs.tab', function (e) {
        initButtonActionGrid(table);
    });
    getForm().find('.tb_deploy_tab a[href="#deploy-render"]').on('shown.bs.tab', function (e) {
        initRendersGrid(table);
    });
    getForm().find('.tb_deploy_tab a[href="#deploy-auto-gen"]').on('shown.bs.tab', function (e) {
        initAutoGenGrid(table);
    });
    getForm().find('.tb_deploy_tab a[href="#deploy-approve-event"]').on('shown.bs.tab', function (e) {
        initApprovePassEvent();
        initApproveRejectEvent();
    });
    getForm().find('.tb_deploy_tab a[href="#deploy-custom-form"]').on('shown.bs.tab', function (e) {
        initCustomLayout();
    });
    getForm().find('.deploy_action_tab a[href="#deploy_action_prerequistie_div"]').on('shown.bs.tab', function (e) {
    	getForm().find('.deploy-table-grid .deploy_action_prerequistie').datagrid('resize');
    });
    getForm().find('.deploy_action_tab a[href="#deploy_action_event_div"]').on('shown.bs.tab', function (e) {
    	getForm().find('.deploy-table-grid .deploy_action_event').datagrid('resize');
    });

    //表格新增行
    getForm().find('.deploy-table-column-add').on('click', function () {
        var grid = $(this).parent().parent().find('.deploy-grid');
        addGridRow(grid);
    });
    //表格删除行
    getForm().find('.deploy-table-column-delect').on('click', function () {
        var grid = $(this).parent().parent().find('.deploy-grid');
        var selectRows = grid.datagrid('getSelections');
        //删除column的列。要删除table中的列。并删除CRUDColumn中的列
        if ($(grid).hasClass('deploy-column')) {
            if (!isEmpty(table)) {
                for (var i = 0; i < selectRows.length; i++) {
                    if (!isEmpty(selectRows[i]) && !isEmpty(selectRows[i].column_name)) {
                        if (!isEmpty(table.columnMap[selectRows[i].column_name]) && table.columnMap[selectRows[i].column_name].columnChange!=2) {
                            if (isEmpty(table.deleteColumns))
                                table.deleteColumns = [];
                            table.deleteColumns.push(table.columnMap[selectRows[i].column_name])
                        }
                        if (!isEmpty(table.columnMap))
                            delete table.columnMap[selectRows[i].column_name];
                        if (!isEmpty(table.cRUDColumns))
                            delete table.cRUDColumns[selectRows[i].column_name];
                    }
                }
                table.columns = [];
                for (var column in table.columnMap) {
                    table.columns.push(table.columnMap[column])
                }
            }
        }
        for (var i = 0; i < selectRows.length; i++) {
            deleteGridRows(grid, grid.datagrid('getRowIndex', selectRows[i]));
        }
    });

    //点击tab切换时。把上一个tab的值保存进table
    getForm().find('.deploy-grid-title li').on('click', function () {
        saveTabData();
    });

    //修改右侧同步表格数据。
    getForm().find('.deploy_column_name input').on('change', function () {
        var grid = getForm().find('.deploy-table-grid .deploy-column');
        var updateRow = {};
        var columnName = $(this).val();
        endAllRowEdit(grid);
        var selectedRow = getColumnSelectedRow();
        if (isEmpty(selectedRow)) {
            return;
        } else {
            updateRow.index = grid.datagrid('getRowIndex', selectedRow);
            updateRow.row = {column_name: columnName};
            grid.datagrid('updateRow', updateRow);

            $(this).val(columnName);
        }
    });
    getForm().find('.deploy_data_type select').on('change', function () {
        var grid = getForm().find('.deploy-table-grid .deploy-column');
        var updateRow = {};
        var dataType = $(this).val();
        endAllRowEdit(getForm().find('.deploy-table-grid .deploy-column'));
        var selectedRow = getColumnSelectedRow();
        if (isEmpty(selectedRow)) {
            return;
        } else {
            updateRow.index = grid.datagrid('getRowIndex', selectedRow);
            updateRow.row = {data_type: dataType};
            grid.datagrid('updateRow', updateRow);
        }
    });
    //修改表名同步树节点名称
    getForm().find('.deploy_table_table_name_I18N input').on('change', function () {
        if (!isEmpty($(this).val()))
            $tableTree.jstree("rename_node", nodeForAdd, $(this).val());
    });
    function buildTableColumn(tables) {
        if (isEmpty(tables))
            return;
        for (var key in tables) {
            var table = tables[key];
            if (isEmpty(table) || isEmpty(table.columnMap))
                continue;
            var cRUDColumns = table.cRUDColumns;
            if (isEmpty(cRUDColumns))
                continue;
            //修改的在原先的中不存在为新增
            for (var columnKey in cRUDColumns) {
                if (isEmpty(columnKey)) {
                    alert(msg('column name is null'));
                    return;
                }
                //修改的在原先的中不存在为新增
                if (!table.columnMap.hasOwnProperty(columnKey)) {
                    cRUDColumns[columnKey].cell_cnt = 3;
                    table.columnMap[columnKey] = cRUDColumns[columnKey];
                    table.columnMap[columnKey].columnChange = 2;
                } else if (table.columnMap.hasOwnProperty(columnKey)) {
                    //修改的在原先中存在。则修改原先的列。
                    table.columnMap[columnKey] = cRUDColumns[columnKey];
                }
            }

            table.columns = [];
            for (var newColumn in table.columnMap) {
                table.columns.push(table.columnMap[newColumn]);
            }
            delete table.cRUDColumns;
        }
    }

    function buildAddTableColumn(tables) {
        if (isEmpty(tables))
            return;
        for (var key in tables) {
            delete tables[key].cRUDColumns;
        }
    }

    function buildDeleteColumn(tables) {
        if (isEmpty(tables))
            return;
        for (var key in tables) {
            delete tables[key].cRUDColumns;
            delete tables[key].autoGens;
        }
    }

    //表总保存按钮
    getForm().find('.deploy-table-submit').on('click', function () {
        //保存时的公式校验。
        //if (!checkTableFormula(table)){
        //    return;
        //}
        if (isEmpty(table)) {
            alert(msg('have no data save'));
        } else {
            //点击保存时保存当前tab页的数据。
            saveTabData();
            //点击保存时 保存table数据。
            changeTableData();
            var editTable;
            for (var key in eidtTableMap) {
                if (addTableMap.hasOwnProperty(key) || deleteTableMap.hasOwnProperty(key))
                    editTable = {};
            }
//            if (!isEmpty(addTableMap)) {
//                for (var key in addTableMap) {
//                    if (isEmpty(addTableMap[key].id)) {
//                        delete addTableMap[key];
//                    }
//                }
//            }
            //buildTableColumn(eidtTableMap);
            buildAddTableColumn(addTableMap);
            buildDeleteColumn(deleteTableMap);
            if(checkTableName()){
            	dx.processing.open();
                postJson('/tableDeploy/CRUTable.do',
                    {editData: eidtTableMap, addTables: addTableMap, deleteTables: deleteTableMap}, function (data) {
                        eidtTableMap = {};
                        addTableMap = {}; //新增表集合。树ID 为key
                        deleteTableMap = {};  //删除表集合
                        postJson('/data/cache/reload.do', function () {
                        	dxReload(function(){
                        		 ajaxLoadGroup(form, callback);
                                 dx.processing.close();
                                 dxToastAlert(msg('Saved successfully!'));
                        	});
                        })
                    });
            }
        }
    });

    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }

    function guid() {
        return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
    }
    function uuid(len, radix) {
        var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
            .split('');
        var uuid = [], i;
        radix = radix || chars.length;

        if (len) {
            for (i = 0; i < len; i++)
                uuid[i] = chars[0 | Math.random() * radix];
        } else {
            var r;
            uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
            uuid[14] = '4';
            for (i = 0; i < 36; i++) {
                if (!uuid[i]) {
                    r = 0 | Math.random() * 16;
                    uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
                }
            }
        }
        return uuid.join('');
    }
    function buidUUIDS(table) {
        if (table == null || isEmpty(table.columns))
            return;
        var columns = table.columns;
        for (var i = 0; i < columns.length; i++) {
            var rowid = guid();
            table.columns[i].rowid = rowid;
            table.columnMap[columns[i].column_name].rowid = rowid;
        }
    }

    function getColumnCorlorHtml() {
        var html = '<div class="clearfix deploy-style deploy-style-demo"><label class="label-1">样式类型</label><div class="input-1 demo-font">文字</div></div>';
        html += '<div class="clearfix deploy-style"><label class="label-1">字体颜色</label><input type="text" class="input-1 font-color"/></div>';
        html += '<div class="clearfix deploy-style"><label class="label-1">背景颜色</label><input type="text" class="input-1 background-color"/></div>';
        html += '<div class="clearfix deploy-style deploy-style-font"><label class="label-1">字体样式</label><div class="input-1 font-style"><span class="btn font-style-bold">B</span><span class="btn font-style-italic">I</span></div></div>';
        return html;
    }
    function initDefaultGroupColumn(){
    	getForm().find('.deploy_table_default_group_column').find('select option').remove();
    	getForm().find('.deploy_table_default_group_column').find('select').append('<option value=""></option>');
    	if(!isEmpty(table)){
    		var column=table.columns;
    		for(var i=0;i<column.length;i++){
    			if(!isEmpty(column[i].ref_table_name) || !isEmpty(column[i].dic_id)){
    				if(column[i].column_name==table.default_group_column){
    					getForm().find('.deploy_table_default_group_column').find('select').append('<option value="'+column[i].column_name+'" selected="selected">'+column[i].i18n[dx.user.language_id]+'</option>');
    				}else{
    					getForm().find('.deploy_table_default_group_column').find('select').append('<option value="'+column[i].column_name+'">'+column[i].i18n[dx.user.language_id]+'</option>');
    				}
    			}
    		}
    	}
    }

    function checkFormula(tableName){
        $('.deploy_default_value_input').g1Formula({table: tableName});
        $('.deploy_prefix_input').g1Formula({table: tableName});
        $('.deploy_suffix_input').g1Formula({table: tableName});
        $('.deploy_read_only_condition_input').g1Formula({table: tableName});
        $('.deploy_formula_input').g1Formula({table: tableName});
    }

    function checkTableFormula(table){
        var flag = true;
        if (isEmpty(table) || isEmpty(table.columns)) {
            return flag;
        }
        var columns = table.columns;
        for (var i=0; i<columns.length; i++){
            var formula, checkData;
            if (!isEmpty(columns[i].default_value)){
                formula = columns[i].default_value;
                checkData = $('.deploy_default_value_input').g1Formula(true).check(table.id, formula);
                if (!checkData.status){
                    alert('column: ' + columns[i].column_name + ', type: default_value (' + checkData.msg + ')');
                    flag = false;
                }
            }
            if (!isEmpty(columns[i].prefix)){
                formula = columns[i].prefix;
                checkData = $('.deploy_default_value_input').g1Formula(true).check(table.id, formula);
                if (!checkData.status){
                    alert('column: ' + columns[i].column_name + ', type: prefix (' + checkData.msg + ')');
                    flag = false;
                }
            }
            if (!isEmpty(columns[i].suffix)){
                formula = columns[i].suffix;
                checkData = $('.deploy_default_value_input').g1Formula(true).check(table.id, formula);
                if (!checkData.status){
                    alert('column: ' + columns[i].column_name + ', type: suffix (' + checkData.msg + ')');
                    flag = false;
                }
            }
            if (!isEmpty(columns[i].read_only_condition)){
                formula = columns[i].read_only_condition;
                checkData = $('.deploy_default_value_input').g1Formula(true).check(table.id, formula);
                if (!checkData.status){
                    alert('column: ' + columns[i].column_name + ', type: read_only_condition (' + checkData.msg + ')');
                    flag = false;
                }
            }
            if (!isEmpty(columns[i].formula)){
                formula = columns[i].formula;
                checkData = $('.deploy_default_value_input').g1Formula(true).check(table.id, formula);
                if (!checkData.status){
                    alert('column: ' + columns[i].column_name + ', type: formula (' + checkData.msg + ')');
                    flag = false;
                }
            }

        }
        return flag;
    }
    //表格向上移动按钮
    getForm().find('.datagrid_row_up').on("click",function(){
    	//var dataGrid = getForm().find('.deploy-table-grid .deploy-column');
    	var dataGrid = $(this).parent().parent().find('.deploy-grid');
    	var row = dataGrid.datagrid('getChecked');
    	dataGridMove(dataGrid,row,"up");
    	var rows = dataGrid.datagrid('getRows');
        if (!isEmpty(rows) && rows.length > 0) {
        	datagridMoveChange(dataGrid,rows)
        }
    });
    //表格向下移动按钮
    getForm().find('.datagrid_row_down').on("click",function(){
    	var dataGrid = $(this).parent().parent().find('.deploy-grid');
    	//var dataGrid = getForm().find('.deploy-table-grid .deploy-column');
    	var row = dataGrid.datagrid('getChecked');
    	dataGridMove(dataGrid,row,"down");
    	var rows = dataGrid.datagrid('getRows');
        if (!isEmpty(rows) && rows.length > 0) {
        	datagridMoveChange(dataGrid,rows)
        }
    });
    function datagridMoveChange(dataGrid,rows){
    	if($(dataGrid).hasClass("deploy-column")){
    		//字段信息
    		for (var i = 0; i < rows.length; i++) {
                if (table.columnMap.hasOwnProperty(rows[i].column_name)) {
                    table.columnMap[rows[i].column_name].seq = i;
                    if (table.columnMap[rows[i].column_name].columnChange != 2)
                        table.columnMap[rows[i].column_name].columnChange = 1;
                    table.columnMap[rows[i].column_name].formula = formatSql(table.columnMap[rows[i].column_name].formula);
                    table.columnMap[rows[i].column_name].default_value = formatSql(table.columnMap[rows[i].column_name].default_value);
                    table.columnMap[rows[i].column_name].read_only_condition = formatSql(table.columnMap[rows[i].column_name].read_only_condition);
                }
            }
            for (var i = 0; i < table.columns.length; i++) {
                table.columns[i].seq = table.columnMap[table.columns[i].column_name].seq;
            }
    	}else if($(dataGrid).hasClass("deploy-order")){
    		//排序规则
            var orderBy = table.orderBy;
            if (!isEmpty(rows) && rows.length > 0 && !isEmpty(orderBy) && orderBy.length > 0){
            	for (var i = 0; i < rows.length; i++) {
                    for (var j = 0; j < orderBy.length; j++) {
                        if (rows[i].column_name == orderBy[j].column_name) {
                            orderBy[j].seq = i;
                        }
                    }
                }
            }
    	}else if($(dataGrid).hasClass("deploy-rule")){
    		//校验规则
    		var checkRules = table.checkRules;
            if (!isEmpty(rows) && rows.length > 0 && !isEmpty(checkRules) && checkRules.length > 0){
            	for (var i = 0; i < rows.length; i++) {
                    for (var j = 0; j < checkRules.length; j++) {
                        if (rows[i].column_name == checkRules[j].column_name) {
                            checkRules[j].seq = i;
                            checkRules[j].formula = formatSql(checkRules[j].formula);
                        }
                    }
                }
            }
    	}else if($(dataGrid).hasClass("deploy_action_prerequistie")){
    		//处理前提
    		var condition = getSelectedButton().condition;
            if (!isEmpty(rows) && rows.length > 0 && !isEmpty(condition) && condition.length > 0){
            	for (var i = 0; i < rows.length; i++) {
                    for (var j = 0; j < condition.length; j++) {
                        if (rows[i].check_condition == condition[j].check_condition
                            && rows[i].level == condition[j].level
                            && rows[i].violate_msg_international_id == condition[j].violate_msg_international_id
                            && rows[i].violate_msg_param == condition[j].violate_msg_param
                            && rows[i].is_using == condition[j].is_using) {
                            condition[j].seq = i;
                            condition[j].formula = formatSql(condition[j].check_condition);
                        }
                    }
                }
            }
    	}else if($(dataGrid).hasClass("deploy_action_event")){
    		//触发事件
    		var condition = getSelectedButton().condition;
            if (!isEmpty(rows) && rows.length > 0 && !isEmpty(condition) && condition.length > 0){
            	for (var i = 0; i < rows.length; i++) {
                    for (var j = 0; j < condition.length; j++) {
                        if (rows[i].event_id == condition[j].event_id
                            && rows[i].event_type == condition[j].event_type
                            && rows[i].event_name == condition[j].event_name
                            && rows[i].event_param == condition[j].event_param
                            && rows[i].is_using == condition[j].is_using) {
                            condition[j].seq = i;
                            condition[j].event_param = formatSql(condition[j].event_param);
                        }
                    }
                }
            }
    	}else if($(dataGrid).hasClass("deploy-render")){
    		//显示样式
    		var renders = table.renders;
            if (!isEmpty(rows) && rows.length > 0 && !isEmpty(renders) && renders.length > 0){
            	for (var i = 0; i < rows.length; i++) {
                    for (var j = 0; j < renders.length; j++) {
                        if (rows[i].column == renders[j].column) {
                            renders[j].seq = i;
                            renders[j].formula = formatSql(renders[j].formula);
                        }
                    }
                }
            }
    	}
    }
    //复制
    form.get().find(".copyTableTree").on("click", function () {
    	var ref = $tableTree.jstree(true),
        sel = ref.get_selected();
    	var tableNode = $tableTree.jstree('get_node', sel[0]);
    	if (!sel.length) {
            return false;
        }
        ref.copy(sel);
        ref.paste(tableNode.parent);
    	var id = $tableTree.jstree('get_node', tableNode.parent).children[0];
    	addTableMap[id]=eidtTableMap[sel[0]];
    	addTableMap[id].updateType=2;
    });
    //check表名是否输入，是否重复
    function checkTableName(){
    	if(!isEmpty(addTableMap)){
    		for(var key in addTableMap){
    			var table_id = addTableMap[key].id;
    			if(isEmpty(table_id)){
    				addTableMap[key].i18n
        			alert(i18n(addTableMap[key].i18n)+"表表名不能为空");
        			return false;
        		}
    			//查询缓存中表名是否存在
        		var table = dx.table[table_id];
        		if(!isEmpty(table)){
        			alert(i18n(addTableMap[key].i18n)+"表表名重复");
        			return false;
        		}
    		}
    	}
    	return true;
    }
});
