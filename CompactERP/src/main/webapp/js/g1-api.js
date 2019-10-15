
/**
 * build api calling parameter data
 * @param formData
 * @param api describe
 * @param rows
 * @param files
 * @returns {{}}
 */
function buildApiParams(formData, api, rows, files, isDetail) {
	function build(record) {
		// maybe no api calling
		if (!api)
			return {};
		var call_param = {};
        var url = dx.urlInterface[api.id];
        var apiTable = getTableDesc(url.id);
        if (isEmpty(apiTable)){
            apiTable = getTableDesc(url.url);
        }
		// set parameter map data, {param_name: param_value}
		apiTable.columns.forEach(function (column) {
			var column_name = column.column_name;
			// parameter need evaluate by expression
			if (api.params && (api.params[column_name] !== undefined)){
                if (!isEmpty(isDetail)){
                    call_param[column_name] = evaluate(api.params[column_name], {id: isDetail});
                }else{
                    var form = {};
                    form.id = record.parent;
                    call_param[column_name] = evaluate(api.params[column_name], form, null, null, null, null, record.id);
                }
            }else // parameter input by user
				call_param[column_name] = formData[column_name];
		});
		return call_param;
	}

	var ret = {};
	if (rows)
		$.each(rows, function (i, rid) {
			var record = w(rid);
			ret[rid] = build(record);
		});
	else
		ret._BLANK_ = build({});

	return {files: files, data: ret};
}

function buildApiCheckParam(formData, rows, files){
    var ret = {};
    if (rows)
        $.each(rows, function (i, rid) {
            var record = w(rid);
            ret[rid] = {rid: rid};
        });
    else
        ret._BLANK_ = {};

    return {files: files, data: ret};
}
/**
 * prepare data map using form data to build ap parameters
 * @param dialog
 * @param api api describe
 * @param rows
 * @returns {{}} api calling parameter to post, {id: {param: val, param:val}, id: {param:val}}
 */
