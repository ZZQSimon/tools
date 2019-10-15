/**
 * User:huyannan
 * Date: 11/12/14
 * Time: 10:19 AM
 */

"use strict";

function initIndex(form) {
    if (dx.user.isMobileLogin == 1) return;

    var $form = form.get();
    var isSave = 1;
    var dashboardCache;
    var isInitSubscribeTab = 1;
    var dashboard_size = 1;	//仪表盘尺寸
    var report_table;
    var curDashboard;
    var checkAll = 0;
    //var chartsCache;


    $('#tab-config-summer').summernote();

    $('.dx-main-tab-content input').iCheck({
        checkboxClass: 'icheckbox_flat-blue',
        radioClass: 'iradio_flat-blue'
    });
    //个人信息
    $(".personalInformation").on("click", function () {
        newTab('/detail/userEdit.view', {
            table: "m_user",
            readonly: true,
            isIndexView: 1
        }, function (newForm) {

        }, "m_user");
    });
    //修改密码
    $(".changePasseord").on("click", function () {
        newTab('/changepassword/edit.view', {}, function (newForm) {

        }, "changepassword");
    });
    //仪表盘跳转
    $(".home-tab-nav a[data-toggle='tab']").on("click", function () {
        $(".tab-homecontent>.tab-pane ").removeClass("active");
        var totag = $(this).attr("href");
        $(totag).addClass("active");

        if (totag === "#home-sub-tab" || totag === "#home-config-tab") {
            if ($(this).attr("href") === "#home-sub-tab"){
            } else if ($(this).attr("href") === "#home-config-tab"){
                // $(".dx-main-tabs>.home-sub-title").html(msg("Configuration dashboard"));
            }
        }
    });

    //点击主导航条，还原仪表盘状态
    $(".dx-main-tabs a[href=#home-tab]").on('shown.bs.tab', function (e) {
        $("button[href=#home-common-tab]").click();
        // $(".nav-tabs.home-tab-nav li").removeClass("active");
        initHomeTab();
        dx.reloadIndexApprove();
    });
    // $(".dx-main-tabs a[href=#home-tab]").on("click",function () {
    // $("button[href=#home-common-tab]").click();
    // // $(".nav-tabs.home-tab-nav li").removeClass("active");
    // initHomeTab();
    // dx.reloadIndexApprove();
    // });

    //点击左边菜单以及消息tab会关闭仪表盘配置的titlt
    $("a[href=#home-common-tab],.dx-main-menu .nav-node-item,.dx-user-module-menu .nav-node-item,.toptool .message").on("click", function () {
        if ($(".dx-main-tabs>.index-tab-navs").is(":hidden")) {
            if ($(".home-navs-rest-drop li").length) {
                $(".home-navs-rest-drop").css({display: "block"});
            }
        }
    });

    //===========================================仪表盘配置函数定义

    function getI18n(language, i18n) {
        if (language == "en") {
            return i18n.en;
        } else if (language == "cn") {
            return i18n.cn;
        } else if (language == "jp") {
            return i18n.jp;
        } else if (language == "other1") {
            return i18n.other1;
        } else if (language == "other2") {
            return i18n.other2;
        }
    }

    Date.prototype.format = function (format) {
        var o = {
            "M+": this.getMonth() + 1,
            "d+": this.getDate(),
            "h+": this.getHours(),
            "m+": this.getMinutes(),
            "s+": this.getSeconds(),
            "q+": Math.floor((this.getMonth() + 3) / 3),
            "S": this.getMilliseconds()
        };	
        if (/(y+)/.test(format))
            format = format.replace(RegExp.$1, (this.getFullYear() + "")
                .substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(format))
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        return format;
    };
    //审批中心
    function newApprove($ol, size, id) {
        var $li = $('<li class="dx-field-' + size + '"></li>');
        var $approve = $('<d	iv class="panel home-approve-tab home-common-require">'
            + '<div class="panel-heading">'
            + msg("approve center") + '<span class="pull-right approve-reload">' + msg("refresh") + '</span>'
            + '   </div>'
            + '   <div class="panel-body" style="overflow:hidden">'
            + '       <div id="' + id + '" class="approve-div dash" style="width: 100%;height:330px;">'
            + '       </div>'
            + '   </div>'
            + '</div>');
        $li.append($approve);
        $ol.append($li);
    }

    function newItems(dash, id, $ol) {
        var size = dash.dashboard_size;
        var height;
        var $close = $("<span class='pull-right remove-dashboard'>×</span><button class='maximizeScrll'></button><button class='minmizeScrll' style='display: none'></button>");

        var require = "";
        height = "300px";
        size = size * 3;
        if (dash.subscribe_type == "1") {
            require = "home-common-require";
            $close = $("<span></span><button class='maximizeScrll'></button><button class='minmizeScrll'  style='display: none'></button>");
        }

        //var $items=$form.find('.sub_tab_items');
        //var $ol=$form.find('.sort-container');
        var $li = $('<li class="dx-field-' + size + '"></li>');
        if (dash.dashboard_type == 2 && $ol.attr("class") == "home-sort-container") {
            //审批中心
            newApprove($ol, size, id);
        } else {
            // var $container = $('<div></div>');
            //添加
            var $panel = $('<div class="panel free panel-checked lex-dash-height' + require + '"></div>');
            var $head = $('<div class="panel-heading">' + msg(dash.dashboard_title) + '</div>');
            var $body, $dash;
            if (dash.dashboard_type === "7") {
                $body = $('<div class="panel-body isTable"></div>');
                $dash = $('<div id="' + id + '" style="width: 100%;height: 300px;overflow: auto;position: relative;" class="dash"></div>');
            } else {
                $body = $('<div class="panel-body"></div>');
                $dash = $('<div id="' + id + '" style="width: 100%;height:' + height + ';overflow: auto" class="dash"></div>');
            }

            $body.append($dash);
            $panel.append($head);
            if ($ol.attr("class") == "home-sort-container") {
                $head.append($close);
            }
            $panel.append($body);
            // $container.append($panel);
            //$items.append($container);
            $li.append($panel);
            $ol.append($li);
            $($li).find(".maximizeScrll").click(function(){
                // this.parent()
                $($li).siblings("li").hide();
                $($li).addClass("dx-field-12").find(".panel-body").css({"height":"600px"});

                $($li).find(".minmizeScrll").css({display:"inline-block"});
                $(this).css({display:"none"});

                console.log(this)
            });
            $($li).find(".minmizeScrll").click(function(){
                // this.parent()
                $($li).siblings("li").show();
                $($li).find(".maximizeScrll").css({display:"inline-block"});
                $($li).removeClass("dx-field-12").find(".panel-body").css({"height":"auto"});;
                $(this).css({display:"none"});
                console.log(this)
            });
            // $("#home-tab .home-sort-containery").getNiceScroll().resize();
            return $dash;
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
            var dashboard_richtext = $('#tab-config-summer').summernote('code');
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

            dashboard_param = {table: tableName, ref_table: ref_table, condition: condition, type: type};
        }
        dashboard["dashboard_param"] = JSON.stringify(dashboard_param);
        return dashboard;

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
        for (var col in columns) {
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
        }
    }

    function initPercent(pieDate, id) {
        var title = pieDate.title;//'出勤率';
        var percent = pieDate.percent.toFixed(2);//20
        var $Pie = document.getElementById(id);//'home-attendance-rate'
        var data = [{value: percent, name: title}, {value: 100 - percent, name: ''}];
        var group = [title];
        var pieChart = echarts.init($Pie);
        var option = {
            tooltip: {trigger: 'item', formatter: "{b}<br/>: {c}%"},
            legend: {orient: 'vertical', x: 'left', selectedMode: false, data: group},
            grid: {
                left: '0%',
                right: '0%',
                bottom: '0%',
                containLabel: true
            },
            series: [{
                name: title,
                type: 'pie',
                radius: ['35%', '50%'],
                hoverOffset: 5,
                center: ['50%', '60%'],
                avoidLabelOverlap: false,
                label: {
                    normal: {show: false, position: 'center'},
                    emphasis: {show: false, textStyle: {fontSize: '14', fontWeight: 'bold'}}
                },
                color: ["#6ec1e5", "#b3b3b3"],
                labelLine: {normal: {show: false}},
                data: data
            }]
        };

        pieChart.setOption(option);

        window.addEventListener("resize", function () {
            if (!$("#home-tab").is(":hidden")) {
                pieChart.resize();
                console.log(11);
            } else {
                console.log(22);
            }
        });
        $("#" + id).css("overflow", "hidden");
    }
    
    function setColor(params){
    	var colorList = ['rgb(164,205,238)','rgb(42,170,227)','rgb(25,46,94)','rgb(195,229,235)','#00F5FF','#CD0000'];
        var index=params.dataIndex%colorList.length;
        return colorList[index];
    }
    
    function initProgress(dash, id) {
        var homebartChart = echarts.init(document.getElementById(id + "-1"));
        var option = {
            tooltip: {
                // trigger: 'axis',
                trigger: {trigger: 'axis', formatter: '{b0}: {b1}<br />{b1}: {b}'},
                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                    type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            legend: {
                // data: ['已达成', '未达成']
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'value',
                show: false,
                axisPointer: {
                    value: '320',
                    snap: true,
                    lineStyle: {
                        color: '#004E52',
                        opacity: 0.5,
                        width: 2
                    },
                    handle: {
                        show: true,
                        color: '#004E52'
                    }
                }
            },
            yAxis: {
                type: 'category',
                data: ['销售目标']
            },
            series: [
                {
                    name: '已达成',
                    type: 'bar',
                    stack: '总量',
                    label: {
                        normal: {
                            show: true,
                            position: 'insideRight',
                            formatter: '{a}: {c}'
                        }
                    },
                    data: [320]
                },
                {
                    name: '未达成',
                    type: 'bar',
                    stack: '总量',
                    label: {
                        normal: {
                            show: true,
                            position: 'insideRight',
                            formatter: ' {a}: {c}'
                        }
                    },
                    data: [180]
                }
            ]
        };
        homebartChart.setOption(option);
        homebartChart = echarts.init(document.getElementById(id + "-2"));
        homebartChart.setOption(option);
    }

    function initBar(dash, data, id) {
        if (data.series == undefined) return;
        var position="top";
    	if(data.series.length==1){
    		for(var key in data.series){
        		var label={normal:{show:true,position: position}};
        		data.series[key]["label"]=label;
        		data.series[key]["itemStyle"]={normal:{color:setColor}};
        	}
    	}
        var $Bar = document.getElementById(id);
        var BarChart = echarts.init($Bar);

        var option = {
            title: {
                //text: dash.dashboard_title
            },
            grid: {
                top: '15%',
                left: '5%',
                right: '10%',
                bottom: '8%',
                containLabel: true
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                left: 'left',
                data: data.legend
            },
            toolbox: {
                show: true
            },
            calculable: true,
            xAxis: [
                {
                    type: 'category',
                    data: data.xAxis
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    show:false
                }
            ],
            series: data.series
        };
        BarChart.setOption(option);
        window.addEventListener("resize", function () {
            if (!$("#home-tab").is(":hidden")) {
                BarChart.resize();
            }
        });
        $("#" + id).css("overflow", "hidden");
    }

    function initRanking(dash, data, id) {
        if (data.series == undefined) return;
        var position="right";

		var label={normal:{show:true,position: position}};
		data.series[0]["label"]=label;
		data.series[0]["itemStyle"]={normal:{color:setColor}};

        var $Bar = document.getElementById(id);
        var BarChart = echarts.init($Bar);

        var option = {
            grid: {
                top: '5%',
                left: '5%',
                right: '10%',
                bottom: '8%',
                containLabel: true
            },
            tooltip: {
                trigger: 'axis'
            },
            toolbox: {
                show: true
            },
            calculable: true,
            yAxis: [
                {
                    type: 'category',
                    data: data.xAxis,
                    axisLabel:{formatter: formatter}
                }
            ],
            xAxis: [
                {
                    type: 'value',
                    show:false
                }
            ],
            series: data.series
        };
        
        function formatter(value, index){
        	var x=value;
            if(value.length>9){
                var t=Math.floor(value.length/2);
                var str1=value.substr(0,t);
                var str3=value.substr(t,value.length-t);
                x=str1+'\n'+str3
            }
            return x;
        } 
        
        BarChart.setOption(option);

        window.addEventListener("resize", function () {
            if (!$("#home-tab").is(":hidden")) {
                BarChart.resize();
            }
        });
        
        $("#" + id).css("overflow", "hidden");
    }

    function initPie(dash, data, id) {
        var $Bar = document.getElementById(id);

        var BarChart = echarts.init($Bar);

        var option = {
            title: {
                //text: dash.dashboard_title,
                x: 'center'
            },
            grid: {
                left: '0%',
                right: '0%',
                bottom: '0%',
                containLabel: true
            },
            tooltip: {
                trigger: 'item',
                formatter: "{b} : {c} ({d}%)"
            },
            legend: {
                left: 'left',
                data: data.legend
            },
            series: [
                {
                    name: dash.dashboard_title,
                    type: 'pie',
                    radius: '50%',
                    center: ['50%', '55%'],
                    hoverOffset: 5,
                    data: data.series,
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        };

        BarChart.setOption(option);

        window.addEventListener("resize", function () {
            if (!$("#home-tab").is(":hidden")) {
                BarChart.resize();
            }
        });
    }

    function initFunnel(dash, data, id) {
        var $Bar = document.getElementById(id);
        var BarChart = echarts.init($Bar);
        var option = {
            title: {
                //  text: dash.dashboard_title,
            },
            grid: {
                left: '0%',
                right: '0%',
                bottom: '0%',
                containLabel: true
            },
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c}%"
            },
            toolbox: {
                feature: {
                    //dataView: {readOnly: false},
                    //restore: {},
                    //saveAsImage: {}
                }
            },
            legend: {
                left: 'left',
                data: data.legend
            },
            calculable: true,
            series: [
                {
                    name: '漏斗图',
                    type: 'funnel',
                    left: '10%',
                    top: 60,
                    bottom: 60,
                    width: '80%',
                    min: 0,
                    max: 100,
                    minSize: '0%',
                    maxSize: '100%',
                    sort: 'descending',
                    gap: 2,
                    label: {
                        normal: {
                            show: true,
                            position: 'inside'
                        },
                        emphasis: {
                            textStyle: {
                                fontSize: 20
                            }
                        }
                    },
                    labelLine: {
                        normal: {
                            length: 10,
                            lineStyle: {
                                width: 1,
                                type: 'solid'
                            }
                        }
                    },
                    itemStyle: {
                        normal: {
                            borderColor: '#fff',
                            borderWidth: 1
                        }
                    },
                    data: data.series
                }
            ]
        };
        BarChart.setOption(option);

        window.addEventListener("resize", function () {
            if (!$("#home-tab").is(":hidden")) {
                BarChart.resize();
            }
        });
    }

    function initRadar(dash, data, id) {
        var $Bar = document.getElementById(id);
        var BarChart = echarts.init($Bar);
        var option = {
            title: {
                // text: dash.dashboard_title
            },
            grid: {
                left: '0%',
                right: '0%',
                bottom: '0%',
                containLabel: true
            },
            tooltip: {},
            legend: {
                left: 'left',
                data: data.legend
            },
            radar: {
                name: {
                    textStyle: {
                        color: '#fff',
                        backgroundColor: '#999',
                        borderRadius: 3,
                        padding: [3, 5]
                    }
                },
                indicator: data.indicator
            },
            series: [{
                type: 'radar',
                data: data.series
            }]
        };
        BarChart.setOption(option);

        window.addEventListener("resize", function () {
            if (!$("#home-tab").is(":hidden")) {
                BarChart.resize();
            }
        });
    }
    
    
    //初始化列表仪表盘数据
    function initTable(dash, $dash, id) {
        var param = eval("(" + dash.dashboard_param + ")");
        var condition = " ";
        for (var key in param.condition) {
            condition += "and " + key + param.type["." + key + "_type"] + "'" + eval("(" + param.condition[key] + ")") + "' ";
        }

        var req = {table:param.table,orderBy:param.orderBy,defaultCount:param.defaultCount, condition: condition};
        postJson('/dashboard/getTableData.do', req, function (data) {
            var totlerow = {
                "total": data.count,
                "rows": data.list
            };
            var $table = $('<table id="' + id + '-1" class=""></table>');
            $dash.append($table);
            if (data.count != 0 && data.count != null && data.count != undefined)
                $dash.parent().prev().append("<span class='badge-count'>（" + data.count + "）</span>");
            var columns = [];
            var list = [];
            var table = dx.table;
            var refIdColumns;
            var columnsCache = table[param.table].columns;
            if (table[param.ref_table].idColumns != undefined) {
                refIdColumns = table[param.ref_table].idColumns;
            }
            var column_name;
            var row;
            for (var col in columnsCache) {
                column_name = columnsCache[col].column_name;
                row = {
                    field: column_name,
                    title: columnsCache[col].i18n[dx.user.language_id],
                    width: 80,
                    hidden: columnsCache[col].hidden,
                    data_type: columnsCache[col].data_type,
                    formatter: function (value, row, index) {
                        if (this.data_type == 4 && value != '' && value != null) {
                            return new Date(value).format("yyyy-MM-dd");
                        } else if (this.data_type == 12 && value != '' && value != null) {
                            return new Date(value).format("yyyy-MM-dd hh:mm");
                        } else if(this.data_type == 8){
                        	return '<a href="#" name="' + value + '" class="dashboard-download-a">' + value.substr(value.lastIndexOf('/') + 1) + '</a>';
                        }else return value;
                    }
                };
                columns.push(row);
            }
            list.push(columns);
            $table.datagrid({
                height: "100%",
                singleSelect: false,
                rownumbers: false,
                striped: true,
                fitColumns: true,
                resizable: true,
                pagination: false,
                columns: list,
                scrollbarSize:0,
                onDblClickRow: function (rowIndex, rowData) {
                    var ref = {};
                    for (var key in refIdColumns) {
                        ref[refIdColumns[key]] = rowData[refIdColumns[key]];
                    }
                    var data = {table: param.ref_table, param: ref, isIndexView: 1, readonly: true}
                    newTab('/detail/edit.view', data, function (form, $li) {
                        var rows = $table.datagrid('getRows');
                        for (var i=0; i<rows.length; i++){
                            rows[i].rowid = i;
                            var ref = {};
                            for (var key in refIdColumns) {
                                ref[refIdColumns[key]] = rows[i][refIdColumns[key]];
                            }
                            rows[i].ref = ref;
                            rows[i].indexGrid = true;
                        }
                        var beforeAfterRows = {};
                        beforeAfterRows.rows = rows;
                        beforeAfterRows.rowid = rowIndex;
                        beforeAfterRows.indexGrid = true;
                        form.beforeAfterRows = beforeAfterRows;
                    }, param.ref_table);
                }
            });
            
            $table.datagrid('loadData', totlerow);
            //是否存在引用表
            if(param.ref_table!=""){
            	var $getMore=$("<div style='width: 100%;height:10px;position: absolute;bottom: 10px;' align='center'>"+msg("view_more")+"</div>");
            	$dash.append($getMore);
            	$getMore.click(function(){
            		newTab('/list/table.view', {table:param.ref_table});
            	});
            }
            
            //列表文件下载
	        $form.find('a.dashboard-download-a').click(function (e) {
	            e.stopPropagation();
	            fileDownload("/dashboard/download.do", {id: $(e.target).prop('name')});
	            var param = {};
	            param.data = {id: $(e.target).prop('name')};
	            postJsonRaw("/dashboard/download.do", param, function (data) {
	                var viewUrl = "";
	                var downloadUrl = "";
	                if (data.data != null) {
	                    viewUrl = data.data.viewUrl;
	                    downloadUrl = data.data.downloadUrl;
	                }
	                $("body").append("<iframe src='" + makeUrl(downloadUrl) + "' style='display: none;' ></iframe>");
	            });
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
        $('#table-size-' + dash.dashboard_size).click();
        $("input[type=radio][name='subscribe-type'][value=" + dash.subscribe_type + "]").iCheck('check');
        if (type == "1") {
            $('#tab-config-summer').summernote('code', dash.dashboard_richtext);
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
            var condition = param.condition;
            var $condition = $form.find('.tab-table-condition');
            initConditionTable(param.table, condition, $condition, "tab-table", param);
        }
    }

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

    function addNotify(dash, id, $ol) {
        var $dash = newItems(dash, id, $ol);
        $dash.html(dash.dashboard_richtext.replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
    }

    function addApprove(dash, id, $ol) {
        newItems(dash, id, $ol);
    }

    function addWorkDate(dash, id, $ol) {
        var $dash = newItems(dash, id, $ol);
        //$dash.datetimepicker();
        $dash.workCalendar({//calendar
            ifSwitch: true, // 是否切换月份
            hoverDate: true, // hover是否显示当天信息
            backToday: true // 是否返回当天
        });
    }

    function addProgress(dash, $ol) {
        var size = dash.dashboard_size;
        if (size == 1 || size == 2) {
            size = 3;
        } else {
            size = 6;
        }
        var $items = $form.find('.sub_tab_items');
        var $container = $('<div class="dx-field-' + size + '"></div>');
        var $panel = $('<div class="panel"></div>');
        var $head = $('<div class="panel-heading">' + msg(dash.dashboard_title) + '</div>');
        var $body = $('<div class="panel-body"></div>');
        var $dash1 = $('<div id="' + dash.dashboard_id + '-1" style="width: 100%;height:100px;"></div>');
        var $dash2 = $('<div id="' + dash.dashboard_id + '-2" style="width: 100%;height:100px;"></div>');
        $body.append($dash1);
        $body.append($dash2);
        $panel.append($head);
        $panel.append($body);
        $container.append($panel);
        $items.append($container);

        initProgress(dash);
    }

    function addPercent(dash, id, $ol) {
        newItems(dash, id, $ol);
        var param = eval("(" + dash.dashboard_param + ")");
        var percent = eval(param.actual_sql) / eval(param.target_sql) * 100;
        initPercent({title: msg(dash.dashboard_title), percent: percent, id: dash.dashboard_id}, id);
    }

    function addTable(dash, id, $ol) {
        var $dash = newItems(dash, id, $ol);
        initTable(dash, $dash, id);
    }

    function addChart(dash, id, $ol) {
        newItems(dash, id, $ol);
        var param = eval("(" + dash.dashboard_param + ")");

        var condition = " ";
        for (var key in param.condition) {
            condition += "and " + key + "='" + eval("(" + param.condition[key] + ")") + "' ";
        }
        condition = $.trim(condition.replace(/and/, ""));

        postJson('/dashboard/getChartData.do', {report_id: param.report_id, condition: condition}, function (data) {
            if (data.ret) {
                if (data.type == 1 || data.type == 6) {
                    initBar(dash, data, id);
                } else if (data.type == 2) {
                    initPie(dash, data, id);
                } else if (data.type == 5) {
                    initFunnel(dash, data, id);
                } else if (data.type == 7) {
                    initRadar(dash, data, id);
                }else if(data.type == 8){
                	initRanking(dash, data, id);
                }
            }
        });
    }

    //初始化仪表盘订阅页
    function initSubscribeTab(Subscribes) {
        var dash;
        var type;
        var id;
        var $ol = $form.find('.sort-container');
        $ol.find('li').remove();
        for (var key in Subscribes) {
            dash = Subscribes[key];
            id = dash.dashboard_id;
            type = dash.dashboard_type;
            //if (dash.subscribe_type == 1) continue;
            if (type == "1") {
                addNotify(dash, id, $ol);
            } else if (type == "2") {
                addApprove(dash, id, $ol);
            } else if (type == "3") {
                addWorkDate(dash, id, $ol);
            } else if (type == "4") {
                addPercent(dash, id, $ol);
            } else if (type == "5") {
                addProgress(dash, id, $ol);
            } else if (type == "6") {
                addChart(dash, id, $ol);
            } else if (type == "7") {
                addTable(dash, id, $ol);
            }
            if (dash.subscribe_type == 1 || dash.subscribe_status == "1") {
                $('#' + dash.dashboard_id).parent().parent().append("<div class='panel-check-box'><img src='img/home-right.png'/></div>");
                if (dash.subscribe_type == 1) {
                    $('#' + dash.dashboard_id).parent().parent().removeClass("free");
                    $('#' + dash.dashboard_id).parents("li").css("display","none");
                }
            }
        }
    }

    /**
     * 帮助文档下载。
     */
    function downloadHelpDocumentation(){
        $("body").append("<iframe src='" + makeUrl('/dashboard/downloadHelpDocumentation.do') + "' style='display: none;' ></iframe>");
    }
    /**
     * lex添加 6.7
     * @param dash
     * @param id
     * @param $ol
     * @returns {Mixed|jQuery|void|HTMLElement}
     */
    function newItemsLex(dash, id, $ol) {
        var title=id;
        var size = 3;
        var height = "300px";
        var require = "home-common-require";
        var $close = $("<span class='pull-right remove-dashboard'>×<span>");
        var $li = $('<li class="dx-field-' + size + '"></li>');
        // var $container = $('<div></div>');
        var $panel = $('<div class="panel free panel-checked ' + require + '"></div>');
        var $head = $('<div class="panel-heading">' + title + '</div>');
        var $body, $dash;
        $body = $('<div class="panel-body" style="position: relative"></div>');
        $dash = $('<div id="' + id + '" style="width: 100%;height:' + height + ';overflow: auto" class="dash eightStyle">' +
            '<img src="./img/file_box@2x.png" alt="">'+
            '<p>' + msg('HelpDocumentation') + '</p ><p style="font-size:14px;font-family:SourceHanSansCN-Regular;color:rgba(153,153,153,1);">1.63MB</p>' +
            '<a class="updown help-documentation" style="width: auto; padding: 0px 6px">' +
            '<img src="./img/up.png"  style="margin-top: -3px;margin-right: 6px;" alt="">' +
            msg('download') + '</a></div>');
        $body.append($dash);
        $panel.append($head);
        $head.append($close);
        $panel.append($body);
        $li.append($panel);
        $ol.append($li);
        $('.help-documentation').off('click');
        $('.help-documentation').on('click', function () {
            downloadHelpDocumentation();
        });
        return $dash;
    }

    function newItemsLex1(dash, id, $ol) {
        var title=id;
        var size = 3;
        var height = "270px";
        var require = "home-common-require";
        var $close = $("<span class='pull-right remove-dashboard'>×<span>");
        var $li = $('<li class="dx-field-' + size + '"></li>');
        // var $container = $('<div></div>');
        var $panel = $('<div class="panel free panel-checked ' + require + '"></div>');
        var $head = $('<div class="panel-heading">' + msg(dash.dashboard_title) + '</div>');
        var $body, $dash;
        $body = $('<div class="panel-body" style="position: relative"></div>');
        var dashHtml = '<div id="' + id + '" style="width: 100%;height:' + height + ';overflow: auto" class="dash nineStyle">' + '<ul>';
        if (!isEmpty(dash) && !isEmpty(dash.dashboard_param)){
            for (var i=0; i<dash.dashboard_param.length; i++){
                dashHtml += '<li class="index-shortcut-menus" page_id="' +
                    dash.dashboard_param[i].page_id + '" menuIcon="  ' +
                    dash.dashboard_param[i].icon + '"><span class="' + dash.dashboard_param[i].icon + '"></span><span>' +
                    msg(dash.dashboard_param[i].international_id) + '</span></li>';
            }
        }
        dashHtml += '</ul></div>';
        $dash = $(dashHtml);
        $body.append($dash);
        $panel.append($head);
        $head.append($close);
        $panel.append($body);
        $li.append($panel);
        $ol.append($li);
        $('.index-shortcut-menus').off('click');
        $('.index-shortcut-menus').on('click', function () {
            var page_id = $(this).attr('page_id');
            var menuIcon = $(this).attr('menumenuIconid');
            openPage(page_id, menuIcon);
        });
        return $dash;
    }

    function newItemsLex2(dash, id, $ol) {
        var title=id;
        var size = 3;
        var height = "300px";
        var require = "home-common-require";
        var $close = $("<span class='pull-right remove-dashboard'><span>");
        var $li = $('<li class="dx-field-' + size + '"></li>');
        // var $container = $('<div></div>');
        var $panel = $('<div class="panel free panel-checked ' + require + '"></div>');
        var $head = $('<div class="panel-heading">' + title + '</div>');
        var $body, $dash;
        $body = $('<div class="panel-body" style="position: relative"></div>');
        $dash = $('<div id="' + id + '" style="width: 100%;height:' + height + ';overflow: auto" class="dash eightStyle index-subscription">' +
            '<img src="./img/subscription@2x.png" alt="">'+
            '<p></p><p style="font-size:12px;font-family:SourceHanSansCN-Regular;color:rgba(153,153,153,1);margin: 13px">' + msg('Subscribe contents you are interested') + '</p>' +
            '<a href="#home-sub-tab" class="updown" id="dash-subscribe" data-toggle="tab" aria-controls="home-sub-tab" role="tab" aria-expanded="true" style="width: auto; padding: 0px 6px">' +
            '<img src="./img/star@2x.png" style="margin-top: -3px;margin-right: 6px;" alt="">' + msg('subscription') + '</a>' +
           '</div>');
        $body.append($dash);
        $panel.append($head);
        $head.append($close);
        $panel.append($body);
        $li.append($panel);
        $ol.append($li);
        $('.index-subscription').off('click');
        $('.index-subscription').on('click', function () {
            if (dashboardCache == undefined) {
                getDashboards();
                initListTableItems();
                initChartList();
            }
        });
        return $dash;
    }

    function menuTab(dash, id, $ol){
        var $dash = newItems(dash, id, $ol);
        var param = eval("(" + dash.dashboard_param + ")");
        var page = getPageDesc(param.page);
        if (!page.url)
            return;
        var data;
        if (page.param)
            data = evaluate('(' + page.param + ')', null);
        else
            data = {};
        postPage(page.url, data, function (result) {
            console.log(page.url)
            $dash.append(result);
            //by now, the embedded js has been executed, we can use cache variables
            var $form = $dash.find('.dx-form');
            var form = getFormModel($form.attr('id'));
            buildFormCache(form, form.widgets);

            if (dx.init[form.widgetName])
                dx.init[form.widgetName](form);
            else
                console.warn('no init function registered for "' + form.widgetName + '"');

            if (dx.moduleInit[form.widgetName])
                dx.moduleInit[form.widgetName].forEach(function (func) {
                    func(form);
                });
            doAutoExpand(form);
            if(form.action!="create"){
                $form.find(".dx-editor-input-textarea").summernote({
                    popover:{
                        image:false
                    }}).summernote('disable');
            }
        });
    }
    //初始化首页仪表盘
    function initHomeTab() {
        postJson('/dashboard/getHomeSubscribes.do', {}, function (data) {
            var dash;
            var type;
            var id;
            var $ol = $form.find('.home-sort-container');
            $ol.find('li').remove();
            for (var key in data) {
                dash = data[key];
                id = dash.subscribe_id;
                type = dash.dashboard_type;
                if (type == "1") {//newHomeItems
                    addNotify(dash, id, $ol);
                } else if (type == "2") {
                    addApprove(dash, id, $ol);
                } else if (type == "3") {
                    addWorkDate(dash, id, $ol);
                } else if (type == "4") {
                    addPercent(dash, id, $ol);
                } else if (type == "5") {
                    addProgress(dash, id, $ol);
                } else if (type == "6") {
                    addChart(dash, id, $ol);
                } else if (type == "7") {
                    addTable(dash, id, $ol);
                }else if (type == "8") {
                    //newItemsLex(dash, id, $ol);
                }else if (type == "9") {
                    newItemsLex1(dash, id, $ol);
                }else if (type == "10") {
                    //newItemsLex2(dash, id, $ol);
                }else if (type == "11") {
                    menuTab(dash, id, $ol);
                }
            }

            initIndexApprove();
            try {
                //$('ul.home-sort-container').sortable();
                $('ul.home-sort-container').sortable().bind('sortupdate', function () {
                    var lis = $form.find('.home-sort-container li .dash');
                    var $dash;
                    var subscribe_id;
                    var subscribes = [];
                    for (var i = 0; i < lis.length; i++) {
                        $dash = lis[i];
                        subscribe_id = $dash.id;
                        subscribes.push({subscribe_id: subscribe_id, seq: i});
                    }
                    postJson('/dashboard/updateSubscribesSeq.do', {subscribes: subscribes}, function (data) {
                        if (data) {
                            alert(msg(data));
                        }
                    });

                });
            } catch (error) {
            }


            $form.find('.remove-dashboard').on("click", function () {
                var $dash = $(this).parent().next().find('.dash');
                var id = $dash.attr("id");
                var $li = $(this).parent().parent().parent();
                $li .siblings("li").show();
                $li.remove();
                postJson('/dashboard/cancelSubscribe.do', {dashboard: {subscribe_id: id}}, function (data) {

                });
            });

        });
    }

    initHomeTab();


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
                $items.append('<option value="' + Key + '" >' + getI18n(dx.user.language_id, table.i18n) + '</option>');
                $refTable.append('<option value="' + Key + '" >' + getI18n(dx.user.language_id, table.i18n) + '</option>');
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

    function initChartList() {
        var $chars = $form.find('.chart-list');
        $chars.empty();
        postJson('/dashboard/getChartList.do', {}, function (result) {
            var charts = result;
            for (var chart in charts) {
                $chars.append('<option value="' + charts[chart].id + '" name="' + charts[chart].table_id + '">' + charts[chart].international_id + '</sption>');
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

    function refresh() {
        getDashboards();
        isInitSubscribeTab = 1;
    }

    //=========================仪表盘配置动作

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
            //if(isSave == 0) isSave=1;
        } else {
            alert(msg("There is no data to be saved"));
            // buildParam();
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


    //初始化配置页菜单
    $form.find('#dash-deploy').click(function () {
        if (dashboardCache == undefined) {
            getDashboards();
            initListTableItems();
            initChartList();
        }
    });

    //初始化订阅页
    $form.find('#dash-subscribe').click(function () {
        if (isInitSubscribeTab == 1) {
            postJson('/dashboard/getSubscribes.do', {}, function (result) {
                initSubscribeTab(result);
            });
            isInitSubscribeTab = 0;
        }

    });


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

    //订阅保存事件
    $('#tab-sub-ok').click(function () {
        var dashs = $form.find('.panel-check-box');
        var dashboard_id;
        var subscribes = [];
        var $dash;
        for (var i = 0; i < dashs.length; i++) {
            $dash = $(dashs[i]).parent().find('.dash');
            dashboard_id = $dash.attr("id");
            subscribes.push({dashboard_id: dashboard_id, seq: i});
        }
        postJson('/dashboard/updateSubscribes.do', {subscribes: subscribes}, function (data) {
            if (data) {
                dxToastAlert(msg(data));
            }
        });
    });

    //默认选择事件
    $("#home-sub-tab").on("click", ".free", function () {
    	/*var  dashboard_id=$(this).find("dash").attr("id");
    	var subscribe_status;*/
        if (!$(this).hasClass("panel-checked")) {
            $(this).addClass("panel-checked");
            $(this).find(".panel-check-box").remove();
            $(this).append("<div class='panel-check-box'><img src='img/home-right.png' /></div>");
            //subscribe_status=1;
        } else {
        	//alert("no");
            $(this).removeClass("panel-checked");
            $(this).find(".panel-check-box").remove();
            //subscribe_status=0;
        }
        /*var req={dashboard:{dashboard_id:dashboard_id,subscribe_status:}}
        postJson('/dashboard/ifSubscribe.do',req,function(){
        	
        });*/
    });


    $("#home-sub-tab .sub-all-select").on("click", function () {
        if (checkAll == 0) {
            for (var i = 0; i < $(".sub_tab_items li").length; i++) {
                if (!$(".sub_tab_items li").eq(i).find(".panel").hasClass("panel-checked")) {
                    $(".sub_tab_items li").eq(i).find(".panel").addClass('panel-checked');
                    $(".sub_tab_items li").eq(i).find(".panel").append("<div class='panel-check-box'><img src='img/home-right.png' /></div>");
                    //$("#home-sub-tab .free").addClass('panel-checked');
                    //$("#home-sub-tab .free").append("<div class='panel-check-box'><img src='img/home-right.png' /></div>");
                }
            }
            checkAll = 1;
        } else {
            $("#home-sub-tab .free").find(".panel-check-box").remove();
            $("#home-sub-tab .free").removeClass('panel-checked');
            checkAll = 0;
        }
    });
    $(".tab-sub-footer input[type=checkbox]").on('ifChecked', function () {
        $("#home-sub-tab .free").find(".panel-check-box").remove();
        $("#home-sub-tab .free").append("<div class='panel-check-box'><img src='img/home-right.png' /></div>");

    }).on('ifUnchecked', function () {
    });


    // 初始化个人头像
    initPersonPhoth();
    function initPersonPhoth() {
        var imageUrl = dx.user.imageUrl;
        if (imageUrl == undefined || imageUrl == "") {
        } else {
            $('#persionPhoth').attr('src', imageUrl);
        }
    }


    initMessageIcon();
    //初始化message图标
    function initMessageIcon() {
        postJson('/widget/message/getUnreadCount.do', {}, function (data) {
            if (data != 0) {
                $('#unreadMessage').addClass("badge");
                $('#unreadMessage .badge-count').text(data);
            }
        });
    }

    //消息按钮点击事件
    $('.headTopTool .message').on('click', function () {
        if (dx.isOpenMessage == 1) {
            newTab('/widget/message/select.view', {});
            dx.isOpenMessage = 0;
        }else{
        	$('.index-tab-navs li a[title="message"]').tab('show');
        	$(".index-tab-navs li,.home-tab-navs-rest li").removeClass("active");
            $('.index-tab-navs li a[title="message"]').parent().addClass("active");
        }
        

    });

    //TODO 邮件审批进的首页。打开待审批数据。。参数由index 存入form中。
    if (!isEmpty(form.emailDetail)){
        var table = form.emailDetail.table;
        var param = form.emailDetail.param;
        newTab('/detail/edit.view', {table: table, param: param}, function (result) {

        });
    }
    function initIndexApprove() {
            var approveDiv = $('.approve-div');
            if (isEmpty(approveDiv) || approveDiv.length == 0)
                return;
            approveDiv.empty();
            postPage('/selectApproveDatas.view', {}, function (result) {
                approveDiv.append(result);
                var newForm = getFormModel(approveDiv.find('.index-approve-form').attr('id'));
                if (isEmpty(newForm)) {
                    return;
                }
                dx.init.indexApprove(newForm);
                var approveData = {};
                $('.index-approve-button').on('click', function () {
                    approveData.approveId = $(this).parent().parent().find('.approve-data').attr('approve_id');
                    approveData.tableId = $(this).parent().parent().find('.approve-data').attr('table_id');
                    approveData.blockId = $(this).parent().parent().find('.approve-data').attr('block_id');
                    approveData.belongBlock = $(this).parent().parent().find('.approve-data').attr('belongBlock');
                    approveData.dataId = $(this).parent().parent().find('.approve-data').attr('data_id');
                    approveData.eventId = $(this).attr('event_id');
                });
                $('.index_approve-pass-submit').off('click');
                $('.index_approve-pass-submit').on('click', function () {
                    if (isEmpty(approveData)) {
                        alert(msg('approve is null'));
                        return;
                    }
                    var approveReason = $('.index-approve-reason').val().trim();
                    var table = getTableDesc(approveData.tableId);
                    var event;
                    if (!isEmpty(approveData.belongBlock))
                        event = table.approveButtonEvent[approveData.belongBlock][approveData.eventId];
                    else
                        event = table.approveButtonEvent[approveData.blockId][approveData.eventId];
                    var blockEvent, requestBlockEvent = {};
                    if (!isEmpty(approveData.belongBlock)){
                        blockEvent = table.approveBlockEvent[approveData.belongBlock];
                    }else{
                        blockEvent = table.approveBlockEvent[approveData.blockId];
                    }
                    buildBlockEventParamForIndex(event, table, approveData.dataId, blockEvent, requestBlockEvent,function (flowEvent,data) {
                    	if(checkApproveButtonEventListOrIndex(flowEvent,data)){
                    		dx.processing.open();
                            postJson('/approve/approve.do', {
                                    blockId: approveData.blockId,
                                    tableId: approveData.tableId,
                                    approveReason: approveReason,
                                    dataId: approveData.dataId,
                                    approveId: approveData.approveId,
                                    flowEvent: flowEvent,
                                    approveBlockEvent : requestBlockEvent,
                                    formId : form.id
                                }, function () {
                                    dxToastAlert(msg("approveSuccess"));
                                    approveAfterNewPage(flowEvent);
                                    dx.reloadIndexApprove();
                                    dx.processing.close();
                                }
                            );
                    	}
                    });
                });

                //刷新按钮点击事件
                $('.approve-reload').off('click');
                $('.approve-reload').on('click', function () {
                    dx.reloadIndexApprove();
                })
            });
    }

    dx.reloadIndexApprove = function reloadIndexApprove() {
        initIndexApprove();
    };
    initIndexApprove();

    function initTodo($container) {
        var timer = null;
        var $currentDiv = null;

        $container.find('table.dx-operation-table').find('tr').each(function () {
            function clear($div) {
                $div.hide();
                timer = null;
            }

            var id = $(this).attr('id');
            var key = $(this).attr('key');
            var parentId = $(this).attr('parentId');
            var $div = $('#' + id + '_' + key);
            $(this).hover(function (event) {
//				function getmouseX(event){
//					var e = event || window.event;
//					return e.clientX;
//				}
                    if (timer) {
                        clearTimeout(timer);
                        clear($currentDiv);
                    }
                    $currentDiv = $div;
                    var $parent = $('#' + parentId);
                    $div.removeClass('hidden');
//				var mouseX = event.offsetX;
//				if(event.target.cellIndex != 0){
//					var $td = event.target;
//					mouseX = mouseX + $td.clientWidth;
//				}
                    $div.css("left", $parent.position().left + 70)
                        .css("top", $(this).position().top + $(this).height() + $parent.position().top).show();
                    $div.hover(function () {
                        if (timer) {
                            clearTimeout(timer);
                        }
                    }, function () {
                        clear($div);
                    });
                },
                function () {
                    timer = setTimeout(function () {
                        clear($div);
                    }, 500);
                });
            desktopOpClick(form.id, key, id);
        });
        $container.find('.dx-todo-refresh').on('click', function (e) {
            dx.processing.open(msg('Processing'));
            postJson('/index/refreshTodo.do', {id: form.id}, function (data) {
                var a = e.target;
                var containerID = $(a).attr('key').indexOf('person') == 0 ? 'home-person-tab' : 'home-common-tab';
                var $container = $('#' + containerID);
                $form.find('.dx-todo-' + a.name).remove();
                $container.append(data.html);
                data.matter.matterDesk.forEach(buildRecordCache);

                initTodo($container);
                dx.processing.close();
            });
        });
    }

    function initRecords(list) {
        var todo;
        $.each(list, function (i, deskdata) {
            if (deskdata.hasOwnProperty('matter')) {
                todo = deskdata.matter.matterDesk;
            }
        });
        if (todo)
            todo.forEach(buildRecordCache);
    }


    initRecords(form.commonDesk);
    initRecords(form.personDesk);

    initTodo($form);
    // init list
    $form.find('.dx-desktop-list').each(function (i, node) {
        var $node = $(node);
        var listId = $node.attr('name');
        var list = form.listMap[listId];
        var options = {
            ajax: function (ignore, callback) {
                execSql(form, list.sql, true, {
                    map: true,
                    callback: function (list) {
                        callback({data: list});
                    }
                });
            },
            paging: false,
            dom: 't',
            columns: [],
            drawCallback: function () {
                if (!list.url)
                    return;

                $node.find('tbody tr').on('click', function () {
                    var data = $node.DataTable().row(this).data();
                    var param = evaluate('(' + data.param + ')', data);
                    newTab(list.url, param);
                }).addClass('dx-cursor-hand');
            }
        };
        var table = getTableDesc(list.view_id);
        table.columns.forEach(function (column) {
            if (!column.hidden)
                options.columns.push({data: column.column_name});
        });
        $(node).dataTable(options);
    })


}

function desktopOpClick(formId, key, id) {
    $('#' + id + '_' + key).find('.dx-operation-click').on('click', function () {
        var actionId = $(this).attr('value');
        var $id = $(this);
        if (key == 'commonDesk') {
            var commonDesk = w(formId).commonDesk;
            Op(commonDesk);
        } else {
            var personDesk = w(formId).personDesk;
            Op(personDesk);
        }

        function Op(deskDatas) {
            var widget = w(formId);
            var record = w(id);
            execOperation(record.table_id, record.status_col, actionId, [record.id], widget);
            widget.onUpdated = function () {

                var params = {
                    key_value: record.key_value,
                    column_name: record.status_col,
                    table: record.table_id,
                    status_col: record.status_now
                };
                postJson('/selectStatus.do', params, function (retData) {
                    var $tr = $id.parent().parent();
                    for (var i = 0; i < $tr.children().size(); i++) {
                        var $td = $tr.children().eq(i);
                        for (var j = 0; j < $td.children().size(); j++) {
                            $td.children().eq(j).attr('disabled', true);
                        }
                    }
                });
            };
        }
    });
}

function pageLink(recordId) {
    var record = w(recordId);
    var data = {};
    $.each(record.columns, function (key, field) {
        data[key] = field.value;
    });
    var viewData = {parent: null, param: data, table: record.table_id};
    viewData.readonly = true;
    var table = getTableDesc(record.table_id);
    if (table.table_type == 1) {
        newTab('/view/edit.view', viewData);
    } else {
        newTab('/detail/edit.view', viewData);
    }
}

