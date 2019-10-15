/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 16-12-30
 * Time: 上午11:37
 */

"use strict";

/**
 * init all extended widgets for DX
 */
var defaultMultiselectOptions;
/**
 * init date input in container
 *
 * @param container
 */
var datePickerInitPara = {
    format: "yyyy-mm-dd",
    autoclose: true
};


function initWidgets() {
    $.widget("dx.inputSelect", $.ui.autocomplete, {
        _renderItem: function (ul, item) {
            var $li = $('<li/>');
            var $a = $('<a style="padding:0;"/>').appendTo($li);
            if (item.display) {
                $('<span class="input-select-value"/>').text(item.label).appendTo($a);
            } else {
                $('<span class="input-select-value"/>').text(item.value).appendTo($a);
                if (item.label)
                    $('<span class="input-select-label"/>').text(item.label).appendTo($a);
            }
            return $li.appendTo(ul);
        },
        _renderMenu: function (ul, items) {
            var cnt = this.options.dict ? items.length : Math.min(items.length, dx.sys.input_selector_lines);
            for (var i = 0; i < cnt; i++)
                if (items[i].value != null)
                    this._renderItemData(ul, items[i]);
            if (this.options.dict)
                return;
            var li;
            var name = getColumnText(getDescById(this.element.attr('id')));
            // add select menu
            li = $('<li />').data('ui-autocomplete-item', {subMenu: menuType.more});
            $('<a class="input-select-menu"/>').attr('href', '#').text(msg('View_More', name)).appendTo(li);
            li.appendTo(ul);
            // add create menu
            // no create menu if input within a dialog
            if (!this.options.new || this.element.closest('.modal-dialog').length)
                return;
            li = $('<li />').data('ui-autocomplete-item', {subMenu: menuType.create});
            $('<a class="input-select-menu"/>').attr('href', '#').text(msg('Add_New', name)).appendTo(li);
            li.appendTo(ul);
        },
        _resizeMenu: function () {
            var $ul = this.menu.element;
            var labelMax = 0;
            var $labels = $ul.find('.input-select-label');
            $labels.each(function () {
                var w = $(this).outerWidth();
                if (w > labelMax)
                    labelMax = w;
            });
            $labels.outerWidth(labelMax + 6);

            var valueMax = 0;
            var $values = $ul.find('.input-select-value');
            $values.each(function () {
                var w = $(this).outerWidth();
                if (w > valueMax)
                    valueMax = w;
            });
            $values.outerWidth(valueMax + 6);
            $ul.outerWidth(Math.max(
                valueMax + labelMax + 14,
                this.element.outerWidth()
            ));
        }
    });

    // vertical group button here
    $.fn.buttonsetv = function () {
        return this.each(function () {
            $(this).buttonset();
            $(this).css({'display': 'table', 'margin-bottom': '7px'});
            $('.btn', this).css({'margin': '0px', 'display': 'table-cell'}).each(function () {
                if (!$(this).parent().is("div.dummy-row")) {
                    $(this).wrap('<div class="dummy-row" style="display:table-row; " />');
                }
            });
            $('.btn:first', this).first().removeClass('ui-corner-left').addClass('ui-corner-top');
            $('.btn:last', this).last().removeClass('ui-corner-right').addClass('ui-corner-bottom');
        });
    };

    // add valid method dict and input-select
    $.validator.addMethod('dx-input-select', function (value, element) {
        var field = w(element.id);
        return field.valid(value);
    }, msg('incorrect value'));

    $.validator.addMethod('dx-dict-select', function (value, element) {
        if (isEmpty(value))
            return true;
        var field = w(element.id);
        var desc = getDesc(field);
        var dict = dx.dict[desc.dic_id];
        var ret = false;
        $.each(dict, function (k, v) {
            if (value === v[dx.user.language_id]) {
                ret = true;
                field.value = k;
                return false;
            }
        });
        /*if (dx.user.isMobileLogin == 1) {
            $('#' + element.id).blur(function () {
                $(this).select("close");
            });
        }*/
        return ret;
    }, msg('incorrect value'));

    $.validator.addMethod('dx-input-with-format', function (value, element) {
        if (isEmpty(value))
            return true;
        return $(element).inputmask("isComplete");
    });
    $.validator.addMethod('dx-date', function (value, element) {
        if (isEmpty(value))
            return true;
        if ($(element).datepicker('getDate') == null)
            return true;
        return !isNaN($(element).datepicker('getDate').getTime());
    }, msg('incorrect value'));
    $.validator.addMethod('dx-complex', complexFieldValidator, msg('incorrect value'));

    //todo internationalization for comma as point
    $.validator.methods.max = function (value, element, param) {
        var val = unformatNumber(value);
        return this.optional(element) || (val <= param);
    };
    $.validator.methods.min = function (value, element, param) {
        var val = unformatNumber(value);
        return this.optional(element) || (val >= param);
    };
    $.validator.methods.range = function (value, element, param) {
        var val = unformatNumber(value);
        return this.optional(element) || (val >= param[0] && val <= param[1]);
    };
    datePickerInitPara.language = 'zh-CN';

    $.extend($.fn.dataTable.defaults, {
        language: {
            processing: msg('dt.processing'),
            lengthMenu: msg('dt.lengthMenu'),
            zeroRecords: msg('dt.zeroRecords'),
            info: msg('dt.info'),
            infoEmpty: msg('dt.infoEmpty'),
            infoFiltered: msg('dt.infoFiltered'),
            infoPostFix: '',
            search: msg('dt.search'),
            emptyTable: msg('dt.emptyTable'),
            loadingRecords: msg('dt.loadingRecords'),
            infoThousands: msg('dt.infoThousands'),
            paginate: {
                first: msg('dt.first'),
                previous: msg('dt.previous'),
                next: msg('dt.next'),
                last: msg('dt.last')
            },
            aria: {
                sortAscending: msg('dt.sortAscending'),
                sortDescending: msg('dt.sortDescending')
            }
        }
    });

    defaultMultiselectOptions = {
        nonSelectedText: msg('None Selected'),
        selectAllText: msg('Select all'),
        allSelectedText: msg('All selected'),
        nSelectedText: msg('selected')
    };

    $.valHooks.text = {
        get: function (elem) {
            var $elem = $(elem);
            if ($elem.hasClass('dx-input-map')) {
                var field = w(elem.id);
                return field.value;
            }
        }
    };
}

function initOpts(defaultValue, opts) {
    if (opts === undefined)
        return defaultValue;
    else
        return $.extend({}, defaultValue, opts);
}
/**
 * init all input select control in container
 *
 * @param form
 * @param [opts]
 */
// default selector not include field in filter form
var defaultInputSelectInitPara = {selector: 'input.dx-input-select.dx-field'};

