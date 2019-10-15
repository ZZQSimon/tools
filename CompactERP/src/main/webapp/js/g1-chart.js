/**
 * Created with IntelliJ IDEA.
 * User: wangqing
 * Date: 5/23/14
 * Time: 10:30 AM
 */

"use strict";

registerInit('chart', function (form) {
    var report_id = form.report_id;
    var $form = form.get();
    initField(form);
    initFilter(form, function (where) {
        // $form.find("#"+form.chart.id).remove();
        $form.find(".chart-pan").append('<div id="' + form.chart.id + '" style="width:100%;height:100%;"></div>');
        showChart(form, where);
    }); 
    function buildFilter(form, type){
        var fb = form.filter.build('exec');
        var data = {};
        if (type == 'chartToList')
            data.table = form.tableName;
        else
            data.report_id = report_id;
        var fixedData = {};
        if (!isEmpty(fb) && !isEmpty(fb.filters)){
            for (var key in fb.filters){
                fixedData[key] = fb.filters[key].value;
            }
        }
        data.fixedData = fixedData;
        return data;
    }
    //图标到列表的跳转
    function chartToList(form){
        var data = buildFilter(form, 'chartToList');
        postPage('/list/table.view', data, function (result) {
            closeForm(form.id, function () {
                var $div = form.get().parent();
                $div.empty();
                $div.append(result);
                var newForm = getFormModel($div.find('.dx-form').attr('id'));
                buildFormCache(newForm, newForm.widgets);
                dx.init.list(newForm);

                $('#' + newForm.id).find('.btn-toolbar').append('<button class="btn list-to-chart" type="button">' + msg('list-to-chart') + '</button>');
                $('#' + newForm.id).find('.list-to-chart').on('click', function () {
                    listToChart(newForm);
                });
            })
        })
    }
    //列表跳回图表
    function listToChart(form){
        var data = buildFilter(form);
        postPage('/chart/chart.view', data, function (result) {
            closeForm(form.id, function () {
                var $div = form.get().parent();
                $div.empty();
                $div.append(result);

                var newForm = getFormModel($div.find('.dx-form').attr('id'));
                buildFormCache(newForm, newForm.widgets);
                dx.init.chart(newForm);
            })
        })
    }
    if(dx.user.isMobileLogin!=1)
    	$form.find(".dx-filter-form").prepend("<h3>条件筛选</h3>");
    
    $form.find(".show-filters").on('click',function(){
    	var target=$(this).attr("toggle");
    	$form.find("."+target).css("display","block");
    	if(target=="chart-filter"){
    		$form.find(".dx-form-con").css("display","none");
    		$(this).attr("toggle","dx-form-con");
    	}else{
    		$form.find(".chart-filter").css("display","none");
    		$(this).attr("toggle","chart-filter");
    	}
    		
    });
    $form.find('.chart-to-list').on('click', function () {
        chartToList(form);
    });
    $form.find(".chart-config-btn").on("click", function () {
        if ($form.find(".chart-config").is(":hidden")) {
            $form.find(".chart-pan").addClass("active");
            $form.find(".chart-config").show();
            $(this).addClass("active");
        } else {
            $form.find(".chart-pan").removeClass("active");
            $form.find(".chart-config").hide();
            $(this).removeClass("active");
        }
    });

    /**
     * 实现图表显示的接口
     */
    function showChartInterface(form_id, report_id, div_id, where, backgroundColor, fontColor) {
        var data = {};
        data["form_id"] = form_id;
        data["report_id"] = report_id;
        data["div_id"] = div_id;
        data["where"] = where;
        postJson('/chart/filter/' + form_id + '.do', data,
            function (retData) {
        		/*if(retData.type==8){
        			barSort(retData);
        		}*/
 
        		dx.ajax=0;
                if (retData.msg != '' && retData.msg != null) {
                    messageBox(msg(retData.msg));
                }
                var dxchart;
                //处理成功
                if (retData.ret == 'true') {
                    if (retData.type == 1 || retData.type == 6 || retData.type == 8) {
                        dxchart = initBar(retData);
                    } else if (retData.type == 2) {
                        dxchart = initPie(retData);
                    } else if (retData.type == 5) {
                        dxchart = initFunnel(retData);
                    } else if (retData.type == 7) {
                        dxchart = initRadar(retData);
                    }
                    if(dx.user.isMobileLogin==1){
                    	$form.find(".show-filters").click();
                    }
                } else {
                    //处理失败
                }
                $form.find(".chart-config-btn").off("click");
                $form.find(".chart-config-btn").on("click", function () {
                    if ($form.find(".chart-config").is(":hidden")) {
                        $form.find(".chart-pan").addClass("active");
                        $form.find(".chart-config").show();
                        $(this).addClass("active");
                    } else {
                        $form.find(".chart-pan").removeClass("active");
                        $form.find(".chart-config").hide();
                        $(this).removeClass("active");
                    }
                    dxchart.resize();
                });

                window.addEventListener("resize",function(){
                    if(!$form.is(":hidden")){
                    	if(dxchart!=undefined)
                    		dxchart.resize();
                    }
                });
            });
    }

    /**
     * 调用图表显示接口
     */
    function showChart(form, where) {
        showChartInterface(form.id, form.report_id, form.chart.id, where, "", "");
    }


    function setColor(params){
    	var colorList = ['rgb(164,205,238)','rgb(42,170,227)','rgb(25,46,94)','rgb(195,229,235)','#00F5FF','#CD0000'];
        var index=params.dataIndex%colorList.length;
        return colorList[index];
    }
    function initBar(data) {
        var $Bar = document.getElementById(data.divId);
        var BarChart = echarts.init($Bar);
        
        var position="top";
    	if(data.series.length==1){
    		for(var key in data.series){
        		var label={normal:{show:true,position: position}};
        		data.series[key]["label"]=label;
        		data.series[key]["itemStyle"]={normal:{color:setColor}};
        	}
    	}
    	
        var option = {
            grid: {
                top: '20%',
                left: '10%',
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
                show: true,
                right: 20,
                feature: {
                    saveAsImage: {show: true}
                }
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
                    type: 'value'
                }
            ],
            series: data.series
        };

        BarChart.setOption(option);
        return BarChart;
    }
    
    
    
    function initRanking(data) {
        var $Bar = document.getElementById(data.divId);
        var BarChart = echarts.init($Bar);
        
        var position="right";
        var label={normal:{show:true,position: position}};
		data.series[0]["label"]=label;
		data.series[0]["itemStyle"]={normal:{color:setColor}};

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
                show: true,
                right: 20,
                feature: {
                    saveAsImage: {show: true}
                }
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
        return BarChart;
    }

    function initPie(data) {

        var $Pie = document.getElementById(data.divId);
        var BarChart = echarts.init($Pie);
        var colorList=["#EC0B43","#60B9E0","#00BAAF","#d48265"];
        for (var i=0;i<data.series.length;i++){
            var itemStyleObj={normal: {color:colorList[i],}};
            data.series[i].itemStyle=itemStyleObj;
            data.series[i].name=data.series[i].name.replace(/&lt;/, "");
            data.legend[i]=data.legend[i].replace(/&lt;/, "");

        }
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
                    //name: dash.dashboard_title,
                    type: 'pie',
                    radius: '50%',
                    center: ['50%', '55%'],
                    data: data.series,
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        },
                    }
                }
            ]
        };
        BarChart.setOption(option);
        return BarChart;
    }

    function initFunnel(data) {
        var $Funnel = document.getElementById(data.divId);
        var BarChart = echarts.init($Funnel);
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
        return BarChart;
    }

    function initRadar(data) {
        var $Radar = document.getElementById(data.divId);
        var BarChart = echarts.init($Radar);
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
        return BarChart;
    }
    

    $form.find(".dx-filter-exec").click();
});