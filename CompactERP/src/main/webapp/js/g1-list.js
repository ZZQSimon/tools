/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 9/30/14
 * Time: 1:54 PM
 */

"use strict";

registerInit('list', function (form) {
	function fixedData() {
		if (!form.filter.fixedIds)
			return undefined;
		var ret = {};
		form.filter.fixedIds.forEach(function (id) {
			var field = w(id);
			ret[getDesc(field).column_name] = field.val();
		});
		return ret;
	}
    function buildBatchApprove(batchData){
        function buildBatchApproveHtml(blockId, blockI18N){
            var html = '<li role="presentation">' +
                '<button type="button" class="btn-sm batch-approve-li" ' + (nodeLength == 1 ? 'style="display: none"' : "")
                + 'role="menuitem" tabindex="-1" blockId="' + blockId + '">'
                + i18n(blockI18N) + '</button>' +
                '</li>';
            return html;
        }
        var $ul = form.get().find('.batch-approve');
        $ul.empty();
        if (isEmpty(batchData) || isEmpty(batchData.nodesI18N)){
            return;
        }
        var nodeLength = 0;
        for (var key in batchData.nodesI18N){
            nodeLength += 1;
        }
        for (var key in batchData.nodesI18N){
            $ul.append(buildBatchApproveHtml(key, batchData.nodesI18N[key]));
        }
    }
    function buildBatchApproveButton(approvebuttons){
        function buildBatchApproveButtonHtml(approvebuttons){
            var html = '<button type="button" class="btn btn-sm batch-approve-button" flowEventId="' + approvebuttons.flow_event_id + '">' +
                i18n(approvebuttons.i18n) + '</button>';
            return html;
        }
        if (isEmpty(approvebuttons)){
            return;
        }
        var $toolBar = form.get().find('.dx-form-top-bar-left');
        $toolBar.find('.batch-approve-button').remove();
        for (var key in approvebuttons){
            $toolBar.append(buildBatchApproveButtonHtml(approvebuttons[key]));
        }
    }
	function initToolbar() {
		var $toolbar = $("#menu-bar-" + form.id);
		$toolbar.find('button.dx-grid-add').on('click', function () {
			if (!checkActionCondition(table, 'create', [form])) return;
			var fd = fixedData();
            postPage('/detail/create.view', {parent: grid.id, table: grid.table, fixedData: fd}, function (result) {
                form.get().hide();
                var $div = form.get().parent().find('.create-edit');
                $div.append(result);
                // init new form
                var newForm = getFormModel($div.find('.dx-form').attr('id'));
                //编辑或新增保存
                newForm.submit = function () {
                    form.get().show();
                    $div.empty();
                    grid.reload();
                };
                newForm.parentShow = function () {
                    form.get().show();
                    $div.empty();
                    grid.reload();
                };
                newForm.filter = form.filter;
                buildFormCache(newForm, newForm.widgets, true);
                if (!isEmpty(table.parent_id_column)){
                    var rows = grid.getSelectedRows();
                    newForm.parentRow = isEmpty(rows) ? null : rows[0];
                }
                dx.init.detail(newForm);
                //newForm.p('batchData', batchData);
                newForm.p('updated', true);
                //返回按钮点击事件
                $div.find('input.dx-back').on("click", function () {
                    form.get().show();
                    $div.empty();
                });
            });
            //newTab('/detail/create.view', {
            //    parent: grid.id,
            //    table: grid.table,
            //    fixedData: fd
            //});
		});
        //批量提交按钮点击事件
        $toolbar.find('button.batch-approve-submit').on('click', function () {
            var rows = grid.getSelectedRows();
            if (isEmpty(rows)){
                alert(msg('No Row Selected'));
                return;
            }
            var recordIds = [];
            for (var i=0; i<rows.length; i++){
                recordIds.push(rows[i].id);
            }
            dx.processing.open();
            isOpenPage = true;
            postJson('/approve/checkBatchApproveSubmit.do', {tableId: table.id, recordIds: recordIds}, function (result) {
                if (!result){
                    dx.processing.close();
                    return;
                }
                var batchFlowEvents = {};
                for (var i=0; i<rows.length; i++){
                    batchFlowEvents[rows[i].id] = buildBlockEventParam(table.submitEvent, rows[i]);
                }
                postJson('/approve/batchApproveSubmit.do', {tableId: table.id, batchFlowEvents: batchFlowEvents}, function (result) {
                    dxToastAlert(msg(result));
                    grid.reload();
                    isOpenPage = false;
                    dx.processing.close();
                });
            });
        });
        //我审批过的按钮点击事件
        $toolbar.find('button.my-already-approve').on('click', function () {
            if($($toolbar.find('button.dx-grid-approve')).hasClass('active')){
                $($toolbar.find('button.dx-grid-approve')).removeClass("active");
            }
            if (!$(this).hasClass("active")) {
                $(this).addClass("active");
                grid.isApproveSelect = 1;
                grid.approveSelectParam = {};
                grid.approveSelectParam.myAlreadyApprove = true;
                grid.reload();
            }else{
                $(this).removeClass("active");
                grid.isApproveSelect = null;
                grid.approveSelectParam = {};
                grid.reload();
            }
        });
        //待我审批按钮点击事件
        $toolbar.find('button.dx-grid-approve').on('click', function () {
            if($($toolbar.find('button.my-already-approve')).hasClass('active')){
                $($toolbar.find('button.my-already-approve')).removeClass("active");
            }
            var that = this;
            if (!$(that).hasClass("active")){
                $(that).addClass("active");
                postJson('/approve/getBatchBlock.do', {tableId: table.id}, function (data) {
                    if (isEmpty(data) || isEmpty(data.nodesI18N)){
                        grid.isApproveSelect = 1;
                        grid.approveSelectParam = {};
                        grid.approveSelectParam.blockId = '111111111111111111';
                        grid.reload();
                    }
                    form.batchApproveData = data;
                    buildBatchApprove(data);
                    form.get().find('.batch-approve-li').on('click', function () {
                        var blockId = $(this).attr('blockId');
                        var approvebuttons = table.approveButtonEvent[blockId];
                        buildBatchApproveButton(approvebuttons);
                        grid.isApproveSelect = 1;
                        grid.approveSelectParam = {};
                        grid.approveSelectParam.blockId = blockId;
                        grid.reload();
                    });
                    //只有一个按钮直接点击一下
                    var batchBlocks = form.get().find('.batch-approve-li');
                    if (!isEmpty(batchBlocks) && batchBlocks.length == 1){
                        $(batchBlocks[0]).click();
                    }
                });
            }else{
                grid.isApproveSelect = 0;
                grid.approveSelectParam = {};
                $(that).removeClass("active");
                form.get().find('.dx-form-top-bar-left').find('.batch-approve-button').remove();
                grid.reload();
            }
        });
        //批量审批按钮点击事件
        $toolbar.on('click', 'button.batch-approve-button', function () {
            var rows = grid.getSelectedRows();
            if (isEmpty(rows)){
                alert(msg('No Row Selected'));
                return;
            }
            var reason = prompt(msg('please write approve reason'));
            if (reason == null)
                return;
            var table = getTableDesc(form.tableName);
            var ids = [];
            var idsObject = {};
            for (var i=0; i<rows.length; i++){
                ids.push(rows[i].columns[table.idColumns[0]].value);
                idsObject[rows[i].columns[table.idColumns[0]].value] = rows[i];
            }
            var flowEventId = $(this).attr('flowEventId');
            var event = table.approveButtonEvent[grid.approveSelectParam.blockId][flowEventId];
            var batchData = form.batchApproveData;
            if (isEmpty(event) || isEmpty(batchData)){
                return;
            }
            var batchNodes = batchData.nodeList[event.block_id];
            var flowEvents = {};
            var flowEvent =[];
            var approveEvents = {};
            for (var i=0; i<batchNodes.length; i++){
                if (!isEmpty(batchNodes[i].data_id) && !isEmpty(idsObject[batchNodes[i].data_id])){
                    flowEvents[batchNodes[i].approve_id] = buildBlockEventParam(event, idsObject[batchNodes[i].data_id], null, true);
                    flowEvents[batchNodes[i].approve_id].data_id = batchNodes[i].data_id;
                    flowEvents[batchNodes[i].approve_id].block_id = batchNodes[i].block_id;
                    flowEvent = buildBlockEventParam(event, idsObject[batchNodes[i].data_id], null, true);
                    flowEvent.data_id = batchNodes[i].data_id;
                    flowEvent.block_id = batchNodes[i].block_id;
                }
            }
            //审批块事件
            var blockEvent, requestBlockEvent = {};
            blockEvent = table.approveBlockEvent[grid.approveSelectParam.blockId];

            if (!isEmpty(blockEvent)){
                for (var i=0; i<batchNodes.length; i++) {
                    if (!isEmpty(batchNodes[i].data_id) && !isEmpty(idsObject[batchNodes[i].data_id])) {
                        requestBlockEvent[batchNodes[i].approve_id] = {};
                        for (var key in blockEvent){
                            requestBlockEvent[batchNodes[i].approve_id][key] = buildBlockEventParam(blockEvent[key], idsObject[batchNodes[i].data_id], null, true);
                            requestBlockEvent[batchNodes[i].approve_id][key].block_id = batchNodes[i].block_id;
                        }
                    }
                }
            }
            //整体的审批通过与驳回事件
            if (!isEmpty(idsObject)){
                for (var key in idsObject){
                    approveEvents[key] = buildEventParam(table, '', idsObject[key]);
                }
            }
            postJson('/approve/approveData.do', {tableId: table.id, dataId: ids[0]}, function (result) {
            	if(checkApproveButtonEventListOrIndex(flowEvent,result)){
                	dx.processing.open();
                    postJson('/approve/batchApprove.do', {tableId: table.id, batchFlowEvents: flowEvents,
                        batchApproveEvents: approveEvents, batchBlockEvents: requestBlockEvent, approveReason: reason,formId : form.id}, function (data) {
                        dxToastAlert(msg("approveSuccess"));
                        dx.reloadIndexApprove();
                        grid.reload();
                        dx.processing.close();
                    });
                }
        	});
        });
        //查看按钮事件
		$toolbar.find('button.dx-grid-edit, button.dx-grid-view').on('click', function (event) {
			var rows = grid.getSelectedRows();
			var readonly = $(event.target).hasClass('dx-grid-view');
			if (!rows) {
				messageBox('noRowSelected');
				return;
			}

			if ((!readonly) && (!checkActionCondition(table, 'edit', rows))) return;

			// set edit request parameter list
			var batchData = [];
			rows.forEach(function (row) {
				var item = grid.buildRecordData(row.id);
				item.fixedData = fixedData();
				item.readonly = readonly;
				item.hasNext = true;
				batchData.push(item);
			});
			// last edit form in list has no next button
			batchData[batchData.length - 1].hasNext = false;
			// view list will using different edit url
			newTab(form.editUrl ? form.editUrl : '/detail/edit.view', batchData[0], function (newForm) {
				newForm.spliceBatchData(batchData);
			}, grid.id + '-' + readonly);
		});
        //双击查看。
        var $gridTable = form.get();

        if (isEmpty(table.block_view_style)){
            if (form.action != 'select')  //查看更多弹出框不加双击查看事件。
                $gridTable.find('table.dx-grid').datagrid('options').onDblClickRow = function (rowIndex, rowData) {
                    dx.processing.open();
                    var beforeAfterRows = {};
                    beforeAfterRows.rows = $gridTable.find('table.dx-grid').treegrid('getData');
                    var gridPaging = $gridTable.find('table.dx-grid').treegrid("getPager").pagination('options');
                    if (!isEmpty(gridPaging)){
                        beforeAfterRows.pageNumber = gridPaging.pageNumber;
                        beforeAfterRows.pageSize = gridPaging.pageSize;
                        beforeAfterRows.total = gridPaging.total;
                    }
                    beforeAfterRows.rowid = rowIndex.rowid;
                    beforeAfterRows.grid = grid;
                    beforeAfterRows.fixedData = fixedData();
                    beforeAfterRows.beforeAfterPage = function (pageNumber, callback) {
                        beforeAfterRows.newPageData = function (newRows) {
                            callback(newRows);
                        };
                        $gridTable.find('table.dx-grid').treegrid("getPager").pagination('select', pageNumber);
                    };
                    form.beforeAfterRows = beforeAfterRows;
                    var rows;
                    rows = rowIndex;
                    var readonly = true;
                    if (!rows) {
                        messageBox('noRowSelected');
                        return;
                    }
                    if ((!readonly) && (!checkActionCondition(table, 'edit', rows))) return;

                    // set edit request parameter list
                    var batchData = [];
                    var item = grid.buildRecordData(rows.rowid);
                    item.fixedData = fixedData();
                    item.readonly = readonly;
                    item.hasNext = false;
                    batchData.push(item);
                    // last edit form in list has no next button
                    batchData[batchData.length - 1].hasNext = false;

                    postPage(form.editUrl ? form.editUrl : '/detail/edit.view', batchData[0], function (result) {
                        form.get().hide();
                        var $div = form.get().parent().find('.create-edit');
                        $div.append(result);
                        // init new form
                        var newForm = getFormModel($div.find('.dx-form').attr('id'));
                        //编辑或新增保存
                        newForm.submit = function () {
                            form.get().show();
                            form.beforeAfterRows = null;
                            $div.empty();
                            grid.reload();
                        };
                        newForm.parentShow = function () {
                            form.get().show();
                            form.beforeAfterRows = null;
                            $div.empty();
                            grid.reload();
                        };
                        //上一条，下一条需要的参数。
                        newForm.beforeAfterRows = beforeAfterRows;
                        buildFormCache(newForm, newForm.widgets, true);
                        dx.init.detail(newForm);

                        newForm.p('batchData', batchData);
                        newForm.p('updated', true);

                        //返回按钮点击事件
                        $div.find('input.dx-back').on("click", function () {
                            form.get().show();
                            $div.empty();
                            grid.reload();
                            $("#"+grid.id).treegrid('resize');
                        });
                        dx.processing.close();
                    });
                    // view list will using different edit url
                    //newTab(form.editUrl ? form.editUrl : '/detail/edit.view', batchData[0], function (newForm) {
                    //    newForm.spliceBatchData(batchData);
                    //}, grid.id + '-' + readonly);
                };
        }else{
            $gridTable.on('dblclick', 'div.table-view-style-param', function () {
                var fieldId = $(this).find('.table-view-style-id').attr('id');
                var rows = w(fieldId);
                var readonly = true;
                if (!rows) {
                    messageBox('noRowSelected');
                    return;
                }
                if ((!readonly) && (!checkActionCondition(table, 'edit', rows))) return;

                // set edit request parameter list
                var batchData = [];
                var item = grid.buildRecordData(rows.rowid);
                item.fixedData = fixedData();
                item.readonly = readonly;
                item.hasNext = false;
                batchData.push(item);
                // last edit form in list has no next button
                batchData[batchData.length - 1].hasNext = false;
                // view list will using different edit url
                newTab(form.editUrl ? form.editUrl : '/detail/edit.view', batchData[0], function (newForm) {
                    newForm.spliceBatchData(batchData);
                }, grid.id + '-' + readonly);
            });
        }
		$toolbar.find('button.dx-grid-select').on('click', function () {
			var rows = grid.getSelectedRows();
			if (!rows) {
				messageBox('noRowSelected');
				return;
			//} else if (rows.length > 1) {
			//	messageBox('noMultiRow');
			//	return;
			}
            //大于1 为多选。
            if (rows.length > 1){
                var data = {table: form.tableName, keys: new Array()};
                for (var i=0; i<rows.length ; i++){
                    var keys = {};
                    table.idColumns.forEach(function (column) {
                        keys[column] = rows[i].columns[column].value;
                    });
                    keys[table.name_column] = rows[i].columns[table.name_column].value;
                    data.keys.push(keys);
                }
                form.close(data);
            }else{
                var data = {table: form.tableName, keys: {}};
                var nameExpression = getNameExpression(getTableDesc(form.tableName), rows[0]);

                table.idColumns.forEach(function (column) {
                    data.ref_id = rows[0].columns[column].value;
                });
                data.nameExpression = nameExpression;
                form.close(data);
            }
		});
		$toolbar.find('button.dx-grid-delete').on('click', function () {
			var rows = grid.getSelectedRows();
			if (!rows) {
				messageBox('noRowSelected');
				return;
			}
			if (!checkActionCondition(table, 'remove', rows)) return;
            checkCRUD(table, 'delete', rows, function (flag) {
                if (!flag)
                    return;
                confirmBox('delete confirm', function () {
                    var data = {
                        id: grid.id,
                        ids: []
                    };
                    $.each(rows, function (i, row) {
                        data.ids.push(row.id);
                    });
                    var triggers = afterCURDONewPage(form, '', '', '', true, rows);
                    if (!isEmpty(triggers)){
                        data.triggerRequestParams = triggers.parentActionButton;
                        data.childTriggerRequestParams = triggers.childActionButton;
                    }
                    postJson('/widget/grid/delete.do', data, function () {
                        grid.reload();
                    });
                });
            });

        });
		initFileUploadButton($toolbar.find('input.dx-grid-import'), function (uuid) {
			dx.processing.open("Import");
			showDialogForm({
				url: '/data/import.view',
				data: {parent: grid.id, fileId: uuid, formId: form.id},
				title: 'Import',
				shown: function (form, dialog) {
					form.p('dialog', dialog);
				}
			});
		});
		$toolbar.find('button.dx-grid-export').on('click', function () {
			var rids = grid.getSelectedRows(true);
			if (!rids) {
				messageBox('noRowSelected');
				return;
			}
			fileDownload('/data/export.do', {rids: rids, formId: form.id});
		});
		
		$toolbar.find('button.dx-grid-exportAll').on('click', function () {
			var filter={};
			var postData = {id: grid.id, filter: filter};
            var fb = form.filter.build('exec');
            postData.filter = fb.data();
			fileDownload('/data/exportAll.do', postData);
		});
		
		$toolbar.find('button.dx-grid-import').on('click', function () {
			var table=this.name;
			showDialogForm({
			   url: '/batch/batch.view',
			   data: {table: table,import_type:1},
			   title: msg('Import'),
			   class:"operation-param-dialog",
			   shown: function (form) {
			   }
			});
		});
		
		$toolbar.find('button.dx-grid-import-update').on('click', function () {
			var table=this.name;
			showDialogForm({
			   url: '/batch/batch.view',
			   data: {table: table,import_type:2},
			   title: msg('Import'),
			   class:"operation-param-dialog",
			   shown: function (form) {
			   }
			});
		});
		
		// set table operation handler
		form.operate = function (column, name, isDetail, checklevel) {
            execNewOperation(grid.table, column, name, grid.getSelectedRows(true), grid, isDetail, checklevel);
		};
		// set table shortcut handler
		form.shortcut = function (shortcut) {
			// shortcut menu active when single record selected
			var row = grid.getSelectedRows()[0];
			openTableShortcut(shortcut, row);
		};
	}

	form.p('noAutoRO', true);
	var $form = form.get();
	var table = getTableDesc(form.tableName);
	initComponent(form);
	var grid = w(form.grid.id);
	initOpMenu($form, form, true);
	if (form.ops || form.shortcuts) {
		grid.p('rowChanged', function () {
            // 原 操作等按钮没有权限的隐藏。针对每选中一条数据判断。先改成点击按钮才判断
            // 原代码任然保留着。下面注释放开就成。
			//calcMenuStatus(form, grid.getSelectedRows());
		});
	}
	var filterCallback = grid.filter;
	var resetCallback = grid.reset;

    //初始化select框
    initFilter(form, filterCallback, resetCallback);
    //初始化左侧查询
    if(isEmpty(table.block_view_style))
        initGridLeftSelect(form, filterCallback, function(){
            if (form.load) {
                //dx.processing.open(msg("Searching"));
                var fb = form.filter.build();
                grid.filter.call(fb);
            }
        });

	if (form.control)
		initToolbar();
	else {
		form.filter.hide();
		form.enableMenu(false);
		grid.columnVisible(0, false);
	}
    //用户自定义列按钮点击事件。
    form.get().on('click', '.user-column',function () {
        showDialogForm({
            url: '/widget/grid/userColumn.view',
            data: {
                parent: form.id,
                tableName: form.tableName
                //action: 'select'
            },
            title: msg('symbol'),
            class: 'dx-input-selector-dialog',
            needAutoExpand: true,
            shown: function (listForm, dialog) {
                //隐藏没有权限。以及列表里没有的列
                if (!isEmpty(grid.noAuthColumns))
                    for (var i=0; i<grid.noAuthColumns.length; i++){
                        listForm.get().find('li[name="' + grid.noAuthColumns[i] + '"]').remove();
                    }
                //TODO 隐藏列表中没有的列。
                //
                listForm.close = function(){
                    dialog.close();
                    reloadForm("/list/table.view",{table : form.tableName}, form, function(){});
                }
            }
        });
    });



    $('.testaddtest').on('click', function () {
        var param = {};
        param.entry_id = '1';
        param.entry_name = 'test111';
        param.login_id = 'test111';
        param.sex = '1';
        param.dept_id = 'develop2';
        param.role_id = '000002';
        param.phone = '1123321';
        param.emp_number = '123123';
        param.post_type = '02';
        param.role_date = '2017-12-27 18:05:03';
        param.begin_date = '2017-12-27 18:05:03';
        param.contract_month = '36';
        param.end_date = '2017-12-29 00:00:00';
        param.contract_plae = '130101';
        param.superior_user = '11111';
        param.work_place = '123';
        param.work_email = '1123';
        param.personal_email = '11234';
        param.probation_period = '3';
        param.probation_end_date = '2017-12-29';
        param.base_salary = '123';
        param.subsidy = '0';
        param.sum_salary = '0';
        postJson('/detail/test.do', {table: 't_entry_hr', param: param}, function (result) {

        });
    });
});                    
