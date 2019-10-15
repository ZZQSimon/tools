function getRootPath() {
    return makeLoginUrl('');
}
function verification() {
    document.getElementById("verificationIMG").src = getRootPath() + "/auth/loginVerification.do?" + Math.random();
}
function changeLanguage(){
	var languageSelect = $(".languageSelect").val();
	localStorage.setItem("login_language", languageSelect);
	window.location.href = "login.view?language="+languageSelect;
  }
function replaceParamLogin(data){
    if (data == null || data == undefined)
        return;
    if (typeof data == 'object'){
        for (var key in data){
            data[key] = replaceParamLogin(data[key]);
        }
    }else if (typeof data == 'array'){
        for (var i=0; i<data.length; i++){
            data[i] = replaceParamLogin(data[i])
        }
    }else if (typeof data == 'string'){
        //data = data.replace(/&/g, '&amp;');
        data = data.replace(/"/g, '&quot;');
        data = data.replace(/'/g, '&#39;');
        data = data.replace(/</g, '&lt;');
        data = data.replace(/>/g, '&gt;');
        return data;
    }
    return data;
}
var isEmailDetail = $('.emailDetail_isEmailDetail').val();
if (isEmailDetail == 'emailDetail'){
    var user_name = $('.emailDetail_user_name').val();
    var pwd = $('.emailDetail_pwd').val();
    var domain = $('.emailDetail_domain').val();
    $('#username').val(user_name + '@' + domain+ '@' + localStorage.getItem("login_language"));
    $('#password').val(pwd);
    $('.form-horizontal').submit();
}
$('body').keydown(function (e) {
    if (e.keyCode == "13") {
        $('.btn-OK.login-btn').click();
    }
});

$('.btn-OK.login-btn').on('click', function () {
    var username = $('#susername').val();
    var password = $('#password').val();
    var verifyCode = $('#loginvail').val();
    var languageSelect = $(".languageSelect").val();
	localStorage.setItem("login_language", languageSelect);
    $.ajax({
        url: getRootPath() + "/auth/checkUserName.do",
        type: 'post',
        data: {username: username, password: password, verifyCode: verifyCode},
        success: function (data) {
            if (!data.success) {
                // alert(data.data);
                $("form[name=loginForm] .errortip").remove();
                $("form[name=loginForm]").append("<i class='errortip'>" + replaceParamLogin(data.data) + "</i>");

                verification();
                return;
            } else {
                if (typeof data.data == "string") {
                    $('#username').val($('#susername').val() + '@' + data.data + '@' + localStorage.getItem("login_language"));
                    $('.form-horizontal').submit();
                } else {
                    $("form[name=loginForm] .errortip").remove();
                    if (data.data.length > 0) {
                    	if(data.data.length==1){
                    		$('#username').val(replaceParamLogin(data.data[0].user_id) + '@' + replaceParamLogin(data.data[0].master_id) 
                    				+ '@' + localStorage.getItem("login_language"));
                            $('.form-horizontal').submit();
                    	}else{
                    		company(data.data);
                    	}
                    } else {
                        $('.login-select-con').show();
                        $('.form-horizontal').hide();
                        $(".login-select-main").css("display", "none");
                        $(".login-no-select").css("display", "block");
                    }
                }
            }
        }
    });
    function company(data) {
        var $company = $('.login-select-con .user_company');
        for (var i = 0; i < data.length; i++) {
            $company.append('<a href="#"><input type="hidden" value="' +replaceParamLogin(data[i].master_id) + '"/><li>' +
            replaceParamLogin(data[i].master_id) + '</li></a>');
        }
        $('.login-select-con').show();
        $('.form-horizontal').hide();
        $company.find('li').on('click', function () {
            var company = $(this).parent().find('input').val();
            $('#username').val($('#susername').val() + '@' + replaceParamLogin(company)+ '@' + localStorage.getItem("login_language"));
            $('.form-horizontal').submit();
        });
    }
});
$(function () {
    var sapId = $('.sap_id').val();
    if (sapId != null && sapId != undefined){
        $.ajax({
            url: getRootPath() + "/auth/sapLogin.do",
            type: 'post',
            data: {sapId: sapId},
            success: function (data) {
                if (data != null && data != undefined && data != ''){
                    $.busyLoadFull("show", { background: "rgba(0, 51, 101, 0.83)", animate: "slide" });
                    //$(".login-loading").busyLoad("show", {spinner: "circle-line"});
                    $('#username').val(data.username + '@' + data.domain + '@' + data.languageId);
                    $('#password').val(data._pwd);
                    $('.form-horizontal').submit();
                }
            }
        });
    }
    var subjectID = $('#subjectID').val();
    var backgroundID = $('#backgroundID').val();
    $('.login-Modal-body .row span').css({'display': 'none'});
    if (subjectID == null || subjectID == "" || subjectID == "undefined") {
        subjectID = "yellow";
    }
    $("#" + subjectID).find("span").css({'display': 'block'});
    $('#subjectID').val(subjectID);
    localStorage.setItem('subject', subjectID);
    localStorage.setItem('background', backgroundID);
    $("#select-pic").change(function (e) {
        $("#file-pic").css({"display": "block"});

        /*var imgShow  = document.querySelector("#file-pic");
         var file = document.querySelector('input[type=file]').files[0];

         var objUrl = getObjectURL(file) ;
         if (objUrl) {
         imgShow.attr("src", objUrl);
         }

         function getObjectURL(file) {
         var url = null;
         if (window.createObjectURL != undefined) { // basic
         url = window.createObjectURL(file);
         } else if (window.URL != undefined) { // mozilla(firefox)
         url = window.URL.createObjectURL(file);
         } else if (window.webkitURL != undefined) { // webkit or chrome
         url = window.webkitURL.createObjectURL(file);
         }
         return url;
         }
         */


        var preview = document.querySelector("#file-pic");
        var file = document.querySelector('input[type=file]').files[0];
        var reader = new FileReader();
        reader.onloadend = function () {

            preview.src = reader.result;
            $("#login-body").css("background", reader.result);
        };
        if (file) {
            reader.readAsDataURL(file);
        } else {
            preview.src = "";
        }
    });
    var check = 1;
    $(".checkbox label span").click(function (event) {
        if (check == 1) {
            console.log(22);
            $(".checkbox label span").addClass("checked");
            $(".checkbox label input").attr({"checked": true});
            check = 0;
        } else {
            console.log(33);
            $(".checkbox label span").removeClass("checked");
            $(".checkbox label input").attr({"checked": false});
            check = 1;
        }

    });
    $(".picture-blur-slider").slider({
        range: "min",
        value: 0,
        min: 1,
        max: 100,
        slide: function (event, ui) {
            $(".picture-blur-amount").val(ui.value);
            var blur = "blur(" + ui.value/20 + "px)";

            $(".select-bg-body img").css({
                // "filter": "url(blur.svg#blur)",
                "-webkit-filter": blur, "-moz-filter": blur,
                "-ms-filter": blur, "filter": blur
            });
        }
    });
    $(".picture-blur-amount").val($(".picture-blur-slider").slider("value"));
});