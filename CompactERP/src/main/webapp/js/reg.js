 $('input[name="entryDate"]').datepicker({
	format: "yyyy-mm-dd",
	autoclose: true
});
function getRootPath() {
    return makeLoginUrl('');
}
var regDia = {
    processing: (function () {
        var $div = $('<div class="progress"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"><span class="sr-only"></span></div></div>');
        var dialog;
        return {
            open: function (title) {
                if (dialog) return;
                dialog = BootstrapDialog.show({
                    title: (title ? title : 'Processing'),
                    closable: false,
                    message: $div
                });
            },
            close: function () {
                if (!dialog) return;
                dialog.close();
                dialog = null;
            },
            isOpen: function () {
                return dialog != null;
            }
        };
    })()
};

function vailform(data) {
    $(data).find(".errorTip").remove();
    var canDo = false;
    var noempty = $(data).find("input.noempty").val();
    var corpaccount = $(data).find("input.corpaccount").val();
    var password = $(data).find("input.password").val();
    // var repassword = $(data).find("input.repassword").val();
    var username = $(data).find("input.username").val();
    var email = $(data).find("input.email").val();
    var vail = $(data).find("input.vail").val();
    var radio = $(data).find("input.radio");
    var phone = $(data).find("input.phone").val();

    /*if(/^[0-9a-zA-Z_]{3,10}$/.test($.trim(corpaccount))){
     $(data).find("input.corpaccount").removeClass("errorItem");
     canDo=true;
     }else{
     $(data).find("input.corpaccount").addClass("errorItem");
     $(data).find("input.corpaccount").after('<i class="errorTip">格式有误!</i>');
     canDo=false;
     }*/

    var testRadio;
    var testUser;
    var testPass;
    // var testRePass;
    var testVail;
    var testPhone;

    if ($(data).find("input.radio").is(":checked")) {
        testRadio = true;
    } else {
        $(data).find("input.radio").addClass("errorItem");
        $(".reg-agree-box>span").append('<i class="errorTip" style="margin: 0;width: 60px; line-height: 12px;">必选!</i>');
        testRadio = false;
    }

    if (/^1\d{10}$/.test($.trim(phone))) {
        $(data).find("input.phone").removeClass("errorItem");
        testPhone = true;
    } else {
        $(data).find("input.phone").addClass("errorItem");
        $(data).find("input.phone").after('<span class="errorTip">请输入正确的手机号码!</span>');
        testPhone = false;
    }

    if (/^.{1,20}$/.test($.trim(username))) {
        $(data).find("input.username").removeClass("errorItem");
        testUser = true;
    } else {
        $(data).find("input.username").addClass("errorItem");
        $(data).find("input.username").after('<span class="errorTip">请输入1-20位字符!</span>');
        testUser = false;
    }

    if (/^.{6,10}$/.test($.trim(password))) {
        $(data).find("input.password").removeClass("errorItem");
        testPass = true;
    } else {
        $(data).find("input.password").addClass("errorItem");
        $(data).find("input.password").after('<i class="errorTip" style="width:250px;">请输入6-10位字符！</i>');
        testPass = false;
    }

    /*if ($.trim(repassword) == $.trim(password)) {
        $(data).find("input.repassword").removeClass("errorItem");
        testRePass = true;
    } else {
        $(data).find("input.repassword").addClass("errorItem");
        $(data).find("input.repassword").after('<i class="errorTip">两次密码不相符！</i>');
        testRePass = false;
    }*/

    /* if(/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test($.trim(email))){
     $(data).find("input.email").removeClass("errorItem");
     canDo=true;
     }else{
     $(data).find("input.email").addClass("errorItem");
     $(data).find("input.email").after('<i class="errorTip">请输入正确的email格式！</i>');
     canDo=false;
     }*/

    if ($.trim(vail) != "") {
        $(data).find("input.vail").removeClass("errorItem");
        testVail = true;
    } else {
        $(data).find("input.vail").addClass("errorItem");
        $(data).find("input.vail").after('<i class="errorTip">验证码不能为空！</i>');
        testVail = false;
    }

    /* if($.trim(noempty)!=""){
     $(data).find("input.noempty").removeClass("errorItem");
     canDo=true;
     }else{
     $(data).find("input.noempty").addClass("errorItem");
     $(data).find("input.noempty").after('<i class="errorTip">必填项!</i>');
     canDo=false;
     }*/
    if (testRadio && testPhone && testUser && testPass && testVail) {
        canDo = true;
    }
    return canDo;
}
function corpvailform(data) {
    $(data).find(".errorTip").remove();
    var canDo = false;
    var noempty = $(data).find("input.noempty").val();
    var corpaccount = $(data).find("input.corpaccount").val();

    var testCorp;
    var testNoempty;
    if (/^[a-zA-Z][a-zA-Z0-9]{2,9}$/.test($.trim(corpaccount))) {
        $(data).find("input.corpaccount").removeClass("errorItem");
        testCorp = true;
    } else {
        $(data).find("input.corpaccount").addClass("errorItem");
        $(data).find("input.corpaccount").after('<i class="errorTip">账号格式有误!</i>');
        testCorp = false;
    }

    if ($.trim(noempty) != "") {
        $(data).find("input.noempty").removeClass("errorItem");
        testNoempty = true;
    } else {
        $(data).find("input.noempty").addClass("errorItem");
        $(data).find("input.noempty").after('<i class="errorTip">必填项!</i>');
        testNoempty = false;
    }

    if (testCorp && testNoempty ) {
        canDo = true;
    }
    return canDo;
}

