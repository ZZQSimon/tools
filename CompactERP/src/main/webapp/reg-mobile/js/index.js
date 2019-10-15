/**
 * Created by Administrator on 2017/8/28.
 */
$(function () {

    $(".getvailCode").on("click",function () {
        if(/^1\d{10}$/.test($.trim($(".regphone").val()))){

            getTelephone();

            $(".regphone").removeClass("errorItem");
            $(".regphone+.errorTip").remove();
            $(this).removeAttr("disabled");


            var sendtime=60;
            var clock="";
            $(this).attr("disabled", "true");
            $(this).val(sendtime + 's后重新获取');
            clock = setInterval(doLoop, 1000);

            function doLoop(){
                sendtime--;
                if(sendtime > -1){
                    $(".getvailCode").attr("disabled", "true");
                    $(".getvailCode").val(sendtime+'s后重新获取');
                }else{
                    clearInterval(clock);
                    $(".getvailCode").removeAttr("disabled");
                    $(".getvailCode").val('发送验证码');
                    sendtime = 60;
                }
            }
        }else{
            $(".regphone").addClass("errorItem");

            $(".errorTip").show();
            $(".errorTip").html('请输入正确的手机号码!');
            setTimeout(function(){
                $(".errorTip").hide();
                $(".errorTip").html("tip");
            },1000);
        }
    });


    function getTelephone() {
        var telephone =$(".regphone").val();
        console.log(telephone);
        $.ajax({
            url:getRootPath_web("/auth/phoneCode.view?telephone="+telephone),
            data:{},
            success : function(data) {  //（action处理完后返回的结果，就是局部刷新，页面不跳转）
            }
        });
    }

    function vailForm(data) {
        var canDo=false;

        var phone=$(data).find("input.phone").val();
        var vail=$(data).find("input.vail").val();

        var testVail;
        var testPhone;

        /*if(/.{1,20}/.test($.trim(username))){
         $(data).find("input.username").removeClass("errorItem");
         testUser=true;
         }else{
         $(data).find("input.username").addClass("errorItem");
         if($(".errorTip").html() == "tip"){
         $(".errorTip").show();
         $(".errorTip").html('请输入1-20位字符的用户名!');
         setTimeout(function(){
         $(".errorTip").html("tip");
         $(".errorTip").hide();
         },1000);
         }
         testUser=false;
         }*/

        if (/^1\d{10}$/.test($.trim(phone))) {
            $(data).find("input.phone").removeClass("errorItem");
            testPhone = true;
        } else {
            $(data).find("input.phone").addClass("errorItem");
            if($(".errorTip").html() =="tip"){
                $(".errorTip").show();
                $(".errorTip").html('请输入正确的手机号码!');
                setTimeout(function(){
                    $(".errorTip").html("tip");
                    $(".errorTip").hide();
                },1000);
            }

            testPhone = false;
        }


        if($.trim(vail)!=""){
            $(data).find("input.vail").removeClass("errorItem");
            testVail=true;
        }else{
            $(data).find("input.vail").addClass("errorItem");
            if($(".errorTip").html() =="tip"){
                $(".errorTip").show();
                $(".errorTip").html('验证码不能为空！');
                setTimeout(function(){
                    $(".errorTip").html("tip");
                    $(".errorTip").hide();
                },1000);
            }
            testVail=false;
        }

        if ( testPhone && testVail) {
            canDo = true;
        }
        return canDo;
    }
    function regVailForm(data) {
        var canDo=false;

        var password=$(data).find("input.password").val();
        var username=$(data).find("input.username").val();

        var testUser;
        var testPass;

        if (/.{1,20}/.test($.trim(username))) {
            $(data).find("input.username").removeClass("errorItem");
            testUser = true;
        } else {
            $(data).find("input.username").addClass("errorItem");
            if ($(".errorTip").html() == "tip") {
                $(".errorTip").show();
                $(".errorTip").html('请输入1-20位字符的用户名!');
                setTimeout(function () {
                    $(".errorTip").html("tip");
                    $(".errorTip").hide();
                }, 1000);
            }
            testUser = false;
        }

        if(/.{6,10}/.test($.trim(password))){
            $(data).find("input.password").removeClass("errorItem");
            testPass=true;
        }else{
            $(data).find("input.password").addClass("errorItem");
            if($(".errorTip").html() =="tip"){
                $(".errorTip").show();
                $(".errorTip").html('请输入6-10位字符的密码！');
                setTimeout(function(){
                    $(".errorTip").html("tip");
                    $(".errorTip").hide();
                },1000);
            }
            testPass=false;
        }

        if ( testUser && testPass) {
            canDo = true;
        }
        return canDo;
    }

    $(".phone input").on("keyup",function(){
        if($(".regphone").val()!=""&&$(".vail").val()!=""){
            $(".phone .tab-btn").removeAttr("disabled");
            $(".phone .tab-btn").removeClass("btn-disable");
        }else{
            $(".phone .tab-btn").attr("disabled",true);
            $(".phone .tab-btn").addClass("btn-disable");
        }
    });

    $(".reg input").on("keyup",function(){
        if($(".username").val()!=""&&$(".password").val()!=""){
            $(".reg .tab-btn").removeAttr("disabled");
            $(".reg .tab-btn").removeClass("btn-disable");
        }else{
            $(".reg .tab-btn").attr("disabled",true);
            $(".reg .tab-btn").addClass("btn-disable");
        }
    });

    $(".reg .tab-btn").on("click",function () {
        regVailForm(".reg");
        if(regVailForm(".reg")){
            var regname =$(".regname").val();
            var password =$(".password").val();
            var that=$(this);
            $.ajax({
                url:getRootPath_web("/auth/phoneReg.do"),
                data:{password:password,regname:regname},
                success : function(data) {  //（action处理完后返回的结果，就是局部刷新，页面不跳转）
                    if(!data.success){
                        //alert(data.data);

                    }else{
                        var goTag=that.attr("name");
                        $(".section").removeClass("active");
                        $(goTag).addClass("active");
                    }
                }
            });
        }

    });


    $(".phone .tab-btn").on("click",function () {
        vailForm(".phone");
        if(vailForm(".phone")){
            var telephone =$(".regphone").val();
            var phoneCode =$("input[name='phoneCode']").val();
            var that=$(this);
            $.ajax({
                url:getRootPath_web("/auth/phoneReg_emp.do"),
                data:{regphone:telephone,phoneCode:phoneCode},
                success : function(data) {  //（action处理完后返回的结果，就是局部刷新，页面不跳转）

                    if(!data.success){
                        //alert(data.data);
                        console.log(data);

                        $(".phone .vail").addClass("errorItem");
                        if($(".errorTip").html() =="tip"){
                            $(".errorTip").show();
                            $(".errorTip").html('验证码错误！');
                            setTimeout(function(){
                                $(".errorTip").html("tip");
                                $(".errorTip").hide();
                            },1000);
                        }

                    }else{
                        $(".phone .vail").removeClass("errorItem");

                        if(data.data){
                       	 	alert("你已加入"+data.data.slice(0,-1)+"公司！");
                            //已有账号，跳第三步

                            $(".section").removeClass("active");
                            $(".staff").addClass("active");
                        }else{
                            //还未注册，跳第二步
                            var goTag=that.attr("name");
                            $(".section").removeClass("active");
                            $(goTag).addClass("active");
                        }
                    }
                }
            });
        }

    });

    $(".staff .tab-btn").on("click",function () {
        var email =$(".corp-staff-info input[name='email']").val();
        var id =$("input[name='id']").val();
        var telePhone =$("input[name='telePhone']").val();
        var entryDate =$("input[name='entryDate']").val();
        var sex =$("select").val();
        var that=$(this);
        $.ajax({
            url:getRootPath_web("/auth/joinCompany.do"),
            data:{email:email,id:id,telePhone:telePhone,entryDate:entryDate,sex:sex},
            success : function(data) {  //（action处理完后返回的结果，就是局部刷新，页面不跳转）
                if(!data.success){
                    alert(data.data);
                }else{
                    var goTag=that.attr("name");
                    $(".section").removeClass("active");
                    $(goTag).addClass("active");
                }
            }
        });
    })

})