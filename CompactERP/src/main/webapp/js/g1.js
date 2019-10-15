/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 4/11/14
 * Time: 9:24 AM
 */

"use strict";

// data holder for all global data
var dx = {
    init: {},// form init functions for each modules
    moduleInit: {},// form post init functions for each modules
    modules: {},
    tabIndex: 1,
    ajax: 0,
    i18n: {},
    cache: {form: {}, modules: {}},
    sysColumnGroupName: '_SYS_COLUMN',
    isOpenMessage: 1,
    /**
     * get/set cache entry per module using id, or whole cache data
     *
     * @param module
     * @param [id]
     */
    c: function (module, id) {
        var data = this.cache.modules[module];
        if (!data) {
            var self = this;
            postJson('/data/cache/' + module + '/load.do?', function (cache) {
                self.cache.modules[module] = data = cache;
            }, null, true);
        }
        if (id === undefined)
            return data;
        if ((data[id] === undefined) && dx.debug)
            dxError('Cache[\'' + module + '\'][\'' + id + '\'] not found.');
        return data[id];
    },
    processing: (function () {
        var $div = $('<div class="progress-wrap"><i class=""></i></div><!--<div class="progress"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"><span class="sr-only"></span></div></div>--!>');
        var dialog;
        return {
            open: function (title) {
                if (dialog) return;
                dialog = BootstrapDialog.show({
                    title: msg(title || 'Processing'),
                    cssClass:"g1-refresh",
                    closable: false,
                    message: $div
                });
            },
            close: function () {
                if (!dialog) return;
                dialog.close();
                dialog = null;
            },
            isOpen: function () {
                return dialog != null;
            }
        };
    })(),
    
    mobileProcessing: (function () {
        var $div = $('<div class="progress-wrap"><i class=""></i></div>');
        var dialog;
        return {
            open: function (title) {
                if (dialog) return;
                dialog = BootstrapDialog.show({
                    title: msg(title || 'Processing'),
                    cssClass:"g1-refresh",
                    closable: false,
                    message: $div
                });
            },
            close: function () {
                if (!dialog) return;
                dialog.close();
                dialog = null;
            },
            isOpen: function () {
                return dialog != null;
            }
        };
    })(),
    startTimer: function () {
        dx.timer = {start: new Date().getTime(), log: []};
    },
    logTimer: function (tag) {
        if (!dx.timer)
            this.startTimer();
        dx.timer.log.push({tag: tag, time: new Date()});
    },
    printTimerLog: function () {
        var prevTime = dx.timer.start;
        dx.timer.log.forEach(function (log) {
            var now = log.time.getTime();
            dxLog(now - dx.timer.start, log.tag, now - prevTime);
            prevTime = now;
        })
    }
};

// input select menu constants
var menuType = {
    more: 1,
    create: 2
};
/**
 * check if array contain the item
 *
 * @param array
 * @param item
 */
function contain(array, item) {
    return array.indexOf(item) >= 0;
}

/**
 * evaluate variable, eg. 'column', 'column.name', '_parent.column.name
 *
 * @param variable
 * @param form
 * @param [emptyVal]
 * @returns {*}
 */
function evaluateVar(variable, form, emptyVal) {
    if (emptyVal === undefined)
        emptyVal = '';
    if (variable.indexOf('__') === 0)
        return eval(variable.substr(2));
    // name[0] =  field name, name[1] = reference field
    var name = variable.split(".");
    var val;
    if (name.length == 1) {
        if (form.columns) {
            var obj = form.columns[name[0]];
            if (obj === undefined)
                if (name[0] === '_table')
                    val = form.tableName;
                else
                    throw {message: 'variable:${' + variable + '} not exists'};
            else if (typeof obj === 'string')  //首页审批 为string
                val = obj;
            else
            // using obj.value if form is record of grid
                val = obj.val ? obj.val() : obj.value;
        } else if (form.var)
            val = form.var(name[0]);
        else
            val = form[name[0]];
    } else if (form.columns[name[0]].ref === undefined) {
        val = form.columns[name[0]];
        for (var i = 1; i < name.length; i++)
            val = val[name[i]];
    } else {
        var field = form.columns[name[0]];
        if (field.ref === null && !isEmpty(field.value)) {  // reference not fetched yet, do it now
            field.val(field.value);
            field.p('synced', true);
        }
        var ref = field.ref;
        for (i = 1; i < name.length - 1; i++)
            ref = ref[name[i]].ref;
        if (ref === null)
            return emptyVal;
        val = ref[name[i]];
        // if val is a field, get value using val()
        if (val === null)
            val = emptyVal;
        else if (typeof val === 'object')
            val = val.val ? val.val() : val.value;
    }
    if (val === undefined)
        throw {message: 'variable:${' + variable + '} not exists'};
    return val;
}

/**
 * register custom function for evaluate
 *
 * @param name
 * @param func
 */
function registerFormulaFunc(name, func) {
    dxf[name] = func;
}

/**
 * set variable value for evaluate
 * @param form
 * @param name
 * @param value
 */
function setFormVariable(form, name, value) {
    if (form.columns === undefined)
        form.columns = {};
    form.columns[name] = {value: value};
}

/**
 * evaluate formula
 *
 * @param formula
 * @param [form] form model, could be omitted when evaluate default values
 * @param [emptyVal] return value when error occur, default is ''
 * @returns {Object}
 */
function evaluate(formula, form, column, oldData, isStr, withNoIdData, dataId) {
    function insert(orig, index, str) {
        if (index > 0)
            return orig.substring(0, index) + str + orig.substring(index, orig.length);
        else
            return str + orig;
    }
    if (isEmpty(formula))
        return null;
    var resultData;
    postJson('/data/formula.do', {id: form ? form.id : null, dataId: dataId, formula: formula,
        withNoIdData: withNoIdData, column: column}, function (result){
        resultData = result;
    }, null, true);
    return resultData;
    var target;
    try {
        //format dxf.function call
        target = formula.replace(/((dxf|@[a-zA-Z0-9]+)\.[^)]+\))/g, function (m, v) {
            if (v.indexOf('@') === 0)
                v = v.substr(1);
            v = 'dx.formula.' + v;
            var i = v.indexOf('(');
            var tmp = insert(v, i, '.call');
            i += 6;
            var j = i;
            for (; i < tmp.length - 1; i++)
                if ((tmp[i] !== ' ') && (tmp[i] != ')'))
                    return insert(tmp, j, 'form, ');
            return insert(tmp, j, 'form');
        });
        target = target.replace(/['"]?[\$]\{([^}]+)}['"]?/g, function (m, v) {
            //old 与 new 的解析。
            if (v.indexOf('old.') != -1) {
                var oldIndex = v.indexOf('old.');
                v = v.substr(0, oldIndex) + v.substr(oldIndex + 4, v.length);
                var oldVal = oldData[v];
                if (typeof oldVal === 'string')
                    return "'" + oldVal + "'";
                else
                    return oldVal;
            } else {
                var newIndex = v.indexOf('new.');
                if (newIndex != -1) {
                    v = v.substr(0, newIndex) + v.substr(newIndex + 4, v.length);
                }
                //if (!isEmpty(oldData)){
                //    if (typeof oldData[v] === 'string')
                //        return "'" + oldData[v] + "'";
                //    else
                //        return oldData[v];
                //}
                var val = evaluateVar(v, form, emptyVal);
                if (isStr)
                    return val;
                if (typeof val === 'string')
                    return "'" + val + "'";
                else
                    return val;
            }
        });
        var ret = eval(target);
        //ie 不会报错。而是返回空。
        if (ret === undefined){
            return target;
        }
        if (dx.debug)
            dxLog('evaluate', formula, target, ret);
        return ret;
    } catch (e) {
        if (dx.debug) {
            console.trace();
            dxError('evaluate failed:', formula, e.message);
        }
        //return emptyVal === undefined ? '' : emptyVal;
        return target;
    }
}

/**
 * evaluate object properties formulas
 *
 * @param obj
 * @param form
 */
function evaluateObject(obj, form) {
    var ret = {};
    forEach(obj, function (prop, formula) {
        ret[prop] = evaluate(formula, form);
    });
    return ret;
}
/**
 * 字段值改变后改变公式值。
 * 所有公式在后台一次性解析出来传到前台。
 * @param form
 * @param field
 * @param evalVal
 */
function evaluateFormByChangeValue(form, field, evalVal){
    form.fields.forEach(function (f) {
        if (f.id === field.id)
            return;
        var desc = getDesc(f);
        if (desc.formula && evalVal.hasOwnProperty(desc.column_name)){
            var value = evalVal[desc.column_name].value;
            if (!isEmpty(desc.ref_table_name)){
                f.ref_id = value;
                //关联表有值要清空。。否则不会去查名称列了。
                f.ref = null;
            }
            if (value !== null) {
                if (f.val)
                    f.val(value);
                else
                    f.value = value;
            }
        }
        if (desc.data_format)
            //f.resetDataFormat();
        if (desc.refTree && desc.refTree.root) {
            f.resetTree();
        }
        if ((desc.prefix && evalVal.hasOwnProperty(desc.column_name)) ||
                (desc.suffix && evalVal.hasOwnProperty(desc.column_name))){
            if (desc.prefix){
                f.get().prev().text(evalVal[desc.column_name].prefix);
            }
            if (desc.suffix){
                f.get().parent().find('.dx-suffix').text(evalVal[desc.column_name].suffix);
            }
        }
    })
}
/**
 * evaluate form fields when source field changed
 *
 * @param form
 * @param field source field
 * @param [allChanged] using to store all changed field
 * @param [embedded] it called embedded
 */