function corpJoinform(data) {
     $(data).find(".errorTip").remove();
     var canDo=false;

    var vail = $(data).find("input.vail").val();
    // var radio = $(data).find("input.radio");
    var phone = $(data).find("input.phone").val();

     var testPhone;
     var testVail;
     // var testRadio;

     if (/^1\d{10}$/.test($.trim(phone))) {
         $(data).find("input.phone").removeClass("errorItem");
         testPhone = true;
     } else {
         $(data).find("input.phone").addClass("errorItem");
         $(data).find("input.phone").after('<span class="errorTip">请输入正确的手机号码!</span>');
         testPhone = false;
     }
     if ($.trim(vail) != "") {
         $(data).find("input.vail").removeClass("errorItem");
         testVail = true;
     } else {
         $(data).find("input.vail").addClass("errorItem");
         $(data).find("input.vail").after('<i class="errorTip">验证码不能为空！</i>');
         testVail = false;
     }

     if ( testPhone && testVail) {
         canDo = true;
     }
     return canDo;
 }
function staffJoinform(data) {
    $(data).find(".errorTip").remove();
    var canDo=false;

    var password = $(data).find("input.password").val();
    var username = $(data).find("input.username").val();
    var radio = $(data).find("input.radio");

    var testUser;
    var testPass;
    var testRadio;

    if (/^.{1,20}$/.test($.trim(username))) {
        $(data).find("input.username").removeClass("errorItem");
        testUser = true;
    } else {
        $(data).find("input.username").addClass("errorItem");
        $(data).find("input.username").after('<span class="errorTip">请输入1-20位字符!</span>');
        testUser = false;
    }

    if (/^.{6,10}$/.test($.trim(password))) {
        $(data).find("input.password").removeClass("errorItem");
        testPass = true;
    } else {
        $(data).find("input.password").addClass("errorItem");
        $(data).find("input.password").after('<i class="errorTip" style="250px;">请输入6-10位字符！</i>');
        testPass = false;
    }

    if ($(data).find("input.radio").is(":checked")) {
        testRadio = true;
    } else {
        $(data).find("input.radio").addClass("errorItem");
        $(".reg-agree-box>span").append('<i class="errorTip" style="margin: 0;width: 60px; line-height: 12px;">必选!</i>');
        testRadio = false;
    }

    if (testRadio && testUser && testPass ) {
        canDo = true;
    }
    return canDo;
}

//显示隐藏密码
$(".passimg").on("click",function () {
     var type=$(this).parent().find("input").attr("type");
     if(type=="password"){
         $(this).parent().find("input").attr("type","text");
     }else if(type=="text"){
         $(this).parent().find("input").attr("type","password");
     }
 });

