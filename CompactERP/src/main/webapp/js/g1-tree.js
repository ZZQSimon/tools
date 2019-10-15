/**
 * User: huyannan
 * Date: 6/5/14
 * Time: 10:20 AM
 */

//设置编辑界面的编辑属性
function columndisabled(formId, param) {
	//初期化时将输入框变成不可入力
	var form = getFormModel(formId);
	for (var i = 0; i < form.fields.length; i++) {
		$('#' + form.fields[i].id).attr('disabled', param);
	}
	//按钮readonly
	$('#' + formId + "_cancel").attr('disabled', param);
	$('#' + formId + "_save").attr('disabled', param);
}

//生成树
registerInit('tree', function (form) {
	var $form = form.get();
	initComponent(form);
	evaluateReadonly(form);

	form.p('noAutoRO', true);
	form.$tree = $('#' + form.id + '_div');
	var formId = form.id;
	var treeType = form.treeType;
	var ids = $form.find(".dx-selector-btn").attr("aria-controls");
	$form.find(".dx-selector-btn").on("click", function () {
		function loadTree() {
			$('#' + formId + 'conditions').val("");

			// 保持查询条件的值
			var idsValue = "";
			$.each(ids.split(","), function (i, fid) {
				var field = w(fid);
				if (idsValue == "") {
					idsValue = field.val();
				} else {
					idsValue = idsValue + "," + field.val();
				}
			});
			$('#' + formId + 'idsValue').val(idsValue);

			// 畫面編輯用按鈕的活性化設定
			var arg = false;
			var searchMode = $('[name="' + formId + 'Mode' + '"]').filter(':checked');
			if (searchMode.length == 0) {
				searchMode.val('1')
			}

			if ('2' == searchMode.val()) {
				arg = true;
			}
			$('#' + formId + "_higher").attr('disabled', arg);
			$('#' + formId + "_edit").attr('disabled', arg);
			$('#' + formId + "_lower").attr('disabled', arg);
			$('#' + formId + "_delete").attr('disabled', arg);

			refreshTree(formId);
		}

		//画面的必须check
		if (treeType != 2)
			loadTree();
		else
			checkForm(form, loadTree);
	});
	// 保持查询条件的标签名
	$('#' + formId + 'ids').val(ids);

	var params = {param: {}, id: formId, conditions: ""};
	postJson('/tree/selectAllTreeNode.do', params,
		function (retData) {
			if (retData.nodes.length > 0) {
				form.$tree.jstree({
					core: {
						'data': retData.nodes
					}
				});
			}
			if (form.extend)
				return;
			form.$tree.on("select_node.jstree", function (e, arg) {
				// 保持BOM选择的节点情报
				$('#' + formId + 'parent').val(arg.node.parent);

				if ($('#' + formId + 'control').val() == '1') {
					return false;
				}

				// BOM树 选择根节点时
				if (treeType == 2 && $('#' + formId + 'parent').val() == "#") {
					clearinput(formId);
					return false;
				}
				//将画面右半部分readonly
				columndisabled(formId, true);

				var data = {};
				data["id"] = (arg.selected)[0];
				var params = {param: data, id: formId};

				postJson('/tree/selectnode.do', params,
					function (retData) {
						var form = getFormModel(formId);
						$.each(form.fieldIds, function (i, id) {
							var field = w(id);
							field.val(retData[field.column]);
						});
					});
			});
		});
	columndisabled(formId, true);

	form.$collapse = $('.dx-tree-collapse').click(function () {
		form.$tree.jstree("close_all");
	});
	form.$expand = $('.dx-tree-expand').click(function () {
		form.$tree.jstree("open_all");
	});
	form.$export = $('.dx-tree-export').click(function () {
		var ids = [];
		form.$tree.find('.jstree-node').each(function (i, node) {
			ids.push(node.id);
		});
		fileDownload('/tree/export.do', {id: form.id, ids: ids});
	});
});