function buildApiFormParam(dialog, api, rows, isDetail) {
	var formData = {};
	var files = {};
	$.each(dialog.fieldIds, function (i, fid) {
		var field = w(fid);
		var desc = getDesc(field);
		if (desc.data_type === dx.dataType.pic || desc.data_type === dx.dataType.file)
			files[field.id] = field.val();
		else
			formData[field.column] = field.val();
	});

	return buildApiParams(formData, api, rows, files, isDetail);
}
function checkOpMenu(oids, rows, callback, isDetail){
    function erroeParam(formal, row){
        if (isDetail){
            return evaluate(formal, {id: isDetail});
        }else{
            var form = {};
            form.id = row.parent;
            return evaluate(formal, form, null, null, null, null, row.id);
        }
    }
    if (!rows)
        rows = [];
    var noAuthRows = {};
    var authOps = {};
    var oid = oids.action_id;
    var op = dx.triggers[oid];
    if (op.is_one_data == 1 && rows.length == 0){
        messageBox('no row selected');
        return;
    }
    if (op.is_one_data == 2 && rows.length != 1){
        messageBox('only one row can be selected');
        return;
    }
    var enabled = ((rows.length > 0) && rows.every(function (row) {
        if (!op.condition)
            return true;
        var conditions = op.condition;
        var flag = true;
        var form = {};
        for (var i=0; i<conditions.length; i++){
            var evalFlag = false;
            if (isDetail) {
                evalFlag = evaluate(conditions[i].check_condition, {id: isDetail});
            }else{
                form.id = row.parent;
                evalFlag = evaluate(conditions[i].check_condition, form, null, null, null, null, row.id)
            }
            if (evalFlag){
                flag = false;
                var msg = dx.i18n.message[conditions[i].violate_msg_international_id] ?
                    dx.i18n.message[conditions[i].violate_msg_international_id] : conditions[i].violate_msg_international_id;
                var msgText;
                var errorMsgParam = conditions[i].violate_msg_param;
                if (errorMsgParam){
                    var params = errorMsgParam.split(",");
                    if (params)
                        for (var i=0; i<params.length; i++){
                            msgText = msg[dx.user.language_id].replace('${' + i + '}', erroeParam(params[i], row))
                        }
                }else{
                    msgText = msg[dx.user.language_id];
                }
                noAuthRows.msg = msgText;
                noAuthRows.row = row;
                noAuthRows.level = conditions[i].level;
                break;
            }
        }
        return flag;
    }));

    if (enabled) {
        var owners = [];
        rows.forEach(function (row) {
            owners.push({rowId : row.id})
        });
        authOps.id = oid;
        authOps.owners = owners;
    }
    if (!isEmpty(authOps)){
        postJson('/auth/opAuth.do', {
            type: 'operation',
            noAuthRows: authOps
        }, function (result) {
            if (result){

            }
            callback(noAuthRows);
        })
    }else{
        callback(noAuthRows);
    }
}
function calcOpMenuStatus(operations, opMenus, rows) {
	if (!rows)
		rows = [];
	$.each(operations, function (column, oids) {
		var authOps = [];
        var oid = oids.action_id;
        var op = dx.triggers[oid];
        var enabled = (op.is_one_data != 1 || (op.is_one_data == 1 && rows.length == 0) ||
            ((rows.length > 0) && rows.every(function (row) {
                    if (!op.condition)
                        return true;
                    var conditions = op.condition;
                    var flag = true;
                    for (var i=0; i<conditions.length; i++){
                        var form = {};
                        form.id = row.parent;
                        if (!evaluate(conditions[i].check_condition, form, null, null, null, null, row.id))
                            flag = false;
                    }
                    return flag;
                })));

        if (enabled) {
            var owners = [];
            rows.forEach(function (row) {
                var owner;
                if (row.columns['owner'])
                    owner = row.columns['owner'].value;
                else if (row.columns['cre_user'])
                    owner = row.columns['cre_user'].value;
                owners.push(owner);
            });
            authOps.push({id: oid, owners: owners});
        } else
            setOpMenuVisible(opMenus, oid, enabled);
		if (authOps.length > 0)
			postJson('/auth/entries.do', {
				type: 'operation',
				entries: authOps
			}, function (result) {
				$.each(result, function (id, value) {
					setOpMenuVisible(opMenus, id, value);
				})
			})
	});
}
/**
 * exec operation, popup operation call dialog if necessary
 * @param tableName apiTable name
 * @param column column name
 * @param oid operation id
 * @param rids record id list
 * @param widget widget model which op belonged
 */
