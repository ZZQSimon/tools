(function($){
//	var jsonstrDefault="";
//	var retunJsonstr="";
	function appendControl(onSubmit,callback,uuid,modalX,modalY){
		$("body").append('<div class="'+uuid+'"><div class="modal  parameterInputControlModal" tabindex="-1" role="dialog">'
            +' <div class="modal-dialog" role="document">'
            +' <div class="modal-content">'
            +' <div class="modal-header">'
            +' <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
        +' </div>'
        +'<div class="modal-body">'
        +' </div>'
        +'<div class="modal-footer">'
            +' <button type="button" class="btn button-color4" data-dismiss="modal">取消</button>'
            +'  <button type="button" class="btn button-color3 saveParamJosn" data-dismiss="modal">保存</button>'
        +' </div>'
        +'</div>'
        +'</div>'
        +'</div></div>');
		$(".parameterInputControlModal").modal();
		
		$("body").on("click",".parameterInputControlModal button[data-dismiss=modal]",function(){
			$(this).parents(".parameterInputControlModal").parent().remove();
		});
		//点击保存
		$("."+uuid).find(".saveParamJosn").on("click",function(){
			var jsonstr="{";
//			jsonstrDefault="{";
			var div=$("."+uuid).find(".parameterInputControlModal .modal-body").find("div[name='modalBody']");
			for(var i=0;i<div.length;i++){
				var str=$(div[i]).find("input").val();
				$.trim(str);
//				var strDefault=$(div[i]).find("input").val();
//				if(strDefault.substr(0, 1)=="{"){
//					jsonstrDefault+=$(div[i]).attr("class")+":"+$(div[i]).find("input").val()+",";
//				}else{
//					jsonstrDefault+=$(div[i]).attr("class")+":'"+$(div[i]).find("input").val()+"',";
//				}
				if($(div[i]).find("input").attr("disabled")!="disabled"){
					if(str.substr(0, 1)=="{"){
						jsonstr+=$(div[i]).attr("class")+":"+$(div[i]).find("input").val()+",";
					}else{
						var value = $(div[i]).find("input").val();
						$.trim(value);
						if(value.substr(0, 1)=='"'){
							jsonstr+=$(div[i]).attr("class")+":'"+$(div[i]).find("input").val()+"',";
						}else if(value.substr(0, 1)=="'"){
							jsonstr+=$(div[i]).attr("class")+':"'+$(div[i]).find("input").val()+'",';
						}else{
							jsonstr+=$(div[i]).attr("class")+":'"+$(div[i]).find("input").val()+"',";
						}
					}
				}
			}
			jsonstr=jsonstr.substr(0,jsonstr.length-1);
			jsonstr+="}";
//			jsonstrDefault=jsonstrDefault.substr(0,jsonstrDefault.length-1);
//			jsonstrDefault+="}";
			//返回数据
			if(!isEmpty(callback)){
				if(jsonstr=="}"){
					jsonstr="";
				}
//				if(jsonstrDefault=="}"){
//					jsonstrDefault="";
//				}
//				retunJsonstr=jsonstr;
				callback(jsonstr);
			}
		});
		//忽略的点击事件
        $("."+uuid+" .modal-body").on("click",".ignore-btn",function () {
        	if($(this).is(":checked")){
        		$(this).parents(".input-bt-Switcher-wrap").parent().parent().find("input[type=text]").attr("disabled","true");
        	}else{
        		$(this).parents(".input-bt-Switcher-wrap").parent().parent().find("input[type=text]").removeAttr("disabled");
        	}
        });
	}
	function setData(onSubmit,callback,josnstr,paramId,modalX,modalY,param){
		if(isEmpty(paramId)){
			alert("请选择页面模板！");
			return;
		}
		var uuid=uid(50,16);
		appendControl(onSubmit,callback,uuid,modalX,modalY);
		var obj ={};
//		var objDefault ={};
		if(!isEmpty(josnstr)){
			obj=eval('(' + josnstr + ')'); 
		}
//		if(isEmpty(jsonstrDefault) || retunJsonstr!=josnstr){
//			jsonstrDefault=josnstr;
//			objDefault=eval('(' + jsonstrDefault + ')'); 
//		}else{
//			objDefault=eval('(' + jsonstrDefault + ')'); 
//		}
		var urlInterface=dx.urlInterface[paramId];
		var map=urlInterface.urlParam;
		for(var key in map) { 
			var interId=map[key].international_id;
			var param=map[key].i18n[dx.user.language_id];
			var flag=false;
			for(var objkey in obj) { 
				if(objkey==key){
					flag=true;
					break;
				}
			}
//			for(var objDefaultKey in objDefault) { 
//				if(objDefaultKey==key){
//					break;
//				}
//			}
			if(flag){
				if(typeof obj[key]=="object"){
					var objvalue=JSON.stringify(obj[key]);
					$("."+uuid).find(".parameterInputControlModal .modal-body").append('<div class="'+key+'" name="modalBody">'
			            +'<label class="label-2">'+param+'</label><div class="input-1 urlValueClick"><input nesting_column_name="'+map[key].nesting_column_name
			            +'" class="input-3" type="text" value='+objvalue+'></div></div>');
				}else{
					$("."+uuid).find(".parameterInputControlModal .modal-body").append('<div class="'+key+'" name="modalBody">'
				            +'<label class="label-2">'+param+'</label><div class="input-1 urlValueClick"><input nesting_column_name="'+map[key].nesting_column_name
				            +'" class="input-3" type="text" value="'+obj[key]+'"/></div></div>');
					$("."+uuid).find(".parameterInputControlModal .modal-body ."+key+" input").val(obj[key]);
				}
				$("."+uuid).find(".parameterInputControlModal .modal-body ."+key+" .urlValueClick").after("<div class='label-1'><label class='input-1'>是否用户输入</label><div class='input-bt-Switcher-wrap'>" +
						"<div class='input-bt-Switcher'><input type='checkbox' class='ignore-btn'/><label></label></div></div></div>");
			}else{
//				if(typeof objDefault[key]=="object"){
//					var objDefaultValue=JSON.stringify(objDefault[key]);
//					$("."+uuid).find(".parameterInputControlModal .modal-body").append('<div class="'+key+'" name="modalBody">'
//				            +'<label class="label-2">'+param+'</label><div class="input-1 urlValueClick"><input nesting_column_name="'+map[key].nesting_column_name
//				            +'" class="input-3" type="text" value='+objDefaultValue+'></div></div>');
//				}else{
//					var value=objDefault[key];
//					if(isEmpty(objDefault[key])){
//						value="";
//					}
					$("."+uuid).find(".parameterInputControlModal .modal-body").append('<div class="'+key+'" name="modalBody">'
				            +'<label class="label-2">'+param+'</label><div class="input-1 urlValueClick"><input nesting_column_name="'+map[key].nesting_column_name
				            +'" class="input-3" type="text" value="" /></div></div>');
//				}
				
				$("."+uuid).find(".parameterInputControlModal .modal-body ."+key+" .urlValueClick").after("<div class='label-1'><label class='input-1'>是否用户输入</label><div class='input-bt-Switcher-wrap'>" +
				"<div class='input-bt-Switcher'><input type='checkbox' class='ignore-btn' /><label></label></div></div></div>");
                $("."+uuid+" .modal-body ."+key+" input.ignore-btn").click();
			}
		}
		$("."+uuid).find(".parameterInputControlModal .modal-body .urlValueClick").on("click",function(){
			if(isEmpty($(this).find("input").attr("disabled"))){
				var that = this;
				var nesting_column_name=$(this).find("input").attr("nesting_column_name");
				if(!isEmpty(nesting_column_name) && nesting_column_name!="null"){
					var nestingParamMap=dx.urlInterface;
					var nestingUrl=$("."+uuid).find("."+nesting_column_name).find("input").val();
					if(!isEmpty(nestingUrl)){
						for(var nestingKey in nestingParamMap) { 
							if(nestingParamMap[nestingKey].url==nestingUrl){
								$(this).parameterInputControl(true).setData("onSubmit",function(data){
									$(that).find("input").val(data);
						    	},$(this).find("input").val(),nestingKey);
							}
						}
					}
				}
			}
		});
	}
    $.fn.extend({
    	parameterInputControl: function(param){
			if (param == true){
				return {
                    setData :  function(onSubmit,callback,josnstr,paramId,modalX,modalY,param){
                    	setData(onSubmit,callback,josnstr,paramId,modalX,modalY,param);
                    }
                }
			}
        }
    });
  //采番菜单Id
    function uid(len, radix) {
        var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
            .split('');
        var uuid = [], i;
        radix = radix || chars.length;

        if (len) {
            for (i = 0; i < len; i++)
                uuid[i] = chars[0 | Math.random() * radix];
        }else {
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
})(jQuery);



