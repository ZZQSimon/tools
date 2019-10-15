/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 9/30/14
 * Time: 1:45 PM
 */

"use strict";

dx.modules.batch = {
	services: {
		"csv-import": function (form) {
			showDialogForm({
				url: '/csv/import.view',
				data: {parent: form.id,import_type:form.import_type},
				title: 'Import',
				shown: function (form, dialog) {
					form.p('dialog', dialog);
				}
			});
		}
	}
};

function registerBatchService(name, callback) {
	dx.modules.batch.services[name] = callback;
}

registerInit('batch', function (form) {
	var $form = form.get();
	var batch = getBatch(form.batchId);
	var api = form.import_type==2?batch.update_api:batch.api;
	var callback = dx.modules.batch.services[batch.interceptor_service];
	initSubmit($form, function () {
		form.reset();
		if (callback)
			callback(form);
	}, {
		dataProcessor: function () {
			return {
				id: form.id,
				param: buildApiFormParam(form, api, [form.id])
			};
		}
	});
	initComponent(form);
	setDefaultValues(form);
	evaluateReadonly(form);
	
	$form.find(".dx-upload-tishi").html("上传说明:只支持'.csv'、'.xls'、'.xls'类型文件");
});