function initInputSelects(form, opts) {
    function buildFilterData(filters, filterForm) {
        var ret = {};
        forEach(filters, function (column, data) {
            var filter = ret[column] = {type: data.type};
            if (data.value)
                if (Array.isArray(data.value)) {
                    var values = [];
                    data.value.forEach(function (value) {
                        values.push(evaluate("'" + value + "'", filterForm));
                    });
                    filter.value = values;
                } else
                    filter.value = evaluate("'" + data.value + "'", filterForm);
            if (data.from)
                filter.from = evaluate("'" + data.from + "'", filterForm);
            if (data.to)
                filter.to = evaluate("'" + data.to + "'", filterForm);
        });
        return ret;
    }

    function fetchInputSelectReference(form, field, val) {
        var desc = getDesc(field);
        var data = {id: field.id, text: field.ref_id};
        var uri;
        //TODO support formula
        if (desc.refFilter) {
            data.filters = buildFilterData(desc.refFilter, form);
            uri = '/widget/inputSelect/reference.do';
        } else if (desc.ref_table_sql) {
            data.param = buildSQLParam(form, desc.ref_table_sql);
            uri = '/widget/inputSelect/custom_reference.do';
        } else
            uri = '/widget/inputSelect/reference.do';
        postJson(uri, data, function (data) {
            field.ref = data;
        }, null, true);
    }

    var $form = form.get();
    var arg = {
        delay: 500,
        autoFocus: true,
        minLength: 0,
        select: function (event, ui) {
            var field = w(this.id);
            event.preventDefault();
            if (ui.item.subMenu) {
                var desc = getDesc(field);
                switch (ui.item.subMenu) {
                    case menuType.more:
                        if (dx.user.isMobileLogin == 1) {
                            mobileListDialog({
                                url: '/list/table.view',
                                data: {
                                    parent: this.id,
                                    table: desc.ref_table_name,
                                    action: 'select',
                                    defaultFilters: field.p('filters')
                                },
                                update: function (data) {
                                    field.updated(data);
                                }
                            });
                        } else {
                            showDialogForm({
                                url: '/list/table.view',
                                data: {
                                    parent: this.id,
                                    table: desc.ref_table_name,
                                    action: 'select',
                                    defaultFilters: field.p('filters')
                                },
                                title: '',
                                class: 'dx-input-selector-dialog more-select-dialog',
                                needAutoExpand: true,
                                shown: function (listForm, dialog) {
                                    listForm.close = function (data) {
                                        dialog.close();
                                        field.updated(data);
                                    }
                                }
                            });
                        }
                        break;
                    case menuType.create:
                        newTab('/detail/create.view', {
                            parent: this.id,
                            table: desc.ref_table_name
                        });
                        break;
                }
                return;
            }
            var key = event.keyCode;
            if (key && ((key == 13) || (key == 8))) {
                field.ref_id = ui.item.value;
                field.val(ui.item.label, ui.item.value);
            } else {
                field.ref_id = ui.item.value;
                field.val(ui.item.label, ui.item.value);
            }
            field.modify();
        },
        source: function (request, response) {
            var field = w(this.element[0].id);
            var desc = getDesc(field);
            var data = {id: field.id};
            data.text = request.term;
            var ref_table = getTableDesc(desc.ref_table_name);
            if (desc.refFilter) {
                data.filters = buildFilterData(desc.refFilter, form);
                field.p('filters', data.filters);
            } else if (desc.ref_table_sql) {
                data.param = buildSQLParam(form, desc.ref_table_sql);
                var sql = 'select T.' + ref_table.idColumns[0] + ' from (' + desc.ref_table_sql + ') T';
                field.p('filters', {__query: {'type': 'query', value: data.param, sql: sql}})
            }
            postJson('/widget/inputSelect/filter.do', data, function (data) {
                var resp = [];
                var id_column = getTableIdColumn(ref_table);
                var name_expression;
                $.each(data, function (i, v) {
                    name_expression = v.ref____name_Expression;
                    resp.push({
                        label: name_expression ? name_expression : null,
                        value: v[id_column],
                        display: true
                    })
                });
                if (resp.length == 0) {
                    resp.push({
                        label: 'no data',
                        value: null
                    })
                }
                response(resp);
            }, null, true);
        }
    };

    var doSearch = function () {
        var $tag = $(this);
        var field = w(this.id);
        var desc = getDesc(field);
        if (dx.user.isMobileLogin == 1) {
        	$tag.inputSelect('search', $tag.val());
            mobileListDialog({
                url: '/list/table.view',
                data: {
                    parent: this.id,
                    table: desc.ref_table_name,
                    action: 'select',
                    defaultFilters: field.p('filters')
                },
                update: function (data) {
                    field.updated(data);
                }
            });
        } else {
            $tag.inputSelect('search', $tag.val());
        }
    };

    function initTreeSelector(field, desc) {
        var $input = field.get();
        var $tree = $input.parent().find('.dx-input-select-tree').jstree({
            core: {
                multiple: false,
                data: function (obj, cb) {
                    if (field.root === undefined) {
                        cb([]);
                        return;
                    }
                    postJson('/widget/tree/load.do', {
                        id: field.id,
                        node: field.root,
                        type: $input.hasClass('dx-filter') ? 'filter' : 'edit'
                    }, function (records) {
                        cb(records);
                    });
                }
            },
            search: {
                case_insensitive: true,
                show_only_matches: true,
                show_only_matches_children: false
            },
            conditionalselect: function (node) {
                if (form.widgetName === 'Filter') return true;
                dx.formula.context('tree', {node: node});
                return !desc.refTree.valid || evaluate(desc.refTree.valid, form);
            },
            plugins: ['search', 'conditionalselect']
        });
        $tree.on('select_node.jstree', function (e, data) {
            // ignore selection by api calling
            if (!data.event) return;
            var id = data.node.id;
            $input.dropdown('toggle');
            field.val(id);
            field.modify();
        });
        var jt = field.jt = $tree.jstree(true);

        $input.dropdown();
        var $div = $input.parents('div.dropdown');
        var $ul = $div.find('.dropdown-menu').on('click', function (e) {
            e.stopPropagation();
        });
        $div.on('show.bs.dropdown', function () {
            // reload on shown
            var width = $input.parents('div.form-group').find('input.dx-input-select-name').outerWidth() + $input.outerWidth();
            $tree.width(width);
            $ul.css('top', $input.outerHeight() + "px");

            var nid;
            if (desc.refTree.root)
                nid = evaluate(desc.refTree.root, form);
            else
                nid = '#';
            if (field.root !== undefined && field.root === nid)
                return;
            field.root = nid;
            jt.deselect_all();
            jt.refresh();
        });
        $input.on('blur', function (e) {
            if (isEmpty($input.val()))
                field.val('');
        }).on('keydown', function (e) {
            if (e.which == 9)
                $div.removeClass('open');
            else
                $div.addClass('open');
        }).on('keyup', function () {
            var str = $(this).val();
            jt.search(str);
        });

        field.resetTree = function () {
            field.val('');
            jt.deselect_all();
        };
    }

    function valid(value) {
        var field = this;
        if (isEmpty(value)) {
            field.val('');
            return true;
        }
        var desc = getDesc(field);
        if (desc.refTree) {
            if (desc.refTree.root && (field.orig != field.val())) {
                var root = evaluate(desc.refTree.root, form);
                if (root !== field.root)
                    return false;
                if (isEmpty(field.root))
                    return false;
                var ids = field.jt.get_selected();
                if (ids.length === 0)
                    return false;
                if (ids[0] !== value)
                    return false;
            }
        }
        if (field.ref && (field.val() == value))
            return true;
        //field.val(value);
        return field.ref != null;
    }

    function initMultipleSelector(field, $this, desc) {
        var content = '<table class="input-select-multiple"><tbody>';
        field.keys = [];
        forEach(field.value, function (key, name) {
            field.keys.push(key);
            content += '<tr><td class="input-select-value">' + key + '</td>';
            content += '<td class="input-select-label">' + name + '</td></tr>'
        });
        content += '</tbody></table>';
        $this.popover({
            trigger: 'hover',
            html: true,
            placement: 'bottom',
            // title: getColumnText(desc),
            content: content
        });
        $this.prop('readonly', true);
        field.valid = function () {
            return true;
        };
        field.val = function () {
            return field.keys;
        }
    }

    opts = initOpts(defaultInputSelectInitPara, opts);
    var $inputs = $form.find(opts.selector);
    $inputs.each(function () {
        var field = w(this.id);
        var desc = getDesc(field);

        var $this = $(this);
        if ($this.hasClass('dx-input-select-multiple')) {
            initMultipleSelector(field, $this, desc);
            return;
        }

        arg.appendTo = $this.parent();
        var table = getTableDesc(desc.ref_table_name);
        field.val = function (val, value) {
            //get
            if (val === undefined) {
                if (!field.ref){
                    return $this.val();
                }else{
                    var ref_id = field.ref[getTableIdColumn(table)];
                    if (isEmpty(ref_id)){
                        return field.value;
                    }
                    return ref_id;
                }
            } else if (isEmpty(val)) {
                field.ref = null;
                $this.val('');
                field.$name.val('');
            } else { //set
                if (!isEmpty(value))
                    fetchInputSelectReference(form, field, value);
                else{
                    if (isEmpty(field.ref_id)){
                        field.ref_id = val;
                    }
                    if (isEmpty(field.ref)){
                        //新增时没有关联表数据。需要查出来。
                        fetchInputSelectReference(form, field, val);
                    }
                }
                var name_expression;
                if (!isEmpty(field.ref)){
                    name_expression = field.ref.ref____name_Expression;
                }else{
                    name_expression = field.ref_id;
                }
                $this.val(name_expression);
                //if (field.ref && table.name_expression_publicity) {
                //    $this.val(evaluate(table.name_expression_publicity, null, null, null, true, field.ref));
                //} else if (field.ref && table.name_column)
                //    $this.val(field.ref == null ? '' : field.ref[table.name_column])
            }
        };
        field.text = function () {
            return field.ref == null ? '' : field.ref[getTableNameColumn(table)];
        };
        // update value using created/selected entry
        field.onUpdated = function (data) {
            field.ref_id = data.ref_id;
            field.ref = null;
            field.val(data.ref_id);
            // valid to clear error status and evaluate related fields
            field.modify();
        };
        field.valid = valid;
        if (desc.refTree) {
            initTreeSelector(field, desc);
            field.$name = $this.parent().next();
        } else {
            $this.on('focus', doSearch);
            $this.on('blur', function () {
                if (isEmpty($this.val()))
                    field.val('');
            });
            arg.new = desc.ref_table_new;
            $this.inputSelect(arg);
            field.$name = $this.next();
        }
        if (isEmpty(field.value) || field.ref)
            return;
        // references cannot retrieved by vm, so do it manually
        if (field.p('sync'))
            field.val(field.value);
        else
            setTimeout(function () {
                if (field.p('synced'))
                    return;
                field.val(field.value);
            }, 200);
    });
}

