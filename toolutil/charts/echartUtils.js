var echartUtils = {};

//echart图表的全局变量，供图表自适应和销毁使用
echartUtils.chart = new Array();

//echart图表的全局变量，供图表追加数据使用
echartUtils.chartMap = new Map();

/* 字体大小默认值 */
var defaultTextSize = 12;// 默认字体大小
var defaultTitleTextSize = 14;// 标题字体大小
var defaultLegendTextSize = 12;// 图例字体大小
var defaultXTextSize = 12;// 横坐标字体大小
var defaultYTextSize = 12;// 纵坐标字体大小
var defaultLSTextSize = 10;// 大屏默认字体大小
var lsXTextSize = 10;// 大屏横坐标字体大小
var lsYTextSize = 7;// 大屏纵坐标字体大小
var defaultBarInsideTextSize = 12;// 柱状图内部数据字体大小
var defaultExceptionTextSize = 16;// 柱状图内部数据字体大小
var defaultBarTextSize = 9;

/* 默认坐标轴标签距离 */
var defaultMargin = 5;

/* 默认纵坐标行数 */
var defaultSplitNumber = 6;

/* 图例默认字体样式 */
var defaultLegendTextStyle = {
	fontSize : defaultLegendTextSize,
	fontWeight : 'bold',
	fontFamily : 'Arial'
};
/* 提示框默认字体样式 */
var defaultTooltipTextStyle = {
		fontSize : defaultLegendTextSize,
};
/* 图例默认字体样式 */
var lsLegendTextStyle = {
		fontSize : 9,
		fontWeight : 'bold',
		fontFamily : 'Arial'
};

/* 饼状图默认提示框 */
var defaultPieTooltip = {
	show : true,
	formatter : "{b} : {c} ({d}%)"
};
/* 饼状图默认开始角度 */
var defaultStartAngle = 90;

/* 饼状图默认配置 */
var startAngleDefault = 90;
var doublePieCenterDefault = [ '50%', '50%' ];
var doublePieCenterDefault_0 = [ '26%', '55%' ];
var doublePieCenterDefault_1 = [ '74%', '55%' ];
var doublePieMinAngleDefault = 10;
var doublePieRadiusDefault = [ 0, '65%' ];

/* 横坐标默认样式 */
var defaultXaxisLabel = {
	show : true,
	interval : 'auto',
	margin : defaultMargin,
	textStyle : {
		fontFamily : 'Arial',
		fontSize : defaultXTextSize,
		fontWeight : 'bold'
	}
};

/* 纵坐标默认样式 */
var defaultYaxisLabel = {
	show : true,
	interval : 'auto',
	margin : defaultMargin,
	textStyle : {
		fontSize : defaultYTextSize,
		fontStyle : 'normal'
	}
};

/* 大屏X轴 */
var lsXaxisLabel = {
		show : true,
		interval : 'auto',
		margin : defaultMargin,
		textStyle : {
			fontFamily : 'Arial',
			fontSize : lsXTextSize,
			fontWeight : 'bold'
		}
	};
/* 大屏资产量X轴 */
var assetsXaxisLabel = {
		show : true,
		interval : 'auto',
		margin : defaultMargin,
		rotate : 0,
		formatter : function(param) {
			var mm = param.substr(5);
			switch (mm) {
			case '01':
				return '一月'
			case '02':
				return '二月'
			case '03':
				return '三月'
			case '04':
				return '四月'
			case '05':
				return '五月'
			case '06':
				return '六月'
			case '07':
				return '七月'
			case '08':
				return '八月'
			case '09':
				return '九月'
			case '10':
				return '十月'
			case '11':
				return '十一月'
			case '12':
				return '十二月'
			default:
				return param
			}
		},
		textStyle : {
			fontFamily : 'Arial',
			fontSize : lsXTextSize,
			fontWeight : 'bold'
		}
	};
/* 大屏Y轴 */
	var lsYaxisLabel = {
		show : true,
		interval : 'auto',
		margin : defaultMargin,
		textStyle : {
			fontSize : lsYTextSize,
			fontStyle : 'normal'
		}
	};

/* 表格没有数据的展示形式 */
var defaultNoDataLoadingOption = {
	text : "暂无数据",
	effect : 'bubble',
	effectOption : {
		effect : {
			n : 0
		}
	},
	textStyle : {
		fontSize : defaultExceptionTextSize,
		fontStyle : 'oblique',
		fontWeight : 'lighter'
	}
};

/* 柱状图默认数据样式 */
var defaultBarItemStyle = {
	normal : {
		barBorderWidth : 1,
		barBorderRadius : 3,
		label : {
			show : true,
			position : 'inside',
			textStyle : {
				fontSize : defaultBarTextSize,
				fontWeight : 'lighter'
			}
		}
	},
	emphasis : {
		// color: '各异',
		barBorderColor : 'rgba(0,0,0,0)', // 柱条边线
		barBorderRadius : 0, // 柱条边线圆角，单位px，默认为0
		barBorderWidth : 1, // 柱条边线线宽，单位px，默认为1
		label : {
			show : false
		}
	}
};
/* 数据加载过度效果 */
echartUtils.showLoading = function() {
	return {
		text : '数据读取中······',
		effect : 'spin',
		textStyle : {
			fontSize : defaultTextSize
		}
	};
};

/* 默认图表标题样式 */
var defaultTitle = {
	show : true,
	text : '',
	subtext : '',
	textStyle : {
		fontSize : defaultTitleTextSize,
		fontWeight : 'bold',
		fontFamily : 'Arial',
		color : '#757575'
	},
	x : 'center',
	y : 'top'
};

/* 表格边距默认值 */
var defaultGrid = {
	borderWidth : 0,
	x : 60,
	x2 : 45,
	y : 30,
	y2 : 30
};

/**
 * 异常数据图表
 * 
 * @returns
 */
echartUtils.exceptionInfo = function(_) {
	var option = {
		noDataLoadingOption : defaultNoDataLoadingOption,
		series : [ {} ]
	}
	option.noDataLoadingOption.text = _;
	return option;
}

/**
 * 构建初始公共GridOption,具体各种图有不一样的属性，可重载对应的属性。
 * 
 * @returns
 */
