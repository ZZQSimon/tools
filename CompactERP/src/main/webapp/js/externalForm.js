var table="";
var param="";   
function initCache(callback, isReload) {
    function initJsCache(data){
        dx.dict = data.dict;
        dx.table = data.table;
        dx.urlInterface = data.urlInterface;
        dx.tableViewStyle = data.tableViewStyle;
        $.each(dx.table, function (i, t) {
            t.columnMap = {};
            if (!isEmpty(t.columns)){
                $.each(t.columns, function (i, c) {
                    t.columnMap[c.column_name] = c;
                });
            }else{
                //alert(msg('table') + ' "' + t.id + '" ' + msg('have no column'));
            }
        });
        dx.user = {};
    	dx.user.language_id = data.domainLanguage;
        dx.user.isOutPage = 1;
        dx.dataType = data.dataType;
        dx.i18n.message = data.message;
        dx.pages = data.pages;
        dx.operations = data.operations;
        dx.triggers = data.triggers;
        dx.shortcuts = data.shortcuts;
        dx.batches = data.batches;
        dx.status = data.status;
        dx.complexColumns = data.complexColumns;
        dx.i18n.report = data.report;
        dx.sysGroupName = data.sysGroupName;
        dx.sys = data.sys;
        dx.biz = data.biz;
        dx.sqlMap = data.sqlMap;
        dx.result = {
            $details: $('#dxResultDetails'),
            $message: $('#dxResultMessage'),
            dialog: $('#dxResultDialog').get()[0]
        };
        callback();
        dx.processing.close();
    }
    
    postJson('/data/initCache.do', function (data) {
        initJsCache(data);
    }, null, true);
}
$.ajax({     
    url:getRootPath()+"/externalForm/selectExternalFormData.do",
    contentType: 'application/json; charset=utf-8',
    success: function(data) {
    	table = data.data.tableId;
    	param = data.data.param;
    	postPage('/detail/edit.view', {table : table, param : param, isOutPage: 1}, function (result) {
            $(".externalForm").empty();
            $(".externalForm").append(result);
    		var $form = $(".externalForm").find('.dx-form');
    		var form = getFormModel($form.attr('id'));
    		form.get = function(){
    			return $form;
    		};
            form.isOutPage = 1;
            $form.find('input.dx-back').hide();
            //$form.find('.dx-operation').hide();
    		buildFormCache(form, form.widgets);
    		if (dx.init.detail)
    			dx.init.detail(form);
    	}, null, 'outPage');
    },     
    error: function(err) {

    }
});     



function getRootPath(){  
   var pathName = window.location.pathname.substring(1);  
   var webName = pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/'));  
   return window.location.protocol + '//' + window.location.host + '/'+ webName;  
}  