/**
 * init string mode date input
 *
 * @param form
 * @param opts
 */
var defaultInputDateInitPara = {selector: 'input.dx-input-with-format'};
var inputmaskDefinitions = {
    regex: {
        val1pre: new RegExp("[0-1]"),
        val1: new RegExp("0[1-9]|1[012]"),
        val2pre: function (separator) {
            var s = $.inputmask.escapeRegex.call(this, separator);
            return new RegExp('((0[13456789]|1[012])' + s + '[0123])|(02' + s + '[012])');
        },
        val2: function (separator) {
            var s = $.inputmask.escapeRegex.call(this, separator);
            return new RegExp('(((0[13578]|1[02])' + s + '((0[1-9])|([12][0-9])|(3[01])))|((0[469]|11)' + s + '((0[1-9])|([12][0-9])|(30)))|(02' + s + '((0[1-9])|([12][0-9]))))');
        }
    },
    getFrontValue: function (mask, buffer, opts, tag) {
        for (var start = 0, length = 0, i = 0; i < mask.length && tag != mask.charAt(i); i++) {
            var definition = opts.definitions[mask.charAt(i)];
            if (definition) {
                start += length;
                length = definition.cardinality;
            } else
                length++;
        }
        return buffer.join("").substr(start, length);
    },
    definitions: {
        2: {
            validator: function (chrs, maskset, pos, strict, opts) {
                var frontValue = opts.getFrontValue(maskset.mask, maskset.buffer, opts, '2');
                if (frontValue.indexOf(opts.placeholder[0]) >= 0)
                    return false;
                var isValid = opts.regex.val1.test(chrs);
                if (strict || isValid
                    || (chrs.charAt(1) != opts.separator && -1 == "-./".indexOf(chrs.charAt(1)))
                    || !(isValid = opts.regex.val1.test("0" + chrs.charAt(0))))
                    return isValid;
                maskset.buffer[pos - 1] = "0";
                return {
                    refreshFromBuffer: {
                        start: pos - 1,
                        end: pos
                    },
                    pos: pos,
                    c: chrs.charAt(0)
                };
            },
            cardinality: 2,
            prevalidator: [{
                validator: function (chrs, maskset, pos, strict, opts) {
                    isNaN(maskset.buffer[pos + 1]) || (chrs += maskset.buffer[pos + 1]);
                    var frontValue = opts.getFrontValue(maskset.mask, maskset.buffer, opts, '2');
                    if (frontValue.indexOf(opts.placeholder[0]) >= 0)
                        return false;
                    var isValid = 1 == chrs.length ? opts.regex.val1pre.test(chrs) : opts.regex.val1.test(chrs);
                    if (strict || isValid || !(isValid = opts.regex.val1.test("0" + chrs)))
                        return isValid;
                    maskset.buffer[pos++] = "0";
                    return {pos: pos};
                },
                cardinality: 1
            }]
        },
        1: {
            validator: function (chrs, maskset, pos, strict, opts) {
                var month = opts.getFrontValue(maskset.mask, maskset.buffer, opts, '1');
                if (month.indexOf(opts.placeholder[0]) >= 0)
                    return false;
                var isValid = opts.regex.val2(opts.separator).test(month + chrs);
                if (!strict && !isValid
                    && (chrs.charAt(1) == opts.separator || -1 != "-./".indexOf(chrs.charAt(1)))
                    && (isValid = opts.regex.val2(opts.separator).test(month + "0" + chrs.charAt(0)))) {

                    maskset.buffer[pos - 1] = "0";
                    return {
                        refreshFromBuffer: {
                            start: pos - 1,
                            end: pos
                        },
                        pos: pos,
                        c: chrs.charAt(0)
                    };
                }
                var year = opts.getFrontValue(maskset.mask, maskset.buffer, opts, '2');

                if (isValid)
                    return isDate(year + month + chrs);
                return isValid;
            },
            cardinality: 2,
            prevalidator: [{
                validator: function (chrs, maskset, pos, strict, opts) {
                    isNaN(maskset.buffer[pos + 1]) || (chrs += maskset.buffer[pos + 1]);
                    var frontValue = opts.getFrontValue(maskset.mask, maskset.buffer, opts, '1');
                    if (frontValue.indexOf(opts.placeholder[0]) >= 0)
                        return false;
                    var isValid = 1 == chrs.length ? opts.regex.val2pre(opts.separator).test(frontValue + chrs) : opts.regex.val2(opts.separator).test(frontValue + chrs);
                    if (strict || isValid || !(isValid = opts.regex.val2(opts.separator).test(frontValue + "0" + chrs)))
                        return isValid;
                    maskset.buffer[pos++] = "0";
                    return {pos: pos};
                },
                cardinality: 1
            }]
        },
        y: {
            validator: function (chrs, maskset, pos, strict, opts) {
                return opts.isInYearRange(chrs, opts.yearrange.minyear, opts.yearrange.maxyear);
            },
            cardinality: 4,
            prevalidator: [{
                validator: function (chrs, maskset, pos, strict, opts) {
                    var isValid = opts.isInYearRange(chrs, opts.yearrange.minyear, opts.yearrange.maxyear);
                    if (!strict && !isValid) {
                        var yearPrefix = opts.determinebaseyear(opts.yearrange.minyear, opts.yearrange.maxyear, chrs + "0").toString().slice(0, 1);
                        if (opts.isInYearRange(yearPrefix + chrs, opts.yearrange.minyear, opts.yearrange.maxyear)) {
                            maskset.buffer[pos++] = yearPrefix.charAt(0);
                            return {pos: pos};
                        }
                        yearPrefix = opts.determinebaseyear(opts.yearrange.minyear, opts.yearrange.maxyear, chrs + "0").toString().slice(0, 2);
                        if (isValid = opts.isInYearRange(yearPrefix + chrs, opts.yearrange.minyear, opts.yearrange.maxyear)) {
                            maskset.buffer[pos++] = yearPrefix.charAt(0);
                            maskset.buffer[pos++] = yearPrefix.charAt(1);
                            return {pos: pos};
                        }
                    }
                    return isValid;
                },
                cardinality: 1
            }, {
                validator: function (chrs, maskset, pos, strict, opts) {
                    var isValid = opts.isInYearRange(chrs, opts.yearrange.minyear, opts.yearrange.maxyear);
                    if (!strict && !isValid) {
                        var yearPrefix = opts.determinebaseyear(opts.yearrange.minyear, opts.yearrange.maxyear, chrs).toString().slice(0, 2);
                        if (opts.isInYearRange(chrs[0] + yearPrefix[1] + chrs[1], opts.yearrange.minyear, opts.yearrange.maxyear)) {
                            maskset.buffer[pos++] = yearPrefix.charAt(1);
                            return {pos: pos};
                        }
                        yearPrefix = opts.determinebaseyear(opts.yearrange.minyear, opts.yearrange.maxyear, chrs).toString().slice(0, 2);
                        if (isValid = opts.isInYearRange(yearPrefix + chrs, opts.yearrange.minyear, opts.yearrange.maxyear)) {
                            maskset.buffer[pos - 1] = yearPrefix.charAt(0);
                            maskset.buffer[pos++] = yearPrefix.charAt(1);
                            maskset.buffer[pos++] = chrs.charAt(0);
                            return {
                                refreshFromBuffer: {
                                    start: pos - 3,
                                    end: pos
                                },
                                pos: pos
                            };
                        }
                    }
                    return isValid;
                },
                cardinality: 2
            }, {
                validator: function (chrs, maskset, pos, strict, opts) {
                    return opts.isInYearRange(chrs, opts.yearrange.minyear, opts.yearrange.maxyear);
                },
                cardinality: 3
            }]
        }
    }
};

