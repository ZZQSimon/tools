

/**
 * init data grid
 *
 * @param form
 */
function initMobileGrid(form) {
	var $form=form.get();
	var grid=w(form.children[0].id);
	var table=getTableDesc(grid.table);
	
	if (table.auto_gen_sql){
		grid.autoGen = function (form) {
			$("#"+grid.id).empty();
	         var param = buildSQLParam(form, table.auto_gen_sql);
	         postJson('/widget/grid/auto.do', {id: grid.id, param: param}, function () {
	        	var postData = {id: form.children[0].id};
	     	    postJson('/widget/grid/list.do', postData, function (records) {
	     	    	 for(var key in records){
	     	    		 data = buildRequestData(records,records[key].id,grid.table);
	     	    		 data.parent=records[key].id;
	     	    		 data.readonly = false;
	     	    		 getSimpleForm("/detail/edit.view",data,null,null,records[key].id,true);
	     	    		 $(".add-detail").css("display","none");
	     	    	 }
	     	     });
	     	   
	         });
	     }
	}
	
	$(".add-detail").click(function(){
		var url="/detail/create.view";
		var data={parent:grid.id,table:grid.table};
    	getSimpleForm("/detail/create.view", data);
    	$(this).css("display","none");
    });
	
	if(form.action=="view"){
		initChildRecord(form,true);
		/*var postData = {id: form.children[0].id};
	    postJson('/widget/grid/list.do', postData, function (records) {
	    	 for(var key in records){
	    		 data = buildRequestData(records,records[key].id,grid.table);
	    		 data.readonly = true;
	    		 data.hasNext = false;
	    		 data.parent=grid.id;
	    		 getSimpleForm("/detail/edit.view",data,"","",records[key].id);
	    		 $(".add-detail").css("display","none");
	    	 }
	     });*/
	}else if(form.action=="create"){
		$(".row-blue-btn").css("display","block");
	}
	
	function initChildRecord(form,readonly){
		var postData = {id: form.children[0].id};
	    postJson('/widget/grid/list.do', postData, function (records) {
	    	 for(var key in records){
	    		 data = buildRequestData(records,records[key].id,grid.table);
	    		 data.readonly = readonly;
	    		 data.hasNext = false;
	    		 data.parent=grid.id;
	    		 getSimpleForm("/detail/edit.view",data,"","",records[key].id);
	    		 $(".add-detail").css("display","none");
	    	 }
	     });
	}
	
    function refreshSeq(data){
    	 $("#"+data.parent).find(".record-head").each(function(key,value){
    		 var seq=key+1;
    		 var name=i18n(dx.table[data.table]["i18n"]);
    		 $(this).find(".title-seq").remove();
    		 $(this).prepend('<span class="pull-left  title-seq">'+name+'('+seq+')</span>');
    	 });
     }
	 function getSimpleForm(url, data, postInit, ownerId,recodeId,sync){
		 if (typeof postInit === 'string') {
		        ownerId = postInit;
		        postInit = null;
		    }
	
		    if (!data)
		        data = {};
		    postPage(url, data, function (result) {
		        
		        var $child = $("#"+$('.mobile-child')[0].id);
		        var curId=$(result)[0].data.replace("cache form:","");
		        
		        $(".add-record").remove();
		        result='<div class="clild-record"><div class="record-head"><div class="pull-left add-record">新增</div><div class="pull-right delete-record">删除</div></div>'+result+'<div class="unfurled">查看更多<i class="fa fa-angle-double-down" aria-hidden="true"></i></div></div>';
		        $child.append($(result));
		        refreshSeq(data);
		        var $page=$("#"+curId);
		        //$page.find(".approve-flow-add-point").remove();
		        $page.find(".gm-top-menu").remove();
		        $page.find(".btn-toolbar").css("display","none");
		        if($page.find(".dx-detail-row").length<2){
		        	$page.next().css("display","none");
		        }
		 
		        var form = getFormModel(curId);
		        
		        if(form.action=="view"){
		        	$page.parents(".clild-record").find(".record-head").css("display","none");
		        	$(".unfurled").css("display","none");
		        }else if(form.action=="edit"){
		        	$(".moble-edit").addClass("mobile-create");
		     	    $(".moble-edit").removeClass("moble-edit");
		        }
		        
		        var table=dx.table[form.tableName];

		        buildFormCache(form, form.widgets);
		        if (dx.init[form.widgetName])
		            dx.init[form.widgetName](form, "");
		        else
		            console.warn('no init function registered for "' + form.widgetName + '"');
		        if (postInit)
		            postInit(form, $li);
		        if (dx.moduleInit[form.widgetName])
		            dx.moduleInit[form.widgetName].forEach(function (func) {
		                func(form, "");
		            });
		        doAutoExpand(form);


                $('html').scrollTop($('.moblie-container')[0].scrollHeight);
		        
		        var flag=0;
		        $page.parent().find(".unfurled").click(function(){
		        	if(flag==0 && form.action!="view"){
		        		$(this).html('收起<i class="fa fa-angle-double-up" aria-hidden="true"></i>');
		        		flag=1;
		        	}else if(flag==1 && form.action!="view"){
		        		$(this).html('查看更多<i class="fa fa-angle-double-down" aria-hidden="true"></i>');
		        		flag=0;
		        	}
		        	
		        });
		        
		        $page.parent().find(".add-record").click(function(){
					getSimpleForm("/detail/create.view", data);
		        });
		        
		        
		        $page.parent().find(".delete-record").click(function(){
		        	var $record=$(this).parents(".clild-record");
		        	var $add=$record.find(".add-record");
		        	var form=getFormModel($record.find(".dx-form").attr("id"));
		        	
		        	if($add.length!=0){
		        		var $prev=$record.prev();
		        		if($prev.length!=0){
		        			$prev.find(".record-head").prepend($add);
		        			if(form.action=="edit"){
		        				deleteRecord(data.parent,recodeId);
		        			}
		        			$record.remove();
		        		}else{
		        			$(".add-detail").css("display","block");
		        			if(form.action=="edit"){
			        			deleteRecord(data.parent,recodeId);
			    			}
		        			$record.remove();
		        		}

		        	}else{
		        		if(form.action=="edit"){
		        			deleteRecord(data.parent,recodeId);
		    			}
		        		$record.remove();
		        	}
		        	refreshSeq(data);
		        });
		        
		        function deleteRecord(id,recodeId){
		       	 var rowIds=[];
		       	 rowIds.push(recodeId);
		       	 var data={id: grid.id,ids: rowIds}
		       	 postJson('/widget/grid/delete.do',data,function(){
		       		 
		       	 });
		        }
		    },null,null,sync);
		}
	
}

function buildRequestData(records,recordId,table) {
    var record;
    for (var i=0; i<records.length; i++){
        if (records[i].id == recordId){
            record = records[i];
            break;
        }
    }
    var data = {};
    $.each(record.fields, function (key, field) {
        data[record.fields[key].column] = record.fields[key].value;
    });
    return { param: data, table: table};
};




