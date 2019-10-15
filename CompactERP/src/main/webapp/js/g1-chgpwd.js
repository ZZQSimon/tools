/**
 * User: huyannan
 * Date: 5/26/14
 * Time: 9:40 AM
 */

"use strict";

function changepassword(formId){
	var form = getFormModel(formId);
	//隐藏密码
	if($(".New_Pwd_Show").hasClass("active")){
		$(".New_Pwd_Show").removeClass("active");
		form.get().find(".New_Pwd_Text").hide();
	    var hidepwd=form.get().find(".New_Pwd_Text").val();
	    $.each(form.fieldIds, function (i, fid) {
	    	var field = w(fid);
	    	if(field.value=="NewPassword"){
	            $('#' + field.id).show();
	    		$('#' + field.id).val(hidepwd);
	    	}
	    });
	}
	if($(".Repeat_New_Pwd_Show").hasClass("active")){
		$(".Repeat_New_Pwd_Show").removeClass("active");
	     var hidepwd=form.get().find(".Repeat_New_Pwd_Text").val();
	     form.get().find(".Repeat_New_Pwd_Text").hide();
	     $.each(form.fieldIds, function (i, fid) {
	     	var field = w(fid);
	     	if(field.value=="RepeatPassword"){
	     		$('#' + field.id).val(hidepwd);
	             $('#' + field.id).show();
	     	}
	     });
	}
	var data = {};
	$.each(form.fieldIds, function (i, fid) {
		var field = w(fid);
		data[field.value] = $('#' + field.id).val();
				
	});
	postJson('/changepassword/edit/' + formId + '.do', data,
			function (retData) {
				if(retData.ret == 'true'){
					messageBox(retData.msg);
					//处理成功的场合，将画面值清空
					clear(formId);
				}else{
					//处理失败的场合，报message
					messageBox(retData.msg);
				}
				
			});
}

//将画面上的值清空
function clear(formId){
	var form = getFormModel(formId);
	var data = {};
	$.each(form.fieldIds, function (i, fid) {
		var field = w(fid);
		data[field.id] = $('#' + field.id).val("");
				
	});
}

function changepasswordClose(formId){
	var form = getFormModel(formId);
	form.p('updated', true);
	closeTab(findFormLi(form.id));
}

//显示密码
function New_Pwd_Show(formId){
	var form = getFormModel(formId);
	var data = {};
	if($(".New_Pwd_Show").hasClass("active")){//显示
        $(".New_Pwd_Show").removeClass("active");
        //隐藏密码
        form.get().find(".New_Pwd_Text").hide();
        var hidepwd=form.get().find(".New_Pwd_Text").val();
        $.each(form.fieldIds, function (i, fid) {
        	var field = w(fid);
        	if(field.value=="NewPassword"){
                $('#' + field.id).show();
        		$('#' + field.id).val(hidepwd);
        	}
        });
	}else{
        $(".New_Pwd_Show").addClass("active");
        $.each(form.fieldIds, function (i, fid) {
            var field = w(fid);
            if(field.value=="NewPassword") {
                data[field.value] = $('#' + field.id).val();
                $('#' + field.id).hide();
            }
        });
        form.get().find(".New_Pwd_Text").show();
        form.get().find(".New_Pwd_Text").val(data.NewPassword);
	}

}
function Repeat_New_Pwd_Show(formId){
	var form = getFormModel(formId);
	var data = {};

    if($(".Repeat_New_Pwd_Show").hasClass("active")) {//显示
        $(".Repeat_New_Pwd_Show").removeClass("active");
        //隐藏密码
        var hidepwd=form.get().find(".Repeat_New_Pwd_Text").val();
        form.get().find(".Repeat_New_Pwd_Text").hide();
        $.each(form.fieldIds, function (i, fid) {
        	var field = w(fid);
        	if(field.value=="RepeatPassword"){
        		$('#' + field.id).val(hidepwd);
                $('#' + field.id).show();
        	}
        });

    }else{
        $(".Repeat_New_Pwd_Show").addClass("active");
        $.each(form.fieldIds, function (i, fid) {
            var field = w(fid);
            if(field.value=="RepeatPassword") {
                data[field.value] = $('#' + field.id).val();
                $('#' + field.id).hide();
            }
        });
        form.get().find(".Repeat_New_Pwd_Text").show();
        form.get().find(".Repeat_New_Pwd_Text").val(data.RepeatPassword);
	}
	

}
