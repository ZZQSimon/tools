/**
 * User: huyannan
 * Date: 12/16/14
 * Time: 4:54 PM
 */

"use strict";

registerInit('import', function (form) {
	form.p('errorSilence', true);
	var $container = form.get();
	var validateOptions = $.extend({success: false}, defaultValidateOptions);
	validateOptions.unhighlight = false;
	$container.validate(validateOptions);

	w(form.grid);
	var grid = form.grid;
	var table = getTableDesc(form.tableName);
	var flag = 0;

	grid.records.forEach(function (record) {
		function renderError(rule, msg) {
			var $record = $('#' + record.id);
			$record.prop('title', msg);
			if (rule.is_error) {
				$record.addClass('has-error-row');
				flag = 2;
			} else {
				$record.addClass('has-warning-row');
				if (flag == 0)
					flag = 1;
			}
		}

		buildRecordCache(record, function(field){
			field.p('sync', true);
		});

		initField(record);
		if (form.noImportSql) {
			setDefaultValues(record, true);

			//表校验规则校验
			if (table.checkRules) {
				checkFormRules(record, table.checkRules, null, function (result) {
					renderError(result.rule, result.msg);
				});
			}
		} else if (record.errorCode >= 0)
			table.checkRules.some(function (rule) {
				if (rule.seq === record.errorCode) {
					var param = record.errorParam ? record.errorParam.split(',') : [];
					renderError(rule, msg(rule.error_msg_id, param));
					return true;
				}
			})
	});
	var $notify = $('#' + form.id + '_notify');
	switch (flag) {
		case 1:
			$notify.text(msg('import data with warning'))
				.addClass('dx-import-notify-warning')
				.removeClass('hidden');
			break;
		case 2:
			$notify.text(msg('import data with error'))
				.addClass('dx-import-notify-error')
				.removeClass('hidden');
			break;
	}

	if (!$container.valid())
		flag = 2;

	containerDisabled($container, true);

	if (flag < 2) {
		$container.find('button.dx-grid-Import').attr('disabled', false);

		//导入数据
		$container.find('button.dx-grid-Import').on('click', function () {
			var postData = {id: form.id, data: {},import_type:form.import_type};
			if (form.noImportSql) {
				buildGridData(grid, grid.records, form);
				$.each(grid.records, function (i, record) {
					var data = {};
					$.each(record.fields, function (i, field) {
						data[field.column] = field.val();
					});
					postData.data[record.id] = data;
				});
			}
			postJson('/data/import.do', postData, function () {
				form.p('dialog').close();			
				w(form.parent).updated();
				w(form.parent).p('reference',null);
				w(form.parent).get().parents(".modal-dialog").find(".bootstrap-dialog-close-button .close").click();
			});
		});
	}

	dx.processing.close();
});
