/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 11/11/14
 * Time: 3:19 PM
 */

"use strict";

function complexFieldValidator(ignore, element) {
	var field = w(element.id);
	return field.valid();
}

function complexReadonlyEval(desc, form) {
	if (!desc.complex_id) return;

	var complex = getComplexDesc(desc.complex_id);
	// init keys connected to complex field
	if (!complex.keys)
		complex.keys = getParamList(complex.ins_disp_sql);
	if ((form.action != 'create')) {
		// set complex key field disabled
		complex.keys.forEach(function (key) {
			var params = getParamList(desc.complex_mapping[key]);
			params.forEach(function (name) {
				if (form.columns[name])
					form.columns[name].enable(false);
			});
		})
	}
}

function initComplexField(form, desc, field) {
	function valid() {
		var field = this;
		// field and details been sync since last save
		if (field.syncValue != field.val())
			return false;
		if (field.syncValue == 0)
			return true;
		// in edit mode, key should be readonly
		if (form.action != 'create')
			return true;
		var desc = getDesc(field);
		var complex = getComplexDesc(desc.complex_id);
		// check keys saved is same with current form values
		var ret = complex.keys.every(function (key) {
			return field.postData.param[key] == evaluate(desc.complex_mapping[key], field.form);
		});
		field.postData.reload = !ret;
		return ret;
	}

	function initComplexButtons(listForm, grid, dialog) {
		listForm.get().find('.dx-complex-save').on('click', function () {
			var saveData;
			if (!complex.extended) {
				// check value range
				if (!listForm.get().valid())
					return;
				saveData = {id: listForm.id, param: {}};
				// build <record id> --> <amount> map
				grid.inputs.each(function () {
					var $this = $(this);
					saveData.param[$this.parents('tr').attr('id')] = Number($this.autoNumeric('get'));
				});
			} else
				saveData = {id: listForm.id};
			postJson('/widget/complex/save.do', saveData, function () {
				// set field value, trigger modify to reset field valid state
				field.val(grid.sum);
				field.syncValue = grid.sum;
				field.p('sum', grid.sum);
				field.modify();
				dialog.close();
			})
		});
		listForm.get().find('.dx-btn-cancel').on('click', function () {
			dialog.close();
		});
	}

	function dialogClosed() {
		field.p('editing', false);
	}

	function gridOptHandler(opts) {
		opts.columns[0].visible = complex.extended;
		if (!complex.extended) {
			var column = opts.columns[opts.columns.length - 1];
			column.render = function (data, type, full) {
				// return input tag with min/max values if any for validation
				var amount = full.rawData.amount;
				return '<input id="' + amount.id
					+ '" name="' + complex.detail_tbl_number_col + full.rowid
					+ '" class="dx-complex-input" value="' + amount.value
					+ '" required min="0" '
					+ (complex.base_tbl_number_col ? 'max="' + full[complex.base_tbl_number_col] + '"' : '')
					+ ' />';
			};
		}
		var grid = this;
		grid.inputColumn = opts.columns.length - 1;
		opts.footerCallback = function () {
			var api = this.api();
			$(api.column(grid.inputColumn - 1).footer()).html(msg('Total'));
			$(api.column(grid.inputColumn).footer()).html(formatNumber(grid.sum, true));
		};
		return opts;
	}

	function gridPostInit() {
		var grid = this;
		grid.records.forEach(function (record) {
			// build records, amount cache
			w(record);
			w(record.amount)
		});
		var $grid = grid.get();
		var api = $grid.dataTable().api();
		if (!complex.extended)
			grid.inputs = $grid.find('.dx-complex-input')
				.autoNumeric('init', {
					vMin: 0,
					vMax: '99999999999999999999'
				}).on('change', function () {
					grid.sum = 0;
					grid.inputs.each(function () {
						grid.sum += Number($(this).autoNumeric('get'));
					});
					$(api.column(grid.inputColumn).footer()).html(formatNumber(grid.sum, true));
				});
		if (w(grid.parent).action == 'view')
			containerDisabled($grid, true, '.dx-complex-input');
	}

	function gridDataHandler(array) {
		var amount = field.val();
		var grid = this;
		grid.sum = 0;
		array.forEach(function (data) {
			var record = data.rawData;
			record.amount = record.fields[record.fields.length - 1];
			// detail table field
			if (record.amount.value === null) {
				var newval = complex.base_tbl_number_col ? Math.min(amount, data[complex.base_tbl_number_col]) : 0;
				record.amount.value = newval;
				amount -= newval;
				grid.sum += newval;
			} else
				grid.sum += record.amount.value;
		});
		return array;
	}

	function gridAddHandler(grid) {
		showDialogForm({
			url: '/widget/complex/create.view',
			data: {parent: grid.id, table: detailTable.id},
			title: i18n(detailTable.i18n),
			shown: function (detailForm, dialog) {
				detailForm.p('savedHandler', function () {
					grid.updated();
					dialog.close();
				});
			}
		});
	}

	function gridEditHandler(grid, recordId) {
		showDialogForm({
			url: '/widget/complex/edit.view',
			data: {parent: recordId, table: detailTable.id},
			title: i18n(detailTable.i18n),
			shown: function (detailForm, dialog) {
				detailForm.p('savedHandler', function () {
					grid.updated();
					dialog.close();
				});
			}
		});
	}

	function popupDialog() {
		if (field.p('editing'))
			return;
		field.p('editing', true);
		// prepare sql parameters
		try {
			// for switching from view to edit, we need action dynamic
			field.postData.action = form.action;
			field.postData.param = buildSQLParam(form, sql, desc.complex_mapping);
		} catch (e) {
			field.p('editing', false);
			messageBox(e);
			return;
		}
		// popup edit dialog
		showDialogForm({
			url: '/widget/complex/dialog.view',
			data: field.postData,
			title: 'complex field',
			shown: function (listForm, dialog) {
				var grid = w(listForm.grid.id);
				var columnList = [];
				listForm.columns.forEach(function (column) {
					columnList.push(getColumnDesc(complex.extended ? complex.detail_tbl : complex.base_tbl, column));
				});
				columnList.push(getColumnDesc(complex.detail_tbl, complex.detail_tbl_number_col));
				grid.p('columns', columnList);
				grid.optionsHandler = gridOptHandler;
				grid.dataHandler = gridDataHandler;
				grid.postInit = gridPostInit;
				if (complex.extended) {
					grid.p('add', gridAddHandler);
					grid.p('edit', gridEditHandler);
				}
				grid.sum = 0;
				initComponent(listForm);
				initComplexButtons(listForm, grid, dialog);
			},
			hidden: dialogClosed,
			fail: dialogClosed
		});
	}

	// init complex editor button
	if (!desc.complex_id)
		return;

	var complex = getComplexDesc(desc.complex_id);
	var detailTable = getTableDesc(complex.detail_tbl);
	var sql = form.action == 'create' ? complex.ins_disp_sql : (complex.extended) ? 'select #{parent_id}, #{parent_name}' : complex.upd_disp_sql;
	field.postData = {parent: field.id};
	// save form for validate
	field.form = form;
	field.syncValue = form.action != 'create' ? field.val() : 0;
	field.valid = valid;
	var $popupButton = $(field.get().attr('aria-controls'));
	$popupButton.on('click', function () {
		popupDialog();
	});
	field.get().on('blur', function () {
		if (field.valid())
			return;
		// detail data needed
		popupDialog();
	});
	var origEnable = field.enable;
	field.enable = function (enabled) {
		if (complex.extended)
			enableItem(this.get(), false);
		else
			origEnable(enabled);

		enableItem($popupButton, enabled);
	};
}

/**
 * make complex parent table id using arguments, used by formula that defined in configurations
 */
function complexKey() {
	var key = null;
	for (var i = 0; i < arguments.length; i++)
		if (key === null)
			key = arguments[i];
		else
			key += ',' + arguments[i];
	return key;
}