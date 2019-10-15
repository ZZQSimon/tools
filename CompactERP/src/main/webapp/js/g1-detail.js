/**
 * Created with IntelliJ IDEA.
 * User: zang.loo
 * Date: 9/30/14
 * Time: 1:45 PM
 */

"use strict";

registerInit('detail', function (form, ignore$li) {
    function fixedData() {
        if (!form.filter)
            return undefined;
        if (!form.filter.fixedIds)
            return undefined;
        var ret = {};
        form.filter.fixedIds.forEach(function (id) {
            var field = w(id);
            ret[getDesc(field).column_name] = field.val();
        });
        return ret;
    }
	var $form = form.get();
	
	if(dx.user!=null && dx.user.isMobileLogin==1 ){
		initAddApprover();
		initMobileStyle();
		//手机端查看更多记录
		$form.find(".dx-more-record").click(function(){
			var pageId=$(this).attr("name");
			
			var page = getPageDesc(pageId);
		    if (!page.url)
		        return;
		    var data;
		    if (page.param)
		        data = evaluate('(' + page.param + ')', null);
		    else
		        data = {};
		    data.menuId = pageId;
		    newMobilePage("/list/mobile/table.view", data, pageId);
		});
		
		//初始化用户列表
		$form.find(".add-approver").click(function(){
			openUserListDialog(form);
	    });
		
		//当点击其他字段是关闭select弹层
		$form.find(".dx-field").click(function(){
			$(".close-select").click();
		});
	}

	hiddenMenuBar();
	function hiddenMenuBar(){
		if(dx.user.isMobileLogin==1){
			var children=$form.find(".dx-menu-bar").children();
			if(children.length==0) $form.find(".dx-menu-bar").css("display","none");
		}
	}
	
	
    function buildApprove($form){
        //收回
        $form.find('.approve-back').on('click', function () {
            dx.processing.open();
            postJson('/approve/approveBack.do', {approveId: form.approveFlow.approve_id,
                tableId: form.approveFlow.table_id, dataId: form.approveFlow.data_id}, function (result) {
                dxToastAlert(msg(result));
                approveNewForm();
                dx.processing.close();
            })
        });
        //审批
        $form.find('.approve-button').on('click', function () {
        	//$(this).attr("disabled","disabled");
            var approveId = $form.find('.approve-button-param').attr('approveId');
            var blockId = $form.find('.approve-button-param').attr('blockId');
            var belongBlock = $form.find('.approve-button-param').attr('belongBlock');
            var approveReason = $form.find('.approveReason').val();
            var table = getTableDesc(form.tableName);
            var dataId = form.columns[table.id_column].orig;
            var event_type=$(this).attr("event_type");
            var buttonEvent;
            var event_id = $(this).attr('event_id');
            var self=this;
            if (!isEmpty(belongBlock)){
                if (isEmpty(table.approveButtonEvent) || isEmpty(table.approveButtonEvent[belongBlock])
                    || isEmpty(table.approveButtonEvent[belongBlock][event_id])){
                    alert(msg('data is wrong'));
                    return;
                }
                buttonEvent = table.approveButtonEvent[belongBlock][event_id];
            }else{
                if (isEmpty(table.approveButtonEvent) || isEmpty(table.approveButtonEvent[blockId])
                        || isEmpty(table.approveButtonEvent[blockId][event_id])){
                    alert(msg('data is wrong'));
                    return;
                }
                buttonEvent = table.approveButtonEvent[blockId][event_id];
            }
            //审批按钮事件
            var requestButtonEvent;
            requestButtonEvent = buildBlockEventParam(buttonEvent, form);
            //审批块事件
            var blockEvent, requestBlockEvent = {};
            if (!isEmpty(table.approveBlockEvent))
                if (!isEmpty(belongBlock)){
                    blockEvent = table.approveBlockEvent[belongBlock];
                }else{
                    blockEvent = table.approveBlockEvent[blockId];
                }
            if (!isEmpty(blockEvent))
                for (var key in blockEvent){
                    requestBlockEvent[key] = buildBlockEventParam(blockEvent[key], form);
                }
            if(checkApproveButtonEvent(requestButtonEvent,form)){
            	 dx.processing.open();
                 postJson('/approve/approve.do',
                     {approveId : approveId, blockId : blockId, tableId : form.tableName, approveReason : approveReason, dataId : dataId,
                         flowEvent : requestButtonEvent, approveBlockEvent : requestBlockEvent, approveEvent : null,formId : form.id}, function(result){
                         if(dx.user.isMobileLogin==1 && event_type=="workEvent"){
                         	$(self).attr("disabled","disabled");
                         }else{
                         	dxToastAlert(msg(result));
                             approveNewForm();
                             approveAfterNewPage(requestBlockEvent);
                         }
                         dx.processing.close();
                     });
            }
        });
        $form.find(".mobile-approve-button").click(function(){
        	$form.find(".main-view").css("display","none");
        	var event_type=$(this).attr("event_type");
        	if(event_type=="work-events"){
        		$form.find(".work-model").css("display","block");
        	}else{
            	$form.find(".approve-button").attr("event_id",$(this).attr('event_id'));
            	$form.find(".approve-button").attr("event_type",$(this).attr('event_type'));
            	$form.find(".reason-model").css("display","block");
        	}	
        });
        
        $form.find(".cancle-work-model").bind("click",function(event){
        	$form.find(".main-view").css("display","block");
        	$form.find(".work-model").css("display","none");
        });
        
        //催办
        $form.find(".m-approve-cuiban").click(function(){
        	$(this).attr("disabled","disabled");
        	var approveFlow=form.approveFlow
        	var req={approveId:approveFlow.approve_id,dataId:approveFlow.data_id,tableId:approveFlow.table_id};
        	postJson('/approve/cuibanApprove.do',req,function(result){
                alert(msg('operation success'));
        	});
        });
        
        /*$form.find(".m-approve-submit").click(function(){
        	//$form.find(".main-view").css("display","block");
        	$(".main-view").css("display","block");
        	$form.find(".submit-approve").css("display","none");
        	
        	var $point=$form.find(".submit-approve .approve-flow-add-point-div");
        	var approveId = $point.attr('approveId');
            var blockId = $point.attr('blockId');
            var approveReason = $form.find('.submit-approve .approveReason').val();
            var table = getTableDesc(form.tableName);
            var dataId = form.columns[table.id_column].orig;
            var type = $point.attr('approve_type');
            var flowEvent = buildBlockEventParam(table, blockId, type, form);
            
            var url="/approve/approvePass.do";
            if(type=="reject") url="/approve/approveReject.do";
            postJson(url,
                {approveId : approveId, blockId : blockId, tableId : form.tableName,
                    approveReason : approveReason, dataId : dataId, flowEvent : flowEvent}, function(result){
                    $.toast();
                    approveNewForm();
                    approveAfterNewPage(flowEvent);
                });
        });*/
    }

    //提交
    $form.find('.g1-approve-submit').on('click', function () {
        dx.processing.open();
        var data = {tableId: form.tableName};
        var param = {};
        $.each(form.fieldIds, function (i, fid) {
            var field = w(fid);
            param[field.id] = field.val();
        });
        data.param = [param];
        var flowEvent = {};
        flowEvent = buildBlockEventParam(table.submitEvent, form);
        data.flowEvent = flowEvent;
        postJson('/approve/approveSubmit.do', data, function (result) {
            dxToastAlert(msg(result));
            approveNewForm();
            dx.processing.close();
        })
    });
  
    function approveNewForm(){
        var table = getTableDesc(form.tableName);
        var param = {};
        param[table.id_column] = form.columns[table.id_column].orig;
        postPage('/detail/edit.view', {table : form.tableName, param : param,
            readonly : true, parent : form.parent, isIndexView : form.isIndexView}, function (result) {
            closeForm(form.id, function () {
                //remove original form , and replace with the result form
                var $form = $('#' + form.id);
                var $div = $form.parent();
                $form.remove();
                $div.append(result);

                // init new form
                var newForm = getFormModel($div.find('.dx-form').attr('id'));
                buildFormCache(newForm, newForm.widgets);
                dx.init.detail(newForm);

                //编辑或新增保存
                newForm.submit = form.submit;
                newForm.parentShow = form.parentShow;
                //返回按钮点击事件
                $div.find('input.dx-back').on("click", function () {
                    form.parentShow();
                });
                newForm.p('updated', true);
            });
        });
        if(dx.user!=null && dx.user.isMobileLogin!=1)
        	dx.reloadIndexApprove();
    }
	function next() {
		var data, batchData;
		if (form.action === 'create')
			data = {id: form.id};
		else {
			batchData = form.p('batchData');
			data = batchData[0];
			data.id = form.id;
		}
        var fd = fixedData();
        data.table = form.tableName;
        data.fixedData = fd;
        var parentId = {};
        if (!isEmpty(table.parent_id)){
            var parentTable = getTableDesc(table.parent_id);
            var parentForm = w(form.parent).parentForm;
            parentId[parentTable.idColumns[0]] = parentForm.columns[parentTable.idColumns[0]].value;
            //parentId = form.columns[table.idColumns[0]].value;
        }
        data.parentId = parentId;
		// next button with different url for some modules
		postPage(form.nextUrl ? form.nextUrl : '/detail/next.view', data, function (result) {
			closeForm(form.id, function () {
				//remove original form , and replace with the result form
				var $form = $('#' + form.id);
				var $div = $form.parent();
				$form.remove();
				$div.append(result);
                if (form.isChild){
                    form.nextLoadCallback = function () {
                        // init new form
                        var newForm = getFormModel($div.find('.dx-form').attr('id'));
                        buildFormCache(newForm, newForm.widgets);
                        dx.init.detail(newForm);

                        newForm.spliceBatchData(batchData);
                        newForm.p('updated', true);

                        //编辑或新增保存
                        newForm.submit = form.submit;
                        newForm.parentShow = form.parentShow;
                        if (form.isIndexView == 1){
                            $div.find('input.dx-back').hide();
                            newForm.isIndexView = 1;
                        }
                        newForm.filter = form.filter;
                        newForm.isChild = form.isChild;
                        //子表新增并下一条隐藏返回按钮。
                        if (newForm.isChild){
                            newForm.get().find('input.dx-back').hide();
                            newForm.next = form.next;
                        }
                        //返回按钮点击事件
                        newForm.get().find('input.dx-back').on("click", function () {
                            form.parentShow();
                        });
                        dx.ajax = 0;
                    };
                }else{
                    // init new form
                    var newForm = getFormModel($div.find('.dx-form').attr('id'));
                    buildFormCache(newForm, newForm.widgets);
                    dx.init.detail(newForm);

                    newForm.spliceBatchData(batchData);
                    newForm.p('updated', true);

                    //编辑或新增保存
                    newForm.submit = form.submit;
                    newForm.parentShow = form.parentShow;
                    if (form.isIndexView == 1){
                        $div.find('input.dx-back').hide();
                        newForm.isIndexView = 1;
                    }
                    newForm.filter = form.filter;
                    newForm.isChild = form.isChild;
                    //子表新增并下一条隐藏返回按钮。
                    if (newForm.isChild){
                        newForm.get().find('input.dx-back').hide();
                        newForm.next = form.next;
                    }
                    //返回按钮点击事件
                    newForm.get().find('input.dx-back').on("click", function () {
                        form.parentShow();
                    });
                    dx.ajax = 0;
                }
                if (form.isChild){
                    form.next();
                }
            });
		});
	}

	
	var table = getTableDesc(form.tableName);
	if (table.viewStyle && table.viewStyle.tab) {
		var bg = table.viewStyle.tab.background;
		//$li.css("cssText", "background-color: " + bg + "!important;");
		$form.css('background-color', bg);
		$form.find('.dx-detail-container').css("background-color", bg);
	}
    buildApprove($form);

    $form.find('.approve-flow-add-point-' + form.id).on("click",".approve-add-selected .item-remove",function () {
		$(this).parent().remove();
    });
    //展开更多驳回记录
    $form.find('.moreRejectRecord').on('click', function () {
    	if($(this).hasClass("open")){
    		$(this).text("展开更多");
       	 	$form.find(".approve-hidden-section").hide();
       	 	$(this).removeClass("open");
    	}else{
    		$(this).text("收起");
       	 	$form.find(".approve-hidden-section").show();   
          	 $(this).addClass("open"); 		
    	}
    });
    //收回
    $form.find('.takeBack').on('click', function () {
    	var approveId = $(this).attr('approveId');
    	var block_id = $(this).attr('block_id');
    	var userId = $(this).attr('userId');
        var tableId = form.approveFlow.table_id;
        var dataId = form.approveFlow.data_id;
    	var nextBlockId;
        for (var i=0; i<form.approveFlow.approveFlowNodes.length; i++){
            if (form.approveFlow.approveFlowNodes[i].block_id == block_id){
                if (i != form.approveFlow.approveFlowNodes.length - 1)
                    nextBlockId = form.approveFlow.approveFlowNodes[i + 1].block_id;
            }
        }
    	postJson('/approve/takeBackApprove.do',{approveId : approveId, user_Id:userId, blockId:block_id,
                nextBlockId:nextBlockId, dataId: dataId, tableId: tableId},
            	function(result){
    			dxToastAlert(msg("takeBack success"));
    	            approveNewForm();
            });
    	
    });
    
    //加签
    $form.find('.approve-flow-add-point-' + form.id).find('.approve-add-point-save').on('click', function () {
        var approveId = $('.approve-flow-add-point-div').attr('approveId');
        var node_seq= $('.approve-flow-add-point-div').attr('node_seq');
        var span=$(".approve-add-selected").find("span.addApprove_span");
        var userId={};
        var blockId = $(".approve-flow-add-point-div").attr('blockId');
        var table = getTableDesc(form.tableName);
        var addApproveCount= $(".approve-add-point").attr("addApproveCount");
        var isUpdateAddApprove= $(".approve-add-point").attr("isUpdateAddApprove");
        var nextBlockId="";
        if(!isEmpty(isUpdateAddApprove)){
        	 var approveNodes=form.approveFlow.approveFlowNodes;
             for(var i=0;i<approveNodes.length;i++){
             	if(i==approveNodes.length-1){
             		nextBlockId="isUpdateAddApprove";
             		break;
             	}
             	if(approveNodes[i].block_id==blockId){
             		nextBlockId=approveNodes[i+1].block_id;
             		break;
             	}
             }
        }
        if(span.length>0){
        	if(span.length>addApproveCount){
            	alert("只允许加签"+addApproveCount+"人!");
            	return false;
            }
        	for(var i=0;i<span.length;i++){
        		userId[$(span[i]).attr("selectValue")]=uuid(10,16);
            }
        	postJson('/approve/addApprove.do',{approveId : approveId,userId:userId,blockId:blockId,tableId:form.tableName,
        		node_seq:node_seq,nextBlockId:nextBlockId}, 
                	function(result){
        				dxToastAlert(msg("add success"));
        	            approveNewForm();
                });
        }else{
        	postJson('/approve/addApprove.do',{approveId : approveId,userId:userId,blockId:blockId,tableId:form.tableName,
        		node_seq:node_seq,nextBlockId:nextBlockId}, 
                	function(result){
        				dxToastAlert(msg("add success"));
        	            approveNewForm();
                });
        }
    });
    //初始化树
    function initLeftTree() {
        $tableTree.jstree({
            'core': {
                "multiple": false,
                'data': null,
                'dblclick_toggle': true,          //tree的双击展开
                "check_callback": true        //
            },
            "plugins": ["search"]
        });
    }
    //左侧输赋值函数
    function callback(result) {
        if (!isEmpty(result)) {
            if (isEmpty(result)) {
                $tableTree.jstree(true).settings.core.data = null;
                $tableTree.jstree(true).refresh();
            } else {
                if (!isEmpty($tableTree.jstree(true).settings)){
                    $tableTree.jstree(true).settings.core.data = result;
                    $tableTree.jstree(true).refresh();
                }
            }
        }
    }
    if(dx.user!=null && dx.user.isMobileLogin!=1){
        var $tableTree = form.get().find('.deptUserTree');
        //加签弹出树
        $form.find(".approve-add-point-wrap .approve-add-toggle-btn").on("click",function () {
            if (!isEmpty($tableTree) && $tableTree.length != 0){
                initLeftTree();
                //查询加签树的数据
                postJson('/approve/initAddApproveTree.do', {}, function (result) {
                    callback(result);
                });
            }
    		if($(this).parent().find(".g1-dropdown-menu").is(":hidden")){
                $(this).parent().find(".g1-dropdown-menu").show();
    		}else {
                $(this).parent().find(".g1-dropdown-menu").hide();
    		}
        });
        $form.find('.approve-flow-add-point-' + form.id).on('hide.bs.modal', function (e) {
            $(this).find(".g1-dropdown-menu").hide();
        });
       $form.find('.approve-flow-add-point-' + form.id).on('show.bs.modal', function (e) {
    	   $(".approve-add-selected").find("span.addApprove_span").remove();
    	   if(!isEmpty(form.approveFlow)){
    		   var approveNodes=form.approveFlow.approveFlowNodes;
        	   for(var i=0;i<approveNodes.length;i++){
        		   if(approveNodes[i].is_addApproveNode==1){
        			   var addApproveUsers= approveNodes[i].approveFlowUsers;
        			   if(!isEmpty(addApproveUsers)){
        				   for(var j=0;j<addApproveUsers.length;j++){
        					   var user = addApproveUsers[j].user;
        					   $(".dropdown.approve-add-point-wrap").before('<span class="addApprove_span" selectValue="'+user.id+'" selectText="'+user.name+'">'+user.name+'<span class="item-remove">×</span></span>');
        				   }
        			   }
        		   }
        	   }
    	   }
        });
        //左侧search
        var to;
        var leftSearch = form.get().find('.approve-add-point-select');
        leftSearch.keyup(function () {
            if (to) {
                clearTimeout(to);
            }
            to = setTimeout(function () {
                $tableTree.jstree(true).search(leftSearch.val());
            }, 250);
        });
        //树节点点击事件。点击后赋值。
        $tableTree.bind("changed.jstree", function (e, data) {
        	if (!isEmpty(data) && !isEmpty(data.node)) {
        		if(data.node.children.length<=0 && !isEmpty(data.node.data)){
        			var user=data.node.data;
        			var selectValue=user.id;
        	        var selectText=user.name;
        	        var span=$(".approve-add-selected").find("span.addApprove_span");
        	        var flag=true;
        	        if(!isEmpty(form.approveFlow)){
        	        	var approveNodes=form.approveFlow.approveFlowNodes;
            	        var approveUser=[];
            	        for(var i=0;i<approveNodes.length;i++){
            	        	var approveUsers=approveNodes[i].approveFlowUsers;
            	        	for(var j=0;j<approveUsers.length;j++){
            	        		approveUser.push(approveUsers[j]);
            	        	}
            	        }
            	        for(var i=0;i<approveUser.length;i++){
            	        	if(approveUser[i].userId==selectValue){
            	        		dxToastAlert("该审批人已存在！");
            	        		flag=false;
            	        		break;
            	        	}
            	        }
        	        }
        	        for(var i=0;i<span.length;i++){
        	        	if($(span[i]).attr("selectValue")==selectValue){
        	        		dxToastAlert("已选择该审批人！");
        	        		flag=false;
        	        		break;
        	        	}
        	        }
        	        if(flag){
        	        	$(".dropdown.approve-add-point-wrap").before('<span class="addApprove_span" selectValue="'+selectValue+'" selectText="'+selectText+'">'+selectText+'<span class="item-remove">×</span></span>');
        	        }

                    $form.find(".approve-add-selected .g1-dropdown-menu").hide();

        		}
        	}
        });
    }
    
  //加签弹出框操作
//    $form.find('.approve-flow-add-point .approve-add-point-select').on("change",function () {
//        var selectValue=$(this).find("option:selected").val();
//        var selectText=$(this).find("option:selected").html();
//        var span=$(".approve-add-selected").find("span.addApprove_span");
//        var flag=true;
//        for(var i=0;i<span.length;i++){
//        	if($(span[i]).attr("selectValue")==selectValue){
//        		alert("已选择该审批人！");
//        		flag=false;
//        		break;
//        	}
//        }
//        if(flag){
//        	 $(".approve-add-selected").append('<span class="addApprove_span" selectValue="'+selectValue+'" selectText="'+selectText+'">'+selectText+'<span class="item-remove">×</span></span>');
//        }
//    });
    //上一条，下一条按钮点击事件
    var beforeRow, afterRow, lastPage;
    if (form.isOutPage != 1){
        if (!isEmpty(form.beforeAfterRows) && !isEmpty(form.beforeAfterRows.rows)){
            var rows = form.beforeAfterRows.rows;
            if (form.beforeAfterRows.indexGrid){
                for (var i=0; i<rows.length; i++){
                    if (rows[i].rowid == form.beforeAfterRows.rowid){
                        if (i == 0 && rows.length != 1){
                            form.get().find('.detail-before-after-data.after').show();
                            afterRow = rows[1];
                        }else if (i == rows.length - 1 && rows.length != 1){
                            form.get().find('.detail-before-after-data.before').show();
                            beforeRow = rows[rows.length - 2];
                        }else if (rows.length != 1){
                            form.get().find('.detail-before-after-data').show();
                            beforeRow = rows[i - 1];
                            afterRow = rows[i + 1];
                        }
                    }
                }
            }else{
                lastPage = Math.ceil(form.beforeAfterRows.total / form.beforeAfterRows.pageSize);
                for (var i=0; i<rows.length; i++){
                    if (rows[i].rowid == form.beforeAfterRows.rowid){
                        if (i == 0 && form.beforeAfterRows.pageNumber == 1 && rows.length != 1){
                            form.get().find('.detail-before-after-data.after').show();
                            afterRow = rows[1];
                        }else if (i == rows.length - 1 && form.beforeAfterRows.pageNumber == lastPage && rows.length != 1){
                            form.get().find('.detail-before-after-data.before').show();
                            beforeRow = rows[rows.length - 2];
                        }else if (rows.length != 1){
                            form.get().find('.detail-before-after-data').show();
                            beforeRow = rows[i - 1];
                            afterRow = rows[i + 1];
                        }else if (rows.length == 1){
                            if (form.beforeAfterRows.pageNumber != 1){
                                form.get().find('.detail-before-after-data.before').show();
                            }
                        }
                    }
                }
            }
        }
    }
    function beforeAfterData(rows){
        var readonly = true;
        if (!rows) {
            messageBox('noRowSelected');
            return;
        }
        // set edit request parameter list
        var batchData = [];
        var item;
        var data = {};
        if (rows.indexGrid)
            data = rows.ref;
        else
            $.each(rows.rawData.columns, function (key, field) {
                data[key] = field.value;
            });
        item = {parent: rows.rowid, param: data, table: form.tableName};
        if (rows.indexGrid){
            item.isIndexView = 1;
            item.parent = null;
        }
        if (rows.indexApprove){
            item.table = rows.table_id;
        }
        item.fixedData = form.beforeAfterRows.fixedData;
        item.readonly = readonly;
        item.hasNext = false;
        batchData.push(item);
        // last edit form in list has no next button
        batchData[batchData.length - 1].hasNext = false;

        postPage('/detail/edit.view', batchData[0], function (result) {
            var $div = form.get().parent();
            $div.empty();
            $div.append(result);
            // init new form
            var newForm = getFormModel($div.find('.dx-form').attr('id'));
            //编辑或新增保存
            newForm.submit = form.submit;
            newForm.parentShow = form.parentShow;

            //上一条，下一条需要的参数。
            newForm.beforeAfterRows = form.beforeAfterRows;
            newForm.beforeAfterRows.rowid = rows.rowid;
            buildFormCache(newForm, newForm.widgets, true);
            dx.init.detail(newForm);

            newForm.p('batchData', batchData);
            newForm.p('updated', true);

            //返回按钮点击事件
            $div.find('input.dx-back').on("click", function () {
                newForm.submit();
            });
        });
    }
    form.get().find('.detail-before-after-data').on('click', function () {
        if ($(this).hasClass('before')){
            if (isEmpty(beforeRow)){
                form.beforeAfterRows.beforeAfterPage(form.beforeAfterRows.pageNumber - 1, function (newRows) {
                    form.beforeAfterRows.rows = newRows;
                    form.beforeAfterRows.pageNumber = form.beforeAfterRows.pageNumber - 1;
                    form.beforeAfterRows.rowid = newRows[newRows.length - 1].rowid;
                    beforeAfterData(newRows[newRows.length - 1]);
                });
            }else {
                beforeAfterData(beforeRow);
            }
        }else {
            if (isEmpty(afterRow)){
                form.beforeAfterRows.beforeAfterPage(form.beforeAfterRows.pageNumber + 1, function (newRows) {
                    form.beforeAfterRows.rows = newRows;
                    form.beforeAfterRows.pageNumber = form.beforeAfterRows.pageNumber + 1;
                    form.beforeAfterRows.rowid = newRows[newRows.length - 1].rowid;
                    beforeAfterData(newRows[0]);
                });
            }else {
                beforeAfterData(afterRow);
            }
        }
    });

    if (!isEmpty(form.p))
	form.p('savedHandler', function (result) {
        //首页进的查看页面。保存后不关闭页面。
        if (!isEmpty(result) && result.isIndexView == 1){
            var data = {table : result.table, param : result.param,
                readonly : true, isIndexView : result.isIndexView};
            if (result.url == '/detail/userEdit.view'){
                data.param = null;
            }
            postPage(!isEmpty(result.url) ? result.url : '/detail/edit.view', data, function (result) {
                closeForm(form.id, function () {
                    //remove original form , and replace with the result form
                    var $form = $('#' + form.id);
                    var $div = $form.parent();
                    $form.remove();
                    $div.append(result);

                    // init new form
                    var newForm = getFormModel($div.find('.dx-form').attr('id'));
                    newForm.beforeAfterRows = form.beforeAfterRows;
                    buildFormCache(newForm, newForm.widgets);
                    dx.init.detail(newForm);
                    newForm.p('updated', true);
                });
            });
        }else if (form.isOutPage == 1){  //外部页面
            alert(msg('save success'));
            postPage('/detail/edit.view', {table : form.tableName, param : result,
                readonly : true}, function (result) {
                closeForm(form.id, function () {
                    //remove original form , and replace with the result form
                    var $form = $('#' + form.id);
                    var $div = $form.parent();
                    $form.remove();
                    $div.append(result);
                    // init new form
                    var newForm = getFormModel($div.find('.dx-form').attr('id'));
                    newForm.isOutPage = 1;
                    //$div.find('.dx-form').find('.dx-operation').hide();
                    $div.find('input.dx-back').hide();
                    buildFormCache(newForm, newForm.widgets);
                    dx.init.detail(newForm);

                    newForm.p('updated', true);
                });
            });
        }else {
        	if(dx.user.isMobileLogin==1){
        		var isClose=$("#"+form.id).find(".dx-submit").attr("isClose");
        		if(isClose==undefined){
        			newMobilePage("/list/mobile/table.view", {table:form.tableName});
        		}
        	}else{
        		// it's edit form, and save data
                if (form.parent) {
                    // result.data will be key's values map
                    form.p('updated', {table: form.tableName, keys: result});
                    if (form.submit) //不新开tab页。
                        form.submit();
                    else             //新开tab页。
                        closeTab(findFormLi(form.id));
                }
        	}
        }
	});

	form.spliceBatchData = function (batchData) {
		// remove first item from edit parameter list, and set to new form
		if (batchData) {
			batchData.splice(0, 1);
			if (batchData.length === 0)
				batchData = null;
			form.p('batchData', batchData);
		}
	};
    if(form.action == "view"){
        // $form.find(".dx-editor-input-textarea").summernote('disable');
        initEditorInput(form,"disable");
        $form.find(".dx-text-area-textarea").attr('readonly', true);
    }
    
	initSubmit($form, function (result, isMobile) {
        //手机端审批按钮点击。隐藏同意按钮与审批流。
        if (isMobile){
            $form.find('.mobile-approve-button').hide();
            $form.find('.approve-line-ways-article').hide();
            $form.find('.approve-edit').hide();
            $form.find('.approve-submit').show();
            $form.find('.approve-submit-back').show();
            $form.find('.approve-submit').val(msg(result));
        }
		// if button clicked is a next button
		if (this.hasClass('dx-next')) {
			// post update event to parent
			// TODO record needed implement update self
			//w(form.parent).updated();
			next();
			return;
		}
        //在查看页面点击编辑。dx没有重新加载，只是隐藏了操作按钮。所以批量按钮没有出来。
		if (form.action == 'view') {
			if (!checkActionCondition(table, 'edit', [form])) return;
			// mode switched
			form.action = 'edit';
            $form.find(".dx-text-area-textarea").attr('readonly', false);

            // $form.find(".dx-editor-input-textarea").summernote('enable');
            initEditorInput(form,'enable');

			// show all upload button for view mode
            form.get().find('.detail-before-after-data').hide();
			$form.find('.dx-upload-button').show();
			$form.find('label.required font').show();
            $form.find('.dx-ag-menu').show();
            $form.find('.save-and-submit').show();
            $form.find('.event-color-button').removeAttr('disabled');
            $form.find('.file-view-class').removeClass('file-view-class');
            $form.find('.g1-approve-submit').hide();
			// remove operate menu
			$form.find('.dx-operate-menu').remove();
			form.submitUrl = '/detail/edit.do';
			$('#' + form.id + '_submit').val(msg(result));

            if (!isEmpty(form.approveFlow) && table.is_approve == 1 && form.approveFlow.state != 3){
                //审批流配置的可编辑按钮。
                blockEditColumn(form);
            }else{
                // reset readonly status
                evaluateReadonly(form);
            }
			var batchData = form.p('batchData');
			// when switch to edit mode, all following form will be edit mode too
			if (batchData)
				batchData.forEach(function (data) {
					data.readonly = false;
				});

			if (form.children)
				form.children.forEach(function (child) {
					if(dx.user.isMobileLogin!=1)
						w(child.id).switchToEdit();
				});
			return;
		}

		// clear modified state
		form.p('modified', false);

		if (form.p('savedHandler')) {
			form.p('savedHandler')(result);
			$form.trigger('detail.saved', [form]);
		}
	});
	// set parent reference for formula
	var parentColumns;
	if (form.parent){
		var parentId=findFormId(form.parent);
		if(parentId)
			parentColumns = getFormModel(findFormId(form.parent));
	}
	if (parentColumns)
		form.columns._parent = {ref: parentColumns};
	initComponent(form);

	// set default values
	if (form.action === 'create'){
		setDefaultValues(form);
        $form.find('.event-color-button').removeAttr('disabled');
    }

    var operates = $form.find('.dx-operate-menu');
    if (operates.length != 0){
        for (var i=0; i<operates.length; i++){
            var operationId = $(operates[i]).find('.dx-operation').attr('name');
            checkOpMenu(table.triggers[operationId], [form], function (notRuleRow) {
                if (!isEmpty(notRuleRow)){
                    //$(operates[i]).hide();
                }
            })
        }
    }
    //修改关联表的名称列
    setRefValues(form);
	// evaluate fields readonly
	evaluateReadonly(form);

	var $autoGenMenu = $form.find('.dx-ag-menu');
	if ($autoGenMenu.length > 0) {
		var currentGrid = form.children[0];
		$autoGenMenu.on('click', function () {
			$autoGenMenu.find('li').addClass('hidden');
			var table = getTableDesc(currentGrid.table);
			$autoGenMenu.find('li[table="' + currentGrid.table + '"]').each(function () {
				var $li = $(this);
				var formula = table.autoGens[$li.attr('name')].exec_condition;
				if (isEmpty(formula) || evaluate(formula, form, false))
					$li.removeClass('hidden');
			});
		});
		$autoGenMenu.find('li').on('click', function () {
			var ag = new AutoGenerator();
			ag.open(currentGrid, $(this).attr('name'));
		});

		form.childSelected = function (grid) {
			currentGrid = grid;
		};
	}
	
	if(dx.user.isMobileLogin!=1){
		$form.find('div.dx-detail-container input:enabled:visible:not([readonly]):first').focus();
	}
	
	// operation menu will only activated in view mode
	if (form.action !== 'view')
		return;

	$form.find('label.required font').hide();
	// hide all upload button for view mode
	$form.find('.dx-upload-button').hide();

	// init operation menu
	initOpMenu($form, form);
	if (!form.ops && !form.shortcuts)
		return;

	if (form.shortcuts) {
		// show all table shortcut available
		setOpMenuVisible(form.shortcuts, null, true);
		// set shortcut handler
		form.shortcut = function (shortcut) {
			openTableShortcut(shortcut, form);
		};
	}

	if (form.ops) {
		//calcOpMenuStatus(table.operations, form.ops, [form]);
		form.operate = function (column, name, isDetail, checklevel) {
            execNewOperation(form.tableName, column, name, [form.id], form, isDetail, checklevel);
		};
		// with operation completed, reload view
		form.onUpdated = function () {
			var data = {id: form.id, parent: form.parent};
			var batchData = form.p('batchData');
            data.isIndexView = form.isIndexView;
            data.url = form.url;
			postPage('/detail/reload.view', data, function (result) {
				//remove original form , and replace with the result form
				var $form = $('#' + form.id);
				var $div = $form.parent();
				$form.remove();
				$div.append(result);

				// init new form
				var newForm = getFormModel($div.find('.dx-form').attr('id'));
				buildFormCache(newForm, newForm.widgets, true);

                //编辑或新增保存
                newForm.submit = form.submit;
                newForm.parentShow = form.parentShow;
                newForm.beforeAfterRows = form.beforeAfterRows;

				dx.init.detail(newForm);
				//calcOpMenuStatus(table.operations, newForm.ops, [newForm]);

				newForm.p('batchData', batchData);
				newForm.p('updated', true);

                if (form.isIndexView == 1){
                    $div.find('input.dx-back').hide();
                    newForm.isIndexView = 1;
                }
                //返回按钮点击事件
                $div.find('input.dx-back').on("click", function () {
                    form.parentShow();
                });

                dx.ajax = 0;
			});
		};
	}
	
	function uuid(len, radix) {
        var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
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
	
	//初始化手机端样式
	function initMobileStyle(){
		var buttons=$form.find(".approve-button-param button");
		var clazz="buttons-"+buttons.length;
		$form.find(".approve-line-ways").addClass(clazz);
	}
	//初始化手机端加签按钮
	function initAddApprover(){
		var $reason=$form.find(".approve-button-param");
		var $title=$form.find(".approve-title");
		if($reason.length!=0){
			var approveId=$reason.attr("approveId");
			var blockId=$reason.attr("blockId");
			var node_seq=$reason.attr("node_seq");
			
			var addApproveCount=$reason.attr("addApproveCount");
			var isAddApprove=$reason.attr("isAddApprove");
			var isReady=$reason.attr("isReady");
			
			var nextBlockId="";
			
			if(isAddApprove==1){
				var html=isReady!=1?msg("Sign up"):msg("modify add approver");
				var $html=$('<button type="button" class="mobile-add-approve">'+html+'</button>');
				$title.find("h4").append($html);
				$html.click(function(){
					openUserDialog(form,function(){
						if(isReady==1){
				        	 var approveNodes=form.approveFlow.approveFlowNodes;
				             for(var i=0;i<approveNodes.length;i++){
				             	if(i==approveNodes.length-1){
				             		nextBlockId="isUpdateAddApprove";
				             		break;
				             	}
				             	if(approveNodes[i].block_id==blockId){
				             		nextBlockId=approveNodes[i+1].block_id;
				             		break;
				             	}
				             }
				        }
						
						var $imgs=$(".approvers").find("img");
	            		if($imgs.length==0){
	            			alert("请选择加签人员！");
	            			return;
	            		}
	            		var userId={};
	            		$imgs.each(function(){
	            			userId[this.name]=uuid(10,16);
	            		});
	            		var postData={approveId : approveId,userId:userId,blockId:blockId,tableId:form.tableName,
		                		node_seq:node_seq,nextBlockId:nextBlockId}
	            		
	            		postJson('/approve/addApprove.do',postData, function(result){
	            			$.closeModal();
	            			$.toast();
	            			var obj=mobileStack.pop();
	            			newMobilePage(obj.url,obj.data);
	            		});
					});
				});
			}
		}
	}
});