function execOperation(tableName, column, oid, rids, widget) {
	var data = {parent: widget.id, column: column, method: oid};
	var op = getOp(oid);
	var api = op.api;
	var newState = {};
	if (op.allow_multi === -1)
		rids = null;
	// calc new state for each records
	if (rids)
		rids.forEach(function (rid) {
			var row = w(rid);
			op.rules.some(function (rule) {
                var form = {};
                form.id = row.parent;
				if (!evaluate(rule.cond, form, null, null, null, null, row.id))
					return false;
				newState[row.id] = rule.status_id_to;
				return true;
			})
		});
	if (api) {
		var apiTable = getTableDesc(api.id);
		if (apiTable.columns.length > 0) {
			// check is there any parameter need input
			if (!(api.params && (objectSize(api.params) == apiTable.columns.length))) {
				if (api.params) {
					var sourceForm;
					if (widget.widgetType === 'grid')
						sourceForm = rids ? w(rids[0]) : null;
					else
						sourceForm = widget;
					data.params = evaluateObject(api.params, sourceForm);
				}

				showDialogForm({
					url: '/operation/dialog.view',
					data: data,
					title: 'Parameters',
					shown: function (form, dialog) {
						initField(form);
						setDefaultValues(form);
						initSubmit(form.get(), function (result) {
							form.opResult = result;
							dialog.close();
						}, {
							dataProcessor: function () {
								return {
									operationId: oid,
									table: tableName,
									column: column,
									param: buildApiFormParam(form, api, rids),
									newState: newState
								};
							}
						})
					},
					hidden: function (form) {
						if (form.opResult)
							widget.updated({
								type: 'op',
								table: tableName,
								column: column,
								result: form.opResult
							});
					}
				});
				return;
			}
		}
	}
	var exec = function () {
		dx.processing.open(msg('Executing operation', i18n(op.i18n)));
		// api has no parameters or all parameters been provided
		postJson('/operation/operate.do', {
				operationId: oid,
				table: tableName,
				column: column,
				param: buildApiParams({}, api, rids),
				newState: newState
			},
			function (result) {
				widget.updated({type: 'op', table: tableName, column: column, result: result});
			})
	};

	if (!op.alert_condition) {
		confirmBox('exec confirm', i18n(op.i18n), exec);
		return;
	}
	var index = 0;
	var doConfirm = function () {
		if (!rids || index >= rids.length) {
			exec();
			return;
		}
		var row = w(rids[index]);
		index++;
        var form = {};
        form.id = row.parent;
		if (evaluate(op.alert_condition, form, null, null, null, null, row.id))
			confirmBox(evaluate(op.alert_message, form, null, null, null, null, row.id), doConfirm);
		else
			doConfirm();
	};

	doConfirm();
}
function encryptStr(str){
    var encryptStr;
    postJson('/externalForm/getEncryptStr.do', {jsondata : str}, function (result) {
        encryptStr = result.encryptStr;
    }, null, true);
    return encryptStr;
}
function codeStr(str){
    var encryptStr;
    postJson('/data/codeStr.do', {str : str}, function (result) {
        encryptStr = result;
    }, null, true);
    return encryptStr;
}
function outPage(url, data){
    var iframeHtml = '<iframe width="100%" height="400px" src="';
    if (isEmpty(data)){
        iframeHtml += url.url + '"';
        iframeHtml += '></iframe>';
    }else{
        var count = 0;
        for (var key in data){
            var str = data[key];
            //var str = data[key];
            //var paramIndex = str.indexOf('?data=');
            //var paseStr = codeStr(str.substr(paramIndex + 6, str.length));
            //encryptParamStr = str.substr(0, paramIndex + 6) + paseStr;
            if (count == 0){
                iframeHtml += url.url + '?' + key + '=' + encodeURIComponent(str);
            }else{
                iframeHtml += '&' + key + '=' + encodeURIComponent(str);
            }
            count += 1;
        }
        iframeHtml += '"></iframe>';
    }
    BootstrapDialog.show({
        title: url.i18n[dx.user.language_id],
        message: iframeHtml,
        cssClass: 'dx-out-url-dialog',
        onshown: function (dialog) {

        },
        onhidden: function (dialog) {

        }
    });
}
function outSidePage(tableName, rids, widget, api){
    var data = {};
    var url = dx.urlInterface[api.id];
    if (api.params) {
        var sourceForm;
        if (widget.widgetType === 'grid')
            sourceForm = rids ? w(rids[0]) : null;
        else
            sourceForm = widget;
        data.params = evaluateObject(api.params, sourceForm);
    }
    var documentElement = document.documentElement;
    var clientWidth = documentElement.clientWidth;
    var iframeHtml;
    var isMoble;
    if (clientWidth < 600){
        iframeHtml = '<iframe width="100%" height="100%" src="';
        isMoble = true;
    }else{
        iframeHtml = '<iframe width="100%" height="400px" src="';
    }
    if (isEmpty(data.params)){
        iframeHtml += url.url + '"';
        iframeHtml += '></iframe>';
    }else{
        var count = 0;
        for (var key in data.params){
            var encryptParamStr, paseStr;
            var str = data.params[key];
            var paramIndex = str.indexOf('?data=');
            if (paramIndex != -1){
                paseStr = codeStr(str.substr(paramIndex + 6, str.length));
                encryptParamStr = str.substr(0, paramIndex + 6) + paseStr;
            }else{
                if (key != 'domain')
                    paseStr = codeStr(str);
                else
                    paseStr = str;
                encryptParamStr = paseStr;
            }
            if (count == 0){
                iframeHtml += url.url + '?' + key + '=' + encodeURIComponent(encryptParamStr);
            }else{
                iframeHtml += '&' + key + '=' + encodeURIComponent(encryptParamStr);
            }
            count += 1;
        }
        iframeHtml += '"></iframe>';
    }
    BootstrapDialog.show({
        title: isEmpty(url.i18n) ? "": url.i18n[dx.user.language_id],
        message: iframeHtml,
        cssClass: isMoble ? "is-mobile-dialog":"dx-out-url-dialog",
        onshown: function (dialog) {

        },
        onhidden: function (dialog) {

        }
    });
}
function outSideInterface(tableName, rids, widget, api){
    var param = [];
    var table = getTableDesc(tableName);
    var data = {};
    if (isEmpty(rids)){
        data.outInterfaceParam = null;
    }else{
        for (var i=0; i<rids.length; i++){
            param.push(evaluateObject(api.params, w(rids[0])));
        }
    }
    data.event_id = api.event_id;
    data.outInterfaceParam = param;
    postJson('/operation/outSideInterface.do', data, function(result){
        alert(result);
    })
}