function evaluateForm(form, field, allChanged, embedded) {
    function columnContained(formula, column) {
        var isSqlFlag = false;
        var sqlId;
        if (formula.indexOf('dxf.sql(')!= -1){
            isSqlFlag = true;
            var regexp = /dxf.sql\(['"](.*)['"]\)/g;
            var match;
            do {
                match = regexp.exec(formula);
                if (!match)
                    break;
                sqlId = match[1];
            } while (true);
        }
        if (isSqlFlag){
            //return true;formatSql
            //return new RegExp('\\{' + column + '[\\.|\\}]', 'g').test(dx.sqlMap[sqlId].sql);
            return new RegExp('\\{' + column + '[\\.|\\}]', 'g').test(formatSql('dxf.sql("' + sqlId + '")'));
        }else{
            //return false;
            return new RegExp('\\{' + column + '[\\.|\\}]', 'g').test(formula);
        }
    }

    if (!embedded) { //第一次进来才初始化allChanged 其余都是递归传进来。
        allChanged = {};
        allChanged[field.id] = field;
    }
    var changed = [];
    form.fields.forEach(function (f) {
        //no need to calc self
        if (f.id === field.id)
            return;
        var desc = getDesc(f);
        if (desc.formula && columnContained(desc.formula, field.column)){
            var value = evaluate(desc.formula, form, desc.column_name);
            if (!isEmpty(desc.ref_table_name))
                f.ref_id = value;
            if (value !== null) {
                if (f.val)
                    f.val(value);
                else
                    f.value = value;
                changed.push(f);
            }
        }
        if (desc.data_format && columnContained(desc.data_format, field.column))
            f.resetDataFormat();
        if (desc.refTree && desc.refTree.root && columnContained(desc.refTree.root, field.column)) {
            f.resetTree();
            changed.push(f);
        }
        if ((desc.prefix && columnContained(desc.prefix, field.column))
            || (desc.suffix && columnContained(desc.suffix, field.column)))
            evaluatePrefixSuffix(f, form, desc);
    });
    $.each(changed, function (i, v) {   //循环拥有公式的列。没改一个递归改全部有公式的。
        evaluateForm(form, v, allChanged, true);
        allChanged[v.id] = v;
    });
    if (embedded) //除开第一次进来。之后都ruturn;
        return;
    // do something finally
    for (var key in allChanged)
        if (allChanged.hasOwnProperty(key)) {
            var f = allChanged[key];
            var desc = getDesc(f);
            if (!desc.auto_gen_children)
                continue;
            desc.auto_gen_children.forEach(function (child) {
                form.children.some(function (grid) {
                    if (grid.table === child) {
                        w(grid.id).autoGen(form);
                        return true;
                    }
                });
            });
        }
}

/**
 * evaluate prefix and/or suffix
 *
 * @param field
 * @param form
 * @param [desc]
 */
function evaluatePrefixSuffix(field, form, desc) {
    if (!desc)
        desc = getDesc(field);
    if (desc.prefix)
        field.get().prev().text(evaluate(desc.prefix, form));
    if (desc.suffix)
        field.get().parent().find('.dx-suffix').text(evaluate(desc.suffix, form));
}

/**
 * evaluate prefix and suffix
 *
 * @param form
 */
function evaluateFormPrefixSuffix(form) {
    if (!form.fieldIds)
        return;
    $.each(form.fieldIds, function (i, v) {
        var field = w(v);
        evaluatePrefixSuffix(field, form);
    });
}

/**
 * set default values if form action is create
 *
 * @param form
 * @param [setEmptyOnly] set default value when field is empty
 */
function setDefaultValues(form, setEmptyOnly) {
    var parentIdColumn = getTableDesc(form.tableName).parent_id_column;
    var childrenIdColumn = getTableDesc(form.tableName).children_id_column;
    form.fields.forEach(function (field) {
        var val = field.val();
        if (setEmptyOnly)
            if (typeof val === 'number') {
                if (val !== 0)
                    return;
            } else if (!isEmpty(val))
                return;
        var desc = getDesc(field);
        if (desc.default_value) {
            val = evaluate(desc.default_value, form, desc.column_name);
            if (!isEmpty(desc.ref_table_name))
                field.ref_id = val;
            field.val(val);
            //长文本 默认值
            if (desc.data_type == dx.dataType.textArea){
                field.get().parent().find('textarea[name="' + desc.column_name + '"]').text(val);
            }
            evaluateForm(form, field);
        }
        //事件颜色默认值
        if (desc.isCalendarEvent == 1 && !isEmpty(desc.calendarEventDefaultColor)){
            var eventColor = evaluate(desc.calendarEventDefaultColor, form);
            if (!isEmpty(eventColor)){
                form.get().find('span[field_id="' + field.id + '"]').css('background', eventColor);
                form.get().find('span[field_id="' + field.id + '"]').attr('event_color', eventColor);
                field.eventColor = function () {
                    return form.get().find('span[field_id="' + field.id + '"]').attr('event_color');
                }
            }
        }
        if (!isEmpty(parentIdColumn) && !isEmpty(form.parentRow) && desc.column_name == parentIdColumn){
            var parentValue = form.parentRow.columns[childrenIdColumn].value;
            field.ref_id = parentValue;
            field.val(parentValue);
        }
    });
}
function setRefValues(form){
    form.fields.forEach(function (field) {
        var val = field.val();
        if (!isEmpty(val)){
            var desc = getDesc(field);
            if (!isEmpty(desc.ref_table_name)){
                field.ref_id = val;
                if (isEmpty(field.ref)){
                    field.val(field.ref_id);
                }else{
                    var refNameExpression = field.ref.ref____name_Expression;
                    if (!isEmpty(refNameExpression))
                        field.val(refNameExpression);
                    else
                        field.val(field.ref_id);
                }
            }
        }
    });
}
function evaluateReadonlyByChangeValue(form, readonlyData){
    var table = getTableDesc(form.tableName);
    var parentIdColumn;
    if (!isEmpty(table))
        parentIdColumn = table.parent_id_column;
    var selector = form.p('roSelector');
    var fields;
    if (selector) {
        fields = selector();
        if (!fields)
            return;
    } else
        fields = form.get().find('.dx-field');
    var doClean = form.p('roEvaluated');
    if (!doClean)
        form.p('roEvaluated', true);
    var fixedData;
    // for detail form, some field may have fixed values specified by request parameter, and readonly
    if ((form.widgetName === 'detail') && form.fixedData)
        fixedData = form.fixedData;
    fields.each(function () {
        var $elem = $(this);
        if ($elem.hasClass('dx-selector-field'))
            return;
        var field = w(this.id);
        var desc = getDesc(field);
        var ro = false;
        if (desc)
            if (form.action == 'view')
                ro = true;
            else if (fixedData && (fixedData[desc.column_name] !== undefined))
                ro = true;
            else if (desc.virtual)
                ro = true;
            else if (form.readonlyColumns && (form.readonlyColumns.indexOf(desc.column_name) >= 0))
                ro = true;
            else if ((form.action == 'create' || form.action == 'batch') && desc.ro_insert)
                ro = true;
            else if (form.action == 'edit' && desc.ro_update)
                ro = true;
            else if ((form.action == 'create' || form.action == 'edit') && desc.column_name == parentIdColumn){
                ro = true;
            }
            else if (!desc.read_only_condition) {
                if (form.action === 'edit' && (contain(table.idColumns, desc.column_name)))
                    ro = true;
            } else if (readonlyData[desc.column_name] === true)
                ro = true;
        if (doClean && (desc.read_only_clear=='true') && field.get().is(':enabled') && ro){
            field.val('');
            if (desc.data_type == 14){
                $elem.parent().find('.dx-text-area-textarea').text('');
            }
        }
        //手机端动态隐藏字段
        if(!isEmpty(desc.default_value) || !isEmpty(desc.formula)){
            $elem.addClass("private-field");
        }
        if (desc.data_type == 14){
            if (ro)
                $elem.parent().find('.dx-text-area-textarea').attr('readonly', 'readonly');
            else
                $elem.parent().find('.dx-text-area-textarea').removeAttr('readonly');
        }else{
            field.enable(!ro);
            // set complex field related field disabled when updating
            complexReadonlyEval(desc, form);
        }
    })
}
/**
 * evaluate fields readonly condition, and set/unset readonly
 *
 * @param form
 */
function evaluateReadonly(form) {
    var table = getTableDesc(form.tableName);
    var parentIdColumn;
    if (!isEmpty(table))
        parentIdColumn = table.parent_id_column;
    var selector = form.p('roSelector');
    var fields;
    if (selector) {
        fields = selector();
        if (!fields)
            return;
    } else
        fields = form.get().find('.dx-field');
    var doClean = form.p('roEvaluated');
    if (!doClean)
        form.p('roEvaluated', true);
    var fixedData;
    // for detail form, some field may have fixed values specified by request parameter, and readonly
    if ((form.widgetName === 'detail') && form.fixedData)
        fixedData = form.fixedData;
    fields.each(function () {
        var $elem = $(this);
        if ($elem.hasClass('dx-selector-field'))
            return;
        var field = w(this.id);
        var desc = getDesc(field);
        var ro = false;
        if (desc)
            if (form.action == 'view')
                ro = true;
            else if (fixedData && (fixedData[desc.column_name] !== undefined))
                ro = true;
            else if (desc.virtual)
                ro = true;
            else if (form.readonlyColumns && (form.readonlyColumns.indexOf(desc.column_name) >= 0))
                ro = true;
            else if ((form.action == 'create' || form.action == 'batch') && desc.ro_insert)
                ro = true;
            else if (form.action == 'edit' && desc.ro_update)
                ro = true;
            else if ((form.action == 'create' || form.action == 'edit') && desc.column_name == parentIdColumn){
                ro = true;
            }
            else if (!desc.read_only_condition) {
                if (form.action === 'edit' && (contain(table.idColumns, desc.column_name)))
                    ro = true;
            } else if (evaluate(desc.read_only_condition, form) === true)
                ro = true;
        	if (doClean && (desc.read_only_clear=='true') && field.get().is(':enabled') && ro){
                field.val('');
                if (desc.data_type == 14){
                    $elem.parent().find('.dx-text-area-textarea').text('');
                }
            }
    	//手机端动态隐藏字段
        if(!isEmpty(desc.default_value) || !isEmpty(desc.formula)){
        	$elem.addClass("private-field");
        }
        if (desc.data_type == 14){
            if (ro)
                $elem.parent().find('.dx-text-area-textarea').attr('readonly', 'readonly');
            else
                $elem.parent().find('.dx-text-area-textarea').removeAttr('readonly');
        }else{
            field.enable(!ro);
            // set complex field related field disabled when updating
            complexReadonlyEval(desc, form);
        }
    })
}

function blockEditColumn(form){
    var table = getTableDesc(form.tableName);
    var currentApproveBlock;
    var nodes = form.approveFlow.approveFlowNodes;
    if (isEmpty(nodes))
        return;
    for (var i=0; i<nodes.length; i++){
        if (nodes[i].state == 'wait'){
            currentApproveBlock = nodes[i];
            break;
        }
    }
    if (isEmpty(currentApproveBlock))
        return;
    if (isEmpty(dx.flowBlocks) || isEmpty(dx.flowBlocks[form.tableName])
            || isEmpty(dx.flowBlocks[form.tableName][currentApproveBlock.block_id]))
        return;
    var flowBlockEditColumns = dx.flowBlocks[form.tableName][currentApproveBlock.block_id].flowBlockEditColumns;
    var editColumnsMap = {};
    if (isEmpty(flowBlockEditColumns))
        return;
    for (var i=0; i<flowBlockEditColumns.length; i++){
        editColumnsMap[flowBlockEditColumns[i].column] = table.columnMap[flowBlockEditColumns[i].column];
    }
    var fields = form.get().find('.dx-field');
    fields.each(function () {
        var $elem = $(this);
        if ($elem.hasClass('dx-selector-field'))
            return;
        var field = w(this.id);
        var desc = getDesc(field);
        var ro = false;
        if (!isEmpty(editColumnsMap[desc.column_name])) {
            ro = true;
            //审批流的节点可编辑字段。可编辑字段加样式区分。
            $elem.css({'border-color': 'red'});
        }
        field.enable(ro);
    });
    form.approveEdit = true;
}
/**
 * bind field data to $node/$('#data.id'), or get field data bind to $('#data')
 *
 * @param data
 * @param [$node]
 * @returns {*|jQuery}
 */
function w(data, $node) {
    function get() {
        return $item;
    }

    function modify() {
        $item.trigger('modify');
    }

    function updated(data) {
        if (this.onUpdated)
            this.onUpdated(data);
        else
            $item.trigger('dx.updated', data);
    }

    /**
     * get/set private data from/to object
     * @param name
     * @param [val]
     */
    function p(name, val) {
        if (val === undefined)
            return this._private ? this._private[name] : undefined;
        else {
            if (this._private === undefined)
                this._private = {};
            this._private[name] = val;
            return val;
        }
    }

    var $item;
    if (typeof data === "string") {
        $item = $('#' + data);
        if (!$item.data('widget'))
            return null;
        return $item.data('widget');
    } else {
        $item = $node || $('#' + data.id);
        if ($item.length === 0)
            return false;
        $item.data('widget', data);
        if (!data.get)
            data.get = get;
        if (!data.modify)
            data.modify = modify;
        if (!data.updated)
            data.updated = updated;
        if (!data.p)
            data.p = p;
        if (!$item.is('input'))
            return true;

        // init dx-field only method
        var field = data;
        field.val = function (v) {
            if (v === undefined)
                return $item.val();
            else {
                $item.val(v);
            }
        };
        field.text = function () {
            return $item.val();
        };
        field.enable = function (enabled) {
            enableItem($item, enabled);
            if (enabled){
            	$item.removeClass('dx-readonly');
            	//手机端动态隐藏字段
            	if(dx.user.isMobileLogin==1){
            		if(!$item.parents(".dx-field-container").hasClass("dx-hidden")){
                		$item.parents(".dx-field-container").css("display","block");
                	}
            	}
            	
            }else{
            	$item.addClass('dx-readonly');
            	
            	//手机端动态隐藏字段
            	if(dx.user.isMobileLogin==1){
            		var form = null; //w($item[0].form.id);
                    if (typeof $item[0].form.id == 'string'){
                        form = w($item[0].form.id);
                    }
            		if(form instanceof Object){
            			if(form.action!="view" && !$item.hasClass("private-field")){
                    		$item.parents(".dx-field-container").css("display","none");
                    	}
            		}
                	
            	}
            }
        };
        return true;
    }
}

/**
 * build new created tab's cache
 *
 * @param form
 * @param widgets
 * @param replace true if replace origin form, so do not increase reference count
 */
function buildFormCache(form, widgets, replace) {
    function addMenu(menu) {
        var $menu = $('#menu-bar-' + this.id);
        if ($menu.length != 1) {
            if (dx.debug) dxError('can not add menu to "' + this.id + '"');
            return;
        }
        if (!menu.sub) {
            $menu.append('<button type="button" class="btn btn-default btn-sm" id="' + menu.id + '">' + msg(menu.title) + '</button>');
            return;
        }
        var $div = $(
            '<div class="btn btn-default btn-sm dropdown dx-table-report-menu">' +
            '<a href="#" id="' + menu.id + '" role="button" class="dropdown-toggle" data-toggle="dropdown">' + msg(menu.title) +
            '<b class="caret"></b>' +
            '</a></div>');
        var $ul = $('<ul class="dropdown-menu dropdown-menu-left" role="menu" aria-labelledby="' + menu.id + '"/>');
        $div.append($ul);
        menu.sub.forEach(function (menu) {
            $ul.append(
                '<li role="presentation"><a ' +
                (menu.name ? 'name="' + menu.name + '" ' : '') +
                (menu.id ? ('id="' + menu.id + '" ') : '') +
                'class="' + menu.clazz + '" role="menuitem" tabindex="-1" href="#">' +
                msg(menu.title) +
                '</a></li>');
        });
        $menu.append($div);
    }

    var reset = function () {
        var $form = this.get();
        $form.get(0).reset();
        $form.find('input.dx-upload-label').val('');
        setDefaultValues(this);
    };

    var enableMenu = function (enable) {
        var $form = this.get();
        if (enable)
            $form.find('div.dx-menu-bar').show();
        else
            $form.find('div.dx-menu-bar').hide();
    };

    w(form);
    form.p('reference', 0);
    form.addMenu = addMenu;
    form.reset = reset;
    form.enableMenu = enableMenu;
    // maybe form is a record, check it
    if (form.widgetType === 'form' && form.parent) {
        var parentFormId = findFormId(form.parent);
        var parentForm = getFormModel(parentFormId);
        // index form has no model, and no need to set it
        if (parentForm) {
            form.p('parent', parentForm);
            if (replace !== true)
                parentForm.p('reference', parentForm.p('reference') + 1);
        }
    }
    //cache field's column-->id
    var initColumns = (form.columns === undefined);
    if (initColumns)
        form.columns = {};
    var initFields = !form.fields;
    if (initFields)
        form.fields = [];
    form.fieldIds = [];
    if (widgets) {
        $.each(widgets, function (i, widget) {
            if (!w(widget))
                return;

            var $item = $('#' + widget.id);
            if (!$item.hasClass('dx-field'))
                return;

            var field = widget;
            // don't cache selector fields
            if ($item.hasClass('dx-selector-field'))
                return;
            // build cache
            if (initColumns)
                form.columns[field.column] = field;
            form.fieldIds.push(field.id);
            if (initFields)
                form.fields.push(field);
        });
        form.getField = getFormField;
    }
}

/**
 * build record model cache map
 *
 * @param record record model
 * @param [fieldCallback] callback for fields
 */
function buildRecordCache(record, fieldCallback) {
    w(record);
    record.columns = {};
    var cb = fieldCallback && (typeof fieldCallback === 'function') ? fieldCallback : null;
    // build record cache and set columns values
    record.fields.forEach(function (field) {
        w(field);
        if (cb) cb(field);
        record.columns[field.column] = field;
    });
    record.getField = getFormField;
}

function getFormField(id) {
    return this.columns[id];
}
/**
 * get input field desc
 *
 * @param field
 * @returns {*}
 */
function getDesc(field) {
    if (isEmpty(dx.table[field.table])){
        return 1;
    }
    return dx.table[field.table].columnMap[field.column];
}

/**
 * get input field desc
 *
 * @param table
 * @param column
 */
function getColumnDesc(table, column) {
    return dx.table[table].columnMap[column];
}

/**
 * get desc display name, i18n
 *
 * @param desc
 * @returns {*}
 */
function getColumnText(desc) {
    return i18n(desc.i18n);
}

/**
 * get report label
 * @param id
 */
function getReportLabel(id) {
    var label = i18n(dx.i18n.message[id]);
    if (isEmpty(label))
        return i18n(dx.i18n.message[id.toLowerCase()]);
    return label;
}
/**
 * get operation desc
 * @param oid operation id
 */
function getOp(oid) {
    return dx.operations[oid];
}

/**
 * get table shortcut desc
 * @param sid shortcut id
 */
function getShortcut(sid) {
    return dx.shortcuts[sid];
}

/**
 * get batch desc
 * @param bid
 */
function getBatch(bid) {
    return dx.batches[bid];
}

/**
 * get complex column describe
 *
 * @param id
 * @returns {*}
 */
function getComplexDesc(id) {
    return dx.complexColumns[id];
}

/**
 * get i18n text
 *
 * @param desc
 * @returns {*}
 */
function i18n(desc) {
    if (desc == null)
        return "";
    var ret = desc[dx.user.language_id];
    if (isEmpty(ret))
        ret = desc[dx.sys.default_language];
    return ret;
}

/**
 * get input field by field id
 *
 * @param id
 * @returns {*}
 */
function getDescById(id) {
    var field = w(id);
    return getDesc(field);
}

/**
 /**
 * get form model from cache by form id
 * @param id
 * @returns {*}
 */
function getFormModel(id) {
    return dx.cache.form[id];
}

/**
 * get obj size, eg. objectSize({a:1, b:2}) == 2
 * @param obj
 * @returns {Number}
 */
function objectSize(obj) {
    return Object.keys(obj).length;
}

/**
 * get i18n message
 * @param str message code
 * @param param message parameters
 */
function msg(str, param) {
    if (!dx.i18n.message)
        return str;

    var ret;
    if (isEmpty(dx.i18n.message[str.toLowerCase()])){
        ret = dx.i18n.message[str];
    }else{
        ret = dx.i18n.message[str.toLowerCase()];
    }
    if (ret === undefined) {
//		messageBox('not defined message:' + str);
        return str;
    }

    var message = i18n(ret);
    var args;
    if ($.isArray(param))
        args = param;
    else {
        args = [];
        for (var i = 1; i < arguments.length; i++)
            args.push(arguments[i]);
    }
    return message.replace(/\$\{(\d+)}/g, function (m, i) {
        return args[Number(i)];
    });
}

/**
 * if the column is a system group column
 * @param desc
 * @returns {boolean}
 */
function isSystemColumn(desc) {
    return desc.group_name === dx.sysColumnGroupName;
}

/**
 * get table describe from cache by table id
 * @param id
 * @returns {*}
 */
function getTableDesc(id) {
    return dx.table[id];
}

/**
 * get menu describe from cache by table id
 * @param id
 * @returns {*}
 */
function getPageDesc(id) {
    return dx.pages[id];
}

/**
 * get jquery ui tabs li by element id
 *
 * @param id
 * @returns {*}
 */
function findFormLi(id) {
    var panelId = $('#' + id).closest('.dx-form').closest('div.tab-pane').attr('id');
    // return dx.mainNav.find('li[aria-controls="' + panelId + '"]'); //原来的
    return $(".dx-main-tabs>.nav-tabs.index-tab-navs,.dx-main-tabs .home-navs-rest-drop").find('li[aria-controls="' + panelId + '"]')
}

/**
 * get form id by element id
 *
 * @param id
 * @returns {*|jQuery}
 */
function findFormId(id) {
    return $('#' + id).closest('.dx-form').attr('id');
}

/**
 * set container en/disable
 *
 * @param $container
 * @param disabled
 * @param [selector] field selector string
 */
function containerDisabled($container, disabled, selector) {
    if (selector === undefined)
        selector = '.dx-field';
    var $fields = $container.find(selector).prop('disabled', disabled);
    if (disabled)
        $fields.addClass('dx-readonly');
    else
        $fields.removeClass('dx-readonly');
}

/**
 * convert object to JSON string
 *
 * @param obj
 * @returns string
 */
function toJSON(obj) {
    return JSON.stringify(obj);
}

/**
 * log data to console, or alert for IEs
 *
 * @param log
 */
function dxLog(log) {
    var args = [];
    for (var i = 0; i < arguments.length; i++)
        if (typeof arguments[i] === 'string')
            args.push(arguments[i]);
        else
            args.push(toJSON(arguments[i]));
    console.log(args);
}

/**
 * using for profile
 *
 * @param tag log tag
 * @param init clear old timer
 * @returns {number}
 */
function deltaTime(tag, init) {
    var nt = new Date().getTime();
    if (init || dx.timer === undefined)
        dx.timer = nt;
    console.log(tag, nt - dx.timer);
    dx.timer = nt;
}

/**
 * log data to console, or alert for IEs
 *
 * @param log
 */
function dxError(log) {
    var args = [];
    for (var i = 0; i < arguments.length; i++)
        if (typeof arguments[i] === 'string')
            args.push(arguments[i]);
        else
            args.push(toJSON(arguments[i]));
    console.error(args);
}

/**
 * show progress dialog
 *
 * @param title
 * @param message
 */
function progressBox(title, message) {
    if (!dx.pb)
        dx.pb = new BootstrapDialog(
            {
                type: BootstrapDialog.TYPE_INFO,
                closable: false,
                autodestroy: false
            }
        );
    dx.pb.setTitle(msg(title))
        .setMessage(msg(message)).open();
}

function hidePorgressBox() {
    dx.pb.close();
}

/**
 * popup a error message box
 *
 * @param message
 * @param [param]
 * @param [callback]
 */
function messageBox(message, param, callback) {
    if (typeof param == "function") {
        callback = param;
        param = undefined;
    }
    if (typeof message === 'string')
        alert(msg(message, param));
    else// exception occur
        alert(msg(message.errorMessage));
    if (callback)
        callback();
}

/**
 * popup a data operation result dialog
 * @param result
 * @param callback
 */
function resultDetailBox(result, callback) {
    dx.result.$message.text(result.message);
    dx.result.$details.hide();
    dx.result.$details.empty();
    $.each(result.details, function (i, log) {
        dx.result.$details.append('<li>' + log + '</li>')
    });

    BootstrapDialog.show({
        title: msg('Result'),
        message: dx.result.dialog,
        autodestroy: false,
        type: result.success ? BootstrapDialog.TYPE_SUCCESS : BootstrapDialog.TYPE_DANGER,
        buttons: [
            {
                label: msg('View_Detail'),
                action: function () {
                    $('#dxResultDetails').show();
                }
            }
        ],
        onhidden: function () {
            if (callback)
                callback();
        }
    });

}

/**
 * popup a confirm box, and call callback if confirmed
 *
 * @param message
 * @param [param] message parameters
 * @param ok
 * @param [cancel]
 */
function confirmBox(message, param, ok, cancel) {
    if (typeof param == 'function') {
        cancel = ok;
        ok = param;
        param = undefined;
    }
    if (confirm(msg(message, param)))
        ok();
    else if (cancel)
        cancel();
}

/**
 * redirect to login page
 */
function redirectLogin() {
    messageBox('NoLogin', function () {
    	var url=dx.user.isMobileLogin==1?'/auth/mobile.view':'/auth/login.view';
        window.location.href = makeUrl(url); 
    });
}

/**
 * popup a sessoin timeout dialog, and reload when dismiss
 */
function sessionTimeout() {
    messageBox('session_timeout', function () {
        if(dx.user.isMobileLogin==0)
        	location.reload();
    });
}

function invalidSession() {
    messageBox('session_timeout', function () {
    	var url=dx.user.isMobileLogin==1?'/auth/mobile.view':'/auth/login.view';
    	window.onbeforeunload = undefined;
        window.location.href = makeUrl(url);
        
    });
}
/**
 * enabled/disable html tag
 * @param $item jQuery object
 * @param enabled
 */
function enableItem($item, enabled) {
    $item.prop('disabled', !enabled);
}

/**
 * format number text
 * @param x
 * @param [dotSize] length for float after dot, default 2
 * @returns {string}
 */
function formatNumber(x, dotSize) {
    if (typeof x != 'number')
        return '';
    if (dotSize === undefined)
        dotSize = 0;
    else if (dotSize === true)
        dotSize = 2;
    var sign = '';
    if (x < 0) {
        x = -x;
        sign = '-'
    }
    x = +(Math.round(x + "e+" + dotSize) + "e-" + dotSize);
    var parts = (sign + x.toString()).split('.');
    var ret = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    if (dotSize == 0)
        return ret;
    if (parts[1] === undefined)
        parts[1] = '';
    var padSize = dotSize - parts[1].length;
    if (padSize > 0)
        parts[1] = parts[1] + new Array(padSize + 1).join('0');
    return ret + '.' + parts[1];
}

function unformatNumber(x) {
    return x.replace(/,/g, '');
}

/**
 * wrap for post json, and callback the cb with raw result
 *
 * @param url post url address
 * @param param post parameters
 * @param callback callback when post completed
 */
function postJsonRaw(url, param, callback) {
    function closeProcess() {
        if (dx.ajax == 0 && dx.processing.isOpen())
            //setTimeout(function () {
                if (dx.ajax == 0)
                    dx.processing.close();
            //}, 500);
        if (!isOpenPage)
            dx.processing.close();
    }

    dx.ajax++;
    // makeUrl() will add app context path before url, it's generated at velocity template
    $.ajax(makeUrl(url), {
        type: param.type ? param.type : 'post',
        contentType: param.contentType ? param.contentType : 'application/json; charset=utf-8',
        dataType: param.dataType ? param.dataType : 'json',
        data: toJSON(param.data),
        async: !param.sync,
        statusCode: {
            401: redirectLogin
        },
        success: function (result, status, xhr) {
            var dataType = param.dataType;
            if (!isEmpty(result) && dataType != 'html'){
                //result = replaceParam(result);
                var jsonResult = toJSON(result).replace(/</g, '&lt;');
                jsonResult = jsonResult.replace(/>/g, '&gt;');

                result = JSON.parse(jsonResult);
            }
            if (callback)
                callback(result, status, xhr);
            closeProcess();
        },
        error: function (xhr, ignore2, message) {
            var aaa = url;
            dx.processing.close();
            if (xhr.status === 401)
                return;
            if (xhr.status === 404)
                redirectLogin();
        }
    });
}

function replaceParam(data){
    if (isEmpty(data))
        return;
    if (typeof data == 'object'){
        for (var key in data){
            data[key] = replaceParam(data[key]);
        }
    }else if (typeof data == 'array'){
        for (var i=0; i<data.length; i++){
            data[i] = replaceParam(data[i])
        }
    }else if (typeof data == 'string'){
        //data = data.replace(/&/g, '&amp;');
        data = data.replace(/"/g, '&quot;');
        data = data.replace(/'/g, '&#39;');
        data = data.replace(/</g, '&lt;');
        data = data.replace(/>/g, '&gt;');
        return data;
    }
    return data;
}

function checkJsonResult(result, success, fail) {
    if (!result || !$.isPlainObject(result)) {
        messageBox('unknownError');
        return;
    }
    if (result.accessDenied) {
        messageBox(result.errorMessage);
        return;
    }
    if (result.sessionTimeout) {
        sessionTimeout();
        return;
    }
    if (result.invalidSession) {
        invalidSession();
        return;
    }
    // action is a complex operation, with detail messages
    if (result.details && !result.noPopup) {
        if (result.success) {
            dxToastAlert(result.message);
            success(result.data);
        } else {
            resultDetailBox(result, function () {
                dx.processing.close();
                if (result.success)
                    success(result.data);
                else if (fail)
                    fail(result.data);
            });
        }
        return;
    }

    if (result.success) {
        if (success)
            success(result.data);
    } else {
        if (!fail)
            dx.processing.close();
        var errorMessage = result.errorMessage;
        errorMessage = errorMessage.replace(/&lt;/g, '<');
        errorMessage = errorMessage.replace(/&gt;/g, '>');
        errorMessage = errorMessage.replace(/&#40;/g, '(');
        errorMessage = errorMessage.replace(/&#41;/g, ')');
        messageBox(errorMessage, function () {
            if (fail)
                fail(result.data);
        });
    }
}
/**
 * post json request, and execute callbacks
 * using post result.success to check
 *
 * @param url post url address
 * @param data post data, put {} if none
 * @param success callback when post successes
 * @param [fail] call when post failed
 * @param [sync] function will not return until post processed
 */
function postJson(url, data, success, fail, sync) {
    var param = {};
    if (typeof data === 'function') {
        param.sync = fail;
        fail = success;
        success = data;
        param.data = {};
    } else {
        param.sync = sync;
        if (typeof data === "undefined")
            param.data = {};
        else
            param.data = data;
    }
    postJsonRaw(url, param, function (result) {
        checkJsonResult(result, success, fail);
    });
}

/**
 * post json data, and expect page data to return
 * @param url post url address
 * @param data post data, put {} if none
 * @param success callback when post successes
 * @param [fail] callback when post failed
 */
var formCacheTag1 = '<!--cache form:';
var formCacheTag2 = '-->';
function postPage(url, data, success, fail, isOutPage,sync) {
    postJsonRaw(url, {data: data, dataType: 'html',sync:sync}, function (result, status, xhr) {
        // check session timeout when remember-me is on
        var responseHeader = xhr.getResponseHeader('Content-Type');
        /*if (responseHeader == null) {
         sessionTimeout();
         return;
         } else*/
        if (responseHeader.indexOf('json') >= 0) {
            var json = JSON.parse(result);
            if (json.invalidSession) {
                invalidSession();
                return;
            }
            if (json.sessionTimeout) {
                sessionTimeout();
                return;
            }
            messageBox(json);
            if (fail)
                fail(json);
            dx.processing.close();
            return;
        }
        // hack the result for the new form id, and retrieve it if any
        var ct1 = result.indexOf(formCacheTag1);
        if (ct1 >= 0) {
            var id1 = ct1 + formCacheTag1.length;
            var ct2 = result.indexOf(formCacheTag2, id1);
            var id = result.substring(id1, ct2);
            postJson('/vm/form/' + id + '.do', function (result) {
                dx.cache.form[id] = result;
            }, null, true);
        }
        success(result);
    });
}

/**
 * send a download request using data
 *
 * @param url
 * @param data
 * @param [success]
 * @param [fail]
 */
function fileDownload(url, data, success, fail) {
    //post and get download link
    postJson(url, data,
        function (data) {
            if (success && (success(data) === false))
                return;
            $("body").append("<iframe src='" + makeUrl(data) + "' style='display: none;' ></iframe>");
        }, function (data) {
            if (fail)
                fail(data);
        })
}

function findTab(id, noSwitch) {
    var $li = $('li[aria-owns="' + id + '"]');
    if ($li.length == 1) {
        if (!noSwitch) {
            $li.find('a').tab('show');
            return true;
        }
    }
    return false;
}
/**
 * open menu url
 *
 * @param id menu id
 */
var isOpenPage = false;
function openPage(id, tabIcon) {
    if (findTab(id)){
        $(".index-tab-navs li,.home-tab-navs-rest li").removeClass("active");
        $('li[aria-owns="' + id + '"]').addClass("active");
        return;
    }
    var page = getPageDesc(id);
    if (!page.url)
        return;
    var data;
    if (page.param)
        data = evaluate('(' + page.param + ')', null);
    else
        data = {};
    if (data != undefined)
        data.tabIcon = tabIcon;

    isOpenPage = true;
    if (page.url == '/list/table.view')
        dx.processing.open();
    newTab(page.url, data, id);
}

function changeMenu(menuName) {
    var $moduleMenus = $('.dx-main-menu .dx-user-module-menu');
    for (var i = 0; i < $moduleMenus.length; i++) {
        if ($($moduleMenus[i]).attr('name') != menuName) {
            $($moduleMenus[i]).hide();
        } else if ($($moduleMenus[i]).attr('name') == menuName) {
            $($moduleMenus[i]).show();
        }
    }
    var $moduleMenus_side = $('#side-menu-bar .dx-user-module-menu');
    for (var i = 0; i < $moduleMenus_side.length; i++) {
        if ($($moduleMenus_side[i]).attr('name') != menuName) {
            $($moduleMenus_side[i]).hide();
        } else if ($($moduleMenus_side[i]).attr('name') == menuName) {
            $($moduleMenus_side[i]).show();
        }
    }
}

/**
 * open table shortcut in a new tab
 *
 * @param shortcut
 * @param form
 */
function openTableShortcut(shortcut, form) {
    var data;
    if (shortcut.param)
        data = evaluate('(' + shortcut.param + ')', form);
    else
        data = {};

    data.parent = form.id;
    newTab(shortcut.url, data, form.id);
}

/**
 * init main menu tags in container
 *
 * @param $container will only init menus in container
 */
function initMainMenu($container) {
    $container.find('a.current').next().show();
    $container.find('a.nav-top-item').on('click', function () {
        var $this = $(this);
        if ($this.hasClass('current'))
            $this.removeClass('current');
        else
            $this.addClass('current').parent().siblings().find('a').removeClass('current');
        $this.parent().siblings().find('ul').slideUp("normal", function () {
            $(".dx-main-menu-entry").getNiceScroll().resize();
        });
        $this.next().slideToggle("normal", function () {
            $(".dx-main-menu-entry").getNiceScroll().resize();
        });

        return false;
    });
}

/**
 * get table's name column, if no name column defined, using last id column
 * @param table
 * @returns {*}
 */
function getTableNameColumn(table) {
    return table.name_column ? table.name_column : table.idColumns[table.idColumns.length - 1];
}

/**
 * get table's last id column
 * @param table
 * @returns {*}
 */
function getTableIdColumn(table) {
    if (isEmpty(table.idColumns))
        return null;
    return table.idColumns[table.idColumns.length - 1];
}

function execCodeSql(form, sql, list, opts){
    if (opts === undefined)
        opts = {};
    var async = opts.callback ? true : false;
    var ret = undefined;
    var param = buildCodeSQLParam(form, sql);
    postJson('/data/sql.do', {
        sql: sql,
        formId: form ? form.id : null,
        param: param,
        list: list,
        map: opts.map
    }, function (data) {
        ret = data;
        if (async)
            opts.callback(ret);
    }, null, !async);
    if (!async)
        return ret;
}
function buildCodeSQLParam(form, sql, mapping){
    var ret = {};
    var names = dx.sqlMap[sql].param;
    names.forEach(function (name) {
        try {
            if (name.indexOf('__') === 0)
                ret[name] = eval(name.substr(2));
            else if (form.var)
                ret[name] = form.var(name, mapping);
            else if (!mapping)
                ret[name] = evaluateVar(name, form);
            else
                ret[name] = evaluate(mapping[name], form);
        } catch (e) {
            if (dx.debug) {
                console.trace();
                dxError('evaluate failed:', name, e.message);
            }
        }
    });

    return ret;
}
/**
 * build sql statment's parameter map
 *
 * @param form
 * @param sql
 * @param [mapping] sql parameter ==> form variable, if not defined, they are same
 * @returns {{}}
 */
function buildSQLParam(form, sql, mapping) {
    var ret = {};
    var names = getParamList(sql);
    names.forEach(function (name) {
        try {
            if (name.indexOf('__') === 0)
                ret[name] = eval(name.substr(2));
            else if (form.var)
                ret[name] = form.var(name, mapping);
            else if (!mapping)
                ret[name] = evaluateVar(name, form);
            else
                ret[name] = evaluate(mapping[name], form);
        } catch (e) {
            if (dx.debug) {
                console.trace();
                dxError('evaluate failed:', name, e.message);
            }
        }
    });

    return ret;
}

/**
 * exec sql
 * @param form
 * @param sql
 * @param [list] return data is array
 * @param [opts]
 *              callback: without callback, the post will be sync
 *                map: result return as a map, or just a single value/list, default=false
 */
function execSql(form, sql, list, opts) {
    if (opts === undefined)
        opts = {};
    var async = opts.callback ? true : false;
    var ret = undefined;
    var param = buildSQLParam(form, sql);
    postJson('/data/sql.do', {
        sql: sql,
        formId: form ? form.id : null,
        param: param,
        list: list,
        map: opts.map
    }, function (data) {
        ret = data;
        if (async)
            opts.callback(ret);
    }, null, !async);
    if (!async)
        return ret;
}
/**
 * get all parameters for sql string
 *
 * @param sql
 * @returns {Array}
 */
function getParamList(sql) {
    var regexp = /[#\$]\{([^}]+)}/g;
    var match;
    var names = [];
    do {
        match = regexp.exec(sql);
        if (!match)
            break;
        names.push(match[1]);
    } while (true);
    return names;
}


/**
 * init main tab control in container
 * the main tab has class 'dx-main-tabs'
 *
 * @param $container
 */
function initMainTab($container) {
    var $div = $container.find('div.dx-main-tabs');
    dx.mainTab = $div.children('.tab-content');
    dx.mainContent = $div.children('.dx-main-tab-content');
    dx.mainNav = $div.children('.nav-tabs');
    dx.mainNav.on('click', 'span.closeTab', function (event) {
        event.preventDefault();
        var li = $(this).closest('li');
        var panelId = li.find('a').attr('href');
        var formId = $(panelId).find('.dx-form').attr('id');
        var form = getFormModel(formId);
        if (form.action === 'edit')
            form.p('confirm', true);
        closeTab(li);

        var $li = $(".index-tab-navs>li");
        var totalWidth = 0;
        for (var i = 0; i < $li.length; i++) {
            totalWidth = totalWidth + $li.eq(i).width() + 8;
        }
        if ($(".home-tab-navs-rest li").length) {
            if (dx.mainNav.width() - 52 - totalWidth > 120) {
                dx.mainNav.append($(".home-tab-navs-rest li:first-of-type"));

                if ($(".home-tab-navs-rest li").length) {
                    $(".home-navs-rest-drop").css("display", "block");
                } else {
                    $(".home-navs-rest-drop").css("display", "none");
                }
            }
            $(".home-tab-navs-rest li").removeClass("active");
        } else {
            $(".home-navs-rest-drop").css("display", "none");
        }
    });
    $(".home-tab-navs-rest").on('click', 'span.closeTab', function (event) {
        event.preventDefault();
        var li = $(this).closest('li');
        var panelId = li.find('a').attr('href');
        var formId = $(panelId).find('.dx-form').attr('id');
        var form = getFormModel(formId);
        if (form.action === 'edit')
            form.p('confirm', true);
        closeTab(li);

        if($(".home-tab-navs-rest li").length==1){
            $(".home-navs-rest-drop").css("display", "none");
            activeTab(dx.mainNav.find("li").length-1);
            return false;
        }
    });
}

/**
 * switch to tab with index
 *
 * @param index
 */
function activeTab(index, rest) {
    // dx.mainNav.find('li:eq(' + index + ') a').tab('show')  //原来的

    if (rest == "rest") {
        $(".dx-main-tabs .home-tab-navs-rest li:eq(" + index +") a").tab('show');
    } else {
        dx.mainNav.find('li:eq(' + index + ') a').tab('show');
    }

}

/**
 *close tab, but first we need clear cache, both server and client
 *
 * @param $li target tab
 */
function closeTab($li) {
    var panelId = $li.find('a').attr('href');
    var formId = $(panelId).find('.dx-form').attr('id');
    closeForm(formId, function () {
        var form = getFormModel(formId);
        var parentId = form.parent;
        if (parentId) {
            var parentForm = form.p('parent');
            var $parentLi = findFormLi(parentForm.id);
            // activeTab($parentLi.index());   //原来的
            var $parentUl=$parentLi.parents("ul");
            if($parentUl.hasClass("home-tab-navs-rest")){
                activeTab($parentUl.find($parentLi).index(),"rest");
            }else{
                activeTab($parentUl.find($parentLi).index());
            }

            var data = form.p('updated');
            if (data && w(parentId))
                w(parentId).updated(data);
        } else{
            // activeTab($li.index() - 1);  //原来的
            if($li.parents("ul").hasClass("home-tab-navs-rest")){
                activeTab($li.parents("ul").find($li).index() - 1,"rest");
            }else{
                activeTab($li.parents("ul").find($li).index() - 1);
            }
        }

        $li.remove();
        $(panelId).remove();
    });

    var name = $(panelId).find('.dx-form').attr('name');
    if (name == 'message') {
        dx.isOpenMessage = 1;
    }
}

/**
 * convert field value to display text, for grid display
 *
 * @param field
 * @param [form]
 * @param [noLink] column with link to <A> tag
 */
var TEXT_DATA_FORMAT_LENGTH_MAP = {
    Y: 4,
    M: 2,
    D: 2,
    h: 2,
    m: 2,
    s: 2
};

function fieldText(field, form, noLink) {
    var desc = getDesc(field);
    var ret = '';
    switch (desc.data_type) {
        case dx.dataType.string:
            if (desc.dic_id) {
                var dict = dx.dict[desc.dic_id];
                if (!dict || !dict[field.value])
                    ret = field.value;
                else
                    ret = i18n(dict[field.value]);
                break;
            } else if (desc.ref_table_name) {
                if (field.ref) {
                    ret = field.ref.ref____name_Expression;
                } else
                    ret = field.value;
                break;
            } else if (desc.viewStyle && desc.viewStyle.map && field.value) {
                ret = mapInputText(field);
                break;
            }

            if (desc.format) {
                if (field.value !== null)
                    switch (desc.format.type) {
                        case 'date':
                            var format = desc.format.format.replace('YM', 'Y/M').replace('MD', 'M/D').replace('Dh', 'D h').replace('hm', 'h:m').replace('ms', 'm:s');
                            var pos = 0;
                            for (var i = 0; i < format.length; i++) {
                                var len = TEXT_DATA_FORMAT_LENGTH_MAP[format[i]];
                                if (len === undefined)
                                    ret += format[i];
                                else {
                                    ret += field.value.substr(pos, len);
                                    pos += len;
                                }
                            }
                        default:
                    }
                break;
            }
        case dx.dataType.link:
        case dx.dataType.textArea:
        case dx.dataType.editorInput:
        case dx.dataType.email:
        case dx.dataType.auto:
        case dx.dataType.digits:
            ret = field.value;
            break;
        case dx.dataType.password:
            return '******';
        case dx.dataType.number:
            if (isEmpty(desc.data_format)){
                ret = formatNumber(field.value, 0);
            }else if (!isNaN(desc.data_format)){
                ret = formatNumber(field.value, parseInt(desc.data_format));
            }else{
                ret = formatNumber(field.value, evaluate(desc.data_format, form, 0));
            }
            break;
        case dx.dataType.date:
            ret = field.value ? $.format.date(new Date(field.value), 'yyyy-MM-dd') : "";
            break;
        case dx.dataType.time:
            ret = field.value ? $.format.date(new Date(field.value), 'HH:mm') : "";
            break;
        case dx.dataType.datetime:
            ret = field.value ? $.format.date(new Date(field.value), 'yyyy-MM-dd HH:mm') : "";
            break;
        case dx.dataType.boolean:
            ret = field.value ? msg('Yes') : msg('No');
            break;
        case dx.dataType.pic:
            if (!field.value)
                return ret;
            var name = field.value.substr(field.value.lastIndexOf('/') + 1);
            return '<a class="dx-preview-a" data-title="' + name + '" data-lightbox="' + name + '" name="' + field.id + '"  href="' + makeUrl('/storage/preview.do') + '?id=' + field.id + '"> ' + name + '</a>';
        case dx.dataType.file:
            if (!field.value)
                return ret;
            return '<a href="#" name="' + field.id + '" class="dx-download-a">' + field.value.substr(field.value.lastIndexOf('/') + 1) + '</a>';
    }
    if (noLink || isEmpty(ret) || !desc.link)
        return ret;
    return '<a href="#" class="dx-column-link" data-form="' + form.id + '" data-table="' + desc.table_id + '" data-column="' + desc.column_name + '">' + ret + '</a>';
}

/**
 * close form, remove cache
 * @param formId
 * @param [callback] called before js form cache destroy, and after server side form destroy
 */
function closeForm(formId, callback) {
    function doClose() {
        form.p('closing', true);
        postJson('/form/close.do', {id: formId}, function () {
            if (callback)
                callback();
            destroyModelCache(formId);
        }, function () {
            form.p('closing', false);
        });
    }

    var form = getFormModel(formId);
    if (form.p('closing'))
        return;
    if (form.p('reference')) {
        messageBox('has child');
        return;
    }
    if (form.p('confirm') && form.p('modified'))
        confirmBox('data modified.', function () {
            doClose();
        });
    else
        doClose();
}

/**
 * check form using table check rules
 *
 * @param form
 * @param success
 */
function checkForm(form, success) {
    if (form.action == 'view') {
        success();
        return true;
    }
    if (!$('#' + form.id).valid()){
    	 return false;
    }
    // in tree module, no table check
    if (form.widgetName === 'tree') {
        success();
        return true;
    }
    var table = getTableDesc(form.tableName);
    var rules = table.checkRules;
    if (!rules) {
        success();
        return true;
    }
    return checkFormRules(form, rules, success);
}

/**
 * check records using table check rules
 *
 * @param records record list
 * @param rules table check rules
 * @param success success callback
 * @param fail callback for failed
 * @param [index] don't set it, internal using
 * @param [suppressWarning] don't show warning message, internal using
 */
function checkRecordsRules(records, rules, success, fail, index, suppressWarning) {
    if (!rules) {
        success();
        return true;
    }
    if (index === undefined)
        index = 0;
    if (index >= records.length) {
        success();
        return true;
    }
    return checkFormRules(records[index], rules, function (error) {
        if ((suppressWarning === undefined) && error)
            suppressWarning = true;
        checkRecordsRules(records, rules, success, fail, ++index, suppressWarning);
    }, function (error) {
        if (fail)
            fail(error);
        else {
            messageBox(error.msg);
            if (error.column_name)
                form.columns[error.column_name].get().focus();
        }
    }, suppressWarning);
}

/**
 * evaluate table's check rules, using form
 *
 * @param form
 * @param rules
 * @param success callback when success
 * @param [fail] callback when failed
 * @param [suppressWarning] don't show warning message
 * @returns {*}
 */
function checkFormRules(form, rules, success, fail, suppressWarning) {
    var error = null;
    if (rules.every(function (rule) {
            if ((form.action === 'edit') && (rule.edit_submit == 0))
                return true;
            if ((form.action === 'create') && (rule.create_submit == 0))
                return true;
            if (rule.check_level == 2){
                if (evaluate(rule.formula, form, null)) {
                    error = rule;
                    return false;
                }
            }
            //if (evaluate(rule.formula, form, null)) {
            //    error = rule;
            //    return false;
            //}
            return true;
        })) {
        if (success) success();
        return true;
    }
    var param = evaluate('[' + error.error_msg_param + ']', form);
    var result = {msg: msg(error.error_msg_id, param), rule: error};

    if (fail) {
        fail(result);
        return false;
    }
    if (error.is_error) {
        messageBox(result.msg, function () {
            if (error.column_name)
                form.columns[error.column_name].get().focus();
        });
        return false;
    }

    if (suppressWarning) {
        if (success) success(error);
        return true;
    }
    return confirmBox(result.msg, function () {
        if (success) success(error);
        return true;
    }, function () {
        if (error.column_name)
            form.columns[error.column_name].get().focus();
        return false
    });
}
//审批按钮的处理前提
function checkApproveButtonEvent(flowEvent,form) {
	var error = null;
	var action_prerequistie = flowEvent.condition
	if(!isEmpty(action_prerequistie)){
		for(var i=0;i<action_prerequistie.length;i++){
			if(action_prerequistie[i].is_using==1 && action_prerequistie[i].level=="2"){
				if (evaluate(action_prerequistie[i].check_condition, form, null)) {
					var param = evaluate('[' + action_prerequistie[i].violate_msg_param + ']', form);
					var result = {msg: msg(action_prerequistie[i].violate_msg_international_id, param), rule: error};
					if(confirm(result.msg)){
						return true;
					}
	                return false;
	            }
			}
		}
	}
	return true;
}
//审批按钮的处理前提--主页/列表页
function checkApproveButtonEventListOrIndex(flowEvent,data) {
	var error = null;
	var action_prerequistie = flowEvent.condition
	if(!isEmpty(action_prerequistie)){
		for(var i=0;i<action_prerequistie.length;i++){
			if(action_prerequistie[i].is_using==1 && action_prerequistie[i].level=="2"){
				if (evaluate(action_prerequistie[i].check_condition, null, null,null,null,data)) {
					var param = evaluate('[' + action_prerequistie[i].violate_msg_param + ']', null, null,null,null,data);
					var result = {msg: msg(action_prerequistie[i].violate_msg_international_id, param), rule: error};
					if(confirm(result.msg)){
						return true;
					}
	                return false;
	            }
			}
		}
	}
	return true;
}

function buildApiJsonParam(param, data, isIndex, isList) {
    var resultParam = {};
    for (var key in param) {
        if (typeof param[key] == 'object') {
            resultParam[key] = buildApiJsonParam(param[key], data, isIndex, isList);
        } else {
            if (isIndex)
                resultParam[key] = evaluate(param[key], null, null, null, null, data);
            else if (isList)
                resultParam[key] = evaluate(param[key], {id: data.parent}, null, null, null, null, data.id);
            else
                resultParam[key] = evaluate(param[key], data);
        }
    }
    return resultParam;
}
/**
 * 增/删/改/查/操作后跳其它页面
 *
 */
function afterCURDONewPage(form, newPageUrl, oldDataForm, trigger, isDelete, rows) {
    function buildParam(param, form, oldData, resultArrayParam) {
        if (isDelete) {
            for (var i = 0; i < oldData.length; i++) {
                var arrayParam = {};
                for (var key in param) {
                    if (typeof param[key] == 'object') {
                        arrayParam[key] = buildParam(param[key], form, oldData, resultArrayParam);
                    } else {
                        arrayParam[key] = evaluate(param[key], null, null, null, null, oldData[i]);
                    }
                }
                if (isEmpty(resultArrayParam))
                    resultArrayParam = [];
                resultArrayParam.push(arrayParam);
            }
            return resultArrayParam;
        } else {
            var resultParam = {};
            for (var key in param) {
                if (typeof param[key] == 'object') {
                    resultParam[key] = buildParam(param[key], form);
                } else {
                    resultParam[key] = evaluate(param[key], form, null, oldData);
                }
            }
            return resultParam;
        }
    }

    function getTrigger(table, isDelete) {
        if (isDelete) {
            for (var key in table.triggers) {
                if (table.triggers[key].system_type == 'delete') {
                    return table.triggers[key];
                }
            }
            return null;
        }
        if (!isEmpty(trigger))
            return trigger;
        else {
            if (isEmpty(table.triggers))
                return null;
            var action = form.action;
            switch (action) {
                case 'edit' :
                    for (var key in table.triggers) {
                        if (table.triggers[key].system_type == 'edit') {
                            return table.triggers[key];
                        }
                    }
                    return null;
                case 'create' :
                    for (var key in table.triggers) {
                        if (table.triggers[key].system_type == 'add') {
                            return table.triggers[key];
                        }
                    }
                    return null;
            }
        }
    }
    function buildApiParam(apis, oldData, form, isDelete){
        for (var i = 0; i < apis.length; i++) {
            if (apis[i].is_using != 1) {
                continue;
            }
            switch (apis[i].event_type) {
                case 1 :   //系统URL
                    if (isDelete) {

                    } else {
                        var url = dx.urlInterface[apis[i].event_name];
                        newTab(url.url, buildParam(apis[i].params, form, oldData));
                    }
                    break;
                case 2 :   //java 事件   新增，编辑先不考虑要在页面输入参数的情况。
                    //解析公式，放到api对象里。
                    if (isDelete) {
                        apis[i].listRequestParams = buildParam(apis[i].params, form, oldData);
                    } else {
                        apis[i].requestParams = buildParam(apis[i].params, form, oldData);
                    }
                    break;
                case 3 :   //存储过程
                    if (isDelete) {
                        apis[i].listRequestParams = buildParam(apis[i].params, form, oldData);
                    } else {
                        apis[i].requestParams = buildParam(apis[i].params, form, oldData);
                    }
                    break;
                case 4 :  //外部接口
                    if (isDelete) {
                        apis[i].listRequestParams = buildParam(apis[i].params, form, oldData);
                    } else {
                        apis[i].requestParams = buildParam(apis[i].params, form, oldData);
                    }
                    break;
                case 5 :  //外部页面
                    if (isDelete) {

                    } else {
                        var url = dx.urlInterface[apis[i].event_name];
                        outPage(url, buildParam(apis[i].params, form, oldData))
                    }
                    break;
            }
        }
        return apis;
    }
    function parentActionButton(actionButton, form, isDelete){
        if (isEmpty(actionButton))
            return;
        var apis = actionButton.api;
        if (isEmpty(apis))
            return;
        //编辑时获取old 数据
        var oldData;
        if (form.action == 'edit') {
            postJson('/detail/getOldParam.do', oldDataForm, function (result) {
                oldData = result;
            }, null, true);
        } else if (isDelete) {
            oldData = [];
            if (!isEmpty(rows)) {
                for (var i = 0; i < rows.length; i++) {
                    var columnValue = {};
                    var columns = rows[i].columns;
                    if (!isEmpty(columns)) {
                        for (var key in columns) {
                            columnValue[key] = columns[key].value;
                        }
                    }
                    oldData.push(columnValue);
                }
            }
        }
        actionButton.api = buildApiParam(apis, oldData, form, isDelete);
        return actionButton;
    }
    function buildChildParam(param, form, childData){
        var resultParam = {};
        for (var key in param) {
            if (typeof param[key] == 'object') {
                resultParam[key] = buildParam(param[key], form);
            } else {
                resultParam[key] = evaluate(param[key], childData, null);
            }
        }
        return resultParam;
    }
    function buildChildApiParam(apis, childData, form){
        for (var i = 0; i < apis.length; i++) {
            if (apis[i].is_using != 1) {
                continue;
            }
            switch (apis[i].event_type) {
                case 1 :   //系统URL
                    if (isDelete) {

                    } else {
                        var url = dx.urlInterface[apis[i].event_name];
                        newTab(url.url, buildChildParam(apis[i].params, form, childData));
                    }
                    break;
                case 2 :   //java 事件   新增，编辑先不考虑要在页面输入参数的情况。
                    //解析公式，放到api对象里。
                    apis[i].listRequestParams = [];
                    for (var j=0; j<childData.length; j++)
                        apis[i].listRequestParams.push(buildChildParam(apis[i].params, form, childData[j]));
                    break;
                case 3 :   //存储过程
                    apis[i].listRequestParams = [];
                    for (var j=0; j<childData.length; j++)
                        apis[i].listRequestParams = buildChildParam(apis[i].params, form, childData[j]);
                    break;
                case 4 :  //外部接口
                    apis[i].listRequestParams = [];
                    for (var j=0; j<childData.length; j++)
                        apis[i].listRequestParams = buildChildParam(apis[i].params, form, childData[j]);
                    break;
                case 5 :  //报表
                    apis[i].listRequestParams = [];
                    for (var j=0; j<childData.length; j++)
                        apis[i].listRequestParams = buildChildParam(apis[i].params, form, childData[j]);
                    break;
            }
        }
        return apis;
    }
    function childActionButton(actionButton){
        if (isEmpty(actionButton))
            return;
        var apis = actionButton.api;
        if (isEmpty(apis))
            return;
        var formWithChildren;
        postJson('/detail/getChildParam.do', oldDataForm, function (result) {
            formWithChildren = result;
        }, null, true);
        if (!isEmpty(formWithChildren) && !isEmpty(formWithChildren.children)){
            for (var i=0; i<formWithChildren.children.length; i++){
                if (formWithChildren.children[i].table == actionButton.table_id){
                    actionButton.api = buildChildApiParam(apis, formWithChildren.children[i].childDatas, form, true);
                }
            }
        }
        return actionButton;
    }
    if (newPageUrl == '/detail/view.do') {
        return;
    }
    var table = getTableDesc(form.tableName);
    var actionButton = getTrigger(table, isDelete);
    actionButton = parentActionButton(actionButton, form, isDelete);
    var childrenActionButton = {};
    if (!isEmpty(form.children)){
        for (var i=0; i<form.children.length; i++){
            childrenActionButton[form.children[i].table] = getTrigger(getTableDesc(form.children[i].table), isDelete);
        }
    }
    if (!isEmpty(childrenActionButton)){
        for (var key in childrenActionButton){
            childrenActionButton[key] = childActionButton(childrenActionButton[key]);
        }
    }
    var actionButtons = {};
    actionButtons.parentActionButton = actionButton;
    actionButtons.childActionButton = childrenActionButton;
    return actionButtons;
}
function buildBlockEventParamForIndex(event, table, dataId, blockEvent, requestBlockEvent, callBack) {
    postJson('/approve/approveData.do', {tableId: table.id, dataId: dataId}, function (result) {
        if (!isEmpty(blockEvent))
            for (var key in blockEvent){
                requestBlockEvent[key] = buildBlockEventParam(blockEvent[key], result, true);
            }

        if (isEmpty(table))
            return;
        if (!isEmpty(table) && !isEmpty(event) && !isEmpty(event.event)) {
            for (var key in event.event) {
                if (!isEmpty(event.event[key].paseParam)) {
                    event.event[key].requestParam = buildApiJsonParam(event.event[key].paseParam, result, true);
                }
            }
            callBack(event,result);
        }
        callBack(event,result);
    });
}
function approveAfterNewPage(flowEvent) {
    if (isEmpty(flowEvent) || flowEvent.event)
        return;
    for (var key in flowEvent.event) {
        var url = dx.urlInterface[flowEvent.event[key].event_name];
        if (isEmpty(url))
            continue;
        switch (url.type) {
            case 1 :
                newTab(url.url, flowEvent.event[key].requestParam);
                break;
            case 2 :
                break;
            case 3 :
                break;
            case 4 :
                break;
            case 5 :
                break;
        }
    }
}
/**
 * init form submit button, for detail form
 *
 * @param $container
 * @param [callback] callback when submit success
 * @param [opts] options {dataProcessor: callback, confirm: callback}
 */
function initSubmit($container, callback, opts) {
    if (!opts) opts = {};
    $container.find('input.dx-submit').on('click', function () {
        var isMobile = false;
        if ($(this).hasClass('approve-edit')){
            isMobile = true;
        }
        var $button = $(this);
        // if button name is empty, do nothing
        if (isEmpty(this.name)) {
            if (callback)
                callback.call($button, isMobile);
            return;
        }
        var $form = $button.closest('.dx-form');
        var form = getFormModel($form.attr('id'));
        var self = this;
        var isSaveAndSubmit = $(this).hasClass('save-and-submit');
        
        //编辑前校验。
        if (form.action == 'view'){
            var checkFlag = true;
            checkCRUD(getTableDesc(form.tableName), 'edit', [form], function (flag) {
                checkFlag = flag;
            });
            if (!checkFlag){
                return;
            }
        }
        if(dx.user.isMobileLogin==1){
        	 $form.find(".approve-record-info").css("display","none");
	    	 $form.find(".dx-form-con").removeClass("mobile-view");
	         $form.find(".dx-form-con").addClass("mobile-create");
	         $form.find(".dx-dict-select ,.dx-date").attr("placeholder","请选择>");
	         $(".row-blue-btn").css("display","block");
	         $(".unfurled").css("display","block");
	         $form.find(".dx-input,.dx-number,.dx-input-with-format,.dx-digits").attr("placeholder","请填写");
	         if(dx.table[form.tableName].is_approve == 1 && form.hasSaveAndSubmitButton){
	        	 isSaveAndSubmit=true;
	        	 $(this).addClass('save-and-submit');
	         }
	         if(form.children!=undefined && form.action == 'view'){
	        	 if(form.children.length!=0){
	        		 $("#"+form.children[0].id).find(".dx-menu-bar .dx-submit").click();
	        		 $("#"+form.children[0].id).find(".record-head").css("display","block");
	        	 }
	         }
        }
       
        dx.mobileValid=checkForm(form, function () {
            function submit(syn) {
            	if(dx.user.isMobileLogin==1 && form.freeApprove==0 && form.action=="create"){
            		var $imgs=$form.find(".approvers").find("img");
            		if($imgs.length==0){
            			alert("请选择加签人员！");
            			return;
            		}
            		var userId={};
            		$imgs.each(function(){
            			userId[this.name]=uuid(10,16);
            		});
            		data.userId=userId;
            	}
            	
                var url = form.submitUrl;
                if (!url)
                    url = '/' + form.widgetName + '/' + self.name + '.do';
                dx.processing.open();
                if (isSaveAndSubmit){  //保存并提交审批
                    data.isSaveAndSubmit = 1;
                    //提交事件
                    var table = getTableDesc(form.tableName);
                    var submitEvent, requestSubmitEvent = {};
                    submitEvent = table.submitEvent;
                    if (!isEmpty(submitEvent))
                        requestSubmitEvent.submitEvent = buildBlockEventParam(submitEvent, form);
                    data.approveBlockEvent = requestSubmitEvent;
                }
                var newPageUrl = url;
                //增删改查，跳转页面。因为有多个API 所以在访问后台前调用。多个API中跳转页面API不一定是后调用。
                var triggers = afterCURDONewPage(form, newPageUrl, data);
                if (!isEmpty(triggers)){
                    if (!isEmpty(triggers.parentActionButton))
                        data.triggerRequestParams = triggers.parentActionButton;
                    if (!isEmpty(triggers.childActionButton))
                        data.childTriggerRequestParams = triggers.childActionButton;
                }
                //是否为首页进的编辑。
                data.isIndexView = form.isIndexView;
                data.isIndexViewParam = form.isIndexViewParam;
                data.import_type=form.import_type;
                dx.processing.open();
                postJson(url, data, function (result) {
                    dx.ajax = 0;
                	if(result=="NoApprover"){
                		$form.find('.approve-flow-add-point .approve-add-point-save').addClass("free-approve-save");
                        $form.find('.approve-flow-add-point .modal-title').addClass("free-approve-title").html("<span></span>您的审批流程为自由流程，需选择审批人");
                		$form.find('.approve-flow-add-point').modal('show');
                		$form.find('.free-approve-save').off("click");
                		$form.find('.free-approve-save').on("click",function(){
                			var userId={};
                			var span=$(".approve-add-selected").find("span.addApprove_span");
                			if(span.length>0){
            		        	for(var i=0;i<span.length;i++){
            		        		userId[$(span[i]).attr("selectValue")]=uuid(10,16);
            		            }
            		        	data.userId=userId;
            		        	postJson(url, data, function (result) {
    		        				if (callback){
    		        					callback.call($button, result, isMobile);
    		        				}
            		            });
            		        }else{
            		        	alert(msg("PleaseSelectApprover"));
            		        	return false;
            		        }
                		});
                	}else{
                		if (callback){
                			callback.call($button, result, isMobile);
                		}
                            	
                	}
                    dx.processing.close();
                },null,syn);

            }

            if (form.p('reference')) {
            	if(dx.user.isMobileLogin!=1){
            		messageBox('has child');
                    return;
            	}
            }
            var data = {table: form.tableName, id: $form.attr('id'), param: {}};
            $.each(form.fieldIds, function (i, fid) {
                var field = w(fid);
                data.param[field.id] = field.val();
                //if (!isEmpty(field.selection))
                //    data.param[field.id] = field.selection;
                if (!isEmpty(field.eventColor)){
                    if (isEmpty(data.calendarColorParam)){
                        data.calendarColorParam = {};
                    }
                    data.calendarColorParam[field.id] = field.eventColor();
                    if (isEmpty(data.calendarColorStatus)){
                        data.calendarColorStatus = {};
                    }
                    data.calendarColorStatus[field.id] = field.eventColorStatue;
                }
            });
            if (opts.dataProcessor)
                data = opts.dataProcessor(form, data);
            if (opts.confirm)
                opts.confirm(data, submit);
            else{
            	if(dx.user.isMobileLogin==1 && form.action!="view"){
            		var syn=false;
            		$.each(form.children,function(k,v){
            			$("#"+v.id).find(".dx-submit").click();
            			$("#"+v.id).find(".dx-submit").attr("isClose","false");
            			syn=true;
            		});
            		if(dx.mobileValid!=false){
            			submit(syn);
            		}
	        	}else
	        		submit(false);
            }
                
        });
    });
}



function uuid(len, radix) {
    var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    var uuid = [], i;
    radix = radix || chars.length;
    if (len) {
        for (i = 0; i < len; i++)
            uuid[i] = chars[0 | Math.random() * radix];
    } else {
        var r;
        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
        uuid[14] = '4';
        for (i = 0; i < 36; i++) {
            if (!uuid[i]) {
                r = 0 | Math.random() * 16;
                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
            }
        }
    }
    return uuid.join('');
}
/*
function getApprovers(){
	
}*/
/**
 * destroy form cached data
 *
 * @param id
 */
function destroyModelCache(id) {
    var form = dx.cache.form[id];
    if (form.parent) {
        var parentForm = form.p('parent');
        // parentForm not set, if form's parent was index form
        if (parentForm)
            parentForm.p('reference', parentForm.p('reference') - 1);
    }
    delete dx.cache.form[id];
}

/**
 *
 */
function doAutoExpand(form) {
    var $expandable = form.get().find('.dx-auto-expand');
    if ($expandable.length === 1) {
        form.p('expandArea', $expandable);
        autoExpand(form);
    }
}

/**
 * attach new tab to main tabs when menu click
 *
 * @param url form url
 * @param data url post data
 * @param [postInit] post init callback after everything build
 * @param [ownerId] id to prevent open multiple tab
 */
function newTab(url, data, postInit, ownerId) {
    if (typeof postInit === 'string') {
        ownerId = postInit;
        postInit = null;
    }

    /*function makeTabLi(id, form) {
     return $('<li aria-controls="' + id + '"><a data-toggle="tab" title="' + form.title + '" href=#' + id + '><img onerror="this.src=\'' + makeUrl('/icons/list.png') + '\'" src="' + makeUrl('/icons/' + form.icon + '.png') + '"/> ' + msg(form.title) + '<span class="close glyphicon glyphicon-remove-sign"></span></a></li>');
     }*/

    function makeTabLi(id, form) {
        return $('<li aria-controls="' + id + '"><a data-toggle="tab" title="' + form.title + '" href=#' + id + '><i class="' + form.tabIcon + '"></i>&nbsp;' + msg(form.title) + (form.widgetName == 'detail' ? msg('Detail') : '') + '<span class="closeTab">×</span></a></li>');
    }

    if (findTab(ownerId)) return;

    if (dx.mainNav.find('a').length >= 999) {
        messageBox('exceed max tab', 15);
        return;
    }

    if (!data)
        data = {};
    postPage(url, data, function (result) {
        var id = "tabs-" + dx.tabIndex;
        var $panel = $('<div id="' + id + '" class="tab-pane">' + result + '</div>');
        dx.mainTab.append($panel);
        //by now, the embedded js has been executed, we can use cache variables
        var $form = $panel.find('.dx-form');
        var form = getFormModel($form.attr('id'));
        buildFormCache(form, form.widgets);

        // var $li = makeTabLi(id, form);   原来的
        // if (data.parent)
        // 	$li.insertAfter(findFormLi(data.parent));
        // else
        // 	dx.mainNav.append($li);
        //
        // activeTab($li.index());

        var $li = makeTabLi(id, form);
        var $allli = dx.mainNav.find("li");
        var totalWidth = 0;
        for (var l = 0; l < $allli.length; l++) {
            totalWidth = totalWidth + $allli.eq(l).width() + 8;
        }
        if (data.parent) {
            $li.insertAfter(findFormLi(data.parent));
            // activeTab($li.index());  //原来的
            if($li.parents("ul").hasClass("home-tab-navs-rest")){
                activeTab($li.parents("ul").find($li).index(),"rest");
                $li.parents("ul").find("li").eq($li.index()-1).removeClass("active");
            }else{
                activeTab($li.parents("ul").find($li).index());
            }
        } else {
            if (dx.mainNav.width() - 52 - totalWidth < 120 || $(".home-tab-navs-rest li").length) {//没有空间，副菜单有li
                $(".dx-main-tabs .home-navs-rest-drop").css("display", "block");
                $(".dx-main-tabs .home-tab-navs-rest").append($li);
                $(".dx-main-tabs .home-tab-navs-rest li").removeClass("active");
                activeTab($(".dx-main-tabs .home-tab-navs-rest li").length-1, "rest");
                dx.mainNav.find("li").removeClass("active");
            } else {
                dx.mainNav.append($li);
                activeTab($li.index());
            }
        }
        $li.attr('aria-owns', ownerId);
        dx.tabIndex++;
        if (postInit)
            postInit(form, $li);
        if (dx.init[form.widgetName])
            dx.init[form.widgetName](form, $li);
        else
            console.warn('no init function registered for "' + form.widgetName + '"');

        if (dx.moduleInit[form.widgetName])
            dx.moduleInit[form.widgetName].forEach(function (func) {
                func(form, $li);
            });
        doAutoExpand(form);
        if(form.action!="create"){
            $form.find(".dx-editor-input-textarea").summernote({
                popover:{
                    image:false
                }}).summernote('disable');
        }
    });
}


function dxToastAlert(msg) {
    $(".dx-main-tabs").prepend('<div class="dxToastAlert alert alert-warning alert-dismissible"><span class="dxToasticon"></span>' + msg + '</div>');
    $(".alert-warning").fadeIn(100);
    var timer;
    timer = setTimeout(function () {
        $(".dxToastAlert.alert-warning").fadeOut().remove();
    }, 4000);
}

/**
 * recalculate expand area height, and call resize handler if exists
 * @param form
 */
function autoExpand(form) {
    var $area = form.p('expandArea');
    if (!$area)
        return;
    // var height = dx.mainContent.height() - 10 - $area.position().top; //原来的
    // var height = dx.mainContent.height() - $area.position().top-2; //

    // if(form.get().find(".dx-filter-select").is(":hidden")){
    //    var height = dx.mainContent.height() - $area.parents(".dx-form-con").position().top-2;
    // }else{
    //    var height = dx.mainContent.height() - $area.parents(".dx-form-con").position().top- form.get().find(".dx-filter-select").height()-1-2;
    // }

    // form.p('expandHeight', height);
    var resize = form.p('resize');
    // if (resize)
    // resize(height, $area);
    // else
    // $area.height(height);
    $area.trigger('dx.auto-expand');
}

/**
 * repeat char for n times and return string
 * @param char
 * @param count
 * @returns {string}
 */
function charRepeat(char, count) {
    return new Array(count + 1).join(char);
}

/**
 * popup dialog using content, and call shown/hidden callback
 *
 * @param opts option object defined bellow
 * url: dialog content url
 * data: dialog url post data
 * title: dialog title
 * type: dialog type
 * [buttons]: dialog buttons
 * [shown]: shown callback
 * [hidden]: hidden callback
 * [fail]: post fail callback
 */
function showDialogForm(opts) {
    postPage(opts.url, opts.data ? opts.data : {}, function (content) {
        BootstrapDialog.show({
            title: opts.title,
            // msg(opts.title),
            message: $(content),
            type: opts.type ? opts.type : BootstrapDialog.TYPE_INFO,
            buttons: opts.buttons ? opts.buttons : [],
            onshow: opts.show,
            animate: false,
            // draggable: true,
            cssClass: opts.class,
            onshown: function (dialog) {
                var $container = dialog.getModalContent().find('.dx-form');
                var dialogForm = getFormModel($container.attr('id'));

                // data prepare callback when submit
                buildFormCache(dialogForm, dialogForm.widgets);
                if (dx.init[dialogForm.widgetName])
                    dx.init[dialogForm.widgetName](dialogForm);
                else
                    console.warn('no init function registered for "' + dialogForm.widgetName + '"');
                dialogForm.p('formDialog', dialog);
                if (opts.shown)
                    opts.shown(dialogForm, dialog);
                if (opts.needAutoExpand)
                    doAutoExpand(dialogForm);
            },
            onhidden: function (dialog) {
                var formId = dialog.getModalContent().find('.dx-form').attr('id');
                if (opts.hidden)
                    opts.hidden(getFormModel(formId), dialog);
                closeForm(formId);
            }
        });
    }, opts.fail);
}

var defaultValidateOptions = {
    submitHandler: function (form) {
    },
    onfocusout: false,
    onfocusin: false,
    onkeyup: false,
    onclick: false,
    highlight: function (element) {
        $(element).closest('.form-group').addClass('has-error');
        $(element).addClass('has-error');
    },
    unhighlight: function (element) {
        var $element = $(element);
        $element.closest('.form-group').removeClass('has-error');
        $element.prop('title', '');
    },
    errorElement: 'span',
    errorClass: 'help-block',
    ignore: '.dx-invisible, [type="checkbox"]',
    errorPlacement: function (error, element) {
        element.prop('title', error.text());
    },
    invalidHandler: function (form, validator) {
        if (w(this.getAttribute('id')).p('errorSilence'))
            return;
        var errors = validator.numberOfInvalids();
        if (errors)
            messageBox('vaildate failed');
    }
};

/**
 * init checkbox field
 *
 * @param form
 */
function initCheckbox(form) {
    form.get().find('input[type="checkbox"].dx-field').each(function () {
        var field = w(this.id);
        var self = this;
        field.val = function (data) {
            if (data === undefined)
                return self.checked;
            else
                self.checked = data;
        };
        field.text = function () {
            return self.checked ? 'true' : 'false';
        };
    });
}

/**
 * always init virtual fields using formula
 *
 * @param form
 */
function initVirtualFields(form) {
    form.fields.forEach(function (field) {
        var desc = getDesc(field);
        if (desc.virtual && field.val)
            field.val(evaluate(desc.formula, form));
    })
}

/**
 * check input is empty
 *
 * @param val
 * @param [keepSpace]
 * @returns {boolean}
 */
function isEmpty(val, keepSpace) {
    if (val === undefined) return true;
    if (val === null) return true;
    if (typeof val == 'object') {
        for (var name in val) {
            return false;
        }
        return true;
    }
    if (!keepSpace)
        val = $.trim(val);
    return val.length === 0;

}

/**
 * validate val for date string
 * @param val
 * @param [format]
 */
function isDate(val, format) {
    if (!format) format = 'YYYY/MM/DD';
    return moment(val, format, true).isValid();
}

/**
 * iterate object properties
 *
 * @param obj
 * @param func
 */
function forEach(obj, func) {
    for (var p in obj)
        if (obj.hasOwnProperty(p))
            func(p, obj[p]);
}

/**
 * get size of object
 * @param obj
 */
function sizeOf(obj) {
    return Object.keys(obj).length;
}
/**
 * init all components in $container
 *
 * @param form
 */
function initComponent(form) {
	if(form.children!=undefined){
		if(form.children.length>0 &&dx.user.isMobileLogin==1){
			initMobileGrid(form);
		}
	}
	
    initGrid(form);
    initField(form);
}

/**
 * init operation drop down menu
 * @param $container
 * @param widget
 */
function initOpMenu($container, widget, isList) {
    $container.find('.dx-operation').each(function () {
        var $a = $(this);
        var $li = $a;
        var id = $a.attr('name');
        // init table shortcut event
        if ($a.hasClass('dx-table-shortcut')) {
            if (!widget.shortcuts)
                widget.shortcuts = {};
            widget.shortcuts[id] = $li;
            $a.on('click', function () {
                widget.shortcut(getShortcut($a.attr('name')));
            });
            return;
        }
        // init table operation event
        if (!widget.ops)
            widget.ops = {};
        // <li> tag map for set enable/disable
        widget.ops[id] = $li;
        $a.on('click', function () {
            //点击操作的时候校验数据是否有操作权限。
            var grid;
            var rows;
            var isDetail;
            if (isList){
                grid = w(widget.grid.id);  //list页操作按钮
                rows = grid.getSelectedRows();
            }else{
                grid = w(widget.id);      //明细查看页操作按钮
                isDetail = grid.id;
                rows = [grid];
            }
            var table = getTableDesc(widget.tableName);
            checkOpMenu(table.triggers[$(this).attr('name')], rows,
                function (notRuleRow) {
                    if (!isEmpty(notRuleRow)) {
                        var nameColumn = table.name_column;
                        var value;
                        if (nameColumn) {
                            value = notRuleRow.row.columns[nameColumn].value
                        } else {
                            var idColumns = table.idColumns;
                            if (idColumns) {
                                value = notRuleRow.row.columns[idColumns[idColumns.length - 1]].value
                            }
                        }
                        //messageBox(notRuleRow.msg[dx.user.language_id] + '  ' + msg('row') + " " + value);
                        if (notRuleRow.level == 2){
                            if (confirm(notRuleRow.msg))
                                widget.operate($a.attr('column'), $a.attr('name'), isDetail, true);
                        }else{
                            messageBox(notRuleRow.msg);
                        }
                        return;
                    }
                    widget.operate($a.attr('column'), $a.attr('name'), isDetail);
                }, isDetail);
        });
    })
}

function checkCRUD(table, buttonType, rows, callback){
    if (isEmpty(table.triggers))
        callback(false);
    var buttonAction;
    for (var key in table.triggers){
        if (table.triggers[key].system_type == buttonType){
            buttonAction = table.triggers[key];
        }
    }
    if (isEmpty(buttonAction))
        callback(false);
    checkOpMenu(buttonAction, rows, function (notRuleRow) {
            if (!isEmpty(notRuleRow)) {
                var nameColumn = table.name_column;
                var value;
                if (nameColumn) {
                    value = notRuleRow.row.columns[nameColumn].value
                } else {
                    var idColumns = table.idColumns;
                    if (idColumns) {
                        value = notRuleRow.row.columns[idColumns[idColumns.length - 1]].value
                    }
                }
                //messageBox(notRuleRow.msg[dx.user.language_id] + '  ' + msg('row') + " " + value);
                messageBox(notRuleRow.msg);
                callback(false);
            }else{
                callback(true);
            }
        });
}
/**
 * set enable/disable status of operation drop down menu
 * @param id operation or shortcut id, set all if id == null
 * @param enabled
 * @param map
 */
function setOpMenuVisible(map, id, enabled) {
    if (id == null) {
        $.each(map, function (oid, $li) {
            if (enabled)
                $li.removeClass('hidden');
            else
                $li.addClass('hidden');
        });
        return;
    }
    if (enabled) {
        if (map[id]) {
            map[id].removeClass('hidden');
        }
    } else {
        if (map[id]) {
            map[id].addClass('hidden');
        }
    }
}

function calcMenuStatus(form, rows) {
    if (form.shortcuts)
        setOpMenuVisible(form.shortcuts, null, rows && rows.length === 1);
    var table = getTableDesc(form.tableName);
    if (form.ops)
        calcOpMenuStatus(table.triggers, form.ops, rows);
}

/**
 * register form init function
 * @param formName
 * @param func
 */
function registerInit(formName, func) {
    dx.init[formName] = func;
}

/**
 * register module init function for form names, which will be called after form inited
 *
 * @param formNames form names array/single form name, ie. ['list', 'detail']
 * @param func
 */
function registerModuleInit(formNames, func) {
    if (typeof formNames === 'string')
        formNames = [formNames];
    formNames.forEach(function (name) {
        var funcs = dx.moduleInit[name];
        if (!funcs)
            dx.moduleInit[name] = funcs = [];
        funcs.push(func);
    });
}

/**
 * reload form
 *
 * @param url
 * @param form
 * @param data
 * @param [callback]
 */
function reloadForm(url, data, form, callback) {
    if (!data.parent)
        data.parent = form.parent;
    postPage(url, data, function (result) {
        var $form = $('#' + form.id);
        var $div = $form.parent();
        //$form.remove();
        $div.empty();
        $div.append(result);

        // init new form
        var newForm = getFormModel($div.find('.dx-form').attr('id'));
        buildFormCache(newForm, newForm.widgets, true);

        if (dx.init[newForm.widgetName])
            dx.init[newForm.widgetName](newForm);
        if (callback)
            callback(newForm)
    })
}

function initCache(callback, isReload) {
    function initJsCache(data) {
        dx.dict = data.dict;
        dx.dictNameI8N = data.dictNameI18NCache;
        dx.table = data.table;
        dx.urlInterface = data.urlInterface;
        dx.tableViewStyle = data.tableViewStyle;
        /*$.each(dx.table, function (i, t) {
        	//console.log(i+","+t);
            t.columnMap = {};
            if (!isEmpty(t.columns)) {
                $.each(t.columns, function (i, c) {
                    t.columnMap[c.column_name] = c;
                });
            } else {
                alert(msg('table') + ' "' + t.id + '" ' + msg('have no column'));
            }
        });*/
        
        for(var key in dx.table){
        	dx.table[key].columnMap = {};
            if (!isEmpty(dx.table[key].columns)) {
                var columns=dx.table[key].columns;
                for(var x in columns){
                	dx.table[key].columnMap[columns[x].column_name] = columns[x];
                }
            } else {
                //alert(msg('table') + ' "' + dx.table[key].id + '" ' + msg('have no column'));
            }
        }
        dx.dataType = data.dataType;
        dx.i18n.message = data.message;
        dx.pages = data.pages;
        dx.operations = data.operations;
        dx.triggers = data.triggers;
        dx.shortcuts = data.shortcuts;
        dx.batches = data.batches;
        dx.status = data.status;
        dx.complexColumns = data.complexColumns;
        //dx.i18n.report = data.report;
        dx.sysGroupName = data.sysGroupName;
        dx.sys = data.sys;
        dx.biz = data.biz;
        dx.flowBlocks = data.flowBlocks;
        dx.domainKey = data.domainKey;
        dx.sqlMap = data.sqlMap;
        dx.result = {
            $details: $('#dxResultDetails'),
            $message: $('#dxResultMessage'),
            dialog: $('#dxResultDialog').get()[0]
        };
        callback();
        dx.processing.close();
        //
        //alert(
        //    '总和=' + JSON.stringify(data).length + '\r\n' +
        //    'dict=' + JSON.stringify(data.dict).length + '\r\n' +
        //'dictNameI18NCache=' + JSON.stringify(data.dictNameI18NCache).length + '\r\n'  +
        //'table=' + JSON.stringify(data.table).length + '\r\n'  +
        //'urlInterface=' + JSON.stringify(data.urlInterface).length + '\r\n'  +
        //'tableViewStyle=' + JSON.stringify(data.tableViewStyle).length + '\r\n'  +
        //'dataType=' + JSON.stringify(data.dataType).length + '\r\n'  +
        //'message=' + JSON.stringify(data.message).length + '\r\n'  +
        //'pages=' + JSON.stringify(data.pages).length + '\r\n'  +
        //'operations=' + JSON.stringify(data.operations).length + '\r\n'  +
        //'triggers=' + JSON.stringify(data.triggers).length + '\r\n'  +
        //'shortcuts=' + JSON.stringify(data.shortcuts).length + '\r\n'  +
        //'batches=' + JSON.stringify(data.batches).length + '\r\n'  +
        ////'status=' + JSON.stringify(data.status).length + '\r\n'  +
        //'complexColumns=' + JSON.stringify(data.complexColumns).length + '\r\n'  +
        //'sysGroupName=' + JSON.stringify(data.sysGroupName).length + '\r\n'  +
        //'sys=' + JSON.stringify(data.sys).length + '\r\n'  +
        //'biz=' + JSON.stringify(data.biz).length + '\r\n');
    }

    if (!isReload) {
        postJson('/data/getDomainKey.do', function (domainKey) {
            var data;
            try {
                data = JSON.stringify(localStorage.getItem(domainKey));
            }catch(err){

            }
            if (data == 'null')
                data = '';
            if (isEmpty(data)) {
                postJson('/data/initCache.do', function (data) {
                    try {
                        localStorage.clear();
                        localStorage.setItem(data.domainKey, JSON.stringify(data));
                    }catch(err){
                        //alert(msg('浏览器空间已满！！！！, 会且只会影响登录速度。请放心使用。'));
                        var aaa = 1;
                    }
                    initJsCache(data);
                });
            } else {
                data = $.parseJSON(data);
                data = $.parseJSON(data);
                postJson('/data/initSqlMapCache.do', function (sqlMap) {
                    data.sqlMap = sqlMap;
                }, null, true);
                initJsCache(data);
            }
        });
    } else {
        postJson('/data/initCache.do', function (data) {
            initJsCache(data);
            try{
                localStorage.clear();
                localStorage.setItem(data.domainKey, JSON.stringify(data));
            }catch(err){
                //alert(msg('浏览器空间已满！！！！, 会且只会影响登录速度。请放心使用。'));
                var aaa = 1;
            }
        });
    }
}

/**
 * init user and menu cache
 */
function initIndexForm() {
    // confirm closing
    function confirmClose() {
        return msg('Confirm close');
    }

    if (isEmpty(dx.user) || dx.user.isOutPage != 1)
        dx.user = dx.cache.form['index'].user;

    if (isEmpty(dx.user) || dx.user.isOutPage != 1)
        if (dx.user.logged){
            //adidas 需求。直接T掉。
            postJson('/auth/logoutElse.do', function () {
                window.onbeforeunload = confirmClose;
            });
            //若需要提示。则走下面逻辑
            //confirmBox("Logged in somewhere else",
            //    function () {
            //        postJson('/auth/logoutElse.do', function () {
            //            window.onbeforeunload = confirmClose;
            //        });
            //    },
            //    function () {
            //        window.location.href = makeUrl('/auth/logout.do');
            //    });
        }else
            window.onbeforeunload = confirmClose;

    if (isEmpty(dx.user) || dx.user.isOutPage != 1)
        buildFormCache(dx.cache.form['index']);
}

/**
 * add event handler on column link click
 */
function initColumnLink() {
    $(document).on('click', 'a.dx-column-link', function (e) {
        var $target = $(e.target);
        var form = w($target.data('form'));
        if (isEmpty(form))
            return;
        var desc = getColumnDesc($target.data('table'), $target.data('column'));
        if (desc.link.valid && !evaluate(desc.link.valid, form))
            return;
        var data;
        if (isEmpty(desc.link.param))
            data = {};
        else
            data = evaluate('(' + desc.link.param + ')', null, null, null, true, form.columns);
//		data.parent = form.id;
        switch (desc.link.method) {
            case 'dialog':
                data.isDialog = 1;
                showDialogForm({
                    url: desc.link.url,
                    title: desc.link.title ? i18n(desc.link.title) : '',
                    data: data
                });
                break;
            case 'tab':
                var tabId = 'cl.' + desc.table + '-' + desc.column_name;
                if (findTab(tabId, true)) {
                    messageBox('cl.opened');
                    break;
                }
                dx.processing.open(msg('opening'));
                newTab(desc.link.url, data, tabId);
                break;
            case 'func':
                eval(desc.link.url + '.call(form, data)');
                break;
            case 'external':
                window.open(evaluate('("' + desc.link.url + '")', form));
                break;
            case 'dx_default':
                var default_data = {};
                default_data.table = desc.ref_table_name;
                default_data.readonly = true;
                if (isEmpty(data)) {
                    var input_id = '#' + $(this).attr('aria-owns');
                    var input_value = $(input_id).val();
                    var column_name = desc.column_name;
                    data = {};
                    data[column_name] = input_value;
                    if (isEmpty(input_value))
                        break;
                }
                var count = 0;
                for (var key in data) {
                    if (isEmpty(data[key])) {
                        count++
                        break;
                    }
                }
                if (count)
                    break;
                default_data.isDialog = 1;
                default_data.param = data;
                default_data.hideButtons = ['all'];
                showDialogForm({
                    url: desc.link.url,
                    title: i18n(desc.i18n) ? i18n(desc.i18n) : '',
                    data: default_data,
                    class: 'dx-form-selector-dialog',
                    shown: function (listForm, dialog) {
                        var menu_id = '#menu-bar-' + listForm.id;
                        $(menu_id).css('display', 'none');
                    }
                });
                break;
        }
    });
}

/**
 * everything started from here
 */
$(function () {
    //var WshNetwork = new ActiveXObject("WScript.Network");
    //alert("Domain = " + WshNetwork.UserDomain);
    //alert("Computer Name = " + WshNetwork.ComputerName);
    //alert("User Name = " + WshNetwork.UserName);

    dx.processing.open();
    $.ajaxSetup({
        contentType: 'application/x-www-form-urlencoded; charset=utf-8',
        cache: false
    });

    window.onerror = function (msg, url, line, col, error) {
        if (dx.processing.isOpen)
            dx.processing.close();
        //messageBox('internal error: ' + msg + '\nurl:' + url + "\nline: " + line + "\nerror:" + error);
        messageBox('application error');

        // If you return true, then error alerts (like in older versions of
        // Internet Explorer) will be suppressed.
        //return true;
    };

    // grid in hidden tab need recalculate column width
    $(document).on('shown.bs.tab', 'a[data-toggle="tab"]', function (e) {
        var $grid = $($(e.target).attr('href')).find('.dataTables_scrollBody').find('.dx-grid');
        if ($grid.length == 0)
            return;
        $grid.DataTable().columns.adjust();
        if (!$(e.target).parents('ul').hasClass('nav-submenu'))
            return;
        var gridId = $grid.attr('id');
        var grid = w(gridId);
        var formId = findFormId(gridId);
        var form = getFormModel(formId);
        if (form.childSelected)
            form.childSelected(grid);
    });

    initColumnLink();
    initCache(function () {
        initIndexForm();
        initWidgets();
        initMainMenu($(document));
        initMainTab($(document));
        if (dx.user.isOutPage != 1)
            initIndex(dx.cache.form['index']);
    }, true);

    // set components height
    function resize() {
        var height = $(window).height();
        // var wht = height - 70;
        var wht = height - 60;
        if (!$(".container").parents(".modal")) {    //fy加
            $(".container").height(height - 2);
        }
        $(".dx-main-menu-entry").height(wht);
//		$(".dx-main-tab-content").height(wht - nht - fht - 2 + 35);
        $(".dx-main-tab-content").height(height - 118);
    }

    var nht = $(".nav-tabs").height();
    // var fht = $(".footer").height() + 10; //原来的
    resize();
    $(window).resize(resize);
});

$(document).on('blur', 'input', function () {
    var result = $(this).val()
    if (!isEmpty(result) && result.split('\\').length == 1) {
        result = result.replace(/(^\s*)|(\s*$)/g, "");
        $(this).val(result);
    }
});


function tabChangeShow(name, $this) {
    var divs = $($this).parent().parent().next().children('div');
    if (divs == null || divs == undefined || divs.length < 0)
        return;
    for (var i = 0; i < divs.length; i++) {
        var divName = $(divs[i]).attr('name');
        if (name == divName) {
            $(divs[i]).addClass('active');
        } else {
            $(divs[i]).removeClass('active');
        }
    }
}
//每一个块的审批事件参数解析
function buildBlockEventParam(event, data, isIndex, isList){
    if (isEmpty(event))
        return;
    var eventToDb = {};
    eventToDb.condition=event.condition;
    eventToDb.table_id = event.table_id;
    eventToDb.block_id = event.block_id;
    eventToDb.event_type = event.event_type;
    eventToDb.flow_event_id = event.flow_event_id;
    eventToDb.data_id = event.data_id;
    eventToDb.email = event.email;
    eventToDb.sms = event.sms;
    if (isEmpty(event) || isEmpty(event.event))
        return eventToDb;
    eventToDb.event = {};
    var events = event.event;
    for (var key in events){
        eventToDb.event[key] = {};
        eventToDb.event[key].requestParam = buildApiJsonParam(events[key].paseParam, data, isIndex, isList);
        eventToDb.event[key].event_id = events[key].event_id;
        eventToDb.event[key].event_name = events[key].event_name;
        eventToDb.event[key].event_type = events[key].event_type;
        eventToDb.event[key].is_using = events[key].is_using;
        eventToDb.event[key].table_action_id = events[key].table_action_id;
    }
    return eventToDb;
}
//总体的审批事件参数解析
function buildEventParam(table, type, form){
    if (isEmpty(table))
        return;
    if (!isEmpty(table) && !isEmpty(table.approveEvent) &&
            !isEmpty(table.approveEvent[type])){
        var blockEvents = table.approveEvent[type];
        var flowEvent = [];
        for (var i=0; i<blockEvents.length; i++){
            if (!isEmpty(blockEvents[i].paseParam)){
                blockEvents[i].requestParam = buildApiJsonParam(blockEvents[i].paseParam, form);
            }
            flowEvent.push(blockEvents[i]);
        }
        return flowEvent;
    }
}
function getNameExpression(table, data){
    var name_column = table.name_column;
    var name_expression_publicity = table.name_expression_publicity;
    var name_expression;
    if (!isEmpty(name_expression_publicity)){
        //name_expression = evaluate(name_expression_publicity, data, null, null, true);
        var dataMap = {};
        if (!isEmpty(data) && !isEmpty(data.columns)){
            for(var key in data.columns){
                dataMap[key] = data.columns[key].value;
            }
        }
        name_expression = evaluate(name_expression_publicity, null, null, null, true, dataMap);
    }else{
        if (isEmpty(name_column)){
            if (isEmpty(data.columns))
                name_expression = data[table.idColumns[0]];
            else
                name_expression = data.columns[table.idColumns[0]].value;
        }else{
            if (isEmpty(data.columns)){
                name_expression = data[name_column];
            }else
                name_expression = data.columns[name_column].value;
        }
    }
    return name_expression;
}

function dxReload(callback){
	postJson('/data/cache/reload.do', function(){
		initCache(function(){
			callback();
		}, true)
		})
}

function formatSql(str){
    if (isEmpty(str))
        return str;
    var formatStr = str;
    formatStr = formatStr.replace(/dxf.sql\(\\?['"](.*?)\\?['"]\)/g, function (m, v) {
        var sqlData;
        var flag = false;
        postJson('/data/getSql.do', {sql: v}, function (data) {
            if (isEmpty(data)){
                flag = true;
                return;
            }
            sqlData = data;
        }, null, true);
        if (flag)
            return m;
        return m.replace(v, sqlData);
    });
    return formatStr;
}

function cloneObject(obj){
    if (obj instanceof Object){
        var resultObj = {};
        for (var key in obj){
            if (obj[key] instanceof Object){
                resultObj[key] = cloneObject(obj[key]);
            }else if (obj[key] instanceof Array){
                var resultArray = [];
                for (var i=0; i<obj[key].length; i++){
                    if (obj[key][i] instanceof Object){
                        resultArray.push(cloneObject(obj[key][i]));
                    }else{
                        resultArray.push(obj[key][i]);
                    }
                }
                resultObj[key] = resultArray;
            }else{
                resultObj[key] = obj[key];
            }
        }
        return resultObj;
    }
}
function getCurrentBlockId(form){
    if (!isEmpty(form.approveFlow) && !isEmpty(form.approveFlow.approveFlowNodes)){
        var nodes = form.approveFlow.approveFlowNodes;
        for (var i=0; i<nodes.length; i++){
            if (nodes[i].state == 'wait'){
                return nodes[i].block_id;
            }
        }
    }
}

if (window.Event)
    document.captureEvents(Event.MOUSEUP);
function nocontextmenu(e){
    if (!$(e.target).hasClass('datagrid-cell')){
        return true;
    }
    event.cancelBubble = true;
    event.returnValue = false;
    return false;
}
function norightclick(e){
    if (!$(e.target).hasClass('datagrid-cell')){
        return true;
    }
    if (window.Event){
        if (e.which == 2 || e.which == 3)
            return false;
    }else if (event.button == 2 || event.button == 3){
        event.cancelBubble = true;
        event.returnValue = false;
        return false;
    }
}

document.oncontextmenu = nocontextmenu; // for IE5+
document.onmousedown = norightclick; // for all others



function dataGridMove(dg, row,type){
	//向上移动一行
	if(type=="up"){
		var datagrid = $(dg);  
		for(var i=0;i<row.length;i++){
			var index = datagrid.datagrid("getRowIndex", row[i]);
			datagrid.datagrid('endEdit',index).datagrid('selectRow', index);
			if (isFirstRow(dg, row[i])) {  
		       alert(msg("is_the_first"));
		        return;  
		    }  
		    datagrid.datagrid("deleteRow", index);  
		    datagrid.datagrid("insertRow", {  
		        index : index - 1, // 索引从0开始  
		        row : row[i] 
		    });  
		    datagrid.datagrid("selectRow", index - 1);  
		}
	} 
	    
	//向下移动一行 
	if(type=="down"){
	    var datagrid = $(dg);  
		for(var i=row.length-1;i>-1;i--){
			var index = datagrid.datagrid("getRowIndex", row[i]); 
			datagrid.datagrid('endEdit',index).datagrid('selectRow', index); 
		    if (isLastRow(dg, row[i])) {  
				alert(msg("is_the_last"));
		        return;  
		    }  
		    datagrid.datagrid("deleteRow", index);  
		    datagrid.datagrid("insertRow", {  
		        index : index + 1, // 索引从0开始  
		        row : row[i]  
		    });  
		    datagrid.datagrid("selectRow", index + 1);
		}
	}  
	// 是否是第一条数据 
	function isFirstRow(dg, row) {  
	    var index = $(dg).datagrid("getRowIndex", row);  
	    if (index == 0) {  
	        return true;  
	    }  
	    return false;  
	}  
	// 是否是最后一条数据 
	function isLastRow(dg, row) {  
	    var rowNum = $(dg).datagrid("getRows").length;  
	    var index = $(dg).datagrid("getRowIndex", row);  
	    if (index == (rowNum - 1)) {  
	        return true;  
	    }  
	    return false;  
	}  
}





