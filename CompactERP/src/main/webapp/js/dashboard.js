registerInit('dashboard', function (form) { 
	var $form = form.get();
	
	var dashboardCache;
	var isSave = 1;
	var dashboard_size = 1;	//仪表盘尺寸
	var report_table;
    var curDashboard;
	
	$form.find('#tab-config-summer').summernote();
	
	getDashboards();
	initListTableItems();
    initChartList();
	
	function getDashboards() {
        postJson('/dashboard/getDashboards.do', {}, function (result) {
            dashboardCache = result;
            var $ul = $form.find('.tab-config-ul');
            $ul.find('li').remove();
            //$ul.empty();
            dashboardCache.forEach(function (value) {
                $ul.append('<li id="' + value.dashboard_id + '">' + msg(value.dashboard_title) + '</li>');
            });
            $form.find(".dashboard-type").change();
            $form.find('.tab-config-ul li').click(function () {
                $form.find('.tab-config-ul li').removeClass("active");
                $(this).addClass("active");
                var id = this.id;
                curDashboard = id;
                var dash = getCashboard(id);
                //isSave=0;
                initConfigTab(dash);
                $form.find(".dashboard-type").change();
            });
        });
    }
	
	function initConfigTab(dash) {
        var type = dash.dashboard_type;
        var param;
        param=dash.dashboard_param==""?null: eval("(" + dash.dashboard_param + ")");
        $form.find('.dashboard-type').val(dash.dashboard_type);
        $form.find('.dashboard-name').val(dash.dashboard_name);
        $form.find('.dashboard-title').val(msg(dash.dashboard_title));
        $form.find('#table-size-' + dash.dashboard_size).click();
        $form.find("input[type=radio][name='subscribe-type'][value=" + dash.subscribe_type + "]").iCheck('check');
        if (type == "1") {
        	 $form.find('#tab-config-summer').summernote('code', dash.dashboard_richtext);
        } else if (type == "4" || type == "5") {
            $form.find('.actual_sql').val(param.actual_sql);
            $form.find('.target_sql').val(param.target_sql);
            if (type == "5") {
                var method = $form.find('.method').val(param.method);
            }
        } else if (type == "6") {
            var report_id = param.report_id;
            var condition = param.condition;
            var $condition = $form.find('.tab-chart-condition');
            initConditionTable(param.report_table, condition, $condition, "tab-chart");
            $form.find('.chart-list').val(report_id);
        } else if (type == "7") {
            $form.find('.table-list-items').val(param.table);
            $form.find('.refer-table').val(param.ref_table);
            $form.find('.default-count').val(param.defaultCount);
            $form.find('.order-by').val(param.orderBy);
            var condition = param.condition;
            var $condition = $form.find('.tab-table-condition');
            initConditionTable(param.table, condition, $condition, "tab-table", param);
        }
    }
	
	function getCashboard(id) {
        for (var key in dashboardCache) {
            if (dashboardCache[key].dashboard_id == id) {
                return dashboardCache[key];
            }
        }
    }
	
	function initConditionTable(tableName, conditions, $condition, clazz, param) {
        var columns = dx.table[tableName].columns;
        var value;
        $condition.empty();
        var $div;
        var $label;
        var $input;
        var $type;
        var type;
        var column_name;
        var $orderBy=$form.find(".order-by");
        $orderBy.empty();
        for (var col in columns) {
        	//初始化条件字段
            column_name = columns[col].column_name;
            $div = $('<div class="form-group ' + clazz + '"></div>');
            $label = $('<label>' + columns[col].i18n[dx.user.language_id] + '</label>');
            if (conditions != undefined) {
                value = conditions[column_name] == undefined ? "" : conditions[columns[col].column_name];
                $input = $('<input  type="text" class="form-control" name="' + columns[col].column_name + '" value="' + value + '"/>');
            } else {
                $input = $('<input  type="text" class="form-control" name="' + columns[col].column_name + '" />');
            }

            $div.append($label);
            if (clazz == "tab-table") {
                if (param != undefined) {
                    if (param.type != undefined)
                        type = param.type["." + column_name + "_type"];
                    else if (type == undefined) {
                        type = "=";
                    }
                }

                $type = $("<select class='" + column_name + "_type' value='" + type + "'><option value='='>=</option><option value='>'>></option><option value='<'><</option><option value='>='>>=</option><option value='<='><=</option><option value='<>'><></option></select>");
                $div.append($type);
                $div.find("option[value = '" + type + "']").attr("selected", "selected");
            }
            $div.append($input);
            $condition.append($div);
            
            //初始化排序字段
            var $orderBy=$form.find(".order-by");
            $orderBy.append('<option value="'+column_name+'">'+columns[col].i18n[dx.user.language_id]+'</option>');
        }
    }
	
	function buildParam() {
        var dashboard_id = curDashboard;
        var dashboard_type = $form.find('.dashboard-type').val();
        var dashboard_name = $form.find('.dashboard-name').val();
        var dashboard_title = $form.find('.dashboard-title').val();
        var subscribe_type = $("input[name='subscribe-type']:checked").val();
        var dashboard_param;
        var dashboard = {
            dashboard_name: dashboard_name,
            dashboard_type: dashboard_type,
            dashboard_size: dashboard_size,
            dashboard_title: dashboard_title,
            subscribe_type: subscribe_type
        };
        if (dashboard_id != undefined && dashboard_id != "") dashboard["dashboard_id"] = dashboard_id;
        if (dashboard_type == "1") {
            var dashboard_richtext = $form.find('#tab-config-summer').summernote('code');
            dashboard["dashboard_richtext"] = dashboard_richtext;
        } else if (dashboard_type == "4" || dashboard_type == "5") {
            var actual_sql = $form.find('.actual_sql').val();
            var target_sql = $form.find('.target_sql').val();
            dashboard_param = {actual_sql: actual_sql, target_sql: target_sql};
            if (dashboard_type == "5") {
                var method = $form.find('.method').val();
                dashboard_param["method"] = method;
            }
        } else if (dashboard_type == "6") {
            var condition = {};
            var report_id = $form.find('.chart-list').val();
            var columns = $form.find('.tab-chart-condition input');
            var $column;
            var name;
            var report_table=$form.find('.chart-list option[value="'+report_id+'"]').attr("name");
            /*var str;
            for (var key in dashboardCache) {
                str = dashboardCache[key].dashboard_param;
                if (str != null && str !== undefined) {
                    var param = eval("(" + str + ")");
                    if (param.report_id == report_id && param != null) {
                        report_table = param.report_table;
                    }
                }
            }*/
            var value;
            for (var i = 0; i < columns.length; i++) {
                $column = $(columns[i]);
                name = $column.attr("name");
                value = $column.val();
                if (value != "" && name != "") {
                    condition[name] = value;
                }
            }
            dashboard_param = {report_id: report_id, report_table: report_table, condition: condition};
        } else if (dashboard_type == "7") {
            var tableName = $form.find('.table-list-items').val();
            var ref_table = $form.find('.refer-table').val();
            var defaultCount=$form.find('.default-count').val();
            var orderBy=$form.find('.order-by').val();
            var condition = {};
            var type = {};
            var columns = $form.find('.tab-table-condition input');
            var value;
            var $column;
            for (var i = 0; i < columns.length; i++) {
                $column = $(columns[i]);
                name = $column.attr("name");
                value = $column.val();

                if (value != "" && name != "") {
                    condition[name] = value;
                    type["." + name + "_type"] = $form.find("." + name + "_type").val();
                }
            }
            dashboard_param = {table: tableName, ref_table: ref_table, condition: condition, type: type,defaultCount:defaultCount,orderBy:orderBy};
        }
        dashboard["dashboard_param"] = JSON.stringify(dashboard_param);
        return dashboard;

    }
	
	//初始化下拉列表项
	function initListTableItems() {
        var tables = dx.table;
        var $items = $form.find('.table-list-items');
        var $refTable = $form.find('.refer-table');
        $items.empty();
        $refTable.empty();
        $items.append('<option>' + msg("Please select an option") + '</option>');
        $refTable.append('<option>' + msg("Please select an option") + '</option>');
        var table;
        var type;
        for (var Key in tables) {
            table = tables[Key];
            type = table.table_type;
            if (type == 1 || type == 4 || type == 5) {
                $items.append('<option value="' + Key + '" >' + msg(table.international_id) + '</option>');
                $refTable.append('<option value="' + Key + '" >' + msg(table.international_id) + '</option>');
            }
        }

        $items.change(function () {
            var tableName = $(this).find("option:selected").val();
            var conditions;
            var $condition = $form.find('.tab-table-condition');
            var clazz = "tab-table";
            initConditionTable(tableName, conditions, $condition, clazz);
        });
    }
	
	
	//初始化图表列表
	function initChartList() {
	        var $chars = $form.find('.chart-list');
	        $chars.empty();
	        postJson('/dashboard/getChartList.do', {}, function (result) {
	            var charts = result;
	            for (var chart in charts) {
	                $chars.append('<option value="' + charts[chart].id + '" name="' + charts[chart].table_id + '">' + msg(charts[chart].international_id) + '</sption>');
	            }
	        });

	        $chars.change(function () {
	            var tableName = $(this).find("option:selected").attr("name");
	            var conditions;
	            var $condition = $form.find('.tab-chart-condition');
	            var clazz = "tab-chart";
	            initConditionTable(tableName, conditions, $condition, clazz);
	            report_table = tableName;
	        });

	    }
	
	//仪表盘类型切换
    $form.find(".dashboard-type").change(function () {
        var tabletype = "." + $(this).find("option:selected").attr("name");//.val();
        $(".tab-config-select>div").css("display", "none");
        $(".tab-config-select").find(tabletype).css("display", "block");
    });
    
    //仪表盘点击事件
    $form.find('.table-size-4').click(function () {
        $form.find(".table-size>div").css("background-color", "rgb(230, 230, 230)");
        $(this).css("background-color", "rgb(194, 223, 235)");
        $(this).prevAll().css("background-color", "rgb(194, 223, 235)");
        dashboard_size = $(this).attr("id");
        dashboard_size = dashboard_size.substr(dashboard_size.length - 1, 1)
    });
    
  //创建仪表盘
  $form.find('.add-dashboard').click(function () {
        if (isSave == 1) {
            var name = $form.find('.dashboard-name').val();
            var $li = $('<li class="active">' + name + '</li>');
            $li.click(function () {
                curDashboard = "";
                $form.find('.tab-config-ul li').removeClass("active");
                $li.addClass("active");
                $(".tab-config-select>div").css("display", "none");
                $(".tab-config-select").find(".common-notice").css("display", "block");
                $(".tab-config-select").val();
                $form.find(".dashboard-title").val("");
                $form.find(".dashboard-type").val(1);

            });
            $form.find('.tab-config-ul').append($li);
            isSave = 0
        } else {
            alert(msg("There are unsaved data"));
        }

    });
    
  //保存仪表盘
    $form.find('.dashboard-submit').click(function () {
        var req = {dashboard: buildParam()};
        if (isSave == 1 || (isSave == 0 && (req.dashboard.dashboard == "" || req.dashboard.dashboard == undefined))) {
            postJson('/dashboard/saveDashboard.do', req, function (result) {
                dxToastAlert(msg(result));
                refresh();
            });
        } else {
            alert(msg("There is no data to be saved"));
        }
    });


    //删除仪表盘
    $form.find('.dashboard-delete').click(function () {
        if (curDashboard != undefined) {
            postJson('/dashboard/deleteDashboard.do', {dashboard: {dashboard_id: curDashboard}}, function (result) {
                dxToastAlert(msg(result));
                refresh();
            });
        } else {
            alert(msg("Please select a dashboard"));
        }
    });
    
    function refresh() {
        getDashboards();
    }
 
});
