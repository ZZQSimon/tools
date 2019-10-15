/**
 * Created by Administrator on 2017/9/1.
 */
registerInit('viewReport', function (form) {

    init();
    var dataGrid = $('#viewReportTable');
    var table_id = [];
    var report_type = gridDiceSelect("report_type");
	var report_disp_type = gridDiceSelect("report_disp_type");
	var report_file_type = gridDiceSelect("report_file_type");
	var tables = dx.table;
	for(var key in tables){
		if (tables[key].table_type != 2){
			table_id.push({
				selectId : key,
	            selectName : tables[key].i18n[dx.user.language_id]
	        });
		}
	}
    //从缓存中获取字典数据
    function gridDiceSelect(dictId){
	    var dict = dx.dict[dictId];
	    var OptionsData = [];
	    for(var key in dict){
	        OptionsData.push({
	        	selectId : key,
	        	selectName : dict[key][dx.user.language_id]
	        });
	    }
	    return OptionsData;
	}
    //初始化表格数据
    function init() {
		postJson('/viewReport/initViewReport.do', {}, function (data) {
			var totlerow = {
                    "total": data.total,
                    "rows": data.rows
                };
            var v_report = dx.table.v_report;
            var column = v_report.columns;
            var row;
            var gridCol=[{field: 'firstckbox', width: 80, checkbox: 'true'}];
            for(var i=0; i<column.length; i++){
            	if(column[i].column_name=="SQL"){
            		column[i].column_name="sql";
            	}
            	if(column[i].column_name=="SQL2"){
            		column[i].column_name="sql2";
            	}
				if(column[i].column_name=="table_id"){
					row=OptionData(table_id,column[i],"table_id");
				}else if(column[i].column_name=="report_type"){
					row=OptionData(report_type,column[i],"report_type");
				}else if(column[i].column_name=="report_disp_type"){
					row=OptionData(report_disp_type,column[i],"report_disp_type");
				}else if(column[i].column_name=="report_file_type"){
					row=OptionData(report_file_type,column[i],"report_file_type");
				}else if(column[i].column_name=="id"){
					row = {
		                field: column[i].column_name,
		                title: column[i].i18n[dx.user.language_id],
		                hidden: column[i]['hidden']
		    		};
				}else{
					row = {
		                field: column[i].column_name,
		                title: column[i].i18n[dx.user.language_id],
		                hidden: column[i]['hidden'],
		                editor: 'textbox'
		    		};
				}
				gridCol.push(row);
    		}
            easydatagrid(gridCol);
            dataGrid.datagrid('loadData', totlerow);
	    });
    }
    //获取表格下拉框数据
    function OptionData(selectOptionData,column,cloName){
    	console.log(selectOptionData);
		var row;
		row = {
	        field: column.column_name,
	        title: column.i18n[dx.user.language_id],
	        hidden: column['hidden'],
	        formatter:function(value,row){
	        	var options = this.editor.options.data;
	        	for (var i=0; i<options.length; i++){
	        	    if (options[i].selectId == value){
	        	        return options[i].selectName
	        	    }
	        	}
	        },
	        editor:{
	            type:'combobox',
	            options:{
	                valueField:'selectId',
	                textField:'selectName',
	                data : selectOptionData
	            }
	        }
	    };
		return row;
    }
    //保存
    function accept() {
        if (endEditing()) {
            var totleRow = getChanges();
            var insert = totleRow.insert;
            var deleted = totleRow.deleted;
            var updated = totleRow.updated;
           /* changSQL(insert);
            changSQL(deleted);
            changSQL(updated);*/
            dx.processing.open();
            postJson('/viewReport/saveViewReport.do', {insert: insert, deleted: deleted, update: updated}, function (data) {
            	postJson('/data/cache/reload.do', function(){
            		dxToastAlert(msg('Saved successfully!'));
            		dx.processing.close();
                })
            });
        }
    }
    function changSQL(dataList){
    	 for(var i=0;i<dataList.length;i++){
    		 dataList[i].sql=dataList[i].SQL;
    		 dataList[i].sql2=dataList[i].SQL2;
         	delete dataList[i].SQL;
         	delete dataList[i].SQL2;
         }
    }
    //获取修改新增删除的数据
    function getChanges() {
        var rows = dataGrid.datagrid('getChanges');
        var delRows = dataGrid.datagrid('getChanges', 'deleted');
        var insRows = dataGrid.datagrid('getChanges', 'inserted');
        var updateRows = dataGrid.datagrid('getChanges', 'updated');
        var totleRows = {
            insert: insRows,
            deleted: delRows,
            updated: updateRows
        };
        return totleRows;
    }
  //采番菜单Id
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
    function easydatagrid(columns) {
        dataGrid.datagrid({
            height: "100%",
            iconCls: 'icon-edit',
            singleSelect: false,
            rownumbers: true,
            // toolbar: '#view_report_btn',
            striped: true,
            // fitColumns: true,
            autoSizeMode:"fill",
            resizable:true,
            onClickRow : function (rowIndex, rowData) {
                $(this).datagrid('unselectAll');
                $(this).datagrid('selectRow', rowIndex);
            },
            onDblClickRow: onClickCell,
            // onLoadSuccess:function(){
            //     dataGrid.datagrid('enableDnd');
            // },
            // onEndEdit: onEndEdit,
            // selectOnCheck: true,
            columns: [columns]
        });
    }

    $(".easyui-linkbutton.append").on("click", function () {
        append();
    });
    $(".easyui-linkbutton.removeit").on("click", function () {
        removeit();
    });
    $(".easyui-linkbutton.accept").on("click", function () {
        accept();
    });
    $(".easyui-linkbutton.reject").on("click", function () {
        reject();
    });
    
    


    var editIndex = undefined;

    


    function endEditing() {
        if (editIndex == undefined) {
            return true
        }
        if (dataGrid.datagrid('validateRow', editIndex)) {
            dataGrid.datagrid('endEdit', editIndex);
            editIndex = undefined;
            return true;
        } else {
            return false;
        }
    }

    function onClickCell(index, field) {
        if (editIndex != index) {
            if (endEditing()) {
                dataGrid.datagrid('selectRow', index)
                    .datagrid('beginEdit', index);
                var ed = dataGrid.datagrid('getEditor', {index: index, field: field});
                if (ed) {
                    ($(ed.target).data('textbox') ? $(ed.target).textbox('textbox') : $(ed.target)).focus();
                }
                editIndex = index;
            } else {
                setTimeout(function () {
                    dataGrid.datagrid('selectRow', editIndex);
                }, 0);
            }
        }
    }

    function onEndEdit(index, row) {
        var ed = $(this).datagrid('getEditor', {
            index: index,
            field: 'module'
        });
        row.module = $(ed.target).combobox('getText');
    }
    function getMaxId(){
    	var rows = dataGrid.datagrid('getRows');
    	var max_id;
    	if (isEmpty(rows))
    		return 'C_1';
    	for(var i=0; i<rows.length; i++){
    		var subId = parseInt(rows[i].id.substring(2));
    		if (isEmpty(max_id)){
    			max_id = subId;
    		}else if(max_id < subId){
    			max_id = subId;
    		}
    	}
    	return 'C_' + ++max_id;
    }
    function append() {
        if (endEditing()) {
        	var id=getMaxId();
            dataGrid.datagrid('appendRow', {id:id});
            editIndex = dataGrid.datagrid('getRows').length - 1;
            dataGrid.datagrid('selectRow', editIndex)
                .datagrid('beginEdit', editIndex);
        }
    }

    function removeit() {
        console.log(editIndex);
//        if (editIndex == undefined){return}
        var ss = [];
        var rows = dataGrid.datagrid('getSelections');
        console.log(rows);
        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var rowIndex = dataGrid.datagrid('getRowIndex', row);
            dataGrid.datagrid('deleteRow', rowIndex);
//				ss.push('<span>'+row.itemid+":"+row.productid+":"+row.attr1+'</span>');
        }
        var re = {
            data: rows,
            leng: rows.length
        };
        return re;
//        editIndex = undefined;
    }

    function reject() {
        dataGrid.datagrid('rejectChanges');
        editIndex = undefined;
    }
    
    /*//加载下拉框的数据
    function RightOptionsData(optionId,OptionData,optionvalue){
    	$("select[optionId="+optionId+"]").html("");
    	for(var i=0; i<OptionData.length; i++){
    		if(OptionData[i].selectId==optionvalue){
    			$("select[optionId="+optionId+"]").append("<option selected selectId="+OptionData[i].selectId+">"+OptionData[i].selectName+"</option>");
    		}else{
    			if(optionvalue==0){
    				$("select[optionId="+optionId+"]").append("<option selected></option>");
    			}
    			$("select[optionId="+optionId+"]").append("<option selectId="+OptionData[i].selectId+">"+OptionData[i].selectName+"</option>");
    		}
		}
    }*/
    /*//点击行获取数据
    function clickrow(index, data){
    	RightOptionsData("table_id",table_id,data.table_id);
    	RightOptionsData("report_type",report_type,data.report_type);
    	RightOptionsData("report_disp_type",report_disp_type,data.report_disp_type);
    	RightOptionsData("report_file_type",report_file_type,data.report_file_type);
    	$("input[name=id]").val(data.id);
    	$("input[name=report_column]").val(data.report_column);
    	$("input[name=report_column_name]").val(data.report_column_name);
    	$("input[name=group_column]").val(data.group_column);
    	$("input[name=date_column]").val(data.date_column);
    	$("input[name=file_name]").val(data.file_name);
    	$("input[name=api_json]").val(data.api_json);
    	$("input[name=condition]").val(data.condition);
    	$("input[name=SQL]").val(data.SQL);
    }	*/
    
});