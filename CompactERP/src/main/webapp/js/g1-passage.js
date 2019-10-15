/**
 * Created with IntelliJ IDEA.
 * User: zhang.lei
 * Date: 10/14/14
 * Time: 2:03 PM
 */

"use strict";

$(function () {
	registerFormulaFunc('pa_day', function (type, index) {
		var date = new Date();
		switch (type) {
			// index is index, -1 is the last one
			case 'month':
				if (index == -1) {
					date.setDate(1);
					date.setMonth(date.getMonth() + 1);
					date.setDate(0);
				} else
					date.setDate(index);
				break;
			default :
				break;
		}
		return date.getTime();
	});
	registerFormulaFunc('pa_month', function (type, index) {
		var date = new Date();
		switch (type) {
			// index is index, -1 is the last one
			case 'year':
				if (index == -1)
					index = 12;
				date.setMonth(index - 1);
				date.setDate(1);
				break;
			default :
				break;
		}
		return date.getTime();
	});
	registerFormulaFunc('pa_week', function (type, index) {
		var date = new Date();
		switch (type) {
			// index is delta with now
			case 'week':
				var dayDelta = 1 - date.getDay();
				date.setDate(date.getDate() + index * 7 + dayDelta);
				break;
			default :
				break;
		}
		return date.getTime();
	});
});