function initInputDate(form, opts) {
    opts = initOpts(defaultInputDateInitPara, opts);
    var $form = form.get();
    $form.find(opts.selector).each(function () {
        var field = w(this.id);
        var desc = getDesc(field);
        var $this = $(this);
        switch (desc.format.type) {
            case 'date':
                var placeholder, mask;
                var format = desc.format.format.replace('YM', 'Y/M').replace('MD', 'M/D').replace('Dh', 'D h').replace('hm', 'h:m').replace('ms', 'm:s');
                placeholder = format.replace('Y', '____').replace('M', '__').replace('D', '__').replace('h', '__').replace('m', '__').replace('s', '__');
                mask = format.replace('Y', 'y').replace('M', '2').replace('D', '1').replace('m', 's');
                $this.inputmask('datetime', $.extend({
                    placeholder: placeholder,
                    mask: mask,
                    oncomplete: function (a, b, c){
                        //字段值改变后修改后台缓存值。
                        postJson('/data/changeModify.do', {id: form ? form.id : null, column: field.column,
                            value: field.val(), currentBlock: getCurrentBlockId(form)}, function (result){
                        }, null, true);
                    }
                }, inputmaskDefinitions));
                field.val = function (val) {
                    if (val === undefined)
                        return $this.inputmask('unmaskedvalue');
                    else
                        $this.val(val);
                };
                break;
            default:
        }
    });
}

/**
 * init datetime input field
 *
 * @param form
 */