function getTreeGridText(node) {
	if (!node.data)
		return '';
	return fieldText(node.data.fields[this.index], node.data);
}

//刷新树时调用
function refreshTree(formId) {
	//将画面右半部分readonly
	columndisabled(formId, true);

	var searchMode = $('[name="' + formId + 'Mode' + '"]').filter(':checked');
	var ids = $('#' + formId + 'ids').val();
	var selectData = {};
	var conditions = $('#' + formId + 'conditions').val();
	if (conditions == "") {
		$.each(ids.split(","), function (i, fid) {
			var field = w(fid);
			selectData[field.id] = field.val();
		});
	}
	var params = {param: selectData, id: formId, conditions: conditions, searchMode: searchMode.val()};

	var form = w(formId);
	var table = getTableDesc(form.tableName);
	postJson('/tree/selectAllTreeNode.do', params,
		function (retData) {
			$('#' + formId + 'conditions').val(retData["conditions"]);
			var options = {
				core: {
					data: retData.nodes
				}
			};
			if (form.extend) {
				var columns = [{width: 300, header: ''}];
				for (var i = 0; i < table.columns.length; i++) {
					var column = table.columns[i];
					columns.push({
						index: i,
						header: i18n(column.i18n),
						value: getTreeGridText,
						width: column.data_type == 12 ? 130 : 100
					})
				}

				options.plugins = ['grid'];
				options.grid = {columns: columns}
			}
			form.$tree.jstree(options);
			form.$tree.jstree(true).settings.core.data = retData.nodes;
			form.$tree.jstree(true).refresh();
			if (!form.extend)
				clearinput(formId);
			else {
				form.$export.attr('disabled', false);
				form.$expand.attr('disabled', false);
				form.$collapse.attr('disabled', false);
			}
		});
}

//点击增加顶级时，先清空输入框的值，再将父机构readonly
function addTreeRootNode(formId, flag) {
	disableSet(formId, true);
	$('#' + formId + 'control').val('1');
	var form = getFormModel(formId);
	$('#' + formId + 'action').val(flag);
	clearinput(formId);

	//将编辑界面中除了父之外的所有输入框变成可编辑模式
	var table = getTableDesc(form.tableName);
	$.each(form.fieldIds, function (i, fid) {
		var field = w(fid);
		if (field.column != table.parent_id_column) {
			$('#' + field.id).attr('disabled', false);
		}
	});
	//按钮可读
	$('#' + formId + "_cancel").attr('disabled', false);
	$('#' + formId + "_save").attr('disabled', false);
}

//设置画面左半边的disabled属性
function disableSet(formId, arg) {
	var ids = $('#' + formId + 'ids').val();
	$.each(ids.split(","), function (i, fid) {
		$('#' + fid).attr('disabled', arg);
	});
	$('#' + formId).find(".dx-selector-btn").attr('disabled', arg);
	$('#' + formId + "_higher").attr('disabled', arg);
	$('#' + formId + "_edit").attr('disabled', arg);
	$('#' + formId + "_lower").attr('disabled', arg);
	$('#' + formId + "_delete").attr('disabled', arg);
}