echartUtils.generateCommonOption = function() {
	var option = {
		title : defaultTitle,
		tooltip : {
			trigger : 'axis',
			axisPointer : { // 坐标轴指示器，坐标轴触发有效
				type : 'line' // 默认为直线，可选为：'line' | 'shadow'
			}
		},
		dataZoom : {
			show : false,
			realtime : true,
			y : 40,
			height : 15,
			fillerColor : '#CECECE',
			dataBackgroundColor : '#C0D5E5',
			start : 0
		},
		legend : {
			textStyle : defaultLegendTextStyle,
			data : []
		},
		xAxis : [ {
			type : 'category',
			axisLabel : defaultXaxisLabel,
			data : []
		} ],
		yAxis : [ {
			type : 'value',
			scale : false,
			axisLabel : defaultYaxisLabel,
			axisLine : {
				onZero : true,
				lineStyle : {}
			}
		} ],
		noDataLoadingOption : defaultNoDataLoadingOption,
		grid : defaultGrid,
		series : []
	};

	option.setTitleText = function(_) {
		this.title.text = _;
	};
	option.setTitleSubtext = function(_) {
		this.title.subtext = _;
	};
	option.setTitleTextStyle = function(_) {
		this.title.textStyle = _;
	};
	option.setTitleX = function(_) {
		this.title.x = _;
	};
	option.setTitleY = function(_) {
		this.title.y = _;
	};
	option.setLegendSize = function(_) {
		this.legend.textStyle.fontSize = _;
	};

	option.boundaryGap = false;// 横坐标起始策略，false为顶头
	option.setBoundaryGap = function(_) {
		this.boundaryGap = _;
	};
	option.defaultColor = '#4AA5FF';
	option.setDefaultColor = function(_) {
		this.defaultColor = _;
	};
	option.setXaixsFormat = function(_) {
		option.xAixsFormat = _;
	};
	option.setMetricName = function(_) {
		option.metricName = _;
	};
	option.setMetricValue = function(_) {
		option.metricValue = _;
	};
	option.setMetricValueBits = function(_) {
		option.metricValueBits = _;
	};
	option.setMetricValueType = function(_) {
		option.valueType = _;
	};
	option.setOnZeroY = function(_) {
		this.yAxis[0].axisLine.onZero = _;
	};
	option.setYScale = function(_) {
		this.yAxis[0].scale = _;
	};
	option.setXYAsLargescreen = function() {
		this.xAxis[0].axisLabel = lsXaxisLabel;
		this.yAxis[0].axisLabel = lsYaxisLabel;
	};
	option.setYAsLargescreen = function() {
		this.yAxis[0].axisLabel = lsYaxisLabel;
	};
	// 设置图表四个方向的空隙间距
	option.fillGrid = function(x, x2, y, y2) {
		if (x != '') {
			this.grid.x = x;
		}
		if (x2 != '') {
			this.grid.x2 = x2;
		}
		if (y != '') {
			this.grid.y = y;
		}
		if (y2 != '') {
			this.grid.y2 = y2;
		}
	};
	//设置缩放滚动滑块的显示以及缩放比例、是否锁定等
	option.fillDataZoom = function(show,end,zoomLock){
		this.dataZoom.show = show;
	    this.dataZoom.end = end;
	    this.dataZoom.zoomLock = zoomLock;
	};
	return option;
};
/**
 * 历史数据线形图
 */
echartUtils.msLine = function() {
	var option = this.generateCommonOption();
	
	// 设置数据、图形类型、数据点的样式等
	option.fillSeries = function(chartType, chartSymbol) {
		var dataSets = this.metricValue.dataSets;
		var metricCount = dataSets.length;
		// 生成series数组
		for (var i = 0; i < metricCount; i++) {
			var dataPoints = dataSets[i].points;
			var pointCount = dataPoints.length;

			var seriesObject = new Object();
			if (metricCount > 1) {
				seriesObject.name = dataSets[i].name;
			} else {
				seriesObject.name = this.metricName;
			}
			seriesObject.symbol = chartSymbol;
			seriesObject.type = chartType;
			// 如果只有一个图例，使用默认颜色进行绘图
			if (i == 0) {
				seriesObject.itemStyle = {
					normal : {
						color : this.defaultColor,
						label : {
							show : this.showLabel
						}
					}
				};
			}
			seriesObject.showAllSymbol = true;
			seriesObject.data = new Array();
			for (var j = 0; j < pointCount; j++) {
				seriesObject.data[j] = new Object();
				var value = dataPoints[j].value;
				if (value != undefined) {
					if (this.valueType == '3') {
						seriesObject.data[j].value = toFixedByBits(value,
								this.metricValueBits);
					} else if (this.valueType == '1') {
						seriesObject.data[j].value = toFixedByBits(value, '0');
					} else {
						seriesObject.data[j].value = value;
					}
				} else {
					seriesObject.data[j].value = "-";
				}
			}
			this.series.push(seriesObject);
		}
	};
	option.fillXaxis = function() {
		var dataSets = this.metricValue.dataSets;
		var metricCount = dataSets.length;
		//生成xAxis数组
		if (metricCount > 0) {
			var xAxisData = new Array();
			for ( var one in dataSets[0].points) {
				var point = dataSets[0].points[one];
				xAxisData[one] = this.xAixsFormat(point.time);
			}
			this.xAxis[0].data = xAxisData;
			this.xAxis[0].boundaryGap = this.boundaryGap;
		}
	};
	return option;
};

echartUtils.udLine = function(metricParam) {
	var formatArray = new Array();
	var option = this.generateCommonOption();
	
	option.tooltip = {
			trigger : 'axis',
			formatter : function(params) {
				var flag = false;
				var res = params[0][1];
				for (var i = 0; i < formatArray.length; i++) {
					if (formatArray[i].time == res) {
						flag = true;
						var valueStatus = phraseStatus(formatArray[i].value,
								metricParam);
						if (valueStatus == undefined) {
							valueStatus = formatArray[i].value;
						}
						res += '<br/>' + params[0][0] + ' : ' + valueStatus;
						break;
					}
				}
				//未正常采集的显示
				if (!flag) {
					res += '<br/>' + params[0][0] + ' : ' + params[0][2];
				}
				return res;
			}
		};
		option.xAxis = [ {
			type : 'category',
			boundaryGap : false,
			splitLine : {
				show : false
			},
			axisLine : {
				onZero : false,
				show : false
			},
			data : {

			}
		} ];
		option.yAxis = [ {
			splitNumber : 1,
			axisLine : {
				show : false
			},
			axisLabel : {
				show : false
			},
			splitLine : {
				show : false
			},
			type : 'value',
		} ];
		option.fillLegend = function(_) {
			this.legend.data.push(_);
		};
		//	option.fillLegend = function() {
		//		var dataSets = this.metricValue.dataSets;
		//		var metricCount = dataSets.length;
		//		for (var i = 0; i < metricCount; i++) {
		//			this.legend.data.push(dataSets[i].name);
		//		}
		//
		//	};

		option.fillXaxis = function() {
			var dataSets = this.metricValue.dataSets;
			var metricCount = dataSets.length;
			//生成xAxis数组
			if (metricCount > 0) {
				var xAxisData = new Array();
				for ( var one in dataSets[0].points) {
					var point = dataSets[0].points[one];
					xAxisData[one] = this.xAixsFormat(point.time);
				}
				this.xAxis[0].data = xAxisData;
			}
		};

		option.fillSeries = function(chartType, chartSymbol) {
			var dataSets = this.metricValue.dataSets;
			var metricCount = dataSets.length;
			//生成series数组
			for (var i = 0; i < metricCount; i++) {
				var seriesObject = new Object();
				if (metricCount > 1) {
					seriesObject.name = dataSets[i].name;
				} else {
					seriesObject.name = this.metricName;
				}

				seriesObject.symbol = chartSymbol;
				seriesObject.symbolSize = 5;
				seriesObject.type = chartType;
				seriesObject.itemStyle = {
					normal : {
						lineStyle : {
							width : 3
						},
						color : '#767575'
					}
				};
				seriesObject.showAllSymbol = true;

				var dataPoints = dataSets[i].points;
				var pointCount = dataPoints.length;
				seriesObject.data = new Array();
				for (var j = 0; j < pointCount; j++) {
					seriesObject.data[j] = new Object();
					var value = dataPoints[j].value;
					var time = this.xAixsFormat(dataPoints[j].time);

					if (value != undefined) {
						var metricInfo = new Object();
						metricInfo.time = time;
						metricInfo.value = parseFloat(value);
						var metricColor = phraseColor(value, metricParam);

						formatArray.push(metricInfo);
						seriesObject.data[j].value = 1;
						seriesObject.data[j].itemStyle = {
							normal : {
								color : metricColor,
								borderColor : '#767575',
								borderWidth : 0.8
							}
						};
					} else {
						seriesObject.data[j].value = "-";
						seriesObject.data[j].itemStyle = {
							normal : {
								color : '#CCCCCC',
								borderColor : '#767575',
								borderWidth : 0.8
							}
						};
					}
				}
				this.series.push(seriesObject);
			}

		};
	
	return option;
};

