/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 6/3/14
 * Time: 2:09 PM
 */

"use strict";

/**
 * dx frontend formula functions, executor, constants, etc.
 * @type {{}}
 */
var dxf = {
	_child: function (form, tableName) {
		var child = null;
		form.children.forEach(function (v) {
			if (v.table === tableName) {
				child = v;
				return false;
			}
		});
		return child;
	},
	/**
	 * sum child table's column
	 * this function should called in a detail form with children tables
	 * when children record updated
	 *
	 * @param id column define, ie. table.column
	 */
	sum: function (id) {
		var name = id.split('.');
		var child = dxf._child(this, name[0]);
		var grid = w(child.id);

		return Number(grid.sum(name[1]));
	},
	/**
	 * get max value of child.column
	 *
	 * @param id column define, ie. table.column
	 * @returns {number}
	 */
	max: function (id) {
		var name = id.split('.');
		var child = dxf._child(this, name[0]);
		var grid = w(child.id);

		var ret = undefined;
		grid.iterate(name[1], function (val) {
			if ((ret === undefined) || (val > ret))
				ret = val;
		});
		return ret;
	},
	/**
	 * get min value of child.column
	 *
	 * @param id column define, ie. table.column
	 * @returns {number}
	 */
	min: function (id) {
		var name = id.split('.');
		var child = dxf._child(this, name[0]);
		var grid = w(child.id);

		var ret = undefined;
		grid.iterate(name[1], function (val) {
			if ((ret === undefined) || (val < ret))
				ret = val;
		});
		return ret;
	},
	/**
	 * check child.column if all value is unique
	 *
	 * @param id column define, ie. table.column
	 * @returns {number}
	 */
	unique: function (id) {
		var name = id.split('.');
		var child = dxf._child(this, name[0]);
		var grid = w(child.id);

		var set = {};
		return grid.iterate(name[1], function (val) {
			if ((val === undefined) || (val === null))
				return;
			if (set[val] !== undefined)
				return false;
			else
				set[val] = true;
		});
	},
    childVal: function (columnName, beforeNum) {
        if (!beforeNum)
            return null;
        var form = this;
        var parentForm = w(form.parent);
        if (isEmpty(parentForm))
            return null;
        switch (beforeNum){
            case 1:
                if (isEmpty(parentForm.records) || parentForm.records.length == 0)
                    return null;
                if (isEmpty(parentForm.records[parentForm.records.length - 1].columns) ||
                        isEmpty(parentForm.records[parentForm.records.length - 1].columns[columnName]))
                    return null;
                return parentForm.records[parentForm.records.length - 1].columns[columnName].value;
            case 2:
                break
        }
        return null;
    },
	/**
	 * get unique values count of child.column
	 *
	 * @param id column define, ie. table.column
	 * @returns {number}
	 */
	uniqueCount: function (id) {
		var name = id.split('.');
		var child = dxf._child(this, name[0]);
		var grid = w(child.id);

		var set = {};
		grid.iterate(name[1], function (val) {
			if ((val === undefined) || (val === null))
				return;
			set[val] = set[val] === undefined;
		});
		var cnt = 0;
		$.each(set, function (k, v) {
			if (v == true)
				cnt++;
		});
		return cnt;
	},
	count: function (tableName) {
		var child = dxf._child(this, tableName);
		return w(child.id).records.length;
	},
	/**
	 * exec sql, should return single record and single field
	 *
	 * @param sql
	 * @param isList
	 */
	sql: function (sql, isList) {
		var ret = execCodeSql(this, sql, isList);
		if (!isList && ret === null)
			return '';
		return ret;
	},
	/**
	 * get table next sequence key
	 * @param table
	 * @param params
	 */
	seq: function (table, params) {
		var ret = '';
		postJson('/data/seq.do', {table: table, keys: params}, function (key) {
			ret = key;
		}, null, true);
		return ret;
	},
	/**
	 * using milliseconds since 01.01.1970 to generate Date
	 * if time not provided, return now
	 *
	 * @param time milliseconds since 01.01.1970
	 * @param index day index for time type, 0 for last day, 1 for first day
	 *      time == 'd', index will be offset, and offset ignored
	 * @param offset offset for time type, only month and year supported by now.
	 *      time == 'm', for month
	 *      time == 'y', for year
	 */
	date: function (time, index, offset) {
		if (time === undefined){
            var time2 = new Date().getTime();
            return new Date().getTime();
        }
		else if (typeof time !== 'string')
			return new Date(time).getTime();
		var date = new Date();
		if (offset === undefined)
			offset = 0;
		if (index === undefined)
			index = 1;
		var day;
		switch (time) {
			case 'm':
				if (index === 0)
					offset++;
				day = new Date(date.getFullYear(), date.getMonth() + offset, index);
				break;
			case 'y':
				var year = date.getFullYear() + offset;
				var month = 0;
				if (index === 0)
					year++;

				day = new Date(year, month, index);
				break;
			case 'd':
				day = new Date(date.getFullYear(), date.getMonth(), date.getDate() + index);
				break;
		}
        if (time.indexOf('-') != -1){
            return new Date(time).getTime();
        }
		return day.getTime();
	},
	/**
	 * get date raw text
	 *
	 * @param time
	 * @param fmt
	 * @returns {*}
	 */
	dateText: function (time, fmt) {
		var t;
		if (time === undefined)
			t = moment();
		else if (typeof time === 'string') {
			t = moment();
			fmt = time;
		} else
			t = moment(time);
		if (fmt === undefined)
			fmt = 'YYYYMMDD';
		return t.format(fmt);
	},
	/**
	 * get dict text
	 *
	 * @param key1
	 * @param key2
	 */
	dict: function (key1, key2) {
		return i18n(dx.dict[key1][key2]);
	},
	/**
	 * get column ter/build/BigDecimal-all-last.min.jsi18n label
	 *
	 * @param table
	 * @param column
	 */
	columnLabel: function (table, column) {
		return i18n(getTableDesc(table).columnMap[column].i18n);
	},
	/**
	 * get original value of field
	 *
	 * @param name field name
	 * @param newValue default value if original value not exists
	 */
	orig: function (name, newValue) {
		var orig = this.columns[name].orig;
		return orig === undefined ? newValue : orig;

	},
    i18n: function(i18n_key){
        if (isEmpty(i18n_key)){
            return '';
        }
        return msg(i18n_key);
    },
    domainKey: function () {
        return dx.domainKey;
    }
};

dx.formula = {
	_context: {},
	context: function (module, data) {
		if (data === undefined)
			return this._context[module];
		this._context[module] = data;
	},
	dxf: dxf,
	tree: {
		_getNode: function (id) {
			var node = dx.formula.context('tree').node;
			// call from outside of the field valid formula
			if (node === undefined) {
				var field = this.getField(id);
				var nodes = field.jt.get_selected(true);
				if (nodes.length === 0) return false;
				node = nodes[0];
			} else
				dx.formula.context('tree', {});
			return node;
		},
		/**
		 * check a tree node is a leaf node
		 */
		isLeaf: function (id) {
			var node = dx.formula.tree._getNode.call(this, id);
			return !node.children || node.children.length === 0;
		},
		/**
		 * return node depth, 1 for root
		 */
		depth: function (id) {
			var node = dx.formula.tree._getNode.call(this, id);
			return node.parents.length;
		}
	}
};
