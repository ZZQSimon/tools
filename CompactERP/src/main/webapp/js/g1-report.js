/**
 * Created with IntelliJ IDEA.
 * User: wangqing
 * Date: 5/23/14
 * Time: 10:30 AM
 */

"use strict";

/**
 * init table print menu within form
 *
 * @param form list form widget
 */
function initTableReport(form) {
	function reportIds() {
		var records = grid.getSelectedRows();
		if (!records)
			return null;
		var ids = [];
		records.forEach(function (record) {
			var val = '';
			table.idColumns.forEach(function (id) {
				if (val != '')
					val += ',';
				val += '"' + record.columns[id].value + '"';
			});
			ids.push(val);
		});
		var ret = {};
		ret[table.idColumns.join()] = {type: 'in', value: ids};
		return ret;
	}

	var cache = dx.c('report');
	var menus = [];
	var grid = w(form.grid.id);
	var table = getTableDesc(form.tableName);
    var tableReportCount = 0;
	$.each(cache, function (id, report) {
		if (report.table_id != form.tableName || report.hidden == 1){
			return;
        }else if(report.table_id == form.tableName && report.hidden != 1){
            tableReportCount += 1;
        }
		menus.push({title: getReportLabel(report.id), name: report.id, clazz: 'dx-table-report'})
	});
	// no report for this table
	if (menus.length == 0)
		return;
	var menuId = 'table-report-menu-' + form.id;
    //没有任何一个报表则隐藏该按钮。
    if (tableReportCount != 0){
	    form.addMenu({title: 'Report', id: menuId, sub: menus});
    }
	var $menu = $('#' + menuId);
	var $subs = $menu.next().find('a.dx-table-report');
	$menu.on('click', function () {
		var records = grid.getSelectedRows();
		if (!records) {
			$subs.addClass('hidden');
			return null;
		}
		$subs.each(function () {
			var $a = $(this);
			var menu = dx.c('report', $a.prop('name'));
			if (!menu.condition || records.every(function (record) {
					return evaluate(menu.condition, record, false);
				}))
				$a.removeClass('hidden');
			else
				$a.addClass('hidden');
		});
	});
	$subs.on('click', function () {
		var $a = $(this);
		var filters = reportIds();
		if (filters) {
			var reportId = $a.attr('name');
			var param = {};
			param.data={
					report_id: reportId,
					filter: {tableName: form.tableName, filters: filters}
				};
			postJsonRaw("/report/pre_print.do", param, function (data) {
				if (data === null) {
					messageBox("print_job_done");
					return false;
				}
                if (!data.success){
                    messageBox(data.errorMessage);
                    return false;
                }
				var cache = dx.c('report');
				var report = cache[reportId];
				if (report.api || report.pre_api)
					grid.reload();
				var viewUrl ="" ;
				var downloadUrl = "" ;
				if(data.data!=null){
					viewUrl = data.data.viewUrl ;
					downloadUrl = data.data.downloadUrl ;
				}
                $("body").append("<iframe src='" + makeUrl(downloadUrl) + "' style='display: none;' ></iframe>");
                //预览。
				//window.open(makeUrl("/report/preview.do?viewUrl="+viewUrl+"&&downloadUrl="+downloadUrl));
			});
			/*fileDownload('/report/pre_print.do', {
				report_id: reportId,
				filter: {tableName: form.tableName, filters: filters}
			}, function (data) {
				if (data === null) {
					messageBox("print_job_done");
					return false;
				}
				var cache = dx.c('report');
				var report = cache[reportId];
				if (report.api || report.pre_api)
					grid.reload();
				window.open(makeUrl("/report/preview.do"));
				
			});*/
		} else
			messageBox('noRowSelected');
	});
	
}

function downloadViewFile(){
	alert(url) ;
	$("body").append("<iframe src='" + makeUrl(url) + "' style='display: none;' ></iframe>");
}	

registerInit('report', function (form) {
	initField(form);
	initFilter(form, function (where, filter) {
		var filterParam={};
		var temp;
		$.each(filter["filters"],function(field,obj){
			temp="";
			if(obj["type"]=='in'){
				$.each(obj["value"],function(k,v){
					temp+=v+",";
				});
			}else if(obj["type"]=='like'){
				temp+=obj["value"]+",";
			}else if(obj["type"]=='between'){
				temp+=$.format.date(new Date(obj["from"]), 'yyyy-MM-dd HH:mm:ss')+",";
				temp+=$.format.date(new Date(obj["to"]), 'yyyy-MM-dd HH:mm:ss')+",";
			}
			
			temp=temp.substr(0,temp.length-1);
			filterParam["_"+field]=temp;
		});
		
		fileDownload('/report/print.do', {report_id: form.report_id, filter: filter, filterParam: filterParam}, function(data){
            $("body").append("<iframe src='" + makeUrl(data.downloadUrl) + "' style='display: none;' ></iframe>");
			//window.open(makeUrl("/report/preview.do?viewUrl="+data.viewUrl+"&&downloadUrl="+data.downloadUrl));
		});
	});
});

registerModuleInit('list', initTableReport);