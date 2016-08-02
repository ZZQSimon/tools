var echartsUtil = {};

//echart图表的全局变量，供图表自适应和销毁使用
echartsUtil.chart = new Array();

echartsUtil.chartMap = new Map();

//echart针对float类型的指标进行option对象构建,线图和饼图通用
echartsUtil.msLine = function(){	
	var option = this.generateCommonOption();
	//实现图表中最大值最小值的显示
	option.fillSeriesMarkPoint = function(option){
		var series = option.series;
		for(var i=0;i<series.length;i++){
			option.series[i].markPoint = new Object();
			option.series[i].markPoint.data = new Array();
			option.series[i].markPoint.data.push({
				type : 'max',
				name : '最大值'
			});
			option.series[i].markPoint.data.push({
				type : 'min',
				name : '最小值'
			});
		}
		return option;
	};
	
	//实现图表中平均值线的显示
	option.fillSeriesMarkLine = function(option){
		var series = option.series;
		for(var i=0;i<series.length;i++){
			option.series[i].markLine = new Object();
			option.series[i].markLine.data = new Array();
			option.series[i].markLine.data.push({
				type : 'average',
				name : '平均值'
			});
		}
		return option;
	};
	
	return option;
};

echartsUtil.udLine = function(metricParam){
	var formatArray = new Array();
	var option = this.generateCommonOption();
	
	option.tooltip = {
			trigger: 'axis',
			formatter: function (params) {
				var flag = false;
				var res = params[0][1];
				for ( var i = 0; i < formatArray.length; i++) {
					if(formatArray[i].time == res){
						flag = true;
						var valueStatus = phraseStatus(formatArray[i].value,metricParam);
						if(valueStatus == undefined){
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
	option.grid = {
				borderWidth : 0,
				x2 : 40,
				y : 35,
				y2 : 30		
			};
	option.xAxis =  [
	  				{
						type : 'category',
						boundaryGap : false,
						splitLine : {
							show: false
						},
						axisLine : {
							onZero: false,
							show:false
						},
						data :{
							
						}
					}     
				];
	option.yAxis =  [
	 				{
						splitNumber : 1,
						axisLine : {
							show:false
						},
						axisLabel : {
							show:false
						},
						splitLine : {
							show:false
						},
						type : 'value',
					}
				];
	
	option.fillLegend = function(){
		var dataSets = this.metricValue.dataSets;
		var metricCount = dataSets.length;
		for (var i = 0; i < metricCount; i++) {
			this.legend.data.push(dataSets[i].name);
		}
		
	};
	
	option.fillXaxis = function(){
		var dataSets = this.metricValue.dataSets;
		var metricCount = dataSets.length;
		//生成xAxis数组
		if (metricCount > 0) {
			var xAxisData = new Array();
			for (var one in dataSets[0].points) {
				var point = dataSets[0].points[one];
				xAxisData[one] = this.xAixsFormat(point.time);
			}
			this.xAxis[0].data = xAxisData;
		}
	};
	
	option.fillSeries= function(chartType,chartSymbol){
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
			for ( var j = 0; j < pointCount; j++) {
				seriesObject.data[j] = new Object();
				var value = dataPoints[j].value;
				var time = this.xAixsFormat(dataPoints[j].time);
				
				
				if (value != undefined) {
					var metricInfo = new Object();
					metricInfo.time = time;
					metricInfo.value = parseFloat(value);
					var metricColor = phraseColor(value,metricParam);
			
					formatArray.push(metricInfo);
//					if (parseFloat(value) == 1) {
						seriesObject.data[j].value =1;
						seriesObject.data[j].itemStyle = {
							normal : {
//								color : '#00FF1A',
								color : metricColor,
								borderColor : '#767575',
								borderWidth : 0.8
							}
						};
//					} else {

//						seriesObject.data[j].value = 1;
//						seriesObject.data[j].itemStyle = {
//							normal : {
//								color : '#CCCCCC',
//								borderColor : '#767575',
//								borderWidth : 0.8
//							}
//						};
//					}
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
//echart针对float类型的指标进行option对象构建
echartsUtil.realTimeChart = function(){
	var option = this.generateCommonOption();
	option.xAxis = [
					{
						type : 'category',
						boundaryGap : false,
						axisLine : {
							lineStyle :{
								color:'#545454',
								onZero: false
							}
						},
						data : {
							
						}
					}     
				];
	//设置相关传递参数，其中图形的统一默认颜色是#C0D5E5，多图例的情况其他颜色根据echart自动默认生成
	var defaultColor = '#4AA5FF';
	option.setDefaultColor(defaultColor);
	option.setMetricData = function(_) {
		option.metricData = _;
	};

	//设置图表的图例显示
	option.fillLegend = function(){
		for(var i in this.metricData){
			var metricName = this.metricData[i].name;
			var localeName = this.metricData[i].localeName;
			var unit = this.metricData[i].value.unit;
			this.legend.data.push(localeName);
		}
	};
	
	//设置x轴显示数据
	option.fillXaxis = function(){
		var metricValue = this.metricData[0].value;
		var dataSets = metricValue.dataSets;
		var metricCount = dataSets.length;
		//生成xAxis数组
		if (metricCount > 0) {
			var xAxisData = new Array();
			var dataPoints = dataSets[0].points;
			var time = dataPoints[dataPoints.length-1].time;
			xAxisData[0] = this.xAixsFormat(time);
			this.xAxis[0].data = xAxisData;
		}
	};
	
	//设置数据、图形类型、数据点的样式等
	option.fillSeries = function(chartType,chartSymbol){
		for(var i in this.metricData){
			var metricName = this.metricData[i].name;
			var localeName = this.metricData[i].localeName;
			var metricValue = this.metricData[i].value;
			var seriesObject = new Object();
			seriesObject.name = localeName;
			seriesObject.symbol = chartSymbol;
			seriesObject.type = chartType;
			//如果只有一个图例，使用默认颜色进行绘图
			if (i == 0) {
				seriesObject.itemStyle = {
					normal : {
						color : defaultColor
					}
				};
			}
			seriesObject.showAllSymbol = true;
			seriesObject.data = new Array();
			
			var dataSets = metricValue.dataSets;
			var metricCount = dataSets.length;
			if(metricCount>0){
				var dataPoints = dataSets[0].points;
				for ( var j = 0; j < dataPoints.length; j++) {
					var value = dataPoints[j].value;
					seriesObject.data[j] = new Object();
					if(value != undefined){
						seriesObject.data[j].value = parseFloat(value);
					}else{
						seriesObject.data[j].value = "-";
					}
				}
			}
			this.series.push(seriesObject);
		}
	};	
	return option;
};
//构造散点图的option
echartsUtil.scatter = function(){
	var option = this.generateCommonOption();
	option.dataRange = {
        orient: 'horizontal',
        y: 'bottom',
        x: 'center',
        //设置图例颜色
        color:['lightgreen','orange']	
    };	
	option.xAxis[0].type = 'value';
	option.xAxis[0].scale = true;
	option.xAxis[0].axisLabel = {
        formatter: function(value){
        	var tempDate = new Date();
    		tempDate.setTime(parseInt(value));
    		return tempDate.format("MM-dd HH:mm");
        }
    };
	option.yAxis[0].type = 'value';
	//设置图例
	option.setEnumValueArray = function(enumValueArray){
		option.enumValueArray = enumValueArray;
	};
	option.setEnumValueLabelArray = function(enumValueLabelArray){
		option.enumValueLabelArray = enumValueLabelArray;
	};
	option.setCategoryDataRange = function(){
	    //设置最小值
		option.dataRange.min = this.enumValueArray[0];
		//设置最大值
		option.dataRange.max = this.enumValueArray[this.enumValueArray.length-1]+1;
		option.dataRange.splitNumber = this.enumValueArray.length;
		//设置图例显示名称
		option.dataRange.formatter = function(v, v2){
			//根据值范围确定类别名称
			for(var i = 0 ; i < option.enumValueArray.length; i++){   
			     if(v<= option.enumValueArray[i] &&  option.enumValueArray[i] <= v2){	
				    return option.enumValueLabelArray[i];
				 }
			}
			//值范围内找不到为最后一个类别
			return option.enumValueLabelArray[(option.enumValueLabelArray.length-1)];
		};
		
	};
	//设置点的toolTip
	option.formatToolTip = function(){
		option.tooltip.trigger = "item";
		option.tooltip.formatter = function(value){
			 var yValue = value[2][1];
			 var yValueLabel = "";
			 //需要个性化处理，枚举值
			 var index = 0;
			for(var i = 0; i < option.enumValueArray.length; i++){
				if(yValue ==option.enumValueArray[i]){
					index = i;
					break;
				}
			}
			yValueLabel = option.enumValueLabelArray[index];
			var xValue = value[2][0];
			var tempDate = new Date();
    		tempDate.setTime(parseInt(xValue));
			var xValueLabel = tempDate.format("MM-dd HH:mm");	
			return  xValueLabel + '<br/> '+option.metricName+":" + yValueLabel; //yValueLabel  显示的
		};
	};
	//设置y轴的显示Label
	option.setYaxisCategoryLabel = function(){
	    option.yAxis[0].axisLabel = {};
		option.yAxis[0].axisLabel.formatter = function(v) {
		 var index = 0;
		 for(var i = 0 ; i < option.enumValueArray.length; i++){
			if(v == option.enumValueArray[i]){
				index = i;
				break;
			}
		 }
		 return option.enumValueLabelArray[index];				
		};
	};
	//设置y轴的值
	option.setYaxisData = function(){
	   option.yAxis[0].min =  this.enumValueArray[0];
	   option.yAxis[0].max =  this.enumValueArray[this.enumValueArray.length-1];
	   option.yAxis[0].data = this.enumValueArray; 
	};
	
	option.init = function(enumValueArray,enumValueLabelArray){
		this.setEnumValueArray(enumValueArray);
		this.setEnumValueLabelArray(enumValueLabelArray);
		option.setCategoryDataRange();
		//设置值的toolTip
		option.formatToolTip();
		//设置y轴的label
		option.setYaxisCategoryLabel();
		//设置y轴对应的值范围
		option.setYaxisData();	
	};
	
	option.fillSeries= function(chartType,chartSymbol){
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
			seriesObject.type = chartType;
			var dataPoints = dataSets[i].points;
			var pointCount = dataPoints.length;
			seriesObject.data = new Array();
			//设置散点图的值
			for ( var j = 0; j < pointCount; j++) {
				var value = dataPoints[j].value;
				var time = dataPoints[j].time;
				if (value != undefined){
					seriesObject.data[j]=[time,value,value];
				} else {
					//空值的处理
					seriesObject.data[j] = [time,"-","-"];
				}
			}
			this.series.push(seriesObject);
		}
		
	};
	return option;
	
};
//echart进行对应dom元素的图表渲染绘图
echartsUtil.render = function(id,option){
	require(
	        [
	            'echarts',
	            'echarts/chart/bar' 
	        ],
	        function (ec) {
	            // 基于准备好的dom,初始化echarts图表,同一个dom下多次init将自动释放已有实例
	        	if(id){
	        		var chart = ec.init(document.getElementById(id)); 
		        	echartsUtil.chart.push(chart);
		        	if(!echartsUtil.chartMap.contain(id)){
		        		echartsUtil.chartMap.put(id,chart);
		        	}
		        	
		        	chart.setOption(option); 
		        	
	        	}
	        }
	);
};

echartsUtil.changeView = function(chartId,echartType,echartSymbol){
	var chart = echartsUtil.chartMap.get(chartId);
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

//动态添加数据
echartsUtil.addData = function(chartId,dataArray){
	if(echartsUtil.chartMap && chartId){
		if(echartsUtil.chartMap.get(chartId)!=undefined){
			echartsUtil.chartMap.get(chartId).addData(dataArray);
		}
	}
};

//窗口改变大小后 echart图表自适应
echartsUtil.resize = function(){
	for ( var i = 0; i < echartsUtil.chart.length; i++) {
		echartsUtil.chart[i].resize();
	}
};

//设置echarts的图表联动
echartsUtil.connect = function(){
	var cursor = 0;
	for ( var i = 0; i < echartsUtil.chart.length; i++) {
		while(cursor < echartsUtil.chart.length){
			if (cursor != i && echartsUtil.chart[i].option.dataZoom.show == true) {
				echartsUtil.chart[i].connect(echartsUtil.chart[cursor]);
			}
			cursor++;
		}
	}
};

//释放并销毁所有的chart对象
echartsUtil.release = function(){
	for ( var i = 0; i < echartsUtil.chart.length; i++) {
		echartsUtil.chart[i].dispose();
	}
};

//构建初始公共Option,具体各种图有不一样的属性，可重载对应的属性。
echartsUtil.generateCommonOption = function(){
	 var option = {
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
				trigger: 'axis'
			},
			toolbox : {
				show : false,
				feature : {
					dataView : {show: true},
					magicType : {show: true, type: ['line', 'bar']},
					restore : {show: true},
					saveAsImage : {show: true}
				}
			},
			legend: {
		        orient: 'horizontal', // 'vertical'
		        x: 'center', // 'center' | 'left' | {number}
		        y: 'bottom', // 'center' | 'bottom' | {number}
		        data: [
		               
		        ]
		    },
			dataZoom :{
				show : false,
				realtime : true,
				y : 8,
				height : 15,
				fillerColor : '#CECECE',
				dataBackgroundColor : '#C0D5E5',
				start : 0
			},
			grid :{
				x2 : 40,
				y : 30,
				y2 : 30	
			},
			xAxis : [
				{
					type : 'category',
					splitLine : {
						show: false
					},
					axisLine : {
						lineStyle :{
							color:'#545454',
							onZero: false
						}
					},
					data :{
						
					}
				}     
			],
			yAxis : [
				{
					type : 'value',
					scale : true,
					axisLine : {
						lineStyle :{
							color:'#545454',
							onZero: false
						}
					}
				}
			],
			series : [
			          
			]
	};
	 
	option.showLabel = false;
	option.setShowLabel = function(_) {
		this.showLabel = _;
	};
	
	option.setLegendValue = function(_) {
		this.legendValue = _;
	};
	
	option.boundaryGap = false;
	option.setBoundaryGap = function(_) {
		this.boundaryGap = _;
	};
	option.defaultColor = '#4AA5FF';
    option.setDefaultColor = function(_){
		this.defaultColor = _;
	};
	option.setMetricName = function(_) {
		option.metricName = _;
	};
	
	option.setMetricValue = function(_) {
		option.metricValue = _;
	};
	
	option.setXaixsFormat = function(_) {
		option.xAixsFormat = _;
	};
	
	//设置图表的图例显示
	option.fillLegend = function(){
		var dataSets = this.metricValue.dataSets;
		var metricCount = dataSets.length;
		//多指标进行排序
		if (metricCount > 1) {
			dataSets.sort( function (a, b) {
				if (a == b) return 0;
				return a.name > b.name ? 1 : -1; 
			});
		}
		for (var i = 0; i < metricCount; i++) {
			this.legend.data.push(dataSets[i].name);
		}
		//return option;
	};
	
	option.fillFixedLegend = function(){
		this.legend.data.push(this.legendValue);
	};
	
	//设置缩放滚动滑块的显示以及缩放比例、是否锁定等
	option.fillDataZoom = function(show,end,zoomLock){
		this.dataZoom.show = show;
	    this.dataZoom.end = end;
	    this.dataZoom.zoomLock = zoomLock;

	};
	//设置图表四个方向的空隙间距
	option.fillGrid = function(x,x2,y,y2){
		if(x!=""){
			this.grid.x = x;
		}
		if(x2!=""){
			this.grid.x2 = x2;
		}
		if(y!=""){
			this.grid.y = y;
		}
		if(y2!=""){
			this.grid.y2 = y2;
		}
		
	};
	
	//设置x轴显示数据
	option.fillXaxis = function(){
		var dataSets = this.metricValue.dataSets;
		var metricCount = dataSets.length;
		//生成xAxis数组
		if (metricCount > 0) {
			var xAxisData = new Array();
			for (var one in dataSets[0].points) {
				var point = dataSets[0].points[one];
				xAxisData[one] = this.xAixsFormat(point.time);
			}
			this.xAxis[0].data = xAxisData;
			this.xAxis[0].boundaryGap = this.boundaryGap;
		}
	};
	option.fillYaxis = function(option){
		return option;
	};
	
	//设置数据、图形类型、数据点的样式等
	option.fillSeries = function(chartType,chartSymbol){
		var dataSets = this.metricValue.dataSets;
		var metricCount = dataSets.length;
		//生成series数组
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
			//如果只有一个图例，使用默认颜色进行绘图
			if (i == 0) {
				seriesObject.itemStyle = {
					normal : {
						color : this.defaultColor,
						label : {
							show :this.showLabel
						}
					}
				};
			}
			seriesObject.showAllSymbol = true;
			seriesObject.data = new Array();
			for (var j = 0; j < pointCount; j++) {
				seriesObject.data[j] = new Object();
				var value = dataPoints[j].value;
				if(value != undefined){
					seriesObject.data[j].value = parseFloat(value);
				}else{
					seriesObject.data[j].value = "-";
				}
			}
			this.series.push(seriesObject);
		}
	};
	return option;
	
};
/**
 * 点状图的指标值国际化截取
 * @param value
 * @param metr
 * @returns
 */
function phraseStatus(value, metr){
	var s = metr.split("|");
	for(var i=0;i<s.length;i++){
		if(parseFloat(s[i].split("-")[0])==parseFloat(value)){
			return s[i].split("-")[1];
		}
	}
	return "N/A";
}
/**
 * 点状图的点颜色格式化
 * @param value
 * @param metr
 * @returns {String}
 */
function phraseColor(value, metr){
	var s = metr.split("|");
	for(var i=0;i<s.length;i++){
		if(parseFloat(s[i].split("-")[0])==parseFloat(value)){
//			return eval('Properties_I18N.'+s[i].split("-")[1]);
			if(s[i].split("-")[2]=='red'){
				return '#FF5555';
			}else if(s[i].split("-")[2]=='green'){
				return '#00FF1A';
			}else{
				return '#CCCCCC';
			}
		}
	}
	return '#CCCCCC';
}