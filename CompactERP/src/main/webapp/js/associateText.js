(function($){
	 function dxLog(msg){
	        console.log(msg);
	    }
	 function Hide(){
		 document.getElementById(elementId)
	 }
	 //下拉查询
	 function inputValueSelect(that,param){
		 if(param != null || param != ''){
			$(that).append("<div class = 'param'></div>");
			$(that).find('input').keyup(function(){
				newSearch(that,param);
			})
		 }
	 }
	 function newSearch(that,param){
		 //获取input框输入的值
		/*var name = $(that).val();
		 var arr = [];
		 $(that).find('input').next().empty();
		 //遍历匹配相符的值
		 for(var i = 0;i<param.length;i++){
			 if(param[i].indexOf(name)>=0){
				 $(that).find('input').next().append(" <span class = 'item'>"+param[i]+"</span></br>");
				 arr.push(param[i]);
			 }
		 }
		 //绑定替换
		 $(that).find('.param').find(".item").on("click",function(){
			var dd=$(this).html();
			 $(that).find('input').val($(this).html());
			 $(that).find('input').next().empty();
		 })*/
			newChange(that,param);
	 }
	 
	function newChange(that,param){
		 //获取替换后input框的值
		 var newName =  $(that).find('input').val();
		 //判断字符串是否包含空格
		 //$ # () {} . "" '' _
		 function Object(str,type){
			 this.Str = str;
			 this.Type = type;
		 }
		 var arrs = [];
			 //最后一个包含特殊字符
				if(newName.lastIndexOf("$")>=0){
					var str1 = newName.lastIndexOf("$");
					arrs.push(new Object(str1,"$"));
				}
				if(newName.lastIndexOf("#")>=0){
					var str2 = newName.lastIndexOf("#");
					arrs.push(new Object(str2,"#"));
				}
				if(newName.lastIndexOf("(")>=0){
					var str3 = newName.lastIndexOf("(");
					arrs.push(new Object(str3,"("));
				}
				if(newName.lastIndexOf(")")>=0){
					var str4 = newName.lastIndexOf(")");
					arrs.push(new Object(str4,")"));
				}
				if(newName.lastIndexOf(".")>=0){
					var str5 = newName.lastIndexOf(".");
					arrs.push(new Object(str5,"."));
				}
				if(newName.lastIndexOf(" \" ")>=0){
					var str6 = newName.lastIndexOf("\"");
					arrs.push(new Object(str6," \" "));
				}
				if(newName.lastIndexOf(" \' ")>=0){
					var str7 = newName.lastIndexOf("\'");
					arrs.push(new Object(str7," \' "));
				}
				if(newName.lastIndexOf(" ")>=0){
					var str8 = newName.lastIndexOf(" ");
					arrs.push(new Object(str8," "));
				}
				if(newName.lastIndexOf("[")>=0){
					var str9 = newName.lastIndexOf("[");
					arrs.push(new Object(str9,"["));
				}
				if(newName.lastIndexOf("]")>=0){
					var str10 = newName.lastIndexOf("]");
					arrs.push(new Object(str10,"]"));
				}
				if(newName.lastIndexOf("{")>=0){
					var str11 = newName.lastIndexOf("{");
					arrs.push(new Object(str11,"{"));
				}
				if(newName.lastIndexOf("}")>=0){
					var str12 = newName.lastIndexOf("}");
					arrs.push(new Object(str12,"}"));
				}
				var max=0;
				var type;
				for(var i=0;i<arrs.length;i++){
					if(max < arrs[i].Str){
						max = arrs[i].Str;
						type = arrs[i].Type;
					}
				}
				var dName = newName.substring(max+1,newName.length);
				 var arr = [];
				 $(that).find('input').next().empty();
				 //遍历匹配相符的值
				 for(var i = 0;i<param.length;i++){
					 if(param[i].indexOf(dName)>=0){
						 $(that).find('input').next().append(" <span class = 'item'>"+param[i]+"</span></br>");
						 arr.push(param[i]);
					 }
				 }
				 //绑定替换
				 $(that).find('input').next().find(".item").on("click",function(){
					 if(max ==0){
						 var  ss = $(this).html();
						 $(that).find('input').val(ss);
						  $(that).find('input').next().empty();
					 }else{
					 var ss1 = $(that).find('input').val().substring(0,max+1);
					 var  ss = ss1+$(this).html();
					 $(that).find('input').val(ss);
					  $(that).find('input').next().empty();
					 }
				 })
		 return dName;
	}
	function getData(that){
		var data = {};
		data = $(that).find('input').val();
		return data;
	}
 $.fn.extend({
        lenovoText : function(param,type){
        	if(param == true){
        		 return {
        			 getData : function (){
                         return getData(that);}
                 }
        	}else{
	        	//获取input的值
	            var that= this;
	            if(type == 'input'){
	            	$(that).append(" <input type = 'text' name = 'input'/>");
	            }
	            if(type == "textarea"){
	            	$(that).append(" <textarea> </textarea>");
	            }
	            if(param != null ){
	            			 getCursortPosition(that,param);
	            }else {
					dxLog("array is null or undefine");
				}
        	}
        }
    });
 //获取光标在文本框的位置
	function getCursortPosition(that,param){
		//var pos = 0;//设置初始光标位置
		 var oTxt1 = document.getElementsByTagName(that);
         var cursurPosition=-1;
         if(oTxt1.selectionStart){//非IE浏览器
             cursurPosition= oTxt1.selectionStart;
         }
         inputValueSelect(that,param);
		return (cursurPosition);
	}
})(jQuery);