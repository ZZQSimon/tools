registerInit('message', function (form) {
	var sysPageNum=1;
	var cloudPageNum=1;
	var mePageNum=1;
	var sysLast;
	var cloudLast;
	var meLast;
	var pageSize=25;
	var checkflag;

	var $form = form.get();
	var $sysMsg=$form.find('.sys-message-list-ul');
	var $cloudMsg=$form.find('.cloud-message-list-ul');
	var $aboutmeMsg=$form.find('.aboutme-message-list-ul');
	var $sysCnt=$form.find('.sys-count');
	var $cloudCnt=$form.find('.cloud-count');
	var $aboutmeCnt=$form.find('.aboutme-count');

    icheckui();
	function icheckui() {
        $('.all-select input,.input-icheck').iCheck({
            checkboxClass: 'icheckbox_flat-blue',
            radioClass: 'iradio_flat-blue'
        });
        
        $('.input-icheck').on('ifUnchecked',function(){
    		var $check=$(this).parents('.aboutme-message-list-ul').prev().find('.all-select');
    		checkflag=0;
    		$check.iCheck('uncheck');
    	});
    }


    // $(".message-main ul").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"});

    $('.message-list-clear').on("click",function(e){
		$('.message-list-ul input:checked').parents(".message-list").remove();
	});
	
	$('.all-select').on('ifChecked',function(){
		$(this).parents(".message-list-top").next("ul").find("input").iCheck('check');
		checkflag=1;
	}).on('ifUnchecked',function(){
		//var length=$(this).parents(".message-list-top").next("ul").find("input:checked").length;
		if(checkflag==1){
			$(this).parents(".message-list-top").next("ul").find("input").iCheck('uncheck');
		}
		checkflag=1;
	});

	function clearMessagePage(){
		sysPageNum=1;
		cloudPageNum=1;
		mePageNum=1;
		sysLast;
		cloudLast;
		meLast;
		pageSize=25;
		$form.find('.sys-count').empty();
		$form.find('.cloud-count').empty();
		$form.find('.aboutme-count').empty();	
		$('#unreadMessage').removeClass("badge");
		$('#unreadMessage').find("span").empty();
	}
	
 	function bindClickofA(){
 		$form.find('.tab-pane ul li a').bind("click", function () {
	 		var messageId=this.id;
	 		$(this).find('.new').removeClass('new');
	 		var messages=[this.id];
			var request={messageId:messageId,messages:messages}
			/*postJson('/widget/message/getMessage.do',request,function(data){
		    		if(data){
			    		$form.find('.message-modal-title').text(data.title);
			    		$form.find('.message-modal-date').text(formatDate(data.send_date));
			    		$form.find('.message-modal-content').html(data.content);
			    		$form.find('.MessageInfoModal').modal('show');
			    		refreshUnread();
		    		}
		    	});*/
			showDialogForm({
			    url: '/widget/message/messageDetail.view',
			    data:request,
			    title: msg('message'),
			    class: 'dx-input-selector-dialog',
			    needAutoExpand: true,
			    shown: function (listForm, dialog) {
			    	refreshUnread();
			    	var $_form=listForm.get();
			    	var $link=$_form.find(".link-approve");
			    	if($link.length==1){
			    		$link.click(function(){
			    			dialog.close();
				    		var tableId=$link.attr("table-id");
				    		var key=dx.table[tableId].id_column;
				    		var param={};
				    		param[key]=$link.attr("data-id");
				    		var req={hasNext:false,readonly:true,table:tableId,param:param};
		    		    	newTab("/detail/edit.view",req);
			    		});	
			    	}
			    	
			    }
			});
	    });
 	}
 	
 	function refreshUnread(){
 		postJson('/widget/message/refreshUnread.do',{},function(data){
    		if(data){
    			initUnreadInfo(data);
    		}
    	});
 	}
 	
 	function revomeUreadClass(unReadObj,cnt,target,style){
 		if(cnt!=0){
 			unReadObj.text(cnt);
 			$form.find(target).parent().addClass(style);
 		}else{
 			unReadObj.text("");
 			 $form.find(target).parent().removeClass(style);
 		}
 	}
 	function initUnreadInfo(data){
 		revomeUreadClass($sysCnt,data.sysUnread,".sys-count","message-count");
 		revomeUreadClass($cloudCnt,data.cloudUnread,".cloud-count","message-count");
 		revomeUreadClass($aboutmeCnt,data.aboutmeUnread,".aboutme-count","message-count");
 		var cnt=data.sysUnread+data.cloudUnread+data.aboutmeUnread;
 		if(cnt!=0){
 			$("#unreadMessage").addClass("badge");
 			$("#unreadMessage").find("span").text(cnt);
 		}else{
 			$("#unreadMessage").find("span").text("");
 			$("#unreadMessage").removeClass("badge");
 		}
 		
 	}
    initMessageTable();
 	
 	function initMessageTable(){
    	var request={pageNumber:1};
    	postJson('/widget/message/initMessageTable.do',request,function(data){
    		if(data){
	    		
	    		addMessage(data.sysList,$sysMsg,"sysCheck");
	    		addMessage(data.cloudList,$cloudMsg,"cloudCheck");
	    		addMessage(data.aboutmeList,$aboutmeMsg,"aboutmeCheck");
	    		sysLast=Math.ceil(data.sysCnt/pageSize);
	    		cloudLast=Math.ceil(data.cloudCnt/pageSize);
	    		meLast=Math.ceil(data.aboutmeCnt/pageSize);
	    		$form.find('.sys-pagination-page').text(msg("of {page}").replace("{pages}",sysLast));
	    		$form.find('.cloud-pagination-page').text(msg("of {page}").replace("{pages}",cloudLast))
	    		$form.find('.aboutme-pagination-page').text(msg("of {page}").replace("{pages}",meLast))
	    		initUnreadInfo(data);
	    		bindClickofA(); 
	    		if((data.sysUnread+data.cloudUnread+data.aboutmeUnread)!=0){
	    			$('#unreadMessage').addClass("badge");
		    		$('#unreadMessage .badge-count').text(data.sysUnread+data.cloudUnread+data.aboutmeUnread);
	    		}
	    		
    		}
    	});
    }
    
 	
 	//系统消息处理
 	$form.find('.sys-message-id').change(function() { 		  
	 	var isSelect=$form.find('.sys-message-id').prop('checked');  
	 	$("input[name='sysCheck']").prop("checked",isSelect);  	
 	}); 	
 	
 	$form.find(".sys-clear").on("click",function(){
 		batchDelete("sysCheck");
 	});
 	
 	$form.find(".sys-read").on("click",function(){
 		batchRead("sysCheck");
 	});
 	
 	//格式化时间戳
 	function formatDate(timeStamp) {     
		var date =new Date(timeStamp);
		return date.Format("yyyy-MM-dd HH:mm:ss");	
	}
 	
 	Date.prototype.Format = function (fmt) { //author: meizz 
 		  var o = {   
 				    "M+" : this.getMonth()+1,                 //月份   
 				    "d+" : this.getDate(),                    //日   
 				    "h+" : this.getHours(),                   //小时   
 				    "m+" : this.getMinutes(),                 //分   
 				    "s+" : this.getSeconds(),                 //秒   
 				    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
 				    "S"  : this.getMilliseconds()             //毫秒   
 				  };   
 				  if(/(y+)/.test(fmt))   
 				    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
 				  for(var k in o)   
 				    if(new RegExp("("+ k +")").test(fmt))   
 				  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
 				  fmt = fmt.replace("HH",o["h+"]);
 				  return fmt; 
 	}
 	
 	//重载
 	function reloadMessage(pageNumber,type,ul,checkName){
 		var request={pageNumber:pageNumber,type:type}
		postJson('/widget/message/reloadMessage.do',request,function(data){
    		if(data){
    			addMessage(data.list,ul,checkName);
    			if(type=1){
    				sysLast=Math.ceil(data.count/pageSize);
    			}else if(type=2){
    				cloudLast=Math.ceil(data.count/pageSize);
    			}else{
    				meLast=Math.ceil(data.count/pageSize);
    			}
    			bindClickofA();
    		}
    	});
 	}
 	
 	//页码变化 
 	function pageChange(pageNumId,pageNumber,type,ul,checkName){
 		$form.find(pageNumId).val(pageNumber);
 		reloadMessage(pageNumber,type,ul,checkName);
	}
 	//第一页
 	$form.find('.sys-pagination-first').on("click",function(){
 		if(sysPageNum!=1){
 			sysPageNum=1;
 	 		pageChange('.sys-pagination-pageNum',sysPageNum,"1",$sysMsg,"sysCheck");
 		}
 	});
 	//上一页
 	$form.find('.sys-pagination-prev').on("click",function(){
 		if(sysPageNum>1){
 			sysPageNum=sysPageNum-1;
 			pageChange('.sys-pagination-pageNum',sysPageNum,"1",$sysMsg,"sysCheck");
 		}
 	});
 	//下一页
 	$form.find('.sys-pagination-next').on("click",function(){
 		if(sysPageNum<sysLast){
 			sysPageNum=sysPageNum+1;
 			pageChange('.sys-pagination-pageNum',sysPageNum,"1",$sysMsg,"sysCheck");
 		}
 		
 	});
 	//最后一页
 	$form.find('.sys-pagination-last').on("click",function(){
 		if(sysPageNum!=sysLast){
 			sysPageNum=sysLast;
 			pageChange('.sys-pagination-pageNum',sysPageNum,"1",$sysMsg,"sysCheck");
 		}
 		
 	});
 	
 	//官方消息处理
 	$form.find('.cloud-message-id').change(function() { 		  
	 	var isSelect=$form.find('.cloud-message-id').prop('checked');//打印选中的值  
	 	$("input[name='cloudCheck']").prop("checked",isSelect);  	
 	}); 	
 	
 	$form.find(".cloud-clear").on("click",function(){
 		batchDelete("cloudCheck");
 	});
 	
 	$form.find(".cloud-read").on("click",function(){
 		batchRead("cloudCheck");
 	});
 	
 	//第一页
 	$form.find('.cloud-pagination-first').on("click",function(){
 		if(cloudPageNum!=1){
 			cloudPageNum=1;
 	 		pageChange('.sys-pagination-pageNum',cloudPageNum,"2",$cloudMsg,"cloudCheck");
 		}
 	});
 	//上一页
 	$form.find('.cloud-pagination-prev').on("click",function(){
 		if(cloudPageNum>1){
 			cloudPageNum=cloudPageNum-1;
 			pageChange('.cloud-pagination-pageNum',cloudPageNum,"2",$cloudMsg,"cloudCheck");
 		}
 	});
 	//下一页
 	$form.find('.cloud-pagination-next').on("click",function(){
 		if(cloudPageNum<cloudLast){
 			cloudPageNum=cloudPageNum+1;
 			pageChange('.cloud-pagination-pageNum',cloudPageNum,"2",$cloudMsg,"cloudCheck");
 		}
 		
 	});
 	//最后一页
 	$form.find('.cloud-pagination-last').on("click",function(){
 		if(cloudPageNum!=cloudLast){
 			cloudPageNum=cloudLast;
 			pageChange('.cloud-pagination-pageNum',cloudPageNum,"2",$cloudMsg,"cloudCheck");
 		}
 		
 	});
 	
 	//我的消息处理
 	$form.find('.aboutme-message-id').change(function() { 		  
	 	var isSelect=$form.find('.aboutme-message-id').prop('checked');//打印选中的值  
	 	$("input[name='aboutmeCheck']").prop("checked",isSelect);  	
 	}); 	
 	
 	$form.find(".aboutme-clear").on("click",function(){
 		batchDelete("aboutmeCheck");
 	});
 	
 	$form.find(".aboutme-read").on("click",function(){
 		batchRead("aboutmeCheck");
 	});
 	
 	//第一页
 	$form.find('.aboutme-pagination-first').on("click",function(){
 		if(mePageNum!=1){
 			mePageNum=1;
 	 		pageChange('.aboutme-pagination-pageNum',mePageNum,"3",$aboutmeMsg,"aboutmeCheck");
 		}
 	});
 	//上一页
 	$form.find('.aboutme-pagination-prev').on("click",function(){
 		if(mePageNum>1){
 			mePageNum=mePageNum-1;
 			pageChange('.aboutme-pagination-pageNum',mePageNum,"3",$aboutmeMsg,"aboutmeCheck");
 		}
 	});
 	//下一页
 	$form.find('.aboutme-pagination-next').on("click",function(){
 		if(mePageNum<meLast){
 			mePageNum=mePageNum+1;
 			pageChange('.aboutme-pagination-pageNum',mePageNum,"3",$aboutmeMsg,"aboutmeCheck");
 		}
 		
 	});
 	//最后一页
 	$form.find('.aboutme-pagination-last').on("click",function(){
 		if(mePageNum!=meLast){
 			mePageNum=meLast;
 			pageChange('.aboutme-pagination-pageNum',mePageNum,"3",$aboutmeMsg,"aboutmeCheck");
 		}
 		
 	});
 	

 	
 	

 	function batchRead(name){
 		var messages=getSelectedIds(name);
 		var request={messages:messages}
 		if (messages.length!=0){
 			postJson('/widget/message/batchRead.do',request,function(data){
 	    		if(data.ret==true){
 	    			clearMessagePage();
 	    			initMessageTable();
 	    			dxToastAlert(data.msg);
 	    			$('.all-select').iCheck('uncheck');
 	    		}else if(data.ret==false){
 	    			alert(data.msg);
 	    		}
 	    	});
 		}
 	}
 	function batchDelete(name){
 		var messages=getSelectedIds(name);
 		var request={messages:messages}
 		if (messages.length!=0){
 			postJson('/widget/message/batchDelete.do',request,function(data){
 	    		if(data.ret==true){
 	    			clearMessagePage();
 	    			initMessageTable();
 	    			dxToastAlert(data.msg);
 	    			$('.all-select').iCheck('uncheck');
 	    		}else if(data.ret==false){
 	    			alert(data);
 	    		}
 	    	});
 		}
 	}
 	function getSelectedIds(name){
 		var sysCheck = $("input[name='"+name+"']");
 	    var ids = new Array();
 	    for(var i = 0; i < sysCheck.length; i++){
 	     if(sysCheck[i].checked)
 	    	ids.push(sysCheck[i].value);
 	    } 
 	    return ids;
 	};
 	
 	//onclick='javascript:showInfo("+list[i].id+")'
    function addMessage(list,ul,checkName){
    	ul.empty();
    	for(var i=0;i<list.length;i++){      
        	var $li=$("<li class='message-list'></li>");	
        	var $input=$("<input type='checkbox' class='input-icheck' value='"+list[i].receive_id+"' name='"+checkName+"'>");
        	var $a=$("<a href='javascript:void(0)'  class='easyui-linkbutton' id='"+list[i].receive_id+"' data-toggle='modal'></a>");
        	var $p;
        	if(list[i].status=='0'){
        		$p=$("<p>"+list[i].title+"<span class='new'></span></p>");
        	}else{
        		$p=$("<p>"+list[i].title+"</p>");
        	}
        	
        	var $span=$("<span class='message-list-time'>"+formatDate(list[i].send_date)+"</span>");        
        	$p.append($span);
        	$a.append($p);
        	$li.append($input);
        	$li.append($a);
        	ul.append($li);
            icheckui();
    	}
    }

});