/**
 * 更换指定图表的显示样式
 * @param chartId
 * @param echartType
 * @param echartSymbol
 */
echartUtils.changeView = function(chartId,echartType,echartSymbol){
	var chart = echartUtils.chartMap.get(chartId);
	var option = null;
	if(chart && chart.getOption()){
		option = chart.getOption();
		for ( var i = 0; i < option.series.length; i++) {
			option.series[i].type = echartType;
			option.series[i].symbol = echartSymbol;
		}
		chart.setOption(option); 
		chart.refresh();
	}
};

/**
 * 动态添加数据
 * @param chartId
 * @param dataArray
 */
echartUtils.addData = function(chartId,dataArray){
	if(echartUtils.chartMap && chartId){
		if(echartUtils.chartMap.get(chartId)!=undefined){
			echartUtils.chartMap.get(chartId).addData(dataArray);
		}
	}
};

/**
 * 窗口改变大小后 echart图表自适应
 */
echartUtils.resize = function(){
	for ( var i = 0; i < echartUtils.chart.length; i++) {
		echartUtils.chart[i].resize();
	}
};

/**
 * 释放并销毁所有的chart对象
 */
echartUtils.release = function(){
	for ( var i = 0; i < echartUtils.chart.length; i++) {
		echartUtils.chart[i].dispose();
	}
};

/**
 * echart针对float类型的指标进行option对象构建,线图和饼图通用
 * @returns
 */
echartUtils.alarmDayLine = function(){
	var option = this.generateCommonOption();
	
	option.setAlarmPoints = function(_){
		this.alarmPoints = _;
	};
	option.tooltip = {
		trigger: 'axis',
        transitionDuration:0,
        textStyle : {
            color: 'yellow',
            fontFamily: 'Verdana, sans-serif',
            fontSize: 15,
            fontStyle: 'italic',
            fontWeight: 'bold'
        },
        formatter: function (params,ticket,callback) {
            var res = params[0].name;
            for (var i = 0, l = params.length; i < l; i++) {
                res += '<br/>' +  params[i].data.name+" : "+params[i].data.value;
            }
            return res;
        }
	},
	
	//设置x轴显示数据
	option.fillXaxis = function(){
		//生成xAxis数组
		var xAxisData = new Array();
		var value = this.alarmPoints;
		for(var one in value){
			var date = value[one].date;
			xAxisData[one] = date;
		}
		this.xAxis[0].data = xAxisData;
		this.xAxis[0].boundaryGap = this.boundaryGap;
	};
	option.fillYaxis = function(option){
		return option;
	};
	//设置数据、图形类型、数据点的样式等
	option.fillSeries = function(chartType,chartSymbol){
		var seriesObject = new Object();
		seriesObject.symbol = chartSymbol;
		seriesObject.type = chartType;
		seriesObject.itemStyle = {
				normal : {
					color : this.defaultColor,
					label : {
						show :this.showLabel
					}
				}
			};
		seriesObject.showAllSymbol = true;
		seriesObject.data = new Array();
		var value = this.alarmPoints;
		for (var j = 0; j < value.length; j++) {
			seriesObject.data[j] = new Object();
			var count = value[j].count;
			var level = value[j].level;
			if(level=='all'){
				seriesObject.data[j].name = '告警总数';
			}else{
				seriesObject.data[j].name = level+'级告警';
			}
			if(value != undefined){
				seriesObject.data[j].value = count;
			}else{
				seriesObject.data[j].value = "-";
			}
		}
		this.series.push(seriesObject);
	};
	return option;
};

/**
 * 告警级别统计饼状图 
 */
echartUtils.alarmDayPie = function(){
	var option = {
			 title : {
				 show : true,
				 text: '',
				 textStyle: {
					 fontWeight: 'normal',
			         color: '#008acd',
					 fontFamily : 'sans-serif',
					 fontSize : 18
				 },
				 x:'center'
			},
			noDataLoadingOption:{
				text :"暂无数据",
				effect:'bubble',
				effectOption : {
					effect: {
						n: 0
					}
				},
				textStyle: {
					fontSize: 20,
					fontStyle:'oblique',
					fontWeight: 'lighter'
				}
			},
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    calculable : true,
		    legend: {
		        data:[]
		    },
//			toolbox: {
//				show : true,
//				feature : {
//					saveAsImage : {show: true}
//				}
//			},
		    series : []
		};
	option.setAlarmPoints = function(_){
		this.alarmPoints = _;
	};
	option.fillLegend = function(){
		this.legend.x='left';
		this.legend.orient='vertical';
		var length = this.alarmPoints.length;
		for (var i = 0; i < length; i++) {
			if(this.alarmPoints[i].level=='all'){
				this.legend.data.push('告警总数');
			}else{
				this.legend.data.push(this.alarmPoints[i].level+'级告警');
			}
		}
		
	};
	//设置数据、图形类型、数据点的样式等
	option.fillSeries = function(chartType){
		var seriesObject = new Object();
		
		seriesObject.type = chartType;
		seriesObject.radius =  '55%';
		seriesObject.showAllSymbol = true;
		
		seriesObject.data = new Array();
		var value = this.alarmPoints;
		for (var j = 0; j < value.length; j++) {
			seriesObject.data[j] = new Object();
			var count = value[j].count;
			var level = value[j].level;
			if(level=='all'){
				seriesObject.data[j].name = '告警总数';
			}else{
				seriesObject.data[j].name = level+'级告警';
			}
			if(value != undefined){
				seriesObject.data[j].value = count;
			}else{
				seriesObject.data[j].value = "-";
			}
		}
		this.series.push(seriesObject);
	};
	option.setTitle = function(_){
		this.title.text = _;
	};
	return option;
};
/**
 * 设备能耗分布图 
 */