function initDatetime(form) {
    form.get().find('input.dx-datetime').datetimepicker({format: 'yyyy-mm-dd hh:ii:ss', autoclose: true});
    form.get().find('input.dx-datetime').each(function () {
        var field = w(this.id);
        var $this = field.get();
        field.val = function (val) {
            //get
            if (val === undefined) {
                if (isEmpty($this.val()))
                    return null;
                var date = $this.data("datetimepicker").getDate();
                var formatted = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
                return formatted;
            } else if (isEmpty(val)) {
                this.value = null;
                $this.val('');
            } else { //set
                this.value = val;
                $this.val(this.text());
            }
        };
        field.text = function () {
            return $.format.date(new Date(this.value), 'yyyy-MM-dd HH:mm:ss');
        };
    });
}

/**
 * init date input field
 *
 * @param form
 * @param [opts]
 */
var defaultDateInitPara = {selector: 'input.dx-date', para: datePickerInitPara};

function initDate(form, opts) {
    opts = initOpts(defaultDateInitPara, opts);
    opts.para.todayHighlight = true;
    form.get().find(opts.selector).datepicker(opts.para).each(function () {
        var $this = $(this);
        if (opts.init)
            opts.init.call(this, $this);
        var field = w(this.id);
        field.val = function (val) {
            //get
            if (val === undefined){
                var date = $this.datepicker('getDate');
                if (!date)
                    return null;
                var formatted = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
                return formatted;
                //return $this.datepicker('getDate') ? $this.datepicker('getDate').getTime() : null;
            }else if (isEmpty(val))
                $this.val('');
            else { //set
                var time = val.toString();
                time = time.replace(/-/g, ':').replace(' ', ':').split(':');

                if (time.length == 1) {
                    $this.datepicker('setDate', new Date(val));
                } else {
                    var dateVal;
                    if (time.length == 1){
                        dateVal = new Date(time[0]);
                    }else if (time.length == 2){
                        dateVal = new Date(time[0], (time[1] - 1));
                    }else if (time.length == 3){
                        dateVal = new Date(time[0], (time[1] - 1), time[2]);
                    }else if (time.length == 4){
                        dateVal = new Date(time[0], (time[1] - 1), time[2], time[3]);
                    }else if (time.length == 5){
                        dateVal = new Date(time[0], (time[1] - 1), time[2], time[3], time[4]);
                    }else if (time.length == 6){
                        dateVal = new Date(time[0], (time[1] - 1), time[2], time[3], time[4], time[5]);
                    }
                    $this.datepicker('setDate', dateVal);
                }
            }
        };
        field.text = function () {
            return $.format.date(new Date(this.value), 'yyyy-MM-dd');
        };

    });
}


/**
 * init dictionary selector
 *
 * @param form
 */
function initDictSelect(form) {
    var arg = {
        delay: 0,
        autoFocus: true,
        minLength: 0,
        dict: true,
        select: function (event, ui) {
            event.preventDefault();
            var field = w(this.id);
            field.val(ui.item.value);
            field.modify();
        }
    };

    function doSearch() {
        var tag = $(this);
        tag.inputSelect('search', tag.val());
    }

    function sortDict(a, b) {
        if (a.value < b.value)
            return -1;
        if (a.value > b.value)
            return 1;
        return 0;
    }

    var $inputs = form.get().find('input.dx-dict-select');
    $inputs.each(function () {
        var $this = $(this);
        var field = w(this.id);
        var desc = getDesc(field);
        var dict = dx.dict[desc.dic_id];
        field.val = function (val) {
            if (val == undefined) {
                var label = $('#' + field.id).val();
                var value = '';
                $.each(dict, function (k, v) {
                    if (label == v[dx.user.language_id])
                        value = k;
                });

                return value;
            } else if (isEmpty(val))
                $this.val('');
            else {
                field.value = val;
                $this.val(i18n(dict[val]));
            }
        };
        field.text = function () {
            return dict[field.value];
        };
        var source = [];
        $.each(dict, function (k, v) {
            source.push({label: v[dx.user.language_id], value: k})
        });
        source.sort(sortDict);
        arg.source = function (request, response) {
            var resp = [];
            var index = 0;
            source.forEach(function (v) {
                v.display = true;
                if ((v.label.indexOf(request.term) >= 0) || (v.value.indexOf(request.term) >= 0))
                    resp.splice(index++, 0, v);
                else
                    resp.push(v);
            });
            response(resp);
        };
        arg.appendTo = $this.parent();
        $(this).inputSelect(arg);
    });

    $inputs.on('focus', doSearch);
}
/**
 * init file upload button
 *
 * @param $file file input tag, jQuery object
 * @param callback callback when upload success
 */
function initFileUploadButton($file, callback) {
    $file.fileupload({
        dataType: 'json ',
        url: makeUrl('/storage/upload.do'),
        autoUpload: true,
        add: function (e, data) {
            data.url = makeUrl('/storage/upload.do');
            progressBox('uploading', data.files[0].name);
            data.submit();
        },
        done: function (e, data) {
            hidePorgressBox();
            checkJsonResult(data.result, function (uuid) {
                alert(uuid);
                callback(uuid);
            });
        }
    });
}

//进入预览页面
function goViewPage(path) {
    window.open(makeUrl("/report/fileview.do?viewUrl=" + path));
}

/**
 * init file/pic fields
 *
 * @param form
 */
