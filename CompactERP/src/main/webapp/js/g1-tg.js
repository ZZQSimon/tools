/**
* Created with IntelliJ IDEA.
* User: zang.loo
* Date: 16-12-14
* Time: 上午10:48
*/

"use strict";

registerInit('tg', function (form) {
	function loadTree(nodes) {
		if (nodes)
			nodes.forEach(function (node) {
				var record = node.data;
				// for bom tree, root node without record
				if (!record) return;
				buildRecordCache(record);
			});

		$tree.jstree(true).settings.core.data = nodes;
		$tree.jstree(true).refresh();
		form.p('nodes', nodes);
	}

	function filter(ignore, filter) {
		postJson("/tg/list.do", {id: form.id, filter: filter}, loadTree);
        dx.processing.close();
	}

	function reloadTree() {
		dx.processing.open();
		postJson("/tg/reload.do", {id: form.id}, loadTree);
        dx.processing.close();
	}

	function reset() {
		if (form.type === 'simple')
			filter(null, {tableName: form.tableName, filters: {}});
		else
			loadTree(null);
	}

	function fixedData(nodes) {
		if (nodes.length === 0)
			return undefined;
		var record = nodes[0].data;
		var ret = undefined;
		if (record) {
			ret = {};
			ret[table.parent_id_column] = record.columns[table.children_id_column].value;
		}
		return ret;
	}

	function getSelectedRow(treeData) {
		var nodes = jt.get_selected(true);
		if (nodes.length === 0 || !nodes[0].data)
			return null;
		return treeData ? nodes[0] : nodes[0].data;
	}

	function initToolbar() {
		var $toolbar = $("#menu-bar-" + form.id);
		$toolbar.find('button.dx-tree-grid-add').on('click', function () {
			if (!form.p('nodes')) {
				messageBox('data not loaded');
				return;
			}

			var row = getSelectedRow();
			if (!checkActionCondition(table, 'create', [row])) return;
			showDialogForm({
				url: '/tg/create.view',
				title: i18n(table.i18n),
				data: {
					parent: form.id,
					table: form.tableName,
					fixedData: fixedData(jt.get_selected(true)),
					hideButtons: ['next']
				},
				class: "detail-dialog",
				shown: function (detailForm, dialog) {
                    detailForm.get('input.dx-back').find().hide();
					// we need valid tree first
					detailForm.submitUrl = '/tg/create.do';
					detailForm.p('savedHandler', function (node) {
						node.parent = jt.get_selected();
						if (node.parent.length === 0)
							node.parent = '#';
						buildRecordCache(node.data);
						dialog.close();
						jt.create_node(node.parent, node, 'last');
					});
				}

			});
		});
		$toolbar.find('button.dx-tree-grid-edit, button.dx-tree-grid-view').on('click', function (event) {
			var row = getSelectedRow();
			if (!row) {
				messageBox('noRowSelected');
				return;
			}

			var readonly = $(event.target).hasClass('dx-tree-grid-view');

			if ((!readonly) && (!checkActionCondition(table, 'edit', [row]))) return;

			// set edit request parameter list
			var item = {
				parent: form.id,
				table: form.tableName,
				readonly: readonly,
				hasNext: false,
				param: {}
			};
			$.each(row.columns, function (key, field) {
				item.param[key] = field.value;
			});

			showDialogForm({
				url: '/detail/edit.view',
				title: i18n(table.i18n),
				data: item,
				class: "detail-dialog",
				shown: function (detailForm, dialog) {
                    detailForm.get('input.dx-back').find().hide();
					detailForm.p('savedHandler', function () {
						dialog.close();
						reloadTree();
					});
				}

			});
		});
		$toolbar.find('button.dx-tree-grid-delete').on('click', function () {
			function doDelete() {
				var data = {
					id: form.id,
					ids: [row.id]
				};
				postJson('/tg/delete.do', data, reloadTree);
			}

			var node = getSelectedRow(true);
			if (!node) {
				messageBox('noRowSelected');
				return;
			}
			var row = node.data;
			if (!checkActionCondition(table, 'remove', [row])) return;

			if (form.type === 'simple' && node.children.length > 0)
				confirmBox('delete node with children', function () {
					doDelete();
				});
			else
				confirmBox('delete confirm', doDelete);
		});
		$tree.on('select_node.jstree', function (e, data) {
			var record = data.node.data;
			var param = !record ? [] : [record];
			calcMenuStatus(form, param);
		});
		function buildRecordsModel(treeNodes) {
			var nodes = form.p('nodes');
			if (!nodes || nodes.length === 0)
				return;
			treeNodes.forEach(function (treeNode) {
				nodes.some(function (node) {
					if (treeNode === node.id && node.data) {
						w(node.data);
						return true;
					}
				});
			});
		}

		$tree.on('redraw.jstree', function (e, data) {
			buildRecordsModel(data.nodes)
		});
		$tree.on('open_node.jstree', function (e, data) {
			buildRecordsModel(data.node.children)
		});
		// set table operation handler
		form.operate = function (column, name) {
			// heck for jstree
			var selected = jt.get_selected(true);
			var ids;
			if (selected.length === 0)
				ids = null;
			else {
				var record = selected[0].data;
				w(record);
				ids = [record.id];
			}
			execOperation(form.tableName, column, name, ids, form);
		};
		// set table shortcut handler
		form.shortcut = function (shortcut) {
			openTableShortcut(shortcut, jt.get_selected(true)[0].data);
		};
		form.onUpdated = function () {
			reloadTree();
		}
	}

	var $form = form.get();
	var table = getTableDesc(form.tableName);
	var columns = [];
	iteratorGridHeaders(table, table.columns, null, function (desc, visible) {
		if (!visible)
			return;
		var column = {
            width:180,
			header: getColumnText(desc), value: function (node) {
				var record = node.data;
				if (!record) return '';
				return fieldText(record.columns[desc.column_name], record);
			}
		};
		if ((form.type === 'bom' && desc.column_name === table.children_id_column)
			|| (form.type === 'simple' && desc.column_name === table.name_column))
			columns.splice(0, 0, column);
		else
			columns.push(column);
	});

	var $tree = $form.find('.dx-tree-grid').jstree({
        height:"100%",
		width:"100%",
        columnWidth:"auto",
		plugins: ['core', 'grid'],
		core: {
            dblclick_toggle: false,
			check_callback: true,
			multiple: false,
			data: []
		},
		grid: {columns: columns,
            resizable:true}
	});

	//树网格距离父级的距离
    $form.find(".tile-tree").css("top",$form.find(".dx-filter-select").height()+1);

	// $tree.parents('.dx-auto-expand').on('dx.auto-expand', function () {
	// 	$tree.parents('.jstree-grid-wrapper').height(form.p('expandHeight'));
	// });
	var jt = $tree.jstree(true);
	initField(form);
	initFilter(form, filter, reset);
	initOpMenu($form, form);
	initToolbar();
	var fb = form.filter.build();
	if (form.load || fb.hasData())
		filter(null, fb.data());

    //form.get().find('.dx-filter-form').find('button.dx-filter-button').click();
});