echartUtils.assetEnergyDistribution = function(){
	var option = {
			 title : {
				 show : true,
				 text: '',
				 textStyle: {
					 fontWeight: 'normal',
			         color: '#008acd',
					 fontFamily : 'sans-serif',
					 fontSize : 18
				 },
				 x:'center'
			},
			tooltip : {
				trigger: 'item',
				formatter: "{b} : {c} ({d}%)"
//					formatter: "{a} <br/>{b} : {c} ({d}%)"
			},
			calculable : true,
			legend: {
				data:[]
			},
			noDataLoadingOption:{
				text :"暂无数据",
				effect:'bubble',
				effectOption : {
					effect: {
						n: 0
					}
				},
				textStyle: {
					fontSize: 20,
					fontStyle:'oblique',
					fontWeight: 'lighter'
				}
			},
//			toolbox: {
//				show : true,
//				feature : {
//					saveAsImage : {show: true}
//				}
//			},
			series : [{
				type:'pie',
				radius : [30, 110],
				roseType : 'area',
				x: '50%',               // for funnel
				max: 40,                // for funnel
				sort : 'ascending',     // for funnel
				data:[]
			}]
	};
	
	option.setTitle = function(_){
		this.title.text = _;
	};
	option.setAlarmPoints = function(_){
		this.alarmPoints = _;
	};
	option.fillLegend = function(){
		this.legend.x='left';
		this.legend.y='bottom';
//		this.legend.orient='vertical';
		var length = this.alarmPoints.length;
		for (var i = 0; i < length; i++) {
			this.legend.data.push(this.alarmPoints[i].typeName);
		}
		
	};
	//设置数据、图形类型、数据点的样式等
	option.fillSeries = function(chartType,roseType){
		var seriesObject = new Object();
		
		seriesObject.type = chartType;
		seriesObject.roseType = roseType;
		
		seriesObject.data = new Array();
		var value = this.alarmPoints;
		for (var j = 0; j < value.length; j++) {
			seriesObject.data[j] = new Object();
			var name = value[j].typeName;
			var v = value[j].energy;
			seriesObject.data[j].name =name;
			if(value != undefined){
				seriesObject.data[j].value = v;
			}else{
				seriesObject.data[j].value = "-";
			}
		}
		this.series.push(seriesObject);
	};
	
	return option;
};

/**
 * 实时能耗统计 折线图
 * @returns
 */
echartUtils.energyActualLine = function(){
	var option = this.generateCommonOption();
	
	option.setAlarmPoints = function(_){
		this.alarmPoints = _;
	};
	option.tooltip = {
		trigger: 'axis',
        transitionDuration:0,
        textStyle : {
            color: 'yellow',
            fontFamily: 'Verdana, sans-serif',
            fontSize: 15,
            fontStyle: 'italic',
            fontWeight: 'bold'
        },
        formatter: function (params,ticket,callback) {
        	var xName = params[0].name;
            return  xName + '<br/>'
//            return  xName.substr(5,11) + '<br/>'
            + params[0].seriesName + ' : ' + params[0].value + ' <br/>';
        }
	},
	
	//设置x轴显示数据
	option.fillXaxis = function(){
		//生成xAxis数组
		var xAxisData = new Array();
		var value = this.alarmPoints;
		for(var one in value){
			var time = value[one].stime;
			xAxisData[one] = this.xAixsFormat(time);
//			xAxisData[one] = time;
		}
		this.xAxis[0].data = xAxisData;
		this.xAxis[0].boundaryGap = this.boundaryGap;
	};
	option.fillYaxis = function(option){
		return option;
	};
	//设置数据、图形类型、数据点的样式等
	option.fillSeries = function(chartType,chartSymbol,lineType){
		var seriesObject = new Object();
		seriesObject.symbol = chartSymbol;
		seriesObject.type = chartType;
		
		if(lineType=='IT'){
			seriesObject.name = 'IT能耗';
		}else if(lineType=='TOTAL'){
			seriesObject.name = '总能耗';
		}else if(lineType=='PUE'){
			seriesObject.name = 'PUE';
		}
		
		seriesObject.itemStyle = {
				normal : {
					color : this.defaultColor,
					label : {
						show :this.showLabel
					}
				}
			};
		seriesObject.showAllSymbol = true;
		seriesObject.data = new Array();
		var value = this.alarmPoints;
		for (var j = 0; j < value.length; j++) {
			seriesObject.data[j] = new Object();
			
			var count;
			
			var itEnergy = value[j].itEnergy;
			var totalEnergy = value[j].totalEnergy;
			var pue = value[j].pueNum;
//			var pue = totalEnergy/itEnergy;
			
			if(lineType=='IT'){
				count = (itEnergy==null||itEnergy=='null'||itEnergy==-1?'-':parseFloat(itEnergy).toFixed(2));
			}else if(lineType=='TOTAL'){
				count = (totalEnergy==null||totalEnergy=='null'||totalEnergy==-1?'-':parseFloat(totalEnergy).toFixed(2));
			}else if(lineType=='PUE'){
				count = (pue==Infinity||pue=='null'||pue==NaN||pue==-1?'-':parseFloat(pue).toFixed(2));
			}
			
			if(value != undefined){
				seriesObject.data[j].value = count;
			}else{
				seriesObject.data[j].value = "-";
			}
		}
		this.series.push(seriesObject);
	};
	return option;
};

/**
 * 实时能耗统计 折线图
 * @returns
 */
