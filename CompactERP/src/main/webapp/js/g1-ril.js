/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 10/14/14
 * Time: 2:03 PM
 */

"use strict";

registerInit('ril', function (form) {
	function ajaxLoad(ignore, callback) {
		var postData = {id: form.id, filterData: {}};
		//prepare key values
		$.each(form.fieldIds, function (i, fid) {
			var field = w(fid);
			postData.filterData[field.id] = field.val();
		});
		postJson('/ril/list.do', postData, function (records) {
			// using by dataTable for rendering
			var data = buildGridData(grid, records, null, function (field, record) {
				// set default values, if any
				var desc = getDesc(field);
				if (desc.default_value)
					field.value = evaluate(desc.default_value, record);
				// for complex column
				record.action = 'create';
				// virtual or mapping field are readonly
				if (desc.virtual || (form.mapping[field.column] != undefined))
					return '<label id="' + field.id + '">' + field.value == null ? '' : fieldText(field, form) + '</label>';
				else
				// get tag html from map which created in ReferenceInputListController
					return record.fieldTags[field.id];

			});
			callback({data: data});
		})
	}

	/**
	 * reset form, clear filter and grid data
	 */
	function reset() {
		for (var i = 0; i < form.fieldIds.length; i++) {
			var field = w(form.fieldIds[i]);
			field.enable(true);
			field.val('');
		}

		enableItem($save, false);
		enableItem($cancel, false);
		enableItem($ok, true);
		$grid.DataTable().ajax.reload();
	}

	function save() {
		// prepare post data
		var postData = {id: form.id, data: {}};
		$.each(grid.records, function (i, record) {
			var data = {};
			$.each(record.fields, function (i, field) {
				if ($('#' + field.id).is('input'))
					data[field.id] = field.val();
				else
					data[field.id] = field.value;
			});
			postData.data[record.id] = data;
		});
		postJson('/ril/save.do', postData, function () {
			reset();
		});
	}

	var $form = form.get();
	initComponent(form);
	var grid = w(form.grid.id);
	var $grid = grid.get();
	var options = {
		sort: false,
		ajax: ajaxLoad,
		order: [],
		paging: false,
		serverSide: true,
		scrollX: "100%",
		deferLoading: true,
		dom: 't',
		columns: [
			{
				data: 'rowid',
				visible: false
			}
		],
		// all record is drawn, we need cache them
		drawCallback: function () {
			$grid.DataTable().columns.adjust();
			if (!grid.records) return;
			$.each(grid.records, function (i, record) {
				buildFormCache(record, record.fields);
				// init widgets, each record treated as a standard form
				initField(record);
				// set new created input with default value assigned in #ajaxLoad
				$.each(record.fields, function (i, field) {
					if ((field.value != null) && $('#' + field.id).is('input'))
						field.val(field.value);
				})
			});
		}
	};
	var table = getTableDesc(form.tableName);
	// push all column display options
	$.each(table.columns, function (i, desc) {
		// no auto increase key
		if (desc.column_name == table.idColumns[0])
			return;
		// no sys columns
		if (isSystemColumn(desc))
			return;
		// push it
		options.columns.push({data: desc.column_name});
	});

	// init grid
	$grid.DataTable(options);

	// set save button click callback
	var $save = $form.find('.dx-ril-save').on('click', function () {
		// field validate
		if (!$form.valid())
			return;
		// check rules for each record
		checkRecordsRules(grid.records, table.checkRules, save);
	});
	// set ok button click callback
	var $ok = $form.find('.dx-btn-ok').on('click', function () {
		for (var i = 0; i < form.fieldIds.length; i++)
			if (!w(form.fieldIds[i]).get().valid())
				return;
		for (i = 0; i < form.fieldIds.length; i++)
			w(form.fieldIds[i]).enable(false);

		enableItem($save, true);
		enableItem($cancel, true);
		enableItem($ok, false);
		$grid.DataTable().ajax.reload();
	});
	// set cancel button click callback
	var $cancel = $form.find('.dx-btn-cancel').on('click', function () {
		reset();
	});
	// disable buttons
	enableItem($save, false);
	enableItem($cancel, false);
});
