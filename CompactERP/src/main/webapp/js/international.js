/**
 * Created by Administrator on 2017/9/1.
 */
registerInit('international', function (form) {
    var $form = form.get();

    /*var internationalData = {
        processing: (function () {
            var $div = $('<div class="progress"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"><span class="sr-only"></span></div></div>');
            var dialog;
            return {
                open: function (title) {
                    if (dialog) return;
                    dialog = BootstrapDialog.show({
                        title: (title ? title : 'Processing'),
                        closable: false,
                        message: $div
                    });
                },
                close: function () {
                    if (!dialog) return;
                    dialog.close();
                    dialog = null;
                },
                isOpen: function () {
                    return dialog != null;
                }
            };
        })()
    };*/

    var dataGrid = $form.find('.internationalDeployTb');
    init();


    function init() {
        var params = $form.find(".international_Sea input").val();
        var url = "/international/selectInternational.do";
        if (isEmpty(params)) {
            var request = {'pageNumber': 1, 'pageSize': 20}
        } else {
            var request = {'pageNumber': 1, 'pageSize': 20, 'params': params};
            url = "/international/selectLikeInternational.do";
        }
        postJson(url, request, function (data) {
            if (data) {
                var totlerow = {
                    "total": data.count,
                    "rows": data.list
                };

                var c_international = dx.table.c_international;
                var column = c_international.columns;
                var row;
                var gridCol = [{field: 'firstckbox', width: 80, checkbox: 'true'}];

                var selectlist = data.map;
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
                            editor: 'textbox',
                            width: 80
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
                                    data: selectOptionData
                                }
                            }
                        };
                    }

                    gridCol.push(row);
                }
                // console.log(totlerow);

                easydatagrid(gridCol);
                dataGrid.datagrid('loadData', totlerow);
            }
        });
    }


    $form.find(".international_Sea input").on("keydown", function (e) {
        if (e.keyCode == 13) {
            $form.find(".international_Sea .search").click();
            return false;
        }
    });
    //搜索查询
    $form.find(".international_Sea .search").on("click", function () {
        var params = $form.find(".international_Sea input").val();
        // console.log(params);
        var request = {'pageNumber': 1, 'pageSize': 20, 'params': params};
        if ($.trim(params) != "") {
            postJson('/international/selectLikeInternational.do', request, function (data) {
                // console.log(data);
                var totlerow = {
                    "total": data.count,
                    "rows": data.list
                };
                // console.log(totlerow);
                dataGrid.datagrid('loadData', totlerow);
            });
        } else {
            init();
        }
    });


    function easydatagrid(columns) {
        dataGrid.datagrid({
            height: "100%",
            singleSelect: false,
            rownumbers: true,
            striped: true,
            fitColumns: true,
            // autoSizeMode: "fill",
            resizable: true,
            pagination: true,
            onClickRow: function (rowIndex, rowData) {
                $(this).datagrid('unselectAll');
                $(this).datagrid('selectRow', rowIndex);
            },
            onDblClickRow: onClickCell,
            // onEndEdit: onEndEdit,
            columns: [columns]
        }).datagrid("getPager").pagination({
            displayMsg: msg('Displaying {from} to {to} of {total} items'),
            beforePageText: msg('Page'),
            afterPageText: msg('of {page}'),
            showRefresh: false,
            pageSize: 20,
            pageList: [20, 30, 50],
            onSelectPage: function (pageNumber, pageSize) {

                var params = $form.find(".international_Sea input").val();
                var request = {'pageNumber': pageNumber, 'pageSize': pageSize, 'params': params}

                if ($.trim(params) != "") {
                    postJson('/international/selectLikeInternational.do', request, function (data) {
                        // console.log(data);
                        var totlerow = {
                            "total": data.count,
                            "rows": data.list
                        };
                        // console.log(totlerow);
                        dataGrid.datagrid('loadData', totlerow);
                    });
                } else {
                    postJson('/international/selectInternational.do', request, function (data) {
                        // console.log(data);
                        var totlerow = {
                            "total": data.count,
                            "rows": data.list
                        };
                        // console.log(totlerow);
                        dataGrid.datagrid('loadData', totlerow);
                    });
                }
            },
            onChangePageSize: function () {
            }
        });
    }

    $form.find(".btn-toolbar .append").on("click", function () {
        append();
    });
    $form.find(".btn-toolbar .removeit").on("click", function () {
        removeit();
    });
    $form.find(".btn-toolbar .accept").on("click", function () {
        accept();
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

            postJson('/international/saveInternational.do', {
                insert: insert,
                deleted: deleted,
                update: updated
            }, function (data) {
                // console.log(data);
                if (data) {
                    dxToastAlert("保存成功！");
                    // alert("保存成功！");

                    init();
                    // $("#funPointTb").datagrid('reload');
                    // dataGrid.datagrid('acceptChanges');
                } else {
                    reject();
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
//         alert(rows.length + ' rows are changed!');

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


    $form.find('.confirmReplace').on("click", function () {
        var findContext = $form.find('.findContext').val();
        var replaceWith = $form.find('.replaceWith').val();
        var scope = $form.find('.scope').val();
        var data = {'findContext': findContext, 'replaceWith': replaceWith, 'scope': scope}

        postJson('/international/replace.do', data, function (result) {
            dxToastAlert(result);
            $form.find(".international_Sea .search").click();
            $form.find(".close").click();
        });
    });

    $form.find('.i18n-import').on("click", function () {
        $form.find('.import_context').click();
    });

    //上传文件
    $file = $form.find('.import_context');
    $file.fileupload({
        dataType: 'json ',
        replaceFileInput: true,
        url: makeUrl('/international/upload.do'),
        formData: function () {
            return [{name: 'id', value: this.fileInput[0].id}];
        },
        autoUpload: true,
        add: function (e, data) {
            data.url = makeUrl('/international/upload.do');
            progressBox('uploading', data.files[0].name);
            data.submit();
        },
        done: function (e, data) {
            hidePorgressBox();
            alert(msg(data.result.data));
        }
    });

    $form.find('.i18n-export').on('click', function () {

        var params = $(".international_Sea input").val();
        var request = {'params': params};

        postJson('/international/export.do', request, function (data) {
            $("body").append("<iframe src='" + makeUrl(data) + "' style='display: none;' ></iframe>");
        }, function (data) {
            fail(data);
        });
    });

});