echartUtils.energyActualLineMixed = function(){
	var option = this.generateCommonOption();
	
	option.setAlarmPoints = function(_){
		this.alarmPoints = _;
	};
	option.tooltip = {
		trigger: 'axis',
        transitionDuration:0,
        textStyle : {
            color: 'yellow',
            fontFamily: 'Verdana, sans-serif',
            fontSize: 15,
            fontStyle: 'italic',
            fontWeight: 'bold'
        },
        formatter: function (params,ticket,callback) {
        	var xName = params[0].name;
            return  xName.substr(5,11) + '<br/>'
            + params[0].seriesName + ' : ' + params[0].value + ' (Kwh)<br/>'
            + params[1].seriesName + ' : ' + params[1].value + ' (Kwh)<br/>'
            + params[2].seriesName + ' : ' + -params[2].value;
        }
	},
	
	//设置x轴显示数据
	option.fillXaxis = function(){
		//生成xAxis数组
		var xAxisData = new Array();
		var value = this.alarmPoints;
		for(var one in value){
			var time = value[one].time;
			xAxisData[one] = time;
		}
		this.xAxis[0].data = xAxisData;
		this.xAxis[0].boundaryGap = this.boundaryGap;
	};
	option.fillYaxis = function(option){
		this.yAxis = [{
            name : '能耗(Kwh)',
            type : 'value'
        },
        {
            name : 'PUE',
            type : 'value',
            axisLabel : {
                formatter: function(v){
                    return v;
                }
            }
        }]
		return option;
	};
	//设置数据、图形类型、数据点的样式等
	option.fillSeries = function(chartType,chartSymbol){
		var seriesObjectItEnergy = new Object();
		var seriesObjectTotalEnergy = new Object();
		var seriesObjectPue = new Object();
		
		seriesObjectItEnergy.name = 'IT能耗';
		seriesObjectTotalEnergy.name = '总能耗';
		seriesObjectPue.name = 'PUE';

		seriesObjectItEnergy.symbol = chartSymbol;
		seriesObjectTotalEnergy.symbol = chartSymbol;
		seriesObjectPue.symbol = chartSymbol;
		
		seriesObjectItEnergy.type = chartType;
		seriesObjectTotalEnergy.type = chartType;
		seriesObjectPue.type = chartType;
//		seriesObjectPue.type = 'bar';
//		seriesObjectPue.barWidth=10;
		
		seriesObjectItEnergy.itemStyle = {
				normal : {
					color : this.defaultColor,
					label : {
						show :this.showLabel
					}
				}
			};
		seriesObjectTotalEnergy.itemStyle = {
				normal : {
					color : '#458B00',
					label : {
						show :this.showLabel
					}
				}
		};
		seriesObjectPue.itemStyle = {
				normal : {
					color : '#00EE00',
					label : {
						show :this.showLabel
					}
				}
		};
		seriesObjectItEnergy.showAllSymbol = true;
		seriesObjectTotalEnergy.showAllSymbol = true;
		seriesObjectPue.showAllSymbol = true;
		
		seriesObjectPue.yAxisIndex = 1;
		
		seriesObjectItEnergy.data = new Array();
		seriesObjectTotalEnergy.data = new Array();
		seriesObjectPue.data = new Array();
		
		var value = this.alarmPoints;
		for (var j = 0; j < value.length; j++) {
			seriesObjectItEnergy.data[j] = new Object();
			seriesObjectTotalEnergy.data[j] = new Object();
			seriesObjectPue.data[j] = new Object();
			var itEnergy = value[j].itEnergy;
			var totalEnergy = value[j].totalEnergy;
			var pue = totalEnergy/itEnergy;
			if(value != undefined){
				seriesObjectItEnergy.data[j].value = itEnergy;
				seriesObjectTotalEnergy.data[j].value = totalEnergy;
				seriesObjectPue.data[j].value = parseFloat(pue).toFixed(2);
			}else{
				seriesObjectItEnergy.data[j].value = "-";
				seriesObjectTotalEnergy.data[j].value = "-";
				seriesObjectPue.data[j].value = "-";
			}
		}
		this.series.push(seriesObjectItEnergy);
		this.series.push(seriesObjectTotalEnergy);
		this.series.push(seriesObjectPue);
	};
	return option;
};

/**
 * echart针对float类型的指标进行option对象构建,线图和饼图通用
 * @returns
 */
echartUtils.energyCycLine = function(){
	var option = this.generateCommonOption();
	
	option.setAlarmPoints = function(_){
		this.alarmPoints = _;
	};
	option.tooltip = {
		trigger: 'axis',
        transitionDuration:0,
        textStyle : {
            color: 'yellow',
            fontFamily: 'Verdana, sans-serif',
            fontSize: 15,
            fontStyle: 'italic',
            fontWeight: 'bold'
        },
        formatter: function (params,ticket,callback) {
            return  params[0].name + '<br/>'
            + params[0].seriesName + ' : ' + params[0].value + ' <br/>';
        }
	},
	
	//设置x轴显示数据
	option.fillXaxis = function(){
		//生成xAxis数组
		var xAxisData = new Array();
		var value = this.alarmPoints;
		for(var one in value){
			var date = value[one].stime;
			xAxisData[one] = date;
		}
		this.xAxis[0].data = xAxisData;
		this.xAxis[0].boundaryGap = this.boundaryGap;
	};
	option.fillYaxis = function(option){
		return option;
	};
	//设置数据、图形类型、数据点的样式等
	option.fillSeries = function(chartType,chartSymbol,lineType){
		var seriesObject = new Object();
		seriesObject.symbol = chartSymbol;
		seriesObject.type = chartType;
		
		if(lineType=='IT'){
			seriesObject.name = 'IT能耗';
		}else if(lineType=='TOTAL'){
			seriesObject.name = '总能耗';
		}else if(lineType=='PUE'){
			seriesObject.name = 'PUE';
		}
		
		seriesObject.itemStyle = {
				normal : {
					color : this.defaultColor,
					label : {
						show :this.showLabel
					}
				}
			};
		seriesObject.showAllSymbol = true;
		seriesObject.data = new Array();
		var value = this.alarmPoints;
		for (var j = 0; j < value.length; j++) {
			seriesObject.data[j] = new Object();
			
			var count;
			
			var itEnergy = value[j].itEnergy;
			var totalEnergy = value[j].totalEnergy;
			var pue = value[j].pueNum;
//			var pue = totalEnergy/itEnergy;
			
			if(lineType=='IT'){
				count = (itEnergy==null||itEnergy=='null'||itEnergy==-1?'-':parseFloat(itEnergy).toFixed(2));
			}else if(lineType=='TOTAL'){
				count = (totalEnergy==null||totalEnergy=='null'||totalEnergy==-1?'-':parseFloat(totalEnergy).toFixed(2));
			}else if(lineType=='PUE'){
				count = (pue==Infinity||pue=='null'||pue==NaN||pue==-1?'-':parseFloat(pue).toFixed(2));
			}
			
			if(value != undefined){
				seriesObject.data[j].value = count;
			}else{
				seriesObject.data[j].value = "-";
			}
		}
		this.series.push(seriesObject);
	};
	
	return option;
};
/**
 * D3.js
 * @returns
 */
echartUtils.alarmYearD3 = function(){
	var option = {
			title : {
		        text: '能耗'
		    },
		    series :
		        {
		    		style:"default",
		            data:[]
		        }
	};
	option.fillDatas = function(data){
		this.series.data = data;
	}
	return option;
}

/**
 * 创建大屏饼状图默认Option
 */
