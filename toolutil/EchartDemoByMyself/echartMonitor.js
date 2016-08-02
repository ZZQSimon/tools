/* 最大监控时间间隔（天） */
var maxMonitorDay = 5;
var renderChartsSumHeight = 0;
var RENDER_NUM_EVERYTIME = 3;

var timeRangeNum = 60;
var timeRangeUnit = 'Min';
var fromTime = 0;
var toTime = 0;
var monitorMetrics = '';

function metricsSetting() {
	layer.open({
		skin : 'layui-layer-lan',
		type : 1,
		title : '指标选择',
		shadeClose : false,
		shade : 0.7,
		area : [ '60%', '84%' ],
		content : $('#metricsSetting')
	});
}

/**
 * 时间单位选择级联时间数值下拉框
 * 
 * @param rec
 */
function onTimeRangeUnitSelected(rec) {
	var timebarCustom = document.getElementById('timebarCustom');
	var timebarOther = document.getElementById('timebarOther');
	if (rec.id == 'Custom') {
		timebarCustom.style.display = "";
		timebarOther.style.display = "none";
	} else {
		timebarCustom.style.display = "none";
		timebarOther.style.display = "";
		var url = WEB_APP + '/monitorPlatform/tool/timeRange?unitId=' + rec.id;
		$('#timeRange').combobox('reload', url);
		$('#timeRange').combobox('setValue', '');
	}
}

/**
 * 初始化duallistbox
 * 
 * assetId 资产ID assetType 资产类型ID selectClass class样式名称
 */
function initListBox(assetId, assetType, selectClass) {
	var paramData = {
		assetId : assetId,
		assetType : assetType
	};
	$.ajax({
		url : WEB_APP + '/monitorPlatform/monitor/metricSetting',
		type : 'get',
		data : paramData,
		async : true,
		success : function(returnData) {
			var ttMetrics = '';
			$(returnData).each(function() {
				if (this['selected'] == 'selected') {
					ttMetrics += ',' + this['id'];
				}
				var o = document.createElement("option");
				o.value = this['id'];
				o.text = this['text'];
				o.selected = this['selected'];
				$("." + selectClass + "")[0].options.add(o);
			});
			// 渲染dualListbox
			$('.' + selectClass + '').bootstrapDualListbox({
				nonSelectedListLabel : '不显示的指标',
				selectedListLabel : '显示的指标',
				preserveselectiononmove : 'moved',
				moveonselect : false,
				infotext : '指标数 {0}',
				infotextempty : '无指标',
				selectorminimalheight : 90
			});

			if (timeRangeUnit == 'Custom') {
				timebarCustom.style.display = "";
				timebarOther.style.display = "none";
				$('#fromTime').datebox('setValue', setInitDatebox(fromTime));	
				$('#toTime').datebox('setValue', setInitDatebox(toTime));	
			} else {
				timebarCustom.style.display = "none";
				timebarOther.style.display = "";
				var url = WEB_APP + '/monitorPlatform/tool/timeRange?unitId=' + timeRangeUnit;
				$('#timeRange').combobox('reload', url);
				$('#timeRange').combobox('setValue', timeRangeNum);
			}
			$('#timeRangeUnit').combobox('setValue', timeRangeUnit);
			
			if (ttMetrics == '') {
				renderMetricCharts(assetId, assetType, fromTime, toTime, monitorMetricNames,
						timeRangeNum, timeRangeUnit);
			} else {
				renderMetricCharts(assetId, assetType, fromTime, toTime, ttMetrics,
						timeRangeNum, timeRangeUnit);
			}
		},
		error : function(e) {
			alert(e.msg);
		}
	});
}

