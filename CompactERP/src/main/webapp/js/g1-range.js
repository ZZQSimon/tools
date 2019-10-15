/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 9/26/14
 * Time: 10:25 AM
 */

"use strict";
registerInit('range', function (form) {
	w(form.children[0].id).p('ajax', false);
	initComponent(form);
	evaluateReadonly(form);
	form.p('roSelector', function () {
		if (form.p('isSearch'))
			return null;
		else
			return form.get().find('.dx-range-filter-field').find('input.dx-field');
	});

	// 画面的初期处理
	if (form.mode == "create") {
		form.p('isSearch', false);

		conditionDisabled(form, false);
		detailDisabled(form, true);
	} else if (form.mode == "edit") {
		searchRange(form.id, false);

		conditionDisabled(form, true);
		detailDisabled(form, false);
	}

	// 一览情报的事件处理
	var grid = w(form.children[0].id);
	// 新建按钮
	grid.p('add', function (grid) {
		if (form.p('isSearch')) {
			$('#' + form.id + 'actionMode').val("create");
			$('#' + form.id + 'recordId').val("");
			form.p('validDate', null);
			conditionDisabled(form, true);
			cleanInput(form, false);
			detailDisabled(form, false);
		}
	});
	// 编辑按钮
	grid.p('edit', function (grid, recordId) {
		var table = getTableDesc(form.tableName);
		$('#' + form.id + 'actionMode').val("edit");
		$('#' + form.id + 'recordId').val(recordId);
		form.p('validDate', null);

		var record = w(recordId);
		$.each(form.fieldIds, function (i, id) {
			var field = w(id);
			var fieldValue = record.columns[field.column].value;
			field.val(fieldValue);

			if (table.valid_date_cols != null
				&& table.valid_date_cols.indexOf(field.column) == 0) {
				form.p('validDate', field.val());
			}
		});

		conditionDisabled(form, true);
		detailDisabled(form, false);
	});
	// 删除按钮
	grid.p('delete', function (grid, recordId) {
		confirmBox(msg("DCS-004"), function () {
			var tempValue = "";
			var data = {};
			var record = w(recordId);
			var table = getTableDesc(form.tableName);
			$.each(form.fieldIds, function (i, id) {
				var field = w(id);
				var fieldValue = record.columns[field.column].value;
				tempValue = field.val();
				field.val(fieldValue);

				if (table.valid_date_cols != null
					&& table.valid_date_cols.indexOf(field.column) >= 0) {
					data[field.column] = field.get().val();
				} else {
					data[field.column] = fieldValue;
				}

				field.val(tempValue);
			});
			var params = {param: data, id: form.id};

			postJson('/range/delete.do', params,
				function (retData) {
					if (retData.ret == "true") {
						messageBox(retData.msg, function () {
							if (recordId == $('#' + form.id + 'recordId').val()) {
								$('#' + form.id + 'actionMode').val("");
								form.p('validDate', null);
								conditionDisabled(form, false);
								cleanInput(form, false);
								detailDisabled(form, true);
							}

							grid.updated();
						});
					} else {
						messageBox(retData.msg);
					}
				});
		});
	});
});

//点击画面上的查询按钮
function searchRange(formId, isReset) {
	function doSearch() {
		// 编辑查询条件
		var where = '';
		$.each(form.fieldIds, function (i, fid) {
			var field = w(fid);
			for (var i = 0; i < form.filter.length; i++) {
				if (field.id == form.filter[i].id) {
					if (where) {
						where = where + ' and ';
					}
					where = where + field.table + "." + field.column + '=\'' + field.val() + '\'';
				}
			}
		});

		// 一览情报再表示
		var grid = w(form.children[0].id);
		grid.filter(where);

		cleanInput(form, false);
	}

	var form = getFormModel(formId);

	form.p('isSearch', !isReset);
	if (isReset)
		doSearch();
	else //画面的必须check
		checkForm(form, doSearch);
}

//点击画面上的重置按钮
function resetRange(formId) {
	var form = getFormModel(formId);

	cleanInput(form, true);
	conditionDisabled(form, false);
	detailDisabled(form, true);

	searchRange(formId, true);
}

//点击画面上的保存按钮
function saveRange(formId) {
	// 画面check
	var form = getFormModel(formId);
	checkForm(form, function () {
		// 参数编辑
		var url = '/range/${actionMode}.do';
		var data = {};
		$.each(form.fieldIds, function (i, fid) {
			var field = w(fid);
			data[field.id] = field.val();
		});
		var params = {
			param: data,
			id: formId,
			groupCols: form.groupCols,
			validDate: form.p('validDate')
		};

		// 数据新规或更新
		url = url.replace('${actionMode}', $('#' + form.id + 'actionMode').val());
		postJson(url, params,
			function (retData) {
				if (retData.ret == "true") {
					messageBox(retData.msg, function () {
						$('#' + formId + 'actionMode').val('');
						cleanInput(form, false);
						conditionDisabled(form, false);
						detailDisabled(form, true);

						w(form.children[0].id).updated();
					});
				} else {
					messageBox(retData.msg);
				}
			});
	});
}

//设置编辑界面详细部以外的编辑属性
function conditionDisabled(form, param) {
	// 检索部的编辑属性
	for (var i = 0; i < form.filter.length; i++) {
		$('#' + form.filter[i].id).attr('disabled', param);
	}

	//按钮部的编辑属性
	$('#' + form.id + "_submit").attr('disabled', !param);
	$('#' + form.id + "_search").attr('disabled', param);
}

//设置编辑界面详细部的编辑属性
function detailDisabled(form, param) {
	var isKey = false;
	var table = getTableDesc(form.tableName);
	// 详细部的编辑属性
	for (var i = 0; i < form.record.length; i++) {
		if ("create" != $('#' + form.id + 'actionMode').val()) {
			for (var j = 0; j < table.idColumns.length; j++) {
				if (table.idColumns[j] == form.record[i].column) {
					$('#' + form.record[i].id).attr('disabled', true);
					isKey = true;
					break;
				}
			}
		}
		if (!isKey) {
			$('#' + form.record[i].id).attr('disabled', param);
		}
		isKey = false;
	}
}

//将画面上的值清空
function cleanInput(form, isReset) {
	var isKey = false;
	var table = getTableDesc(form.tableName);
	$.each(form.fieldIds, function (i, fid) {
		var field = w(fid);

		if (!isReset && form.groupCols != null) {
			var valid = form.groupCols.split(",");
			for (var k = 0; k < valid.length; k++) {
				if (valid[k] == field.column) {
					isKey = true;
					break;
				}
			}
		}

		if (!isKey) {
			var Desc = table.columnMap[field.column];
			if (Desc.data_type == "5") {
				field.val(false);
			} else {
				field.val("");
			}
		}
		isKey = false;
	});
}