echartUtils.generateDefaultPieOption = function() {
	var option = {
		tooltip : defaultPieTooltip,
		series : [],
		legend : {
			orient : 'vertical',
			x : 'left',
			y : 'bottom',
			itemGap : 1,
			textStyle : defaultLegendTextStyle,
			data : []
		}
	};
	option.setPieDatas = function(_) {
		this.pieDatas = _;
	};
	option.setLegendPosition = function(x, y) {
		this.legend.x = x;
		this.legend.y = y;
	};
	option.setLegendOrient = function(_) {
		this.legend.orient = _;
	};
	option.piePosition = doublePieCenterDefault;
	option.setPiePosition = function(_) {
		this.piePosition = _;
	};
	option.fillLegend = function(_) {
		var data = this.pieDatas;
		if (_ == undefined) {
			for ( var one in data) {
				this.legend.data.push(data[one].name);
			}
		} else {
			for ( var one in data) {
				this.legend.data.push(data[one].name + _);
			}
		}
	};
	option.fillSeries = function(_, cooon) {
		var seriesObjectArray = new Array();
		var seriesType = 'pie';
		var itemStyle = {
			normal : {
				label : {
					formatter : function(params) {
						//						if(cooon=='fault'){
						//							return params.name + '(' + params.percent + '%)'
						//						}
						//						return params.percent + '%'
						return params.percent + '%\n' + params.name;
					},
					textStyle : defaultLegendTextStyle
				},
				labelLine : {
					show : true,
					length : 2
				}
			}
		}

		var seriesObject = new Object();
		seriesObject.type = seriesType;
		seriesObject.center = this.piePosition;
		seriesObject.itemStyle = itemStyle;
		seriesObject.startAngle = startAngleDefault;
		seriesObject.minAngle = doublePieMinAngleDefault;
		seriesObject.radius = doublePieRadiusDefault;
		seriesObject.data = new Array();
		var value = this.pieDatas;
		for (var j = 0; j < value.length; j++) {
			seriesObject.data[j] = new Object();
			var count = value[j].count;
			if (_ == undefined) {
				seriesObject.data[j].name = value[j].name;
			} else {
				seriesObject.data[j].name = value[j].name + _;
			}
			if (value != undefined) {
				seriesObject.data[j].value = count;
			} else {
				seriesObject.data[j].value = "-";
			}
		}
		this.series.push(seriesObject);
	}
	return option;
}

/**
 * 创建告警级别柱状图
 * 
 * @returns
 */
echartUtils.alarmLevelBar = function() {
	var option = this.generateCommonOption();
	option.setOptionDatas = function(_) {
		this.optionDatas = _;
	};
	option.fillXaxis = function() {
		var xAxisData = new Array();
		var value = this.optionDatas;
		for ( var one in value) {
			var level = value[one].name + '告警';
			xAxisData[one] = level;
		}
		this.xAxis[0].data = xAxisData;
	};
	option.fillSeries = function() {
		var seriesObject = new Object();
		seriesObject.type = 'bar';
		seriesObject.name = '数量';
		seriesObject.barMinHeight = 0;
		seriesObject.barGap = '40%';
		seriesObject.barCategoryGap = '60%';
		seriesObject.itemStyle = defaultBarItemStyle;

		seriesObject.data = new Array();
		var valueAL = this.optionDatas;
		for (var j = 0; j < valueAL.length; j++) {
			seriesObject.data[j] = new Object();
			var count = valueAL[j].count;
			if (valueAL != undefined) {
				seriesObject.data[j].value = count;
			} else {
				seriesObject.data[j].value = "-";
			}
		}
		this.series.push(seriesObject);
	};
	return option;
};
/**
 * 创建故障类型柱状图
 * 
 * @returns
 */
echartUtils.faultBar = function() {
	var option = this.generateCommonOption();
	option.setOptionDatas = function(_) {
		this.optionDatas = _;
	};
	option.fillXaxis = function() {
		var xAxisData = new Array();
		var value = this.optionDatas;
		for ( var one in value) {
			var level = value[one].name;
			if (level == null) {
				level = '未知';
			}
			xAxisData[one] = level;
		}
		this.xAxis[0].data = xAxisData;
	};
	option.fillSeries = function() {
		var seriesObject = new Object();
		seriesObject.type = 'bar';
		seriesObject.name = '数量';
		seriesObject.barMinHeight = 0;
		seriesObject.barGap = '40%';
		seriesObject.barCategoryGap = '60%';
		seriesObject.itemStyle = defaultBarItemStyle;

		seriesObject.data = new Array();
		var valueFA = this.optionDatas;
		for (var j = 0; j < valueFA.length; j++) {
			seriesObject.data[j] = new Object();
			var count = valueFA[j].count;
			if (valueFA != undefined) {
				seriesObject.data[j].value = count;
			} else {
				seriesObject.data[j].value = "-";
			}
		}
		this.series.push(seriesObject);
	};
	return option;
};

/**
 * 创建容量矩形树图
 * 
 * @returns
 */
echartUtils.capacityTreemap = function() {
	var option = {
		tooltip : {
			trigger : 'item',
			formatter : "{b} : {c}%"
		},
		calculable : false,
		noDataLoadingOption : defaultNoDataLoadingOption,
		series : []
	};
	option.setCapacityDatas = function(_) {
		this.capacityDatas = _;
	};

	option.fillSeries = function() {
		var seriesType = 'treemap';
		var seriesCapacityObject = new Object();
		seriesCapacityObject.type = seriesType;
		seriesCapacityObject.name = '';
		seriesCapacityObject.itemStyle = {
			normal : {
				label : {
					show : true,
					formatter : "{b}",
					textStyle : {
						align : 'center',
						fontSize : defaultLSTextSize
					},
				},
				borderWidth : 1
			},
			emphasis : {
				label : {
					show : false
				}
			}
		};
		seriesCapacityObject.data = new Array();
		var valueCA = this.capacityDatas;
		for (var j = 0; j < valueCA.length; j++) {
			seriesCapacityObject.data[j] = new Object();
			seriesCapacityObject.data[j].name = valueCA[j].customer;
			var total = valueCA[j].total;
			var free = valueCA[j].free;
			if (valueCA != undefined) {
				seriesCapacityObject.data[j].value = Math.round(free / total
						* 10000) / 100.00;
			} else {
				seriesCapacityObject.data[j].value = "-";
			}
		}
		this.series.push(seriesCapacityObject);
	}
	return option;
};
/**
 * 创建资产量堆积柱状图
 * 
 * @returns
 */
echartUtils.assetsCountBar = function() {
	var option = this.generateCommonOption();
	option.xAxis = [ {
		type : 'category',
		axisLabel : assetsXaxisLabel,
		data : []
	}]; 
	option.setAssetsCountData = function(_) {
		this.assetsCountData = _;
	};

	option.setAssetsCountMonth = function(_) {
		this.assetsCountMonth = _;
	};

	option.setAssetsCountCustomer = function(_) {
		this.assetsCountCustomer = _;
	};

	option.fillLegend = function() {
		this.legend.textStyle = lsLegendTextStyle;
		this.legend.data = this.assetsCountCustomer;
	};

	option.fillXAxis = function() {
		this.xAxis[0].data = this.assetsCountMonth;
	};

	option.fillSeries = function() {
		var seriesType = 'bar';
		var cusomers = this.assetsCountCustomer;
		if(cusomers.length==0){
			this.series.push(new Object());
		}else{
			for ( var cus in cusomers) {
				var seriesAssetsObject = new Object();
				seriesAssetsObject.type = seriesType;
				seriesAssetsObject.itemStyle = defaultBarItemStyle;
				seriesAssetsObject.name = cusomers[cus];
				seriesAssetsObject.stack = 'ass';
				seriesAssetsObject.barGap = '20%';
				seriesAssetsObject.barCategoryGap = '40%';
				seriesAssetsObject.data = new Array();
				for (var j = 0; j < this.assetsCountMonth.length; j++) {
					seriesAssetsObject.data[j] = getCountForAssetsLS(cusomers[cus],
							this.assetsCountMonth[j], this.assetsCountData);
				}
				this.series.push(seriesAssetsObject);
			}
		}
	}
	return option;
};
/**
 * 创建PUE雷达图
 * 
 * @returns
 */
