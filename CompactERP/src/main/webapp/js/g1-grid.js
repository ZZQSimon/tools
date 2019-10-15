///**
//* Created with IntelliJ IDEA.
//* User: zang.loo
//* Date: 9/15/14
//* Time: 1:03 PM
//*/
//
//"use strict";
//
///**
//* cache records to grid, build record models, and build data array for DataTable
//*
//* @param grid grid model
//* @param records record data
//* @param parent parent widget model
//* @param [cellProcess] callback before each cell data been build
//* @returns {Array}
//*/
//function buildGridData(grid, records, parent, cellProcess) {
//	grid.records = records;
//	var array = [];
//	// build all records cache and dataTable data array
//	$.each(records, function (i, record) {
//		var data = {rowid: record.id};
//		record.columns = {};
//		if (parent)
//			record.columns._parent = {ref: parent.columns};
//		// build record cache and set columns values
//		$.each(record.fields, function (i, field) {
//			if (cellProcess)
//				data[field.column] = cellProcess(field, record);
//			else
//				data[field.column] = fieldText(field, record);
//			record.columns[field.column] = field;
//		});
//		// set rowid for tr
//		data.DT_RowId = record.id;
//		data.rawData = record;
//		array.push(data);
//	});
//	if (grid.dataHandler)
//		array = grid.dataHandler.call(grid, array);
//	return array;
//}
//
///**
//* check action execute condition
//*
//* @param table
//* @param action
//* @param [rows] used for edit/remove
//* @returns {boolean}
//*/
//function checkActionCondition(table, action, rows) {
//	if (!table.action_condition) return true;
//	var condition = table.action_condition[action];
//	if (!condition) return true;
//	switch (action) {
//		case 'create':
//			if (!evaluate(condition.formula, rows[0], false)) {
//				messageBox(evaluate('"' + condition.message + '"', rows[0], 'can not create data'));
//				return false;
//			}
//			break;
//		case 'edit':
//		case 'remove':
//			if (!rows.every(function (row) {
//					if (!evaluate(condition.formula, row, false)) {
//						messageBox(evaluate('"' + condition.message + '"', row, 'can not ' + action + ' data'));
//						return false;
//					}
//					return true;
//				})) {
//				return false;
//			}
//			break;
//	}
//	return true;
//}
//
//function iteratorGridHeaders(table, columns, noAuthColumns,callback, userColumns) {
//	var lastKey = getTableIdColumn(table);
//    var tableName = table.id;
//    var userColumn;
//    if (!isEmpty(userColumns))
//        userColumn = userColumns[tableName];
//    if (userColumn){
//        userColumn.forEach(function (column) {
//            // virtual or password field will not display
//            var desc = column.columnDescribe;
//            if (desc.virtual || desc.data_type == 6)
//                return;
//            var visible = true;
//            if ((table.parent_id) && (desc.column_name != lastKey) && (table.id_column.indexOf("[" + desc.column_name + "]") >= 0))
//                visible = false;
//            if (desc.hidden)
//                visible = false;
//            if (visible && noAuthColumns)
//                visible = noAuthColumns.indexOf(desc.column_name) == -1;
//
//            callback(desc, visible);
//        });
//    }else{
//        columns.forEach(function (desc) {
//            // virtual or password field will not display
//            if (desc.virtual || desc.data_type == 6)
//                return;
//            var visible = true;
//            if ((table.parent_id) && (desc.column_name != lastKey) && (table.id_column.indexOf("[" + desc.column_name + "]") >= 0))
//                visible = false;
//            if (desc.hidden)
//                visible = false;
//            if (visible && noAuthColumns)
//                visible = noAuthColumns.indexOf(desc.column_name) == -1;
//
//            callback(desc, visible);
//        });
//    }
//}
//
///**
//* init data grid
//*
//* @param form
//*/
//function initGrid(form) {
//	form.get().find('table.dx-grid').each(function () {
//		function ajaxLoadPaging(param, callback) {
//			$checkall.prop('checked', false);
//			if (!grid.p('ajax')) {
//				callback({
//					draw: param.draw,
//					recordsTotal: 0,
//					recordsFiltered: 0,
//					data: []
//				});
//				return;
//			}
//            var filter = grid.p('filter');
//			var postData = {id: id, filter: filter, paging: param};
//			var orders = param.order;
//			if (orders)
//				orders.forEach(function (order) {
//					order.name = options.columns[order.column].data;
//				});
//			postJson('/widget/grid/page.do', postData, function (ret) {
//				// using by dataTable for rendering
//				ret.data = buildGridData(grid, ret.records, form);
//				callback(ret);
//				if (grid.p('rowChanged'))
//					grid.p('rowChanged')();
//			})
//		}
//
//		function ajaxLoad(ignore, callback) {
//			if (!grid.p('ajax')) {
//				callback({data: []});
//				return;
//			}
//			var postData = {id: id};
//			postJson('/widget/grid/list.do', postData, function (records) {
//				// using by dataTable for rendering
//				callback({data: buildGridData(grid, records, form)});
//				if (grid.p('rowChanged'))
//					grid.p('rowChanged')();
//			})
//		}
//
//        function tableViewStyleAjaxLoad(callback) {
//            if (!grid.p('ajax')) {
//                callback({data: []});
//                return;
//            }
//            var postData = {id: id, filter: grid.p('filter')};
//            postJson('/widget/grid/list.do', postData, function (records) {
//                // using by dataTable for rendering
//                callback({data: buildGridData(grid, records, form)});
//                if (grid.p('rowChanged'))
//                    grid.p('rowChanged')();
//            })
//        }
//		function getSelectRow(idOnly) {
//			var $rows = $grid.find('tbody tr.selected');
//			if ($rows.length == 0)
//				return null;
//			var rows = [];
//			$rows.each(function (i, tr) {
//				rows.push(idOnly ? tr.id : w(tr.id));
//			});
//			return rows;
//		}
//        //获取选中style_html值
//        function getViewTableSelectRow(idOnly) {
//            var $rows = $grid.find('div input:checkbox:checked');
//            if ($rows.length == 0)
//                return null;
//            var rows = [];
//            $rows.each(function (i, tr) {
//                rows.push(idOnly ? tr.id : w(tr.id));
//            });
//            return rows;
//        }
//        //获取双击的行
//        function getdbclickRow(idOnly) {
//            var $rows = $(idOnly);
//            if ($rows.length == 0)
//                return null;
//            var rows = [];
//            $rows.each(function (i, tr) {
//                rows.push(w(tr.id));
//            });
//            return rows;
//        }
//
//		function unselectRow($tr) {
//			$tr.removeClass('selected');
//			$tr.find('.dx-grid-checkrow').prop('checked', false);
//		}
//
//		function selectRow($tr) {
//			if ($tr.find('td.dataTables_empty').length > 0)
//				return;
//			$tr.addClass('selected');
//			$tr.find('.dx-grid-checkrow').prop('checked', true);
//		}
//
//		function toggleSelect($tr) {
//			if ($tr.hasClass('selected'))
//				unselectRow($tr);
//			else
//				selectRow($tr);
//			if (grid.p('rowChanged')) grid.p('rowChanged')();
//		}
//
//		/**
//		 * build grid selected record's key data
//		 *
//		 * @param recordId record id
//		 */
//		function buildGridRecordKey(recordId) {
//			var record = w(recordId);
//			var data = {};
//			$.each(record.columns, function (key, field) {
//				data[key] = field.value;
//			});
//			return {parent: record.id, param: data, table: grid.table};
//		}
//
//		// init child tabs grid buttons
//		function initChildToolbar() {
//			$($grid.DataTable().table().header()).on('click', 'button.dx-grid-add', function (e) {
//				if (!checkActionCondition(table, 'create', [form])) return;
//				if (grid.p('add'))
//					grid.p('add')(grid);
//				else
//					newTab('/detail/create.view', {parent: grid.id, table: grid.table});
//			});
//			$grid.on('click', 'button.dx-grid-edit', function () {
//				if (!checkActionCondition(table, 'edit', [w(this.value)])) return;
//
//				if (grid.p('edit'))
//					grid.p('edit')(grid, this.value);
//				else
//					newTab('/detail/edit.view', buildGridRecordKey(this.value), grid.id + '-' + this.value);
//			});
//			$grid.on('click', 'button.dx-grid-delete', function () {
//				if (!checkActionCondition(table, 'remove', [w(this.value)])) return;
//				if (grid.p('delete'))
//					grid.p('delete')(grid, this.value);
//				else {
//					var self = this;
//					confirmBox('delete confirm', function () {
//						postJson('/widget/grid/delete.do', {
//							id: grid.id,
//							ids: [self.value]
//						}, function () {
//							grid.updated();
//						});
//					});
//				}
//			});
//		}
//        //获取style_html中参数值
//        function makeParamsData(param, form){
//            return evaluate(param, form);
//        }
//		var $grid = $(this);
//		var grid = w(this.id);
//		var table = getTableDesc(grid.table);
//		var id = $grid.attr('id');
//        var blockViewStyle = table.block_view_style;
//        var default_group_column = table.default_group_column;
//        if(!isEmpty(blockViewStyle) && !form.viewTableStyle){
//
//            var obj = eval('(' + blockViewStyle + ')');
//            var params = obj.params;
//            var tableViewStyle = dx.tableViewStyle[obj.styleId].style_html;
//            var appendTableViewStyle = tableViewStyle;
//            var $checkall = $grid.find('input.dx-grid-checkall');
//            grid.filter = function () {
//                $checkall.prop('checked', false);
//                grid.p('filter', this.data());
//                grid.p('ajax', true);
//                $grid.empty();
//                tableViewStyleAjaxLoad(function(data){
//                    var resultData = data.data;
//                    //分组为空与分组不为空逻辑
//                    var ret;
//                    if(isEmpty(default_group_column)){
//                        for(var i=0; i<resultData.length; i++){
//                            appendTableViewStyle = '<div style="float: left"><input type="checkbox" id="'+resultData[i].rowid+'"/>' +
//                                        appendTableViewStyle + '</div>';
//                            for(var j=0; j<params.length; j++){
//                                ret = makeParamsData(params[j], resultData[i]);
//                                appendTableViewStyle =
//                                appendTableViewStyle = appendTableViewStyle.replace('${'+j+'}', ret);
//                            }
//                            $grid.append(appendTableViewStyle);
//                            appendTableViewStyle = tableViewStyle;
//                            resultData[i].id = resultData[i].rowid;
//                            w(resultData[i]);
//                        }
//                    }
//                    else{
//                        var groupValue;
//                        var groupValues = new Array();
//                        var appendDivs;
//                        for(var i=0; i<resultData.length; i++){
//                            appendTableViewStyle = '<div style="float: left" class="table-view-style-param"><input type="checkbox" id="'+resultData[i].rowid+'"/>' +
//                                    appendTableViewStyle + '</div>';
//                            for(var j=0; j<params.length; j++){
//                                ret = makeParamsData(params[j], resultData[i])
//                                appendTableViewStyle = appendTableViewStyle.replace('${'+j+'}', ret);
//                            }
//                            groupValue = resultData[i][default_group_column];
//                            if (isEmpty(groupValues[groupValue])){
//                                groupValues[groupValue] = groupValue;
//                                appendDivs = '<div name="' + groupValue + '" style="clear:both" class="table-view-style-group"><span>' + groupValue + '</span></div>'
//                                $grid.append(appendDivs);
//                                $grid.find('div[name='+ groupValue +']')//.append('<hr size="5"/>');
//                                $grid.find('div[name='+ groupValue +']').append(appendTableViewStyle);
//                            }else{
//                                $grid.find('div[name='+ groupValue +']').append(appendTableViewStyle);
//                            }
//                            appendTableViewStyle = tableViewStyle;
//                            resultData[i].id = resultData[i].rowid
//                            w(resultData[i]);
//                        }
//                    }
//                    grid.p('ajax', false);
//                    dx.processing.close("Searching");
//                });
//            };
//            grid.reset = function () {
//                $checkall.prop('checked', false);
//                grid.p('filter', null);
//                grid.reload();
//            };
//            grid.reload = function () {
//                $checkall.prop('checked', false);
//            };
//            grid.getSelectedRows = getViewTableSelectRow;
//            grid.getdbclickRow = getdbclickRow;
//        }else{
//            $grid.parents('.dx-auto-expand').on('dx.auto-expand', function () {
//                $(this).find('div.dataTables_scrollBody').height(form.p('expandHeight') - (grid.listing ? 83 : 30));
//            });
//            var options = {
//                //sort: false,
//                order: [],
//                scrollX: "100%",
//                // set handler which will be called when table cells has been draw
//                drawCallback: function () {
//                    $grid.DataTable().columns.adjust();
//                    if (!grid.records) return;
//                    // check if any field has formula need this table's column
//                    if (grid.reloaded && form.fieldIds) {
//                        grid.reloaded = false;
//                        $.each(form.fieldIds, function (i, id) {
//                            var field = w(id);
//                            var desc = getDesc(field);
//                            if (desc.formula && (desc.formula.indexOf('"' + grid.table + '.') >= 0)) {
//                                field.val(evaluate(desc.formula, form));
//                                evaluateForm(form, field);
//                            }
//                        });
//                    }
//                    if (grid.postInit)
//                        grid.postInit.call(grid);
//                }
//            };
//
//            if (grid.listing) {
//                if (grid.p('ajax') === undefined)
//                    grid.p('ajax', false);
//                options.ajax = ajaxLoadPaging;
//                options.pageLength = 30;
//                options.pagingType = 'input';
//                options.serverSide = true;
//                options.aLengthMenu = [[10, 30, 50, 999999], ["10", "30", "50", "All"]];
//                //options.bProcessing = true;
//                options.bStateSave = true;
//                options.columns = [
//                    {
//                        data: 'rowid',
//                        orderable: false,
//                        render: function () {
//                            if (form.action === 'select')
//                                return '<input type="radio" name="dx-grid-radio-"' + grid.id + ' class="dx-grid-checkrow"/>';
//                            else
//                                return '<input type="checkbox" class="dx-grid-checkrow"/>';
//                        }
//                    }
//                ];
//                var domVar = 'rt<".dataTables_foot"<".column_tool">pil>';
//                options.dom = domVar;
//    //			options.dom = 'Rtilp';
//            } else {
//                if (grid.p('ajax') === undefined)
//                    grid.p('ajax', true);
//                options.paging = false;
//                options.ajax = ajaxLoad;
//                options.columns = [
//                    {
//                        data: 'rowid',
//                        visible: form.action != 'view',
//                        orderable: false,
//                        render: function (data) {
//                            return sprintf('<button type="button" class="btn btn-default btn-xs dx-grid-delete dx-grid-child-delete" value="%s"><span class="glyphicon glyphicon-minus"></span></button><button type="button" class="btn btn-default btn-xs dx-grid-edit dx-grid-child-edit" value="%s"><span class="glyphicon glyphicon-edit"></span></button>', data, data);
//                        }
//                    }
//                ];
//                if (table.viewStyle && table.viewStyle.seq) {
                    //表格数据排序
