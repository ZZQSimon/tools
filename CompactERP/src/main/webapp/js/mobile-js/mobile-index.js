function mobileIndex(){	
	reloadMobile();
	function reloadMobile(){
		function initMobileJsCache(data) {
	        dx.dict = data.dict;
	        dx.dictNameI8N = data.dictNameI18NCache;
	        dx.table = data.table;
	        dx.urlInterface = data.urlInterface;
	        dx.tableViewStyle = data.tableViewStyle;
	        for(var key in dx.table){
	        	dx.table[key].columnMap = {};
	            if (!isEmpty(dx.table[key].columns)) {
	                var columns=dx.table[key].columns;
	                for(var x in columns){
	                	dx.table[key].columnMap[columns[x].column_name] = columns[x];
	                }
	            } else {
	                //alert(msg('table') + ' "' + dx.table[key].id + '" ' + msg('have no column'));
	            }
	        }
	        dx.dataType = data.dataType;
	        dx.i18n.message = data.message;
	        dx.pages = data.pages;
	        dx.operations = data.operations;
	        dx.triggers = data.triggers;
	        dx.shortcuts = data.shortcuts;
	        dx.batches = data.batches;
	        dx.status = data.status;
	        dx.complexColumns = data.complexColumns;
	        dx.sysGroupName = data.sysGroupName;
	        dx.sys = data.sys;
	        dx.biz = data.biz;
	        dx.result = {
	            $details: $('#dxResultDetails'),
	            $message: $('#dxResultMessage'),
	            dialog: $('#dxResultDialog').get()[0]
	        }; 
	    }
		dx.mobileProcessing.open();
		
		/*postJson('/data/cache/reload.do',function(){
			
		});*/
		postJson('/data/getDomainKey.do', function (domainKey) {
        	postJson('/data/initCache.do', function (data) {
        		initMobileJsCache(data);
                dx.mobileProcessing.close();
				initIndexForm();
		        initWidgets();
		        initMainMenu($(document));
		        initMainTab($(document));
		        if (dx.user.isOutPage != 1)
		            initIndex(dx.cache.form['index']);
		        var src=dx.user.imageUrl==""?makeUrl("/img/defaultPhoto.png"):dx.user.imageUrl;
		    	$(".moblie-personal-center").find(".userlogo").attr("src",src);
		    	
		    	var approveParam=getFormModel("index").approveParam;
		    	if(!isEmpty(approveParam)){
		    		var key=dx.table[approveParam.table].id_column;
		    		var param={};
		    		param[key]=approveParam.dataid;
		    		var req={hasNext:false,readonly:true,table:approveParam.table,param:param};
    		    	newMobilePage("/detail/edit.view",req);
		    	}
            });
        });
	}
	
	var form = getFormModel("index");
	openLocalPage();
	disableBack();
	
	//主页切换
	function toggle(className){
		$(".inner-page").children().each(function(){
			$(this).css("display","none");
		});
		$(".moblie-container").empty();
		$("."+className).css("display","block");
		$(".moblie-footer-menu").css("display","block");
	}
	
	//底部菜单事件
	function openLocalPage(){
		$(".footer-menu-item").click(function(){
			if(this.id=="moblie-approve-center"){
				$("body").addClass("white-bgcolor");
			}else{
				$("body").removeClass("white-bgcolor");
			};
			$(".footer-menu-item").removeClass("active");
			$(this).addClass("active");
			toggle(this.id);
			mobileStack.push({url:"local",data:{},clazz:this.id});
			if(this.id=="moblie-work-date"){
				initWorkDate();
			}
		});
	}
	
	function initWorkDate(){
		var url="/calendar/mobile_calendar.view";
		var data={}
		postPage(url, data, function (result) {
	    	var $page = $(result);
	    	var $container=$(".moblie-work-date");
	    	$container.empty();
	    	$container.append($page);
	        
	        var $form = $container.find('.dx-form');
	        var form = getFormModel($form.attr('id'));
	        buildFormCache(form, form.widgets);
	        if (dx.init[form.widgetName])
	            dx.init[form.widgetName](form, "");
	        else
	            console.warn('no init function registered for "' + form.widgetName + '"');
	        if (dx.moduleInit[form.widgetName])
	            dx.moduleInit[form.widgetName].forEach(function (func) {
	                func(form, "");
	            });
	        doAutoExpand(form);
	    });
	}
	
	//重写浏览器返回功能
	function disableBack(){
		if (window.history && window.history.pushState) {
			$(window).on('popstate', function () {
			  $(".close-select").click();
	    	  if(mobileStack.length==1) return;
      		  window.history.pushState('forward', null, ''); 
      		  window.history.forward(1);
  			  mobileStack.pop();
      	      var staus;
      		  var obj=mobileStack.pop();
  			  
  			  if(obj.url!="local"){
  				newMobilePage(obj.url,obj.data);
  				staus="block";
  			  }else{
  				$("#"+obj.clazz).click();
  				staus="none";
  			  }

              if(staus==undefined || staus=="block"){
            	  $(".inner-page").children().each(function(){
            			$(this).css("display","none");
            	  });
              	  $(".moblie-container").css("display","block");
              }else{
            	  toggle(obj.clazz)
              }
              
          });
		}
		
		window.history.pushState('forward', null, '');  //在IE中必须得有这两行
		window.history.forward(1);	
	}
	
	//待我审批事件
	$(".wait-me").click(function(){
		toggle("moblie-approve-center");
		$("body").addClass("white-bgcolor");
		$(".i-start-list").css("display","none");
		$(".wait-me-list").css("display","block");
		$(".type-i-start").removeClass("isActive");
		$(".type-wait-me").addClass("isActive");
		$(".footer-menu-item").removeClass("active");
		$("#moblie-approve-center").addClass("active");
		mobileStack.push({url:"local",data:{},clazz:"moblie-approve-center"});
	});
	
	//我发起的审批事件
	$(".i-start").click(function(){
		toggle("moblie-approve-center");
		$("body").addClass("white-bgcolor");
		$(".wait-me-list").css("display","none");
		$(".i-start-list").css("display","block");	
		$(".type-wait-me").removeClass("isActive");
		$(".type-i-start").addClass("isActive");
		$(".footer-menu-item").removeClass("active");
		$("#moblie-approve-center").addClass("active");
		mobileStack.push({url:"local",data:{},clazz:"moblie-approve-center"});
	});
	
	$(".type-wait-me").click(function(){
		$(".i-start-list").css("display","none");
		$(".wait-me-list").css("display","block");
		$(".type-i-start").removeClass("isActive");
		$(".type-wait-me").addClass("isActive");
	});
	
	$(".type-i-start").click(function(){
		$(".wait-me-list").css("display","none");
		$(".i-start-list").css("display","block");
		$(".type-wait-me").removeClass("isActive");
		$(".type-i-start").addClass("isActive");
	});
	
	//初始化审批中心
	initApproveCenter(form);
    //定时刷新审批中心数据
    if (!isEmpty(form.approveFlushTime) && form.approveFlushTime > 1000){
        setInterval(function(){
            postJson('/mobileApproveFlush.do', {}, function(reult){
                initApproveCenter(reult)
            });
        }, form.approveFlushTime);
    }
	function initApproveCenter(form){
		var template='<div class="col-xs-12 approve-record g-cell-item " data_id="${data_id}" table_id="${table_id}">'
		           +     '<img class="record-user userlogo" src="${imageUrl}"/>'
		           +     '<div class="record-detail">'
		           +      '<p  class="record-title">${user_name}</p>'
		           +      '<p class="record-time">${approve_time}</p>'
		           +      '<p class="record-event">${name_expression_publicity}</p>'
		           +    '</div>'
		           +'</div>';
		
		function buildTemplate(obj){
			var record=template;
			var imageUrl=obj.imageUrl==""?makeUrl("/img/defaultPhoto.png"):obj.imageUrl;
			record=record.replace("${data_id}",obj.data_id);
			record=record.replace("${table_id}",obj.table_id);
			record=record.replace("${imageUrl}",imageUrl);
			record=record.replace("${user_name}",obj.user_name);
			record=record.replace("${approve_time}",$.format.date(new Date(obj.approve_time), 'yyyy-MM-dd HH:mm'));
			record=record.replace("${name_expression_publicity}",obj.name_expression_publicity);
			return record;
		}

        $(".wait-me-list").empty();
        $(".i-start-list").empty();
		for(var key in form.waitMeApprove){
			$(".wait-me-list").append(buildTemplate(form.waitMeApprove[key]));
		}
		
		for(var key in form.myApprove){
			$(".i-start-list").append(buildTemplate(form.myApprove[key]));
		}
		
		$(".approve-record").click(function(){
			var table_id=$(this).attr("table_id");
			var data_id=$(this).attr("data_id");
			table= getTableDesc(table_id);
			var idColumn=getTableIdColumn(table);
			var param={};
			
			param[idColumn]=data_id;
			var req={table: table_id, param: param, readonly: true};
		    newMobilePage("/detail/edit.view", req);
		});
		
		if(form.waitMeApproveSize==0) $(".wait-me-list .no-record").css("display","block");
		if(form.myApproveSize==0) $(".i-start-list .no-record").css("display","block");
	}

	inittodayTip();
	function inittodayTip(){
		var date=new Date().Format("yyyy年MM月dd日");
		var week="星期" + "日一二三四五六".charAt(new Date().getDay());
		$(".today-tip").html(date+'<span>'+week+'</span>');
	}
	
	//个人中心菜单事件
	var $personal=$(".moblie-personal-center");
	
	$personal.find(".personal-data").click(function(){
		newMobilePage('/detail/userEdit.view',{
            table: "m_user",
            readonly: true,
            isIndexView: 1
        });
	});
	
	$personal.find(".change-password").click(function(){	
		newMobilePage('/changepassword/edit.view',{});
		$("body").addClass("white-bgcolor");
	});
	
	$personal.find(".mobile-logout").click(function(){
		//WeixinJSBridge.call('closeWindow');
	});

}