function refreshEcharts(metricLists) {
	// debugger;
	timeRangeUnit = $('#timeRangeUnit').combobox('getValue');
	if (timeRangeUnit == "" || timeRangeUnit == undefined) {
		$.messager.alert(Properties_I18N.title_prompt,
				Properties_I18N.validate_timeunit, 'error');
		return false;
	} else {
		var timeRangeMilli = 0;
		var fromTime = 0;
		var toTime = 0;

		if (timeRangeUnit == 'Custom') {
			var startdate = $('#fromTime').datebox('getValue');
			var enddate = $('#toTime').datebox('getValue');
			if (checkTime(startdate, enddate)) {
				fromTime = toMillisecond(startdate + ' 00:00:00');
				toTime = toMillisecond(enddate + ' 23:59:59');
			} else {
				return false;
			}
		} else {
			timeRangeNum = $('#timeRange').combobox('getValue');
			if (timeRangeNum == "" || timeRangeNum == undefined) {
				$.messager.alert(Properties_I18N.title_prompt,
						Properties_I18N.validate_timerange, 'error');
				return false;
			} else {
				if (timeRangeUnit == 'Min') {
					timeRangeMilli = timeRangeNum * 60 * 1000;
					toTime = new Date().getTime();
					fromTime = toTime - timeRangeMilli;
				} else if (timeRangeUnit == 'Hour') {
					timeRangeMilli = timeRangeNum * 60 * 60 * 1000;
					toTime = new Date().getTime();
					fromTime = toTime - timeRangeMilli;
				} else if (timeRangeUnit == 'Day') {
					timeRangeMilli = timeRangeNum * 24 * 60 * 60 * 1000;
					toTime = new Date().getTime();
					fromTime = toTime - timeRangeMilli;
				}
			}
		}
		removeEnvents();
		if (null == metricLists || metricLists == undefined) {
			var metricNames = $('[name="monitorMetricSetting"]').val();
			renderMetricCharts(assetId, assetType, fromTime, toTime,
					metricNames.toString(), timeRangeNum, timeRangeUnit);
		} else {
			renderMetricCharts(assetId, assetType, fromTime, toTime,
					metricLists, timeRangeNum, timeRangeUnit);
		}
	}
}
function removeEnvents() {
	document.getElementById("monitorData").innerHTML = '';

	if ($(window).data("events") && $(window).data("events")["resize"]) {
		$(".panel-body").unbind("resize");
	}
	if (($(".panel-body").data("events") && $(".panel-body").data("events")["scroll"])) {
		$(".panel-body").unbind("scroll");
	}
}
/**
 * 刷新数据，绘制图表
 * 
 */
function renderMetricCharts(assetId, assetType, fromTime, toTime, metricNames,
		timeRangeNum, timeRangeUnit) {
	removeEnvents();
	$.post(WEB_APP + "/monitorPlatform/monitor/getMonitorDatas", {
		assetId : assetId,
		assetType : assetType,
		fromTime : fromTime,
		timeRangeNum : timeRangeNum,
		timeRangeUnit : timeRangeUnit,
		toTime : toTime,
		metricNames : metricNames
	}, function(data) {
		removeEnvents();
		// 根据返回的数据，进行图表重新渲染
		var chartData = data.metricDatas;
		// console.log(chartData);
		// console.log("AAA:"+typeof(chartData));
		dynamicRenderChart(chartData);
	}, "json");
}

/*******************************************************************************
 * @author neu.zhangzhq
 * 
 * 根据窗口大小动态绘制指标图
 */
function dynamicRenderChart(chartData) {
	var windowHeight = $(window).height();

	// 初始载入窗口时应绘制的指标数
	var initRenderNum = calculateNextRenderNum(chartData, windowHeight - 20);
	chartData = renderPartCharts(chartData, initRenderNum);
	bindEvent(chartData);

}
/*******************************************************************************
 * @author neu.zhangzhq
 * 
 * 将未展现的指标绑定鼠标滚轮滚动事件
 * @param chartData
 */
function bindEvent(chartData) {
	if ($(window).data("events") && $(window).data("events")["resize"]) {
		$(".panel-body").unbind("resize");
	}
	// 窗口大小调整后echart自适应调整
	$(window)
			.resize(
					function() {
						echartsUtil.resize();
						var height = $(window).height() - 20
								- renderChartsSumHeight;
						if (height < 0)
							return;
						else {
							var nextRenderNum = calculateNextRenderNum(
									chartData, height);
							nextRenderNum = (nextRenderNum < RENDER_NUM_EVERYTIME) ? RENDER_NUM_EVERYTIME
									: nextRenderNum;
							nextRenderNum = (chartData.length > nextRenderNum) ? nextRenderNum
									: chartData.length;
							// 当前已绘制指标图的数量
							chartData = renderPartCharts(chartData,
									nextRenderNum);
						}
					});

	if (($(".panel-body").data("events") && $(".panel-body").data("events")["scroll"])) {
		$(".panel-body").unbind("scroll");
	}
	$(".panel-body")
			.scroll(
					function() {
						if (chartData.length == 0)
							return;
						var nextDrawChartNum = 0;
						// 滚动条是否到窗口底部，默认再加载高度为483
						if ($(this)[0].scrollTop + $(".panel-body").height() >= $(this)[0].scrollHeight) {
							var nextRenderNum = calculateNextRenderNum(
									chartData, 483);
							chartData = renderPartCharts(chartData,
									nextRenderNum);
						}
					});
}