$(".reg-get-vail").on("click", function () {
    if (/^1\d{10}$/.test($.trim($(this).parents("form").find(".sendCodePhone").val()))) {

        var send=$(this).parents("form").find(".sendCodePhone");
    	getTelephone(send);

        $(".sendCodePhone").removeClass("errorItem");
        $(".sendCodePhone+.errorTip").remove();
        $(".reg-get-vail").removeAttr("disabled");
        // $("#regphone").attr("disabled", "true");

        var sendtime = 60;
        var clock = "";

        $(this).attr("disabled", "true");
        $(this).val(sendtime + 's后重新获取');
        clock = setInterval(doLoop, 1000);

        function doLoop() {
            sendtime--;
            if (sendtime > -1) {
            	$(".reg-get-vail").attr("disabled", "true");
                $(".reg-get-vail").val(sendtime + 's后重新获取');
            } else {
                clearInterval(clock);
                $(".reg-get-vail").removeAttr("disabled");
                // $("#regphone").removeAttr("disabled");
                $(".reg-get-vail").val('发送验证码');
                sendtime = 60;
            }
        }
    } else {
//        $(this).attr("disabled", "true");
        $(".sendCodePhone+.errorTip").remove();
        $(".sendCodePhone").addClass("errorItem");
        $(".sendCodePhone").after('<span class="errorTip">请输入正确的手机号码!</span>');
    }
});

function getTelephone(send) {
    console.log(send);
    var telephone = $(send).val();

    $.ajax({
        url: getRootPath() + "/auth/phoneCode.view?telephone=" + telephone,
        data: {},
        success: function (data) {  //（action处理完后返回的结果，就是局部刷新，页面不跳转）
        }
    });
}

if ($("#reg-pass-code").val() == 1) {
    //邀请新员工
    $(".reg-form-head ul").css("display", "none");
    // $(".reg-form-head .staff").css("display", "block");
    $(".left_area").removeClass("active");
    $(".join_area").addClass("active");
} else if ($("#reg-pass-code").val() == 2) {
    //上次信息未完成
    $(".content_box .section").removeClass("active");
    $(".corp-info").addClass("active");
    $(".reg-form-head").attr("name", ".corp-info");
}


$(".reg-agree-box label input").on("change", function () {
    if ($(this).is(':checked')) {
        $(".reg-agree-box label").addClass("checked");
    } else {
        $(".reg-agree-box label").removeClass("checked");
    }
});

$(".reg-invite-copy").on("click", function () {
    $(".reg-invite-addr").select();
});


