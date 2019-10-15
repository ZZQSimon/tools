registerInit('timingTask', function (form) {
    var $form= form.get();
    var timingTaskList=form.timingTask;
    var timingTaskMap=[];
    var dataGrid = $form.find('.timingTaskTable');
    initDataGrid(); //初始化表格

    $form.find(".treeScroll").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"});
    $form.find('.icheckbox input').iCheck({
        checkboxClass: 'icheckbox_flat-blue',
        radioClass: 'iradio_flat-blue'
    });
    //加载日期时间控件
    $form.find('.begin_date').datetimepicker({format: 'yyyy-mm-dd hh:ii:ss', autoclose: true});
    $form.find('.end_date').datetimepicker({format: 'yyyy-mm-dd hh:ii:ss', autoclose: true});

    //初始化任务
    for(var i=0;i<timingTaskList.length;i++){
    	$form.find(".timingTaskItem").append('<li class="item"><span class="timeTaskContent" taskId="'+timingTaskList[i].task_id+'">'
    			+ (isEmpty(timingTaskList[i].name_i18n) ? '' : timingTaskList[i].name_i18n[dx.user.language_id]) + '</span></li>');
    	timingTaskMap[timingTaskList[i].task_id]=timingTaskList[i];
    }
    
    //点击任务，查询任务数据
    $form.find(".timingTaskItem").on("click","li",function(){
    	if(endAllRowEdit() && getTimeTaskDate()){
        	//清空数据
        	 $form.find(".timingTaskItem span").removeClass("active");
        	 $(this).find("span").addClass("active");
        	 $form.find(".sysTimeCheckBok").iCheck('uncheck');
        	 $form.find('.begin_date').val("");
        	 $form.find('.end_date').val("");
        	 $form.find(".endDateCheckbox").iCheck('uncheck');
        	 $form.find('.end_date').attr("disabled","disabled");   
    		 $form.find(".isLoopCheckBox").iCheck('uncheck');
    		 $form.find('.space_sysTime').attr("disabled","disabled");
    		 $form.find('.space_sysTime').val("");
    		 $form.find('.loop_type').attr("disabled","disabled");
    		 $form.find('.loop_type option').removeAttr("selected");
    		 $form.find('.lead_sysTime').val("");
    		 $form.find('.lead_type_sysTime option').removeAttr("selected");
    		 $form.find(".businessTimeCheckBox").iCheck('uncheck');
    		 $form.find('.businessTable option').removeAttr("selected");
    		 $form.find('.businessColumn option').removeAttr("selected");
    		 $form.find('.businessColumn').empty();
    		 $form.find('.task_lead').val("");
    		 $form.find('.lead_type option').removeAttr("selected");
    		 $form.find('.filter_sql').val("");
    		 var task_id=$(this).find("span").attr("taskId");
    		 $form.find(".deploy-left-tree-delete").show();
        	 //设置任务内容
    	     dataGrid.datagrid('loadData', timingTaskMap[task_id].timeTaskEventDescribes);
    	     //设置系统时间
    	     var timeTaskSysTime = timingTaskMap[task_id].timeTaskSysTimeDescribes;
    	     for(var i=0;i<timeTaskSysTime.length;i++){
    	    	 if(timeTaskSysTime[i].is_using==1){
    	    		 $form.find(".sysTimeCheckBok").iCheck('check');
    		     }
    	    	 if(!isEmpty(timeTaskSysTime[i].begin_date)){
    	    		 $form.find('.begin_date').val(Format(new Date(timeTaskSysTime[i].begin_date)));
    	    	 }else{
    	    		 $form.find('.begin_date').val("");
    	    	 }
    	    	 if(!isEmpty(timeTaskSysTime[i].end_date)){
    	    		 $form.find('.end_date').val(Format(new Date(timeTaskSysTime[i].end_date)));
    	    		 $form.find(".endDateCheckbox").iCheck('check');
    	    		 $form.find('.end_date').removeAttr("disabled");   
    	    	 }
    	    	 if(timeTaskSysTime[i].is_loop==1){
    	    		 $form.find(".isLoopCheckBox").iCheck('check');
    	    		 $form.find('.space_sysTime').removeAttr("disabled");  
    	    		 $form.find('.loop_type').removeAttr("disabled");
    	    		 $form.find('.space_sysTime').val(timeTaskSysTime[i].space);
    	    		 $form.find('.loop_type option[value="'+timeTaskSysTime[i].loop_type+'"]').prop("selected",true);
    	    	 }
    	    	 $form.find('.lead_sysTime').val(timeTaskSysTime[i].lead);
    	    	 $form.find('.lead_type_sysTime option[value="'+timeTaskSysTime[i].lead_type+'"]').prop("selected",true);
    	     }
    	     //设置业务时间
    	     var timeTaskBusinessTime = timingTaskMap[task_id].timeTaskBusinessTimeDescribes;
    	     for(var i=0;i<timeTaskBusinessTime.length;i++){
    	    	 if(timeTaskBusinessTime[i].is_using==1){
    	    		 $form.find(".businessTimeCheckBox").iCheck('check');
    		     }
    	    	 $form.find('.businessTable option[value="'+timeTaskBusinessTime[i].table+'"]').prop("selected",true);
    	    	 $form.find('.businessColumn').empty();
    	     	 $form.find('.businessColumn').append('<option value=""></option>');
    	    	 if(!isEmpty(timeTaskBusinessTime[i].table)){
    	    		 var columns=dx.table[timeTaskBusinessTime[i].table].columns;
    	    		 for(var j=0;j<columns.length;j++){
    		     	 	 $form.find('.businessColumn').append('<option value="'+columns[j].column_name+'">'+columns[j].i18n[dx.user.language_id]+'</option>');
    		     	 }
    	    	 }
    	    	 $form.find('.businessColumn option[value="'+timeTaskBusinessTime[i].column+'"]').prop("selected",true);
    	    	 $form.find('.task_lead').val(timeTaskBusinessTime[i].lead);
    	    	 $form.find('.lead_type option[value="'+timeTaskBusinessTime[i].lead_type+'"]').prop("selected",true);
    	    	 $form.find('.filter_sql').val(timeTaskBusinessTime[i].filter_sql);
    	     }
    	}
    });
  //默认点击第一个定时任务
    $form.find(".timingTaskItem li:first-of-type").click();
    //获取定时任务数据
    function getTimeTaskDate(){
    	var taskId=$form.find(".timingTaskItem .timeTaskContent.active").attr("taskId");
    	if(!isEmpty(taskId)){
    		if(timingTaskMap[taskId].saveType!=1){
    			timingTaskMap[taskId].saveType=2;
    		}
    		timingTaskMap[taskId].timeTaskEventDescribes=dataGrid.datagrid('getRows');
        	var timeTaskSysTimeList=[];
        	var timeTaskSysTime={};
        	timeTaskSysTime.task_id=taskId;
        	if(timingTaskMap[taskId].timeTaskSysTimeDescribes.length>0){
            	timeTaskSysTime.sys_time_id=timingTaskMap[taskId].timeTaskSysTimeDescribes[0].sys_time_id;
        	}else{
        		timeTaskSysTime.sys_time_id=uuid(5,16);
        	}
        	var beginDate=$form.find('.begin_date').val();
        	if ($form.find(".sysTimeCheckBok").is(':checked')) {
        		timeTaskSysTime.is_using = 1;
        		if(isEmpty(beginDate)){
        			alert("开始时间不能为空！");
        			return false;
        		}
        	}else{
        		timeTaskSysTime.is_using = 0;
        	}
    		
        	timeTaskSysTime.begin_date = new Date(Date.parse(beginDate.replace(/-/g, "/")));
        	var endDate=$form.find('.end_date').val();
        	if ($form.find(".endDateCheckbox").is(':checked')) {
        		timeTaskSysTime.end_date = new Date(Date.parse(endDate.replace(/-/g, "/")));;
        	}else{
        		timeTaskSysTime.end_date = "";
        	}
        	if ($form.find(".isLoopCheckBox").is(':checked')) {
        		timeTaskSysTime.is_loop = 1;
        		timeTaskSysTime.space = $form.find('.space_sysTime').val();
        		timeTaskSysTime.loop_type = $form.find(".loop_type option:selected").val();
        	}else{
        		timeTaskSysTime.is_loop=0;
        		timeTaskSysTime.space = "";
        		timeTaskSysTime.loop_type = "";
        	}
        	timeTaskSysTime.lead = $form.find('.lead_sysTime').val();;
        	timeTaskSysTime.lead_type =  $form.find('.lead_type_sysTime option:selected').val();
        	timeTaskSysTimeList.push(timeTaskSysTime);
    		timingTaskMap[taskId].timeTaskSysTimeDescribes = timeTaskSysTimeList;
    		
    		var timeTaskBusinessTimeList=[];
        	var timeTaskBusinessTime={};
        	timeTaskBusinessTime.task_id=taskId;
        	if(timingTaskMap[taskId].timeTaskBusinessTimeDescribes.length>0){
        		timeTaskBusinessTime.business_time_id=timingTaskMap[taskId].timeTaskBusinessTimeDescribes[0].business_time_id;
        	}else{
        		timeTaskBusinessTime.business_time_id=uuid(5,16);
        	}
        	var table=$form.find('.businessTable option:selected').val();
        	var column = $form.find('.businessColumn option:selected').val();
        	if ($form.find(".businessTimeCheckBox").is(':checked')) {
        		timeTaskBusinessTime.is_using = 1;
        		if(isEmpty(table)){
        			alert("业务表不能为空！");
        			return false;
        		}
        		if(isEmpty(column)){
        			alert("字段不能为空！");
        			return false;
        		}
        	}else{
        		timeTaskBusinessTime.is_using = 0;
        	}
        	timeTaskBusinessTime.table = $form.find('.businessTable option:selected').val();
        	timeTaskBusinessTime.column = $form.find('.businessColumn option:selected').val();
        	timeTaskBusinessTime.lead = $form.find('.task_lead').val();
        	timeTaskBusinessTime.lead_type = $form.find('.lead_type option:selected').val();
        	timeTaskBusinessTime.filter_sql = $form.find('.filter_sql').val();
        	timeTaskBusinessTimeList.push(timeTaskBusinessTime);
    		timingTaskMap[taskId].timeTaskBusinessTimeDescribes =  timeTaskBusinessTimeList;
    	}
    	return true;
    }
    //格式化日期
    function Format(date)
    {
    	var formatted = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
    	return formatted;
    };
    //任务事件启用的点击事件
    $form.find(".dicContent").on("click",".taskIsUsing .input-bt-Switcher",function () {
    	var row=dataGrid.datagrid('getSelected');
    	if ($form.find(".taskIsUingCheckBox").is(':checked')) {
    		row.is_using=1;
    	}else{
    		row.is_using=0;
    	}
    });
    //结束时间复选框的点击事件
    $form.find(".endDateCheckbox").on('ifChecked', function () {
    	$form.find('.end_date').removeAttr("disabled");  
    }).on('ifUnchecked', function () {
    	$form.find('.end_date').attr("disabled","disabled"); 
    });
    //业务表选择事件
    $form.find('.businessTable').change(function () {
    	var tableId = $(".businessTable option:selected").val();
    	var columns=dx.table[tableId].columns;
    	$form.find('.businessColumn').empty();
    	$form.find('.businessColumn').append('<option value=""></option>');
    	for(var i=0;i<columns.length;i++){
    		$form.find('.businessColumn').append('<option value="'+columns[i].column_name+'">'+columns[i].i18n[dx.user.language_id]+'</option>');
    	}
    });
    //循环点击事件
    $form.find(".sysTimeIsLoop").on("click",".input-bt-Switcher",function () {
    	if ($form.find(".isLoopCheckBox").is(':checked')) {
    		 $form.find('.space_sysTime').removeAttr("disabled");  
    		 $form.find('.loop_type').removeAttr("disabled");
        }else{
        	 $form.find('.space_sysTime').attr("disabled","disabled");
    		 $form.find('.loop_type').attr("disabled","disabled");
        }
    });
    //检索
    retrieveTimeTask();
    function retrieveTimeTask(){
        var to;
        var leftSearch = $form.find('.retrieve_input');
        leftSearch.keyup(function () {
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                var retrieveValue = leftSearch.val();
                postJson('/timingTask/retrieveTimeTask.do', {retrieveValue: retrieveValue}, function (data) {
                	$form.find(".timingTaskItem span").removeClass("active");
                	$form.find(".timingTaskItem li").remove();
                	if(!isEmpty(data)){
                         for(var i=0;i<data.length;i++){
                         	$form.find(".timingTaskItem").append('<li class="item"><span class="timeTaskContent" taskId="'+data[i].task_id+'">'
                         			+data[i].name_i18n[dx.user.language_id]+'</span></li>');
                         }
                	}
                	for(var key in timingTaskMap){
                		if(isEmpty(retrieveValue)){
                			if(timingTaskMap[key].saveType==1){
                    			$form.find(".timingTaskItem").append('<li class="item"><span class="timeTaskContent" taskId="'+timingTaskMap[key].task_id+'">'
                             			+timingTaskMap[key].name_i18n[dx.user.language_id]+'</span></li>');
                    		}
                		}else{
                			if(timingTaskMap[key].saveType==1 && timingTaskMap[key].name_i18n[dx.user.language_id].indexOf(retrieveValue)!=-1){
                    			$form.find(".timingTaskItem").append('<li class="item"><span class="timeTaskContent" taskId="'+timingTaskMap[key].task_id+'">'
                             			+timingTaskMap[key].name_i18n[dx.user.language_id]+'</span></li>');
                    		}
                		}
                	}
                });
            }, 250);
        });
    }
    //删除定时任务
    $form.find(".deleteTimeTask").on("click",function () {
        var task_id=$form.find(".timingTaskItem .timeTaskContent.active").attr("taskId");
        if(confirm("是否删除定时任务？")){
        	timingTaskMap[task_id].saveType=3;
        	$form.find(".timingTaskItem .timeTaskContent.active").parent("li").remove();
        	dataGrid.datagrid('loadData',[]);
        	$form.find(".timingTaskItem li:first-of-type").click();
        }
    });

    //新增定时任务
    $form.find(".addTimeTask").on("click", function () {
        if(endAllRowEdit()){
        	var task_id=uuid(10,16);
            var timeTaskName=$form.find("input[name=addTimeTaskValue]").val();
            $form.find(".timingTaskItem").append('<li class="item"><span class="timeTaskContent" taskId="'+task_id+'">'
        			+timeTaskName+'</span></li>');
            var timeTask={};
            timeTask.international_id="";
            timeTask.name_i18n={};
            timeTask.name_i18n[dx.user.language_id]=timeTaskName;
            timeTask.saveType=1;
            timeTask.task_id=task_id;
            
            var timeTaskBusinessTimeList=[];
            var timeTaskBusinessTime={};
            timeTaskBusinessTime.task_id=task_id;
            timeTaskBusinessTime.business_time_id=uuid(5,16);
            timeTaskBusinessTimeList.push(timeTaskBusinessTime);
            timeTask.timeTaskBusinessTimeDescribes = timeTaskBusinessTimeList;
            
            var timeTaskEventList=[];
            var timeTaskEvent={};
            timeTaskEvent.task_id=task_id;
            timeTaskEvent.event_id=uuid(5,16);
            timeTaskEventList.push(timeTaskEvent);
            timeTask.timeTaskEventDescribes=timeTaskEventList;
            
            var timeTaskSysTimeList=[];
            var timeTaskSysTime={};
            timeTaskSysTime.task_id=task_id;
            timeTaskSysTime.sys_time_id=uuid(5,16);
            timeTaskSysTimeList.push(timeTaskSysTime);
            timeTask.timeTaskSysTimeDescribes= timeTaskSysTimeList;
            
            timingTaskMap[task_id]=timeTask;
            dataGrid.datagrid('loadData', []);
            $form.find(".timingTaskItem .timeTaskContent[taskId='"+task_id+"']").click();
        }
    });
    //保存
    $form.find(".btn-toolbar .accept").on("click", function () {
    	if(endAllRowEdit() && getTimeTaskDate()){
        	var saveTimingTask=[];
        	for(var key in timingTaskMap){
        		saveTimingTask.push(timingTaskMap[key]);
        	}
        	dx.processing.open();
        	postJson('/timingTask/saveTimingTask.do', {saveTimingTask: saveTimingTask}, function (result) {
        		timingTaskList=result;
        		timingTaskMap=[];
        		for(var i=0;i<timingTaskList.length;i++){
    		    	timingTaskMap[timingTaskList[i].task_id]=timingTaskList[i];
        		}
        		dxToastAlert(msg('Saved successfully!'));
            	postJson('/data/cache/reload.do', function(){
            		dxReload(function(){
                		dxToastAlert(msg('Saved successfully!'));
                		dx.processing.close();
                	});
                })
            });
    	}
    });
  //事件类型下拉框
    function gridDiceSelect(dictId) {
        var dict = dx.dict[dictId];
        var OptionsData = [];
        OptionsData.push({key: '', productname: ''});
        for (var key in dict) {
            OptionsData.push({
                key: key,
                productname: dict[key][dx.user.language_id]
            });
        }
        return OptionsData;
    }
  //事件名称下拉框
    function getUrlSelect(type) {
        var url = dx.urlInterface;
        var OptionsData = [];
        OptionsData.push({key: '', productname: ''});
        for (var key in url) {
            OptionsData.push({
                key: key,
                productname: url[key].i18n ? url[key].i18n[dx.user.language_id] : url[key].name
            });
        }
        return OptionsData;
    }
    $form.find('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
    	dataGrid.datagrid('resize')
    });
    function initDataGrid(){
    	//表格文本编辑器
        $.extend($.fn.datagrid.defaults.editors, {    
        	interParam: {    
                init: function(container, options){ 
                	var str ='<div class="input-4 clearfix" style="position: relative;width:100%">'
                			+'<input class="urlValue" type="text" name="urlValue" readonly disabled/>'
                			+'<button class="btn parameterInput" type="button" ></button></div>'
                    var input = $(str).appendTo(container);    
                    return input;    
                },     
                getValue: function(target){
                	return form.get().find(".urlValue").val();
                },    
                setValue: function(target, value){
                	form.get().find(".urlValue").val(value);
                	var getInterRow=dataGrid.datagrid('getSelected');
                	var index=dataGrid.datagrid('getRowIndex',getInterRow);
                	var id = form.get().find('tr[datagrid-row-index='+index+']').find("td[field=event_url_id]").find('input.textbox-text').val();
                	//加载参数输入控件
                    form.get().find(".parameterInput").on("click", function () {
                        var josnstr = form.get().find(".urlValue").val();
                        dataGrid.datagrid('endEdit',index);
                        dataGrid.datagrid('selectRow', index).datagrid('beginEdit', index);
                        var row=dataGrid.datagrid('getRows')[index];
                        var urlId = row.event_url_id;
                        var modalX = "40";
                        var modalY = "40";
                        form.get().find(".parameterInput").parameterInputControl(true).setData("onSubmit", function (data) {
                            form.get().find(".urlValue").val(data);
                        }, josnstr, urlId, modalX, modalY, "aa");

                    });
                } 
            }    
        });  
    	var eventTypeOptionsData = gridDiceSelect('event_type');
    	var urlSelectOptionsData = getUrlSelect();
	    dataGrid.datagrid({
	        height: "100%",
	        singleSelect: false,
	        rownumbers: true,
	        striped: true,
	        fitColumns: true,
	        onClickRow : function (rowIndex, rowData) {
	            $(this).datagrid('unselectAll');
	            $(this).datagrid('selectRow', rowIndex);
	        },
	        onLoadSuccess:function(){
	           // dataGrid.datagrid('enableDnd');
	        },
	        onDrop:function(targetRow, sourceRow, point) {
                //拖拽某行到指定位置后触发
                var rows = dataGrid.datagrid('getRows');
            },
	        onClickCell : function(rowIndex, field, value){
	            var ed = dataGrid.datagrid('getEditor', {index: rowIndex, field: field});
	            if (ed) {
	                ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
	            }
	        },
	        onDblClickRow: onClickCells,
	        columns: [[
	            {field: 'checkbox', title: '复选框', width: 100, checkbox: true},
	            {field: 'task_id', title: '任务ID', width: 100, hidden: true},
	            {field: 'event_id', title: '任务事件ID', width: 100, hidden: true},
	            {field: 'event_type', title: '事件类型', width: 100,
	                formatter: function (value, row, index) {
	                    var options = this.editor.options.data;
	                    for (var i = 0; i < options.length; i++) {
	                        if (options[i].key == value) {
	                            return options[i].productname
	                        }
	                    }
	                },
	                editor: {
	                    type: 'combobox',
	                    options: {
	                        valueField: 'key',
	                        textField: 'productname',
	                        data: eventTypeOptionsData
	                    }
	                }
	            },
	            {field: 'event_url_id', title: '事件名称', width: 100,
	                formatter: function (value, row, index) {
	                    var options = this.editor.options.data;
	                    for (var i = 0; i < options.length; i++) {
	                        if (options[i].key == value) {
	                            return options[i].productname
	                        }
	                    }
	                },
	                editor: {
	                    type: 'combobox',
	                    options: {
	                        valueField: 'key',
	                        textField: 'productname',
	                        data: urlSelectOptionsData
	                    }
	                }
	            },
	            {field: 'event_param', title: '事件参数', width: 100, editor: {type: 'interParam'},
	            	formatter: function(value,row,index){
		        		 if(!isEmpty(value)){
		        			 return row.event_param;
		        		 }else{
		        			 return "";
		        		 }
		        	 }
	            },
	            {field: 'is_using', title: '启用', width: 100,
	                formatter: function (value, row, index) {
	                    if (value == "1") {
	                        return '<div class="input-bt-Switcher-wrap taskIsUsing"><div class="input-bt-Switcher">' +
	                            '<input type="checkbox" class="taskIsUingCheckBox" rowIndex = "' + index + '" checked/><label></label></div></div>';
	                    } else {
	                        return '<div class="input-bt-Switcher-wrap taskIsUsing"><div class="input-bt-Switcher">' +
	                            '<input type="checkbox" class="taskIsUingCheckBox" rowIndex = "' + index + '" /><label></label></div></div>';
	                    }
	                }
	            }]],
	        onAfterEdit : function(rowIndex, rowData, changes){
	            var rows = dataGrid.datagrid('getRows');
	            dataGrid.datagrid('loadData', rows);
	        }
	    });
    }
    function append() {
        if (endEditing()) {
        	dataGrid.datagrid('clearChecked');
        	var taskId=$form.find(".timingTaskItem .timeTaskContent.active").attr("taskId");
            dataGrid.datagrid('appendRow', {"event_id": uuid(5,16),"task_id": taskId});
            editIndex = dataGrid.datagrid('getRows').length - 1;
            dataGrid.datagrid('selectRow', editIndex)
                .datagrid('beginEdit', editIndex);
        }
    }
   
    $form.find(".datagrid-foot .removeit").on("click", function () {
        removeit();
    });
    form.get().find(".datagrid-foot .append").on("click", function () {
        append();
    });
    $form.find(".datagrid .datagrid-editable").on("click",".datagrid-editable-input",function(){
        $(this).parents(".datagrid-editable").parent("td").click();
    });

    var editIndex = undefined;

    //结束所有行编辑
    function endAllRowEdit(){
        var rows = dataGrid.datagrid('getRows');
        if (isEmpty(rows))
            return true;
        for (var i=0; i<rows.length; i++){
            dataGrid.datagrid('endEdit', $(dataGrid).datagrid('getRowIndex', rows[i]));
        }
        //判断事件类型名称是否为空
        for(var i = 0; i < rows.length; i++){
        	if(isEmpty(rows[i].event_type)){
        		alert("事件类型不能为空！");
        		return false;
        	}
        	if(isEmpty(rows[i].event_url_id)){
        		alert("事件名称不能为空！");
        		return false;
        	}
        }
        return true;
    }
    function endEditing() {
        if (editIndex == undefined) {
            return true
        }
        if (dataGrid.datagrid('validateRow', editIndex)) {
            dataGrid.datagrid('endEdit', editIndex);
            editIndex = undefined;
            return true;
        } else {
            return false;
        }
    }

    function onClickCells(index, field) {
        if (editIndex != index) {
            if (endEditing()) {
                dataGrid.datagrid('selectRow', index)
                    .datagrid('beginEdit', index);
                var ed = dataGrid.datagrid('getEditor', {index: index, field: field});
                if (ed) {
                    ($(ed.target).data('checkbox') ? $(ed.target).textbox('checkbox') : $(ed.target)).focus();
                }
                editIndex = index;
            } else {
                setTimeout(function () {
                    dataGrid.datagrid('selectRow', editIndex);
                }, 0);
            }
        }
    }

    function onEndEdit(index, row) {
        var ed = $(this).datagrid('getEditor', {
            index: index,
            field: 'module'
        });
        row.module = $(ed.target).combobox('getText');
    }

    function removeit() {
        console.log(editIndex);
//        if (editIndex == undefined){return}
        var ss = [];
        var rows = dataGrid.datagrid('getSelections');
        console.log(rows);
        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            var rowIndex = dataGrid.datagrid('getRowIndex', row);
            dataGrid.datagrid('deleteRow', rowIndex);
//				ss.push('<span>'+row.itemid+":"+row.productid+":"+row.attr1+'</span>');
        }
        var re = {
            data: rows,
            leng: rows.length
        };
        return re;
//        editIndex = undefined;
    }

    function reject() {
        dataGrid.datagrid('rejectChanges');
        editIndex = undefined;
    }

    function getChanges() {
        var rows = dataGrid.datagrid('getChanges');
        var delRows = dataGrid.datagrid('getChanges', 'deleted');
        var insRows = dataGrid.datagrid('getChanges', 'inserted');
        var updateRows = dataGrid.datagrid('getChanges', 'updated');

        var totleRows = {
            insert: insRows,
            deleted: delRows,
            updated: updateRows
        };
        return totleRows;
    }
    function uuid(len, radix) {
        var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
            .split('');
        var uuid = [], i;
        radix = radix || chars.length;

        if (len) {
            for (i = 0; i < len; i++)
                uuid[i] = chars[0 | Math.random() * radix];
        } else {
            var r;
            uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
            uuid[14] = '4';
            for (i = 0; i < 36; i++) {
                if (!uuid[i]) {
                    r = 0 | Math.random() * 16;
                    uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
                }
            }
        }
        return uuid.join('');
    }
});

