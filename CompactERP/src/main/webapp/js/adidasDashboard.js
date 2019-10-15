/**
 * Created by Administrator on 2017/12/14.
 */
registerInit('adidasDashboard', function (form) {
    function initCharts(data){
        //图表1
        function hcFteChart(data){
            var $chart = form.get().find('.hcFteChart');
            var adidasChart = echarts.init($chart[0]);
            var option = {
                xAxis: {
                    show:false,
                    type: 'category'
                },
                yAxis: {
                    show: false,
                    type: 'value'
                },
                legend: {
                    selectedMode: false,
                    bottom: 'bottom',
                    data: [{
                        name: 'HC'
                    }, {
                        name: 'Headcount FTE'
                    }],
                    pageIconColor: 'red'
                },
                series: [{
                    name: 'HC',
                    itemStyle: {normal: {color:'#1C81CA', label:{show:true, position: 'top', textStyle:{color:'black',fontWeight:'bolder'}}}},
                    data: [data.Intern+data.FTE+data.FSM],
                    type: 'bar'
                },{
                    name: 'Headcount FTE',
                    itemStyle: {normal: {color:'#D11149', label:{show:true, position: 'top',textStyle:{color:'black',fontWeight:'bolder'}}}},
                    data: [data.Intern+data.FTE+data.FSM],
                    type: 'bar'
                }]
            };
            adidasChart.setOption(option);
        }
        //图表2
        function contractChart(data){
            var $chart = form.get().find('.contractChart');
            var adidasChart = echarts.init($chart[0]);
            var option = {
                xAxis: {
                    show:false,
                    type: 'category'
                },
                yAxis: {
                    show: false,
                    type: 'value'
                },
                legend: {
                    selectedMode: false,
                    bottom: 'bottom',
                    data: [{
                        name: '1st Term'
                    },{
                        name: '2nd Term'
                    },{
                        name: 'Open-Ended'
                    },{
                        name: 'Intern'
                    }]
                },
                series: [{
                    name: '1st Term',
                    stack:1,
                    itemStyle: {normal: {color:'#F08A47', label:{show:true,position:"top",textStyle:{color:'black',fontWeight:'bolder'},formatter: function(data){
                        return data.data + "%";
                    }}}},
                    data: [data.two],
                    type: 'bar'
                },{
                    name: 'Open-Ended',
                    stack:3,
                    itemStyle: {normal: {color:'#00BAAF', label:{show:true,position:"top",textStyle:{color:'black',fontWeight:'bolder'}, formatter: function(data){
                        return data.data + "%";
                    }}}},
                    data: [data.three],
                    type: 'bar'
                },{
                    name: '2nd Term',
                    stack:2,
                    itemStyle: {normal: {color:'#D11149', label:{show:true,position:"top",textStyle:{color:'black',fontWeight:'bolder'},formatter: function(data){
                        return data.data + "%";
                    }}}},
                    data: [data.one],
                    type: 'bar'
                },{
                    name: 'Intern',
                    stack:4,
                    itemStyle: {normal: {color: '#1C81CA', label:{show:true,position:"top",textStyle:{color:'black',fontWeight:'bolder'}, formatter: function(data){
                        return data.data + "%";
                    }}}},
                    data: [100],
                    type: 'bar'
                }]
            };
            adidasChart.setOption(option);
        }
        //图表3
        function lengthServiceChart(data){
            var $chart = form.get().find('.lengthServiceChart');
            var adidasChart = echarts.init($chart[0]);
            var option = {
                title : {
                    text: 'AVG LOS\r\n'+data.avg,
                    subtext: '',
                    textAlign: 'center',
                    x: 'right',
                    y: '10px',
                    backgroundColor: 'rgb(206, 206, 206)'
                },
                xAxis: {
                    show:false,
                    type: 'value'
                },
                yAxis: {
                    show: true,
                    type: 'category',
                    data: ['<0.5', '0.5-1', '1-2', '2-3', '3-5', '5-10', '10+']
                },
                series: [{
                    itemStyle: {normal: {color:'rgb(0, 186, 175)', label:{show:true, position: 'right', textStyle:{color:'black',fontWeight:'bolder'}, formatter: function(data){
                        return data.data + "%";
                    }}}},
                    data: data.list,
                    type: 'bar'
                }]
            };
            adidasChart.setOption(option);
        }
        //图表4
        function distriButtonChart(data){
            form.get().find('.girl-count').text(data.girl + "%");
            form.get().find('.girl-FTE-count').text(data.girlFTE + "%");
            form.get().find('.girl-Intern-count').text(data.girlIntern + "%");
            form.get().find('.boy-count').text(data.boy + "%");
            form.get().find('.boy-FTE-count').text(data.boyFTE + "%");
            form.get().find('.boy-Intern-count').text(data.boyIntern + "%");
        }
        //图表5
        function ageChart(data){
            var $chart = form.get().find('.ageChart');
            var adidasChart = echarts.init($chart[0]);
            var option = {
                grid: {
                    left: '0%',
                    right: '20%',
                    bottom: '20%',
                    containLabel: true
                },
                xAxis: {
                    show: true,
                    type: 'value',
                    boundaryGap: [0.2]
                },
                yAxis: {
                    axisTick: {show: false},
                    axisLabel: {show: true, margin: 3},
                    type: 'category',
                    data: ['<25', '25-30', '31-40', '41-50', '51-60', '>60']
                },
                series: [{
                    stack: 1,
                    itemStyle: {normal: {color: '#D11149', label:{show: true, position: 'left', textStyle: {color:'black',fontWeight:'bolder'}, formatter: function(data){
                        return (0 - data.data) + "%";
                    }}}},
                    data: data.girl,
                    type: 'bar'
                },{
                    stack: 1,
                    itemStyle: {normal: {color: '#00BAAF', label:{show: true, position: 'right', textStyle: {color:'black',fontWeight:'bolder'}, formatter: function (data) {
                        return data.data + "%";
                    }}}},
                    data: data.boy,
                    type: 'bar'
                }]
            };
            adidasChart.setOption(option);
        }
        //图表6
        function workYearChart(data){
            var $chart = form.get().find('.workYearChart');
            var adidasChart = echarts.init($chart[0]);
            var option = {
                grid: {
                    left: '0%',
                    right: '20%',
                    bottom: '20%',
                    containLabel: true
                },
                xAxis: {
                    show: true,
                    type: 'value',
                    boundaryGap: [0.1]
                },
                yAxis: {
                    axisTick : {show: false},
                    type: 'category',
                    data: ['<0.5', '0.5-1', '1-2', '2-3', '3-5', '5-10', '10+']
                },
                series: [{
                    stack: 1,
                    itemStyle: {normal: {color: '#D11149', label:{show: true, position: 'left', textStyle: {color:'black',fontWeight:'bolder'}, formatter: function(data){
                        return (0 - data.data) + "%";
                    }}}},
                    data: data.girl,
                    type: 'bar'
                },{
                    stack: 1,
                    itemStyle: {normal: {color: '#1C81CA', label:{show: true, position: 'right', textStyle: {color:'black',fontWeight:'bolder'}, formatter: function (data) {
                        return data.data + "%";
                    }}}},
                    data: data.boy,
                    type: 'bar'
                }]
            };
            adidasChart.setOption(option);
        }
        hcFteChart(data.one);
        contractChart(data.two);
        lengthServiceChart(data.three);
        distriButtonChart(data.four);
        ageChart(data.five);
        workYearChart(data.six);
    }
    function initChartsMovement(data){
    	for(var i=0; i<data.length;i++){
    		if(data[i].change_type=='hires'){
    			var hirescount = data[i].count;
    			$(".hirescount").text(hirescount);
    		}
    		if(data[i].change_type=='Promotion'){
    			var promotioncount = data[i].count;
    			$(".Termination").text(promotioncount);
    		}
    		if(data[i].change_type=='transfer in'){
    			var transferincount = data[i].count;
    			$(".transferoutcount").text(transferincount);
    		}
    		if(data[i].change_type=='transfer out'){
    			var transferoutcount = data[i].count;
    			$(".terminationcount").text(transferoutcount);
    		}
    		if(data[i].change_type=='termination'){
    			var terminationcount = data[i].count;
    			$(".promotioncount").text(terminationcount);
    		}
    	}
    	
    	
    }
    function resetDatetime(){
        var datetimeDom = form.get().find('div[name="period"]').find(".dx-filter-container");
        $(datetimeDom).empty();
        $(datetimeDom).append('<input type="text" name="period" class="dx-filter dx-filter-date">')
        //$(datetimeDom).append('<input type="text" name="period" class="dx-filter dx-filter-date dx-filter-start">')
    }
    form.get = function () {
        return $('#' + form.id);
    };
    form.filter.get = function () {
        return $('#' + form.filter.id);
    };
    if (form.type == 1){
        //重定义日期类型区间
        resetDatetime();
    }
    initFilter(form, function (a, filter, c) {
        //report按钮。
        postJson('/adidasDashboard/search.do', {filterTable: form.tableName, filter: filter, type: form.type}, function (result) {
            if (form.type == 1)
                initCharts(result);
            else
                initChartsMovement(result);
        })
    }, function (a, b, c) {
        //重置按钮回调。
        var bbb = 2;
    });
    if (form.type == 1) {
        form.get().find('div[name="period"]').find('input.dx-filter-date').datepicker('remove');
        form.get().find('div[name="period"]').find('input.dx-filter-date').datepicker({
            format: "yyyy-mm",
            startView: 2,
            maxViewMode: 1,
            minViewMode:1,
            autoclose: true
        });
    }
});