function initFileField(form) {
    if (dx.user.isMobileLogin == 1) {
        form.get().find('input.dx-file-input, input.dx-pic-input').parents(".dx-field-container").addClass("mobile-upload");
        form.get().find('.dx-upload-tishi').remove();
    }

    form.get().find('input.dx-file-input, input.dx-pic-input').fileupload({
        dataType: 'json',
        replaceFileInput: false,
        formData: function () {
            return [{name: 'id', value: this.fileInput[0].id}]
        },
        add: function (e, data) {
            data.url = makeUrl('/storage/upload.do');
            progressBox('uploading', data.files[0].name);
            data.submit();
        },
        done: function (e, data) {
            hidePorgressBox();
            checkJsonResult(data.result, function (name) {
                var field = w(e.target.id);
                field.updateValue(name);
            });
        }
    }).each(function () {
        var field = w(this.id);
        field.val = function (val) {
            if (val === undefined)
                return field.value;
            postJson('/widget/file/update.do', {id: field.id, value: val}, function () {
                field.updateValue(val);
            });
            //dxError('set value of file/pic is not supported');
        };

        /*field.updateValue = function (val) {
         field.value = val;
         var desc = getDesc(field);
         var $container = $(field.get()).parents('.dx-field-container');
         if (desc.data_type === 8) {
         $container.find('.dx-upload-label').val(field.text()).removeAttr("type");
         $container.find('.dx-download-a, .dx-preview-a').remove();
         } else {
         var $img = $container.find('img');
         if ($img.length === 0) {
         var $a = $('<a class="dx-preview-a" data-title="' + field.text() + '" ' +
         'data-lightbox="' + field.text() + '" name="' + field.id + '" ' +
         'href="' + makeUrl('/storage/preview.do?id=' + field.id) + '"> ' +
         '<img src="' + makeUrl('/storage/thumbnail.do?id=' + field.id) + '" ' + '' +
         'alt="' + field.text() + '"></a>');
         $a.insertBefore($container.find('span.dx-upload-button'))
         } else {
         var url = $img.attr('src');
         $img.attr('src', url + "&" + new Date().getTime())
         }
         }
         };*/
        field.updateValue = function (val) {
            var filename;
            var viewUrl;
            if (val && val.split("|")) {
                var temp = val.split("|");
                filename = temp[1];
                viewUrl = temp[0];
            } else
                filename = val
            
            field.value = filename==undefined?val:filename;
            var desc = getDesc(field);
            var $container = $(field.get()).parents('.dx-field-container');
            if (desc.data_type === 8) {
                $container.find('.dx-upload-label').val(field.text()).attr("type","hidden");
                $container.find('.dx-download-a, .dx-preview-a').remove();
                // $container.find('.dx-upload-label').val(field.text()).removeAttr("type");
                // $container.find('.dx-download-a, .dx-preview-a,.dx-upload-label').remove();

                // $(this).parents(".dx-upload-div").find(".view_a").remove();
                var $a = $('<a class="dx-download-a" data-title="' + field.text() + '" ' +
                    ' name="' + field.id + '" ' +
                    'href="#" onclick="goViewPage(\'' + viewUrl + '\')"> ' +
                    // '<input value="' + field.text() + '" ' + '' +
                    // 'alt="' + field.text() + '">' +
                    field.text()+'</a>');
                $a.insertBefore($container.find('span.dx-upload-button'))
            } else {
                var $img = $container.find('img');
                if ($img.length === 0) {
                    var $a = $('<a class="dx-preview-a" data-title="' + field.text() + '" ' +
                        'data-lightbox="' + field.text() + '" name="' + field.id + '" ' +
                        'href="' + makeUrl('/storage/preview.do?id=' + field.id) + '"> ' +
                        '<img src="' + makeUrl('/storage/thumbnail.do?id=' + field.id) + '" ' + '' +
                        'alt="' + field.text() + '"></a>');
                    $a.insertBefore($container.find('span.dx-upload-button'))
                } else {
                    var url = $img.attr('src');
                    $img.attr('src', url + "&" + new Date().getTime())
                }
            }
        };
        field.text = function () {
            if (field.value)
                return field.value.substr(field.value.lastIndexOf('/') + 1);
            else
                return '';
        };
        field.enable = function (enable) {
            if (enable)
                field.get().fileupload('enable');
            else{
            	$(field.get()).parents('.dx-field-container').addClass("file-view-class");
            	$(field.get()).parents('.dx-field-container').find(".dx-upload-button").css("display","none");
            	field.get().fileupload('disable');
            }
        }
    });
    form.get().on('click', 'a.dx-download-a', function (e) {
        e.stopPropagation();
        fileDownload("/storage/request.do", {id: $(e.target).prop('name')});
        var param = {};
        param.data = {id: $(e.target).prop('name')};
        postJsonRaw("/storage/request.do", param, function (data) {
            var viewUrl = "";
            var downloadUrl = "";
            if (data.data != null) {
                viewUrl = data.data.viewUrl;
                downloadUrl = data.data.downloadUrl;
            }
            $("body").append("<iframe src='" + makeUrl(downloadUrl) + "' style='display: none;' ></iframe>");
            //window.open(makeUrl("/report/preview.do?viewUrl=" + viewUrl + "&&downloadUrl=" + downloadUrl));
        });
    });
}

/**
 * init number and digits field in $container
 *
 * @param form
 */
function initNumberDigits(form) {
    function max(len, dec) {
        if (!len)
            len = 20;
        if (!dec)
            return charRepeat('9', len);
        else
            return charRepeat('9', len) + '.' + charRepeat('9', dec);
    }

    function formatOpts(desc) {
        //if (!desc.data_format || (desc.data_type === dx.dataType.number))
        //	return {};
        var dotlen = desc.data_format ? evaluate(desc.data_format, form) : 0;
        var m = max(desc.max_len, dotlen);
        return {
            vMax: m,
            vMin: '-' + m
        }
    }

    form.get().find('input.dx-number, input.dx-digits').each(function () {
        var $this = $(this);
        var field = w(this.id);
        var desc = getDesc(field);
        $this.autoNumeric('init', formatOpts(desc));
        field.val = function (v) {
            if (v === undefined)
                return Number($this.autoNumeric('get'));
            else if (v === null)
                $this.autoNumeric('set', "");
            else
                $this.autoNumeric('set', v);
        };
        field.text = function () {
            return $this.val();
        };
        field.resetDataFormat = function () {
            $this.autoNumeric('destroy');
            $this.autoNumeric('init', formatOpts(desc));
        };
        initComplexField(form, desc, field);
    })
}

function mapInputText(field) {
    var size = sizeOf(field.selection);
    if (size == 0)
        return msg('w.map.noneSelected');
    else {
        var selection = field.selection;
        for (var i = 0; i < selection.length; i++) {
            var content = '<div>bbb</div>'
        }
        //var id = Object.keys(field.selection)[0];
        //var name = field.selection[id];
        //return msg('w.map.selected', size, isEmpty(name) ? id : name);
    }
}

var mapInputPopOptions = {
    trigger: 'hover',
    html: true,
    placement: 'bottom',
    container: 'body',
    content: function () {
        var field;
        // in grid
        if (isEmpty(this.id)) {
            var $this = $(this);
            var record = w($this.data('rowid'));
            field = record.columns[$this.data('column')];
        } else// in detail
            field = w(this.id);
        var content = field.popoverContent;
        if (!content) {
            content = '<table class="input-select-multiple"><tbody>';
            forEach(field.selection, function (id, name) {
                content += '<tr><td class="input-select-value">' + id;
                if (isEmpty(name))
                    content += '</td></tr>';
                else
                    content += '</td><td class="input-select-label">' + name + '</td></tr>';
            });
            content += '</tbody></table>';
            field.popoverContent = content;
        }
        return content;
    }
};

/**
 * init map field
 * @param form
 */
