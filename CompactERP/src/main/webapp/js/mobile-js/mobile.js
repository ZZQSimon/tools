
Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

//定义浏览历史堆栈
var mobileStack=[{url:"local",data:{},clazz:"moblie-home"}];

/**
 * 
 * @param url
 * @param data
 * @param postInit
 * @param ownerId
 * @returns
 */
function newMobilePage(url, data, postInit, ownerId) {
	
	$("body").removeClass("white-bgcolor");
	$(".inner-page").children().each(function(){
		$(this).css("display","none");
	});
	$(".moblie-container").css("display","block");
	  
	
    if (typeof postInit === 'string') {
        ownerId = postInit;
        postInit = null;
    }
    if (!data)
        data = {};
    dx.mobileProcessing.open();
    postPage(url, data, function (result) {
    	setTimeout(function () {
    		dx.mobileProcessing.close();
        }, 4000);
    	dx.mobileProcessing.close();
    	mobileStack.push({url:url,data:data});
    	var $page = $(result);
    	var $container=$(".moblie-container");
    	$container.empty();
    	$container.append($page);
        
        var $form = $container.find('.dx-form');
        var form = getFormModel($form.attr('id'));
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
        
        //隐藏富文本字段
        if(form.action!="create"){
            $form.find(".dx-editor-input-textarea").summernote('disable');
        }
    });
}

function openMobilePage(id) {
    var page = getPageDesc(id);
    var data={};
    var param = evaluate('(' + page.param + ')',null);
    if(page.url_id=="01"){
        data={table:param.table, pageId:id} ;
        postJson("/detail/checkAuth.do",data,function(result){
        	if(result){
        		newMobilePage("/detail/create.view", data);
        	}else{
        		data={table:param.table, menuId:id} ;
    		    newMobilePage("/list/mobile/table.view", data, id);
        	}
        	
        });
        
    }else{
    	if (!page.url)
            return;
        var data;
        if (page.param)
            data = evaluate('(' + page.param + ')', null);
        else
            data = {};
    	newMobilePage(page.url, data);
    }
    
}



var userHtml=''
	+'<div id="user-pupup" class="user-pupup">'
	    +'<div class="approvers input-group">'
    		+'<p class="approver-add-tip">点击头像可删除</p><div>'
		    +'<span class="btn dx-upload-button add-approver">'
		       +'<label></label>'
		    +'</span>'
		+'</div></div>'
	    +'<div class="moblie-footer-menu">'
	    +'<button type="button" class="btn submit-add-approver">'+msg("Save")+'</button>'
    +'</div>';


var userListHtml=''
	+'<div id="user-list-pupup" class="weui-popup__container">'
		+'<div class="weui-popup__modal ref-filed-list">'
			+'<div class="gm-top-menu">'
				+'<button type="button" class="cancle-pupup">取消</button>'
				+'<input class="search" type="text" placeholder="搜索你要找的人"/>'
			+'</div>'
			+'<div class="ref-records">'
			    +'<div class="row ref-record">'
			    +'</div>'
			+'</div>'
		+'<div>'
	+'</div>';

var defaultListHtml=''
	+'<div id="ref-list-pupup" class="weui-popup__container">'
		+'<div class="weui-popup__modal ref-filed-list">'
			+'<div class="gm-top-menu">'
				+'<button type="button" class="cancle-pupup">取消</button>'
				+'<input class="search" type="text" placeholder="搜索"/>'
			+'</div>'
			+'<div class="ref-records">'
			    +'<div class="row ref-record">'
			    +'</div>'
			+'</div>'
		+'<div>'
	+'</div>';



function UserListCallBack(){
	var value=$(".search").val();
	var $list=$(".ref-records");
	$list.empty();
	var postData={filter:{action:"exec",filters:{name:{type:"like",value:value}},tableName:"m_user"}};
	 postJson('/widget/grid/key.do', postData,function(ret){
		 var src;
		 for(var key in ret){
			 src=ret[key].imageUrl==""?makeUrl("/img/defaultPhoto.png"):ret[key].imageUrl;
			 $list.append('<div class="close-popup ref-record g-cell-item"><img class="add-approve-img" src="'+src+'" name="'+ret[key].id+'" user_id="'+ret[key].name+'"><span>'+ret[key].name+'</span></div>');
		 }
		 $list.find(".ref-record").click(function(){
		     var img=$(this).find("img"); 
			 if($(".approvers").find("img[name='"+img[0].name+"']").length!=0){
				 alert("已选择联系人");
				 return;
			 }
		     var $addApprover=$(".add-approver");
		     $addApprover.before('<span class="btn add-approve-wrap">'+img[0].outerHTML+'<p class="add-approve-name">'+img.attr("user_id")+'</p></span>');
		     $addApprover.siblings().click(function(){
		    	 $(".search").remove();
		    	 $(this).remove();
		     });
		     $.closePopup();
		 });
	 });
}

function searchUser(){
	$(".search").keydown(function(e){
		if(e.keyCode==13){
			UserListCallBack();
		}
	});
}


function openUserDialog(form,callback){
	var $form=form.get();
	var content=userHtml+userListHtml;
	
	$.modal({
	    title: '<span class="top-menu-title pull-left cancle-add-approver">'+msg("Cancel")+'</span>添加审批人',
	    text: content,
	    buttons: [
	       /* { text: msg("Save"), onClick: function(){ callback();} },
	        { text: msg("Cancel"), onClick: function(){ $.closeModal();} },*/
	      ]
	});
	
	
	$(".add-approver").click(function(){
		$("#user-list-pupup").popup();
    });
	UserListCallBack();
	searchUser();
	
	$(".submit-add-approver").click(function(){
		callback();
	});
	$(".cancle-add-approver").click(function(){
		$.closeModal();
	});
}


