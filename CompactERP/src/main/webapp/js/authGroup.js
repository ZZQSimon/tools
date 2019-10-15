registerInit('authGroup', function (form) {

    var $form = form.get();
    menu();
    data();
    column();
    uiinit();
    editAndPop();
    var deptuuid;
    var roleuuid;
    var useruuid;
    var checkboxValuesId = "";
    //默认点击第一个菜单组
    $form.find('.menuGroup li:first-of-type .changeContent_menu').click();
    //默认点击第一个数据表
    $form.find('.menuGroup li:first-of-type .changeContent_data').click();

    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }

    function guid() {
        return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
    }
    function uuid(len, radix) {
        var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
        var uuid = [], i;
        radix = radix || chars.length;
        if (len) {
            for (i = 0; i < len; i++)
                uuid[i] = chars[0 | Math.random() * radix];
        } else {
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

    $form.find('.nav-auth a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        //tab初始化
        uiinit();
        // $("#auth-data-div .menuGroup,.columntree").getNiceScroll().resize();
        $form.find(".auth-data-user, .auth-detail-user, .auth-menu-user").css("height", "100%");
        //隐藏collapse弹出框
        $form.find('.collapse').collapse('hide');
        iCheck();
    });

    $('.authgroupdiv .collapse').on('shown.bs.collapse', function () {
        uiinit();
    });
  //初始化国际化控件
    function international(divClass, internationalId, menuName, isShow, onSubmintCallback, onSelectCallback) {
        $(divClass).internationalControl({
            internationalId: internationalId,
            menuName: menuName,
            isShow: isShow,
            onSubmit: function (data) {
                
            },
            onSelect: function (data) {
                
            }
        })
    }

    //点击添加权限  清空弹出框的数据
    $form.find(".authgroupadd").on("click", function () {
        $form.find('.authgroupdiv .collapse').collapse('hide');
        deptuuid = "";
        roleuuid = "";
        useruuid = "";
        $form.find(".select_department").dxDefaultWidget(true).setDate();
        $form.find(".select_role").dxDefaultWidget(true).setDate();
        $form.find(".select_user").dxDefaultWidget(true).setDate();
    });
    function uiinit() {
        //出现滚动条
        // $("#auth-menu-div .menuGroup").niceScroll({cursorborder:"",cursorcolor:"#aeb2b7"});
        $form.find(".auth-group-body .menuGroup").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"});
        $form.find(".auth-data-user,#auth-menu-div .menutree,.auth-menu-user").niceScroll({
            cursorborder: "",
            cursorcolor: "#aeb2b7"
        });
        $form.find(".columntree,.auth-detail-user").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"});

        $form.find(".auth-group-body .menuGroup,.columntree,.auth-detail-user").getNiceScroll().resize();
        $form.find(".auth-data-user,.auth-menu-user").getNiceScroll().resize();
    }

    $form.find('a[data-toggle="collapse"]').on("click", function () {
        $(".auth-checkBox input").iCheck("uncheck");
        $(".checkBoxDeptAndRole_data").find("label.view").find("input").iCheck("check");
        $(".checkBoxUser_data").find("label.view").find("input").iCheck("check");
        $(".collapse .dropdown input").val();
    });
    //弹出编辑框
    function editAndPop() {
        $form.find(".deptAndRole").on("click", ".editAuthGroupMember", function () {
            //$('.authgroupdiv .collapse').collapse('hide');
            checkboxValuesId = "";
            var editboxid = $(this).parents(".deptAndRole").siblings(".authgroupadd").attr("data-target");
            deptuuid = $(this).parent().parent().find('input[name="department"]').attr("class");
            roleuuid = $(this).parent().parent().find('input[name="role"]').attr("class");
            //$(this).parent().parent().hide();
            var id = $(this).parent().parent().find('input[name="department"]').val();
            var name = $(this).parent().parent().find('input[name="department"]').attr("valueName");
            var data = {id: id, name: name};
            $form.find(".select_department").dxDefaultWidget(true).setDate(data);
            var id2 = $(this).parent().parent().find('input[name="role"]').val();
            var name2 = $(this).parent().parent().find('input[name="role"]').attr("valueName");
            var data2 = {id: id2, name: name2};
            $form.find(".select_role").dxDefaultWidget(true).setDate(data2);
            var deptAndRole_span = $(this).parent().find('span');
            var checkBoxClass_deptAndRole = $(editboxid + " .collapseWrap .select_checkbox label:first-of-type input").attr("class");
            $form.find(".select_checkbox input:checkbox").iCheck("uncheck");
            if (!isEmpty(checkBoxClass_deptAndRole)) {
                for (var s = 0; s < deptAndRole_span.length; s++) {
                    var checkedId = $(deptAndRole_span[s]).text().trim();
                    var $checkBox = $('.' + checkBoxClass_deptAndRole);
                    for (var c = 0; c < $checkBox.length; c++) {
                        var checkBoxId = $($checkBox[c]).attr('checkName');
                        if (checkBoxId == checkedId) {
                            checkboxValuesId += checkedId + ",";
                            var y = $($checkBox).eq(c).parent();
                            $($checkBox).eq(c).parent().iCheck("check");
                        }
                    }
                }
            }
            $form.find(editboxid).collapse('show');
        });

        $form.find(".userDiv").on("click", ".editAuthGroupMember", function () {
            checkboxValuesId = "";
            $(this).parent().parent().addClass("nowEdit");
            var editboxuserid = $(this).parents(".userDiv").siblings(".authgroupadd").attr("data-target");
            useruuid = $(this).parent().parent().find('input').attr("class");
            var id = $(this).parent().parent().find('input').val();
            var name = $(this).parent().parent().find('input').attr("valueName");
            var data = {id: id, name: name};
            $form.find(".select_user").dxDefaultWidget(true).setDate(data);
            var user_span = $(this).parent().find('span');
            $form.find(".select_checkbox input:checkbox").iCheck("uncheck");
            var checkBoxClass_user = $(editboxuserid + " .collapseWrap .select_checkbox label:first-of-type input").attr("class");
            if (!isEmpty(checkBoxClass_user)) {
                for (var s = 0; s < user_span.length; s++) {
                    var checkedId = $(user_span[s]).text().trim();
                    var $checkBox = $('.' + checkBoxClass_user);
                    for (var c = 0; c < $checkBox.length; c++) {
                        var checkBoxId = $($checkBox[c]).attr('checkName');
                        if (checkBoxId == checkedId) {
                            checkboxValuesId += checkedId + ",";
                            $($checkBox).eq(c).parent().iCheck("check");
                        }
                    }
                }
            }
            $(editboxuserid).collapse('show');
        })
    }

    function iCheck() {
        // $(".auth-data-user, .auth-detail-user, .auth-menu-user").css("height", "100%");
        $form.find('.deptAndRole input,.userDiv input').iCheck({
            checkboxClass: 'icheckbox_flat-blue',
            radioClass: 'iradio_flat-blue'
        });
        $('.checkBoxDeptAndRole_data input,.checkBoxUser_data input').iCheck({
            checkboxClass: 'icheckbox_flat-blue',
            radioClass: 'iradio_flat-blue'
        });
    }

    //删除权限组
    $form.find(".deptAndRole,.userDiv").on("click", ".deleteAuthGroupMember", function () {
        $(this).parent().parent().remove();
    });

    function menu() {
        initLeftTree('.menutree', 'checkbox');
        var menuGroupID = $('.changeContent_menu').attr('name');
        //获取菜单权限成员数据
        selectDpetAndRoleAndUser('.select_department_menu', 'c_dept_role_user', 'dept');
        selectDpetAndRoleAndUser('.select_role_menu', 'c_dept_role_user', 'role');
        selectDpetAndRoleAndUser('.select_user_menu', 'c_dept_role_user', 'user');
        $form.find('.addAuthGroupMember_menu_dept').on("click", function () {
            //保存菜单权限成员信息
            var param_dept_menu = $('.select_department_menu').dxDefaultWidget(true).getDate();
            var param_role_menu = $('.select_role_menu').dxDefaultWidget(true).getDate();
            var param_user_menu = $('.select_user_menu').dxDefaultWidget(true).getDate();
            saveOnclick('.deptAndRole_menu', '', param_dept_menu, param_role_menu, param_user_menu, '', '.select_department_menu',
                '.select_role_menu', '.select_user_menu', deptuuid, roleuuid, useruuid);
            iCheck();
        });
        $form.find('.addAuthGroupMember_menu_user').on("click", function () {
            //保存菜单权限成员信息
            var param_dept_menu = $('.select_department_menu').dxDefaultWidget(true).getDate();
            var param_role_menu = $('.select_role_menu').dxDefaultWidget(true).getDate();
            var param_user_menu = $('.select_user_menu').dxDefaultWidget(true).getDate();
            saveOnclick('', '.userDiv_menu', param_dept_menu, param_role_menu, param_user_menu, '', '.select_department_menu',
                '.select_role_menu', '.select_user_menu', deptuuid, roleuuid, useruuid);
            iCheck();
        });

        //添加菜單組
        $form.find('.addMenuGroup').on("click", function () {
            menuGroupID = $('.auth-menu-group input[name="menuGroupName"]').val();
            //保存编辑的菜单组
            if ($(this).parents(".dropup").hasClass("update")) {
                var menuGroup_ID = $('.menuGroup li.active').find("a").html();
                var upMenuGroupId = menuGroupID;
                postJson('/authGroup/updateMenuGroup.do', {
                    menuGroupId: menuGroup_ID,
                    upMenuGroupId: upMenuGroupId
                }, function (result) {
                    $('.menuGroup li.active').find("a").attr("name", menuGroupID);
                    $('.menuGroup li.active').find("a").html(menuGroupID);
                    $(this).parents(".dropup").removeClass("update");
                    alert(msg("Update success"));
                });
            } else {
                if ($.trim(menuGroupID) != "") {
                    var menuGroupName = $('.menuGroupList').find("li");
                    var flag = true;
                    for (var i = 0; i < menuGroupName.length; i++) {
                        var name = $(menuGroupName[i]).find("a").html();
                        if (name == menuGroupID) {
                            alert(msg("MenuNameCanNotRepeat"));
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        var authGroup = new Array();
                        var param = {};
                        param.table = '';
                        param.column = '';
                        param.type = 1;
                        param.menu = menuGroupID;
                        authGroup.push(param);
                        postJson('/authGroup/addMenuGroup.do', {authGroup: authGroup}, function (result) {
                            $form.find('.menuGroup li').removeClass("active");
                            $form.find('.menuGroup').append("<li class='active'><a href='javascript:void(0)' class='changeContent_menu' name='" + menuGroupID + "'>" + menuGroupID + "</a></li>");

                            postJson('/authGroup/selectMember.do', {authGroup: authGroup}, function (result) {
                                callback(result, '.menutree', 'menuTree');
                                AuthGroupMember('.deptAndRole_menu', '.userDiv_menu', '', '', result.authGroupMember, '',
                                    'checkDeptAndRole_menu', 'checkUser_menu');
                            });
                        });
                    }
                }
            }
        });
        //删除菜单组
        $form.find('.delete-Menu-Group-btn').on("click", function () {
            if ($('.menuGroup li').hasClass("active")) {
                var menuGroup_ID = $('.menuGroup li.active').find("a").attr("name");
                if (confirm(msg('delete real?'))) {
                    postJson('/authGroup/deleteMenuGroup.do', {menuGroupId: menuGroup_ID}, function (result) {
                        $form.find('.menuGroup li.active').remove();
                        alert(msg("Delete success"));
                    });
                }
            } else {
                alert(msg("PleaseChooseMenu"));
            }
        });
        //编辑菜单组
        $form.find('.update-Menu-Group-btn').on("click", function () {
            if ($('.menuGroup li').hasClass("active")) {
                $(this).parent().addClass("update");
                $('.auth-menu-group input[name="menuGroupName"]').val(menuGroupID);
            } else {
                alert(msg("PleaseChooseMenu"));
            }
        });
        menuGroupID = $('.changeContent_menu').attr('name');
        //根据菜单组改变菜单和权限成员数据
        $form.find('.menuGroup').on("click", '.changeContent_menu', function () {
            $('#auth-menu-div .menuGroup li').removeClass("active");
            $(this).parent("li").addClass("active");

            menuGroupID = $(this).attr('name');
            var authGroup = new Array();
            var param = {};
            param.table = '';
            param.column = '';
            param.type = 1;
            param.menu = menuGroupID;
            authGroup.push(param);
            postJson('/authGroup/selectMember.do', {authGroup: authGroup}, function (result) {
                callback(result, '.menutree', 'menuTree');
                AuthGroupMember('.deptAndRole_menu', '.userDiv_menu', '', '', result.authGroupMember, '', 'checkDeptAndRole_menu',
                    'checkUser_menu')

                iCheck();
            });
            $form.find('.collapse').collapse('hide');
        });
        //新建菜单权限
        $form.find('.addMenuAuthGroup').on("click", function () {
            var saveAuthGroupParam = addAuthGroup('deptAndRole_menu>div', 'userDiv_menu>div', '', menuGroupID, '', '', 1);
            saveAuthGroup(saveAuthGroupParam["authGroup"],saveAuthGroupParam["menu_Id"]);
        });
    }

    function data() {
        //获取数据权限成员数据
        selectDpetAndRoleAndUser('.select_department_data', 'c_dept_role_user', 'data_dept');
        selectDpetAndRoleAndUser('.select_role_data', 'c_dept_role_user', 'role');
        selectDpetAndRoleAndUser('.select_user_data', 'c_dept_role_user', 'data_user');

        $form.find('.addAuthGroupMember_data_dept').on("click", function () {
            //保存菜单权限成员信息
            var param_dept_data = $('.select_department_data').dxDefaultWidget(true).getDate();
            var param_role_data = $('.select_role_data').dxDefaultWidget(true).getDate();
            var param_user_data = $('.select_user_data').dxDefaultWidget(true).getDate();
            saveOnclick('.deptAndRole_data', '', param_dept_data, param_role_data, param_user_data, 'checkDeptAndRole_data',
                '.select_department_data', '.select_role_data', '.select_user_data', deptuuid, roleuuid, useruuid);

            iCheck();

            $(".check-Box-Value").addClass("check-Box-Value");
        });
        $form.find('.addAuthGroupMember_data_user').on("click", function () {
            //保存菜单权限成员信息
            var param_dept_data = $('.select_department_data').dxDefaultWidget(true).getDate();
            var param_role_data = $('.select_role_data').dxDefaultWidget(true).getDate();
            var param_user_data = $('.select_user_data').dxDefaultWidget(true).getDate();
            saveOnclick('', '.userDiv_data', param_dept_data, param_role_data, param_user_data, 'checkUser_data',
                '.select_department_data', '.select_role_data', '.select_user_data', deptuuid, roleuuid, useruuid);

            iCheck();
            $form.find(".check-Box-Value").addClass(".check-Box-Value");
        });
        //根据数据表改变权限成员数据
        var tableId_data = $('.changeContent_data').attr('name');
        $form.find('.changeContent_data').on("click", function () {
            var authGroup = new Array();
            var param = {};
            tableId_data = $(this).attr('name');
            param.table = tableId_data;
            param.column = '';
            param.type = 2;
            param.menu = '';
            authGroup.push(param);
            dx.processing.open();
            postJson('/authGroup/selectMember.do', {authGroup: authGroup}, function (result) {
            	 //设置数据组数据
            	AuthGroupMember('.deptAndRole_data', '.userDiv_data', '.checkBoxDeptAndRole_data',
                      '.checkBoxUser_data', '', '', 'checkDeptAndRole_data', 'checkUser_data',
                      '.select_department_data', '.select_role_data', '.select_user_data');
                $form.find('.tableDataGroupList').html("");
                $form.find('.tableDataGroupList').append('<li><a href="javascript:void(0)" class="changeAuthDataGroup" internationalId="" name=""><span class="showGroupName" style="display: inline-block;width: 88%;">默认数据组</span></a></li>');
                auth_data_group_map = {};
                authDataGroupMap = {};
                auth_data_group_checkboxAll = [];
                authDataGroupMap = result.authDataGroupMap;
                auth_data_group_checkboxAll = result.checkboxAll;
                
                var data_group_idIsnull=[];
                for(var i=0;i<result.authGroupMember.length;i++){
            		if(isEmpty(result.authGroupMember[i].data_group_id)){
            			data_group_idIsnull.push(result.authGroupMember[i]);
            		}
            	}
                auth_data_group_map[""] = data_group_idIsnull;
                for(var key in authDataGroupMap){
                	var data_group_idNotIsnull=[];
                	for(var i=0;i<result.authGroupMember.length;i++){
            			if(key == result.authGroupMember[i].data_group_id){
            				data_group_idNotIsnull.push(result.authGroupMember[i]);
                    	}
                	}
                	auth_data_group_map[key] =data_group_idNotIsnull;
                	var authDataGroupList = authDataGroupMap[key];
                	if(authDataGroupList.length>0){
                		$form.find('.tableDataGroupList').append('<li><a href="javascript:void(0)" class="changeAuthDataGroup" internationalId="'
                				+authDataGroupList[0].international_id+'" name="'+authDataGroupList[0].group_id+'" groupName="'+
                				msg(authDataGroupList[0].international_id)+'"><span class="showGroupName" style="display: inline-block;width: 88%;">'
                				+msg(authDataGroupList[0].international_id)+'</span><span class="glyphicon glyphicon-cog"></span></a></li>');
                	}
                }
                iCheck();
                /*默认第一个选中*/
                //保存上一次点击的数据组权限成员的数据
                $form.find(".tableDataGroupList .changeAuthDataGroup:first").trigger("click");
                dx.processing.close();
            });
            $('#auth-data-div .menuGroup li').removeClass("active");
            $(this).parent("li").addClass("active");
            $form.find('.collapse').collapse('hide');
         });
        var authDataGroupMap = {};
        var auth_data_group_map=[];
        var auth_data_group_checkboxAll={};
        //点击数据权限组，设置权限成员数据
        $form.find('.tableDataGroupList').on("click",".changeAuthDataGroup", function () {
        	dx.processing.open();
        	//保存上一次点击的数据组权限成员的数据
        	var old_group_id = $form.find(".tableDataGroupList .active .changeAuthDataGroup").attr("name");
        	if(old_group_id!==undefined){
        		var saveAuthGroupParam = addAuthGroup('deptAndRole_data>div', 'userDiv_data>div', tableId_data, '', '', '', 2);
                auth_data_group_map[old_group_id] = saveAuthGroupParam["authGroup"];
        	}
        	var _this=$(this).parent();
            _this.siblings().removeClass("active").find(".glyphicon").css({"display":"none"});
            _this.addClass("active").find(".glyphicon").css({"display":"inline-block"});
            hideChose();

            var group_id= $(this).attr("name");
        	AuthGroupMember('.deptAndRole_data', '.userDiv_data', '.checkBoxDeptAndRole_data',
                  '.checkBoxUser_data', auth_data_group_map[group_id], auth_data_group_checkboxAll, 'checkDeptAndRole_data', 'checkUser_data',
                  '.select_department_data', '.select_role_data', '.select_user_data');
        	 dx.processing.close();
        });
        $form.find('.tableDataGroupList').on("click",".glyphicon", function (e) {
            var _this=$(this);
            if (!e) var e = window.event;
            e.cancelBubble = true;
            if (e.stopPropagation) e.stopPropagation();
            var top= _this.offset().top-120;
            showChose(top);
        });
        $(document).click(function(){
            $form.find(".tableDataGroupList").css({"margin-top":"0px"})
            $form.find(".chose").css({"display":"none"});
        });
        function showPop(){
            $form.find(".data—permission").css({"display":"block"});
        };
        function hidePop(){
            $form.find(".data—permission").css({"display":"none"});
          //移除国际化控件
            $form.find(".authDataGroupName").html("");
        };
        function showChose(top){
            $form.find(".tableDataGroupList").css({"margin-top":"-55px"});
            // console.log(top);
            $form.find(".chose").css({"top":top,"display":"block"})
        };
        function hideChose(){
            $form.find(".tableDataGroupList").css({"margin-top":"0px"})
            $form.find(".chose").css({"display":"none"});
          //移除国际化控件
            $form.find(".authDataGroupName").html("");
        };
       
        $form.find(".canel_x").click(function(){
            hidePop();
            hideChose();
        });
        //添加新数据组
        $form.find(".add_new_arry").click(function(){
            showPop();
            hideChose();
        	international(form.get().find(".authDataGroupName"), "", "");
        	$form.find('.auth_data_group_detail').html("");
        	$form.find('.add_authDataGroupSymbol').click();
        });
         //编辑数据权限组
        $form.find('.update-data-Group-btn').on("click", function () {
        	showPop();
        	var name=$form.find(".tableDataGroupList .active .changeAuthDataGroup").attr("name");
        	var groupName=$form.find(".tableDataGroupList .active .changeAuthDataGroup").attr("groupName");
        	var internationalId=$form.find(".tableDataGroupList .active .changeAuthDataGroup").attr("internationalId");
        	international(form.get().find(".authDataGroupName"), internationalId, groupName);
        	$form.find('.auth_data_group_detail').html("");
        	var authDataGroupSymbolList = authDataGroupMap[name];
        	 for(var i=0;i<authDataGroupSymbolList.length;i++){
        		 $form.find('.add_authDataGroupSymbol').click();
        		 var groupDetailDiv=$form.find('.auth_data_group_detail').find("div");
        		 var div = $(groupDetailDiv[groupDetailDiv.length-1]);
        		 $(div).find('.auth_data_group_detail_list').val(authDataGroupSymbolList[i].column_name);
        		 $(div).find('.symbol_select').val(authDataGroupSymbolList[i].symbol);
        		 $(div).find('.auth_data_group_detail_value').val(authDataGroupSymbolList[i].value);
             }
        	 $form.find('.save_authDataGroupSymbol').addClass("edit_save_button");
        })
        //保存新增或编辑的数据权限组条件数据
        $form.find('.save_authDataGroupSymbol').on("click", function () {
        	 //获取菜单点击前的菜单国际化控件返回的数据
           var getInternational = form.get().find(".authDataGroupName").internationalControl(true).getData();
           var authDataGroupName = "";
           var group_id="";
           var international_id = getInternational.international_id;
           var i18n={};
           if($(this).parent().find(".edit_save_button").length<=0){
        	   group_id=uuid(10,16);
           }else{
        	   group_id=$form.find(".tableDataGroupList .active .changeAuthDataGroup").attr("name");
           }
           i18n.international_id = international_id;
           if (isEmpty(getInternational.international_id)) {
        	   authDataGroupName = getInternational.interValue;
        	   i18n[dx.user.language_id] = getInternational.interValue;
           }else{
        	   authDataGroupName = getInternational[dx.user.language_id];
        	   i18n=getInternational;
           }
           var authDataGroupSymbolList=[];
           var groupDetailDiv=$form.find('.auth_data_group_detail').find("div");
           for(var i=0;i<groupDetailDiv.length;i++){
        	   var column_name = $(groupDetailDiv[i]).find('.auth_data_group_detail_list').val();
        	   var symbol = $(groupDetailDiv[i]).find('.symbol_select').val();
        	   var value = $(groupDetailDiv[i]).find('.auth_data_group_detail_value').val();
        	   var param={};
        	   param.table_id = tableId_data;
        	   param.group_id = group_id;
        	   param.group_detail_id = uuid(5,16);
        	   param.column_name=column_name;
        	   param.symbol=symbol;
        	   param.value=value;
        	   param.international_id=international_id;
        	   param.i18n = i18n;
        	   authDataGroupSymbolList.push(param);
           }
           if($(this).parent().find(".edit_save_button").length<=0){
        	   $form.find('.tableDataGroupList').append('<li><a href="javascript:void(0)" class="changeAuthDataGroup"  internationalId="'+
           			getInternational.international_id+'" name="'+group_id+'" groupName="'+authDataGroupName+'"><span class="showGroupName" style="display: inline-block;width: 88%;">'
           			+authDataGroupName+'</span><span class="glyphicon glyphicon-cog"></span></a></li>');
               authDataGroupMap[group_id] = authDataGroupSymbolList;
        	   $(this).removeClass("edit_save_button");
           }else{
        	   $form.find(".tableDataGroupList .active .changeAuthDataGroup").attr("internationalid",getInternational.international_id);
        	   $form.find(".tableDataGroupList .active .changeAuthDataGroup").attr("groupname",authDataGroupName);
        	   $form.find(".tableDataGroupList .active .showGroupName").html(authDataGroupName);
               authDataGroupMap[group_id] = authDataGroupSymbolList;
           }
        	//移除国际化控件
            $form.find(".authDataGroupName").html("");
            hidePop();
            hideChose();
        })
        //删除数据权限组
        $form.find('.delete-data-Group-btn').on("click", function () {
        	var name=$form.find(".tableDataGroupList .active .changeAuthDataGroup").attr("name");
        	auth_data_group_map[name]=[];
        	authDataGroupMap[name]=[];
        	$form.find(".tableDataGroupList .active").remove();
            hideChose();
        })
        //新增数据权限组条件
        $form.find('.add_authDataGroupSymbol').on("click", function () {
        	$form.find('.auth_data_group_detail').append('<div style="margin-bottom: 10px">'
        			+'<select class="auth_data_group_detail_list"></select>'
        			+'<select class="symbol_select">'
        			+'<option value="=">=</option>'
        			+'<option value="<=">&lt;=</option>'
        			+'<option value="!=">!=</option>'
        			+'<option value=">">&gt;</option>'
        			+'<option value="<">&lt;</option>'
        			+'<option value=">=">&gt;=</option>'
        			+'</select>'
        			+'<input type="text" class="auth_data_group_detail_value">'
        			+'<button class="delete_authDataGroupSymbol button-color2" type="button">删除</button></div>');
        	//设置数据权限组条件数据
            var columns=dx.table[tableId_data].columns;
            for(var i=0;i<columns.length;i++){
            	$form.find('.auth_data_group_detail_list').append('<option value="'+columns[i].column_name+'">'+msg(columns[i].international_id)+'</option>');
            }
            
        })
         //删除数据权限组条件
        $form.find('.auth_data_group_detail').on("click", ".delete_authDataGroupSymbol",function () {
        	$(this).parent().remove();
        })

        //新增数据权限
        $form.find('.addDataAuthGroup').on("click", function () {
        	var saveAuthGroupParam = addAuthGroup('deptAndRole_data>div', 'userDiv_data>div', tableId_data, '', '', '', 2);
        	var group_id = $form.find(".tableDataGroupList .active .changeAuthDataGroup").attr("name");
        	if(group_id!==undefined){
        		auth_data_group_map[group_id]=saveAuthGroupParam["authGroup"];
        	}
        	authDataGroupMap;
        	var authGroup = new Array();
        	var param = {};
            param.type = 2;
            param.department = 2;
            param.table = tableId_data;
            param.menu = "";
            param.column = "";
            authGroup.push(param);
        	saveAuthGroup(authGroup,saveAuthGroupParam["menu_Id"],auth_data_group_map,authDataGroupMap);
        });
        //保存模板
        $form.find('.saveTemplate_data').on("click", function () {
            var template_name = $(this).parent().find('input').val();
            var saveAuthGroupParam = addAuthGroup('deptAndRole_data>div', 'userDiv_data>div', '', '', '', template_name, 2);
            saveAuthGroup(saveAuthGroupParam["authGroup"],saveAuthGroupParam["menu_Id"]);
        });
        //从模板中选择
        $form.find('.lookAllTemplate_data').on("click", function () {
            $(this).parent().find('ul').empty();
            var authGroup = new Array();
            var param = {};
            param.table = tableId_data;
            param.type = 2;
            authGroup.push(param);
            lookAllTemplate(authGroup, ".lookAllTemplate_data", "template_data",
                ".deptAndRole_data", ".userDiv_data", 'checkBoxDeptAndRole_data', 'checkBoxUser_data',
                'checkDeptAndRole_data', 'checkUser_data');
        });

    }

    function column() {
        var tableId_column = "";
        var columnId = "";
        initLeftTree('.columntree', '');
        postJson('/authGroup/selectColumn.do', {}, function (data) {
        	callback(data, '.columntree', 'columnTree', function (data) {});
            var flage = false;
            var authGroup = new Array();
            for (var i = 0; i < data.length; i++) {
                var param = {};
                if (!isEmpty(data[i].id)) {
                    if (data[i].parent != "#") {
                        flage = true;
                        tableId_column = data[i].parent;
                        columnId = data[i].id;
                        param.table = tableId_column;
                        param.column = columnId;
                        param.type = 3;
                        param.menu = '';
                        authGroup.push(param);
                        postJson('/authGroup/selectMember.do', {authGroup: authGroup}, function (result) {
                            AuthGroupMember('.deptAndRole_column', '.userDiv_column', '', '', result.authGroupMember, '',
                                'checkDeptAndRole_column', 'checkUser_column');
                        });
                    }
                }
                if (flage) {
                    break;
                }
            }
        });
        //获取字段权限成员数据
        selectDpetAndRoleAndUser('.select_department_column', 'c_dept_role_user', 'dept');
        selectDpetAndRoleAndUser('.select_role_column', 'c_dept_role_user', 'role');
        selectDpetAndRoleAndUser('.select_user_column', 'c_dept_role_user', 'user');

        $form.find('.addAuthGroupMember_column_dept').on("click", function () {
            //保存字段成員信息
            var param_dept_column = $('.select_department_column').dxDefaultWidget(true).getDate();
            var param_role_column = $('.select_role_column').dxDefaultWidget(true).getDate();
            var param_user_column = $('.select_user_column').dxDefaultWidget(true).getDate();
            saveOnclick('.deptAndRole_column', '', param_dept_column, param_role_column, param_user_column, 'checkDeptAndRole_column',
                '.select_department_column', '.select_role_column', '.select_user_column', deptuuid, roleuuid, useruuid);
            iCheck();
        });
        $form.find('.addAuthGroupMember_column_user').on("click", function () {
            //保存字段成員信息
            var param_dept_column = $('.select_department_column').dxDefaultWidget(true).getDate();
            var param_role_column = $('.select_role_column').dxDefaultWidget(true).getDate();
            var param_user_column = $('.select_user_column').dxDefaultWidget(true).getDate();
            if (param_user_column.id != "") {
                saveOnclick('', '.userDiv_column', param_dept_column, param_role_column, param_user_column, 'checkUser_column',
                    '.select_department_column', '.select_role_column', '.select_user_column', deptuuid, roleuuid, useruuid);
                iCheck();
            }
        });

        //树节点点击事件。。可在选择节点时调用,根据字段Id改变权限成员数据。
        $form.find('.columntree').bind("activate_node.jstree", function (e, data) {
            if (!isEmpty(data.node)) {
                if (data.node.parent != "#") {
                    form.get().find(".auth-detail-user").find(".empty-form").hide();
                    var authGroup = new Array();
                    var param = {};
                    tableId_column = data.node.parent;
                    columnId = data.node.id;
                    param.table = tableId_column;
                    param.column = columnId.split("%")[1];
                    param.menu = '';
                    param.type = 3;
                    authGroup.push(param);
                    postJson('/authGroup/selectMember.do', {authGroup: authGroup}, function (result) {
                        if ($(".auth-detail-user .auth-detail-user-wrap").css("display") == "none") {
                            $(".auth-detail-user .auth-detail-user-wrap").css("display", "block");
                        }
                        AuthGroupMember('.deptAndRole_column', '.userDiv_column', '', '', result.authGroupMember, '',
                            'checkDeptAndRole_column', 'checkUser_column')
                    });
                }
            }
        });
        //默认点击第一个节点下的第一个子节点

        $form.find('.addColumnAuthGroup').on("click", function () {
        	var saveAuthGroupParam = addAuthGroup('deptAndRole_column>div', 'userDiv_column>div', tableId_column, '', columnId, '', 3);
        	saveAuthGroup(saveAuthGroupParam["authGroup"],saveAuthGroupParam["menu_Id"]);
        });
        //保存模板
        $form.find('.saveTemplate_column').on("click", function () {
            var template_name = $(this).parent().find('input').val();
            var saveAuthGroupParam = addAuthGroup('deptAndRole_column>div', 'userDiv_column>div', '', '', '', template_name, 3);
        	saveAuthGroup(saveAuthGroupParam["authGroup"],saveAuthGroupParam["menu_Id"]);
        });
        //从模板中选择
        $form.find('.lookAllTemplate_column').on("click", function () {
            $(this).parent().find('ul').empty();
            var authGroup = new Array();
            var param = {};
            param.table = tableId_column;
            param.type = 3;
            authGroup.push(param);
            lookAllTemplate(authGroup, ".lookAllTemplate_column", "template_column",
                ".deptAndRole_column", ".userDiv_column", 'checkBoxDeptAndRole_column', 'checkBoxUser_column',
                'checkDeptAndRole_column', 'checkUser_column', '.select_department_column', '.select_role_column', '.select_user_column');
        });
    }

    function initLeftTree(divTree, pluginsValue) {
        $(divTree).jstree({
            'core': {
                "multiple": true,
                'data': null,
                'dblclick_toggle': true,          //tree的双击展开
            },
            "plugins": ["search", pluginsValue]
        });
    }

    //jstree
    function callback(result, divTree, resultValue, callBack) {
        if (!isEmpty(result)) {
            if (isEmpty(result[resultValue])) {
                $(divTree).jstree(true).settings.core.data = null;
                $(divTree).jstree(true).refresh();
            } else {
                $(divTree).jstree(true).settings.core.data = result[resultValue];
                $(divTree).jstree(true).refresh();
                $(divTree).jstree("check_node");
                $(divTree).jstree(true).refresh();
            }
        }
        if (!isEmpty(callBack))
            callBack(result[resultValue]);
    }

    //查询部门角色用户数据
    function selectDpetAndRoleAndUser(divClass, tableName, columnName) {
        $(divClass).dxDefaultWidget({
            tableName: tableName,
            columnName: columnName,
            dxOnBlur: function (data) {

            },
            dxOnChange: function (data) {

            }
        });
    }

    //保存权限成员信息
    function saveOnclick(deptAndRoleDiv, userDiv, param_dept, param_role, param_user, checkboxClass,
                         select_department, select_role, select_user, deptuuid, roleuuid, useruuid) {
        var displayDiv = $(deptAndRoleDiv).children('div');
        var displayDiv_user = $(userDiv).children('div');
        var checkValue = ',';
        var checkValueId = "";
        if (!isEmpty(checkboxClass)) {
            var checkboxs = $("." + checkboxClass);
            if (!isEmpty(checkboxs)) {
                if (checkboxs.length > 0) {
                    checkValue = '';
                }
                for (var i = 0; i < checkboxs.length; i++) {
                    if (checkboxs[i].checked) {
                        checkValueId += $(checkboxs[i]).attr('checkName') + ",";
                        checkValue += '<span name="' + $(checkboxs[i]).val() + '"checkBoxId="' + $(checkboxs[i]).attr('checkBoxId') + '">'
                            + $(checkboxs[i]).attr('checkName') + '&nbsp&nbsp  </span>';
                    }
                }
            }
        }
        if (!isEmpty(deptAndRoleDiv)) {
            if (isEmpty(param_dept.id) || isEmpty(param_role.id)) {
                alert(msg("content can not be empty"));
            } else {
                if (isEmpty(param_dept.name) || isEmpty(param_role.name)) {
                    alert(msg("dept roel content mistake"));
                    return;
                }
                var flag = false;
                if (displayDiv.length != 0) {
                    for (var i = 0; i < displayDiv.length; i++) {
                        var deptValue = $(displayDiv[i]).find('input[name=department]').val();
                        var roleValue = $(displayDiv[i]).find('input[name=role]').val();
                        if (param_dept.id == deptValue && param_role.id == roleValue) {
                            if (isEmpty(checkboxValuesId)) {
                                flag = true;
                                alert(msg("content repetition"));
                                return;
                            }
                            if (checkboxValuesId == checkValueId) {
                                flag = true;
                                alert(msg("content repetition"));
                                return;
                            }
                        }
                        if (i == displayDiv.length - 1 && !flag) {
                            //判断是否为编辑
                            if (!isEmpty(deptuuid) && !isEmpty(roleuuid)) {
                                //重新获取勾选的复选框
                                if (!isEmpty(checkboxClass)) {
                                    var checkboxs = $("." + checkboxClass);
                                    if (!isEmpty(checkboxs)) {
                                        checkValue = "";
                                        checkValueId = ""
                                        for (var j = 0; j < checkboxs.length; j++) {
                                            if (checkboxs[j].checked) {
                                                checkValueId += $(checkboxs[j]).attr('checkName') + ",";
                                                checkValue += '<span name="' + $(checkboxs[j]).val() + '"checkBoxId="' + $(checkboxs[j]).attr('checkBoxId') + '">'
                                                    + $(checkboxs[j]).attr('checkName') + '&nbsp&nbsp  </span>  ';
                                            }
                                        }
                                    }
                                }
                                if (checkValue.indexOf("新增") > 0 && (param_dept.name == "上一级部门" || param_dept.name == "上二级部门" || param_dept.name == "本级部门")) {
                                    alert(msg("AddNotChoiceSuperiorDeptOrOwnDept"));
                                    return;
                                } else {
                                    if (isEmpty(checkValue)) {
                                        alert(msg("UnableToSave"));
                                        return;
                                    }
                                    if (checkValue == ",") {
                                        checkValue = "";
                                    }
                                    $('.' + roleuuid).parent().attr("class", "editauth");
                                    $('.editauth').empty();
                                    $('.editauth').append("<label><input class='" + guid() + "' type='checkbox' name='department' value='" + param_dept.id
                                        + "' valueName='" + param_dept.name + "'/><label>" + param_dept.name + "的" + param_role.name + "</label></label>" +
                                        "<span class='check-Box-Value'>" + checkValue + "<a class='deleteAuthGroupMember remove-btn'></a><a class='editAuthGroupMember'></a></span>" +
                                        "<input type='hidden' class='" + guid() + "' name='role'  value='" + param_role.id + "' valueName='"
                                        + param_role.name + "'/>");
                                    $('.editauth').attr("class", "");
                                }
                            } else {
                                if (checkValue.indexOf("新增") > 0 && (param_dept.name == "上一级部门" || param_dept.name == "上二级部门" || param_dept.name == "本级部门")) {
                                    alert(msg("AddNotChoiceSuperiorDeptOrOwnDept"));
                                    return;
                                } else {
                                    if (isEmpty(checkValue)) {
                                        alert(msg("UnableToSave"));
                                        return;
                                    }
                                    if (checkValue == ",") {
                                        checkValue = "";
                                    }
                                    $(deptAndRoleDiv).append("<div><label><input class='" + guid() + "' type='checkbox' name='department' value='"
                                        + param_dept.id + "' valueName='" + param_dept.name + "'/><label>" + param_dept.name + "的" + param_role.name +
                                        "</label></label><span class='check-Box-Value'>" + checkValue + "<a class='deleteAuthGroupMember remove-btn'></a><a class='editAuthGroupMember'></a></span>" +
                                        "<input type='hidden' class='" + guid() + "' name='role'  value='" + param_role.id + "' valueName='"
                                        + param_role.name + "'/></div>");
                                }
                            }
                        }
                    }
                } else {
                    if (checkValue.indexOf("新增") > 0 && (param_dept.name == "上一级部门" || param_dept.name == "上二级部门" || param_dept.name == "本级部门")) {
                        alert(msg("AddNotChoiceSuperiorDeptOrOwnDept"));
                        return;
                    } else {
                        if (isEmpty(checkValue)) {
                            alert(msg("UnableToSave"));
                            return;
                        }
                        if (checkValue == ",") {
                            checkValue = "";
                        }
                        $(deptAndRoleDiv).append("<div><label><input class='" + guid() + "' type='checkbox' name='department' value='" + param_dept.id
                            + "' valueName='" + param_dept.name + "'/><label>" + param_dept.name + "的" + param_role.name + "</label></label>" +
                            "<span class='check-Box-Value'>" + checkValue + "<a class='deleteAuthGroupMember remove-btn'></a><a class='editAuthGroupMember'></a></span>" +
                            "<input type='hidden' class='" + guid() + "' name='role'  value='" + param_role.id + "' valueName='" + param_role.name + "'/></div>");
                    }

                }
            }
        }
        if (!isEmpty(userDiv)) {
            if (isEmpty(param_user.id)) {
                alert(msg("content can not be empty"));
                return;
            } else {
                if (isEmpty(param_user.name) || isEmpty(param_user.name)) {
                    alert(msg("user content mistake"));
                    return;
                }
                var flag = false;
                if (displayDiv_user.length != 0) {
                    for (var i = 0; i < displayDiv_user.length; i++) {
                        var userValue = $(displayDiv_user[i]).find('input[name=user]').val();
                        if (param_user.id == userValue) {
                            if (isEmpty(checkboxValuesId)) {
                                flag = true;
                                alert(msg("content repetition"));
                                return;
                            }
                            if (checkboxValuesId == checkValueId) {
                                flag = true;
                                alert(msg("content repetition"));
                                return;
                            }
                        }
                        if (i == displayDiv_user.length - 1 && !flag) {
                            if (!isEmpty(useruuid)) {
                                //重新获取勾选的复选框
                                if (!isEmpty(checkboxClass)) {
                                    var checkboxs = $("." + checkboxClass);
                                    if (!isEmpty(checkboxs)) {
                                        checkValue = "";
                                        checkValueId = ""
                                        for (var i = 0; i < checkboxs.length; i++) {
                                            if (checkboxs[i].checked) {
                                                checkValueId += $(checkboxs[i]).attr('checkName') + ",";
                                                checkValue += '<span name="' + $(checkboxs[i]).val() + '"checkBoxId="' + $(checkboxs[i]).attr('checkBoxId') + '">'
                                                    + $(checkboxs[i]).attr('checkName') + '&nbsp&nbsp  </span>  ';
                                            }
                                        }
                                    }
                                }
                                if (checkValue.indexOf("新增") > 0 && (param_user.name == "上一级领导" || param_user.name == "上二级领导" || param_user.name == "当前用户")) {
                                    alert(msg("AddNotChoiceSuperiorDeptOrOwnDept"));
                                    return;
                                } else {
                                    if (isEmpty(checkValue)) {
                                        alert(msg("UnableToSave"));
                                        return;
                                    }
                                    if (checkValue == ",") {
                                        checkValue = "";
                                    }
                                    $('.' + useruuid).parent().parent().parent().attr("class", "editauth");
                                    $('.editauth').empty();
                                    $('.editauth').append("<label><input class='" + guid() + "' type='checkbox' name='user' value='" + param_user.id +
                                        "' valueName='" + param_user.name + "'/><label>" + param_user.name + "</label></label>" +
                                        "<span class='check-Box-Value'>" + checkValue + "<a class='deleteAuthGroupMember remove-btn'></a><a class='editAuthGroupMember'></a></span>");
                                    $('.editauth').attr("class", "");
                                }
                            } else {
                                if (checkValue.indexOf("新增") > 0 && (param_user.name == "上一级领导" || param_user.name == "上二级领导" || param_user.name == "当前用户")) {
                                    alert(msg("AddNotChoiceSuperiorDeptOrOwnDept"));
                                    return;
                                } else {
                                    if (isEmpty(checkValue)) {
                                        alert(msg("UnableToSave"));
                                        return;
                                    }
                                    if (checkValue == ",") {
                                        checkValue = "";
                                    }
                                    $(userDiv).append("<div><label><input class='" + guid() + "' type='checkbox' name='user' value='" + param_user.id +
                                        "' valueName='" + param_user.name + "'/><label>" + param_user.name + "</label></label>" +
                                        "<span class='check-Box-Value'>" + checkValue + "<a class='deleteAuthGroupMember remove-btn'></a><a class='editAuthGroupMember'></a></span></div>");
                                }
                            }
                        }
                    }
                } else {
                    if (checkValue.indexOf("新增") > 0 && (param_user.name == "上一级领导" || param_user.name == "上二级领导" || param_user.name == "当前用户")) {
                        alert(msg("AddNotChoiceSuperiorDeptOrOwnDept"));
                    } else {
                        if (isEmpty(checkValue)) {
                            alert(msg("UnableToSave"));
                            return;
                        }
                        if (checkValue == ",") {
                            checkValue = "";
                        }
                        $(userDiv).append("<div><label><input class='" + guid() + "' type='checkbox' name='user' value='" + param_user.id +
                            "' valueName='" + param_user.name + "'/><label>" + param_user.name + "</label></label>" +
                            "<span class='check-Box-Value'>" + checkValue + "<a class='deleteAuthGroupMember remove-btn'></a><a class='editAuthGroupMember'></a></span></div>");
                    }
                }
            }
        }
        checkboxValuesId = "";
        uiinit();
    }

//根据ID改变权限成员数据
    function AuthGroupMember(deptAndRoleDiv, userDiv, checkBoxDiv_deptAndRole, checkBoxDiv_user, authGroupMember,
                             checkboxAll, checkBoxClass_deptAndRole, checkBoxClass_user) {
    	dx.processing.open();
        $(deptAndRoleDiv).empty();
        $(userDiv).empty();
        $(checkBoxDiv_deptAndRole).empty();
        $(checkBoxDiv_user).empty();
        if (!isEmpty(checkboxAll)) {
            for (var i = 0; i < checkboxAll.length; i++) {
                if (!isEmpty(checkboxAll[i].action_name_international)) {
                    if (checkboxAll[i].system_type == "view") {
                        var appendHtml_deptAndRole = '<label class="view"><input type="checkBox" value="' + checkboxAll[i].action_id
                            + '"checkBoxId="' + checkboxAll[i].system_type + '"checkName="' + checkboxAll[i].operate_name
                            + '"  class="' + checkBoxClass_deptAndRole + '"/>' + checkboxAll[i].operate_name + '</label>';
                        var appendHtml_user = '<label class="view"><input type="checkBox" value="' + checkboxAll[i].action_id
                            + '"checkBoxId="' + checkboxAll[i].system_type + '" checkName="' + checkboxAll[i].operate_name
                            + '" class="' + checkBoxClass_user + '"/>' + checkboxAll[i].operate_name + '</label>';
                        $(checkBoxDiv_deptAndRole).append(appendHtml_deptAndRole);
                        $(checkBoxDiv_user).append(appendHtml_user);
                        iCheck();
                    } else {
                        var appendHtml_deptAndRole = '<label><input type="checkBox" value="' + checkboxAll[i].action_id
                            + '"checkBoxId="' + checkboxAll[i].system_type + '"checkName="' + checkboxAll[i].operate_name
                            + '"  class="' + checkBoxClass_deptAndRole + '"/>' + checkboxAll[i].operate_name + '</label>';
                        var appendHtml_user = '<label><input type="checkBox" value="' + checkboxAll[i].action_id
                            + '"checkBoxId="' + checkboxAll[i].system_type + '" checkName="' + checkboxAll[i].operate_name
                            + '" class="' + checkBoxClass_user + '"/>' + checkboxAll[i].operate_name + '</label>';
                        $(checkBoxDiv_deptAndRole).append(appendHtml_deptAndRole);
                        $(checkBoxDiv_user).append(appendHtml_user);
                        iCheck();
                    }
                }
            }
        }

        if (!isEmpty(authGroupMember)) {
            for (var j = 0; j < authGroupMember.length; j++) {
                if (isEmpty(authGroupMember[j].department) && isEmpty(authGroupMember[j].department_relation)) {
                    var checkBoxValue = "";
                    if (!isEmpty(authGroupMember[j].user_relation)) {
                        var $checkBox = $('.' + checkBoxClass_user);
                        for (var k = 0; k < $checkBox.length; k++) {
                            var checkBoxId = $($checkBox[k]).attr('checkBoxId');

                            if (checkBoxId == 'view' && authGroupMember[j].read) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                    + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'add' && authGroupMember[j].create) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                    + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'edit' && authGroupMember[j].update) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                    + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'delete' && authGroupMember[j]['delete']) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                    + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'operation') {
                                if (!isEmpty(authGroupMember[j].operate)) {
                                    var operateArray = authGroupMember[j].operate.split(',');
                                    for (var a = 0; a < operateArray.length; a++) {
                                        if ($($checkBox[k]).val() == operateArray[a]) {
                                            $checkBox[k].checked = true;
                                            checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                                        }
                                    }
                                }
                            }
                            
                            if (checkBoxId == 'import-insert' && (authGroupMember[j].import_auth == 'insert' || authGroupMember[j].import_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'import-update' && (authGroupMember[j].import_auth == 'update' || authGroupMember[j].import_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'export-selected' && (authGroupMember[j].export_auth == 'selected' || authGroupMember[j].export_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'exportall' && (authGroupMember[j].export_auth == 'allOut' || authGroupMember[j].export_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                        }
                        var appendHtml = '<div><label><input class="' + guid() + '" type="checkbox" name="user" value="' + authGroupMember[j].user_relation + '" valueName="'
                            + authGroupMember[j].user_relation_name + '"><label>' + authGroupMember[j].user_relation_name + '</label></label><span class="check-Box-Value">'
                            + checkBoxValue + '<a class="deleteAuthGroupMember"></a><a class="editAuthGroupMember"></a></span></div>';
                        $(userDiv).append(appendHtml);
                        iCheck();
                        uiinit();

                    } else {
                        var $checkBox = $('.' + checkBoxClass_user);
                        for (var k = 0; k < $checkBox.length; k++) {
                            var checkBoxId = $($checkBox[k]).attr('checkBoxId');

                            if (checkBoxId == 'view' && authGroupMember[j].read) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'add' && authGroupMember[j].create) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'edit' && authGroupMember[j].update) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'delete' && authGroupMember[j]['delete']) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'operation') {
                                if (!isEmpty(authGroupMember[j].operate)) {
                                    var operateArray = authGroupMember[j].operate.split(',');
                                    for (var a = 0; a < operateArray.length; a++) {
                                        if ($($checkBox[k]).val() == operateArray[a]) {
                                            $checkBox[k].checked = true;
                                            checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                                + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                                        }
                                    }
                                }
                            }
                            if (checkBoxId == 'import-insert' && (authGroupMember[j].import_auth == 'insert' || authGroupMember[j].import_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'import-update' && (authGroupMember[j].import_auth == 'update' || authGroupMember[j].import_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'export-selected' && (authGroupMember[j].export_auth == 'selected' || authGroupMember[j].export_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'exportall' && (authGroupMember[j].export_auth == 'allOut' || authGroupMember[j].export_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                        }
                        var appendHtml = '<div><label><input class="' + guid() + '" type="checkbox" name="user" value="' + authGroupMember[j].user + '" valueName="'
                            + authGroupMember[j].userName + '"><label>' + authGroupMember[j].userName + '</label></label><span class="check-Box-Value">' + checkBoxValue
                            + '<a class="deleteAuthGroupMember"></a><a class="editAuthGroupMember"></a></span></div>';
                        $(userDiv).append(appendHtml);
                        iCheck();
                        uiinit();

                    }
                } else if (isEmpty(authGroupMember[j].user_relation) && isEmpty(authGroupMember[j].user)) {
                    var checkBoxValue = "";
                    if (!isEmpty(authGroupMember[j].department_relation)) {
                        var $checkBox = $('.' + checkBoxClass_deptAndRole);
                        for (var k = 0; k < $checkBox.length; k++) {
                            var checkBoxId = $($checkBox[k]).attr('checkBoxId');
                            if (checkBoxId == 'view' && authGroupMember[j].read) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'add' && authGroupMember[j].create) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'edit' && authGroupMember[j].update) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'delete' && authGroupMember[j]['delete']) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'operation') {
                                if (!isEmpty(authGroupMember[j].operate)) {
                                    var operateArray = authGroupMember[j].operate.split(',');
                                    for (var a = 0; a < operateArray.length; a++) {
                                        if ($($checkBox[k]).val() == operateArray[a]) {
                                            $checkBox[k].checked = true;
                                            checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                                + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                                        }
                                    }
                                }
                            }
                            if (checkBoxId == 'import-insert' && (authGroupMember[j].import_auth == 'insert' || authGroupMember[j].import_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'import-update' && (authGroupMember[j].import_auth == 'update' || authGroupMember[j].import_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'export-selected' && (authGroupMember[j].export_auth == 'selected' || authGroupMember[j].export_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'exportall' && (authGroupMember[j].export_auth == 'allOut' || authGroupMember[j].export_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                        }
                        var appendHtml = '<div><label><input class="' + guid() + '" type="checkbox" name="department" value="' + authGroupMember[j].department_relation
                            + '" valueName="' + authGroupMember[j].department_relation_name + '"/><label>'
                            + authGroupMember[j].department_relation_name + '的' + authGroupMember[j].roleName + '</label></label><span class="check-Box-Value">'
                            + checkBoxValue + '<a class="deleteAuthGroupMember"></a><a class="editAuthGroupMember"></a></span><input class="' + guid() + '" type="hidden" name="role"  value="'
                            + authGroupMember[j].role + '" valueName="' + authGroupMember[j].roleName + '"></div>';
                        $(deptAndRoleDiv).append(appendHtml);

                        iCheck();
                        uiinit();
                    } else {
                        var $checkBox = $('.' + checkBoxClass_deptAndRole);
                        for (var k = 0; k < $checkBox.length; k++) {
                            var checkBoxId = $($checkBox[k]).attr('checkBoxId');

                            if (checkBoxId == 'view' && authGroupMember[j].read) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'add' && authGroupMember[j].create) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'edit' && authGroupMember[j].update) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'delete' && authGroupMember[j]['delete']) {
                                $checkBox[k].checked = true;
                                checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                    + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'operation') {
                                if (!isEmpty(authGroupMember[j].operate)) {
                                    var operateArray = authGroupMember[j].operate.split(',');
                                    for (var a = 0; a < operateArray.length; a++) {
                                        if ($($checkBox[k]).val() == operateArray[a]) {
                                            $checkBox[k].checked = true;
                                            checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId') + '">'
                                                + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                                        }
                                    }
                                }
                            }
                            if (checkBoxId == 'import-insert' && (authGroupMember[j].import_auth == 'insert' || authGroupMember[j].import_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'import-update' && (authGroupMember[j].import_auth == 'update' || authGroupMember[j].import_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'export-selected' && (authGroupMember[j].export_auth == 'selected' || authGroupMember[j].export_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                            if (checkBoxId == 'exportall' && (authGroupMember[j].export_auth == 'allOut' || authGroupMember[j].export_auth == 'all')) {
                            	$checkBox[k].checked = true;
                            	checkBoxValue += '<span name="' + $($checkBox[k]).val() + '"checkBoxId="' + $($checkBox[k]).attr('checkBoxId')
                                + '">' + $($checkBox[k]).attr('checkName') + "&nbsp&nbsp  </span>";
                            }
                        }
                        var appendHtml = '<div><label><input class="' + guid() + '" type="checkbox" name="department" value="' + authGroupMember[j].department
                            + '" valueName="' + authGroupMember[j].deptName + '"/><label>'
                            + authGroupMember[j].deptName + '的' + authGroupMember[j].roleName + '</label></label><span class="check-Box-Value">' + checkBoxValue
                            + '<a class="deleteAuthGroupMember"></a><a class="editAuthGroupMember"></a></span><input class="' + guid() + '" type="hidden" name="role"  value="' + authGroupMember[j].role
                            + '" valueName="' + authGroupMember[j].roleName + '"/></div>';
                        $(deptAndRoleDiv).append(appendHtml);

                        iCheck();
                        uiinit();
                    }
                }
            }
        }
        dx.processing.close();
    }

//新增权限数据
    function addAuthGroup(deptAndRoleDiv, userDiv, tableId_data, menuGroupId, columnId, template_name, type) {
        var authGroup = new Array();
        var menu_param = new Array();
        var list_deptAndRole = $('.' + deptAndRoleDiv);
        var list_user = $('.' + userDiv);
        for (var j = 0; j < list_deptAndRole.length; j++) {
            var operateValue = "";
            var param = {}
            var deptAndRole_span = $(list_deptAndRole[j]).find('span');
            var importFlag=false;
            var exportFlag=false;
            for (var k = 0; k < deptAndRole_span.length; k++) {
                var checkBoxId = $(deptAndRole_span[k]).attr('checkBoxId');
                var checkBoxName = $(deptAndRole_span[k]).attr('name');
                if (checkBoxId == 'view') {
                    param.read = checkBoxName;
                }
                if (checkBoxId == 'add') {
                    param.create = checkBoxName;
                }
                if (checkBoxId == 'edit') {
                    param.update = checkBoxName;
                }
                if (checkBoxId == 'delete') {
                    param['delete'] = checkBoxName;
                }
                if (isEmpty(template_name)) {
                    if (checkBoxId == 'operation') {
                        operateValue += checkBoxName + ',';
                        param.operate = operateValue;
                    }
                }
                if (checkBoxId == 'import-insert') {
                	if(importFlag){
                		param.import_auth = 'all';
                	}else{
                		param.import_auth = 'insert';
                		importFlag=true;
                	}
                }
                if (checkBoxId == 'import-update') {
                	if(importFlag){
                		param.import_auth = 'all';
                	}else{
                		param.import_auth = 'update';
                		importFlag=true;
                	}
                }
                if (checkBoxId == 'export-selected') {
                	if(exportFlag){
                		param.export_auth = 'all';
                	}else{
                		param.export_auth = 'selected';
                		exportFlag=true;
                	}
                }
                if (checkBoxId == 'exportall') {
                	if(exportFlag){
                		param.export_auth = 'all';
                	}else{
                		param.export_auth = 'allOut';
                		exportFlag=true;
                	}
                }
            }
            var dept = $(list_deptAndRole[j]).find('input[name=department]').val();
            var deptName = $(list_deptAndRole[j]).find('input[name=department]').attr("valuename");
            var role = $(list_deptAndRole[j]).find('input[name=role]').val();
            var roleName = $(list_deptAndRole[j]).find('input[name=role]').attr("valuename");
            param.department = dept;
            param.deptName = deptName;
            param.role = role;
            param.roleName = roleName;
            param.department_relation = "";
            param.department_relation_name = "";
            if (dept == '1' || dept == '2' || dept == '0') {
                param.department_relation = dept;
                param.department_relation_name = deptName;
            }
            param.user_relation = "";
            param.user = "";
            param.userName = "";
            param.user_relation_name = "";
            param.table = tableId_data;
            param.menu = menuGroupId;
            param.column = columnId;
            param.type = type;
            param.template = template_name;
//            if(!isEmpty(param.department) || !isEmpty(param.role) || !isEmpty(param.user)){
//            	authGroup.push(param);
//            }
            authGroup.push(param);
            var menuId = form.get().find('.menutree').jstree("get_checked");
            for (var k = 0; k < menuId.length; k++) {
                menu_param.push(menuId[k]);
            }
        }
        var checkboxsValue = "";
        for (var j = 0; j < list_user.length; j++) {
            var checkboxsValue = "";
            var param = {}
            var user_span = $(list_user[j]).find('span');
            var importFlag=false;
            var exportFlag=false;
            for (var k = 0; k < user_span.length; k++) {
                var checkBoxId = $(user_span[k]).attr('checkBoxId');
                var checkBoxName = $(user_span[k]).attr('name');
                if (checkBoxId == 'view') {
                    param.read = checkBoxName;
                }
                if (checkBoxId == 'add') {
                    param.create = checkBoxName;
                }
                if (checkBoxId == 'edit') {
                    param.update = checkBoxName;
                }
                if (checkBoxId == 'delete') {
                    param['delete'] = checkBoxName;
                }
                if (isEmpty(template_name)) {
                    if (checkBoxId == 'operation') {
                        checkboxsValue += checkBoxName + ',';
                        param.operate = checkboxsValue;
                    }
                }
                if (checkBoxId == 'import-insert') {
                	if(importFlag){
                		param.import_auth = 'all';
                	}else{
                		param.import_auth = 'insert';
                		importFlag=true;
                	}
                }
                if (checkBoxId == 'import-update') {
                	if(importFlag){
                		param.import_auth = 'all';
                	}else{
                		param.import_auth = 'update';
                		importFlag=true;
                	}
                }
                if (checkBoxId == 'export-selected') {
                	if(exportFlag){
                		param.export_auth = 'all';
                	}else{
                		param.export_auth = 'selected';
                		exportFlag=true;
                	}
                }
                if (checkBoxId == 'exportall') {
                	if(exportFlag){
                		param.export_auth = 'all';
                	}else{
                		param.export_auth = 'allOut';
                		exportFlag=true;
                	}
                }
            }
            var user = $(list_user[j]).find('input[name=user]').val();
            var userName = $(list_user[j]).find('input[name=user]').attr("valuename");
            param.user = user;
            param.userName = userName;
            param.user_relation = "";
            param.user_relation_name = "";
            if (user == '1' || user == '2' || user == '0') {
                param.user_relation = user;
                param.user_relation_name = userName;
            }
            param.department = "";
            param.deptName = "";
            param.department_relation = "";
            param.department_relation_name = "";
            param.role = "";
            param.roleName = "";
            
            param.table = tableId_data;
            param.menu = menuGroupId;
            param.column = columnId;
            param.type = type;
            param.template = template_name;
//            if(!isEmpty(param.department) || !isEmpty(param.role) || !isEmpty(param.user)){
//            	authGroup.push(param);
//            }
            authGroup.push(param);
            var menuId = form.get().find('.menutree').jstree("get_checked");
            for (var k = 0; k < menuId.length; k++) {
                menu_param.push(menuId[k]);
            }
        }
        if (list_user.length <= 0 && list_deptAndRole.length <= 0) {
        	  authGroup = [];
        	  menu_param = [];
            var param = {}
            param.table = tableId_data;
            param.menu = menuGroupId;
            param.column = columnId;
            param.type = type;
            authGroup.push(param);
            var menuId = form.get().find('.menutree').jstree("get_checked");
            for (var k = 0; k < menuId.length; k++) {
                menu_param.push(menuId[k]);
            }
        }
        var retrunParam={};
        retrunParam["authGroup"]=authGroup;
        retrunParam["menu_Id"]=menu_param;
        return retrunParam;
    }
    function saveAuthGroup(authGroup,menu_Id,authGroupMap,authDataGroup){
    	dx.processing.open();
        postJson('/authGroup/addAuthGroup.do', {authGroup: authGroup, menu_Id: menu_Id,
        	authGroupMap: authGroupMap, authDataGroupMap: authDataGroup}, function (result) {
            postJson('/data/cache/reload.do', function () {
                dxReload(function () {
                    dxToastAlert(msg('Saved successfully!'));
                    dx.processing.close();
                });
            })
        });
    }

    var allTemplate = [];
//   从模板中选择
    function lookAllTemplate(authGroup, lookAllTemplateUl, liClass, deptAndRoleDiv, userDiv, checkBoxDiv_deptAndRole, checkBoxDiv_user,
                             checkBoxClass_deptAndRole, checkBoxClass_user, select_department, select_role, select_user, type) {
        postJson('/authGroup/lookAllTemplate.do', {authGroup: authGroup}, function (result) {
            var tmp = result.authGroupMember;
            var checkboxAll = result.checkboxAll;
            var templateName = "";
            allTemplate = [];
            if (!isEmpty(tmp)) {
                for (var i = 0; i < tmp.length; i++) {
                    if (!isEmpty(tmp[i].template)) {
                        if (templateName != tmp[i].template) {
                            allTemplate.push(tmp[i]);
                            templateName = tmp[i].template;
                            $(lookAllTemplateUl).parent().find('ul').append("<li class='" + liClass + "' name='" + tmp[i].template + "'>" + tmp[i].template + "</li>");
                        }
                    }
                }

                $(lookAllTemplateUl).parent().find('ul').append('<li class="model-tag-manage">'
                    + '<button type="button" class="" data-toggle="modal" data-target="#auth-tag-modal">模板管理 </button></li>');

                $('.' + liClass).on("click", function () {
                    var authGroup = new Array();
                    var template_name = $(this).attr('name');
                    for (var i = 0; i < tmp.length; i++) {
                        if (tmp[i].template == template_name) {
                            authGroup.push(tmp[i]);
                        }
                    }
                    AuthGroupMember(deptAndRoleDiv, userDiv, checkBoxDiv_deptAndRole, checkBoxDiv_user, authGroup,
                        checkboxAll, checkBoxClass_deptAndRole, checkBoxClass_user, select_department, select_role, select_user)
                });
            }
        });
    }

    //模板管理点击事件
    form.get().find('#auth-tag-modal').on('show.bs.modal', function (e) {
        form.get().find('.modal-body').empty();
        for (var i = 0; i < allTemplate.length; i++) {
            allTemplate[i];
            form.get().find('.modal-body').append('<div class="dx-field-12 tab-list">' +
                '<div class="dx-field-7"><input class="input-1"  type="text" disabled value="' + allTemplate[i].template + '"/></div>' +
                '<div class="dx-field-5"><button class="tag-edit" type="button">编辑</button><button class="tag-delete" ' +
                'type="button">删除</button><button class="tag-save button-color3" type="button">确认</button></div></div>');
        }
    });
    var templateEditOrDelete = {};
    form.get().find(".model-tag-modal").on("click", ".tag-delete", function () {
        templateEditOrDelete = {};
        for (var i = 0; i < allTemplate.length; i++) {
            if (allTemplate[i].template == $(this).parents(".tab-list").find("input").val()) {
                templateEditOrDelete = allTemplate[i];
                break;
            }
        }
        templateEditOrDelete.editOrdetele = 2;
        var template = [];
        template.push(templateEditOrDelete);
        postJson('/approve/saveTemplate.do', {authGroup: template}, function (result) {
            dxToastAlert(msg('success'));
        });
        $(this).parents(".tab-list").remove();
    });
    form.get().find(".model-tag-modal").on("click", ".tag-edit", function () {
        templateEditOrDelete = {};
        for (var i = 0; i < allTemplate.length; i++) {
            if (allTemplate[i].template == $(this).parents(".tab-list").find("input").val()) {
                templateEditOrDelete = allTemplate[i];
                break;
            }
        }
        $(this).parents(".tab-list").find("button").hide();
        $(this).parents(".tab-list").find(".tag-save").show();
        $(this).parents(".tab-list").find("input").removeAttr("disabled").focus();
    });

    form.get().find(".model-tag-modal").on("click", ".tag-save", function () {
        if (isEmpty($(this).parents(".tab-list").find("input").val())) {
            alert("模板名称不能为空！");
        } else {
            templateEditOrDelete.editOrdetele = 1;
            templateEditOrDelete.editTemplateName = $(this).parents(".tab-list").find("input").val();
            var template = [];
            template.push(templateEditOrDelete);
            postJson('/approve/saveTemplate.do', {authGroup: template}, function (result) {
                dxToastAlert(msg('success'));
            });
            $(this).parents(".tab-list").find("button").show();
            $(this).parents(".tab-list").find(".tag-save").hide();
            $(this).parents(".tab-list").find("input").attr("disabled", true);
        }
    });
});

