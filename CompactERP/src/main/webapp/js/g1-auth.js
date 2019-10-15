/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 9/30/14
 * Time: 1:54 PM
 */

"use strict";

var AuthEntryEditor = function ($form, noOwner) {
	var self = this;
	this.hasOwner = !noOwner;
	this.$editor = $form.find('.auth-entry-editor');
	this.dialog = new BootstrapDialog({
		autodestroy: false,
		message: self.$editor,
		title: msg('Auth edit')
	});

	if (this.hasOwner)
		this.$enabler = this.$editor.find('.auth-editor-enabler').on('change', function () {
			self.$editor.find('.dx-auth-owner').find(':input').prop('disabled', !this.checked);
		});

	this.$selects = this.$editor.find('select.dx-auth-editor-options').multiselect($.extend({}, defaultMultiselectOptions, {
		onChange: function (option, checked, select) {
			if (!checked)
				return;
			if (option.val() === '*') {
				this.$select.multiselect('deselectAll');
				this.$select.multiselect('select', '*');
			} else
				this.$select.multiselect('deselect', '*');
		}
	}));

	this.$editor.find('button.dx-auth-entry-save').on('click', AuthEntryEditor.prototype.save.bind(this));
	this.$editor.find('button.dx-btn-cancel').on('click', this.dialog.close.bind(this.dialog));
};

AuthEntryEditor.prototype.open = function (updateHandler, entry) {
	this.updateHandler = updateHandler;
	this.dialog.open();
	this.$editor.removeClass('hidden');

	// reset all select for creating
	this.$selects.multiselect('deselectAll', false);
	var $s1, $s2, $s3;
	if (entry) {
		$s1 = $(this.$selects[0]);
		$s2 = $(this.$selects[1]);
		$s3 = $(this.$selects[2]);
		entry.operators.forEach(function (op) {
			if (op.user)
				$s3.multiselect('select', op.user);
			else {
				$s1.multiselect('select', op.dept);
				$s2.multiselect('select', op.role);
			}
		});
		if (this.hasOwner && entry.owners) {
			$s1 = $(this.$selects[3]);
			$s2 = $(this.$selects[4]);
			$s3 = $(this.$selects[5]);
			entry.owners.forEach(function (op) {
				if (op.user)
					$s3.multiselect('select', op.user);
				else {
					$s1.multiselect('select', op.dept);
					$s2.multiselect('select', op.role);
				}
			});
		}
	}

	// update select text
	this.$selects.multiselect('updateButtonText');

	if (this.hasOwner)
		this.$enabler.prop('checked', (entry && entry.owners) != undefined).change();
};

AuthEntryEditor.prototype.save = function () {
	function makeRoleArray(a1, a2) {
		if (a1 == null || a2 == null)
			return [];
		var ret = [];
		a1.forEach(function (i1) {
			a2.forEach(function (i2) {
				ret.push({dept: i1, role: i2});
			})
		});
		return ret;
	}

	function makeUserArray(a) {
		if (a == null)
			return [];
		var ret = [];
		a.forEach(function (i) {
			ret.push({user: i});
		});
		return ret;
	}

	// check first
	var entry = {operators: [], owners: []};

	entry.operators = entry.operators.concat(makeRoleArray($(this.$selects[0]).val(), $(this.$selects[1]).val()));
	entry.operators = entry.operators.concat(makeUserArray($(this.$selects[2]).val()));
	if (entry.operators.length === 0) {
		messageBox("select at least one operator");
		return;
	}
	if (this.hasOwner && this.$enabler.prop('checked')) {
		entry.owners = entry.owners.concat(makeRoleArray($(this.$selects[3]).val(), $(this.$selects[4]).val()));
		entry.owners = entry.owners.concat(makeUserArray($(this.$selects[5]).val()));
		if (entry.owners.length === 0) {
			messageBox("select at least one owners");
			return;
		}
	} else
		entry.owners = null;

	this.updateHandler(entry);
	this.dialog.close();
};

var AuthControlList = function (form, $form, editor) {
	var self = this;
	var $lis = $form.find('ul.dx-auth-list').find('li');
	var $ul = $form.find('div.dx-div-with-scroll');
	this.map = {};
	form.controls.forEach(function (control) {
		self.map[control.id] = control;
	});
	$form.find('a.dx-auth-edit').on('click', function (e) {
		e.preventDefault();
		$lis.removeClass('active');
		$(this).parent().addClass('active');

		editor.load(self.map[this.name]);
	});

	form.p('resize', function (height, $div) {
		$div.height(height);
		$ul.height(height);
	});

	// if exists, load first entry
	if ($lis.length) {
		$($lis[0]).addClass('active');
		editor.load(form.controls[0]);
	}
};

AuthControlList.prototype.control = function (id) {
	return this.map[id];
};

var AuthGroupEditor = function () {
};