echartUtils.pueRadar = function() {
	var option = {
		title : {
			text : '',
			subtext:'',
			x : 'center',
			y : 'bottom',
			textStyle : {
				fontSize : defaultTitleTextSize,
				fontWeight : 'bold',
				fontFamily : 'Arial',
				color : '#757575'
			}
		},
		tooltip : {
			trigger : 'axis',
			textStyle : defaultTooltipTextStyle
		},
		legend : {
			x : 'right',
			y : 'bottom',
			textStyle : lsLegendTextStyle,
			data : []
		},
		polar : [],
		calculable : true,
		noDataLoadingOption : defaultNoDataLoadingOption,
		series : []
	};
	option.setPueDatas = function(_) {
		this.pueDatas = _;
	};
	option.setSubTitle = function(_) {
		this.title.subtext = _;
	};
	option.setTitle = function(_) {
		this.title.text = _;
	};
	option.setTitleXY = function(x,y) {
		this.title.x = x;
		this.title.y = y;
	};
	option.setEnergyRadarDatas = function(_) {
		this.energyRadarDatas = _;
	};
	option.setCusyomers = function(_) {
		this.customers = _;
	};
	option.setEnergyType = function(_) {
		this.energyTypes = _;
	};
	option.setEnergyMaxValue = function(_) {
		this.energyMaxValue = _;
	};

	option.fillPolar = function() {
		this.polar = new Array();
		var ty = this.energyTypes;
		var polarObject = new Object();
		
		var indicator = new Array();
		for ( var one in ty) {
			var idc = new Object();
			idc.text = ty[one].name;
			idc.max = parseFloat(this.energyMaxValue) * 1.2;
			indicator.push(idc);
		}
		polarObject.indicator = indicator;
		polarObject.radius = '60%';
		polarObject.startAngle = 75;
		polarObject.name = {
				textStyle : lsLegendTextStyle
		};
		this.polar.push(polarObject);
	};
	option.fillLegend = function() {
		this.legend.data = this.customers;
	};
	option.setTitleText = function(_) {
		this.title.text = _;
	};
	option.fillSeries = function() {
		var seriesType = 'radar';

		var cusomers = this.customers;
		if(cusomers.length==0){
			this.series.push(new Object());
		}else{
			for ( var cus in cusomers) {
				var seriesPUEObject = new Object();
				seriesPUEObject.type = seriesType;
				seriesPUEObject.data = new Array();
				seriesPUEObject.data[0] = new Object();
				seriesPUEObject.data[0].value = new Array();
				seriesPUEObject.data[0].name = cusomers[cus];
				for (var j = 0; j < this.energyTypes.length; j++) {
					seriesPUEObject.data[0].value[j] = getEnergyForPUELS(
							cusomers[cus], this.energyTypes[j],
							this.energyRadarDatas);
				}
				this.series.push(seriesPUEObject);
			}
		}
	};
	return option;
};

echartUtils.actualLine = function(startTime) {
	var option = this.generateCommonOption();
	option.startTime = startTime;
	option.xAxis = [ {
		type : 'category',
		axisLabel : defaultXaxisLabel,
		boundaryGap : true,
		data : []
	} ];

	//[{"cname":"电压L3报警","i18n":"","lineType":"msLine","name":"VOLTAGEL3WARNSTATE","no":"0","unit":""},{"cname":"输入总电流L2","i18n":"","lineType":"msLine","name":"TOTAL_INPUT_CURRENT_L2","no":"1","unit":"A"}]
	option.setInitData = function(_) {
		this.metricInfo = _;
	}
	option.setNoDataTitle = function(_) {
		this.noDataLoadingOption.text = _;
	}
	option.fillSeries = function(_) {
		if(_ =='del'){
			this.series.push(new Object());
		}else{
			var metrics = this.metricInfo;
			for ( var one in metrics) {
				var seriesObject = new Object();
				seriesObject.name = metrics[one].cname;
				seriesObject.type = "line";
				seriesObject.symbol = "emptyCircle";
				seriesObject.symbolSize = 4;
				
				seriesObject.data = new Array();
				seriesObject.data[0] = new Object();
				var no = metrics[one].no;
				this.series[no] = seriesObject;
			}
		}
	};
	option.fillLegend = function() {
		this.legend.x = 'right';
		this.legend.y = 29;
		var metrics = this.metricInfo;
		for ( var one in metrics) {
			this.legend.data.push(metrics[one].cname);
		}
	};
	option.fillXaxis = function() {
		var xAxisData = new Array();
		xAxisData[0] = this.startTime;
		this.xAxis[0].data = xAxisData;
	};
	option.setTitlePositionX = function(_) {
		this.title.x = _;
	};
	//	option.tooltip = {
	//			trigger: 'axis',
	//			formatter: function (params) {
	//				var flag = false;
	//				var res = params[0][1];
	//				for ( var i = 0; i < formatArray.length; i++) {
	//					if(formatArray[i].time == res){
	//						flag = true;
	//						var valueStatus = phraseStatus(formatArray[i].value,metricParam);
	//						if(valueStatus == undefined){
	//							valueStatus = formatArray[i].value;
	//						}
	//						res += '<br/>' + params[0][0] + ' : ' + valueStatus;
	//						break;
	//					}
	//				}
	//				//未正常采集的显示
	//				if (!flag) {
	//					res += '<br/>' + params[0][0] + ' : ' + params[0][2];
	//				}
	//				return res;
	//			}
	//		};

	return option;
}

/**
 * 创建大屏告警级别饼状图
 * 
 * @returns
 */