//邀请员工输入手机号
 $('.join_area .tab-btn').on("click", function () {
     if (corpJoinform("#reg-join-form")) {
         var telephone = $("#regJoinPhone").val();
         var phoneCode = $("#phoneJoinCode").val();
         var that = $(this);
         $.ajax({
             url: getRootPath() + "/auth/phoneReg_emp.do",
             data: {regphone: telephone, phoneCode: phoneCode},
             success: function (data) {
                 if (!data.success) {
                     $("#phoneJoinCode").removeClass("errorItem");
                     $(".reg-vail-box .errorTip").remove();
                     if (data.data == 1) {
                         $("#phoneJoinCode").addClass("errorItem");
                         $("#phoneJoinCode").after('<i class="errorTip">验证码有误！</i>');
                     } else if (data.data == 2) {
                         $("#regJoinPhone").addClass("errorItem");
                         $("#regJoinPhone").after('<i class="errorTip">该号码已注册！</i>');
                     }else if (data.data == 3) {
                         $("#regJoinPhone").addClass("errorItem");
                         $("#regJoinPhone").after('<i class="errorTip">与发送验证码的号码不一致！</i>');
                     }
                 } else {
                     $("#regJoinPhone").removeClass("errorItem");
                     $("#regJoinPhone+.errorTip").remove();
                     $("#phoneJoinCode").removeClass("errorItem");
                     $("#phoneJoinCode+.errorTip").remove();


                     $(".content_box .section").removeClass("active");
                     if(data.data){
                    	 alert("你已加入"+data.data.slice(0,-1)+"公司!");
                         //已有信息，跳到第三步
                         var gotag =".corp-staff-info";
                         $(gotag).addClass("active");
                         $(".reg-form-head").attr("name", gotag);
                     }else{
                         //还未注册，跳第二步
                         var gotag = that.attr("name");
                         $(gotag).addClass("active");
                         $(".reg-form-head").attr("name", gotag);
                     }

                    /* var gotag = that.attr("name");
                     $(".content_box .section").removeClass("active");
                     $(gotag).addClass("active");
                     $(".reg-form-head").attr("name", gotag);*/
                 }
             }
         });
     }
 });

 //邀请员工注册输入个人信息
 $('.staff-join-info .tab-btn').on("click", function () {
     if (staffJoinform(".staff-join-main")) {
         var regname = $("#regJoinName").val();
         var password = $("#regJoinPass").val();
         var that = $(this);
         $.ajax({
             url: getRootPath() + "/auth/phoneReg.do",
             data: { password: password, regname: regname},
             success: function (data) {  //（action处理完后返回的结果，就是局部刷新，页面不跳转）
                 if (!data.success){
                     // alert(data.data);

                 } else {
                     var gotag = that.attr("name");
                     $(".content_box .section").removeClass("active");
                     $(gotag).addClass("active");
                     $(".reg-form-head").attr("name", gotag);
                 }
             }
         });
     }
 });

 $('#reg-phone-ok').on("click", function () {
     if (vailform("#reg-phone-form")) {
         console.log(vailform("#reg-phone-form"));
         var regname = $("#regname").val();
         var telephone = $("#regphone").val();
         var phoneCode = $("#phoneCode").val();
         var regpassword = $("#regpassword").val();
         var that = $(this);
         $.ajax({
             url: getRootPath() + "/auth/phoneReg.do",
             data: {regphone: telephone, password: regpassword, phoneCode: phoneCode, regname: regname},
             success: function (data) {  //（action处理完后返回的结果，就是局部刷新，页面不跳转）
                 if (!data.success) {
                     // alert(data.data);
                     $("#phoneCode").removeClass("errorItem");
                     $(".reg-vail-box .errorTip").remove();
                     if (data.data == 1) {
                         $("#phoneCode").addClass("errorItem");
                         $("#phoneCode").after('<i class="errorTip">验证码有误！</i>');
                     } else if (data.data == 2) {
                         $("#regphone").addClass("errorItem");
                         $("#regphone").after('<i class="errorTip">该号码已注册！</i>');
                     }else if (data.data == 3) {
                         $("#regphone").addClass("errorItem");
                         $("#regphone").after('<i class="errorTip">与发送验证码的号码不一致！</i>');
                     }

                 } else {
                     $("#regphone").removeClass("errorItem");
                     $("#regphone+.errorTip").remove();
                     $("#phoneCode").removeClass("errorItem");
                     $("#phoneCode+.errorTip").remove();
                     var gotag = that.attr("name");
                     $(".content_box .section").removeClass("active");
                     $(gotag).addClass("active");
                     $(".reg-form-head").attr("name", gotag);
                 }
             }
         });
     }
 });

$('.corp-info .tab-btn').on("click", function () {
    if (corpvailform(".corp-info-main")) {
        var companyId = $("input[name='companyId']").val();
        var companyName = $("input[name='companyName']").val();
        var companyName_abbreviation = $("input[name='companyName_abbreviation']").val();
        var files = $("input[name='logo']").get(0).files[0]; //获取file控件中的内容
        var fd = new FormData();
        var that = $(this);
        fd.append("companyId", companyId);
        fd.append("companyName", companyName);
        fd.append("companyName_abbreviation", companyName_abbreviation);
        fd.append("logo", files);
        regDia.processing.open("公司创建中。。。请稍后");
        $.ajax({
            type: "POST",
            contentType: false, //必须false才会避开jQuery对 formdata 的默认处理 , XMLHttpRequest会对 formdata 进行正确的处理
            processData: false, //必须false才会自动加上正确的Content-Type
            url: getRootPath() + "/auth/saveCompany.do",
            data: fd,
            success: function (data) {
                regDia.processing.close();
                if (!data.success) {
                    // alert(data.data);
                    $(".corpaccount").addClass("errorItem");
                    $(".corpaccount").after('<i class="errorTip">' + data.data + '</i>');

                } else {
                    $(".corpaccount").removeClass("errorItem");
                    $(".corpaccount+.errorTip").remove();

                    var gotag = that.attr("name");
                    $(".content_box .section").removeClass("active");
                    $(gotag).addClass("active");
                    $(".reg-form-head").attr("name", gotag);
                }
            }
        });
    }
});