/**
 * 审批流用户列表弹窗组件
 * @param form
 * @returns
 */
function openUserListDialog(form){
	var $form=form.get();
	var $content=userListHtml;
	$form.find("#user-list-pupup").remove();
	$form.append($content);
	$("#user-list-pupup").popup();
	UserListCallBack();
	$("#user-list-pupup").find(".cancle-pupup").click(function(){
		$.closePopup();
	});
	searchUser();
}


/**
 * 列表弹窗组件
 * @param opts
 * @returns
 */
function mobileListDialog(opts){
	var $field=$("#"+opts.data.parent).blur();
	postPage(opts.url, opts.data ? opts.data : {}, function (content) {
        function loadData(){
            postJson('/widget/grid/list.do', postData, function (records) {
                if (isEmpty(records) || records.length == 0)
                    return;
                var rows = buildGridData(form.grid, records, form);
                var table = dx.table[form.tableName];
                var id_column = table.idColumns[0];
                var name_column = table.name_column;
                var param={table: table, id_column: id_column, name_column: name_column, rows: rows, records: records};

                initRefRecord(param, opts.update);

                //$(".ref-filed-list").scroll(function() {
                //    //当时滚动条离底部60px时开始加载下一页的内容
                //    if (($(this)[0].scrollTop + $(this).height() + 60) >= $(this)[0].scrollHeight) {
                //        loadData(false);
                //    }
                //});
                $(".search").keydown(function(e){
                    if(e.keyCode==13){
                        postData.filter.filters["search"]={type:"like",value:this.value};
                        postJson('/widget/grid/list.do', postData, function (records){
                            var rows = buildGridData(form.grid, records, form);
                            param.rows = rows;
                            param.records = records;
                            $(".ref-records").empty();
                            initRefRecord(param,opts.update);
                        });
                    }
                });
                //var to;
                //$(".search").keyup(function () {
                //    var that = this;
                //    if (to) {
                //        clearTimeout(to);
                //    }
                //    to = setTimeout(function () {
                //        postData.filter.filters["search"] = {type:"like", value: that.value};
                //        postJson('/widget/grid/list.do', postData, function (records){
                //            var rows = buildGridData(form.grid, records, form);
                //            param.rows = rows;
                //            param.records = records;
                //            $(".ref-records").empty();
                //            initRefRecord(param,opts.update);
                //        });
                //    }, 250);
                //});

                var bind_name="input";//定义所要绑定的事件名称
                if(navigator.userAgent.indexOf("MSIE")!=-1) bind_name="propertychange";//判断是否为IE内核 IE内核的事件名称要改为propertychange
                /*输入框键盘离开事件绑定*/
                $(".search").bind(bind_name,function(){
                    postData.filter.filters["search"] = {type:"like", value: this.value};
                    postJson('/widget/grid/list.do', postData, function (records){
                        var rows = buildGridData(form.grid, records, form);
                        param.rows = rows;
                        param.records = records;
                        $(".ref-records").empty();
                        initRefRecord(param,opts.update);
                    });
                });
            })
        }
		var formId=$(content)[0].data.replace("cache form:","");
		var form = getFormModel(formId);
		var $content=$(content);
		$content.css("display","none");
		$("body").append($content);
		buildFormCache(form, form.widgets);
		initFilter(w(form.id));
		var fb = form.filter.mobileBuild(this.value);
		var postData = {id: form.grid.id, filter: {action:fb.action,filters:fb.filters,tableName:fb.table.id}};

        var $content = defaultListHtml;
        $("#ref-list-pupup").remove();
        $("body").append($content);
        $("#ref-list-pupup").popup();
        $("#ref-list-pupup").find(".cancle-pupup").click(function(){
            $.closePopup();
        });
        var paging = {};
        paging.start = 0;
        paging.length = 15;
        postData.paging = paging;
        loadData();
        //滚动加载方法2
        $(".ref-filed-list").scroll(function() {
            //当时滚动条离底部60px时开始加载下一页的内容
            if (($(this)[0].scrollTop + $(this).height() + 60) >= $(this)[0].scrollHeight) {
                paging.start = paging.start + paging.length;
                loadData();
            }
        });
    }, opts.fail);
}


function initRefRecord(param, update){
    var nameExpressions = {};
    for (var i=0; i<param.records.length; i++){
        nameExpressions[param.records[i].id] = param.records[i].ref____name_Expression;
    }
	//$(".ref-records").empty();
	var rawDatas = {};
	if(isEmpty(param.name_column)) param.name_column = param.id_column;
	for(var key in param.rows){
		rawDatas[param.rows[key]["DT_RowId"]] = param.rows[key]["rawData"];

		$(".ref-records").append('<div id="' + param.rows[key]["DT_RowId"]+'" class="close-popup ref-record g-cell-item"><span>' +
        nameExpressions[param.rows[key].rowid] + '</span></div>');
	}
	$(".ref-records").find(".ref-record").click(function(){
		var rows = rawDatas[this.id];
		var data = {table: param.table, keys: {}};
	    var nameExpression = nameExpressions[rows.rowid];
	    param.table.idColumns.forEach(function (column) {
	        data.ref_id = rows.columns[column].value;
	    });
	    data.nameExpression = nameExpression;
	    update(data);
	    $.closePopup();
	});
}






