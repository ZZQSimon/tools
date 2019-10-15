/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 3/2/15
 * Time: 2:50 PM
 */

"use strict";

var FilterBuilder = function (table, action) {
    this.table = table;
    this.action = action;
    this.filters = {};
    this.dataCount = 0;
};

FilterBuilder.prototype.in = function (name, val) {
    if (val.length === 0) return;
    this.filters[name] = ({value: val, type: 'in'});
};

FilterBuilder.prototype.like = function (name, val) {
    if (isEmpty(val)) return;
    this.filters[name] = ({value: val, type: 'like'});
};

FilterBuilder.prototype.tree = function (name, val) {
    if (isEmpty(val)) return;
    this.filters[name] = ({value: val, type: 'tree'});
};

FilterBuilder.prototype.from = function (name, val, elem) {
    if (isEmpty(val)) return;
    if ((this.table.columnMap[name].data_type === dx.dataType.date) || (this.table.columnMap[name].data_type === dx.dataType.datetime))
        val = elem.datepicker('getDate').getTime();
    var filter = this.filters[name];
    if (filter === undefined)
        this.filters[name] = filter = {type: 'between'};
    filter.from = val;
};

FilterBuilder.prototype.to = function (name, val, elem) {
    if (isEmpty(val)) return;
    if ((this.table.columnMap[name].data_type === dx.dataType.date) || (this.table.columnMap[name].data_type === dx.dataType.datetime))
        val = elem.datepicker('getDate').getTime();
    var filter = this.filters[name];
    if (filter === undefined)
        this.filters[name] = filter = {type: 'between'};
    filter.to = val;
};

FilterBuilder.prototype.range = function (name, val, from, to, elem) {
    if (isEmpty(val)) return;
    if ((this.table.columnMap[name].data_type === dx.dataType.date) || (this.table.columnMap[name].data_type === dx.dataType.datetime))
        val = elem.datepicker('getDate').getTime();
    this.filters[name] = {value: val, type: 'range', from: from, to: to};
};

FilterBuilder.prototype.eq = function (name, val) {
    if (isEmpty(val)) return;
    this.filters[name] = ({value: val, type: 'eq'});
};

FilterBuilder.prototype.hasData = function () {
    return this.dataCount;
};

FilterBuilder.prototype.data = function () {
    return {tableName: this.table.id, filters: this.filters, action: this.action};
};

//TODO this function is legacy, should use filterService#toWhere
FilterBuilder.prototype.toWhere = function () {
    function makeAlias(column) {
        return tableName + '.' + column;
    }

    var tableName = this.table.id;
    var table = this.table;
    var where = null;
    forEach(this.filters, function (column, data) {
        // for new style, not support convert in js, ignore
        if (column.indexOf('__') == 0)
            return;
        var alias = makeAlias(column);
        var condition;
        var from, to;
        switch (data.type) {
            case 'in':
                condition = alias + ' in (\'' + data.value.join('\',\'') + '\')';
                break;
            case 'like':
                condition = alias + ' like \'%' + data.value + '%\'';
                break;
            case 'between':
                if (table.columnMap[column].data_type === dx.dataType.date) {
                    alias = 'convert(varchar(10), ' + alias + ', 20)';
                    if (data.from)
                        from = moment(data.from).format('YYYY-MM-DD');
                    if (data.to)
                        to = moment(data.to).format('YYYY-MM-DD');
                } else if (table.columnMap[column].data_type === dx.dataType.datetime) {
                    alias = 'convert(varchar(20), ' + alias + ', 120)';
                    if (data.from)
                        from = moment(data.from).format('YYYY-MM-DD HH:mm:ss');
                    if (data.to)
                        to = moment(data.to).format('YYYY-MM-DD HH:mm:ss');
                } else {
                    from = data.from;
                    to = data.to;
                }

                if (from && to)
                    condition = alias + ' between \'' + from + '\' and \'' + to + '\'';
                else if (from)
                    condition = alias + ' >= \'' + from + '\'';
                else
                    condition = alias + ' <= \'' + to + '\'';
                break;
            case 'range':
                var val;
                if (table.columnMap[column].data_type === dx.dataType.date) {
                    from = 'convert(varchar(10), ' + data.from + ', 20)';
                    to = 'convert(varchar(10), ' + data.to + ', 20)';
                    val = moment(data.value).format('YYYY-MM-DD');
                } else if (table.columnMap[column].data_type === dx.dataType.datetime) {
                    from = 'convert(varchar(20), ' + data.from + ', 120)';
                    to = 'convert(varchar(20), ' + data.to + ', 120)';
                    val = moment(data.value).format('YYYY-MM-DD HH:mm:ss');
                } else {
                    val = data.value;
                    from = data.from;
                    to = data.to;
                }
                condition = '\'' + val + '\' between ' + from + ' and ' + to;
                break;
            case 'eq':
                if (typeof data.value === 'boolean')
                    condition = alias + ' = ' + (data.value ? 1 : 0);
                else
                    condition = alias + ' = \'' + data.value + '\'';
                break;
            default :
                dxError('not supported filter type');
        }
        if (where === null)
            where = condition;
        else
            where += ' and ' + condition;
    });
    return where;
};

