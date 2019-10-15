/**
 * Created by Administrator on 2017/8/30.
 */
registerInit('functionPoint', function (form) {

    init();
    var dataGrid = $('#funPointTb');

    function init() {
        postJson('/functionPoint/selectFunctionPoint.do',{},function (data) {
            if(data){
                var totlerow = {
                    "total": data.count,
                    "rows": data.list
                };

                console.log(data);
                console.log(totlerow);

                var c_page = dx.table.c_page;
                var column = c_page.columns;
                var row;
                var gridCol = [{field: 'firstckbox', width: 80, checkbox: 'true'}];

                var selectlist=data.aMap;
                var selectColumnName;
                var selectOptionList;
                for(var m in selectlist){
                    selectColumnName=m;
                    selectOptionList=selectlist[m];
                }

                var selectOptionData=[];
                for(var j=0,jleng=selectOptionList.length;j<jleng;j++){
                    var selectoption={
                        selectId : selectOptionList[j].id,
                        selectName : selectOptionList[j].name
                    };
                    selectOptionData.push(selectoption);
                }
                console.log(selectOptionData);

                for (var i = 0, ileng = column.length; i < ileng; i++) {
                    if(column[i].column_name !==selectColumnName){
                        row = {
                            field: column[i].column_name,
                            title: column[i].i18n[dx.user.language_id],
                            hidden: column[i]['hidden'],
                            editor: 'textbox'
                        };
                    }else{
                        row = {
                            field: column[i].column_name,
                            title: column[i].i18n[dx.user.language_id],
                            hidden: column[i]['hidden'],
                            formatter:function(value,row){
                                return row.url_name;
                            },
                            editor:{
                                type:'combobox',
                                options:{
                                    valueField:'selectId',
                                    textField:'selectName',
                                    data : selectOptionData
                                    // required:true
                                }
                            }
                        };
                    }
                    gridCol.push(row);
                }
                console.log(gridCol);

                easydatagrid(gridCol);
                dataGrid.datagrid('loadData', totlerow);
            }
        });
    }

    function easydatagrid(columns) {
        dataGrid.datagrid({
            height: "100%",
            singleSelect: false,
            rownumbers: true,
            toolbar: '#fun_point_btn',
            striped: true,
            // fitColumns: true,
            autoSizeMode:"fill",
            resizable:true,
            onClickRow : onClickRow,
            onDblClickRow: onDblClickRow,
            onAfterEdit:onAfterEdit,
            // onLoadSuccess:function(){
            //     dataGrid.datagrid('enableDnd');
            // },
//            onEndEdit: onEndEdit,
            // selectOnCheck: true,
            columns: [columns]
        });
    }

    $(".funPointSea input").on("keydown",function (e) {
        if (e.keyCode == 13) {
            $(".funPointSea .search").click();
            return false;
        }
    });

    $(".funPointSea .search").on("click", function () {
        var search = $(".funPointSea input").val();
        console.log(search);
        if ($.trim(search) != "") {
            postJson('/functionPoint/selectDimFunctionPoint.do',{params: search},function (data) {
                console.log(data);
                var totlerow = {
                    "total": data.count,
                    "rows": data.list
                };
                console.log(totlerow);
                dataGrid.datagrid('loadData', totlerow);
            });
        } else {
            init();
        }

    });


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

    function accept() {
        if (endEditing()) {
            var totleRow = getChanges();
            var insert = totleRow.insert;
            var deleted = totleRow.deleted;
            var updated = totleRow.updated;

            var functionPoint = {
                insert: insert, deleted: deleted, update: updated
            };

            postJson('/functionPoint/saveFunctionPoint.do', {insert: insert, deleted: deleted, update: updated}, function (data) {
                console.log(data);
                if (data) {
                    init();
                    alert(msg("Saved successfully!"));
                } else {
                    reject();
                    alert(msg("Save failed!"));
                }
            });
        }
    }


    function endEditing() {
        if (editIndex == undefined) {
            return true;
        }
        if (dataGrid.datagrid('validateRow', editIndex)) {
            dataGrid.datagrid('endEdit', editIndex);
            editIndex = undefined;
            return true;
        } else {
            return false;
        }
    }
    function objectEpt(data) {
        for(var a in data){
            return true;
        }
        return false;
    }
    
    function onAfterEdit(rowIndex, rowData, changes) {
        console.log(rowData);
        console.log(changes);
        if(objectEpt(changes)){
            console.log(191);
            console.log(rowIndex);
            if($("tr[datagrid-row-index="+rowIndex+"]").hasClass("rowChange")){
                $("tr[datagrid-row-index="+rowIndex+"]").removeClass("rowChange");
                $("tr[datagrid-row-index="+rowIndex+"]").addClass("rowChange");
            }else {

            }
        }
    }
    function onClickRow(rowIndex, rowData) {
        $(this).datagrid('unselectAll');
        $(this).datagrid('selectRow', rowIndex);
        dataGrid.datagrid('endEdit');

        var rows = dataGrid.datagrid('getChanges');

    }

    function onDblClickRow(index, field) {
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
            field: 'url_id'
        });
        row.url_name = $(ed.target).combobox('getText');
    }

    function append() {
        // if (endEditing()) {
            dataGrid.datagrid('appendRow', {});
            var index = dataGrid.datagrid('getRows').length - 1;
            dataGrid.datagrid('selectRow', index);
                // .datagrid('beginEdit', editIndex);
        // }
    }

    function removeit() {
        console.log(editIndex);
//        if (editIndex == undefined){return}
        var rows = dataGrid.datagrid('getSelections');
        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var rowIndex = dataGrid.datagrid('getRowIndex', row);
            dataGrid.datagrid('deleteRow', rowIndex);
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

    function getChanges() {
        var rows = dataGrid.datagrid('getChanges');

        var delRows = dataGrid.datagrid('getChanges', 'deleted');
        var insRows = dataGrid.datagrid('getChanges', 'inserted');
        var updateRows = dataGrid.datagrid('getChanges', 'updated');
       console.log(rows);

        var totleRows = {
            insert: insRows,
            deleted: delRows,
            updated: updateRows
        };
        return totleRows;
    }
});