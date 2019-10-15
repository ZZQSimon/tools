(function($){
	 var international={};
	function appendControl(div, param){
		$(div).append('<div class="g1-internation-input"><input type="text" interId="'+param.internationalId
			+'" class="retrievalInter"/>'
			+'<ul class="g1-dropdown-menu-tip"></ul><div class="g1-dropdown dropdown">'
			+'<button class="btn g1-dropdown-toggle" type="button"  data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"></button>'
			+'<ul class="dropdown-menu clearfix">'
			+' <li class="clearfix">'
			+'<label class="label-1 international">'+msg('17d8b0aa-2792-4b5f-9dd4-5f310c57690a')+'</label>'
			+'<input type="text" name="cn" class="input-1">'
			+'</li>'
			+'<li class="clearfix">'
			+'<label class="label-1 international">'+msg('bc837556-2235-449b-801b-fc7f8bbddf89')+'</label>'
			+'<input type="text" name="en" class="input-1">'
			+'</li>'
			+'<li class="clearfix">'
			+'<label class="label-1 international">'+msg('d9639d69-5a5b-487a-b440-b88187b7bc78')+'</label>'
			+'<input type="text" name="jp" class="input-1">'
			+'</li>'
			+'<li class="clearfix">'
			+'<label class="label-1 international">'+msg('24ad661b-b0c7-48fd-897c-a8b33965b2bb')+'</label>'
			+'<input type="text" name="other1" class="input-1">'
			+'</li>'
			+'<li class="clearfix">'
			+'<label class="label-1 international">'+msg('355af2af-0e5a-4476-a2ad-99de9a337ab0')+'</label>'
			+'<input type="text" name="other2" class="input-1">'
			+'</li>'
			+'<li class="g1-dropdown-menu-btn">'
			+'<button class="button-color3 g1-dropdown-toggle saveInternational" type="button">'+msg('save')+'</button>'
			+'<button class="button-color4 g1-dropdown-toggle" type="button">'+msg('cancel')+'</button>'
			+'</li></ul></div></div>');
        var name = '';
        if (!isEmpty(param.menuName)){
            name = param.menuName.replace(/&lt;/g, '<');
            name = name.replace(/&gt;/g, '>');
        }
		$(div).find($(".retrievalInter")).val(name);
//		if(isEmpty(param.internationalId) && isEmpty(param.menuName) && !isEmpty(param.isShow)){
//			$(div).find($(".retrievalInter")).attr("disabled",true);
//			$(div).find("button.g1-dropdown-toggle").attr("disabled",true);
//		}
		if(!isEmpty(param.isShow)){
			$(div).find($(".retrievalInter")).attr("disabled",true);
			$(div).find("button.g1-dropdown-toggle").attr("disabled",true);
		}
		$(div).find("button.g1-dropdown-toggle").on("click",function () {
			if($(this).parents("table").hasClass("datagrid-btable")){
                if($(this).parents(".g1-dropdown").hasClass("open")){
                    $(this).parents(".g1-dropdown").removeClass("open");
				}else{
                    $(this).parents(".g1-dropdown").addClass("open");
                    openAfter();
				}
			}else{
                if(!$(this).parents(".g1-dropdown").hasClass("open")){
                    openAfter();
                }
			}


            function openAfter() {
                $(this).parents(".g1-dropdown").find("input").val("");
                //判断input框输入的值与选择的国际化或新增的国际化的值是否相同
                var interValue=$(div).find($(".retrievalInter")).val();
                var internationalId=$(div).find($(".retrievalInter")).attr("interId").toLowerCase();
                if(isEmpty(internationalId) || internationalId=="undefined"){
                    /*international.cn="";
                    international.en="";
                    international.jp="";
                    international.other1="";
                    international.other2="";*/
                }else{
                    international=dx.i18n.message[internationalId];
                }
                var language_id=dx.user.language_id;
                if(!isEmpty(international)){
                    if(interValue!=international[language_id]){
                    	$(div).find($("input[name=cn]")).val("");
                        $(div).find($("input[name=en]")).val("");
                        $(div).find($("input[name=jp]")).val("");
                        $(div).find($("input[name=other1]")).val("");
                        $(div).find($("input[name=other2]")).val("");
                        $(div).find($("input[name="+language_id+"]")).val(interValue);
                        international={};
                    }else{
                        $(div).find($("input[name=cn]")).val(international.cn);
                        $(div).find($("input[name=en]")).val(international.en);
                        $(div).find($("input[name=jp]")).val(international.jp);
                        $(div).find($("input[name=other1]")).val(international.other1);
                        $(div).find($("input[name=other2]")).val(international.other2);
                    }
                }else{
                    $(div).find($("input[name="+language_id+"]")).val(interValue);
                }
            }
		});
		
		//检索	
		retrieval(div, '.retrievalInter',param);
		//将input框里面的值带到弹出框里
		$(div).find(".g1-dropdown>.g1-dropdown-toggle").on("click",function(){
			var internationalId=$(div).find($(".retrievalInter")).attr("interId");
			if(isEmpty(internationalId)){
				$(div).find($("input[name=cn]")).val("");
                $(div).find($("input[name=en]")).val("");
                $(div).find($("input[name=jp]")).val("");
                $(div).find($("input[name=other1]")).val("");
                $(div).find($("input[name=other2]")).val("");
				var internationalValue= $(div).find($(".retrievalInter")).val();
				$(div).find($("input[name="+dx.user.language_id+"]")).val(internationalValue);
			}
		});
		//保存国际化
		$(div).find(".saveInternational").on("click",function(){
			var addInternational=[];
			var internationalId=$(div).find($(".retrievalInter")).attr("interId");
			if(isEmpty(internationalId) || internationalId=="undefined"){
				internationalId=uuid(12,16);
			}
			international={};
			international.international_id=internationalId.toLowerCase();
			international.cn= $(div).find($("input[name=cn]")).val();
			international.en= $(div).find($("input[name=en]")).val();
			international.jp= $(div).find($("input[name=jp]")).val();
			international.other1= $(div).find($("input[name=other1]")).val();
			international.other2= $(div).find($("input[name=other2]")).val();
			addInternational.push(international);
			postJson('/international/saveRetrievalInter.do', {insert: addInternational}, function (data) {
				 dxToastAlert(msg('theSaved'));
				 $(div).find($(".retrievalInter")).attr("interId",internationalId.toLowerCase());
				 var language_id=dx.user.language_id;
				 var interValue=$(div).find($("input[name="+language_id+"]")).val();
				 $(div).find($(".retrievalInter")).val(interValue);
				 dx.i18n.message[international.international_id.toLowerCase()]=international;
				 
				//返回数据
					if(!isEmpty(param) && !isEmpty(param.onSubmit)){
						param.onSubmit(international);
					}
			});
			
	    });
	}
	
	
	//检索
	function retrieval(div,retrievalInter,param){
		var to;
		var leftSearch = $(div).find($(retrievalInter));
		leftSearch.keyup(function () {
			if (to) {
				clearTimeout(to);
			}
			to = setTimeout(function () {
				var retrieveValue = leftSearch.val();
				postJson('/international/retrievalInter.do', {retrieveValue: retrieveValue}, function (data) {
					$(div).find($(".g1-dropdown-menu-tip")).css({display:"block"});
					$(document).click(function () {
                        $(".g1-dropdown-menu-tip").css({display:"none"});
                    });
                    $(div).find($(".g1-dropdown-menu-tip")).css({display:"block"});

                    $(div).find($(".g1-dropdown-menu-tip li")).remove();
                    data = replaceParam(data);
					 if (data) {
			             for (var i = 0; i < data.length; i++) {
			            	 interId=data[i].international_id;
			            	 if(!isEmpty(dx.i18n.message[interId.toLowerCase()])){
			            		 if(!isEmpty(dx.i18n.message[interId.toLowerCase()][dx.user.language_id])){
			            			 var i18Name = dx.i18n.message[interId.toLowerCase()][dx.user.language_id];
			            			 i18Name = replaceParam(i18Name);
			            			 $(div).find($(".g1-dropdown-menu-tip")).append("<li class='chooseInter' interId='" + interId + "'>" 
					            			 + i18Name + "</li>");
				            	 }
			            	 }
			             }
			             $(div).find($(".chooseInter")).on("click",function(){
			            	 $(div).find($(".g1-dropdown-menu-tip")).css({display:"none"});
			            	 chooseInter(div,$(this).attr("interId"),$(this).html(),param);
			             });
			         }
				});
			}, 250);
		});
	}
	function chooseInter(div,interId,interValue,param){
		 var internationalMsg=dx.i18n.message[interId];
		 international={};
		 international.international_id=interId;
	  	 international.cn= internationalMsg.cn;
		 international.en= internationalMsg.en;
		 international.jp= internationalMsg.jp;
		 international.other1= internationalMsg.other1;
		 international.other2= internationalMsg.other2;
		 $(div).find($(".retrievalInter")).attr("interId",interId);
        var name = '';
        if (!isEmpty(interValue)){
            name = interValue.replace(/&lt;/g, '<');
            name = name.replace(/&gt;/g, '>');
        }
		 $(div).find($(".retrievalInter")).val(name);
			//返回数据
		 if(!isEmpty(param) && !isEmpty(param.onSelect)){
			 param.onSelect(international);
		 }
	}
	function getData(div){
		var interValue=$(div).find($(".retrievalInter")).val();
		interValue = interValue.replace("<", "&lt;");
		interValue = interValue.replace(">", "&gt;");
		var internationalId=$(div).find($(".retrievalInter")).attr("interId").toLowerCase();
		if(isEmpty(internationalId) || internationalId=="undefined"){
			international={};
			international.international_id="";
			international.interValue=interValue;
			return international;
		}
		international=dx.i18n.message[internationalId];
        var language_id=dx.user.language_id;
		if(isEmpty(international)){
			$(div).find($(".retrievalInter")).attr("interId","");
			international={};
			international.international_id="";
			international.interValue=interValue;
			return international;
		}else{
			if(interValue!=international[language_id]){
				$(div).find($(".retrievalInter")).attr("interId","");
				international={};
				international.international_id="";
				international.interValue=interValue;
				return international;
			}else{
				return international;
			}
		}
	}
    $.fn.extend({
		internationalControl: function(param){
			var that=$(this);
			if (param == true){
				return {
                    getData :  function(){
                    	return getData(that);
                    }
					
                }
			}else{
				appendControl($(this), param);
			}
        }
    });
  //采番菜单Id
    function uuid(len, radix) {
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