registerInit('passage', function (form) {
	function resize() {
		$grid.fixedHeaderTable('destroy');
		$grid.fixedHeaderTable({height: form.p('expandHeight')/*, fixedColumn:true, fixedColumns:form.headColumns.length +1*/});
	}

	function reset(keepFilter) {
		// 条件查询form取得
		var $container = form.get().find('.dx-filter-form');
		$container.find('.dx-filter').each(function () {
			var $this = $(this);
			var column = $this.attr('name');

			$this.attr('disabled', false);
		});

		var postData = {
			id: formId
			, start_date: $('#' + formId + 'startDate').val()
			, end_date: $('#' + formId + 'endDate').val()
		};
		postJson('/passage/table.do', postData, function (headPassColumns) {
			// 清除全局检索项目
			if (!keepFilter) {
				var ids = $('#' + formId + 'ids').val();
				if (ids != "") {
					$.each(ids.split(","), function (i, fid) {
						var field = w(fid);
						var table = getTableDesc(field.table);
						var Desc = table.columnMap[field.column];
						if (Desc.data_type == "5") {
							field.val(false);
						} else {
							field.val("");
						}
					});
				}
			}
			// 清除 行映射缓存
			cacheRowsMap = {};
			// 清除 详细数据缓存
			cacheTable = new Array();
			// 清除 行表头映射缓存
			cacheRowsHead = [];
			// 清除 数据行可否编辑缓存
			cacheRowsEdit = [];

			// reset the dataTable
			buildNewTable(headPassColumns);

			$('#' + formId + 'save').attr('disabled', true);
			$('#' + formId + 'export').attr('disabled', true);
			$('#' + formId + 'cancel').attr('disabled', true);
			$('#' + formId + 'add').attr('disabled', true);

			if (form.hasAppend) {
				$('#' + formId + 'startDate').attr('disabled', false);
				$('#' + formId + 'endDate').attr('disabled', false);
				if (form.searchConditions.length > 0) {
					// 设定全局检索条件的值
					var ids = $('#' + formId + 'ids').val();

					$.each(ids.split(","), function (i, fid) {
						$('#' + fid).attr('disabled', false);
					});
				}
			}
		});

		// 条件检索内的按钮设定
		$container.find('.dx-filter-add').each(function () {
			$(this).attr('disabled', true);
		});
		$container.find('.dx-filter-exec').each(function () {
			$(this).attr('disabled', false);
		});
	}

	var $container = form.get();
	var formId = form.id;

	//form.p('resize', resize);

	// 用于数据推移时的全局变量
	var curRow = 0;
	var curCol = 0;
	var originalRow = 0;
	var originalCol = 0;
	var curMainId = "";
	var isUsedMainId = true;
	var relatedFlag = false;
	var pkeysCheck = [];
	var pkeysAppended = [];
	var hasDetail = false;

	// 行映射缓存
	var cacheRowsMap = {};
	// 详细数据缓存
	var cacheTable = new Array();
	// 行表头映射缓存
	var cacheRowsHead = [];
	// 数据行可否编辑缓存
	var cacheRowsEdit = [];

	var rowStartHtml = '<tr role="row">';
	var rowEndHtml = '</tr>';
	var cellStartHtml = '<td>';
	var cellEndHtml = '</td>';
	var inputHtml = '<input type="text" style="inputStyle" class="dx-pa-input dx-number" value="inputValue"'
		+ 'digit="inputDigit" row="inputRow" column="inputColumn"/>';
	var uninputHtml = '<input type="text" style="inputStyle" class="dx-pa-input dx-number" value="inputValue"'
		+ 'digit="inputDigit" row="inputRow" column="inputColumn" readonly="true"/>';

	var headPassColumns = [];
	var headPassColumns_date = [];
	var passRecords = [];
	// 详细数据总行数
	var dataRow = 0;
	// 表示项目数(明细行除外)
	var passageObjs = new Array();

	// build data control with passage
	var datePickerInitParaPass = {};
	$.extend(datePickerInitParaPass, datePickerInitPara);
	// set data control
	if (form.type == 0) {
		datePickerInitParaPass.todayBtn = 'linked';
	} else if (form.type == 1) {
		datePickerInitParaPass.minViewMode = 1;
	} else if (form.type == 2) {
		datePickerInitParaPass.daysOfWeekDisabled = '0,2,3,4,5,6';
	}
	$('#' + formId + 'startDate').datepicker(datePickerInitParaPass);
	$('#' + formId + 'endDate').datepicker(datePickerInitParaPass);

	var $grid = $('#' + formId + 'detail');
	var table = document.getElementById(formId + 'detail');
	initComponent(form);
	$container.find('form').validate(defaultValidateOptions);
	// build new dataTable
	buildNewTable(form.headPassColumns);

	// 条件查询的按钮的设定
	$container.find('.dx-filter-form').find('.dx-filter-add').each(function () {
		$(this).attr('disabled', true);
	});
	// 条件查询的设定
	initFilter(
		form
		, function (where, data) {
			var pkeys = {};
			var pkeysAppendStr = '';

			// 全局检索条件
			var searchConditions = {};
			if (form.searchConditions.length > 0) {
				// 设定全局检索条件的值
				var ids = $('#' + formId + 'ids').val();

				$.each(ids.split(","), function (i, fid) {
					var field = w(fid);
					if (field.val() == "") {
						field.get().focus();
						messageBox(msg('Input_Search_Text'));
						searchConditions = null;
						return false;
					}
					searchConditions[field.column] = field.val();
				});

				// 全局检索条件未入力的场合
				if (searchConditions == null) {
					return;
				}
			}

			// 条件查询form取得
			var $container = form.get().find('.dx-filter-form');

			// 查询button按下
			if (data.action == "exec") {
				pkeysCheck = [];
				pkeysAppended = [];
				$('#' + formId + 'where').val(where);

//				if (form.hasAppend) {
//					// 条件查询form遍历
//					$container.find('.dx-filter').each(function () {
//						var $this = $(this);
//						var column = $this.attr('name');
//						
//						if (form.mainIds.indexOf(column) >= 0 && !isEmpty($this.val())) {
//							$this.attr('disabled', true);
//						}
//					});
//				}

				// 追加button按下
			} else if (data.action == "add") {
				var hasEmpty = false;

				// 条件查询form遍历
				$container.find('.dx-filter').each(function () {
					var $this = $(this);
					var column = $this.attr('name');

					// 主键 必须check
					if (form.mainIds.indexOf(column) >= 0 && isEmpty($this.val())) {
						hasEmpty = true;
						messageBox(msg('Input_Search_Text'));
						$this.focus();
						return false;
					}
				});
				// 条件查询中的主键项目没有全部入力的场合
				if (hasEmpty) {
					return false;
				} else {
					$container.find('.dx-filter').each(function () {
						var $this = $(this);
						var column = $this.attr('name');

						if (form.mainIds.indexOf(column) >= 0) {
							pkeys[column] = $this.val();
							pkeysAppendStr += $this.val() + ",";
//							if (!$this.attr('disabled')) {
//								w($this.attr('id')).val('');
//							}
						}
					});

					// 画面追加推移重复check
					for (var index in pkeysCheck) {
						if (pkeysCheck[index] == pkeysAppendStr) {
							messageBox(msg('repeat key'));
							return false;
						}
					}

					pkeysCheck.push(pkeysAppendStr);
					pkeysAppended.push(pkeys);
				}
			}

			var postData = {
				id: formId
				, param: searchConditions
				, start_date: $('#' + formId + 'startDate').val()
				, end_date: $('#' + formId + 'endDate').val()
				, where: $('#' + formId + 'where').val()
				, pkeys: pkeysAppended
			};
			dx.processing.open("Searching");
			postJson('/passage/list.do', postData, function (rs) {
				if (!isEmpty(rs.msg)) {
					if (data.action == "add") {
						pkeysCheck.pop(pkeysAppendStr);
						pkeysAppended.pop(pkeys);
					}
					messageBox(rs.msg);
					return false;
				}

				// 查询button按下
				if (form.hasAppend && data.action == "exec") {
					$container.find('.dx-filter-add').each(function () {
						$(this).attr('disabled', false);
					});
					$container.find('.dx-filter-exec').each(function () {
						$(this).attr('disabled', true);
					});
				}

				// 重置head部内容
				$(table).fixedHeaderTable('destroy');

				$grid.hide();

				passageObjs = rs.passageObjs;
				// edit the passage data
				editPassageData(rs);
				// build the table
				buildNewTable(rs.headPassColumns);
				// create the new table data
				createTable();

				//为每个单元格命名
				for (var i = 0; i < table.rows.length; i++) {
					for (var j = 0; j < table.rows[0].cells.length; j++) {
						table.rows[i].cells[j].id = formId + "_" + i.toString() + "_" + j.toString();
					}
				}
				// 合并行头单元格(推移對象)
				uniteTable(form.headColumns.length, form.headColumns.length + 2);
				// 合并行头单元格(推移項目)
				uniteTable(0, form.headColumns.length);

				// insert into first row with one blank row
//				addTr(formId + 'detail', 0, createBlankRows());

//				$(table).fixedHeaderTable({height: form.p('expandHeight'), fixedColumn:true, fixedColumns:form.headColumns.length +1});
				$(table).fixedHeaderTable({height: form.p('expandHeight')});

				// delete first row
//				delTr(formId + 'detail', 0);

				$('#' + formId + 'cancel').attr('disabled', false);
				if (cacheTable.length > 0) {
					$('#' + formId + 'save').attr('disabled', false);
					$('#' + formId + 'export').attr('disabled', false);

					$('#' + formId + 'hidStartDate').val(postData['start_date']);
					$('#' + formId + 'hidEndDate').val(postData['end_date']);
				} else {
					$('#' + formId + 'save').attr('disabled', true);
					$('#' + formId + 'export').attr('disabled', true);
				}

				if (data.action == "exec" && form.hasAppend) {
					// 设定日期范围的非活性
					$('#' + formId + 'startDate').attr('disabled', true);
					$('#' + formId + 'endDate').attr('disabled', true);

					// 设定全局检索条件的非活性
					var ids = $('#' + formId + 'ids').val();

					$.each(ids.split(","), function (i, fid) {
						$('#' + fid).attr('disabled', true);
					});
				}

				$grid.show();
			});
		}, reset, {noProcessing: true}
	);

	registerFormulaFunc('pa_cal_cell', function (deltaItem, deltaY) {
		var curX = cacheRowsMap[curMainId + ',' + deltaItem]['curRow'];
		var curY = curCol;
		if (typeof deltaY == 'string') {
			deltaY = deltaY.replace(/d_xf/g, 'dxf');
			deltaY = deltaY.replace(/@/g, '#');
			curY = curY + evaluate(deltaY, form);
		} else {
			curY = curY + deltaY;
		}

		if (curX == originalRow && curY == originalCol) {
			relatedFlag = true;
		}

		if (curX < 0 || curY < 0) {
			return 0;
		}

		var tempCacheCell = cacheTable[curY][curX];
		var value = 0;
		if (tempCacheCell.isEdit) {
			value = tempCacheCell.val;
		} else {
			value = $(document.getElementById(createCellId(curX, curY)).innerHTML).val();
		}
		return disFormatNumber(value);
	});

	registerFormulaFunc('pa_dis_cell', function (deltaItem, deltaY) {
		var cacheRow = cacheRowsMap[curMainId + ',' + deltaItem];

		if (deltaY <= 0) {
			var cellVal = cacheTable[curCol + deltaY][cacheRow['curRow']];
			return disFormatNumber(cellVal['val']);
		}

		return 0;
	});

	function buildNewTable(headPassColumns) {
		// grid的表示与否设定
		if (headPassColumns.length == 0) {
			$grid.hide();
			return false;
		}
		$grid.show();

		// detail的表示与否设定
		if (cacheTable.length == 0)
			$grid.find('tbody').empty();

		// 重置head部内容
		var $tr_thead = $('#' + formId + 'detail').find('thead').find('tr');
		$tr_thead.empty();

		$.each(form.headColumns, function (i, desc) {
			if (i == 0) {
				$tr_thead.append('<th class="dx-pa-head dx-pa-head-group">' + i18n(desc.i18n) + '</th>');
			} else {
				$tr_thead.append('<th class="dx-pa-head dx-pa-head-disp">' + i18n(desc.i18n) + '</th>');
			}
		});

		$tr_thead.append('<th class="dx-pa-head dx-pa-head-obj">' + msg('passage_object') + '</th>');
		$tr_thead.append('<th class="dx-pa-head dx-pa-head-obj">' + msg('passage_object') + '</th>');

		headPassColumns_date = [];
		$.each(headPassColumns, function (i, headPass) {
			headPassColumns_date.push(formatDate_passage(headPass));
			$tr_thead.append('<th class="dx-pa-head dx-pa-head-data">' + headPass + '</th>');
		});

		// column total cell
		$tr_thead.append('<th class="dx-pa-head dx-pa-head-data">' + msg('Total') + '</th>');
	}

	// edit data with passage data
	function editPassageData(records) {
		headPassColumns = records.headPassColumns;
		passRecords = records.passRecords;
		var index = 0;
		dataRow = 0;

		var tempCacheRowHead = [];
		var tempRowHead = {};
		var cacheRowHead = [];
		cacheRowsHead = [];
		var cacheRowMap = {};
		cacheRowsMap = {};
		var maxRow = 0;
		cacheRowsEdit = [];

		var sumDetail = 0; // 各合计用的明细集计
		var detailMap = {};
		var sumDetailMap = {};
		var totalSumMap = {};
		var subTotalSumMap = {};
		var cacheCell = {};
		var baseDate = "";
		// edit passage data
		$.each(headPassColumns, function (colIndex, headPassColumn) {
			if (passRecords.length > 0) {
				cacheTable[colIndex] = new Array();
			} else {
				cacheTable = new Array();
				return false;
			}

			dataRow = 0;
			totalSumMap = {};
			sumDetailMap = {};

			$.each(passRecords, function (i, passRecord) {
				if (i + 1 > passRecords.length) {
					return false;
				}

				// hold current cell
				curMainId = passRecord.mainId;
				curRow = dataRow;
				curCol = colIndex;

				// 初期化表的行头
				cacheRowHead = [];

				// set param
				setFormVariable(form, 'id', passRecord.mainId);
				setFormVariable(form, 'col', colIndex + 1);
				setFormVariable(form, 'row', passRecord.dispNameKey);
				baseDate = setFormVariable_passage(headPassColumn, true);

				// set 行映射(cacheRowsMap)
				cacheRowMap = {curRow: curRow, updStatement: passRecord.updStatement};
				cacheRowsMap[passRecord.mainId + ',' + passRecord.dispNameKey] = cacheRowMap;

				// 设定表的行头于缓存
				// set group and display items
				$.each(passRecord.fields, function (j, field) {
					if (j == 0) {
						tempRowHead = {passageClass: 'dx-pa-cell dx-pa-group-cols'};
					} else {
						tempRowHead = {passageClass: 'dx-pa-cell dx-pa-disp-cols'};
					}
					tempRowHead['column'] = field.column;
					tempRowHead['id'] = field.value;
					tempRowHead['value'] = fieldText(field);
					if (isEmpty(tempRowHead['value'])) {
						tempRowHead['value'] = field.value;
					}
					cacheRowHead.push(tempRowHead);
				});

				// 各合计用的明细集计初期化
				sumDetail = 0;

				// 该行没有明细行的场合
				if (!passRecord.detailSQL) {
					editCacheCell(cacheCell, passRecord, baseDate, null);
					cacheCell['detailNo'] = 0;

					sumDetail = cacheCell['val'];

					// set passage value
					cacheTable[colIndex][curRow] = cacheCell;
					dataRow += 1;

					// 只做一次设定表的行头
					if (colIndex == 0) {
						// 设定表的行头于缓存
						// set dispNameKey items
						tempRowHead = {passageClass: 'dx-pa-cell dx-pa-pa-obj'};
						tempRowHead['column'] = '';
						tempRowHead['value'] = msg(passRecord.dispNameKey);
						cacheRowHead.push(tempRowHead);
						cacheRowHead.push(tempRowHead);
						// set one row with head
						cacheRowsHead.push(cacheRowHead);
					}

					// 数据行可否编辑缓存设定
					if (cacheRowsEdit[curRow] == undefined) {
						cacheRowsEdit[curRow] = cacheCell['isEdit'];
					} else if (!cacheRowsEdit[curRow] && cacheCell['isEdit']) {
						cacheRowsEdit[curRow] = true;
					}

					// 该行有明细行的场合
				} else {
					hasDetail = true;

					// 只做一次设定表的行头
					if (colIndex == 0) {
						// 设定表的行头于缓存
						// set detail dispNameKey items
						tempRowHead = {passageClass: 'dx-pa-cell dx-pa-pa-obj'};
						tempRowHead['column'] = '';
						tempRowHead['value'] = msg(passRecord.dispNameKey) + msg('passage_all');
						cacheRowHead.push(tempRowHead);
						cacheRowHead.push(tempRowHead);
						// set one row with head
						cacheRowsHead.push(cacheRowHead);
					}

					// 编辑明细合计行
					editCacheCell(cacheCell, passRecord, baseDate, null);
					cacheCell['isEdit'] = false;
					cacheCell['detailNo'] = 0;

					// set passage value
					cacheTable[colIndex][curRow] = cacheCell;
					dataRow += 1;

					// 数据行可否编辑缓存设定
					cacheRowsEdit[curRow] = false;

					// 明细行标签初期化
					index = 0;

					// 编辑明细行
					$.each(passRecord.resultList, function (j, result) {
						isUsedMainId = true;
						for (var key in passRecord.mainIds) {
							if (curMainId.indexOf(key + result['passage_' + key]) < 0) {
								isUsedMainId = false;
								break;
							}
						}
						// 同一mainId的场合
						if (isUsedMainId) {
							cacheCell = {};
							// 明细行标签设定
							index += 1;

							// reset current cell
							curRow = dataRow;

							// 只做一次设定表的行头
							if (colIndex == 0) {
								// 设定表的行头于缓存
								tempCacheRowHead = [];
								// set group and display items
								$.each(passRecord.fields, function (l, field) {
									if (l == 0) {
										tempRowHead = {passageClass: 'dx-pa-cell dx-pa-group-cols'};
									} else {
										tempRowHead = {passageClass: 'dx-pa-cell dx-pa-disp-cols'};
									}
									tempRowHead['column'] = field.column;
									tempRowHead['value'] = fieldText(field);
									tempCacheRowHead.push(tempRowHead);
								});
								// set detail dispNameKey items
								tempRowHead = {passageClass: 'dx-pa-cell dx-pa-pa-group'};
								tempRowHead['column'] = '';
								tempRowHead['value'] = msg(passRecord.dispNameKey);
								tempCacheRowHead.push(tempRowHead);
								tempRowHead = {passageClass: 'dx-pa-cell dx-pa-pa-group-detail'};
								tempRowHead['column'] = '';
								tempRowHead['value'] = msg(result['detailName']);
								tempCacheRowHead.push(tempRowHead);
								// set one row with head
								cacheRowsHead.push(tempCacheRowHead);
							}

							// set 行映射(cacheRowsMap)
							cacheRowMap = {
								curRow: curRow,
								updStatement: passRecord.updStatement
							};
							cacheRowsMap[passRecord.mainId + ','
							+ passRecord.dispNameKey + ',' + result['detailId']] = cacheRowMap;

							editCacheCell(cacheCell, passRecord, baseDate, result['detailId']);
							cacheCell['detailId'] = result['detailId'];
							cacheCell['detailNo'] = index;

							// 明细总计数
							sumDetail += cacheCell['val'];
							if (passRecord.totalRow) {
								// 明细分合计数
								var newCacheCell = {
									isEdit: false,
									dispNameKey: passRecord.dispNameKey
									,
									detailId: result['detailId'],
									detailName: result['detailName']
								};
								newCacheCell['digit'] = cacheCell['digit'];
								newCacheCell['totalCol'] = cacheCell['totalCol'];
								newCacheCell['uneditPartBgColor'] = cacheCell['uneditPartBgColor'];
								newCacheCell['uneditPartFgColor'] = cacheCell['uneditPartFgColor'];
								if (sumDetailMap[passRecord.dispNameKey] == undefined) {
									detailMap = {};

									newCacheCell['val'] = cacheCell['val'];
									detailMap[result['detailId']] = newCacheCell;
									sumDetailMap[passRecord.dispNameKey] = detailMap;
								} else {
									detailMap = sumDetailMap[passRecord.dispNameKey];

									if (detailMap[result['detailId']] == undefined) {
										newCacheCell['val'] = cacheCell['val'];
										detailMap[result['detailId']] = newCacheCell;
									} else {
										newCacheCell = detailMap[result['detailId']];
										newCacheCell['val'] += cacheCell['val'];
										detailMap[result['detailId']] = newCacheCell;
									}

									sumDetailMap[passRecord.dispNameKey] = detailMap;
								}
							}

							// set passage value
							cacheTable[colIndex][curRow] = cacheCell;
							dataRow += 1;

							// 数据行可否编辑缓存设定
							if (cacheRowsEdit[curRow] == undefined) {
								cacheRowsEdit[curRow] = cacheCell['isEdit'];
							} else if (!cacheRowsEdit[curRow] && cacheCell['isEdit']) {
								cacheRowsEdit[curRow] = true;
							}
						}
					});

					// 设定明细总计
					cacheTable[colIndex][curRow - index]['val'] = sumDetail;
				}

				// 下一行数据的大区分是否变化
				var isChange = isGroupColChange(cacheRowHead, passRecords, i);

				// hold passage sum value
				if (passRecord.totalRow) {
					// 保存小计行数据
					var subCacheCell = {isEdit: false, dispNameKey: passRecord.dispNameKey};
					subCacheCell['digit'] = cacheCell['digit'];
					subCacheCell['totalCol'] = passRecord.totalCol;
					subCacheCell['uneditPartBgColor'] = cacheCell['uneditPartBgColor'];
					subCacheCell['uneditPartFgColor'] = cacheCell['uneditPartFgColor'];
					if (subTotalSumMap[passRecord.dispNameKey] == undefined) {
						subCacheCell['val'] = sumDetail;
						subTotalSumMap[passRecord.dispNameKey] = subCacheCell;
					} else {
						subCacheCell = subTotalSumMap[passRecord.dispNameKey];
						subCacheCell['val'] += sumDetail;
						subTotalSumMap[passRecord.dispNameKey] = subCacheCell;
					}

					// 保存总计行数据
					var newCacheCell = {isEdit: false, dispNameKey: passRecord.dispNameKey};
					newCacheCell['digit'] = cacheCell['digit'];
					newCacheCell['totalCol'] = passRecord.totalCol;
					newCacheCell['uneditPartBgColor'] = cacheCell['uneditPartBgColor'];
					newCacheCell['uneditPartFgColor'] = cacheCell['uneditPartFgColor'];
					if (totalSumMap[passRecord.dispNameKey] == undefined) {
						newCacheCell['val'] = sumDetail;
						totalSumMap[passRecord.dispNameKey] = newCacheCell;
					} else {
						newCacheCell = totalSumMap[passRecord.dispNameKey];
						newCacheCell['val'] += sumDetail;
						totalSumMap[passRecord.dispNameKey] = newCacheCell;
					}
				}

				// 下一行数据的大区分变化的场合
				if (isChange) {
					// set passage sub total sum info
					$.each(subTotalSumMap, function (key, calSum) {
						// 只做一次设定表的行头
						if (colIndex == 0) {
							// 设定表的行头于缓存
							tempCacheRowHead = [];
							// set group and display items
							$.each(passRecord.fields, function (j, field) {
								if (j == 0) {
									tempRowHead = {passageClass: 'dx-pa-cell dx-pa-group-cols'};
									tempRowHead['column'] = '';
									tempRowHead['value'] = cacheRowHead[0].value;
									tempCacheRowHead.push(tempRowHead);
								} else {
									tempRowHead = {passageClass: 'dx-pa-cell dx-pa-subTotal'};
									tempRowHead['column'] = '';
									tempRowHead['value'] = msg('Sub_Total');
									tempCacheRowHead.push(tempRowHead);
								}
							});
							// set detail dispNameKey items
							tempRowHead = {passageClass: 'dx-pa-cell dx-pa-pa-subTotal'};
							tempRowHead['column'] = '';
							tempRowHead['value'] = msg(calSum['dispNameKey']);
							tempCacheRowHead.push(tempRowHead);
							tempCacheRowHead.push(tempRowHead);
							// set one row with head
							cacheRowsHead.push(tempCacheRowHead);
						}

						curRow = dataRow;
						// 设定推移小计数于缓存
						cacheTable[colIndex][curRow] = calSum;
						dataRow += 1;

						// 数据行可否编辑缓存设定
						cacheRowsEdit[curRow] = false;

						// set 行映射(cacheRowsMap)
						$.each(cacheRowsMap, function (cacheRowKey, cacheRow) {
							var strKey = cacheRowKey.split(',');
							if (strKey[1] == key && cacheRow['subTotalRow'] == undefined) {
								cacheRow['subTotalRow'] = curRow;
							}
						});
					});

					// clear 小计映射数据
					subTotalSumMap = {};
				}

				// clear
				cacheCell = {};
			});

			// set passage total sum info
			maxRow = dataRow;
			$.each(totalSumMap, function (key, sum) {
				// 只做一次设定表的行头
				if (cacheRowsHead.length <= maxRow) {
					// 设定表的行头于缓存
					tempCacheRowHead = [];
					// set group and display items
					tempRowHead = {passageClass: 'dx-pa-cell dx-pa-total'};
					tempRowHead['column'] = '';
					tempRowHead['value'] = msg('Total');
					$.each(passRecords[0].fields, function (j, field) {
						tempCacheRowHead.push(tempRowHead);
					});
					// set detail dispNameKey items
					tempRowHead = {passageClass: 'dx-pa-cell dx-pa-pa-total'};
					tempRowHead['column'] = '';
					tempRowHead['value'] = msg(sum['dispNameKey']);
					tempCacheRowHead.push(tempRowHead);
					tempCacheRowHead.push(tempRowHead);
					// set one row with head
					cacheRowsHead.push(tempCacheRowHead);
				}

				// 设定推移总计数于缓存
				cacheTable[colIndex][maxRow] = sum;

				// 数据行可否编辑缓存设定
				cacheRowsEdit[maxRow] = false;

				// set 行映射(cacheRowsMap)
				$.each(cacheRowsMap, function (cacheRowKey, cacheRow) {
					var strKey = cacheRowKey.split(',');
					if (strKey[1] == key) {
						cacheRow['totalRow'] = maxRow;
					}
				});

				maxRow += 1;
			});

			// set passage detail sum info
			$.each(sumDetailMap, function (sumKey, detailMap) {
				$.each(detailMap, function (key, detail) {
					// 只做一次设定表的行头
					if (cacheRowsHead.length <= maxRow) {
						// 设定表的行头于缓存
						tempCacheRowHead = [];
						// set group and display items
						tempRowHead = {passageClass: 'dx-pa-cell dx-pa-detailTotal'};
						tempRowHead['column'] = '';
						tempRowHead['value'] = msg(detail['dispNameKey']) + msg('passage_cal');
						$.each(passRecords[0].fields, function (j, field) {
							tempCacheRowHead.push(tempRowHead);
						});
						// set detail dispNameKey items
						tempRowHead = {passageClass: 'dx-pa-cell dx-pa-pa-detailTotal'};
						tempRowHead['column'] = '';
						tempRowHead['value'] = detail['detailName'];
						tempCacheRowHead.push(tempRowHead);
						tempCacheRowHead.push(tempRowHead);
						// set one row with head
						cacheRowsHead.push(tempCacheRowHead);
					}

					// 设定明细总计数于缓存
					cacheTable[colIndex][maxRow] = detail;

					// 数据行可否编辑缓存设定
					cacheRowsEdit[maxRow] = false;

					// set 行映射(cacheRowsMap)
					$.each(cacheRowsMap, function (cacheRowKey, cacheRow) {
						var strKey = cacheRowKey.split(',');
						if (strKey[1] == sumKey && strKey[2] == key) {
							cacheRow['detailRow'] = maxRow;
						}
					});

					maxRow += 1;
				});
			});

		});
	}

	// 判断是否下一行数据的大区分发生变化
	function isGroupColChange(cacheRowHead, passRecords, index) {
		var nextGroupCols = "";

		// 分组字段存在的时候
		if (form.groupCols != "") {
			// 取得下一行数据的大区分
			if (passRecords.length > index + 1) {
				nextGroupCols = fieldText(passRecords[index + 1].fields[0]);
			} else {
				nextGroupCols = null;
			}

			if (nextGroupCols == null || cacheRowHead[0].value != nextGroupCols) {
				return true;
			}
		}

		return false;
	}

	// 编辑单元格于缓存
	function editCacheCell(cacheCell, passRecord, baseDate, detailId) {
		var hasDisFlag = false;
		var hasCalFlag = false;
		$.each(passRecord.passageFields, function (i, passageField) {
			if (passageField.cond == "" || evaluate(passageField.cond, form)) {
				if (passageField.type == 0 && !hasDisFlag) {
					hasDisFlag = true;
					cacheCell['id'] = passageField.id;
					cacheCell['mainId'] = passRecord.mainId;
					cacheCell['mainIds'] = passRecord.mainIds;
					cacheCell['digit'] = passRecord.decimalDigit;
					cacheCell['totalCol'] = passRecord.totalCol;
					cacheCell['editPartBgColor'] = passRecord.editPartBgColor;
					cacheCell['editPartFgColor'] = passRecord.editPartFgColor;
					cacheCell['uneditPartBgColor'] = passRecord.uneditPartBgColor;
					cacheCell['uneditPartFgColor'] = passRecord.uneditPartFgColor;

					if (passageField.sql) {
						cacheCell['val'] = 0;
						$.each(passageField.resultList, function (j, result) {
							isUsedMainId = true;
							for (var key in passRecord.mainIds) {
								if (curMainId.indexOf(key + result['passage_' + key]) < 0) {
									isUsedMainId = false;
									break;
								}
							}

							if (isUsedMainId) {
								if (result['passageDate'].length == 6 && result['passageDate'] == baseDate.substring(0, 6)
									|| result['passageDate'] == baseDate) {
									if (detailId == null || result['detailId'] == detailId) {
										cacheCell['val'] = result['number'];
										return false;
									}
								}
							}
						});
					} else {
						cacheCell['val'] = evaluate(passageField.formula, form, 0);
						cacheCell['dis_formula'] = passageField.formula;
					}
					cacheCell['originalVal'] = cacheCell['val'];
				} else if (passageField.type == 1 && !hasCalFlag) {
					hasCalFlag = true;
					cacheCell['cal_formula'] = passageField.formula;
				}
				cacheCell['isEdit'] = evaluate(passRecord.editCond, form);
				if (hasDisFlag && hasCalFlag) {
					return false;
				}
			}
		});
		cacheCell['mainId'] = passRecord.mainId;
		cacheCell['passageObj'] = passRecord.dispNameKey;
		cacheCell['isChange'] = false;
	}

	// edit data with dataTable
	function createTable() {
		// 重置推移部内容
		var $tr_tbody = $('#' + formId + 'detail').find('tbody');
		$tr_tbody.empty();

		var sumColumn = 0;
		var cacheCell = {};
		var tempHtml = '';
		var tempId = '';

		// edit dataTable with passage
		$.each(cacheRowsHead, function (rowIndex, cacheRowHead) {
			tempHtml += rowStartHtml;

			// edit passage head data
			$.each(cacheRowHead, function (i, rowHead) {
				tempId = '';
				if (rowHead.id && rowHead.id != rowHead.value) {
					tempId = rowHead.id + ' ';
				}
				tempHtml += '<td class="' + rowHead.passageClass + '">' + tempId + rowHead.value + cellEndHtml;
			});

			// edit passage data cell
			sumColumn = 0;
			$.each(headPassColumns, function (colIndex, headPassColumn) {
				cacheCell = cacheTable[colIndex][rowIndex];

				sumColumn += cacheCell['val'];

				tempHtml += editCellAttr(cacheCell, rowIndex, false);
				if (cacheCell['isEdit']) {
					tempHtml += editCellValAttr(cacheCell, rowIndex, colIndex, false);
				} else {
					tempHtml += editCellValAttr(cacheCell, rowIndex, colIndex, true);
				}
				tempHtml += cellEndHtml;
			});

			// edit column total cell
			tempHtml += editCellAttr(cacheCell, rowIndex, true);
			var tempCacheCell = {};

			tempCacheCell['isEdit'] = false;
			tempCacheCell['totalCol'] = cacheCell['totalCol'];
			if (cacheCell['totalCol']) {
				tempCacheCell['val'] = sumColumn;
				tempCacheCell['digit'] = cacheCell['digit'];
			} else {
				tempCacheCell['val'] = '--';
				tempCacheCell['digit'] = '';
			}
			tempCacheCell['uneditPartBgColor'] = cacheCell['uneditPartBgColor'];
			tempCacheCell['uneditPartFgColor'] = cacheCell['uneditPartFgColor'];
			if (cacheTable[headPassColumns.length] == null) {
				cacheTable[headPassColumns.length] = new Array();
			}
			cacheTable[headPassColumns.length][rowIndex] = tempCacheCell;
			tempHtml += editCellValAttr(tempCacheCell, rowIndex, headPassColumns.length, true);

			tempHtml += rowEndHtml;
		});
		$tr_tbody.append(tempHtml);

		$container.find('input.dx-number').each(function () {
			var $this = $(this);
			if ($this.attr('digit') != '') {
				$this.autoNumeric('init', formatOpts_passage(20, $this.attr('digit')));
			}
		});
	}

	function editCellAttr(cacheCell, rowIndex, isReadonly) {
		var strtd = '<td style="';
		if (cacheCell['isEdit'] && !isReadonly) {
			strtd += 'background-color:' + cacheCell['editPartBgColor'] + ';';
			strtd += 'color: ' + cacheCell['editPartFgColor'] + ';" ';
			strtd += 'class="dx-pa-cell dx-pa-cell-edit"';
		} else {
			strtd += 'background-color:' + cacheCell['uneditPartBgColor'] + ';';
			strtd += 'color: ' + cacheCell['uneditPartFgColor'] + ';" ';
			strtd += 'class="dx-pa-cell dx-pa-cell-unedit"';
		}
		strtd += '>';

		return strtd;
	}

	function editCellValAttr(cacheCell, row, column, isReadonly) {
		var strStyle;
		var strInput;
		if (!isReadonly) {
			strInput = inputHtml;

			strStyle = 'background-color:' + cacheCell['editPartBgColor'] + '!important' + ';';
			strStyle += 'color:' + cacheCell['editPartFgColor'] + ';';
		} else {
			strInput = uninputHtml;

			strStyle = 'background-color:' + cacheCell['uneditPartBgColor'] + ';';
			strStyle += 'color:' + cacheCell['uneditPartFgColor'] + ';';
		}

		if (!cacheCell['totalCol'] && column == headPassColumns.length) {
			strInput = strInput.replace('inputValue', '--');
			strInput = strInput.replace('inputDigit', '');
		} else {
			strInput = strInput.replace('inputValue', cacheCell.val.toFixed(cacheCell.digit));
			strInput = strInput.replace('inputDigit', cacheCell['digit']);
		}
		strInput = strInput.replace('inputStyle', strStyle);
		strInput = strInput.replace('inputRow', row);
		strInput = strInput.replace('inputColumn', column);

		return strInput;
	}

	// when passage has been changed
	$grid.on('change', 'input', function (event) {
		var $this = $(this);
		var changeRowIndex = $this.attr('row');
		var changeColIndex = $this.attr('column');

		var changeCell = cacheTable[changeColIndex][changeRowIndex];
		var preCellValue = changeCell['val'];
		var changeCellValue = disFormatNumber(this.value);

		// reset the first changed cell
		if (String(changeCellValue) == "") {
			this.value = formatNumber(preCellValue, changeCell['digit']);
			return;
		} else {
			if (changeCellValue == 0) {
				this.value = formatNumber(0, changeCell['digit']);
			}

			changeCell['val'] = changeCellValue;
			if (changeCellValue == changeCell['originalVal']) {
				changeCell['isChange'] = false;
			} else {
				changeCell['isChange'] = true;
			}
			cacheTable[changeColIndex][changeRowIndex] = changeCell;
		}

		curMainId = changeCell['mainId'];

		var tempCellValue = preCellValue - disFormatNumber(changeCellValue);
		// reset the first column total cell
		calTotal(changeRowIndex, headPassColumns.length, tempCellValue);

		// reset the first sum cells
		changeRowIndex = calAllTotal(changeCell, changeRowIndex, changeColIndex, tempCellValue);

		// hold the original cells
		var hasRelatedCells = false;
		var tempCell = {};
		var cacheCell = {};
		var cellValue = 0;
		var tempRelatedCells = [];
		var relatedCells = [];
		relatedCells.push({
			row: changeRowIndex
			, col: changeColIndex
		});
		// reset the related cell
		while (relatedCells.length > 0) {
			hasRelatedCells = true;
			originalRow = relatedCells[0]['row'];
			originalCol = relatedCells[0]['col'];
			tempRelatedCells = [];
			for (var i = 1; i < relatedCells.length; i++) {
				tempRelatedCells.push(relatedCells[i]);
			}
			relatedCells = tempRelatedCells;

			// draw related cells
			$.each(headPassColumns, function (colIndex, headPassColumn) {
				$.each(passageObjs, function (rowIndex, passageObj) {
					// hold current cell
					relatedFlag = false;
					curRow = parseInt(cacheRowsMap[curMainId + ',' + passageObj]['curRow']);
					curCol = colIndex;

					$.each(relatedCells, function (index, relatedCell) {
						// when have calculated cell
						if (curRow == relatedCell['row'] && curCol == relatedCell['col']) {
							relatedFlag = true;
							return;
						}
					});
					if (!relatedFlag) {
						// update cell data
						cacheCell = cacheTable[curCol][curRow];
						// set param
						setFormVariable(form, 'id', curMainId);
						setFormVariable(form, 'col', colIndex + 1);
						setFormVariable(form, 'row', curRow + 1);
						$.each(cacheCell.mainIds, function (key, value) {
							setFormVariable(form, key, value);
						});

						setFormVariable_passage(headPassColumn, false);
						// calculate cell data
						cellValue = evaluate(cacheCell['cal_formula'], form);

						// when have calculated cell
						if (relatedFlag) {
							tempCell = document.getElementById(createCellId(curRow, curCol));
							// draw the related cell
							tempCell.innerHTML = calInputValue(tempCell.innerHTML, $(tempCell.innerHTML).val() - cellValue);

							var tempCellValue = cacheCell['val'] - cellValue;
							// reset the column total cell
							calTotal(curRow, headPassColumns.length, tempCellValue);

							// reset the total cell
							curRow = calAllTotal(cacheCell, curRow, curCol, tempCellValue);

							// update cache with changed cell
							cacheCell['val'] = cellValue;
							if (cellValue == cacheCell['originalVal']) {
								cacheCell['isChange'] = false;
							} else {
								cacheCell['isChange'] = true;
							}
							cacheTable[curCol][curRow] = cacheCell;

							// hold original related cells
							relatedCells.push({
								row: curRow
								, col: curCol
							});
						}
					}
				});
			});
		}

		$container.find('input.dx-number').each(function () {
			var $this = $(this);
			if ($this.attr('digit') != '') {
				$this.autoNumeric('init', formatOpts_passage(20, $this.attr('digit')));
			}
		});

		event.cancelBubble = true;
	});

	// 保存按钮
	$('#' + formId + 'cancel').bind('click', function () {
		if (form.hasAppend) {
			reset(true);
			return;
		}
		$('#' + formId + 'startDate').attr('disabled', false);
		$('#' + formId + 'endDate').attr('disabled', false);
		$container.find('.dx-filter-exec').each(function () {
			$(this).attr('disabled', false);
		});
		if (form.searchConditions.length > 0) {
			// 设定全局检索条件的值
			var ids = $('#' + formId + 'ids').val();

			$.each(ids.split(","), function (i, fid) {
				$('#' + fid).attr('disabled', false);
			});
		}
	});

	// 保存按钮
	$('#' + formId + 'save').bind('click', function () {
		// edit params
		var params = {
			id: formId
			, start_date: $('#' + formId + 'hidStartDate').val()
			, end_date: $('#' + formId + 'hidEndDate').val()
		};

		// set be changed data
		var cacheCell = {};
		var cacheRow = "";
		var param = {};
		var data = {};
		var datas = [];
		var hasChange = false;

		$.each(cacheRowsEdit, function (rowIndex, isRowEdit) {
			if (isRowEdit) {
				cacheCell = cacheTable[0][rowIndex];

				if (cacheCell.detailNo > 0) {
					cacheRow = cacheRowsMap[cacheCell.mainId + ','
					+ cacheCell.passageObj + ',' + cacheCell.detailId];
				} else {
					cacheRow = cacheRowsMap[cacheCell.mainId + ',' + cacheCell.passageObj];
				}

				if (cacheRow.updStatement != "") {
					if (param[cacheRow.updStatement] == undefined) {
						datas = [];
					} else {
						datas = param[cacheRow.updStatement];
					}

					$.each(headPassColumns, function (colIndex, headPassColumn) {
						cacheCell = cacheTable[colIndex][rowIndex];

						if (cacheCell['isEdit'] && cacheCell['isChange']) {
							data = {};
							data.mainIds = cacheCell.mainIds;
							data.date = headPassColumn;
							data.dateIndex = colIndex;
							data.detailId = cacheCell.detailId;
							data.number = cacheCell.val;

							datas.push(data);
						}
					});

					if (datas.length > 0) {
						hasChange = true;
						param[cacheRow.updStatement] = datas;
					}
				}

			}
		});

		if (form.searchConditions.length > 0) {
			// 设定全局检索条件的值
			var ids = $('#' + formId + 'ids').val()

			$.each(ids.split(","), function (i, fid) {
				var field = w(fid);
				param[field.column] = field.val();
			});
		}

		if (!hasChange) {
			messageBox('DCS-911');
		} else {
			params.param = param;

			// call save method
			postJson('/passage/save.do', params,
				function (retData) {
					messageBox(retData.msg);
				});
		}
	});

	// 导出按钮
	$('#' + formId + 'export').bind('click', function () {
		var i = 0;
		var j = 0;

		var spans = new Array(cacheRowsHead[0].length);
		var span = {};
		var exportData = new Array();
		var isKeyChange = false;
		var cacheCell = {};
		var cacheFirstCell = {};
		var cellVal = {};

		var curTable = document.getElementById(formId + 'detail');
		var rowCount = curTable.rows.length;          // Max行数
		var colCount = curTable.rows[0].cells.length; // Max列数
		var obj = null;

		for (i = 0; i < rowCount; i++) {
			isKeyChange = false;
			exportData[i] = new Array();

			if (i > 0 && i < rowCount - 1 && form.groupCols == "") {
				cacheFirstCell = cacheTable[0][i];
				cacheCell = cacheTable[0][i - 1];
				if (cacheCell.mainId != cacheFirstCell.mainId) {
					isKeyChange = true;
				}
			}

			for (j = 0; j < colCount + 1; j++) {
				cellVal = {};
				obj = document.getElementById(formId + "_" + i.toString() + "_" + j.toString());

				// header行
				if (i == 0) {
					if (obj == null) {
						obj = document.getElementById(formId + "_" + (i).toString() + "_" + (j - 1).toString());
					}
					cellVal.val = obj.textContent;
					cellVal.bgColor = "gray";
					cellVal.color = "";
					cellVal.isEdit = false;
					cellVal.type = "head"
					cellVal.isKeyChange = isKeyChange;
					exportData[i].push(cellVal);

					// rowheader列
				} else if (j < cacheRowsHead[0].length) {
					if (obj != null) {
						span = {};

						if (obj.rowSpan == 1 && obj.colSpan > 1) {
							span.colSpan = true;
							for (var colSpanIndex = 1; colSpanIndex < obj.colSpan; colSpanIndex++) {
								spans[j + colSpanIndex] = span;
							}
						} else if (obj.rowSpan > 1 && obj.colSpan == 1) {
							span.rowSpan = true;
							spans[j] = span;
						} else if (obj.rowSpan > 1 && obj.colSpan > 1) {
							span.rowSpan = true;
							spans[j] = span;

							span = {};
							span.colSpan = true;
							for (var colSpanIndex = 1; colSpanIndex < obj.colSpan; colSpanIndex++) {
								spans[j + colSpanIndex] = span;
							}
						}

						cellVal.val = obj.textContent;
						cellVal.bgColor = "";
						cellVal.color = "";
						cellVal.isEdit = false;
						cellVal.type = "rowHead";
						cellVal.isKeyChange = isKeyChange;

						exportData[i].push(cellVal);

					} else {
						span = spans[j];
						var tempCellVal;
						if (span.rowSpan) {
							tempCellVal = exportData[i - 1][j];
						} else if (span.colSpan) {
							tempCellVal = exportData[i][j - 1];
						}

						cellVal.val = tempCellVal.val;
						cellVal.bgColor = tempCellVal.bgColor;
						cellVal.color = tempCellVal.color;
						cellVal.isEdit = tempCellVal.isEdit;
						cellVal.type = tempCellVal.type;
						cellVal.isKeyChange = isKeyChange;
						exportData[i].push(cellVal);
					}

					// 推移数据列
				} else {
					cacheCell = cacheTable[j - cacheRowsHead[0].length][i - 1];
					cellVal.val = formatNumber(cacheCell['val'], cacheCell["digit"]);
					cellVal.bgColor = obj.style.backgroundColor;
					cellVal.color = obj.style.color;
					cellVal.isEdit = cacheCell['isEdit'];
					cellVal.type = "data";
					exportData[i].push(cellVal);
				}
			}
		}
		// edit params
		var params = {
			id: formId
			, start_date: $('#' + formId + 'hidStartDate').val()
			, end_date: $('#' + formId + 'hidEndDate').val()
			, exportData: exportData
		};

		// call export method
		fileDownload('/passage/export.do', params, null);
	});

	// 追加按钮
	$('#' + formId + 'add').bind('click', function () {

	});

	function setFormVariable_passage(headPassColumn, hasSysDate) {
		var baseDate = headPassColumn.split('-');
		if (baseDate.length == 2) {
			if (hasSysDate) {
				setFormVariable(form, 'sysDate', $.format.date(new Date(), 'yyyyMM'));
			}
			setFormVariable(form, 'date', $.format.date(new Date(baseDate[0], baseDate[1] - 1), 'yyyyMM'));

			baseDate = baseDate[0] + baseDate[1] + "01";
		} else {
			if (hasSysDate) {
				setFormVariable(form, 'sysDate', $.format.date(new Date(), 'yyyyMMdd'));
			}
			setFormVariable(form, 'date', $.format.date(new Date(baseDate[0], baseDate[1] - 1, baseDate[2]), 'yyyyMMdd'));

			baseDate = baseDate[0] + baseDate[1] + baseDate[2];
		}

		return baseDate;
	}

	// 合并单元格
	function uniteTable(colStart, colEnd) {
		//colLength-- 需要合并单元格的列 1开始
		var i = 0;
		var j = 0;
		var rowCount = table.rows.length; //   行数
		var colCount = table.rows[0].cells.length; //   列数
		var obj1 = null;
		var obj2 = null;
		var cacheCell1 = null;
		var cacheCell2 = null;

		//合并行
		for (j = colStart; j < colCount; j++) {
			if (j == colEnd || rowCount == 1) break;
			cacheCell1 = cacheTable[0][0];
			obj1 = document.getElementById(formId + "_1_" + j.toString());
			for (i = 2; i < rowCount; i++) {
				obj2 = document.getElementById(formId + "_" + i.toString() + "_" + j.toString());
				cacheCell2 = cacheTable[0][i - 1];

				if ((form.groupCols != "" || cacheCell1["mainId"] == cacheCell2["mainId"])
					&& obj1.innerHTML == obj2.innerHTML) {
					if (j == colStart || document.getElementById(formId + "_" + i + "_" + (j - 1)) == null) {
						obj1.rowSpan++;
						obj2.parentNode.removeChild(obj2);
					} else {
						obj1 = document.getElementById(formId + "_" + i.toString() + "_" + j.toString());
					}
				} else {
					obj1 = document.getElementById(formId + "_" + i.toString() + "_" + j.toString());
				}

				cacheCell1 = cacheTable[0][i - 1];
			}
		}
		//合并列
		for (i = 0; i < rowCount; i++) {
			colCount = table.rows[i].cells.length;
			obj1 = document.getElementById(table.rows[i].cells[colStart].id);
			var tempIndex = obj1.id.split("_").length;
			for (j = colStart + 1; j < colCount; j++) {
				if (obj1.id.split("_")[tempIndex - 1] >= colEnd - obj1.colSpan) break;

				obj2 = document.getElementById(table.rows[i].cells[j].id);
				if (colStart == 0
					&& obj1.innerHTML.indexOf(msg('Total')) < 0
					&& obj1.innerHTML.indexOf(msg('Sub_Total')) < 0
					&& obj1.innerHTML.indexOf(msg('passage_all')) < 0) {
					obj1 = obj2;
					j = j + obj1.colSpan - 1;

					continue;
				}

				if (obj1.innerHTML == obj2.innerHTML) {
					obj1.colSpan++;
					obj2.parentNode.removeChild(obj2);
					j = j - 1;
				}
				else {
					obj1 = obj2;
					j = j + obj1.colSpan - 1;
				}
			}
		}
	}

	function calAllTotal(changeCell, changeRowIndex, changeColIndex, tempCellValue) {
		var cacheRow = {};
		// 明细数据存在的场合
		if (changeCell['detailNo'] > 0) {
			changeRowIndex = changeRowIndex - changeCell['detailNo'];

			// 映射行取得
			cacheRow = cacheRowsMap[curMainId + ',' + changeCell['passageObj'] + ',' + changeCell['detailId']];

			// 明细(总)行缓存再计算
			changeCell = cacheTable[changeColIndex][changeRowIndex];
			changeCell['val'] -= tempCellValue;
			cacheTable[changeColIndex][changeRowIndex] = changeCell;
			// 明细(总)行再计算
			var tempCell = document.getElementById(createCellId(changeRowIndex, changeColIndex));
			tempCell.innerHTML = calInputValue(tempCell.innerHTML, tempCellValue);
			// 明细(总)行(column total)再计算
			tempCell = document.getElementById(createCellId(changeRowIndex, headPassColumns.length));
			tempCell.innerHTML = calInputValue(tempCell.innerHTML, tempCellValue);

			if (cacheRow['detailRow'] != null) {
				// 明细计行再计算
				calTotal(cacheRow['detailRow'], changeColIndex, tempCellValue);
				// 明细计行(column total)再计算
				calTotal(cacheRow['detailRow'], headPassColumns.length, tempCellValue);
			}
		} else {
			// 映射行取得
			cacheRow = cacheRowsMap[curMainId + ',' + changeCell['passageObj']];
		}

		if (cacheRow['subTotalRow'] != null) {
			// 小计行再计算
			calTotal(cacheRow['subTotalRow'], changeColIndex, tempCellValue);
			// 小计行(column total)再计算
			calTotal(cacheRow['subTotalRow'], headPassColumns.length, tempCellValue);
		}

		if (cacheRow['totalRow'] != null) {
			// 合计行再计算
			calTotal(cacheRow['totalRow'], changeColIndex, tempCellValue);
			// 合计行(column total)再计算
			calTotal(cacheRow['totalRow'], headPassColumns.length, tempCellValue);
		}

		// 返回实际变化的行数
		return changeRowIndex;
	}

	function calTotal(rowIndex, colIndex, tempCellValue) {
		// 集计行缓存再计算
		var cacheCell = cacheTable[colIndex][rowIndex];
		if (headPassColumns.length != colIndex || cacheCell['totalCol'] != '') {
			cacheCell['val'] = cacheCell['val'] - tempCellValue;
			cacheTable[colIndex][rowIndex] = cacheCell;
			// 集计行再计算
			var tempCell = document.getElementById(createCellId(rowIndex, colIndex));
			tempCell.innerHTML = calInputValue(tempCell.innerHTML, tempCellValue);
		}
	}

	function createCellId(rowIndex, colIndex) {
		var tempColIndex = cacheRowsHead[0].length + parseInt(colIndex);
		return formId + '_' + (parseInt(rowIndex) + 1) + '_' + tempColIndex;
	}

	function calInputValue(innerHTML, tempCellValue) {
		var cellVlue = $(innerHTML).val();
		var tempValue = disFormatNumber(cellVlue) - tempCellValue;

		return innerHTML.replace('value="' + cellVlue + '"', 'value="' + tempValue + '"');
	}

	// create new row with html
	function createBlankRows() {
		// create 空行
		var passageObjCount = 1;
		if (hasDetail) {
			passageObjCount = 2;
		}
		var maxcellCount = form.headColumns.length + passageObjCount + headPassColumns.length;
		var tempHtml = '<tr>';
		for (var i = 0; i < maxcellCount; i++) {
			tempHtml += '<td style="height:0">';
			tempHtml += cellEndHtml;
		}
		tempHtml += rowEndHtml;

		return tempHtml;
	}

	/**
	 * 为table指定行添加一行
	 *
	 * table 表id
	 * row 行数，如：0->第一行 1->第二行 -2->倒数第二行 -1->最后一行
	 * trHtml 添加行的html代码
	 *
	 */
	function addTr(table, row, trHtml) {
		//获取table最后一行 $("#tab tr:last")
		//获取table第一行 $("#tab tr").eq(0)
		//获取table倒数第二行 $("#tab tr").eq(-2)
		var $tr = $("#" + table + " thead tr").eq(row);
		if ($tr.size() == 0) {
			alert("add error!");
			return;
		}
		$tr.before(trHtml);
	}

	/**
	 * 將table指定行刪除
	 *
	 * table 表id
	 * row 行数，如：0->第一行 1->第二行 -2->倒数第二行 -1->最后一行
	 *
	 */
	function delTr(table, row) {
		//获取table最后一行 $("#tab tr:last")
		//获取table第一行 $("#tab tr").eq(0)
		//获取table倒数第二行 $("#tab tr").eq(-2)
		var $tr = $("#" + table + " thead tr").eq(row);
		if ($tr.size() == 0) {
			alert("del error!");
			return;
		}
		$tr.remove();
	}
});

function formatDate_passage(headPassColumn) {
	var baseDate = headPassColumn.split('-');
	if (baseDate.length == 2) {
		return $.format.date(new Date(baseDate[0], baseDate[1] - 1), 'yyyyMMdd');
	} else {
		return $.format.date(new Date(baseDate[0], baseDate[1] - 1, baseDate[2]), 'yyyyMMdd');
	}
}

function formatOpts_passage(len, dec) {
	var maxLen;
	if (!len) {
		len = 20;
	}
	if (!dec) {
		maxLen = charRepeat('9', len);
	} else {
		maxLen = charRepeat('9', len) + '.' + charRepeat('9', parseInt(dec));
	}

	return {
		vMax: maxLen,
		vMin: '-' + maxLen,
		mRound: 'C'
	};
}

function disFormatNumber(x) {
	var temp = x.toString().replace(/\,/g, '');
	if (temp == "") {
		return 0;
	} else if (isNaN(temp)) {
		return "";
	}

	return parseFloat(temp);
}