//                    options.rowReorder = {
//                        selector: '.reorder',
//                        dataSrc: table.viewStyle.seq
//                    };
//                    options.order = [[getColumnDesc(table.id, table.viewStyle.seq).seq, "asc"]];
//                    options.columnDefs = [{orderable: false, targets: '_all'}];
//                }
//                options.dom = 'Rrt';
//            }
//            if (grid.hasSum)
//                options.footerCallback = function () {
//                    var api = this.api();
//                    var index = 0;
//                    if (grid.records)
//                        $(api.column(0).footer()).html(msg("RowCount", grid.records.length));
//                    for (var i = 0; i < grid.p('columns').length; i++) {
//                        var column = grid.p('columns')[i];
//                        if (column.virtual)
//                            continue;
//                        index++;
//                        if (column.sum_flag == null)
//                            continue;
//                        // Update footer
//                        $(api.column(index).footer()).html(formatNumber(grid.sum(column.column_name), column.sum_flag));
//                    }
//                };
//            options.fnRowCallback = function (nRow) {
//                var record = grid.records[nRow._DT_RowIndex];
//                // cache record
//                w(record, $(nRow));
//                //TODO reload record only, not grid
//                $grid.popover($.extend({selector: '.dx-grid-map-input'}, mapInputPopOptions));
//                record.onUpdated = grid.updated.bind(grid);
//                if (table.renders)
//                    table.renders.some(function (render) {
//                        if (evaluate(render.formula, record)) {
//                            if (render.style.bg)
//                                $('td', nRow).css('background-color', render.style.bg);
//                            if (render.style.color)
//                                $('td', nRow).css('color', render.style.color);
//                            return true;
//                        }
//                    });
//            };
//            if (!grid.p('columns'))
//                grid.p('columns', table.columns);
//            iteratorGridHeaders(table, grid.p('columns'), grid.noAuthColumns, function (desc, visible) {
//                var columnDefine = {data: desc.column_name, visible: visible};
//                // for number/digits column, right align
//                if (desc.data_type == 2 || desc.data_type == 3) {
//                    columnDefine.className = 'right';
//                    if (form.action !== 'view' && table.viewStyle && desc.column_name === table.viewStyle.seq)
//                        columnDefine.className += ' reorder';
//                    if (desc.data_type == dx.dataType.number)
//                        columnDefine.render = function (data, type, row) {
//                            // If display or filter data is requested, format the date
//                            if (type === 'display' || type === 'filter')
//                                return data;
//
//                            // Otherwise the data type requested (`type`) is type detection or
//                            // sorting data, for which we want to use the integer, so just return
//                            // that, unaltered
//                            return Number(data.replace(',', ''));
//                        };
//                } else if (desc.data_type == 1) {
//                    if (desc.viewStyle && desc.viewStyle.map) {
//                        // using by popover
//                        columnDefine.className = 'dx-grid-map-input';
//                        columnDefine.createdCell = function(td, cellData, rowData, row, col){
//                            $(td).data('rowid', rowData.rowid);
//                            $(td).data('column', desc.column_name);
//                        }
//                    }
//                    columnDefine.render = function (data, type, row) {
//                        if (!data)
//                            return '';
//                        if (type === 'display') {
//                            if (!desc.link && (typeof data === 'string'))
//                                return data.replace(/</g, "&lt;").replace(/>/g, "&gt;");
//                        }
//                        return data;
//                    };
//                }
//                options.columns.push(columnDefine);
//            }, grid.userColumns);
//            var $checkall = $grid.find('input.dx-grid-checkall');
//            $checkall.on('click', function () {
//                var func = this.checked ? selectRow : unselectRow;
//                $grid.find('tr').each(function () {
//                    func($(this));
//                });
//                if (grid.p('rowChanged')) grid.p('rowChanged')();
//            });
//            if (grid.optionsHandler)
//                options = grid.optionsHandler.call(grid, options);
//            /**
//             * iterate child.column values using callback
//             *
//             * @param name
//             * @param callback
//             * @private
//             */
//            grid.iterate = function (name, callback) {
//                // not inited
//                if (!grid.records) return true;
//                return grid.records.every(function (record) {
//                    var value = record.columns[name].value;
//                    return callback(value) !== false;
//                });
//            };
//
//            grid.sum = function (name) {
//                var ret = new BigDecimal('0');
//                grid.iterate(name, function (val) {
//                    var value;
//                    if (val == null)
//                        value = "0";
//                    else
//                        value = val.toString();
//                    ret = ret.add(new BigDecimal(value))
//                });
//                return Number(ret.toString());
//            };
//
//            grid.columnVisible = function (index, visible) {
//                dt.column(index).visible(visible);
//            };
//
//            var dt = $grid.DataTable(options);
//            //初始化datatable之后添加选择列按钮
//            form.get().find('.column_tool').append('<button>' +msg("symbol")+'</button>');
//            //add button after next page button
//    //        $("#selectAllButton_"+id).append("<input type='button' value='按钮' style='float: right'/>")
//    //        $(".dataTables_length").css("height","10px");
//            $("#pageSize").css("padding-left","200px");
//
                //拖动