function execNewOperation(tableName, column, oid, rids, widget, isDetail, checklevel) {
    var table = getTableDesc(tableName);
    var op = table.triggers[oid];
    switch (op.is_one_data){
        case 0:
            break;
        case 1:
            if (isEmpty(rids) || rids.length == 0){
                alert('no row selected');
                return;
            }
            break;
        case 2:
            if (isEmpty(rids) || rids.length != 1){
                alert('only can selected one row');
                return;
            }
            break;
    }
    //TODO 权限校验
    //if (op.is_one_data != 0){
    if (!isEmpty(rids))
        postJson('/operation/checkOperationAuth.do',
            {operationId: oid, table: tableName, param: buildApiCheckParam({}, rids)},function (result) {
                authExecApi(tableName, column, oid, rids, widget, isDetail, checklevel);
        }, null, true);
    //}else{
    //    authExecApi(tableName, column, oid, rids, widget);
    //}
}
function authExecApi(tableName, column, oid, rids, widget, isDetail, checklevel){
    var table = getTableDesc(tableName);
    var op = table.triggers[oid];
    var api = op.api;
    if (isEmpty(api) || api.length == 0){
        return;
    }
    //api只有前一个执行完成才能执行第二个。
    var apiExecCount = 0;
    function apiExecCallback(result){
        //if (!result.success){
        //    return;
        //}
        apiExecCount += 1;
        if (apiExecCount < api.length){
            execApi(tableName, column, oid, rids, widget, api[apiExecCount], apiExecCallback, apiExecCount == api.length - 1, isDetail);
        }
    }
    if(hasAlert(api) && !checklevel){
        if(confirm(msg('exec real'))) {
            //apiExecCount == api.length - 1 执行到最后一个菜更新grid
            execApi(tableName, column, oid, rids, widget, api[apiExecCount], apiExecCallback, apiExecCount == api.length - 1, isDetail);
        }
    }else{
        execApi(tableName, column, oid, rids, widget, api[apiExecCount], apiExecCallback, apiExecCount == api.length - 1, isDetail);
    }
}

