/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 9/15/14
 * Time: 1:03 PM
 */
//TODO 1 表格行样式。。2 sum统计。。3 多选鼠标移上去显示明细。。4 鼠标右键固定列在左侧
"use strict";

/**
 * cache records to grid, build record models, and build data array for DataTable
 *
 * @param grid grid model
 * @param records record data
 * @param parent parent widget model
 * @param [cellProcess] callback before each cell data been build
 * @returns {Array}
 */
function buildGridData(grid, records, parent, cellProcess) {
    var idForParent = {};
    var childIdColumn, parentIdColumn;
    if (!isEmpty(grid) && !isEmpty(grid.table)){
        var table = getTableDesc(grid.table);
        childIdColumn = table.children_id_column;
        parentIdColumn = table.parent_id_column;
        if (!isEmpty(childIdColumn)){
            $.each(records, function (i, record) {
                $.each(record.fields, function (i, field) {
                    //树子值对应的ID，用于父值
                    if (!isEmpty(childIdColumn) && field.column == childIdColumn){
                        idForParent[field.value] = record.id;
                    }
                })
            })
        }
    }
    grid.records = records;
    var array = [];
    // build all records cache and dataTable data array
    $.each(records, function (i, record) {
        var data = {rowid: record.id};
        record.columns = {};
        if (parent)
            record.columns._parent = {ref: parent.columns};
        // build record cache and set columns values
        $.each(record.fields, function (i, field) {
            if (cellProcess)
                data[field.column] = cellProcess(field, record);
            else
                data[field.column] = fieldText(field, record);
            if (!isEmpty(parentIdColumn) && field.column == parentIdColumn){
                data._parentId = idForParent[field.value] ? idForParent[field.value] : null;
            }
            record.columns[field.column] = field;
        });
        // set rowid for tr
        data.DT_RowId = record.id;
        data.rawData = record;
        array.push(data);
    });
    if (grid.dataHandler)
        array = grid.dataHandler.call(grid, array);
    return array;
}

/**
 * check action execute condition
 *
 * @param table
 * @param action
 * @param [rows] used for edit/remove
 * @returns {boolean}
 */
function checkActionCondition(table, action, rows) {
    if (!table.action_condition) return true;
    var condition = table.action_condition[action];
    if (!condition) return true;
    switch (action) {
        case 'create':
            if (!evaluate(condition.formula, rows[0], false)) {
                messageBox(evaluate('"' + condition.message + '"', rows[0], 'can not create data'));
                return false;
            }
            break;
        case 'edit':
        case 'remove':
            if (!rows.every(function (row) {
                    if (!evaluate(condition.formula, row, false)) {
                        messageBox(evaluate('"' + condition.message + '"', row, 'can not ' + action + ' data'));
                        return false;
                    }
                    return true;
                })) {
                return false;
            }
            break;
    }
    return true;
}

function iteratorGridHeaders(table, columns, noAuthColumns,callback, userColumns) {
    var lastKey = getTableIdColumn(table);
    var tableName = table.id;
    var userColumn;
    if (!isEmpty(userColumns))
        userColumn = userColumns[tableName];
    if (userColumn){
        userColumn.forEach(function (column) {
            // virtual or password field will not display
            var desc = column.columnDescribe;
            if (desc.virtual || desc.data_type == 15)
                return;
            var visible = true;
            if ((table.parent_id) && (desc.column_name != lastKey) && (table.id_column.indexOf("" + desc.column_name + "") >= 0))
                visible = false;
            if (desc.hidden)
                visible = false;
            if (visible && noAuthColumns)
                visible = noAuthColumns.indexOf(desc.column_name) == -1;

            callback(desc, visible);
        });
    }else{
        columns.forEach(function (desc) {
            // virtual or password field will not display
            if (desc.virtual || desc.data_type == 15)
                return;
            var visible = true;
            if ((table.parent_id) && (desc.column_name != lastKey) && (table.id_column.indexOf("" + desc.column_name + "") >= 0))
                visible = false;
            if (desc.hidden)
                visible = false;
            if (visible && noAuthColumns)
                visible = noAuthColumns.indexOf(desc.column_name) == -1;

            callback(desc, visible);
        });
    }
}