function initMapField(form) {
    function buildRemoveClickEvent($parent) {
        $parent.find('.map-selected-param .remove-map-selected').on('click', function () {
            $(this).parent().remove();
        });
    }

    buildRemoveClickEvent(form.get().find('.dx-input-map-div'));
    form.get().find('.dx-input-map-div').on('click', function () {
        function savaSelection(tager) {
            var selection = {};
            var $rows = $(tager).find('.map-selected-param');
            for (var i = 0; i < $rows.length; i++) {
                var key = $($rows[i]).find('input').val();
                var value = $($rows[i]).find('.map-selected-value').text()
                selection[key] = value;
            }
            var fieldID = field.id;
            if (isEmpty(field.value))
                field.val(fieldID);
            else
                field.val(field.value);
            var data = {id: fieldID, selection: {}};
            data.selection = selection;
            postJson('/widget/map/save.do', data, function () {
                var field = w(fieldID);
                field.updated(data.selection);
            });
            field.selection = selection;
        }

        function addSelected(tager, node, buildClickEvent, data) {
            if (isEmpty(data)) {
                var key = $(node).find('span[name="key"]').text();
                var value = $(node).find('span[name="value"]').text();
                if (!isEmpty(key) && !isEmpty(value)) {
                    var appendHtml = '<div class="map-selected-param">' +
                        '<input type="hidden" value="' + key +
                        '"><span class="map-selected-value">' + value + '</span><span class="remove-map-selected">x</span></div>';
                    var $keys = $(tager).find('.map-selected-param input');
                    for (var i = 0; i < $keys.length; i++) {
                        if ($($keys[i]).val() == key)
                            return;
                    }
                    $(tager).append(appendHtml);
                }
            } else {
                var table = getTableDesc(data.table);
                var id_column = table.idColumns[table.idColumns.length - 1];
                var flage = true;
                for (var i = 0; i < data.keys.length; i++) {
                    var key = data.keys[i][id_column];
                    var value = data.keys[i][table.name_column];
                    var $keys = $(tager).find('.map-selected-param input');
                    for (var j = 0; j < $keys.length; j++) {
                        if ($($keys[j]).val() == key) {
                            flage = false;
                            break;
                        }
                    }
                    if (flage) {
                        var appendHtml = '<div class="map-selected-param">' +
                            '<input type="hidden" value="' + key +
                            '"><span class="map-selected-value">' + value + '</span><span class="remove-map-selected">x</span></div>';
                        $(tager).append(appendHtml);
                    }
                    flage = true;
                }
            }
            savaSelection(tager);
            buildClickEvent();
        }

        function loadSelection(url, data, callback) {
            postPage(url, data, function (result) {
                $dropdown.empty();
                $dropdown.append(result);
                $dropdown.show();

                $dropdown.find('.map-dropdown-menu .select-data').on('click', function () {
                    addSelected($parent.find('.map-selected'), this, function () {
                        buildRemoveClickEvent($parent);
                    });
                });
                $dropdown.find('.map-dropdown-menu .more-select').on('click', function () {
                    showDialogForm({
                        url: '/list/table.view',
                        data: {
                            parent: id,
                            table: desc.ref_table_name,
                            action: 'map',
                            defaultFilters: field.p('filters')
                        },
                        title: 'Select',
                        class: 'dx-input-selector-dialog',
                        needAutoExpand: true,
                        shown: function (listForm, dialog) {
                            listForm.close = function (data) {
                                dialog.close();
                                //field.updated(data);
                                addSelected($parent.find('.map-selected'), this, function () {
                                    buildRemoveClickEvent($parent);
                                }, data);
                            }
                        }
                    });
                });
                $dropdown.find('.map-dropdown-menu .create-data').on('click', function () {
                    newTab('/detail/create.view', {
                        parent: id,
                        table: desc.ref_table_name
                    });
                });
                callback();
            });
        }

        if (form.action !== 'create' && form.action !== 'edit')
            return;
        var id = $(this).find('.dx-input-map').attr('id');
        var field = w(id);
        var desc = getDesc(field);

        var newVar = field.get();
        var $parent = newVar.parent();
        var $dropdown = $parent.siblings('.dx-select-map-input-div').find('.map-dropdown');

        var filterData = isEmpty(desc.ref_table_sql) ? null : buildSQLParam(form, desc.ref_table_sql);
        loadSelection('/widget/map/select.view', {id: form.id, parent: id, filterData: filterData}, function () {


        });
        var to = false;
        var $this = $parent.siblings('.dx-select-map-input-div').find('.map-select-input');
        $this.keyup(function () {
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                var val = field.val;
                var filter = $this.val();
                filterData.input_map_filter = filter;
                loadSelection('/widget/map/select.view', {
                    id: form.id,
                    parent: id,
                    filterData: filterData
                }, function () {

                });
            }, 500);
        });
        //showDialogForm({
        //	url: '/widget/map/select.view',
        //	title: i18n(desc.i18n),
        //	data: {parent: this.id, filterData: filterData},
        //	class: 'dx-input-map-dialog'
        //});
    }).on('dx.updated', function (e, data) {
        //var field = w($(this).parent().find('.dx-input-map').attr('id'));
        //field.selection = data;
        //field.popoverContent = undefined;
        //if (isEmpty(field.value))
        //	field.value = field.id;
        //var newVar = field.get();
        //var $parent = newVar.parent();
        //addSelected($parent.find('.map-selected'),this, function(){
        //    $parent.find('.map-selected-param .remove-map-selected').on('click', function () {
        //        $(this).parent().remove();
        //    });
        //},data);
        //field.get().val(mapInputText(field));
    })//.popover(mapInputPopOptions);
}
//初始化长文本
function initTextArea(form) {
    form.get().find('.dx-text-area-textarea').on('blur', function () {
        var $textAreaInput = $(this).parent().find(".dx-text-area");
        $textAreaInput.val($(this).val());
        var field = w($textAreaInput[0].id);
        var currentApproveBlock = getCurrentBlockId(form);
        //字段值改变后修改后台缓存值。
        postJson('/data/changeModify.do', {id: form ? form.id : null, column: field.column,
            value: field.val(), currentBlock: currentApproveBlock}, function (result){
        }, null, true);
    });
    /*form.get().find('.dx-text-area-textarea').parent().prev().click(function(){
     alert("label");
     });*/
    form.get().find('.dx-text-area-textarea').each(function () {
        var $area = $(this);
        var id = $(this).prev().attr("id");
        form.get().find("label[for='" + id + "']").click(function () {
            $area.focus();
        });
    });
    if(dx.user.isMobileLogin==1)
    	form.get().find('.dx-text-area-textarea').parents(".dx-field-container").addClass("field-textarea");
}
//初始化富文本
function initEditorInput(form, isable) {
    function editorInputValue(contents, $thisNode) {
        var $editorInputHidden = $($thisNode).parent().find(".dx-editor-input");
        $editorInputHidden.val(contents);
    }

    var $editorInput = form.get().find('.dx-editor-input-textarea');

    $editorInput.summernote({
        height: 400,
        // width: 400,
        // minHeight: 300,
        // maxHeight: 400,
        popover: {
            image: false
        },
        focus: true,
        lang: 'zh-CN',
        //// 重写图片上传
        //onImageUpload: function(files, editor, $editable) {
        //    sendFile(files[0],editor,$editable);
        //}
        callbacks: {
            onChange: function (contents, $editable) {
                editorInputValue(contents, this);
            }
        }
    });
    var editValue = $editorInput.summernote('code');
    editorInputValue(editValue, $editorInput);
    if (isable == "enable") {
        $editorInput.summernote('enable');
    } else if (isable == "disable") {
        $editorInput.summernote('disable');
    }
}
//初始化日历事件
function initCalendarEvent(form) {
    var calendarEventsMap = {};
    if (!isEmpty(form.calendarEvents)) {
        for (var i = 0; i < form.calendarEvents.length; i++) {
            calendarEventsMap[form.calendarEvents[i].ref_table_column] = form.calendarEvents[i];
        }
    }
    form.get().find('.event-color-button-span').each(function () {
        var that = this;
        var field = w($(this).attr('field_id'));
        if (!isEmpty(calendarEventsMap[field.column])) {
            $(this).css('background', calendarEventsMap[field.column].color);
            $(this).attr('event_color', calendarEventsMap[field.column].color);
        } else { //没有的为新增。。有的为编辑。
            field.eventColorStatue = 'add';
        }
        field.eventColor = function () {
            return $(that).attr('event_color');
        }
    });
    form.get().find('.event-color').on('click', function () {
        var $eventColorButton = $(this).parent().parent().find('.event-color-button-span');
        var eventBackgroundColor = $(this).attr('event_backgroud_color');
        $eventColorButton.css('background', eventBackgroundColor);
        $eventColorButton.attr('event_color', eventBackgroundColor);
    })
}