/*******************************************************************************
 * @author neu.zhangzhq
 * 
 * 计算需要渲染的指标数量
 */
function calculateNextRenderNum(chartData, height) {
	var renderHeight = 0;
	var num = 0;
	while (renderHeight <= height && num < chartData.length) {
		renderHeight += calculateChartHeight(chartData[num]);
		num++;
	}
	renderChartsSumHeight += renderHeight;
	return num;
}
/*******************************************************************************
 * @author neu.zhangzhq
 * 
 * 计算指标高度
 */
function calculateChartHeight(chart) {
	if (chart.chartStyle == "udLine")
		return 95;
	return 185;
}
/*******************************************************************************
 * @author neu.zhangzhq
 * 
 * 绘图函数
 * @param chartData
 *            当前未绘制的指标数据的数组
 * @param num
 *            需要绘制的指标图数量
 */
function renderPartCharts(chartData, num) {
	fillMetricCharts(chartData.slice(0, num));
	chartData = chartData.slice(num, chartData.length);
	return chartData;
}

/**
 * 根据封装的监控数据进行echarts图表的数据填充工作
 */
function fillMetricCharts(metricData) {
	for ( var one in metricData) {
		// debugger;
		var name = metricData[one].name;
		var chartStyle = metricData[one].chartStyle;
		var localeName = metricData[one].localeName;
		var metricParams = metricData[one].metricI18N;

		var value = metricData[one].value;
		var unit = value.unit;

		fillMetricChart(name, localeName, unit, chartStyle);

		var chartID = 'monitor_' + name;

		var dataSets = value.dataSets;
		var metricCount = dataSets.length;

		var echartType = 'line';
		var echartSymbol = 'circle';

		// 基于准备好的dom，初始化echarts图表
		var myChart = echarts.init(document.getElementById(chartID));
		var option = null;
		if (chartStyle == 'msLine') {
			option = echartsUtil.msLine();
		} else if (chartStyle == 'udLine') {
			option = echartsUtil.udLine(metricParams);
		} else if (chartStyle == 'scatter') {
			option = echartsUtil.msLine();
		} else {
			option = echartsUtil.msLine();
		}
		// option设置监控点显示名称、x轴格式、所有点的监控值
		option.setMetricName(localeName);
		option.setXaixsFormat(timeFormat);
		option.setMetricValue(value);

		// 多条线，生成图例，调整grid间距
		if (metricCount > 1) {
			// option = option.fillLegend(option);
			option.fillLegend();
			option.fillGrid("", "", "", 60);
		}

		// 获得缩放比例，小于100的话生成echarts的滑块实时拖拽
		var scale = getDataZoomScale(value);
		if (scale < 100) {
			option.fillDataZoom(true, scale, true);
		}
		// 获得图中最大监控值的长度，根据不同的边距，则调整对应的边距
		var maxValueLength = getMaxValueLength(value);
		if (maxValueLength >= 9 && maxValueLength <= 12) {
			option.fillGrid(100, "", "", "");
		} else if (maxValueLength > 12) {
			option.fillGrid(100, "", "", "");
		}
		// 填充x轴
		if (chartStyle != 'scatter') {
			option.fillXaxis();
		}
		// 填充图表数据内容、图表类型、图表中点的样式
		option.fillSeries(echartType, echartSymbol);
		// echarts渲染成图到对应的dom中

		// 为echarts对象加载数据
		myChart.setOption(option);

	}
}

/**
 * 构造图标的DOM元素
 * 
 * metricName 指标名称 localeMetricName 指标国际化 unit 指标单位 chartType 指标图类型
 * udLine（点图），msLine（线图）
 * 
 * <div class="monitor-echart"> <div class="monitor-echart-title">开关分合阀</div>
 * <div class="monitor-echart-body"><img
 * src="${pageContext.request.contextPath}/styles/images/echart.png"/></div>
 * </div>
 */