function execApi(tableName, column, oid, rids, widget, api, callback, isUpdate, isDetail){
    var data = {parent: widget.id, column: column, method: oid};
    var table = getTableDesc(tableName);
    var op = table.triggers[oid];
    var url = dx.urlInterface[api.id];
    if (url.type == 5){  //外部页面
        outSidePage(tableName, rids, widget, api);
        callback();
    }else if (url.type == 4){ //外部接口
        outSideInterface(tableName, rids, widget, api);
        callback();
    }else{
        var apiTable = getTableDesc(url.id);
        if (isEmpty(apiTable)){
            apiTable = getTableDesc(url.url);
        }
        if (apiTable.columns.length > 0) {
            // check is there any parameter need input
            if ((isEmpty(api.params) && apiTable.columns.length != 0) ||
                (!isEmpty(api.params) && objectSize(api.params) < apiTable.columns.length)){
                if (api.params) {
                    var sourceForm;
                    if (widget.widgetType === 'grid'){
                        sourceForm = rids ? w(rids[0]) : null;
                        var ret = {};
                        var paramData = {};
                        for (var key in sourceForm.columns){
                            paramData[key] = sourceForm.columns[key].value;
                        }
                        forEach(api.params, function (prop, formula) {
                            ret[prop] = evaluate(formula, null, null, null, null, paramData)
                        });
                        data.params = ret;
                    }else{
                        sourceForm = widget;
                        data.params = evaluateObject(api.params, sourceForm);
                    }
                }
                data.event_id = api.event_id;
                showDialogForm({
                    url: '/operation/dialog.view',
                    data: data,
                    title: i18n(op.action_name_I18N),
                    class: 'operation-param-dialog',
                    shown: function (form, dialog) {
                        form.get().find('input.dx-back').hide();
                        initField(form);
                        setDefaultValues(form);
                        initSubmit(form.get(), function (result) {
                            form.opResult = result;
                            dialog.close();
                            callback(result);
                        }, {
                            dataProcessor: function () {
                                return {
                                    operationId: oid,
                                    table: tableName,
                                    column: column,
                                    //param: data.params,
                                    param: buildApiFormParam(form, op.apiMap[form.event_id], rids, isDetail),
                                    event_id : form.event_id
                                };
                            }
                        })
                    },
                    hidden: function (form) {
                        if (form.opResult && isUpdate)
                            widget.updated({type: 'op', table: tableName, column: column, result: form.opResult});
                    }
                });
                return;
            }
        }
        var exec = function () {
            dx.processing.open(msg('Executing operation'));
            // api has no parameters or all parameters been provided
            postJson('/operation/operate.do',
                {operationId: oid, table: tableName, column: column, param: buildApiParams({}, api, rids, null, isDetail), event_id : api.event_id
                    },function (result) {
                    if (isUpdate)
                        widget.updated({type: 'op', table: tableName, column: column, result: result});
                    callback(result);
                })
        };
        var index = 0;
        var doConfirm = function () {
            if (!rids || index >= rids.length) {
                exec();
                return;
            }
            var row = w(rids[index]);
            index++;
            var form = {};
            form.id = row.parent;
            if (evaluate(op.alert_condition, form, null, null, null, null, row.id))
                confirmBox(evaluate(op.alert_message, form, null, null, null, null, row.id), doConfirm);
            else
                doConfirm();
        };
        doConfirm();
    }
}
//api里有输入参数则不弹框。无则弹框。
function hasAlert(apis){
    var flag = true;
    for (var i=0; i<apis.length; i++){
        var api = apis[i];
        var url = dx.urlInterface[api.id];
        if (url.type == 5){  //外部页面
        }else if (url.type == 4){ //外部接口
        }else{
            var apiTable = getTableDesc(url.id);
            if (isEmpty(apiTable)){
                apiTable = getTableDesc(url.url);
            }
            if (apiTable.columns.length > 0) {
                // check is there any parameter need input
                if ((isEmpty(api.params) && apiTable.columns.length != 0) ||
                    (!isEmpty(api.params) && objectSize(api.params) < apiTable.columns.length)) {
                    return false;
                }
            }
        }
    }
    return flag;
}