//点击编辑按钮
function editTreeNode(formId, flag) {
	var form = getFormModel(formId);
	$('#' + formId + 'action').val(flag);

	var val = "";
	//根据选择的节点将数据显示在画面上
	if ($.jstree.reference('#' + formId + '_div') != null) {
		val = $.jstree.reference('#' + formId + '_div').get_selected();
	}
	if (val.length == 0) {
		return messageBox(msg("DCS-904"));
	} else if (form.treeType == 2 && $('#' + formId + 'parent').val() == "#") {
		return messageBox(msg("DCS-904"));
	} else {
		disableSet(formId, true);
		$('#' + formId + 'control').val('1');

		var data = {};
		data["id"] = val[0];
		var params = {param: data, id: formId};

		//点击编辑按钮之后重新将选中的节点对应的数据填充到编辑界面上，并且编辑界面为可编辑模式
		postJson('/tree/selectnode.do', params,
			function (retData) {
				$.each(form.fieldIds, function (i, id) {
					var field = w(id);
					field.val(retData[field.column]);
				});
			});

		var table = getTableDesc(form.tableName);
		$.each(form.fieldIds, function (i, fid) {
			var field = w(fid);
			var disabledFlag = false;
			$.each(table.idColumns, function (j, col) {
				if (field.column == col || field.column == table.parent_id_column) {
					disabledFlag = true;
					return;
				}
			});
			$('#' + field.id).attr('disabled', disabledFlag);
		});
		//按钮可读
		$('#' + formId + "_cancel").attr('disabled', false);
		$('#' + formId + "_save").attr('disabled', false);
	}
}

//点击增加低级时，先将父机构readonly，再清空个输入框的值
function addTreeNode(formId, flag) {
	var form = getFormModel(formId);
	$('#' + formId + 'action').val(flag);

	//获取选择节点的id
	var val = "";
	//根据选择的节点将数据显示在画面上
	if ($.jstree.reference('#' + formId + '_div') != null) {
		val = $.jstree.reference('#' + formId + '_div').get_selected();
	}
	if (val.length == 0) {
		messageBox(msg("DCS-904"));
	} else {
		disableSet(formId, true);
		$('#' + formId + 'control').val('1');

		var table = getTableDesc(form.tableName);
		if (form.treeType == 2 && $('#' + formId + 'parent').val() == "#") {
			$.each(form.fieldIds, function (i, fid) {
				var field = w(fid);
				if (table.valid_date_cols != null
					&& table.valid_date_cols.indexOf(field.column) >= 0) {
					// 清空输入框的值和可读
					field.val("");
					$('#' + field.id).attr('disabled', false);
				} else {
					var disabledFlag = false;
					var ids = $('#' + formId + 'ids').val();
					var idsValue = $('#' + formId + 'idsValue').val().split(",");
					$.each(ids.split(","), function (j, id) {
						var conditionField = w(id);
						if (conditionField.column == field.column) {
							disabledFlag = true;
							field.val(idsValue[j]);
							return;
						}
					});
					if (!disabledFlag) {
						// 清空输入框的值和可读
						field.val("");
						$('#' + field.id).attr('disabled', false);
					}
				}
			});
		} else {
			var data = {};
			data["id"] = val[0];
			var params = {param: data, id: formId};

			postJson('/tree/selectnode.do', params,
				function (retData) {
					if (retData == null) {
						messageBox("DCS-901");
					} else {
						$.each(form.fieldIds, function (i, fid) {
							var field = w(fid);
							if (field.column == table.parent_id_column) {
								field.val(retData[table.children_id_column]);
							} else if (field.column == table.children_id_column
								|| (table.valid_date_cols != null
								&& table.valid_date_cols.indexOf(field.column) >= 0)) {
								// 清空输入框的值和可读
								field.val("");
								$('#' + field.id).attr('disabled', false);
							} else {
								if (form.treeType == 2) {
									var flag = false;
									$.each(table.idColumns, function (j, col) {
										if (field.column == col) {
											flag = true;
											field.val(retData[field.column]);
											return;
										}
									});
									if (!flag) {
										// 清空输入框的值和可读
										field.val("");
										$('#' + field.id).attr('disabled', false);
									}
								} else {
									// 清空输入框的值和可读
									field.val("");
									$('#' + field.id).attr('disabled', false);
								}
							}
						});
					}
				});
		}
		//按钮可读
		$("#" + formId + "_cancel").attr('disabled', false);
		$("#" + formId + "_save").attr('disabled', false);
	}
}