AuthGroupEditor.prototype.init = function (form) {
	var self = this;
	this.control = null;
	this.form = form;
	var $form = form.get();
	$form.find('.dx-auth-save').on('click', AuthGroupEditor.prototype.save.bind(this));

	this.entryEditor = new AuthEntryEditor($form);

	this.$table = $form.find('table.dx-auth-entries');
	this.table = this.$table.DataTable({
		paging: false,
		dom: 't',
		columnDefs: [
			{
				targets: [0],
				render: function (data) {
					return sprintf('<button type="button" class="btn btn-default btn-xs dx-grid-delete dx-grid-child-delete" value="%s"><span class="glyphicon glyphicon-minus"></span></button><button type="button" class="btn btn-default btn-xs dx-grid-edit dx-grid-child-edit" value="%s"><span class="glyphicon glyphicon-edit"></span></button>', data, data);
				}
			}
		]
	});

	this.$table.find('.dx-grid-add').on('click', function () {
		self.entry = null;
		self.entryEditor.open(AuthGroupEditor.prototype.update.bind(authGroupEditor));
	});
	this.$table.on('click', '.dx-grid-delete', function () {
		self.remove(this.value);
	});
	this.$table.on('click', '.dx-grid-edit', function () {
		self.entry = self.control.entries[this.value];
		self.entryEditor.open(AuthGroupEditor.prototype.update.bind(authGroupEditor), self.entry);
	});

	new AuthControlList(form, $form, this);
};

AuthGroupEditor.prototype.remove = function (index) {
	var self = this;
	confirmBox('delete confirm', function () {
		self.control.entries.splice(index, 1);
		self.load()
	});
};

AuthGroupEditor.prototype.update = function (entry) {
	if (!this.entry)
		this.control.entries.push(entry);
	else {
		this.entry.operators = entry.operators;
		this.entry.owners = entry.owners;
	}
	this.load();
};

AuthGroupEditor.prototype.load = function (control) {
	function format(ops) {
		if (!ops) return '';
		var ret = '';
		ops.forEach(function (op) {
			if (ret.length) ret += ', ';
			if (op.user)
				ret += op.user;
			else
				ret += op.dept + '.' + op.role;
		});
		return ret;
	}

	var self = this;
	// control not defined mean reload current control
	if (control) this.control = control;
	this.table.clear(false);
	if (!this.control.entries)
		this.control.entries = [];
	this.control.entries.forEach(function (entry, index) {
		self.table.row.add([index, format(entry.operators), format(entry.owners)]);
	});
	this.table.draw();
};

AuthGroupEditor.prototype.save = function () {
	postJson('/auth/group.do', {controls: this.form.controls}, function () {
		messageBox("Update success");
	});
};

var authGroupEditor = new AuthGroupEditor();
registerInit('authGroup', AuthGroupEditor.prototype.init.bind(authGroupEditor));

// auth menu edit form
var AuthMenuEditor = function () {
};

AuthMenuEditor.prototype.init = function (form) {
	var self = this;
	this.control = null;
	this.form = form;
	var $form = form.get();
	this.configMap = {};
	this.form.configs.forEach(function (config) {
		var authId = config.auth_id;
		var array = self.configMap[authId];
		if (!array)
			array = self.configMap[authId] = [];
		array.push(config.target_id);
	});

	this.$tree = $form.find('.dx-auth-menu-tree').jstree({
		plugins: ['checkbox', 'types']
	});

	$form.find('.dx-auth-save').on('click', AuthMenuEditor.prototype.save.bind(this));

	this.entryEditor = new AuthEntryEditor($form, true);
	$form.find('.dx-auth-entry-edit').on('click', function () {
		self.load(self.controlList.control(this.value));
		var entry = self.control.entries && self.control.entries[0] ? self.control.entries[0] : null;
		self.entryEditor.open(AuthMenuEditor.prototype.update.bind(self), entry);
	});

	this.controlList = new AuthControlList(form, $form, this);
};

AuthMenuEditor.prototype.update = function (entry) {
	if (!this.control.entries)
		this.control.entries = [];
	this.control.entries[0] = entry;
};

AuthMenuEditor.prototype.updateConfig = function () {
	var self = this;
	var ids = this.configMap[this.control.id];
	// clear menu ids connect to control, and keep the reference
	ids.length = 0;
	this.$tree.jstree("get_selected").forEach(function (id) {
		if (id.indexOf(self.form.id) === 0)
			ids.push(id.substr(self.form.id.length + 1));
	});
};

AuthMenuEditor.prototype.load = function (control) {
	var self = this;
	if (this.control) {
		if (this.control.id === control.id)
			return;
		// when switching control, save menu permission
		this.updateConfig();
	}

	this.control = control;

	// load tree
	this.$tree.jstree('deselect_all');
	this.ids = this.configMap[control.id];
	if (!this.ids) {
		this.ids = this.configMap[control.id] = [];
		return;
	}
	var nodes = [];
	this.ids.forEach(function (id) {
		nodes.push(self.form.id + '-' + id)
	});
	this.$tree.jstree('select_node', nodes, true, true);
};

AuthMenuEditor.prototype.save = function () {
	this.updateConfig();
	postJson('/auth/menu.do', {controls: this.form.controls, configMap: this.configMap}, function () {
		messageBox("Update success");
	});
};

var authMenuEditor = new AuthMenuEditor();
registerInit('authMenu', AuthMenuEditor.prototype.init.bind(authMenuEditor));
