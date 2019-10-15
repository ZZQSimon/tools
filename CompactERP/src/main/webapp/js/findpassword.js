/**
 * 切换验证方式
 */
$(".by-eamil").on("click",function(){
	$("input[type='text']").val("");
	$("#fp-phone").removeClass("errorItem");
    $("#fp-phone+.errorTip").remove();
	var type=$(".valid-type").attr("value");
	if(type=="phone"){
		$(".valid-type").text("通过手机找回 >");
		$(".valid-txt").text("邮箱验证");
		$("label[for='fp-phone']").text("用户邮箱");
		$(".phone").attr("placeholder","请输入用户邮箱");
		$("label[for='fp-phone-vail']").text("邮箱验证");
		$(".valid-type").attr("value","email");
	}else{
		$(".valid-type").text("通过邮箱找回 >");
		$(".valid-txt").text("手机验证");
		$("label[for='fp-phone']").text("手机号码");
		$(".phone").attr("placeholder","请输入手机号码");
		$("label[for='fp-phone-vail']").text("短信验证");
		$(".valid-type").attr("value","phone");
	}
});

/**
 * 获取验证码
 */
$(".fp-send-vail").on("click",function () {
	 var type=$(".valid-type").attr("value");
	 var patt=type=="phone"?/^1\d{10}$/:/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/;
	 if(patt.test($.trim($("#fp-phone").val()))){
		 sendValidCode();
	     $("#fp-phone").removeClass("errorItem");
	     $("#fp-phone+.errorTip").remove();
	     $(".fp-send-vail").removeAttr("disabled");
	
	     var sendtime = 60;
	     var clock="";
	     $(this).attr("disabled", "true");
	     $(this).val(sendtime + 's后重新获取');
	     clock = setInterval(doLoop, 1000);
	     
	     function doLoop(){
	         sendtime--;
	         if(sendtime > -1){
	        	 $(".fp-send-vail").attr("disabled", "true");
	             $(".fp-send-vail").val(sendtime+'s后重新获取');
	         }else{
	             clearInterval(clock);
	             $(".fp-send-vail").removeAttr("disabled");
	             $(".fp-send-vail").val('发送验证码');
	             sendtime = 60;
	         }
	     }
	 }else{
		 var error=type=="phone"?"手机号码":"邮箱地址";
	     $("#fp-phone+.errorTip").remove();
	     $("#fp-phone").addClass("errorItem");
	     $("#fp-phone").after('<span class="errorTip">请输入正确的'+error+'!</span>');
	 }
});


/**
 * 控制手机表单的跳转流程
 */
 $("#fp-phone-reg").on("click",function () {
     if(fpvailform(".fp-form-start")){
         var code=$("#fp-phone-vail").val();
         $.ajax({
             url:getRootPath()+"/auth/checkVaildCode.do",
             data:{code:code},
             success : function(data) {
                 if(!data.success){
                     if(data.data){
                         $("#fp-phone-vail").addClass("errorItem");
                         $(".fp-send-vail").after('<i class="errorTip">'+data.data+'</i>');
                     }
                 }else{

                     $("#fp-phone-vail").addClass("errorItem");
                     $(".fp-send-vail+.errorTip").remove();

                     $(".fp-form-start").removeClass("active");
                     $(".fp-form-set").addClass("active");
                     $(".find-phone .fp-top>div").addClass("show-color");
                     $(".find-phone .pholine").addClass("phtline");
                 }
             }
        });
     }
 });
 
//密码重置
$("#fp-phone-ok").on("click",function () {
	if(setVailForm(".fp-form-set")){
	    var password=$("#fp-phone-newp").val();
	     $.ajax({
	         url:getRootPath()+"/auth/updatePassword.do",
	         data:{password:password},
	         success : function(data) {
	             if(!data.success){
                     var msg = data.data;
                     if (msg != null) {
                         msg = msg.replace(/&quot;/g, '"');
                         msg = msg.replace(/&#39;/g, '\'');
                         msg = msg.replace(/&lt;/g, '<');
                         msg = msg.replace(/&gt;/g, '>');
                         msg = msg.replace(/&#40;/g, '(');
                         msg = msg.replace(/&#41;/g, ')');
                     }
	                 alert(msg);
	             }else{
	                 $("#fp-phone-ok").after();
	                 $(".reg_success_form").submit();
	             }
	         }
	     });
	}
});
	
function getRootPath(){
	 return makeLoginUrl('');
}


function fpvailform(data) {
	 var type=$(".valid-type").attr("value");
	 var patt=type=="phone"?/^1\d{10}$/:/^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/;
	 var error=type=="phone"?"手机号码":"邮箱地址";
	 
	 $(data).find(".errorTip").remove();
	 var canDo=false;
	 var phone = $(data).find("input.phone").val();
	 var vail=$(data).find("input.vail").val();
	
	 var testPhone;
	 var testVail;
	
	 if (patt.test($.trim(phone))) {
	     $(data).find("input.phone").removeClass("errorItem");
	     testPhone = true;
	 } else {
	     $(data).find("input.phone").addClass("errorItem");
	     $(data).find("input.phone").after('<span class="errorTip">请输入正确的'+error+'!</span>');
	     testPhone = false;
	 }
	 if($.trim(vail)!=""){
	     $(data).find("input.vail").removeClass("errorItem");
	     testVail=true;
	 }else{
	     $(data).find("input.vail").addClass("errorItem");
	     $(data).find("input.vail").after('<i class="errorTip">验证码不能为空！</i>');
	     testVail=false;
	 }
	
	 if (testPhone && testVail ) {
	     canDo = true;
	 }
	
	 return canDo;
}


function setVailForm(data) {
	 $(data).find(".errorTip").remove();
	 var canDo=false;
	
	 var password=$(data).find("input.password").val();
	 var repassword=$(data).find("input.repassword").val();
	
	 var testPass;
	 var testRePass;
	
	 if(/.{6,10}/.test($.trim(password))){
	     $(data).find("input.password").removeClass("errorItem");
	     testPass=true;
	 }else{
	     $(data).find("input.password").addClass("errorItem");
	     $(data).find("input.password").after('<i class="errorTip">请输入6-10位字符！</i>');
	     testPass=false;
	 }
	
	 if($.trim(repassword)==$.trim(password)){
	     $(data).find("input.repassword").removeClass("errorItem");
	     testRePass=true;
	 }else{
	     $(data).find("input.repassword").addClass("errorItem");
	     $(data).find("input.repassword").after('<i class="errorTip">两次密码不相符！</i>');
	     testRePass=false;
	 }
	
	 if (testPass && testRePass ) {
	     canDo = true;
	 }
	 return canDo;
}

function sendValidCode() {
	var address =$("#fp-phone").val();
	var type=$(".valid-type").attr("value");
	$.ajax({
		url:getRootPath()+"/auth/sendValidCode.do",
		data:{address:address,type:type},
		success : function(data) {
			if(!data.success){
				$('.fp-form-start .phone+.errorTip').remove();
			    $('.fp-form-start').find("input.phone").addClass("errorItem");
			    $('.fp-form-start').find("input.phone").after('<span class="errorTip">'+data.data+'</span>');
				
			}else{
	     	    $('.fp-form-start').find("input.phone").removeClass("errorItem");
				$('.fp-form-start .phone+.errorTip').remove(); 
			}
		}
	});
        
}
