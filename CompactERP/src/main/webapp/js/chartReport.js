registerInit('chartReport', function (form) {
    var $form = form.get();
    var $items = $form.find(".report_items");
    
    var $chart_id = $form.find('.chart_id');
    var $report_name = $form.find('.report_name');
    var $table_id = $form.find('.table_name');
    var $report_type = $form.find('.report_type');
    var $report_column = $form.find('.report_column');
    var $count_column = $form.find('.count_column');
    var $group_column = $form.find('.group_column');
    var $date_column = $form.find('.date_column');
    var $date_type = $form.find('.date_type');
    var $condition = $form.find('.condition');
    var chartReports=form.chartReports;
    
    //添加
    $form.find('.chart-report-add').click(function () {
        $form.find('.report-list').hide();
        $form.find('.report-deployee').show();
    });
    //删除
    $form.find('.chart-report-delete').click(function () {
    	var id=$form.find(".to-be-selected").attr("name");
    	var i18n_id=$form.find(".to-be-selected p").attr("name");
    	var req = {report: {id:id,international_id:i18n_id},type:form.type};
    	postJson('/chartReport/deleteReport.do', req, function (data) {
            if (data.ret == "succeed") {
            	dxToastAlert(msg(data.msg));
            	//$form.find(".to-be-selected").parent().remove();
            	chartReports=data.chartReports;
            	initReports();
            }
            else alert(msg(data.msg));
            
        });
    	
    });
    //保存
    $form.find('.chart-report-save').click(function () {
    	var status=$(this).attr("status");
    	if(status=="save"){
    		$(this).text(msg("edit"));
    		var req = {report: buildParam(),type:form.type};
            postJson('/chartReport/saveReport.do', req, function (data) {
                if (data.ret == "succeed"){
                	dxToastAlert(msg(data.msg));
                	/*var $items = $form.find(".report_items");    
                    $item = $('<div class="col-xs-3 report-item-wrap"><div class="report-item" name="' + data.report.id + '"><span class="chart-pic ' + getImgUrl(data.report) + '"></span><p name='+data.report.international_id+'>' + data.i18nName + '</p></div></div>');
                    $items.append($item);
                    bindEvent();*/
                	chartReports=data.chartReports;
                	initReports();
                	$form.find('.chart-report-return').click();
                } 
                else alert(msg(data.msg));
            });
    	}else{
    		$form.find(".dx-detail-container .field").removeAttr("disabled");
    		$(this).attr("status","save");
    		$(this).text(msg("save"));
    	}
        
    });
    //返回
    $form.find('.chart-report-return').click(function () {
        $form.find('.report-list').show();
        $form.find('.report-deployee').hide();
        $form.find('.chart-report-save').text(msg("edit"));
        clear();
    });
    
    initReports();
    initTable();
    
    function getImgUrl(chart){
    	var type;
    	if (chart.report_type != 4) {
    		switch(chart.report_type)
			{
			    case 1:
			    	type="line";
			        break;
			    case 2:
			    	type="pie";
			        break;
			    case 5:
			    	type="funnel";
			        break;
			    case 6:
			    	type="bar";
			        break;
			    case 7:
			    	type="radar";
			        break;
			}
     
        } else {
        	if(chart.report_file_type==0)
        		type="xlsx";
        	else
        		type="pdf";
        }
    	if(type==undefined) type="pie";
    	return "chart-"+type;
    }
    
    function bindEvent(){
    	$items.find(".report-item").dblclick(function () {
            var report_id = $(this).attr("name");
            $form.find('.report-list').hide();
            $form.find('.report-deployee').show();
            initReport(report_id, chartReports);
        });
        
        $items.find(".report-item").click(function () {
           $form.find(".to-be-selected").removeClass("to-be-selected");
           $(this).addClass("to-be-selected");
        });
    }
    
    function initReports() {
        //chartReports = dx.cache.form[form.id].chartReports;
        var url;
        $items.empty();
        for (var key in chartReports) {     
            $item = $('<div class="col-xs-3 report-item-wrap"><div class="report-item" name="' + chartReports[key].id + '"><span class="chart-pic ' + getImgUrl(chartReports[key]) + '"></span><p name='+chartReports[key].international_id+'>' + msg(chartReports[key].international_id) + '</p></div></div>');
            $items.append($item);
        }
        bindEvent();
    }

    function initReport(report_id, chartReports) {
        var report;
        for (var key in chartReports) {
            if (chartReports[key].id == report_id) {
                report = chartReports[key];
                break;
            }
        }
        initColumn(report.table_id);
        $chart_id.val(report.id);
        $report_name.val(msg(report.international_id));
        $table_id.val(report.table_id);
        $report_type.val(report.report_type);
        $report_column.val(report.report_column);
        $count_column.val(report.count_column);
        $group_column.val(report.group_column);
        $date_column.val(report.date_column);
        $date_type.val(report.date_type);
        $condition.val(report.condition);
        $form.find(".dx-detail-container .field").attr("disabled","disabled");
        $form.find('.chart-report-save').attr("status","edit");
    }

    function initTable() {
        var tables = dx.table;
        var $option;
        var type;
        $table_id.empty();
        $table_id.append('<option value="">' + msg("Please select an option") + '</option>');
        for (var key in tables) {
            type = tables[key].table_type;
            if (type == 1 || type == 4 || type == 5) {
                $option = '<option value=' + key + '>' + tables[key].i18n[dx.user.language_id] + '</option>';
                $table_id.append($option);
            }
        }
        $table_id.change(function () {
            initColumn($table_id.val());
        });
    }

    function initColumn(table_id) {
        var columns = dx.table[table_id].columns;
        clearSelect();
        for (var key in columns) {
            var $option = '<option value=' + columns[key].column_name + '>' + msg(columns[key].international_id) + '</option>';
            $report_column.append($option);
            $count_column.append($option);
            $group_column.append($option);
            $date_column.append($option);
        }
    }

    function clear(flag) {
    	$form.find(".dx-detail-container .field").val("");
        clearSelect();
    }

    function clearSelect() {
        $report_column.empty();
        $count_column.empty();
        $group_column.empty();
        $date_column.empty();

        var $default = '<option value="">' + msg("Please select an option") + '</option>';
        $report_column.append($default);
        $count_column.append($default);
        $group_column.append($default);
        $date_column.append($default);
    }

    function buildParam() {
    	var report ={};
    	$form.find(".dx-detail-container .field").each(function(i){
    		report[this.name]=$(this).val();
    	});
        return report;
    }

    function check(value) {
        if (value != null && value != "" && value == undefined) return true;
        else return false;
    }

});