/**
 * Created by Administrator on 2017/9/1.
 */
registerInit('aBatch', function (form) {

    init();
    var dataGrid = $('#abatchDeployTb');

    function init() {

        postJson('/abatch/selectBatch.do', {}, function (data) {
            if (data) {
                var totlerow = {
                    "total": data.count,
                    "rows": data.list
                };

                var c_batch = dx.table.c_batch;
                var column = c_batch.columns;
                var row;
                var gridCol = [{field: 'firstckbox', width: 80, checkbox: 'true'}];

                var selectlist = data.aMap;
                var selectColumnName;
                var selectOptionList;
                for (var m in selectlist) {
                    selectColumnName = m;
                    selectOptionList = selectlist[m];
                }
                for (var i = 0, ileng = column.length; i < ileng; i++) {
                    if (column[i].column_name !== selectColumnName) {
                        row = {
                            field: column[i].column_name,
                            title: column[i].i18n[dx.user.language_id],
                            hidden: column[i]['hidden'],
                            editor: 'textbox'
                        };
                    } else {
                        row = {
                            field: column[i].column_name,
                            title: column[i].i18n[dx.user.language_id],
                            hidden: column[i]['hidden'],
                            editor: {
                                type: 'combobox',
                                options: {
                                    valueField: 'selectId',
                                    textField: 'selectName',
                                    data: selectOptionData,
                                    required: true
                                }
                            }
                        };
                    }

                    gridCol.push(row);
                }
                console.log(totlerow);

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
            // toolbar: '#url_deploy_btn',
            striped: true,
            autoSizeMode: "fill",
            resizable: true,
            onClickRow : function (rowIndex, rowData) {
                $(this).datagrid('unselectAll');
                $(this).datagrid('selectRow', rowIndex);
            },
            onDblClickRow: onClickCell,
            // onEndEdit: onEndEdit,
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

    function accept() {
        if (endEditing()) {
            var totleRow = getChanges();
            var insert = totleRow.insert;
            var deleted = totleRow.deleted;
            var updated = totleRow.updated;

            // var functionPoint = {
            //     insert: insert, deleted: deleted, update: updated
            // };

            postJson('/abatch/saveBatch.do', {
                insert: insert,
                deleted: deleted,
                update: updated
            }, function (data) {
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


    function append() {
        if (endEditing()) {
            dataGrid.datagrid('appendRow', {});
            editIndex = dataGrid.datagrid('getRows').length - 1;
            dataGrid.datagrid('selectRow', editIndex)
                .datagrid('beginEdit', editIndex);
        }
    }

    function removeit() {
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
//        console.log(rows);
        alert(rows.length + ' rows are changed!');


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
});