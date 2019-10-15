/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 9/30/14
 * Time: 1:54 PM
 */

"use strict";

var AutoGenerator = function () {
	this.initHandler = AutoGenerator.prototype.init.bind(this);
	this.ajaxHandler = AutoGenerator.prototype.ajax.bind(this);
	this.resetHandler = AutoGenerator.prototype.reset.bind(this);
	this.saveHandler = AutoGenerator.prototype.save.bind(this);
	this.reloadHandler = AutoGenerator.prototype.reload.bind(this);
	this.drawHandler = AutoGenerator.prototype.draw.bind(this);
};

AutoGenerator.prototype.resize = function (height, $area) {
	$area.height(height);
	var $body = this.grid.get().parent();
	var $head = $body.prev();
	$body.height(this.form.p('expandHeight') - $head.height() - (this.form.hasSum ? 32 : 0));
};

AutoGenerator.prototype.reset = function () {
	this.form.p('modified', false);

	for (var i = 0; i < this.form.fieldIds.length; i++)
		w(this.form.fieldIds[i]).val('');

	delete this.grid.records;
	this.mode(false);
	this.grid.get().DataTable().ajax.reload();
};

AutoGenerator.prototype.mode = function (editing) {
	this.editing = editing;
	this.$cancel.prop('disabled', !editing);
	this.$save.prop('disabled', !editing);
	this.$checkall.prop('disabled', !editing).prop('checked', false);

	this.$ok.prop('disabled', editing);

	this.form.get().find('.dx-filter-form').find('input.dx-filter').prop('disabled', editing);
};

AutoGenerator.prototype.reload = function (ignore, filterData) {
	this.mode(true);
	this.filters = filterData.filters;
	this.grid.get().DataTable().ajax.reload();
};

AutoGenerator.prototype.save = function () {
	var self = this;
	var $form = this.form.get();
	var table = getTableDesc(this.form.grid.table);
	// field validate
	if (!$form.valid())
		return;
	// check rules for each selected records
	var records = [];
	w(this.grid.get().find(':checked').each(function () {
		records.push(w($(this).prop('name')));
	}));
	checkRecordsRules(records, table.checkRules, function () {
		// prepare post data
		var postData = {id: self.form.id, data: {}};
		records.forEach(function (record) {
			var data = {};
			$.each(record.fields, function (i, field) {
				if ($('#' + field.id).is('input'))
					data[field.id] = field.val();
				else
					data[field.id] = field.value;
			});
			postData.data[record.id] = data;
		});
		postJson('/ag/save.do', postData, function () {
			self.form.p('modified', false);
            self.dialog.close();
			//closeTab(self.$li);
			w(self.form.parent).updated();
		});
	});
};

AutoGenerator.prototype.ajax = function (ignore, callback) {
	var self = this;
	if (!this.editing)
		return callback({data: []});
	var filterData = buildSQLParam(this.form, this.form.ag.gen_sql);
	forEach(this.filters, function (column, data) {
		switch (data.type) {
			case 'between':
				if (data.from)
					filterData[column + '.from'] = data.from;
				if (data.to)
					filterData[column + '.to'] = data.to;
				break;
			default:
				filterData[column] = data.value;
		}
	});
	postJson('/ag/list.do', {id: this.form.id, filterData: filterData}, function (records) {
		// using by dataTable for rendering
		var data = buildGridData(self.grid, records, null, function (field, record) {
			// set parent fields for evaluate formula
			record.columns._parent = {ref: self.form.p('parent').columns};
			record.action = 'create';
			if (record.fieldTags[field.id])
			// get tag html from map which created in controller
				return record.fieldTags[field.id];
			else{
				return '<label id="' + field.id + '">' + (field.value == null ? '' : fieldText(field, record) + '</label>');
            }
		});
		callback({data: data});
	})
};

AutoGenerator.prototype.init = function (form, dialog) {
	this.form = form;
	this.prepare(form.get());
	initField(form);
	initFilter(form, this.reloadHandler, this.resetHandler);
	this.dialog = dialog;
	this.grid = w(form.grid.id);
	form.p('resize', AutoGenerator.prototype.resize.bind(this));

	// prepare parent columns for sql
	this.form.columns._parent = {ref: form.p('parent').columns};
	var $form = form.get();

	// set save button click callback
	this.$save = $form.find('.dx-ag-save').on('click', this.saveHandler);
	// set ok button click callback
	this.$ok = $form.find('.dx-filter-button');
	// set cancel button click callback
	this.$cancel = $form.find('.dx-btn-cancel').on('click', this.resetHandler);
	this.mode(false);
};

AutoGenerator.prototype.draw = function () {
	// not init yet
	if (!this.grid) return;
	var grid = this.grid;
	var $grid = grid.get();
	$grid.DataTable().columns.adjust();
	if (!grid.records) return;
	var self = this;
	$.each(grid.records, function (i, record) {
		buildFormCache(record, record.fields);
		// init widgets, each record treated as a standard form
		initField(record);
		// set new created input with default value assigned in #ajaxLoad
		record.p('noAutoRO', true);
		$.each(record.fields, function (i, field) {
			// set default values, if any
			var desc = getDesc(field);
			if ((field.value === null) && desc.default_value)
				field.value = evaluate(desc.default_value, record);
            if (!isEmpty(desc.ref_table_name)){
                var table = getTableDesc(desc.ref_table_name);
                if (!isEmpty(table.name_expression_publicity)){
                    $('#' + field.id).val(evaluate(table.name_expression_publicity, record.columns[desc.column_name].ref, null, null, true));
                }else if (table.name_column){
                    var nameValue;
                    if (!isEmpty(record.columns[desc.column_name]) && record.columns[desc.column_name].ref)
                        nameValue = record.columns[desc.column_name].ref[table.name_column];
                    $('#' + field.id).val(nameValue);
                }else{
                    $('#' + field.id).val(record.columns[desc.column_name].value);
                }
            }
		});
		self.disableRecord(record);
		record.p('noAutoRO', false);
	});
};

