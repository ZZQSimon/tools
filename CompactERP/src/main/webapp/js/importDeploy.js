registerInit('importDeploy', function (form) { 
	var $form = form.get();
	var curTable='0';
	var columns;
	var tmplateCols;
	var parent="#";
	
	var $tableTree = $form.find('.deploy-left-table-data');
    
	initLeftTree();
    
    //左侧search
    var to;
    var leftSearch = $form.find('.deploy-left-tree-search');

    $form.find(".deploy-left-menu .deploy-left-table-data").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"});
    
    leftSearch.keyup(function () {
        if(to) {
            clearTimeout(to);
        }
        to = setTimeout(function () {
            $tableTree.jstree(true).search(leftSearch.val());
        }, 250);
    });
    
    ajaxLoadGroup(form, callback);
    
    function ajaxLoadGroup(form, callBack){
        postJson('/importDeploy/selectTableDeploy.do', {}, function (result) {
            callBack(result);
        });
    }
    //初始化左侧树
    function initLeftTree(){
        $tableTree.jstree({
            'core' : {
                "multiple" : false,
                'data' : null,
                'dblclick_toggle': true,          //tree的双击展开
                "check_callback" : true        //
            },
            "plugins" : ["search", "contextmenu"]
        });
    }
    
    //左侧输赋值函数
    function callback(result){
    	// console.log(result);
        if(!isEmpty(result))
            if(isEmpty(result)){
                $tableTree.jstree(true).settings.core.data = null;
                $tableTree.jstree(true).refresh();
            }else{
                $tableTree.jstree(true).settings.core.data = result;
                $tableTree.jstree(true).refresh();
            }
    }
    
    
    //树节点点击事件。点击后给table赋值。
    $tableTree.bind("changed.jstree",function(e, data){
    	if(!isEmpty(data.node) && data.node.id != 0 && data.node.id != 1){
    		$form.find('.api_id').val('');  
    		$form.find('.create_trigger').val('');
	    	$form.find('.update_api').val('');
	    	$form.find('.interceptor_service').val('');
	    	$form.find('.charset').val('');
	    	$form.find('.batch_id').val('');
	    	$form.find(".keywork").text(msg("none selected"));
	    	$form.find(".is_insert").prop("checked", false);
	    	$form.find(".is_update").prop("checked", false);
	    	
	    	if($form.find('.empty-form').not(":hidden")){
                $form.find('.empty-form').hide();
			}

    		var tableName = data.node.original.table_id;
    		parent=data.node.original.parent;
    		if(parent!="#"){
    			curTable=tableName;
        		curTreeNode=tableName;
    			//获取配置
        		postJson('/importDeploy/getImportDeploy.do', {'importDeploy':{'table_id':tableName}}, function (result) {
        				initImportPage(result);	
    	        });
    		}
    	}
		
    });
    
   //初始化映射表
    function initImportPage(result){
    	//初始化表字段
		var table = getTableDesc(curTreeNode);
		var $body=$form.find('.mapper-table-body');
		var $keyword=$form.find('.dropdown-menu');
		
		columns=table.columns;
		$body.empty();
		$keyword.empty();
        for(var i=0;i<columns.length;i++){
        	var column_id=table.columns[i].column_name;
        	var $trTemp = $("<tr></tr>");
        	$trTemp.append("<td>"+column_id+" ("+getColumnText(columns[i])+")"+"</td>");
        	$trTemp.append("<td value='"+getColumnText(columns[i])+"'><select class='input-2 "+column_id+"'><option value=''>选择映射字段</option></select></td>");
        	$trTemp.appendTo("#J_TbData");
        	$body.append($trTemp);
        	
        	$keyword.append('<li><input type="checkbox" name="keyword" value="'+column_id+'"/>'+column_id+'('+getColumnText(columns[i])+')</li>');
    	}
        initCheckbox();
        
    	if(result!=null){
	    	var service_param=eval("("+result.service_param+")");
			var column_mapper=eval("("+result.column_mapper+")");
	    	$form.find('.api_id').val(result.statement); 
	    	$form.find('.create_trigger').val(result.create_trigger);
	    	$form.find('.update_api').val(result.update_statement);
	    	$form.find('.interceptor_service').val(result.interceptor_service);
	    	$form.find('.batch_id').val(result.batch_id);
	    	$form.find('.charset').val(service_param.charset);
	    	$form.find(".is_insert").prop("checked", result.is_insert==1?true:false);
	    	$form.find(".is_update").prop("checked", result.is_update==1?true:false);
	    	var $opt;
	    	if(result.is_update==1){
	    		var keywords=result.update_keywords.split()
		    	$.each(keywords,function(k,v){
		    		$opt=$form.find("input[value='"+v+"']");
		    		$opt.iCheck('check');
		    	});	
	    	}

	        // 初始化映射选择框
	        tmplateCols=column_mapper.tmplateCols;
			for(var i=0;i<columns.length;i++){
				var clz="."+columns[i].column_name+"";
				var $select=$form.find(clz);
				for (var value of tmplateCols) {
					value=value.replace(/\s/g, '_');
					var $option = $('<option value='+value+'>'+value+'</option>');
					$select.append($option);
				}
				var mapper=column_mapper.mapper;
				if(typeof(mapper[columns[i].column_name])!="undefined"){
					$select.val(mapper[columns[i].column_name]);
				}
			}

    	}
        
    }
    
    //初始化复选框
    function getKeywords(){
    	var $keyword=$form.find('.dropdown-menu');
		var keywords="";
    	$keyword.find("input[name='keyword']").each(function(k,v){
			if(v.checked) keywords+=this.value+",";
		});
    	return keywords.substring(0,keywords.length-1);
	}
    
    function initCheckbox(){
    	var $keyword=$form.find('.dropdown-menu');
    	$keyword.find("input[name='keyword']").iCheck({
            checkboxClass: 'icheckbox_flat-blue',
            radioClass: 'iradio_flat-blue'
        });
        
        $keyword.find("input[name='keyword']").on('ifUnchecked',function(){
        	var value=getKeywords();
        	var text=isEmpty(value)?msg("none selected"):value;
        	$form.find(".keywork").text(text);
    	});
        $keyword.find("input[name='keyword']").on('ifChecked',function(){
        	var value=getKeywords();
        	var text=isEmpty(value)?msg("none selected"):value;
        	$form.find(".keywork").text(text);
    	});
    }
    
    //上传文件
    $file=$form.find('.upload_context');
    $file.fileupload({
		dataType: 'json ',
		replaceFileInput: true,
		url: makeUrl('/importDeploy/upload.do'),
		formData: function () {
			return [{name: 'id', value: this.fileInput[0].id}];
		},
		autoUpload: true,
		add: function (e, data) {
			data.url = makeUrl('/importDeploy/upload.do');
			progressBox('uploading', data.files[0].name);
			data.submit();
		},
		done: function (e, data) {
			hidePorgressBox();
			tmplateCols=data.result.data;
			if(data.result.success==true && tmplateCols!=null){
				for(var i=0;i<columns.length;i++){
					var clz="."+columns[i].column_name+"";
					var $select=$form.find(clz);
					$select.empty();
					$select.append( $("<option value=''>选择映射字段</option>"));
					for (var value of tmplateCols) {
						value=value.replace(/\s/g, '_');
						var $option = $("<option value="+value+">"+value+"</option>");
						$select.append($option);
					}
					$select.find("option[value='"+$select.parent().attr("value")+"']").attr("selected","selected");
				}
			}else{
				alert(data.result.errorMessage);
			}
		}
	});
    
    //参数输入控件
    $form.find(".parameterInput").on("click",function(){
    	var target=$(this).prev();
    	var josnstr=target.val();
        var paramId="00000000000000000000000000000000000000000000000005";
        var modalX="44";
        var modalY="44"; 
        $(".parameterInput").parameterInputControl(true).setData("onSubmit",function(data){
        	target.val(data);
        },josnstr,paramId,modalX,modalY);

    });

    //保存
    $form.find('.deploy-import-submit').on("click",function(){
    	if(curTable=='0' || parent=="#"){
    		alert(msg("Please select a table!"));
    	}else{
    		var column_mapper=JSON.stringify(buildColumnMpper());
        	var statement=$form.find('.api_id').val();
        	var create_trigger=$form.find('.create_trigger').val();
        	var update_statement=$form.find('.update_api').val();
        	var batch_id=$form.find('.batch_id').val();
        	var interceptor_service='csv-import';
        	var charset=$form.find('.charset').val();
        	var service_param=toJSON({table:curTable, column:'file', charset:charset, tmpId: '_id'});
        	var update_keywords=getKeywords();
        	var is_insert=$form.find('.is_insert').is(':checked')?1:0;
        	var is_update=$form.find('.is_update').is(':checked')?1:0;
        	var data={'importDeploy':{'table_id':curTable,'statement':statement,'interceptor_service':interceptor_service,'service_param':service_param,'column_mapper':column_mapper,'batch_id':batch_id,create_trigger:create_trigger,update_statement:update_statement,is_insert:is_insert,is_update:is_update,update_keywords:update_keywords}};
        	
        	postJson('/importDeploy/saveImportDeploy.do', data, function (result) {
                dxToastAlert(result);
            });
    	}
    });
    
    //校验模板列名是否包含空格、或重复映射
  /*  function check(){
    	for(var i=0;i<columns.length;i++){
    		var key=columns[i].column_name;
			var clz="."+key+"";
			var $select=$form.find(clz);
			var value=$select.val();
			if(value!=""){
				//mapper=addProperty(mapper,key,value);
				
			}
		}
    }*/
    
    
    function buildColumnMpper(){
    	var column_mapper={};
    	var mapper={};
    	for(var i=0;i<columns.length;i++){
    		var key=columns[i].column_name;
			var clz="."+key+"";
			var $select=$form.find(clz);
			var value=$select.val();
			if(value!=""){
				mapper=addProperty(mapper,key,value);
			}
		}
    	column_mapper=addProperty(column_mapper,"mapper",mapper);
    	column_mapper=addProperty(column_mapper,"tmplateCols",tmplateCols);
    	
    	return column_mapper;
    }
    
    function addProperty(data,key,val){
         data[key] = val;
    	 //data+=key+":'"+val+"',";
        return data;
    }
    
    $form.find(".deploy-file-upload").on("click",function(){
    	//document.getElementById('upload_context').click();
    	if(curTable=='0'){
    		alert(msg("Please select a table!"));
    	}else{
    		$form.find('.upload_context').click();
    	}
    	
    });
});