/**
 * init filter component in $container
 *
 * @param form
 * @param filterCallback
 * @param [resetCallback] reset callback
 * @param [opts]
 */
function initFilter(form, filterCallback, resetCallback, opts) {
    function collapseFilters() {
        $span.addClass('dx-filter-open').removeClass('dx-filter-close');
        $switch.data('expand', false);
        $container.find('.dx-filter').each(function () {
            var $this = $(this);
            var desc = table.columnMap[$this.attr('name')];
            if (!desc.is_condition)
                $this.closest('div.form-group').hide();
        });
    }

    function expandFilters() {
        $span.removeClass('dx-filter-open').addClass('dx-filter-close');
        $switch.data('expand', true);
        $container.find('.dx-filter').closest('div.form-group').show();
    }

    function setInputSelectorFilter(filters) {
        var extra = {};
        forEach(filters, function (column, data) {
            if (column.indexOf('__query') === 0) {
                extra[table.idColumns[0]] = data;
                return;
            }
            var desc = getColumnDesc(tableName, column);
            var $field = $container.find(':input[name="' + column + '"]');
            if (desc.data_type === dx.dataType.string) {
                if (desc.dic_id)
                    $field.multiselect('disable');
            } else if (!desc.ref_table_name)
                enableItem($field, false);
        });
        return extra;
    }

    if (resetCallback != undefined && typeof resetCallback != 'function') {
        opts = resetCallback;
        resetCallback = null;
    }

    var $container = form.get().find('.dx-filter-form');
    var tableName = form.filter.table;
    var table = getTableDesc(tableName);

    var $switch = $container.find('button.dx-filter-switch');
    var $span = $switch.find('span');
    collapseFilters();

    $container.find('input.dx-filter-date').datepicker(datePickerInitPara);
    $container.find('input.dx-filter-datetime').datepicker(datePickerInitPara);
    $container.find('input.dx-filter-digits').autoNumeric('init');
    $container.find('input.dx-filter-number').autoNumeric('init');
    $container.find('select.dx-filter-dict').each(function () {
        $(this).multiselect($.extend({}, defaultMultiselectOptions, {
            //buttonWidth: $(this).parent().width() - 1 + "px",
            includeSelectAllOption: true
        }));
    });

    // dont init validate, module will do it self
    if ($container.is('form'))
        $container.validate(defaultValidateOptions);
    else if (form.get().is('form'))
        form.get().validate(defaultValidateOptions);
    // init filter model
    w(form.filter);
    form.filter.var = function (name, ignoreMapping) {
        var $form = this.get();
        var $field = $form.find('input[name="' + name + '"]');
        if ($field.length === 0)
            return;
        var id = $field.attr('id');
        var ret;
        if (isEmpty(id))
            ret = $field.val();
        else
            ret = w(id).val();
        if (!isEmpty(ret))
            return ret;
    };
    form.filter.getField = function (name) {
        var $form = this.get();
        var $field = $form.find('input[name="' + name + '"]');
        if ($field.length === 0)
            return;
        var id = $field.attr('id');
        return w(id);
    };
    form.filter.build = function (action) {
        var fb = new FilterBuilder(table, action || 'exec');
        $container.find('.dx-filter').each(function () {
            var $this = $(this);
            var column = $this.attr('name');
            var desc = table.columnMap[column];
            if (isEmpty($this.val()) && (!desc.dic_id || ($this.find(':checked').length == 0)))
                return;
            //添加表原有字段作为查询条件
            switch (desc.data_type) {
                case dx.dataType.string:
                    if (desc.dic_id) {
                        if (form.filter.mode === 'match')
                            fb.eq(column, w(this.id).val());
                        else
                            fb.in(column, $(this).val());
                        break;
                    } else if (desc.ref_table_name && desc.refTree) {
                        fb.tree(column, w(this.id).val());
                        break;
                    } else if (desc.ref_table_name || desc.format) {
                        if ($this.hasClass('dx-input-select-multiple'))
                            fb.in(column, w(this.id).val());
                        else {
                            //fb.eq(column, w(this.id).val());
                            fb.in(column, new Array(w(this.id).val()));
                        }
                        break;
                    }
                case dx.dataType.link:
                case dx.dataType.email:
                case dx.dataType.auto:
                    fb.like(column, $this.val());
                    break;
                case dx.dataType.digits:
                case dx.dataType.number:
                    if ($this.hasClass('dx-filter-start'))
                        fb.from(column, $this.autoNumeric('get'));
                    else
                        fb.to(column, $this.autoNumeric('get'));
                    break;
                case dx.dataType.date:
                case dx.dataType.time:
                case dx.dataType.datetime:
                    if ($this.hasClass('dx-filter-range')) {
                        var names = table.valid_date_cols.split(',');
                        fb.range(column, $this.val(), names[0], names[1], $this)
                    } else if ($this.hasClass('dx-filter-start'))
                        fb.from(column, $this.val(), $this);
                    else
                        fb.to(column, $this.val(), $this);
                    break;
                case dx.dataType.boolean:
                    switch ($this.val()) {
                        case '1':
                            fb.eq(column, true);
                            break;
                        case '0':
                            fb.eq(column, false);
                            break;
                        case '2':
                            break;
                    }
                    break;
            }
            fb.dataCount++;
        });
        
        forEach(extraFilters, function (column, data) {
            fb.filters[column] = data;
            fb.dataCount++;
        });
        //添加左侧查询条件
        var leftGroupColumnName = form.get().find('.group-column-name').val();
        if (!isEmpty(leftGroupColumnName)) {
            var nodes = form.get().find('.grid-left-select').jstree("get_checked");
            //查询条件与左侧树条件合并
            if (isEmpty(fb.filters[leftGroupColumnName])) {
                fb.in(leftGroupColumnName, nodes);
            } else {
                var value = fb.filters[leftGroupColumnName].value;
                for (var i = 0; i < nodes.length; i++) {
                    value.push(nodes[i]);
                }
            }
        }
        //添加search 作为查询条件。grid_search
        var searchVal = form.get().find('.grid_search').val();
        fb.like('search', searchVal);
        return fb;
    };
    
    form.filter.mobileBuild = function (action){
    	var fb = new FilterBuilder(table, action || 'exec');
    	forEach(extraFilters, function (column, data) {
            fb.filters[column] = data;
            fb.dataCount++;
        });
    	return fb;
    }

    form.filter.hide = function () {
        form.get().find('.dx-filter-form').hide();
    };

    initInputSelects(form.filter, {selector: 'input.dx-input-select'});

    var extraFilters = {};
    if (form.action === 'select') {
        var filters;
        if (!isEmpty(w(form.parent)))
            filters = w(form.parent).p('filters');
        if (filters)
            extraFilters = setInputSelectorFilter(filters);
    }
    $switch.data('expand', false);
    $switch.on('click', function () {
        if ($switch.data('expand'))
            collapseFilters();
        else
            expandFilters();
        autoExpand(form);
    });
    $container.find('button.dx-filter-reset').on('click', function () {
        $container[0].reset();
        $container.find('select.dx-filter-dict:enabled').multiselect("clearSelection").multiselect('refresh');
        //清除左侧树选中节点。
        form.get().find('.grid-left-select').jstree("uncheck_all");
        if (resetCallback)
            resetCallback();
    });
    $container.find('button.dx-filter-button').on('click', function () {
        if ($container.is('form') && (!$container.valid()))
            return;
        else if (form.get().is('form') && (!form.get().valid()))
            return;

        var fb = form.filter.build(this.value);

        collapseFilters();
        autoExpand(form);

        if (!opts || !opts.noProcessing);
           // dx.processing.open("Searching");
        filterCallback.call(fb, fb.toWhere(), fb.data());
    });
    //全文search
    var to = false;
    var $gridSearch = form.get().find('.grid_search');
    $gridSearch.keydown(function (e) {
        if (e.keyCode == "13") {//keyCode=13是回车键
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                if ($container.is('form') && (!$container.valid()))
                    return;
                else if (form.get().is('form') && (!form.get().valid()))
                    return;
                var fb = form.filter.build(this.value);
                collapseFilters();
                autoExpand(form);
                if (!opts || !opts.noProcessing)
                    dx.processing.open("Searching");
                filterCallback.call(fb, fb.toWhere(), fb.data());
            }, 250);
        }
    });
    //搜索按钮点击事件
    form.get().find('.grid_search_button').on('click', function () {
        if ($container.is('form') && (!$container.valid()))
            return;
        else if (form.get().is('form') && (!form.get().valid()))
            return;
        var fb = form.filter.build(this.value);
        collapseFilters();
        autoExpand(form);
        if (!opts || !opts.noProcessing)	
            dx.processing.open("Searching");
        filterCallback.call(fb, fb.toWhere(), fb.data());
    });
    //树节点点击事件。选择则查询。
    form.get().find('.grid-left-select').on("changed.jstree", function (e, data) {
        if (isEmpty(data) || isEmpty(data.node)){
            return;
        }
        if ($container.is('form') && (!$container.valid()))
            return;
        else if (form.get().is('form') && (!form.get().valid()))
            return;
        var fb = form.filter.build(this.value);
        collapseFilters();
        autoExpand(form);
        if (!opts || !opts.noProcessing)
            dx.processing.open(msg("Searching"));
        filterCallback.call(fb, fb.toWhere(), fb.data());
    });
}