AutoGenerator.prototype.disableRecord = function (record) {
	record.fields.forEach(function (field) {
		if ($('#' + field.id).is('input'))
			field.enable(false);
	});
};

AutoGenerator.prototype.sum = function (desc) {
	if (!this.grid || !this.grid.records)
		return "0";
	var sum = this.grid.records.reduce(function (a, b) {
		var model = b.columns[desc.column_name];
		var field = w(model.id);
		if (field == null)
			return a + Number(model.value);
		else if (field.val)
			return a + field.val();
		else
			return a + Number(field.value);
	}, 0);
	return formatNumber(sum, desc.sum_flag);
};

AutoGenerator.prototype.updateSum = function (table, dataTable) {
	for (var i = 0; i < this.form.columns.length; i++) {
		var columnName = this.form.columns[i];
		var column = table.columnMap[columnName];
		if (column.sum_flag == null)
			continue;
		// Update footer
		$(dataTable.column(i + 1).footer()).html(this.sum(column));
	}
};

/**
 * render table before popup, better user xp
 * @param $form
 */
AutoGenerator.prototype.prepare = function ($form) {
	var $grid = $form.find('.dx-ag-grid');
	//init grid
	var options = {
		ajax: this.ajaxHandler,
		order: [],
		columns: [
			{
				data: 'rowid',
				orderable: false,
				render: function (val) {
					return '<input type="checkbox" class="dx-grid-checkrow" name="' + val + '" id="' + val + '_ck"/>';
				}
			}
		],
		paging: false,
		//serverSide: true,
		scrollX: "100%",
		deferLoading: true,
		dom: 't',
		// all record is drawn, we need cache them
		drawCallback: this.drawHandler
	};

	var self = this;
	var table = getTableDesc(this.parentGrid.table);
	var view = getTableDesc(table.autoGens[this.agid].ref_view);
	this.form.columns.forEach(function (columnName) {
		var desc = table.columnMap[columnName];
		var width;
		var viewDesc = view.columnMap[desc.column_name];
		if (viewDesc && viewDesc.viewStyle && viewDesc.viewStyle.ag)
			width = viewDesc.viewStyle.ag.width;
		else
			width = undefined;
		// push it
		options.columns.push({
			orderable: self.form.inputs.indexOf(columnName) == -1,
			data: desc.column_name,
			width: width,
			visible: ((view.columnMap[desc.column_name] != undefined) && (!view.columnMap[desc.column_name].hidden))
		});
	});
	if (this.form.hasSum)
		options.footerCallback = function () {
			self.updateSum(table, this.api());
		};

	var $tbody = $grid.find('tbody');
	// checkbox for all
	this.$checkall = $grid.find(':checkbox').on('click', function () {
		$tbody.find(':checkbox').prop('checked', this.checked).trigger('modify');
	});
	// checkbox for rows
	$tbody.on('click modify', ':checkbox', function () {
		if (this.checked) {
			self.form.p('modified', true);
			evaluateReadonly(w(this.name));
		} else
			self.disableRecord(w(this.name));
	});

	// update sum
	if (self.form.hasSum)
		$tbody.on('change modify', '.dx-number, .dx-digits', function () {
			self.updateSum(table, self.grid.get().DataTable());
		});
	// init grid
	$grid.DataTable(options);
};

AutoGenerator.prototype.open = function (grid, agid) {
	if (this.opened)
		return;
	this.form = this.grid = null;
	this.opened = true;
	this.agid = agid;
	this.parentGrid = grid;
	var ag = getTableDesc(grid.table).autoGens[agid];
	if (ag.mode === 'single')
		showDialogForm({
			url: '/ag/dialog.view',
			title: i18n(getTableDesc(grid.table).i18n),
			data: {parent: grid.id, id: agid},
			shown: function (form, dialog) {
				form.p('dialog', dialog);
				form.p('ag', ag);
			}
		});
	else{
        var that = this;
        showDialogForm({
            url: '/ag/dialog.view',
            title: i18n(getTableDesc(grid.table).i18n),
            data: {parent: grid.id, id: agid},
            class: 'child-dialog',
            shown: function (newForm, dialog) {
                that.initHandler(newForm, dialog);
                newForm.submit = function () {
                    dialog.close();
                };
            }
            //hidden: function (form) {
            //    grid.reload();
            //}
        });
        //newTab('/ag/dialog.view', {parent: grid.id, id: agid}, this.initHandler);
    }
};

registerInit('agSingle', function (form) {
	function generate() {
		var param = buildSQLParam(form, form.p('ag').gen_sql);
		postJson('/ag/single/create.do', {id: form.id, param: param}, function () {
			form.reset();
			grid.reload();
		}, null, true);
	}

	form.columns._parent = {ref: form.p('parent').columns};
	var $form = form.get();
	var $div = $form.find('div.container');
	var grid = w(form.parent);
	$div.find('input:enabled:visible:not([readonly]):first').focus();
	$div.find('input:enabled:visible:not([readonly])').keypress(function (e) {
		if (e.which != 13)
			return;
		generate();
		return false;
	});
	$form.find('button.dx-ag-single-add').on('click', function () {
		generate();
	});
	$form.find('button.dx-btn-cancel').on('click', function () {
		form.p('dialog').close();
	});
});