//点击删除按钮
function DeleteTreeNode(formId) {
	var form = getFormModel(formId);

	//获取选择节点的id
	var val = "";
	//根据选择的节点将数据显示在画面上
	if ($.jstree.reference('#' + formId + '_div') != null) {
		val = $.jstree.reference('#' + formId + '_div').get_selected();
	}

	if (val.length == 0) {
		messageBox(msg("DCS-905"));
	} else if (form.treeType == 2 && $('#' + formId + 'parent').val() == "#") {
		return messageBox(msg("DCS-905"));
	} else {
		var data = {};
		data["id"] = val[0];
		data["parent_id"] = $('#' + formId + 'parent').val();
		var params = {param: data, id: formId};

		postJson('/tree/deleteCheck.do', params,
			function (msg) {
				confirmBox(msg, function () {
					postJson('/tree/delete.do', params,
						function (msg) {
							messageBox(msg, function () {
								refreshTree(formId);
							});
						});
				});
			});
	}
}

//点击保存按钮
function SaveTreeData(formId, flag) {
	var form = getFormModel(formId);
	checkForm(form, function () {

		var url = '/tree/${action}.do';
		var data = {};
		$.each(form.fieldIds, function (i, fid) {
			var field = w(fid);
			data[field.id] = field.val();
		});
		var params = {param: data, id: formId, conditions: $('#' + formId + 'conditions').val()};

		if (flag == "1") {
			//增加顶级或增加低级
			url = url.replace('${action}', 'create');
			postJson(url, params,
				function (msg) {
					messageBox(msg, function () {
						$('#' + formId + 'control').val('0');
						disableSet(formId, false);

						clearinput(formId);
						refreshTree(formId);
					});
				});
		} else if (flag == "2") {
			//编辑处理
			url = url.replace('${action}', 'edit');
			postJson(url, params,
				function (msg) {
					messageBox(msg, function () {
						$('#' + formId + 'control').val('0');
						disableSet(formId, false);

						refreshTree(formId);
					});
				});
		}
	});
}

//点击取消按钮
function CloseTree(formId) {
	$('#' + formId + 'control').val('0');
	columndisabled(formId, true);
	disableSet(formId, false);
	clearinput(formId);
}

//将画面上的值清空
function clearinput(formId) {
	var form = getFormModel(formId);
	var table = getTableDesc(form.tableName);
	$.each(form.fieldIds, function (i, fid) {
		var field = w(fid);
		var Desc = table.columnMap[field.column];
		if (Desc.data_type == "5") {
			field.val(false);
		} else {
			field.val("");
		}
	});
}

function Tree(tree, selected) {
	var tthis = this;
	this.model = tree;
	this.$tree = tree.get().jstree({
		core: {
			multiple: false,
			data: function (node, cb) {
				var tthis = this;
				postJson('/tree/load.do', {id: tree.id, node: node.id}, function (result) {
					result.forEach(function (n) {
						n.children = true;
					});
					cb.call(tthis, result);
				});
			}
		}
	});
	this.selectedId = '';
	this.$tree.on('select_node.jstree', function (e, data) {
		if (tthis.selectedId === data.node.id) {
			data.instance.deselect_node(data.node);
			tthis.selectedId = '';
			return;
		} else
			tthis.selectedId = data.node.id;
		if (selected)
			selected.call(tree, data.node.id);
	});
}

Tree.prototype.getSelectedId = function () {
	return this.selectedId;
};
Tree.prototype.reload = function () {
	return this.$tree.jstree('refresh');
};
Tree.prototype.reset = function () {
	this.selectedId = '';
	this.$tree.jstree('deselect_all');
};
Tree.prototype.valid = function (formula) {
	var nodes = this.$tree.jstree(true).get_selected(true);
	if (nodes.length === 0)
		return false;
	if (isEmpty(formula))
		return true;
	var node = nodes[0];
	if (node.data) {
		node.data._node = node;
		node.data._tree = this.model;
	} else
		node.data = {_node: node, _tree: this.model};
	return evaluate(formula, node.data);
};