function fillMetricChart(metricName, localeMetricName, unit, chartType) {
	if (document.getElementById('monitor_' + metricName) != null) {
		return;
	}
	var chartContainer = document.createElement("div");
	var cell = [ '<div class="monitor-echart-title">' ];
	cell.push(localeMetricName);
	if (!(unit == null || unit == "null" || unit == '' || unit == undefined)) {
		cell.push("（" + unit + "）");
	}
	cell.push('</div><div class="monitor-echart-body" id="');
	cell.push('monitor_' + metricName);
	if (chartType == 'udLine') {
		cell.push('" style="width:100%;height: 80px');
	} else {
		cell.push('" style="width:100%;height:180px');
	}
	cell.push('"></div>');
	$(chartContainer).addClass('monitor-echart').css("width", "100%").html(
			cell.join(""));
	var monitor = $('#monitorData');
	monitor.append(chartContainer);
}

/**
 * 横坐标时间显示格式
 * 
 * @param time
 * @returns
 */
function timeFormat(time) {
	var tempDate = new Date();
	tempDate.setTime(parseInt(time));
	return tempDate.Format("MM-dd HH:mm");
}
/**
 * 获得缩放比例，如果等于100则全部显示原始点，没有拖动条
 * 
 * @param value
 * @returns {Number}
 */
function getDataZoomScale(value) {
	var dataSets = value.dataSets;
	var metricCount = dataSets.length;
	var maxPointCount = 0;
	// 默认推荐显示的最大原始点数，不会造成挤压、重叠等问题
	var defaultPoint = 120;
	// 默认缩放比例为100，即显示所有原始点，不出现缩放滑块
	var scale = 100;
	for ( var i = 0; i < metricCount; i++) {
		var dataPoints = dataSets[i].points;
		var pointCount = dataPoints.length;
		if (pointCount > maxPointCount) {
			maxPointCount = pointCount;
		}
	}
	if (maxPointCount <= defaultPoint) {
		return 100;
	} else {
		// 缩放比例为默认点除以最大点数得出的百分比乘以100，数值区间在（0-100）
		scale = (defaultPoint / maxPointCount * 100).toFixed(1);
		return scale;
	}
}

/**
 * 获得图中最大监控值的长度
 */
function getMaxValueLength(metricValue) {
	var dataSets = metricValue.dataSets;
	var metricCount = dataSets.length;
	var maxValueLength = 0;
	for ( var i = 0; i < metricCount; i++) {
		var dataPoints = dataSets[i].points;
		var pointCount = dataPoints.length;
		for ( var j = 0; j < pointCount; j++) {
			var value = dataPoints[j].value;
			var length = String(parseInt(value)).length;
			if (value != undefined) {
				if (length > maxValueLength) {
					maxValueLength = length;
				}
			}
		}
	}
	return maxValueLength;
}
/**
 * 自定义时间范围的起始终止时间逻辑校验
 * 
 * @param startdate
 * @param enddate
 * @returns {Boolean}
 */
function checkTime(startdate, enddate) {
	var errorInfo = null;
	var currentTime = (new Date()).Format("yyyy-MM-dd");
	var maxDate = currentTime + " 23:59:59";
	if (!startdate) {
		errorInfo = Properties_I18N.validate_startdate_fail;
	}
	if (!errorInfo && !enddate) {
		errorInfo = Properties_I18N.validate_enddate_fail;
	}
	if (!errorInfo && (startdate > enddate)) {
		errorInfo = Properties_I18N.validate_compare_fail;
	}
	if (!errorInfo && (enddate > maxDate)) {
		errorInfo = Properties_I18N.validate_enddate_exceed_fail;
	}

	// 自定义时间范围不能大于maxMonitorDay天
	var time = maxMonitorDay * 24 * 60 * 60 * 1000;
	var startTime = toMillisecond(startdate);
	var endTime = toMillisecond(enddate);
	var diff = endTime - startTime;
	if (!errorInfo && (diff > time)) {
		errorInfo = Properties_I18N.validate_timerange_fail + maxMonitorDay
				+ Properties_I18N.validate_timerange_day;
	}
	if (errorInfo) {
		$.messager.alert(Properties_I18N.title_prompt, errorInfo, 'error');
		return false;
	} else {
		fromTime = startTime;
		toTime = endTime;
		return true;
	}
}