/**
 * init data grid
 *
 * @param form
 */
function initGrid(form) {
    function columnWidth(desc){
        if (desc.data_type == 2 || desc.data_type == 3){
            return 100;
        }else if (desc.data_type == 5){
            return 50;
        }else if (desc.data_type == 4 || desc.data_type == 11){
            return 100;
        }else if (desc.data_type == 12){
            return 150;
        }else {
            if (desc.max_len > 30){
                return 300;
            }else {
                return desc.max_len * 10;
            }
        }
    }
    form.get().find('table.dx-grid').each(function () {
        function ajaxLoadPaging(param, callback) {
            if (!grid.p('ajax')) {
                callback([]);
                return;
            }
            $grid.treegrid("unselectAll");
            var gridPaging = $grid.treegrid("getPager").pagination('options');
            var filter = grid.p('filter');
            var paging = {};
            if (gridPaging.pageNumber == 0)
                paging.start = 0;
            else
                paging.start = (gridPaging.pageNumber - 1) * gridPaging.pageSize;
            paging.length = gridPaging.pageSize;
            var postData = {id: id, filter: filter, paging: paging, isApproveSelect: grid.isApproveSelect, approveSelectParam: grid.approveSelectParam};
            var orders = {};
            if (param.order){
                orders.name = param.sort;
                orders.dir = param.order;
                paging.order = [orders];
            }
            dx.processing.open(msg("Searching"));
            postJson('/widget/grid/page.do', postData, function (ret) {
                // using by dataTable for rendering
                var data = {};
                data.total = ret.recordsTotal;
                data.rows = buildGridData(grid, ret.records, form);
                if (!isEmpty(form.beforeAfterRows) && form.beforeAfterRows.newPageData){
                    form.beforeAfterRows.newPageData(data.rows);
                }
                callback(data);
                form.get().find('.pagination .user-column').remove();
                form.get().find('.pagination').prepend('<button class="user-column">' +msg("symbol")+'</button>');
                if (isOpenPage)
                    isOpenPage = false;
                dx.ajax = 0;
                if (grid.p('rowChanged'))
                    grid.p('rowChanged')();
            })
        }

        function ajaxLoad(ignore, callback) {
            if (!grid.p('ajax')) {
                callback({data: []});
                return;
            }
            $grid.treegrid("unselectAll");
            var postData = {id: id, filter: grid.p('filter')};
            postJson('/widget/grid/list.do', postData, function (records) {
                // using by dataTable for rendering
                var data = {};
                var rows = buildGridData(grid, records, form);
                //next 方法回调。
                if (!isEmpty(form.nextLoadCallback))
                    form.nextLoadCallback();
                data.total = records.length;
                data.rows = rows;
                callback(data);
                form.get().find('.pagination .user-column').remove();
                form.get().find('.pagination').prepend('<button class="user-column">' +msg("symbol")+'</button>');
                form.get().find('.pagination').find(".pagination-info").hide();
                form.get().find('.pagination').find("table").hide();
                // form.get().find('.user-column').siblings().hide();

                $grid.datagrid('clearSelections');
                if (isOpenPage)
                    isOpenPage = false;
                dx.ajax = 0;
                if (grid.p('rowChanged'))
                    grid.p('rowChanged')();
                //子表表头数量
                $('a[href="#' + grid.id + '-tab"]').find('.child-data-count').text('(' + (isEmpty(records) ? 0 : records.length) + ')');
            })
        }

        function getSelectRow(idOnly, withNoChild) {
            var $rows = $grid.treegrid('getSelections');
            if ($rows.length == 0)
                return null;
            if (!withNoChild){
                var children = $grid.treegrid('getChildren', $rows[0].rowid);
                if (!isEmpty(children))
                    $rows.push.apply($rows, children);
            }
            var rows = [];
            for (var i=0; i<$rows.length; i++){
                rows.push(idOnly ? $rows[i].rowid : $rows[i].rawData);
            }
            return rows;
        }
        //获取双击的行
        function getdbclickRow(idOnly) {
            var $rows = $(idOnly);
            if ($rows.length == 0)
                return null;
            var rows = [];
            $rows.each(function (i, tr) {
                rows.push(w(tr.id));
            });
            return rows;
        }

        /**
         * build grid selected record's key data
         *
         * @param recordId record id
         */
        function buildGridRecordKey(recordId) {
            var record = w(recordId);
            var data = {};
            $.each(record.columns, function (key, field) {
                data[key] = field.value;
            });
            return {parent: record.id, param: data, table: grid.table};
        }

        // init child tabs grid buttons
        function initChildToolbar() {
            $('#' + grid.id + '-tab').find('.dx-grid-child-add').on('click', function (event) {
                if (!checkActionCondition(table, 'create', [form])) return;
                if (grid.p('add')){
                    grid.p('add')(grid);
                }else{
                    var parentId = {};
                    if (!isEmpty(table.parent_id)){
                        var parentTable = getTableDesc(form.tableName);
                        parentId[parentTable.idColumns[0]] = form.columns[parentTable.idColumns[0]].value;
                        //parentId = form.columns[table.idColumns[0]].value;
                    }
                    showDialogForm({
                        url: '/detail/create.view',
                        title: '',
                        data: {parent: grid.id, table: grid.table, parentId: parentId},
                        class: 'child-dialog',
                        shown: function (newForm, dialog) {
                            newForm.isChild = true;
                            newForm.filter = form.filter;
                            newForm.get().find('input.dx-back').hide();
                            newForm.submit = function () {
                                dialog.close();
                            };
                            newForm.next = function () {
                                form.nextLoadCallback = newForm.nextLoadCallback;
                                grid.reload();
                            }
                        },
                        hidden: function () {
                            form.nextLoadCallback = null;
                            grid.reload();
                        }
                    });
                    //newTab('/detail/create.view', {parent: grid.id, table: grid.table, parentId: parentId});
                }
            });
            $('#' + grid.id + '-tab').find('.dx-grid-child-delete').on('click', function (event) {
                var rows = grid.getSelectedRows();
                var rowIds = [];
                if (!isEmpty(rows))
                    for (var i=0; i<rows.length; i++){
                        rowIds.push(rows[i].id)
                    }
                if (!checkActionCondition(table, 'remove', rows)) return;
                if (grid.p('delete'))
                    grid.p('delete')(grid, rowid);
                else {
                    confirmBox('delete confirm', function () {
                        postJson('/widget/grid/delete.do', {
                            id: grid.id,
                            ids: rowIds
                        }, function () {
                            grid.updated();
                        });
                    });
                }
            });
            //子表双击编辑。。只有在新增或者点击编辑按钮后才触发
            if (form.action == 'create')
                $grid.datagrid('options').onDblClickRow = function (rowIndex) {
                    var rowid = rowIndex.rowid;
                    if (!checkActionCondition(table, 'edit', [w(rowid)])) return;

                    if (grid.p('edit'))
                        grid.p('edit')(grid, rowid);
                    else {
                        showDialogForm({
                            url: '/detail/edit.view',
                            title: '',
                            data: buildGridRecordKey(rowid),
                            class: 'child-dialog',
                            shown: function (newForm, dialog) {
                                newForm.get().find('input.dx-back').hide();
                                if (form.action == 'view'){
                                    newForm.get().find('input.dx-submit').hide();
                                }
                                newForm.submit = function () {
                                    dialog.close();
                                };
                            },
                            hidden: function (form) {
                                grid.reload();
                            }
                        });
                    }
                };
            if (form.action == 'view'){
                $grid.datagrid('options').onDblClickRow = function (rowIndex) {
                    var rowid = rowIndex.rowid;
                    var data = buildGridRecordKey(rowid);
                    data.readonly = true;
                    if (grid.p('edit'))
                        grid.p('edit')(grid, rowid);
                    else {
                        showDialogForm({
                            url: '/detail/edit.view',
                            title: '',
                            data: data,
                            class: 'child-dialog',
                            shown: function (newForm, dialog) {
                                //newForm.get().find('input.dx-back').hide();
                                //newForm.get().find('input.dx-submit').hide();
                                newForm.get().find('.dx-menu-bar').hide();
                                newForm.submit = function () {
                                    dialog.close();
                                };
                            },
                            hidden: function (form) {
                                //grid.reload();
                            }
                        });
                    }
                }
            }
        }

        var $grid = $(this);
        var grid = w(this.id);
        grid.parentForm = form;
        var table = getTableDesc(grid.table);
        var id = $grid.attr('id');
        var blockViewStyle = table.block_view_style;
        var default_group_column = table.default_group_column;
        if(!isEmpty(blockViewStyle) && !form.viewTableStyle){
            //列表块显示。
            viewTableStyle($grid, grid, form)
        }else{
            $grid.parents('.dx-auto-expand').on('dx.auto-expand', function () {
                $(this).find('div.dataTables_scrollBody').height(form.p('expandHeight') - (grid.listing ? 83 : 30));
            });
            var options = {
                rowStyler: function (index, row) {
                    if (isEmpty(index) || isEmpty(index.rawData))
                        return;
                    if (!isEmpty(index.rawData.rowColor)){
                        return index.rawData.rowColor;
                    }
                    //表行颜色。全部在后台一次性解析出来了。
                    //if (table.renders)
                    //    for (var i=0; i<table.renders.length; i++){
                    //        if (table.renders[i].level == 1) {
                    //            var form = {};
                    //            form.id = index.rawData.parent;
                    //            if (evaluate(table.renders[i].formula, form, null, null, null, null, index.rawData.id)) {
                    //                return table.renders[i].color;
                    //            }
                    //        }
                    //    }
                },
                // set handler which will be called when table cells has been draw
                onLoadSuccess: function (row) {
                    if (table.child_seq) {
                        //启用dnd支持
                        $(this).treegrid('enableDnd');
                    }
                    if (!grid.records) return;
                    // check if any field has formula need this table's column
                    //求子表sum, max等
                    if (grid.reloaded && form.fieldIds) {
                        grid.reloaded = false;
                        $.each(form.fieldIds, function (i, id) {
                            var field = w(id);
                            var desc = getDesc(field);
                            if (desc.formula && (desc.formula.indexOf('"' + grid.table + '.') >= 0)) {
                                field.val(evaluate(desc.formula, form, desc.column_name));
                                evaluateForm(form, field);
                            }
                        });
                    }
                    if (grid.postInit)
                        grid.postInit.call(grid);

                    //缓存列数据form。 用来给字段超链接
                    for (var i=0; i<grid.records.length; i++){
                        var record = grid.records[i];
                        // cache record
                        w(record);
                    }
                    if (form.action == 'create'){
                        //新增。表格第一列显示
                        $grid.treegrid('showColumn', 'checkbox');
                    }
                },
                loadMsg: 0,
                idField: 'rowid',
                treeField: isEmpty(table.parent_id_column) ? '' : table.name_column
            };
            //子表有复选框。反向关联表，树无复选框。
            if ((!isEmpty(table.parent_id) || grid.listing) && isEmpty(table.parent_id_column)){
                options.onClickRow = function (rowIndex, rowData) {
                    //$(this).treegrid('unselectAll');
                    //$(this).treegrid('selectRow', rowIndex.rowid);
                };
                options.onClickCell = function (field, row){
                    if (field == 'checkbox'){
                        $grid.treegrid('unselect', row.rowid);
                    }else{
                        $grid.datagrid('clearSelections');
                    }
                };
                options.frozenColumns = [[
                    {field: 'rowid', title: 'rowid', hidden: true, width: 100},
                    {field: 'fieldId', title: 'fieldId', hidden: true,
                        formatter : function(value, rowData, rowIndex){
                            return '<div id="' + rowData.rowid + '"></div>';
                        }, width: 100
                    },
                    {field: 'checkbox', title: 'checkbox', checkbox: true, width: 100}
                ]];
            }else{
                options.frozenColumns = [[
                    {field: 'rowid', title: 'rowid', hidden: true, width: 100},
                    {field: 'fieldId', title: 'fieldId', hidden: true,
                        formatter : function(value, rowData, rowIndex){
                            return '<div id="' + rowData.rowid + '"></div>';
                        }, width: 100
                    }
                ]];
            }
            if (grid.listing) {
                if (grid.p('ajax') === undefined)
                    grid.p('ajax', false);
                options.height = "100%";
                options.pagination = true;
                options.pageSize = 30;
                options.pageList = [10, 30, 50, 100];
                if (isEmpty(table.parent_id_column)) {
                    options.loader = ajaxLoadPaging;
                }else{
                    options.loader = ajaxLoad;
                }
                if (form.action === 'select' && !isEmpty(table.parent_id_column))
                    options.singleSelect = true;
                else{
                    if (!isEmpty(table.parent_id_column))
                        options.singleSelect = true;
                    else
                        options.singleSelect = false;
                }
                options.columns = [[]];
            } else {
                //子表拖动排序
                if (table.child_seq){
                    options.onDrop = function (targetRow, sourceRow, point) {
                        //alert(1);
                        var aaa = 1;
                    }
                }
                if (grid.p('ajax') === undefined)
                    grid.p('ajax', true);
                options.pagination = false;
                options.height = "100%";
                options.loader = ajaxLoad;
                options.columns = [[]];
            }
            //冻结左侧列
            options.onHeaderContextMenu = function (e, field) {
                if (isEmpty(dx.user.frozenColumns)){
                    dx.user.frozenColumns = {};
                }
                if (isEmpty(dx.user.frozenColumns[table.id])){
                    dx.user.frozenColumns[table.id] = {};
                }
                if (!isEmpty(field)){
                    if (dx.user.frozenColumns[table.id][field]){
                        dxToastAlert(msg('cancel frozen'));
                        dx.user.frozenColumns[table.id][field] = false;
                    }else{
                        dxToastAlert(msg('frozen, do it again cancel frozen'));
                        dx.user.frozenColumns[table.id][field] = true;
                    }
                }
            };
            //options.onSelect = function (index, row) {
            //    var rows = grid.getSelectedRows();
            //    var triggers = table.triggers;
            //    if (isEmpty(triggers))
            //        return;
            //    else
            //        for (var key in triggers)
            //            if (triggers[key].system_type == 'operation')
            //                checkOpMenu(triggers[key], rows, function (notRuleRow) {
            //                    if (!isEmpty(notRuleRow))
            //                        form.get().find('button[name="' + triggers[key].action_id + '"]').hide();
            //                    else
            //                        form.get().find('button[name="' + triggers[key].action_id + '"]').show();
            //                    var ccc = 1;
            //                });
            //    var aaa = 1;
            //};
            //options.onUnselect = function (index, row) {
            //    var bbb = 1;
            //};
            if (!grid.p('columns'))
                grid.p('columns', table.columns);
            //十个显示列充满页面。十个以上固定宽度100
            var count = 0;
            iteratorGridHeaders(table, grid.p('columns'), grid.noAuthColumns, function (desc, visible) {
                if (visible){
                    count += 1;
                }
                var width = columnWidth(desc);
                var columnDefine = {field: desc.column_name, title: i18n(desc.i18n), width: width, hidden: !visible, sortable: true};
                // for number/digits column, right align
                if (desc.data_type == 2 || desc.data_type == 3) {

                } else if (desc.data_type == 1) {
                    //以前的多选。鼠标移上去显示多选的内容。
                    //TODO 改成新的。
                    //if (desc.viewStyle && desc.viewStyle.map) {
                    //    // using by popover
                    //    columnDefine.className = 'dx-grid-map-input';
                    //    columnDefine.createdCell = function(td, cellData, rowData, row, col){
                    //        $(td).data('rowid', rowData.rowid);
                    //        $(td).data('column', desc.column_name);
                    //    }
                    //}
                    columnDefine.formatter = function (value, rowData, rowIndex) {
                        if (!value)
                            return '';
                        if (!desc.link && (typeof value === 'string'))
                            return value.replace(/</g, "&lt;").replace(/>/g, "&gt;");

                        return value;
                    };
                }
                columnDefine.styler = function(value, row, index){
                    if (isEmpty(index) || isEmpty(index.rawData))
                        return;
                    if (!isEmpty(index.rawData.columnColor)){
                        return index.rawData.columnColor;
                    }
                    //表列颜色。全部在后台一次性解析出来了。
                    //if (table.renders)
                    //    for (var i=0; i<table.renders.length; i++){
                    //        if (table.renders[i].level != 1 && !isEmpty(table.renders[i].column)
                    //                    && columnDefine.field == table.renders[i].column) {
                    //            var form = {};
                    //            form.id = row.rawData.parent;
                    //            if (evaluate(table.renders[i].formula, form, null, null, null, null, row.rawData.id)) {
                    //                return table.renders[i].color;
                    //            }
                    //        }
                    //    }
                };
                //冻结的列。
                if (!isEmpty(dx.user.frozenColumns) && !isEmpty(dx.user.frozenColumns[table.id]) &&
                        dx.user.frozenColumns[table.id][desc.column_name]){
                    options.frozenColumns[0].push(columnDefine);
                }else{
                    options.columns[0].push(columnDefine);
                }
            }, grid.userColumns);
            if (!isEmpty(options.columns[0])){
                if (count <= 10){
                    options.fitColumns = true;
                }
            }
            if (grid.optionsHandler)
                options = grid.optionsHandler.call(grid, options);
            /**
             * iterate child.column values using callback
             *
             * @param name
             * @param callback
             * @private
             */
            grid.iterate = function (name, callback) {
                // not inited
                if (!grid.records) return true;
                return grid.records.every(function (record) {
                    var value = record.columns[name].value;
                    return callback(value) !== false;
                });
            };

            grid.sum = function (name) {
                var ret = new BigDecimal('0');
                grid.iterate(name, function (val) {
                    var value;
                    if (val == null)
                        value = "0";
                    else
                        value = val.toString();
                    ret = ret.add(new BigDecimal(value))
                });
                return Number(ret.toString());
            };
            var dt = $grid.treegrid(options).treegrid("getPager").pagination({
                displayMsg: msg('Displaying {from} to {to} of {total} items'),
                beforePageText: msg('Page'),
                afterPageText: msg('of {page}'),
                showRefresh: false,
                onSelectPage:function(pageNumber, pageSize){
                    $grid.treegrid('reload');
                },
                onChangePageSize:function(){

                }
            });

            if (options.rowReorder) {
                dt.on('row-reordered', function (e, details) {
                    if (details.length === 0)
                        return;
                    var data = {};
                    for (var i = 0, ien = details.length; i < ien; i++) {
                        var d = details[i];
                        var rowid = dt.row(d.node).data().rowid;
                        data[rowid] = d.newData;
                    }
                    postJson('/widget/grid/reorder.do', {id: grid.id, reorder: data});
                });
            }

            // init updated handler
            grid.onUpdated = function () {
                grid.reloaded = true;
                $grid.treegrid('reload');
                $grid.treegrid('unselectAll');
                form.p('modified', true);
            };
            if (grid.listing) {
                //$grid.find('tbody').on('click', 'tr', function (e) {
                //    if ($(e.target).is('a'))
                //        return;
                //    if (form.action === 'select')
                //        $grid.find('tbody tr.selected').removeClass('selected');
                //    toggleSelect($(this));
                //});
            } else {
                // init toolbar in children mode
                initChildToolbar();
                grid.switchToEdit = function () {
                    //查看进编辑。表格第一列显示
                    $grid.treegrid('showColumn', 'checkbox');
                    //查看进编辑。表格新增/删除显示
                    $('#' + grid.id + '-tab').find('.dx-grid-child-add').show();
                    $('#' + grid.id + '-tab').find('.dx-grid-child-delete').show();
                    //子表双击编辑。。只有在点击编辑按钮后才触发
                    $grid.datagrid('options').onDblClickRow = function (rowIndex) {
                        var rowid = rowIndex.rowid;
                        if (!checkActionCondition(table, 'edit', [w(rowid)])) return;

                        if (grid.p('edit'))
                            grid.p('edit')(grid, rowid);
                        else{
                            showDialogForm({
                                url: '/detail/edit.view',
                                title: '',
                                data: buildGridRecordKey(rowid),
                                class: 'child-dialog',
                                shown: function (newForm, dialog) {
                                    newForm.get().find('input.dx-back').hide();
                                    newForm.submit = function () {
                                        dialog.close();
                                    };
                                },
                                hidden: function (form) {
                                    grid.reload();
                                }
                            });
                            //newTab('/detail/edit.view', buildGridRecordKey(rowid), grid.id + '-' + rowid);
                        }
                    };
                }
            }

            // set filter handler
            grid.filter = function () {
                var data = this.data();
                grid.p('filter', this.data());
                grid.p('ajax', true);
                $grid.treegrid('reload');
            };
            // set filter reset handler
            grid.reset = function () {
                grid.p('filter', null);
                grid.p('ajax', false);
                grid.reload();
            };
            grid.reload = function () {
                grid.reloaded = true;
                $grid.treegrid('reload');
                $grid.treegrid('unselectAll');
            };

            grid.getSelectedRows = getSelectRow;
            grid.buildRecordData = buildGridRecordKey;
            grid.getdbclickRow = getdbclickRow;
            if (table.auto_gen_sql)
                grid.autoGen = function (form) {
                    var param = buildSQLParam(form, table.auto_gen_sql);
                    postJson('/widget/grid/auto.do', {id: grid.id, param: param}, function () {
                        $grid.treegrid('reload');
                    });
                }
        }

        form.get().find('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            $grid.treegrid('resize');
        });



        function findgrid(){
            var formClass = $(".dx-main-tab-content>.tab-pane.active>.dx-form");
            if (formClass.is(":hidden")) {
                if(formClass.parent().find(".dx-detail-form .tab-content .tab-pane.active .panel").hasClass("datagrid")){
                    $(".dx-detail-form .tab-content .tab-pane.active .panel").find($grid).treegrid('resize');
                }
            }else {
                formClass.find($grid).treegrid('resize');
            }
        }
        $("#menubar").off("click", findgrid);
        $("#menubar").on("click", findgrid);

         $(".index-tab-navs,.home-tab-navs-rest").find("a[data-toggle='tab']").on('shown.bs.tab', findgrid
            /* function (e) {
             var formClass = $(".dx-main-tab-content>.tab-pane.active>.dx-form");
             if (formClass.is(":hidden")) {
                 if($(".dx-detail-form .tab-content .tab-pane.active .panel").hasClass("datagrid")){
                     $(".dx-detail-form .tab-content .tab-pane.active .panel").find($grid).treegrid('resize')
                 }
             }else {
                 $grid.treegrid('resize');
                 console.log($grid)
             }
         }*/
         );
    });
}