function initGridLeftSelect(form, leftSelectCallback, selectInitCallback) {
    function ajaxLoadGroup(form, columnName, callBack, isFilter) {
        var column;
        if (typeof columnName === 'string') {
            column = columnName;
        } else {
            column = columnName.default_group_column;
        }
        postJson('/leftSelect/gridLeftSelect.do', {
            id: form.id,
            tableName: form.tableName,
            column: column,
            leftSelectFilters: form.leftSelectFilters
        }, function (result) {
            callBack(result);
        }, null, true);
    }

    function initTree(){
        $container.jstree({
            'core': {
                "multiple": true,
                'data': null,
                'dblclick_toggle': true          //tree的双击展开
            },
            "plugins": ["search", "checkbox"]
        }).on('redraw.jstree', function (e, data) {
            selectInitCallback();
        });
    }
    function callback(result) {
        if (!isEmpty(result)){
            if (isEmpty(result.leftNodes)) {
                $container.jstree(true).settings.core.data = null;
                $container.jstree(true).refresh();
            } else {
                $container.jstree(true).settings.core.data = result.leftNodes;
                $container.jstree(true).refresh(true, true);  //refresh 第二个true应用节点选中。不加节点选中会被取消。
            }
        }else{
            selectInitCallback();
        }
    }

    var $form = form.get();
    var $container = $form.find('.grid-left-select');
    var table = getTableDesc(form.tableName);
    var $groupContainer = $form.find('.left-group');
    //设置默认分组中文到页面
    if (!isEmpty(table.default_group_column)) {
        $groupContainer.text(i18n(getColumnDesc(table.id, table.default_group_column).i18n));
        form.get().find('.group-column-name').val(table.default_group_column);
    } else {
        //未设置默认分组
        $groupContainer.text(msg("defaultGroup"));

        $form.find(".dx-left-clear-Group").find(".fa").removeClass("fa-rotate-180");
        $form.find('.dx-grid-left-select').removeClass("open");
    }
    initTree();
    ajaxLoadGroup(form, table, callback, true);

    var $group = $form.find('.dx_grid_left_group');
    $groupContainer.on('click', function () {
        var columns = table.columns;
        if ($group.find('a').length == 0) {
            var userColumns;
            if (!isEmpty(form.grid) && !isEmpty(form.grid.userColumns)){
                userColumns = form.grid.userColumns[table.id];
            }
            //字典与关联表设置到左分组。只有在用户自定义列中存在的列才显示。
            for (var i = 0; i < columns.length; i++) {
                if (!isEmpty(columns[i].dic_id) || !isEmpty(columns[i].ref_table_name)) {
                    if (isEmpty(userColumns)){
                        $group.append('<a href="#" class="dx-left-groupColumn" value="' +
                            columns[i].column_name + '">' + i18n(columns[i].i18n) + '</a>');
                    }else {
                        for (var j=0; j<userColumns.length; j++){
                            if (columns[i].column_name == userColumns[j].column_name){
                                $group.append('<a href="#" class="dx-left-groupColumn" value="' +
                                    columns[i].column_name + '">' + i18n(columns[i].i18n) + '</a>');
                                break;
                            }
                        }
                    }
                }
            }
            $group.find('.dx-left-groupColumn').bind('click', function () {
                var columnCn = $(this).text();
                $groupContainer.text(columnCn);
                var columnName = $(this).attr('value');
                $form.find('.group-column-name').val(columnName);
                $group.hide();     //如果元素为显现,则将其隐藏
                ajaxLoadGroup(form, columnName, callback, false);
            })
        }
        if ($group.is(":hidden")) {
            $group.show();    //如果元素为隐藏,则将它显现
        } else {
            $group.hide();     //如果元素为显现,则将其隐藏
        }
    });

    // form.get().find('.dx-left-clear-Group a').on('click', function () {
    //清除左侧树选中节点。

    // });
    //表格宽度自适应
    function autoDatagrid(formselect) {
        formselect.find(".datagrid").css("width", "100%");
        formselect.find(".datagrid-wrap").css("width", "100%");
        formselect.find(".datagrid-view").css("width", "100%");

        var width = formselect.find(".datagrid-wrap").width() - formselect.find(".datagrid-view1").width();
        formselect.find(".datagrid-wrap").find(".datagrid-view2").css("width", width);
        formselect.find(".datagrid-wrap").find(".datagrid-header").css("width", width);
        formselect.find(".datagrid-wrap").find(".datagrid-body").css("width", width);
        // formselect.find(".datagrid-wrap").find(".datagrid-btable").css("width", width-18);
    }

    $form.find('.dx-left-clear-Group').on('click', function () {
        var $left_select = $form.find('.dx-grid-left-select');
        var $right_data = $form.find(".dx-auto-expand .dx-grid-right-grid");

        if (!$left_select.hasClass("open")) {//打开
            $container.jstree('open_all');
            $left_select.addClass("open");
            $form.find(".dx-left-clear-Group").find(".fa").addClass("fa-rotate-180");
            // $right_data.addClass("col-xs-10");
        } else {//关闭
            $form.find(".dx-left-clear-Group").find(".fa").removeClass("fa-rotate-180");
            $left_select.removeClass("open");
            // $right_data.removeClass("col-xs-10");
            $form.find('.grid-left-select').jstree("uncheck_all");
        }
        // autoDatagrid($("#"+form.id));
        $form.find("table.dx-grid").datagrid("resize");
    });

    //默认打开左分组
    if (form.leftSelect){
        $form.find('.dx-left-clear-Group').click();
    }
    $form.find('.more-select').on('click', function () {
        var height;
        var $dxFilterSelect = $form.find('.dx-filter-select');
        var initheight = $form.find(".dx-auto-expand").height();
        if ($dxFilterSelect.is(":hidden")) {
            $(this).addClass("active");
            $dxFilterSelect.show();

            height = initheight - $dxFilterSelect.height();
            $form.find(".dx-auto-expand").height(height);
        } else {
            $(this).removeClass("active");
            $dxFilterSelect.hide();

            height = initheight + $dxFilterSelect.height();
            $form.find(".dx-auto-expand").height(height);
        }
        $form.find("table.dx-grid").datagrid("resize");
    });
    //左侧search
    var to;
    var leftSearch = $form.find('.dx-grid-left-group-filter input');
    leftSearch.keyup(function () {
        if (to) {
            clearTimeout(to);
        }
        to = setTimeout(function () {
            $container.jstree(true).search(leftSearch.val());
        }, 250);
    });
}