//=========================手机端控件=======================================
/**
 * init date input field for mobile
 *
 * @param form
 * @param [opts]
 */
var mobileDatePickerInitPara = {
    dateFormat: "yyyy-mm-dd"
};

var defaultMobileDateInitPara = {selector: 'input.dx-date', para: mobileDatePickerInitPara};

function initMobileDate(form, opts) {
    opts = initOpts(defaultMobileDateInitPara, opts);
    //修复点开日历控件后，选择列表控件无法正常打开
    form.get().find(opts.selector).click(function(){
    	$(".close-select").click();
    });
    
    form.get().find(opts.selector).calendar(opts.para).each(function () {
        var $this = $(this);
        if (opts.init)
            opts.init.call(this, $this);
        var field = w(this.id);
        field.val = function (val) {
            //get
            if (val === undefined)
                return $this.val() ? $this.val() : null;
            else if (isEmpty(val))
                $this.val('');
            else  //set .getTime()
                $this.val($.format.date(new Date(val), 'yyyy-MM-dd'));
            //$this.val(new Date(val).getTime());
        };
        field.text = function () {
            return $.format.date(new Date(this.value), 'yyyy-MM-dd');
        };
        form.get().find("label[for='" + this.id + "']").attr("for", "");
    });
}


/**
 * init dictionary selector
 *
 * @param form
 */
function initMobileDictSelect(form) {
    var arg = {};

    function sortDict(a, b) {
        if (a.value < b.value)
            return -1;
        if (a.value > b.value)
            return 1;
        return 0;
    }

    var $inputs = form.get().find('input.dx-dict-select');
    $inputs.each(function () {
        var $this = $(this);
        var field = w(this.id);
        var desc = getDesc(field);
        var dict = dx.dict[desc.dic_id];
        arg.title = desc.i18n[dx.user.language_id];
        field.val = function (val) {
            if (val == undefined) {
                var label = $('#' + field.id).val();
                var value = '';
                $.each(dict, function (k, v) {
                    if (label == v[dx.user.language_id])
                        value = k;
                });

                return value;
            } else if (isEmpty(val))
                $this.val('');
            else {
                field.value = val;
                $this.val(i18n(dict[val]));
            }
        };
        field.text = function () {
            return dict[field.value];
        };
        var source = [];
        $.each(dict, function (k, v) {
            source.push({title: v[dx.user.language_id], value: v[dx.user.language_id]})
        });
        source.sort(sortDict);
        arg.items = source;
        $('#' + field.id).select(arg);
    });

}

//手机隐藏富文本字段
function initMobileEditorInput(form) {
    form.get().find('.dx-editor-input-textarea').parent().parent().css("display", "none");
}


//================================================================
/**
 * init all input fields within $container
 *
 * @param form
 */
function initField(form) {
    if (dx.user.isMobileLogin == 1) {
        initInputSelects(form);
        initInputDate(form);
        initMobileDictSelect(form);
        initNumberDigits(form);
        initMobileDate(form);
        initDatetime(form);
        initCheckbox(form);
        initVirtualFields(form);
        initFileField(form);
        initMapField(form);
        initTextArea(form);
        initMobileEditorInput(form);

        if (form.action == "create") {
            form.get().find(".dx-dict-select ,.dx-date,.dx-input-select").attr("placeholder", "请选择>");
            form.get().find(".dx-input,.dx-number,.dx-input-with-format,.dx-digits").attr("placeholder", "请填写");
            form.get().find(".dx-readonly").attr("placeholder", "");
        }
    } else {
        initInputSelects(form);
        initInputDate(form);
        initDictSelect(form);
        initNumberDigits(form);
        initDate(form);
        initDatetime(form);
        initCheckbox(form);
        initVirtualFields(form);
        initFileField(form);
        initMapField(form);
        initTextArea(form);
        initEditorInput(form);
        initCalendarEvent(form);
    }
    var $container = form.get();
    var fields;
    var currentApproveBlock = getCurrentBlockId(form);
    fields = $container.find('.dx-field').on('change blur modify', function () {
        if (!$(this).valid()) {
            if (($(this).hasClass("dx-dict-select") && dx.user.isMobileLogin == 1)) {
                $(this).removeClass("has-error");
            }else{
                if (($(this).hasClass("has-error") && dx.user.isMobileLogin == 0)) {
                    $(this).removeClass("has-error");
                }
                return;
            }
            return;
        }
        var field = w(this.id);
        var isEvalReadonly = true;
        if (!form.p('noAutoRO') && form.approveEdit){
            isEvalReadonly = false;
        }
        //字段值改变后修改后台缓存值。
        if (!isEmpty(field.column))
            postJson('/data/changeModify.do', {id: form ? form.id : null, column: field.column,
                value: field.val(), currentBlock: currentApproveBlock, evalReadonly: isEvalReadonly}, function (result){
                if (!isEmpty(result) && !isEmpty(result.evalValue))
                    evaluateFormByChangeValue(form, field, result.evalValue);
                if (!isEmpty(result) && !isEmpty(result.readonly)){
                    evaluateReadonlyByChangeValue(form, result.readonly);
                }
            }, null, true);
        //evaluateForm(form, field);
        //if (!form.p('noAutoRO') && !form.approveEdit)
        //    evaluateReadonly(form);
        }).tooltip({delay: {show: 0, hide: 0}});

    if (form.action == 'edit')
        fields.each(function () {
            var field = w(this.id);
            field.orig = field.val();
        });
    evaluateFormPrefixSuffix(form);
    if ($container.is('form'))
        $container.validate(defaultValidateOptions);

}