function viewTableStyle($grid, grid, form){
    function tableViewStyleAjaxLoad(callback) {
        if (!grid.p('ajax')) {
            callback({data: []});
            return;
        }
        var postData = {id: id, filter: grid.p('filter')};
        postJson('/widget/grid/list.do', postData, function (records) {
            // using by dataTable for rendering
            callback({data: buildGridData(grid, records, form)});
            if (grid.p('rowChanged'))
                grid.p('rowChanged')();
        })
    }
    //获取style_html中参数值
    function makeParamsData(param, form){
        return evaluate(param, form);
    }
    //获取双击的行
    function getdbclickRow(idOnly) {
        var $rows = $(idOnly);
        if ($rows.length == 0)
            return null;
        var rows = [];
        $rows.each(function (i, tr) {
            rows.push(w(tr.id));
        });
        return rows;
    }
    //获取选中style_html值
    function getViewTableSelectRow(idOnly) {
        var $rows = $grid.find('div input:checkbox:checked');
        if ($rows.length == 0)
            return null;
        var rows = [];
        $rows.each(function (i, tr) {
            rows.push(idOnly ? tr.id : w(tr.id));
        });
        return rows;
    }
    var table = getTableDesc(grid.table);
    var blockViewStyle = table.block_view_style;
    var default_group_column = table.default_group_column;
    var obj = eval('(' + blockViewStyle + ')');
    var params = obj.params;
    var tableViewStyle = dx.tableViewStyle[obj.styleId].style_html;
    var appendTableViewStyle = tableViewStyle;
    var $checkall = $grid.find('input.dx-grid-checkall');
    grid.filter = function () {
        $checkall.prop('checked', false);
        grid.p('filter', this.data());
        grid.p('ajax', true);
        $grid.empty();
        tableViewStyleAjaxLoad(function(data){
            var resultData = data.data;
            //分组为空与分组不为空逻辑
            var ret;
            if(isEmpty(default_group_column)){
                for(var i=0; i<resultData.length; i++){
                    appendTableViewStyle = '<div style="float: left" class="table-view-style-param"><input type="checkbox" class="table-view-style-id" id="'+resultData[i].rowid+'"/>' +
                    appendTableViewStyle + '</div>';
                    for(var j=0; j<params.length; j++){
                        ret = makeParamsData(params[j], resultData[i]);
                        appendTableViewStyle =
                            appendTableViewStyle = appendTableViewStyle.replace('${'+j+'}', ret);
                    }
                    $grid.append(appendTableViewStyle);
                    appendTableViewStyle = tableViewStyle;
                    resultData[i].id = resultData[i].rowid;
                    w(resultData[i]);
                }
            }else{
                var groupValue;
                var groupValues = new Array();
                var appendDivs;
                for(var i=0; i<resultData.length; i++){
                    appendTableViewStyle = '<div style="float: left" class="table-view-style-param"><input type="checkbox" class="table-view-style-id" id="'+resultData[i].rowid+'"/>' +
                    appendTableViewStyle + '</div>';
                    for(var j=0; j<params.length; j++){
                        ret = makeParamsData(params[j], resultData[i])
                        appendTableViewStyle = appendTableViewStyle.replace('${'+j+'}', ret);
                    }
                    groupValue = resultData[i][default_group_column];
                    if (isEmpty(groupValues[groupValue])){
                        groupValues[groupValue] = groupValue;
                        appendDivs = '<div name="' + groupValue + '" style="clear:both" class="table-view-style-group"><span>' + groupValue + '</span></div>'
                        $grid.append(appendDivs);
                        $grid.find('div[name='+ groupValue +']')//.append('<hr size="5"/>');
                        $grid.find('div[name='+ groupValue +']').append(appendTableViewStyle);
                    }else{
                        $grid.find('div[name='+ groupValue +']').append(appendTableViewStyle);
                    }
                    appendTableViewStyle = tableViewStyle;
                    resultData[i].id = resultData[i].rowid;
                    w(resultData[i]);
                }
            }
            grid.p('ajax', false);
            dx.processing.close("Searching");
        });
    };
    grid.reset = function () {
        $checkall.prop('checked', false);
        grid.p('filter', null);
        grid.reload();
    };
    grid.reload = function () {
        $checkall.prop('checked', false);
        // 列表块显示的刷新页面
        var fb = form.filter.build();
        grid.filter.call(fb);
    };
    grid.onUpdated = function () {
        grid.reloaded = true;
        var fb = form.filter.build();
        grid.filter.call(fb);
    };
    grid.getSelectedRows = getViewTableSelectRow;
    grid.buildRecordData = function (recordId) {
        var record;
        for (var i=0; i<grid.records.length; i++){
            if (grid.records[i].id == recordId){
                record = grid.records[i];
                break;
            }
        }
        var data = {};
        $.each(record.columns, function (key, field) {
            data[key] = field.value;
        });
        return {parent: record.id, param: data, table: grid.table};
    };
    grid.getdbclickRow = getdbclickRow;
}