//            if (options.rowReorder) {
//                dt.on('row-reordered', function (e, details) {
//                    if (details.length === 0)
//                        return;
//                    var data = {};
//                    for (var i = 0, ien = details.length; i < ien; i++) {
//                        var d = details[i];
//                        var rowid = dt.row(d.node).data().rowid;
//                        data[rowid] = d.newData;
//                    }
//                    postJson('/widget/grid/reorder.do', {id: grid.id, reorder: data});
//                });
//            }
//
//            // init updated handler
//            grid.onUpdated = function () {
//                grid.reloaded = true;
//                $checkall.prop('checked', false);
//                var page = $grid.DataTable().page()
//                $grid.DataTable().ajax.reload();
//                //updated keep now page
//                $grid.DataTable().page(page).draw(false);
//                form.p('modified', true);
//            };
//            if (grid.listing) {
//                $grid.find('tbody').on('click', 'tr', function (e) {
//                    if ($(e.target).is('a'))
//                        return;
//                    if (form.action === 'select')
//                        $grid.find('tbody tr.selected').removeClass('selected');
//                    toggleSelect($(this));
//                });
//            } else {
//                // init toolbar in children mode
//                initChildToolbar();
//                grid.switchToEdit = function () {
//                    $grid.DataTable().column(0).visible(true);
//                    if (table.viewStyle && table.viewStyle.seq) {
//                        var desc = getColumnDesc(table.id, table.viewStyle.seq);
//                        $grid.find('tbody td:nth-child(' + desc.seq + ')').addClass('reorder');
//                    }
//                }
//            }
//
//            // set filter handler
//            grid.filter = function () {
//                $checkall.prop('checked', false);
//                var data = this.data();
//                grid.p('filter', this.data());
//                grid.p('ajax', true);
//    //			$grid.DataTable().ajax.reload();
//                $grid.DataTable().page($grid.DataTable().page()).draw(false);
//            };
//            // set filter reset handler
//            grid.reset = function () {
//                $checkall.prop('checked', false);
//                grid.p('filter', null);
//                grid.p('ajax', false);
//                grid.reload();
//            };
//            grid.reload = function () {
//                $checkall.prop('checked', false);
//    //			$grid.DataTable().ajax.reload();
//                //if the last data, return before page
//                var nowPageLenght = $grid.DataTable().data().length;
//                if(nowPageLenght == 1){
//                    $grid.DataTable().page( 'previous').draw(false);
//                }else{
//                    $grid.DataTable().page($grid.DataTable().page()).draw(false);
//                }
//            };
//
//            grid.getSelectedRows = getSelectRow;
//            grid.buildRecordData = buildGridRecordKey;
//            grid.getdbclickRow = getdbclickRow;
//            if (table.auto_gen_sql)
//                grid.autoGen = function (form) {
//                    var param = buildSQLParam(form, table.auto_gen_sql);
//                    postJson('/widget/grid/auto.do', {id: grid.id, param: param}, function () {
//    //					$grid.DataTable().ajax.reload();
//                        $grid.DataTable().page($grid.DataTable().page()).draw(false);
//                    });
//                }
//        }
//	});
//}