$('.corp-staff-info .tab-btn').on("click", function () {
    var email = $(".corp-staff-info input[name='email']").val();
    var id = $("input[name='id']").val();
    var telePhone = $("input[name='telePhone']").val();
    var entryDate = $("input[name='entryDate']").val();
    var sex = $("select").val();
    var that = $(this);
    $.ajax({
        url: getRootPath() + "/auth/joinCompany.do",
        data: {email: email, id: id, telePhone: telePhone, entryDate: entryDate, sex: sex},
        success: function (data) {  //（action处理完后返回的结果，就是局部刷新，页面不跳转）
            if (!data.success) {
                alert(data.data);
            } else {
                var gotag = that.attr("name");
                $(".content_box .section").removeClass("active");
                $(gotag).addClass("active");
                $(".reg-invite-addr").val(data.data);
                $(".reg-form-head").attr("name", gotag);
            }
        }
    });
});

$("#reg-em-ok").on("click", function () {
    var email = $("#reg-email").val();
    var password = $("#reg-em-password").val();
    $.ajax({
        url: getRootPath() + "/auth/emailReg.view?action=register",
        data: {email: email, password: password},
        success: function (data) {
            if (!data.success) {
                alert(data.data);
            } else {
                $(".email_p").empty();
                $(".email_p").text(data.data);
                $(".form_body").removeClass("active");
                $(".email-success").addClass("active");
            }
        }
    });
});
$("#reg-em-see").on("click", function () {
    var email = $(".email_p").text();
    var email_spilt = email.split("@");
    var emailstr = email_spilt[1];
    switch (emailstr) {
        case "163.com":
            window.open("http://mail.163.com/");
            break;
        case "qq.com":
            window.open("https://mail.qq.com/cgi-bin/loginpage");
            break;
        case "126.com":
            window.open("http://www.126.com/");
            break;
        case "139.com":
            window.open("http://mail.10086.cn/");
            break;
        case "yahoo.com":
            window.open("www.baidu.com");
            break;
        case "sina.com":
            window.open("http://mail.sina.com.cn/");
            break;
        case "189.cn":
            window.open("http://webmail30.189.cn/w2/");
            break;
        case "sohu.com":
            window.open("https://mail.sohu.com/fe/#/login");
            break;
        case "aliyun.com":
            window.open("https://mail.aliyun.com/");
            break;
        case "tom.com":
            window.open("http://mail.tom.com/");
            break;
        case "outlook.com":
            window.open("https://outlook.live.com/owa/");
            break;
        case "2980.com":
            window.open("https://www.2980.com/login?callback=/");
            break;
        case "21cn.com":
            window.open("http://mail.21cn.com/w2/");
            break;
        case "188.com":
            window.open("http://www.188.com/");
            break;
        case "yeah.net":
            window.open("//www.yeah.net/");
            break;
        case "188.com":
            window.open("http://www.188.com/");
            break;
        case "188.com":
            window.open("http://www.188.com/");
            break;
        default:
            window.open("www.baidu.com");
    }
});
$("#slccorplogo").change(function (e) {
    $("#file-corplogo").css({"display": "block"});

    var preview = document.querySelector("#file-corplogo");
    var file = document.querySelector('input[type=file]').files[0];
    var reader = new FileReader();
    reader.onloadend = function () {

        preview.src = reader.result;
        $("#dx-reg-corp").css("background", reader.result);
    }
    if (file) {
        reader.readAsDataURL(file);
    } else {
        preview.src = "";
        $("#file-corplogo").css({"display": "none"});
    }
});