echartUtils.alarmTypePie = function() {
	var option = {
		tooltip : defaultPieTooltip,
		noDataLoadingOption : defaultNoDataLoadingOption,
		series : []
	};
	option.setAlarmsByLevel = function(_) {
		this.levelAlarms = _;
	};
	option.setAlarmsByType = function(_) {
		this.typeAlarms = _;
	};
	option.fillSeries = function() {
		var seriesObjectArray = new Array();
		var seriesType = 'pie';
		var itemStyle = {
			normal : {
				label : {
					formatter : function(params) {
						return params.percent + '%\n' + params.name + "告警";
					},
					textStyle : lsLegendTextStyle
				},
				labelLine : {
					show : true,
					length : 2
				}
			}
		}
		var seriesLevelObject = new Object();
		seriesLevelObject.type = seriesType;
		seriesLevelObject.center = doublePieCenterDefault_0;
		seriesLevelObject.itemStyle = itemStyle;
		seriesLevelObject.radius = doublePieRadiusDefault;
		seriesLevelObject.minAngle = doublePieMinAngleDefault;
		seriesLevelObject.data = new Array();
		var value = this.levelAlarms;
		for (var j = 0; j < value.length; j++) {
			if(value[j].count==0&&value[j].name=='其他'){
				continue;
			}
			seriesLevelObject.data[j] = new Object();
			var count = value[j].count;
			seriesLevelObject.data[j].name = value[j].name;
			if (value != undefined) {
				seriesLevelObject.data[j].value = count;
			} else {
				seriesLevelObject.data[j].value = "-";
			}
		}
		this.series.push(seriesLevelObject);

		var seriesTypeObject = new Object();
		seriesTypeObject.type = seriesType;
		seriesTypeObject.itemStyle = itemStyle;
		seriesTypeObject.center = doublePieCenterDefault_1;
		seriesTypeObject.radius = doublePieRadiusDefault;
		seriesTypeObject.minAngle = doublePieMinAngleDefault;
		seriesTypeObject.data = new Array();
		var valueType = this.typeAlarms;
		for (var j = 0; j < valueType.length; j++) {
			seriesTypeObject.data[j] = new Object();
			var count = valueType[j].count;
			seriesTypeObject.data[j].name = valueType[j].name;
			if (valueType != undefined) {
				seriesTypeObject.data[j].value = count;
			} else {
				seriesTypeObject.data[j].value = "-";
			}
		}
		this.series.push(seriesTypeObject);
	}
	return option;
};

/**
 * 创建大屏故障饼状图
 * 
 * @returns
 */
echartUtils.faultTypePie = function() {
	var option = {
		tooltip : defaultPieTooltip,
		noDataLoadingOption : defaultNoDataLoadingOption,
		series : []
	};
	option.setFaultsByCustomer = function(_) {
		this.faultCustomer = _;
	};
	option.setFaultsByType = function(_) {
		this.faultType = _;
	};
	option.fillSeries = function() {
		var seriesObjectArray = new Array();
		var seriesType = 'pie';
		var itemStyle = {
			normal : {
				label : {
					formatter : function(params) {
						return params.percent + '%\n' + params.name
					},
					textStyle : lsLegendTextStyle
				},
				labelLine : {
					show : true,
					length : 2
				}
			}
		}

		var seriesLevelObject = new Object();
		seriesLevelObject.type = seriesType;
		seriesLevelObject.center = doublePieCenterDefault_0;
		seriesLevelObject.itemStyle = itemStyle;
		seriesLevelObject.radius = doublePieRadiusDefault;
		seriesLevelObject.minAngle = doublePieMinAngleDefault;
		seriesLevelObject.data = new Array();
		var value = this.faultCustomer;
		for (var j = 0; j < value.length; j++) {
			if(value[j].count==0&&value[j].name=='其他'){
				continue;
			}
			seriesLevelObject.data[j] = new Object();
			var count = value[j].count;
			seriesLevelObject.data[j].name = value[j].name;
			if (value != undefined) {
				seriesLevelObject.data[j].value = count;
			} else {
				seriesLevelObject.data[j].value = "-";
			}
		}
		this.series.push(seriesLevelObject);

		var seriesTypeObject = new Object();
		seriesTypeObject.type = seriesType;
		seriesTypeObject.itemStyle = itemStyle;
		seriesTypeObject.center = doublePieCenterDefault_1;
		seriesTypeObject.radius = doublePieRadiusDefault;
		seriesTypeObject.minAngle = doublePieMinAngleDefault;
		seriesTypeObject.data = new Array();
		var valueType = this.faultType;
		for (var j = 0; j < valueType.length; j++) {
			seriesTypeObject.data[j] = new Object();
			var count = valueType[j].count;
			seriesTypeObject.data[j].name = valueType[j].name;
			if (valueType != undefined) {
				seriesTypeObject.data[j].value = count;
			} else {
				seriesTypeObject.data[j].value = "-";
			}
		}
		this.series.push(seriesTypeObject);
	}
	return option;
};

function getEnergyForPUELS(cusomers, energyType, pueDatas) {
	for ( var one in pueDatas) {
		if (pueDatas[one].customerName == cusomers
				&& pueDatas[one].energyType == energyType.name) {
			return pueDatas[one].value;
		}
	}
	return '-';
}

function getCountForAssetsLS(cust, month, assetsCountData) {
	for ( var one in assetsCountData) {
		if (assetsCountData[one].customer == cust
				&& assetsCountData[one].month == month) {
			return assetsCountData[one].count;
		}
	}
	return '-';
}

/**
 * 设置当天的起止时间
 * @param type
 * @returns
 */
function getNowDatebox(time) {
	var tempDate = new Date();
	if (time == 'fromTime') {
		tempDate.setTime(parseInt(fromTime));
	} else if (time == 'toTime') {
		tempDate.setTime(parseInt(toTime));
	} else {
	}
	return tempDate.Format("yyyy-MM-dd");
}

/**
 * 点状图的指标值国际化截取
 * @param value
 * @param metr
 * @returns
 */
function phraseStatus(value, metr){
	var cValue = parseFloat(value);
	if(isNaN(cValue)){
		return 'N/A';
	}
	try{
		var s = metr.split("|");
		for(var i=0;i<s.length;i++){
			if(parseFloat(s[i].split("-")[0])==cValue){
				return s[i].split("-")[1];
			}
		}
		return '未知';
	}catch(err){
		return cValue;
	}
}

/**
 * 点状图的点颜色格式化
 * @param value
 * @param metr
 * @returns {String}
 */
function phraseColor(value, metr) {
	var s = metr.split("|");
	for (var i = 0; i < s.length; i++) {
		if (parseFloat(s[i].split("-")[0]) == parseFloat(value)) {
			//			return eval('Properties_I18N.'+s[i].split("-")[1]);
			if (s[i].split("-")[2] == 'red') {
				return '#FF5555';
			} else if (s[i].split("-")[2] == 'green') {
				return '#00FF1A';
			} else {
				return '#CCCCCC';
			}
		}
	}
	return '#CCCCCC';
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
	var defaultPoint = 60;
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
 * 获得缩放比例，如果等于100则全部显示原始点，没有拖动条
 * 
 * @param value
 * @returns {Number}
 */
function getDataZoomScale4Report(value) {
	var maxPointCount = 0;
	// 默认推荐显示的最大原始点数，不会造成挤压、重叠等问题
	var defaultPoint = 120;
	// 默认缩放比例为100，即显示所有原始点，不出现缩放滑块
	var scale = 100;
	var pointCount = value.length;
	if (pointCount > maxPointCount) {
		maxPointCount = pointCount;
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
 * 
 * @param metricValue
 * @returns {Number}
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
 * 横坐标时间显示格式
 * 
 * @param time  
 * 						毫秒数
 * @returns
 */
function timeFormat(time) {
	var tempDate = new Date();
	tempDate.setTime(parseInt(time));
	return tempDate.Format("MM-dd HH:mm");
}

/**
 * 横坐标时间显示格式
 * 
 * @param time
 * 			格式：yyyy-MM-dd HH:mm:ss
 * @returns
 */
function timeFormat4Largescreen(time) {
	return time.substr(5,11);
}