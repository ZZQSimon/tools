/*
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 9/30/14
 * Time: 1:54 PM
 */

"use strict";
registerInit('importer', function (form) {
	function load(ignore, callback) {
		if (!form.p('ajax')) {
			callback({data: []});
			return;
		}
		var result = form.p('result');
		if (result.details) {
			result.dataList = [];
			result.details.forEach(function (detail) {
				var data = $.extend({}, detail, result.headers[detail.voucher_id]);
				if (data.errors)
					data.errors.concat(detail.errors);
				else
					data.errors = detail.errors;
				result.dataList.push(data);
			});
		}

		callback({data: result.dataList});
	}

	var $container = form.get();
	var $saveButton = $container.find('button.dx-import-Save').attr('disabled', true);
	var $table = $container.find('table.dx-import-table').dataTable({
		ajax: load,
		paging: false,
		dom: 't',
		fnRowCallback: function (nRow, data) {
			var result = form.p('result');
			if (result.details) {
				if (data.errors) {
					data.errors.forEach(function (index) {
						$('td:eq(' + index + ')', nRow).css('background-color', 'pink');
					});
					$saveButton.attr('disabled', true);
				}
				return;
			}
			if (result.status[nRow._DT_RowIndex] !== null) {
				var errColIndexs = result.status[nRow._DT_RowIndex].split(',');
				if (errColIndexs !== null) {
					for (var i = errColIndexs.length; i--;) {
						var errColIndex = errColIndexs[i];
						$('td:eq(' + errColIndex + ')', nRow).css('background-color', 'pink');
					}
					$saveButton.attr('disabled', true);
				}
			}
		},
		columns: [{
			data: 'rowid'
		}, {
			data: 'import_date'
		}, {
			data: 'order_date'
		}, {
			data: 'customer_id'
		}, {
			data: 'receiver_id'
		}, {
			data: 'request_id'
		}, {
			data: 'receive_indicate_date'
		}, {
			data: 'classes'
		}, {
			data: 'deliver_date'
		}, {
			data: 'receive_time'
		}, {
			data: 'delivery_no'
		}, {
			data: 'receive_warehouse'
		}, {
			data: 'out_warehouse'
		}, {
			data: 'status'
		}, {
			data: 'currency_H'
		}, {
			data: 'exchange_rate'
		}, {
			data: 'exchange_type'
		}, {
			data: 'item_id'
		}, {
			data: 'customer_item_id'
		}, {
			data: 'trade_type'
		}, {
			data: 'quantity'
		}, {
			data: 'unit_price'
		}, {
			data: 'amount_D'
		}, {
			data: 'currency_D'
		}, {
			data: 'tax'
		}, {
			data: 'unit'
		}, {
			data: 'tax_type'
		}, {
			data: 'voucher_id'
		}]
	});
	initFileUploadButton($container.find('input.dx-import-import'), function (uuid) {
		dx.processing.open('Importing');
		postJson('/import/import.do', {fileId: uuid, id: form.id}, function (result) {
			form.p('ajax', true);
			form.p('result', result);
			$saveButton.attr('disabled', false);
			$table.DataTable().ajax.reload();
		}, function () {
			$saveButton.attr('disabled', true);
		})
	});

	//导入数据
	$saveButton.on('click', function () {
		dx.processing.open('Saving');
		postJson('/import/save.do', {id: form.id}, function () {
			$saveButton.attr('disabled', true);
			alert(msg('import_success_msg'));
		});
	});

});

